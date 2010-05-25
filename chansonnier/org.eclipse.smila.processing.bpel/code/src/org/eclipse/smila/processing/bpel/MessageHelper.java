/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.processing.bpel;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.utils.DOMUtils;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.datamodel.record.dom.RecordBuilder;
import org.eclipse.smila.datamodel.record.dom.RecordParser;
import org.eclipse.smila.datamodel.tools.record.filter.RecordFilterNotFoundException;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessorMessage;
import org.eclipse.smila.processing.SearchMessage;
import org.eclipse.smila.processing.WorkflowProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Utility class to create and parse the DOM XML messages which we use to talk to the ODE engine, and sync workflow
 * objects to the blackboard.
 *
 * @author jschumacher
 *
 */
public final class MessageHelper {
  /**
   * WSDL type of search processing messages.
   */
  public static final QName TYPE_SEARCHMESSAGE =
    new QName(WorkflowProcessor.NAMESPACE_PROCESSOR, "SearchProcessorMessage");

  /**
   * WSDL type of standard processor messages.
   */
  public static final QName TYPE_PROCESSORMESSAGE =
    new QName(WorkflowProcessor.NAMESPACE_PROCESSOR, "ProcessorMessage");

  /**
   * name of root element of ODE message variables.
   */
  private static final String NAME_MESSAGE = "message";

  /**
   * name of records part of SMILA ODE messages.
   */
  private static final String PART_RECORDS = "records";

  /**
   * name of id part of SMILA ODE processor messages.
   */
  private static final String PART_ID = "id";

  /**
   * name of root element in id part.
   */
  private static final String NAME_REQID = "ReqId";

  /**
   * configuration property name: name of record filter to use for creating workflow objects = "record.filter". Can be
   * extended by ".&lt;pipeline-name&gt;" to specicy pipeline specific filter.
   */
  private static final String PROP_RECORD_FILTER = "record.filter";

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(ODEWorkflowProcessor.class);

  /**
   * record builder for creating DOM messages about workflow objects.
   */
  private final RecordBuilder _recordBuilder = new RecordBuilder();

  /**
   * record parser for parsing DOM messages about workflow objects.
   */
  private final RecordParser _recordParser = new RecordParser();

  /**
   * configuraton properties.
   */
  private Properties _properties;

  /**
   * create instance.
   *
   * @param properties
   *          configuration properties
   */
  public MessageHelper(final Properties properties) {
    _properties = properties;
  }

  /**
   * read request Id from message variable.
   *
   * @param message
   *          variable value
   * @return request Id
   */
  public String parseRequestId(final Element message) {
    final Element part = DOMUtils.getFirstChildElement(message);
    final Element idNode = DOMUtils.getFirstChildElement(part);
    final String requestId = idNode.getTextContent();
    return requestId;
  }

  /**
   * sync workflow objects from DOM message to blackboard and return IDs of workflow objects.
   *
   * @param blackboard
   *          to sync workflow objects to.
   * @param message
   *          DOM message from BPEL engine containing workflow objects.
   * @return IDs of workflow objects in message
   */
  public ProcessorMessage parseMessage(final Blackboard blackboard, final Element message) {
    final ProcessorMessage processorMessage = new ProcessorMessage();
    final Element recordsPart = findChildByName(message, PART_RECORDS);
    final Element recordList = findChildByName(recordsPart, RecordParser.TAG_RECORDLIST);
    final List<Record> workflowRecords = _recordParser.parseRecordsIn(recordList);
    if (workflowRecords == null) {
      return null;
    }
    processorMessage.setRecords(synchronizeBlackboard(blackboard, workflowRecords));
    return processorMessage;
  }

  /**
   * sync workflow objects of a search message from DOM message to blackboard and return IDs of query and result
   * objects.
   *
   * @param blackboard
   *          to sync workflow objects to.
   * @param message
   *          DOM message from BPEL engine containing workflow objects.
   * @return search message containing IDs of query and result records (if present in message).
   */
  public SearchMessage parseSearchMessage(final Blackboard blackboard, final Element message) {
    final SearchMessage searchMessage = new SearchMessage();
    final Element recordsPart = findChildByName(message, PART_RECORDS);
    final Element record = findChildByName(recordsPart, RecordParser.TAG_RECORD);
    if (record != null) {
      final Record workflowQuery = _recordParser.parseRecordFrom(record);
      searchMessage.setQuery(workflowQuery.getId());
      try {
        blackboard.synchronize(workflowQuery);
      } catch (final BlackboardAccessException e) {
        _log.error("error syncing record " + workflowQuery.getId() + " with blackboard", e);
      }
    }
    final Element recordList = findChildByName(recordsPart, RecordParser.TAG_RECORDLIST);
    if (recordList != null) {
      final List<Record> workflowRecords = _recordParser.parseRecordsIn(recordList);
      searchMessage.setRecords(synchronizeBlackboard(blackboard, workflowRecords));
    }
    return searchMessage;
  }

  /**
   * create workflow objects for specified IDs, create DOM representations of this record list and embed it as BPEL
   * message. This is done by appending them to <code>&lt;message&gt;&lt;records&gt;</code> elements.
   *
   * @param blackboard
   *          blackboard to read records from.
   * @param procMessage
   *          IDs of workflow objects in message
   * @return DOM representation of workflow objects
   * @throws ProcessingException
   *           error creating workflow record.
   */
  public Element createMessage(final Blackboard blackboard, final ProcessorMessage procMessage)
    throws ProcessingException {
    final List<Record> workflowRecords = createWorkflowObjects(blackboard, procMessage.getRecords());
    final Document doc = DOMUtils.newDocument();
    final Element message = doc.createElementNS(null, NAME_MESSAGE);
    final Element part = doc.createElementNS(null, PART_RECORDS);
    message.appendChild(part);
    _recordBuilder.appendRecordList(part, workflowRecords);
    return message;
  }

  /**
   * create workflow objects for query and record IDs in search message, create DOM representations of these records *
   * and embed it as BPEL message.
   *
   * @param blackboard
   *          blackboard to read records from.
   * @param searchMessage
   *          IDs of query and workflow objects in message
   * @return DOM representation of workflow objects
   * @throws ProcessingException
   *           error creating workflow record.
   */
  public Element createSearchMessage(final Blackboard blackboard, final SearchMessage searchMessage)
    throws ProcessingException {
    Record workflowQuery = null;
    if (searchMessage.hasQuery()) {
      workflowQuery = createWorkflowRecord(blackboard, searchMessage.getQuery());
    }
    List<Record> workflowRecords = null;
    if (searchMessage.hasRecords()) {
      workflowRecords = createWorkflowObjects(blackboard, searchMessage.getRecords());
    }
    final Document doc = DOMUtils.newDocument();
    final Element message = doc.createElementNS(null, NAME_MESSAGE);
    final Element recordsPart = doc.createElementNS(null, PART_RECORDS);
    message.appendChild(recordsPart);
    if (workflowQuery != null) {
      recordsPart.appendChild(_recordBuilder.buildRecord(doc, workflowQuery));
    }
    message.appendChild(recordsPart);
    if (workflowRecords != null) {
      _recordBuilder.appendRecordList(recordsPart, workflowRecords);
    }
    return message;
  }

  /**
   * add request id part as first child element of message.
   *
   * @param message
   *          a message variable
   * @param requestId
   *          request id.
   * @param varType
   *          type of variable. currently necessary to decide where to put the Id.
   */
  public void addRequestId(final Element message, final String requestId, final QName varType) {
    Element idNode = createRequestIdNode(requestId, message.getOwnerDocument());
    Element parent = message;
    if (MessageHelper.TYPE_SEARCHMESSAGE.equals(varType)) {
      parent = DOMUtils.getFirstChildElement(message);
    } else {
      final Element idPart = message.getOwnerDocument().createElementNS(null, PART_ID);
      idPart.appendChild(idNode);
      idNode = idPart;
    }
    if (DOMUtils.isEmptyElement(parent)) {
      parent.appendChild(idNode);
    } else {
      parent.insertBefore(idNode, DOMUtils.getFirstChildElement(parent));
    }
  }

  /**
   *
   * @return true if a global record filter is configured.
   */
  public boolean hasRecordFilter() {
    return _properties.containsKey(PROP_RECORD_FILTER);
  }

  /**
   * find first child element of parent with given local name.
   *
   * @param parent
   *          root element
   * @param name
   *          local name
   * @return matching element or null.
   */
  private Element findChildByName(final Element parent, final String name) {
    final NodeList children = parent.getChildNodes();
    for (int i = 0; i < children.getLength(); ++i) {
      final Node c = children.item(i);
      if (c.getNodeType() == Node.ELEMENT_NODE) {
        final String nodeName = c.getNodeName();
        if (name.equals(nodeName)) {
          return (Element) c;
        }
      }
    }
    return null;
  }

  /**
   * create workflow records for specified IDs.
   *
   * @param blackboard
   *          the blackboard holding the records.
   * @param recordIds
   *          IDs of workflow records
   * @return workflow records.
   * @throws ProcessingException
   *           error creating a workflow object: null or invalid id, invalid record filter name.
   */
  private List<Record> createWorkflowObjects(final Blackboard blackboard, final Id[] recordIds)
    throws ProcessingException {
    if (recordIds == null) {
      _log.error("null list of ids passed to create workflow objects.");
      throw new ProcessingException("no list of ids passed to create workflow objects.");
    }
    final List<Record> workflowRecords = new ArrayList<Record>(recordIds.length);
    for (final Id id : recordIds) {
      final Record workflowRecord = createWorkflowRecord(blackboard, id);
      workflowRecords.add(workflowRecord);
    }
    return workflowRecords;
  }

  /**
   * create workflow record from blackboard for specified ID.
   *
   * @param blackboard
   *          the blackboard holding the records.
   * @param id
   *          ID of record to access.
   * @return workflow record
   * @throws ProcessingException
   *           e rror creating a workflow object: null or invalid id, invalid record filter name.
   */
  private Record createWorkflowRecord(final Blackboard blackboard, final Id id) throws ProcessingException {
    if (id == null) {
      _log.error("cannot create workflow object from null record id.");
      throw new ProcessingException("can not create workflow object from null record id.");
    }
    Record workflowRecord = null;
    final String recordFilterName = _properties.getProperty(PROP_RECORD_FILTER);
    if (recordFilterName != null) {
      try {
        workflowRecord = blackboard.getRecord(id, recordFilterName);
      } catch (final RecordFilterNotFoundException ex) {
        _log.error("unknown record filter name " + recordFilterName + " in workflow object creation.", ex);
        throw new ProcessingException("unknown record filter name " + recordFilterName
          + " in workflow object creation.", ex);
      } catch (final BlackboardAccessException ex) {
        _log.error("cannot create workflow object from record " + id, ex);
        throw new ProcessingException("cannot create workflow object from record " + id, ex);
      }
    }
    if (workflowRecord == null) {
      workflowRecord = RecordFactory.DEFAULT_INSTANCE.createRecord();
      workflowRecord.setId(id);
    }
    return workflowRecord;
  }

  /**
   * synchronize workflow records to blackboard, return IDs.
   *
   * @param blackboard
   *          to sync workflow objects to.
   * @param workflowRecords
   *          workflow records to sync.
   * @return record IDs
   */
  private Id[] synchronizeBlackboard(final Blackboard blackboard, final List<Record> workflowRecords) {
    final Id[] recordIds = new Id[workflowRecords.size()];
    for (int i = 0; i < recordIds.length; i++) {
      final Record record = workflowRecords.get(i);
      recordIds[i] = record.getId();
      try {
        blackboard.synchronize(record);
      } catch (final BlackboardAccessException e) {
        _log.error("error syncing record " + recordIds[i] + " with blackboard", e);
      }
    }
    return recordIds;
  }

  /**
   * create DOM representation of the id part of the SMILA messages.
   *
   * @param requestId
   *          the id value
   * @param doc
   *          element factory
   * @return id part.
   */
  private Element createRequestIdNode(final String requestId, final Document doc) {
    final Element idNode = doc.createElementNS(WorkflowProcessor.NAMESPACE_PROCESSOR, NAME_REQID);
    final Text content = doc.createTextNode(requestId);
    idNode.appendChild(content);
    return idNode;
  }
}

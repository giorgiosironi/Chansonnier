/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.processing.pipelets.xmlprocessing;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.connectivity.ConnectivityException;
import org.eclipse.smila.connectivity.ConnectivityManager;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.processing.JMSMessageAnnotations;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.pipelets.ATransformationPipelet;
import org.eclipse.smila.utils.service.ServiceUtils;
import org.eclipse.smila.utils.xml.stax.MarkerTag;
import org.eclipse.smila.utils.xml.stax.SimpleTagExtractor;
import org.eclipse.smila.utils.xml.stax.XmlSnippetHandler;
import org.eclipse.smila.utils.xml.stax.XmlSnippetSplitter;

/**
 * Pipelet that splits a XML stream into multiple xml snippets. For each snippet a new Record is created where the XML
 * snippet is stored in either an attribute or attachment. The created records are not returned as a PipeletResult (this
 * is just the same as the incoming RecordIds) but are directly send to the ConnectivityManager and are routed once more
 * to the Queue.
 * 
 * On each created record the Annotation <tt>MessageProperties</tt> is set with the key value pair
 * <tt>isXmlSnippet</tt>=<tt>true</tt>. This can be used in Listener rules to select for XML snippets to process.The
 * possible properties are:
 * <ul>
 * <li>beginTagName: the name of the tag to start the xml snippet</li>
 * <li>isBeginClosingTag: boolean flag if the beginTagName is a closing tag (true) or not (false)</li>
 * <li>endTagName: the name of the tag to end the xml snippet</li>
 * <li>isEndClosingTag: boolean flag if the endTagName is a closing tag (true) or not (false)</li>
 * <li>keyTagName: the name of the tag used to create a record id</li>
 * <li>maxBufferSize: the maximum size of the internal record buffer (optional, default is 20)</li>
 * <li>inputName: name of the Attribute/Attachment to read the XML Document from.</li>
 * <li>outputName: name of the Attribute/Attachment to store the extracted value in</li>
 * <li>inputType: the type (Attribute or Attachment of the inputName. An input Attribute is not interpreted as content
 * but as a file path or an URL to the XML document</li>
 * <li>outputType: the type (Attribute or Attachment of the outputtName</li>
 * </ul>
 */
public class XmlSplitterPipelet extends ATransformationPipelet {
  /**
   * Constant for the property beginTagName.
   */
  public static final String PROP_BEGIN_TAG_NAME = "beginTagName";

  /**
   * Constant for the property isBeginClosingTag.
   */
  public static final String PROP_IS_BEGIN_CLOSING_TAG = "isBeginClosingTag";

  /**
   * Constant for the property endTagName.
   */
  public static final String PROP_END_TAG_NAME = "endTagName";

  /**
   * Constant for the property isEndClosingTag.
   */
  public static final String PROP_IS_END_CLOSING_TAG = "isEndClosingTag";

  /**
   * Constant for the property keyTagName.
   */
  public static final String PROP_KEY_TAG_NAME = "keyTagName";

  /**
   * Constant for the property maxBufferSize.
   */
  public static final String PROP_MAX_BUFFER_SIZE = "maxBufferSize";

  /**
   * Constant for the default max buffer size (20).
   */
  public static final int DEFAULT_MAX_BUFFER_SIZE = 20;

  /**
   * The MarkerTag for the snippet begin.
   */
  private MarkerTag _beginTag;

  /**
   * The MarkerTag for the snippet end.
   */
  private MarkerTag _endTag;

  /**
   * The name of the tag containing the key.
   */
  private String _keyTagName;

  /**
   * SimpleTagExtractor instance to extract key values.
   */
  private SimpleTagExtractor _extractor = new SimpleTagExtractor(true);

  /**
   * Reference to the ConnectivityManager.
   */
  private ConnectivityManager _connectivityManager;

  /**
   * The record buffer used to buffer created records before sending them in blocks to ConnectivityManager.
   */
  private ArrayList<Record> _recordBuffer = new ArrayList<Record>();

  /**
   * The max buffer size. If reached the buffer is flushed.
   */
  private int _maxBufferSize = DEFAULT_MAX_BUFFER_SIZE;

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet
   *      #configure(org.eclipse.smila.processing.configuration.PipeletConfiguration)
   */
  public void configure(PipeletConfiguration configuration) throws ProcessingException {
    super.configure(configuration);
    final String beginTagName = (String) configuration.getPropertyFirstValueNotNull(PROP_BEGIN_TAG_NAME);
    if (beginTagName.trim().length() == 0) {
      throw new ProcessingException("Property " + PROP_BEGIN_TAG_NAME + " must not be an empty String");
    }
    final boolean isBeginEndTag =
      Boolean.valueOf((String) configuration.getPropertyFirstValueNotNull(PROP_IS_BEGIN_CLOSING_TAG));

    final String endTagName = (String) configuration.getPropertyFirstValueNotNull(PROP_END_TAG_NAME);
    if (endTagName.trim().length() == 0) {
      throw new ProcessingException("Property " + PROP_END_TAG_NAME + " must not be an empty String");
    }
    final boolean isEndEndTag =
      Boolean.valueOf((String) configuration.getPropertyFirstValueNotNull(PROP_IS_END_CLOSING_TAG));

    _keyTagName = (String) configuration.getPropertyFirstValueNotNull(PROP_KEY_TAG_NAME);
    if (_keyTagName.trim().length() == 0) {
      throw new ProcessingException("Property " + PROP_KEY_TAG_NAME + " must not be an empty String");
    }

    final String bufferSize = (String) configuration.getPropertyFirstValue(PROP_MAX_BUFFER_SIZE);
    if (bufferSize != null) {
      _maxBufferSize = Integer.parseInt(bufferSize);
    }

    _beginTag = new MarkerTag(beginTagName, isBeginEndTag);
    _endTag = new MarkerTag(endTagName, isEndEndTag);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet#process(org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.datamodel.id.Id[])
   */
  public Id[] process(final Blackboard blackboard, final Id[] recordIds) throws ProcessingException {
    final InternalHandler snippetHandler = new InternalHandler();
    final XmlSnippetSplitter splitter = new XmlSnippetSplitter(snippetHandler, _beginTag, _endTag);
    if (recordIds != null) {
      for (Id id : recordIds) {
        try {
          // get xml input stream
          InputStream inputStream = null;
          if (isReadFromAttribute()) {
            inputStream = loadExternalInputStream(readStringInput(blackboard, id));
          } else {
            inputStream = blackboard.getAttachmentAsStream(id, getInputName());
          }

          snippetHandler.setCurrentId(id);
          splitter.read(inputStream);
          if (_log.isInfoEnabled()) {
            _log.info("Created " + snippetHandler.getRecordCount() + " records from processing record " + id);
          }
        } catch (Exception e) {
          if (_log.isWarnEnabled()) {
            _log.warn("unable to split record " + id, e);
          }
        }
      } // for
      try {
        flushRecordBuffer();
      } catch (Exception e) {
        throw new ProcessingException("error flushing record buffer", e);
      }
    } // if
    return recordIds;
  }

  /**
   * Get the ConnectivityManager.
   * 
   * @return the ConnectivityManager.
   * @throws InterruptedException
   *           if any error occurs
   */
  private ConnectivityManager getConnectivityManager() throws InterruptedException {
    if (_connectivityManager == null) {
      _connectivityManager = ServiceUtils.getService(ConnectivityManager.class);
    }
    return _connectivityManager;
  }

  /**
   * Adds the given record to the record buffer. If _maxBufferSize is reached a flush of the buffer is done.
   * 
   * @param record
   *          the Record to add to the buffer
   * @throws ConnectivityException
   *           if any error occurs
   * @throws InterruptedException
   *           if any error occurs
   */
  private void addToRecordBuffer(final Record record) throws ConnectivityException, InterruptedException {
    synchronized (_recordBuffer) {
      _recordBuffer.add(record);
      if (_recordBuffer.size() >= _maxBufferSize) {
        flushRecordBuffer();
      }
    }
  }

  /**
   * Flushes the record buffer if it is not empty.
   * 
   * @throws ConnectivityException
   *           if any error occurs
   * @throws InterruptedException
   *           if any error occurs
   */
  private void flushRecordBuffer() throws ConnectivityException, InterruptedException {
    synchronized (_recordBuffer) {
      if (!_recordBuffer.isEmpty()) {
        try {
          getConnectivityManager().add(_recordBuffer.toArray(new Record[_recordBuffer.size()]));
        } finally {
          _recordBuffer.clear();
        }
      }
    }
  }

  /**
   * Get the external InputStream to the given url or filee path.
   * 
   * @param attrtibuteValue
   *          the attrtibuteValue denoting an URL or file path
   * @return a InputStream or null
   * @throws IOException
   *           if any error occurs
   */
  private InputStream loadExternalInputStream(final String attrtibuteValue) throws IOException {
    InputStream stream = null;
    if (attrtibuteValue != null && attrtibuteValue.trim().length() > 0) {
      if (attrtibuteValue.startsWith("file")) {
        final URL url = new URL(attrtibuteValue);
        stream = new FileInputStream(url.getAuthority() + url.getPath());
      } else if (attrtibuteValue.startsWith("http")) {
        final URL url = new URL(attrtibuteValue);
        final HttpClient httpClient = new HttpClient();
        final GetMethod getMethod = new GetMethod(url.toString());
        httpClient.executeMethod(getMethod);
        stream = getMethod.getResponseBodyAsStream();
      } else {
        stream = new FileInputStream(attrtibuteValue);
      }
    } // if
    return stream;
  }

  /**
   * Internal XmlSnippetHandler implementation to handle the snippets, create id and record objects and send them to the
   * Queue.
   */
  class InternalHandler implements XmlSnippetHandler {

    /**
     * The currently processed Id. used to generate fragment id objects.
     */
    private Id _currentId;

    /**
     * Counts the total number of created records.
     */
    private int _recordCounter;

    /**
     * Counts the number of invokes of handleSnippet() for the _currentId.
     */
    private int _countById;

    /**
     * Set the current Id used for fragment Id creation.
     * 
     * @param id
     *          the current Id.
     */
    void setCurrentId(final Id id) {
      _currentId = id;
      _countById = 0;
    }

    /**
     * Returns the number of created records.
     * 
     * @return the number of created records
     */
    int getRecordCount() {
      return _recordCounter;
    }

    /**
     * {@inheritDoc}
     * 
     * @see XmlSnippetHandler#handleSnippet(byte[])
     */
    public void handleSnippet(final byte[] snippet) {
      _countById++;
      Id snippetId = null;
      try {
        final List<String> keys = _extractor.getTags(_keyTagName, new ByteArrayInputStream(snippet));
        if (!keys.isEmpty()) {
          snippetId = _currentId.createFragmentId(keys.get(0));

          final Record record = RecordFactory.DEFAULT_INSTANCE.createRecord();
          record.setId(snippetId);
          if (isStoreInAttribute()) {
            final Literal literal = RecordFactory.DEFAULT_INSTANCE.createLiteral();
            literal.setStringValue(new String(snippet, ENCODING_ATTACHMENT));
            final Attribute attribute = RecordFactory.DEFAULT_INSTANCE.createAttribute();
            attribute.addLiteral(literal);
            record.getMetadata().setAttribute(_outputName, attribute);
          } else {
            record.setAttachment(_outputName, snippet);
          }

          // set message properties
          final Annotation messageProperties = RecordFactory.DEFAULT_INSTANCE.createAnnotation();
          messageProperties.setNamedValue(JMSMessageAnnotations.PROPERTY_IS_XML_SNIPPET, Boolean.toString(true));
          record.getMetadata()
            .addAnnotation(JMSMessageAnnotations.ANNOTATION_MESSAGE_PROPERTIES, messageProperties);

          _recordCounter++;
          addToRecordBuffer(record);
        } else {
          if (_log.isWarnEnabled()) {
            _log.warn("could not find tag " + _keyTagName + " in snippet number " + _countById + " of record "
              + _currentId);
          }
          if (_log.isTraceEnabled()) {
            _log.trace("snippet content: " + new String(snippet));
          }
        }
      } catch (Exception e) {
        if (_log.isErrorEnabled()) {
          _log.error("error creating record for xml snippet number " + _countById + " with id " + snippetId
            + " of record " + _currentId, e);
        }
        if (_log.isTraceEnabled()) {
          _log.trace("snippet content: " + new String(snippet));
        }
      }
    }
  } // InternalHandler
}

/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.connectivity.framework.agent.jobfile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.stax.IdReader;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.stax.RecordReader;

/**
 * StAX based JobFile reader. Should give better performance than the DOM based IdParser.
 */
public class JobFileReader {

  /**
   * XML tag JobFile.
   */
  public static final String TAG_JOB_FILE = "JobFile";

  /**
   * XML tag Add.
   */
  public static final String TAG_ADD = "Add";

  /**
   * XML tag Delete.
   */
  public static final String TAG_DELETE = "Delete";

  /**
   * my Record reader.
   */
  private RecordReader _recordReader;

  /**
   * my Id reader.
   */
  private IdReader _idReader;

  /**
   * Callback to the JobFileHandler.
   */
  private JobFileHandler _jobFileHandler;

  /**
   * The separator of attachment name and url.
   */
  private String _attachmentSeparator;

  /**
   * The length of the attachment separator.
   */
  private int _attachmentSeparatorLength;

  /**
   * The LOG.
   */
  private final Log _log = LogFactory.getLog(JobFileReader.class);

  /**
   * create default instance.
   * 
   * @param jobFileHandler
   *          the JobFileHandler
   * @param attachmentSeparator
   *          the attachmentSeparator
   */
  public JobFileReader(final JobFileHandler jobFileHandler, final String attachmentSeparator) {
    this(jobFileHandler, attachmentSeparator, new RecordReader(), new IdReader());
  }

  /**
   * Conversion Constructor.
   * 
   * @param jobFileHandler
   *          the JobFileHandler
   * @param attachmentSeparator
   *          the attachmentSeparator
   * @param recordReader
   *          the RecordReader
   * @param idReader
   *          the IdReader
   */
  public JobFileReader(final JobFileHandler jobFileHandler, final String attachmentSeparator,
    final RecordReader recordReader, final IdReader idReader) {
    // check parameters
    if (jobFileHandler == null) {
      throw new IllegalArgumentException("parameter jobFileHandler is null");
    }
    if (attachmentSeparator == null) {
      throw new IllegalArgumentException("parameter attachmentSeparator is null");
    }
    if (attachmentSeparator.trim().length() == 0) {
      throw new IllegalArgumentException("parameter attachmentSeparator is an empty String");
    }
    if (recordReader == null) {
      throw new IllegalArgumentException("parameter recordReader is null");
    }
    if (idReader == null) {
      throw new IllegalArgumentException("parameter idReader is null");
    }

    _jobFileHandler = jobFileHandler;
    _recordReader = recordReader;
    _idReader = idReader;
    _attachmentSeparator = attachmentSeparator;
    _attachmentSeparatorLength = _attachmentSeparator.length();
  }

  /**
   * Reads in and processes a JobFile.
   * 
   * @param url
   *          the url of the job file
   * @throws XMLStreamException
   *           the StAX Exception
   */
  public void readJobFile(final URL url) throws XMLStreamException {
    // check parameters
    if (url == null) {
      throw new IllegalArgumentException("parameter url is null");
    }

    InputStream stream = null;
    try {
      stream = getInputStream(url);
      final XMLStreamReader staxReader = Activator.getXMLInputFactory().createXMLStreamReader(stream);
      parse(staxReader, url);
    } catch (XMLStreamException e) {
      throw e;
    } catch (Exception e) {
      final String msg = "Error while loading job file " + url;
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      throw new XMLStreamException(msg, e);
    } finally {
      IOUtils.closeQuietly(stream);
    }
  }

  /**
   * Parse JobFile, read Records from the XML stream. The stream must be currently at the RecordList start tag.
   * 
   * @param staxReader
   *          source XML stream
   * @param url
   *          the url of the job file
   * @throws XMLStreamException
   *           StAX error.
   */
  private void parse(final XMLStreamReader staxReader, final URL url) throws XMLStreamException {
    staxReader.nextTag();
    if (isStartTag(staxReader, TAG_JOB_FILE)) {
      while (staxReader.hasNext()) {
        staxReader.nextTag();
        if (isStartTag(staxReader, TAG_ADD)) {
          staxReader.nextTag();
          do {
            final Record record = _recordReader.readRecord(staxReader);
            if (record != null) {
              try {
                loadAttachments(record);
                _jobFileHandler.add(record);
              } catch (IOException e) {
                final String msg =
                  "Error loading attachments for record " + record.getId() + ". Record is skipped.";
                if (_log.isErrorEnabled()) {
                  _log.error(msg, e);
                }
              }
            } // if
            staxReader.nextTag();
          } while (staxReader.hasNext() && !isEndTag(staxReader, TAG_ADD));
        } else if (isStartTag(staxReader, TAG_DELETE)) {
          staxReader.nextTag();
          do {
            final Id id = _idReader.readId(staxReader);
            _jobFileHandler.delete(id);
            staxReader.nextTag();
          } while (staxReader.hasNext() && !isEndTag(staxReader, TAG_DELETE));
        } else if (isEndTag(staxReader, TAG_JOB_FILE)) {
          break;
        }
      } // while
    } else {
      throw new XMLStreamException("Invalid document " + url + ". Must begin with tag <" + TAG_JOB_FILE + ">");
    }
  }

  /**
   * 
   * @param staxReader
   *          source XML stream
   * @param tagName
   *          tag name
   * @return true if we are currently at a start tag with the specified name
   */
  private boolean isStartTag(final XMLStreamReader staxReader, final String tagName) {
    return staxReader.isStartElement() && tagName.equals(staxReader.getLocalName());
  }

  /**
   * 
   * @param staxReader
   *          source XML stream
   * @param tagName
   *          tag name
   * @return true if we are currently at a end tag with the specified name
   */
  private boolean isEndTag(final XMLStreamReader staxReader, final String tagName) {
    return staxReader.isEndElement() && tagName.equals(staxReader.getLocalName());
  }

  /**
   * Loads all attachments for the given record.
   * 
   * @param record
   *          the record
   * @throws IOException
   *           if any error occurs
   */
  private void loadAttachments(final Record record) throws IOException {
    final Iterator<String> attachmentNames = record.getAttachmentNames();
    while (attachmentNames.hasNext()) {
      loadAttachment(record, attachmentNames.next());
    }
  }

  /**
   * Loads the attachment specified by nameUrl into the given record.
   * 
   * @param record
   *          the record
   * @param nameUrl
   *          the nameUrl, a String containing the attachment name separated from the url to the attachment
   * @throws IOException
   *           if any error occurs
   */
  private void loadAttachment(final Record record, final String nameUrl) throws IOException {
    if (nameUrl != null) {
      final int index = nameUrl.indexOf(_attachmentSeparator);
      if (index > 0) {
        final String attachmentName = nameUrl.substring(0, index);
        String urlString = nameUrl.substring(index + _attachmentSeparatorLength);
        if (!urlString.startsWith("http") && !urlString.startsWith("file")) {
          urlString = "file://" + urlString.replaceAll("\\\\", "/");
        }
        record.setAttachment(attachmentName, readBytes(new URL(urlString.trim())));
      } else {
        if (_log.isWarnEnabled()) {
          _log.warn("Invalid name pattern for attachment name " + nameUrl + " for record id " + record.getId()
            + ". Attachment is skipped.");
        }
      }
      // remove the name-url attachment
      record.removeAttachment(nameUrl);
    } // if
  }

  /**
   * Get the InputStream to the given url.
   * 
   * @param url
   *          the url
   * @return a InputStream or null
   * @throws IOException
   *           if any error occurs
   */
  private InputStream getInputStream(final URL url) throws IOException {
    InputStream stream = null;
    if (url != null) {
      if (url.getProtocol().startsWith("file")) {
        stream = new FileInputStream(url.getAuthority() + url.getPath());
      } else {
        final HttpClient httpClient = new HttpClient();
        final GetMethod getMethod = new GetMethod(url.toString());
        httpClient.executeMethod(getMethod);
        stream = getMethod.getResponseBodyAsStream();
      }
    } // if
    return stream;
  }

  /**
   * Returns the bytes for the given url.
   * 
   * @param url
   *          the url
   * @return a byte[]
   * @throws IOException
   *           if any error occurs
   */
  private byte[] readBytes(final URL url) throws IOException {
    InputStream stream = null;
    try {
      stream = getInputStream(url);
      return IOUtils.toByteArray(stream);
    } finally {
      IOUtils.closeQuietly(stream);
    }
  }
}

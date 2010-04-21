/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing.pipelets;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.filters.DefaultFilter;
import org.cyberneko.html.filters.ElementRemover;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.configuration.PipeletConfiguration.Property;

/**
 * Simple HTML-to-Text extractor pipelet using NekoHTML parser.
 * 
 * @author jschumacher
 * 
 */
public class HtmlToTextPipelet extends ATransformationPipelet {

  /**
   * By default the content of these tags is also removed from the result. This can be overwritten using the
   * "removeContentTags" property.
   */
  private static final String[] DEFAULT_REMOVE_CONTENT_TAGS = { "applet", "frame", "object", "script", "style" };

  /**
   * property to configure the tags for which the complete content is removed.
   */
  private static final String PROP_REMOVE_CONTENT_TAGS = "removeContentTags";

  /**
   * property to configure attribute targets for HTML meta tag contents.
   */
  private static final String PROP_META = "meta:";

  /**
   * property to configure attribute targets for HTML title tag content.
   */
  private static final String PROP_TITLE = "tag:title";

  /**
   * tag names for which the complete content is removed from result.
   */
  private String[] _removeContentTags = DEFAULT_REMOVE_CONTENT_TAGS;

  /**
   * mapping of META tags to attribute names.
   */
  private final Map<String, String> _metaAttributeMapping = new HashMap<String, String>();

  /** The log. */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.pipelets.ATransformationPipelet
   *      #configure(org.eclipse.smila.processing.configuration.PipeletConfiguration)
   */
  @Override
  public void configure(PipeletConfiguration configuration) throws ProcessingException {
    super.configure(configuration);
    final Object removeContentTagValue = configuration.getPropertyFirstValue(PROP_REMOVE_CONTENT_TAGS);
    if (removeContentTagValue != null) {
      final String removeContentTagList = removeContentTagValue.toString().trim();
      if (removeContentTagList.length() > 0) {
        _removeContentTags = removeContentTagList.split(",");
        _log.info("Removing complete content of these tags: " + Arrays.toString(_removeContentTags));
      }
    }
    for (Property property : configuration.getProperties()) {
      if (property.getName().startsWith(PROP_META)) {
        final String metaName = property.getName().substring(PROP_META.length());
        final String attributeName = property.getValue().toString();
        _metaAttributeMapping.put(metaName, attributeName);
      } else if (property.getName().equalsIgnoreCase(PROP_TITLE)) {
        final String attributeName = property.getValue().toString();
        _metaAttributeMapping.put(PROP_TITLE, attributeName);
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet#process(org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.datamodel.id.Id[])
   */
  public Id[] process(Blackboard blackboard, Id[] recordIds) throws ProcessingException {
    if (recordIds != null) {
      for (Id id : recordIds) {
        try {
          final MultiValueMap metadata = new MultiValueMap();
          final List<String> results = new ArrayList<String>();
          if (isReadFromAttribute()) {
            processAttributeValues(blackboard, id, results, metadata);
          } else {
            final InputStream stream = blackboard.getAttachmentAsStream(id, _inputName);
            if (stream != null) {
              results.add(extractText(id, stream, metadata));
            }
          }
          storeResults(blackboard, id, results);
          storeMetadata(blackboard, id, metadata);
        } catch (BlackboardAccessException ex) {
          _log.error("Error processing ID " + id, ex);
        }
      }
    }
    return recordIds;
  }

  /**
   * read HTML strings from an attribute and add plain text strings and metadata to the results.
   * 
   * @param blackboard
   *          blackboard service to use.
   * @param id
   *          record ID to process
   * @param results
   *          list of plain text strings.
   * @param metadata
   *          metadata map
   * @throws BlackboardAccessException
   *           record is not on blackboard
   * @throws ProcessingException
   *           error parsing the HTML.
   */
  private void processAttributeValues(Blackboard blackboard, Id id, final List<String> results,
    final MultiValueMap metadata) throws BlackboardAccessException, ProcessingException {
    final List<Literal> literals = blackboard.getLiterals(id, _inputPath);
    if (!literals.isEmpty()) {
      for (Literal literal : literals) {
        final String content = literal.getStringValue();
        if (content != null) {
          results.add(extractText(id, content, metadata));
        }
      }
    }
  }

  /**
   * write metadata to attributes on blackboard.
   * 
   * @param blackboard
   *          blackboard
   * @param id
   *          record id
   * @param metadata
   *          metadata to store
   * @throws BlackboardAccessException
   *           error writing values.
   */
  @SuppressWarnings("unchecked")
  private void storeMetadata(Blackboard blackboard, Id id, MultiValueMap metadata) throws BlackboardAccessException {
    if (!metadata.isEmpty()) {
      for (final Iterator attributeNames = metadata.keySet().iterator(); attributeNames.hasNext();) {
        final String attributeName = (String) attributeNames.next();
        final Collection values = metadata.getCollection(attributeName);
        final Path path = new Path(attributeName);
        if (!values.isEmpty()) {
          blackboard.removeLiterals(id, path);
          for (final Iterator iter = values.iterator(); iter.hasNext();) {
            final String value = (String) iter.next();
            final Literal literal = blackboard.createLiteral(id);
            literal.setStringValue(value);
            blackboard.addLiteral(id, path, literal);
          }
        }
      }
    }
  }

  /**
   * extract the plain text from the HTML document given by the stream.
   * 
   * @param id
   *          record ID (for logging purposes)
   * @param stream
   *          HTML stream *
   * @param metadata
   *          put extracted metadata in this map.
   * @return plain text
   * @throws ProcessingException
   *           error in parsing
   */
  private String extractText(Id id, InputStream stream, MultiValueMap metadata) throws ProcessingException {
    final StringBuilder result = new StringBuilder();
    final XMLParserConfiguration parser = createParser(result, metadata);
    try {
      parser.parse(new XMLInputSource(null, id.toString(), null, stream, null));
    } catch (Exception e) {
      _log.error("error parsing HTML document in record " + id, e);
      throw new ProcessingException("error parsing HTML document in record " + id + ": " + e.toString(), e);
    }
    return result.toString();
  }

  /**
   * extract the plain text from the HTML document given by the string.
   * 
   * @param id
   *          record ID (for logging purposes)
   * @param content
   *          HTML string
   * @param metadata
   *          put extracted metadata in this map.
   * @return plain text
   * @throws ProcessingException
   *           error in parsing
   */
  private String extractText(Id id, String content, MultiValueMap metadata) throws ProcessingException {
    final StringBuilder result = new StringBuilder();
    final XMLParserConfiguration parser = createParser(result, metadata);

    try {
      parser.parse(new XMLInputSource(null, id.getIdHash(), null, new StringReader(content), null));
    } catch (Exception e) {
      _log.error("error parsing HTML document in record " + id, e);
      throw new ProcessingException("error parsing HTML document in record " + id + ": " + e.toString(), e);
    }
    return result.toString();
  }

  /**
   * create the HTML Parser/Filter to extract plain text.
   * 
   * @param result
   *          string builder to take the result.
   * @param metadata
   *          put extracted metadata in this map.
   * @return HTML parser/filter.
   */
  private XMLParserConfiguration createParser(final StringBuilder result, final MultiValueMap metadata) {
    final ElementRemover elementRemover = new ElementRemover();
    for (String tag : _removeContentTags) {
      elementRemover.removeElement(tag);
    }
    final CommentRemover commentRemover = new CommentRemover();
    final MetadataExtractor metadataExtractor = new MetadataExtractor(metadata);
    final PlainTextWriter writer = new PlainTextWriter(result);
    final XMLDocumentFilter[] filters = { commentRemover, metadataExtractor, elementRemover, writer };
    final XMLParserConfiguration parser = new HTMLConfiguration();
    parser.setProperty("http://cyberneko.org/html/properties/filters", filters);
    return parser;
  }

  /**
   * Append plain text from document to a string builder.
   * 
   * @author jschumacher
   * 
   */
  public class PlainTextWriter extends DefaultFilter {
    /**
     * target StringBuilder.
     */
    private StringBuilder _target;

    /**
     * @param target
     *          the StringBuilder to write to
     */
    public PlainTextWriter(StringBuilder target) {
      super();
      _target = target;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.cyberneko.html.filters.DefaultFilter#characters(org.apache.xerces.xni.XMLString,
     *      org.apache.xerces.xni.Augmentations)
     */
    @Override
    public void characters(XMLString text, Augmentations augs) {
      _target.append(text.ch, text.offset, text.length);
      super.characters(text, augs);
    }
  }

  /**
   * removes comments from HTML files.
   * 
   * @author jschumacher
   * 
   */
  public class CommentRemover extends DefaultFilter {
    /**
     * {@inheritDoc}
     * 
     * @see org.cyberneko.html.filters.DefaultFilter#comment(org.apache.xerces.xni.XMLString,
     *      org.apache.xerces.xni.Augmentations)
     */
    @Override
    public void comment(XMLString text, Augmentations augs) {
      // do nothing
    }
  }

  /**
   * extract metadata from META tags.
   * 
   * @author jschumacher
   * 
   */
  public class MetadataExtractor extends DefaultFilter {
    /**
     * attribute to value map extracted from document.
     */
    private final MultiValueMap _metadata;

    /**
     * boolean flag if the parser is in the title start tag.
     */
    private boolean _inTitleTag;

    /**
     * Buffer to store the content of the title tag.
     */
    private StringBuffer _titleBuffer = new StringBuffer();

    /**
     * @param metadata
     *          map to use as target for storing the attribute-value lists.
     */
    public MetadataExtractor(MultiValueMap metadata) {
      super();
      _metadata = metadata;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.cyberneko.html.filters.DefaultFilter#startElement(org.apache.xerces.xni.QName,
     *      org.apache.xerces.xni.XMLAttributes, org.apache.xerces.xni.Augmentations)
     */
    @Override
    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) {
      super.startElement(element, attributes, augs);
      if ("meta".equalsIgnoreCase(element.localpart)) {
        extractMetadata(element, attributes);
      } else if ("title".equalsIgnoreCase(element.localpart)) {
        _inTitleTag = true;
        _titleBuffer = new StringBuffer();
      }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.cyberneko.html.filters.DefaultFilter#emptyElement(org.apache.xerces.xni.QName,
     *      org.apache.xerces.xni.XMLAttributes, org.apache.xerces.xni.Augmentations)
     */
    @Override
    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) {
      super.emptyElement(element, attributes, augs);
      if ("meta".equalsIgnoreCase(element.localpart)) {
        extractMetadata(element, attributes);
      }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.cyberneko.html.filters.DefaultFilter#endElement(org.apache.xerces.xni.QName,
     *      org.apache.xerces.xni.Augmentations)
     */
    @Override
    public void endElement(QName element, Augmentations augs) {
      super.endElement(element, augs);
      if ("title".equalsIgnoreCase(element.localpart)) {
        _inTitleTag = false;
        setTitle();
      }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.cyberneko.html.filters.DefaultFilter#endElement(XMLString text, org.apache.xerces.xni.Augmentations)
     */
    @Override
    public void characters(XMLString text, Augmentations augs) {
      super.characters(text, augs);
      if (_inTitleTag) {
        _titleBuffer.append(text.toString());
      }
    }

    /**
     * check for META tag.
     * 
     * @param element
     *          current element.
     * @param attributes
     *          attributes of tag.
     */
    private void extractMetadata(QName element, XMLAttributes attributes) {
      String metaName = null;
      String metaValue = null;
      for (int i = 0; i < attributes.getLength(); i++) {
        final String attributeName = attributes.getLocalName(i);
        if ("name".equalsIgnoreCase(attributeName)) {
          metaName = attributes.getValue(i).trim().toLowerCase();
        } else if ("content".equalsIgnoreCase(attributeName)) {
          metaValue = attributes.getValue(i);
        }
      }
      if (metaName != null && metaValue != null) {
        final String attributeName = _metaAttributeMapping.get(metaName);
        if (attributeName != null) {
          _metadata.put(attributeName, metaValue);
        }
      }
    }

    /**
     * Adds the content of the _titleBuffer as an attribute value to the _metadata map.
     */
    private void setTitle() {
      final String attributeName = _metaAttributeMapping.get(PROP_TITLE);
      if (attributeName != null && _titleBuffer != null) {
        final String title = _titleBuffer.toString().trim();
        if (title.length() > 0) {
          _metadata.put(attributeName, title);
        }
      }
    }
  }
}

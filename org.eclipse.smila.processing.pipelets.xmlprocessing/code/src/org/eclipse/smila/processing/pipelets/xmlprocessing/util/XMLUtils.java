/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.processing.pipelets.xmlprocessing.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.parsers.XMLGrammarPreparser;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * All <code>parse()</code> methods must be given a wellformatted XML rescource, in which the <b>encoding</b> is set
 * forth, since none of them recieves such an parameter. If no encoding is specified in the XML recource, UTF-8 is
 * assumed as set forth by the W3C recommendation.
 * <p>
 * For all <code>stream()</code> methods without the encoding-paramter the default encoding UTF-8 is assumed (W3C
 * Recommendation?).
 * 
 * This is a merge of XMLUtils and XmlUtils classes provided by brox.
 */
public abstract class XMLUtils {

  /**
   * The SymbolTable.
   */
  private static SymbolTable s_symbolTable;

  /**
   * The GrammarPool.
   */
  private static XMLGrammarPoolImpl s_grammarPool;

  /**
   * Cached per JVM server IP.
   */
  private static String s_hexServerIP;

  /**
   * initialise the secure random instance.
   */
  private static final java.security.SecureRandom SEEDER = new java.security.SecureRandom();

  /**
   * a larg(ish) prime to use for a symbol table to be shared among potentially man parsers. Start one as close to 2K
   * (20 times larger than normal) and see what happens...
   */
  private static final int BIG_PRIME = 2039;

  /** Property identifier: symbol table. */
  private static final String SYMBOL_TABLE = Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;

  /** Property identifier: grammar pool. */
  private static final String GRAMMAR_POOL = Constants.XERCES_PROPERTY_PREFIX + Constants.XMLGRAMMAR_POOL_PROPERTY;

  /**
   * 
   */
  private static TransformerFactory s_transformerFactory = TransformerFactory.newInstance();

  /**
   * 
   */
  private static DocumentBuilderFactory s_builderFactory = DocumentBuilderFactory.newInstance();

  /**
   * Default Constructor.
   */
  private XMLUtils() {
  }

  /**
   * Creates a new Document.
   * 
   * @return the Document
   * @throws XmlException
   *           if any error occurs
   */
  public static Document newDocument() throws XmlException {
    try {
      final DocumentBuilder documentBuilder = s_builderFactory.newDocumentBuilder();
      final Document document = documentBuilder.newDocument();
      return document;
    } catch (final ParserConfigurationException e) {
      throw new XmlException(e);
    }

  }

  /**
   * Converts a Document to a String.
   * 
   * @param document
   *          the Document to convert
   * @return a String
   * @throws XmlException
   *           if any error occurs
   */
  public static String documentToString(Document document) throws XmlException {
    if (document == null) {
      return null;
    }
    try {
      final Transformer transformer = s_transformerFactory.newTransformer();
      final Source source = new DOMSource(document);
      final Writer writer = new StringWriter();
      final Result result = new StreamResult(writer);
      transformer.transform(source, result);
      return writer.toString();
    } catch (final TransformerConfigurationException e) {
      throw new XmlException(e);
    } catch (final TransformerException e) {
      throw new XmlException(e);
    }
  }

  /**
   * Returns a parser with set proerties and features as set in the XMLUtilsConfig Object. If any properties or features
   * are set there that are not supported or the value is wrong the exception is raised here!
   * 
   * @param cfg
   *          the XMLUtilsConfig
   * @return a DOMParser
   * @throws XMLUtilsException
   *           if any error occurs
   */
  private static synchronized DOMParser getParser(XMLUtilsConfig cfg) throws XMLUtilsException {
    final DOMParser parser = new DOMParser();

    /*
     * if (cfg.getValidate() != null && cfg.getValidate().booleanValue() && Version.fVersion.equals("Xerces 1.2.0")) {
     * 
     * //if version of parser to small throw new XMLUtilsException("Cant perform validation with Xerces 1.2.0"); }
     */

    if (cfg.getIncludeIgnorabelWhitespace() != null && !cfg.getIncludeIgnorabelWhitespace().booleanValue()
      && (cfg.getValidate() == null || !cfg.getValidate().booleanValue())) {
      throw new XMLUtilsException("When setting ignorableWhitespace, validate must be true too! "
        + "To remove all TextNodes containg only WS use method XMLUtils.removeWhitespaceTextNodes(Element)");
    }

    try {

      if (s_symbolTable != null) {
        parser.setProperty(SYMBOL_TABLE, s_symbolTable);
      }
      if (s_grammarPool != null) {
        parser.setProperty(GRAMMAR_POOL, s_grammarPool);
      }

      final Iterator<String> iter = cfg.getFeatures();
      while (iter.hasNext()) {
        final String feat = iter.next();
        parser.setFeature(feat, cfg.getFeatureValue(feat));
      }
    } catch (SAXException e) {
      throw new XMLUtilsException("Error while setting feature!", e);
    }
    // set features (and properties) -- last not yet supported
    parser.setErrorHandler(new DOMErrorHandler());

    return parser;
  }

  /**
   * Returns an empty document whose document element is not set ! The type of the document is by default an
   * XML-Document.
   * 
   * @return a Document
   */
  public static Document getDocument() {
    // TODO: add DocumentType ? <? xml version=1.0>
    return new DocumentImpl();
  }

  /**
   * Creates a document with a new document element with it's name set to the given text.
   * 
   * @param docElement
   *          a docElement
   * @return a Document
   */
  public static Document getDocument(String docElement) {
    final Document doc = new DocumentImpl();
    final Element el = doc.createElement(docElement);
    doc.appendChild(el);
    return doc;
  }

  /**
   * Shorthand for <code>parse(bytes, new XMLUtilsConfig(validate,true)).</code>
   * 
   * @param bytes
   *          a byte[]
   * @param validate
   *          boolean flag
   * @return a Document
   * @throws XMLUtilsException
   *           if any error occurs
   */
  public static Document parse(byte[] bytes, boolean validate) throws XMLUtilsException {

    return parse(new InputSource(new ByteArrayInputStream(bytes)), new XMLUtilsConfig(validate, true));
  }

  /**
   * Parses a document from the given byte-array.The applied encoding must be declared within the XML-document-instance
   * or otherwise UTF-8 is assumed according to the W3C recommendation.
   * 
   * @param bytes
   *          a byte[]
   * @param cfg
   *          a XMLUtilsConfig
   * @return a Document
   * @throws XMLUtilsException
   *           if cgf contains invalid values or parsing results in an error.
   */
  public static Document parse(byte[] bytes, XMLUtilsConfig cfg) throws XMLUtilsException {

    return parse(new InputSource(new ByteArrayInputStream(bytes)), cfg);

  }

  /**
   * Shorthand for <code>parse(file, new XMLUtilsConfig(validate,true)).</code>
   * 
   * @param file
   *          a File
   * @param validate
   *          boolean flag
   * @return a Document
   * @throws XMLUtilsException
   *           if any error occurs
   */
  public static Document parse(File file, boolean validate) throws XMLUtilsException {
    return parse(file, new XMLUtilsConfig(validate, true));
  }

  /**
   * Parses a document from the given File. The applied encoding must be declared within the XML-document-instance or
   * otherwise UTF-8 is assumed according to the W3C recommendation.
   * 
   * 
   * @param file
   *          a File
   * @param cfg
   *          the XMLUtilsConfig
   * @return a Document
   * @throws XMLUtilsException
   *           if the File dosn't point to a valid location, cgf contains invalid values, or parsing results in an
   *           error.
   */
  public static Document parse(File file, XMLUtilsConfig cfg) throws XMLUtilsException {
    try {
      return parse(new InputSource(new FileInputStream(file)), cfg);
    } catch (Throwable e) {
      throw new XMLUtilsException("File " + file.getAbsolutePath() + " not found.", e);
    }
  }

  /**
   * Shorthand for <code>parse(is , new XMLUtilsConfig(validate,true)).</code>
   * 
   * @param is
   *          a InputStream
   * @param validate
   *          boolean flag
   * @return a Document
   * @throws XMLUtilsException
   *           if any error occurs
   */
  public static Document parse(InputStream is, boolean validate) throws XMLUtilsException {
    return parse(is, new XMLUtilsConfig(validate, true));
  }

  /**
   * Parses a document from the given InputStream. The applied encoding must be declared within the
   * XML-document-instance or otherwise UTF-8 is assumed according to the W3C recommendation.
   * 
   * @param is
   *          a InputStream
   * @param cfg
   *          the XMLUtilsConfig
   * @return a Document
   * @throws XMLUtilsException
   *           if cfg contains invalid values or parsing results in an error.
   */
  public static Document parse(InputStream is, XMLUtilsConfig cfg) throws XMLUtilsException {
    return parse(new InputSource(is), cfg);
  }

  /**
   * Parses a document from the given InputStream.
   * 
   * @param inputSource
   *          a InputSource
   * @param cfg
   *          the XMLUtilsConfig
   * @return a Document
   * @throws XMLUtilsException
   *           if cgf contains invalid values or parsing results in an error.
   */
  private static Document parse(InputSource inputSource, XMLUtilsConfig cfg) throws XMLUtilsException {
    final DOMParser parser = getParser(cfg);

    try {
      parser.parse(inputSource);

      return parser.getDocument();
    } catch (Exception e) {
      throw new XMLUtilsException("Error while parsing XML document!", e);
    }
  }

  /**
   * Shorthand for <code>stream(el , validate, "UTF-8")).</code>
   * 
   * @param el
   *          a Element
   * @param validate
   *          boolean flag
   * @return a byte[]
   * @throws XMLUtilsException
   *           if any exception occurs
   */
  public static byte[] stream(Element el, boolean validate) throws XMLUtilsException {
    return stream(el, validate, "UTF-8", false);
  }

  /**
   * Shorthand for <code>stream(el , validate,
   * "UTF-8")).</code>
   * 
   * @param el
   *          a Element
   * @param validate
   *          boolean flag
   * @param preserveSpace
   *          boolean flag
   * @return a byte[]
   * @throws XMLUtilsException
   *           if any exception occurs
   */
  public static byte[] stream(Element el, boolean validate, boolean preserveSpace) throws XMLUtilsException {
    return stream(el, validate, "UTF-8", preserveSpace);
  }

  /**
   * Streams the given DOM-(Sub)tree starting at the given Element into the returned byte-array.
   * 
   * @param el
   *          a Element
   * @param validate
   *          whether the given DOM-(sub)tree shall be validated against an XML-Schema that is referenced in the given
   *          element.
   * @param enc
   *          the encoding that shall be applied. The given String must conform to the encodings as set forth by the W3C
   *          and are somewhat different to that of Java.
   * @param preserveSpace
   *          boolean flag
   * @return a byte[]
   * @throws XMLUtilsException
   *           if any exception occurs
   */
  public static byte[] stream(Element el, boolean validate, String enc, boolean preserveSpace)
    throws XMLUtilsException {
    /*
     * this is a fully implemented method not relying on anyother than the parse method, to achieve highest performance!
     */

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] xml = null;

    try {
      // create & configure OutputFormat
      final OutputFormat of = new OutputFormat("XML", enc, true);
      of.setIndent(2);
      of.setLineWidth(0);
      if (preserveSpace) {
        of.setPreserveSpace(preserveSpace);
      }

      // stream
      final XMLSerializer xs = new XMLSerializer(baos, of);
      xs.setNamespaces(true);
      xs.serialize(el);

      xml = baos.toByteArray();
      baos.close();

    } catch (Exception e) {
      throw new XMLUtilsException("Failed to stream due to: ", e);
    }

    // validate the produced document tree
    try {
      /* validation is performed here afterwards to save doing things twice */

      if (validate) {
        XMLUtils.parse(xml, validate);
      }

      return xml;
    } catch (XMLUtilsException e) {
      throw new XMLUtilsException("The given xml document is not valid!", e);
    } finally {
      try {
        if (baos != null) {
          baos.close();
        }
      } catch (IOException e) {
      }
    }

  }

  /**
   * Streams the given DOM-(Sub)tree starting at the given Element into the given File. *
   * 
   * @param el
   *          a Element
   * 
   * @param enc
   *          the encoding that shall be applied. The given String must conform to the encodings as set forth by the W3C
   *          and are somewhat different to that of Java.
   * @param validate
   *          whether the given DOM-(sub)tree shall be validated against an XML-Schema that is referenced in the given
   *          element.
   * @param file
   *          if it exist it will be overwritten otherwise created. If access rights are violated an XMLUtilsExcpetion
   *          is thrown.
   * @throws XMLUtilsException
   *           if any error occurs
   */
  public static void stream(Element el, boolean validate, String enc, File file) throws XMLUtilsException {

    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(file);
      stream(el, validate, enc, fos);
      fos.close();
    } catch (FileNotFoundException e) {
      throw new XMLUtilsException("File not found.", e);
    } catch (IOException e) {
      throw new XMLUtilsException("Other file-access exception", e);
    } finally {
      try {
        if (fos != null) {
          fos.close();
        }
      } catch (IOException e) {
      }
    }

  }

  /**
   * Streams the given DOM-(Sub)tree starting at the given Element into the returned byte-array.
   * 
   * @param el
   *          a Element
   * @param enc
   *          the encoding that shall be applied. The given String must conform to the encodings as set forth by the W3C
   *          and are somewhat different to that of Java.
   * @param validate
   *          whether the given DOM-(sub)tree shall be validated against an XML-Schema that is referenced in the given
   *          element.
   * @param os
   *          a OutputStream
   * @throws XMLUtilsException
   *           if any error occurs
   */
  public static void stream(Element el, boolean validate, String enc, OutputStream os) throws XMLUtilsException {

    if (validate) {
      validate(el);
    }

    try {
      // create & configure OutputFormat
      final OutputFormat of = new OutputFormat("XML", enc, true);
      of.setIndent(2);
      of.setLineWidth(0);

      // stream
      final XMLSerializer xs = new XMLSerializer(os, of);
      xs.setNamespaces(true);
      xs.serialize(el);
    } catch (Exception e) {
      throw new XMLUtilsException("Failed to stream due to: ", e);
    }
  }

  /**
   * Validates the given element and it's decendants against the XML-Schema as specified in the given element. When
   * validation has been succesfull true is returned but an exception if the document isnt valid, since this is in most
   * cases the desired result anyways.
   * <p>
   * Unless someone finds a way to validate a life DOM-Tree against a given schema, the quickest way to do this is to,
   * write the damn thing into an ByteArrayOutputStream and to read from that again, and while reading it perform a
   * validation.
   * 
   * @param el
   *          a Element
   * @return a boolean
   * @throws XMLUtilsException
   *           if any error occurs
   * 
   */
  public static boolean validate(Element el) throws XMLUtilsException {
    /*
     * this is a fully implemented method not relying on any other than the parse method, to achieve highest
     * performance! Encoding US-ASCII is supposed to be the fasted, since it can be expected to have no need of many
     * other letters than those in that set. (see XERXES FAQ) Also pretty-printing is turned off!
     */

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] xml = null;

    // stream the (sub-)tree
    try {
      // create & configure OutputFormat
      final OutputFormat of = new OutputFormat("XML", "UTF-8", false);
      of.setIndent(0);
      of.setLineWidth(0);

      // stream
      final XMLSerializer xs = new XMLSerializer(baos, of);
      xs.setNamespaces(true);
      xs.serialize(el);

      xml = baos.toByteArray();
      baos.close();

    } catch (Exception e) {
      throw new XMLUtilsException("Failed to stream due to: ", e);
    } finally {
      try {
        if (baos != null) {
          baos.close();
        }
      } catch (IOException e) {
      }
    }

    // validate the produced document tree by (re)parsing it
    try {
      XMLUtils.parse(xml, true);
      return true;
    } catch (XMLUtilsException e) {
      throw new XMLUtilsException("The given XML document is not valid!", e);
    }
  }

  /**
   * Traverses through a DOM tree starting at the given element and removes all those text-nodes from it that only
   * contain white spaces.
   * 
   * @param parent
   *          the parent Element
   */
  public static void removeWhitespaceTextNodes(Element parent) {
    final NodeList nl = parent.getChildNodes();

    for (int i = 0; i < nl.getLength(); i++) {
      final Node child = nl.item(i);

      if (child.getNodeType() == Node.TEXT_NODE) {
        if (child.getNodeValue().trim().length() == 0) {
          parent.removeChild(child);
          i--; // since the child is removed counting up must be made undone
        }
      } else if (child.getNodeType() == Node.ELEMENT_NODE && child.getChildNodes().getLength() > 0) {
        removeWhitespaceTextNodes((Element) child);
      }
    }
  }

  /**
   * Decodes a Date.
   * 
   * @param xmlDate
   *          the xmlDate
   * @return a Date
   * @throws ParseException
   *           if any error occurs
   */
  public static Date decodeDate(String xmlDate) throws ParseException {
    final SimpleDateFormat df = new SimpleDateFormat();
    df.applyPattern("yyyy-MM-dd");
    return df.parse(xmlDate);
  }

  /**
   * Encodes a date.
   * 
   * @param javaDate
   *          a Date
   * @return a String
   */
  public static String encodeDate(Date javaDate) {
    final SimpleDateFormat df = new SimpleDateFormat();
    df.applyPattern("yyyy-MM-dd");
    return df.format(javaDate);
  }

  /**
   * Decodes a DateTime.
   * 
   * @param xmlDate
   *          the xmlDate
   * @return Date
   * @throws ParseException
   *           if any error occurs
   */
  public static Date decodeDateTime(String xmlDate) throws ParseException {
    final SimpleDateFormat df = new SimpleDateFormat();
    df.applyPattern("yyyy-MM-dd'T'hh:mm:ss");
    return df.parse(xmlDate);
  }

  /**
   * Encodes a DateTime.
   * 
   * @param javaDate
   *          a date
   * @return a String
   */
  public static String encodeDateTime(Date javaDate) {
    final SimpleDateFormat df = new SimpleDateFormat();
    df.applyPattern("yyyy-MM-dd'T'hh:mm:ss");
    return df.format(javaDate);
  }

  /**
   * Decodes a Boolean.
   * 
   * @param xmlBoolean
   *          the xmlBoolean
   * @return a boolean
   */
  public static boolean decodeBoolean(String xmlBoolean) {
    return "true".equals(xmlBoolean) || "yes".equals(xmlBoolean) || "on".equals(xmlBoolean)
      || "1".equals(xmlBoolean);
  }

  /**
   * Encodes a boolean.
   * 
   * @param javaBoolean
   *          a boolean
   * @return a String
   */
  public static String encodeBoolean(boolean javaBoolean) {
    if (javaBoolean) {
      return "true";
    } else {
      return "false";
    }
  }

  /**
   * Decodes an Integer.
   * 
   * @param xmlInteger
   *          the xmlInteger
   * @return a int
   */
  public static int decodeInteger(String xmlInteger) {
    return new Integer(xmlInteger).intValue();
  }

  /**
   * Encodes and Integer.
   * 
   * @param value
   *          a int
   * @return a String
   */
  public static String encodeInteger(int value) {
    return value + "";
  }

  /**
   * Encodes and Integer.
   * 
   * @param value
   *          Integer
   * @return a String
   */
  public static String encodeInteger(Integer value) {
    return value.intValue() + "";
  }

  /**
   * This method converts a given XML attribute or element name to a local representation by stripping it of its
   * namespace prefix.
   * 
   * @param xml
   *          the xml
   * @return String
   */
  public static String getLocalPart(String xml) {
    final int pos;
    if ((pos = xml.indexOf(":")) >= 0) {
      xml = xml.substring(pos + 1);
    }
    return xml;
  }

  /**
   * Checks if the given file is XML.
   * 
   * @param file
   *          a File
   * @return boolean
   */
  public static boolean isXML(File file) {
    boolean retval = false;

    try {
      XMLUtils.parse(file, false);
      retval = true;
    } catch (XMLUtilsException e) {
    }

    return retval;
  }

  /**
   * Checks if the given byte[] is XML.
   * 
   * @param message
   *          a byte[]
   * @return boolean
   */
  public static boolean isXML(byte[] message) {
    boolean retval = false;

    try {
      XMLUtils.parse(message, false);
      retval = true;
    } catch (XMLUtilsException e) {
    }

    return retval;
  }

  /**
   * Checks if the given InputStream is XML.
   * 
   * @param is
   *          a InputStream
   * @return boolean
   */
  public static boolean isXML(InputStream is) {
    boolean retval = false;

    try {
      XMLUtils.parse(is, false);
      retval = true;
    } catch (XMLUtilsException e) {
    }

    return retval;
  }

  /**
   * Load schemas.
   * 
   * @param folder
   *          the folder
   * @param filter
   *          the filter
   * @param schemaUrlPrefix
   *          the schemaUrlPrefix
   * @throws XMLUtilsException
   *           if any error occurs
   */
  public static synchronized void loadSchemas(File folder, final String filter, String schemaUrlPrefix)
    throws XMLUtilsException {
    try {
      final File[] schemas = folder.listFiles(new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return name.matches(filter);
        }
      });
      for (int i = 0; i < schemas.length; i++) {
        FileInputStream fis = null;
        try {
          fis = new FileInputStream(schemas[i]);
          loadSchema(schemaUrlPrefix + schemas[i].getName(), fis);
        } catch (IOException e) {
          throw e;
        } finally {
          if (fis != null) {
            try {
              fis.close();
            } catch (IOException e) {
            }
          }
        }
      }
    } catch (IOException e) {
      throw new XMLUtilsException("unable to cache schemas", e);
    }
  }

  /**
   * Load schema.
   * 
   * @param schemaName
   *          the schemaName
   * @param schema
   *          a InputStream
   * @throws XMLUtilsException
   *           if any error occurs
   */
  public static synchronized void loadSchema(String schemaName, InputStream schema) throws XMLUtilsException {
    if (s_symbolTable == null) {
      s_symbolTable = new SymbolTable(BIG_PRIME);
      s_grammarPool = new XMLGrammarPoolImpl();
    }

    final XMLGrammarPreparser preparser = new XMLGrammarPreparser(s_symbolTable);
    preparser.registerPreparser(XMLGrammarDescription.XML_SCHEMA, null);
    preparser.setProperty(XMLUtils.GRAMMAR_POOL, s_grammarPool);
    try {
      final Grammar g =
        preparser.preparseGrammar(XMLGrammarDescription.XML_SCHEMA, new XMLInputSource(null, schemaName, null,
          schema, null));
      if (g != null) {
        // remove compiler warning (g is not used)
      }
    } catch (IOException e) {
      throw new XMLUtilsException("unable to cache schema [" + schemaName + "]", e);
    }
  }

  /**
   * Clear grammar cache.
   */
  public static synchronized void clearGrammarCache() {
    s_symbolTable = null;
    s_grammarPool = null;
  }

  /**
   * Sets the grammar cache lock.
   * 
   * @param lockState
   *          boolean flag
   */
  public static synchronized void setGrammarCacheLock(boolean lockState) {
    if (s_grammarPool != null) {
      if (lockState) {
        s_grammarPool.lockPool();
      } else {
        s_grammarPool.unlockPool();
      }
    }
  }

  /**
   * A 32 byte GUID generator (Globally Unique ID). These artificial keys SHOULD <strong>NOT </strong> be seen by the
   * user, not even touched by the DBA but with very rare exceptions, just manipulated by the database and the programs.
   * 
   * Usage: Add an id field (type java.lang.String) to your EJB, and add setId(XXXUtil.generateGUID(this)); to the
   * ejbCreate method.
   * 
   * @param o
   *          a Object
   * @return a String
   */
  public static final String generateGUID(Object o) {
    final Log log = LogFactory.getLog(XMLUtils.class);
    final StringBuffer tmpBuffer = new StringBuffer(16);
    if (s_hexServerIP == null) {
      java.net.InetAddress localInetAddress = null;
      try {
        // get the inet address
        localInetAddress = java.net.InetAddress.getLocalHost();
      } catch (java.net.UnknownHostException uhe) {
        // todo: find better way to get around this...
        final String msg =
          "ConfigurationUtil: Could not get the local IP address using InetAddress.getLocalHost()!";
        System.err.println(msg);
        if (log.isErrorEnabled()) {
          log.error(uhe);
        }
        return null;
      }
      final byte[] serverIP = localInetAddress.getAddress();
      s_hexServerIP = hexFormat(getInt(serverIP), 8);
    }
    final String hashcode = hexFormat(System.identityHashCode(o), 8);
    tmpBuffer.append(s_hexServerIP);
    tmpBuffer.append(hashcode);

    final long timeNow = System.currentTimeMillis();
    final int timeLow = (int) timeNow & 0xFFFFFFFF;
    final int node = SEEDER.nextInt();

    final StringBuffer guid = new StringBuffer(32);
    guid.append(hexFormat(timeLow, 8));
    guid.append(tmpBuffer.toString());
    guid.append(hexFormat(node, 8));
    return guid.toString();
  }

  /**
   * Get int.
   * 
   * @param bytes
   *          a byte[]
   * @return a int
   */
  private static int getInt(byte[] bytes) {
    int i = 0;
    int j = 24;
    for (int k = 0; j >= 0; k++) {
      final int l = bytes[k] & 0xff;
      i += l << j;
      j -= 8;
    }
    return i;
  }

  /**
   * Hex format.
   * 
   * @param i
   *          a int
   * @param j
   *          a int
   * @return a String
   */
  private static String hexFormat(int i, int j) {
    final String s = Integer.toHexString(i);
    return padHex(s, j) + s;
  }

  /**
   * Pad Hex.
   * 
   * @param s
   *          a String
   * @param i
   *          a int
   * @return a String
   */
  private static String padHex(String s, int i) {
    final StringBuffer tmpBuffer = new StringBuffer();
    if (s.length() < i) {
      for (int j = 0; j < i - s.length(); j++) {
        tmpBuffer.append('0');
      }
    }
    return tmpBuffer.toString();
  }

  /**
   * Converts a Document into a byte[].
   * 
   * @param document
   *          the Document
   * @return a byte[]
   * @throws XmlException
   *           if any error occurs
   */
  public static byte[] documentToBytes(Document document) throws XmlException {
    if (document == null) {
      return null;
    }
    try {
      final Source source = new DOMSource(document);
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final Result result = new StreamResult(out);
      final Transformer transformer = s_transformerFactory.newTransformer();
      transformer.transform(source, result);
      return out.toByteArray();
    } catch (TransformerConfigurationException e) {
      throw new XmlException(e);
    } catch (TransformerException e) {
      throw new XmlException(e);
    }
  }

}

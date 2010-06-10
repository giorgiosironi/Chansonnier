/*******************************************************************************
 * Copyright (c) 2010 Empolis GmbH and brox IT Solutions GmbH. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Igor Novakovic (Empolis GmbH) - initial implementation
 *******************************************************************************/

package org.eclipse.smila.integration.solr;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.dom.IdBuilder;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.SearchMessage;
import org.eclipse.smila.processing.SearchPipelet;
import org.eclipse.smila.processing.SimplePipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.parameters.ParameterAccessor;
import org.eclipse.smila.processing.parameters.SearchAnnotations;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * This pipelet integrates the Apache Solr's REST API and is designed to be used
 * both in processing and in search pipelines.
 * 
 * */
public class SolrPipelet implements SimplePipelet, SearchPipelet {

	private static final String UPDATE = "update";
	private static final String HIGHLIGHT_PARAMS = "highlight.params";
	private static final String COMMIT_WITHIN = "commitWithin";
	private static final String OVERWRITE = "overwrite";
	private static final String HIGHLIGHT = "highlight";
	private static final String DEFAULT_SEARCH_TERM = "SMILA";
	private static final String CONTENT_LENGTH = "Content-Length";
	private static final String RESPONSE_HEADER = "ResponseHeader";
	private static final String META_DATA = "MetaData";
	private static final String SELECT = "select?";
	private static final String TEXT_XML_CHARSET = "text/xml; charset=";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String SHARDS = "shards";
	private static final String HTTP_LOCALHOST = "http://localhost";
	private static final String SOLR_WEBAPP = ":8983/solr/";
	private static final String GET = "GET";
	private static final String POST = "POST";
	private static final String INDEX_NAME = "indexName";
	private static final String ALLOW_DOUBLETS = "allowDoublets";
	private static final String EXECUTION_MODE = "executionMode";
	private static final String FIELD = "field";
	private static final String ADD = "add";
	private static final String DELETE = "delete";
	public static final String UTF8 = "utf-8";
	private static final SAXParserFactory pf = SAXParserFactory.newInstance();
	private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory
			.newInstance();

	private String[] _shards = null;
	private boolean _highlight = true;
	private String[] _highlightParams = null;
	private ExecutionMode _mode = ExecutionMode.ADD;
	private boolean _allowDoublets = false;
	private int _commitWithin = 10000;
	private String _indexName = "";
	private Log _log = LogFactory.getLog(SolrPipelet.class);

	public enum ExecutionMode {
		ADD, DELETE
	};

	public Id[] process(Blackboard blackboard, Id[] recordIds)
			throws ProcessingException {
		String updateURL = HTTP_LOCALHOST + SOLR_WEBAPP + UPDATE;
		String updateXMLMessage = null;
		URL url = null;
		HttpURLConnection conn = null;
		Id _id = null;
		try {
			url = new URL(updateURL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(POST);
			conn.setRequestProperty(CONTENT_TYPE, TEXT_XML_CHARSET + UTF8);
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(10000);
		} catch (Exception e) {
			String msg = "Error while opening Solr connection: '"
					+ e.getMessage() + "'";
			_log.error(msg, e);
			throw new ProcessingException(msg, e);
		}
		try {
			DOMImplementation impl = DOMImplementationImpl
					.getDOMImplementation();
			Document document = impl.createDocument(null,
					SolrResponseHandler.SOLR, null);
			Element add = null;
			if (_mode == ExecutionMode.ADD) {
				add = document.createElement(ADD);
			} else {
				add = document.createElement(DELETE);
			}
			if (_allowDoublets) {
				add.setAttribute(OVERWRITE, "false");
			} else {
				add.setAttribute(OVERWRITE, "true");
			}
			add.setAttribute(COMMIT_WITHIN, String.valueOf(_commitWithin));

			for (Id id : recordIds) {
				_id = id;
				Element doc = document.createElement(SolrResponseHandler.DOC);
				add.appendChild(doc);

				// Create id attribute
				Element field = document.createElement(FIELD);
				field.setAttribute(SolrResponseHandler.NAME,
						SolrResponseHandler.ID);
				IdBuilder idBuilder = new IdBuilder();
				String idXML = idBuilder.idToString(id);
				String idEncoded = URLEncoder.encode(idXML, UTF8);
				Text text = document.createTextNode(idEncoded);
				field.appendChild(text);
				doc.appendChild(field);

				// Create all other attributes
				Iterator<String> i = blackboard.getAttributeNames(id);
				while (i.hasNext()) {
					String attrName = i.next();
					if (!attrName.startsWith(META_DATA)
							&& !attrName.startsWith(RESPONSE_HEADER)) {
						Path path = new Path(attrName);
						Iterator<Literal> literals = blackboard.getLiterals(id,
								path).iterator();
						while (literals.hasNext()) {
							Literal value = literals.next();
							String stringValue = null;
							if (Literal.DataType.DATE.equals(value
									.getDataType())) {
								SimpleDateFormat df = new SimpleDateFormat(
										SolrResponseHandler.DATE_FORMAT_PATTERN);
								stringValue = df.format(value.getDateValue());
							} else if (Literal.DataType.DATETIME.equals(value
									.getDataType())) {
								SimpleDateFormat df = new SimpleDateFormat(
										SolrResponseHandler.DATE_FORMAT_PATTERN);
								stringValue = df.format(value
										.getDateTimeValue());
							} else {
								stringValue = replaceNonXMLChars(value
										.getStringValue());
							}
							field = document.createElement(FIELD);
							field.setAttribute(SolrResponseHandler.NAME,
									attrName);
							text = document.createTextNode(stringValue);
							field.appendChild(text);
							doc.appendChild(field);
						}
					}
				}
			}
			Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
			if (_log.isDebugEnabled()) {
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			}
			DOMSource source = new DOMSource(add);
			Writer w = new StringWriter();
			StreamResult streamResult = new StreamResult(w);
			transformer.transform(source, streamResult);
			updateXMLMessage = streamResult.getWriter().toString();
			conn.setRequestProperty(CONTENT_LENGTH, Integer
					.toString(updateXMLMessage.length()));
			DataOutputStream os = new DataOutputStream(conn.getOutputStream());
			os.write(updateXMLMessage.getBytes(UTF8));
			os.flush();
			os.close();
			System.out.println(updateXMLMessage);

			BufferedReader rd = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			System.out.println("Response:\n" + response.toString());
		} catch (Exception e) {
			String msg = "Error while processing record '" + _id
					+ "' for index '" + _indexName + "': " + e.getMessage()
					+ "'.";
			_log.error(msg, e);
			if (_log.isDebugEnabled()) {
				try {
					FileOutputStream fos = new FileOutputStream(_id.getIdHash()
							+ ".xml");
					fos.write(updateXMLMessage.getBytes(UTF8));
					fos.flush();
					fos.close();
				} catch (Exception ee) {
					throw new ProcessingException(msg, ee);
				}
			}
			throw new ProcessingException(msg, e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return recordIds;
	}

	public SearchMessage process(Blackboard blackboard, SearchMessage message)
			throws ProcessingException {
		if (message.hasQuery()) {
			ParameterAccessor parameters = new ParameterAccessor(blackboard,
					message.getQuery());
			String query = parameters.getQuery();
			int resultSize = parameters.getResultSize();
			int resultOffset = parameters.getResultOffset();
			List<String> resultAttributes = parameters.getResultAttributes();
			// Threshold seems not to be implemented in Solr, so we just ignore
			// it for now.
			// double threshold = parameters.getThreshold();
			Id queryId = message.getQuery();

			HttpURLConnection conn = null;
			List<Id> rIds = new ArrayList<Id>();

			String searchURL = HTTP_LOCALHOST + SOLR_WEBAPP + SELECT;
			if (_shards != null) {
				searchURL += "shards=";
				for (String shard : _shards) {
					searchURL += shard + SOLR_WEBAPP + ",";
				}
			}
			if (_highlight) {
				searchURL += "&hl=true";
			}
			if (_highlightParams != null) {
				for (String hp : _highlightParams) {
					searchURL += "&" + hp;
				}
			}
			searchURL += "&start=" + resultOffset;
			searchURL += "&rows=" + resultSize;
			// Include requested attributes and scores
			// into the result.
			if (resultAttributes != null) {
				// We need to retrieve id explicitly
				searchURL += "&fl=id,";
				for (String ra : resultAttributes) {
					searchURL += ra + ",";
				}
				searchURL += "score";
			}
			searchURL += "&indent=true&q=";
			try {
				if (query != null) {
					searchURL += URLEncoder.encode(query, UTF8);
				} else {
					// We have to set any value for a query otherwise we get
					// error 500
					searchURL += DEFAULT_SEARCH_TERM;
				}
				URL url = new URL(searchURL);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod(GET);
				conn.setDoOutput(true);
				conn.setReadTimeout(10000);
				conn.connect();

				InputStream is = conn.getInputStream();
				SAXParser p = pf.newSAXParser();
				SolrResponseHandler srh = new SolrResponseHandler(blackboard,
						rIds);
				p.parse(is, srh);
				String totalHits = Integer.toString(srh.noOfHits);
				Annotation resultAnno = blackboard.getAnnotation(queryId, null,
						SearchAnnotations.RESULT);
				if (resultAnno == null) {
					resultAnno = blackboard.createAnnotation(queryId);
					blackboard.setAnnotation(queryId, null,
							SearchAnnotations.RESULT, resultAnno);
				}
				resultAnno.setNamedValue(SearchAnnotations.TOTAL_HITS,
						totalHits);
			} catch (Exception e) {
				String msg = "Error while while processing search request: '"
						+ e.getMessage() + "'.";
				_log.error(msg, e);
				throw new ProcessingException(msg, e);
			} finally {
				conn.disconnect();
				conn = null;
			}
			message.setRecords(rIds);
		}
		return message;
	}

	public void configure(PipeletConfiguration config)
			throws ProcessingException {
		// Search configuration parameters
		_shards = config.getPropertyStringValues(SHARDS);
		Object configValue = config.getPropertyFirstValue(HIGHLIGHT);
		if (configValue != null) {
			_highlight = (Boolean) configValue;
		}
		_highlightParams = config.getPropertyStringValues(HIGHLIGHT_PARAMS);

		// Indexing configuration parameters
		configValue = config.getPropertyFirstValue(EXECUTION_MODE);
		if (configValue != null) {
			String mode = (String) configValue;
			try {
				_mode = ExecutionMode.valueOf(mode);
			} catch (IllegalArgumentException iae) {
				String msg = "Error while configuring SolrPipelet: mode '"
						+ mode + "' is not supported!";
				_log.error(msg);
				throw new ProcessingException(msg, iae);
			}
		}
		configValue = config.getPropertyFirstValue(ALLOW_DOUBLETS);
		if (configValue != null) {
			_allowDoublets = (Boolean) configValue;
		}
		configValue = config.getPropertyFirstValue(COMMIT_WITHIN);
		if (configValue != null) {
			_commitWithin = (Integer) configValue;
		}

		// Currently this pipelet does not support more than one
		// index i.e. core, but we read this parameter anyway.
		// Note: In Solr an index is called a core.
		configValue = config.getPropertyFirstValue(INDEX_NAME);
		if (configValue != null) {
			_indexName = (String) configValue;
		}

		// Pass own reference to a JXM agent, but do this
		// only if we are in search pipeline.
		if (_shards.length != 0) {
			new SolrPipeletAgent(this);
		}
	}

	public void setShards(String shards) {
		if (!shards.isEmpty()) {
			String[] nodes = shards.split(",");
			List<String> sList = new ArrayList<String>();
			for (int i = 0; i < nodes.length; i++) {
				if (!nodes[i].isEmpty()) {
					sList.add(nodes[i]);
				}
			}
			_shards= sList.toArray(new String[]{});
		}
	}

	public String getShards() {
		String shardsStr = new String();
		if (_shards != null) {
			for (String shard : _shards) {
				shardsStr += shard + ",";
			}
		}
		return shardsStr;
	}

	/**
	 * This method replaces valid UTF-8 characters which are not allowed in XML
	 * with spaces.
	 * 
	 * @param source
	 *            - the original source string
	 * @return The filtered string
	 */
	public static String replaceNonXMLChars(String source) {
		if (source == null)
			return null;
		StringBuffer sb = new StringBuffer();
		char[] charArray = source.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			char c = charArray[i];
			if (c != '\u0000' && c != '\u0001' && c != '\u0002'
					&& c != '\u0003' && c != '\u0004' && c != '\u0005'
					&& c != '\u0006' && c != '\u0007' && c != '\u0008'
					&& c != '\u000B' && c != '\u000C' && c != '\u000E'
					&& c != '\u000F' && c != '\u0010' && c != '\u0011'
					&& c != '\u0012' && c != '\u0013' && c != '\u0014'
					&& c != '\u0015' && c != '\u0016' && c != '\u0017'
					&& c != '\u0018' && c != '\u0019' && c != '\u001A'
					&& c != '\u001B' && c != '\u001C' && c != '\u001D'
					&& c != '\u001E' && c != '\u001F' && c != '\uFFFE'
					&& c != '\uFFFF') {
				sb.append(c);
			} else {
				sb.append(' ');
			}
		}
		return sb.toString();
	}
}

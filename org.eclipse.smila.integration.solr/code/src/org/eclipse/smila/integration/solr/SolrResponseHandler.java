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

import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.blackboard.path.PathStep;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.id.dom.IdParser;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.processing.parameters.SearchAnnotations;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SolrResponseHandler extends DefaultHandler {

	private static final String TEXT = "text";
	private static final String HIGHLIGHT = "highlight";
	private static final String HIGHLIGHTING = "highlighting";
	private static final String LST = "lst";
	private static final String MAX_SCORE = "maxScore";
	private static final String NUM_FOUND = "numFound";
	private static final String RESULT = "result";
	private static final String ARR = "arr";
	private static final String STR = "str";
	private static final String BOOL = "bool";
	private static final String INT = "int";
	private static final String FLOAT = "float";
	private static final String DATE = "date";
	private static final String SCORE = "score";
	private static final RecordFactory rf = RecordFactory.DEFAULT_INSTANCE;

	private Blackboard _b = null;
	private List<Id> _ids = null;
	private Id _id = null;
	private String _tagName = null;
	private String _attrName = null;
	private boolean _mvAttr = false;
	private boolean _highlight = false;
	private String _hl = "";
	private double _maxScore = 1.0;
	private Log _log = LogFactory.getLog(SolrResponseHandler.class);

	public int noOfHits = 0;
	public static final String SOLR = "solr";
	public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String DOC = "doc";
	public static final String NAME = "name";
	public static final String ID = "id";

	public SolrResponseHandler(Blackboard blackboard, List<Id> rrIds) {
		_b = blackboard;
		_ids = rrIds;
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		try {
			if (qName.equals(ARR)) {
				_mvAttr = false;
				if (_highlight) {
					Annotation annotation = _b.createAnnotation(_id);
					annotation.setNamedValue(TEXT, _hl);
					Path path = new Path().add(_attrName,
							PathStep.ATTRIBUTE_ANNOTATION);
					_b.setAnnotation(_id, path, HIGHLIGHT, annotation);
				}
				_hl = "";
			}
			if (qName.equals(DOC)) {
				_id = null;
			}
			_tagName = null;
		} catch (Exception e) {
			_log.error("Error while closeing a tag'" + _tagName + "'.", e);
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String value = new String(ch, start, length);
		try {
			if (_id != null && _tagName != null && !_highlight) {
				Literal literal = rf.createLiteral();
				if (_tagName.equals(STR)) {
					if (!_mvAttr) {
						if (_attrName.equals(ID)) {
							IdParser idParser = new IdParser();
							String idDecoded = URLDecoder.decode(value,
									SolrPipelet.UTF8);
							_id = idParser.parseIdFrom(idDecoded);
							Record r = _b.getRecord(_id);
							r.setId(_id);
							_ids.add(_id);
						} else {
							literal.setStringValue(value);
							_b.addLiteral(_id, new Path(_attrName), literal);
						}
					} else {
						literal.setStringValue(value);
						_b.addLiteral(_id, new Path(_attrName), literal);
					}
				} else if (_tagName.equals(BOOL)) {
					Boolean attrValue = new Boolean(value);
					literal.setBoolValue(attrValue);
					_b.addLiteral(_id, new Path(_attrName), literal);
				} else if (_tagName.equals(DATE)) {
					DateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN);
					Date attrValue = df.parse(value);
					literal.setDateTimeValue(attrValue);
					_b.addLiteral(_id, new Path(_attrName), literal);
				} else if (_tagName.equals(INT)) {
					Integer attrValue = new Integer(value);
					literal.setIntValue(attrValue);
					_b.addLiteral(_id, new Path(_attrName), literal);
				} else if (_tagName.equals(FLOAT)) {
					if (_attrName.equals(SCORE)) {
						Double rawScore = Double.parseDouble(value);
						Double normalizedScore = rawScore / _maxScore;
						String nScore = Double.toString(normalizedScore);
						Annotation recordAnno = _b.createAnnotation(_id);
						recordAnno.setNamedValue(SearchAnnotations.RELEVANCE,
								nScore);
						_b.setAnnotation(_id, null, SearchAnnotations.RESULT,
								recordAnno);
					} else {
						Double attrValue = new Double(value);
						literal.setFpValue(attrValue);
						_b.addLiteral(_id, new Path(_attrName), literal);
					}
				}
			} else if (_id != null && _tagName != null && _highlight) {
				if (_tagName.equals(STR)) {
					if (_mvAttr) {
						_hl += value;
					}
				}
			}
		} catch (Exception e) {
			_log.error("Error while extracting characters '" + value
					+ "' for tag'" + _tagName + "'.", e);
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		_tagName = qName;
		try {
			if (_tagName.equals(DOC)) {
				_id = IdFactory.DEFAULT_INSTANCE.createId(SOLR, Double
						.toString(System.nanoTime()));
				_b.create(_id);
			} else if (_tagName.equals(STR)) {
				if (!_mvAttr) {
					_attrName = attributes.getValue(NAME);
				}
			} else if (_tagName.equals(BOOL)) {
				_attrName = attributes.getValue(NAME);
			} else if (_tagName.equals(DATE)) {
				_attrName = attributes.getValue(NAME);
			} else if (_tagName.equals(INT)) {
				_attrName = attributes.getValue(NAME);
			} else if (_tagName.equals(FLOAT)) {
				_attrName = attributes.getValue(NAME);
			} else if (_tagName.equals(ARR)) {
				_attrName = attributes.getValue(NAME);
				_mvAttr = true;
			} else if (_tagName.equals(RESULT)) {
				String numFound = attributes.getValue(NUM_FOUND);
				noOfHits = Integer.parseInt(numFound);
				String maxScoreStr = attributes.getValue(MAX_SCORE);
				_maxScore = Double.parseDouble(maxScoreStr);
			} else if (_tagName.equals(LST)) {
				String name = attributes.getValue(NAME);
				if (name.equals(HIGHLIGHTING)) {
					_highlight = true;
				} else if (_highlight) {
					IdParser idParser = new IdParser();
					String idDecoded = URLDecoder
							.decode(name, SolrPipelet.UTF8);
					_id = idParser.parseIdFrom(idDecoded);
				}
			}
		} catch (Exception e) {
			_log.error("Error while extracting attributes for tag'" + _tagName
					+ "'.", e);
		}
	}
}

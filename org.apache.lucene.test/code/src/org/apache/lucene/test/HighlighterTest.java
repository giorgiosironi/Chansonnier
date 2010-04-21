/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.apache.lucene.test;

import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.store.RAMDirectory;

/**
 * The Class HighlighterTest.
 */
public class HighlighterTest extends TestCase {

  /**
   * The Constant FRAGMENTER_LENGTH.
   */
  private static final int FRAGMENTER_LENGTH = 40;

  /**
   * Default field name.
   */
  private static final String FIELD_NAME = "content";

  /**
   * A query String.
   */
  private static final String QUERY_STRING = "Keneddy";

  /**
   * A directory.
   */
  private RAMDirectory _ramDir;

  /**
   * A index reader.
   */
  private IndexReader _reader;

  /**
   * The _log.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * A text for indexing.
   */
  private final String[] _texts =
    {
      "Hello this is a piece of text that is very long and contains too much preamble and the meat is really here which says kennedy has been shot",
      "This piece of text refers to Kennedy at the beginning then has a longer piece of text that is very long in the middle and finally ends with another reference to Kennedy",
      "JFK has been shot", "John Kennedy has been shot", "This text has a typo in referring to Keneddy" };

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() throws Exception {
    _ramDir = new RAMDirectory();
    final IndexWriter writer = new IndexWriter(_ramDir, new StandardAnalyzer(), true);
    for (int i = 0; i < _texts.length; i++) {
      final Document d = new Document();
      final Field f = new Field(FIELD_NAME, _texts[i], Field.Store.YES, Field.Index.TOKENIZED);
      d.add(f);
      writer.addDocument(d);
    }

    writer.optimize();
    writer.close();
    _reader = IndexReader.open(_ramDir);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Simple test.
   * 
   * @throws Exception
   *           if any error has occured
   */
  public void testHighlight() throws Exception {
    Query query = null;
    Hits hits = null;

    final Analyzer analyzer = new StandardAnalyzer();

    final QueryParser parser = new QueryParser(FIELD_NAME, new StandardAnalyzer());
    query = parser.parse(QUERY_STRING);

    final Searcher searcher = new IndexSearcher(_ramDir);
    query = query.rewrite(_reader);
    _log.info("Searching for: " + query.toString(FIELD_NAME));
    hits = searcher.search(query);
    final Highlighter highlighter = new Highlighter(new QueryScorer(query));
    highlighter.setTextFragmenter(new SimpleFragmenter(FRAGMENTER_LENGTH));

    final int maxNumFragmentsRequired = 2;
    for (int i = 0; i < hits.length(); i++) {
      final String text = hits.doc(i).get(FIELD_NAME);
      final TokenStream tokenStream = analyzer.tokenStream(FIELD_NAME, new StringReader(text));
      final String result = highlighter.getBestFragments(tokenStream, text, maxNumFragmentsRequired, "...");
      _log.info("\t" + result);
    }
  }
}

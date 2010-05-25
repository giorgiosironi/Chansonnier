/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.index;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.dom.IdBuilder;
import org.eclipse.smila.datamodel.id.dom.IdParser;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.lucene.LuceneIndexService;
import org.eclipse.smila.processing.parameters.SearchAnnotations;
import org.eclipse.smila.search.datadictionary.DataDictionaryController;
import org.eclipse.smila.search.datadictionary.DataDictionaryException;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DIndex;
import org.eclipse.smila.search.datadictionary.messages.ddconfig.DConfiguration;
import org.eclipse.smila.search.index.IndexException;
import org.eclipse.smila.search.lucene.index.access.AddDocumentOperation;
import org.eclipse.smila.search.lucene.index.access.CountTotalOperation;
import org.eclipse.smila.search.lucene.index.access.DeleteDocumentsOperation;
import org.eclipse.smila.search.lucene.index.access.DoQueryOperation;
import org.eclipse.smila.search.lucene.index.access.ExistsOperation;
import org.eclipse.smila.search.lucene.index.access.GetDocumentOperation;
import org.eclipse.smila.search.lucene.index.access.IndexWriterPool;
import org.eclipse.smila.search.lucene.index.access.SynchronizedIndexReaderExecutor;
import org.eclipse.smila.search.lucene.index.access.SynchronizedIndexSearcherExecutor;
import org.eclipse.smila.search.lucene.index.access.SynchronizedIndexWriterExecutor;
import org.eclipse.smila.search.lucene.messages.advsearch.DDateField;
import org.eclipse.smila.search.lucene.messages.advsearch.DNumField;
import org.eclipse.smila.search.lucene.messages.advsearch.DOP1;
import org.eclipse.smila.search.lucene.messages.advsearch.DOPN;
import org.eclipse.smila.search.lucene.messages.advsearch.DQueryExpression;
import org.eclipse.smila.search.lucene.messages.advsearch.DQueryExpressionCodec;
import org.eclipse.smila.search.lucene.messages.advsearch.DTerm;
import org.eclipse.smila.search.lucene.messages.advsearch.DTextField;
import org.eclipse.smila.search.lucene.messages.advsearch.DWMEAN;
import org.eclipse.smila.search.lucene.messages.indexstructure.DIndexField;
import org.eclipse.smila.search.lucene.tools.search.lucene.DTextFieldParameter;
import org.eclipse.smila.search.templates.NodeTransformer;
import org.eclipse.smila.search.templates.NodeTransformerException;
import org.eclipse.smila.search.templates.NodeTransformerRegistryController;
import org.eclipse.smila.search.templates.TemplateException;
import org.eclipse.smila.search.templates.TemplateRegistryController;
import org.eclipse.smila.search.templates.messages.fieldtemplates.DFieldTemplate;
import org.eclipse.smila.search.utils.advsearch.AdvSearchException;
import org.eclipse.smila.search.utils.advsearch.IQueryExpression;
import org.eclipse.smila.search.utils.indexstructure.DIndexStructure;
import org.eclipse.smila.search.utils.search.DQuery;
import org.eclipse.smila.search.utils.search.IDFParameter;
import org.eclipse.smila.search.utils.search.INFParameter;
import org.eclipse.smila.search.utils.search.ITFParameter;
import org.eclipse.smila.search.utils.searchresult.DHit;
import org.eclipse.smila.search.utils.searchresult.DHitDistribution;
import org.eclipse.smila.search.utils.searchresult.LuceneSearchResult;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.eclipse.smila.utils.xml.XMLUtilsException;

/**
 * The Class IndexConnection.
 * 
 * @author August Georg Schmidt (BROX)
 */
public class IndexConnection extends org.eclipse.smila.search.index.IndexConnection {

  /**
   * Date format pattern.
   */
  public static final String DATE_FORMAT_PATTERN = "yyyyMMddHHmmss";

  /**
   * The Constant MAX_NUMBER_FRAGMENTS.
   */
  private static final int MAX_NUMBER_FRAGMENTS = 100;

  /**
   * The Constant MERGE_CONTIGUOSE_FRAGMENTS.
   */
  private static final boolean MERGE_CONTIGUOSE_FRAGMENTS = false;

  /**
   * Data structure containing a fixed set of zero prefixes for sorting.
   */
  private static final String[] ZEROES =
    { "", "0", "00", "000", "0000", "00000", "000000", "0000000", "00000000", "000000000", "0000000000",
      "00000000000", "000000000000", "0000000000000", "00000000000000", "000000000000000", "0000000000000000",
      "00000000000000000", "000000000000000000", "0000000000000000000" };

  /**
   * Logging.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * Index name.
   */
  private final String _indexName;

  /**
   * Index.
   */
  private DIndex _index;

  /**
   * Index location.
   */
  private final String _indexStore;

  /**
   * Analyzer.
   */
  private final Analyzer _analyzer;

  /**
   * Hits of a given search query.
   */
  private Map<String, Document> _hits;

  /**
   * The IdParser.
   */
  private IdParser _idParser = new IdParser();

  /**
   * The Constructor.
   * 
   * @param indexName
   *          Index name.
   * 
   * @throws IndexException
   *           Unable to create index connection.
   */
  public IndexConnection(final String indexName) throws IndexException {

    super(indexName);

    try {
      // store index structure from data dictionary
      _index = DataDictionaryController.getIndex(indexName);
    } catch (final DataDictionaryException e) {
      throw new IndexException(e.getMessage(), e);
    }

    if (_index == null) {
      throw new IndexException("index not in data dictionary [" + indexName + "]");
    }

    try {
      // TODO: add parameter
      final String maxClauseCountString = "4096"; // ConfigManager.getString(ConfigBuilder.MAX_CLAUSE_COUNT);
      final int maxClauseCount = Integer.parseInt(maxClauseCountString);
      BooleanQuery.setMaxClauseCount(maxClauseCount);
    } catch (final NumberFormatException ex) {
      _log.error("unable to set BooleanQuery max clause count. using default value", ex);
    }

    _indexName = indexName;
    try {
      final File dataFolder = WorkspaceHelper.createWorkingDir(LuceneIndexService.BUNDLE_NAME, indexName);
      _indexStore = dataFolder.getAbsolutePath();
    } catch (final IOException e) {
      if (_log.isErrorEnabled()) {
        _log.error(e);
      }
      throw new IndexException(e);
    }

    // prepare analyzer
    _analyzer = AnalyzerFactory.getAnalyzer(_index);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection#close()
   */
  @Override
  protected void close() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection#docExists(java.lang.String)
   */
  @Override
  public boolean docExists(final String key) throws IndexException {
    final SynchronizedIndexReaderExecutor<Boolean> executor =
      new SynchronizedIndexReaderExecutor<Boolean>(_index, _indexStore);
    final ExistsOperation operation = new ExistsOperation(new Term("##key", key));
    return executor.execute(operation);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection#docExists(org.eclipse.smila.datamodel.id.Id)
   */
  @Override
  public boolean docExists(final Id id) throws IndexException {
    try {
      final IdBuilder idBuilder = new IdBuilder();
      return docExists(idBuilder.idToString(id));
    } catch (final IOException e) {
      throw new IndexException("error checking if document with id " + id + " exists", e);
    } catch (final ParserConfigurationException e) {
      throw new IndexException("error checking if document with id " + id + " exists", e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection#deleteDocument(java.lang.String)
   */
  @Override
  public void deleteDocument(final String key) throws IndexException {
    final SynchronizedIndexWriterExecutor<Void> executor =
      new SynchronizedIndexWriterExecutor<Void>(_index, _indexStore, _analyzer);
    final DeleteDocumentsOperation operation = new DeleteDocumentsOperation(new Term("##key", key));
    executor.execute(operation);
    flushIndex();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection#deleteDocument(org.eclipse.smila.datamodel.id.Id)
   */
  @Override
  public void deleteDocument(final Id id) throws IndexException {
    try {
      final IdBuilder idBuilder = new IdBuilder();
      deleteDocument(idBuilder.idToString(id));
    } catch (final IOException e) {
      throw new IndexException("error deleting document with id " + id, e);
    } catch (final ParserConfigurationException e) {
      throw new IndexException("error deleting document with id " + id, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection#learnDocument(org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.datamodel.id.Id, java.util.Map, java.util.Map)
   */
  @Override
  public void learnDocument(final Blackboard blackboard, final Id id, final Map<String, Integer> attributeMapping,
    final Map<String, Integer> attachmentMapping) throws IndexException, BlackboardAccessException {

    // convert record to document
    final Document document = recordToDocument(blackboard, id, attributeMapping, attachmentMapping);
    writeDocumentToIndex(document);
  }

  /**
   * Write document to index.
   * 
   * @param document
   *          the document
   * 
   * @throws IndexException
   *           the index exception
   */
  private void writeDocumentToIndex(final Document document) throws IndexException {
    final SynchronizedIndexWriterExecutor<Void> executor =
      new SynchronizedIndexWriterExecutor<Void>(_index, _indexStore, _analyzer);
    final AddDocumentOperation operation = new AddDocumentOperation(document);
    executor.execute(operation);

    flushIndex();
  }

  /**
   * Flushes the index if force flush is set to true in the data dictionary.
   * 
   * @throws IndexException
   *           if any error occurs
   */
  private void flushIndex() throws IndexException {
    if (_index.isForceFlush()) {
      IndexWriterPool.flushIndexWriter(_indexName);
    }
  }

  /**
   * Converts a record content to a Lucene Document.
   * 
   * @param blackboard
   *          the Blackboard
   * @param id
   *          the record Id
   * @param attributeMapping
   *          the mapping of attribute names to FieldNo
   * @param attachmentMapping
   *          the mapping of attachment names to FieldNo
   * 
   * @return a Document
   * 
   * @throws BlackboardAccessException
   *           if any error occurs
   * @throws IndexException
   *           if any error occurs
   */
  private Document recordToDocument(final Blackboard blackboard, final Id id,
    final Map<String, Integer> attributeMapping, final Map<String, Integer> attachmentMapping)
    throws BlackboardAccessException, IndexException {
    final Document document = new Document();

    try {
      // add Id
      final IdBuilder idBuilder = new IdBuilder();
      document.add(new Field("##key", idBuilder.idToString(id), Field.Store.YES, Field.Index.UN_TOKENIZED));
    } catch (final IOException e) {
      throw new IndexException("error converting record with id " + id + "  to document", e);
    } catch (final ParserConfigurationException e) {
      throw new IndexException("error converting document with id " + id + "  to document", e);
    }

    // add attributes
    Set<Entry<String, Integer>> set = attributeMapping.entrySet();
    Iterator<Entry<String, Integer>> it = set.iterator();
    while (it.hasNext()) {
      final Entry<String, Integer> entry = it.next();
      final Path path = new Path(entry.getKey());
      if (blackboard.hasAttribute(id, path)) {
        final Iterator<Literal> literals = blackboard.getLiterals(id, path).iterator();
        while (literals.hasNext()) {
          final Literal value = literals.next();
          String stringValue;
          if (Literal.DataType.DATE.equals(value.getDataType())) {
            final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN);
            stringValue = df.format(value.getDateValue());
          } else if (Literal.DataType.DATETIME.equals(value.getDataType())) {
            final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN);
            stringValue = df.format(value.getDateTimeValue());
          } else {
            stringValue = value.getStringValue();
          }
          document.add(createField(entry.getValue(), stringValue));
        } // while
      } else {
        if (_log.isWarnEnabled()) {
          _log.warn("Record does not contain a attribute named " + entry.getKey());
        }
      }
    }

    // add attachments
    set = attachmentMapping.entrySet();
    it = set.iterator();
    while (it.hasNext()) {
      final Entry<String, Integer> entry = it.next();
      if (blackboard.hasAttachment(id, entry.getKey())) {
        try {
          final byte[] byteValue = blackboard.getAttachment(id, entry.getKey());
          if (byteValue != null) {
            final String stringValue = new String(byteValue, "UTF-8");
            document.add(createField(entry.getValue(), stringValue));
          }
        } catch (final UnsupportedEncodingException e) {
          if (_log.isWarnEnabled()) {
            _log.warn("Error while converting attachment " + entry.getKey(), e);
          }
        }
      } else {
        if (_log.isWarnEnabled()) {
          _log.warn("Record does not contain a attachment named " + entry.getKey());
        }
      }
    }

    return document;
  }

  /**
   * Creates a Lucene index field.
   * 
   * @param fieldNo
   *          the FieldNo
   * @param fieldText
   *          the value to index
   * 
   * @return a Field
   */
  private Field createField(final int fieldNo, String fieldText) {
    final DIndexField dIF = (DIndexField) _index.getIndexStructure().getField(fieldNo);
    // assignation of fields is much more complex... date, int, ...
    if (dIF.getType().equals("Number")) {
      fieldText = padNumField(fieldText);
    }

    // TODO: update to lucene 1.9 logic
    final Field.Store storeText = dIF.getStoreText() ? Field.Store.YES : Field.Store.NO;
    Field.Index index = null;

    if (dIF.getIndexValue()) {
      if (dIF.getTokenize()) {
        index = Field.Index.TOKENIZED;
      } else {
        index = Field.Index.UN_TOKENIZED;
      }
    } else {
      index = Field.Index.NO;
    }
    return new Field(dIF.getName(), fieldText, storeText, index);
  }

  /**
   * Enclose number in a fixed length text to allow range search.
   * 
   * @param text
   *          Number to pad.
   * 
   * @return Padded number.
   */
  public String padNumField(String text) {
    // TODO: move to new implementation way.
    text = text.trim();
    long num = 0;
    if (!"".equals(text)) {
      try {
        num = Long.parseLong(text);
      } catch (final NumberFormatException e) {
        final double d = Double.parseDouble(text);
        num = new BigDecimal(d).longValueExact();
        text = Long.toString(num);
        if (_log.isTraceEnabled()) {
          _log.trace("converted double to long value for indexing");
        }
      }
    } // if

    final boolean negative = (num < 0);
    if (negative) {
      text = text.substring(1);
    }

    final int z = Math.min(Math.max(0, 19 - text.length()), 19);
    if (negative) {
      text = "a" + ZEROES[z] + text;
    } else {
      text = "z" + ZEROES[z] + text;
    }
    return text;
  }

  /**
   * Get Analyzer.
   * 
   * @return Analyzer.
   */
  public Analyzer getAnalyzer() {
    return _analyzer;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection#startBurstmode()
   */
  @Override
  protected void startBurstmode() throws IndexException {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection#stopBurstmode()
   */
  @Override
  protected void stopBurstmode() throws IndexException {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection
   *      #getSimpleSearchQuery(org.eclipse.smila.search.utils.search.DQuery)
   */
  @SuppressWarnings("unchecked")
  @Override
  public IQueryExpression getSimpleSearchQuery(final DQuery dQuery) throws IndexException,
    NodeTransformerException, TemplateException {
    // transform
    final DQueryExpression dQE = new DQueryExpression(dQuery.getIndexName(), dQuery.getMaxHits());
    dQE.setShowHitDistribution(dQuery.getShowHitDistribution());
    dQE.setMinSimilarity(dQuery.getMinSimilarity());
    dQE.setStartHits(dQuery.getStartHits());
    dQE.setMaxHits(dQuery.getMaxHits());

    if (dQuery.getFieldsCount() > 1) {
      final Enumeration enm = dQuery.getFields();
      final DWMEAN op = new DWMEAN();
      op.setOperation("MEAN");
      while (enm.hasMoreElements()) {
        final org.eclipse.smila.search.utils.search.DField dField =
          (org.eclipse.smila.search.utils.search.DField) enm.nextElement();

        // evaluate field templates
        final DFieldTemplate dFieldTemplate =
          TemplateRegistryController.getFieldTemplate(dQuery, dField, dQuery.getIndexName());
        if (dFieldTemplate != null) {
          op.addTerm((DTerm) TemplateRegistryController.applyFieldTemplate(dField, dFieldTemplate, this), dField
            .getWeight().floatValue(), dField.getConstraint());
        } else {
          // execute node transformers
          final NodeTransformer transformer =
            NodeTransformerRegistryController.getNodeTransformer(dField.getNodeTransformer(), this);
          op.addTerm(new DTerm(transformer.transformNode(dField)), dField.getWeight().floatValue(), dField
            .getConstraint());
        }
      }
      dQE.setTerm(new DTerm(op));
    } else {
      final org.eclipse.smila.search.utils.search.DField dField =
        (org.eclipse.smila.search.utils.search.DField) dQuery.getFields().nextElement();
      // evaluate field templates
      final DFieldTemplate dFieldTemplate =
        TemplateRegistryController.getFieldTemplate(dQuery, dField, dQuery.getIndexName());
      if (dFieldTemplate != null) {
        dQE.setTerm(TemplateRegistryController.applyFieldTemplate(dField, dFieldTemplate, this));
      } else {
        // execute node transformers
        final NodeTransformer transformer =
          NodeTransformerRegistryController.getNodeTransformer(dField.getNodeTransformer(), this);
        dQE.setTerm(new DTerm(transformer.transformNode(dField)));
      }
    }
    return dQE;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection#doQuery(org.eclipse.smila.search.utils.advsearch.IQueryExpression,
   *      int)
   */
  @Override
  public LuceneSearchResult doQuery(final IQueryExpression queryExpression, final int startPos)
    throws IndexException {
    if (!(queryExpression instanceof DQueryExpression)) {
      throw new IllegalArgumentException("invalid type for dQE [" + queryExpression.getClass().getName() + "]");
    }
    final SynchronizedIndexSearcherExecutor<LuceneSearchResult> executor =
      new SynchronizedIndexSearcherExecutor<LuceneSearchResult>(_index, _indexStore);
    return executor.execute(new DoQueryOperation(this, (DQueryExpression) queryExpression, startPos));
  }

  /**
   * Do query callback for DoQueryOperation. (I was too lazy to split correctly all functionality to operations and
   * sometimes it call callback to original code).
   * 
   * @param indexSearcher
   *          the index searcher
   * @param queryExpression
   *          the query expression
   * @param startPos
   *          the start pos
   * 
   * @return the d result
   * 
   * @throws XMLUtilsException
   *           the XML utils exception
   * @throws AdvSearchException
   *           the adv search exception
   * @throws IndexException
   *           the index exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public LuceneSearchResult doQueryCallback(final IndexSearcher indexSearcher,
    final DQueryExpression queryExpression, final int startPos) throws XMLUtilsException, AdvSearchException,
    IndexException, IOException {
    // search and get distinct results
    Hits hits = null;
    if (_log.isDebugEnabled()) {
      _log.debug("Resulting advanced search:\n"
        + new String(XMLUtils.stream(DQueryExpressionCodec.encode(queryExpression, XMLUtils.getDocument(
          "AnyFinderAdvancedSearch").getDocumentElement()), false)));
    }
    final Query q = transformQuery((DTerm) queryExpression.getTerm(), 1.0f);
    _hits = new HashMap<String, Document>();
    hits = indexSearcher.search(q);
    final int maxHits = queryExpression.getMaxHits();
    final int minSimilarity = queryExpression.getMinSimilarity();
    final boolean showHitDistribution = queryExpression.getShowHitDistribution();
    return doQueryHits2LuceneResult(hits, queryExpression.getIndexName(), maxHits, minSimilarity, startPos,
      showHitDistribution);
  }

  /**
   * Converts lucene Hits into a LuceneResult object containing a hit distribution and the results as a list of records.
   * 
   * @param hits
   *          the hits
   * @param indexName
   *          the index name
   * @param maxHits
   *          the max hits
   * @param minSimilarity
   *          the min similarity
   * @param startPos
   *          the start pos
   * @param showHitDistribution
   *          the show hit distribution
   * 
   * @return the LuceneSearchResult
   * 
   * @throws IndexException
   *           the index exception
   */
  private LuceneSearchResult doQueryHits2LuceneResult(final Hits hits, final String indexName, final int maxHits,
    final int minSimilarity, final int startPos, final boolean showHitDistribution) throws IndexException {

    // create result object
    final LuceneSearchResult result = new LuceneSearchResult();
    final ArrayList<Record> resultList = new ArrayList<Record>();
    final DHitDistribution dHitDist = new DHitDistribution();

    // fetch result from data dictionary
    if (hits.length() > 0) {
      final int loopCnt = Math.min(hits.length(), maxHits + startPos);
      int hitScore = -1;
      int hitCnt = 0;
      int hitCnts = 0;

      // build hit distribution
      for (int i = 0; i < hits.length(); i++) {
        try {
          final int score = (int) Math.ceil(hits.score(i) * 100);
          if (score < minSimilarity) {
            break;
          }

          if (hitScore == score) {
            hitCnt++;
          } else {
            if (hitScore >= 0) {
              dHitDist.addHit(new DHit(hitScore, hitCnt));
              hitCnts += hitCnt;
            }
            hitCnt = 1;
            hitScore = score;
          }

          if (i < startPos) {
            continue;
          }
        } catch (final IOException e) {
          if (_log.isErrorEnabled()) {
            _log.error(e);
          }
        }
      } // for

      if (startPos < loopCnt) {
        for (int i = startPos; i < loopCnt; i++) {
          try {
            final int score = (int) Math.ceil(hits.score(i) * 100);
            if (score < minSimilarity) {
              break;
            }

            final Record record = RecordFactory.DEFAULT_INSTANCE.createRecord();
            final String xmlString = hits.doc(i).get("##key");
            _hits.put(xmlString, hits.doc(i));
            try {
              final Id resultId = _idParser.parseIdFrom(xmlString);
              record.setId(resultId);
            } catch (final Exception e) {
              final String msg = "error creating id from xmlString";
              if (_log.isErrorEnabled()) {
                _log.error(msg, e);
              }
              throw new IndexException(msg, e);
            }

            // save for later access
            _hits.put(xmlString, hits.doc(i));

            final Annotation resultAnno = record.getFactory().createAnnotation();
            resultAnno.setNamedValue(SearchAnnotations.RELEVANCE, Double.toString(((double) score / (double) 100)));
            record.getMetadata().setAnnotation(SearchAnnotations.RESULT, resultAnno);

            resultList.add(record);
          } catch (final IOException e) {
            if (_log.isErrorEnabled()) {
              _log.error(e);
            }
          }
        } // for
      } // if

      if (hitScore >= 0) {
        dHitDist.addHit(new DHit(hitScore, hitCnt));
      }
      hitCnts += hitCnt;
      dHitDist.addHit(new DHit(0, this.getDocumentCount() - hitCnts));
    } // if

    result.setIndexName(indexName);
    result.setResultList(resultList);
    if (showHitDistribution) {
      result.setHitDistribution(dHitDist);
    }
    return result;
  }

  /**
   * Get amount of documents in index.
   * 
   * @return Amount of documents in index.
   * 
   * @throws IndexException
   *           Unable to get amount of documents.
   */
  private int getDocumentCount() throws IndexException {
    final SynchronizedIndexReaderExecutor<Integer> executor =
      new SynchronizedIndexReaderExecutor<Integer>(_index, _indexStore);
    final CountTotalOperation operation = new CountTotalOperation();
    return executor.execute(operation);
  }

  /**
   * Transform query.
   * 
   * @param dTerm
   *          the d term
   * @param boostFactor
   *          the boost factor
   * 
   * @return the query
   */
  private Query transformQuery(final DTerm dTerm, final float boostFactor) {
    if (dTerm.getTerm() instanceof DOPN) {
      final BooleanQuery q = new BooleanQuery();
      if (_log.isDebugEnabled()) {
        _log.debug("<BooleanQuery>");
      }
      final DOPN op = dTerm.getOpN();
      final String operation = op.getOperation();
      for (int i = 0; i < op.getTermCount(); i++) {
        BooleanClause bc;
        // Lucene does not correctly execute BooleanQueries with only
        // prohibited
        // elements.
        // Therefore we must take the child term of the NOT operation
        // and move
        // it up to the current query.
        // Consequence: "a OR b OR c OR NOT d" will be converted to "(a
        // OR b OR
        // c) AND NOT d"
        if (op.getTerm(i).getTerm() instanceof DOP1
          && ((DOP1) op.getTerm(i).getTerm()).getOperation().equals("NOT")) {
          if (_log.isDebugEnabled()) {
            _log.debug("<BooleanClause boost=" + boostFactor + " prohibited>");
          }
          final Query tq = transformQuery(((DOP1) op.getTerm(i).getTerm()).getTerm(), boostFactor);
          tq.setBoost(boostFactor);
          bc = new BooleanClause(tq, BooleanClause.Occur.MUST_NOT);
        } else {
          if (_log.isDebugEnabled()) {
            _log.debug("<BooleanClause boost=" + boostFactor + " " + (operation.equals("AND") ? "required" : "")
              + ">");
          }
          final Query tq = transformQuery(op.getTerm(i), boostFactor);
          tq.setBoost(boostFactor);

          if (operation.equals("AND")) {
            bc = new BooleanClause(tq, BooleanClause.Occur.MUST);
          } else {
            bc = new BooleanClause(tq, BooleanClause.Occur.SHOULD);
          }
          if (_log.isDebugEnabled()) {
            _log.debug("</BooleanClause>");
          }
        }
        q.add(bc);
      }
      if (_log.isDebugEnabled()) {
        _log.debug("</BooleanQuery>");
      }
      return q;
    } else if (dTerm.getTerm() instanceof DWMEAN) {
      final BooleanQuery q = new BooleanQuery();
      if (_log.isDebugEnabled()) {
        _log.debug("<BooleanQuery>");
      }
      final DWMEAN op = dTerm.getWMEAN();
      for (int i = 0; i < op.getTermCount(); i++) {
        BooleanClause bc;
        final String constraint = op.getConstraint(i);
        final float boost = op.getBoost(i) * boostFactor;
        // Lucene does not correctly execute BooleanQueries with only
        // prohibited
        // elements.
        // Therefore we must take the child term of the NOT operation
        // and move
        // it up to the current query.
        // Consequence: "a OR b OR c OR NOT d" will be converted to "(a
        // OR b OR
        // c) AND NOT d"
        if (op.getTerm(i).getTerm() instanceof DOP1
          && ((DOP1) op.getTerm(i).getTerm()).getOperation().equals("NOT")) {
          if (_log.isDebugEnabled()) {
            _log.debug("<BooleanClause boost=" + boost + " prohibited>");
          }
          final Query tq = transformQuery(((DOP1) op.getTerm(i).getTerm()).getTerm(), boost);
          tq.setBoost(boost);
          bc = new BooleanClause(tq, BooleanClause.Occur.MUST_NOT);
          if (_log.isDebugEnabled()) {
            _log.debug("</BooleanClause>");
          }
        } else {
          if (_log.isDebugEnabled()) {
            _log.debug("<BooleanClause boost=" + boost + " " + (constraint.equals("required") ? "required " : "")
              + (constraint.equals("prohibited") ? "prohibited" : "") + ">");
          }
          final Query tq = transformQuery(op.getTerm(i), boost);
          tq.setBoost(boost);

          if (constraint.equals("required")) {
            bc = new BooleanClause(tq, BooleanClause.Occur.MUST);
          } else {
            if (constraint.equals("prohibited")) {
              bc = new BooleanClause(tq, BooleanClause.Occur.MUST_NOT);
            } else {
              bc = new BooleanClause(tq, BooleanClause.Occur.SHOULD);
            }
          }

          if (_log.isDebugEnabled()) {
            _log.debug("</BooleanClause>");
          }
        }
        q.add(bc);
      }
      if (_log.isDebugEnabled()) {
        _log.debug("</BooleanQuery>");
      }
      return q;
    } else if (dTerm.getTerm() instanceof DOP1) {
      final BooleanQuery q = new BooleanQuery();
      if (_log.isDebugEnabled()) {
        _log.debug("<BooleanQuery>");
      }
      final DOP1 op = dTerm.getOP1();
      if (op.getOperation().equals("NOT")) {
        if (_log.isDebugEnabled()) {
          _log.debug("<BooleanClause prohibited>");
        }
        q.add(transformQuery(op.getTerm(), boostFactor), BooleanClause.Occur.MUST_NOT);
        if (_log.isDebugEnabled()) {
          _log.debug("</BooleanClause>");
        }
      }
      if (_log.isDebugEnabled()) {
        _log.debug("</BooleanQuery>");
      }
      return q;
    } else if (dTerm.getTerm() instanceof DNumField) {
      final DNumField field = (DNumField) dTerm.getTerm();
      final Term lower =
        new Term(_index.getIndexStructure().getField(field.getFieldNo()).getName(),
          padNumField("" + field.getMin()));
      final Term upper =
        new Term(_index.getIndexStructure().getField(field.getFieldNo()).getName(),
          padNumField("" + field.getMax()));
      if (_log.isDebugEnabled()) {
        _log.debug("<RangeQuery inclusive>");
        _log.debug("<LowerTerm FieldName=" + lower.field() + " Text=" + lower.text() + "/>");
        _log.debug("<UpperTerm FieldName=" + upper.field() + " Text=" + upper.text() + "/>");
        _log.debug("</RangeQuery>");
      }
      final RangeQuery q = new RangeQuery(lower, upper, true);
      return q;
    } else if (dTerm.getTerm() instanceof DDateField) {
      final DDateField field = (DDateField) dTerm.getTerm();
      final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN);
      Term lower = null;
      Term upper = null;
      if (_log.isDebugEnabled()) {
        _log.debug("<RangeQuery inclusive>");
      }
      if (field.getMin() != null) {
        lower =
          new Term(_index.getIndexStructure().getField(field.getFieldNo()).getName(), df.format(field.getMin()));
        if (_log.isDebugEnabled()) {
          _log.debug("<LowerTerm FieldName=" + lower.field() + " Text=" + lower.text() + "/>");
        }
      }
      if (field.getMax() != null) {
        upper =
          new Term(_index.getIndexStructure().getField(field.getFieldNo()).getName(), df.format(field.getMax()));
        if (_log.isDebugEnabled()) {
          _log.debug("<UpperTerm FieldName=" + upper.field() + " Text=" + upper.text() + "/>");
        }
      }

      if (_log.isDebugEnabled()) {
        _log.debug("</RangeQuery>");
      }
      final RangeQuery q = new RangeQuery(lower, upper, true);
      return q;
    } else {
      Query q;
      final DTextField tf = dTerm.getTextField();
      final Analyzer a = getAnalyzer();
      final String fieldName = _index.getIndexStructure().getField(tf.getFieldNo()).getName();
      final String fieldText = tf.getText().trim();
      final boolean isPhraseSearch = (fieldText.indexOf(" ") > 0);
      boolean isWildcardSearch = false;
      boolean isFuzzySearch = false;

      final ArrayList<Term> terms = new ArrayList<Term>();
      final DIndexField indexField = (DIndexField) _index.getIndexStructure().getField(tf.getFieldNo());
      if (indexField.getTokenize()) {

        // wildcard and fuzzy search must not contain spaces
        isWildcardSearch = tf.getParseWildcards() && !isPhraseSearch && containsWildcards(fieldText);
        isFuzzySearch = tf.getFuzzy() && !isPhraseSearch && !isWildcardSearch;

        if (isWildcardSearch) {
          if (_log.isDebugEnabled()) {
            _log.debug("fieldText=" + fieldText + ", token " + fieldText);
          }
          terms.add(new Term(fieldName, fieldText));
        } else {
          try {
            Token t;
            final TokenStream ts = a.tokenStream(fieldName, new StringReader(fieldText));
            while ((t = ts.next()) != null) {
              final String text = t.termText();
              if (_log.isDebugEnabled()) {
                _log.debug("fieldText=" + fieldText + ", token " + text + " (type " + t.type() + ")");
              }
              terms.add(new Term(fieldName, text));
            }
            ts.close();
          } catch (final IOException ioe) {
            if (_log.isErrorEnabled()) {
              _log.error(ioe);
            }
          }
        }

      } else {
        // wildcard and fuzzy search could contain spaces
        isWildcardSearch = tf.getParseWildcards() && containsWildcards(fieldText);
        isFuzzySearch = tf.getFuzzy() && !isWildcardSearch;

        if (_log.isDebugEnabled()) {
          _log.debug("fieldText=" + fieldText + ", token " + fieldText);
        }
        terms.add(new Term(fieldName, fieldText));
      }

      if (terms.size() == 0) {
        terms.add(new Term(fieldName, ""));
      }

      if (isFuzzySearch && (terms.size() > 1)) {
        q = new BooleanQuery();
        if (_log.isDebugEnabled()) {
          _log.debug("<BooleanQuery>");
        }
        for (int i = 0; i < terms.size(); i++) {
          final FuzzyQuery fq = new FuzzyQuery(new Term(fieldName, terms.get(i).text()));
          ((BooleanQuery) q).add(new BooleanClause(fq, BooleanClause.Occur.MUST));
          if (_log.isDebugEnabled()) {
            _log.debug("<FuzzyQuery FieldName=" + terms.get(i).field() + " Text=" + terms.get(i).text() + "/>");
          }
        }
        if (_log.isDebugEnabled()) {
          _log.debug("</BooleanQuery>");
        }
      } else if ((terms.size() > 1)) {
        // PhraseQuery if field contains at least two words
        // Checking done by whitespace. This is a problem with analyzers
        // that
        // don't treat
        // all whitespace as word separators!
        q = new PhraseQuery();
        if (_log.isDebugEnabled()) {
          _log.debug("<PhraseQuery Slop=" + tf.getSlop() + ">");
        }
        for (int i = 0; i < terms.size(); i++) {
          ((PhraseQuery) q).add(terms.get(i));
          if (_log.isDebugEnabled()) {
            _log.debug("<Term FieldName=" + terms.get(i).field() + " Text=" + terms.get(i).text() + "/>");
          }
        }
        ((PhraseQuery) q).setSlop(tf.getSlop());
        if (_log.isDebugEnabled()) {
          _log.debug("</PhraseQuery>");
        }
      } else if (isWildcardSearch) {
        final String txt = indexField.getTokenize() ? fieldText.toLowerCase() : fieldText;
        if (_log.isDebugEnabled()) {
          _log.debug("<WildcardQuery FieldName=" + fieldName + " Text=" + txt + "/>");
        }
        q = new WildcardQuery(new Term(fieldName, txt));
      } else if (isFuzzySearch) {
        if (_log.isDebugEnabled()) {
          _log.debug("<FuzzyQuery FieldName=" + fieldName + " Text=" + terms.get(0).text() + "/>");
        }
        q = new FuzzyQuery(new Term(fieldName, terms.get(0).text()));
      } else {
        if (_log.isDebugEnabled()) {
          _log.debug("<TermQuery FieldName=" + fieldName + " Text=" + terms.get(0).text() + "/>");
        }
        q = new TermQuery(new Term(fieldName, terms.get(0).text()));
      }
      return q;
    }
  }

  /**
   * Check whether a query term contains wildcard characters.
   * 
   * @param fieldText
   *          Field text.
   * 
   * @return Whether the text contains wildcards.
   */
  private boolean containsWildcards(final String fieldText) {
    return ((fieldText.indexOf("*") >= 0) || (fieldText.indexOf("?") >= 0));
  }

  /**
   * Get document by key.
   * 
   * @param key
   *          Key.
   * 
   * @return Document.
   * 
   * @throws IndexException
   *           Unable to get document.
   */
  private Document getDocument(final String key) throws IndexException {
    final SynchronizedIndexSearcherExecutor<Document> executor =
      new SynchronizedIndexSearcherExecutor<Document>(_index, _indexStore);
    return executor.execute(new GetDocumentOperation(key));
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection#getTestQueryExpression(java.lang.String)
   */
  @Override
  protected IQueryExpression getTestQueryExpression(final String text) throws IndexException {
    final DQueryExpression dQueryExp = new DQueryExpression();

    final String[] tokens = text.split(" ");

    final DWMEAN wmean = new DWMEAN();
    for (int i = 0; i < tokens.length; i++) {
      final DTextField field = new DTextField(0, tokens[i], true, true, 0);
      wmean.addTerm(new DTerm(field), 1.0f, "optional");
    }

    dQueryExp.setIndexName(_indexName);
    dQueryExp.setMaxHits(100);
    dQueryExp.setTerm(new DTerm(wmean));

    return dQueryExp;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection#insertTextParameter(org.eclipse.smila.search.utils.search.DTextField,
   *      org.eclipse.smila.search.utils.search.ITFParameter, java.lang.String, java.lang.StringBuffer)
   */
  @Override
  protected void insertTextParameter(final org.eclipse.smila.search.utils.search.DTextField field,
    final ITFParameter configParameter, final String config, StringBuffer sb) {
    if (configParameter == null) {
      return;
    }

    DTextFieldParameter tfp = null;

    if (field.getParameter() == null) {
      field.setParameter(new DTextFieldParameter());
    }

    tfp = (DTextFieldParameter) field.getParameter();

    final DTextFieldParameter ctfp = (DTextFieldParameter) configParameter;

    if ((ctfp.getTolerance() != null) && (tfp.getTolerance() == null)) {
      tfp.setTolerance(ctfp.getTolerance());
      sb = sb.append(config + ":Tolerance=" + ctfp.getTolerance().toString() + " ");
    }

    if ((ctfp.getOperator() != null) && (tfp.getOperator() == null)) {
      tfp.setOperator(ctfp.getOperator());
      sb = sb.append(config + ":Operator=" + ctfp.getOperator().toString() + " ");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection
   *      #insertNumberParameter(org.eclipse.smila.search.utils.search.DNumberField,
   *      org.eclipse.smila.search.utils.search.INFParameter, java.lang.String, java.lang.StringBuffer)
   */
  @Override
  protected void insertNumberParameter(final org.eclipse.smila.search.utils.search.DNumberField field,
    final INFParameter configParameter, final String config, final StringBuffer sb) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection
   *      #insertDateParameter(org.eclipse.smila.search.utils.search.DDateField,
   *      org.eclipse.smila.search.utils.search.IDFParameter, java.lang.String, java.lang.StringBuffer)
   */
  @Override
  protected void insertDateParameter(final org.eclipse.smila.search.utils.search.DDateField field,
    final IDFParameter configParameter, final String config, final StringBuffer sb) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection #getResultValues(java.lang.String[], int[],
   *      java.lang.String[][])
   */
  @Override
  protected void getResultValues(final String[] keys, final int[] fields, final String[][] values)
    throws IndexException {

    final DIndexStructure dIS = _index.getIndexStructure();
    final Map<String, Document> documents = _hits;

    for (int i = 0; i < keys.length; i++) {
      final Document doc = documents.get(keys[i]);
      for (int j = 0; j < fields.length; j++) {

        final DIndexField field = (DIndexField) dIS.getField(fields[j]);

        if (field == null) {
          throw new IndexException("invalid field no in result [" + fields[j] + "]");
        }

        values[i][j] = doc.get(field.getName());

      }
    }

  }

  /**
   *
   */
  @Override
  protected String[] getResultValues(final Id id, final int fieldNo) throws IndexException {
    final DIndexStructure dIS = _index.getIndexStructure();
    try {
      final IdBuilder idBuilder = new IdBuilder();
      final Document doc = _hits.get(idBuilder.idToString(id));

      final DIndexField field = (DIndexField) dIS.getField(fieldNo);

      if (field == null) {
        throw new IndexException("invalid field no in result [" + fieldNo + "]");
      }
      return doc.getValues(field.getName());
    } catch (final IOException e) {
      throw new IndexException("error getting result value for record with id " + id, e);
    } catch (final ParserConfigurationException e) {
      throw new IndexException("error getting result value for record with id " + id, e);
    }
  }

  /**
   * Get highlighting queries per field.
   * 
   * @param hrFields
   *          Fields.
   * @param queryExpression
   *          Query expression.
   * @param indexReader
   *          Index reader.
   * 
   * @return Map of Queries.
   * 
   * @throws IOException
   *           Unable to rewrite query.
   */
  private Map<Integer, Query> getHlQueries(final int[] hrFields, final IQueryExpression queryExpression,
    final IndexReader indexReader) throws IOException {

    final Map<Integer, Query> hlQueries = new HashMap<Integer, Query>();
    // final DIndexStructure indexStructure = _index.getIndexStructure();
    final Map<Integer, Set<String>> highlightingTermsPerField = new HashMap<Integer, Set<String>>();
    getQueryText((DTerm) queryExpression.getTerm(), highlightingTermsPerField);
    for (int i = 0; i < hrFields.length; i++) {
      Query query = null;
      try {
        DTerm dTerm = (DTerm) queryExpression.getTerm().clone();
        dTerm = normalizeTemplateSearch(dTerm, hrFields[i]);

        if ((dTerm == null) || (dTerm.getTerm() == null)) {
          continue;
        }

        query = transformQuery(dTerm, 1.0f);
        query = query.rewrite(indexReader);
        hlQueries.put(Integer.valueOf(hrFields[i]), query);
      } catch (final CloneNotSupportedException exception) {
        _log.error("unalbe to prepare highlighting query", exception);
        continue;
      }
    }

    return hlQueries;
  }

  /**
   * Normalize term for highlighting.
   * 
   * @param term
   *          Term to normalize.
   * @param remainingFieldNo
   *          Remaining field no.
   * 
   * @return Normalized term.
   */
  private DTerm normalizeTemplateSearch(final DTerm term, final int remainingFieldNo) {
    if (term.getTerm() instanceof DNumField) {
      term.setTerm(null);
    } else if (term.getTerm() instanceof DDateField) {
      term.setTerm(null);
    } else if (term.getTerm() instanceof DTextField) {
      final DTextField tf = term.getTextField();
      final DIndexField ddField = (DIndexField) _index.getIndexStructure().getField(tf.getFieldNo());
      if (!ddField.getStoreText()) {
        term.setTerm(null);
      }

      if (tf.getFieldNo() != remainingFieldNo) {
        term.setTerm(null);
      }
    } else if (term.getTerm() instanceof DOP1) {
      normalizeTemplateSearch(term.getOP1().getTerm(), remainingFieldNo);
      if (term.getOP1().getTerm().getTerm() == null) {
        term.setTerm(null);
      }
    } else if (term.getTerm() instanceof DOPN) {
      final DOPN op = term.getOpN();
      for (int i = op.getTermCount() - 1; i >= 0; i--) {
        normalizeTemplateSearch(op.getTerm(i), remainingFieldNo);
        if (op.getTerm(i).getTerm() == null) {
          op.removeTerm(i);
        }
      }

      if (op.getTermCount() == 1) {
        term.setTerm(op.getTerm(0).getTerm());
      } else if (op.getTermCount() == 0) {
        term.setTerm(null);
      }
    } else if (term.getTerm() instanceof DWMEAN) {
      final DWMEAN op = term.getWMEAN();
      for (int i = op.getTermCount() - 1; i >= 0; i--) {
        normalizeTemplateSearch(op.getTerm(i), remainingFieldNo);
      }
      final DWMEAN opNew = new DWMEAN();
      opNew.setOperation(op.getOperation());
      for (int i = 0; i < op.getTermCount(); i++) {
        if (op.getTerm(i).getTerm() != null) {
          opNew.addTerm(op.getTerm(i), op.getBoost(i), op.getConstraint(i));
        }
      }

      if (opNew.getTermCount() > 1) {
        term.setTerm(opNew);
      } else if (opNew.getTermCount() == 1) {
        term.setTerm(opNew.getTerm(0).getTerm());
      } else if (opNew.getTermCount() == 0) {
        term.setTerm(null);
      }
    }

    return term;
  }

  /**
   * Gets the query text.
   * 
   * @param term
   *          Term.
   * @param highlightingTermsPerField
   *          Highlighting terms per field.
   */
  private void getQueryText(final DTerm term, final Map<Integer, Set<String>> highlightingTermsPerField) {

    if (term.getTerm() instanceof DTextField) {
      final DTextField textField = term.getTextField();
      final Integer fieldNo = new Integer(textField.getFieldNo());
      Set<String> terms = null;

      if (highlightingTermsPerField.containsKey(fieldNo)) {
        terms = highlightingTermsPerField.get(fieldNo);
      } else {
        terms = new HashSet<String>();
        highlightingTermsPerField.put(fieldNo, terms);
      }

      if (textField.getText() != null) {
        for (String textTerm : textField.getText().split(" ")) {
          if (textTerm != null) {
            textTerm = textTerm.trim();
            if (!"".equals(textTerm)) {
              terms.add(textTerm);
            }
          }
        }
      }
    } else if (term.getTerm() instanceof DOP1) {
      getQueryText(term.getOP1().getTerm(), highlightingTermsPerField);
    } else if (term.getTerm() instanceof DOPN) {
      final DOPN op = term.getOpN();
      for (int i = 0; i < op.getTermCount(); i++) {
        getQueryText(op.getTerm(i), highlightingTermsPerField);
      }
    } else if (term.getTerm() instanceof DWMEAN) {
      final DWMEAN op = term.getWMEAN();
      for (int i = 0; i < op.getTermCount(); i++) {
        getQueryText(op.getTerm(i), highlightingTermsPerField);
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection#encodeTextField(org.eclipse.smila.search.utils.search.DTextField)
   */
  @Override
  protected void encodeTextField(final org.eclipse.smila.search.utils.search.DTextField tf) throws IndexException {
    // no encoding required...
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection#isSupportedValue(int, java.lang.Object)
   */
  @Override
  protected void isSupportedValue(final int fieldNo, final Object value) throws IndexException {
    if ((value == null) || (value instanceof String)) {
    } else {
      throw new IndexException("field has invalid content type [" + fieldNo + ";" + value.getClass().getName()
        + "]; null|java.lang.String required");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection#setLiteralValue(Literal, int, String)
   */
  @Override
  protected void setLiteralValue(final Literal literal, final int fieldNo, final String value)
    throws IndexException {
    final DConfiguration dConfig = _index.getConfiguration();
    final String type = dConfig.getDefaultConfig().getField(fieldNo).getFieldConfig().getType();
    if ("FTText".equals(type)) {
      literal.setStringValue(value);
    } else if ("FTDate".equals(type)) {
      try {
        final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        literal.setDateTimeValue(df.parse(value));
      } catch (final ParseException e) {
        throw new IndexException("error parsing valuee of field no " + fieldNo, e);
      }
    } else if ("FTNumber".equals(type)) {
      final String longValue = value.replaceAll("a|z[0]*", "");
      literal.setIntValue(Long.parseLong(longValue));
    } else {
      throw new IndexException("unknown FieldConfig type " + type);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexConnection#addHighlightAnnotation(IQueryExpression, Record,
   *      fieldNo,String, String)
   */
  @Override
  protected void addHighlightAnnotation(final IQueryExpression dQE, final Record record, final int fieldNo,
    final String attributeName, final String indexName) throws IndexException {
    final DIndexStructure dIS = _index.getIndexStructure();
    try {
      final IdBuilder idBuilder = new IdBuilder();
      final Document doc = _hits.get(idBuilder.idToString(record.getId()));

      final DIndexField field = (DIndexField) dIS.getField(fieldNo);
      if (field == null) {
        throw new IndexException("invalid field no in result [" + fieldNo + "]");
      }

      final String text = doc.get(field.getName());
      if (text != null) {
        final AnnotationFormatter formatter = new AnnotationFormatter(record.getFactory());
        formatter.reset(text);
        final Query hlQuery = getHighlightQuery(fieldNo, dQE);
        if (hlQuery != null) {
          final TokenStream tokenStream = _analyzer.tokenStream(attributeName, new StringReader(text));
          final Highlighter highlighter = new Highlighter(formatter, new QueryScorer(hlQuery));
          // this triggers the execution of the Formatter
          highlighter.getBestTextFragments(tokenStream, text, MERGE_CONTIGUOSE_FRAGMENTS, MAX_NUMBER_FRAGMENTS);

          final Annotation highlight = record.getFactory().createAnnotation();
          final List<Annotation> highlightingPositions = formatter.getHighlightPositions();
          for (final Annotation highlightingPos : highlightingPositions) {
            highlight.addAnnotation(SearchAnnotations.HIGHLIGHT_POSITIONS, highlightingPos);
          } // for

          highlight.setNamedValue(SearchAnnotations.HIGHLIGHT_TEXT, text);
          record.getMetadata().getAttribute(attributeName).addAnnotation(SearchAnnotations.HIGHLIGHT, highlight);
        } // if
      } // if
    } catch (final IOException e) {
      throw new IndexException("error getting result value for record with id " + record.getId(), e);
    } catch (final ParserConfigurationException e) {
      throw new IndexException("error getting result value for record with id " + record.getId(), e);
    }
  }

  /**
   * Returns the highlight query.
   * 
   * @param highlightResultFieldNo
   *          the highlightResult fieldNo
   * @param queryExpression
   *          the queryExpression
   * @return the Query object
   * @throws IOException
   *           if any error occurs
   */
  private Query getHighlightQuery(final int highlightResultFieldNo, final IQueryExpression queryExpression)
    throws IOException {
    Query query = null;
    try {
      DTerm dTerm = (DTerm) queryExpression.getTerm().clone();
      dTerm = normalizeTemplateSearch(dTerm, highlightResultFieldNo);
      if ((dTerm != null) && (dTerm.getTerm() != null)) {
        query = transformQuery(dTerm, 1.0f);
        final IndexReader indexReader = IndexReader.open(_indexStore);
        query = query.rewrite(indexReader);
      }
    } catch (final CloneNotSupportedException exception) {
      _log.error("unalbe to prepare highlighting query", exception);
    }
    return query;
  }

  /**
   * @see IndexConnection#unlock()
   * @throws IndexException
   *           if any error occurs
   */
  public void unlock() throws IndexException {
    try {
      if (IndexReader.isLocked(_indexStore)) {
        IndexReader.unlock(FSDirectory.getDirectory(_indexStore));
        if (_log.isInfoEnabled()) {
          _log.info("Removed lock on Lucene index " + _indexName);
        }
      }
    } catch (Exception e) {
      throw new IndexException(e);
    }
  }
}

/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.search.index.IndexConnection;
import org.eclipse.smila.search.index.IndexException;
import org.eclipse.smila.search.lucene.messages.advsearch.DOP1;
import org.eclipse.smila.search.lucene.messages.advsearch.DOPN;
import org.eclipse.smila.search.lucene.messages.advsearch.DQueryExpression;
import org.eclipse.smila.search.lucene.messages.advsearch.DTemplateField;
import org.eclipse.smila.search.lucene.messages.advsearch.DTerm;
import org.eclipse.smila.search.lucene.messages.advsearch.DTextTemplateField;
import org.eclipse.smila.search.lucene.messages.advsearch.DWMEAN;
import org.eclipse.smila.search.lucene.tools.search.lucene.DTextFieldParameter;
import org.eclipse.smila.search.plugin.ITemplateAccess;
import org.eclipse.smila.search.templates.NodeTransformer;
import org.eclipse.smila.search.templates.NodeTransformerException;
import org.eclipse.smila.search.templates.NodeTransformerRegistryController;
import org.eclipse.smila.search.templates.TemplateException;
import org.eclipse.smila.search.templates.messages.fieldtemplates.DFieldTemplate;
import org.eclipse.smila.search.templates.messages.searchtemplates.DTemplate;
import org.eclipse.smila.search.utils.advsearch.IQueryExpression;
import org.eclipse.smila.search.utils.advsearch.ITerm;
import org.eclipse.smila.search.utils.advsearch.ITermContent;
import org.eclipse.smila.search.utils.indexstructure.DIndexStructure;
import org.eclipse.smila.search.utils.search.DField;
import org.eclipse.smila.search.utils.search.DQuery;
import org.eclipse.smila.search.utils.search.DTextField;
import org.eclipse.smila.search.utils.search.parameterobjects.DNodeTransformer;

/**
 * @author GSchmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TemplateAccess implements ITemplateAccess {

  /**
   * Logging.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * 
   */
  public TemplateAccess() {
    super();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.plugin.ITemplateAccess#applyTemplate(org.eclipse.smila.search.utils.search.DQuery,
   *      org.eclipse.smila.search.templates.messages.searchtemplates.DTemplate,
   *      org.eclipse.smila.search.index.IndexConnection)
   */
  public IQueryExpression applyTemplate(DQuery dQuery, DTemplate dTemplate, IndexConnection ic)
    throws TemplateException, NodeTransformerException, IndexException {
    final DQueryExpression queryExp =
      (DQueryExpression) ((DQueryExpression) dTemplate.getAdvSearch().getQueryExpression(0)).clone();

    final Hashtable<Integer, DField> fields = new Hashtable<Integer, DField>();
    final Enumeration enm = dQuery.getFields();
    while (enm.hasMoreElements()) {
      final DField dField = (DField) enm.nextElement();
      fields.put(new Integer(dField.getFieldNo()), dField);
    }

    transformSearchQuery((DTerm) queryExp.getTerm(), fields, ic.getIndex().getIndexStructure(), ic);
    queryExp.setMaxHits(dQuery.getMaxHits());
    queryExp.setIndexName(dQuery.getIndexName());
    queryExp.setMinSimilarity(dQuery.getMinSimilarity());
    queryExp.setShowHitDistribution(dQuery.getShowHitDistribution());
    return queryExp;
  }

  private void transformSearchQuery(DTerm term, Hashtable fields, DIndexStructure is, IndexConnection ic)
    throws TemplateException, NodeTransformerException, IndexException {
    if (term.getTerm() instanceof DTemplateField) {
      if (_log.isDebugEnabled()) {
        _log.debug("term=" + term);
      }
      final DTemplateField tplField = term.getTemplateField();
      NodeTransformer transformer =
        NodeTransformerRegistryController.getNodeTransformer(tplField.getNodeTransformer(), ic);
      // change field nos

      // TODO: change field type checks. Resulting field must be of same type as target field,
      // not source field. All necessary conversions must be performed.
      final DField field = (DField) ((DField) fields.get(new Integer(tplField.getSourceFieldNo()))).clone();
      if (field == null) {
        throw new TemplateException("unable to locate field in search query [" + tplField.getSourceFieldNo() + "]");
      }

      if (tplField.getNodeTransformer() != null) {
        transformer = NodeTransformerRegistryController.getNodeTransformer(tplField.getNodeTransformer(), ic);
        if (_log.isDebugEnabled()) {
          _log.debug("using node transformer from template field [" + tplField.getFieldNo() + ";"
            + tplField.getNodeTransformer() + "]");
        }
      }

      final org.eclipse.smila.search.utils.indexstructure.DIndexField isField = is.getField(tplField.getFieldNo());

      if (isField == null) {
        throw new TemplateException("field referenced in template does not exist in index ["
          + tplField.getFieldNo() + "]");
      }

      if (field instanceof org.eclipse.smila.search.utils.search.DTextField) {

        final DTextField tf = (DTextField) field;
        final DTextFieldParameter tfp = (DTextFieldParameter) tf.getParameter();

        if (!isField.getType().equals("Text")) {
          throw new TemplateException("conversion impossible [DTextField;" + isField.getClass().getName() + "]");
        }

        if (tplField instanceof DTextTemplateField) {
          final DTextTemplateField ttf = (DTextTemplateField) tplField;

          if (ttf.getParameter() != null) {
            final DTextFieldParameter ttfp = ttf.getParameter();
            if (ttfp.getOperator() != null) {
              if (_log.isDebugEnabled()) {
                _log.debug("using operator from template field [" + tplField.getFieldNo() + ";"
                  + ttfp.getOperator().toString() + "]");
              }
              tfp.setOperator(ttfp.getOperator());
            }

            if (ttfp.getTolerance() != null) {
              if (_log.isDebugEnabled()) {
                _log.debug("using tolerance from template field [" + tplField.getFieldNo() + ";"
                  + ttfp.getTolerance().toString() + "]");
              }
              tfp.setTolerance(ttfp.getTolerance());
            }
          }

        } else {
          throw new TemplateException("invalid template field type for field [" + tplField.getFieldNo() + ";"
            + tplField.getClass().getName() + "]");
        }
      }
      field.setFieldNo(tplField.getFieldNo());
      term.setTerm(transformer.transformNode(field));
    } else if (term.getTerm() instanceof DOP1) {

      transformSearchQuery(term.getOP1().getTerm(), fields, is, ic);

    } else if (term.getTerm() instanceof DOPN) {

      final DOPN op = term.getOpN();
      for (int i = 0; i < op.getTermCount(); i++) {
        transformSearchQuery(op.getTerm(i), fields, is, ic);
      }

    } else if (term.getTerm() instanceof DWMEAN) {

      final DWMEAN op = term.getWMEAN();
      for (int i = 0; i < op.getTermCount(); i++) {
        transformSearchQuery(op.getTerm(i), fields, is, ic);
      }

    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.plugin.ITemplateAccess#applyFieldTemplate(org.eclipse.smila.search.utils.search.DField,
   *      org.eclipse.smila.search.templates.messages.fieldtemplates.DTemplate,
   *      org.eclipse.smila.search.index.IndexConnection)
   */
  public ITerm applyFieldTemplate(DField dField,
    org.eclipse.smila.search.templates.messages.fieldtemplates.DFieldTemplate dTemplate, IndexConnection ic)
    throws TemplateException, NodeTransformerException, IndexException {
    DFieldTemplate template = null;
    try {
      template = (DFieldTemplate) dTemplate.clone();
    } catch (final CloneNotSupportedException e) {
    }
    final DTerm dTerm = (DTerm) template.getTerm();
    // set values
    evaluateTemplateFields(dTerm, dField, ic);
    return dTerm;
  }

  protected void evaluateTemplateFields(DTerm newField, DField dField, IndexConnection ic)
    throws NodeTransformerException, TemplateException, IndexException {
    final ITermContent tc = newField.getTerm();
    if (tc instanceof DWMEAN) {
      for (final Iterator i = ((DWMEAN) tc).getTerms(); i.hasNext();) {
        evaluateTemplateFields((DTerm) i.next(), dField, ic);
      }
    } else if (tc instanceof DOPN) {
      for (final Iterator i = ((DOPN) tc).getTerms(); i.hasNext();) {
        evaluateTemplateFields((DTerm) i.next(), dField, ic);
      }
    } else if (tc instanceof DOP1) {
      evaluateTemplateFields(((DOP1) tc).getTerm(), dField, ic);
    } else if (tc instanceof DTemplateField) {

      final DTemplateField tplField = (DTemplateField) tc;
      if (tplField.getSourceFieldNo() != dField.getFieldNo()) {
        throw new TemplateException("SourceFieldNo in TemplateField (" + tplField.getSourceFieldNo()
          + ") must match FieldNo in search field (" + dField.getFieldNo() + ")");
      }

      NodeTransformer transformer = null;
      DNodeTransformer transformerDef = ((DTemplateField) tc).getNodeTransformer();
      if (transformerDef == null) {
        transformerDef = dField.getNodeTransformer();
      }
      transformer = NodeTransformerRegistryController.getNodeTransformer(transformerDef, ic);

      // change field nos
      // TODO: change field type checks. Resulting field must be of same type as target field,
      // not source field. All necessary conversions must be performed.
      final DField fld = (DField) dField.clone();
      if (tplField.getNodeTransformer() != null) {
        transformer = NodeTransformerRegistryController.getNodeTransformer(tplField.getNodeTransformer(), ic);
        if (_log.isDebugEnabled()) {
          _log.debug("using node transformer from template field [" + tplField.getFieldNo() + ";"
            + tplField.getNodeTransformer() + "]");
        }
      }

      if (dField instanceof org.eclipse.smila.search.utils.search.DTextField) {

        final DTextFieldParameter tfp =
          (DTextFieldParameter) ((org.eclipse.smila.search.utils.search.DTextField) fld).getParameter();

        if (tplField instanceof DTextTemplateField) {
          final DTextTemplateField ttf = (DTextTemplateField) tplField;

          if (ttf.getParameter() != null) {
            final DTextFieldParameter ttfp = ttf.getParameter();
            if (ttfp.getOperator() != null) {
              if (_log.isDebugEnabled()) {
                _log.debug("using operator from template field [" + tplField.getFieldNo() + ";"
                  + ttfp.getOperator().toString() + "]");
              }
              tfp.setOperator(ttfp.getOperator());
            }

            if (ttfp.getTolerance() != null) {
              if (_log.isDebugEnabled()) {
                _log.debug("using tolerance from template field [" + tplField.getFieldNo() + ";"
                  + ttfp.getTolerance().toString() + "]");
              }
              tfp.setTolerance(ttfp.getTolerance());
            }
          }

        } else {
          throw new TemplateException("invalid template field type for field [" + tplField.getFieldNo() + ";"
            + tplField.getClass().getName() + "]");
        }
      }

      fld.setFieldNo(tplField.getFieldNo());
      newField.setTerm(transformer.transformNode(fld));
    }

  }

}

package org.eclipse.smila.search.servlet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smila.datamodel.record.InvalidTypeException;
import org.eclipse.smila.processing.parameters.SearchParameters;
import org.eclipse.smila.processing.parameters.SearchAnnotations.FilterMode;
import org.eclipse.smila.processing.parameters.SearchParameters.OrderMode;
import org.eclipse.smila.search.api.helper.ParameterAnnotation;
import org.eclipse.smila.search.api.helper.QueryBuilder;
import org.eclipse.smila.utils.collections.MultiValueMap;

/**
 * base class for request parsers.
 * 
 * @author jschumacher
 */
public class ARequestParser {

  /**
   * prefix for http parameter names specifying attribute values. The attribute name is the rest of the parameter name
   * after this prefix.
   */
  public static final String PREFIX_ATTRIBUTE = "A.";

  /**
   * prefix for OrderBy parameter. After the prefix follows the attribute name, the value is either ASC or DESC.
   */
  public static final String PREFIX_ORDERBY = SearchParameters.ORDERBY + ".";

  /**
   * prefix for filter mode parameter. Rest of parameter name is attribute name.
   */
  public static final String PREFIX_FILTER = "F.";

  /**
   * prefix for enum filter values. Rest of parameter name is attribute name.
   */
  public static final String PREFIX_FILTER_VAL = "Fval.";

  /**
   * prefix for range filter min values. Rest of parameter name is attribute name.
   */
  public static final String PREFIX_FILTER_MIN = "Fmin.";

  /**
   * prefix for range filter max values. Rest of parameter name is attribute name.
   */
  public static final String PREFIX_FILTER_MAX = "Fmax.";

  /**
   * prefix for ranking annotations. Rest of parameter name is value name dot attribute name.
   */
  public static final String PREFIX_RANKING = "R.";

  /**
   * name of parameter specifying the pipelet name.
   */
  public static final String PARAM_PIPELINE = "pipeline";

  /**
   * name of pipeline to use, if the request does not contain a pipeline parameter.
   */
  protected String _defaultPipeline;

  /**
   * collects filter modes during parameter processing.
   */
  private Map<String, FilterMode> _filterModes = new HashMap<String, FilterMode>();

  /**
   * collects enum filter values during parameter processing.
   */
  private MultiValueMap<String, String> _filterValues = new MultiValueMap<String, String>();

  /**
   * collects range filter min values during parameter processing.
   */
  private Map<String, String> _filterMin = new HashMap<String, String>();

  /**
   * collects range filter max values during parameter processing.
   */
  private Map<String, String> _filterMax = new HashMap<String, String>();

  /**
   * create new instance with default pipeline.
   * 
   * @param defaultPipeline
   *          default pipeline name to use, if request does not contain a pipeline parameter.
   */
  public ARequestParser(String defaultPipeline) {
    super();
    _defaultPipeline = defaultPipeline;
  }

  /**
   * create filters from information collected during http parameter processing.
   * 
   * @param builder
   *          query builder
   */
  protected void setupFilters(QueryBuilder builder) {
    for (String attributeName : _filterValues.keySet()) {
      setupEnumFilter(builder, attributeName);
    }
    final Set<String> rangeFilterAttributes = new HashSet<String>();
    rangeFilterAttributes.addAll(_filterMin.keySet());
    rangeFilterAttributes.addAll(_filterMax.keySet());
    for (String attributeName : rangeFilterAttributes) {
      setupRangeFilter(builder, attributeName);
    }
  }

  /**
   * add an enum filter.
   * 
   * @param builder
   *          query builder.
   * @param attributeName
   *          attribute name.
   */
  protected void setupEnumFilter(QueryBuilder builder, String attributeName) {
    final List<String> values = _filterValues.get(attributeName);
    if (values != null && values.size() > 0) {
      FilterMode mode = _filterModes.get(attributeName);
      if (mode == null) {
        mode = FilterMode.ANY;
      }
      try {
        builder.addEnumFilter(attributeName, mode, values);
      } catch (Exception ex) {
        ex = null; // ignore. strings are ok here.
      }
    }
  }

  /**
   * add a range filter.
   * 
   * @param builder
   *          query builder.
   * @param attributeName
   *          attribute name.
   */
  protected void setupRangeFilter(QueryBuilder builder, String attributeName) {
    FilterMode mode = _filterModes.get(attributeName);
    if (mode == null) {
      mode = FilterMode.ANY;
    }
    try {
      builder.addRangeFilter(attributeName, mode, _filterMin.get(attributeName), _filterMax.get(attributeName));
    } catch (Exception ex) {
      ex = null; // ignore. strings are ok here.
    }
  }

  /**
   * set default parameters for resultSize and resultOffset parameters, if no values where found in the request. This is
   * done mainly to make life easier for the XSL sheet processing the result.
   * 
   * @param builder
   *          query builder created from http request.
   */
  protected void setDefaultParameters(QueryBuilder builder) {
    final ParameterAnnotation parameters = builder.getParameters();
    if (parameters.getParameter(SearchParameters.RESULTSIZE) == null) {
      parameters.setIntParameter(SearchParameters.RESULTSIZE, SearchParameters.DEFAULT_RESULTSIZE);
    }
    if (parameters.getParameter(SearchParameters.RESULTOFFSET) == null) {
      parameters.setIntParameter(SearchParameters.RESULTOFFSET, SearchParameters.DEFAULT_RESULTOFFSET);
    }
  }

  /**
   * process http param values.
   * 
   * @param builder
   *          query builder
   * @param paramName
   *          parameter name
   * @param paramValues
   *          parameter values, must not be null or empty.
   */
  protected void processParameter(QueryBuilder builder, String paramName, String[] paramValues) {
    if (paramName.startsWith(PREFIX_RANKING)) {
      processRankingAnnotation(builder, paramName, paramValues);
    } else if (paramName.startsWith(PREFIX_FILTER)) {
      processFilterMode(paramName, paramValues);
    } else if (paramName.startsWith(PREFIX_FILTER_VAL)) {
      processFilterValue(paramName, paramValues);
    } else if (paramName.startsWith(PREFIX_FILTER_MIN)) {
      processFilterMin(paramName, paramValues);
    } else if (paramName.startsWith(PREFIX_FILTER_MAX)) {
      processFilterMax(paramName, paramValues);
    } else if (paramName.startsWith(PREFIX_ATTRIBUTE)) {
      final String[] pathElements = paramName.substring(PREFIX_ATTRIBUTE.length()).split("\\.");
      if (pathElements != null && pathElements.length > 0) {
        if (pathElements.length == 1) {
          if (!StringUtils.isEmpty(pathElements[0])) {
            setAttributeValues(builder, pathElements[0], paramValues);
          }
        } else if (pathElements.length > 2) {
          setAttributeAnnotations(builder, pathElements, paramValues);
        }
      }
    } else {
      setParameterValues(builder, paramName, paramValues);
    }

  }

  /**
   * process a parameter name of the form "Fmax.&lt;attributeName>" to set a max value in a range filter annotation for
   * an attribute.
   * 
   * @param paramName
   *          parameter name
   * @param paramValues
   *          parameter values. Only the first value is used.
   */
  private void processFilterMax(String paramName, String[] paramValues) {
    final String attributeName = paramName.substring(PREFIX_FILTER_MAX.length());
    if (!StringUtils.isEmpty(attributeName) && !StringUtils.isEmpty(paramValues[0])) {
      _filterMax.put(attributeName, paramValues[0]);
    }
  }

  /**
   * process a parameter name of the form "Fmin.&lt;attributeName>" to set a min value in a range filter annotation for
   * an attribute.
   * 
   * @param paramName
   *          parameter name
   * @param paramValues
   *          parameter values. Only the first value is used.
   */
  private void processFilterMin(String paramName, String[] paramValues) {
    final String attributeName = paramName.substring(PREFIX_FILTER_MIN.length());
    if (!StringUtils.isEmpty(attributeName) && !StringUtils.isEmpty(paramValues[0])) {
      _filterMin.put(attributeName, paramValues[0]);
    }
  }

  /**
   * process a parameter name of the form "Fval.&lt;attributeName>" to set a filter mode annotation for an attribute.
   * 
   * @param paramName
   *          parameter name
   * @param paramValues
   *          parameter values. All values are added to the filter annotation.
   */
  private void processFilterValue(String paramName, String[] paramValues) {
    final String attributeName = paramName.substring(PREFIX_FILTER_VAL.length());
    if (!StringUtils.isEmpty(attributeName)) {
      for (String value : paramValues) {
        if (!StringUtils.isEmpty(value)) {
          _filterValues.add(attributeName, value);
        }
      }
    }
  }

  /**
   * process a parameter name of the form "F.&lt;attributeName>" to set a filter mode annotation for an attribute.
   * 
   * @param paramName
   *          parameter name
   * @param paramValues
   *          parameter values. Only the first value is used.
   */
  private void processFilterMode(String paramName, String[] paramValues) {
    final String attributeName = paramName.substring(PREFIX_FILTER.length());
    if (!StringUtils.isEmpty(attributeName) && !StringUtils.isEmpty(paramValues[0])) {
      try {
        final FilterMode mode = FilterMode.valueOf(paramValues[0].toUpperCase());
        _filterModes.put(attributeName, mode);
      } catch (Exception ex) {
        ex = null; // illegal mode value.
      }
    }
  }

  /**
   * process a parameter name of the form "Rank.&lt;valueName>" or "Rank.&lt;valueName>.&lt;attributeName>" to set
   * ranking annotation values either on the record or one of its attributes.
   * 
   * @param builder
   *          query builder
   * @param paramName
   *          parameter name
   * @param paramValues
   *          parameter values. Only the first value is used.
   */
  private void processRankingAnnotation(QueryBuilder builder, String paramName, String[] paramValues) {
    final String valueAndAttributeName = paramName.substring(PREFIX_RANKING.length());
    final int dotIndex = valueAndAttributeName.indexOf('.');
    String valueName = null;
    String attributeName = null;
    if (dotIndex < 0) {
      valueName = valueAndAttributeName;
    } else {
      valueName = valueAndAttributeName.substring(0, dotIndex);
      attributeName = valueAndAttributeName.substring(dotIndex + 1);
    }
    if (!StringUtils.isEmpty(paramValues[0])) {
      builder.getRankingAnnotation(attributeName).setNamedValue(valueName, paramValues[0]);
    }
  }

  /**
   * set attribute values from http params.
   * 
   * @param builder
   *          query builder
   * @param attributeName
   *          attribute name (param name without {@link #PREFIX_ATTRIBUTE})
   * @param paramValues
   *          parameter values, must not be null or empty.
   */
  protected void setAttributeValues(QueryBuilder builder, String attributeName, String[] paramValues) {
    for (String value : paramValues) {
      if (!StringUtils.isEmpty(value)) {
        try {
          builder.addLiteral(attributeName, value);
        } catch (InvalidTypeException ex) {
          ex = null; // ignore. String *is* a supported value type.
        }
      }
    }
  }

  protected void setAttributeAnnotations(QueryBuilder builder, String[] pathElements, String[] paramValues) {
    final String attributeName = pathElements[0];
    for (String value : paramValues) {
      if (!StringUtils.isEmpty(value)) {
        try {
          final String annotationName = pathElements[pathElements.length - 1];
          final String[] annotationNames = new String[pathElements.length - 2];
          System.arraycopy(pathElements, 1, annotationNames, 0, pathElements.length - 2);
          builder.addAnnotationNamedValue(attributeName, annotationNames, annotationName, value);
        } catch (InvalidTypeException ex) {
          ex = null; // ignore. String *is* a supported value type.
        }
      }
    }
  }

  /**
   * set query parameters from http params.
   * 
   * @param builder
   *          query builder
   * @param paramName
   *          parameter name
   * @param paramValues
   *          parameter values, must not be null or empty.
   */
  protected void setParameterValues(QueryBuilder builder, String paramName, String[] paramValues) {
    // set first value as single-value-parameter
    if (paramName.startsWith(PREFIX_ORDERBY)) {
      final String attributeName = paramName.substring(PREFIX_ORDERBY.length());
      if (!StringUtils.isEmpty(attributeName) && !StringUtils.isEmpty(paramValues[0])) {
        try {
          final OrderMode orderMode = OrderMode.valueOf(paramValues[0].toUpperCase());
          builder.addOrderBy(attributeName, orderMode);
        } catch (Exception ex) {
          ex = null; // illegal order by mode
        }
      }
    } else if (paramValues.length == 1) {
      if (!StringUtils.isEmpty(paramValues[0])) {
        builder.setParameter(paramName, paramValues[0]);
      }
    } else {
      // set all values als list parameter.
      for (String value : paramValues) {
        if (!StringUtils.isEmpty(value)) {
          builder.addParameter(paramName, value);
        }
      }
    }
  }

}

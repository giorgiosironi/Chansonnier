/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.security.processing;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessingService;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.configuration.PipeletConfigurationLoader;
import org.eclipse.smila.processing.parameters.SearchAnnotations;
import org.eclipse.smila.security.SecurityAnnotation;
import org.eclipse.smila.security.SecurityException;
import org.eclipse.smila.security.SecurityResolver;
import org.eclipse.smila.security.SecurityAnnotations.AccessRightType;
import org.eclipse.smila.security.SecurityAnnotations.EntityType;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.osgi.service.component.ComponentContext;

/**
 * Sample Security Converter Index Service.
 */
public class SampleSecurityConverter implements ProcessingService {

  /**
   * name of bundle. Used in configuration reading.
   */
  public static final String BUNDLE_NAME = "org.eclipse.smila.security.processing";

  /**
   * name of configuration file. Hardcoded for now (or fallback), configuration properties should be received from
   * configuration service later.
   */
  public static final String CONFIG_FILE = "SampleSecurityConverter.xml";

  /**
   * Constant for the property readUsersAttributeName.
   */
  public static final String PROP_READ_USERS_ATTRIBUTE_NAME = "readUsersAttributeName";

  /**
   * Constant for the property resolveGroups.
   */
  public static final String PROP_RESOLVE_GROUPS = "resolveGroups";

  /**
   * Constant for the property resolveUserNames.
   */
  public static final String PROP_RESOLVE_USER_NAMES = "resolveUserNames";

  /**
   * Constant for the property resolvedUserNamePropertyName.
   */
  public static final String PROP_RESOLVED_USER_NAME_PROPERTY_NAME = "resolvedUserNamePropertyName";

  /**
   * name of annotation configuring the type of execution.
   */
  public static final String EXECUTION_MODE = "executionMode";

  /**
   * Types of execution modes this service supports.
   */
  public enum ExecutionMode {

    /**
     * Add the record to the index.
     */
    INDEX,

    /**
     * Delete the id from the index.
     */
    SEARCH
  };

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(SampleSecurityConverter.class);

  /**
   * The configuration.
   */
  private PipeletConfiguration _configuration;

  /**
   * Name of the attribute to store the users with read access in.
   */
  private String _readUsersAttributeName;

  /**
   * Boolean flag if to resolve groups to users.
   */
  private boolean _resolveGroups;

  /**
   * Boolean flag if to resolver users to display names.
   */
  private boolean _resolveUserNames;

  /**
   * The property to retrieve for a user as display name.
   */
  private String _resolvedUserNameProperty;

  /**
   * The SecurityResolver to use (optional).
   */
  private SecurityResolver _securityResolver;

  /**
   * DS activate method.
   * 
   * @param context
   *          ComponentContext
   * 
   * @throws Exception
   *           if any error occurs
   */
  protected void activate(final ComponentContext context) throws Exception {
    try {
      // load configuration
      if (_configuration == null) {
        readConfiguration();
        _readUsersAttributeName =
          (String) _configuration.getPropertyFirstValueNotNull(PROP_READ_USERS_ATTRIBUTE_NAME);
        _resolveGroups =
          ((Boolean) _configuration.getPropertyFirstValueNotNull(PROP_RESOLVE_GROUPS)).booleanValue();
        _resolveUserNames =
          ((Boolean) _configuration.getPropertyFirstValueNotNull(PROP_RESOLVE_USER_NAMES)).booleanValue();
        _resolvedUserNameProperty =
          (String) _configuration.getPropertyFirstValueNotNull(PROP_RESOLVED_USER_NAME_PROPERTY_NAME);
      }
    } catch (final Exception e) {
      if (_log.isErrorEnabled()) {
        _log.error("error initializing SampleSecurityConverter", e);
      }
      throw e;
    }
  }

  /**
   * DS deactivate method.
   * 
   * @param context
   *          the ComponentContext
   * 
   * @throws Exception
   *           if any error occurs
   */
  protected void deactivate(final ComponentContext context) throws Exception {
    try {
      _configuration = null;
      _readUsersAttributeName = null;
      _resolvedUserNameProperty = null;
    } catch (final Exception e) {
      if (_log.isErrorEnabled()) {
        _log.error("error deactivating SampleSecurityConverter", e);
      }
      throw e;
    }
  }

  /**
   * Sets the _securityResolver. Used by OSGi Declarative Services.
   * 
   * @param securityResolver
   *          the SecurityResolver to set
   */
  public void setSecurityResolver(final SecurityResolver securityResolver) {
    _securityResolver = securityResolver;
  }

  /**
   * Set the _securityResolver to null. Used by OSGi Declarative Services.
   * 
   * @param securityResolver
   *          the SecurityResolver to unset
   */
  public void unsetSecurityResolver(final SecurityResolver securityResolver) {
    if (_securityResolver == securityResolver) {
      _securityResolver = null;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.ProcessingService#process(Blackboard, Id[])
   */
  public Id[] process(final Blackboard blackboard, final Id[] recordIds) throws ProcessingException {
    for (int i = 0; i < recordIds.length; i++) {
      try {
        final Annotation pipeletAnnotation = blackboard.getAnnotation(recordIds[i], null, getClass().getName());
        if (pipeletAnnotation != null) {
          final String executionModeValue = pipeletAnnotation.getNamedValue(EXECUTION_MODE);
          final ExecutionMode executionMode = ExecutionMode.valueOf(executionModeValue);
          switch (executionMode) {
            case INDEX:
              convertToAttributes(blackboard, recordIds[i]);
              break;
            case SEARCH:
              converteToFilter(blackboard, recordIds[i]);
              break;
            default:
              break;
          }
        }
      } catch (final Exception ex) {
        if (_log.isErrorEnabled()) {
          _log.error("error processing record " + recordIds[i], ex);
        }
      }
    } // for
    return recordIds;
  }

  /**
   * Converts the security annotations of a record into an attribute with values for indexing.
   * 
   * @param blackboard
   *          the BlackboardService
   * @param id
   *          the record Id
   * @throws BlackboardAccessException
   *           if any error occurs
   * @throws SecurityException
   *           if any security error occurs
   */
  private void convertToAttributes(final Blackboard blackboard, final Id id)
    throws BlackboardAccessException, SecurityException {
    final SecurityAnnotation sa = new SecurityAnnotation(blackboard.getRecord(id));
    final Set<String> readAccessRights = getReadAccessRights(sa);

    if (!readAccessRights.isEmpty()) {
      // create attribute and add values
      final Path path = new Path(_readUsersAttributeName);
      for (String value : readAccessRights) {
        final Literal literal = blackboard.getRecord(id).getFactory().createLiteral();
        literal.setStringValue(value);
        blackboard.addLiteral(id, path, literal);
      }
    }

    if (_log.isTraceEnabled()) {
      _log.trace("converted security annotations for id " + id + " into attribute values");
    }

  }

  /**
   * Converts the security annotations of a record into a query filter and appends it to the query.
   * 
   * @param blackboard
   *          the BlackboardService
   * @param id
   *          the record Id
   * @throws BlackboardAccessException
   *           if any error occurs
   * @throws SecurityException
   *           if any security error occurs
   */
  private void converteToFilter(final Blackboard blackboard, final Id id) throws BlackboardAccessException,
    SecurityException {
    final SecurityAnnotation sa = new SecurityAnnotation(blackboard.getRecord(id));
    final Set<String> readAccessRights = getReadAccessRights(sa);

    if (!readAccessRights.isEmpty()) {
      // create enumeration filter and add it to the configured _readUsersAttributeName
      final Annotation filter = blackboard.getRecord(id).getFactory().createAnnotation();
      filter.setNamedValue(SearchAnnotations.FILTER_TYPE, SearchAnnotations.FilterType.ENUMERATION.toString());
      filter.setNamedValue(SearchAnnotations.FILTER_MODE, SearchAnnotations.FilterMode.ANY.name());
      for (String value : readAccessRights) {
        filter.addAnonValue(value);
      }

      final Path readUsersAttributePath = new Path(_readUsersAttributeName);
      // ensure that the attribute exists
      if (!blackboard.hasAttribute(id, readUsersAttributePath)) {
        final Literal literal = blackboard.getRecord(id).getFactory().createLiteral();
        literal.setStringValue("dummy");
        blackboard.addLiteral(id, readUsersAttributePath, literal);
      }
      blackboard.getRecord(id).getMetadata().getAttribute(_readUsersAttributeName).addAnnotation(
        SearchAnnotations.FACET_FILTER, filter);

      blackboard.addAnnotation(id, readUsersAttributePath, SearchAnnotations.FACET_FILTER, filter);
    }

    if (_log.isTraceEnabled()) {
      _log.trace("converted security annotations for id " + id + " into query filter");
    }
  }

  /**
   * Gets the access rights values from the security annotations. Depending on the configuration the return values are
   * the plain values provided by a crawler/search client or are resolved against a SecurityResolver.
   * 
   * @param sa
   *          the SecurityAnnotation
   * @return a Set of Strings containing the values
   * @throws BlackboardAccessException
   *           if any error occurs
   * @throws SecurityException
   *           if any security error occurs
   */
  private Set<String> getReadAccessRights(SecurityAnnotation sa) throws BlackboardAccessException,
    SecurityException {
    final HashSet<String> accessRights = new HashSet<String>();
    final Collection<String> users =
      sa.getAccessRights(AccessRightType.READ, EntityType.PRINCIPALS).getAnonValues();
    // check if there was a security resolver set, else skip any resolving
    if (_securityResolver != null) {
      if (users != null) {
        for (String user : users) {
          final String userDN = _securityResolver.resolvePrincipal(user);
          accessRights.add(userDN);
        }
      }

      // check if to resolve members of groups
      if (_resolveGroups) {
        final Collection<String> groups =
          sa.getAccessRights(AccessRightType.READ, EntityType.GROUPS).getAnonValues();
        if (groups != null) {
          for (String group : groups) {
            final String groupDN = _securityResolver.resolvePrincipal(group);
            final Set<String> groupMembers = _securityResolver.resolveGroupMembers(groupDN);
            accessRights.addAll(groupMembers);
          } // for
        } // if
      } // if

      // check if to resolve user names to some display name
      Set<String> displayNames = new HashSet<String>();
      if (_resolveUserNames) {
        for (String principalDN : accessRights) {
          final Map<String, Collection<String>> properties = _securityResolver.getProperties(principalDN);
          final Collection<String> resolvedUserNames = properties.get(_resolvedUserNameProperty);
          if (resolvedUserNames != null && !resolvedUserNames.isEmpty()) {
            displayNames.add(resolvedUserNames.iterator().next());
          }
        } // for
      } else {
        displayNames = accessRights;
      }
      return displayNames;
    } else {
      if (users != null) {
        accessRights.addAll(users);
      }
      return accessRights;
    }
  }

  /**
   * Read configuration property file.
   * 
   * @throws ProcessingException
   *           error reading configuration file
   */
  private void readConfiguration() throws ProcessingException {
    InputStream configurationFileStream = null;
    try {
      configurationFileStream = ConfigUtils.getConfigStream(BUNDLE_NAME, CONFIG_FILE);
      final Unmarshaller unmarshaller = PipeletConfigurationLoader.createPipeletConfigurationUnmarshaller();
      _configuration = (PipeletConfiguration) unmarshaller.unmarshal(configurationFileStream);
    } catch (final Exception ex) {
      if (_log.isErrorEnabled()) {
        _log.error("Could not read configuration property file " + CONFIG_FILE, ex);
      }
      throw new ProcessingException("Could not read configuration property file " + CONFIG_FILE, ex);
    } finally {
      IOUtils.closeQuietly(configurationFileStream);
    }
  }
}

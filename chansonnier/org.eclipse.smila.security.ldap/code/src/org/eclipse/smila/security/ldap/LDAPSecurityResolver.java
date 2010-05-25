/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.security.ldap;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.security.SecurityException;
import org.eclipse.smila.security.SecurityResolver;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.osgi.service.component.ComponentContext;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPSearchResults;

/**
 * A LDAP SecurityResolver.
 */
public class LDAPSecurityResolver implements SecurityResolver {

  /**
   * name of bundle. Used in configuration reading.
   */
  public static final String BUNDLE_NAME = "org.eclipse.smila.security.ldap";

  /**
   * name of configuration file. Hardcoded for now (or fallback), configuration properties should be received from
   * configuration service later.
   */
  public static final String CONFIGURATION_FILE = "ldap.properties";

  /**
   * The configuration property ldap.host.
   */
  public static final String PROPERTY_LDAP_HOST = "ldap.host";

  /**
   * The configuration property ldap.port.
   */
  public static final String PROPERTY_LDAP_PORT = "ldap.port";

  /**
   * The configuration property ldap.login.
   */
  public static final String PROPERTY_LDAP_LOGIN = "ldap.login";

  /**
   * The configuration property ldap.password.
   */
  public static final String PROPERTY_LDAP_PASSWORD = "ldap.password";

  /**
   * The configuration property ldap.base.dn.
   */
  public static final String PROPERTY_LDAP_BASE_DN = "ldap.base.dn";

  /**
   * The configuration property ldap.base.dn.
   */
  public static final String PROPERTY_LDAP_USER_NAME_ATTRIBUTE = "ldap.username.attribute";

  /**
   * The default value for the user name attribute.
   */
  public static final String DEFAULT_LDAP_USER_NAME_ATTRIBUTE = "cn";

  /**
   * Constant for the LDAP attribute objectClass.
   */
  public static final String LDAP_ATTRIBUTE_OBJECT_CLASS = "objectClass";

  /**
   * Constant for the LDAP attribute member.
   */
  public static final String LDAP_ATTRIBUTE_MEMBER = "member";

  /**
   * Constant for the LDAP attribute uniqueMember.
   */
  public static final String LDAP_ATTRIBUTE_UNIQUE_MEMBER = "uniqueMember";

  /**
   * Constant for the LDAP attribute memberOf.
   */
  public static final String LDAP_ATTRIBUTE_MEMBER_OF = "memberOf";

  /**
   * Constant for the LDAP object name group.
   */
  public static final String LDAP_OBJECT_NAME_GROUP = "group";

  /**
   * Constant for the LDAP object name groupOfNames.
   */
  public static final String LDAP_OBJECT_NAME_GROUP_OF_NAMES = "groupOfNames";

  /**
   * Constant for the LDAP object name groupOfUniqueNames.
   */
  public static final String LDAP_OBJECT_NAME_GROUP_OF_UNIQUE_NAMES = "groupOfUniqueNames";

  /**
   * Constant for the LDAP object name dynamicGroup.
   */
  public static final String LDAP_OBJECT_NAME_DYNAMIC_GROUP = "dynamicGroup";

  /**
   * Constant for the LDAP object name dynamicGroupAux.
   */
  public static final String LDAP_OBJECT_NAME_DYNAMIC_GROUP_AUX = "dynamicGroupAux";

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(LDAPSecurityResolver.class);

  /**
   * configuraton properties.
   */
  private Properties _properties;

  /**
   * The LDAP connection.
   */
  private LDAPConnection _ldapConnection;

  /**
   * Default Constructor.
   */
  public LDAPSecurityResolver() {
    if (_log.isTraceEnabled()) {
      _log.trace("creating instance of LDAPSecurityResolver");
    }
  }

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
      readConfiguration();
      ensureConnection();
    } catch (final Exception e) {
      if (_log.isErrorEnabled()) {
        _log.error("error activating LDAPSecurityResolver service", e);
      }
      throw e;
    }
    if (_log.isTraceEnabled()) {
      _log.trace("started LDAPSecurityResolver service");
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
    if (_properties != null) {
      _properties.clear();
      _properties = null;
    }

    if (_ldapConnection != null) {
      _ldapConnection.disconnect();
    }

    if (_log.isTraceEnabled()) {
      _log.trace("deactivated LDAPSecurityResolver service");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.security.SecurityResolver#resolvePrincipal(java.lang.String)
   */
  public String resolvePrincipal(String name) throws SecurityException {
    if (name == null) {
      throw new SecurityException("parameter name is null");
    }
    ensureConnection();
    try {
      final String filter =
        _properties.getProperty(PROPERTY_LDAP_USER_NAME_ATTRIBUTE, DEFAULT_LDAP_USER_NAME_ATTRIBUTE) + "=" + name;
      final LDAPSearchResults searchResults =
        _ldapConnection.search(_properties.getProperty(PROPERTY_LDAP_BASE_DN), LDAPConnection.SCOPE_SUB, filter,
          null, true);
      if (searchResults != null && searchResults.hasMore()) {
        final LDAPEntry entry = searchResults.next();
        final String dn = entry.getDN();
        return dn;
      }
      return null;
    } catch (Exception e) {
      final String msg = "error resolving name " + name;
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      throw new SecurityException(msg, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.security.SecurityResolver#getProperties(java.lang.String)
   */
  public Map<String, Collection<String>> getProperties(String principal) throws SecurityException {
    if (principal == null) {
      throw new SecurityException("parameter principal is null");
    }
    ensureConnection();
    try {
      final HashMap<String, Collection<String>> properties = new HashMap<String, Collection<String>>();

      final LDAPEntry entry = _ldapConnection.read(principal);
      final LDAPAttributeSet attributeSet = entry.getAttributeSet();
      if (attributeSet != null) {
        final Iterator it = attributeSet.iterator();
        while (it.hasNext()) {
          final LDAPAttribute attribute = (LDAPAttribute) it.next();
          if (attribute != null) {
            final ArrayList<String> values = new ArrayList<String>();
            final String name = attribute.getName();
            final Enumeration valueEnum = attribute.getStringValues();
            while (valueEnum.hasMoreElements()) {
              final String value = (String) valueEnum.nextElement();
              values.add(value);
            } // while
            properties.put(name, values);
          } // if
        } // while
      } // if
      return properties;
    } catch (Exception e) {
      final String msg = "error getting properties of principal " + principal;
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      throw new SecurityException(msg, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.security.SecurityResolver#resolveGroupMembers(java.lang.String)
   */
  public Set<String> resolveGroupMembers(String group) throws SecurityException {
    if (group == null) {
      throw new SecurityException("parameter group is null");
    }
    ensureConnection();
    try {
      final HashSet<String> members = new HashSet<String>();

      final LDAPEntry entry = _ldapConnection.read(group);
      final LDAPAttributeSet attributeSet = entry.getAttributeSet();
      if (attributeSet != null) {
        if (containsGroupAttribute(attributeSet)) {
          LDAPAttribute attribute = attributeSet.getAttribute(LDAP_ATTRIBUTE_MEMBER);
          appendGroupMembers(members, attribute);
          attribute = attributeSet.getAttribute(LDAP_ATTRIBUTE_UNIQUE_MEMBER);
          appendGroupMembers(members, attribute);
        } else {
          throw new SecurityException("group " + group + " is not a group");
        }
      } // if
      return members;
    } catch (SecurityException e) {
      throw e;
    } catch (Exception e) {
      final String msg = "error resolving members for group " + group;
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      throw new SecurityException(msg, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.security.SecurityResolver#resolveMembership(java.lang.String)
   */
  public Set<String> resolveMembership(String principal) throws SecurityException {
    if (principal == null) {
      throw new SecurityException("parameter principal is null");
    }
    ensureConnection();
    try {
      final HashSet<String> groups = new HashSet<String>();

      final LDAPEntry entry = _ldapConnection.read(principal);
      final LDAPAttributeSet attributeSet = entry.getAttributeSet();
      if (attributeSet != null) {
        final LDAPAttribute attribute = attributeSet.getAttribute(LDAP_ATTRIBUTE_MEMBER_OF);
        if (attribute != null) {
          final Enumeration values = attribute.getStringValues();
          while (values.hasMoreElements()) {
            final String group = (String) values.nextElement();
            groups.add(group);
          } // while
        } // if
      } // if
      return groups;
    } catch (Exception e) {
      final String msg = "error resolving membership of principal " + principal;
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      throw new SecurityException(msg, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.security.SecurityResolver#isGroup(java.lang.String)
   */
  public boolean isGroup(String principal) throws SecurityException {
    if (principal == null) {
      throw new SecurityException("parameter principal is null");
    }
    ensureConnection();
    try {
      final LDAPEntry entry = _ldapConnection.read(principal);
      final LDAPAttributeSet attributeSet = entry.getAttributeSet();
      return containsGroupAttribute(attributeSet);
    } catch (Exception e) {
      final String msg = "error checking if principal " + principal + " is a group";
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      throw new SecurityException(msg, e);
    }
  }

  /**
   * Checks if the attributeSet contains a attribute "objectClass" that has a object name identifying a group.
   * 
   * @param attributeSet
   *          an LDAPAttributeSet
   * @return true if the attribute "objectClass" has a object name identifying a group, false otherwise
   */
  private boolean containsGroupAttribute(LDAPAttributeSet attributeSet) {
    if (attributeSet != null) {
      final LDAPAttribute attribute = attributeSet.getAttribute(LDAP_ATTRIBUTE_OBJECT_CLASS);
      if (attribute != null) {
        final Enumeration objectClassEnum = attribute.getStringValues();
        while (objectClassEnum.hasMoreElements()) {
          final String objectName = (String) objectClassEnum.nextElement();
          if (LDAP_OBJECT_NAME_GROUP.equalsIgnoreCase(objectName)
            || LDAP_OBJECT_NAME_GROUP_OF_NAMES.equalsIgnoreCase(objectName)
            || LDAP_OBJECT_NAME_GROUP_OF_UNIQUE_NAMES.equalsIgnoreCase(objectName)
            || LDAP_OBJECT_NAME_DYNAMIC_GROUP.equalsIgnoreCase(objectName)
            || LDAP_OBJECT_NAME_DYNAMIC_GROUP_AUX.equalsIgnoreCase(objectName)) {
            return true;
          } // if
        } // while
      } // if
    } // if
    return false;
  }

  /**
   * Appends all values of the given attribute to the given set of members.
   * 
   * @param members
   *          the set of meembers to append the values to
   * @param attribute
   *          the LDAPAttribute containing the values
   */
  private void appendGroupMembers(HashSet<String> members, LDAPAttribute attribute) {
    if (attribute != null) {
      final Enumeration values = attribute.getStringValues();
      while (values.hasMoreElements()) {
        final String member = (String) values.nextElement();
        members.add(member);
      } // while
    } // if
  }

  /**
   * read configuration property file.
   * 
   * @throws IOException
   *           error reading configuration file
   */
  private void readConfiguration() throws IOException {
    _properties = new Properties();
    InputStream configurationFileStream = null;
    try {
      configurationFileStream = ConfigUtils.getConfigStream(BUNDLE_NAME, CONFIGURATION_FILE);
      _properties.load(configurationFileStream);
    } catch (final IOException ex) {
      throw new IOException("Could not read configuration property file " + CONFIGURATION_FILE + ": "
        + ex.toString());
    } finally {
      IOUtils.closeQuietly(configurationFileStream);
    }
  }

  /**
   * Makes sure that a connection to the LDAP server exists.
   * 
   * @throws SecurityException
   *           if any connection error occurs
   */
  private void ensureConnection() throws SecurityException {
    try {
      if (_ldapConnection == null || !_ldapConnection.isConnected() || !_ldapConnection.isConnectionAlive()) {
        _ldapConnection = new LDAPConnection();
        // connect to the server
        _ldapConnection.connect(_properties.getProperty(PROPERTY_LDAP_HOST), Integer.parseInt(_properties
          .getProperty(PROPERTY_LDAP_PORT, Integer.toString(LDAPConnection.DEFAULT_PORT))));
        // bind to the server
        _ldapConnection.bind(LDAPConnection.LDAP_V3, _properties.getProperty(PROPERTY_LDAP_LOGIN), _properties
          .getProperty(PROPERTY_LDAP_PASSWORD).getBytes("UTF8"));
      }
    } catch (Exception e) {
      final String msg = "Error ensuring LDAP connection";
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      throw new SecurityException(msg, e);
    }
  }
}

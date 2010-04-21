/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.security.test;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.smila.security.SecurityException;
import org.eclipse.smila.security.SecurityResolver;
import org.eclipse.smila.security.ldap.LDAPSecurityResolver;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * The Class TestSecurityResolver.
 */
public class TestSecurityResolver extends DeclarativeServiceTestCase {

  /** the SecurityResolver. */
  private SecurityResolver _securityResolver;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    _securityResolver = getService(SecurityResolver.class);
    assertNotNull(_securityResolver);
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _securityResolver = null;
  }

  /**
   * Test getProperties.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testResolvePrincipal() throws Exception {
    String expectedDN = "CN=stuc07,CN=Users,DC=empolis,DC=local";
    String name = "stuc07";
    String dn = _securityResolver.resolvePrincipal(name);
    assertNotNull(expectedDN, dn);
    
    expectedDN = "CN=Webmail,CN=Users,DC=empolis,DC=local";
    name = "Webmail";
    dn = _securityResolver.resolvePrincipal(name);
    assertNotNull(expectedDN, dn);
    
    // test exceptions
    final String nonExistingName = "dummy";
    try {
      _securityResolver.resolvePrincipal(nonExistingName);
      fail("expected SecurityException");
    } catch (SecurityException e) {
      assertNotNull(e);
      assertEquals("error resolving name " + nonExistingName, e.getMessage());
    }
  }

  /**
   * Test getProperties.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testGetProperties() throws Exception {
    final int expectedGroupCount = 2;
    final String principal = "CN=stuc07,CN=Users,DC=empolis,DC=local";
    final Map<String, Collection<String>> properties = _securityResolver.getProperties(principal);
    assertNotNull(properties);
    assertTrue(properties.containsKey(LDAPSecurityResolver.LDAP_ATTRIBUTE_MEMBER_OF));
    final Collection<String> groups = properties.get(LDAPSecurityResolver.LDAP_ATTRIBUTE_MEMBER_OF);
    assertNotNull(groups);
    assertEquals(expectedGroupCount, groups.size());

    // test exceptions
    final String nonExistingPrincipal = "dummy";
    try {
      _securityResolver.getProperties(nonExistingPrincipal);
      fail("expected SecurityException");
    } catch (SecurityException e) {
      assertNotNull(e);
      assertEquals("error getting properties of principal " + nonExistingPrincipal, e.getMessage());
    }
  }

  /**
   * Test resolveGroupMembers.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testResolveGroupMembers() throws Exception {
    final int expectedMemberCount = 149;
    final String group = "CN=Webmail,CN=Users,DC=empolis,DC=local";
    final Set<String> groupMembers = _securityResolver.resolveGroupMembers(group);
    assertNotNull(groupMembers);
    assertEquals(expectedMemberCount, groupMembers.size());

    // test exceptions
    final String user = "CN=stuc07,CN=Users,DC=empolis,DC=local";
    try {
      _securityResolver.resolveGroupMembers(user);
      fail("expected SecurityException");
    } catch (SecurityException e) {
      assertNotNull(e);
      assertEquals("group " + user + " is not a group", e.getMessage());
    }

    final String nonExistingGroup = "dummy";
    try {
      _securityResolver.resolveGroupMembers(nonExistingGroup);
      fail("expected SecurityException");
    } catch (SecurityException e) {
      assertNotNull(e);
      assertEquals("error resolving members for group " + nonExistingGroup, e.getMessage());
    }
  }

  /**
   * Test resolveMembership.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testResolveMembership() throws Exception {
    final int expectedGroupCount = 2;

    final String user = "CN=stuc07,CN=Users,DC=empolis,DC=local";
    Set<String> groups = _securityResolver.resolveMembership(user);
    assertNotNull(groups);
    assertEquals(expectedGroupCount, groups.size());

    final String group = "CN=Webmail,CN=Users,DC=empolis,DC=local";
    groups = _securityResolver.resolveMembership(group);
    assertNotNull(groups);
    assertTrue(groups.isEmpty());

    // test exceptions
    final String nonExistingPrincipal = "dummy";
    try {
      _securityResolver.resolveMembership(nonExistingPrincipal);
      fail("expected SecurityException");
    } catch (SecurityException e) {
      assertNotNull(e);
      assertEquals("error resolving membership of principal " + nonExistingPrincipal, e.getMessage());
    }
  }

  /**
   * Test isGroup.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testIsGroup() throws Exception {
    final String user = "CN=stuc07,CN=Users,DC=empolis,DC=local";
    assertFalse(_securityResolver.isGroup(user));

    final String group = "CN=Webmail,CN=Users,DC=empolis,DC=local";
    assertTrue(_securityResolver.isGroup(group));

    final String nonExistingGroup = "dummy";
    try {
      _securityResolver.isGroup(nonExistingGroup);
      fail("expected SecurityException");
    } catch (SecurityException e) {
      assertNotNull(e);
      assertEquals("error checking if principal " + nonExistingGroup + " is a group", e.getMessage());
    }
  }

  /**
   * Test Exceptions handling.
   * 
   * @throws Exception
   *           if any unecpected exception occurs
   */
  public void testExceptions() throws Exception {
    try {
      _securityResolver.getProperties(null);
      fail("expected SecurityException");
    } catch (SecurityException e) {
      assertNotNull(e);
      assertEquals("parameter principal is null", e.getMessage());
    }

    try {
      _securityResolver.resolveGroupMembers(null);
      fail("expected SecurityException");
    } catch (SecurityException e) {
      assertNotNull(e);
      assertEquals("parameter group is null", e.getMessage());
    }

    try {
      _securityResolver.resolveMembership(null);
      fail("expected SecurityException");
    } catch (SecurityException e) {
      assertNotNull(e);
      assertEquals("parameter principal is null", e.getMessage());
    }

    try {
      _securityResolver.isGroup(null);
      fail("expected SecurityException");
    } catch (SecurityException e) {
      assertNotNull(e);
      assertEquals("parameter principal is null", e.getMessage());
    }
  }
}

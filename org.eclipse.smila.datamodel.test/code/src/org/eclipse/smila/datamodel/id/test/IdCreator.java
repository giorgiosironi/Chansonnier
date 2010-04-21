/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.datamodel.id.test;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.id.IdHandlingException;
import org.eclipse.smila.datamodel.id.impl.DefaultIdFactoryImpl;

/**
 * utility class to create Id objects for tests.
 * 
 * @author jschumacher
 * 
 */
public final class IdCreator {
  /**
   * id factory used to create Ids.
   */
  public static final IdFactory FACTORY = new DefaultIdFactoryImpl();

  /**
   * utility class, do not create instances.
   */
  private IdCreator() {
  }

  /**
   * @return id with simple key and no elements/fragments
   */
  public static Id createSourceObjectIdSimpleKey() {
    return FACTORY.createId("source", "keyvalue");
  }

  /**
   * Creates the source object id simple key.
   * 
   * @param source
   *          the source
   * @param keyvalue
   *          the keyvalue
   * 
   * @return the id
   */
  public static Id createSourceObjectIdSimpleKey(final String source, final String keyvalue) {
    return FACTORY.createId(source, keyvalue);
  }

  /**
   * @return id with simple named key and no elements/fragments
   */
  public static Id createSourceObjectIdSimpleNamedKey() {
    return FACTORY.createId("source", "keyname", "keyvalue");
  }

  /**
   * @return id with composite key and no elements/fragments
   */
  public static Id createSourceObjectIdCompositeKey() {
    final Map<String, String> keyvalues = new HashMap<String, String>();
    keyvalues.put("name1", "value1");
    keyvalues.put("name2", "value2");
    keyvalues.put("name3", "value3");
    return FACTORY.createId("source", keyvalues);
  }

  /**
   * @return id with simple key, 1 elements, 0 fragments
   * @throws IdHandlingException
   *           error in Id creation (should not happen)
   */
  public static Id createElement1Id() throws IdHandlingException {
    final Id sourceId = createSourceObjectIdSimpleKey();
    return sourceId.createElementId("elementkey1");
  }

  /**
   * @return id with simple key, 2 elements, 0 fragments
   * @throws IdHandlingException
   *           error in Id creation (should not happen)
   */
  public static Id createElement2Id() throws IdHandlingException {
    final Id elementId = createElement1Id();
    return elementId.createElementId("elementkey2");
  }

  /**
   * @return id with simple key, 0 elements, 1 fragments
   * @throws IdHandlingException
   *           error in Id creation (should not happen)
   */
  public static Id createFragment1Id() throws IdHandlingException {
    final Id sourceId = createSourceObjectIdSimpleKey();
    return sourceId.createFragmentId("fragmentname1");
  }

  /**
   * @return id with simple key, 0 elements, 2 fragments
   * @throws IdHandlingException
   *           error in Id creation (should not happen)
   */
  public static Id createFragment2Id() throws IdHandlingException {
    final Id fragmentId = createFragment1Id();
    return fragmentId.createFragmentId("fragmentname2");
  }

  /**
   * @return id with simple key, 1 elements, 1 fragments
   * @throws IdHandlingException
   *           error in Id creation (should not happen)
   */
  public static Id createElement1Fragment1Id() throws IdHandlingException {
    final Id elementId = createElement1Id();
    return elementId.createFragmentId("fragmentname1");
  }

  /**
   * @return id with simple key, 2 elements, 1 fragments
   * @throws IdHandlingException
   *           error in Id creation (should not happen)
   */
  public static Id createElement2Fragment1Id() throws IdHandlingException {
    final Id elementId = createElement2Id();
    return elementId.createFragmentId("fragmentname1");
  }

  /**
   * @return id with simple key, 1 elements, 2 fragments
   * @throws IdHandlingException
   *           error in Id creation (should not happen)
   */
  public static Id createElement1Fragment2Id() throws IdHandlingException {
    final Id elementId = createElement1Fragment1Id();
    return elementId.createFragmentId("fragmentname2");
  }

  /**
   * @return id with simple key, 2 elements, 2 fragments
   * @throws IdHandlingException
   *           error in Id creation (should not happen)
   */
  public static Id createElement2Fragment2Id() throws IdHandlingException {
    final Id elementId = createElement2Fragment1Id();
    return elementId.createFragmentId("fragmentname2");
  }
}

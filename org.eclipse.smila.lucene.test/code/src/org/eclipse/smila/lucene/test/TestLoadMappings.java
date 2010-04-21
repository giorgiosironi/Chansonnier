/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.  
 * All rights reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.lucene.test;

import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.smila.lucene.MappingsLoader;
import org.eclipse.smila.lucene.config.Attachment;
import org.eclipse.smila.lucene.config.Attachments;
import org.eclipse.smila.lucene.config.Attribute;
import org.eclipse.smila.lucene.config.Attributes;
import org.eclipse.smila.lucene.config.Mapping;
import org.eclipse.smila.lucene.config.Mappings;

/**
 * The Class TestLoadMappings.
 */
public class TestLoadMappings extends TestCase {

  /**
   * Constant for the mappings file.
   */
  private static final String MAPPINGS_FILE_NAME = "Mappings.xml";

  /**
   * Constant for number 3.
   */
  private static final int NUMBER_3 = 3;

  /**
   * Constant for number 4.
   */
  private static final int NUMBER_4 = 4;

  /**
   * Constant for number 5.
   */
  private static final int NUMBER_5 = 5;

  /**
   * Constant for number 6.
   */
  private static final int NUMBER_6 = 6;
  
  /**
   * Constant for number 10.
   */
  private static final int NUMBER_10 = 10;

  /**
   * Constant for number 11.
   */
  private static final int NUMBER_11 = 11;

  /**
   * Constant for number 12.
   */
  private static final int NUMBER_12 = 12;

  /**
   * Constant for number 13.
   */
  private static final int NUMBER_13 = 13;

  /**
   * Constant for number 14.
   */
  private static final int NUMBER_14 = 14;

  /**
   * Constant for number 15.
   */
  private static final int NUMBER_15 = 15;

  /**
   * Constant for number 16.
   */
  private static final int NUMBER_16 = 16;
  
  /**
   * Test loadMappings().
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testLoadMapping() throws Exception {
    final Mappings mappings = (Mappings) MappingsLoader.loadMappings(MAPPINGS_FILE_NAME);
    assertNotNull(mappings);
    final List<Mapping> mappingList = mappings.getMapping();
    assertNotNull(mappingList);
    assertEquals(2, mappingList.size());

    // check 1st mapping
    Mapping mapping = mappingList.get(0);
    checkMapping1(mapping);

    // check 2nd mapping
    mapping = mappingList.get(1);
    checkMapping2(mapping);
  }

  /**
   * Test loadMappingsMap().
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testLoadMappingsMap() throws Exception {
    final HashMap<String, HashMap<String, HashMap<String, Integer>>> mappingsMap =
      MappingsLoader.loadMappingsMap(MAPPINGS_FILE_NAME);
    assertNotNull(mappingsMap);
    assertEquals(2, mappingsMap.size());
    assertTrue(mappingsMap.containsKey("test_index"));
    assertTrue(mappingsMap.containsKey("test_index2"));

    HashMap<String, HashMap<String, Integer>> indexMap = mappingsMap.get("test_index");
    assertNotNull(indexMap);
    assertEquals(2, indexMap.size());
    assertTrue(indexMap.containsKey(MappingsLoader.ATTRIBUTES));
    assertTrue(indexMap.containsKey(MappingsLoader.ATTACHMENTS));

    HashMap<String, Integer> attributeMap = indexMap.get(MappingsLoader.ATTRIBUTES);
    assertNotNull(attributeMap);
    assertEquals(NUMBER_6, attributeMap.size());
    assertEquals(1, attributeMap.get("Filename").intValue());
    assertEquals(2, attributeMap.get("Path").intValue());
    assertEquals(NUMBER_3, attributeMap.get("Date").intValue());
    assertEquals(NUMBER_4, attributeMap.get("Url").intValue());
    assertEquals(NUMBER_5, attributeMap.get("Title").intValue());
    assertEquals(NUMBER_6, attributeMap.get("XMLID").intValue());    

    HashMap<String, Integer> attachmentMap = indexMap.get(MappingsLoader.ATTACHMENTS);
    assertNotNull(attachmentMap);
    assertEquals(1, attachmentMap.size());
    assertEquals(0, attachmentMap.get("Content").intValue());

    /* ******************************************************************************** */

    indexMap = mappingsMap.get("test_index2");
    assertNotNull(indexMap);
    assertEquals(2, indexMap.size());
    assertTrue(indexMap.containsKey(MappingsLoader.ATTRIBUTES));
    assertTrue(indexMap.containsKey(MappingsLoader.ATTACHMENTS));

    attributeMap = indexMap.get(MappingsLoader.ATTRIBUTES);
    assertNotNull(attributeMap);
    assertEquals(NUMBER_6, attributeMap.size());
    assertEquals(NUMBER_11, attributeMap.get("Filename").intValue());
    assertEquals(NUMBER_12, attributeMap.get("Path").intValue());
    assertEquals(NUMBER_13, attributeMap.get("Date").intValue());
    assertEquals(NUMBER_14, attributeMap.get("Url").intValue());
    assertEquals(NUMBER_15, attributeMap.get("Title").intValue());
    assertEquals(NUMBER_16, attributeMap.get("XMLID").intValue());    

    attachmentMap = indexMap.get(MappingsLoader.ATTACHMENTS);
    assertNotNull(attachmentMap);
    assertEquals(1, attachmentMap.size());
    assertEquals(NUMBER_10, attachmentMap.get("Content").intValue());
  }

  /**
   * Check the 1st mapping.
   * 
   * @param mapping
   *          the Mapping
   * @throws Exception
   *           if any error occurs
   */
  private void checkMapping1(Mapping mapping) throws Exception {
    assertNotNull(mapping);
    assertEquals("test_index", mapping.getIndexName());

    // check attributes
    final Attributes attributesType = mapping.getAttributes();
    assertNotNull(attributesType);
    final List<Attribute> attributesList = attributesType.getAttribute();
    assertNotNull(attributesList);
    assertEquals(NUMBER_6, attributesList.size());

    final Attribute filename = attributesList.get(0);
    assertNotNull(filename);
    assertEquals("Filename", filename.getName());
    assertEquals(1, filename.getFieldNo());

    final Attribute path = attributesList.get(1);
    assertNotNull(path);
    assertEquals("Path", path.getName());
    assertEquals(2, path.getFieldNo());

    final Attribute date = attributesList.get(2);
    assertNotNull(date);
    assertEquals("Date", date.getName());
    assertEquals(NUMBER_3, date.getFieldNo());

    final Attribute url = attributesList.get(NUMBER_3);
    assertNotNull(url);
    assertEquals("Url", url.getName());
    assertEquals(NUMBER_4, url.getFieldNo());

    final Attribute title = attributesList.get(NUMBER_4);
    assertNotNull(title);
    assertEquals("Title", title.getName());
    assertEquals(NUMBER_5, title.getFieldNo());

    // check attachments
    final Attachments attachmentsType = mapping.getAttachments();
    assertNotNull(attachmentsType);
    final List<Attachment> attachmentsList = attachmentsType.getAttachment();
    assertNotNull(attachmentsList);
    assertEquals(1, attachmentsList.size());

    final Attachment content = attachmentsList.get(0);
    assertNotNull(content);
    assertEquals("Content", content.getName());
    assertEquals(0, content.getFieldNo());
  }

  /**
   * Check the 2nd mapping.
   * 
   * @param mapping
   *          the Mapping
   * @throws Exception
   *           if any error occurs
   */
  private void checkMapping2(Mapping mapping) throws Exception {
    assertNotNull(mapping);
    assertEquals("test_index2", mapping.getIndexName());

    // check attributes
    final Attributes attributesType = mapping.getAttributes();
    assertNotNull(attributesType);
    final List<Attribute> attributesList = attributesType.getAttribute();
    assertNotNull(attributesList);
    assertEquals(NUMBER_6, attributesList.size());

    final Attribute filename = attributesList.get(0);
    assertNotNull(filename);
    assertEquals("Filename", filename.getName());
    assertEquals(NUMBER_11, filename.getFieldNo());

    final Attribute path = attributesList.get(1);
    assertNotNull(path);
    assertEquals("Path", path.getName());
    assertEquals(NUMBER_12, path.getFieldNo());

    final Attribute date = attributesList.get(2);
    assertNotNull(date);
    assertEquals("Date", date.getName());
    assertEquals(NUMBER_13, date.getFieldNo());

    final Attribute url = attributesList.get(NUMBER_3);
    assertNotNull(url);
    assertEquals("Url", url.getName());
    assertEquals(NUMBER_14, url.getFieldNo());

    final Attribute title = attributesList.get(NUMBER_4);
    assertNotNull(title);
    assertEquals("Title", title.getName());
    assertEquals(NUMBER_15, title.getFieldNo());

    // check attachments
    final Attachments attachmentsType = mapping.getAttachments();
    assertNotNull(attachmentsType);
    final List<Attachment> attachmentsList = attachmentsType.getAttachment();
    assertNotNull(attachmentsList);
    assertEquals(1, attachmentsList.size());

    final Attachment content = attachmentsList.get(0);
    assertNotNull(content);
    assertEquals("Content", content.getName());
    assertEquals(NUMBER_10, content.getFieldNo());
  }
}

/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.smila.connectivity.framework.crawler.web.IndexDocument;

/**
 * The Class TestIndexDocument.
 * 
 * @author Alexander Eliseyev
 */
public class TestIndexDocument extends TestCase {

  /**
   * Test index document.
   * 
   * @throws Exception
   *           the exception
   */
  public void testIndexDocument() throws Exception {
    final List<String> responseHeaders = new ArrayList<String>();
    responseHeaders.add("responseHeader");
    
    final List<String> htmlMetaData = new ArrayList<String>();
    responseHeaders.add("htmlMetaData");
    
    final List<String> metaDataWithResponseHeaderFallBack = new ArrayList<String>();
    metaDataWithResponseHeaderFallBack.add("metaDataWithResponseHeaderFallBack");

    final IndexDocument indexDocument = new IndexDocument(null, null, null, null, null, null);
    
    indexDocument.setContent("testContent".getBytes());
    indexDocument.setHtmlMetaData(htmlMetaData);
    indexDocument.setMetaDataWithResponseHeaderFallBack(metaDataWithResponseHeaderFallBack);
    indexDocument.setResponseHeaders(responseHeaders);
    indexDocument.setTitle("testTitle");
    indexDocument.setUrl("http://www.eclipse.org");
        
    assertEquals(htmlMetaData, indexDocument.getHtmlMetaData());
    assertEquals(responseHeaders, indexDocument.getResponseHeaders());
    assertEquals(metaDataWithResponseHeaderFallBack, indexDocument.getMetaDataWithResponseHeaderFallBack());
    assertEquals("http://www.eclipse.org", indexDocument.getUrl());
    assertEquals("testTitle", indexDocument.getTitle());    
  }

}

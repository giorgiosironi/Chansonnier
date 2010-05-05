<?xml version="1.0"?>
<!--
/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
-->
<t:stylesheet xmlns:t="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:java="http://xml.apache.org/xslt/java"
  exclude-result-prefixes="java"
>

  <t:template match="doc">
    <out>
      <t:apply-templates select="text()"/>
    </out>
  </t:template>
  
  <t:template match="text()">
      <t:value-of select="java:org.apache.xalan.test.XsltTools.splitLongWords(., 3)"/>
  </t:template>
  
</t:stylesheet>

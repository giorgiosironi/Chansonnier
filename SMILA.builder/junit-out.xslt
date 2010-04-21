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
<t:transform version="1.0" xmlns:t="http://www.w3.org/1999/XSL/Transform">
  
  <t:output method="text" encoding="ISO-8859-1"/>
  
  <t:template match="/">
    <t:apply-templates select="testsuites"/>
  </t:template>

  <t:template match="testsuites">
      <t:variable name="failures" select="sum(testsuite/@failures)"/>
      <t:variable name="errors" select="sum(testsuite/@errors)"/>
      <t:if test="$failures > 0 or $errors >0">
        <t:apply-templates select="testsuite[@failures >0 or @errors >0]"/>
      </t:if>
  </t:template>
  
  <t:template match="testsuite[@failures>0 or @errors >0]">
<t:value-of select="'&#13;&#10;'"/>******************** Testsuite <t:value-of select="@package"/> FAILED!<!---->
<t:apply-templates select="testcase[error|failure] | error | failure"/>  
  </t:template>
  
  <t:template match="testcase[error|failure]">
<t:value-of select="'&#13;&#10;'"/>* Testcase name:<t:value-of select="@name"/><!---->
<t:value-of select="'&#13;&#10;'"/>* Testcase class:<t:value-of select="@classname"/><!---->
<t:apply-templates select="error|failure"/>
  </t:template>
  
  <t:template match="error|failure">
<t:value-of select="'&#13;&#10;'"/>* Message:<t:value-of select="@message"/>
<t:value-of select="'&#13;&#10;'"/>* Type:<t:value-of select="@type"/>
    <!--<t:value-of select="'&#13;&#10;'"/><t:copy-of select="text()"/>-->
  </t:template>
  
</t:transform>
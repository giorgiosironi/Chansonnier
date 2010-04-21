<?xml version="1.0" encoding="UTF-8"?>
<!--
/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Georg Schmidt (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:as="http://www.anyfinder.de/AdvancedSearch" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

  <xsl:template match="/">
    <xsl:apply-templates select="//as:QueryExpression"/>
  </xsl:template>

  <xsl:template match="//as:QueryExpression">
    <xsl:element name="AdvancedSearchTemplateFields" namespace="http://www.anyfinder.de/RapidDeployer/AdvancedSearchTemplateFields">
      <xsl:attribute name="IndexName">
        <xsl:value-of select="@IndexName"/>
      </xsl:attribute>
      <xsl:apply-templates select=".//as:Field[@xsi:type='TextTemplateField' or @xsi:type='NumberTemplateField' or @xsi:Type='DateTemplateField']"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="//as:Field[@xsi:type='TextTemplateField' or @xsi:type='NumberTemplateField' or @xsi:Type='DateTemplateField']">
    <xsl:element name="TemplateField" namespace="http://www.anyfinder.de/RapidDeployer/AdvancedSearchTemplateFields">
      <xsl:copy-of select="@FieldNo"/>
      <xsl:copy-of select="@SourceFieldNo"/>
    </xsl:element>
  </xsl:template>

</xsl:stylesheet>

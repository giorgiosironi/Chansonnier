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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:is="http://www.anyfinder.de/IndexStructure" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

  <xsl:template match="*"/>

  <xsl:template match="/">
    <xsl:element name="IndexStructures" namespace="http://www.anyfinder.de/RapidDeployer/IndexStructure">
      <xsl:apply-templates select="//is:IndexStructure"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="//is:IndexStructure">
    <xsl:element name="IndexStructure" namespace="http://www.anyfinder.de/RapidDeployer/IndexStructure">
      <xsl:attribute name="Name"><xsl:value-of select="@Name"/></xsl:attribute>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="is:IndexField">
    <xsl:element name="IndexField" namespace="http://www.anyfinder.de/RapidDeployer/IndexStructure">
      <xsl:attribute name="FieldNo"><xsl:value-of select="@FieldNo"/></xsl:attribute>
      <xsl:attribute name="Name"><xsl:value-of select="@Name"/></xsl:attribute>
      <xsl:attribute name="Type"><xsl:value-of select="@Type"/></xsl:attribute>
    </xsl:element>
  </xsl:template>
  
</xsl:stylesheet>

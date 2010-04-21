<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


<xsl:output
     method="xml"
     encoding="utf-8"/>

<xsl:template match="/">
<newRoot>
  <xsl:apply-templates select="//child"/>
</newRoot>
</xsl:template>

<xsl:template match="child">
  <newChild><xsl:value-of select="."/></newChild>
</xsl:template>

</xsl:stylesheet>


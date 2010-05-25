<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.lib.Redirect"
    extension-element-prefixes="redirect">
<xsl:output method="html" indent="yes" encoding="US-ASCII"/>
<xsl:decimal-format decimal-separator="." grouping-separator="," />
<!--
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->
<!--

    Stylesheet to transform an XML file generated by the Ant MAudit task into
    a set of JavaDoc-like HTML page to make pages more convenient to be browsed.

    It use the Xalan redirect extension to write to multiple output files.

    @author Stephane Bailliez <a href="mailto:sbailliez@apache.org"/>
-->

<xsl:param name="output.dir" select="'.'"/>


<xsl:template match="classes">
    <!-- create the index.html -->
    <redirect:write file="{$output.dir}/index.html">
        <xsl:call-template name="index.html"/>
    </redirect:write>

    <!-- create the stylesheet.css -->
    <redirect:write file="{$output.dir}/stylesheet.css">
        <xsl:call-template name="stylesheet.css"/>
    </redirect:write>

    <!-- create the overview-packages.html at the root -->
    <redirect:write file="{$output.dir}/overview-summary.html">
        <xsl:apply-templates select="." mode="overview.packages"/>
    </redirect:write>

    <!-- create the all-packages.html at the root -->
    <redirect:write file="{$output.dir}/overview-frame.html">
        <xsl:apply-templates select="." mode="all.packages"/>
    </redirect:write>

    <!-- create the all-classes.html at the root -->
    <redirect:write file="{$output.dir}/allclasses-frame.html">
        <xsl:apply-templates select="." mode="all.classes"/>
    </redirect:write>

    <!-- process all packages -->
    <xsl:for-each select="./class[not(./@package = preceding-sibling::class/@package)]">
        <xsl:call-template name="package">
            <xsl:with-param name="name" select="@package"/>
        </xsl:call-template>
    </xsl:for-each>
</xsl:template>


<xsl:template name="package">
    <xsl:param name="name"/>
    <xsl:variable name="package.dir">
        <xsl:if test="not($name = '')"><xsl:value-of select="translate($name,'.','/')"/></xsl:if>
        <xsl:if test="$name = ''">.</xsl:if>
    </xsl:variable>
    <!--Processing package <xsl:value-of select="@name"/> in <xsl:value-of select="$output.dir"/> -->
    <!-- create a classes-list.html in the package directory -->
    <redirect:write file="{$output.dir}/{$package.dir}/package-frame.html">
        <xsl:call-template name="classes.list">
            <xsl:with-param name="name" select="$name"/>
        </xsl:call-template>
    </redirect:write>

    <!-- create a package-summary.html in the package directory -->
    <redirect:write file="{$output.dir}/{$package.dir}/package-summary.html">
        <xsl:call-template name="package.summary">
            <xsl:with-param name="name" select="$name"/>
        </xsl:call-template>
    </redirect:write>

    <!-- for each class, creates a @name.html -->
    <!-- @bug there will be a problem with inner classes having the same name, it will be overwritten -->
    <xsl:for-each select="/classes/class[@package = $name]">
        <redirect:write file="{$output.dir}/{$package.dir}/{@name}.html">
            <xsl:apply-templates select="." mode="class.details"/>
        </redirect:write>
    </xsl:for-each>
</xsl:template>

<xsl:template name="index.html">
<HTML>
    <HEAD><TITLE>Audit Results.</TITLE></HEAD>
    <FRAMESET cols="20%,80%">
        <FRAMESET rows="30%,70%">
            <FRAME src="overview-frame.html" name="packageListFrame"/>
            <FRAME src="allclasses-frame.html" name="classListFrame"/>
        </FRAMESET>
        <FRAME src="overview-summary.html" name="classFrame"/>
    </FRAMESET>
    <noframes>
        <H2>Frame Alert</H2>
        <P>
        This document is designed to be viewed using the frames feature. If you see this message, you are using a non-frame-capable web client.
        </P>
    </noframes>
</HTML>
</xsl:template>


<!-- this is the stylesheet css to use for nearly everything -->
<xsl:template name="stylesheet.css">
    .bannercell {
      border: 0px;
      padding: 0px;
    }
    body {
      margin-left: 10;
      margin-right: 10;
      font:normal 80% arial,helvetica,sanserif;
      background-color:#FFFFFF;
      color:#000000;
    }
    .a td {
      background: #efefef;
    }
    .b td {
      background: #fff;
    }
    th, td {
      text-align: left;
      vertical-align: top;
    }
    th {
      font-weight:bold;
      background: #ccc;
      color: black;
    }
    table, th, td {
      font-size:100%;
      border: none
    }
    table.log tr td, tr th {

    }
    h2 {
      font-weight:bold;
      font-size:140%;
      margin-bottom: 5;
    }
    h3 {
      font-size:100%;
      font-weight:bold;
      background: #525D76;
      color: white;
      text-decoration: none;
      padding: 5px;
      margin-right: 2px;
      margin-left: 2px;
      margin-bottom: 0;
    }
</xsl:template>


<!-- print the violations of the class -->
<xsl:template match="class" mode="class.details">
    <xsl:variable name="package.name" select="@package"/>
    <HTML>
        <HEAD>
            <xsl:call-template name="create.stylesheet.link">
                <xsl:with-param name="package.name" select="$package.name"/>
            </xsl:call-template>
        </HEAD>
        <BODY>
            <xsl:call-template name="pageHeader"/>
            <H3>Class <xsl:if test="not($package.name = '')"><xsl:value-of select="$package.name"/>.</xsl:if><xsl:value-of select="@name"/></H3>

            <table class="log" border="0" cellpadding="5" cellspacing="2" width="100%">
                <xsl:call-template name="class.audit.header"/>
                <xsl:apply-templates select="." mode="print.audit"/>
            </table>

            <H3>Violations</H3>
            <table class="log" border="0" cellpadding="5" cellspacing="2" width="100%">
                <xsl:call-template name="violation.audit.header"/>
                <xsl:apply-templates select="./violation" mode="print.audit">
                    <xsl:sort data-type="number" select="@line"/>
                </xsl:apply-templates>
            </table>
            <xsl:call-template name="pageFooter"/>
        </BODY>
    </HTML>
</xsl:template>


<!-- list of classes in a package -->
<xsl:template name="classes.list">
    <xsl:param name="name"/>
    <HTML>
        <HEAD>
            <xsl:call-template name="create.stylesheet.link">
                <xsl:with-param name="package.name" select="$name"/>
            </xsl:call-template>
        </HEAD>
        <BODY>
            <table width="100%">
                <tr>
                    <td nowrap="nowrap">
                        <H2><a href="package-summary.html" target="classFrame"><xsl:value-of select="$name"/></a></H2>
                    </td>
                </tr>
            </table>

            <h2>Classes</h2>
            <TABLE WIDTH="100%">
                <xsl:apply-templates select="/classes/class[./@package = $name]" mode="classes.list">
                    <xsl:sort select="@name"/>
                </xsl:apply-templates>
            </TABLE>
        </BODY>
    </HTML>
</xsl:template>
<!-- the class to list -->
<xsl:template match="class" mode="classes.list">
    <tr>
        <td nowrap="nowrap">
            <!-- @bug naming to fix for inner classes -->
            <a href="{@name}.html" target="classFrame"><xsl:value-of select="@name"/></a>
        </td>
    </tr>
</xsl:template>


<!--
    Creates an all-classes.html file that contains a link to all package-summary.html
    on each class.
-->
<xsl:template match="classes" mode="all.classes">
    <html>
        <head>
            <xsl:call-template name="create.stylesheet.link">
                <xsl:with-param name="package.name"/>
            </xsl:call-template>
        </head>
        <body>
            <h2>Classes</h2>
            <table width="100%">
                <xsl:apply-templates select=".//class" mode="all.classes">
                    <xsl:sort select="@name"/>
                </xsl:apply-templates>
            </table>
        </body>
    </html>
</xsl:template>

<xsl:template match="class" mode="all.classes">
    <!-- (ancestor::package)[last()] is buggy in MSXML3 ? -->
    <xsl:variable name="package.name" select="@package"/>
    <tr>
        <td nowrap="nowrap">
            <a target="classFrame">
                <xsl:attribute name="href">
                    <xsl:if test="not($package.name='')">
                        <xsl:value-of select="translate($package.name,'.','/')"/><xsl:text>/</xsl:text>
                    </xsl:if><xsl:value-of select="@name"/><xsl:text>.html</xsl:text>
                </xsl:attribute>
                <xsl:value-of select="@name"/>
            </a>
        </td>
    </tr>
</xsl:template>


<!--
    Creates an html file that contains a link to all package-summary.html files on
    each package existing on testsuites.
    @bug there will be a problem here, I don't know yet how to handle unnamed package :(
-->
<xsl:template match="classes" mode="all.packages">
    <html>
        <head>
            <xsl:call-template name="create.stylesheet.link">
                <xsl:with-param name="package.name"/>
            </xsl:call-template>
        </head>
        <body>
            <h2><a href="overview-summary.html" target="classFrame">Home</a></h2>
            <h2>Packages</h2>
                <table width="100%">
                    <xsl:apply-templates select="class[not(./@package = preceding-sibling::class/@package)]" mode="all.packages">
                        <xsl:sort select="@package" order="ascending"/>
                    </xsl:apply-templates>
                </table>
        </body>
    </html>
</xsl:template>

<xsl:template match="class" mode="all.packages">
    <tr>
        <td nowrap="nowrap">
            <a href="{translate(@package,'.','/')}/package-summary.html" target="classFrame">
                <xsl:value-of select="@package"/>
            </a>
        </td>
    </tr>
</xsl:template>


<xsl:template match="classes" mode="overview.packages">
    <html>
        <head>
            <xsl:call-template name="create.stylesheet.link">
                <xsl:with-param name="package.name"/>
            </xsl:call-template>
        </head>
        <body onload="open('allclasses-frame.html','classListFrame')">
        <xsl:call-template name="pageHeader"/>
        <h3>Summary</h3>
        <table class="log" border="0" cellpadding="5" cellspacing="2" width="100%">
        <tr>
            <th>Audited classes</th>
            <th>Reported classes</th>
            <th>Violations</th>
        </tr>
        <tr class="a">
            <td><xsl:value-of select="@audited"/></td>
            <td><xsl:value-of select="@reported"/></td>
            <td><xsl:value-of select="@violations"/></td>
        </tr>
        </table>
        <table border="0" width="100%">
        <tr>
        <td style="text-align: justify;">
        Note: Rules checked have originated from style guidelines suggested by the language designers,
        experience from the Java development community and insite experience. Violations are generally
        reported with a reference to the <a href="http://java.sun.com/docs/books/jls/second_edition/html/jTOC.doc.html">Java Language Specifications</a> (JLS x.x.x)
        and Metamata Audit rules (x.x).
        Please consult these documents for additional information about violations.
        <p/>
        Rules checked also enforce adherence to <a href="http://java.sun.com/docs/codeconv/html/CodeConvTOC.doc.html">Sun Java coding guidelines</a> in use at Jakarta.
        <p/>
        One should note that these violations do not necessary underline errors but should be used
        as an indication for <i>possible</i> errors. As always, use your best judgment and review
        them carefully, it might save you hours of debugging.
        </td>
        </tr>
        </table>

        <h3>Packages</h3>
        <table class="log" border="0" cellpadding="5" cellspacing="2" width="100%">
            <xsl:call-template name="class.audit.header"/>
            <xsl:for-each select="class[not(./@package = preceding-sibling::class/@package)]">
                <xsl:sort select="@package" order="ascending"/>
                <tr>
          <xsl:call-template name="alternate-row"/>
                    <td><a href="{translate(@package,'.','/')}/package-summary.html"><xsl:value-of select="@package"/></a></td>
                    <td><xsl:value-of select="sum(/classes/class[./@package = current()/@package]/@violations)"/></td>
                </tr>
            </xsl:for-each>
        </table>
        <xsl:call-template name="pageFooter"/>
        </body>
        </html>
</xsl:template>


<xsl:template name="package.summary">
    <xsl:param name="name"/>
    <HTML>
        <HEAD>
            <xsl:call-template name="create.stylesheet.link">
                <xsl:with-param name="package.name" select="$name"/>
            </xsl:call-template>
        </HEAD>
        <BODY>
            <xsl:attribute name="onload">open('package-frame.html','classListFrame')</xsl:attribute>
            <xsl:call-template name="pageHeader"/>
            <h3>Package <xsl:value-of select="$name"/></h3>

            <!--table border="0" cellpadding="5" cellspacing="2" width="100%">
                <xsl:call-template name="class.metrics.header"/>
                <xsl:apply-templates select="." mode="print.metrics"/>
            </table-->

            <xsl:if test="count(/classes/class[./@package = $name]) &gt; 0">
                <H3>Classes</H3>
                <table class="log" border="0" cellpadding="5" cellspacing="2" width="100%">
                    <xsl:call-template name="class.audit.header"/>
                    <xsl:apply-templates select="/classes/class[./@package = $name]" mode="print.audit">
                        <xsl:sort select="@name"/>
                    </xsl:apply-templates>
                </table>
            </xsl:if>
            <xsl:call-template name="pageFooter"/>
        </BODY>
    </HTML>
</xsl:template>


<!--
    transform string like a.b.c to ../../../
    @param path the path to transform into a descending directory path
-->
<xsl:template name="path">
    <xsl:param name="path"/>
    <xsl:if test="contains($path,'.')">
        <xsl:text>../</xsl:text>
        <xsl:call-template name="path">
            <xsl:with-param name="path"><xsl:value-of select="substring-after($path,'.')"/></xsl:with-param>
        </xsl:call-template>
    </xsl:if>
    <xsl:if test="not(contains($path,'.')) and not($path = '')">
        <xsl:text>../</xsl:text>
    </xsl:if>
</xsl:template>


<!-- create the link to the stylesheet based on the package name -->
<xsl:template name="create.stylesheet.link">
    <xsl:param name="package.name"/>
    <LINK REL ="stylesheet" TYPE="text/css" TITLE="Style"><xsl:attribute name="href"><xsl:if test="not($package.name = 'unnamed package')"><xsl:call-template name="path"><xsl:with-param name="path" select="$package.name"/></xsl:call-template></xsl:if>stylesheet.css</xsl:attribute></LINK>
</xsl:template>

<!-- Page HEADER -->
<xsl:template name="pageHeader">

  <!-- jakarta logo -->
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr>
    <td class="bannercell" rowspan="2">
      <a href="http://jakarta.apache.org/">
      <img src="http://jakarta.apache.org/images/jakarta-logo.gif" alt="http://jakarta.apache.org" align="left" border="0"/>
      </a>
    </td>
        <td style="text-align:right"><h2>Source Code Audit</h2></td>
        </tr>
        <tr>
        <td style="text-align:right">Designed for use with <a href='http://www.webgain.com/products/quality_analyzer/'>Webgain QA/Metamata Audit</a> and <a href='http://jakarta.apache.org'>Ant</a>.</td>
        </tr>
  </table>
    <hr size="1"/>
</xsl:template>

<!-- Page HEADER -->
<xsl:template name="pageFooter">
</xsl:template>


<!-- class header -->
<xsl:template name="class.audit.header">
    <tr>
        <th width="80%">Name</th>
        <th>Violations</th>
    </tr>
</xsl:template>

<!-- method header -->
<xsl:template name="violation.audit.header">
    <tr>
        <th>Line</th>
        <th>Message</th>
    </tr>
</xsl:template>


<!-- class information -->
<xsl:template match="class" mode="print.audit">
    <tr>
    <xsl:call-template name="alternate-row"/>
        <td><a href="{@name}.html"><xsl:value-of select="@name"/></a></td>
        <td><xsl:apply-templates select="@violations"/></td>
    </tr>
</xsl:template>

<xsl:template match="violation" mode="print.audit">
    <tr>
    <xsl:call-template name="alternate-row"/>
        <td><xsl:value-of select="@line"/></td>
        <td><xsl:apply-templates select="@message"/></td>
    </tr>
</xsl:template>

<!-- alternated row style -->
<xsl:template name="alternate-row">
<xsl:attribute name="class">
  <xsl:if test="position() mod 2 = 1">a</xsl:if>
  <xsl:if test="position() mod 2 = 0">b</xsl:if>
</xsl:attribute>
</xsl:template>

</xsl:stylesheet>


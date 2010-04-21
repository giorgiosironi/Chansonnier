<?xml version="1.0" encoding="UTF-8"?>
<?altova_samplexml D:\AnyFinder\af-Engine-Caatoosee-SDK\appserv\webapps\AnyFinder\SearchPage.xml?>
<!--
/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Georg Schmidt (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:sr="http://www.anyfinder.de/SearchResult" xmlns:em="http://www.anyfinder.de/ErrorMessage" xmlns:java="http://xml.apache.org/xslt/java">

<!-- ############################ 

There are 3 template examples in this stylesheet. 

1 for a Custom Searchform 
2 for Custom Resultlists (Results in Columns or Rows)  Please specify which template to use in the SearchPage Stylesheets template call   <xsl:template name="SearchResult">.
 
 ############################## -->


<!-- ############ Specifiy Custom Searchform here, call Template in DefaultSearchPage or other used Stylesheet ########### -->
<!-- ############ Templatenames should consist of SearchForm-'Indexname' ########### -->
<xsl:template name="SearchForm-PlaceIndexNameHere">
  <xsl:param name="IndexName" />
  <xsl:param name="State" />
  
    <tr>
    <td width="100"/>
    <td width="483">
      <input type="hidden" name="index">
        <xsl:attribute name="value"><xsl:value-of select="$IndexName"/></xsl:attribute>
      </input>
      <!-- ############ Specifiy minSimilarity default=0 ########### -->
      <input type="hidden" name="minSimilarity" id="minSimilarity" value="100" />
      <!-- ############ Specifiy maxHits default=10 ########### -->
      <input type="hidden" name="maxHits" id="maxHits" value="20" />
      <!-- ############ Specifiy TemplateSelectorName ########### -->
      <!--input type="hidden" name="templateSelectorName" id="templateSelectorName" value=""/-->      
      <!-- ############ Specifiy ResultName ########### -->
      <!--input type="hidden" name="resultName" id="resultName" value=""/-->
      <table cellSpacing="0" cellPadding="0" border="0">
        <tbody>
          <tr>
            <td height="8" colspan="3"/>
          </tr>
          <div id="Field18" title="PublicationDisplay">
          <tr>
            <td class="headline2" width="100">Publikation:</td>
            <td class="headline2">
              <input type="text" name="text18" id="text18">
                <xsl:if test="$State/FieldValue[@FieldNo=18]">
                  <xsl:attribute name="value"><xsl:value-of select="$State/FieldValue[@FieldNo=18]/@Text"/></xsl:attribute>
                </xsl:if>
              </input>
            </td>
            <td class="headline2"/>
          </tr>
          </div>
          <div id="Field1" title="title">
          <tr>
            <td class="headline2" width="100">Dokument Titel:</td>
            <td class="headline2">
              <input type="text" name="text1" id="text1">
                <xsl:if test="$State/FieldValue[@FieldNo=1]">
                  <xsl:attribute name="value"><xsl:value-of select="$State/FieldValue[@FieldNo=1]/@Text"/></xsl:attribute>
                </xsl:if>
              </input>
              <!-- ############ Weight example ############## -->
              <!-- ############ Mind Field No ############## -->
              <xsl:variable name="fieldValue" select="//FieldValue[@FieldNo = '1']" />
              <select id="weight1" name="weight1">
                <!--option value="0" ><xsl:if test="$fieldValue[@Weight = 0]"><xsl:attribute name="selected" /></xsl:if> 0</option-->
                <option value="1" ><xsl:if test="$fieldValue[@Weight = 1]"><xsl:attribute name="selected" /></xsl:if> 1</option>
                <option value="2" ><xsl:if test="$fieldValue[@Weight = 2]"><xsl:attribute name="selected" /></xsl:if> 2</option>
                <option value="3" ><xsl:if test="$fieldValue[@Weight = 3]"><xsl:attribute name="selected" /></xsl:if> 3</option>
              </select>
              <!-- ############ Parameter Descriptor example ############## -->
              <!-- ############ Mind Field No, Specifiy value ############## -->
              <!-- input type="hidden" name="parameterDescriptor1" value="HollaDieWaldfee" /-->
              <!-- ############ FieldTemplate example ############## -->
              <!-- ############ Mind Field No, Specifiy value ############## -->
              <!--input type="hidden" name="fieldTemplate1" value="FieldMeUp" /-->
            </td>
            <td class="headline2"/>
          </tr>
          </div>
          <div id="Field15" title="Document">
          <tr>
            <td class="headline2" width="100">Volltext:</td>
            <td class="headline2">
              <input type="text" name="text15" id="text15">
                <xsl:if test="$State/FieldValue[@FieldNo=15]">
                  <xsl:attribute name="value"><xsl:value-of select="$State/FieldValue[@FieldNo=15]/@Text"/></xsl:attribute>
                </xsl:if>
              </input>
              <xsl:variable name="fieldValue" select="//FieldValue[@FieldNo = '15']" />
              <select id="weight15" name="weight15">
                <!--option value="0" ><xsl:if test="$fieldValue[@Weight = 0]"><xsl:attribute name="selected" /></xsl:if> 0</option-->
                <option value="1" ><xsl:if test="$fieldValue[@Weight = 1]"><xsl:attribute name="selected" /></xsl:if> 1</option>
                <option value="2" ><xsl:if test="$fieldValue[@Weight = 2]"><xsl:attribute name="selected" /></xsl:if> 2</option>
                <option value="3" ><xsl:if test="$fieldValue[@Weight = 3]"><xsl:attribute name="selected" /></xsl:if> 3</option>
              </select>
            </td>
            <td class="headline2"/>
          </tr>
          </div>
          <tr>
            <td height="8" colspan="3"/>
          </tr>
          <xsl:call-template name="SearchForm-Submit" />
      </tbody>
      </table>
    </td>
    <td width="100"/>
    </tr>
</xsl:template>

<!-- ############ Specifiy Custom Resultlist here, call Template in DefaultSearchPage or other used Stylesheet ########### -->
<!-- ############ Templatenames should consist of SearchResult-'Indexname' ########### -->

<!-- ############ Results Column Ouput ############# -->
<xsl:template name="SearchResult-PlaceIndexNameHere-Column">
  <xsl:param name="IndexName" />
  <xsl:param name="State" />

  <xsl:if test="/SearchPage/State/sr:AnyFinderSearchResult">
    <tr>
      <td height="16" colspan="5"/>
    </tr>
    <xsl:choose>
      <xsl:when test="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/sr:Items">
          <tr>
            <td class="headline3" valign="top">Relevanz:</td>
            <td class="headline3" valign="top">Dokument:</td>
            <td class="headline3" valign="top">Author:</td>
          </tr>
          <tr>
            <td bgColor="{$SeparatorBgColor}" colspan="4">
              <img height="1" src="img/x.gif" alt="" width="1" border="0"/>
            </td>
          </tr>
          <tr>
            <td height="10" colspan="3"/>
          </tr>

        <xsl:for-each select="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/sr:Items/sr:Item">
            <xsl:sort data-type="number" select="@Score" order="descending"/>
            <tr align="left">
              <td class="headline3"><xsl:value-of select="@Score div 2.5" /> %</td>
          <!--xsl:for-each select="sr:Field">
              <td><xsl:apply-templates /></td>
          </xsl:for-each-->                             
          <!-- Show All -->
          <!--xsl:for-each select="sr:ResultField">
            <tr align="left">
              <td class="headline3" valign="top"  width="125"><xsl:value-of select="@Name" />:</td>
              <td width="558" colspan="2"><xsl:apply-templates /></td>
            </tr>
          </xsl:for-each-->                             
          <!-- Example Show Speicific -->
              <td class="headline3" width="100%">
              <!-- for a linkage to a document use following example with display of fullpath or documentname -->
              <a>
                <xsl:attribute name="href"><xsl:value-of select="sr:ResultField[@Name='FullPath']/text()" /></xsl:attribute>
                <!--xsl:attribute name="class">headline3</xsl:attribute--><xsl:attribute name="target">_blank</xsl:attribute>
                <!--xsl:value-of select="sr:ResultField[@Name='FullPath']/text()" /-->
                <xsl:value-of select="sr:ResultField[@Name='FullPath']/text()" />
              </a>
              </td>
              <td class="headline3"><xsl:value-of select="sr:ResultField[@Name='Author']/text()" /></td>
            </tr>
          <!-- Show Highlighting Fields -->
          <xsl:for-each select="sr:HighlightingField">
            <tr align="left">
              <td  class="headline3" width="558" colspan="5"><xsl:apply-templates /></td>
            </tr>
          </xsl:for-each>                             
          <tr>
            <td height="20" colspan="3"/>
          </tr>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="ErrorMessage" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:if>

</xsl:template>


<!-- ############ Results Row Ouput ############# -->

<xsl:template name="SearchResult-PlaceIndexNameHere">
  <xsl:param name="IndexName" />
  <xsl:param name="State" />

  <xsl:if test="/SearchPage/State/sr:AnyFinderSearchResult">
    <xsl:choose>
      <xsl:when test="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/sr:Items">
        <xsl:for-each select="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/sr:Items/sr:Item">
            <xsl:sort data-type="number" select="@Score" order="descending"/>
            <tr>
              <td class="headline3" valign="top" width="125">Relevanz:</td>
              <td class="headline3" width="558" colspan="2"><xsl:value-of select="@Score div 2.5" /> %</td>
            </tr>
          <xsl:for-each select="sr:Field">
            <tr align="left">
              <td class="headline2" valign="top" width="125">Pfad:</td>
              <td class="headline2" width="558" colspan="2"><xsl:apply-templates /></td>
            </tr>
          </xsl:for-each>                             
          <!-- Show All >
          <xsl:for-each select="sr:ResultField">
            <tr align="left">
              <td class="headline3" valign="top"  width="125"><xsl:value-of select="@Name" />:</td>
              <td class="headline3" width="558" colspan="2"><xsl:apply-templates /></td>
            </tr>
          </xsl:for-each-->                             
          <!-- Example Show Specific -->
            <tr align="left">
              <td class="headline3" valign="top"  width="125">Titel:</td>
              <td class="headline3" width="558" colspan="2"><xsl:value-of select="sr:ResultField[@Name='title']/text()" /></td>
            </tr>
            <tr align="left">
              <td class="headline3" valign="top"  width="125">Datum:</td>
              <td class="headline3" width="558" colspan="2"><xsl:value-of select="sr:ResultField[@Name='DocDateDisplay']/text()" /></td>
            </tr>
          <xsl:for-each select="sr:HighlightingField">
            <tr align="left">
              <td class="headline3" valign="top" width="125">Volltext:</td>
              <td class="headline3" width="558" colspan="2"><xsl:apply-templates /></td>
            </tr>
          </xsl:for-each>                             
            <tr>
              <td height="20" colspan="3"/>
            </tr>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="ErrorMessage" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:if>

</xsl:template>

</xsl:stylesheet>
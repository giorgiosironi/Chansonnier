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
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:sr="http://www.anyfinder.de/SearchResult" xmlns="http://www.anyfinder.de/SearchForm" xmlns:em="http://www.anyfinder.de/ErrorMessage" xmlns:java="http://xml.apache.org/xslt/java">

  <!-- ######### Sepcify Import Stylesheet for Custom Resullt and or Searchforms per Index here ############# -->
  <xsl:import href="configuration/org.eclipse.smila.lucene/CustomListandForm.xsl" />

  <xsl:output method="html" encoding="UTF-8" indent="yes" doctype-public="-//W3C//DTD HTML 4.0 Transitional//EN"/>

  <xsl:variable name="SeparatorBgColor">DCDCDE</xsl:variable>

  <xsl:template match="/">
    <html>
      <head>
        <title>Smila - Sample Search Site</title>
        <link rel="stylesheet" type="text/css" href="stylesheets/SMILA.css"/>
        <link rel="stylesheet" type="text/css" href="stylesheets/SMILA_subNav.css"/>
        <link rel="stylesheet" type="text/css" href="stylesheets/SMILA_content.css"/>
        <script type="text/javascript"></script>
      </head>
      <body leftMargin="0" topMargin="0" marginwidth="0" marginheight="0">
        <xsl:apply-templates select="/SearchPage"/>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template name="Header">
  <div class="head" id="head">
    <img src="images/eclipse_home_header.jpg" style="border-width:0px;" alt=""/>
    <div class="projectname" name="projectname">
      SMILA
    </div>
    <div class="headright" id="headright" >
      <img src="images/eclipseheaderright.jpg" style="border-width:0px;" alt=""/>
    </div>
      <div class="metanav" id="metanav" >
      <span>
      <table cellpadding="0" cellspacing="0" border="0">
        <tr>
          <td>
            <a target="blank">
              <xsl:attribute name="href">#</xsl:attribute>
              eclipse.org</a>
          </td>
        </tr>
      </table>
      </span>
    </div>

  <!-- ######### Sepcify Main Menu Entries here ############# -->

    
    <!--div class="mainnav" id="mainnav" style="position:absolute; top:106px;left:40px;z-index:2;">
      <span >
      <table cellpadding="0" cellspacing="0" border="0">
      <tr>
      <td style="padding-right:4px;"><img src="images/butt_circle_arrow_right_white_active.jpg" alt="" style="z-index:2;" \></td><td style="padding-right:9px;"><a class="aktiveLink" href="index.html">Unternehmen</a></td>   
      <td style="padding-right:4px;"><img src="images/butt_circle_arrow_right_white.jpg" alt="" style="z-index:2;" \></td><td style="padding-right:9px;"><a href="products.html">Produkte</a></td>   
      </tr>
      </table>
      </span>
    </div-->
    </div>
  </xsl:template>

  <xsl:template name="Marginalia">
    <div class="right" id="right">
      <div class="paragraph">
        <div class="paragraphHeadline">Project Proposal</div>
        <div class="paragraphText">Click here to read the project proposal:</div>
        <div class="paragraphTeaserLink">
        <a>
        <xsl:attribute name="href">http://www.eclipse.org/proposals/smila/</xsl:attribute>www.eclipse.org/proposals/smila/
        </a>
        </div>
      </div>
      <div class="paragraph">
        <div class="paragraphHeadline">Project Newsgroup</div>
        <div class="paragraphText">Join the projects newsgoup here:</div>
        <div class="paragraphTeaserLink">
          <a>
            <xsl:attribute name="href">http://www.eclipse.org/newsportal/thread.php?group=eclipse.technology.smila</xsl:attribute>eclipse.technology.smila
          </a>
        </div>
      </div>
    </div>
  </xsl:template>     
      
  <xsl:template match="/SearchPage">
  <div class="frame" id="frame">
    <form action="" accept-charset="UTF-8">
      <input type="hidden" name="Style" id="Style" value="{/SearchPage/State/Style}"/>
      <xsl:call-template name="Header" />
      <xsl:call-template name="Marginalia" />
      <xsl:call-template name="IndexList" />

      <table cellSpacing="0" cellPadding="8" width="100%" border="0">
        <tbody>
          <tr>
            <td vAlign="top" align="middle">
              <table cellSpacing="0" border="0">
                <tbody>
                  <tr>
                    <td colspan="7">
                    <div class="display" id="display">
                    <div class="content">
                      <table cellspacing="0" cellpadding="0" border="0">
                        <tbody>
                          <tr>
                              <!-- Search Table Column-->
                            <td vAlign="top" bgColor="#ffffff" colSpan="4">
                              <xsl:if test="//SearchPage/State/Index">
                                <table cellSpacing="0" cellPadding="0" border="0">
                                  <tbody>
                                    <!-- search mask -->
                                    <xsl:call-template name="SearchForm">
                                      <xsl:with-param name="IndexName" select="//SearchPage/State/Index/@Name" />
                                      <xsl:with-param name="State" select="//SearchPage/State" />
                                    </xsl:call-template>
                                  </tbody>
                                </table>

                                    <!-- separator to result (simple line or pagingrow) -->
                                <div id="Separator">
                                    <!--xsl:call-template name="PagingRow" /-->
                                    <xsl:call-template name="SeparatorLine" />
                                </div>
                                    <!-- result list -->
                                
                                <table cellSpacing="0" cellPadding="0" border="0">
                                  <tbody>
                                    <xsl:call-template name="SearchResult">
                                      <xsl:with-param name="IndexName" select="//SearchPage/State/Index/@Name" />
                                      <xsl:with-param name="State" select="//SearchPage/State" />
                                    </xsl:call-template>
                                  </tbody>
                                </table>
                                
                              </xsl:if>
                            </td>
                          </tr>
<!-- ############ Pagingrow below Resultlist ############# -->
                          <tr>
                            <td colSpan="4">
                              <xsl:call-template name="PagingRow" />
                            </td>
                          </tr>
                        </tbody>
                      </table>
                      </div>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </td>
          </tr>
        </tbody>
      </table>
    </form>
    </div>
  </xsl:template>

<!-- ########### Specify Modified Forms, Mind Style Import  ##############-->
<!-- ########### Standard Output Form: SearchForm-Standard ##############-->

  <xsl:template name="SearchForm">
    <xsl:param name="IndexName" />
    <xsl:param name="State" />
  
    <xsl:choose>
<!-- ############ Templatenames should consist of SearchForm-'Indexname' ########### -->
<!-- ############ To use custom search forms, replace PlaceIndexNameHere with desired Indexname, 
  also adjust NewCustomsListandForm.xsl or equivalent external file########### -->

    <xsl:when test="$IndexName = 'PlaceIndexNameHere'">
      <xsl:call-template name="SearchForm-PlaceIndexNameHere">
        <xsl:with-param name="IndexName" select="$IndexName" />
        <xsl:with-param name="State" select="$State" />
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="SearchForm-Standard">
        <xsl:with-param name="IndexName" select="$IndexName" />
        <xsl:with-param name="State" select="$State" />
      </xsl:call-template>
    </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

<!-- ########### Specify Modified Resultlist, Mind Style Import  ##############-->
<!-- ########### Standard Output List: SearchResult-Standard ##############-->

  <xsl:template name="SearchResult">
    <xsl:param name="IndexName" />
    <xsl:param name="State" />
  
    <xsl:choose>
<!-- ############ Templatenames should consist of SearchResult-'Indexname' ########### -->
<!-- ############ To use custom search results, replace PlaceIndexNameHere with desired Indexname, 
  also adjust NewCustomsListandForm.xsl or equivalent external file########### -->
      <xsl:when test="$IndexName = 'PlaceIndexNameHere'">
        <xsl:call-template name="SearchResult-PlaceIndexNameHere-Column">
          <xsl:with-param name="IndexName" select="$IndexName" />
          <xsl:with-param name="State" select="$State" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="SearchResult-Standard">
          <xsl:with-param name="IndexName" select="$IndexName" />
          <xsl:with-param name="State" select="$State" />
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  
</xsl:template>

<!-- ########### Standard Forms ##############-->
<xsl:template name="SearchForm-Standard">
  <xsl:param name="IndexName" />
  <xsl:param name="State" />
    <tr>
    <td width="100"/>
    <td width="483">
      <input type="hidden" name="index">
        <xsl:attribute name="value"><xsl:value-of select="$IndexName"/></xsl:attribute>
      </input>
      <!-- to retrieve all data change minSimilarity to 0 -->
      <input type="hidden" name="minSimilarity" id="minSimilarity" value="1" />
      <table cellSpacing="0" cellPadding="0" border="0">
        <tbody>
          <tr>
            <td height="8" colspan="3"/>
          </tr>
          <xsl:for-each select="//SearchPage/Indices/Index[@Name=$IndexName]/Field">
            <tr>
              <xsl:variable name="fieldNo">
                <xsl:value-of select="@FieldNo"/>
              </xsl:variable>
              <td class="headline2" width="100">
                <xsl:value-of select="@Name"/>:
              </td>
              <xsl:choose>
                <xsl:when test="@Type='Text'">
                  <td class="headline2">
                    <input type="text">
                      <xsl:attribute name="name">text<xsl:value-of select="@FieldNo"/></xsl:attribute>
                      <xsl:if test="$State/FieldValue[@FieldNo=$fieldNo]">
                        <xsl:attribute name="value"><xsl:value-of select="$State/FieldValue[@FieldNo=$fieldNo]/@Text"/></xsl:attribute>
                      </xsl:if>
                    </input>
                  </td>
                  <td class="headline2"/>
                </xsl:when>
                <xsl:otherwise>
                  <td class="headline2">
                    <input type="text">
                      <xsl:attribute name="name">min<xsl:value-of select="@FieldNo"/></xsl:attribute>
                      <xsl:if test="$State/FieldValue[@FieldNo=$fieldNo]">
                        <xsl:attribute name="value"><xsl:value-of select="$State/FieldValue[@FieldNo=$fieldNo]/@Min"/></xsl:attribute>
                      </xsl:if>
                    </input>
                  </td>
                  <td width="20" align="center" class="term1">-</td>
                  <td class="headline2">
                    <input type="text">
                      <xsl:attribute name="name">max<xsl:value-of select="@FieldNo"/></xsl:attribute>
                      <xsl:if test="$State/FieldValue[@FieldNo=$fieldNo]">
                        <xsl:attribute name="value"><xsl:value-of select="$State/FieldValue[@FieldNo=$fieldNo]/@Max"/></xsl:attribute>
                      </xsl:if>
                    </input>
                  </td>
                </xsl:otherwise>
              </xsl:choose>
            </tr>
          </xsl:for-each>
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

<!-- ########### Form Submit ##############-->
<xsl:template name="SearchForm-Submit">
  <tr>
    <td class="term2" width="100"/>
    <td colspan="4" align="right">
    
      <a>
      <xsl:attribute name="href">SearchForm?Style=<xsl:value-of select="/SearchPage/State/Style"/>&amp;index=<xsl:value-of select="/SearchPage/State/Index/@Name"/></xsl:attribute>
      <xsl:attribute name="class">term1</xsl:attribute>
      <xsl:value-of select="@Name"/>
      <img src="images/Back.png" border="0" alt="Clear Form"/>
      </a>
      <img src="img/x.gif" alt="" width="5" border="0"/>
      <input onclick="submit();" type="image" src="images/Search.png"/>
      <input type="hidden" name="SelectedPage" value="1" />
    </td>
  </tr>
  <tr>
    <td height="8" colspan="3"/>
  </tr>
</xsl:template>


<!-- ########### Index List ##############-->

<xsl:template name="IndexList">
    <div class="subnavigation" id="subnavigation">
      <div class="subnavHeader" id="subnavHeader">
      <table class="subnavTitle" cellpadding="0" cellspacing="0" border="0">
        <tr>
          <td align="center"> Indexlist </td>
          <td> </td>
        </tr>
      </table>
      </div>
    <xsl:for-each select="//SearchPage/Indices/Index">
      <div class="subnavSectionColorGrey">
        <div class="subnavSection" id="subnavSection1">
        <table class="subNavEntry" cellpadding="0" cellspacing="0" border="0">
        <tr>
          <td>

            <table class="subnavLevel1" cellpadding="0" cellspacing="0" border="0">
            <xsl:if test="@Name = //SearchPage/State/Index/@Name"><xsl:attribute name="class">subnavActiveLevel1</xsl:attribute>
            </xsl:if>
              <tr>
                <td>
                  <img src="images/butt_arrow_right_grey.gif" alt="" style="z-index:2;padding-right:2px;" />
                </td>
                <td>
                  <a>

                    <xsl:attribute name="href">SearchForm?Style=<xsl:value-of select="/SearchPage/State/Style" />&amp;index=<xsl:value-of select="@Name"/></xsl:attribute>
                    <xsl:value-of select="@Name"/>
                  </a>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        </table>
        <span class="rbottom"><span class="rl4"></span><span class="rl3"></span><span class="rl2"></span><span class="rlbline"></span></span>
        </div>
      </div>
    </xsl:for-each>
      <div class="subnavEmptySection" id="subnavEmptySection">
        <table class="subNavEntry" cellpadding="0" cellspacing="0" border="0">
        <tr>
          <td> </td>
        </tr>
        </table>
      </div>
    </div>
</xsl:template>



<xsl:template name="SeparatorLine">
  <xsl:if test="/SearchPage/State/sr:AnyFinderSearchResult">
    <div class="headline">Searchresults
    </div>
    <div class="headline2">
    <xsl:choose>
      <xsl:when test="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/sr:HitDistribution">
        <xsl:variable name="sum" select="sum(/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/sr:HitDistribution/sr:Hit/@Hits)" />
        Documents Indexed:<xsl:value-of select="$sum" /> 
      </xsl:when>
    </xsl:choose>
    </div>
  </xsl:if>
</xsl:template>

<!-- ########### Standard Result ##############-->
<xsl:template name="SearchResult-Standard">
  <xsl:param name="IndexName" />
  <xsl:param name="State" />

  <xsl:if test="/SearchPage/State/sr:AnyFinderSearchResult">
    <tr>
      <td height="16" colspan="5"/>
    </tr>
    <xsl:choose>
      <xsl:when test="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/sr:Items">
        <xsl:for-each select="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/sr:Items/sr:Item">
            <xsl:sort data-type="number" select="@Score" order="descending"/>
            <tr>
              <td class="headline3" valign="top" width="125">Score:</td>
              <td class="headline3" width="558" colspan="2"><xsl:value-of select="@Score div 2.5" /> %</td>
            </tr>
          <xsl:for-each select="sr:Field">
            <tr align="left">
              <td class="headline2" valign="top" width="125"><xsl:value-of select="@Name" />:</td>
              <td class="headline2" width="558" colspan="2"><xsl:apply-templates /></td>
            </tr>
          </xsl:for-each>                             
          <!-- Show All -->
          <xsl:for-each select="sr:ResultField">
            <tr align="left">
              <td class="headline3" valign="top"  width="125"><xsl:value-of select="@Name" />:</td>
              <td class="headline3" width="558" colspan="2"><xsl:apply-templates /></td>
            </tr>
          </xsl:for-each>                             
          <xsl:for-each select="sr:HighlightingField">
            <tr align="left">
              <td class="headline3" valign="top" width="125"><xsl:value-of select="@Name" />:</td>
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

<!-- ########### Error Handling ##############-->
<xsl:template name="ErrorMessage">
  <tr>
    <tr>
      <td class="headline2" valign="top" width="125">Code:</td>
      <td class="headline3" width="558" colspan="2"><xsl:value-of select="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/em:ErrorMessage/em:Code" /></td>
    </tr>
    <tr>
      <td class="headline2" valign="top" width="125">Message:</td>
      <td class="headline3" width="558" colspan="2"><xsl:value-of select="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/em:ErrorMessage/em:Message" /></td>
    </tr>
    <tr>
      <td class="headline2" valign="top" width="125">Detail:</td>
      <td class="headline3" width="558" colspan="2"><xsl:value-of select="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/em:ErrorMessage/em:Detail" /></td>
    </tr>
    <tr>
      <td class="headline2" valign="top" width="125">Source:</td>
      <td class="headline3" width="558" colspan="2"><xsl:value-of select="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/em:ErrorMessage/em:Source" /></td>
    </tr>
  </tr>
</xsl:template>

<xsl:template name="PagingRow">
  <xsl:if test="/SearchPage/State/sr:AnyFinderSearchResult">
    <table cellSpacing="0" cellPadding="0" width="100%" border="0" class="pagingrow">
      <tbody>
        <tr>
          <td width="17">
          </td>
          <td class="headline3" align="left">
            <!--xsl:if test="count(//em:ErrorMessage) = 0"-->
            <xsl:if test="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result and count(//em:ErrorMessage) = 0">
              Results <span style="COLOR: black"><xsl:value-of select="/SearchPage/State/StartHits+1" /> - </span>
              <span style="color: #000000"><xsl:value-of select="/SearchPage/State/StartHits+/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/@Hits" /></span> of   <xsl:value-of select="/SearchPage/State/TotalRecords" />
            </xsl:if>
          </td>
          <td class="headline3" align="right">
            <xsl:call-template name="SearchResultPaging" />
          </td>
        </tr>
      </tbody>
    </table>
  </xsl:if>
</xsl:template>

<!-- ########### Result Paging ##############-->
<xsl:template name="SearchResultPaging">
    <table border="0">
      <tbody>
        <tr>
          <xsl:choose>
            <xsl:when test="count(//em:ErrorMessage) = 0">
              <xsl:if test="/SearchPage/State/SelectedPage &gt;= 6"><td class="heading" valign="baseline"><a><xsl:attribute name="href">
                <xsl:value-of select="/SearchPage/State/PageingURL"/>1</xsl:attribute><img src="images/First.png" border="0"><xsl:attribute name="alt">Page 1</xsl:attribute></img></a></td>
              </xsl:if>
              <xsl:if test="/SearchPage/State/SelectedPage != 1"><td class="heading" valign="baseline"><a><xsl:attribute name="href">
              <xsl:value-of select="/SearchPage/State/PageingURL"/><xsl:value-of select="/SearchPage/State/SelectedPage - 1" /></xsl:attribute><img src="images/Bwd.png" border="0" ><xsl:attribute name="alt">Page <xsl:value-of select="/SearchPage/State/SelectedPage - 1" /></xsl:attribute></img></a></td></xsl:if>
              <xsl:variable name="linksDisplayed" select="0" />
              <xsl:variable name="startLinks">
                <xsl:choose>
                  <xsl:when test="(/SearchPage/State/SelectedPage - 4) > 1"><xsl:value-of select="(/SearchPage/State/SelectedPage - 4)" /></xsl:when>
                  <xsl:otherwise><xsl:value-of select="1" /></xsl:otherwise>
                </xsl:choose>
              </xsl:variable>
              <xsl:variable name="toDisplay">
                <xsl:choose>
                  <xsl:when test="$startLinks = 1"><xsl:value-of select="5 - /SearchPage/State/SelectedPage" /></xsl:when>
                  <xsl:otherwise><xsl:value-of select="0" /></xsl:otherwise>
                </xsl:choose>
              </xsl:variable>
              <xsl:variable name="stopLinks">
                <xsl:choose>
                  <xsl:when test="(ceiling(/SearchPage/State/TotalRecords div /SearchPage/State/MaxHits) - /SearchPage/State/SelectedPage) &lt; 4"><xsl:value-of select="ceiling(/SearchPage/State/TotalRecords div /SearchPage/State/MaxHits)" /></xsl:when>
                  <xsl:otherwise><xsl:value-of select="/SearchPage/State/SelectedPage + 4" /></xsl:otherwise>
                </xsl:choose>
              </xsl:variable>
              <xsl:variable name="fromDisplay">
                <xsl:choose>
                  <xsl:when test="$stopLinks = ceiling(/SearchPage/State/TotalRecords div /SearchPage/State/MaxHits)"><xsl:value-of select="4 - (ceiling(/SearchPage/State/TotalRecords div /SearchPage/State/MaxHits) - /SearchPage/State/SelectedPage)" /></xsl:when>
                  <xsl:otherwise><xsl:value-of select="0" /></xsl:otherwise>
                </xsl:choose>
              </xsl:variable>
              <xsl:call-template name="displayPages">
                  <xsl:with-param name="i"><xsl:choose>
                    <xsl:when test="$startLinks - $fromDisplay &gt; 0"><xsl:value-of select="$startLinks - $fromDisplay"/></xsl:when>
                    <xsl:otherwise>1</xsl:otherwise>
                  </xsl:choose>
                  </xsl:with-param>
                  <xsl:with-param name="count" select="/SearchPage/State/SelectedPage + $toDisplay + 4" />
              </xsl:call-template>
  
              <xsl:if test="/SearchPage/State/SelectedPage != ceiling(/SearchPage/State/TotalRecords div /SearchPage/State/MaxHits)">
                <td class="headline2" valign="baseline">
                  <a>
                    <xsl:attribute name="href"><xsl:value-of select="/SearchPage/State/PageingURL"/><xsl:value-of select="/SearchPage/State/SelectedPage + 1" /></xsl:attribute>
                    <img src="images/Fwd.png" border="0" >
                      <xsl:attribute name="alt">Page <xsl:value-of select="/SearchPage/State/SelectedPage + 1" /></xsl:attribute>
                    </img>
                  </a>
                </td>
              </xsl:if>
              <xsl:if test="/SearchPage/State/SelectedPage != ceiling(/SearchPage/State/TotalRecords div /SearchPage/State/MaxHits) and ceiling(/SearchPage/State/TotalRecords div /SearchPage/State/MaxHits) &gt;= 10">
                <td class="headline2" valign="baseline">
                  <a>
                    <xsl:attribute name="href">
                      <xsl:value-of select="/SearchPage/State/PageingURL"/>
                      <xsl:choose>
                        <xsl:when test="/SearchPage/State/SelectedPage &lt;= (ceiling(/SearchPage/State/TotalRecords div /SearchPage/State/MaxHits) - 11) and /SearchPage/State/SelectedPage &gt; 5">
                          <xsl:value-of select="$stopLinks + 1"/>
                        </xsl:when>
                        <xsl:when test="/SearchPage/State/SelectedPage &lt;= 5">
                          <xsl:value-of select="10"/>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:value-of select="ceiling(/SearchPage/State/TotalRecords div /SearchPage/State/MaxHits)" />
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:attribute>
                    <img src="images/FastFwd.png" border="0">
                    </img>
                  </a>
                </td>
              </xsl:if>
            </xsl:when>
            <xsl:otherwise><td colspan="3"/></xsl:otherwise>
          </xsl:choose>
        </tr>
      </tbody>
    </table>
</xsl:template>

<xsl:template name="displayPages">
        <xsl:param name="i"/>
        <xsl:param name="count"/>
        <xsl:if test="$i &lt;= $count and $i &lt;= ceiling(/SearchPage/State/TotalRecords div /SearchPage/State/MaxHits)">
          <td class="headline2">
            <xsl:choose>
              <xsl:when test="$i = /SearchPage/State/SelectedPage"><span style="color:black;font-size:12px;"><xsl:value-of select="$i" /></span></xsl:when>
              <xsl:otherwise><a><xsl:attribute name="href"><xsl:value-of select="/SearchPage/State/PageingURL" /><xsl:value-of select="$i" /></xsl:attribute><xsl:value-of select="$i"/></a></xsl:otherwise>
            </xsl:choose>
          </td>
            <xsl:call-template name="displayPages">
                <xsl:with-param name="i" select="$i + 1" />
                <xsl:with-param name="count"  select="$count"/>
            </xsl:call-template>
        </xsl:if>
</xsl:template>

<xsl:template match="text()" ><xsl:value-of select="java:org.eclipse.smila.utils.xml.XsltTools.splitLongWords(string(.), 50)" /></xsl:template>

<xsl:template match="sr:HighLighted"><xsl:text> </xsl:text><b><xsl:apply-templates /> </b><xsl:text> </xsl:text></xsl:template>
</xsl:stylesheet>

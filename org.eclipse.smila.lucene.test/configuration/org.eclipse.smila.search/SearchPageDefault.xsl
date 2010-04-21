<?xml version="1.0" encoding="UTF-8"?>
<?altova_samplexml D:\AnyFinder\af-Engine-Caatoosee-SDK\appserv\webapps\AnyFinder\SearchPage.xml?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:sr="http://www.anyfinder.de/SearchResult" xmlns="http://www.anyfinder.de/SearchForm" xmlns:em="http://www.anyfinder.de/ErrorMessage" xmlns:java="http://xml.apache.org/xslt/java">

	<!-- ######### Sepcify Import Stylesheet for Custom Resullt and or Searchforms per Index here ############# -->
	<xsl:import href="configuration/org.eclipse.smila.search/CustomListandForm.xsl" />

	<xsl:output method="html" encoding="UTF-8" indent="yes" doctype-public="-//W3C//DTD HTML 4.0 Transitional//EN"/>

	<xsl:variable name="SeparatorBgColor">DCDCDE</xsl:variable>

	<xsl:template match="/">
		<html>
			<head>
				<title>Smila - Sample Search Site</title>
				<link rel="stylesheet" type="text/css" href="stylesheets/Smila_new.css"/>
				<link rel="stylesheet" type="text/css" href="stylesheets/Smila_paging.css"/>
				<script type="text/javascript"></script>
			</head>
			<body leftMargin="0" topMargin="0" marginwidth="0" marginheight="0">
				<div id="maincontainer">
				<div id="topsection">
					<xsl:call-template name="Header" />
				</div>
				
				<div id="contentwrapper">
					<div id="contentcolumn">
						<div class="innertube">
							<xsl:apply-templates select="/SearchPage"/>
						</div>
					</div>
				</div>
				
				<div id="leftcolumn">
					<div class="innertube">
						<xsl:call-template name="IndexList" />
					</div>
					<div class="fade">
					</div>
				</div>
				
				<div id="rightcolumn">
					<div class="innertube">
						<xsl:call-template name="Marginalia" />
					</div>
				</div>
				
				<div id="footer">				
					<xsl:call-template name="Footer" />
				</div>
				
				</div>
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
			      <a target="blank">
			        <xsl:attribute name="href">http://www.eclipse.org</xsl:attribute>
			        eclipse.org</a>
		</div>
		<div class="header-menu"></div>
	</div>
	</xsl:template>




	<xsl:template name="Footer">
		<div id="footertext">
			<ul>
				<li class="first">Copyright 2008</li>
				<li>Smila Edition Version 1.0.0</li>
			</ul>
		</div>
	</xsl:template>


	<xsl:template name="Marginalia">
		<div class="right" id="right">
			<div class="paragraph">
				<div class="paragraphHeadline">SMILA</div>
				<div class="paragraphText">Find out more about SMILA</div>
				<div class="paragraphTeaserLink">
				<a>
				<xsl:attribute name="href">http://www.eclipse.org/smila/</xsl:attribute>www.eclipse.org/smila</a>
				</div>
			</div>
	      	<div class="paragraph">
	        	<div class="paragraphHeadline">SMILA Project Newsgroup</div>
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
		<form action="" accept-charset="UTF-8">
			<input type="hidden" name="Style" id="Style" value="{/SearchPage/State/Style}"/>
				<!--div class="display" id="display"-->
					<xsl:if test="//SearchPage/State/Index">
								<!-- search mask -->
								<xsl:call-template name="SearchForm">
									<xsl:with-param name="IndexName" select="//SearchPage/State/Index/@Name" />
									<xsl:with-param name="State" select="//SearchPage/State" />
								</xsl:call-template>
	
								<!-- separator to result (simple line or pagingrow) -->
								<div id="Separator">
									<xsl:call-template name="SeparatorLine" />
									<xsl:call-template name="PagingRow" />
								</div>
								<!-- result list -->
						
								<xsl:call-template name="SearchResult">
									<xsl:with-param name="IndexName" select="//SearchPage/State/Index/@Name" />
									<xsl:with-param name="State" select="//SearchPage/State" />
								</xsl:call-template>
						
					</xsl:if>
	<!-- ############ Pagingrow below Resultlist ############# -->
					<xsl:call-template name="PagingRow" />
				<!--/div-->
		</form>
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
		<div class="cmxform">

			<input type="hidden" name="index">
				<xsl:attribute name="value"><xsl:value-of select="$IndexName"/></xsl:attribute>
			</input>
			<!-- to retrieve all data change minSimilarity to 0 -->
			<input type="hidden" name="minSimilarity" id="minSimilarity" value="1" />
			<fieldset>
				<ol>
					<xsl:for-each select="//SearchPage/Indices/Index[@Name=$IndexName]/Field">
						<li>
							<xsl:variable name="fieldNo">
								<xsl:value-of select="@FieldNo"/>
							</xsl:variable>
							<label for="Fieldlabel1" style="display: -moz-inline-box;">
							<span style="display: block; width: 120px;">
								<xsl:value-of select="@Name"/>:
							</span>
							</label> 
							<xsl:choose>
								<xsl:when test="@Type='Text'">
										<input type="text">
											<xsl:attribute name="name">text<xsl:value-of select="@FieldNo"/></xsl:attribute>
											<xsl:if test="$State/FieldValue[@FieldNo=$fieldNo]">
												<xsl:attribute name="value"><xsl:value-of select="$State/FieldValue[@FieldNo=$fieldNo]/@Text"/></xsl:attribute>
											</xsl:if>
										</input>
								</xsl:when>
								<xsl:otherwise>
										<input type="text">
											<xsl:attribute name="name">min<xsl:value-of select="@FieldNo"/></xsl:attribute>
											<xsl:if test="$State/FieldValue[@FieldNo=$fieldNo]">
												<xsl:attribute name="value"><xsl:value-of select="$State/FieldValue[@FieldNo=$fieldNo]/@Min"/></xsl:attribute>
											</xsl:if>
										</input>
										<input type="text">
											<xsl:attribute name="name">max<xsl:value-of select="@FieldNo"/></xsl:attribute>
											<xsl:if test="$State/FieldValue[@FieldNo=$fieldNo]">
												<xsl:attribute name="value"><xsl:value-of select="$State/FieldValue[@FieldNo=$fieldNo]/@Max"/></xsl:attribute>
											</xsl:if>
										</input>
								</xsl:otherwise>
							</xsl:choose>
							</li>
					</xsl:for-each>
					<xsl:call-template name="SearchForm-Submit" />
				</ol>
			</fieldset>
		</div>
</xsl:template>

<!-- ########### Form Submit ##############-->
<xsl:template name="SearchForm-Submit">
			<!--a>
			<xsl:attribute name="href">SearchForm?Style=<xsl:value-of select="/SearchPage/State/Style"/>&amp;index=<xsl:value-of select="/SearchPage/State/Index/@Name"/></xsl:attribute>
			<xsl:attribute name="class">term1</xsl:attribute>
			<xsl:value-of select="@Name"/>
			<img src="images/Back.png" border="0" alt="Clear Form"/>
			</a-->
			<li>
				<input type="reset" value="Cancel" class="formbutton"/>
				<img src="img/x.gif" alt="" width="5" border="0"/>
				<input type="submit" value="Submit" class="formbutton"/>
				<input type="hidden" name="SelectedPage" value="1" />
			</li>
</xsl:template>


<!-- ########### Index List ##############-->

<xsl:template name="IndexList">
	<div class="indexlist">		
		<div class="header">
			Indexlist 
		</div>
		<xsl:for-each select="//SearchPage/Indices/Index">
			<div class="subnavSectionColorGrey">
				<div class="subnavSection" id="subnavSection1">
					<xsl:if test="@Name = //SearchPage/State/Index/@Name"><xsl:attribute name="class">subnavActiveLevel1</xsl:attribute>
					</xsl:if>
					<a>
						<xsl:attribute name="href">SearchForm?Style=<xsl:value-of select="/SearchPage/State/Style" />&amp;index=<xsl:value-of select="@Name"/></xsl:attribute>
						<xsl:value-of select="@Name"/>
					</a>
				</div>
			</div>
		</xsl:for-each>
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
		<div class="headline2">
			<xsl:if test="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result and count(//em:ErrorMessage) = 0">
				Results <span style="COLOR: black"><xsl:value-of select="/SearchPage/State/StartHits+1" /> - </span>
				<span style="color: #000000"><xsl:value-of select="/SearchPage/State/StartHits+/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/@Hits" /></span> of 	<xsl:value-of select="/SearchPage/State/TotalRecords" />
			</xsl:if>
		</div>
	</xsl:if>
</xsl:template>

<!-- ########### Standard Result ##############-->
<xsl:template name="SearchResult-Standard-old">
	<xsl:param name="IndexName" />
	<xsl:param name="State" />

	<xsl:if test="/SearchPage/State/sr:AnyFinderSearchResult">
		<table class="resultitem">
		<tr>
			<td height="16" colspan="5"/>
		</tr>
		<xsl:choose>
			<xsl:when test="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/sr:Items">
				<xsl:for-each select="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/sr:Items/sr:Item">
						<xsl:sort data-type="number" select="@Score" order="descending"/>
						<tr>
							<td class="score" valign="top" width="125">Score:</td>
							<td class="score" width="558" colspan="2"><xsl:value-of select="@Score div 2.5" /> %</td>
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
		</table>
	</xsl:if>
</xsl:template>

<!-- ########### Standard Result ##############-->
<xsl:template name="SearchResult-Standard-test">
	<xsl:param name="IndexName" />
	<xsl:param name="State" />

	<xsl:if test="/SearchPage/State/sr:AnyFinderSearchResult">

		<xsl:choose>
			<xsl:when test="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/sr:Items">
				<xsl:for-each select="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/sr:Items/sr:Item">
						<xsl:sort data-type="number" select="@Score" order="descending"/>
						<div class="table-row">
							<div class="left-container2">Score:</div>
							<div class="right-container2"><xsl:value-of select="@Score div 2.5" /> %</div>
						</div>
					<xsl:for-each select="sr:Field">
						<div class="table-row">
							<div class="left-container2"><xsl:value-of select="@Name" />:</div>
							<div class="right-container2"><xsl:apply-templates /></div>
						</div>
					</xsl:for-each>															
					<!-- Show All -->
					<xsl:for-each select="sr:ResultField">
						<div class="table-row">
							<div class="left-container2"><xsl:value-of select="@Name" />:</div>
							<div class="right-container2"><xsl:apply-templates /></div>
						</div>
					</xsl:for-each>															
					<xsl:for-each select="sr:HighlightingField">
						<div class="table-row">
							<div class="left-container2"><xsl:value-of select="@Name" />:</div>
							<div class="right-container2"><xsl:apply-templates /></div>
						</div>
					</xsl:for-each>															
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="ErrorMessage" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:if>
</xsl:template>

<!-- ########### Standard Result ##############-->
<xsl:template name="SearchResult-Standard">
	<xsl:param name="IndexName" />
	<xsl:param name="State" />

	<xsl:if test="/SearchPage/State/sr:AnyFinderSearchResult">
		<xsl:choose>
			<xsl:when test="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/sr:Items">
				<xsl:for-each select="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/sr:Items/sr:Item">
					<xsl:sort data-type="number" select="@Score" order="descending"/>
						<div id="result">
							<div id="resultset">
								<div id="resultitem">
									<ol>
										<li>
											<span class="label">Score:</span><xsl:value-of select="@Score div 2.5" /> %
										</li>
										<li>
											<xsl:for-each select="sr:Field">
											<span class="label"><xsl:value-of select="@Name" />:</span>
											<xsl:apply-templates />
											</xsl:for-each>															
										</li>
										<!-- Show All -->
										
										<xsl:for-each select="sr:ResultField">
										<li>
											<span class="label"><xsl:value-of select="@Name" />:</span>
											<xsl:apply-templates />
										</li>
										</xsl:for-each>															
										
										<li>
										<xsl:for-each select="sr:HighlightingField">
											<span class="label"><xsl:value-of select="@Name" />:</span>
											<xsl:apply-templates />
										</xsl:for-each>															
										</li>
									</ol>
								</div>
							</div>
						</div>
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
			<!--xsl:if test="count(//em:ErrorMessage) = 0"-->
	<div id="paging">
		<div id="pagingrow">
			<xsl:call-template name="SearchResultPaging" />
			<!-- Show currently displayed resultnumbers -->
			<!--ul>
				<li>
			<xsl:if test="/SearchPage/State/sr:AnyFinderSearchResult/sr:Result and count(//em:ErrorMessage) = 0">
				Results <span style="COLOR: black"><xsl:value-of select="/SearchPage/State/StartHits+1" /> - </span>
				<span style="color: #000000"><xsl:value-of select="/SearchPage/State/StartHits+/SearchPage/State/sr:AnyFinderSearchResult/sr:Result/@Hits" /></span> of 	<xsl:value-of select="/SearchPage/State/TotalRecords" />
			</xsl:if></li>
			</ul-->
		</div>
	</div>
	</xsl:if>
</xsl:template>

<!-- ########### Result Paging ##############-->
<xsl:template name="SearchResultPaging">
	<div class="sb_pag">
		<ul>
		<li>
			<xsl:choose>
			<xsl:when test="count(//em:ErrorMessage) = 0">
				<xsl:if test="/SearchPage/State/SelectedPage &gt;= 6">

					<a><xsl:attribute name="href"><xsl:value-of select="/SearchPage/State/PageingURL"/>1</xsl:attribute>|&lt;</a>

				</xsl:if>
				<xsl:if test="/SearchPage/State/SelectedPage != 1">

					<a><xsl:attribute name="href"><xsl:value-of select="/SearchPage/State/PageingURL"/><xsl:value-of select="/SearchPage/State/SelectedPage - 1" /></xsl:attribute>&lt;</a>

				</xsl:if>
				<xsl:variable name="linksDisplayed" select="0" />
				<xsl:variable name="startLinks">
					<xsl:choose>
						<xsl:when test="(/SearchPage/State/SelectedPage - 4) &gt; 1"><xsl:value-of select="(/SearchPage/State/SelectedPage - 4)" /></xsl:when>
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

						<a>
							<xsl:attribute name="href"><xsl:value-of select="/SearchPage/State/PageingURL"/><xsl:value-of select="/SearchPage/State/SelectedPage + 1" /></xsl:attribute>
							&gt;
						</a>

				</xsl:if>
				<xsl:if test="/SearchPage/State/SelectedPage != ceiling(/SearchPage/State/TotalRecords div /SearchPage/State/MaxHits) and ceiling(/SearchPage/State/TotalRecords div /SearchPage/State/MaxHits) &gt;= 10">

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
							&gt;&gt;
						</a>

				</xsl:if>
			</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
		</li>
        </ul>
	</div>
</xsl:template>

<xsl:template name="displayPages">
        <xsl:param name="i"/>
        <xsl:param name="count"/>
        <xsl:if test="$i &lt;= $count and $i &lt;= ceiling(/SearchPage/State/TotalRecords div /SearchPage/State/MaxHits)">
				<xsl:choose>
				    <xsl:when test="$i = /SearchPage/State/SelectedPage"><a style="color:#004080;font-size:12px;font-weight:bold;" class="sb_pagS"><xsl:value-of select="$i" /></a></xsl:when>
				    <xsl:otherwise><a><xsl:attribute name="href"><xsl:value-of select="/SearchPage/State/PageingURL" /><xsl:value-of select="$i" /></xsl:attribute><xsl:value-of select="$i"/></a></xsl:otherwise>
			    </xsl:choose>
	  		    <xsl:call-template name="displayPages">
                <xsl:with-param name="i" select="$i + 1" />
				        <xsl:with-param name="count"  select="$count"/>
            </xsl:call-template>
        </xsl:if>
</xsl:template>

<xsl:template match="text()" ><xsl:value-of select="java:org.eclipse.smila.utils.xml.XsltTools.splitLongWords(string(.), 50)" /></xsl:template>

<xsl:template match="sr:HighLighted"><xsl:text> </xsl:text><b><xsl:apply-templates /> </b><xsl:text> </xsl:text></xsl:template>
</xsl:stylesheet>

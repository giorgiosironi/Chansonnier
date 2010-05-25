<?xml version="1.0" encoding="UTF-8"?>
<!-- 
	/*******************************************************************************
	* Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH.
	* All rights reserved. This program and the accompanying materials
	* are made available under the terms of the Eclipse Public License v1.0
	* which accompanies this distribution, and is available at
	* http://www.eclipse.org/legal/epl-v10.html
	*
	* Contributors:
	*    Juergen Schumacher (empolis GmbH) - initial API and implementation
	*******************************************************************************/
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fn="http://www.w3.org/2005/02/xpath-functions" xmlns:s="http://www.eclipse.org/smila/search"
	xmlns:r="http://www.eclipse.org/smila/record" xmlns:i="http://www.eclipse.org/smila/id">
	<xsl:output method="html" encoding="UTF-8" indent="yes" doctype-public="-//W3C//DTD HTML 4.0 Transitional//EN" />

	<xsl:param name="stylesheet" select="'SMILASearchTest'" />

	<xsl:variable name="resultInfo" select="/s:SearchResult/s:Query/r:Record/r:An[@n='result']" />
	<xsl:variable name="totalHits" select="$resultInfo/r:V[@n='totalHits']" />

	<xsl:template match="/s:SearchResult">
		<html>
			<head>
				<title>SMILA - Sample Search Site</title>
			</head>
			<body leftMargin="0" topMargin="0" marginwidth="0" marginheight="0">
				<h2>SMILA Search</h2>

				<xsl:apply-templates select="s:Query" />

				<xsl:if test="$totalHits > 0">
					<xsl:apply-templates select="r:RecordList" />
				</xsl:if>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="/s:Error">
		<html>
			<head>
				<title>SMILA - Sample Search Site</title>
			</head>
			<body leftMargin="0" topMargin="0" marginwidth="0" marginheight="0">
				<h2>SMILA Search Error</h2>
				
				<h3>Message</h3>
				<p><xsl:value-of select="s:Message" /></p>
				
				<h3>Details</h3>
				<pre><xsl:value-of select="s:Details" /></pre>
				
			</body>
		</html>
	</xsl:template>

	<xsl:template match="s:Query">
		<hr />
		<h3>Query</h3>

		<xsl:variable name="parameters" select="r:Record/r:An[@n='parameters']" />

		<form action="" method="POST" accept-charset="UTF-8" enctype="multipart/form-data">
		    <!-- attribute to search in //-->                            			  
            <input type="hidden" name="QueryAttribute" value="Content" />
		
		    <!-- result attributes //-->                            			  
		    <input type="hidden" name="resultAttributes" value="MimeType" />
			<input type="hidden" name="resultAttributes" value="Size" />
			<input type="hidden" name="resultAttributes" value="Extension" />
			<input type="hidden" name="resultAttributes" value="Title" />
			<input type="hidden" name="resultAttributes" value="Url" />
			<input type="hidden" name="resultAttributes" value="LastModifiedDate" />
			<input type="hidden" name="resultAttributes" value="Path" />
			<input type="hidden" name="resultAttributes" value="Filename" />
 			<input type="hidden" name="resultAttributes" value="Author" />			  
		
			<table>
				<tr>
					<td>Query:</td>
					<td>
						<input type="text" name="query" value="{$parameters/r:V[@n='query']}" />
					</td>
					<td>Stylesheet:</td>
					<td>
						<input type="text" name="style" value="{$stylesheet}" />
					</td>
				</tr>
				<tr>
					<td>Result Size:</td>
					<td>
						<input type="text" name="resultSize" value="{$parameters/r:V[@n='resultSize']}" />
					</td>
					<td>Pipeline:</td>
					<td>
						<input type="text" name="pipeline" value="{s:Workflow}" />
					</td>
				</tr>
				<tr>
					<td>Result Offset:</td>
					<td>
						<input type="text" name="resultOffset" value="{$parameters/r:V[@n='resultOffset']}" />
					</td>
					<td>Index Name:</td>
					<td>
						<input type="text" name="indexName" value="{$parameters/r:V[@n='indexName']}" />
					</td>
				</tr>
				<tr>
					<td>Threshold:</td>
					<td>
						<input type="text" name="threshold" value="{$parameters/r:V[@n='threshold']}" />
					</td>
					<td>Show XML result</td>
					<td>
						<input type="checkbox" name="DEBUG" value="true"></input>
					</td>
				</tr>
			</table>
			<h4>Filter examples:</h4>
			<table>
				<tr>
					<xsl:variable name="enumFilter" select="r:Record/r:A[@n='enum']/r:An[@n='filter']" />
					<td>Enum Mode:</td>
					<td>
						<input type="text" name="F.enum" value="{$enumFilter/r:V[@n='mode']}" />
					</td>
					<td>Enum Value:</td>
					<td>
						<input type="text" name="Fval.enum" value="{$enumFilter/r:V[not(@n)]}" />
					</td>
				</tr>
				<tr>
					<xsl:variable name="rangeFilter" select="r:Record/r:A[@n='range']/r:An[@n='filter']" />
					<td>Range Mode:</td>
					<td>
						<input type="text" name="F.range" value="{$rangeFilter/r:V[@n='mode']}" />
					</td>
					<td>Range Min/Max:</td>
					<td>
						<input type="text" name="Fmin.range" value="{$rangeFilter/r:V[@n='min']}" />
						<br />
						<input type="text" name="Fmax.range" value="{$rangeFilter/r:V[@n='max']}" />
					</td>
				</tr>
			</table>
			<h4>Attachment:</h4>
			<table>
				<tr>
					<td>Select file:</td>
					<td>
						<input type="file" name="file" />
					</td>
					<xsl:if test="r:Record/r:An[@n='attachmentFileNames']/r:V[@n='file']">
						<td>Current selection:</td>
						<td>
							<xsl:value-of select="r:Record/r:An[@n='attachmentFileNames']/r:V[@n='file']" />
						</td>
					</xsl:if>
				</tr>
			</table>
			<input type="submit" name="submit" value="OK" />
		</form>

		<xsl:if test="$totalHits &gt; 0">
			<hr />
			<h3>Result</h3>
			<ul>
				<xsl:variable name="queryAttr" select="r:Record/r:A[@n='query']" />
				<li>
					Query terms:
					<xsl:for-each select="$queryAttr/r:An[@n='terms']">
						'
						<xsl:value-of select="r:V[@n='token']" />
						'
					</xsl:for-each>
				</li>
				<li>
					Hits per Term:
					<ul>
						<xsl:for-each select="$queryAttr/r:An[@n='facets']">
							<li>
								'
								<xsl:value-of select="r:V[@n='name']" />
								' :
								<xsl:value-of select="r:V[@n='count']" />
								hits
							</li>
						</xsl:for-each>
					</ul>
				</li>
				<li>
					Searched
					<xsl:value-of select="$resultInfo/r:V[@n='indexSize']" />
					objects in
					<xsl:value-of select="$resultInfo/r:V[@n='runtime']" />
					msec.
				</li>
				<li>
					Listing Results
					<xsl:value-of select="$parameters/r:V[@n='resultOffset'] + 1" />
					-
					<xsl:variable name="expected"
						select="$parameters/r:V[@n='resultSize'] + $parameters/r:V[@n='resultOffset']" />
					<xsl:if test="$totalHits &gt; $expected">
						<xsl:value-of select="$expected" />
					</xsl:if>
					<xsl:if test="$totalHits &lt;= $expected">
						<xsl:value-of select="$totalHits" />
					</xsl:if>
					of
					<xsl:value-of select="$totalHits" />
					:
				</li>
			</ul>
		</xsl:if>
	</xsl:template>

	<xsl:template match="r:RecordList">
		<hr />
		<xsl:for-each select="r:Record">
			<ul>
				<li>
					Score:
					<xsl:value-of select="round(r:An[@n='result']/r:V[@n='relevance']*100)" />
					%
					<ul>
						<li>
							Source:
							<xsl:value-of select="i:Id/i:Source" />
						</li>
						<li>
							Key:
							<xsl:value-of select="i:Id/i:Key" />
						</li>
						<xsl:if test="i:Id/i:Fragment">
							<li>
								Fragment:
								<xsl:value-of select="i:Id/i:Fragment" />
							</li>
						</xsl:if>
						<xsl:for-each select="r:A[r:L/r:V]">
							<li>
								<xsl:value-of select="@n" />
								:
								<xsl:value-of select="r:L/r:V" />
							</li>
						</xsl:for-each>
						<li>
							Summary:
							<xsl:value-of select="r:A/r:An[@n='highlight']/r:V[@n='text']" disable-output-escaping="yes" />
						</li>
					</ul>
				</li>
			</ul>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>

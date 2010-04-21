<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<html>
<head>
<title>Author.xsl</title>
</head> 
<body>
<table border="1">
<tr>
<td width="34%">Name</td>
<td width="6%">Nic</td>
<td width="60%">Email</td>
</tr>
<tr>
<td><xsl:value-of select="author/name"/></td>
<td><xsl:value-of select="author/nic"/></td>
<td><xsl:value-of select="author/email"/></td>
</tr>
</table>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
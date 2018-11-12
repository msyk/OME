<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
<xsl:output method="xml" encoding="UTF-8" />
<xsl:template match="/folder-info">
	<html><head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<style>
<![CDATA[
.indent1	{margin-left: 15px;}
.indent2	{margin-left: 30px;}
.indent3	{margin-left: 45px;}
.indent4	{margin-left: 60px;}
.indent5	{margin-left: 75px;}
.indent6	{margin-left: 90px;}
.indent7	{margin-left: 105px;}
.indent8	{margin-left: 120px;}
.indent9	{margin-left: 135px;}
]]>
		</style>
	</head>
	<body>
	<table>
	<xsl:for-each select="mail-info">
		<tr>
		<td width="35%">
			<xsl:element name="a">
				<xsl:attribute name="href">
					<xsl:value-of select="file-name" />
				</xsl:attribute>
				<xsl:value-of select="from" />
			</xsl:element>
		</td>
		<td width="40%">
			<xsl:element name="div">
				<xsl:attribute name="class">
					<xsl:value-of select="concat('indent', level)" />
				</xsl:attribute>
			<xsl:value-of select="subject" />
			</xsl:element>
		</td>
		<td width="25%">
			<xsl:value-of select="date-string" />
		</td>
		</tr>
	</xsl:for-each>
	</table>
	</body></html>
</xsl:template>
</xsl:stylesheet>
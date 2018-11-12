<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
<xsl:output method="xml" encoding="UTF-8" />
<xsl:template match="/folder-info">
	<html><head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
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
			<xsl:value-of select="subject" />
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
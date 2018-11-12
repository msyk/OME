<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="text" version="1.0" encoding="UTF-8" />

<xsl:variable name="LF"><xsl:value-of select="'&#10;'" /></xsl:variable>

<xsl:template match="h1">
	<xsl:text>┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</xsl:text>
	<xsl:value-of select="$LF" />
	<xsl:text>┃</xsl:text>
	<xsl:value-of select="." />
	<xsl:value-of select="$LF" />
	<xsl:text>┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</xsl:text>
	<xsl:value-of select="$LF" />
</xsl:template>

<xsl:template match="h2">
	<xsl:text>■ </xsl:text>
	<xsl:value-of select="." />
	<xsl:value-of select="$LF" />
	<xsl:text>　━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</xsl:text>
	<xsl:value-of select="$LF" />
</xsl:template>

<xsl:template match="h3">
	<xsl:text>《</xsl:text>
	<xsl:value-of select="." />
	<xsl:text>》</xsl:text>
	<xsl:value-of select="$LF" />
</xsl:template>

<xsl:template match="br">
	<xsl:value-of select="$LF" />
</xsl:template>

<xsl:template match="hr">
	<xsl:text>　　　　　　　　　━━━━━━━━━━━━━━━━━━━━━━</xsl:text>
	<xsl:value-of select="$LF" />
</xsl:template>

<xsl:template match="ol">
	<xsl:variable name="num" select="'0'" />
	<xsl:value-of select="$LF" />
	<xsl:for-each select="li">
		<xsl:variable name="num" select="$num+position()" />
		<xsl:text>&lt;!--OME_SendMail:INDENTING:6:0:</xsl:text>
		<xsl:value-of select="$num" />
		<xsl:text>.:--&gt;</xsl:text>
		<xsl:apply-templates/>
		<xsl:value-of select="$LF" />
	</xsl:for-each>
	<xsl:value-of select="$LF" />
</xsl:template>
<xsl:template match="ul">
	<xsl:value-of select="$LF" />
	<xsl:for-each select="li">
		<xsl:text>&lt;!--OME_SendMail:INDENTING:6:0:◎:--&gt;</xsl:text>
		<xsl:apply-templates/>
		<xsl:value-of select="$LF" />
	</xsl:for-each>
	<xsl:value-of select="$LF" />
</xsl:template>

<xsl:template match="comment()">
	<xsl:text>&lt;!--</xsl:text>
	<xsl:value-of select="." />
	<xsl:text>--&gt;</xsl:text>
</xsl:template>

</xsl:stylesheet>

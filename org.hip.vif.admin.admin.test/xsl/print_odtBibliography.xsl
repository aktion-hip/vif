<?xml version="1.0" encoding="UTF-8"?>
<!-- 	(c) GNU General Public License
    Author: Benno Luthiger, Aktion HIP, Switzerland
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
    xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0">

    <xsl:output method="xml" omit-xml-declaration="yes"/>
    
    <!-- global parameter -->
    <xsl:param name="BibliographyLbl" />
    
    <xsl:template match="/">
        <text:p text:style-name="Standard"><text:span text:style-name="T2"><xsl:value-of select="$BibliographyLbl"/>:</text:span></text:p>
        <xsl:apply-templates select="texts/JoinQuestionToText" />
    </xsl:template>
    
    <xsl:template match="JoinQuestionToText">
        <text:p text:style-name="Text_20_body">[<text:span text:style-name="T2"><xsl:value-of select="propertySet/Reference"/></text:span>]&#160;
        <xsl:value-of select="propertySet/bibliography"/></text:p>
    </xsl:template>
    
</xsl:stylesheet>
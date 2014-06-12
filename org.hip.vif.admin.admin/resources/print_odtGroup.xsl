<?xml version="1.0" encoding="UTF-8"?>
<!-- 	(c) GNU General Public License
    Author: Benno Luthiger, Aktion HIP, Switzerland
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
    xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0">

    <xsl:output method="xml" omit-xml-declaration="yes"/>
    
    <!-- global parameter -->
    <xsl:param name="GroupLbl" />

    <!-- write group's title and description -->
    <xsl:template match="/">
        <text:h text:style-name="Heading_20_2" text:outline-level="3"><xsl:value-of select="$GroupLbl"/>&#32;"<xsl:value-of select="normalize-space(Group/propertySet/Name)"/>"</text:h>      
        <text:p text:style-name="Standard"><xsl:value-of select="normalize-space(Group/propertySet/Description)"/></text:p>
    </xsl:template>

</xsl:stylesheet>
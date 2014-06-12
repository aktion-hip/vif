<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- 	(c) GNU General Public License
	Author: Benno Luthiger, Aktion HIP, Switzerland
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="ISO-8859-1"/>
    
<xsl:param name="styleParameter" select="0" />
    
<xsl:template match="/">
<transformed>
	<xsl:copy-of select="/"/>
    <xsl:choose>
        <xsl:when test="$styleParameter">
            <p>enabled</p>
        </xsl:when>
        <xsl:otherwise>
            <p>disabled</p>
        </xsl:otherwise>
    </xsl:choose>
</transformed>
</xsl:template>

</xsl:stylesheet>

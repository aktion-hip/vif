<?xml version="1.0" encoding="UTF-8"?>
<!-- 	(c) GNU General Public License
    Author: Benno Luthiger, Aktion HIP, Switzerland
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
    xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0">

    <xsl:import href="print_odtInclude.xsl" />
    <xsl:output method="xml" omit-xml-declaration="yes"/>
    
    <!-- global parameter -->
    <xsl:param name="CompletionLbl" />
    <xsl:param name="AuthorLbl" />
    <xsl:param name="ReviewerLbl" />
    
    <xsl:template match="/">
        <xsl:apply-templates select="completion/JoinQuestionToCompletionAndContributors/propertySet/Completion" />
        <xsl:apply-templates select="completion/contributors" />
    </xsl:template>
    
    <xsl:template match="Completion">
        <text:p text:style-name="Standard"><text:span text:style-name="T2"><xsl:value-of select="$CompletionLbl"/>:</text:span></text:p>
        <xsl:call-template name="paragraph" />
    </xsl:template>
    
    <xsl:template match="contributors">
        <text:p text:style-name="Standard">
            <xsl:for-each select="./propertySet">
            <xsl:sort select="./IsAuthor" order="descending" />
                <text:span text:style-name="T2"><xsl:choose>
                    <xsl:when test="./IsAuthor=1">
                        <xsl:value-of select="$AuthorLbl"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$ReviewerLbl"/>
                    </xsl:otherwise>
                </xsl:choose>:</text:span>&#160;<text:span 
                    text:style-name="Standard"><xsl:value-of select="./FullName"/>
                    <xsl:choose>
                        <xsl:when test="position()=last()">&#32;(<xsl:value-of select="//JoinQuestionToCompletionAndContributors/propertySet/Mutation" />)</xsl:when>
                        <xsl:otherwise>,&#32;</xsl:otherwise>
                    </xsl:choose>
                </text:span>
            </xsl:for-each>
        </text:p>
    </xsl:template>
    
</xsl:stylesheet>
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
    <xsl:param name="QuestionLbl" />
    <xsl:param name="RemarkLbl" />
    <xsl:param name="AuthorLbl" />
    <xsl:param name="ReviewerLbl" />
    <xsl:param name="StateLbl" />
    <xsl:param name="StateOpenLbl" />
    <xsl:param name="StateAnsweredRequestedLbl" />
    <xsl:param name="StateAnsweredLbl" />
    
    <xsl:template match="/">
        <text:h text:style-name="Heading_20_3" text:outline-level="3">            
        <xsl:value-of select="$QuestionLbl"/>&#160;<xsl:value-of 
            select="question/JoinQuestionToContributors/propertySet/DecimalID"/>
        </text:h>
        <xsl:apply-templates select="question/JoinQuestionToContributors/propertySet/Question" />
        <xsl:apply-templates select="question/JoinQuestionToContributors/propertySet/Remark" />
        <xsl:apply-templates select="question/JoinQuestionToContributors/propertySet/State" />
        <xsl:apply-templates select="question/contributors" />
        <xsl:apply-templates select="question/texts" />
    </xsl:template>
    
    <xsl:template match="Question">
        <xsl:apply-templates />
    </xsl:template>
    
    <xsl:template match="Remark">
        <text:p text:style-name="Standard"><text:span text:style-name="T2"><xsl:value-of select="$RemarkLbl"/>:</text:span></text:p>
        <xsl:apply-templates />
    </xsl:template>
    
    <xsl:template match="State">
        <text:p text:style-name="Standard">
            <text:span text:style-name="T2"><xsl:value-of select="$StateLbl"/>:</text:span>&#160;<text:span text:style-name="Standard">
                <xsl:choose>
                    <xsl:when test=".=5"><xsl:value-of select="$StateAnsweredRequestedLbl"/></xsl:when>
                    <xsl:when test=".=6"><xsl:value-of select="$StateAnsweredLbl"/></xsl:when>
                    <xsl:otherwise><xsl:value-of select="$StateOpenLbl"/></xsl:otherwise>
                </xsl:choose>
            </text:span>
        </text:p>
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
                        <xsl:when test="position()=last()">&#32;(<xsl:value-of select="//JoinQuestionToContributors/propertySet/Mutation" />)</xsl:when>
                        <xsl:otherwise>,&#32;</xsl:otherwise>
                    </xsl:choose>
                </text:span>
            </xsl:for-each>
        </text:p>
    </xsl:template>
    
    <xsl:template match="texts">
    </xsl:template>

</xsl:stylesheet>
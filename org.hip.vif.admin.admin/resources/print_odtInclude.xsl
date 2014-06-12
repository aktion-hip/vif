<?xml version="1.0" encoding="UTF-8"?>
<!-- 	(c) GNU General Public License
    Author: Benno Luthiger, Aktion HIP, Switzerland
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
    xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
    xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
    xmlns:xlink="http://www.w3.org/1999/xlink">
    
    <!-- format line break -->
    <xsl:template match="br">
        <text:line-break/>
    </xsl:template>
    
    <!-- format inline styles: bold, italic, underline -->
    <xsl:template match="span[contains(@style, 'font-weight: bold')]">
        <text:span text:style-name="T1">
            <xsl:apply-templates />
        </text:span>        
    </xsl:template>
    <xsl:template match="span[contains(@style, 'font-style: italic')]">
        <text:span text:style-name="T2">
            <xsl:apply-templates />
        </text:span>        
    </xsl:template>
    <xsl:template match="span[contains(@style, 'text-decoration: underline')]">
        <text:span text:style-name="T3">
            <xsl:apply-templates />
        </text:span>        
    </xsl:template>
    
    <!-- sub, super -->
    <xsl:template match="sub">
        <text:span text:style-name="T4">
            <xsl:apply-templates />
        </text:span>
    </xsl:template>
    <xsl:template match="sup">
        <text:span text:style-name="T5">
            <xsl:apply-templates />
        </text:span>        
    </xsl:template>

    <!-- format a list: ul, ol -->
    <xsl:template match="ul | ol">
        <text:list>
            <xsl:if test="not((ancestor::ul) | (ancestor::ol))">
                <xsl:attribute name="text:style-name">
                    <xsl:choose>
                        <xsl:when test="self::ol">L2</xsl:when>
                        <xsl:otherwise>L1</xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
            </xsl:if>
            <xsl:for-each select="li">
                <text:list-item>
                    <xsl:choose>
                        <xsl:when test="child::ul | child::ol">
                            <xsl:apply-templates select="ul | ol" />
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:call-template name="para" />
                        </xsl:otherwise>
                    </xsl:choose>
                </text:list-item>
            </xsl:for-each>
        </text:list>
    </xsl:template>

    <!-- format table -->
    <xsl:template match="table">
        <table:table table:name="Tabelle1" table:style-name="Tabelle1">
            <xsl:for-each select="child::tr[1]/th | child::tr[1]/td">
                <table:table-column table:style-name="Tabelle1.A"/>
            </xsl:for-each>
            <xsl:apply-templates />
        </table:table>
    </xsl:template>
    <xsl:template match="tr">
        <table:table-row>
            <xsl:apply-templates />
        </table:table-row>
    </xsl:template>
    <xsl:template match="td">
        <table:table-cell office:value-type="string">
            <xsl:choose>
                <xsl:when test="child::p">
                    <xsl:call-template name="para" />
                </xsl:when>
                <xsl:when test="child::ul | child::ol">
                    <xsl:apply-templates select="ul | ol" />
                </xsl:when>
                <xsl:otherwise>
                    <text:p text:style-name="Table_20_Contents"><xsl:apply-templates /></text:p>
                </xsl:otherwise>
            </xsl:choose>
        </table:table-cell>
    </xsl:template>
    <xsl:template match="th">
        <table:table-cell office:value-type="string">
            <xsl:choose>
                <xsl:when test="child::p">
                    <xsl:call-template name="para" />
                </xsl:when>
                <xsl:when test="child::ul | child::ol">
                    <xsl:apply-templates select="ul | ol" />
                </xsl:when>
                <xsl:otherwise>
                    <text:p text:style-name="Table_20_Heading"><xsl:apply-templates /></text:p>
                </xsl:otherwise>
            </xsl:choose>
        </table:table-cell>
    </xsl:template>
    
    <!-- format link -->
    <xsl:template match="a">
        <text:a xlink:type="simple" xlink:href="{@href}">
            <text:span text:style-name="Standard">
                <xsl:apply-templates />
            </text:span>
        </text:a>
    </xsl:template>
    
    <xsl:template name="para">
        <text:p>
            <xsl:attribute name="text:style-name">
                <xsl:choose>
                    <xsl:when test="ancestor::ol">
                        <xsl:choose>
                            <xsl:when test="following-sibling::li">P7</xsl:when>
                            <xsl:otherwise>P5</xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:choose>
                            <xsl:when test="following-sibling::li">P6</xsl:when>
                            <xsl:otherwise>P4</xsl:otherwise>
                        </xsl:choose>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:apply-templates />
        </text:p>        
    </xsl:template>
    
    <!-- format a paragraph -->
    <xsl:template match="p | div">
        <text:p text:style-name="Text_20_body">
        <xsl:choose>
            <xsl:when test="@style[contains(., 'text-align: center')]">
                <xsl:attribute name="text:style-name">P8</xsl:attribute>
                <xsl:apply-templates />
            </xsl:when>
            <xsl:when test="@style[contains(., 'text-align: right')]">
                <xsl:attribute name="text:style-name">P9</xsl:attribute>
                <xsl:apply-templates />
            </xsl:when>
            <xsl:otherwise>
                <xsl:attribute name="text:style-name">Text_20_body</xsl:attribute>
                <xsl:apply-templates />
            </xsl:otherwise>
        </xsl:choose>                
        </text:p>
    </xsl:template>

</xsl:stylesheet>
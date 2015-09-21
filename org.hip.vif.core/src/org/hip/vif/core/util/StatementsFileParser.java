/*
	This package is part of the application VIF.
	Copyright (C) 2008-2012, Benno Luthiger

	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package org.hip.vif.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.service.PreferencesHandler;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/** SAX parser for the XML containing the application's SQL statements to create the data structure.
 *
 * @author Luthiger */
public class StatementsFileParser extends DefaultHandler {
    private static final Logger LOG = LoggerFactory.getLogger(StatementsFileParser.class);

    private final static String NODE_NAME = "statement";
    private final static String DB_DERBY = "vif_derby.xml";
    private final static String DB_MYSQL = "vif_mysql.xml";

    private Vector<String> statements;
    private boolean parsingStatement;
    private StringBuilder actualStatement;

    /** Returns the SQL create statements by parsing the XML.
     *
     * @return Collection<String>
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException */
    public Collection<String> getStatements() throws IOException, SAXException, ParserConfigurationException {
        final InputStream lInput = getXML();
        try {
            final InputSource lSource = new InputSource(lInput);
            final XMLReader lParser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            lParser.setContentHandler(this);
            lParser.parse(lSource);
        } finally {
            lInput.close();
        }
        return statements;
    }

    protected InputStream getXML() throws IOException {
        final Bundle bundle = FrameworkUtil.getBundle(StatementsFileParser.class);
        if (bundle == null) {
            throw new IOException("Unable to find bundle of " + StatementsFileParser.class.getName());
        }
        final URL url = bundle
                .getEntry(String.format("%s/%s", ApplicationConstants.LOCAL_RESOURCES_DIR, getStatementsXML()));
        if (url == null) {
            throw new IOException("Unable to find find resource "
                    + String.format("%s/%s", ApplicationConstants.LOCAL_RESOURCES_DIR, getStatementsXML()));
        }
        return url.openStream();
    }

    private String getStatementsXML() {
        try {
            if (PreferencesHandler.INSTANCE.isDerbyDB()) {
                return DB_DERBY;
            }
        } catch (final IOException exc) {
            LOG.error("Error encounteres while checking the DB type!", exc);
        }
        return DB_MYSQL;
    }

    @Override
    public void startDocument() throws SAXException {
        statements = new Vector<String>();
        parsingStatement = false;
    }

    @Override
    public void startElement(final String inUri, final String inLocalName, final String inName,
            final Attributes inAttributes) throws SAXException {
        if (NODE_NAME.equals(inName)) {
            parsingStatement = true;
            actualStatement = new StringBuilder();
        }
    }

    @Override
    public void endElement(final String inUri, final String inLocalName, final String inName) throws SAXException {
        if (NODE_NAME.equals(inName)) {
            statements.add(new String(actualStatement));
            parsingStatement = false;
        }
    }

    @Override
    public void characters(final char[] inChars, final int inStart, final int inLength) throws SAXException {
        if (!parsingStatement) {
            return;
        }

        actualStatement.append(String.copyValueOf(inChars, inStart, inLength));
    }

}

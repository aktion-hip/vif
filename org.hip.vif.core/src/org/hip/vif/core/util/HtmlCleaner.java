/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.Tidy;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Helper class for cleaning up html input in a rich text field.
 * 
 * @author Luthiger
 * Created: 26.07.2011
 */
public class HtmlCleaner {
	private static final Logger LOG = LoggerFactory.getLogger(HtmlCleaner.class);
	
	private static final String NL = System.getProperty("line.separator");

	private static final String TAG_ROOT = "root";
	private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><%s>%s</%s>";
	private static final Pattern LINE_BREAKS = Pattern.compile("(\\r?\\n)+");
	private static final Pattern TRAILING_SPACE = Pattern.compile("(<br\\s*/*>)+\\z");
	private static final String HTML = "<html><head><title></title></head><body>%s</body></html>";

	/**
	 * Converts rich text field input to proper XHTML body, all surrounding white space removed.
	 * 
	 * @param inHTML String the rich text field input
	 * @return String the cleaned HTML
	 */
	public static String cleanUp(String inHTML) {
		return makeTidy(TRAILING_SPACE.matcher(inHTML).replaceAll("").trim());		
	}
	
	private static String makeTidy(String inHTML) {
		StringWriter out = new StringWriter();
		Tidy lTidy = new Tidy();
		lTidy.setPrintBodyOnly(true);
		lTidy.setXmlOut(true);
		lTidy.setQuiet(true);
		lTidy.setSpaces(0);
		lTidy.setShowWarnings(false);
		lTidy.parse(new StringReader(String.format(HTML, inHTML)), out);
		return out.toString();
	}
	
	/**
	 * Converts XHTML to plain text.
	 * 
	 * @param inHTML String 
	 * @return String all XHTML tags removed
	 */
	public static String toPlain(String inHTML) {
		StringReader lReader = new StringReader(String.format(XML, TAG_ROOT, LINE_BREAKS.matcher(makeTidy(inHTML)).replaceAll(""), TAG_ROOT));
		InputSource lInputSource = new InputSource(lReader);

		Parser lParser;
		try {
			lParser = new Parser();
			lParser.parse(lInputSource);
			return lParser.getPlainText();
		} catch (SAXException exc) {
			LOG.error("SAX parser error", exc);
		} catch (ParserConfigurationException exc) {
			LOG.error("SAX parser error", exc);
		} catch (IOException exc) {
			LOG.error("SAX parser error", exc);
		}
		return inHTML;
	}
	
// --- inner classes ---
	
	private static class Parser extends DefaultHandler {
		private static final List<String> NEWLINES = Arrays.asList("p", "br");
		
		private XMLReader parser;
		private StringBuilder plain;

		Parser() throws SAXException, ParserConfigurationException {
			parser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			parser.setContentHandler(this);
			parser.setErrorHandler(this);
		}

		void parse(InputSource inInputSource) throws IOException, SAXException {
			parser.parse(inInputSource);
		}
		
		String getPlainText() {
			return new String(plain);
		}
		
		@Override
		public void startDocument() throws SAXException {
			plain = new StringBuilder();
		}
		
		@Override
		public void characters(char[] inChars, int inStart, int inLength) throws SAXException {
			plain.append(new String(inChars, inStart, inLength));
		}
		
		@Override
		public void endElement(String inUri, String inLocalName, String inQName) throws SAXException {
			if (NEWLINES.contains(inQName.toLowerCase())) {
				plain.append(NL);
			}
		}
	}

}

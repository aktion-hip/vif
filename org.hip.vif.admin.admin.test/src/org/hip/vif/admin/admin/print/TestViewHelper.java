/*
	This package is part of the application VIF.
	Copyright (C) 2010, Benno Luthiger

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

package org.hip.vif.admin.admin.print;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.hip.kernel.servlet.ISourceCreatorStrategy;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Helper class providing functionality to test views, i.e. classes extending <code>AbstractVIFView</code>.
 * 
 * @author Luthiger
 * Created: 19.07.2010
 */
public class TestViewHelper {
	private static final String TMPL_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><doc>%s</doc>";
//	private static final String TMPL_HTML = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">%s<html>%s<head><title>VIF</title></head>%s<body>%s</body>%s</html>";
//	private static final String NL = System.getProperty("line.separator");

	/**
	 * The view has to override <code>getSourceStrategy()</code> and can delegate to this method:
	 * <pre> @Override
	 * protected ISourceCreatorStrategy getSourceStrategy() {
	 *	return TestViewHelper.getSourceStrategy();
	 * }</pre>
	 * 
	 * @return {@link ISourceCreatorStrategy}
	 */
	public static ISourceCreatorStrategy getSourceStrategy() {
		return new ISourceCreatorStrategy() {
			public String getResourceId() {
				return "0";
			}
			public Source createSource() throws IOException {
				return null;
			}
		};
	}
	
	/**
	 * Return a new XPath using the underlying object model determined when the XPathFactory was instantiated.
	 * 
	 * @return {@link XPath}
	 */
	public static XPath getXPath() {
		return XPathFactory.newInstance().newXPath();
	}

	/**
	 * Returns the specified xml string <code>dom.Document</code>.
	 * 
	 * @param inXML String
	 * @param inNameSpaceAware 
	 * @return {@link Document}
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public static Document getXML(String inXML, boolean inNameSpaceAware) throws SAXException, IOException, ParserConfigurationException {
		StringReader lReader = new StringReader(String.format(TMPL_XML, inXML));
		DocumentBuilderFactory lBuilder = DocumentBuilderFactory.newInstance();
		lBuilder.setNamespaceAware(inNameSpaceAware);
		return lBuilder.newDocumentBuilder().parse(new InputSource(lReader));
	}
	
}

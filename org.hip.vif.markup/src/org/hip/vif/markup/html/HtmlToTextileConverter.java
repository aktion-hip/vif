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

package org.hip.vif.markup.html;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParserFactory;

import org.hip.kernel.exc.DefaultExceptionWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.Tidy;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Luthiger
 * Created: 14.07.2011
 */
public class HtmlToTextileConverter extends DefaultHandler {
	private static final Logger LOG = LoggerFactory.getLogger(HtmlToTextileConverter.class);
	
	private static final String TAG_ROOT = "root";
	private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><%s>%s</%s>";
	private static final String HTML = "<html><head><title></title></head><body>%s</body></html>";
	private static final Pattern WHITE_SPACE = Pattern.compile("\\s+");
	private static final Pattern MULTI_LINE = Pattern.compile("(\\r?\\n){3,}+");
	private static final String NL = System.getProperty("line.separator");
	private static final String NL_TAG = NL+"<";
	private static final IHandlerFactory DFT_FACTORY = new QuickTagFactory("dft", "");
	
	private XMLReader parser;
	private String converted;
	private Stack<ITagHandler> handlerStack;
	private ITagHandler actHandler;
	private static String listPrefix = "";
	
	private static Map<String, IHandlerFactory> tagFactories = new HashMap<String, HtmlToTextileConverter.IHandlerFactory>();
	
	static {
		tagFactories.put("b", new QuickTagFactory("b", "*"));
		tagFactories.put("strong", new QuickTagFactory("strong", "*"));
		tagFactories.put("i", new QuickTagFactory("i", "_"));
		tagFactories.put("em", new QuickTagFactory("em", "_"));
		tagFactories.put("cite", new QuickTagFactory("cite", "??"));
		tagFactories.put("s", new QuickTagFactory("s", "-"));
		tagFactories.put("del", new QuickTagFactory("del", "- "));
		tagFactories.put("ins", new QuickTagFactory("ins", "+ "));
		tagFactories.put("sup", new QuickTagFactory("sup", "^"));
		tagFactories.put("sub", new QuickTagFactory("sub", "~"));
		tagFactories.put("code", new QuickTagFactory("code", "@"));
		
		tagFactories.put("h1", new BlockFactory("h1", "h1"));
		tagFactories.put("h2", new BlockFactory("h2", "h2"));
		tagFactories.put("h3", new BlockFactory("h3", "h3"));
		tagFactories.put("h4", new BlockFactory("h4", "h4"));
		tagFactories.put("h5", new BlockFactory("h5", "h5"));
		tagFactories.put("h6", new BlockFactory("h6", "h6"));
		tagFactories.put("h7", new BlockFactory("h7", "h7"));
		tagFactories.put("blockquote", new BlockFactory("blockquote", "bq"));
		
		tagFactories.put("ol", new ListFactory("ol", "#"));
		tagFactories.put("ul", new ListFactory("ul", "*"));
		tagFactories.put("li", new ListElementFactory());

		tagFactories.put("br", new EndTagFactory(NL));
		tagFactories.put("td", new EndTagFactory("|"));
		tagFactories.put("tr", new TableHandlerFactory());
		
		tagFactories.put("a", new LinkHandlerFactory());
		tagFactories.put("img", new ImageHandlerFactory());
		
		tagFactories.put("div", new DivFactory());
		tagFactories.put("p", new DivFactory());
		tagFactories.put("span", new SpanFactory());
	}

	public String convertToTextile(String inHTML) {
		StringReader lReader = new StringReader(String.format(XML, TAG_ROOT, normalizeSpace(makeTidy(inHTML)), TAG_ROOT));
		InputSource lInputSource = new InputSource(lReader);
		try {
			parser().parse(lInputSource);
		} catch (IOException exc) {
			LOG.error("SAX parser error", exc);
		} catch (SAXException exc) {
			LOG.error("SAX parser error", exc);
		}
		return converted;
	}
	
	private String makeTidy(String inHTML) {
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
	
	private String normalizeSpace(String inText) {
		return WHITE_SPACE.matcher(inText.replace(NL_TAG, "<")).replaceAll(" ");
	}
	
	private String normalizeNL(String inText) {
		String out = MULTI_LINE.matcher(inText).replaceAll(NL+NL);
		return out.startsWith(NL) ? out.substring(NL.length()) : out;
	}

	private XMLReader parser() {
		if (parser == null) {
			try { 
				parser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
				parser.setContentHandler(this);
				parser.setErrorHandler(this);
			} 
			catch (Exception exc) {
				DefaultExceptionWriter.printOut(this, exc, true);
			}	
	
		}
		return parser;
	}
	
// --- SAX parser methods ---
	
	@Override
	public void startDocument() throws SAXException {
		listPrefix = "";
		actHandler = DFT_FACTORY.createHandler(null);
		handlerStack = new Stack<ITagHandler>();
	}
	
	@Override
	public void endDocument() throws SAXException {
		converted = normalizeNL(new String(actHandler.endTag())).trim();
	}
	
	@Override
	public void startElement(String inUri, String inLocalName, String inQName, Attributes inAttributes) throws SAXException {
		//push the actual handler on the stack
		handlerStack.push(actHandler);
		
		//initialize new actual handler
		IHandlerFactory lFactory = tagFactories.get(inQName.toLowerCase());
		actHandler = lFactory == null ? DFT_FACTORY.createHandler(inAttributes) : lFactory.createHandler(inAttributes);
	}
	
	@Override
	public void endElement(String inUri, String inLocalName, String inQName) throws SAXException {
		StringBuilder lPart = actHandler.endTag();
		
		//initialize actual handler with top handler on stack
		actHandler = handlerStack.pop();
		actHandler.append(new String(lPart));
	}
	
	@Override
	public void characters(char[] inChars, int inStart, int inLength) throws SAXException {
		actHandler.append(new String(inChars, inStart, inLength));
	}
	
// --- private classes ---
	
	private static interface ITagHandler {
		StringBuilder endTag();
		void append(String inText);
	}
	
	private static abstract class TagHandler {
		protected StringBuilder content;
		public void append(String inText) {
			content.append(inText);
		}	
	}
	
	private static class QuickTag extends TagHandler implements ITagHandler {
		private String wrapChar;
		private String tag;
		
		QuickTag(String inTag, String inWrapChar) {
			tag = inTag;
			wrapChar = inWrapChar;
			content = new StringBuilder(wrapChar.trim());
		}
		public StringBuilder endTag() {
			content.append(wrapChar);
			return content;
		}
		@Override
		public String toString() {
			return String.format("QuickTag[%s]", tag);
		}
	}
	
	private static class Block extends TagHandler implements ITagHandler {
		private String tag;
		private String htmlTag;

		Block(String inHtmlTag, String inTag) {
			htmlTag = inHtmlTag;
			tag = String.format("%s. ", inTag);
			content = new StringBuilder(NL).append(tag);
		}
		public StringBuilder endTag() {
			content.append(NL).append(NL);
			return content;
		}
		public String toString() {
			return String.format("Block[%s]", htmlTag);
		}
	}
	
	private static class List extends TagHandler implements ITagHandler {
		private String tag;
		List(String inTag) {
			tag = inTag;
			content = new StringBuilder(listPrefix.length() == 1 ? "" : NL);
		}
		public StringBuilder endTag() {
			listPrefix = listPrefix.substring(0, listPrefix.length()-1);
			if (listPrefix.length() == 0) return content;
			String outContent = new String(content).substring(0, content.length() - NL.length());
			return new StringBuilder(outContent);
		}
		public String toString() {
			return String.format("List[%s]", tag);
		}
	}
	
	private static class ListElement extends TagHandler implements ITagHandler {
		ListElement() {
			content = new StringBuilder(String.format("%s ", listPrefix));
		}
		public StringBuilder endTag() {
			content.append(NL);
			return content;
		}
		@Override
		public String toString() {
			return "li";
		}
	}
	
	private static class EndTagger extends TagHandler implements ITagHandler {
		private String endTag;
		EndTagger(String inEndTag) {
			endTag = inEndTag;
			content = new StringBuilder();
		}
		@Override
		public StringBuilder endTag() {
			content.append(endTag);
			return content;
		}
		
	}
	
	private static class TableHandler extends TagHandler implements ITagHandler {
		TableHandler() {
			content = new StringBuilder("|");
		}
		@Override
		public StringBuilder endTag() {
			content.append(NL);
			return content;
		}
	}
	
	private static class LinkHandler extends TagHandler implements ITagHandler {
		private String href;
		LinkHandler(String inHref) {
			href = inHref;
			content = new StringBuilder(" \"");			
		}
		@Override
		public StringBuilder endTag() {
			content.append("\":").append(href).append(" ");
			return content;
		}
	}
	
	private static class ImageHandler extends TagHandler implements ITagHandler {
		ImageHandler(String inSrc) {
			content = new StringBuilder(" !").append(inSrc).append("! ");			
		}
		@Override
		public StringBuilder endTag() {
			return content;
		}
		
	}
	
	
//	--- interfaces ---
	
	private static interface IHandlerFactory {
		ITagHandler createHandler(Attributes inAttributes);
	}
	
	private static class QuickTagFactory implements IHandlerFactory {
		private String tag;
		private String wrapChar;
		QuickTagFactory(String inTag, String inWrapChar) {
			tag = inTag;
			wrapChar = inWrapChar;
		}
		public ITagHandler createHandler(Attributes inAttributes) {
			return new QuickTag(tag, wrapChar); 
		}
	}
	
	private static class BlockFactory implements IHandlerFactory {
		private String htmlTag;
		private String tag;
		BlockFactory(String inHtmlTag, String inTag) {
			htmlTag = inHtmlTag;
			tag = inTag;
		}
		public ITagHandler createHandler(Attributes inAttributes) {
			return new Block(htmlTag, tag); 
		}
	}
	
	private static class ListFactory implements IHandlerFactory {
		private String prefixSymbol;
		private String tag;
		ListFactory(String inTag, String inPrefixSymbol) {
			tag = inTag;
			prefixSymbol = inPrefixSymbol;
		}
		public ITagHandler createHandler(Attributes inAttributes) {
			listPrefix += prefixSymbol;
			return new List(tag);
		}
	}
	
	private static class ListElementFactory implements IHandlerFactory {
		public ITagHandler createHandler(Attributes inAttributes) {
			return new ListElement();
		}		
	}
	
	private static class EndTagFactory implements IHandlerFactory {
		private String endTag;
		EndTagFactory(String inEndTag) {
			endTag = inEndTag;
		}
		public ITagHandler createHandler(Attributes inAttributes) {
			return new EndTagger(endTag);
		}
	}
	
	private static class TableHandlerFactory implements IHandlerFactory {
		public ITagHandler createHandler(Attributes inAttributes) {
			return new TableHandler();
		}
	}
	
	private static class LinkHandlerFactory implements IHandlerFactory {
		public ITagHandler createHandler(Attributes inAttributes) {
			return new LinkHandler(inAttributes.getValue("href"));
		}
		
	}

	private static class ImageHandlerFactory implements IHandlerFactory {
		public ITagHandler createHandler(Attributes inAttributes) {
			return new ImageHandler(inAttributes.getValue("src"));
		}
	}
	
	private static class DivFactory implements IHandlerFactory {
		public ITagHandler createHandler(Attributes inAttributes) {
			String lStyle = inAttributes.getValue("style");
			if (lStyle == null) return new Block("normal", "p");
			if (lStyle.contains("text-align:")) {
				if (lStyle.contains("center;")) {
					return new Block("center", "p=");
				}
				else if (lStyle.contains("right;")) {
					return new Block("right", "p>");					
				}
				else {
					return new Block("left", "p<");
				}
			}
			else if (lStyle.contains("margin-left:")) {
				if (lStyle.contains("40px;")) {					
					return new Block("left", "p(");
				}
				return new Block("left", "p((");
			}
			else if (lStyle.contains("padding-left:")) {
				if (lStyle.contains("1em;")) {					
					return new Block("left", "p(");
				}
				return new Block("left", "p((");
			}
			return new QuickTag("dft", "");
		}
	}
	
	private static class SpanFactory implements IHandlerFactory {
		public ITagHandler createHandler(Attributes inAttributes) {
			String lStyle = inAttributes.getValue("style");
			if (lStyle == null) return new QuickTag("dft", "");
			if (lStyle.contains("font-style:")) {
				if (lStyle.contains("italic;")) {
					return new QuickTag("i", "_"); 
				}
			}
			else if (lStyle.contains("font-weight:")) {
				if (lStyle.contains("bold;")) {
					return new QuickTag("strong", "*"); 
				}				
			}
			else if (lStyle.contains("text-decoration:")) {
				if (lStyle.contains("underline;")) {
					return new QuickTag("ins", "+ "); 
				}
				else if (lStyle.contains("line-through;")) {
					return new QuickTag("del", "- ");					
				}
			}
			return new QuickTag("dft", "");
		}
		
	}
	
}

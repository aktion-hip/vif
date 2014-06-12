package org.hip.kernel.stext;

/*
	This package is part of the structured text framework used for the application VIF.
	Copyright (C) 2003, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

import java.util.Collection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hip.kernel.sys.VObject;

/**
 * This class converts inline formats according StructuredText rules
 * to HTML code.
 * 
 * @author: Benno Luthiger
 */
public class InlineStructuredText2HTML {
	private static InlineStructuredText2HTML singleton = null;

	private static Collection<StructureFactory> cFactories = new Vector<StructureFactory>();
	
	static {
		singleton = new InlineStructuredText2HTML();
		cFactories.add(singleton.new FactoryEmphasized());
		cFactories.add(singleton.new FactoryStrong());
		cFactories.add(singleton.new FactoryUnderlined());
		cFactories.add(singleton.new FactoryLiteral());
	}

	//--------- start inner classes -------------
	//--------- inner class StructureFactory
	
	/**
	 * Interface for StructuredText factory classes
	 */
	public interface StructureFactory {
		/**
		 * Returns the Pattern of this factory.
		 * 
		 * @return Pattern
		 */
		Pattern getPattern();
		
		/**
		 * Returns the replacement of structured text formats with plain HTML.
		 * 
		 * @param inMatcher Matcher
		 * @return java.lang.String
		 */
		String createHTMLReplacement(Matcher inMatcher);
		
		/**
		 * Returns the replacement of structured text formats with escaped HTML.
		 * 
		 * @param inMatcher Matcher
		 * @return java.lang.String
		 */
		String createEscapedHTMLReplacement(Matcher inMatcher);
		
		/**
		 * Returns the replacement ignoring structured text formats.
		 * 
		 * @param inMatcher Matcher
		 * @return java.lang.String
		 */
		String createIgnoringReplacement(Matcher inMatcher);
	}
	
	/**
	 * Generic funtionality for StructuredText factory classes
	 */
	private abstract class AbstractStructureFactory extends VObject {
		private static final String TAG_START_NORMAL 	= "<";
		private static final String TAG_END_NORMAL 		= ">";
		private static final String TAG_START_ESCAPED 	= "&lt;";
		private static final String TAG_END_ESCAPED 		= "&gt;";

		public AbstractStructureFactory() {
			super();
		}
		
		abstract protected String getTagContent();
		
		public String createHTMLReplacement(Matcher inMatcher) {
			StringBuffer outReplacement = new StringBuffer();
			outReplacement.append(inMatcher.group(1)).append(TAG_START_NORMAL).append(getTagContent()).append(TAG_END_NORMAL).append(inMatcher.group(2)).append(TAG_START_NORMAL).append("/").append(getTagContent()).append(TAG_END_NORMAL).append(inMatcher.group(3));
			return new String(outReplacement);
		}
		
		public String createEscapedHTMLReplacement(Matcher inMatcher) {
			StringBuffer outReplacement = new StringBuffer();
			outReplacement.append(inMatcher.group(1)).append(TAG_START_ESCAPED).append(getTagContent()).append(TAG_END_ESCAPED).append(inMatcher.group(2)).append(TAG_START_ESCAPED).append("/").append(getTagContent()).append(TAG_END_ESCAPED).append(inMatcher.group(3));
			return new String(outReplacement);
		}
		
		public String createIgnoringReplacement(Matcher inMatcher) {
			StringBuffer outReplacement = new StringBuffer();
			outReplacement.append(inMatcher.group(1)).append(inMatcher.group(2)).append(inMatcher.group(3));
			return new String(outReplacement);
		}
	}

	/**
	 * Factory class for emphasized inline StructuredText format.
	 */
	private class FactoryEmphasized extends AbstractStructureFactory implements StructureFactory {
		private final static String TAG_CONTENT = "em";
		Pattern pattern = Pattern.compile("(^|[\\s\\(\\[\\{])\\*\\b([\\p{L}\\p{Digit}\\p{Punct}\\s]+?)\\*(\\s|\\p{Punct}|$)");
		
		public FactoryEmphasized() {
			super();
		}
		
		public Pattern getPattern() {
			return pattern;
		}
		
		protected String getTagContent() {
			return TAG_CONTENT;
		}
	}

	/**
	 * Factory class for strong inline StructuredText format.
	 */
	private class FactoryStrong extends AbstractStructureFactory implements StructureFactory {
		private final static String TAG_CONTENT = "strong";
		Pattern pattern = Pattern.compile("(^|[\\s\\(\\[\\{])\\*\\*\\b([\\p{L}\\p{Digit}\\p{Punct}\\s&&[^\\*]]+?)\\*\\*(\\s|\\p{Punct}|$)");
		
		public FactoryStrong() {
			super();
		}
		
		public Pattern getPattern() {
			return pattern;
		}
		
		protected String getTagContent() {
			return TAG_CONTENT;
		}
	}

	/**
	 * Factory class for underlined inline StructuredText format.
	 */
	private class FactoryUnderlined extends AbstractStructureFactory implements StructureFactory {
		private final static String TAG_CONTENT = "u";
		Pattern pattern = Pattern.compile("(^|[\\s\\(\\[\\{])_([\\p{L}\\p{Digit}\\p{Punct}\\s&&[^_<>]]+?)_(\\s|\\p{Punct}|$)");
		
		public FactoryUnderlined() {
			super();
		}
		
		public Pattern getPattern() {
			return pattern;
		}
		
		protected String getTagContent() {
			return TAG_CONTENT;
		}
	}

	/**
	 * Factory class for literal inline StructuredText format.
	 */
	private class FactoryLiteral extends AbstractStructureFactory implements StructureFactory {
		private final static String TAG_CONTENT = "code";
		Pattern pattern = Pattern.compile("(\\W+|^)'([\\p{L}\\p{Digit}\\p{Punct}\\s]+?)'(\\s|\\p{Punct}|$)");
		
		public FactoryLiteral() {
			super();
		}
		
		public Pattern getPattern() {
			return pattern;
		}
		
		protected String getTagContent() {
			return TAG_CONTENT;
		}
	}
	
	//--------- inner class StructuredText converters
	
	/**
	 * Generic functionality for StructuredText converters
	 */
	private abstract class AbstractConverter extends VObject {
		public AbstractConverter() {
			super();
		}

		abstract protected String getReplacement(StructureFactory inFactory, Matcher inMatcher);
		
		public StringBuffer convert(String inStructuredTextString) {
			StringBuffer outHtml = new StringBuffer(inStructuredTextString);
			for (StructureFactory lFactory : cFactories) {
				Matcher lMatcher = lFactory.getPattern().matcher(outHtml);
				StringBuffer lReplaced = new StringBuffer();
				boolean lFound = lMatcher.find();
				while (lFound) {
					lMatcher.appendReplacement(lReplaced, getReplacement(lFactory, lMatcher));
					lFound = lMatcher.find();
				}
				lMatcher.appendTail(lReplaced);
				outHtml = lReplaced;
			}
			return outHtml;
		}
	}
	
	/**
	 * Inner class to convert text inline formated to plain HTML code.
	 */
	private class Converter2HTML extends AbstractConverter {
		public Converter2HTML() {
			super();
		}
		
		protected String getReplacement(StructureFactory inFactory, Matcher inMatcher) {
			return inFactory.createHTMLReplacement(inMatcher);
		}
	}
	
	/**
	 * Inner class to convert text inline formated to escaped HTML code.
	 */
	private class Converter2EscapedHTML extends AbstractConverter {
		public Converter2EscapedHTML() {
			super();
		}
		
		protected String getReplacement(StructureFactory inFactory, Matcher inMatcher) {
			return inFactory.createEscapedHTMLReplacement(inMatcher);
		}
	}
	
	/**
	 * Inner class to ignore the inline formates in the text.
	 */
	private class ConverterIgnoring extends AbstractConverter {
		public ConverterIgnoring() {
			super();
		}
		
		protected String getReplacement(StructureFactory inFactory, Matcher inMatcher) {
			return inFactory.createIgnoringReplacement(inMatcher);
		}
	}
	//--------- end inner classes -------------

	/**
	 * Singleton constructor for InlineStructuredText2HTML.
	 */
	private InlineStructuredText2HTML() {
		super();
	}

	/**
	 * Returns singleton instance of inline format converter.
	 * 
	 * @return InlineStructuredText2HTML
	 */
	public static InlineStructuredText2HTML getSingleton() {
		if (singleton == null)
			 singleton = new InlineStructuredText2HTML();
		
		return singleton;
	}
	
	/**
	 * Returns the specified text with inline format converted to HTML.
	 * 
	 * @param inStructuredTextString java.lang.String
	 * @return java.lang.StringBuffer
	 */
	public StringBuffer convertToHTML(String inStructuredTextString) {
		return (new Converter2HTML()).convert(inStructuredTextString);
	}
	
	/**
	 * Returns the specified text with inline format converted to escaped HTML.
	 * 
	 * @param inStructuredTextString java.lang.String
	 * @return java.lang.StringBuffer
	 */
	public StringBuffer convertToEscapedHTML(String inStructuredTextString) {
		return (new Converter2EscapedHTML()).convert(inStructuredTextString);
	}
	
	/**
	 * Returns the specified text with inline format ignored.
	 * 
	 * @param inStructuredTextString java.lang.String
	 * @return java.lang.StringBuffer
	 */
	public StringBuffer convertIgnoring(String inStructuredTextString) {
		return (new ConverterIgnoring()).convert(inStructuredTextString);
	}
}

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
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hip.kernel.sys.VObject;


/**
 * 
 * @author: Benno Luthiger
 */
public class StructuredText extends VObject {
	//class variables
	private static StructuredText cSingleton = null;
	private static Collection<StructuredParagraphFactory> cFactories = new Vector<StructuredParagraphFactory>();
	
	//instance variables
	private Collection<StructuredTextParagraph> paragraphs = null;
	
	static {
		cSingleton = new StructuredText(new String[] {});
		cFactories.add(cSingleton.new StructuredTextBulletFactory());
		cFactories.add(cSingleton.new StructuredTextNumberedFactory());
		cFactories.add(cSingleton.new StructuredTextIndentedFactory());
	}
	
	private interface StructuredParagraphFactory {
		StructuredTextParagraph createStructuredParagraph(String inRawParagraph);
		boolean matches(String inRawParagraph);
		int getSubstringStart();
	}
	
	private abstract class AbstractStructuredParagraphFactory {
		int startSubstring = 0;
		
		public AbstractStructuredParagraphFactory() {
			super();
		}
		
		abstract Pattern getPattern();
		
		public boolean matches(String inRawParagraph) {
			Matcher lMatcher = getPattern().matcher(inRawParagraph);
			boolean outFind = lMatcher.find();
			if (outFind) startSubstring = lMatcher.end();
			return outFind;
		}
		
		public int getSubstringStart() {
			return startSubstring;
		}
	}
	
	private class StructuredTextBulletFactory extends AbstractStructuredParagraphFactory implements StructuredParagraphFactory {
		private Pattern pattern = Pattern.compile("^\\s*[-*o]\\s+");

		public StructuredTextBulletFactory() {
			super();
		}
		public StructuredTextParagraph createStructuredParagraph(String inRawParagraph) {
			return new StructuredTextBullet(inRawParagraph);
		}
		protected Pattern getPattern() {
			return pattern;
		}
	}
	
	private class StructuredTextNumberedFactory extends AbstractStructuredParagraphFactory implements StructuredParagraphFactory {
		private Pattern pattern = Pattern.compile("(^\\s*[\\p{Alnum}]\\.)|(^\\s*[0-9]+\\.)|(^\\s*[0-9]+\\s+)");

		public StructuredTextNumberedFactory() {
			super();
		}
		public StructuredTextParagraph createStructuredParagraph(String inRawParagraph) {
			return new StructuredTextNumbered(inRawParagraph);
		}
		protected Pattern getPattern() {
			return pattern;
		}
	}
	
	private class StructuredTextIndentedFactory extends AbstractStructuredParagraphFactory implements StructuredParagraphFactory {
		private Pattern pattern = Pattern.compile("^\\s+");

		public StructuredTextIndentedFactory() {
			super();
		}
		public StructuredTextParagraph createStructuredParagraph(String inRawParagraph) {
			return new StructuredTextIndented(inRawParagraph);
		}
		protected Pattern getPattern() {
			return pattern;
		}
	}
	
	/**
	 * Constructor for StructuredText.
	 * Creates an instance of StructuredText from the specified paragraphs.
	 * 
	 * @param inRawParagraphs java.lang.String[]
	 */
	public StructuredText(String[] inRawParagraphs) {
		super();
		paragraphs = initialize(inRawParagraphs);
	}
	
	private Collection<StructuredTextParagraph> initialize(String[] inRawParagraphs) {
		Collection<StructuredTextParagraph> outParagraphs = new Vector<StructuredTextParagraph>();
		StructuredTextParagraph lActualParagraph = null;
		
		for (int i = 0; i < inRawParagraphs.length; i++) {
			boolean lMatched = false;
			for (StructuredParagraphFactory lFactory : cFactories) {
				if (lFactory.matches(inRawParagraphs[i])) {
					StructuredTextParagraph lParagraph = lFactory.createStructuredParagraph(inRawParagraphs[i].substring(lFactory.getSubstringStart()));
					
					// if paragraph is indented, add indented to the actual paragraph
					if (lParagraph.getParagraphType() == StructuredTextParagraph.PARAGRAPH_INDENTED) {
						if (lActualParagraph != null) {
							lActualParagraph.addIndented(lParagraph.getRawString());
							lMatched = true;
							break;
						}
					}
					
					// if paragraph is of equal type, add to the actual paragraph
					if (lParagraph.equalsType(lActualParagraph)) {
						lActualParagraph.add(lParagraph.getRawString());
						lMatched = true;
						break;
					}
					
					// else add to the top level of paragraphs and set new paragraph as actual paragraph
					outParagraphs.add(lParagraph);
					lActualParagraph = lParagraph;
					lMatched = true;
					break;
				}
			}
			
			// if paragraph didn't match, it is of plain type
			if (!lMatched) {
				StructuredTextParagraph lParagraph = new StructuredTextPlain(inRawParagraphs[i]);
				outParagraphs.add(lParagraph);
				lActualParagraph = lParagraph;
			}
		}
		return outParagraphs;
	}
	
	/**
	 * Implementation of Visitor Pattern, e.g. to create an HTML string out
	 * of this text formatted with structured text rules.
	 * 
	 * @param inSerializer StructuredTextSerializer
	 */
	public void accept(StructuredTextSerializer inSerializer) {
		inSerializer.visitStructuredText(this);
	}
	
	/**
	 * Returns an Iterator over this StructuredText's paragraphs.
	 * 
	 * @return java.util.Iterator
	 */
	public Iterator<StructuredTextParagraph> getStructuredTextParagraphs() {
		return paragraphs.iterator();
	}
}

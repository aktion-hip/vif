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

package org.hip.vif.web.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for sanitizing the input of <code>RichTextArea</code> input.
 * The sanitizer ensures that every text input is properly formatted as paragraphs (i.e. enclosed in &lt;p> tags). 
 * 
 * @author Luthiger
 * Created: 15.01.2012
 */
public class RichTextSanitizer {
	private static final String P_START = "<p>"; //$NON-NLS-1$
	private static final String P_END = "</p>"; //$NON-NLS-1$
	private static final Pattern TEXT_BEGIN = Pattern.compile("(.*?)<p.*?>", Pattern.DOTALL); //$NON-NLS-1$
	private static final Pattern TEXT_END = Pattern.compile("(.*</p>)(.*\\z)", Pattern.DOTALL); //$NON-NLS-1$
	
	private static final Pattern WHITE_SPACE = Pattern.compile("[\\s\\n\\r[<br>][<br />][&nbsp;]]+", Pattern.DOTALL); //$NON-NLS-1$
	private static final Pattern BLOCK_CONTENT = Pattern.compile("<p.*?>(.*?)</p>", Pattern.DOTALL); //$NON-NLS-1$
	private static final Pattern BLOCK_WITH_CONTENT = Pattern.compile("(<p.*?>(.*?)</p>)", Pattern.DOTALL); //$NON-NLS-1$


	/**
	 * Sanitizes input from <code>RichTextArea</code>.
	 * 
	 * @param inInput String the input from the <code>RichTextArea</code> field
	 * @return String properly enclosed input
	 */
	public static String sanitize(String inInput) {
		String lInput = toPara(inInput.trim());
		Matcher lMatcher1 = TEXT_BEGIN.matcher(lInput);
		Matcher lMatcher2 = TEXT_END.matcher(lInput);
		
		String lPrefix = ""; //$NON-NLS-1$
		if (lMatcher1.find()) {
			lPrefix = lMatcher1.group(1);
		}
		boolean lHasPrefix = lPrefix.trim().length() > 0;
		
		String lPostfix = ""; //$NON-NLS-1$
		if (lMatcher2.find()) {
			lPostfix = lMatcher2.group(2);
		}
		boolean lHasPostfix = lPostfix.trim().length() > 0;
		
		if (!lHasPrefix && !lHasPostfix) {
			if (lInput.contains(P_END)) {
				return emptyBlockRemover(new StringBuilder(lInput));
			}
			return emptyBlockRemover(new StringBuilder(P_START).append(lInput).append(P_END));
		}
		
		StringBuilder out = new StringBuilder();
		if (lHasPrefix) {
			out.append(P_START).append(lPrefix.trim()).append(P_END);
		}
		String lRemain = lInput.substring(lPrefix.length());
		if (!lHasPostfix) {
			out.append(lRemain);
			return emptyBlockRemover(out);
		}
		lRemain = lRemain.substring(0, lRemain.length() - lPostfix.length());
		out.append(lRemain).append(P_START).append(lPostfix.trim()).append(P_END);
		return emptyBlockRemover(out);
	}
	
	private static String emptyBlockRemover(StringBuilder inText) {
		StringBuilder out = new StringBuilder();
		Matcher lBlock = BLOCK_WITH_CONTENT.matcher(inText);
		while (lBlock.find()) {
			String lPara = lBlock.group(1);
			if (!isWhiteSpaceOnly(lBlock.group(2))) {
				out.append(lPara);
			}
		}
		return new String(out);
	}

	/**
	 * Checks whether the html input is empty, i.e. contains only white space and html markup.
	 * 
	 * @param inText String the html input
	 * @return boolean <code>true</code> if the input is empty (i.e. contains only white space)
	 */
	public static boolean checkInputEmpty(String inText) {
		if (isWhiteSpaceOnly(inText)) {
			return true;
		}
		
		String lText = toPara(inText);
		//check blocks
		Matcher lBlockContentFinder = BLOCK_CONTENT.matcher(lText);
		boolean lNoBlocks = true;
		while (lBlockContentFinder.find()) {
			lNoBlocks = false;
			if (!isWhiteSpaceOnly(getBlock(lBlockContentFinder))) {
				return false;
			}
		}
		if (lNoBlocks) {
			return false;
		}
		
		Matcher lMatcher = TEXT_BEGIN.matcher(lText);
		if (lMatcher.find()) {
			if (!isWhiteSpaceOnly(lMatcher.group(1))) {
				return false;
			}
		}
		lMatcher = TEXT_END.matcher(lText);
		if (lMatcher.find()) {
			if (!isWhiteSpaceOnly(lMatcher.group(2))) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean isWhiteSpaceOnly(String inText) {
		return WHITE_SPACE.matcher(inText.trim()).replaceAll("").trim().length() == 0; //$NON-NLS-1$
	}
	
	private static String getBlock(Matcher inMatcher) {
		String out = inMatcher.group(1);
		return out == null ? inMatcher.group(2) : out;
	}
	
	/**
	 * Converts all &lt;div>...&lt;/div> to &lt;p>...&lt;/p>.
	 * 
	 * @param inText String the text to process
	 * @return the converted text
	 */
	public static String toPara(String inText) {
		String out = inText.replace("<div ", "<p "); //$NON-NLS-1$ //$NON-NLS-2$
		out = out.replace("<div>", "<p>"); //$NON-NLS-1$ //$NON-NLS-2$
		out = out.replace("</div>", "</p>"); //$NON-NLS-1$ //$NON-NLS-2$
		return out;
	}
	
	/**
	 * Removes the html paragraph tags in a text.<br />
	 * Note: Use this function to prepare questions displayed in a table.
	 * 
	 * @param inText String the text to process
	 * @return the text without paras
	 */
	public static String removePara(String inText) {
		String out = inText.replaceAll("<p.*?>", ""); //$NON-NLS-1$ //$NON-NLS-2$
		out = out.replaceAll("</p>", ""); //$NON-NLS-1$ //$NON-NLS-2$
		return out;
	}
	
}

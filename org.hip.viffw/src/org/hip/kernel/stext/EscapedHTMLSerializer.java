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

/**
 * This class creates output escaped html code by serializing a StructuredText.
 * 
 * @see org.hip.kernel.stext.StructuredText
 * @author: Benno Luthiger
 */
public class EscapedHTMLSerializer extends AbstractHTMLSerializer {
	
	/**
	 * Constructor for EscapedHTMLSerializer.
	 */
	public EscapedHTMLSerializer() {
		super();
	}

	/**
	 * Emits a start tag which is output escaped.
	 * 
	 * @param inContent java.lang.String
	 */
	protected synchronized void emitHTMLStartTag(String inContent) {
		emit("&lt;" + inContent + "&gt;");
	}

	/**
	 * Emits an end tag which is output escaped.
	 * 
	 * @param inContent java.lang.String
	 */
	protected synchronized void emitHTMLEndTag(String inContent) {
		emit("&lt;/" + inContent + "&gt;");
	}
	
	protected StringBuffer getConverted(String inRawString) {
		return InlineStructuredText2HTML.getSingleton().convertToEscapedHTML(inRawString);
	}
}

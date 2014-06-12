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

import java.util.Iterator;

/**
 * This serializer ignores all StructuredText format and returns cleaned text.
 * 
 * @author: Benno Luthiger
 */
public class IgnoringFormatSerializer extends AbstractStructuredTextSerializer implements StructuredTextSerializer {
	// Instance variables
	private boolean inList = false;

	/**
	 * Constructor for IgnoringFormatSerializer.
	 */
	public IgnoringFormatSerializer() {
		super();
	}

	/**
	 * @see org.hip.kernel.stext.StructuredTextSerializer#visitStructuredText(StructuredText)
	 */
	public void visitStructuredText(StructuredText inStructuredText) {
		for (Iterator<StructuredTextParagraph> lParagraphs = inStructuredText.getStructuredTextParagraphs(); lParagraphs.hasNext();) {
			lParagraphs.next().accept(this);
		}
	}

	/**
	 * @see org.hip.kernel.stext.StructuredTextSerializer#visitStructuredTextBullet(StructuredTextBullet)
	 */
	public void visitStructuredTextBullet(StructuredTextBullet inStructuredTextBullet) {
		iterateOverListElements(inStructuredTextBullet.getSubElements());
	}

	/**
	 * @see org.hip.kernel.stext.StructuredTextSerializer#visitStructuredTextNumbered(StructuredTextNumbered)
	 */
	public void visitStructuredTextNumbered(StructuredTextNumbered inStructuredTextNumbered) {
		iterateOverListElements(inStructuredTextNumbered.getSubElements());
	}
	
	private void iterateOverListElements(Iterator<StructuredTextParagraph> inListElements) {
		inList = true;
		for (Iterator<StructuredTextParagraph> lElements = inListElements; lElements.hasNext();) {
			lElements.next().accept(this);
			emit_nl();
		}
		inList = false;
	}

	/**
	 * @see org.hip.kernel.stext.StructuredTextSerializer#visitStructuredTextPlain(StructuredTextPlain)
	 */
	public void visitStructuredTextPlain(StructuredTextPlain inStructuredTextPlain) {
		emit(getConverted(inStructuredTextPlain.getRawString()));
		if (!inList) {
			emit_nl();
		}
		if (inStructuredTextPlain.hasSubElements()) {
			for (Iterator<StructuredTextParagraph> lParagraphs = inStructuredTextPlain.getSubElements(); lParagraphs.hasNext();) {
				emit_nl();
				lParagraphs.next().accept(this);
			}
		}
	}
	
	private StringBuffer getConverted(String inRawString) {
		return InlineStructuredText2HTML.getSingleton().convertIgnoring(inRawString);
	}	
}

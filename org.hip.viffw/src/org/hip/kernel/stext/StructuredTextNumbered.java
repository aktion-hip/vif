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

import org.hip.kernel.util.Debug;

/**
 * This class implements a Structured Text Numbered List, i.e. 
 * <pre>
 * <ol>
 *   <li><p>Some text</p></li>
 *   <li><p>Further text</p></li>
 * </ol>
 * </pre>
 * 
 * @author: Benno Luthiger
 */
public class StructuredTextNumbered extends AbstractStructuredTextParagraph implements StructuredTextParagraph {
	Collection<StructuredTextParagraph> numbereds;
	StructuredTextParagraph currentNumbered;

	/**
	 * Constructor for StructuredTextNumbered.
	 * 
	 * @param inRawParagraph java.lang.String
	 */
	public StructuredTextNumbered(String inRawParagraph) {
		super(inRawParagraph);
		numbereds = new Vector<StructuredTextParagraph>();
		add(inRawParagraph);
	}
	
	/**
	 * Implementation of Visitor Pattern, e.g. to create an HTML string out
	 * of this text formatted with structured text rules.
	 * 
	 * @param inSerializer org.hip.kernel.stext.StructuredTextSerializer
	 */
	public void accept(StructuredTextSerializer inSerializer) {
		inSerializer.visitStructuredTextNumbered(this);
	};
	
	/**
	 * Adds a new list element to this numbered list.
	 * 
	 * @param inRawParagraph java.lang.String
	 */
	public void add(String inRawParagraph) {
		currentNumbered = new StructuredTextPlain(inRawParagraph);;
		numbereds.add(currentNumbered);
	};
	
	
	/**
	 * Adds a new paragraph to the current list element.
	 * 
	 * @param inRawParagraph java.lang.String
	 */
	public void addIndented(String inRawParagraph) {
		currentNumbered.add(inRawParagraph);
	};
	
	/**
	 * Returns the type of this paragraph.
	 * 
	 * @return int
	 * @see org.hip.kernel.stext.StructuredTextParagraph
	 */
	public int getParagraphType() {
		return PARAGRAPH_NUMBERED;
	}
	
	/**
	 * Checks whether this list has list elements.
	 * 
	 * @return boolean
	 */
	public boolean hasSubElements() {
		return !numbereds.isEmpty();
	}

	/**
	 * Returns an iterator over all list elements of this numbered list.
	 * 
	 * @return java.util.Iterator
	 */
	public Iterator<StructuredTextParagraph> getSubElements() {
		return numbereds.iterator();
	}

	/**
	 * Returns a string representation of this object.
	 * 
	 * @return java.lang.String
	 */	
	public String toString() {
		return Debug.classMultilineMarkupString(this, getRawString());
	}
	
	/**
	 * Returns a hash code value for this object.
	 *
	 * @return int
	 */
	public int hashCode() {
		return toString().hashCode();
	}
}

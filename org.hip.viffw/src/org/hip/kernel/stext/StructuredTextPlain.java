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
 * This class implements a plain StructuredText paragraph, i.e. 
 * <pre>
 *   <p>Some text</p>
 * </pre>
 * 
 * @author: Benno Luthiger
 */
public class StructuredTextPlain extends AbstractStructuredTextParagraph implements StructuredTextParagraph {
	Collection<StructuredTextParagraph> plains = null;
	
	/**
	 * Constructor for StructuredTextPlain out of a raw paragraph.
	 * 
	 * @param inRawParagraph java.lang.String
	 */
	public StructuredTextPlain(String inRawParagraph) {
		super(inRawParagraph);
	}
	
	/**
	 * Implementation of Visitor Pattern, e.g. to create an HTML string out
	 * of this text formatted with structured text rules.
	 * 
	 * @param inSerializer org.hip.kernel.stext.StructuredTextSerializer
	 */
	public void accept(StructuredTextSerializer inSerializer) {
		inSerializer.visitStructuredTextPlain(this);
	};
	
	/**
	 * Adds a new sub paragraph.
	 * 
	 * @param inRawParagraph java.lang.String
	 */
	public void add(String inRawParagraph) {
		getPlains().add(new StructuredTextPlain(inRawParagraph));
	};
	
	/**
	 * Adds a new sub paragraph.
	 * 
	 * @param inRawParagraph java.lang.String
	 */
	public void addIndented(String inRawParagraph) {
		add(inRawParagraph);
	};
	
	/**
	 * Returns the type of this paragraph.
	 * 
	 * @return int
	 * @see org.hip.kernel.stext.StructuredTextParagraph
	 */
	public int getParagraphType() {
		return PARAGRAPH_PLAIN;
	}
	
	/**
	 * Checks whether this paragraph has subparagraphs.
	 * 
	 * @return boolean
	 */
	public boolean hasSubElements() {
		return plains != null;
	}
	
	/**
	 * Returns an iterator over this paragraph's subparagraphs.
	 * 
	 * @return java.util.Iterator
	 */
	public Iterator<StructuredTextParagraph> getSubElements() {
		return getPlains().iterator();
	}
	
	/**
	 * Lazy initialisation
	 */
	private Collection<StructuredTextParagraph> getPlains() {
		if (plains == null) {
			plains = new Vector<StructuredTextParagraph>();
		}
		return plains;
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

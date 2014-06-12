package org.hip.kernel.stext;

import java.util.Iterator;

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
 * Interface for a paragraph formatted with structured text rules.
 * 
 * @author: Benno Luthiger
 */
public interface StructuredTextParagraph {
	public static final int PARAGRAPH_PLAIN 	= 0;
	public static final int PARAGRAPH_BULLET	= 1;
	public static final int PARAGRAPH_NUMBERED 	= 2;
	public static final int PARAGRAPH_INDENTED 	= 3;
	
	void accept(StructuredTextSerializer inSerializer);
	void add(String inRawParagraph);
	void addIndented(String inRawParagraph);
	int getParagraphType();
	String getRawString();
	boolean equalsType(Object inObject);
	boolean hasSubElements();
	Iterator<StructuredTextParagraph> getSubElements();
}

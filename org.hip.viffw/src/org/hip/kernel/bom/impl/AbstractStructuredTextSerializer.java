package org.hip.kernel.bom.impl;

/*
	This package is part of the servlet framework used for the application VIF.
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

import org.hip.kernel.bom.Property;
import org.hip.kernel.stext.StructuredText;
import org.hip.kernel.stext.StructuredTextGenerator;
import org.hip.kernel.stext.StructuredTextSerializer;

/**
 * This abstract class provides the generic functionality which can be used 
 * to serialize DomainObjects with String type properties whose values are 
 * formated according to StructuredText rules.
 * 
 * @author: Benno Luthiger
 */
public abstract class AbstractStructuredTextSerializer extends XMLSerializer {

	/**
	 * Constructor for AbstractStructuredTextSerializer.
	 */
	public AbstractStructuredTextSerializer() {
		super();
	}

	/**
	 * Constructor for AbstractStructuredTextSerializer.
	 * @param inLevel
	 */
	public AbstractStructuredTextSerializer(int inLevel) {
		super(inLevel);
	}

	/**
	 * Constructor for AbstractStructuredTextSerializer.
	 * @param inLevel
	 * @param inFilter
	 */
	public AbstractStructuredTextSerializer(int inLevel, XMLCharacterFilter inFilter) {
		super(inLevel, inFilter);
	}

	/**
	 * Constructor for AbstractStructuredTextSerializer.
	 * @param inFilter
	 */
	public AbstractStructuredTextSerializer(XMLCharacterFilter inFilter) {
		super(inFilter);
	}
	
	/**
	 * Overwriting the superclass' method for that the property's value
	 * is serialized as structured text.
	 * 
	 * @param inProperty org.hip.kernel.bom.Property
	 * @return java.lang.Object
	 */
	protected Object emitPropertyValue(Property inProperty) {
		StructuredText lStructuredText = StructuredTextGenerator.getSingleton().createStructuredText((String)inProperty.getValue());
		StructuredTextSerializer lSerializer = getStructuredTextSerializer();
		lStructuredText.accept(lSerializer);
		return lSerializer.toString().trim(); 
	}
	
	/**
	 * Hook for subclasses.
	 * Subclasses have to implement this method to return
	 * the correct StructuredTextSerializer to serialize
	 * DomainObjects with text formated according to StructuredText rules.
	 * 
	 * @return org.hip.kernel.stext.StructuredTextSerializer
	 */
	abstract protected StructuredTextSerializer getStructuredTextSerializer();
}

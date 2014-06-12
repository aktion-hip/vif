/*
	This package is part of the framework used for the application VIF.
	Copyright (C) 2006, Benno Luthiger

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

package org.hip.kernel.bom.impl;

import org.hip.kernel.stext.StructuredTextSerializer;

/**
 * This class can be used to serialize DomainObjects with String type
 * properties whose values contain text formated according to StructuredText rules.
 * This serializer converts these formatings to HTML text.
 *
 * @author Luthiger
 * Created on 29.05.2008
 * @see org.hip.kernel.bom.impl.EscapedHTMLSerializer
 */
public class HTMLSerializer extends AbstractStructuredTextSerializer {

	/**
	 * Returns the correct StructuredTextSerializer to serialize
	 * DomainObjects with text formated according to StructuredText rules.
	 * In this case returns an instance of 
	 * <code>org.hip.kernel.stext.HTMLSerializer.HTMLSerializer()</code>.
	 * 
	 * @return StructuredTextSerializer
	 */
	@Override
	protected StructuredTextSerializer getStructuredTextSerializer() {
		return new org.hip.kernel.stext.HTMLSerializer();
	}

}

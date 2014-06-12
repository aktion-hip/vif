package org.hip.kernel.bom.impl;

/*
	This package is part of the servlet framework used for the application VIF.
	Copyright (C) 2001, Benno Luthiger

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

import org.hip.kernel.bom.PropertySet;

/**
 * 	The DefaultSemanticObject is a concrete implementation
 *	of SemanticObject. It has no definition level and is
 *	highly dynamic - but also not very type-safe.
 * 
 * 	@author		Benno Luthiger
 */
public class DefaultSemanticObject extends AbstractSemanticObject {
/**
 * DefaultSemanticObject default constructor.
 */
public DefaultSemanticObject() {
	super();
}
/**
 * 	@param inSet org.hip.kernel.bom.PropertySet
 */
protected void initializePropertySet(PropertySet inSet) {
}
/**
 * @return boolean
 */
protected boolean isDynamicAddAllowed() {
	return true ;
}
}

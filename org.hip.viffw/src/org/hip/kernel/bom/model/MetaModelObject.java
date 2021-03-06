/**
	This package is part of the servlet framework used for the application VIF.
	Copyright (C) 2001-2014, Benno Luthiger

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
package org.hip.kernel.bom.model;

import org.hip.kernel.bom.SemanticObject;

/** MetaModelObjects describes ModelObjects, for example the ObjectDef, PropertyDef classes etc. Even the simplest
 * interprative system needs this level. Actually here stops the story. MetaModelObjects do not have any dynamics.
 * Herein lies the limitation of the interprative part of the system. E.g. the current implementation does not provide
 * any support on an interprated level for object methods.
 *
 * @author Benno Luthiger */
public interface MetaModelObject extends SemanticObject {
}

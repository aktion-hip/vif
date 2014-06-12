/**
	This package is part of the application VIF.
	Copyright (C) 2011-2014, Benno Luthiger

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

package org.hip.vif.core.bom;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.exc.VException;

/**
 * Special interface for group like domain objects.
 * 
 * @author Luthiger Created: 12.12.2011
 */
public interface IGroup extends DomainObject {

	/**
	 * The group is deletable if it is either created or closed.
	 * 
	 * @return boolean <code>true</code> if the group is deletable.
	 */
	boolean isDeletable() throws VException;

}

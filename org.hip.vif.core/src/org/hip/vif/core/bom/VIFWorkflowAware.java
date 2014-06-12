/*
	This package is part of the application VIF.
	Copyright (C) 2003, Benno Luthiger

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

import org.hip.kernel.exc.VException;

/**
 * This interface describes VIF specific workflow behaviour for contributions.
 * 
 * Created on 28.07.2003
 * @author Luthiger
 */
public interface VIFWorkflowAware {

	/**
	 * Returns true if the state of this object is <code>private</code>.
	 * 
	 * @param boolean
	 */
	boolean isPrivate();
	
	/**
	 * Returns true if the state of this object is unpublished.
	 * 
	 * @param boolean
	 */
	boolean isUnpublished();

	/**
	 * Returns true if the state of this object is published.
	 * 
	 * @param boolean
	 */
	boolean isPublished();
	
	/**
	 * Returns the node ID of this contribution.
	 * In case of a question, it's the question ID,
	 * in case of a completions, it's the question's ID the completions belongs to.
	 * 
	 * @return Long
	 * @throws VException
	 */
	Long getNodeID() throws VException;
	
	/**
	 * Returns true if the object is a node.
	 * @return boolean
	 */
	boolean isNode();

}

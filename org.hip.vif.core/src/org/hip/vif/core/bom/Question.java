/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2002, Benno Luthiger

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

import org.hip.vif.core.exc.BOMChangeValueException;

/**
 * This interface defines the behaviour of the Question domain object.
 * A Question is a node in a discussion tree. A Question can be answered 
 * (through Contributions) or divided in further Questions.
 * 
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.Completion
 */
public interface Question extends IQuestion {
	
	/**
	 * Use case to create a new question by saving initial question data.
	 * 
	 * @param inQuestion java.lang.String
	 * @param inRemark java.lang.String
	 * @param inParent Long may be 0 for root questioin
	 * @param inGroup java.lang.String
	 * @param inAuthorID java.lang.Long
	 * @return Long the new entry's ID
	 * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException
	 */
	Long ucNew(String inQuestion, String inRemark, Long inParent, String inGroup, Long inAuthorID) throws BOMChangeValueException;

	/**
	 * Use case to save edited question data (without change of state).
	 * 
	 * @param inQuestion String
	 * @param inRemark String
	 * @param inAuthorID Long
	 * @throws BOMChangeValueException
	 */
	void ucSave(String inQuestion, String inRemark, Long inAuthorID) throws BOMChangeValueException;

	/**
	 * Use case to save edited question data.
	 * 
	 * @param inQuestion java.lang.String
	 * @param inRemark java.lang.String
	 * @param inState java.lang.String
	 * @param inAuthorID java.lang.Long
	 * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException
	 */
	void ucSave(String inQuestion, String inRemark, String inState, Long inAuthorID) throws BOMChangeValueException;

}

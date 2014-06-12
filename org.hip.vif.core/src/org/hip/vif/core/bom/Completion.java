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

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.exc.BOMChangeValueException;

/**
 * This interface defines the behaviour of the Completion domain object.
 * Completions are texts provided by participants to resolve questions.
 * 
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.Participant
 * @see org.hip.vif.core.bom.Question
 */
public interface Completion extends DomainObject {

	/**
	 * Use case to create a new completion by saving initial completion data.
	 * 
	 * @param inCompletion java.lang.String
	 * @param inQuestion java.lang.String
	 * @param inAuthorID java.lang.Long
	 * @return Long the ID of the created entry
	 * @throws BOMChangeValueException
	 */
	Long ucNew(String inCompletion, String inQuestion, Long inAuthorID) throws BOMChangeValueException;

	/**
	 * Use case to save modified contribution data (without change of state).
	 * 
	 * @param inContribution String
	 * @param inAuthorID Long
	 * @throws BOMChangeValueException
	 */
	void ucSave(String inContribution, Long inAuthorID) throws BOMChangeValueException;

	/**
	 * Use case to save modified contribution data.
	 * 
	 * @param inContribution java.lang.String
	 * @param inState java.lang.String
	 * @param inAuthorID  java.lang.Long
	 * @throws BOMChangeValueException
	 */
	void ucSave(String inContribution, String inState, Long inAuthorID) throws BOMChangeValueException;

	
	/**
	 * Returns the question this completion belongs to.
	 * 
	 * @return Question
	 * @throws VException
	 */
	Question getOwningQuestion() throws VException;

	/**
	 * Returns the decimal ID of the question this completion belongs to.
	 * 
	 * @return String
	 * @throws VException
	 */
	String getDecimalID() throws VException;
	
	/**
	 * Returns the completion's ID.
	 * 
	 * @return Long
	 * @throws VException
	 */
	Long getID() throws VException;
}

package org.hip.vif.core.bom;

/*
	This package is part of the persistency layer of the application VIF.
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

import java.sql.SQLException;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.exc.VException;

/**
 * This interface defines the behaviour of the QuestionHierarchy domain object.
 * It is used to describe the hierarchy and dependencies of questions.
 * 
 * @author: Benno Luthiger
 */
public interface QuestionHierarchy extends DomainObject {
	
	/**
	 * Returns the question associated with this QuestionHiearchy entry.
	 * 
	 * @return Question
	 * @throws VException
	 * @throws SQLException
	 */
	public Question getAssociatedQuestion() throws VException, SQLException;
}

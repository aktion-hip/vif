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

import java.sql.SQLException;

import org.hip.kernel.exc.VException;

/**
 * Interface for all responsible domain objects, i.e. all author-reviewer objects.
 * 
 * Created on 15.08.2003
 * @author Luthiger
 */
public interface Responsible {
	/**
	 * The member has refused to review the contributions,
	 * therefore, set the entrie's flag to refused.
	 * 
	 * @throws VException
	 * @throws SQLException
	 */
	void setRefused() throws VException, SQLException;
	
	/**
	 * Returns the ID of the responsible member.
	 * 
	 * @return Long
	 * @throws VException
	 */
	Long getResponsibleID() throws VException;

}

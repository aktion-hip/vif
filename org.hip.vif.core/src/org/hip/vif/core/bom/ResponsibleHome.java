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
 * Interface for all homes of responsible domain objects, 
 * i.e. all author-reviewer objects.
 * 
 * Created on 15.08.2003
 * @author Luthiger
 */
public interface ResponsibleHome {
	public enum Type {
		AUTHOR(0), REVIEWER(1), REVIEWER_REFUSED(2);
		
		private int value;
		Type(int inValue) {
			value = inValue;
		}
		public Integer getValue() {
			return new Integer(value);
		}
		/**
		 * Checks the passed value.
		 * @param inValue Object
		 * @return <code>true</code> if this type equals the passed value
		 */
		public boolean check(Object inValue) {
			try {
				return value == Integer.parseInt(inValue.toString());
			}
			catch (Exception exc) {
				//intentionally left empty
			}
			return false;
		}
	}
	
	public final static String KEY_TYPE				= "Type";
	public final static String KEY_MEMBER_ID 		= "MemberID";
	public final static String KEY_CREATED 			= "CreatedDate";
	
	/**
	 * Returns the entry identified by the specified values, i.e. either
	 * author or reviewer.
	 * 
	 * @param inContributionID String
	 * @param inMemberID Long
	 * @return Responsible
	 * @throws VException
	 * @throws SQLException
	 */
	Responsible getResponsible(String inContributionID, Long inMemberID) throws VException, SQLException;
	
	/**
	 * Returns the responsible author.
	 * 
	 * @param inContributionID String
	 * @return Responsible
	 * @throws VException
	 * @throws SQLException
	 */
	Responsible getAuthor(String inContributionID) throws VException, SQLException;
}

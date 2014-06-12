/*
	This package is part of the application VIF.
	Copyright (C) 2009, Benno Luthiger

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
package org.hip.vifapp.fixtures;



/**
 *
 * @author Luthiger
 * Created 27.01.2009 
 */
public abstract class VifAdminFixture extends VifFixture {
	protected final static String APP = "admin";
	
	public VifAdminFixture(String inPort) {
		super(inPort);
	}
	
	protected String getApp() {
		return APP;
	}
	
	public String numberOfMembers() throws Exception {
		int outNumber = getTableRows(String.format(XPATH_TR, "dataSmall odd"), String.format(XPATH_TR, "dataSmall even")).size();
		return String.valueOf(outNumber);
	}

}

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

import static fitnesse.util.ListUtility.list;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

/**
 * Fitnesse test to query the status quo of VIF members. 
 *
 * @author Luthiger
 * Created 14.02.2009 
 */
public class VifMembers extends VifAdminFixture {
	private final static String XPATH_TR = "//tr[@class='dataSmall %s']"; 
	
	private String userName;
	private String password;

	public VifMembers(String inPort, String inUserName, String inPassword) {
		super(inPort);
		userName = inUserName;
		password = inPassword;
	}

	public List<Object> query() throws Exception {
		loginAsWithPassword(userName, password);
		changeTo("org.hip.vif.admin.member.searchMember");
		List<HtmlTableRow> lRows = getTableRows(String.format(XPATH_TR, "even"), String.format(XPATH_TR, "odd"));
		
		List<Object> outList = list();
		for (HtmlTableRow lRow : lRows) {
			outList.add(evalRow(lRow));
		}
		
		logout();
		return outList;
	}

	private Object evalRow(HtmlTableRow inRow) {
		return list(
				list("User ID", inRow.getCell(1).asText()),
				list("name", inRow.getCell(2).asText()),
				list("street", inRow.getCell(3).asText()),
				list("place", inRow.getCell(4).asText()),
				list("mail", inRow.getCell(5).asText())
				);
	}

	@Override
	protected String getRequestType() {
		return null;
	}
	
}

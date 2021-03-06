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

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

/**
 * Fitnesse test to query the status quo of group admins of the selected discussion group.
 *
 * @author Luthiger
 * Created 15.02.2009 
 */
public class DiscussionGroups extends VifAdminFixture {

	private String userName;
	private String password;
	private String groupID;

	public DiscussionGroups(String inPort, String inUserName, String inPassword, String inGroupID) {
		super(inPort);
		userName = inUserName;
		password = inPassword;
		groupID = inGroupID;
	}

	/* (non-Javadoc)
	 * @see org.hip.vifapp.fixtures.VifFixture#getRequestType()
	 */
	@Override
	protected String getRequestType() {
		return null;
	}

	public List<Object> query() throws Exception {
		loginAsWithPassword(userName, password);
		changeTo("org.hip.vif.admin.groupedit.showGroupList");
		
		List<Object> outList = list();

		//show group's edit page
		List<HtmlTableRow> lRows = getTableRows(String.format(XPATH_TR, "even"), String.format(XPATH_TR, "odd"));
		HtmlAnchor lAnchor = null;
		for (HtmlTableRow lRow : lRows) {
			if (groupID.equals(lRow.getCell(1).asText())) {
				lAnchor = (HtmlAnchor)lRow.getCell(1).getFirstChild();
			}
		}		
		if (lAnchor == null) {
			return outList;
		}
		setSuccess(getActionPageChecked(lAnchor.click()));

		//at last, create list
		lRows = getTableRows(String.format(XPATH_TR, "dataSmall even"), String.format(XPATH_TR, "dataSmall odd"));
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
				list("mail", inRow.getCell(3).asText())
				);
	}
}

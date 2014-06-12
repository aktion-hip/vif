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
 *
 * @author Luthiger
 * Created 21.02.2009 
 */
public class Contributions extends VifForumFixture {
	private final static String CHANGE_TO = "org.hip.vif.forum.groups.showGroup&groupID=%s";
	
	private String userName;
	private String groupName;

	/**
	 * @param inPort
	 */
	public Contributions(String inPort, String inUserName, String inGroupName) {
		super(inPort);
		userName = inUserName;
		groupName = inGroupName;
	}

	/* (non-Javadoc)
	 * @see org.hip.vifapp.fixtures.VifFixture#getRequestType()
	 */
	@Override
	protected String getRequestType() {
		return null;
	}
	
	public List<Object> query() throws Exception {
		loginAsWithPassword(userName, userName);
		String lGroupId = getGroupId(groupName, 0, getTableRows(String.format(XPATH_TR, "data odd"), String.format(XPATH_TR, "data even")));
		changeTo(String.format(CHANGE_TO, lGroupId));
		
		List<Object> outList = list();
		List<HtmlTableRow> lRows = getTableRows(String.format(XPATH_TR, "even"), String.format(XPATH_TR, "odd"));
		for (HtmlTableRow lRow : lRows) {
			outList.add(evalRow(lRow));
		}
		
		logout();
		
		return outList;
	}

	private Object evalRow(HtmlTableRow inRow) {
		return list(
				list("question", inRow.getCell(1).asText()),
				list("state", inRow.getCell(2).asText())
				);
	}

}

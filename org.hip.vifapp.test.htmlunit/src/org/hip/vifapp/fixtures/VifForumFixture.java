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

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;


/**
 *
 * @author Luthiger
 * Created 05.02.2009 
 */
public abstract class VifForumFixture extends VifFixture {
	protected final static String APP = "forum";
	private static final String QUESTION_ID_KEY = "questionID";
	
	public VifForumFixture(String inPort) {
		super(inPort);
	}

	/* (non-Javadoc)
	 * @see org.hip.vifapp.fixtures.VifFixture#getApp()
	 */
	@Override
	protected String getApp() {
		return APP;
	}

	public String getQuestionId(String inQuestion) throws Exception {
		List<HtmlTableRow> lRows = getTableRows(String.format(XPATH_TR, "odd"), String.format(XPATH_TR, "even"));
		for (HtmlTableRow lRow : lRows) {
			if (inQuestion.equals(lRow.getCell(1).asText())) {
				return getID(((HtmlAnchor) lRow.getCell(1).getFirstChild()).getHrefAttribute(), QUESTION_ID_KEY);
			}
		}
		return "";
	}

}

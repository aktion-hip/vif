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

import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;


/**
 *
 * @author Luthiger
 * Created 21.02.2009 
 */
public class VifReviewAction extends VifForumFixture {
	private final static String CHANGE_TO = "org.hip.vif.forum.groups.showRequests&groupID=%s";

	private static final long DELAY = 500;
	
	private String groupID = null;

	/**
	 * @param inPort
	 */
	public VifReviewAction(String inPort) {
		super(inPort);
	}

	/* (non-Javadoc)
	 * @see org.hip.vifapp.fixtures.VifFixture#getRequestType()
	 */
	@Override
	protected String getRequestType() {
		return null;
	}
	
	public String getReviewerFor(String inPotential, String inGroupName) throws Exception {
		for (String lPart : inPotential.split(",")) {
			String lPotential = lPart.trim();
			if (isReviewer(lPotential, inGroupName)) {
				return lPotential;
			}
		}
		return "n.a.";
	}

	private boolean isReviewer(String inPotential, String inGroupName) throws Exception {
		loginAsWithPassword(inPotential, inPotential);
		String lGroupId = getGroupId(inGroupName);
		changeTo(String.format(CHANGE_TO, lGroupId));
		boolean outValue = getTableRows(String.format(XPATH_TR, "odd"), String.format(XPATH_TR, "even")).size() > 0;
		logout();
		return outValue;
	}

	public String getGroupId(String inGroupName) throws Exception {
		if (groupID == null) {
			groupID = getGroupId(inGroupName, 0, getTableRows(String.format(XPATH_TR, "data odd"), String.format(XPATH_TR, "data even")));
		}
		return groupID;
	}
	
	public boolean acceptReview() throws Exception {
		return process("Waiting", "taskAccept");
	}
	
	public boolean publish() throws Exception {
		long lUntil = System.currentTimeMillis() + DELAY;
		while (System.currentTimeMillis() < lUntil) {
			//loop
		}
		return process("Reviewed", "taskPublish");
	}
	
	private boolean process(String inFormName, String inTask) throws Exception {
		List<HtmlTableRow> lRows = getTableRows(String.format(XPATH_TR, "odd"), String.format(XPATH_TR, "even"));
		for (HtmlTableRow lRow : lRows) {
			((HtmlCheckBoxInput)lRow.getCell(0).getFirstChild()).setChecked(true);
		}
		
		HtmlForm lForm = getSuccess().getFormByName(inFormName);
		setSuccess(getActionPageChecked(lForm.getInputByName(inTask).click()));
		return true;
	}

	public String numberOf(String inState) throws Exception {
		int lNumberOf = 0;
		List<HtmlTableRow> lRows = getTableRows(String.format(XPATH_TR, "odd"), String.format(XPATH_TR, "even"));
		for (HtmlTableRow lRow : lRows) {
			lNumberOf += inState.equals(lRow.getCell(3).asText()) ? 1 : 0;
		}
		return String.valueOf(lNumberOf);
	}
	
}

/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

package org.hip.vif.admin.groupedit.data;

import java.sql.SQLException;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.QuestionHome;

import com.vaadin.data.util.BeanItemContainer;

/**
 * View model for the list of groups. 
 * 
 * @author Luthiger
 * Created: 06.11.2011
 */
@SuppressWarnings("serial")
public class GroupContainer extends BeanItemContainer<GroupWrapper> {
	public static final String GROUP_CHECK = "chk"; //$NON-NLS-1$
	public static final String GROUP_CHECKED = "checked"; //$NON-NLS-1$
	public static final String[] NATURAL_COL_ORDER = new String[] {GROUP_CHECK, "name", "description", "numberOfReviewers", "guestDepth", "minGroupSize", "state"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	public static final String[] COL_HEADERS = new String[] {"", "container.group.headers.name", "container.group.headers.description", "container.group.headers.number.of", "container.group.headers.guest.depth", "container.group.headers.min.group.size", "container.group.headers.state"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

	/**
	 * Private constructor.
	 */
	private GroupContainer() {
		super(GroupWrapper.class);
	}

	/**
	 * Factory method.
	 * 
	 * @param inResult {@link QueryResult}
	 * @param inCodeList {@link CodeList}
	 * @return {@link GroupContainer}
	 * @throws VException
	 * @throws SQLException
	 */
	public static GroupContainer createData(QueryResult inResult, CodeList inCodeList) throws VException, SQLException {
		GroupContainer out = new GroupContainer();
		QuestionHome lQuestionHome = BOMHelper.getQuestionHome();
		while (inResult.hasMoreElements()) {
			out.addItem(GroupWrapper.createItem(inResult.nextAsDomainObject(), inCodeList, lQuestionHome));
		}
		return out;
	}

}

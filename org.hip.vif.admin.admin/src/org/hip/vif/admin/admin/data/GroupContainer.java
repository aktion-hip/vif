/**
	This package is part of the application VIF.
	Copyright (C) 2011-2014, Benno Luthiger

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

package org.hip.vif.admin.admin.data;

import java.sql.SQLException;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;

import com.vaadin.data.util.BeanItemContainer;

/**
 * View model for group selection in send mail view.
 * 
 * @author Luthiger Created: 03.11.2011
 */
@SuppressWarnings("serial")
public class GroupContainer extends BeanItemContainer<GroupWrapper> {
	public static final String PROPERTY_CAPTION = "name"; //$NON-NLS-1$

	private GroupContainer() {
		super(GroupWrapper.class);
	}

	/**
	 * Static factory method to create an instance of
	 * <code>GroupContainer</code>.
	 * 
	 * @param inGroups
	 *            {@link QueryResult}
	 * @param inIsAdmin
	 *            boolean if <code>true</code>, an entry <code>All</code> is
	 *            added at top of the set.
	 * @return {@link GroupContainer}
	 * @throws VException
	 * @throws SQLException
	 */
	public static GroupContainer createData(final QueryResult inGroups,
			final boolean inIsAdmin) throws VException, SQLException {
		final GroupContainer out = new GroupContainer();
		if (inIsAdmin) {
			out.addItem(GroupWrapper.createAllItem());
		}
		while (inGroups.hasMoreElements()) {
			out.addItem(GroupWrapper.createItem(inGroups.nextAsDomainObject()));
		}
		return out;
	}

}

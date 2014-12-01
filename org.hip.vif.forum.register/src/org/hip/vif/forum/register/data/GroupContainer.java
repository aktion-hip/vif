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

package org.hip.vif.forum.register.data;

import java.sql.SQLException;
import java.util.Collection;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;

import com.vaadin.data.util.BeanItemContainer;

/** Container for group model instances.
 *
 * @author Luthiger Created: 21.05.2011 */
@SuppressWarnings("serial")
public class GroupContainer extends BeanItemContainer<GroupWrapper> {
    public static final String GROUP_CHK = "chk"; //$NON-NLS-1$
    public static final String GROUP_CHECKED = "checked"; //$NON-NLS-1$
    public static final Object[] NATURAL_COL_ORDER = new Object[] { GROUP_CHK,
        "name", "description", "registered", "state" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    public static final String[] COL_HEADERS = new String[] {
        "", "container.group.headers.short.description", "container.group.headers.description", "container.group.headers.number.of", "container.table.headers.state" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

    private GroupContainer() {
        super(GroupWrapper.class);
    }

    /** Factory method: Creates and returns an instance of <code>GroupContainer</code>.
     *
     * @param inGroups {@link QueryResult} the discussion groups
     * @param inRegisterings {@link Collection} the ids of groups the user has registered yet
     * @param inCodeList {@link CodeList} the code list for group states
     * @return {@link GroupContainer} the container filled with groups
     * @throws VException
     * @throws SQLException */
    public static GroupContainer createData(final QueryResult inGroups, final Collection<Long> inRegisterings,
            final CodeList inCodeList) throws VException, SQLException {
        final GroupContainer outGroups = new GroupContainer();
        while (inGroups.hasMoreElements()) {
            outGroups.addItem(GroupWrapper.createItem(inGroups.next(), inRegisterings, inCodeList));
        }
        inGroups.close();
        return outGroups;
    }

}

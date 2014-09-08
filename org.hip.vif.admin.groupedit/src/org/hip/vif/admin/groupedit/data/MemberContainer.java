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

package org.hip.vif.admin.groupedit.data;

import java.sql.SQLException;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.web.util.MemberBean;

import com.vaadin.data.util.BeanItemContainer;

/** Container (i.e. view model) for member model instances.
 *
 * @author Luthiger Created: 17.10.2011 */
@SuppressWarnings("serial")
public class MemberContainer extends BeanItemContainer<MemberBean> {
    public static final String MEMBER_CHECK = "chk"; //$NON-NLS-1$
    public static final String MEMBER_CHECKED = "checked"; //$NON-NLS-1$
    public static final Object[] NATURAL_COL_ORDER = new Object[] { MEMBER_CHECK, "userID", "name", "mail" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    public static String[] COL_HEADERS = new String[] {
            "", "container.member.headers.userid", "container.member.headers.name", "container.member.headers.mail" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    /** Private constructor. */
    private MemberContainer() {
        super(MemberBean.class);
    }

    /** Factory method: Creates and returns an instance of <code>MemberContainer</code>.
     *
     * @param inMembers {@link QueryResult}
     * @return {@link MemberContainer}
     * @throws VException
     * @throws SQLException */
    public static MemberContainer createData(final QueryResult inMembers) throws VException, SQLException {
        final MemberContainer outContainer = new MemberContainer();
        while (inMembers.hasMoreElements()) {
            outContainer.addItem(MemberBean.createItem(inMembers.nextAsDomainObject()));
        }
        return outContainer;
    }

}

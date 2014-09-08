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

package org.hip.vif.admin.permissions.data;

import java.sql.SQLException;
import java.util.Collection;

import org.hip.kernel.bom.AlternativeModel;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;

import com.vaadin.data.util.BeanItemContainer;

/** The container of permission objects.
 *
 * @author Luthiger Created: 15.12.2011 */
@SuppressWarnings("serial")
public class LoadedPermissionContainer extends BeanItemContainer<LoadedPermissionBean> {
    public static final String PERMISSION_CHECKED = "checked"; //$NON-NLS-1$
    public static final String FIELD_CHECK = ""; //$NON-NLS-1$
    public static final String FIELD_LABEL = "label"; //$NON-NLS-1$
    public static final String FIELD_ROLE_SU = "su"; //$NON-NLS-1$
    public static final String FIELD_ROLE_ADMIN = "admin"; //$NON-NLS-1$
    public static final String FIELD_ROLE_GROUPADMIN = "groupAdmin"; //$NON-NLS-1$
    public static final String FIELD_ROLE_PARTICIPANT = "participant"; //$NON-NLS-1$
    public static final String FIELD_ROLE_MEMBER = "member"; //$NON-NLS-1$
    public static final String FIELD_ROLE_GUEST = "guest"; //$NON-NLS-1$
    public static final String FIELD_ROLE_EXCLUDED = "excluded"; //$NON-NLS-1$
    public static final Object[] NATURAL_COL_ORDER = new Object[] {
        "", FIELD_LABEL, FIELD_ROLE_SU, FIELD_ROLE_ADMIN, FIELD_ROLE_GROUPADMIN, FIELD_ROLE_PARTICIPANT, FIELD_ROLE_MEMBER, FIELD_ROLE_GUEST, FIELD_ROLE_EXCLUDED }; //$NON-NLS-1$

    private LoadedPermissionContainer() {
        super(LoadedPermissionBean.class);
    }

    /** Factory method, loads the permissions into the container.
     *
     * @param inPermissions {@link QueryResult}
     * @param inAssociations {@link Collection} of <code>AlternativeModel</code>
     * @return {@link LoadedPermissionContainer}
     * @throws VException
     * @throws SQLException */
    public static LoadedPermissionContainer createData(final QueryResult inPermissions,
            final Collection<AlternativeModel> inAssociations) throws VException, SQLException {
        final LoadedPermissionContainer outData = new LoadedPermissionContainer();

        while (inPermissions.hasMoreElements()) {
            outData.addItem(LoadedPermissionBean.createItem(inPermissions.nextAsDomainObject(), inAssociations));
        }
        outData.sort(new Object[] { FIELD_LABEL }, new boolean[] { true });
        return outData;
    }

}

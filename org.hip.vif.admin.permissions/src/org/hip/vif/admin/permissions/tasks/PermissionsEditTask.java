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

package org.hip.vif.admin.permissions.tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.hip.kernel.bom.AlternativeModel;
import org.hip.kernel.bom.AlternativeModelFactory;
import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.Home;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.code.CodeListNotFoundException;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.hip.vif.admin.permissions.Activator;
import org.hip.vif.admin.permissions.Constants;
import org.hip.vif.admin.permissions.data.LoadedPermissionBean;
import org.hip.vif.admin.permissions.data.LoadedPermissionContainer;
import org.hip.vif.admin.permissions.ui.PermissionEditView;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.LinkPermissionRoleHome;
import org.hip.vif.core.bom.Permission;
import org.hip.vif.core.bom.PermissionHome;
import org.hip.vif.core.bom.impl.LinkPermissionRoleAlternate;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;

/** Task to edit permissions and their associations to rules.
 *
 * @author Luthiger Created: 14.12.2011 */
@UseCaseController
public class PermissionsEditTask extends AbstractWebController {
    private static final Logger LOG = LoggerFactory.getLogger(PermissionsEditTask.class);

    private LoadedPermissionContainer permissions;

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_EDIT;
    }

    @Override
    protected Component runChecked() throws RiplaException {
        try {
            emptyContextMenu();

            final CodeList lRoles = CodeListHome.instance().getCodeList(org.hip.vif.core.code.Role.class,
                    getAppLocale().getLanguage());
            permissions = LoadedPermissionContainer.createData(
                    BOMHelper.getPermissionHome().select(createOrder("ID", true)), retrieveAssociations()); //$NON-NLS-1$
            return new PermissionEditView(permissions, lRoles, this);
        } catch (final SQLException exc) {
            throw createContactAdminException(exc);
        } catch (final CodeListNotFoundException exc) {
            throw createContactAdminException(exc);
        } catch (final BOMException exc) {
            throw createContactAdminException(exc);
        } catch (final VException exc) {
            throw createContactAdminException(exc);
        }
    }

    /** Fills the content of the QueryResult in a Collection. We need this because Derby doesn't like to open
     * <code>java.sql.ResultSet</code>s at the same time.
     *
     * @return Collection<AlternativeModel>
     * @throws BOMException
     * @throws SQLException */
    private Collection<AlternativeModel> retrieveAssociations() throws BOMException, SQLException {
        final Home lHome = VSys.homeManager.getHome(LinkPermissionRoleAlternate.ALTERNATE_HOME_CLASS_NAME);
        final QueryResult lContent = ((DomainObjectHome) lHome).select();
        final Collection<AlternativeModel> outContent = lContent.load(new AlternativeModelFactory() {
            @Override
            public AlternativeModel createModel(final ResultSet inResultSet) throws SQLException {
                return new LinkPermissionRoleAlternate(inResultSet.getLong(LinkPermissionRoleHome.KEY_PERMISSION_ID),
                        inResultSet.getLong(LinkPermissionRoleHome.KEY_ROLE_ID));
            }
        });
        return outContent;
    }

    /** Callback method, deletes the selected permissions.
     *
     * @return boolean <code>true</code> if successful */
    public boolean deletePermissions() {
        try {
            final PermissionHome lPermissionHome = BOMHelper.getPermissionHome();
            final LinkPermissionRoleHome lLinkHome = BOMHelper.getLinkPermissionRoleHome();
            int i = 0;
            for (final LoadedPermissionBean lPermission : permissions.getItemIds()) {
                if (lPermission.isChecked()) {
                    final Long lPermissionID = lPermission.getPermissionID();
                    lLinkHome.delete(lPermissionID);
                    lPermissionHome.getPermission(lPermissionID).delete(true);
                    i++;
                }
            }
            showNotification(Activator.getMessages().getFormattedMessage("msg.permissions.deleted", i)); //$NON-NLS-1$
            sendEvent(PermissionsEditTask.class);
            return true;
        } catch (final VException exc) {
            LOG.error("Error encountered while deleting the permission!", exc); //$NON-NLS-1$
        } catch (final SQLException exc) {
            LOG.error("Error encountered while deleting the permission!", exc); //$NON-NLS-1$
        }
        return false;
    }

    /** Callback method, saves the changes to the database.
     *
     * @return boolean <code>true</code> if successful */
    public boolean saveChanges() {
        try {
            final LinkPermissionRoleHome lLinkHome = BOMHelper.getLinkPermissionRoleHome();
            for (final LoadedPermissionBean lPermission : permissions.getItemIds()) {
                if (lPermission.isDirty()) {
                    final Long lPermissionID = lPermission.getPermissionID();
                    lLinkHome.delete(lPermissionID);
                    createAssociations(lPermission, lPermissionID, lLinkHome);
                }
            }
            showNotification(Activator.getMessages().getMessage("msg.permissions.saved")); //$NON-NLS-1$
            return true;
        } catch (final VException exc) {
            LOG.error("Error encountered while saving the changes!", exc); //$NON-NLS-1$
        } catch (final SQLException exc) {
            LOG.error("Error encountered while saving the changes!", exc); //$NON-NLS-1$
        }
        return false;
    }

    private void createAssociations(final LoadedPermissionBean inPermission, final Long inPermissionID,
            final LinkPermissionRoleHome inLinkHome) throws VException {
        for (final LoadedPermissionBean.Role lRole : LoadedPermissionBean.Role.values()) {
            if (inPermission.getRoleValue(lRole)) {
                inLinkHome.createLink(inPermissionID, lRole.getID());
            }
        }
    }

    /** Callback method: creates a new permission with the specified values.
     *
     * @param inLabel String
     * @param inDescription String
     * @return boolean <code>true</code> if successful
     * @throws ExternIDNotUniqueException */
    public boolean createPermission(final String inLabel, final String inDescription) throws ExternIDNotUniqueException {
        try {
            final Permission lPermission = (Permission) BOMHelper.getPermissionHome().create();
            lPermission.ucNew(inLabel, inDescription);

            showNotification(Activator.getMessages().getFormattedMessage("msg.permissions.added", inLabel)); //$NON-NLS-1$
            sendEvent(PermissionsEditTask.class);
            return true;
        } catch (final BOMChangeValueException exc) {
            LOG.error("Error encountered while creating the permission!", exc); //$NON-NLS-1$
        } catch (final ExternIDNotUniqueException exc) {
            throw exc;
        } catch (final BOMException exc) {
            LOG.error("Error encountered while creating the permission!", exc); //$NON-NLS-1$
        }
        return false;
    }

}

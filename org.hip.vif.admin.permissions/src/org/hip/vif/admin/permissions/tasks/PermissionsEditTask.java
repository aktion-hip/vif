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
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.hip.vif.admin.permissions.Activator;
import org.hip.vif.admin.permissions.Constants;
import org.hip.vif.admin.permissions.data.LoadedPermissionBean;
import org.hip.vif.admin.permissions.data.LoadedPermissionContainer;
import org.hip.vif.admin.permissions.ui.PermissionEditView;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.LinkPermissionRoleHome;
import org.hip.vif.core.bom.Permission;
import org.hip.vif.core.bom.PermissionHome;
import org.hip.vif.core.bom.impl.LinkPermissionRoleAlternate;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.hip.vif.web.tasks.AbstractVIFTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;

/**
 * Task to edit permissions and their associations to rules.
 * 
 * @author Luthiger
 * Created: 14.12.2011
 */
@Partlet
public class PermissionsEditTask extends AbstractVIFTask {
	private static final Logger LOG = LoggerFactory.getLogger(PermissionsEditTask.class);

	private LoadedPermissionContainer permissions;

	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#needsPermission()
	 */
	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_EDIT;
	}

	@Override
	protected Component runChecked() throws VException {
		try {
			emptyContextMenu();
			
			CodeList lRoles = CodeListHome.instance().getCodeList(org.hip.vif.core.code.Role.class, getAppLocale().getLanguage());
			permissions = LoadedPermissionContainer.createData(BOMHelper.getPermissionHome().select(createOrder("ID", true)), retrieveAssociations()); //$NON-NLS-1$
			return new PermissionEditView(permissions, lRoles, this);
		}
		catch (SQLException exc) {
			throw createContactAdminException(exc);
		}
	}
	
	/**
	 * Fills the content of the QueryResult in a Collection. We need this because Derby doesn't like to open <code>java.sql.ResultSet</code>s at the same time.
	 * 
	 * @return Collection<AlternativeModel>
	 * @throws BOMException
	 * @throws SQLException
	 */
	private Collection<AlternativeModel> retrieveAssociations() throws BOMException, SQLException {
		Home lHome = VSys.homeManager.getHome(LinkPermissionRoleAlternate.ALTERNATE_HOME_CLASS_NAME);
		QueryResult lContent = ((DomainObjectHome)lHome).select();
		Collection<AlternativeModel> outContent = lContent.load(new AlternativeModelFactory() {
			public AlternativeModel createModel(ResultSet inResultSet) throws SQLException {
				return new LinkPermissionRoleAlternate(inResultSet.getLong(LinkPermissionRoleHome.KEY_PERMISSION_ID), 
												   inResultSet.getLong(LinkPermissionRoleHome.KEY_ROLE_ID));
			}				
		});
		return outContent;
	}

	/**
	 * Callback method, deletes the selected permissions.
	 * 
	 * @return boolean <code>true</code> if successful
	 */
	public boolean deletePermissions() {
		try {
			PermissionHome lPermissionHome = BOMHelper.getPermissionHome();
			LinkPermissionRoleHome lLinkHome = BOMHelper.getLinkPermissionRoleHome();
			int i = 0;
			for (LoadedPermissionBean lPermission : permissions.getItemIds()) {
				if (lPermission.isChecked()) {
					Long lPermissionID = lPermission.getPermissionID();
					lLinkHome.delete(lPermissionID);
					lPermissionHome.getPermission(lPermissionID).delete(true);
					i++;
				}
			}
			showNotification(Activator.getMessages().getFormattedMessage("msg.permissions.deleted", i)); //$NON-NLS-1$
			sendEvent(PermissionsEditTask.class);
			return true;
		}
		catch (VException exc) {
			LOG.error("Error encountered while deleting the permission!", exc); //$NON-NLS-1$
		}
		catch (SQLException exc) {
			LOG.error("Error encountered while deleting the permission!", exc); //$NON-NLS-1$
		}
		return false;
	}

	/**
	 * Callback method, saves the changes to the database.
	 * 
	 * @return boolean <code>true</code> if successful
	 */
	public boolean saveChanges() {
		try {
			LinkPermissionRoleHome lLinkHome = BOMHelper.getLinkPermissionRoleHome();
			for (LoadedPermissionBean lPermission : permissions.getItemIds()) {
				if (lPermission.isDirty()) {
					Long lPermissionID = lPermission.getPermissionID();
					lLinkHome.delete(lPermissionID);
					createAssociations(lPermission, lPermissionID, lLinkHome);
				}
			}
			showNotification(Activator.getMessages().getMessage("msg.permissions.saved")); //$NON-NLS-1$
			return true;
		}
		catch (VException exc) {
			LOG.error("Error encountered while saving the changes!", exc); //$NON-NLS-1$
		}
		catch (SQLException exc) {
			LOG.error("Error encountered while saving the changes!", exc); //$NON-NLS-1$
		}
		return false;
	}

	private void createAssociations(LoadedPermissionBean inPermission, Long inPermissionID, LinkPermissionRoleHome inLinkHome) throws VException {
		for (LoadedPermissionBean.Role lRole : LoadedPermissionBean.Role.values()) {
			if (inPermission.getRoleValue(lRole)) {
				inLinkHome.createLink(inPermissionID, lRole.getID());
			}
		}
	}

	/**
	 * Callback method: creates a new permission with the specified values.
	 * 
	 * @param inLabel String
	 * @param inDescription String
	 * @return boolean <code>true</code> if successful
	 * @throws ExternIDNotUniqueException 
	 */
	public boolean createPermission(String inLabel, String inDescription) throws ExternIDNotUniqueException {
		try {
			Permission lPermission = (Permission)BOMHelper.getPermissionHome().create();
			lPermission.ucNew(inLabel, inDescription);
			
			showNotification(Activator.getMessages().getFormattedMessage("msg.permissions.added", inLabel)); //$NON-NLS-1$
			sendEvent(PermissionsEditTask.class);
			return true;
		}
		catch (BOMChangeValueException exc) {
			LOG.error("Error encountered while creating the permission!", exc); //$NON-NLS-1$
		}
		catch (ExternIDNotUniqueException exc) {
			throw exc;
		}
		catch (BOMException exc) {
			LOG.error("Error encountered while creating the permission!", exc); //$NON-NLS-1$
		}
		return false;
	}

}

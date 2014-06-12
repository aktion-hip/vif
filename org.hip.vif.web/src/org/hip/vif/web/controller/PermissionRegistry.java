/*
 This package is part of the application VIF.
 Copyright (C) 2010, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.web.controller;

import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.BOMException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.LinkPermissionRoleHome;
import org.hip.vif.core.bom.PermissionHome;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.hip.vif.core.interfaces.IPermissionRecord;
import org.hip.vif.core.interfaces.IPermissionRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Singleton to delay the creation of registered permissions records to the application's servlet initialization.
 * </p><p>
 * Using OSGi DS, every bundle can register permissions used in the bundle dynamically. 
 * However, the permissions probably can't be created at this time because the persistence manager might not be configured correctly yet.
 * Therefore, we delay the permission creation to the servlet's init hook.
 * </p>
 *
 * @author Luthiger
 * Created: 08.12.2010
 */
public enum PermissionRegistry {
	INSTANCE;
	
	private static final Logger LOG = LoggerFactory.getLogger(PermissionRegistry.class);
	
	private Collection<IPermissionRecords> permissionRecords = new Vector<IPermissionRecords>();
	private boolean initialized = false;
	
	/**
	 * Registers the permissions for that they can be created after during servlet initialization.<br />
	 * This registration must be called by the <code>BundleController</code>.<br />
	 * If the application is initialized already, the permissions are created immediately. 
	 * 
	 * @param inPermissionRecords IPermissionRecords
	 */
	public void registerPermissions(IPermissionRecords inPermissionRecords) {
		synchronized (permissionRecords) {
			if (initialized) {
				processPermissionRecords(inPermissionRecords, BOMHelper.getPermissionHome(), BOMHelper.getLinkPermissionRoleHome());
				return;
			}
			permissionRecords.add(inPermissionRecords);
		}
	}
	
	/**
	 * Executes the scheduled creation of permissions.<br />
	 * This method must be called in the servlet's init() method.
	 */
	public void createPermissions() {
		if (permissionRecords.isEmpty()) return;
		
		synchronized (permissionRecords) {			
			PermissionHome lPermissionHome = BOMHelper.getPermissionHome();
			LinkPermissionRoleHome lLinkHome = BOMHelper.getLinkPermissionRoleHome();
			for (IPermissionRecords lPermissionRecords : permissionRecords) {
				processPermissionRecords(lPermissionRecords, lPermissionHome, lLinkHome);
			}
			permissionRecords.clear();
			initialized  = true;
		}
	}
	
	private void processPermissionRecords(IPermissionRecords inPermissionRecords, PermissionHome inPermissionHome, LinkPermissionRoleHome inLinkHome) {		
		for (IPermissionRecord lPermissionRecord : inPermissionRecords.getPermissionRecords()) {
			try {
				Long lPermissionID = inPermissionHome.createPermission(lPermissionRecord.getPermissionLabel(), lPermissionRecord.getPermissionDescription());
				for (int lRole : lPermissionRecord.getPermittedRoles()) {
					inLinkHome.createLink(lPermissionID, lRole);
				}
			} 
			catch (ExternIDNotUniqueException exc) {
				// intentionally left empty
			} 
			catch (BOMException exc) {
				LOG.error("Error encountered while creating permission records!", exc); //$NON-NLS-1$
			}
		}
	}

}

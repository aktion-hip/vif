package org.hip.vif.useradmin.internal;

import org.osgi.service.useradmin.UserAdmin;
import org.ripla.useradmin.admin.RiplaUserAdmin;

/**
 * Provider of the OSGi <code>UserAdmin</code> service.
 * 
 * @author Luthiger
 */
public class VifUserAdmin extends RiplaUserAdmin implements UserAdmin {

	/**
	 * @throws Exception
	 */
	public VifUserAdmin() throws Exception { // NOPMD by Luthiger
		super();
	}

}

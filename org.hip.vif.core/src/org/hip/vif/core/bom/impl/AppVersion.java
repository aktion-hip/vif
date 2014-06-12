package org.hip.vif.core.bom.impl;

import org.hip.kernel.bom.impl.DomainObjectImpl;

/**
 * The AppVersion model: stores the application's actual version for that
 * the application's upgrader can check the need for an upgrade. 
 * 
 * @author Luthiger
 * Created: 16.02.2012
 */
@SuppressWarnings("serial")
public class AppVersion extends DomainObjectImpl {

	final static public String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.AppVersionHome";

	/**
	 * This Method returns the class name of the home.
	 *
	 * @return java.lang.String
	 */
	public String getHomeClassName() {
		return HOME_CLASS_NAME;
	}

}

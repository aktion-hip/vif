/*
 This package is part of the application VIF.
 Copyright (C) 2010, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.core.bom.impl;

import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.kernel.exc.VException;

/**
 * Model to calculate the max value of bibliography entries.
 *
 * @author Luthiger
 * Created: 13.08.2010
 */
public class TextMax extends DomainObjectImpl {
	public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.TextMaxHome";

	/**
	 * This Method returns the class name of the home.
	 *
	 * @return java.lang.String
	 */
	public String getHomeClassName() {
		return HOME_CLASS_NAME;
	}
	
	/**
	 * Returns the maximum version.
	 * 
	 * @return int this entry's max version value.
	 * @throws VException
	 */
	public int getMaxVersion() throws VException {
		Object out = get(TextMaxHome.KEY_MAX_VERSION);
		return out == null ? 0 : Integer.parseInt(out.toString());
	}

}

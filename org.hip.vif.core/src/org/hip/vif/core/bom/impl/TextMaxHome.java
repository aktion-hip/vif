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

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.TextHome;

/**
 * Home to calculate the max value of bibliography entries.
 *
 * @author Luthiger
 * Created: 13.08.2010
 */
@SuppressWarnings("serial")
public class TextMaxHome extends JoinedDomainObjectHomeImpl {
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.TextMax";
	
	public static final String KEY_MAX_VERSION = "MaxVersion";
	
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<joinedObjectDef objectName='TextMax' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	" +
		"	<columnDefs>	" +
		"		<hidden columnName='" + TextHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	" +
		"		<columnDef columnName='" + TextHome.KEY_VERSION + "' modifier='MAX' as='" + KEY_MAX_VERSION + "' valueType='Number' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	" +
		"	</columnDefs>	" +
		"	<joinDef joinType='NO_JOIN'>	" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.TextImpl'/>	" +
		"	</joinDef>	" +
		"</joinedObjectDef>";

	/**
	 * Returns the name of the objects which this home can create.
	 *
	 * @return java.lang.String
	 */
	public String getObjectClassName() {
		return OBJECT_CLASS_NAME;
	}

	/**
	 * Returns the object definition string of the class managed by this home.
	 *
	 * @return java.lang.String
	 */
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}
	
	/**
	 * Returns the max version of the bibliography entry with the specified ID. 
	 * 
	 * @param inTextID Long
	 * @return int the maximum version
	 * @throws VException
	 */
	public int getMaxVersion(Long inTextID) throws VException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(TextHome.KEY_ID, inTextID);
		TextMax lMax = (TextMax) findByKey(lKey);
		return lMax.getMaxVersion();
	}

}

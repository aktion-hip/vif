/**
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2001-2014, Benno Luthiger

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
package org.hip.vif.core.bom.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.AbstractDomainObjectHome;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.RoleHome;

/**
 * This domain object home implements the RoleHome interface.
 * 
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.RoleHome
 */
@SuppressWarnings("serial")
public class RoleHomeImpl extends DomainObjectHomeImpl implements RoleHome {

	/*
	 * Every home has to know the class it handles. They provide access to this
	 * name through the method <I>getObjectClassName</I>;
	 */
	private final static String ROLE_CLASS_NAME = "org.hip.vif.core.bom.impl.RoleImpl";

	/*
	 * The current version of the domain object framework provides no support
	 * for externelized metadata. We build them up with hard coded definition
	 * strings.
	 */
	// CAUTION: The current version of the lightweight DomainObject
	// framework makes only a limited check of the correctness
	// of the definition string. Make extensive basic test to
	// ensure that the definition works correct.
	private final static String XML_OBJECT_DEF = "<?xml version='1.0' encoding='ISO-8859-1'?>	\n"
			+ "<objectDef objectName='Role' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	\n"
			+ "	<keyDefs>	\n"
			+ "		<keyDef>	\n"
			+ "			<keyItemDef seq='0' keyPropertyName='"
			+ KEY_ID
			+ "'/>	\n"
			+ "		</keyDef>	\n"
			+ "	</keyDefs>	\n"
			+ "	<propertyDefs>	\n"
			+ "		<propertyDef propertyName='"
			+ KEY_ID
			+ "' valueType='Number' propertyType='simple'>	\n"
			+ "			<mappingDef tableName='tblRole' columnName='RoleID'/>	\n"
			+ "		</propertyDef>	\n"
			+ "		<propertyDef propertyName='"
			+ KEY_CODE_ID
			+ "' valueType='String' propertyType='simple'>	\n"
			+ "			<mappingDef tableName='tblRole' columnName='sXMLRoleID'/>	\n"
			+ "		</propertyDef>	\n"
			+ "		<propertyDef propertyName='"
			+ KEY_DESCRIPTION
			+ "' valueType='String' propertyType='simple'>	\n"
			+ "			<mappingDef tableName='tblRole' columnName='sRoleDescription'/>	\n"
			+ "		</propertyDef>	\n"
			+ "		<propertyDef propertyName='"
			+ KEY_GROUP_SPECIFIC
			+ "' valueType='Number' propertyType='simple'>	\n"
			+ "			<mappingDef tableName='tblRole' columnName='bGroupSpecific'/>	\n"
			+ "		</propertyDef>	\n" + "	</propertyDefs>	\n" + "</objectDef>";

	/**
	 * Constructor for RoleHomeImpl.
	 */
	public RoleHomeImpl() {
		super();
	}

	/**
	 * @see GeneralDomainObjectHome#getObjectClassName()
	 */
	@Override
	public String getObjectClassName() {
		return ROLE_CLASS_NAME;
	}

	/**
	 * @see AbstractDomainObjectHome#getObjectDefString()
	 */
	@Override
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}

	@Override
	public Collection<String> getGroupSpecificIDs() throws VException,
			SQLException {
		final Collection<String> out = new Vector<String>();
		final KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(KEY_GROUP_SPECIFIC, 1l);
		final QueryResult lSelected = select(lKey);
		while (lSelected.hasMoreElements()) {
			out.add(lSelected.nextAsDomainObject().get(KEY_CODE_ID).toString());
		}
		return out;
	}

}

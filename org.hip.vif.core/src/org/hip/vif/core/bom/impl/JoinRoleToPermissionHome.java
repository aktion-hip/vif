/*
	This package is part of application VIF.
	Copyright (C) 2001, Benno Luthiger

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

import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.vif.core.bom.LinkPermissionRoleHome;
import org.hip.vif.core.bom.PermissionHome;

/** Home of join from the Role BOM to the Permission BOM
 *
 * Created on 26.08.2002
 * 
 * @author Benno Luthiger */
@SuppressWarnings("serial")
public class JoinRoleToPermissionHome extends JoinedDomainObjectHomeImpl {
    public final static String KEY_ALIAS_PERMISSION_ID = "PermissionID";
    public final static String KEY_ALIAS_PERMISSION_LABEL = "PermissionLabel";

    // Every home has to know the class it handles. They provide access to
    // this name through the method <I>getObjectClassName</I>;
    private final static String JOINED_OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinRoleToPermission";

    /*
     * The current version of the domain object framework provides no support for externelized metadata. We build them
     * up with hard coded definition strings.
     */
    // CAUTION: The current version of the lightweight DomainObject
    // framework makes only a limited check of the correctness
    // of the definition string. Make extensive basic test to
    // ensure that the definition works correct.

    private final static String XML_OBJECT_DEF =
            "<?xml version='1.0' encoding='ISO-8859-1'?>	\n"
                    +
                    "<joinedObjectDef objectName='JoinMemberToPermission' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n"
                    +
                    "	<columnDefs>	\n" +
                    "		<columnDef columnName='"
                    + LinkPermissionRoleHome.KEY_ROLE_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.LinkPermissionRoleImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + PermissionHome.KEY_ID
                    + "' alias='"
                    + KEY_ALIAS_PERMISSION_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.PermissionImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + PermissionHome.KEY_LABEL
                    + "' alias='"
                    + KEY_ALIAS_PERMISSION_LABEL
                    + "' domainObject='org.hip.vif.core.bom.impl.PermissionImpl'/>	\n"
                    +
                    "	</columnDefs>	\n"
                    +
                    "	<joinDef joinType='EQUI_JOIN'>	\n"
                    +
                    "		<objectDesc objectClassName='org.hip.vif.core.bom.impl.LinkPermissionRoleImpl'/>	\n"
                    +
                    "		<objectDesc objectClassName='org.hip.vif.core.bom.impl.PermissionImpl'/>	\n"
                    +
                    "		<joinCondition>	\n"
                    +
                    "			<columnDef columnName='"
                    + LinkPermissionRoleHome.KEY_PERMISSION_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.LinkPermissionRoleImpl'/>	\n"
                    +
                    "			<columnDef columnName='"
                    + PermissionHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.PermissionImpl'/>	\n" +
                    "		</joinCondition>	\n" +
                    "	</joinDef>	\n" +
                    "</joinedObjectDef>";

    /** Constructor for JoinRoleToPermissionHome. */
    public JoinRoleToPermissionHome() {
        super();
    }

    /** @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectClassName() */
    @Override
    public String getObjectClassName() {
        return JOINED_OBJECT_CLASS_NAME;
    }

    /** @see org.hip.kernel.bom.impl.AbstractDomainObjectHome#getObjectDefString() */
    @Override
    protected String getObjectDefString() {
        return XML_OBJECT_DEF;
    }

}

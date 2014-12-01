/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2003, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public
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
package org.hip.vif.core.bom.impl;

import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.ParticipantHome;

/** Home to select all groups with all participants registered. This home can be used to filter all public groups.
 *
 * @author Benno Luthiger Created on Dec 31, 2003 */
@SuppressWarnings("serial")
public class NestedGroupHome extends JoinedDomainObjectHomeImpl {
    private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.NestedGroup";

    public final static String KEY_REGISTERED = "Registered";
    public final static String KEY_GROUP_ID = "GroupID";

    private final static String XML_OBJECT_DEF =
            "<?xml version='1.0' encoding='ISO-8859-1'?>	\n"
                    +
                    "<joinedObjectDef objectName='NestedGroup' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n"
                    +
                    "	<columnDefs>	\n" +
                    "		<columnDef columnName='"
                    + GroupHome.KEY_NAME
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + GroupHome.KEY_ID
                    + "' alias='"
                    + KEY_GROUP_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + GroupHome.KEY_DESCRIPTION
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + GroupHome.KEY_MIN_GROUP_SIZE
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + GroupHome.KEY_STATE
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + GroupHome.KEY_REVIEWERS
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + GroupHome.KEY_GUEST_DEPTH
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + GroupHome.KEY_PRIVATE
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + KEY_REGISTERED
                    + "' nestedObject='count' valueType='Number'/>	\n"
                    +
                    "	</columnDefs>	\n"
                    +
                    "	<joinDef joinType='EQUI_JOIN'>	\n"
                    +
                    "		<objectDesc objectClassName='org.hip.vif.core.bom.impl.GroupImpl'/>	\n"
                    +
                    "		<objectNested name='count'>	\n"
                    +
                    "			<columnDef columnName='"
                    + ParticipantHome.KEY_GROUP_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.ParticipantImpl'/>	\n"
                    +
                    "			<columnDef columnName='"
                    + ParticipantHome.KEY_MEMBER_ID
                    + "' as='"
                    + KEY_REGISTERED
                    + "' modifier='COUNT' domainObject='org.hip.vif.core.bom.impl.ParticipantImpl'/>	\n"
                    +
                    "			<resultGrouping modifier='GROUP'>	\n"
                    +
                    "				<columnDef columnName='"
                    + ParticipantHome.KEY_GROUP_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.ParticipantImpl'/>	\n"
                    +
                    "			</resultGrouping>	\n"
                    +
                    "		</objectNested>	\n"
                    +
                    "		<joinCondition>	\n"
                    +
                    "			<columnDef columnName='"
                    + GroupHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	\n" +
                    "			<columnDef columnName='" + ParticipantHome.KEY_GROUP_ID + "' nestedObject='count'/>	\n" +
                    "		</joinCondition>	\n" +
                    "	</joinDef>	\n" +
                    "</joinedObjectDef>";

    /** NestedGroupHome constructor. */
    public NestedGroupHome() {
        super();
    }

    /** Returns the name of the objects which this home can create.
     *
     * @return java.lang.String */
    @Override
    public String getObjectClassName() {
        return OBJECT_CLASS_NAME;
    }

    /** Returns the object definition string of the class managed by this home.
     *
     * @return java.lang.String */
    @Override
    protected String getObjectDefString() {
        return XML_OBJECT_DEF;
    }

}

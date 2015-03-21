/*
	This package is part of application VIF.
	Copyright (C) 2003, Benno Luthiger

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
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.ParticipantHome;

/** Home of join from the Participant BOM to the Group BOM This join can be used to retrieve the data of groups a member
 * is participating.
 *
 * @author Benno Luthiger */
@SuppressWarnings("serial")
public class JoinParticipantToGroupHome extends JoinedDomainObjectHomeImpl {
    // Every home has to know the class it handles. They provide access to
    // this name through the method <I>getObjectClassName</I>;
    private final static String JOIN_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinParticipantToGroup";

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
                    "<joinedObjectDef objectName='JoinParticipantToGroup' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n"
                    +
                    "	<columnDefs>	\n" +
                    "		<columnDef columnName='"
                    + ParticipantHome.KEY_MEMBER_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.ParticipantImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + GroupHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + GroupHome.KEY_NAME
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + GroupHome.KEY_DESCRIPTION
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
                    + GroupHome.KEY_MIN_GROUP_SIZE
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + GroupHome.KEY_STATE
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + GroupHome.KEY_PRIVATE
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	\n"
                    +
                    "	</columnDefs>	\n"
                    +
                    "	<joinDef joinType='EQUI_JOIN'>	\n"
                    +
                    "		<objectDesc objectClassName='org.hip.vif.core.bom.impl.ParticipantImpl'/>	\n"
                    +
                    "		<objectDesc objectClassName='org.hip.vif.core.bom.impl.GroupImpl'/>	\n"
                    +
                    "		<joinCondition>	\n"
                    +
                    "			<columnDef columnName='"
                    + ParticipantHome.KEY_GROUP_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.ParticipantImpl'/>	\n"
                    +
                    "			<columnDef columnName='"
                    + GroupHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	\n" +
                    "		</joinCondition>	\n" +
                    "	</joinDef>	\n" +
                    "</joinedObjectDef>";

    /** Constructor for JoinParticipantToGroupHome. */
    public JoinParticipantToGroupHome() {
        super();
    }

    /** @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectClassName() */
    @Override
    public String getObjectClassName() {
        return JOIN_CLASS_NAME;
    }

    /** @see org.hip.kernel.bom.impl.AbstractDomainObjectHome#getObjectDefString() */
    @Override
    protected String getObjectDefString() {
        return XML_OBJECT_DEF;
    }
}

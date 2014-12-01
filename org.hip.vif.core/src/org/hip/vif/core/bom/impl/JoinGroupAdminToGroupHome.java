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

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.GroupAdminHome;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.exc.BOMChangeValueException;

/** Home of join from the GroupAdmin BOM to the Group BOM This join can be used to retrieve the data of groups a group
 * administrator is administering.
 *
 * Created on 27.09.2002
 * 
 * @author Benno Luthiger */
@SuppressWarnings("serial")
public class JoinGroupAdminToGroupHome extends JoinedDomainObjectHomeImpl {
    // Every home has to know the class it handles. They provide access to
    // this name through the method <I>getObjectClassName</I>;
    private final static String JOIN_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinGroupAdminToGroup";

    private final static String XML_OBJECT_DEF =
            "<?xml version='1.0' encoding='ISO-8859-1'?>	\n"
                    +
                    "<joinedObjectDef objectName='JoinGroupAdminToGroup' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n"
                    +
                    "	<columnDefs>	\n" +
                    "		<columnDef columnName='"
                    + GroupAdminHome.KEY_MEMBER_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupAdminImpl'/>	\n"
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
                    "	</columnDefs>	\n"
                    +
                    "	<joinDef joinType='EQUI_JOIN'>	\n"
                    +
                    "		<objectDesc objectClassName='org.hip.vif.core.bom.impl.GroupAdminImpl'/>	\n"
                    +
                    "		<objectDesc objectClassName='org.hip.vif.core.bom.impl.GroupImpl'/>	\n"
                    +
                    "		<joinCondition>	\n"
                    +
                    "			<columnDef columnName='"
                    + GroupAdminHome.KEY_GROUP_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupAdminImpl'/>	\n"
                    +
                    "			<columnDef columnName='"
                    + GroupHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	\n" +
                    "		</joinCondition>	\n" +
                    "	</joinDef>	\n" +
                    "</joinedObjectDef>";

    /** Constructor for JoinGroupAdminToGroupHome. */
    public JoinGroupAdminToGroupHome() {
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

    /** Returns the groups the specified member/group administrator is administering.
     * 
     * @param inMemberID java.lang.Long
     * @param inOrder org.hip.kernel.bom.OrderObject
     * @return {@link QueryResult}
     * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException */
    public QueryResult select(final Long inMemberID, final OrderObject inOrder) throws BOMChangeValueException {
        try {
            final KeyObject lKey = new KeyObjectImpl();
            lKey.setValue("MemberID", inMemberID);
            return select(lKey, inOrder);
        } catch (final VException exc) {
            throw new BOMChangeValueException(exc.getMessage());
        } catch (final SQLException exc) {
            throw new BOMChangeValueException(exc.getMessage());
        }
    }

    /** Returns the number of groups the specified member/group administrator is administering.
     * 
     * @param inMemberID java.lang.Integer
     * @return int
     * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException */
    public int getCount(final Integer inMemberID) throws BOMChangeValueException {
        try {
            final KeyObject lKey = new KeyObjectImpl();
            lKey.setValue(GroupAdminHome.KEY_MEMBER_ID, inMemberID);
            return getCount(lKey);
        } catch (final VException exc) {
            throw new BOMChangeValueException(exc.getMessage());
        } catch (final SQLException exc) {
            throw new BOMChangeValueException(exc.getMessage());
        }
    }
}

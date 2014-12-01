/*
	This package is part of the persistency layer of the application VIF.
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
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.AbstractDomainObjectHome;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.GroupAdminHome;
import org.hip.vif.core.exc.BOMChangeValueException;

/** This domain object home implements the GroupAdminHome interface.
 *
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.GroupAdminHome */
@SuppressWarnings("serial")
public class GroupAdminHomeImpl extends DomainObjectHomeImpl implements GroupAdminHome {

    /*
     * Every home has to know the class it handles. They provide access to this name through the method
     * <I>getObjectClassName</I>;
     */
    private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.GroupAdminImpl";

    private final static String XML_OBJECT_DEF =
            "<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
                    "<objectDef objectName='GroupAdmin' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	\n" +
                    "	<keyDefs>	\n" +
                    "		<keyDef>	\n" +
                    "			<keyItemDef seq='0' keyPropertyName='" + KEY_GROUP_ID + "'/>	\n" +
                    "			<keyItemDef seq='1' keyPropertyName='" + KEY_MEMBER_ID + "'/>	\n" +
                    "		</keyDef>	\n" +
                    "	</keyDefs>	\n" +
                    "	<propertyDefs>	\n" +
                    "		<propertyDef propertyName='" + KEY_GROUP_ID + "' valueType='Long' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblGroupAdmin' columnName='GroupID'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + KEY_MEMBER_ID + "' valueType='Long' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblGroupAdmin' columnName='MemberID'/>	\n" +
                    "		</propertyDef>	\n" +
                    "	</propertyDefs>	\n" +
                    "</objectDef>";

    /** @see GeneralDomainObjectHome#getObjectClassName() */
    @Override
    public String getObjectClassName() {
        return OBJECT_CLASS_NAME;
    }

    /** @see AbstractDomainObjectHome#getObjectDefString() */
    @Override
    protected String getObjectDefString() {
        return XML_OBJECT_DEF;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hip.vif.core.bom.GroupAdminHome#associateGroupAdmins(java.lang.Long, java.lang.String[])
     */
    @Override
    public void associateGroupAdmins(final Long inGroupID, final String[] inGroupAdmins) throws BOMChangeValueException {
        final Collection<Long> lAdminIDs = new Vector<Long>(inGroupAdmins.length);
        for (int i = 0; i < inGroupAdmins.length; i++) {
            lAdminIDs.add(Long.valueOf(inGroupAdmins[i]));
        }
        associateGroupAdmins(inGroupID, lAdminIDs);
    }

    @Override
    public void associateGroupAdmins(final Long inGroupID, final Collection<Long> inGroupAdmins)
            throws BOMChangeValueException {
        try {
            for (final Long lMemberID : inGroupAdmins) {
                if (notFound(inGroupID, lMemberID)) {
                    associateGroupAdmin(inGroupID, lMemberID);
                }
            }
        } catch (final VException exc) {
            throw new BOMChangeValueException(exc.getMessage());
        } catch (final SQLException exc) {
            throw new BOMChangeValueException(exc.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hip.vif.core.bom.GroupAdminHome#associateGroupAdmin(java.lang.Long, java.lang.Long)
     */
    @Override
    public void associateGroupAdmin(final Long inGroupID, final Long inMemberID) throws VException, SQLException {
        final DomainObject lGroupAdmin = create();
        lGroupAdmin.set(GroupAdminHome.KEY_GROUP_ID, inGroupID);
        lGroupAdmin.set(GroupAdminHome.KEY_MEMBER_ID, inMemberID);
        lGroupAdmin.insert(true);
    }

    private boolean notFound(final Long inGroupID, final Long inMemberID) {
        try {
            final KeyObject lKey = new KeyObjectImpl();
            lKey.setValue(GroupAdminHome.KEY_GROUP_ID, inGroupID);
            lKey.setValue(GroupAdminHome.KEY_MEMBER_ID, inMemberID);
            return findByKey(lKey) == null;
        } catch (final VException exc) {
            return true;
        }
    }

    /** Tests whether the specified member is group admin.
     * 
     * @param inMemberID Long
     * @return boolean true, if the member is group admin.
     * @throws VException */
    @Override
    public boolean isGroupAdmin(final Long inMemberID) throws VException {
        try {
            final KeyObject lKey = new KeyObjectImpl();
            lKey.setValue(GroupAdminHome.KEY_MEMBER_ID, inMemberID);
            return getCount(lKey) > 0;
        } catch (final SQLException exc) {
            throw new VException(exc.getMessage());
        }
    }

    /** Tests whether the specified member is admin of the specified group.
     * 
     * @param inMemberID Long
     * @param inGroupID Long
     * @return boolean true, if the member is admin of the specified group.
     * @throws VException */
    @Override
    public boolean isGroupAdmin(final Long inMemberID, final Long inGroupID) throws BOMChangeValueException {
        try {
            final KeyObject lKey = new KeyObjectImpl();
            lKey.setValue(GroupAdminHome.KEY_MEMBER_ID, inMemberID);
            lKey.setValue(GroupAdminHome.KEY_GROUP_ID, inGroupID);
            findByKey(lKey);
            return true;
        } catch (final BOMNotFoundException exc) {
            return false;
        } catch (final VException exc) {
            throw new BOMChangeValueException(exc.getMessage());
        }
    }

    /** Tests the specified list of member ids and returns a collection of ids of those members which are not group
     * administrators.
     * 
     * @param inMemberIDs String[]
     * @return Collection<String> of ids of those members which are not group administrators.
     * @throws VException */
    @Override
    public Collection<String> checkGroupAdmins(final String[] inMemberIDs) throws VException {
        final Collection<String> outNoAdmin = new Vector<String>();
        for (int i = 0; i < inMemberIDs.length; i++) {
            if (!isGroupAdmin(new Long(inMemberIDs[i]))) {
                outNoAdmin.add(inMemberIDs[i]);
            }
        }
        return outNoAdmin;
    }

}

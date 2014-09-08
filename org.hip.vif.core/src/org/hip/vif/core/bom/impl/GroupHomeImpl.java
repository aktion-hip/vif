/**
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2003-2014, Benno Luthiger

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

import org.hip.kernel.bom.BOMInvalidKeyException;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.EmptyQueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.RoleHome;
import org.hip.vif.core.exc.ExternIDNotUniqueException;

/** This domain object home implements the GroupHome interface.
 *
 * Created on 19.07.2002
 *
 * @author Benno Luthiger
 * @see org.hip.vif.core.bom.GroupHome */
@SuppressWarnings("serial")
public class GroupHomeImpl extends DomainObjectHomeImpl implements GroupHome {

    /*
     * Every home has to know the class it handles. They provide access to this name through the method
     * <I>getObjectClassName</I>;
     */
    private final static String GROUP_CLASS_NAME = "org.hip.vif.core.bom.impl.GroupImpl";

    /*
     * The current version of the domain object framework provides no support for externelized metadata. We build them
     * up with hard coded definition strings.
     */
    // CAUTION: The current version of the lightweight DomainObject
    // framework makes only a limited check of the correctness
    // of the definition string. Make extensive basic test to
    // ensure that the definition works correct.
    private final static String XML_OBJECT_DEF = "<?xml version='1.0' encoding='ISO-8859-1'?>	\n"
            + "<objectDef objectName='Group' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	\n"
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
            + "' valueType='Long' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblGroup' columnName='GroupID'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_NAME
            + "' valueType='String' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblGroup' columnName='sName'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_DESCRIPTION
            + "' valueType='String' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblGroup' columnName='sDescription'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_REVIEWERS
            + "' valueType='Long' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblGroup' columnName='nReviewer'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_GUEST_DEPTH
            + "' valueType='Long' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblGroup' columnName='nGuestDepth'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_MIN_GROUP_SIZE
            + "' valueType='Long' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblGroup' columnName='nMinGroupSize'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_PRIVATE
            + "' valueType='Long' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblGroup' columnName='bPrivate'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_STATE
            + "' valueType='Long' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblGroup' columnName='nState'/>	\n"
            + "		</propertyDef>	\n" + "	</propertyDefs>	\n" + "</objectDef>";

    /** Constructor for GroupHomeImpl. */
    public GroupHomeImpl() {
        super();
    }

    /** @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectClassName() */
    @Override
    public String getObjectClassName() {
        return GROUP_CLASS_NAME;
    }

    /** @see org.hip.kernel.bom.impl.AbstractDomainObjectHome#getObjectDefString() */
    @Override
    protected String getObjectDefString() {
        return XML_OBJECT_DEF;
    }

    /** Returns the discussion group identified by the specified ID
     *
     * @param inGroupID java.lang.String The (numeric) key of the discussion group
     * @throws org.hip.kernel.bom.BOMInvalidKeyException */
    @Override
    public Group getGroup(final String inGroupID) throws BOMInvalidKeyException {
        return getGroup(new Long(inGroupID));
    }

    @Override
    public Group getGroup(final Long inGroupID) throws BOMInvalidKeyException {
        // create a key for the GroupID
        final KeyObject lKey = new KeyObjectImpl();
        try {
            lKey.setValue(KEY_ID, inGroupID);
            return (Group) findByKey(lKey);
        } catch (final VException exc) {
            throw new BOMInvalidKeyException(exc.getMessage());
        }
    }

    /** Creates a new discussion group with the specified data and returns the internal key of the newly created group.
     *
     * @param inGroupID java.lang.String
     * @param inName java.lang.String
     * @param inReviewers java.lang.String
     * @param inGuestDepth java.lang.String
     * @param inMinGoupSize java.lang.String
     * @param inIsPrivate boolean
     * @return java.lang.Long Key of newly created discussion group
     * @throws java.sql.SQLException
     * @throws org.hip.kernel.exc.VException
     * @throws ExternIDNotUniqueException */
    @Override
    public Long createNew(final String inGroupID, final String inName,
            final String inReviewers, final String inGuestDepth,
            final String inMinGoupSize, final boolean inIsPrivate)
                    throws SQLException, VException, ExternIDNotUniqueException {
        final Group lGroup = (Group) create();
        return lGroup.ucNew(inGroupID, inName, inReviewers, inGuestDepth,
                inMinGoupSize, inIsPrivate);
    }

    @Override
    public QueryResult selectForAdministration(final Long inActorID,
            final OrderObject inOrder) throws Exception {
        final int lRole = BOMHelper.getMemberCacheHome()
                .getMember(inActorID.toString()).getBestRole();
        switch (lRole) {
        case RoleHome.ROLE_SU:
        case RoleHome.ROLE_ADMIN:
            // returns all
            return select(inOrder);
        case RoleHome.ROLE_GROUP_ADMIN:
            // returns the groups the user is administering
            return BOMHelper.getJoinGroupAdminToGroupHome().select(inActorID,
                    inOrder);
        default:
            // returns none
            return new EmptyQueryResult(this);
        }
    }

    /** Returns true if the specified group is of type where the contributions need to be reviewed (i.e. the number of
     * reviewers is set > 0).
     *
     * @param inGroupID String
     * @return true, if the contributions have to be reviewed.
     * @throws VException */
    @Override
    public boolean needsReview(final String inGroupID) throws VException {
        return getGroup(inGroupID).needsReview();
    }
}

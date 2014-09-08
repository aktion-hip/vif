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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.BOMInvalidKeyException;
import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.exc.InvalidAuthenticationException;
import org.hip.vif.core.member.IMemberInformation;
import org.hip.vif.core.service.ApplicationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This domain object home implements the MemberHome interface.
 *
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.MemberHome */
@SuppressWarnings("serial")
public class MemberHomeImpl extends DomainObjectHomeImpl implements MemberHome {
    private static final Logger LOG = LoggerFactory
            .getLogger(MemberHomeImpl.class);

    /* Key for error message invalid user */
    protected static final String INVALID_USER_ERROR_MESSAG_KEY = "org.hip.vif.errmsg.input.invalidUserId";
    /* Key for error message invalid key */
    protected static final String INVALID_KEY_ERROR_MESSAG_KEY = "org.hip.vif.errmsg.input.invalidKey";

    /*
     * Every home has to know the class it handles. They provide access to this name through the method
     * <I>getObjectClassName</I>;
     */
    private final static String MEMBER_CLASS_NAME = "org.hip.vif.core.bom.impl.MemberImpl";

    private final static String XML_OBJECT_DEF = "<?xml version='1.0' encoding='ISO-8859-1'?>	\n"
            + "<objectDef objectName='Member' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	\n"
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
            + "			<mappingDef tableName='tblMember' columnName='MEMBERID'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_USER_ID
            + "' valueType='String' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblMember' columnName='SUSERID'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_NAME
            + "' valueType='String' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblMember' columnName='SNAME'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_FIRSTNAME
            + "' valueType='String' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblMember' columnName='SFIRSTNAME'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_STREET
            + "' valueType='String' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblMember' columnName='SSTREET'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_ZIP
            + "' valueType='String' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblMember' columnName='SZIP'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_CITY
            + "' valueType='String' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblMember' columnName='SCITY'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_PHONE
            + "' valueType='String' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblMember' columnName='STEL'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_FAX
            + "' valueType='String' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblMember' columnName='SFAX'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_MAIL
            + "' valueType='String' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblMember' columnName='SMAIL'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_SEX
            + "' valueType='Number' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblMember' columnName='BSEX'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_LANGUAGE
            + "' valueType='String' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblMember' columnName='SLANGUAGE'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_PASSWORD
            + "' valueType='String' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblMember' columnName='SPASSWORD'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_SETTINGS
            + "' valueType='String' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblMember' columnName='SSETTINGS'/>	\n"
            + "		</propertyDef>	\n"
            + "		<propertyDef propertyName='"
            + KEY_MUTATION
            + "' valueType='Timestamp' propertyType='simple'>	\n"
            + "			<mappingDef tableName='tblMember' columnName='DTMUTATION'/>	\n"
            + "		</propertyDef>			\n" + "	</propertyDefs>	\n" + "</objectDef>";

    /** MemberHomeImpl default constructor. */
    public MemberHomeImpl() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hip.vif.bom.MemberHome#checkAuthentication(java.lang.String, java.lang.String,
     * org.hip.vif.servlets.VIFContext)
     */
    @Override
    public Member checkAuthentication(final String inUserID,
            final String inPassword) throws InvalidAuthenticationException,
            BOMChangeValueException {
        // create a key for the UserID
        final KeyObject lKeyUserID = new KeyObjectImpl();
        try {
            if ("".equals(inUserID)) {
                // guest member, no authentication
                return null;
            } else {
                lKeyUserID.setValue(KEY_USER_ID, inUserID);
                final Member lMember = (Member) findByKey(lKeyUserID);
                lMember.checkAuthentication(inPassword, VSys.dftLocale);
                return lMember;
            }
        } catch (final BOMNotFoundException exc) {
            logInvalidAuthentication(inUserID);
            throw new InvalidAuthenticationException(
                    "org.hip.vif.errmsg.input.invalidUserId", VSys.dftLocale);
        } catch (final VException exc) {
            logInvalidAuthentication(inUserID);
            throw new InvalidAuthenticationException(
                    "org.hip.vif.errmsg.input.invalidUserId", VSys.dftLocale);
        }
    }

    private void logInvalidAuthentication(final String inUserID) {
        final StringBuilder lLog = new StringBuilder(
                "Note: Invalid try to authenticate:\n");
        lLog.append("   User: ").append(inUserID).append("\n");
        // TODO
        //		lLog.append("   IP number: ").append(((WebBrowser) ApplicationData.getWindow().getTerminal()).getAddress()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
        LOG.warn(new String(lLog));
    }

    @Override
    protected Vector<Object> createTestObjects() {
        return null;
    }

    /** Returns the entry of the authenticated member
     *
     * @return org.hip.vif.bom.Member
     * @exception org.hip.kernel.bom.BOMInvalidKeyException */
    @Override
    public Member getActor() throws BOMInvalidKeyException {
        // create a key for the UserID
        final KeyObject lKeyUserID = new KeyObjectImpl();
        try {
            lKeyUserID
                    .setValue(KEY_ID, ApplicationData.getActor().getActorID());
            return (Member) findByKey(lKeyUserID);
        } catch (final VException exc) {
            throw new BOMInvalidKeyException(exc.getMessage());
        }
    }

    /** Returns the member identified by the specified ID
     *
     * @param inMemberID java.lang.String
     * @return Member
     * @throws org.hip.kernel.bom.BOMInvalidKeyException */
    @Override
    public Member getMember(final String inMemberID)
            throws BOMInvalidKeyException {
        return getMember(new Long(inMemberID));
    }

    /** Returns the member identified by the specified ID
     *
     * @param inMemberID java.lang.Long
     * @return Member
     * @throws BOMInvalidKeyException */
    @Override
    public Member getMember(final Long inMemberID)
            throws BOMInvalidKeyException {
        // create a key for the MemberID
        final KeyObject lKey = new KeyObjectImpl();
        try {
            lKey.setValue(KEY_ID, inMemberID);
            return (Member) findByKey(lKey);
        } catch (final VException exc) {
            throw new BOMInvalidKeyException(exc.getMessage());
        }
    }

    /** Returns the member identified by the specified user ID
     *
     * @param inUserID java.lang.String
     * @throws org.hip.kernel.bom.BOMInvalidKeyException */
    @Override
    public Member getMemberByUserID(final String inUserID)
            throws BOMInvalidKeyException {
        // create a key for the UserID
        final KeyObject lKeyMemberID = new KeyObjectImpl();
        try {
            lKeyMemberID.setValue(KEY_USER_ID, inUserID);
            return (Member) findByKey(lKeyMemberID);
        } catch (final VException exc) {
            throw new BOMInvalidKeyException(exc.getMessage());
        }
    }

    /** Returns a collection of members identified by the specified array of IDs.
     *
     * @param inMemberIDs java.lang.String[]
     * @return java.util.Collection<Member> Collection of Member domain objects. */
    @Override
    public Collection<Member> getMembers(final Collection<Long> inMemberIDs) {
        final Collection<Member> outMembers = new ArrayList<Member>();
        for (final Long lMemberID : inMemberIDs) {
            try {
                outMembers.add(getMember(lMemberID));
            } catch (final BOMInvalidKeyException exc) {
                // left blank intentionally
            }
        }
        return outMembers;
    }

    /** Returns the Member class name
     *
     * @return java.lang.String */
    @Override
    public String getObjectClassName() {
        return MEMBER_CLASS_NAME;
    }

    /** Every class must provide some meta-data required by the lightweight DomainObject framework. This method returns
     * the definition string (in form of xml).
     *
     * @return java.lang.String */
    @Override
    protected String getObjectDefString() {
        return XML_OBJECT_DEF;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hip.vif.bom.MemberHome#updateMemberCache(java.lang.String, org.hip.vif.member.MemberInformation)
     */
    @Override
    public Member updateMemberCache(final IMemberInformation inInformation)
            throws SQLException, VException {
        try {
            final Member outMember = getMemberByUserID(inInformation
                    .getUserID());
            inInformation.update(outMember);
            return outMember;
        } catch (final VException exc) {
            final Member outMember = (Member) create();
            inInformation.insert(outMember);
            return outMember;
        }
    }

}

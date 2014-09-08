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

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObjectVisitor;
import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.bom.impl.XMLCharacterFilter;
import org.hip.kernel.bom.impl.XMLSerializer;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.AssertionFailedError;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.util.XMLRepresentation;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.LinkMemberRoleHome;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHistory;
import org.hip.vif.core.bom.MemberHistoryHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.search.VIFMemberIndexer;
import org.hip.vif.core.exc.Assert;
import org.hip.vif.core.exc.Assert.AssertLevel;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.hip.vif.core.exc.InvalidAuthenticationException;
import org.hip.vif.core.member.IAuthenticator;
import org.hip.vif.core.search.AbstractSearching;
import org.hip.vif.core.search.FullTextHelper;
import org.hip.vif.core.search.Indexable;
import org.hip.vif.core.service.MemberUtility;
import org.hip.vif.core.util.RolesCheck;
import org.hip.vif.core.util.UserSettings;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** This domain object implements the Member interface.
 *
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.Member */
@SuppressWarnings("serial")
public class MemberImpl extends AbstractMember implements Member, Indexable {
    public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.MemberHomeImpl";

    /* Key for error message invalid password */
    protected static final String INVALID_PWRD_ERROR_MESSAG_KEY = "org.hip.vif.errmsg.input.invalidPwrd";

    /** Authenticate this member with the specified password.
     * 
     * @param inPassword java.lang.String
     * @param inLocale Locale
     * @throws VException
     * @throws InvalidAuthenticationException */
    @Override
    public void checkAuthentication(final String inPassword,
            final Locale inLocale) throws VException,
            InvalidAuthenticationException {
        try {
            if (!getAuthenticator().matches(
                    (String) get(MemberHome.KEY_PASSWORD), inPassword)) {
                throw new InvalidAuthenticationException(
                        INVALID_PWRD_ERROR_MESSAG_KEY, inLocale);
            }
        } catch (final GettingException exc) {
            throw new InvalidAuthenticationException(exc.getMessage());
        }
    }

    private IAuthenticator getAuthenticator() throws VException {
        return MemberUtility.INSTANCE.getActiveAuthenticator();
    }

    /** Creates a history entry and fills it with the values of the actual member entry except for the password, which is
     * not copied. */
    private MemberHistory createHistory() throws BOMChangeValueException {
        MemberHistory outHistory;

        try {
            outHistory = (MemberHistory) BOMHelper.getMemberHistoryHome()
                    .create();
            for (final String lPropertyName : getPropertyNames2()) {
                if (!MemberHome.KEY_PASSWORD.equals(lPropertyName)) {
                    outHistory.set(lPropertyName, this.get(lPropertyName));
                }
            }
        } catch (final Exception exc) {
            throw new BOMChangeValueException("MemberImpl.createHistory:  "
                    + exc.getMessage());
        }

        return outHistory;
    }

    private Long doInsert(final String inUserID, final String inName,
            final String inFirstName, final String inStreet,
            final String inZIP, final String inCity, final String inTel,
            final String inFax, final String inMail, final String inSex,
            final String inLanguage, final String inPassword)
            throws BOMChangeValueException {
        try {
            set(MemberHome.KEY_USER_ID, inUserID);
            set(MemberHome.KEY_NAME, inName);
            set(MemberHome.KEY_FIRSTNAME, inFirstName);
            set(MemberHome.KEY_STREET, inStreet);
            set(MemberHome.KEY_ZIP, inZIP);
            set(MemberHome.KEY_CITY, inCity);
            set(MemberHome.KEY_PHONE, inTel);
            set(MemberHome.KEY_FAX, inFax);
            set(MemberHome.KEY_MAIL, inMail);
            set(MemberHome.KEY_SEX, new Integer(inSex));
            set(MemberHome.KEY_LANGUAGE, inLanguage);
            set(MemberHome.KEY_PASSWORD, inPassword);

            return insert(true);
        } catch (final VException exc) {
            throw new BOMChangeValueException(exc.getMessage());
        } catch (final SQLException exc) {
            throw new BOMChangeValueException(exc.getMessage());
        }
    }

    /** Every subclass of DomainObject must provide the fully qualified name of its home class. It's good practice to
     * define the name in a static variable and to return in this method those variable.
     * 
     * @return java.lang.String */
    @Override
    public String getHomeClassName() {
        return HOME_CLASS_NAME;
    }

    /** Returns the content of this object as XML string. The data is secure because information about ID and password is
     * filtered out.
     * 
     * @return org.hip.xml.utilities.XMLRepresentation */
    @Override
    public XMLRepresentation getSecureData() {
        final XMLSerializer lSerializer = new XMLSerializer(
                XMLCharacterFilter.DEFAULT_FILTER);
        accept(lSerializer);
        final XMLRepresentation lOriginalXML = new XMLRepresentation(
                lSerializer.toString());

        final Node lPropertySetRoot = lOriginalXML.reveal()
                .getElementsByTagName(DomainObjectVisitor.PROPERTY_SET_TAG)
                .item(0);
        final NodeList lPropertySet = lPropertySetRoot.getChildNodes();
        int lRemoved = 0;
        int i = lPropertySet.getLength();
        while (i-- > 0 && lRemoved < 2) {
            final Node lChild = lPropertySet.item(i);
            if (MemberHome.KEY_ID.equals(lChild.getNodeName())) {
                lPropertySetRoot.removeChild(lChild);
                lRemoved++;
            }
            if (MemberHome.KEY_PASSWORD.equals(lChild.getNodeName())) {
                lPropertySetRoot.removeChild(lChild);
                lRemoved++;
            }
        }
        return new XMLRepresentation(lPropertySetRoot.getOwnerDocument());
    }

    /** Save the new password.
     * 
     * @param inPassword java.lang.String
     * @exception org.hip.vif.core.exc.bom.impl.BOMChangeValueException */
    @Override
    public void savePwrd(final String inPassword)
            throws BOMChangeValueException {
        try {
            // pre: Password must be set
            if (Assert.assertTrue(AssertLevel.ERROR_OUT, this,
                    "Member:savePwrd", !"".equals(inPassword)) == Assert.FAILURE)
                return;

            set(MemberHome.KEY_PASSWORD, getAuthenticator().encrypt(inPassword));

            if (isChanged()) {
                update(true);
            }
        } catch (final Exception exc) {
            throw new BOMChangeValueException(exc.getMessage());
        }
    }

    /** Insert a new entry and save the data.
     * 
     * @param inUserID java.lang.String
     * @param inName java.lang.String
     * @param inFirstName java.lang.String
     * @param inStreet java.lang.String
     * @param inZIP java.lang.String
     * @param inCity java.lang.String
     * @param inTel java.lang.String
     * @param inFax java.lang.String
     * @param inMail java.lang.String
     * @param inSex java.lang.String
     * @param inLanguage java.lang.String
     * @param inPassword java.lang.String
     * @param inRoles java.lang.String[]
     * @return Long the new entry's MemberID.
     * @throws Exception */
    @Override
    public Long ucNew(final String inUserID, final String inName,
            final String inFirstName, final String inStreet,
            final String inZIP, final String inCity, final String inTel,
            final String inFax, final String inMail, final String inSex,
            final String inLanguage, final String inPassword,
            final String[] inRoles) throws Exception {
        preCheck(inUserID, inName, inFirstName, inStreet, inZIP, inCity,
                inMail, inRoles);

        if (checkExistingUserID(inUserID)) {
            throw new ExternIDNotUniqueException(String.format(
                    "User-ID %s not unique", inUserID));
        }

        // the specified UserID does not exist yet, therefore, we can continue
        final Long outMemberID = doInsert(inUserID, inName, inFirstName,
                inStreet, inZIP, inCity, inTel, inFax, inMail, inSex,
                inLanguage, inPassword);
        try {
            saveRolesAndIndex(outMemberID, Arrays.asList(inRoles));
            return outMemberID;
        } catch (final Exception exc) {
            throw new BOMChangeValueException(exc.getMessage());
        }
    }

    /** Persist the new member entry.
     * 
     * @param inRoles Collection of ids of roles assigned to the new member
     * @return Long the new entry's MemberID
     * @throws Exception */
    @Override
    public Long ucNew(final Collection<String> inRoles) throws Exception {
        final boolean lSaveableMember = isSaveableMember();
        if (lSaveableMember) {
            preCheck(get(MemberHome.KEY_USER_ID).toString(),
                    get(MemberHome.KEY_NAME).toString(),
                    get(MemberHome.KEY_FIRSTNAME).toString(),
                    get(MemberHome.KEY_STREET).toString(),
                    get(MemberHome.KEY_ZIP).toString(),
                    get(MemberHome.KEY_CITY).toString(),
                    get(MemberHome.KEY_MAIL).toString(), inRoles);
        }

        final String lUserID = get(MemberHome.KEY_USER_ID).toString();
        if (checkExistingUserID(lUserID)) {
            throw new ExternIDNotUniqueException(String.format(
                    "User-ID %s not unique", lUserID));
        }

        // the specified UserID does not exist yet, therefore, we can continue
        final Long outMemberID = insert(true);
        try {
            saveRolesAndIndex(outMemberID, inRoles);
            return outMemberID;
        } catch (final Exception exc) {
            throw new BOMChangeValueException(exc.getMessage());
        }
    }

    private void saveRolesAndIndex(final Long inMemberID,
            final Collection<String> inRoles) throws Exception {
        final MemberHomeImpl lHome = (MemberHomeImpl) BOMHelper.getMemberHome();
        // save roles of member
        final Member lNewMember = lHome.getMember(inMemberID);
        final LinkMemberRoleHome lLinkHome = BOMHelper.getLinkMemberRoleHome();
        final Long lMemberID = new Long(lNewMember.get(MemberHome.KEY_ID)
                .toString());
        lLinkHome.associateRoles(lMemberID, inRoles);

        // index member data
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(MemberHome.KEY_ID, lMemberID);
        final VIFMemberIndexer lIndexer = new VIFMemberIndexer();
        lIndexer.addMemberToIndex(lKey);
    }

    /** @param inUserID String
     * @return boolean <code>true</code> if specified user id exists yet, else <code>false</code>
     * @throws Exception */
    private boolean checkExistingUserID(final String inUserID) throws Exception {
        final KeyObject lKeyUserID = new KeyObjectImpl();
        lKeyUserID.setValue(MemberHome.KEY_USER_ID, inUserID);

        // check whether the key exists yet
        try {
            BOMHelper.getMemberHome().findByKey(lKeyUserID);
            return true;
        } catch (final BOMNotFoundException exc) {
            return false;
        }
    }

    /** Save the data of an edited entry.
     * 
     * @param inName java.lang.String
     * @param inFirstName java.lang.String
     * @param inStreet java.lang.String
     * @param inZIP java.lang.String
     * @param inCity java.lang.String
     * @param inTel java.lang.String
     * @param inFax java.lang.String
     * @param inMail java.lang.String
     * @param inSex java.lang.String
     * @param inLanguage java.lang.String
     * @param inRoles java.lang.String[]
     * @param inActorID java.lang.Long
     * @return boolean true if the members new roles are different from the old ones.
     * @exception org.hip.vif.core.exc.bom.impl.BOMChangeValueException */
    @Override
    public boolean ucSave(final String inName, final String inFirstName,
            final String inStreet, final String inZIP, final String inCity,
            final String inTel, final String inFax, final String inMail,
            final String inSex, final String inLanguage,
            final String[] inRoles, final Long inActorID)
            throws BOMChangeValueException {
        try {
            final Timestamp lMutationDate = new Timestamp(
                    System.currentTimeMillis());
            MemberHistory lHistory = null;
            boolean lMemberChanged = false;
            final boolean lSaveableMember = isSaveableMember();
            if (lSaveableMember) {
                preCheck(inName, inFirstName, inStreet, inZIP, inCity, inMail,
                        inRoles);

                lHistory = setValues(inName, inFirstName, inStreet, inZIP,
                        inCity, inTel, inFax, inMail, inSex, inLanguage,
                        lMutationDate, inActorID);
                lMemberChanged = isChanged();
            }

            // create check of change of roles
            final Long lMemberID = new Long(get(MemberHome.KEY_ID).toString());
            final LinkMemberRoleHome lLinkHome = BOMHelper
                    .getLinkMemberRoleHome();
            final RolesCheck lRolesCheck = lLinkHome.checkRolesOf(lMemberID,
                    inRoles);

            if (lMemberChanged || lRolesCheck.hasChanged()) {
                if (lMemberChanged && lSaveableMember) {
                    // insert old values in history entry
                    lHistory.set(MemberHistoryHome.KEY_REMARKS,
                            lRolesCheck.getOldRoles());
                    lHistory.insert(true);

                    set(MemberHome.KEY_MUTATION, lMutationDate);
                    update(true);
                    refreshIndex();
                }

                if (lRolesCheck.hasChanged()) {
                    lLinkHome.deleteRolesOf(lMemberID);
                    lLinkHome.associateRoles(lMemberID,
                            lRolesCheck.getNewRoles());
                }
            }
            return lRolesCheck.hasChanged();
        } catch (final Exception exc) {
            throw new BOMChangeValueException(exc.getMessage());
        }
    }

    /** Method is protected for testing purposes.
     * 
     * @return boolean
     * @throws VException */
    protected boolean isSaveableMember() throws VException {
        final IAuthenticator lAuthenticator = getAuthenticator();
        return lAuthenticator == null ? true : !lAuthenticator.isExternal();
    }

    /** Save the data of an edited entry. No roles data is added, therefore, the associations to roles are left
     * unchanged.
     * 
     * @param inName java.lang.String
     * @param inFirstName java.lang.String
     * @param inStreet java.lang.String
     * @param inZIP java.lang.String
     * @param inCity java.lang.String
     * @param inTel java.lang.String
     * @param inFax java.lang.String
     * @param inMail java.lang.String
     * @param inSex java.lang.String
     * @param inLanguage java.lang.String
     * @param inActorID java.lang.Long
     * @exception org.hip.vif.core.exc.bom.impl.BOMChangeValueException */
    @Override
    public void ucSave(final String inName, final String inFirstName,
            final String inStreet, final String inZIP, final String inCity,
            final String inTel, final String inFax, final String inMail,
            final String inSex, final String inLanguage, final Long inActorID)
            throws BOMChangeValueException {
        try {
            preCheck(inName, inFirstName, inStreet, inZIP, inCity, inMail);

            final Timestamp lMutationDate = new Timestamp(
                    System.currentTimeMillis());
            final MemberHistory lHistory = setValues(inName, inFirstName,
                    inStreet, inZIP, inCity, inTel, inFax, inMail, inSex,
                    inLanguage, lMutationDate, inActorID);

            if (isChanged()) {
                // insert old values in history entry
                lHistory.insert(true);

                set(MemberHome.KEY_MUTATION, lMutationDate);
                update(true);
                refreshIndex();
            }
        } catch (final Exception exc) {
            throw new BOMChangeValueException(exc.getMessage());
        }
    }

    @Override
    public void ucSave(final Long inActorID) throws BOMChangeValueException {
        if (!isChanged())
            return;

        // historize changes
        try {
            preCheck(get(MemberHome.KEY_NAME).toString(),
                    get(MemberHome.KEY_FIRSTNAME).toString(),
                    get(MemberHome.KEY_STREET).toString(),
                    get(MemberHome.KEY_ZIP).toString(),
                    get(MemberHome.KEY_CITY).toString(),
                    get(MemberHome.KEY_MAIL).toString());

            final MemberHistory lHistory = createHistory();

            final Timestamp lMutationDate = new Timestamp(
                    System.currentTimeMillis());
            lHistory.set(MemberHistoryHome.KEY_VALID_TO, lMutationDate);
            lHistory.set(MemberHistoryHome.KEY_EDITOR_ID, inActorID);
            lHistory.insert(true);

            set(MemberHome.KEY_MUTATION, lMutationDate);
            update(true);
            refreshIndex();
        } catch (final Exception exc) {
            throw new BOMChangeValueException(exc.getMessage());
        }
    }

    @Override
    public boolean ucSave(final Collection<String> inRoles, final Long inActorID)
            throws BOMChangeValueException {
        try {
            final boolean lSaveableMember = isSaveableMember();
            if (lSaveableMember) {
                preCheck(get(MemberHome.KEY_NAME).toString(),
                        get(MemberHome.KEY_FIRSTNAME).toString(),
                        get(MemberHome.KEY_STREET).toString(),
                        get(MemberHome.KEY_ZIP).toString(),
                        get(MemberHome.KEY_CITY).toString(),
                        get(MemberHome.KEY_MAIL).toString(), inRoles);
            }

            // create check of change of roles
            final Long lMemberID = new Long(get(MemberHome.KEY_ID).toString());
            final LinkMemberRoleHome lLinkHome = BOMHelper
                    .getLinkMemberRoleHome();
            final RolesCheck lRolesCheck = lLinkHome.checkRolesOf(lMemberID,
                    inRoles);

            if (isChanged() || lRolesCheck.hasChanged()) {
                if (isChanged() && lSaveableMember) {
                    final MemberHistory lHistory = createHistory();
                    final Timestamp lMutationDate = new Timestamp(
                            System.currentTimeMillis());
                    lHistory.set(MemberHistoryHome.KEY_VALID_TO, lMutationDate);
                    lHistory.set(MemberHistoryHome.KEY_EDITOR_ID, inActorID);
                    lHistory.set(MemberHistoryHome.KEY_REMARKS,
                            lRolesCheck.getOldRoles());
                    lHistory.insert(true);

                    set(MemberHome.KEY_MUTATION, lMutationDate);
                    update(true);
                    refreshIndex();
                }
                if (lRolesCheck.hasChanged()) {
                    lLinkHome.deleteRolesOf(lMemberID);
                    lLinkHome.associateRoles(lMemberID,
                            lRolesCheck.getNewRoles());
                }
            }
            return lRolesCheck.hasChanged();
        } catch (final Exception exc) {
            throw new BOMChangeValueException(exc.getMessage());
        }
    }

    /** Sets the values to the entry and prepares a history entry.
     * 
     * @param inName java.lang.String
     * @param inFirstName java.lang.String
     * @param inStreet java.lang.String
     * @param inZIP java.lang.String
     * @param inCity java.lang.String
     * @param inTel java.lang.String
     * @param inFax java.lang.String
     * @param inMail java.lang.String
     * @param inSex java.lang.String
     * @param inLanguage java.lang.String
     * @param inMutationDate java.sql.Timestamp
     * @param inActorID java.lang.Long
     * @exception org.hip.vif.core.exc.bom.impl.BOMChangeValueException */
    private MemberHistory setValues(final String inName,
            final String inFirstName, final String inStreet,
            final String inZIP, final String inCity, final String inTel,
            final String inFax, final String inMail, final String inSex,
            final String inLanguage, final Timestamp inMutationDate,
            final Long inActorID) throws BOMChangeValueException {
        try {
            final MemberHistory outHistory = createHistory();
            outHistory.set(MemberHistoryHome.KEY_VALID_TO, inMutationDate);
            outHistory.set(MemberHistoryHome.KEY_EDITOR_ID, inActorID);

            set(MemberHome.KEY_NAME, inName);
            set(MemberHome.KEY_FIRSTNAME, inFirstName);
            set(MemberHome.KEY_STREET, inStreet);
            set(MemberHome.KEY_ZIP, inZIP);
            set(MemberHome.KEY_CITY, inCity);
            set(MemberHome.KEY_PHONE, inTel);
            set(MemberHome.KEY_FAX, inFax);
            set(MemberHome.KEY_MAIL, inMail);
            set(MemberHome.KEY_SEX, new BigDecimal(inSex));
            set(MemberHome.KEY_LANGUAGE, inLanguage);

            return outHistory;
        } catch (final VException exc) {
            throw new BOMChangeValueException(exc.getMessage());
        }
    }

    private void refreshIndex() throws Exception {
        try {
            final VIFMemberIndexer lIndexer = new VIFMemberIndexer();
            lIndexer.refreshMemberInIndex(get(MemberHome.KEY_ID).toString());
        } catch (final IOException exc) {
            throw new VException(exc.getMessage());
        }
    }

    /** This method inserts an new su for initials administration purpose
     * 
     * @param inUserID java.lang.String
     * @param inPassword java.lang.String
     * @param inLanguage java.lang.String
     * @exception org.hip.vif.core.exc.bom.impl.BOMChangeValueException */
    @Override
    public void ucCreateSU(final String inUserID, final String inPassword,
            final String inLanguage) throws BOMChangeValueException {
        // pre: userId must be set
        Assert.assertTrue(AssertLevel.ERROR, this, "ucCreateSU",
                !"".equals(inUserID));

        // pre: password must be set
        Assert.assertTrue(AssertLevel.ERROR, this, "ucCreateSU",
                !"".equals(inPassword));

        doInsert(inUserID, "SU", "", "", "", "", "", "", "", "0", inLanguage,
                inPassword);

        // associate su role
        try {
            final MemberHomeImpl lHome = (MemberHomeImpl) BOMHelper
                    .getMemberCacheHome();

            final KeyObject lKeyUserID = new KeyObjectImpl();
            lKeyUserID.setValue(MemberHome.KEY_USER_ID, inUserID);
            final Member lNewMember = (Member) lHome.findByKey(lKeyUserID);
            final LinkMemberRoleHome lLinkHome = BOMHelper
                    .getLinkMemberRoleHome();
            lLinkHome.associateRoles(new Long(lNewMember.get(MemberHome.KEY_ID)
                    .toString()), new String[] { "1" });
            return;
        } catch (final Exception exc) {
            throw new BOMChangeValueException(exc.getMessage());
        }
    }

    /** This method deletes the member entry and reports the deletion to the member history table and the member index.
     * 
     * @param inActorID java.lang.Long
     * @exception java.sql.SQLException */
    @Override
    public void delete(final Long inActorID) throws VException, SQLException {
        try {
            final MemberHistory lHistory = createHistory();
            lHistory.set(MemberHistoryHome.KEY_VALID_TO,
                    new Timestamp(System.currentTimeMillis()));
            lHistory.set(MemberHistoryHome.KEY_REMARKS, "Member entry deleted");
            lHistory.set(MemberHistoryHome.KEY_EDITOR_ID, inActorID);
            lHistory.insert(true);
            super.delete(true);

            final VIFMemberIndexer lIndexer = new VIFMemberIndexer();
            lIndexer.deleteMemberInIndex(getMemberID().toString());
        } catch (final IOException exc) {
            throw new VException(exc.getMessage());
        }
    }

    /** This method overwrites the superclass' delete method. It throws an AssertionFailedError, because we want to force
     * the user to use delete(Integer inActorID)
     * 
     * @param inActorID java.lang.Integer
     * @exception java.sql.SQLException
     * @see delete(Integer inActorID) */
    @Override
    public void delete() throws SQLException {
        // throws assert error using delete without ActorID
        VSys.assertNotNull(org.hip.kernel.sys.Assert.ERROR, this, "delete",
                null);
    }

    @Override
    public void delete(final boolean inCommit) throws SQLException {
        // throws assert error using delete without ActorID
        VSys.assertNotNull(org.hip.kernel.sys.Assert.ERROR, this, "delete",
                null);
    }

    /** Asserts the existence of mandatory input
     * 
     * @param inUserID java.lang.String
     * @param inName java.lang.String
     * @param inFirstName java.lang.String
     * @param inStreet java.lang.String
     * @param inZIP java.lang.String
     * @param inCity java.lang.String
     * @param inMail java.lang.String
     * @param inRoles java.lang.String[] */
    private void preCheck(final String inUserID, final String inName,
            final String inFirstName, final String inStreet,
            final String inZIP, final String inCity, final String inMail,
            final String[] inRoles) {
        // pre: userId must be set
        Assert.assertTrue(AssertLevel.ERROR, this, "preCheck",
                !"".equals(inUserID));

        preCheck(inName, inFirstName, inStreet, inZIP, inCity, inMail, inRoles);
    }

    private void preCheck(final String inUserID, final String inName,
            final String inFirstName, final String inStreet,
            final String inZIP, final String inCity, final String inMail,
            final Collection<String> inRoles) {
        // pre: userId must be set
        Assert.assertTrue(AssertLevel.ERROR, this, "preCheck",
                !"".equals(inUserID));

        preCheck(inName, inFirstName, inStreet, inZIP, inCity, inMail, inRoles);
    }

    /** Asserts the existence of mandatory input
     * 
     * @param inName java.lang.String
     * @param inFirstName java.lang.String
     * @param inStreet java.lang.String
     * @param inZIP java.lang.String
     * @param inCity java.lang.String
     * @param inMail java.lang.String
     * @param inRoles java.lang.String[] */
    private void preCheck(final String inName, final String inFirstName,
            final String inStreet, final String inZIP, final String inCity,
            final String inMail, final String[] inRoles) {
        preCheck(inName, inFirstName, inStreet, inZIP, inCity, inMail);

        // pre: Roles must be set
        Assert.assertTrue(AssertLevel.ERROR, this, "preCheck",
                inRoles.length > 0);
    }

    private void preCheck(final String inName, final String inFirstName,
            final String inStreet, final String inZIP, final String inCity,
            final String inMail, final Collection<String> inRoles) {
        preCheck(inName, inFirstName, inStreet, inZIP, inCity, inMail);

        // pre: Roles must be set
        Assert.assertTrue(AssertLevel.ERROR, this, "preCheck",
                inRoles.size() > 0);
    }

    /** Asserts the existence of mandatory input
     * 
     * @param inName java.lang.String
     * @param inFirstName java.lang.String
     * @param inStreet java.lang.String
     * @param inZIP java.lang.String
     * @param inCity java.lang.String
     * @param inMail java.lang.String */
    private void preCheck(final String inName, final String inFirstName,
            final String inStreet, final String inZIP, final String inCity,
            final String inMail) {
        // pre: Name must be set
        Assert.assertTrue(AssertLevel.ERROR, this, "preCheck",
                !"".equals(inName));

        // pre: FirstName must be set
        Assert.assertTrue(AssertLevel.ERROR, this, "preCheck",
                !"".equals(inFirstName));
        // pre: Street must be set
        Assert.assertTrue(AssertLevel.ERROR, this, "preCheck",
                !"".equals(inStreet));
        // pre: ZIP must be set
        Assert.assertTrue(AssertLevel.ERROR, this, "preCheck",
                !"".equals(inZIP));
        // pre: City must be set
        Assert.assertTrue(AssertLevel.ERROR, this, "preCheck",
                !"".equals(inCity));
        // pre: Mail must be set
        Assert.assertTrue(AssertLevel.ERROR, this, "preCheck",
                !"".equals(inMail));
    }

    /** Returns the ID of the best role the member has.
     * 
     * @return int the ID of the best role the member has
     * @throws VException
     * @throws SQLException */
    @Override
    public int getBestRole() throws VException, SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(LinkMemberRoleHome.KEY_MEMBER_ID, get(MemberHome.KEY_ID));
        final OrderObject lOrder = new OrderObjectImpl();
        lOrder.setValue(JoinMemberToRoleHome.KEY_ALIAS_ROLE_INT_ID, 0);
        final QueryResult lResult = BOMHelper.getJoinMemberToRoleHome().select(
                lKey, lOrder);
        return ((BigDecimal) lResult.next().get(
                JoinMemberToRoleHome.KEY_ALIAS_ROLE_INT_ID)).intValue();
    }

    /** @throws IOException
     * @throws VException
     * @see Indexable#indexContent(IndexWriter) */
    @Override
    public void indexContent(final IndexWriter inWriter) throws IOException,
            VException {
        final FullTextHelper lFullText = new FullTextHelper();
        final Document lDocument = new Document();
        lDocument.add(AbstractSearching.IndexField.MEMBER_ID.createField(get(
                MemberHome.KEY_ID).toString()));
        lDocument.add(AbstractSearching.IndexField.MEMBER_USER_ID
                .createField(get(MemberHome.KEY_USER_ID).toString()));
        lDocument
                .add(AbstractSearching.IndexField.MEMBER_NAME
                        .createField(lFullText.add(get(MemberHome.KEY_NAME)
                                .toString())));
        lDocument.add(AbstractSearching.IndexField.MEMBER_FIRSTNAME
                .createField(lFullText.add(get(MemberHome.KEY_FIRSTNAME)
                        .toString())));
        lDocument.add(AbstractSearching.IndexField.MEMBER_STREET
                .createField(lFullText.add(get(MemberHome.KEY_STREET)
                        .toString())));
        lDocument
                .add(AbstractSearching.IndexField.MEMBER_POSTAL
                        .createField(lFullText.add(get(MemberHome.KEY_ZIP)
                                .toString())));
        lDocument
                .add(AbstractSearching.IndexField.MEMBER_CITY
                        .createField(lFullText.add(get(MemberHome.KEY_CITY)
                                .toString())));
        lDocument.add(AbstractSearching.IndexField.MEMBER_MAIL.createField(get(
                MemberHome.KEY_MAIL).toString()));
        lDocument.add(AbstractSearching.IndexField.MEMBER_FULL_TEXT
                .createField(lFullText.getFullText()));
        synchronized (this) {
            inWriter.addDocument(lDocument);
        }
    }

    @Override
    public boolean isValid() {
        try {
            preCheck(get(MemberHome.KEY_NAME).toString(),
                    get(MemberHome.KEY_FIRSTNAME).toString(),
                    get(MemberHome.KEY_STREET).toString(),
                    get(MemberHome.KEY_ZIP).toString(),
                    get(MemberHome.KEY_CITY).toString(),
                    get(MemberHome.KEY_MAIL).toString());
            return true;
        } catch (final GettingException exc) {
            return false;
        } catch (final AssertionFailedError exc) {
            return false;
        } catch (final NullPointerException exc) {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hip.vif.core.bom.Member#getUserSettings(java.lang.String)
     */
    @Override
    public String getUserSettings(final String inKey) throws VException {
        final UserSettings lSettings = new UserSettings(
                (String) get(MemberHome.KEY_SETTINGS));
        return lSettings.getValue(inKey);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hip.vif.core.bom.Member#setUserSettings(java.lang.String, java.lang.String)
     */
    @Override
    public void setUserSettings(final String inKey, final String inValue)
            throws VException, SQLException {
        try {
            final UserSettings lSettings = new UserSettings(
                    (String) get(MemberHome.KEY_SETTINGS));
            lSettings.setValue(inKey, inValue);
            set(MemberHome.KEY_SETTINGS, lSettings.toXML());
            update(true);
        } catch (final Exception exc) {
            throw new VException(exc.getMessage());
        }
    }

}

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
package org.hip.vif.core.bom.impl; // NOPMD

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.hip.kernel.bom.BOMInvalidKeyException;
import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.SettingException;
import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.AssertionFailedError;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.util.VInvalidNameException;
import org.hip.kernel.util.VInvalidValueException;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.exc.Assert;
import org.hip.vif.core.exc.Assert.AssertLevel;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.hip.vif.core.util.GroupStateChangeParameters;

/** This domain object implements the Group interface.
 *
 * Created on 19.07.2002
 *
 * @author Benno Luthiger
 * @see org.hip.vif.core.bom.Group */
@SuppressWarnings("serial")
public class GroupImpl extends DomainObjectImpl implements Group {
    public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.GroupHomeImpl";

    private final static int DEFAULT_REVIEWERS = 1;
    private final static int DEFAULT_GUEST_DEPTH = 0;
    private final static int DEFAULT_MIN_GROUP_SIZE = 0;

    /** Constructor for GroupImpl. */
    public GroupImpl() throws WorkflowException {
        super();
    }

    /** @see org.hip.kernel.bom.GeneralDomainObject#getHomeClassName() */
    @Override
    public String getHomeClassName() {
        return HOME_CLASS_NAME;
    }

    /** Insert a new entry and save the data.
     *
     * @param inName java.lang.String
     * @param inDescription java.lang.String
     * @param inReviewers java.lang.String
     * @param inGuestDepth java.lang.String
     * @param inMinGoupSize java.lang.String
     * @param inIsPrivate boolean
     * @return Long The ID of the new group entry.
     * @exception org.hip.vif.core.exc.bom.impl.BOMChangeValueException
     * @exception org.hip.vif.core.exc.bom.impl.ExternIDNotUniqueException */
    @Override
    public Long ucNew(final String inName, final String inDescription, // NOPMD
            final String inReviewers, final String inGuestDepth,
            final String inMinGoupSize, final boolean inIsPrivate)
            throws BOMChangeValueException, ExternIDNotUniqueException {
        Long outGroupID = Long.valueOf(0);
        preCheck(inName, inDescription);

        if (checkIdUnique(inName)) {
            setValues(inName, inDescription, inReviewers, inGuestDepth,
                    inMinGoupSize, inIsPrivate ? GroupHome.IS_PRIVATE
                            : GroupHome.IS_PUBLIC);
            try {
                set(GroupHome.KEY_STATE, Long.valueOf(1));
                outGroupID = insert(true);
            } catch (final VException | SQLException exc) {
                throw new BOMChangeValueException(exc.getMessage(), exc);
            }

            // save group administrators?

        } else {
            throw new ExternIDNotUniqueException(String.format(
                    "Group-Name %s not unique", inName)); // NOPMD
        }
        return outGroupID;
    }

    /** Insert a new entry and save the data.
     *
     * @return Long the ID of the new group entry
     * @throws BOMChangeValueException
     * @throws ExternIDNotUniqueException */
    @Override
    public Long ucNew() throws BOMChangeValueException,
            ExternIDNotUniqueException {
        Long outGroupID = Long.valueOf(0);
        String lName = "";
        boolean lIdUnique = true;
        try {
            lName = get(GroupHome.KEY_NAME).toString();
            preCheck(lName, get(GroupHome.KEY_DESCRIPTION).toString());
            if (checkIdUnique(lName)) {
                set(GroupHome.KEY_STATE, Long.valueOf(1));
                outGroupID = insert(true);
            } else {
                lIdUnique = false;
            }
        } catch (final SQLException | VException exc) {
            throw new BOMChangeValueException(exc.getMessage(), exc);
        }
        if (!lIdUnique) {
            throw new ExternIDNotUniqueException(String.format(
                    "Group-Name %s not unique", lName));
        }
        return outGroupID;
    }

    /** Update a discussion group entry .
     *
     * @param inName java.lang.String
     * @param inDescription java.lang.String
     * @param inReviewers java.lang.String
     * @param inGuestDepth java.lang.String
     * @param inMinGoupSize java.lang.String
     * @param inIsPrivate boolean
     * @exception VException
     * @exception WorkflowException
     * @throws ExternIDNotUniqueException */
    @Override
    public void ucSave(final String inName, final String inDescription, // NOPMD
            final String inReviewers, final String inGuestDepth,
            final String inMinGoupSize, final boolean inIsPrivate)
            throws VException, WorkflowException, ExternIDNotUniqueException {
        preCheck(inName, inDescription);

        if (!compareID(inName) && !checkIdUnique(inName)) { // NOPMD
            throw new ExternIDNotUniqueException(String.format(
                    "Group-Name %s not unique", inName));
        } else {
            final int lRegistered = getNumberOfRegistered();
            final boolean lActivePre = lRegistered >= getMinGroupSize();
            setValues(inName, inDescription, inReviewers, inGuestDepth,
                    inMinGoupSize, inIsPrivate ? GroupHome.IS_PRIVATE
                            : GroupHome.IS_PUBLIC);
            final boolean lActivePost = lRegistered >= getMinGroupSize();
            doUpdate(lActivePre, lActivePost);
        }
    }

    /** @param inActivePre
     * @param inActivePost
     * @throws GettingException
     * @throws WorkflowException
     * @throws BOMChangeValueException */
    private void doUpdate(final boolean inActivePre, final boolean inActivePost)
            throws GettingException, WorkflowException, BOMChangeValueException {
        try {
            update(true);

            // do we have to perform state transition?
            if (inActivePre != inActivePost) {
                final GroupStateChangeParameters lParameters = new GroupStateChangeParameters();
                if (inActivePost) {
                    if (isOpen()) {
                        doTransition(VIFGroupWorkflow.TRANS_ACTIVATE,
                                new Object[] { lParameters });
                    }
                } else {
                    if (isActive()) {
                        doTransition(VIFGroupWorkflow.TRANS_DEACTIVATE,
                                new Object[] { lParameters });
                    }
                }
            }
        } catch (final SQLException exc) {
            throw new BOMChangeValueException(exc.getMessage(), exc);
        }
    }

    /** Workflow aware subclasses may override.
     *
     * @param inTransitionName
     * @param inArgs
     * @throws WorkflowException */
    public void doTransition(final String inTransitionName,
            final Object... inArgs) throws WorkflowException {
        // intentionally left empty
    }

    @Override
    public void ucSave(final String inGroupNameBefore, // NOPMD
            final int inMinGroupSizeBefore) throws VException,
            WorkflowException, ExternIDNotUniqueException {
        final String lName = get(GroupHome.KEY_NAME).toString();
        preCheck(lName, get(GroupHome.KEY_DESCRIPTION).toString());
        if (!compareID(inGroupNameBefore) && !checkIdUnique(lName)) { // NOPMD
            throw new ExternIDNotUniqueException(String.format(
                    "Group-Name %s not unique", lName));
        } else {
            final int lRegistered = getNumberOfRegistered();
            final boolean lActivePre = lRegistered >= inMinGroupSizeBefore;
            final boolean lActivePost = lRegistered >= getMinGroupSize();
            doUpdate(lActivePre, lActivePost);
        }
    }

    /** Asserts the existence of mandatory input
     *
     * @param inName java.lang.String
     * @param inDescription java.lang.String */
    private void preCheck(final String inName, final String inDescription) {
        // pre: Name must be set
        Assert.assertTrue(AssertLevel.ERROR, this, "preCheck",
                !"".equals(inName));

        // pre: Description must be set
        Assert.assertTrue(AssertLevel.ERROR, this, "preCheck",
                !"".equals(inDescription));

    }

    private void setValues(final String inName, final String inDescription, // NOPMD
            final String inReviewers, final String inGuestDepth,
            final String inMinGoupSize, final Integer inPrivate)
            throws BOMChangeValueException {
        // the specified GroupID does not exist yet, therefore, we can continue
        Long lReviewers = Long.valueOf(DEFAULT_REVIEWERS);
        Long lGuestDepth = Long.valueOf(DEFAULT_GUEST_DEPTH);
        Long lMinGoupSize = Long.valueOf(DEFAULT_MIN_GROUP_SIZE);

        if (!inReviewers.isEmpty()) {
            lReviewers = Long.valueOf(inReviewers);
        }
        if (!inGuestDepth.isEmpty()) {
            lGuestDepth = Long.valueOf(inGuestDepth);
        }
        if (!inMinGoupSize.isEmpty()) {
            lMinGoupSize = Long.valueOf(inMinGoupSize);
        }

        try {
            set(GroupHome.KEY_NAME, inName);
            set(GroupHome.KEY_DESCRIPTION, inDescription);
            set(GroupHome.KEY_REVIEWERS, lReviewers);
            set(GroupHome.KEY_GUEST_DEPTH, lGuestDepth);
            set(GroupHome.KEY_MIN_GROUP_SIZE, lMinGoupSize);
            set(GroupHome.KEY_PRIVATE, Long.valueOf(inPrivate));
        } catch (final SettingException exc) {
            throw new BOMChangeValueException(exc.getMessage(), exc);
        }
    }

    private boolean checkIdUnique(final String inGroupID)
            throws BOMChangeValueException {
        // create a key for the GroupID
        final KeyObject lKeyGroupID = new KeyObjectImpl();
        try {
            lKeyGroupID.setValue(GroupHome.KEY_NAME, inGroupID);
        } catch (final VInvalidNameException | VInvalidValueException exc) {
            throw new BOMChangeValueException(exc.getMessage(), exc);
        }

        // check whether the key exists yet
        final GroupHomeImpl lHome = (GroupHomeImpl) VSys.homeManager
                .getHome(HOME_CLASS_NAME);
        try {
            lHome.findByKey(lKeyGroupID);
            return false;
        } catch (final BOMNotFoundException exc) {
            return true;
        } catch (final BOMInvalidKeyException exc) {
            return false;
        }
    }

    /** Returns true if the specified external GroupID is the same as this entry's external GroupID
     *
     * @param inGroupID java.lang.String
     * @return boolean True if equal */
    private boolean compareID(final String inGroupID) {
        try {
            return inGroupID.equals(get(GroupHome.KEY_NAME));
        } catch (final GettingException exc) {
            return false;
        }
    }

    /** Returns the number of root questions attached to this group.
     *
     * @return int */
    @Override
    public int rootCount() throws SQLException, VException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(QuestionHome.KEY_GROUP_ID, get(GroupHome.KEY_ID));
        lKey.setValue(QuestionHome.KEY_ROOT_QUESTION, QuestionHome.IS_ROOT);
        return BOMHelper.getQuestionHome().getCount(lKey);
    }

    /** Returns true if this group is of type where the contributions need to be reviewed (i.e. the number of reviewers
     * is set > 0).
     *
     * @return true, if the contributions have to be reviewed.
     * @throws VException */
    @Override
    public boolean needsReview() throws VException {
        return !"0".equals(get(GroupHome.KEY_REVIEWERS).toString());
    }

    /** Returns true if this group is private.
     *
     * @return boolean True if this group is private.
     * @throws VException */
    @Override
    public boolean isPrivate() throws VException {
        return !"0".equals(get(GroupHome.KEY_PRIVATE).toString());
    }

    /** Returns a collection of possible transitions contingent on this group's state.
     *
     * @return Collection of transitions
     * @throws VException */
    @Override
    public Collection<String> getTransitions() throws VException {
        final int lState = Integer
                .parseInt(get(GroupHome.KEY_STATE).toString());
        final Collection<String> outTransitions = new ArrayList<String>();
        switch (lState) {
        case VIFGroupWorkflow.S_CREATED:
            outTransitions.add(VIFGroupWorkflow.TRANS_OPEN);
            break;
        case VIFGroupWorkflow.S_OPEN:
            outTransitions.add(VIFGroupWorkflow.TRANS_CLOSE);
            break;
        case VIFGroupWorkflow.S_ACTIVE:
            outTransitions.add(VIFGroupWorkflow.TRANS_SUSPEND);
            outTransitions.add(VIFGroupWorkflow.TRANS_CLOSE);
            if (getNumberOfRegistered() < getMinGroupSize()) {
                outTransitions.add(VIFGroupWorkflow.TRANS_DEACTIVATE);
            }
            break;
        case VIFGroupWorkflow.S_SUSPENDED:
            outTransitions.add(VIFGroupWorkflow.TRANS_REACTIVATE);
            break;
        case VIFGroupWorkflow.S_SETTLED:
            outTransitions.add(VIFGroupWorkflow.TRANS_REACTIVATE);
            break;
        case VIFGroupWorkflow.S_CLOSED:
            outTransitions.add(VIFGroupWorkflow.TRANS_REACTIVATE);
            break;
        default:
        }
        return outTransitions;
    }

    /** Returns the correct type of closing transition according to this group's state.
     *
     * @return
     * @throws VException */
    @Override
    public String getCloseTransition() throws VException {
        switch (Integer.parseInt(get(GroupHome.KEY_STATE).toString())) {
        case VIFGroupWorkflow.S_OPEN:
            return VIFGroupWorkflow.TRANS_CLOSE1;
        case VIFGroupWorkflow.S_ACTIVE:
            return VIFGroupWorkflow.TRANS_CLOSE2;
        case VIFGroupWorkflow.S_SETTLED:
            return VIFGroupWorkflow.TRANS_CLOSE3;
        default:
        }
        return VIFGroupWorkflow.TRANS_CLOSE1;
    }

    /** Returns the correct type of reactivating transition according to this group's state.
     *
     * @return String
     * @throws VException */
    @Override
    public String getReactivateTransition() throws VException {
        switch (Integer.parseInt(get(GroupHome.KEY_STATE).toString())) {
        case VIFGroupWorkflow.S_SUSPENDED:
            return VIFGroupWorkflow.TRANS_REACTIVATE1;
        case VIFGroupWorkflow.S_SETTLED:
            return VIFGroupWorkflow.TRANS_REACTIVATE2;
        case VIFGroupWorkflow.S_CLOSED:
            if (getNumberOfRegistered() >= getMinGroupSize()) {
                return VIFGroupWorkflow.TRANS_REACTIVATE3;
            } else {
                return VIFGroupWorkflow.TRANS_REOPEN;
            }
        default:
        }
        return VIFGroupWorkflow.TRANS_REACTIVATE1;
    }

    /** Returns the number of participants registered to this group.
     *
     * @return int The number of registered participants.
     * @throws VException */
    @Override
    public int getNumberOfRegistered() throws VException {
        return BOMHelper.getParticipantHome().getParticipantsOfGroup(
                Long.valueOf(get(GroupHome.KEY_ID).toString()));
    }

    /** Returns the minimal number of participants needed for this group to be active.
     *
     * @return int The minimal group size
     * @throws GettingException */
    @Override
    public int getMinGroupSize() throws GettingException {
        return Integer.parseInt(get(GroupHome.KEY_MIN_GROUP_SIZE).toString());
    }

    /** Returns the group's visibility, i.e. the depth a unauthenticated user (that is a guest) can see questions. 0
     * means the whole group is invisible for guests, 1 means only the group's root question is visible, 2 means only
     * the root question and the first level of follow up questions is visible, ...
     *
     * @return Long
     * @throws GettingException */
    @Override
    public Long getGuestDepth() throws GettingException {
        return Long.valueOf(get(GroupHome.KEY_GUEST_DEPTH).toString());
    }

    private String getActualStateValue() throws GettingException {
        return get(GroupHome.KEY_STATE).toString();
    }

    /** Checks whether this group in the state just created.
     *
     * @return boolean <code>true</code> if the group has just been created.
     * @throws GettingException */
    @Override
    public boolean isCreated() throws GettingException {
        return VIFGroupWorkflow.STATE_CREATED.equals(getActualStateValue());
    }

    /** Checks whether this group is active.
     *
     * @return boolean True if this group is active.
     * @throws GettingException */
    @Override
    public boolean isActive() throws GettingException {
        return VIFGroupWorkflow.STATE_ACTIVE.equals(getActualStateValue());
    }

    /** Checks whether this group is open.
     *
     * @return boolean True if this group is open.
     * @throws GettingException */
    private boolean isOpen() throws GettingException {
        return VIFGroupWorkflow.STATE_OPEN.equals(getActualStateValue());
    }

    /** Checks whether the specified actor is participant in this group.
     *
     * @param inActorID Long
     * @return boolean True if the actor is participant.
     * @throws VException */
    @Override
    public boolean isParticipant(final Long inActorID) throws VException {
        return BOMHelper.getParticipantHome().isParticipantOfGroup(
                Long.valueOf(get(GroupHome.KEY_ID).toString()), inActorID);
    }

    /** Checks the group's activation state against the specified value. If the number exceeds the minimal group size,
     * the group is activated.
     *
     * @param inRegistered Number of registered members of this group.
     * @throws GettingException
     * @throws WorkflowException */
    @Override
    public void checkActivationState(final int inRegistered)
            throws GettingException, WorkflowException {
        if (!isActive()) {
            if (isOpen()) {
                // we only can activate open groups
                if (inRegistered >= getMinGroupSize()) {
                    doActivation();
                }
            }
        }
    }

    /** Subclasses may override.
     *
     * @throws WorkflowException */
    protected void doActivation() throws WorkflowException {
        // intentionally left empty
    }

    /** Returns a array of the mail addresses of this group's participants.
     *
     * @return InternetAddress[]
     * @throws VException
     * @throws SQLException
     * @throws AddressException */
    @Override
    public InternetAddress[] getParticipantsMail() throws VException,
            SQLException, AddressException {
        final Collection<String> lMails = BOMHelper
                .getJoinParticipantToMemberHome().getParticipantsMail(
                        Long.valueOf(get(GroupHome.KEY_ID).toString()));
        final StringBuffer lAddresses = new StringBuffer();
        boolean lFirst = true;
        for (final String lMailAddress : lMails) {
            if (!lFirst) {
                lAddresses.append(", ");
            }
            lFirst = false;
            lAddresses.append(lMailAddress);
        }
        return InternetAddress.parse(new String(lAddresses));
    }

    @Override
    public boolean isDeletable() throws VException { // NOPMD
        final String lState = get(GroupHome.KEY_STATE).toString();
        return lState.equals(VIFGroupWorkflow.STATE_CREATED)
                || lState.equals(VIFGroupWorkflow.STATE_CLOSED);
    }

    @Override
    public boolean isValid() { // NOPMD
        try {
            preCheck(get(GroupHome.KEY_NAME).toString(),
                    get(GroupHome.KEY_DESCRIPTION).toString());
            return true;
        } catch (final GettingException | AssertionFailedError | NullPointerException exc) { // NOPMD
            return false;
        }
    }

}

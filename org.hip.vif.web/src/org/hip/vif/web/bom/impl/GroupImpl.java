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
package org.hip.vif.web.bom.impl; // NOPMD

import java.io.IOException;
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
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.DefaultExceptionHandler;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.AssertionFailedError;
import org.hip.kernel.util.VInvalidNameException;
import org.hip.kernel.util.VInvalidValueException;
import org.hip.kernel.workflow.State;
import org.hip.kernel.workflow.Workflow;
import org.hip.kernel.workflow.WorkflowAware;
import org.hip.kernel.workflow.WorkflowAwareImpl;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.bom.search.VIFContentIndexer;
import org.hip.vif.core.exc.Assert;
import org.hip.vif.core.exc.Assert.AssertLevel;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.hip.vif.core.util.GroupStateChangeParameters;
import org.hip.vif.web.Activator;
import org.hip.vif.web.bom.Group;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.mail.GroupStateChangeNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This domain object implements the Group interface.<br />
 * Note: this group class is workflow aware.
 *
 * @author Benno Luthiger
 * @see org.hip.vif.core.bom.Group */
@SuppressWarnings("serial")
public class GroupImpl extends org.hip.vif.core.bom.impl.GroupImpl implements
Group, WorkflowAware, VIFGroupWorkflow {
    private static final Logger LOG = LoggerFactory.getLogger(GroupImpl.class);

    public final static String HOME_CLASS_NAME = "org.hip.vif.web.bom.impl.GroupHomeImpl";

    private final static int DEFAULT_REVIEWERS = 1;
    private final static int DEFAULT_GUEST_DEPTH = 0;
    private final static int DEFAULT_MIN_GROUP_SIZE = 0;

    private final static String MSG_ACTIVE = "org.hip.vif.msg.notification.state.active";
    private final static String MSG_SUSPENDED = "org.hip.vif.msg.notification.state.suspended";
    private final static String MSG_SETTLED = "org.hip.vif.msg.notification.state.settled";
    private final static String MSG_CLOSED = "org.hip.vif.msg.notification.state.closed";
    private final static String MSG_DEACTIVATED = "org.hip.vif.msg.notification.state.deactivated";
    private final static String MSG_REACTIVATED = "org.hip.vif.msg.notification.state.reactivated";

    // instance variables
    private final WorkflowAwareImpl workflowAware;

    /** Constructor for GroupImpl. */
    public GroupImpl() throws WorkflowException {
        super();
        workflowAware = new WorkflowAwareImpl(createWorkflow(STATE_CREATED),
                new Object[] {}, this);
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
            } catch (final VException exc) {
                throw new BOMChangeValueException(exc.getMessage(), exc);
            } catch (final SQLException exc) {
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
        } catch (final SQLException exc) {
            throw new BOMChangeValueException(exc.getMessage(), exc);
        } catch (final VException exc) {
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
        } catch (final VInvalidNameException exc) {
            throw new BOMChangeValueException(exc.getMessage(), exc);
        } catch (final VInvalidValueException exc) {
            throw new BOMChangeValueException(exc.getMessage(), exc);
        }

        // check whether the key exists yet
        try {
            final GroupHomeImpl lHome = (GroupHomeImpl) VifBOMHelper.getGroupHome();
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
        case S_CREATED:
            outTransitions.add(VIFGroupWorkflow.TRANS_OPEN);
            break;
        case S_OPEN:
            outTransitions.add(VIFGroupWorkflow.TRANS_CLOSE);
            break;
        case S_ACTIVE:
            outTransitions.add(VIFGroupWorkflow.TRANS_SUSPEND);
            outTransitions.add(VIFGroupWorkflow.TRANS_CLOSE);
            if (getNumberOfRegistered() < getMinGroupSize()) {
                outTransitions.add(VIFGroupWorkflow.TRANS_DEACTIVATE);
            }
            break;
        case S_SUSPENDED:
            outTransitions.add(VIFGroupWorkflow.TRANS_REACTIVATE);
            break;
        case S_SETTLED:
            outTransitions.add(VIFGroupWorkflow.TRANS_REACTIVATE);
            break;
        case S_CLOSED:
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
        case S_OPEN:
            return TRANS_CLOSE1;
        case S_ACTIVE:
            return TRANS_CLOSE2;
        case S_SETTLED:
            return TRANS_CLOSE3;
        default:
        }
        return TRANS_CLOSE1;
    }

    /** Returns the correct type of reactivating transition according to this group's state.
     *
     * @return String
     * @throws VException */
    @Override
    public String getReactivateTransition() throws VException {
        switch (Integer.parseInt(get(GroupHome.KEY_STATE).toString())) {
        case S_SUSPENDED:
            return TRANS_REACTIVATE1;
        case S_SETTLED:
            return TRANS_REACTIVATE2;
        case S_CLOSED:
            if (getNumberOfRegistered() >= getMinGroupSize()) {
                return TRANS_REACTIVATE3;
            } else {
                return TRANS_REOPEN;
            }
        default:
        }
        return TRANS_REACTIVATE1;
    }

    /** Returns the number of participants registered to this group.
     *
     * @return int The number of registered participants.
     * @throws VException */
    @Override
    public int getNumberOfRegistered() throws VException {
        return VifBOMHelper.getParticipantHome().getParticipantsOfGroup(
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

    private void setState(final String inNewState) {
        try {
            set(GroupHome.KEY_STATE, Long.valueOf(inNewState));
            update(true);
        } catch (final VException | SQLException exc) {
            DefaultExceptionHandler.instance().handle(this, exc);
        }
    }

    /** Use the afterLoad hook to rebind the reinitialized object to the Workflow, i.e. to set the stored state to the
     * workflow. */
    @Override
    protected void afterLoad() {
        try {
            workflowAware.enterWorkflow(createWorkflow(getActualStateValue()),
                    new Object[] {}, this);
        } catch (final VException | WorkflowException exc) {
            DefaultExceptionHandler.instance().handle(this, exc);
        }
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
        return STATE_CREATED.equals(getActualStateValue());
    }

    /** Checks whether this group is active.
     *
     * @return boolean True if this group is active.
     * @throws GettingException */
    @Override
    public boolean isActive() throws GettingException {
        return STATE_ACTIVE.equals(getActualStateValue());
    }

    /** Checks whether this group is open.
     *
     * @return boolean True if this group is open.
     * @throws GettingException */
    private boolean isOpen() throws GettingException {
        return STATE_OPEN.equals(getActualStateValue());
    }

    /** Checks whether the specified actor is participant in this group.
     *
     * @param inActorID Long
     * @return boolean True if the actor is participant.
     * @throws VException */
    @Override
    public boolean isParticipant(final Long inActorID) throws VException {
        return VifBOMHelper.getParticipantHome().isParticipantOfGroup(
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
                    final GroupStateChangeParameters lParameters = new GroupStateChangeParameters();
                    doTransition(TRANS_ACTIVATE, new Object[] { lParameters });
                }
            }
        }
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

    /** As administrators can add questions to a group that is created but not public, these questions are not indexed
     * (and, consistently, arn't retrieved by a search query). However, such questions have to be made searchable as
     * soon as the group is opened (transition from <code>created</code> to <code>open</code> state).
     *
     * @throws SQLException
     * @throws VException
     * @throws IOException */
    private void indexContent() throws VException, SQLException, IOException {
        final VIFContentIndexer lIndexer = new VIFContentIndexer();
        lIndexer.addGroupContentToIndex(get(GroupHome.KEY_ID).toString());
    }

    /** We don't want content in closed groups to show up in a search query. Therefore, remove them from the index.
     *
     * @throws IOException
     * @throws SQLException
     * @throws VException */
    private void deleteContentFromIndex() throws VException, SQLException,
    IOException {
        final VIFContentIndexer lIndexer = new VIFContentIndexer();
        lIndexer.removeGroupContent(get(GroupHome.KEY_ID).toString());
    }

    // *****************************************************************
    // Handle group workflow

    @Override
    public void doTransition(final String inTransitionName, // NOPMD
            final Object... inArgs) throws WorkflowException {
        workflowAware.doTransition(inTransitionName, inArgs, this);
    }

    @Override
    public String getStateName() { // NOPMD
        return workflowAware.getStateName();
    }

    @Override
    public State getState() throws WorkflowException { // NOPMD
        return workflowAware.getState();
    }

    @Override
    public void enterWorkflow(final Workflow inWorkflow, final Object... inArgs) // NOPMD
            throws WorkflowException {
        workflowAware.enterWorkflow(inWorkflow, inArgs, this);
    }

    @Override
    public void enterWorkflow(final Workflow inWorkflow, // NOPMD
            final String inInitialStateName, final Object... inArgs)
                    throws WorkflowException {
        workflowAware.enterWorkflow(inWorkflow, inInitialStateName, inArgs,
                this);
    }

    private Workflow createWorkflow(final String inInitialStateName)
            throws WorkflowException {
        final Workflow lWorkflow = new Workflow();
        lWorkflow.addState(STATE_CREATED);
        lWorkflow.addState(STATE_OPEN);
        lWorkflow.addState(STATE_ACTIVE);
        lWorkflow.addState(STATE_SUSPENDED);
        lWorkflow.addState(STATE_SETTLED);
        lWorkflow.addState(STATE_CLOSED);

        lWorkflow.addTransition(TRANS_OPEN, STATE_CREATED, STATE_OPEN);
        lWorkflow.addTransition(TRANS_ACTIVATE, STATE_OPEN, STATE_ACTIVE);
        lWorkflow.addTransition(TRANS_DEACTIVATE, STATE_ACTIVE, STATE_OPEN);
        lWorkflow.addTransition(TRANS_SUSPEND, STATE_ACTIVE, STATE_SUSPENDED);
        lWorkflow.addTransition(TRANS_SETTLE, STATE_ACTIVE, STATE_SETTLED);
        lWorkflow.addTransition(TRANS_CLOSE1, STATE_OPEN, STATE_CLOSED);
        lWorkflow.addTransition(TRANS_CLOSE2, STATE_ACTIVE, STATE_CLOSED);
        lWorkflow.addTransition(TRANS_CLOSE3, STATE_SETTLED, STATE_CLOSED);
        lWorkflow.addTransition(TRANS_REACTIVATE1, STATE_SUSPENDED,
                STATE_ACTIVE);
        lWorkflow.addTransition(TRANS_REACTIVATE2, STATE_SETTLED, STATE_ACTIVE);
        lWorkflow.addTransition(TRANS_REACTIVATE3, STATE_CLOSED, STATE_ACTIVE);
        lWorkflow.addTransition(TRANS_REOPEN, STATE_CLOSED, STATE_OPEN);

        lWorkflow.setInitialStateName(inInitialStateName);
        return lWorkflow;
    }

    // The following methods define the interface of the workflow.

    /** STATE_CREATED */
    public void onEnter_1(final GroupStateChangeParameters inParameters) { // NOPMD
        LOG.trace("onEnter_1(): STATE_CREATED");
    }

    /** STATE_CREATED */
    public void onLeave_1(final GroupStateChangeParameters inParameters) { // NOPMD
        LOG.trace("onLeave_1(): STATE_CREATED");
    }

    /** STATE_OPEN */
    public void onEnter_2(final GroupStateChangeParameters inParameters) { // NOPMD
        LOG.trace("onEnter_2(): STATE_OPEN");
    }

    /** STATE_OPEN */
    public void onLeave_2(final GroupStateChangeParameters inParameters) { // NOPMD
        LOG.trace("onLeave_2(): STATE_OPEN");
    }

    /** STATE_ACTIVE */
    public void onEnter_3(final GroupStateChangeParameters inParameters) { // NOPMD
        LOG.trace("onEnter_3(): STATE_ACTIVE");
    }

    /** STATE_ACTIVE */
    public void onLeave_3(final GroupStateChangeParameters inParameters) { // NOPMD
        LOG.trace("onLeave_3(): STATE_ACTIVE");
    }

    /** STATE_SUSPENDED */
    public void onEnter_4(final GroupStateChangeParameters inParameters) { // NOPMD
        LOG.trace("onEnter_4(): STATE_SUSPENDED");
        setParameters(MSG_SUSPENDED, inParameters);
    }

    /** STATE_SUSPENDED */
    public void onLeave_4(final GroupStateChangeParameters inParameters) { // NOPMD
        LOG.trace("onLeave_4(): STATE_SUSPENDED");
    }

    /** STATE_SETTLED */
    public void onEnter_5(final GroupStateChangeParameters inParameters) { // NOPMD
        LOG.trace("onEnter_5(): STATE_SETTLED");
        setParameters(MSG_SETTLED, inParameters);
    }

    /** STATE_SETTLED */
    public void onLeave_5(final GroupStateChangeParameters inParameters) { // NOPMD
        LOG.trace("onLeave_5(): STATE_SETTLED");
    }

    /** STATE_CLOSED */
    public void onEnter_6(final GroupStateChangeParameters inParameters) { // NOPMD
        LOG.trace("onEnter_6(): STATE_CLOSED");
        setParameters(MSG_CLOSED, inParameters);
        try {
            deleteContentFromIndex();
        } catch (final VException | SQLException | IOException exc) {
            DefaultExceptionHandler.instance().handle(this, exc);
        }
    }

    /** STATE_CLOSED */
    public void onLeave_6(final GroupStateChangeParameters inParameters) { // NOPMD
        LOG.trace("onLeave_6(): STATE_CLOSED");
    }

    public void onTransition_Open(final GroupStateChangeParameters inParameters) { // NOPMD
        LOG.trace("onTransition_Open(): Open");
        setState(STATE_OPEN);
        try {
            indexContent();
        } catch (final VException | SQLException | IOException exc) {
            DefaultExceptionHandler.instance().handle(this, exc);
        }
    }

    public void onTransition_Activate( // NOPMD
            final GroupStateChangeParameters inParameters) {
        LOG.trace("onTransition_Activate(): Activate");
        setState(STATE_ACTIVE);
        try {
            final String lMessage = Activator.getMessages().getMessage(
                    MSG_ACTIVE);
            final GroupStateChangeNotification lMail = new GroupStateChangeNotification(
                    getParticipantsMail(), lMessage,
                    (String) get(GroupHome.KEY_NAME), true);
            lMail.send();
            LOG.trace("onTransition_Activate(): A notification mail to the group's participants has been sent.");
        } catch (final AddressException | VException | SQLException exc) {
            DefaultExceptionHandler.instance().handle(this, exc);
        }
    }

    public void onTransition_Deactivate( // NOPMD
            final GroupStateChangeParameters inParameters) {
        LOG.trace("onTransition_Deactivate(): Deactivate");
        setState(STATE_OPEN);
        setParameters(MSG_DEACTIVATED, inParameters);
    }

    public void onTransition_Suspend( // NOPMD
            final GroupStateChangeParameters inParameters) {
        LOG.trace("onTransition_Suspend(): Suspend");
        setState(STATE_SUSPENDED);
    }

    public void onTransition_Settle( // NOPMD
            final GroupStateChangeParameters inParameters) {
        LOG.trace("onTransition_Settle(): Settle");
        setState(STATE_SETTLED);
    }

    public void onTransition_Close1( // NOPMD
            final GroupStateChangeParameters inParameters) {
        LOG.trace("onTransition_Close1(): Close1");
        setState(STATE_CLOSED);
    }

    public void onTransition_Close2( // NOPMD
            final GroupStateChangeParameters inParameters) {
        LOG.trace("onTransition_Close2(): Close2");
        setState(STATE_CLOSED);
    }

    public void onTransition_Close3( // NOPMD
            final GroupStateChangeParameters inParameters) {
        LOG.trace("onTransition_Close3(): Close3");
        setState(STATE_CLOSED);
    }

    public void onTransition_Reactivate1( // NOPMD
            final GroupStateChangeParameters inParameters) {
        LOG.trace("onTransition_Reactivate1(): Reactivate1");
        setState(STATE_ACTIVE);
        setParameters(MSG_REACTIVATED, inParameters);
    }

    public void onTransition_Reactivate2( // NOPMD
            final GroupStateChangeParameters inParameters) {
        LOG.trace("onTransition_Reactivate2(): Reactivate2");
        setState(STATE_ACTIVE);
        setParameters(MSG_REACTIVATED, inParameters);
    }

    public void onTransition_Reactivate3( // NOPMD
            final GroupStateChangeParameters inParameters) {
        LOG.trace("onTransition_Reactivate3(): Reactivate3");
        setState(STATE_ACTIVE);
        setParameters(MSG_REACTIVATED, inParameters);
    }

    public void onTransition_Reopen( // NOPMD
            final GroupStateChangeParameters inParameters) {
        LOG.trace("onTransition_Reopen(): Reopen");
        setState(STATE_OPEN);
        try {
            indexContent();
        } catch (final VException | SQLException | IOException exc) {
            DefaultExceptionHandler.instance().handle(this, exc);
        }
    }

    private void setParameters(final String inMessageKey,
            final GroupStateChangeParameters inParameters) {
        try {
            final String lGroupName = (String) get(GroupHome.KEY_NAME);
            inParameters.setNotification(true);
            inParameters.setMailSubject(GroupStateChangeNotification
                    .getSubject(lGroupName));
            inParameters.setMailBody(GroupStateChangeNotification.getBody(
                    lGroupName, Activator.getMessages()
                    .getMessage(inMessageKey)));
            inParameters.setGroupName(lGroupName);
            inParameters.setGroupID(get(GroupHome.KEY_ID).toString());
        } catch (final VException exc) {
            DefaultExceptionHandler.instance().handle(this, exc);
        }
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

    // ====

    @Override
    protected void doActivation() throws WorkflowException { // NOPMD
        final GroupStateChangeParameters lParameters = new GroupStateChangeParameters();
        doTransition(TRANS_ACTIVATE, new Object[] { lParameters });
    }

}

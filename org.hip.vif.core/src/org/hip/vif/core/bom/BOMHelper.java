/**
	This package is part of the application VIF.
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
package org.hip.vif.core.bom; // NOPMD

import java.io.IOException;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.util.VInvalidNameException;
import org.hip.kernel.util.VInvalidValueException;
import org.hip.vif.core.bom.impl.*;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.member.IMemberSearcher;
import org.hip.vif.core.service.MemberUtility;
import org.hip.vif.core.service.PreferencesHandler;

/** Helper class to return the various domain object homes.
 *
 * @author: Benno Luthiger */
public final class BOMHelper {

    private BOMHelper() {
        // prevent instantiatio
    }

    /** Returns the MemberHistoryHome
     *
     * @return {@link MemberHistoryHome} */
    public static MemberHistoryHome getMemberHistoryHome() {
        return (MemberHistoryHome) VSys.homeManager
                .getHome(MemberHistoryImpl.HOME_CLASS_NAME);
    }

    /** Returns the MemberHome for the <code>Member</code> objects the user are authenticated against.
     *
     * @return {@link MemberHome} */
    public static MemberHome getMemberHome() {
        final IMemberSearcher lMemberSearcher = MemberUtility.INSTANCE
                .getActiveMemberSearcher();
        if (lMemberSearcher != null) {
            return lMemberSearcher.getMemberAuthenticationHome();
        }
        return (MemberHome) VSys.homeManager
                .getHome(MemberImpl.HOME_CLASS_NAME);
    }

    /** Returns the MemberHome for the cached <code>Member</code> objects.
     *
     * @return {@link MemberHome} */
    public static MemberHome getMemberCacheHome() {
        final IMemberSearcher lMemberSearcher = MemberUtility.INSTANCE
                .getActiveMemberSearcher();
        if (lMemberSearcher != null) {
            return lMemberSearcher.getMemberCacheHome();
        }
        return (MemberHome) VSys.homeManager
                .getHome(MemberImpl.HOME_CLASS_NAME);
    }

    /** Returns the RoleHome
     *
     * @return {@link RoleHome} */
    public static RoleHome getRoleHome() {
        return (RoleHome) VSys.homeManager.getHome(RoleImpl.HOME_CLASS_NAME);
    }

    /** Returns the LinkMemberRoleHome
     *
     * @return {@link LinkMemberRoleHome} */
    public static LinkMemberRoleHome getLinkMemberRoleHome() {
        return (LinkMemberRoleHome) VSys.homeManager
                .getHome(LinkMemberRoleImpl.HOME_CLASS_NAME);
    }

    /** Returns the PermissionHome
     *
     * @return {@link PermissionHome} */
    public static PermissionHome getPermissionHome() {
        return (PermissionHome) VSys.homeManager
                .getHome(PermissionImpl.HOME_CLASS_NAME);
    }

    /** Returns the LinkPermissionRoleHome
     *
     * @return {@link LinkPermissionRoleHome} */
    public static LinkPermissionRoleHome getLinkPermissionRoleHome() {
        return (LinkPermissionRoleHome) VSys.homeManager
                .getHome(LinkPermissionRoleImpl.HOME_CLASS_NAME);
    }

    /** Returns the GroupHome.
     * <p>
     * <b>Note:</b> this will return the home for the group model which is not workflow aware!
     * </p>
     *
     * @return {@link GroupHome} */
    public static GroupHome getGroupHome() {
        return (GroupHome) VSys.homeManager.getHome(GroupImpl.HOME_CLASS_NAME);
    }

    /** Returns the GroupAdminHome
     *
     * @return {@link GroupAdminHome} */
    public static GroupAdminHome getGroupAdminHome() {
        return (GroupAdminHome) VSys.homeManager
                .getHome(GroupAdminImpl.HOME_CLASS_NAME);
    }

    /** Returns the ParticipantHome
     *
     * @return {@link ParticipantHome} */
    public static ParticipantHome getParticipantHome() {
        return (ParticipantHome) VSys.homeManager
                .getHome(ParticipantImpl.HOME_CLASS_NAME);
    }

    /** Returns the QuestionHome
     *
     * @return {@link QuestionHome} */
    public static QuestionHome getQuestionHome() {
        return (QuestionHome) VSys.homeManager
                .getHome(QuestionImpl.HOME_CLASS_NAME);
    }

    /** Returns the QuestionHistoryHome
     *
     * @return {@link QuestionHistoryHome} */
    public static QuestionHistoryHome getQuestionHistoryHome() {
        return (QuestionHistoryHome) VSys.homeManager
                .getHome(QuestionHistoryImpl.HOME_CLASS_NAME);
    }

    /** Returns the CompletionHome
     *
     * @return {@link CompletionHome} */
    public static CompletionHome getCompletionHome() {
        return (CompletionHome) VSys.homeManager
                .getHome(CompletionImpl.HOME_CLASS_NAME);
    }

    /** Returns the CompletionHistoryHome
     *
     * @return {@link CompletionHistoryHome} */
    public static CompletionHistoryHome getCompletionHistoryHome() {
        return (CompletionHistoryHome) VSys.homeManager
                .getHome(CompletionHistoryImpl.HOME_CLASS_NAME);
    }

    /** Returns the QuestionAuthorReviewerHome
     *
     * @return org.hip.vif.bom.QuestionAuthorReviewerHome */
    public static QuestionAuthorReviewerHome getQuestionAuthorReviewerHome() {
        return (QuestionAuthorReviewerHome) VSys.homeManager
                .getHome(QuestionAuthorReviewerImpl.HOME_CLASS_NAME);
    }

    /** Returns the CompletionAuthorReviewerHome
     *
     * @return org.hip.vif.bom.CompletionAuthorReviewerHome */
    public static CompletionAuthorReviewerHome getCompletionAuthorReviewerHome() {
        return (CompletionAuthorReviewerHome) VSys.homeManager
                .getHome(CompletionAuthorReviewerImpl.HOME_CLASS_NAME);
    }

    /** Returns the TextAuthorReviewerHome
     *
     * @return {@link TextAuthorReviewerHome} */
    public static TextAuthorReviewerHome getTextAuthorReviewerHome() {
        return (TextAuthorReviewerHome) VSys.homeManager
                .getHome(TextAuthorReviewerImpl.HOME_CLASS_NAME);
    }

    /** Returns the QuestionHierarchyHome
     *
     * @return {@link QuestionHierarchyHome} */
    public static QuestionHierarchyHome getQuestionHierarchyHome() {
        return (QuestionHierarchyHome) VSys.homeManager
                .getHome(QuestionHierarchyImpl.HOME_CLASS_NAME);
    }

    /** Returns the SubscriptionHome
     *
     * @return {@link SubscriptionHome} */
    public static SubscriptionHome getSubscriptionHome() {
        return (SubscriptionHome) VSys.homeManager
                .getHome(SubscriptionImpl.HOME_CLASS_NAME);
    }

    /** Returns the BookmarkHome
     *
     * @return {@link BookmarkHome} */
    public static BookmarkHome getBookmarkHome() {
        return (BookmarkHome) VSys.homeManager
                .getHome(BookmarkImpl.HOME_CLASS_NAME);
    }

    /** Returns the JoinMemberToPermissionHome
     *
     * @return {@link JoinMemberToPermissionHome} */
    public static JoinMemberToPermissionHome getJoinMemberToPermissionHome() {
        return (JoinMemberToPermissionHome) VSys.homeManager
                .getHome(JoinMemberToPermission.HOME_CLASS_NAME);
    }

    /** Returns the JoinMemberToRoleHome
     *
     * @return {@link JoinMemberToRoleHome} */
    public static JoinMemberToRoleHome getJoinMemberToRoleHome() {
        return (JoinMemberToRoleHome) VSys.homeManager
                .getHome(JoinMemberToRole.HOME_CLASS_NAME);
    }

    /** Returns the JoinGroupAdminToMemberHome
     *
     * @return {@link JoinGroupAdminToMemberHome} */
    public static JoinGroupAdminToMemberHome getJoinGroupAdminToMemberHome() {
        return (JoinGroupAdminToMemberHome) VSys.homeManager
                .getHome(JoinGroupAdminToMember.HOME_CLASS_NAME);
    }

    /** Returns the JoinGroupAdminToGroupHome
     *
     * @return {@link JoinGroupAdminToGroupHome} */
    public static JoinGroupAdminToGroupHome getJoinGroupAdminToGroupHome() {
        return (JoinGroupAdminToGroupHome) VSys.homeManager
                .getHome(JoinGroupAdminToGroup.HOME_CLASS_NAME);
    }

    /** Returns the JoinParticipantToMemberHome
     *
     * @return {@link JoinParticipantToMemberHome} */
    public static JoinParticipantToMemberHome getJoinParticipantToMemberHome() {
        return (JoinParticipantToMemberHome) VSys.homeManager
                .getHome(JoinParticipantToMember.HOME_CLASS_NAME);
    }

    /** Returns the JoinParticipantToGroupHome
     *
     * @return {@link JoinParticipantToGroupHome} */
    public static JoinParticipantToGroupHome getJoinParticipantToGroupHome() {
        return (JoinParticipantToGroupHome) VSys.homeManager
                .getHome(JoinParticipantToGroup.HOME_CLASS_NAME);
    }

    /** Returns the JoinRoleToPermissionHome
     *
     * @return {@link JoinRoleToPermissionHome} */
    public static JoinRoleToPermissionHome getJoinRoleToPermissionHome() {
        return (JoinRoleToPermissionHome) VSys.homeManager
                .getHome(JoinRoleToPermission.HOME_CLASS_NAME);
    }

    /** Returns the JoinQuestionToAuthorReviewerHome
     *
     * @return {@link JoinQuestionToAuthorReviewerHome} */
    public static JoinQuestionToAuthorReviewerHome getJoinQuestionToAuthorReviewerHome() {
        return (JoinQuestionToAuthorReviewerHome) VSys.homeManager
                .getHome(JoinQuestionToAuthorReviewer.HOME_CLASS_NAME);
    }

    /** @return JoinQuestionToContributorsHome */
    public static JoinQuestionToContributorsHome getJoinQuestionToContributorsHome() {
        return (JoinQuestionToContributorsHome) VSys.homeManager
                .getHome(JoinQuestionToContributors.HOME_CLASS_NAME);
    }

    /** Returns the JoinQuestionToChildHome
     *
     * @return {@link JoinQuestionToChildHome} */
    public static JoinQuestionToChildHome getJoinQuestionToChildHome() {
        return (JoinQuestionToChildHome) VSys.homeManager
                .getHome(JoinQuestionToChild.HOME_CLASS_NAME);
    }

    /** Returns the JoinQuestionToChildAndAuthorHome
     *
     * @return {@link JoinQuestionToChildAndAuthorHome} */
    public static JoinQuestionToChildAndAuthorHome getJoinQuestionToChildAndAuthorHome() {
        return (JoinQuestionToChildAndAuthorHome) VSys.homeManager
                .getHome(JoinQuestionToChildAndAuthor.HOME_CLASS_NAME);
    }

    /** Returns the JoinCompletionToMemberHome
     *
     * @return {@link JoinCompletionToMemberHome} */
    public static JoinCompletionToMemberHome getJoinCompletionToMemberHome() {
        return (JoinCompletionToMemberHome) VSys.homeManager
                .getHome(JoinCompletionToMember.HOME_CLASS_NAME);
    }

    /** @return {@link JoinTextToQuestionHome} */
    public static JoinTextToQuestionHome getJoinTextToQuestionHome() {
        return (JoinTextToQuestionHome) VSys.homeManager
                .getHome(JoinTextToQuestion.HOME_CLASS_NAME);
    }

    /** @return {@link JoinTextToAuthorReviewerHome} */
    public static JoinTextToAuthorReviewerHome getJoinTextToAuthorReviewerHome() {
        return (JoinTextToAuthorReviewerHome) VSys.homeManager
                .getHome(JoinTextToAuthorReviewer.HOME_CLASS_NAME);
    }

    /** Returns the JoinAuthorReviewerToQuestionHome
     *
     * @return {@link JoinAuthorReviewerToQuestionHome} */
    public static JoinAuthorReviewerToQuestionHome getJoinAuthorReviewerToQuestionHome() {
        return (JoinAuthorReviewerToQuestionHome) VSys.homeManager
                .getHome(JoinAuthorReviewerToQuestion.HOME_CLASS_NAME);
    }

    /** Returns the JoinAuthorReviewerToCompletionHome
     *
     * @return {@link JoinAuthorReviewerToCompletionHome} */
    public static JoinAuthorReviewerToCompletionHome getJoinAuthorReviewerToCompletionHome() {
        return (JoinAuthorReviewerToCompletionHome) VSys.homeManager
                .getHome(JoinAuthorReviewerToCompletion.HOME_CLASS_NAME);
    }

    /** Returns the JoinSubscriptionToMemberHome
     *
     * @return {@link JoinSubscriptionToMemberHome} */
    public static JoinSubscriptionToMemberHome getJoinSubscriptionToMemberHome() {
        return (JoinSubscriptionToMemberHome) VSys.homeManager
                .getHome(JoinSubscriptionToMember.HOME_CLASS_NAME);
    }

    /** Returns the JoinSubscriptionToQuestionHome
     *
     * @return {@link JoinSubscriptionToQuestionHome} */
    public static JoinSubscriptionToQuestionHome getJoinSubscriptionToQuestionHome() {
        return (JoinSubscriptionToQuestionHome) VSys.homeManager
                .getHome(JoinSubscriptionToQuestion.HOME_CLASS_NAME);
    }

    /** Returns the JoinCompletionToQuestionHome
     *
     * @return {@link JoinCompletionToQuestionHome} */
    public static JoinCompletionToQuestionHome getJoinCompletionToQuestionHome() {
        return (JoinCompletionToQuestionHome) VSys.homeManager
                .getHome(JoinCompletionToQuestion.HOME_CLASS_NAME);
    }

    /** Returns the NestedGroupHome
     *
     * @return {@link NestedGroupHome} */
    public static NestedGroupHome getNestedGroupHome() {
        return (NestedGroupHome) VSys.homeManager
                .getHome(NestedGroup.HOME_CLASS_NAME);
    }

    /** @return {@link NestedGroupHome2} */
    public static NestedGroupHome2 getNestedGroupHome2() {
        return (NestedGroupHome2) VSys.homeManager
                .getHome(NestedGroup.HOME2_CLASS_NAME);
    }

    /** @return {@link NestedParticipantsOfGroupHome} */
    public static NestedParticipantsOfGroupHome getNestedParticipantsOfGroupHome() {
        return (NestedParticipantsOfGroupHome) VSys.homeManager
                .getHome(NestedParticipantsOfGroup.HOME_CLASS_NAME);
    }

    /** @return {@link QuestionForGuestsHome} */
    public static QuestionForGuestsHome getQuestionForGuestsHome() {
        final QuestionForGuestsHome outHome = (QuestionForGuestsHome) VSys.homeManager
                .getHome("org.hip.vif.core.bom.impl.QuestionForGuestsDerbyHome");
        try {
            if (PreferencesHandler.INSTANCE.isDerbyDB()) {
                return outHome;
            }
            return (QuestionForGuestsHome) VSys.homeManager
                    .getHome(QuestionForGuests.HOME_CLASS_NAME);
        } catch (final IOException exc) {
            return outHome;
        }
    }

    /** @return {@link JoinQuestionForIndexHome} */
    public static JoinQuestionForIndexHome getJoinQuestionForIndexHome() {
        return (JoinQuestionForIndexHome) VSys.homeManager
                .getHome(JoinQuestionForIndex.HOME_CLASS_NAME);
    }

    /** @return {@link JoinCompletionForIndexHome} */
    public static JoinCompletionForIndexHome getJoinCompletionForIndexHome() {
        return (JoinCompletionForIndexHome) VSys.homeManager
                .getHome(JoinCompletionForIndex.HOME_CLASS_NAME);
    }

    /** @return {@link RatingsHome} */
    public static RatingsHome getRatingsHome() {
        return (RatingsHome) VSys.homeManager.getHome(Ratings.HOME_CLASS_NAME);
    }

    /** @return {@link JoinRatingsToRaterHome} */
    public static JoinRatingsToRaterHome getJoinRatingsToRaterHome() {
        return (JoinRatingsToRaterHome) VSys.homeManager
                .getHome(JoinRatingsToRater.HOME_CLASS_NAME);
    }

    /** @return {@link RatingEventsHome} */
    public static RatingEventsHome getRatingEventsHome() {
        return (RatingEventsHome) VSys.homeManager
                .getHome(RatingEvents.HOME_CLASS_NAME);
    }

    /** @return {@link JoinRatingsToQuestionHome} */
    public static JoinRatingsToQuestionHome getJoinRatingsToQuestionHome() {
        return (JoinRatingsToQuestionHome) VSys.homeManager
                .getHome(JoinRatingsToQuestion.HOME_CLASS_NAME);
    }

    /** @return {@link JoinRatingsToCompletionHome} */
    public static JoinRatingsToCompletionHome getJoinRatingsToCompletionHome() {
        return (JoinRatingsToCompletionHome) VSys.homeManager
                .getHome(JoinRatingsToCompletion.HOME_CLASS_NAME);
    }

    /** @return {@link JoinRatingsToTextHome} */
    public static JoinRatingsToTextHome getJoinRatingsToTextHome() {
        return (JoinRatingsToTextHome) VSys.homeManager
                .getHome(JoinRatingsToText.HOME_CLASS_NAME);
    }

    /** @return {@link RatingsQuestionHome} */
    public static RatingsQuestionHome getRatingsQuestionHome() {
        return (RatingsQuestionHome) VSys.homeManager
                .getHome(RatingsQuestion.HOME_CLASS_NAME);
    }

    /** @return {@link RatingsCompletionHome} */
    public static RatingsCompletionHome getRatingsCompletionHome() {
        return (RatingsCompletionHome) VSys.homeManager
                .getHome(RatingsCompletion.HOME_CLASS_NAME);
    }

    /** @return {@link RatingsTextHome} */
    public static RatingsTextHome getRatingsTextHome() {
        return (RatingsTextHome) VSys.homeManager
                .getHome(RatingsText.HOME_CLASS_NAME);
    }

    /** Returns the text home.
     *
     * <p>
     * <b>Note:</b> this will return the home for the text model which is not workflow aware!
     * </p>
     *
     * @return {@link TextHome} */
    public static TextHome getTextHome() {
        return (TextHome) VSys.homeManager.getHome(TextImpl.HOME_CLASS_NAME);
    }

    /** @return {@link DownloadTextHome} */
    public static DownloadTextHome getDownloadTextHome() {
        return (DownloadTextHome) VSys.homeManager
                .getHome(DownloadTextImpl.HOME_CLASS_NAME);
    }

    /** @return {@link TextMaxHome} */
    public static TextMaxHome getTextMaxHome() {
        return (TextMaxHome) VSys.homeManager.getHome(TextMax.HOME_CLASS_NAME);
    }

    /** @return TextHistoryHome */
    public static TextHistoryHome getTextHistoryHome() {
        return (TextHistoryHome) VSys.homeManager
                .getHome(TextHistoryImpl.HOME_CLASS_NAME);
    }

    /** @return {@link TextQuestionHome} */
    public static TextQuestionHome getTextQuestionHome() {
        return (TextQuestionHome) VSys.homeManager
                .getHome(TextQuestion.HOME_CLASS_NAME);
    }

    /** @return {@link JoinAuthorReviewerToTextHome} */
    public static JoinAuthorReviewerToTextHome getJoinAuthorReviewerToTextHome() {
        return (JoinAuthorReviewerToTextHome) VSys.homeManager
                .getHome(JoinAuthorReviewerToText.HOME_CLASS_NAME);
    }

    /** @return {@link JoinTextToMemberHome} */
    public static JoinTextToMemberHome getJoinTextToMemberHome() {
        return (JoinTextToMemberHome) VSys.homeManager
                .getHome(JoinTextToMember.HOME_CLASS_NAME);
    }

    /** @return {@link AppVersionHome} */
    public static AppVersionHome getAppVersionHome() {
        return (AppVersionHome) VSys.homeManager
                .getHome(AppVersion.HOME_CLASS_NAME);
    }

    /** Helper method to retrieve the ID of a newly created DomainObject
     *
     * @param inFindValue java.lang.String The value which identifies externally the newly created DomainObject
     * @param inFindKey java.lang.String The field name of external identification
     * @param inHome org.hip.kernel.bom.DomainObjectHome The home managing the newly created DomainObject
     * @param inIDKey java.lang.String The field name which identifies internally the DomainObject */
    public static Long getNewID(final String inFindValue,
            final String inFindKey, final DomainObjectHome inHome,
            final String inIDKey) throws BOMChangeValueException {
        try {
            final KeyObject lKey = new KeyObjectImpl();
            lKey.setValue(inFindKey, inFindValue);
            final DomainObject lNew = inHome.findByKey(lKey);
            return Long.parseLong(lNew.get(inIDKey).toString());
        } catch (final VException exc) {
            throw new BOMChangeValueException(exc.getMessage(), exc);
        }
    }

    /** Returns a KeyObject to select entries with the specified states, i.e. which creates the SQL string '(FIELD_STATE
     * = inState1 OR FIELD_STATE = inState2)'
     *
     * @param inFieldState String name of the state field
     * @param inStates Integer[] the states the selected entries must have
     * @return KeyObject
     * @throws VInvalidNameException
     * @throws VInvalidValueException */
    public static KeyObject getKeyStates(final String inFieldState,
            final Integer... inStates) throws VInvalidNameException,
            VInvalidValueException {
        final KeyObject outKey = new KeyObjectImpl();
        for (int i = 0; i < inStates.length; i++) {
            outKey.setValue(inFieldState, inStates[i], "=",
                    BinaryBooleanOperator.OR);
        }
        return outKey;
    }

    /** Returns a KeyObject to select entries with published states, i.e. which creates the SQL string '(FIELD_STATE =
     * OPEN OR FIELD_STATE = SETTLED)'
     *
     * @param inFieldState String name of the state field
     * @return KeyObject
     * @throws VInvalidNameException
     * @throws VInvalidValueException */
    public static KeyObject getKeyPublished(final String inFieldState)
            throws VInvalidNameException, VInvalidValueException {
        return getKeyStates(inFieldState,
                WorkflowAwareContribution.STATES_PUBLISHED);
    }

    /** Convenience method: Returns a KeyObject to select entries in visible groups.
     *
     * @param inFieldState String name of the state field
     * @return KeyObject
     * @throws VInvalidNameException
     * @throws VInvalidValueException */
    public static KeyObject getKeyVisibleGroup(final String inFieldState)
            throws VInvalidNameException, VInvalidValueException {
        return getKeyStates(inFieldState, VIFGroupWorkflow.VISIBLE_STATES);
    }

}

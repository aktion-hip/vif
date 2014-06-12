package org.hip.vif.core.bom.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AbstractMemberTest.class, AbstractResponsibleTest.class,
		ActorTest.class, AppVersionHomeTest.class, BookmarkHomeImplTest.class,
		CompletionAuthorReviewerHomeImplTest.class,
		CompletionHomeImplTest.class, CompletionImplTest.class,
		ContributionDeletionHandlerTest.class, DownloadTextHomeImplTest.class,
		DownloadTextImplTest.class, ExtMemberHomeImplTest.class,
		GenericHomeTest.class, GroupAdminHomeImplTest.class,
		GroupHomeImplTest.class, GroupImplTest.class,
		JoinAuthorReviewerToCompletionHomeTest.class,
		JoinAuthorReviewerToQuestionHomeTest.class,
		JoinAuthorReviewerToTextHomeTest.class,
		JoinCompletionForIndexHomeTest.class,
		JoinCompletionToMemberHomeTest.class,
		JoinCompletionToQuestionHomeTest.class,
		JoinGroupAdminToMemberHomeTest.class,
		JoinMemberToPermissionHomeTest.class, JoinMemberToRoleHomeTest.class,
		JoinParticipantToMemberHomeTest.class,
		JoinQuestionForIndexHomeTest.class,
		JoinQuestionToAuthorReviewerHomeTest.class,
		JoinQuestionToChildAndAuthorHomeTest.class,
		JoinQuestionToChildHomeTest.class,
		JoinQuestionToCompletionAndContributorsHomeTest.class,
		JoinQuestionToContributorsHomeTest.class,
		JoinQuestionToContributorsTest.class, JoinQuestionToTextHomeTest.class,
		JoinRatingsToCompletionHomeTest.class,
		JoinRatingsToQuestionHomeTest.class, JoinRatingsToRaterHomeTest.class,
		JoinSubscriptionToMemberHomeTest.class,
		JoinSubscriptionToQuestionHomeTest.class,
		JoinTextToMemberHomeTest.class, LinkMemberRoleHomeImplTest.class,
		LinkPermissionRoleHomeImplTest.class, MemberHomeImplTest.class,
		MemberImplTest.class, NestedGroupHome2Test.class,
		NestedGroupHomeTest.class, NestedParticipantsOfGroupHomeTest.class,
		NotificationTextCollectorTest.class, ParticipantHomeImplTest.class,
		PermissionHomeTest.class, PermissionImplTest.class,
		QuestionAuthorReviewerHomeImplTest.class,
		QuestionBranchIteratorTest.class, QuestionForGuestsHomeTest.class,
		QuestionHierarchyHomeImplTest.class, QuestionHierarchyImplTest.class,
		QuestionHomeImplTest.class, QuestionImplTest.class,
		QuestionStateChangeHelperTest.class, RatingEventsHomeTest.class,
		RatingsCalculateHomeTest.class, RatingsCountEfficiencyHomeTest.class,
		RatingsHomeTest.class, ResponsibleHomeTest.class,
		SubscriptionHomeImplTest.class, TextAuthorReviewerHomeImplTest.class,
		TextHomeImplTest.class, TextImplTest.class, TextMaxHomeTest.class,
		TextQuestionHomeTest.class, VIFWorkflowAwareTest.class,
		WorkflowAwareDomainObjectTest.class })
public class AllTests {

}

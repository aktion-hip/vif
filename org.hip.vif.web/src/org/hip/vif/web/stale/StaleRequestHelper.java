/**
 This package is part of the application VIF.
 Copyright (C) 2010-2014, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.web.stale;

import java.sql.SQLException;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.interfaces.IReviewable;
import org.hip.vif.web.Activator;
import org.hip.vif.web.bom.Text;
import org.hip.vif.web.bom.VifBOMHelper;
import org.ripla.interfaces.IMessages;

/** Helper class to handle new reviewers for stale requests.
 *
 * @author Luthiger
 * @see StaleRequestRemover */
public class StaleRequestHelper {

    /** Interface for classes collecting information about contributions. */
    public static interface Collector extends IReviewable {
        Long getReviewerID();

        String getReviewerFirstname();

        String getReviewerName();

        String getReviewerMail();

        void removeReviewer() throws VException, SQLException;

        String getContributionType();

        String getContributionTitle();

        AuthorGroup getAuthorGroup() throws Exception;

        void accept(StaleTextCollector inNotificator);
    }

    public static abstract class AbstractCollector {
        private final Long reviewerID;
        private final String reviewerFirstname;
        private final String reviewerName;
        private final String reviewerMail;
        private final IMessages messages = Activator.getMessages();

        AbstractCollector(final GeneralDomainObject inModel) throws VException {
            reviewerID = new Long(inModel.get(ResponsibleHome.KEY_MEMBER_ID).toString());
            reviewerFirstname = inModel.get(MemberHome.KEY_FIRSTNAME).toString();
            reviewerName = inModel.get(MemberHome.KEY_NAME).toString();
            reviewerMail = inModel.get(MemberHome.KEY_MAIL).toString();
        }

        public Long getReviewerID() {
            return reviewerID;
        }

        public String getReviewerFirstname() {
            return reviewerFirstname;
        }

        public String getReviewerName() {
            return reviewerName;
        }

        public String getReviewerMail() {
            return reviewerMail;
        }

        public String getContributionType() {
            return messages.getMessage(getMessageKey());
        }

        abstract protected String getMessageKey();
    }

    public static class QuestionCollector extends AbstractCollector implements Collector {
        private static final String KEY_TYPE = "org.hip.vif.msg.question.Question"; //$NON-NLS-1$
        private final Long questionID;
        private final String title;
        private final Long groupID;
        private final String decimal;
        private final String remark;

        protected QuestionCollector(final GeneralDomainObject inModel) throws VException {
            super(inModel);
            questionID = new Long(inModel.get(QuestionHome.KEY_ID).toString());
            title = inModel.get(QuestionHome.KEY_QUESTION).toString();
            groupID = new Long(inModel.get(QuestionHome.KEY_GROUP_ID).toString());
            decimal = inModel.get(QuestionHome.KEY_QUESTION_DECIMAL).toString();
            remark = inModel.get(QuestionHome.KEY_REMARK).toString();
        }

        @Override
        public void removeReviewer() throws VException, SQLException {
            BOMHelper.getQuestionAuthorReviewerHome().removeReviewer(getReviewerID(), questionID);
        }

        @Override
        public String getContributionTitle() {
            return title;
        }

        @Override
        protected String getMessageKey() {
            return KEY_TYPE;
        }

        @Override
        public AuthorGroup getAuthorGroup() throws Exception {
            final Member lMember = BOMHelper.getQuestionAuthorReviewerHome().getAuthor(questionID);
            return new AuthorGroup(lMember, groupID);
        }

        @Override
        public void setReviewer(final Long inReviewerID) throws VException, SQLException {
            BOMHelper.getQuestionAuthorReviewerHome().setReviewer(inReviewerID, questionID);
        }

        @Override
        public boolean checkRefused(final Long inReviewerID) throws VException, SQLException {
            return BOMHelper.getQuestionAuthorReviewerHome().checkRefused(inReviewerID, questionID);
        }

        @Override
        public void accept(final StaleTextCollector inNotificator) {
            inNotificator.visitQuestion(this);
        }

        public String getDecimal() {
            return decimal;
        }

        public String getRemark() {
            return remark;
        }
    }

    public static class CompletionCollector extends AbstractCollector implements Collector {
        private static final String KEY_TYPE = "org.hip.vif.msg.question.Completion"; //$NON-NLS-1$
        private final Long completionID;
        private final String title;
        private final String decimal;

        protected CompletionCollector(final GeneralDomainObject inModel) throws VException {
            super(inModel);
            completionID = new Long(inModel.get(CompletionHome.KEY_ID).toString());
            title = inModel.get(CompletionHome.KEY_COMPLETION).toString();
            decimal = (String) getOwningQuestion(inModel).get(QuestionHome.KEY_QUESTION_DECIMAL);
        }

        private Question getOwningQuestion(final GeneralDomainObject inModel) throws VException {
            final KeyObject lKey = new KeyObjectImpl();
            lKey.setValue(QuestionHome.KEY_ID, inModel.get(CompletionHome.KEY_QUESTION_ID));
            return (Question) BOMHelper.getQuestionHome().findByKey(lKey);
        }

        @Override
        public void removeReviewer() throws VException, SQLException {
            BOMHelper.getCompletionAuthorReviewerHome().removeReviewer(getReviewerID(), completionID);
        }

        @Override
        public String getContributionTitle() {
            return title;
        }

        @Override
        protected String getMessageKey() {
            return KEY_TYPE;
        }

        @Override
        public AuthorGroup getAuthorGroup() throws Exception {
            final Member lMember = BOMHelper.getCompletionAuthorReviewerHome().getAuthor(completionID);
            final Long lGroupID = BOMHelper.getJoinCompletionToQuestionHome().getGroupID(completionID);
            return new AuthorGroup(lMember, lGroupID);
        }

        @Override
        public void setReviewer(final Long inReviewerID) throws VException, SQLException {
            BOMHelper.getCompletionAuthorReviewerHome().setReviewer(inReviewerID, completionID);
        }

        @Override
        public boolean checkRefused(final Long inReviewerID) throws VException, SQLException {
            return BOMHelper.getCompletionAuthorReviewerHome().checkRefused(inReviewerID, completionID);
        }

        @Override
        public void accept(final StaleTextCollector inNotificator) {
            inNotificator.visitCompletion(this);
        }

        public String getDecimalID() {
            return decimal;
        }
    }

    public static class TextCollector extends AbstractCollector implements Collector {
        private static final String KEY_TYPE = "org.hip.vif.msg.question.Bibliography"; //$NON-NLS-1$
        private final Long textID;
        private final int textVersion;
        private final String title;
        private final String contentPlain;
        private final String contentHtml;

        protected TextCollector(final GeneralDomainObject inModel) throws VException, SQLException {
            super(inModel);
            textID = new Long(inModel.get(TextHome.KEY_ID).toString());
            textVersion = Integer.parseInt(inModel.get(TextHome.KEY_VERSION).toString());
            title = inModel.get(TextHome.KEY_REFERENCE).toString();
            final Text lText = getText(textID, textVersion);
            contentPlain = lText.getNotification();
            contentHtml = lText.getNotificationHtml();
        }

        private Text getText(final Long inTextID, final int inVersion) throws VException, SQLException {
            return (Text) VifBOMHelper.getTextHome().getText(inTextID, inVersion);
        }

        @Override
        public void removeReviewer() throws VException, SQLException {
            BOMHelper.getTextAuthorReviewerHome().removeReviewer(getReviewerID(), textID, textVersion);
        }

        @Override
        public String getContributionTitle() {
            return title;
        }

        @Override
        protected String getMessageKey() {
            return KEY_TYPE;
        }

        @Override
        public AuthorGroup getAuthorGroup() throws Exception {
            final Member lMember = BOMHelper.getTextAuthorReviewerHome().getAuthor(textID, textVersion);
            return new AuthorGroup(lMember, null);
        }

        @Override
        public void setReviewer(final Long inReviewerID) throws VException, SQLException {
            BOMHelper.getTextAuthorReviewerHome().setReviewer(inReviewerID, textID, textVersion);
        }

        @Override
        public boolean checkRefused(final Long inReviewerID) throws VException, SQLException {
            return BOMHelper.getTextAuthorReviewerHome().checkRefused(inReviewerID, textID, textVersion);
        }

        @Override
        public void accept(final StaleTextCollector inNotificator) {
            inNotificator.visitText(this);
        }

        public String getContentPlain() {
            return contentPlain;
        }

        public String getContentHtml() {
            return contentHtml;
        }
    }

    /** Parameter object containing both the <code>Member</code> object and the group ID.
     *
     * @author Luthiger Created: 17.10.2010 */
    public static class AuthorGroup {
        private final Member author;
        private final Long groupID;

        AuthorGroup(final Member inAuthor, final Long inGroupID) {
            author = inAuthor;
            groupID = inGroupID;
        }

        public Long getAuthorID() throws VException {
            return new Long(author.get(MemberHome.KEY_ID).toString());
        }

        public Member getAuthor() {
            return author;
        }

        /** @return Long, may be <code>null</code> in case of Text entries */
        public Long getGroupID() {
            return groupID;
        }
    }

}

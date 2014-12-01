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

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.core.interfaces.IReviewable;
import org.hip.vif.web.stale.StaleRequestHelper.AuthorGroup;
import org.hip.vif.web.stale.StaleRequestHelper.Collector;
import org.hip.vif.web.util.RequestForReviewMail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Helper class to remove stale requests for review.
 *
 * @author Luthiger Created: 09.10.2010 */
public class StaleRequestRemover {
    private static final Logger LOG = LoggerFactory.getLogger(StaleRequestRemover.class);

    private final Timestamp staleDate;
    private HelperRemoveReviewers helper1;
    private HelperAssignReviewers helper2;

    /** Constructor
     * 
     * @param inStaleDate {@link Timestamp} entries older than this date are stale. */
    public StaleRequestRemover(final Timestamp inStaleDate) {
        staleDate = inStaleDate;
    }

    /** Process the task to remove stale requests and create new requests.
     * 
     * @throws Exception */
    public void processStaleRequests() throws Exception {
        removeStaleRequests();
        createNewRequests();
    }

    /** Removes the stale request, i.e. collects the stale requests, removes them and notifies the assigned reviewers
     * that their review isn't needed anymore.
     * 
     * @throws VException
     * @throws SQLException */
    protected void removeStaleRequests() throws Exception {
        helper1 = new HelperRemoveReviewers();

        // first collect
        // collect questions
        QueryResult lResult = BOMHelper.getJoinQuestionToContributorsHome().selectStaleWaitingForReview(staleDate);
        while (lResult.hasMoreElements()) {
            helper1.addEntry(new StaleRequestHelper.QuestionCollector(lResult.next()));
        }

        // collect completions
        lResult = BOMHelper.getJoinCompletionToMemberHome().selectStaleWaitingForReview(staleDate);
        while (lResult.hasMoreElements()) {
            helper1.addEntry(new StaleRequestHelper.CompletionCollector(lResult.next()));
        }

        // collect texts
        lResult = BOMHelper.getJoinTextToMemberHome().selectStaleWaitingForReview(staleDate);
        while (lResult.hasMoreElements()) {
            helper1.addEntry(new StaleRequestHelper.TextCollector(lResult.next()));
        }

        // then process
        // process: remove reviewer entries
        for (final Collection<Collector> lCollection : helper1.getValues()) {
            for (final Collector lCollector : lCollection) {
                lCollector.removeReviewer();
            }
        }

        // process: send notification
        for (final Long lReviewerID : helper1.getReviewerIDs()) {
            sendRequestExpirationNotification(lReviewerID);
        }
    }

    protected void sendRequestExpirationNotification(final Long inReviewerID) throws Exception {
        final Member lMember = BOMHelper.getMemberCacheHome().getMember(inReviewerID);
        final RequestExpirationMail lMail = new RequestExpirationMail((VIFMember) lMember,
                helper1.getCollected(inReviewerID));
        lMail.send();
    }

    /** Assigns new reviewers for the contributions and notifies them about the new pending requests.
     * 
     * @throws Exception */
    protected void createNewRequests() throws Exception {
        helper2 = new HelperAssignReviewers();

        // regroup the collected entries: per author and per discussion group
        for (final Collection<Collector> lCollection : helper1.getValues()) {
            for (final Collector lCollector : lCollection) {
                final AuthorGroup lAuthorGroup = lCollector.getAuthorGroup();
                helper2.addAuthorGroup(lAuthorGroup, lCollector);
            }
        }

        // for each author's contributions
        for (final HandlerAuthor lAuthorHandler : helper2.getHandlerPerAuthor()) {
            lAuthorHandler.createNewRequest();
        }
    }

    // --- inner classes ---

    private static class HelperRemoveReviewers {
        private final Map<Long, Collection<Collector>> entries = new HashMap<Long, Collection<Collector>>();

        void addEntry(final Collector inCollector) {
            Collection<Collector> lCollected = entries.get(inCollector.getReviewerID());
            if (lCollected == null) {
                lCollected = new Vector<Collector>();
                entries.put(inCollector.getReviewerID(), lCollected);
            }
            lCollected.add(inCollector);
        }

        Collection<Collector> getCollected(final Long inReviewerID) {
            return entries.get(inReviewerID);
        }

        Collection<Collection<Collector>> getValues() {
            return entries.values();
        }

        Set<Long> getReviewerIDs() {
            return entries.keySet();
        }
    }

    private static class HelperAssignReviewers {
        private final Map<Long, HandlerAuthor> authorHandlers = new HashMap<Long, StaleRequestRemover.HandlerAuthor>();

        public void addAuthorGroup(final AuthorGroup inAuthorGroup, final Collector inCollector) throws VException {
            final Long lAuthorID = inAuthorGroup.getAuthorID();
            HandlerAuthor lHandler = authorHandlers.get(lAuthorID);
            if (lHandler == null) {
                lHandler = new HandlerAuthor();
                authorHandlers.put(lAuthorID, lHandler);
            }
            lHandler.add(inAuthorGroup, inCollector);
        }

        public Collection<HandlerAuthor> getHandlerPerAuthor() {
            return authorHandlers.values();
        }
    }

    /** A class to handle all contributions a distinct author created in different groups. */
    private static class HandlerAuthor {
        private final Map<Long, Collection<Collector>> groups = new HashMap<Long, Collection<Collector>>();
        private Long authorID;

        /** Adds the contributions of an author in a group. */
        public void add(final AuthorGroup inAuthorGroup, final Collector inCollector) throws VException {
            authorID = inAuthorGroup.getAuthorID();

            // If we still could not specify a suitable group to draw a participant to review the TEXT entry,
            // we drop this TEXT entry from automatic processing for that this situation can be sorted out manually.
            final Long lGroupID = getCheckedGroupID(inAuthorGroup.getGroupID());
            if (lGroupID == null) {
                prepareMailToAuthor(inAuthorGroup.getAuthor(), inCollector);
                return;
            }

            Collection<Collector> lGroup = groups.get(lGroupID);
            if (lGroup == null) {
                lGroup = new Vector<Collector>();
                groups.put(lGroupID, lGroup);
            }
            lGroup.add(inCollector);
        }

        private Long getCheckedGroupID(final Long inGroupID) {
            if (inGroupID != null)
                return inGroupID;
            final Set<Long> lIDs = groups.keySet();
            if (!lIDs.isEmpty()) {
                return lIDs.iterator().next();
            }
            return null;
        }

        void createNewRequest() throws VException, SQLException, IOException {
            final StaleTextCollector lNotificator = new StaleTextCollector();
            // for each group
            for (final Long lGroupID : groups.keySet()) {
                try {
                    processGroup(lNotificator, lGroupID);
                } catch (final Exception exc) {
                    LOG.error("Error encountered while sending new requests for review!", exc); //$NON-NLS-1$
                }
            }
        }

        private void processGroup(final StaleTextCollector inNotificator, final Long inGroupID) throws Exception {
            // assign new reviewer in group
            final Collection<IReviewable> lContributions = convertToReviewable(groups.get(inGroupID));
            final VIFMember lReviewer = BOMHelper.getJoinParticipantToMemberHome().getRandomParticipant(inGroupID,
                    authorID, lContributions);
            // create entry in author-reviewer
            for (final IReviewable lContribution : lContributions) {
                lContribution.setReviewer(lReviewer.getMemberID());
            }
            // send mail
            inNotificator.reset();
            for (final Collector lContribution : groups.get(inGroupID)) {
                lContribution.accept(inNotificator);
            }
            final RequestForReviewMail lMail = new RequestForReviewMail(lReviewer, (VIFMember) BOMHelper
                    .getMemberCacheHome().getMember(authorID),
                    inNotificator.getNotificationText(), inNotificator.getNotificationTextHtml());
            lMail.send();
        }

        private Collection<IReviewable> convertToReviewable(final Collection<Collector> inContributions) {
            final Collection<IReviewable> outContributions = new Vector<IReviewable>(inContributions.size());
            for (final Collector lContribution : inContributions) {
                outContributions.add(lContribution);
            }
            return outContributions;
        }

        private void prepareMailToAuthor(final Member inAuthor, final Collector inCollector) {
            final StaleTextCollector lNotificator = new StaleTextCollector();
            inCollector.accept(lNotificator);
            try {
                final TextAuthorNotification lMail = new TextAuthorNotification((VIFMember) inAuthor, lNotificator);
                lMail.send();
            } catch (final VException exc) {
                LOG.error(
                        "Could not send the notification to the author that he should trigger the request for review again!", exc); //$NON-NLS-1$
            } catch (final IOException exc) {
                LOG.error(
                        "Could not send the notification to the author that he should trigger the request for review again!", exc); //$NON-NLS-1$
            }
        }

    }

}

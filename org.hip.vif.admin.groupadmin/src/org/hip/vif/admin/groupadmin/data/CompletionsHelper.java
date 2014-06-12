/**
	This package is part of the application VIF.
	Copyright (C) 2011-2014, Benno Luthiger

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
package org.hip.vif.admin.groupadmin.data;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupadmin.util.AuthorReviewerRenderHelper;
import org.hip.vif.admin.groupadmin.util.AuthorReviewerRenderHelper.MemberWrapper;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.util.BeanWrapperHelper;

/**
 * Helper class to handle a question's completions<br />
 * Reason: the query to retrieve the question's completions may retrieve each
 * completion twice, first with the author data and second with the reviewer
 * data.<br/>
 * This helper class has to normalize such multiple entries.
 * 
 * @author Luthiger Created: 27.10.2011
 */
public class CompletionsHelper {

	/**
	 * Normalize (i.e. remove duplicates) the list of completions.
	 * 
	 * @param inCompletions
	 *            {@link QueryResult} the set of completions linked to authors
	 *            and reviewers
	 * @return List of {@link Completion} the normalized list of completions
	 * @throws VException
	 * @throws SQLException
	 */
	public static List<Completion> getNormalizedCompletions(
			final QueryResult inCompletions) throws VException, SQLException {
		final Map<Long, Completion> lCompletions = new HashMap<Long, CompletionsHelper.Completion>();
		final List<Long> lCompletionIDs = new Vector<Long>();

		while (inCompletions.hasMoreElements()) {
			final GeneralDomainObject lModel = inCompletions
					.nextAsDomainObject();
			final Long lCompletionID = BeanWrapperHelper.getLong(
					CompletionHome.KEY_ID, lModel);
			final Completion lCompletion = lCompletions.get(lCompletionID);
			if (lCompletion == null) {
				lCompletions.put(lCompletionID, new Completion(lModel));
				lCompletionIDs.add(lCompletionID);
			} else {
				lCompletion.addContributor(lModel);
			}
		}

		final List<Completion> out = new Vector<CompletionsHelper.Completion>();
		for (final Long lCompletionID : lCompletionIDs) {
			out.add(lCompletions.get(lCompletionID));
		}
		return out;
	}

	// --- inner classes ---

	public static class Completion {
		private final Long completionID;
		private final String completionText;
		private final String completionState;
		private final String completionDate;
		private final Collection<MemberWrapper> authors = new Vector<AuthorReviewerRenderHelper.MemberWrapper>();
		private final Collection<MemberWrapper> reviewers = new Vector<AuthorReviewerRenderHelper.MemberWrapper>();

		private Completion(final GeneralDomainObject inModel) {
			completionID = BeanWrapperHelper.getLong(CompletionHome.KEY_ID,
					inModel);
			completionText = BeanWrapperHelper.getString(
					CompletionHome.KEY_COMPLETION, inModel);
			completionState = BeanWrapperHelper.getString(
					CompletionHome.KEY_STATE, inModel);
			completionDate = BeanWrapperHelper.getFormattedDate(
					CompletionHome.KEY_MUTATION, inModel);
			addContributor(inModel);
		}

		private void addContributor(final GeneralDomainObject inModel) {
			final MemberWrapper lContributor = AuthorReviewerRenderHelper
					.wrapMember(inModel);
			final Integer lType = BeanWrapperHelper.getInteger(
					ResponsibleHome.KEY_TYPE, inModel);
			if (ResponsibleHome.Type.AUTHOR.check(lType)) {
				authors.add(lContributor);
			} else if (ResponsibleHome.Type.REVIEWER.check(lType)) {
				reviewers.add(lContributor);
			}
		}

		public Long getCompletionID() {
			return completionID;
		}

		/**
		 * @return String the completion's text
		 */
		public String getCompletionText() {
			return completionText;
		}

		/**
		 * @return String the completion's state
		 */
		public String getState() {
			return completionState;
		}

		public String getFormattedDate() {
			return completionDate;
		}

		/**
		 * @return Collection of {@link MemberWrapper} this completion's authors
		 */
		public Collection<MemberWrapper> getAuthors() {
			return authors;
		}

		/**
		 * @return Collection of {@link MemberWrapper} this completion's
		 *         reviewers
		 */
		public Collection<MemberWrapper> getReviewers() {
			return reviewers;
		}
	}

}

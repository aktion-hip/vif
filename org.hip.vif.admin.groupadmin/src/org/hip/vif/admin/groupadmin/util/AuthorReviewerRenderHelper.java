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

package org.hip.vif.admin.groupadmin.util;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.impl.JoinCompletionToMember;
import org.hip.vif.core.bom.impl.JoinCompletionToMemberHome;
import org.hip.vif.web.interfaces.IPluggableWithLookup;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.LinkButtonHelper;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * Helper class to render the author/reviewer on a contribution's view.
 * 
 * @author Luthiger Created: 13.06.2011
 */
public class AuthorReviewerRenderHelper {
	private static final String NAME_TMPL = "%s %s"; //$NON-NLS-1$
	private static final String STYLE_CLASS = "vif-contributor"; //$NON-NLS-1$
	private static final String TMPL_TITLE = "<div class=\"%s\">%s</div>"; //$NON-NLS-1$
	private static final String TMPL_SPACED = "<span class=\"spaced\">%s</span>"; //$NON-NLS-1$
	private static final String TMPL_COLON = "<span class=\"spaced\">%s:</span>"; //$NON-NLS-1$

	/**
	 * Convenience method to create a <code>AuthorReviewerRenderer</code>
	 * instance.
	 * 
	 * @param inAuthorReviewer
	 *            {@link GeneralDomainObject}
	 * @param inTask
	 *            {@link IPluggableWithLookup}
	 * @return {@link AuthorReviewerRenderer}
	 * @throws VException
	 * @throws SQLException
	 */
	public static AuthorReviewerRenderer createRenderer(
			final GeneralDomainObject inAuthorReviewer,
			final IPluggableWithLookup inTask) throws VException, SQLException {
		return new ClickabelAuthorReviewerRenderer(inAuthorReviewer, inTask);
	}

	/**
	 * Convenience method to create a <code>AuthorReviewerRenderer</code>
	 * instance.
	 * 
	 * @param inAuthors
	 *            {@link QueryResult}
	 * @param inReviewers
	 *            {@link QueryResult}
	 * @param inPluggable
	 *            {@link IPluggableWithLookup}
	 * @return {@link AuthorReviewerRenderer}
	 * @throws VException
	 * @throws SQLException
	 */
	public static AuthorReviewerRenderer createRenderer(
			final QueryResult inAuthors, final QueryResult inReviewers,
			final IPluggableWithLookup inPluggable) throws VException,
			SQLException {
		return new ClickabelAuthorReviewerRenderer(inAuthors, inReviewers,
				inPluggable);
	}

	/**
	 * Convenience method to create a <code>AuthorReviewerRenderer</code>
	 * instance.
	 * 
	 * @param inAuthor
	 *            {@link Member}
	 * @param inReviewer
	 *            {@link Member}
	 * @param inTask
	 *            {@link IPluggableWithLookup}
	 * @return {@link AuthorReviewerRenderer}
	 * @throws VException
	 * @throws SQLException
	 */
	public static AuthorReviewerRenderer createRenderer(final Member inAuthor,
			final Member inReviewer, final IPluggableWithLookup inTask)
			throws VException, SQLException {
		if (inReviewer == null) {
			return createRenderer(inAuthor, inTask);
		}
		return new ClickabelAuthorReviewerRenderer(inAuthor, inReviewer, inTask);
	}

	/**
	 * Convenience method to create a <code>AuthorReviewerRenderer</code>
	 * instance.
	 * 
	 * @param inAuthors
	 *            Collection<MemberWrapper>
	 * @param inReviewers
	 *            Collection<MemberWrapper>
	 * @param inTask
	 *            {@link IPluggableWithLookup}
	 * @return {@link AuthorReviewerRenderer}
	 */
	public static AuthorReviewerRenderer createRenderer(
			final Collection<MemberWrapper> inAuthors,
			final Collection<MemberWrapper> inReviewers,
			final IPluggableWithLookup inTask) {
		return new ClickabelAuthorReviewerRenderer(inAuthors, inReviewers,
				inTask);
	}

	/**
	 * Factory method: Creates a <code>MemberWrapper</code> adapter instance.
	 * 
	 * @param inMember
	 *            GeneralDomainObject
	 * @return {@link MemberWrapper}
	 */
	public static MemberWrapper wrapMember(final GeneralDomainObject inMember) {
		return new MemberWrapper(
				String.format(NAME_TMPL, BeanWrapperHelper.getString(
						MemberHome.KEY_FIRSTNAME, inMember), BeanWrapperHelper
						.getString(MemberHome.KEY_NAME, inMember)),
				getMemberID(inMember));
	}

	private static Long getMemberID(final GeneralDomainObject inMember) {
		final String lIDKey = (inMember instanceof JoinCompletionToMember) ? JoinCompletionToMemberHome.KEY_ALIAS_MEMBER_ID
				: MemberHome.KEY_ID;
		return BeanWrapperHelper.getLong(lIDKey, inMember);
	}

	// --- inner classes ---

	public static class AuthorReviewerRenderer {
		protected Collection<MemberWrapper> authors;
		protected Collection<MemberWrapper> reviewers;

		AuthorReviewerRenderer(final QueryResult inAuthors,
				final QueryResult inReviewers) throws VException, SQLException {
			authors = fillNames(inAuthors);
			reviewers = fillNames(inReviewers);
		}

		AuthorReviewerRenderer(final GeneralDomainObject inAuthor)
				throws VException, SQLException {
			authors = new Vector<MemberWrapper>();
			authors.add(wrapMember(inAuthor));
			reviewers = new Vector<MemberWrapper>();
		}

		AuthorReviewerRenderer(final GeneralDomainObject inAuthor,
				final GeneralDomainObject inReviewer) throws VException,
				SQLException {
			authors = new Vector<MemberWrapper>();
			authors.add(wrapMember(inAuthor));
			reviewers = new Vector<MemberWrapper>();
			reviewers.add(wrapMember(inReviewer));
		}

		AuthorReviewerRenderer(final Collection<MemberWrapper> inAuthors,
				final Collection<MemberWrapper> inReviewers) {
			authors = inAuthors;
			reviewers = inReviewers;
		}

		private Collection<MemberWrapper> fillNames(final QueryResult inPersons)
				throws VException, SQLException {
			final Collection<MemberWrapper> outNames = new Vector<MemberWrapper>();
			while (inPersons.hasMoreElements()) {
				final GeneralDomainObject lPerson = inPersons
						.nextAsDomainObject();
				outNames.add(wrapMember(lPerson));
			}
			return outNames;
		}

		/**
		 * Renders the author/reviewer information.
		 * 
		 * @param inAuthorLbl
		 *            String
		 * @param inReviewerLbl
		 *            String
		 * @param inDate
		 *            String the formatted publication date
		 * @return {@link Component} the possibly clickable component displaying
		 *         the author/reviewer information
		 */
		public Component render(final String inAuthorLbl,
				final String inReviewerLbl, final String inDate) {
			final String lAuthorRevierDate = String
					.format("%s [%s]", renderAuthorReviewer(inAuthorLbl, inReviewerLbl), inDate); //$NON-NLS-1$
			return new Label(String.format(TMPL_TITLE, STYLE_CLASS,
					lAuthorRevierDate), ContentMode.HTML); //$NON-NLS-1$
		}

		/**
		 * Renders the author/reviewer information.
		 * 
		 * @param inAuthorLbl
		 *            String
		 * @param inReviewerLbl
		 *            String
		 * @return {@link Component} the possibly clickable component displaying
		 *         the author/reviewer information
		 */
		public Component render(final String inAuthorLbl,
				final String inReviewerLbl) {
			return new Label(String.format(TMPL_TITLE, STYLE_CLASS,
					renderAuthorReviewer(inAuthorLbl, inReviewerLbl)),
					ContentMode.HTML); //$NON-NLS-1$
		}

		private StringBuilder renderAuthorReviewer(final String inAuthorLbl,
				final String inReviewerLbl) {
			final StringBuilder out = new StringBuilder();
			if (authors.size() != 0) {
				out.append(inAuthorLbl).append(": "); //$NON-NLS-1$
				out.append(concat(authors));
				out.append(reviewers.size() == 0 ? "" : ", "); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (reviewers.size() != 0) {
				out.append(inReviewerLbl).append(": "); //$NON-NLS-1$
				out.append(concat(reviewers));
			}
			return out;
		}

		private StringBuilder concat(final Collection<MemberWrapper> inSet) {
			final StringBuilder out = new StringBuilder();
			boolean lFirst = true;
			for (final MemberWrapper lEntry : inSet) {
				if (!lFirst)
					out.append(", "); //$NON-NLS-1$
				lFirst = false;
				out.append(lEntry.toString());
			}
			return out;
		}

	}

	private static class ClickabelAuthorReviewerRenderer extends
			AuthorReviewerRenderer {
		private final Label SEP_COMMA = RiplaViewHelper
				.makeUndefinedWidth(new Label(
						String.format(TMPL_SPACED, ","), ContentMode.HTML)); //$NON-NLS-1$
		private final IPluggableWithLookup task;

		public ClickabelAuthorReviewerRenderer(final QueryResult inAuthors,
				final QueryResult inReviewers, final IPluggableWithLookup inTask)
				throws VException, SQLException {
			super(inAuthors, inReviewers);
			task = inTask;
		}

		ClickabelAuthorReviewerRenderer(final GeneralDomainObject inAuthor,
				final IPluggableWithLookup inTask) throws VException,
				SQLException {
			super(inAuthor);
			task = inTask;
		}

		ClickabelAuthorReviewerRenderer(final GeneralDomainObject inAuthor,
				final GeneralDomainObject inMember,
				final IPluggableWithLookup inTask) throws VException,
				SQLException {
			super(inAuthor, inMember);
			task = inTask;
		}

		ClickabelAuthorReviewerRenderer(
				final Collection<MemberWrapper> inAuthors,
				final Collection<MemberWrapper> inReviewers,
				final IPluggableWithLookup inTask) {
			super(inAuthors, inReviewers);
			task = inTask;
		}

		@Override
		public Component render(final String inAuthorLbl,
				final String inReviewerLbl, final String inDate) {
			final HorizontalLayout out = (HorizontalLayout) this.render(
					inAuthorLbl, inReviewerLbl);
			out.addComponent(RiplaViewHelper.makeUndefinedWidth(new Label(
					String.format(
							"<span class=\"vif-contribution-date\">[%s]<span>", inDate), ContentMode.HTML))); //$NON-NLS-1$
			return out;
		}

		@Override
		public Component render(final String inAuthorLbl,
				final String inReviewerLbl) {
			final HorizontalLayout out = new HorizontalLayout();
			out.addStyleName(STYLE_CLASS);

			if (authors.size() != 0) {
				out.addComponent(RiplaViewHelper.makeUndefinedWidth(new Label(
						String.format(TMPL_COLON, inAuthorLbl),
						ContentMode.HTML))); //$NON-NLS-1$
				addConcatenated(out, authors);
				if (reviewers.size() != 0) {
					out.addComponent(SEP_COMMA);
				}
			}
			if (reviewers.size() != 0) {
				out.addComponent(RiplaViewHelper.makeUndefinedWidth(new Label(
						String.format(TMPL_COLON, inReviewerLbl),
						ContentMode.HTML))); //$NON-NLS-1$
				addConcatenated(out, reviewers);
			}
			return out;
		}

		private void addConcatenated(final HorizontalLayout inLayout,
				final Collection<MemberWrapper> inSet) {
			boolean lFirst = true;
			for (final MemberWrapper lEntry : inSet) {
				if (!lFirst) {
					inLayout.addComponent(SEP_COMMA);
				}
				lFirst = false;
				inLayout.addComponent(createLink(lEntry));
			}
		}

		private Button createLink(final MemberWrapper inMember) {
			return LinkButtonHelper
					.createLinkButton(inMember.toString(),
							LinkButtonHelper.LookupType.MEMBER,
							inMember.memberID, task);
		}
	}

	public static class MemberWrapper {
		String name;
		Long memberID;

		private MemberWrapper(final String inName, final Long inID) {
			name = inName;
			memberID = inID;
		}

		@Override
		public String toString() {
			return name;
		}
	}

}

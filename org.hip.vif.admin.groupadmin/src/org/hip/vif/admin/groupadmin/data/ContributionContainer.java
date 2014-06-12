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

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupadmin.data.ContributionWrapper.EntryType;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.web.biblio.BibliographyAdapter;
import org.hip.vif.web.util.BibliographyFormatter;
import org.hip.vif.web.util.RichTextSanitizer;

import com.vaadin.data.util.BeanItemContainer;

/**
 * Data container for contributions, i.e. questions, completions and texts.
 * 
 * @author Luthiger Created: 03.07.2011
 */
@SuppressWarnings("serial")
public class ContributionContainer extends
		BeanItemContainer<ContributionWrapper> {
	public static final String CONTRIBUTION_CHECK = "chk"; //$NON-NLS-1$
	public static final String CONTRIBUTION_CHECKED = "checked"; //$NON-NLS-1$
	public static final String CONTRIBUTION_TEXT = "contributionText"; //$NON-NLS-1$
	public static final Object[] NATURAL_COL_ORDER = new Object[] {
			CONTRIBUTION_CHECK,
			"publicID", CONTRIBUTION_TEXT, "contributionState" }; //$NON-NLS-1$ //$NON-NLS-2$
	public static final String[] COL_HEADERS = new String[] {
			"", "container.table.headers.nr", "container.table.headers.question", "container.table.headers.state" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	public static final Object[] NATURAL_COL_ORDER_WO_STATE = new String[] {
			CONTRIBUTION_CHECK, "publicID", CONTRIBUTION_TEXT }; //$NON-NLS-1$

	private ContributionContainer() {
		super(ContributionWrapper.class);
	}

	/**
	 * @return boolean <code>true</code> if the container contains items
	 */
	public boolean hasItems() {
		return getAllItemIds().size() > 0;
	}

	/**
	 * Factory method: returns the data container for the administrator's review
	 * view.
	 * 
	 * @param inQuestions
	 *            {@link QueryResult}
	 * @param inCompletions
	 *            {@link QueryResult}
	 * @param inTexts
	 *            {@link QueryResult}
	 * @param inCodeList
	 *            {@link CodeList}
	 * @return {@link ContributionContainer}
	 * @throws VException
	 * @throws SQLException
	 */
	public static ContributionContainer createData(
			final QueryResult inQuestions, final QueryResult inCompletions,
			final QueryResult inTexts, final CodeList inCodeList)
			throws VException, SQLException {
		final ContributionContainer out = new ContributionContainer();
		// questions
		while (inQuestions.hasMoreElements()) {
			final GeneralDomainObject lEntry = inQuestions.next();
			final String lDecimalID = BeanWrapperHelper.getString(
					QuestionHome.KEY_QUESTION_DECIMAL, lEntry);
			out.addItem(ContributionWrapper.createItem(
					lEntry,
					EntryType.QUESTION,
					String.format("0_%s", lDecimalID), //$NON-NLS-1$
					BeanWrapperHelper.getString(QuestionHome.KEY_ID, lEntry),
					lDecimalID, RichTextSanitizer.removePara(BeanWrapperHelper
							.getString(QuestionHome.KEY_QUESTION, lEntry)),
					inCodeList));
		}
		// completions
		while (inCompletions.hasMoreElements()) {
			final GeneralDomainObject lEntry = inCompletions.next();
			final String lDecimalID = BeanWrapperHelper.getString(
					QuestionHome.KEY_QUESTION_DECIMAL, lEntry);
			out.addItem(ContributionWrapper.createItem(lEntry,
					EntryType.COMPLETION,
					String.format("0_%s_1", lDecimalID), //$NON-NLS-1$
					BeanWrapperHelper.getString(CompletionHome.KEY_ID, lEntry),
					String.format("(%s)", lDecimalID), //$NON-NLS-1$
					RichTextSanitizer.removePara(BeanWrapperHelper.getString(
							CompletionHome.KEY_COMPLETION, lEntry)), inCodeList));
		}
		// texts
		while (inTexts.hasMoreElements()) {
			final GeneralDomainObject lEntry = inTexts.next();
			final String lID = String.format(Text.FORMAT_ID_VERSION,
					BeanWrapperHelper.getString(TextHome.KEY_ID, lEntry),
					BeanWrapperHelper.getString(TextHome.KEY_VERSION, lEntry));
			final String lReference = BeanWrapperHelper.getString(
					TextHome.KEY_REFERENCE, lEntry);
			out.addItem(ContributionWrapper.createItem(lEntry, EntryType.TEXT,
					String.format("9_%s", lReference), lID, lReference, //$NON-NLS-1$
					getBibliography(lEntry), inCodeList));
		}

		out.sort(new String[] { "sortValue" }, new boolean[] { true }); //$NON-NLS-1$
		return out;
	}

	/**
	 * Factory method: returns the data container for the administrator's view
	 * of pending tasks (i.e. of questions requested to set answered or to be
	 * reopened).
	 * 
	 * @param inQuestions
	 * @param inCodeList
	 * @return
	 * @throws VException
	 * @throws SQLException
	 */
	public static ContributionContainer createQuestions(
			final QueryResult inQuestions, final CodeList inCodeList)
			throws VException, SQLException {
		final ContributionContainer out = new ContributionContainer();
		while (inQuestions.hasMoreElements()) {
			final GeneralDomainObject lEntry = inQuestions.next();
			final String lDecimalID = BeanWrapperHelper.getString(
					QuestionHome.KEY_QUESTION_DECIMAL, lEntry);
			out.addItem(ContributionWrapper.createItem(
					lEntry,
					EntryType.QUESTION,
					String.format("0_%s", lDecimalID), //$NON-NLS-1$
					BeanWrapperHelper.getString(QuestionHome.KEY_ID, lEntry),
					lDecimalID, RichTextSanitizer.removePara(BeanWrapperHelper
							.getString(QuestionHome.KEY_QUESTION, lEntry)),
					inCodeList));
		}
		return out;
	}

	private static String getBibliography(
			final GeneralDomainObject inBibliography) throws VException {
		final BibliographyFormatter lFormatter = new BibliographyFormatter(
				new BibliographyAdapter(inBibliography,
						TextHome.KEY_BIBLIO_TYPE));
		return lFormatter.renderHtml();
	}

	/**
	 * Factory method: returns the data container for the view to add
	 * bibliography entries.
	 * 
	 * @param inTexts
	 *            {@link QueryResult}
	 * @param inCodeList
	 *            {@link CodeList}
	 * @return {@link ContributionContainer}
	 * @throws VException
	 * @throws SQLException
	 */
	public static ContributionContainer createTexts(final QueryResult inTexts,
			final CodeList inCodeList) throws VException, SQLException {
		final ContributionContainer out = new ContributionContainer();
		while (inTexts.hasMoreElements()) {
			final GeneralDomainObject lEntry = inTexts.next();
			final String lID = String.format(Text.FORMAT_ID_VERSION,
					BeanWrapperHelper.getString(TextHome.KEY_ID, lEntry),
					BeanWrapperHelper.getString(TextHome.KEY_VERSION, lEntry));
			;
			final String lReference = BeanWrapperHelper.getString(
					TextHome.KEY_REFERENCE, lEntry);
			out.addItem(ContributionWrapper.createItem(lEntry, EntryType.TEXT,
					String.format("9_%s", lReference), lID, lReference, //$NON-NLS-1$
					getText(lEntry), inCodeList));
		}

		out.sort(new String[] { "sortValue" }, new boolean[] { true }); //$NON-NLS-1$
		return out;
	}

	private static String getText(final GeneralDomainObject inBibliography)
			throws VException {
		final BibliographyFormatter lFormatter = new BibliographyFormatter(
				new BibliographyAdapter(inBibliography, TextHome.KEY_TYPE));
		return lFormatter.renderHtml();
	}

	/**
	 * Creates an empty <code>ContributionContainer</code>.
	 * 
	 * @return {@link ContributionContainer}
	 */
	public static ContributionContainer createEmpty() {
		return new ContributionContainer();
	}

}

/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Question;

import com.vaadin.data.util.BeanItemContainer;

/**
 * Container for data displayed on table of questions.
 * 
 * @author Luthiger Created: 06.06.2011
 */
@SuppressWarnings("serial")
public class QuestionContainer extends BeanItemContainer<QuestionWrapper> {
	public static final String QUESTION = "question"; //$NON-NLS-1$
	public static final String QUESTION_DECIMAL_ID = "questionDecimal"; //$NON-NLS-1$
	public static final Object[] NATURAL_COL_ORDER = new String[] {
			QUESTION_DECIMAL_ID, QUESTION, "state" }; //$NON-NLS-1$
	public static final String[] COL_HEADERS = new String[] {
			"container.table.headers.id", "container.table.headers.question", "container.table.headers.state" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private boolean isEmtpy = true;

	private QuestionContainer() {
		super(QuestionWrapper.class);
	}

	/**
	 * Factory method, processing a result set.
	 * 
	 * @param inQuestions
	 *            {@link QueryResult}
	 * @param inCodeList
	 *            {@link CodeList}
	 * @return {@link QuestionContainer}
	 * @throws VException
	 * @throws SQLException
	 */
	public static QuestionContainer createData(final QueryResult inQuestions,
			final CodeList inCodeList) throws VException, SQLException {
		final QuestionContainer outQuestions = new QuestionContainer();
		while (inQuestions.hasMoreElements()) {
			outQuestions.addItem(QuestionWrapper.createItem(inQuestions.next(),
					inCodeList));
			outQuestions.setEmpty(false);
		}
		inQuestions.close();
		return outQuestions;
	}

	/**
	 * Factory method, processing a single question.
	 * 
	 * @param inQuestion
	 *            {@link Question}
	 * @param inCodeList
	 *            {@link CodeList}
	 * @return {@link QuestionContainer}
	 */
	public static QuestionContainer createData(final Question inQuestion,
			final CodeList inCodeList) {
		final QuestionContainer outQuestions = new QuestionContainer();
		outQuestions
				.addItem(QuestionWrapper.createItem(inQuestion, inCodeList));
		outQuestions.setEmpty(false);
		return outQuestions;
	}

	/**
	 * Factory method, creates an empty container for questions.
	 * 
	 * @return {@link QuestionContainer}
	 */
	public static QuestionContainer createEmpty() {
		return new QuestionContainer();
	}

	private void setEmpty(final boolean inEmpty) {
		isEmtpy = inEmpty;
	}

	/**
	 * @return boolean <code>true</code> it the container doesn't contain any
	 *         elements.
	 */
	public boolean isEmpty() {
		return isEmtpy;
	}

}

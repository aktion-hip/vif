package org.hip.vif.core.bom.impl;

/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2001, Benno Luthiger

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

import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.IQuestion;
import org.hip.vif.core.bom.QuestionHome;

/**
 * This class implements the join between question-author/reviewer and questions.
 * 
 * @author: Benno Luthiger
 */
public class JoinAuthorReviewerToQuestion extends DomainObjectImpl implements IQuestion {
	public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinAuthorReviewerToQuestionHome";

	/**
	 * Constructor for JoinQuestionToAuthorReviewer.
	 */
	public JoinAuthorReviewerToQuestion() {
		super();
	}

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObject#getHomeClassName()
	 */
	public String getHomeClassName() {
		return HOME_CLASS_NAME;
	}

	public boolean isRoot() throws VException {
		return QuestionHome.IS_ROOT.equals(new Long(get(QuestionHome.KEY_ROOT_QUESTION).toString()));
	}
}

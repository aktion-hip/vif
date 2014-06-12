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

package org.hip.vif.forum.groups.data;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.web.util.RichTextSanitizer;

/**
 * @author Luthiger
 * Created: 06.06.2011
 */
public class QuestionWrapper {
	private Long questionID;
	private String questionDecimal;
	private String question;
	
	/**
	 * Private constructor.
	 * 
	 * @param inDomainObject
	 */
	private QuestionWrapper(GeneralDomainObject inDomainObject) {
		questionID = BeanWrapperHelper.getLong(QuestionHome.KEY_ID, inDomainObject);
		questionDecimal = BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION_DECIMAL, inDomainObject);
		question = RichTextSanitizer.removePara(BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION, inDomainObject));
	}
	
	/**
	 * Factory method, creation the instance.
	 * 
	 * @param inDomainObject
	 * @return {@link QuestionWrapper}
	 */
	public static QuestionWrapper createItem(GeneralDomainObject inDomainObject) {
		return new QuestionWrapper(inDomainObject);
	}

	public Long getQuestionID() {
		return questionID;
	}

	public String getQuestionDecimal() {
		return questionDecimal;
	}

	public String getQuestion() {
		return question;
	}

}

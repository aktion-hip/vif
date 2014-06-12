/*
	This package is part of the application VIF.
	Copyright (C) 2003, Benno Luthiger

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
package org.hip.vif.core.code;

import org.hip.kernel.code.AbstractCode;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.code.CodeListNotFoundException;

/**
 * Represents code for the state of a question.
 * The possible states are:<br/>
 * <ul>
 * <li><code>private</code>: A question that is created but not yet published.</li>
 * <li><code>waiting for review</code>: A question the author has requested a review.</li>
 * <li><code>under revision</code>: A question a reviewer is looking at.</li>
 * <li><code>open</code>: A published question not yet answered.</li>
 * <li><code>answered_requested</code>: A state change to answered requested.</li>
 * <li><code>answered</code>: An answered question.</li>
 * <li><code>deleted</code>: A question deleted by the group administrator, i.e. not more publicly viewable.</li>
 * </ul>
 * 
 * @author: Benno Luthiger
 */
public class QuestionState extends AbstractCode {
	//constants
	public static final String CODEID = "QUESTIONSTATES";

	/**
	 * Constructor for QuestionState.
	 * @param inCodeID
	 */
	public QuestionState() {
		this("");
	}
	
	/**
	 * Constructor for QuestionState.
	 * @param inElementID java.lang.String
	 */
	public QuestionState(String inElementID) {
		super(CODEID);
		setElementID(inElementID);
	}
	
	/**
	 * Convenience method, returns the question states as html select.
	 * 
	 * @param inElementID String the selected element's id
	 * @param inLanguage String
	 * @return String html select
	 * @throws CodeListNotFoundException
	 */
	public static String createQuestionStates(String inElementID, String inLanguage) throws CodeListNotFoundException {
		AbstractCode[] lStates = new AbstractCode[] {new QuestionState(inElementID)};
		CodeList outList = CodeListHome.instance().getCodeList(QuestionState.class, inLanguage);
		return outList.toSelectionString(lStates);
	}
	
}

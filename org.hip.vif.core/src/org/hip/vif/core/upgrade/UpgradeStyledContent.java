/*
	This package is part of the application VIF.
	Copyright (C) 2012, Benno Luthiger

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

package org.hip.vif.core.upgrade;

import java.util.regex.Pattern;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.exc.UpgradeException;
import org.hip.vif.core.interfaces.IVIFUpgrade;
import org.hip.vif.core.service.UpgradeRegistry.ProgressIndicator;
import org.hip.vif.markup.TextParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Upgrader: Replaces textile style by proper html in all text fields.
 * 
 * @author Luthiger
 * Created: 16.02.2012
 */
public class UpgradeStyledContent implements IVIFUpgrade {
	private static final Logger LOG = LoggerFactory.getLogger(UpgradeStyledContent.class);
	
	private static final Pattern PARA = Pattern.compile("<p>(.*)</p>", Pattern.DOTALL);
	
	private static final String DESCRIPTION = "Replaces textile style by proper html in all text fields.";

	/* (non-Javadoc)
	 * @see org.hip.vif.core.interfaces.IVIFUpgrade#version()
	 */
	public String version() {
		return Upgrade11.VERSION;
	}
	
	/* (non-Javadoc)
	 * @see org.hip.vif.core.interfaces.IVIFUpgrade#getDescription()
	 */
	public String getDescription() {
		return DESCRIPTION;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.core.interfaces.IVIFUpgrade#getNumberOfSteps()
	 */
	public int getNumberOfSteps() {
		return 3;
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.core.interfaces.IVIFUpgrade#execute()
	 */
	public void execute(ProgressIndicator inIndicator) throws UpgradeException {
		TextParser lParser = new TextParser();
		try {
			processQuestions(lParser);
			inIndicator.nextStep();
			processCompletions(lParser);
			inIndicator.nextStep();
			processTexts(lParser);
			inIndicator.nextStep();
		}
		catch (Exception exc) {
			LOG.error(DESCRIPTION, exc);
			throw new UpgradeException(exc);
		}
	}

	private void processQuestions(TextParser inParser) throws Exception {
		QueryResult lQuestions = BOMHelper.getQuestionHome().select();
		while (lQuestions.hasMoreElements()) {
			DomainObject lQuestion = (DomainObject) lQuestions.nextAsDomainObject();
			String lText = lQuestion.get(QuestionHome.KEY_QUESTION).toString();
			lQuestion.set(QuestionHome.KEY_QUESTION, toHtml(lText, inParser));
			lText = lQuestion.get(QuestionHome.KEY_REMARK).toString();
			lQuestion.set(QuestionHome.KEY_REMARK, toHtml(lText, inParser));
			lQuestion.update(true);
		}
	}

	private void processCompletions(TextParser inParser) throws Exception {
		QueryResult lCompletions = BOMHelper.getCompletionHome().select();
		while (lCompletions.hasMoreElements()) {
			DomainObject lCompletion = (DomainObject) lCompletions.nextAsDomainObject();
			String lText = lCompletion.get(CompletionHome.KEY_COMPLETION).toString();
			lCompletion.set(CompletionHome.KEY_COMPLETION, toHtml(lText, inParser));
			lCompletion.update(true);
		}
	}
	
	private void processTexts(TextParser inParser) throws Exception {
		QueryResult lTexts = BOMHelper.getTextHome().select();
		while (lTexts.hasMoreElements()) {
			DomainObject lText = (DomainObject) lTexts.nextAsDomainObject();
			String lTextText = (String)lText.get(TextHome.KEY_REMARK);
			if (lTextText != null) {				
				lText.set(TextHome.KEY_REMARK, toHtml(lTextText, inParser));
				lText.update(true);
			}
		}
	}
	
	private String toHtml(String inText, TextParser inParser) {
		if (PARA.matcher(inText).matches()) {
			return inText;
		}
		return inParser.parseToHtml(inText);
	}

}

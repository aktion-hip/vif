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

package org.hip.vif.forum.usersettings.ui;

import java.sql.SQLException;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.impl.JoinRatingsToRater;
import org.hip.vif.core.bom.impl.RatingsHome;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.forum.usersettings.Activator;
import org.hip.vif.forum.usersettings.data.RatingsContainer;
import org.hip.vif.forum.usersettings.rating.RatingUserTask;
import org.hip.vif.web.util.VIFViewHelper;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

/**
 * View to display the form to review the participant.
 *  
 * @author Luthiger
 * Created: 23.12.2011
 */
@SuppressWarnings("serial")
public class ShowRatingView extends AbstractRatingView {
	
	/**
	 * Constructor for the rating form.
	 * 
	 * @param inRating {@link JoinRatingsToRater}
	 * @param inFullName String the rated person's name
	 * @param inQuestions {@link QueryResult} the questions created
	 * @param inCompletions {@link QueryResult} the completions created
	 * @param inTexts {@link QueryResult} the text entries created
	 * @param inTask {@link RatingUserTask}
	 * @throws VException
	 * @throws SQLException
	 */
	public ShowRatingView(final JoinRatingsToRater inRating, String inFullName, QueryResult inQuestions, QueryResult inCompletions, QueryResult inTexts, final RatingUserTask inTask) throws VException, SQLException {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		
		final IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-view"); //$NON-NLS-1$
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-title", lMessages.getFormattedMessage("ui.rating.title", inFullName)), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$
		
		lLayout.addComponent(new Label(lMessages.getFormattedMessage("ui.rating.subtitle", inFullName), Label.CONTENT_XHTML)); //$NON-NLS-1$
		listContributions(inQuestions, inCompletions, inTexts, inTask, lLayout, lMessages);		
		
		VerticalLayout lFormLayout = new VerticalLayout();
		lLayout.setStyleName("vif-ratings"); //$NON-NLS-1$
		final Form lForm = new Form(lFormLayout);
		final OptionGroup lCorrectness = createOptionGroup(lForm, "correctness", "ui.rating.remark.correctness", lMessages); //$NON-NLS-1$ //$NON-NLS-2$
		final OptionGroup lEfficiency = createOptionGroup(lForm, "efficiency", "ui.rating.remark.efficiency", lMessages); //$NON-NLS-1$ //$NON-NLS-2$
		final OptionGroup lEtiquette = createOptionGroup(lForm, "etiquette", "ui.rating.remark.etiquette", lMessages); //$NON-NLS-1$ //$NON-NLS-2$
		lLayout.addComponent(lForm);
		
		final TextArea lRemark = new TextArea(lMessages.getMessage("ui.rating.remark.remarks")); //$NON-NLS-1$
		lRemark.setRows(3);
		lRemark.setColumns(70);
		lLayout.addComponent(lRemark);
		
		Button lSend = new Button(lMessages.getMessage("ui.rating.button.send")); //$NON-NLS-1$
		lSend.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				try {
					lForm.commit();
					if (!inTask.saveRatings((RatingsContainer.RatingItem)lCorrectness.getValue(), 
							(RatingsContainer.RatingItem)lEfficiency.getValue(), 
							(RatingsContainer.RatingItem)lEtiquette.getValue(), 
							lRemark.getValue().toString(),
							BeanWrapperHelper.getLong(RatingsHome.KEY_RATINGEVENTS_ID, inRating))) {
						getWindow().showNotification(lMessages.getMessage("errmsg.ratings.save"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
					}
				}
				catch (InvalidValueException exc) {
					
				}
			}
		});
		lLayout.addComponent(lSend);
	}
	
	private OptionGroup createOptionGroup(Form inForm, String inFieldID, String inMsgKey, IMessages inMessages) {
		String lCaption = inMessages.getMessage(inMsgKey);
		OptionGroup out = new OptionGroup(lCaption, RatingsContainer.getRatingsContainer());
		out.setRequired(true);
		out.setImmediate(true);
		out.setRequiredError(inMessages.getFormattedMessage("ui.rating.feedback.empty", lCaption)); //$NON-NLS-1$
		inForm.addField(inFieldID, out);
		return out;
	}

}

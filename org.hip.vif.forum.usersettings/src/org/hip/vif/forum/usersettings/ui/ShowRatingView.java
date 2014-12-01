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

package org.hip.vif.forum.usersettings.ui;

import java.sql.SQLException;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.impl.JoinRatingsToRater;
import org.hip.vif.forum.usersettings.Activator;
import org.hip.vif.forum.usersettings.data.RatingsBean;
import org.hip.vif.forum.usersettings.data.RatingsContainer;
import org.hip.vif.forum.usersettings.rating.RatingUserTask;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.AbstractFormCreator;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

/** View to display the form to review the participant.
 *
 * @author Luthiger Created: 23.12.2011 */
@SuppressWarnings("serial")
public class ShowRatingView extends AbstractRatingView {

    /** Constructor for the rating form.
     *
     * @param inRating {@link JoinRatingsToRater}
     * @param inTask {@link RatingUserTask}
     * @throws VException
     * @throws SQLException */
    public ShowRatingView(final JoinRatingsToRater inRating, final RatingUserTask inTask) throws VException,
    SQLException {
        final VerticalLayout lLayout = new VerticalLayout();
        setCompositionRoot(lLayout);

        final IMessages lMessages = Activator.getMessages();
        lLayout.setStyleName("vif-view"); //$NON-NLS-1$
        lLayout.addComponent(new Label(
                String.format(VIFViewHelper.TMPL_TITLE,
                        "vif-title", lMessages.getFormattedMessage("ui.rating.title", inRating.getFullName())), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$

        lLayout.addComponent(new Label(
                lMessages.getFormattedMessage("ui.rating.subtitle", inRating.getFullName()), ContentMode.HTML)); //$NON-NLS-1$
        listContributions(inRating.getQuestionsToBeRated(), inRating.getCompletionsToBeRated(),
                inRating.getTextsToBeRated(), inTask, lLayout, lMessages);

        lLayout.setStyleName("vif-ratings"); //$NON-NLS-1$
        lLayout.addComponent(RiplaViewHelper.createSpacer());

        final UserTaskFormCreator lForm = new UserTaskFormCreator(inRating);
        lLayout.addComponent(lForm.createForm());

        final Button lSend = new Button(lMessages.getMessage("ui.rating.button.send")); //$NON-NLS-1$
        lSend.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                try {
                    lForm.commit();
                    if (!inTask.saveRatings(inRating)) {
                        Notification.show(
                                lMessages.getMessage("errmsg.ratings.save"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                    }
                }
                catch (final CommitException exc) {
                    // notification
                }
            }
        });
        lLayout.addComponent(lSend);
    }

    // ---

    private static class UserTaskFormCreator extends AbstractFormCreator {
        protected UserTaskFormCreator(final JoinRatingsToRater inRatings) {
            super(RatingsBean.createRatingsBean(inRatings));
        }

        @Override
        protected Component createTable() {
            final IMessages lMessages = Activator.getMessages();
            final VerticalLayout out = new VerticalLayout();

            out.addComponent(createOptionGroup(RatingsBean.FN_CORRECTNESS, "ui.rating.remark.correctness", lMessages));
            out.addComponent(createOptionGroup(RatingsBean.FN_EFFICIENCY, "ui.rating.remark.efficiency", lMessages));
            out.addComponent(createOptionGroup(RatingsBean.FN_ETIQUETTE, "ui.rating.remark.etiquette", lMessages));

            final TextArea lRemark = new TextArea(lMessages.getMessage("ui.rating.remark.remarks")); //$NON-NLS-1$
            lRemark.setRows(3);
            lRemark.setColumns(70);
            out.addComponent(addField(RatingsBean.FN_REMARK, lRemark));

            return out;
        }

        private Field<?> createOptionGroup(final String inFieldID, final String inMsgKey,
                final IMessages inMessages) {
            final String lCaption = inMessages.getMessage(inMsgKey);
            final OptionGroup ratingOptions = new OptionGroup(lCaption, RatingsContainer.getRatingsContainer());
            ratingOptions.setRequiredError(inMessages.getFormattedMessage("ui.rating.feedback.empty", lCaption)); //$NON-NLS-1$
            final Field<?> outField = addFieldRequired(inFieldID, ratingOptions);
            outField.setValue(null);
            return outField;
        }
    }

}

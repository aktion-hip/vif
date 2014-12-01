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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.impl.RatingsHome;
import org.hip.vif.forum.usersettings.Activator;
import org.hip.vif.web.interfaces.IPluggableWithLookup;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.RatingValue;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/** View to display the result of the rating process to the involved participants.
 *
 * @author Luthiger Created: 26.12.2011 */
@SuppressWarnings("serial")
public class ShowCompletedRatingView extends AbstractRatingView {
    private static final String TMPL_FEEDBACK = "<b>%s</b>"; //$NON-NLS-1$
    private static final String TMPL_NAME = "%s %s"; //$NON-NLS-1$

    /** Constructor for view to display the feedback that the rating is not displayed yet.
     *
     * @param inMessage String */
    public ShowCompletedRatingView(final String inMessage) {
        final VerticalLayout lLayout = new VerticalLayout();
        lLayout.setStyleName("vif-view"); //$NON-NLS-1$
        lLayout.addComponent(new Label(String.format(TMPL_FEEDBACK, inMessage), ContentMode.HTML));
        setCompositionRoot(lLayout);
    }

    /** Constructor for view to display the completed ratings given.
     *
     * @param inRatings {@link QueryResult} the participants involved in the review and rating process
     * @param inQuestions {@link QueryResult} the reviewed questions
     * @param inCompletions {@link QueryResult} the reviewed completions
     * @param inTexts {@link QueryResult} the reviewed texts
     * @param inTask {@link IPluggableWithLookup} the controlling task
     * @throws VException
     * @throws SQLException */
    public ShowCompletedRatingView(final QueryResult inRatings, final QueryResult inQuestions,
            final QueryResult inCompletions, final QueryResult inTexts, final IPluggableWithLookup inTask)
            throws VException,
            SQLException {
        final VerticalLayout lLayout = new VerticalLayout();
        setCompositionRoot(lLayout);

        final IMessages lMessages = Activator.getMessages();
        lLayout.setStyleName("vif-view"); //$NON-NLS-1$
        lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE,
                "vif-title", lMessages.getMessage("ratings.completed.title")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$

        lLayout.addComponent(new Label(lMessages.getMessage("ui.rated.subtitle"), ContentMode.HTML)); //$NON-NLS-1$
        listContributions(inQuestions, inCompletions, inTexts, inTask, lLayout, lMessages);
        lLayout.addComponent(RiplaViewHelper.createSpacer());

        final GridLayout lRatings = new GridLayout(5, 3);
        lRatings.setStyleName("vif-rating"); //$NON-NLS-1$
        lRatings.setWidth("100%"); //$NON-NLS-1$
        lRatings.setColumnExpandRatio(4, 1);
        lLayout.addComponent(lRatings);

        // headers
        lRatings.addComponent(new Label("")); //$NON-NLS-1$
        addComponentSized(new Label(lMessages.getMessage("ui.rated.column.correctness"), ContentMode.HTML), lRatings); //$NON-NLS-1$
        addComponentSized(new Label(lMessages.getMessage("ui.rated.column.efficiency"), ContentMode.HTML), lRatings); //$NON-NLS-1$
        addComponentSized(new Label(lMessages.getMessage("ui.rated.column.etiquette"), ContentMode.HTML), lRatings); //$NON-NLS-1$
        Label lRemark = new Label(lMessages.getMessage("ui.rated.column.remark"), ContentMode.HTML); //$NON-NLS-1$
        lRemark.setStyleName("vif-colhead vif-padding-left"); //$NON-NLS-1$
        lRatings.addComponent(lRemark);
        lRatings.getComponent(0, 0).setWidth(350, Unit.PIXELS);

        // ratings
        final Map<Long, String> lParticipants = new HashMap<Long, String>();
        final List<Line> lLines = new Vector<ShowCompletedRatingView.Line>();
        while (inRatings.hasMoreElements()) {
            final GeneralDomainObject lRating = inRatings.nextAsDomainObject();
            setParticipant(lRating, lParticipants);
            lLines.add(new Line(lRating));
        }

        for (final Line lLine : lLines) {
            lRatings.addComponent(new Label(lLine.getInvolved(lParticipants, lMessages), ContentMode.HTML));
            addComponentAligened(lLine.getCorrectness(), lRatings);
            addComponentAligened(lLine.getEfficiency(), lRatings);
            addComponentAligened(lLine.getEtiquette(), lRatings);
            lRemark = new Label(lLine.getRemark(), ContentMode.HTML);
            lRemark.setStyleName("vif-padding-left"); //$NON-NLS-1$
            lRatings.addComponent(lRemark);
        }
    }

    private void addComponentSized(final Component inComponent, final GridLayout inLayout) {
        inComponent.setWidth(110, Unit.PIXELS);
        inComponent.setStyleName("vif-colhead vif-center"); //$NON-NLS-1$
        inLayout.addComponent(inComponent);
        inLayout.setComponentAlignment(inComponent, Alignment.MIDDLE_CENTER);
    }

    private void addComponentAligened(final Component inComponent, final GridLayout inLayout) {
        inLayout.addComponent(inComponent);
        inLayout.setComponentAlignment(inComponent, Alignment.MIDDLE_CENTER);
    }

    private void setParticipant(final GeneralDomainObject inRating, final Map<Long, String> inParticipants) {
        final Long lID = BeanWrapperHelper.getLong(RatingsHome.KEY_RATED_ID, inRating);
        if (!inParticipants.containsKey(lID)) {
            inParticipants.put(lID,
                    String.format(TMPL_NAME, BeanWrapperHelper.getString(MemberHome.KEY_FIRSTNAME, inRating),
                            BeanWrapperHelper.getString(MemberHome.KEY_NAME, inRating)));
        }
    }

    // --- inner classes ---

    private static class Line {
        private final Component correctness;
        private final Component efficiency;
        private final Component etiquette;
        private final String remark;
        private final Long sender;
        private final Long receiver;

        Line(final GeneralDomainObject inRating) {
            correctness = getRatingValue(RatingsHome.KEY_CORRECTNESS, inRating);
            efficiency = getRatingValue(RatingsHome.KEY_EFFICIENCY, inRating);
            etiquette = getRatingValue(RatingsHome.KEY_ETIQUETTE, inRating);
            remark = BeanWrapperHelper.getPlain(RatingsHome.KEY_REMARK, inRating);
            sender = BeanWrapperHelper.getLong(RatingsHome.KEY_RATER_ID, inRating);
            receiver = BeanWrapperHelper.getLong(RatingsHome.KEY_RATED_ID, inRating);
        }

        private Component getRatingValue(final String inKey, final GeneralDomainObject inRating) {
            final String lRating = BeanWrapperHelper.getString(inKey, inRating);
            for (final RatingValue lRatingValue : RatingValue.values()) {
                if (lRatingValue.check(lRating)) {
                    return lRatingValue.render();
                }
            }
            return new Label(""); //$NON-NLS-1$
        }

        Component getCorrectness() {
            return correctness;
        }

        Component getEfficiency() {
            return efficiency;
        }

        Component getEtiquette() {
            return etiquette;
        }

        String getRemark() {
            return remark;
        }

        String getInvolved(final Map<Long, String> inParticipants, final IMessages inMessages) {
            final String lReceiver = inParticipants.get(receiver);
            final String lSender = inParticipants.get(sender);
            return inMessages.getFormattedMessage(
                    "ui.rated.row.rating", lReceiver == null ? "-" : lReceiver, lSender == null ? "-" : lSender); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

}

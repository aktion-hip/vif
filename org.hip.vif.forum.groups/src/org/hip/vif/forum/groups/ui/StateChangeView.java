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

package org.hip.vif.forum.groups.ui;

import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.tasks.StateChangePrepareTask;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

/** View to initiate a question's state change.
 *
 * @author Luthiger Created: 27.08.2011 */
@SuppressWarnings("serial")
public class StateChangeView extends CustomComponent {

    /** Constructor
     *
     * @param inQuestion {@link Question} the question which's state has to be changed
     * @param inMsgKeyTitle String the view's title
     * @param inMsgKeyQuestion String the view's message
     * @param inMsgKeyButton String the action button's caption
     * @param inTask {@link StateChangePrepareTask} */
    public StateChangeView(final Question inQuestion, final String inMsgKeyTitle, final String inMsgKeyQuestion,
            final String inMsgKeyButton, final StateChangePrepareTask inTask) {
        final VerticalLayout lLayout = new VerticalLayout();
        setCompositionRoot(lLayout);

        final IMessages lMessages = Activator.getMessages();

        lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE,
                "vif-pagetitle", lMessages.getMessage(inMsgKeyTitle)), ContentMode.HTML)); //$NON-NLS-1$
        lLayout.setStyleName("vif-view"); //$NON-NLS-1$

        final String lLabel = String
                .format(lMessages.getMessage("ui.question.view.title.question"), BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION_DECIMAL, inQuestion)); //$NON-NLS-1$
        lLayout.addComponent(new Label(
                String.format(VIFViewHelper.TMPL_TITLE, "vif-title", lLabel), ContentMode.HTML)); //$NON-NLS-1$
        lLayout.addComponent(new Label(BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION, inQuestion),
                ContentMode.HTML));

        lLayout.addComponent(RiplaViewHelper.createSpacer());
        lLayout.addComponent(new Label(lMessages.getMessage(inMsgKeyQuestion)));

        final Button lProcess = new Button(lMessages.getMessage(inMsgKeyButton), new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                if (!inTask.processStateChange()) {
                    Notification.show(lMessages.getMessage("errmsg.save.general"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                }
            }
        });
        lLayout.addComponent(lProcess);
    }

}

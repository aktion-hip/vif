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

package org.hip.vif.forum.groups.ui;

import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.tasks.StateChangePrepareTask;
import org.hip.vif.web.util.VIFViewHelper;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

/**
 * View to initiate a question's state change.
 * 
 * @author Luthiger
 * Created: 27.08.2011
 */
@SuppressWarnings("serial")
public class StateChangeView extends CustomComponent {

	/**
	 * Constructor
	 * 
	 * @param inQuestion {@link Question} the question which's state has to be changed
	 * @param inMsgKeyTitle String the view's title
	 * @param inMsgKeyQuestion String the view's message
	 * @param inMsgKeyButton String the action button's caption
	 * @param inTask {@link StateChangePrepareTask}
	 */
	public StateChangeView(Question inQuestion, String inMsgKeyTitle, String inMsgKeyQuestion, String inMsgKeyButton, final StateChangePrepareTask inTask) {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);

		final IMessages lMessages = Activator.getMessages();

		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-pagetitle", lMessages.getMessage(inMsgKeyTitle)), Label.CONTENT_XHTML)); //$NON-NLS-1$
		lLayout.setStyleName("vif-view"); //$NON-NLS-1$

		String lLabel = String.format(lMessages.getMessage("ui.question.view.title.question"), BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION_DECIMAL, inQuestion)); //$NON-NLS-1$
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-title", lLabel), Label.CONTENT_XHTML)); //$NON-NLS-1$
		lLayout.addComponent(new Label(BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION, inQuestion), Label.CONTENT_XHTML));

		lLayout.addComponent(VIFViewHelper.createSpacer());
		lLayout.addComponent(new Label(lMessages.getMessage(inMsgKeyQuestion)));
		
		Button lProcess = new Button(lMessages.getMessage(inMsgKeyButton), new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				if (!inTask.processStateChange()) {
					getWindow().showNotification(lMessages.getMessage("errmsg.save.general"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
				}
			}
		});
		lLayout.addComponent(lProcess);
	}
	
}

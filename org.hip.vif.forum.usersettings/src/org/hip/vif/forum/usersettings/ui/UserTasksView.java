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

import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.forum.usersettings.Activator;
import org.hip.vif.web.util.VIFViewHelper;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * View to display the user tasks.
 * 
 * @author Luthiger
 * Created: 23.12.2011
 */
@SuppressWarnings("serial")
public class UserTasksView extends CustomComponent {
	private VerticalLayout layout;
	private IMessages messages = Activator.getMessages();
	
	/**
	 * Default constructor.
	 */
	public UserTasksView() {
		layout = new VerticalLayout();
		setCompositionRoot(layout);
		
		layout.setStyleName("vif-view"); //$NON-NLS-1$
		layout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-pagetitle", messages.getMessage("usersettings.menu.open.tasks")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Constructor for view displaying the message that there's no open user task.
	 * 
	 * @param inName String 
	 */
	public UserTasksView(String inName) {
		this();
		layout.addComponent(new Label(messages.getFormattedMessage("ui.usersettings.usertasks.no", inName), Label.CONTENT_XHTML)); //$NON-NLS-1$
	}
	
	@Override
	public void addComponent(Component inComponent) {
		layout.addComponent(inComponent);
	}

}

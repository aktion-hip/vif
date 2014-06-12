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

package org.hip.vif.admin.member.ui;

import java.util.Collection;

import org.hip.vif.admin.member.Activator;
import org.hip.vif.admin.member.data.MemberBeanContainer;
import org.hip.vif.admin.member.tasks.AbstrachtMemberLookupTask;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.member.MemberBean;
import org.hip.vif.web.util.VIFViewHelper;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

/**
 * @author Luthiger
 * Created: 14.11.2011
 */
@SuppressWarnings("serial")
public class SelectMemberLookup extends CustomComponent {

	/**
	 * View constructor.
	 * 
	 * @param inSubtitle String
	 * @param inRightColumnTitle String
	 * @param inMembers {@link MemberBeanContainer} the available members to select from
	 * @param inAdmins {@link Collection} the collection of members already selected (must be a subset of <code>inMembers</code>)
	 * @param inTask {@link AbstrachtMemberLookupTask}
	 */
	public SelectMemberLookup(String inSubtitle, String inRightColumnTitle, final MemberBeanContainer inMembers, Collection<MemberBean> inAdmins, final AbstrachtMemberLookupTask inTask) {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		
		final IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-view"); //$NON-NLS-1$
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-pagetitle", lMessages.getMessage("ui.member.lookup.title.page")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-description", inSubtitle), Label.CONTENT_XHTML)); //$NON-NLS-1$
		
		final TwinColSelect lSelect = new TwinColSelect();
		lSelect.setContainerDataSource(inMembers);
		lSelect.setValue(inAdmins);
		
		lSelect.setLeftColumnCaption(lMessages.getMessage("ui.member.lookup.available")); //$NON-NLS-1$
		lSelect.setRightColumnCaption(inRightColumnTitle); //$NON-NLS-1$
		
		lSelect.setWidth(650, UNITS_PIXELS);
		lSelect.setRows(19);
		lSelect.setImmediate(true);
		lLayout.addComponent(lSelect);
		lLayout.addComponent(VIFViewHelper.createSpacer());
		
		Button lSave = new Button(lMessages.getMessage("ui.member.button.save")); //$NON-NLS-1$
		lSave.addListener(new Button.ClickListener() {			
			@SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent inEvent) {
				if (!inTask.selectMembers((Collection<MemberBean>) lSelect.getValue(), inEvent.getButton().getWindow())) {
					getWindow().showNotification(lMessages.getMessage("ui.member.lookup.error.msg"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
				}
			}
		});
		lLayout.addComponent(lSave);
	}
	
}

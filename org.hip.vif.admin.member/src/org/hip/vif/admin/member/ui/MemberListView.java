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

import org.hip.vif.admin.member.Activator;
import org.hip.vif.admin.member.data.MemberContainer;
import org.hip.vif.admin.member.tasks.AbstractMemberSearchTask;
import org.hip.vif.admin.member.tasks.MemberShowListTask;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.web.util.VIFViewHelper;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

/**
 * View to show the list of members.
 * 
 * @author Luthiger
 * Created: 17.10.2011
 */
@SuppressWarnings("serial")
public class MemberListView extends CustomComponent {
	private static final String SUBTITLE_WARNING = "<div class=\"vif-warning\">%s</div>"; //$NON-NLS-1$
	private static final int PAGE_LENGTH = 15;

	private boolean confirmationMode = false;

	/**
	 * Constructor
	 * 
	 * @param inMembers {@link MemberContainer}
	 * @param inIsDeletable boolean <code>true</code> if the member entries are deletable and, therefore, a delete button is displayed
	 * @param inTask {@link MemberShowListTask}
	 */
	public MemberListView(final MemberContainer inMembers, boolean inIsDeletable, final AbstractMemberSearchTask inTask) {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		
		final IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-view"); //$NON-NLS-1$
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-pagetitle", lMessages.getMessage("ui.member.list.title.page")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$
		
		final Label lSubtitle = new Label(String.format(SUBTITLE_WARNING, lMessages.getMessage("ui.member.delete.warning")), Label.CONTENT_XHTML); //$NON-NLS-1$
		lSubtitle.setVisible(false);
		lLayout.addComponent(lSubtitle);

		final Table lTable = new Table();
		lTable.setWidth("100%"); //$NON-NLS-1$
		lTable.setContainerDataSource(inMembers);
		
		if (inIsDeletable) {
			//generate check box
			lTable.addGeneratedColumn(MemberContainer.MEMBER_CHECK, new VIFViewHelper.CheckBoxColumnGenerator(new VIFViewHelper.IConfirmationModeChecker() {			
				public boolean inConfirmationMode() {
					return confirmationMode;
				}
			}));
			lTable.setVisibleColumns(VIFViewHelper.getModifiedArray(MemberContainer.MEMBER_CHECK, MemberContainer.NATURAL_COL_ORDER));
			lTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(VIFViewHelper.getModifiedArray("", MemberContainer.COL_HEADERS), lMessages)); //$NON-NLS-1$
		}
		else {
			lTable.setVisibleColumns(MemberContainer.NATURAL_COL_ORDER);
			lTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(MemberContainer.COL_HEADERS, lMessages));
		}

		lTable.setColumnCollapsingAllowed(true);
		lTable.setColumnReorderingAllowed(true);
		lTable.setSelectable(true);
		lTable.setImmediate(true);
		lTable.setPageLength(inMembers.size() > PAGE_LENGTH ? PAGE_LENGTH : 0);
		lTable.setColumnExpandRatio(MemberContainer.MEMBER_NAME, 1);
		lTable.setColumnWidth(MemberContainer.MEMBER_STREET, 200);
		lTable.setColumnWidth(MemberContainer.MEMBER_PLACE, 200);
		lTable.setColumnWidth(MemberContainer.MEMBER_MAIL, 200);
		lTable.addListener((Property.ValueChangeListener)inTask);
		lLayout.addComponent(lTable);
		
		if (inIsDeletable) {			
			lLayout.addComponent(VIFViewHelper.createSpacer());
			Button lDelete = new Button(lMessages.getMessage("ui.member.button.delete")); //$NON-NLS-1$
			lDelete.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent inEvent) {
					if (confirmationMode) {					
						if (!inTask.deleteMember()) {
							getWindow().showNotification(lMessages.getMessage("errmsg.process.delete"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
						}
					}
					else {
						if (VIFViewHelper.processAction(inMembers, getWindow())) {
							confirmationMode = true;
							inMembers.addContainerFilter(new SelectedFilter());
							lSubtitle.setVisible(true);
							lTable.setSelectable(false);
							lTable.setPageLength(0);
						}
					}
				}
			});
			lLayout.addComponent(lDelete);
		}
	}
	
// --- private classes ---
	
	private static class SelectedFilter implements Filter {
		public boolean passesFilter(Object inItemId, Item inItem) throws UnsupportedOperationException {
			Property lCheckBox = inItem.getItemProperty(MemberContainer.MEMBER_CHECKED);
			return (Boolean) lCheckBox.getValue();
		}
		
		public boolean appliesToProperty(Object inPropertyId) {
			return MemberContainer.MEMBER_CHECKED.equals(inPropertyId);
		}
	}
	
}

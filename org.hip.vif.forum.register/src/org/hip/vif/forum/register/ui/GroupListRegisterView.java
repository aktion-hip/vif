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

package org.hip.vif.forum.register.ui;

import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.forum.register.Activator;
import org.hip.vif.forum.register.data.GroupContainer;
import org.hip.vif.forum.register.tasks.RegisterShowListTask;
import org.hip.vif.forum.register.tasks.RegisterShowListTask.Feedback;
import org.hip.vif.web.util.VIFViewHelper;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * View to display the list of open groups for that the user can register and become group participant.
 * 
 * @author Luthiger
 * Created: 30.09.2011
 */
@SuppressWarnings("serial")
public class GroupListRegisterView extends CustomComponent {
	private Table table;

	/**
	 * Constructor
	 * 
	 * @param inGroups {@link GroupContainer} the groups to display in the table
	 * @param inTask {@link RegisterShowListTask}
	 */
	public GroupListRegisterView(final GroupContainer inGroups, final RegisterShowListTask inTask) {
		final VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		
		final IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-table"); //$NON-NLS-1$
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-title", lMessages.getMessage("ui.register.view.title.page")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$
		
		table = createTable(inGroups, lMessages);
		lLayout.addComponent(table);

		lLayout.addComponent(VIFViewHelper.createSpacer());
		Button lButton = new Button(lMessages.getMessage("ui.register.view.button")); //$NON-NLS-1$
		lButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				if (VIFViewHelper.processAction(inGroups, getWindow())) {					
					Feedback lFeedback = inTask.saveRegisterings();
					//refresh the view, we do this to display the correct number of participants
					Table lOld = table;
					table = createTable(lFeedback.groups, lMessages);
					lLayout.replaceComponent(lOld, table);
					table.requestRepaint();
					//give feedback
					getWindow().showNotification(lFeedback.message, lFeedback.notificationType);
				}
			}
		});
		lLayout.addComponent(lButton);
	}
	
	private Table createTable(GroupContainer inGroups, IMessages inMessages) {
		Table outTable = new Table();
		outTable.setWidth("100%"); //$NON-NLS-1$
		outTable.setContainerDataSource(inGroups);
		
		outTable.addGeneratedColumn(GroupContainer.GROUP_CHK, new VIFViewHelper.CheckBoxColumnGenerator(new VIFViewHelper.IConfirmationModeChecker() {
			public boolean inConfirmationMode() {
				return false;
			}
		}));
		
		outTable.setColumnCollapsingAllowed(true);
		outTable.setColumnReorderingAllowed(true);
		outTable.setSelectable(false);
		outTable.setImmediate(false);
		outTable.setPageLength(VIFViewHelper.getTablePageLength(inGroups.size()));
		
		outTable.setVisibleColumns(GroupContainer.NATURAL_COL_ORDER);
		outTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(GroupContainer.COL_HEADERS, inMessages));
		return outTable;
	}
	
}

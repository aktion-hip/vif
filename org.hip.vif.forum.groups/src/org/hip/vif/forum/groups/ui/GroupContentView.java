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

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.data.GroupContentContainer;
import org.hip.vif.forum.groups.data.GroupContentWrapper;
import org.hip.vif.forum.groups.tasks.GroupShowTask;
import org.hip.vif.web.util.VIFViewHelper;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

/**
 * The view to display the group's tree of questions.
 * 
 * @author Luthiger
 * Created: 22.05.2011
 */
@SuppressWarnings("serial")
public class GroupContentView extends CustomComponent {
	private Tree questionTree;
	private GroupShowTask task;

	/**
	 * Constructor
	 * 
	 * @param inGroup {@link Group}
	 * @param inContent {@link GroupContentContainer}
	 * @param inTask {@link GroupShowTask}
	 * @throws VException
	 */
	public GroupContentView(Group inGroup, GroupContentContainer inContent, GroupShowTask inTask) throws VException {
		task = inTask;
		
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		lLayout.setStyleName("vif-table"); //$NON-NLS-1$
		
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-title",  //$NON-NLS-1$
				Activator.getMessages().getFormattedMessage("ui.group.content.view.title.page", inGroup.get(GroupHome.KEY_NAME).toString())),  //$NON-NLS-1$
				Label.CONTENT_XHTML)); //$NON-NLS-1$
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-description", inGroup.get(GroupHome.KEY_DESCRIPTION).toString()),  //$NON-NLS-1$
				Label.CONTENT_XHTML));
		
		questionTree = new Tree();
		questionTree.focus();
		questionTree.setContainerDataSource(inContent);
		lLayout.addComponent(questionTree);
		
		for (GroupContentWrapper lNode : inContent.getExpandedNodes()) {			
			questionTree.expandItem(lNode);
		}
		
		questionTree.addListener(new ItemClickEvent.ItemClickListener() {
			public void itemClick(ItemClickEvent inEvent) {
				task.processSelection((GroupContentWrapper) inEvent.getItemId());
			}
		});
		questionTree.addShortcutListener(new ExtendedShortcutListener("enter", KeyCode.ENTER)); //$NON-NLS-1$
	}
	
	private class ExtendedShortcutListener extends ShortcutListener {
		public ExtendedShortcutListener(String inShorthandCaption, int inKey) {
			super(inShorthandCaption, inKey, null);
		}
		@Override
		public void handleAction(Object inSender, Object inTarget) {
			task.processSelection((GroupContentWrapper) ((Tree)inTarget).getValue());
		}
	}
	
}

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
package org.hip.vif.admin.groupadmin.ui;

import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.data.GroupContentContainer;
import org.hip.vif.admin.groupadmin.data.GroupContentWrapper;
import org.hip.vif.admin.groupadmin.tasks.AdminQuestionListTask;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.web.bom.Group;
import org.hip.vif.web.bom.GroupHome;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

/**
 * View to display the list of questions belonging to a specified discussion
 * group.
 * 
 * @author Luthiger Created: 20.11.2011
 */
@SuppressWarnings("serial")
public class GroupContentView extends CustomComponent {
	private final AdminQuestionListTask task;

	/**
	 * View constructor
	 * 
	 * @param inGroup
	 *            {@link Group}
	 * @param inQuestions
	 *            {@link GroupContentContainer}
	 * @param inTask
	 *            {@link AdminQuestionListTask}
	 */
	public GroupContentView(final Group inGroup,
			final GroupContentContainer inQuestions,
			final AdminQuestionListTask inTask) {
		task = inTask;
		final VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);

		final IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-table"); //$NON-NLS-1$
		final String lTitle = lMessages.getFormattedMessage(
				"ui.discussion.questions.view.title.page", //$NON-NLS-1$
				BeanWrapperHelper.getLong(GroupHome.KEY_ID, inGroup),
				BeanWrapperHelper.getString(GroupHome.KEY_NAME, inGroup));
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE,
				"vif-pagetitle", lTitle), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$

		final Label lSubtitle = new Label(
				String.format(
						VIFViewHelper.TMPL_TITLE,
						"vif-description", lMessages.getMessage("ui.discussion.questions.view.title.sub")), ContentMode.HTML); //$NON-NLS-1$ //$NON-NLS-2$
		lLayout.addComponent(lSubtitle);

		final Tree lTree = new Tree();
		lTree.focus();
		lTree.setContainerDataSource(inQuestions);
		lLayout.addComponent(lTree);

		for (final GroupContentWrapper lNode : inQuestions.getExpandedNodes()) {
			lTree.expandItem(lNode);
		}

		lTree.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(final ItemClickEvent inEvent) {
				task.processSelection((GroupContentWrapper) inEvent.getItemId());
			}
		});
		lTree.addShortcutListener(new ExtendedShortcutListener(
				"enter", KeyCode.ENTER)); //$NON-NLS-1$
	}

	private class ExtendedShortcutListener extends ShortcutListener {
		public ExtendedShortcutListener(final String inShorthandCaption,
				final int inKey) {
			super(inShorthandCaption, inKey, null);
		}

		@Override
		public void handleAction(final Object inSender, final Object inTarget) {
			task.processSelection((GroupContentWrapper) ((Tree) inTarget)
					.getValue());
		}
	}

}

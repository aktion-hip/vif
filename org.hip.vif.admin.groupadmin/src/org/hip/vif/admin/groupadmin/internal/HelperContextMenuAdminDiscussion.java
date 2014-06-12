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

package org.hip.vif.admin.groupadmin.internal;

import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.Constants;
import org.hip.vif.admin.groupadmin.tasks.AdminCompletionNewTask;
import org.hip.vif.admin.groupadmin.tasks.AdminContributionsPublishablesTask;
import org.hip.vif.admin.groupadmin.tasks.AdminQuestionListTask;
import org.hip.vif.admin.groupadmin.tasks.AdminQuestionNewTask;
import org.hip.vif.admin.groupadmin.tasks.AdminShowPendingTask;
import org.hip.vif.admin.groupadmin.tasks.BibliographyHandleTask;
import org.hip.vif.web.tasks.BackTask;
import org.ripla.interfaces.IMessages;
import org.ripla.web.interfaces.IContextMenuItem;
import org.ripla.web.interfaces.IMenuSet;
import org.ripla.web.menu.ContextMenuItem;

/**
 * Helper class to create the context menus.
 * 
 * @author Luthiger Created: 19.11.2011
 */
public class HelperContextMenuAdminDiscussion {
	private static final IMessages MESSAGES = Activator.getMessages();
	private static final IContextMenuItem GROUP_CONTENT = new ContextMenuItem(
			AdminQuestionListTask.class,
			"context.menu.discussion.overview", Constants.PERMISSION_QUESTION_NEW, //$NON-NLS-1$
			MESSAGES);
	private static final IContextMenuItem QUESTION_NEW = new ContextMenuItem(
			AdminQuestionNewTask.class,
			"context.menu.discussion.question.new", Constants.PERMISSION_QUESTION_NEW, //$NON-NLS-1$
			MESSAGES);
	private static final IContextMenuItem COMPLETION_NEW = new ContextMenuItem(
			AdminCompletionNewTask.class,
			"context.menu.discussion.completion.new", Constants.PERMISSION_QUESTION_NEW, //$NON-NLS-1$
			MESSAGES);
	private static final IContextMenuItem BIBLIO_ADD = new ContextMenuItem(
			BibliographyHandleTask.class,
			"context.menu.discussion.biblio.add", Constants.PERMISSION_QUESTION_NEW, //$NON-NLS-1$
			MESSAGES);
	private static final IContextMenuItem PUBLISH = new ContextMenuItem(
			AdminContributionsPublishablesTask.class,
			"context.menu.discussion.publish", Constants.PERMISSION_QUESTION_NEW, //$NON-NLS-1$
			MESSAGES);
	private static final IContextMenuItem PENDING = new ContextMenuItem(
			AdminShowPendingTask.class,
			"context.menu.discussion.pending", Constants.PERMISSION_QUESTION_NEW, //$NON-NLS-1$
			MESSAGES);
	private static final IContextMenuItem BACK = new BackTask.ContextMenuItemBack(
			org.hip.vif.admin.groupadmin.tasks.BackTask.class);

	// ---

	public static IMenuSet createContextMenuSet1() {
		return new IMenuSet() {
			@Override
			public String getSetID() {
				return Constants.MENU_SET_ID_GROUP_CONTENT;
			}

			@Override
			public IContextMenuItem[] getContextMenuItems() {
				return new IContextMenuItem[] { PUBLISH, PENDING };
			}
		};
	}

	public static IMenuSet createContextMenuSet2() {
		return new IMenuSet() {
			@Override
			public String getSetID() {
				return Constants.MENU_SET_ID_QUESTION_SHOW;
			}

			@Override
			public IContextMenuItem[] getContextMenuItems() {
				return new IContextMenuItem[] { GROUP_CONTENT, QUESTION_NEW,
						COMPLETION_NEW, BIBLIO_ADD, PUBLISH, PENDING };
			}
		};
	}

	public static IMenuSet createContextMenuSet3() {
		return new IMenuSet() {
			@Override
			public String getSetID() {
				return Constants.MENU_SET_ID_CONTRIBUTION_EDIT;
			}

			@Override
			public IContextMenuItem[] getContextMenuItems() {
				return new IContextMenuItem[] { GROUP_CONTENT, PUBLISH,
						PENDING, BACK };
			}
		};
	}

	public static IMenuSet createContextMenuSet4() {
		return new IMenuSet() {
			@Override
			public String getSetID() {
				return Constants.MENU_SET_ID_PUBLISH;
			}

			@Override
			public IContextMenuItem[] getContextMenuItems() {
				return new IContextMenuItem[] { GROUP_CONTENT, PENDING, BACK };
			}
		};
	}

	public static IMenuSet createContextMenuSet5() {
		return new IMenuSet() {
			@Override
			public String getSetID() {
				return Constants.MENU_SET_ID_PENDING;
			}

			@Override
			public IContextMenuItem[] getContextMenuItems() {
				return new IContextMenuItem[] { GROUP_CONTENT, PUBLISH, BACK };
			}
		};
	}

	public static IMenuSet createContextMenuSet6() {
		return new IMenuSet() {
			@Override
			public String getSetID() {
				return Constants.MENU_SET_ID_BACK;
			}

			@Override
			public IContextMenuItem[] getContextMenuItems() {
				return new IContextMenuItem[] { GROUP_CONTENT, BACK };
			}
		};
	}

}

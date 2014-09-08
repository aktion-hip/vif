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

package org.hip.vif.forum.groups.internal.submenu;

import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.Constants;
import org.hip.vif.forum.groups.tasks.BibliographyHandleTask;
import org.hip.vif.forum.groups.tasks.CompletionNewTask;
import org.hip.vif.forum.groups.tasks.ContributionsListTask;
import org.hip.vif.forum.groups.tasks.GroupShowTask;
import org.hip.vif.forum.groups.tasks.QuestionNewTask;
import org.hip.vif.forum.groups.tasks.RequestsListTask;
import org.hip.vif.forum.groups.tasks.StateChangePrepareTask;
import org.hip.vif.web.tasks.BackTask;
import org.ripla.interfaces.IMessages;
import org.ripla.web.interfaces.IContextMenuItem;
import org.ripla.web.interfaces.IMenuSet;
import org.ripla.web.menu.ContextMenuItem;

/** Helper class to create the context menu item set when the group content is displayed.
 *
 * @author Luthiger Created: 26.06.2011 */
public class HelperContextMenuForumGroups {
    private static final IMessages MESSAGES = Activator.getMessages();
    private static final IContextMenuItem LIST_UNPUBLISHED = new ContextMenuItem(ContributionsListTask.class,
            "context.menu.groups.pending", //$NON-NLS-1$
            Constants.PERMISSION_REVIEW_REQUEST, //$NON-NLS-1$
            MESSAGES);
    private static final IContextMenuItem LIST_REQUESTS = new ContextMenuItem(RequestsListTask.class,
            "context.menu.groups.process", //$NON-NLS-1$
            Constants.PERMISSION_REVIEW_PROCESS,
            MESSAGES);
    private static final IContextMenuItem NEW_COMPLETION = new ContextMenuItem(CompletionNewTask.class,
            "context.menu.groups.new.completion", //$NON-NLS-1$
            Constants.PERMISSION_NEW_COMPLETION,
            MESSAGES);
    private static final IContextMenuItem NEW_QUESTION = new ContextMenuItem(QuestionNewTask.class,
            "context.menu.groups.new.question", //$NON-NLS-1$
            Constants.PERMISSION_NEW_QUESTION,
            MESSAGES);
    private static final IContextMenuItem PREPARE_STATE_CHANGE = new ContextMenuItem(StateChangePrepareTask.class,
            "context.menu.groups.state.change", //$NON-NLS-1$
            Constants.PERMISSION_STATE_CHANGE_REQUEST,
            MESSAGES);
    private static final IContextMenuItem HANDLE_BIBLIOGRAPHY = new ContextMenuItem(BibliographyHandleTask.class,
            "context.menu.groups.link.bibliography", //$NON-NLS-1$
            Constants.PERMISSION_EDIT_BIBLIOGRAPHY,
            MESSAGES);
    private static final IContextMenuItem SHOW_GROUP = new ContextMenuItem(GroupShowTask.class,
            "context.menu.groups.group", "", //$NON-NLS-1$ //$NON-NLS-2$
            MESSAGES);

    // private static final IVIFSubMenuItem DELETE_CONTRIBUTION = new SubMenuItem(ContributionsPublishTask.class,
    // "org.hip.vif.forum.groups.delete", "",
    // false, true, false,
    // new String[] {VIFGroupWorkflow.STATE_ACTIVE}
    // );

    public static IMenuSet createContextMenuSet1() {
        return new IMenuSet() {
            @Override
            public String getSetID() {
                return Constants.MENU_SET_ID_GROUP_REVIEW;
            }

            @Override
            public IContextMenuItem[] getContextMenuItems() {
                return new IContextMenuItem[] { LIST_UNPUBLISHED, LIST_REQUESTS };
            }
        };
    }

    public static IMenuSet createContextMenuSet2() {
        return new IMenuSet() {
            @Override
            public String getSetID() {
                return Constants.MENU_SET_ID_PROCESS;
            }

            @Override
            public IContextMenuItem[] getContextMenuItems() {
                return new IContextMenuItem[] { NEW_COMPLETION, NEW_QUESTION, PREPARE_STATE_CHANGE, LIST_UNPUBLISHED };
            }
        };
    }

    public static IMenuSet createContextMenuSet3() {
        return new IMenuSet() {
            @Override
            public String getSetID() {
                return Constants.MENU_SET_ID_CONTRIBUTE;
            }

            @Override
            public IContextMenuItem[] getContextMenuItems() {
                return new IContextMenuItem[] { SHOW_GROUP, NEW_COMPLETION, NEW_QUESTION, HANDLE_BIBLIOGRAPHY,
                        PREPARE_STATE_CHANGE, LIST_UNPUBLISHED, LIST_REQUESTS };
            }
        };
    }

    public static IMenuSet createContextMenuSet4() {
        return new IMenuSet() {
            @Override
            public String getSetID() {
                return Constants.MENU_SET_ID_GROUP_CONTENT;
            }

            @Override
            public IContextMenuItem[] getContextMenuItems() {
                return new IContextMenuItem[] { SHOW_GROUP,
                        new BackTask.ContextMenuItemBack(org.hip.vif.forum.groups.tasks.BackTask.class) };
            }
        };
    }

    public static IMenuSet createContextMenuSet5() {
        return new IMenuSet() {
            @Override
            public String getSetID() {
                return Constants.MENU_SET_ID_REVIEW;
            }

            @Override
            public IContextMenuItem[] getContextMenuItems() {
                return new IContextMenuItem[] { SHOW_GROUP, LIST_REQUESTS,
                        new BackTask.ContextMenuItemBack(org.hip.vif.forum.groups.tasks.BackTask.class) };
            }
        };
    }

    public static IMenuSet createContextMenuSet6() {
        return new IMenuSet() {
            @Override
            public String getSetID() {
                return Constants.MENU_SET_ID_EDIT;
            }

            @Override
            public IContextMenuItem[] getContextMenuItems() {
                return new IContextMenuItem[] { SHOW_GROUP, LIST_UNPUBLISHED,
                        new BackTask.ContextMenuItemBack(org.hip.vif.forum.groups.tasks.BackTask.class) };
            }
        };
    }

}

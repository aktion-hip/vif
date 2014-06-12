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

package org.hip.vif.web;

import java.util.Locale;

/**
 * This bundle's constants.
 * 
 * @author Luthiger Created: 24.10.2011
 */
public class Constants {
	public static final String CONTEXT_FORUM = "forum"; //$NON-NLS-1$
	public static final String CONTEXT_ADMIN = "admin"; //$NON-NLS-1$

	public static final String TMP_UPLOAD_PREFIX = "vif_"; //$NON-NLS-1$
	public static final String TMP_UPLOAD_SUFFIX = ".upload"; //$NON-NLS-1$

	public static final String MENU_SET_ID_EMPTY = "empty"; //$NON-NLS-1$

	public static final int MIN_PWRD_LENGTH = 6;

	public final static long ONCE_PER_DAY = 1000 * 60 * 60 * 24;

	// the languages available for the application
	public static final Locale[] LANGUAGES = new Locale[] { Locale.ENGLISH,
			Locale.GERMAN };

	public static final String TASK_PATTERN = "%s/%s"; //$NON-NLS-1$

	public static final String EVENT_TOPIC_TASKS = "org/hip/vif/web/TaskEvent/TASK"; //$NON-NLS-1$
	public static final String EVENT_PROPERTY_NEXT_TASK = "next.task"; //$NON-NLS-1$
	public static final String EVENT_TOPIC_CONTEXT_MENU = "org/hip/vif/web/TaskEvent/CONTEXTMENU"; //$NON-NLS-1$
	public static final String EVENT_PROPERTY_CONTEXT_MENU_ID = "context.menu.id"; //$NON-NLS-1$
	public static final String EVENT_TOPIC_NOTIFICATION = "org/hip/vif/web/TaskEvent/NOTIFICATION"; //$NON-NLS-1$
	public static final String EVENT_PROPERTY_NOTIFICATION_MSG = "notification.msg"; //$NON-NLS-1$
	public static final String EVENT_PROPERTY_NOTIFICATION_TYPE = "notification.type"; //$NON-NLS-1$
	public static final String EVENT_TOPIC_REFRESH = "org/hip/vif/web/TaskEvent/REFRESH"; //$NON-NLS-1$
	public static final String EVENT_PROPERTY_REFRESH = "refresh"; //$NON-NLS-1$

	public static final String EVENT_TOPIC_LOOKUP = "org/hip/vif/web/LookupEvent/LOOKUP"; //$NON-NLS-1$
	public static final String EVENT_PROPERTY_LOOKUP_TYPE = "lookup.type"; //$NON-NLS-1$
	public static final String EVENT_PROPERTY_LOOKUP_ID = "lookup.id"; //$NON-NLS-1$

	public static final String GROUP_ID_KEY = "vif.group.id";
	public static final String QUESTION_ID_KEY = "vif.question.id";
	public static final String COMPLETION_ID_KEY = "vif.completion.id";
	public static final String TEXT_ID_KEY = "vif.text.id";
	public static final String TEXT_VERSION_ID_KEY = "vif.text.version.id";
}

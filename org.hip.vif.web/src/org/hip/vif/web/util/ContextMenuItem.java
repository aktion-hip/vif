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
package org.hip.vif.web.util;

import org.hip.vif.web.interfaces.IVIFContextMenuItem;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.User;
import org.ripla.interfaces.IMessages;
import org.ripla.util.ParameterObject;
import org.ripla.web.interfaces.IContextMenuItem;
import org.ripla.web.interfaces.IPluggable;

/**
 * Default implementation of <code>IVIFContextMenuItem</code>.<br />
 * This class is more or less a parameter object. All fields are set in the
 * constructor and are read-only after instantiation.
 * 
 * @author Luthiger Created: 12.10.2011
 * @see IVIFContextMenuItem
 */
public class ContextMenuItem implements IContextMenuItem {
	private final Class<? extends IPluggable> taskClass;
	private final String titleMsg;
	private final String menuPermission;
	private final boolean needsGroupAmin;
	private final boolean needsRegistration;
	private final boolean needsTypePrivate;
	private final String[] groupStates;
	private final IMessages messages;

	/**
	 * ContextMenuItem constructor.
	 * 
	 * @param inTaskClass
	 *            Class of {@link IPluggable}
	 * @param inTitleMsg
	 *            String key
	 * @param inMenuPermission
	 *            String
	 * @param inNeedsGroupAmin
	 *            boolean
	 * @param inNeedsRegistration
	 *            boolean
	 * @param inNeedsTypePrivate
	 *            boolean
	 * @param inGroupStates
	 *            String[]
	 * @param inMessages
	 *            {@link IMessages}
	 */
	public ContextMenuItem(final Class<? extends IPluggable> inTaskClass,
			final String inTitleMsg, final String inMenuPermission,
			final boolean inNeedsGroupAmin, final boolean inNeedsRegistration,
			final boolean inNeedsTypePrivate, final String[] inGroupStates,
			final IMessages inMessages) {
		taskClass = inTaskClass;
		titleMsg = inTitleMsg;
		menuPermission = inMenuPermission;
		needsGroupAmin = inNeedsGroupAmin;
		needsRegistration = inNeedsRegistration;
		needsTypePrivate = inNeedsTypePrivate;
		groupStates = inGroupStates;
		messages = inMessages;
	}

	public Class<? extends IPluggable> getTaskClass() {
		return taskClass;
	}

	@Override
	public String getTitleMsg() {
		return messages.getMessage(titleMsg);
	}

	@Override
	public String getMenuPermission() {
		return menuPermission;
	}

	public boolean needsGroupAmin() {
		return needsGroupAmin;
	}

	public boolean needsRegistration() {
		return needsRegistration;
	}

	public boolean needsTypePrivate() {
		return needsTypePrivate;
	}

	public String[] getGroupStates() {
		return groupStates;
	}

	@Override
	public Class<? extends IPluggable> getControllerClass() {
		return taskClass;
	}

	@Override
	public boolean checkConditions(final User inUser,
			final Authorization inAuthorization,
			final ParameterObject inParameters) {
		// TODO Auto-generated method stub
		return false;
	}

}
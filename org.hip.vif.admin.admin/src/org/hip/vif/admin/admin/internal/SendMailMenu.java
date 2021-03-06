/**
	This package is part of the application VIF.
	Copyright (C) 2012-2014, Benno Luthiger

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
package org.hip.vif.admin.admin.internal;

import java.util.Collections;
import java.util.List;

import org.hip.vif.admin.admin.Activator;
import org.hip.vif.admin.admin.Constants;
import org.hip.vif.admin.admin.tasks.SendMailTask;
import org.ripla.interfaces.IMenuItem;
import org.ripla.interfaces.IMessages;
import org.ripla.services.IExtendibleMenuContribution;
import org.ripla.util.ExtendibleMenuMarker;
import org.ripla.util.ExtendibleMenuMarker.Position;
import org.ripla.web.util.UseCaseHelper;

/**
 * The menu item <code>Send Mail</code>.
 * 
 * @author lbenno
 */
public class SendMailMenu implements IExtendibleMenuContribution {
	private static final IMessages MESSAGES = Activator.getMessages();

	@Override
	public String getLabel() {
		return MESSAGES.getMessage("admin.menu.send.mail"); //$NON-NLS-1$
	}

	@Override
	public String getControllerName() {
		return UseCaseHelper
				.createFullyQualifiedControllerName(SendMailTask.class);
	}

	@Override
	public List<IMenuItem> getSubMenu() {
		return Collections.emptyList();
	}

	@Override
	public String getPermission() {
		return Constants.PERMISSION_SEND_MAIL;
	}

	@Override
	public String getExtendibleMenuID() {
		return Constants.EXTENDIBLE_MENU_ID;
	}

	@Override
	public Position getPosition() {
		return new Position(ExtendibleMenuMarker.PositionType.APPEND,
				Constants.EXTENDIBLE_MENU_POSITION_START);
	}

}

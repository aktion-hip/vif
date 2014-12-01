/**
    This package is part of the persistency layer of the application VIF.
    Copyright (C) 2003-2014, Benno Luthiger

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
package org.hip.vif.forum.usersettings.internal;

import java.util.Collections;
import java.util.List;

import org.hip.vif.forum.usersettings.Activator;
import org.hip.vif.forum.usersettings.Constants;
import org.hip.vif.forum.usersettings.tasks.SubscriptionsManageTask;
import org.ripla.interfaces.IMenuItem;
import org.ripla.services.IExtendibleMenuContribution;
import org.ripla.util.ExtendibleMenuMarker.Position;
import org.ripla.util.ExtendibleMenuMarker.PositionType;
import org.ripla.web.util.UseCaseHelper;

/** The menu item <code>Manage Subscriptions</code>.
 *
 * @author lbenno */
public class ManageSubscriptionsMenu implements IExtendibleMenuContribution {

    @Override
    public String getLabel() {
        return Activator.getMessages().getMessage("usersettings.menu.subscription"); //$NON-NLS-1$
    }

    @Override
    public String getControllerName() {
        return UseCaseHelper.createFullyQualifiedControllerName(SubscriptionsManageTask.class);
    }

    @Override
    public List<IMenuItem> getSubMenu() {
        return Collections.emptyList();

    }

    @Override
    public String getPermission() {
        return Constants.PERMISSION_MANAGE_SUBSCRIPTIONS;
    }

    @Override
    public String getExtendibleMenuID() {
        return Constants.EXTENDIBLE_MENU_ID;
    }

    @Override
    public Position getPosition() {
        return new Position(PositionType.APPEND, Constants.EXTENDIBLE_MENU_POSITION_START);
    }

}

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

package org.hip.vif.admin.member.internal;

import org.hip.vif.admin.member.tasks.MemberSearchTask;
import org.ripla.interfaces.IControllerSet;
import org.ripla.interfaces.IMenuItem;
import org.ripla.web.interfaces.IMenuSet;
import org.ripla.web.services.IUseCase;
import org.ripla.web.util.UseCaseHelper;

/** This bundle's service instance for <code>IUseCaseAdmin</code>.
 *
 * @author Luthiger Created: 16.10.2011 */
public class UseCaseComponent implements IUseCase {

    @Override
    public IMenuItem getMenu() {
        return ComponentHelper.createMenu();
    }

    @Override
    public IMenuSet[] getContextMenus() {
        return new IMenuSet[] { HelperContextMenuAdminMembers.createContextMenuSet1(),
                HelperContextMenuAdminMembers.createContextMenuSet2() };
    }

    @Override
    public Package getControllerClasses() {
        return MemberSearchTask.class.getPackage();
    }

    @Override
    public IControllerSet getControllerSet() {
        return UseCaseHelper.EMPTY_CONTROLLER_SET;
    }

}

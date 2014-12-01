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

package org.hip.vif.forum.member.internal;

import org.hip.vif.forum.member.Activator;
import org.hip.vif.forum.member.Constants;
import org.hip.vif.forum.member.tasks.PersonalDataEditTask;
import org.hip.vif.forum.member.tasks.PwrdEditTask;
import org.ripla.interfaces.IControllerSet;
import org.ripla.interfaces.IMenuItem;
import org.ripla.interfaces.IMessages;
import org.ripla.menu.RiplaMenuComposite;
import org.ripla.web.interfaces.IContextMenuItem;
import org.ripla.web.interfaces.IMenuSet;
import org.ripla.web.menu.ContextMenuItem;
import org.ripla.web.services.IUseCase;
import org.ripla.web.util.UseCaseHelper;

/** This bundle's service provider for <code>IUseCaseForum</code>.
 *
 * @author Luthiger Created: 06.10.2011 */
public class UseCaseComponent implements IUseCase {
    private static final IMessages MESSAGES = Activator.getMessages();
    private static final IContextMenuItem EDIT_PWRD = new ContextMenuItem(PwrdEditTask.class,
            "context.menu.member.pwrd", //$NON-NLS-1$
            Constants.PERMISSION_EDIT_DATA, MESSAGES);
    private static final IContextMenuItem EDIT_DATA = new ContextMenuItem(PersonalDataEditTask.class,
            "component.menu.title", //$NON-NLS-1$
            Constants.PERMISSION_EDIT_DATA, MESSAGES);

    @Override
    public IMenuItem getMenu() {
        final RiplaMenuComposite outMenu = new RiplaMenuComposite(Activator.getMessages()
                .getMessage("component.menu.title"), 50); //$NON-NLS-1$
        outMenu.setControllerName(UseCaseHelper.createFullyQualifiedControllerName(PersonalDataEditTask.class));
        outMenu.setPermission(Constants.PERMISSION_EDIT_DATA);

        final RiplaMenuComposite lSubMenu = new RiplaMenuComposite(Activator.getMessages().getMessage(
                "context.menu.member.pwrd"), 10); //$NON-NLS-1$
        lSubMenu.setControllerName(UseCaseHelper.createFullyQualifiedControllerName(PwrdEditTask.class));
        lSubMenu.setPermission(Constants.PERMISSION_EDIT_DATA);

        outMenu.add(lSubMenu);
        outMenu.setTag("vif.forum.menu");
        return outMenu;
    }

    @Override
    public Package getControllerClasses() {
        return PersonalDataEditTask.class.getPackage();
    }

    @Override
    public IControllerSet getControllerSet() {
        return UseCaseHelper.EMPTY_CONTROLLER_SET;
    }

    @Override
    public IMenuSet[] getContextMenus() {
        return new IMenuSet[] { new IMenuSet() {
            @Override
            public String getSetID() {
                return Constants.MENU_SET_ID_EDIT_DATA;
            }

            @Override
            public IContextMenuItem[] getContextMenuItems() {
                return new IContextMenuItem[] { EDIT_DATA };
            }
        },
                new IMenuSet() {
                    @Override
                    public String getSetID() {
                        return Constants.MENU_SET_ID_EDIT_PWRD;
                    }

                    @Override
                    public IContextMenuItem[] getContextMenuItems() {
                        return new IContextMenuItem[] { EDIT_PWRD };
                    }
                } };
    }

}

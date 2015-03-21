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

package org.hip.vif.web.tasks;

import org.hip.vif.web.Activator;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.User;
import org.ripla.exceptions.RiplaException;
import org.ripla.util.ParameterObject;
import org.ripla.web.interfaces.IContextMenuItem;
import org.ripla.web.interfaces.IPluggable;
import org.ripla.web.util.ControllerStack;

import com.vaadin.ui.Component;

/** Reruns the last task, thus, mimicking the back button.
 *
 * @author Luthiger Created: 14.07.2011 */
public class BackTask extends AbstractWebController { // NOPMD

    @Override
    protected Component runChecked() throws RiplaException { // NOPMD
        final ControllerStack controllers = ControllerStack
                .getControllerStack();
        controllers.pop();
        controllers.pop();
        return controllers.peek().run();
    }

    @Override
    protected String needsPermission() { // NOPMD
        return ""; //$NON-NLS-1$
    }

    // ---

    /** Implementation of <code>IContextMenuItem</code> for the back task. */
    public static class ContextMenuItemBack implements IContextMenuItem {
        private final transient Class<? extends IPluggable> backClass;

        /** @param inClass the bundle's implementation of the back task class. */
        public ContextMenuItemBack(final Class<? extends IPluggable> inClass) {
            backClass = inClass;
        }

        @Override
        public Class<? extends IPluggable> getControllerClass() { // NOPMD
            return backClass;
        }

        @Override
        public String getTitleMsg() { // NOPMD
            return Activator.getMessages().getMessage("context.menu.item.back"); //$NON-NLS-1$
        }

        @Override
        public String getMenuPermission() { // NOPMD
            return ""; //$NON-NLS-1$
        }

        @Override
        public boolean checkConditions(final User inUser, // NOPMD
                final Authorization inAuthorization,
                final ParameterObject inParameters) {
            return true;
        }
    }

}

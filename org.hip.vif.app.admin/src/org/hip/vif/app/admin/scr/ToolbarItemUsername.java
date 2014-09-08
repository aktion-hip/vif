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
package org.hip.vif.app.admin.scr;

import org.hip.vif.app.admin.Activator;
import org.osgi.service.useradmin.User;
import org.ripla.web.RiplaApplication;
import org.ripla.web.interfaces.IToolbarActionListener;
import org.ripla.web.interfaces.IToolbarItemCreator;
import org.ripla.web.services.IToolbarItem;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/** A provider for the <code>IToolbarItem</code> service. This toolbar item displays the user's name.
 *
 * @author Luthiger */
public class ToolbarItemUsername implements IToolbarItem {

    @Override
    public Component getComponent() {
        return null;
    }

    @Override
    public IToolbarItemCreator getCreator() {
        return new IToolbarItemCreator() {
            @Override
            public Component createToolbarItem(
                    final RiplaApplication inApplication, final User inUser) {
                if (inUser == null) {
                    return null;
                }

                final HorizontalLayout out = new HorizontalLayout();
                out.setSizeUndefined();
                out.addComponent(new Label(Activator.getMessages()
                        .getFormattedMessage("toolbar.username.label",
                                inUser.getName())));
                return out;
            }
        };
    }

    @Override
    public int getPosition() {
        return 5;
    }

    @Override
    public void registerToolbarActionListener(
            final IToolbarActionListener inListener) {
        // this item has no action
    }

}

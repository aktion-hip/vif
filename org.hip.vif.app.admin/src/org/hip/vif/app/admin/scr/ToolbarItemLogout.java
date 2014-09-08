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

import java.util.HashMap;

import org.hip.vif.app.admin.Activator;
import org.osgi.service.useradmin.User;
import org.ripla.interfaces.IRiplaEventDispatcher;
import org.ripla.web.RiplaApplication;
import org.ripla.web.interfaces.IToolbarAction;
import org.ripla.web.interfaces.IToolbarActionListener;
import org.ripla.web.interfaces.IToolbarItemCreator;
import org.ripla.web.services.IToolbarItem;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.BaseTheme;

/** A provider for the <code>IToolbarItem</code> service. This toolbar item displays the logout link.
 *
 * @author Luthiger */
public class ToolbarItemLogout implements IToolbarItem {

    private transient IToolbarActionListener listener;

    @SuppressWarnings("serial")
    @Override
    public Component getComponent() {
        final HorizontalLayout out = new HorizontalLayout();
        out.setSizeUndefined();
        final Button lLogout = new Button(Activator.getMessages().getMessage(
                "toolbar.logout.label"));
        lLogout.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                if (inEvent.getButton() != lLogout) {
                    return;
                }

                if (listener != null) {
                    listener.processAction(new IToolbarAction() {
                        @Override
                        public void run() {
                            VaadinSession
                                    .getCurrent()
                                    .getAttribute(IRiplaEventDispatcher.class)
                                    .dispatch(
                                            IRiplaEventDispatcher.Event.LOGOUT,
                                            new HashMap<String, Object>());
                        }
                    });
                }
            }
        });
        lLogout.setStyleName(BaseTheme.BUTTON_LINK);
        out.addComponent(lLogout);
        return out;
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
                return getComponent();
            }
        };
    }

    @Override
    public int getPosition() {
        return 4;
    }

    @Override
    public void registerToolbarActionListener(
            final IToolbarActionListener inListener) {
        listener = inListener;
    }

}

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

package org.hip.vif.skin.green;

import org.hip.vif.core.ApplicationConstants;
import org.ripla.web.services.ISkin;
import org.ripla.web.util.FooterHelper;

import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/** Skin implementation.
 *
 * @author Luthiger Created: 03.01.2012 */
public class Skin implements ISkin {

    public String getWelcomeForum() {
        return Activator.getMessages().getMessage(
                "LoginViewInformation.welcome.forum");
    }

    public String getWelcomeAdmin() {
        return Activator.getMessages().getMessage(
                "LoginViewInformation.welcome.admin");
    }

    @Override
    public boolean hasHeader() {
        return true;
    }

    @Override
    public Component getHeader(final String inAppName) {
        final HorizontalLayout outLayout = new HorizontalLayout();
        outLayout.setWidth("100%");
        outLayout.setHeight(80, Unit.PIXELS);
        outLayout.setStyleName("vif-green-head");

        final Embedded lImage = new Embedded();
        lImage.setSource(new ThemeResource("images/head.jpg"));
        outLayout.addComponent(lImage);
        outLayout.setComponentAlignment(lImage, Alignment.TOP_LEFT);

        final Label lTitle = new Label("VIF - Virtual Discussion Forum");
        lTitle.setStyleName("vif-head-title");
        lTitle.setWidth(700, Unit.PIXELS);
        outLayout.addComponent(lTitle);

        return outLayout;
    }

    @Override
    public boolean hasFooter() {
        return true;
    }

    @Override
    public Component getFooter() {
        final FooterHelper out = FooterHelper
                .createFooter(ApplicationConstants.FOOTER_TEXT);
        out.setHeight(19);
        out.setStyleName("vif-footer");
        return out;
    }

    @Override
    public boolean hasToolBar() {
        return true;
    }

    @Override
    public Label getToolbarSeparator() {
        final Label outSeparator = new Label("&bull;", ContentMode.HTML); //$NON-NLS-1$
        outSeparator.setWidth(6, Unit.PIXELS);
        return outSeparator;
    }

    @Override
    public boolean hasMenuBar() {
        return true;
    }

    @Override
    public HorizontalLayout getMenuBarMedium() {
        return null;
    }

    @Override
    public HorizontalLayout getMenuBar() {
        return null;
    }

    @Override
    public Resource getSubMenuIcon() {
        return null;
    }

}

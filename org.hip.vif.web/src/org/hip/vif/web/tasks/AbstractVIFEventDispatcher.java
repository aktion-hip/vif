/**
    This package is part of the application VIF.
    Copyright (C) 2003-2015, Benno Luthiger

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

import java.util.Map;

import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.bom.Text;
import org.hip.vif.web.Activator;
import org.hip.vif.web.Constants;
import org.hip.vif.web.controller.LookupManager;
import org.hip.vif.web.interfaces.ILookupWindow;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.hip.vif.web.util.VIFAppHelper;
import org.ripla.exceptions.NoControllerFoundException;
import org.ripla.util.ParameterObject;
import org.ripla.web.util.Popup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/** Abstract event dispatcher implementation, provides the functionality for the lookup event.
 *
 * @author lbenno */
public abstract class AbstractVIFEventDispatcher { // NOPMD by lbenno
    public static final Logger LOG = LoggerFactory.getLogger(AbstractVIFEventDispatcher.class);

    private static final String PARAMETER_KEY_ID = "key_id";

    /** Calls the lookup window of the specified type.
     *
     * @param inType {@link LookupType}
     * @param inId String the ID of the entry to display
     * @param inProperties Map<String, Object> additional properties
     * @param inController {@link AbstractWebController} the controller for the view of the content to display in the
     *            lookup */
    protected void doLookup(final LookupType inType, final String inId, final Map<String, Object> inProperties, // NOPMD
            final AbstractWebController inController) {
        ILookupWindow lLookup = null;
        switch (inType) {
        case BIBLIOGRAPHY:
            lLookup = LookupManager.INSTANCE.getLookup(LookupType.BIBLIOGRAPHY);
            if (lLookup == null) {
                break;
            }
            if (inId.contains(Text.DELIMITER_ID_VERSION)) {
                final String[] lTextID = inId.split(Text.DELIMITER_ID_VERSION);
                VIFAppHelper.setValueToSession(Constants.TEXT_ID_KEY, Long.parseLong(lTextID[0]));
                VIFAppHelper.setValueToSession(Constants.TEXT_VERSION_ID_KEY, Long.parseLong(lTextID[1]));
            }
            else {
                VIFAppHelper.setValueToSession(Constants.TEXT_ID_KEY, Long.parseLong(inId));
                VIFAppHelper.setValueToSession(Constants.TEXT_VERSION_ID_KEY, -1l);
            }
            displayLookup(lLookup, "lookup.window.title.bibliography", inController); //$NON-NLS-1$
            break;
        case CONTENT:
            lLookup = LookupManager.INSTANCE.getLookup(LookupType.CONTENT);
            if (lLookup == null) {
                break;
            }
            inController.setUseCaseParameter(createModelIdParameter(inId));
            displayLookup(lLookup, "lookup.window.title.content", inController); //$NON-NLS-1$
            break;
        case MEMBER:
            lLookup = LookupManager.INSTANCE.getLookup(LookupType.MEMBER);
            if (lLookup == null) {
                break;
            }
            inController.setUseCaseParameter(createModelIdParameter(inId));
            displayLookup(lLookup, "lookup.window.title.member", inController); //$NON-NLS-1$
            break;
        case MEMBER_SEARCH:
            lLookup = LookupManager.INSTANCE.getLookup(LookupType.MEMBER_SEARCH);
            if (lLookup == null) {
                break;
            }
            displayLookup(lLookup, "lookup.window.title.select", inController); //$NON-NLS-1$
            break;
        case MEMBER_SELECT:
            lLookup = LookupManager.INSTANCE.getLookup(LookupType.MEMBER_SELECT);
            if (lLookup == null) {
                break;
            }
            displayLookup(lLookup, "lookup.window.title.select", inController); //$NON-NLS-1$
            break;
        default:
        }
    }

    private void displayLookup(final ILookupWindow inLookup, final String inWindowTitleKey,
            final AbstractWebController inController) {
        final VerticalLayout lLayout = new VerticalLayout();
        Component lComponent;
        try {
            lComponent = inController.sendLookupTo(inLookup.getControllerName());
        } catch (final NoControllerFoundException exc) {
            LOG.error("Configuration error:", exc); //$NON-NLS-1$
            lComponent = new DefaultVIFView(exc);
        }
        lLayout.addComponent(lComponent);
        Popup.displayPopup(Activator.getMessages().getMessage(inWindowTitleKey), lLayout, inLookup.getWidth(),
                inLookup.getHeight());
    }

    private ParameterObject createModelIdParameter(final String inId) {
        final ParameterObject out = new ParameterObject();
        out.set(ApplicationConstants.PARAMETER_KEY_GENERIC, PARAMETER_KEY_ID);
        out.set(PARAMETER_KEY_ID, inId);
        return out;
    }

}

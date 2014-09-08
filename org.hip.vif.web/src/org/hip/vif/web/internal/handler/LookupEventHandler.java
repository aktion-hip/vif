/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

package org.hip.vif.web.internal.handler;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.exc.NoTaskFoundException;
import org.hip.vif.core.service.ApplicationData;
import org.hip.vif.web.Activator;
import org.hip.vif.web.Constants;
import org.hip.vif.web.controller.LookupManager;
import org.hip.vif.web.controller.TaskManager;
import org.hip.vif.web.interfaces.ILookupWindow;
import org.hip.vif.web.tasks.DefaultVIFView;
import org.hip.vif.web.util.LinkButtonHelper;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.ripla.util.ParameterObject;
import org.ripla.web.util.Popup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/** Implementation class of the event handler service.<br/>
 * This event handler is registered for the <code>event.topics</code> <i>org/hip/vif/web/LookupEvent/LOOKUP</i>. (see
 * <code>OSGI-INF/eventHandlerLookup.xml</code>)
 *
 * @author Luthiger Created: 16.11.2011 */
@Deprecated
public class LookupEventHandler implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(LookupEventHandler.class);

    private static final String PARAMETER_KEY_ID = "key_id"; //$NON-NLS-1$

    @Override
    public void handleEvent(final Event inEvent) {
        LOG.debug("Lookup event {}.", inEvent.getProperty(Constants.EVENT_PROPERTY_LOOKUP_TYPE)); //$NON-NLS-1$

        ILookupWindow lLookup = null;
        final LinkButtonHelper.LookupType lType = (LookupType) inEvent
                .getProperty(Constants.EVENT_PROPERTY_LOOKUP_TYPE);
        switch (lType) {
        case BIBLIOGRAPHY:
            lLookup = LookupManager.INSTANCE.getLookup(LookupType.BIBLIOGRAPHY);
            if (lLookup == null)
                break;

            final String lID = inEvent.getProperty(Constants.EVENT_PROPERTY_LOOKUP_ID).toString();
            if (lID.contains(Text.DELIMITER_ID_VERSION)) {
                final String[] lTextID = lID.split(Text.DELIMITER_ID_VERSION);
                ApplicationData.setTextID(Long.parseLong(lTextID[0]));
                ApplicationData.setTextVersion(lTextID[1]);
            }
            else {
                ApplicationData.setTextID(Long.parseLong(lID));
            }
            displayLookup(lLookup, "lookup.window.title.bibliography"); //$NON-NLS-1$
            break;

        case CONTENT:
            lLookup = LookupManager.INSTANCE.getLookup(LookupType.CONTENT);
            if (lLookup == null)
                break;

            ApplicationData.setQuestionID(Long.parseLong(inEvent.getProperty(Constants.EVENT_PROPERTY_LOOKUP_ID)
                    .toString()));
            displayLookup(lLookup, "lookup.window.title.content"); //$NON-NLS-1$
            break;
        case MEMBER:
            lLookup = LookupManager.INSTANCE.getLookup(LookupType.MEMBER);
            if (lLookup == null)
                break;

            try {
                final ParameterObject lParameters = new ParameterObject();
                lParameters.set(ApplicationConstants.PARAMETER_KEY_GENERIC, PARAMETER_KEY_ID);
                lParameters.set(PARAMETER_KEY_ID, inEvent.getProperty(Constants.EVENT_PROPERTY_LOOKUP_ID));
                ApplicationData.setParameters(lParameters);
                displayLookup(lLookup, "lookup.window.title.member"); //$NON-NLS-1$
            } catch (final VException exc) {
                LOG.error("Error while looking up member record.", exc); //$NON-NLS-1$
            }
            break;
        case MEMBER_SEARCH:
            lLookup = LookupManager.INSTANCE.getLookup(LookupType.MEMBER_SEARCH);
            if (lLookup == null)
                break;

            displayLookup(lLookup, "lookup.window.title.select"); //$NON-NLS-1$
            break;
        case MEMBER_SELECT:
            lLookup = LookupManager.INSTANCE.getLookup(LookupType.MEMBER_SELECT);
            if (lLookup == null)
                break;

            displayLookup(lLookup, "lookup.window.title.select"); //$NON-NLS-1$
            break;
        }
    }

    private void displayLookup(final ILookupWindow inLookup, final String inWindowTitleKey) {
        Popup.displayPopup(Activator.getMessages().getMessage(inWindowTitleKey), inLayout, inLookup.getWidth(),
                inLookup.getHeight());

        final LookupWindow lLookup = new LookupWindow(inLookup, Activator.getMessages().getMessage(inWindowTitleKey));
        if (lLookup.getParent() == null) {
            ApplicationData.getWindow().addWindow(lLookup.getLookupWindow());
        }
        lLookup.setPosition(50, 50);
    }

    // ---

    @SuppressWarnings("serial")
    private class LookupWindow extends VerticalLayout {
        Window lookupWindow;

        LookupWindow(final ILookupWindow inLookup, final String inCaption) {
            setSpacing(true);
            lookupWindow = new Window(inCaption);
            lookupWindow.setWidth(inLookup.getWidth(), UNITS_PIXELS);
            lookupWindow.setHeight(inLookup.getHeight(), UNITS_PIXELS);

            final VerticalLayout lLayout = (VerticalLayout) lookupWindow.getContent();
            lLayout.setStyleName("vif-lookup"); //$NON-NLS-1$
            lLayout.setMargin(true);
            lLayout.setSpacing(true);
            lLayout.setSizeFull();

            try {
                lLayout.addComponent(inLookup.isForum() ?
                        TaskManager.INSTANCE.getForumContent(inLookup.getControllerName()) :
                            TaskManager.INSTANCE.getAdminContent(inLookup.getControllerName()));
            } catch (final NoTaskFoundException exc) {
                LOG.error("Configuration error:", exc); //$NON-NLS-1$
                lLayout.addComponent(new DefaultVIFView(exc));
            }

        }

        Window getLookupWindow() {
            return lookupWindow;
        }

        void setPosition(final int inPositionX, final int inPositionY) {
            lookupWindow.setPositionX(inPositionX);
            lookupWindow.setPositionX(inPositionY);
        }
    }

}

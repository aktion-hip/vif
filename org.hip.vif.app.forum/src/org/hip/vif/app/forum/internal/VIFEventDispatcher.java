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
package org.hip.vif.app.forum.internal;

import java.util.Map;

import org.hip.vif.web.interfaces.IVIFEventDispatcher;
import org.hip.vif.web.tasks.AbstractVIFEventDispatcher;
import org.hip.vif.web.tasks.AbstractWebController;
import org.hip.vif.web.util.LinkButtonHelper;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;

/** The event dispatcher for the forum part.
 *
 * @author lbenno */
public class VIFEventDispatcher extends AbstractVIFEventDispatcher implements IVIFEventDispatcher { // NOPMD

    @Override
    public void dispatch(final Event inType, final Map<String, Object> inProperties) { // NOPMD
        if (Event.LOOKUP.equals(inType)) {
            final LinkButtonHelper.LookupType lType = (LookupType) inProperties
                    .get(AbstractWebController.EVENT_PROPERTY_LOOKUP_TYPE);
            LOG.debug("Lookup event {}.", lType);
            final AbstractWebController lController = (AbstractWebController) inProperties
                    .get(AbstractWebController.EVENT_PROPERTY_LOOKUP_CONTROLLER);
            doLookup(lType, inProperties.get(AbstractWebController.EVENT_PROPERTY_LOOKUP_ID).toString(), inProperties,
                    lController);
        }
    }

}

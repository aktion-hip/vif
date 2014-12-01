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
package org.hip.vif.web.components;

import org.ripla.web.RiplaApplication;
import org.ripla.web.controllers.RiplaBody;
import org.ripla.web.interfaces.IBodyComponent;
import org.ripla.web.services.ISkin;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

/** The body view for the VIF application. We need this body version to override the toolbar height.
 *
 * @author lbenno */
@SuppressWarnings("serial")
public class VIFBody extends RiplaBody {

    /** VIFBody constructor.
     *
     * @param inSkin {@link ISkin}
     * @param inApplication {@link RiplaApplication}
     * @param inMenuTagFilter String */
    protected VIFBody(final ISkin inSkin, final RiplaApplication inApplication, final String inMenuTagFilter) {
        super(inSkin, inApplication, inMenuTagFilter);
    }

    @Override
    protected Component createToolbar(final Label inSeparator) {
        final Component out = super.createToolbar(inSeparator);
        out.setHeight(23, Unit.PIXELS);
        return out;
    }

    /** Creates an instance of the VIF body.
     *
     * @param inSkin {@link ISkin}
     * @param inApplication {@link RiplaApplication}
     * @param inMenuTagFilter String
     * @return {@link IBodyComponent} */
    public static IBodyComponent createVIFInstance(final ISkin inSkin,
            final RiplaApplication inApplication, final String inMenuTagFilter) {
        final VIFBody outBody = new VIFBody(inSkin, inApplication, inMenuTagFilter);
        outBody.initializeLayout();
        return outBody;
    }

}

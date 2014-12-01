/**
 This package is part of the administration of the application VIF.
 Copyright (C) 2008-2014, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.admin.admin.tasks;

import org.hip.vif.admin.admin.Constants;
import org.hip.vif.admin.admin.data.SkinBean;
import org.hip.vif.admin.admin.ui.SkinSelectView;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;

import com.vaadin.ui.Component;

/** Taks to change the application's skin.
 *
 * @author Luthiger Created: 29.12.2007 */
@UseCaseController
public class SkinSelectTask extends AbstractWebController {

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_SELECT_SKIN;
    }

    @Override
    protected Component runChecked() throws RiplaException {
        emptyContextMenu();
        return new SkinSelectView(this);
    }

    /** Callback method to save the selected skin.
     *
     * @param inSkin {@link SkinBean} */
    public void save(final SkinBean inSkin) {
        changeSkin(inSkin.getSkinID());
        logout();
    }

}

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

package org.hip.vif.forum.groups.internal;

import org.hip.vif.forum.groups.tasks.QustionLookupTask;
import org.hip.vif.web.interfaces.ILookupWindow;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;

/** @author Luthiger Created: 29.12.2011 */
public class LookupContent implements ILookupWindow {

    @Override
    public int getWidth() {
        return 700;
    }

    @Override
    public int getHeight() {
        return 700;
    }

    @Override
    public String getControllerName() {
        return org.ripla.web.util.UseCaseHelper.createFullyQualifiedControllerName(QustionLookupTask.class);
    }

    @Override
    public boolean isForum() {
        return true;
    }

    @Override
    public LookupType getType() {
        return LookupType.CONTENT;
    }

}

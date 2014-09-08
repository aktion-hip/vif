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

package org.hip.vif.web.interfaces;

import org.hip.vif.web.util.LinkButtonHelper;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.ripla.web.interfaces.IPluggable;

/** Interface for the lookup window service.
 *
 * @author Luthiger Created: 10.06.2011 */
public interface ILookupWindow {

    /** The lookup window's preferred width.
     *
     * @return int height in px */
    int getWidth();

    /** The lookup window's preferred height.
     *
     * @return int height in px */
    int getHeight();

    /** The name of the task that controls the lookup's view. This class must implement {@link IPluggable}.
     *
     * @return String the task's fully qualified name, i.e. <code>org.hip.vif.mybundle/mytask</code> */
    String getControllerName();

    /** Tells which part provides the lookup task.
     *
     * @return boolean <code>true</code> is lookup task is provided by the forum part, <code>false</code> if task is
     *         provided by admin part. */
    boolean isForum();

    /** The type of lookup the component provides.
     *
     * @return {@link LookupType} */
    LinkButtonHelper.LookupType getType();

}

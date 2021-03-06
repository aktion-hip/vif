/**
	This package is part of the application VIF.
	Copyright (C) 2012-2015, Benno Luthiger

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

package org.hip.vif.core.upgrade;

import org.hip.vif.core.interfaces.IVIFUpgrade;
import org.hip.vif.core.interfaces.IVIFUpgradeService;

/** The upgrade provider to upgrade the application to version 1.1.
 *
 * @author Luthiger Created: 16.02.2012 */
public class Upgrade11 implements IVIFUpgradeService {
    public static final String VERSION = "1.1";

    @Override
    public IVIFUpgrade[] getUpgraders() {
        return new IVIFUpgrade[] { new UpgradeStyledContent(), new UpgradeDB10() };
    }

}

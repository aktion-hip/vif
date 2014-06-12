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

import org.ripla.services.ISkin;
import org.ripla.services.ISkinService;

/**
 * The service to create a demo skin.
 * 
 * @author Luthiger
 */
public class SkinService implements ISkinService {
	public static final String SKIN_ID = "org.hip.vif.green";

	@Override
	public String getSkinID() {
		return SKIN_ID;
	}

	@Override
	public String getSkinName() {
		return "VIF Skin Green";
	}

	@Override
	public ISkin createSkin() {
		return new Skin();
	}

}

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

package org.hip.vif.web.util;

import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.web.interfaces.ISkin;

/**
 * Singleton registery for the application's skins.
 * 
 * @author Luthiger
 * Created: 02.01.2012
 */
public enum SkinRegistry {
	INSTANCE;
	
	private Collection<ISkin> skins = new Vector<ISkin>();
	private ISkin activeSkin = null;
	
	/**
	 * @param inSkin {@link ISkin} the skin to register
	 */
	public void registerSkin(ISkin inSkin) {
		skins.add(inSkin);
	}
	
	/**
	 * @param inSkin {@link ISkin} the skin to unregister
	 */
	public void unregisterSkin(ISkin inSkin) {
		skins.remove(inSkin);
	}
	
	/**
	 * @return Collection of {@link ISkin} the registered skins
	 */
	public Collection<ISkin> getSkins() {
		return skins;
	}
	
	/**
	 * Returns the active skin.
	 * 
	 * @return {@link ISkin}
	 * @throws VException
	 */
	public ISkin getActiveSkin() throws VException {
		if (activeSkin == null) {
			if (skins.isEmpty()) {
				throw new VException("Skin configuration error!"); //$NON-NLS-1$
			}
			activeSkin = calculateSkin(PreferencesHandler.INSTANCE.getActiveSkinID());
		}
		return activeSkin;
	}
	
	private ISkin calculateSkin(String inSkinID) {
		for (ISkin lSkin : skins) {
			if (lSkin.getSkinID().equals(inSkinID)) {
				return lSkin;
			}
		}
		return skins.iterator().next();
	}

	/**
	 * Notify the registry about the new skin selection.
	 * 
	 * @param inSkinID String the new skin's id
	 */
	public void changeSkin(String inSkinID) {
		PreferencesHandler.INSTANCE.set(PreferencesHandler.KEY_SKIN, inSkinID);
		activeSkin = calculateSkin(inSkinID);
	}
	
}

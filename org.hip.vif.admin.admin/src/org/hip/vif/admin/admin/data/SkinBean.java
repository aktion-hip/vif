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

package org.hip.vif.admin.admin.data;

import org.ripla.services.ISkinService;

/**
 * Bean for an <code>ISkinService</code> instance.
 * 
 * @author Luthiger Created: 03.01.2012
 */
public class SkinBean implements Comparable<SkinBean> {
	private final String skinID;
	private final String name;

	private SkinBean(final ISkinService inSkin) {
		skinID = inSkin.getSkinID();
		name = inSkin.getSkinName();
	}

	/**
	 * Factory method.
	 * 
	 * @param inSkin
	 *            {@link ISkinService}
	 * @return {@link SkinBean}
	 */
	public static SkinBean createItem(final ISkinService inSkin) {
		return new SkinBean(inSkin);
	}

	public String getSkinID() {
		return skinID;
	}

	public String getSkinName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(final SkinBean inSkin) {
		return getSkinName().compareTo(inSkin.getSkinName());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((skinID == null) ? 0 : skinID.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SkinBean other = (SkinBean) obj;
		if (skinID == null) {
			if (other.skinID != null)
				return false;
		} else if (!skinID.equals(other.skinID))
			return false;
		return true;
	}

}

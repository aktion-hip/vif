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

import java.util.Collection;

import org.ripla.services.ISkinService;

import com.vaadin.data.util.BeanItemContainer;

/**
 * View model for the selection of skin's.
 * 
 * @author Luthiger Created: 03.01.2012
 */
@SuppressWarnings("serial")
public class SkinContainer extends BeanItemContainer<SkinBean> {

	private static SkinBean activeSkin;

	private SkinContainer() {
		super(SkinBean.class);
	}

	/**
	 * Factory method to create an instance of <code>SkinContainer</code>.
	 * 
	 * @param inSkins
	 *            Collection of {@link ISkinService}
	 * @param inActiveSkinID
	 *            String
	 * @return {@link SkinContainer}
	 */
	public static SkinContainer createData(
			final Collection<ISkinService> inSkins, final String inActiveSkinID) {
		final SkinContainer out = new SkinContainer();
		for (final ISkinService lSkin : inSkins) {
			final SkinBean lBean = SkinBean.createItem(lSkin);
			out.addItem(lBean);
			if (inActiveSkinID.equals(lBean.getSkinID())) {
				activeSkin = lBean;
			}
		}
		out.sort(new Object[] { "skinName" }, new boolean[] { true }); //$NON-NLS-1$
		return out;
	}

	/**
	 * @return {@link SkinBean} the active skin when the view is created
	 */
	public SkinBean getActiveSkin() {
		return activeSkin;
	}

}

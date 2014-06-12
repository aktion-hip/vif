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

import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.web.Activator;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;

/**
 * Enum to render an embedded resource (gif) as rating value.
 * 
 * @author Luthiger
 * Created: 27.12.2011
 */
public enum RatingValue {
	GOOD("1", "green.gif", "ratings.table.value.good"),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	AVERAGE("0", "yellow.gif", "ratings.table.value.average"),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	BAD("-1", "red.gif", "ratings.table.value.bad"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private static final String TMPL_IMG = "./images/%s"; //$NON-NLS-1$
	private static IMessages messages = Activator.getMessages();
	
	private String value;
	private String img;
	private String msgKey;
	
	RatingValue(String inValue, String inImg, String inMsgKey) {
		value = inValue;
		img = inImg;
		msgKey = inMsgKey;
	}
	
	/**
	 * @return {@link Component} the rating value as embedded gif.
	 */
	public Component render() {
		Embedded out = new Embedded(null, new ThemeResource(String.format(TMPL_IMG, img)));
		out.setStyleName("vif-rating-value"); //$NON-NLS-1$
		out.setDescription(messages.getMessage(msgKey));
		return out;
	}
	
	/**
	 * @param inCompare String
	 * @return boolean <code>true</code> if this rating's value equals the passed value
	 */
	public boolean check(String inCompare) {
		return value.equals(inCompare);
	}

}

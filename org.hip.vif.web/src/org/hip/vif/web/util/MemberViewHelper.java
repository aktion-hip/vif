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

import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.web.Activator;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Select;

/**
 * Helper class for member views.
 * 
 * @author Luthiger
 * Created: 20.10.2011
 */
public class MemberViewHelper {

	/**
	 * Creates the drop down to select the member's address (i.e. 'Mr','Mrs') 
	 * 
	 * @param inMember {@link Member}
	 * @return {@link Select}
	 */
	public static Select getMemberAddress(Member inMember) {
		IMessages lMessages = Activator.getMessages();
		Select out = new Select();
		addItem(out, -1, "-"); //$NON-NLS-1$
		addItem(out, 0, lMessages.getMessage("ui.member.editor.select.male")); //$NON-NLS-1$
		addItem(out, 1, lMessages.getMessage("ui.member.editor.select.female")); //$NON-NLS-1$
		out.select(BeanWrapperHelper.getInteger(MemberHome.KEY_SEX, inMember));
		out.setStyleName("vif-input"); //$NON-NLS-1$
		out.setWidth(80, Sizeable.UNITS_PIXELS);
		out.setNullSelectionAllowed(false);
		return out;
	}
	
	/**
	 * Returns the correct address for the specified member.
	 * 
	 * @param inMember {@link Member}
	 * @return String
	 */
	public static String getMemberAddressLabel(Member inMember) {
		IMessages lMessages = Activator.getMessages();
		Integer lGender = BeanWrapperHelper.getInteger(MemberHome.KEY_SEX, inMember);
		switch (lGender) {
		case 1:
			return lMessages.getMessage("ui.member.editor.select.female"); //$NON-NLS-1$
		case 0:
			return lMessages.getMessage("ui.member.editor.select.male"); //$NON-NLS-1$
		}
		return "-"; //$NON-NLS-1$
	}
	
	private static void addItem(Select inSelect, int inValue, String inCaption) {
		inSelect.addItem(inValue);
		inSelect.setItemCaption(inValue, inCaption);
	}
	
}
/**
	This package is part of the application VIF.
	Copyright (C) 2012-2014, Benno Luthiger

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
package org.hip.vif.web.biblio;

import org.hip.vif.web.Activator;
import org.ripla.interfaces.IMessages;

/**
 * The VIF text types.
 * 
 * @author lbenno
 */
public enum TextType {
	BOOK(0, "org.hip.vif.bibliography.book"), ARTICLE(1,
			"org.hip.vif.bibliography.article"), CONTRIBUTION(2,
			"org.hip.vif.bibliography.contribution"), WEBPAGE(3,
			"org.hip.vif.bibliography.webpage");

	private static final String TMPL_OPTION = "<option value=\"%s\"%s>%s</option>";

	private static final IMessages MSG = Activator.getMessages();
	private int typeValue;
	private String key;

	/**
	 * TextType constructor.
	 * 
	 * @param inValue
	 *            int the DB field's value
	 * @param inKey
	 *            String the message key for localication
	 */
	TextType(final int inValue, final String inKey) {
		typeValue = inValue;
		key = inKey;
	}

	/**
	 * @return int this type's value (i.e. to be saved in the DB)
	 */
	public int getTypeValue() {
		return typeValue;
	}

	/**
	 * @return String the type's localized label
	 */
	public String getLabel() {
		return MSG.getMessage(key);
	}

	/**
	 * Check the specified value with this type's value.
	 * 
	 * @param inValue
	 *            an Integer object
	 * @return boolean <code>true</code> if the specified object equals this
	 *         text type's value
	 */
	public boolean checkType(final Object inValue) {
		try {
			return Integer.parseInt(inValue.toString()) == typeValue;
		}
		catch (final NumberFormatException exc) {
			return false;
		}
	}

	/**
	 * For html selection.
	 * 
	 * @param isSelected
	 *            whether this type should rendered as selected
	 * @return String renders this type as html selection option
	 */
	public String renderHtml(final boolean isSelected) {
		return String
				.format(TMPL_OPTION, typeValue,
						isSelected ? " selected=\"selected\"" : "",
						MSG.getMessage(key));
	}

	/**
	 * Returns the localized label for the specified text type.
	 * 
	 * @param inType
	 *            Object the text type value
	 * @return String the specified type's label
	 */
	public static String getOption(final Object inType) {
		for (final TextType lTextType : TextType.values()) {
			if (lTextType.checkType(inType)) {
				return lTextType.getLabel();
			}
		}
		return TextType.ARTICLE.getLabel();
	}

}

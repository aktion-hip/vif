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

package org.hip.vif.web.util;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.ComboBox;

/**
 * Helper class to create a select of registered member authenticators and
 * searchers.
 * 
 * @author Luthiger Created: 24.02.2012
 */
public class ConfigurableSelect {

	/**
	 * Create the <code>ComboBox</code> widget containing the registered member
	 * authenticators/searchers.
	 * 
	 * @param inItems
	 *            String[] the bundle names of registered
	 *            authenticators/searchers
	 * @param inProperty
	 *            {@link Property} the selected item
	 * @param inWidth
	 *            int the widget width
	 * @return {@link ComboBox}
	 */
	@SuppressWarnings("serial")
	public static ComboBox getSelect(final String[] inItems,
			final Property<String> inProperty, final int inWidth) {
		final ComboBox outSelect = createSelect(inItems);
		outSelect.select(inProperty.getValue().toString());
		outSelect.setStyleName("vif-select"); //$NON-NLS-1$
		outSelect.setWidth(inWidth, Unit.PIXELS);
		outSelect.setNullSelectionAllowed(false);
		outSelect.setImmediate(true);
		outSelect.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent inEvent) {
				final String lValue = (String) inEvent.getProperty().getValue();
				inProperty.setValue(lValue);
			}
		});

		return outSelect;
	}

	private static ComboBox createSelect(final String[] inItems) {
		final ComboBox outSelect = new ComboBox();
		for (final String lItem : inItems) {
			outSelect.addItem(lItem);
		}
		return outSelect;
	}

}

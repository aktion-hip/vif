/*
	This package is part of the application VIF.
	Copyright (C) 2012, Benno Luthiger

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Select;

/**
 * Class to create a generic select widget.
 * 
 * @author Luthiger
 * Created: 24.03.2012
 */
public class GenericSelect {
	
	/**
	 * Creates a <code>Selection</code> widget with the provided options.
	 * 
	 * @param inProperty {@link Property}
	 * @param inOptions {@link Collection}
	 * @param inWidth int
	 * @param inAllowNull boolean
	 * @param inProcessor {@link IProcessor} for post processing, may be <code>null</code>
	 * @return {@link Select}
	 */
	@SuppressWarnings("serial")
	public static Select getSelection(final Property inProperty, Collection<String> inOptions, int inWidth, boolean inAllowNull, final IProcessor inProcessor) {
		Select outSelect = new Select(null, inOptions);
		outSelect.select(inProperty.getValue().toString());
		outSelect.setStyleName("vif-select"); //$NON-NLS-1$
		outSelect.setWidth(inWidth, Sizeable.UNITS_PIXELS);
		outSelect.setNullSelectionAllowed(inAllowNull);
		outSelect.setImmediate(true);
		outSelect.addListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent inEvent) {
				String lItemID = (String) inEvent.getProperty().getValue();
				inProperty.setValue(lItemID);
				if (inProcessor != null) {
					inProcessor.process(lItemID);
				}
			}
		});
		return outSelect;
	}
	
	/**
	 * Convenience method, creates a <code>Collection</code> with the specified String values.
	 * 
	 * @param inValues String []
	 * @return {@link Collection}
	 */
	public static Collection<String> toCollection(String... inValues) {
		Collection<String> out = new ArrayList<String>();
		Collections.addAll(out, inValues);
		return out;
	}
	
// ---
	
	/**
	 * Process the selection.
	 * 
	 * @author Luthiger
	 * Created: 09.02.2012
	 */
	public static interface IProcessor {
		/**
		 * Do something depending on the selected item.
		 * 
		 * @param inItemID String the selected item's id
		 */
		void process(String inItemID);
	}

}

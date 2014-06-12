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

package org.hip.vif.forum.usersettings.ui;

import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.web.util.VIFViewHelper;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Luthiger
 * Created: 22.12.2011
 */
@SuppressWarnings("serial")
public abstract class AbstactUsersettingsView extends CustomComponent {

	/**
	 * @param inMessages {@link IMessages}
	 * @param inSubtitle {@link Label}
	 * @param inMsgKey 
	 * @return {@link VerticalLayout}
	 */
	protected VerticalLayout createLayout(final IMessages inMessages, final Label inSubtitle, String inMsgKey) {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		
		lLayout.setStyleName("vif-table"); //$NON-NLS-1$
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-pagetitle", inMessages.getMessage(inMsgKey)), Label.CONTENT_XHTML)); //$NON-NLS-1$
	
		inSubtitle.setVisible(false);
		lLayout.addComponent(inSubtitle);
		return lLayout;
	}

	/**
	 * @param inTask {@link ValueChangeListener}
	 * @return {@link Table}
	 */
	protected Table createTable(ValueChangeListener inTask) {
		final Table lTable = new Table();
		lTable.setWidth("100%"); //$NON-NLS-1$
		lTable.setColumnCollapsingAllowed(true);
		lTable.setColumnReorderingAllowed(true);
		lTable.setSelectable(true);
		lTable.setImmediate(true);
		lTable.addListener(inTask);
		return lTable;
	}

// --- inner classes ---
	
	protected static class SelectedFilter implements Filter {
		private String checkedItemID;
		
		protected SelectedFilter(String inCheckedItemID) {
			checkedItemID = inCheckedItemID;
		}
		public boolean passesFilter(Object inItemId, Item inItem) throws UnsupportedOperationException {
			Property lCheckBox = inItem.getItemProperty(checkedItemID);
			return (Boolean) lCheckBox.getValue();
		}
		
		public boolean appliesToProperty(Object inPropertyId) {
			return checkedItemID.equals(inPropertyId);
		}
	}

}

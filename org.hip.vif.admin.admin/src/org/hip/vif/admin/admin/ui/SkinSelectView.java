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

package org.hip.vif.admin.admin.ui;

import org.hip.vif.admin.admin.Activator;
import org.hip.vif.admin.admin.data.SkinBean;
import org.hip.vif.admin.admin.data.SkinConfigRegistry;
import org.hip.vif.admin.admin.tasks.SkinSelectTask;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * The view to display the selection of skins.
 * 
 * @author Luthiger Created: 30.10.2011
 */
@SuppressWarnings("serial")
public class SkinSelectView extends AbstractAdminView {
	private final ComboBox skinSelect;

	/**
	 * Constructor.
	 * 
	 * @param inTask
	 *            {@link SkinSelectTask}
	 */
	public SkinSelectView(final SkinSelectTask inTask) {
		final IMessages lMessages = Activator.getMessages();
		final VerticalLayout lLayout = initLayout(lMessages,
				"admin.select.skin.title.page"); //$NON-NLS-1$

		lLayout.addComponent(new Label(
				String.format(
						VIFViewHelper.TMPL_TITLE,
						"vif-title", lMessages.getMessage("admin.select.skin.selection")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$

		skinSelect = new ComboBox(null, SkinConfigRegistry.INSTANCE.getSkins());
		skinSelect.setNullSelectionAllowed(false);
		skinSelect.setNewItemsAllowed(false);
		skinSelect.setWidth(230, Unit.PIXELS);
		skinSelect.focus();
		lLayout.addComponent(skinSelect);

		final Button lSave = new Button(
				lMessages.getMessage("admin.select.skin.button.save")); //$NON-NLS-1$
		lSave.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(final ClickEvent inEvent) {
				inTask.save((SkinBean) skinSelect.getValue());
			}
		});
		lSave.setClickShortcut(KeyCode.ENTER);
		lLayout.addComponent(lSave);
	}

	private SkinBean getActive(final String inSkinID) {
		for (final SkinBean lSkin : SkinConfigRegistry.INSTANCE.getSkins()) {
			if (inSkinID.equals(lSkin.getSkinID())) {
				return lSkin;
			}
		}
		return null;
	}

	@Override
	public void attach() {
		super.attach();
		skinSelect.select(getActive(getUI().getTheme()));
	}

}

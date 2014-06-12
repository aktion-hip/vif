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

import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Base class for administrative views.
 * 
 * @author Luthiger Created: 30.10.2011
 */
@SuppressWarnings("serial")
public abstract class AbstractAdminView extends CustomComponent {

	protected VerticalLayout initLayout(final IMessages inMessages) {
		final VerticalLayout outLayout = new VerticalLayout();
		setCompositionRoot(outLayout);
		outLayout.setStyleName("vif-view"); //$NON-NLS-1$
		return outLayout;
	}

	protected VerticalLayout initLayout(final IMessages inMessages,
			final String inTitleKey) {
		final VerticalLayout outLayout = initLayout(inMessages);
		outLayout
				.addComponent(new Label(
						String.format(
								VIFViewHelper.TMPL_TITLE,
								"vif-pagetitle", inMessages.getMessage(inTitleKey)), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$
		return outLayout;
	}
}

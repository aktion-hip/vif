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
import org.hip.vif.core.util.RatingsHelper;
import org.hip.vif.web.Activator;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Component to display a user's ratings.
 * 
 * @author Luthiger
 * Created: 06.10.2011
 */
@SuppressWarnings("serial")
public class RatingsTable extends CustomComponent {

	/**
	 * Constructor
	 * 
	 * @param inRatings {@link RatingsHelper} the helper object to access the calculated values
	 */
	public RatingsTable(RatingsHelper inRatings) {
		setWidth(SIZE_UNDEFINED, 0);
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		lLayout.setWidth(SIZE_UNDEFINED, 0);
		
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-caption", Activator.getMessages().getMessage("ratings.table.title")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$
		lLayout.addComponent(createRatingsTable(inRatings));
	}
	
	private Component createRatingsTable(RatingsHelper inRatings) {
		GridLayout outLayout = new GridLayout(4, 6);
		outLayout.setWidth(400, UNITS_PIXELS);
		outLayout.setStyleName("vif-ratings"); //$NON-NLS-1$
		
		IMessages lMessages = Activator.getMessages();
		
		//first row: table header
		Label lSpacer = new Label(""); //$NON-NLS-1$
		lSpacer.setWidth(70, UNITS_PIXELS);
		outLayout.addComponent(lSpacer, 0, 0);
		outLayout.addComponent(createLabel(lMessages.getMessage("ratings.table.column.correctness")), 1, 0); //$NON-NLS-1$
		outLayout.addComponent(createLabel(lMessages.getMessage("ratings.table.column.responsiveness")), 2, 0); //$NON-NLS-1$
		outLayout.addComponent(createLabel(lMessages.getMessage("ratings.table.column.etiquette")), 3, 0); //$NON-NLS-1$
		
		//first content: good
		addComponent(outLayout, RatingValue.GOOD.render(), 0, 1, Alignment.MIDDLE_CENTER); //$NON-NLS-1$
		addComponent(outLayout, createLabel(inRatings.getCorrectnessA()), 1, 1, Alignment.MIDDLE_CENTER);
		addComponent(outLayout, createLabel(inRatings.getEfficiencyA()), 2, 1, Alignment.MIDDLE_CENTER);
		addComponent(outLayout, createLabel(inRatings.getEtiquetteA()), 3, 1, Alignment.MIDDLE_CENTER);
		//second content: average
		addComponent(outLayout, RatingValue.AVERAGE.render(), 0, 2, Alignment.MIDDLE_CENTER); //$NON-NLS-1$
		addComponent(outLayout, createLabel(inRatings.getCorrectnessB()), 1, 2, Alignment.MIDDLE_CENTER);
		addComponent(outLayout, createLabel(inRatings.getEfficiencyB()), 2, 2, Alignment.MIDDLE_CENTER);
		addComponent(outLayout, createLabel(inRatings.getEtiquetteB()), 3, 2, Alignment.MIDDLE_CENTER);
		//third content: bad
		addComponent(outLayout, RatingValue.BAD.render(), 0, 3, Alignment.MIDDLE_CENTER); //$NON-NLS-1$
		addComponent(outLayout, createLabel(inRatings.getCorrectnessC()), 1, 3, Alignment.MIDDLE_CENTER);
		addComponent(outLayout, createLabel(inRatings.getEfficiencyC()), 2, 3, Alignment.MIDDLE_CENTER);
		addComponent(outLayout, createLabel(inRatings.getEtiquetteC()), 3, 3, Alignment.MIDDLE_CENTER);
		
		//total
		addComponent(outLayout, new Label(lMessages.getMessage("ratings.table.label.total")), 0, 4, Alignment.MIDDLE_LEFT); //$NON-NLS-1$
		addComponent(outLayout, createLabel(inRatings.getTotal1()), 1, 4, Alignment.MIDDLE_CENTER);
		addComponent(outLayout, createLabel(inRatings.getTotal2()), 2, 4, Alignment.MIDDLE_CENTER);
		addComponent(outLayout, createLabel(inRatings.getTotal3()), 3, 4, Alignment.MIDDLE_CENTER);
		
		//average
		addComponent(outLayout, new Label(lMessages.getMessage("ratings.table.label.mean")), 0, 5, Alignment.MIDDLE_LEFT); //$NON-NLS-1$
		addComponent(outLayout, createLabel(inRatings.getMean1()), 1, 5, Alignment.MIDDLE_CENTER);
		addComponent(outLayout, createLabel(inRatings.getMean2()), 2, 5, Alignment.MIDDLE_CENTER);
		addComponent(outLayout, createLabel(inRatings.getMean3()), 3, 5, Alignment.MIDDLE_CENTER);

		return outLayout;
	}
	
	private Label createLabel(String inText) {
		Label out = new Label(inText);
		out.setWidth(SIZE_UNDEFINED, 0);
		return out;
	}
	
	private void addComponent(GridLayout inLayout, Component inComponen, int inColumn, int inRow, Alignment inAlignment) {
		inLayout.addComponent(inComponen, inColumn, inRow);
		inLayout.setComponentAlignment(inComponen, inAlignment);
	}
	
}

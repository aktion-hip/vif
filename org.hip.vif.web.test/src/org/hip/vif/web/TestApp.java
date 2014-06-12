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

package org.hip.vif.web;

import com.vaadin.Application;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Application for testing vaadin UI components.
 * 
 * @author Luthiger
 * Created: 08.03.2012
 */
@SuppressWarnings("serial")
public class TestApp extends Application {
	
	private Component component;

	public TestApp(Component inComponent) {
		component = inComponent;
	}

	/* (non-Javadoc)
	 * @see com.vaadin.Application#init()
	 */
	@Override
	public void init() {
		VerticalLayout lLayout = new VerticalLayout();
		
		setMainWindow(new Window("VIF Testing", lLayout));
		lLayout.addComponent(component);
	}

}

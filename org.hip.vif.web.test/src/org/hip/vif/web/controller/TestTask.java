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

package org.hip.vif.web.controller;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.interfaces.IPluggableTask;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.osgi.service.event.EventAdmin;

import com.vaadin.ui.Component;

/**
 * Task for testing purposes.
 * Note: this task is annotated as @Partlet, therefore, it is prepared 
 * to be looked up by the task registration.
 * 
 * @author Luthiger
 * Created: 03.07.2011
 */
@Partlet
public class TestTask implements IPluggableTask {

	/* (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IPluggableTask#run()
	 */
	@Override
	public Component run() throws VException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IPluggableTask#setEventAdmin(org.osgi.service.event.EventAdmin)
	 */
	@Override
	public void setEventAdmin(EventAdmin inEventAdmin) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IPluggableTask#requestLookup(org.hip.vif.web.util.LinkButtonHelper.LookupType, java.lang.Long)
	 */
	@Override
	public void requestLookup(LookupType inType, Long inID) {
		// TODO Auto-generated method stub
	}

	@Override
	public void requestLookup(LookupType inType, String inTextID) {
		// TODO Auto-generated method stub
	}

}

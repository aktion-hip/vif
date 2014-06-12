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

package org.hip.vif.web.internal.controller;

import org.hip.vif.web.controller.ControllerHelper;
import org.hip.vif.web.interfaces.IUseCaseAdmin;

/**
 * The service component controlling the <code>IUseCaseAdmin</code> service references. 
 *  
 * @author Luthiger
 * Created: 15.05.2011
 */
public class AdminController {

	/**
	 * @param inUseCase {@link IUseCaseAdmin} bind
	 */
	protected void addUseCase(IUseCaseAdmin inUseCase) {
		ControllerHelper.INSTANCE.addAdminUseCase(inUseCase);
	}
	
	/**
	 * @param inUseCase {@link IUseCaseAdmin} unbind
	 */
	protected void removeUseCase(IUseCaseAdmin inUseCase) {
		ControllerHelper.INSTANCE.removeAdminUseCase(inUseCase);
	}

}

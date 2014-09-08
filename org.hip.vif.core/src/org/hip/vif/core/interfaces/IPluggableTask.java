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

package org.hip.vif.core.interfaces;

import org.hip.kernel.exc.VException;

import com.vaadin.ui.Component;

/** Interface for all Task in the framework. Tasks implementing this interface have basically to implement the
 * <code>run()</code> method, which should contain all the actions of this task.
 *
 * @author Luthiger
 * @deprecated Use <code>org.ripla.web.interfaces.IPluggable</code> instead */
@Deprecated
public interface IPluggableTask extends IEventableTask {

    /** Runs this task.
     * 
     * @return {@link Component} the component created by the task.
     * @throws VException */
    Component run() throws VException;

}

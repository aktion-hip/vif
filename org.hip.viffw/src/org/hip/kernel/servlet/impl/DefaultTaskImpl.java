package org.hip.kernel.servlet.impl;

import org.hip.kernel.exc.VException;

/*
	This package is part of the servlet framework used for the application VIF.
	Copyright (C) 2001, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

/**
 * 	A dummy task
 *
 *	@author	Benno Luthiger
 */

public class DefaultTaskImpl extends AbstractTask {
	Exception exc;
	public DefaultTaskImpl() {
		super();
	}
	public DefaultTaskImpl(Exception inException) {
		exc = inException;
	}
	public void run() throws VException {
		if (exc != null) {
			throw new VException(exc.getMessage());
		}
	}
}

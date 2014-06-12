/*
This package is part of the application VIF.
Copyright (C) 2011, Benno Luthiger

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
package org.hip.vif.forum.search;

import org.hip.vif.core.util.AbstractMessages;

/**
* Bundle specific messages.
* 
* @author Luthiger
* Created: 04.06.2011
*/
public class Messages extends AbstractMessages {
	private static final String BASE_NAME = "messages"; //$NON-NLS-1$

	@Override
	protected ClassLoader getLoader() {
		return getClass().getClassLoader();
	}

	@Override
	protected String getBaseName() {
		return BASE_NAME;
	}

}

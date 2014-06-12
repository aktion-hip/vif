/**
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2003-2014, Benno Luthiger

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
package org.hip.vif.web.bom.impl;

import org.hip.vif.core.bom.GroupHome;

/**
 * This domain object home implements the GroupHome interface.
 * 
 * Created on 19.07.2002
 * 
 * @author Benno Luthiger
 * @see org.hip.vif.core.bom.GroupHome
 */
@SuppressWarnings("serial")
public class GroupHomeImpl extends org.hip.vif.core.bom.impl.GroupHomeImpl
		implements GroupHome {

	/*
	 * Every home has to know the class it handles. They provide access to this
	 * name through the method <I>getObjectClassName</I>;
	 */
	private final static String GROUP_CLASS_NAME = "org.hip.vif.web.bom.impl.GroupImpl";

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectClassName()
	 */
	@Override
	public String getObjectClassName() {
		return GROUP_CLASS_NAME;
	}

}

package org.hip.kernel.bom.model;

/*
 This package is part of the persistency framework used for the application VIF.
 Copyright (C) 2004, Benno Luthiger

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
 * Holds information about hidden columns used for the joining of domain objects.
 * 
 * @author Benno Luthiger
 * Created on Nov 1, 2004
 */
public interface HiddenDef extends MetaModelObject {
	public final static String columnName 	= "columnName".intern();
	public final static String domainObject	= "domainObject".intern();
}
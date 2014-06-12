package org.hip.kernel.bom.model.impl;

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

import org.hip.kernel.bom.model.RelationshipDefDef;

/**
 * 	Implements the RelationshipDefDef interface
 * 
 * 	@author		Benno Luthiger
 * 	@see		org.hip.kernel.bom.model.RelationshipDefDef
 */
public class RelationshipDefDefImpl extends AbstractMetaModelObject implements RelationshipDefDef {
	//
	private final static Object[][] 	def = {
			{ RelationshipDefDef.homeName		, "java.lang.String"	}
		,	{ RelationshipDefDef.keyDefName		, "java.lang.String"	}
	};
	
	/**
	 * RelationshipDefDefImpl default constructor.
	 */
	public RelationshipDefDefImpl() {
		super() ;
	}
	/**
	 * Returns the meta information.
	 * 
	 * @return java.lang.Object[][]
	 */
	protected Object[][] getConstantDef() {
		return def;
	}
}

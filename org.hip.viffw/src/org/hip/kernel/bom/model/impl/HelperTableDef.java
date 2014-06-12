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

import java.io.Serializable;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.sys.Assert;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.util.Debug;

/**
 * This helper class provides a special view on the mapping
 * definitions. It is more like an index then a real definition.
 * It creates a list of tableNames and associated mappingDefs out of an objectDef.
 * 
 * @author Benno Luthiger
 */
public class HelperTableDef implements Serializable {

	// Instance variables
	private String tableName = null;
	private Hashtable<String, MappingDef> mappingDefs = null;
	
	/**
	 * HelperTableDef singleton constructor.
	 */
	private HelperTableDef(String inTableName) {
		super();
		tableName = inTableName;
	}

	/**
	 * This method adds the specified MappingDef to the collection of MappingDefs
	 * 
	 * @param inMappingDef org.hip.kernel.bom.model.MappingDef
	 */
	public void add(MappingDef inMappingDef) {
		// Pre: mappingDef not null
		if (VSys.assertNotNull(this, "add", inMappingDef) == Assert.FAILURE)
			return;
		mappingDefs().put(inMappingDef.getColumnName(), inMappingDef);     
	}

	/**
	 * 	This method creates a new list HelperTableDefs based on the specified ObjectDef.
	 *  Each HelperTableDef in the collection can return the tableName 
	 *  and the associated mappingDefs.
	 * 
	 * 	@return java.util.Hashtable
	 * 	@param inObjectDef org.hip.kernel.bom.model.ObjectDef
	 */
	public static synchronized Hashtable<String, HelperTableDef> createTableDefs( ObjectDef inObjectDef ) {
		Hashtable<String, HelperTableDef> outTable = new Hashtable<String, HelperTableDef>(7);
	
		// Pre: inObjectDef not null
		if (VSys.assertNotNull(HelperTableDef.class, "createTableDef", inObjectDef) == Assert.FAILURE )
			return outTable;
	
		// Get mapping defs out of the specified objectDef
		for (MappingDef lMappingDef : inObjectDef.getMappingDefs2()) {
			String lTableName = lMappingDef.getTableName();
			HelperTableDef lTableDef = (HelperTableDef)outTable.get(lTableName);
			if (lTableDef == null) {
				lTableDef = new HelperTableDef(lTableName);
				outTable.put(lTableName, lTableDef);
			}
			lTableDef.add(lMappingDef);
		}
			 
		return outTable;
	}

	/**
	 * Returns an <code>Enumeration</code> of all <code>MappingDef</code>s contained in this <code>HelperTableDef</code> instance.
	 * 
	 * @return java.util.Enumeration
	 * @deprecated Use {@link HelperTableDef#getMappingDefs2()} instead.
	 */
	public Enumeration<?> getMappingDefs() {
		return mappingDefs().elements();
	}
	
	/**
	 * Returns Collection view of all <code>MappingDef</code>s contained in this <code>HelperTableDef</code> instance.
	 *  
	 * @return Collection<MappingDef>
	 */
	public Collection<MappingDef> getMappingDefs2() {
		return mappingDefs().values();
	}

	/**
	 * Returns the table name.
	 * 
	 * @return java.lang.String
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @return java.util.Hashtable<String, MappingDef>
	 */
	private Hashtable<String, MappingDef> mappingDefs() {
		if (mappingDefs == null)
			 mappingDefs = new Hashtable<String, MappingDef>(53);
		return mappingDefs;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return Debug.classMarkupString(this, "tableName=\"" + (tableName == null ? "null" : tableName) + "\"");
	}
}

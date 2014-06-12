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

import java.util.Hashtable;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.SettingException;
import org.hip.kernel.bom.model.ColumnDefDef;
import org.hip.kernel.bom.model.JoinDef;
import org.hip.kernel.bom.model.JoinedObjectDef;
import org.hip.kernel.bom.model.JoinedObjectDefDef;
import org.hip.kernel.bom.model.MetaModelHome;
import org.hip.kernel.bom.model.MetaModelObject;
import org.hip.kernel.bom.model.NestedDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.ObjectDefDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.exc.DefaultExceptionHandler;
import org.hip.kernel.util.Debug;
import org.hip.kernel.util.NameValueList;
import org.hip.kernel.util.VInvalidNameException;
import org.hip.kernel.util.VInvalidValueException;

/**
 * Implements the JoinedObjectDef interface
 * 
 * @author Benno Luthiger
 * @see org.hip.kernel.bom.model.ObjectDef
 */
public class JoinedObjectDefImpl extends AbstractDefWithColumns implements JoinedObjectDef {
	private ObjectDef objectDef	= null;
	private Hashtable<String, NestedDef> nestedDefs = null;
	
	/**
	 * JoinedObjectDefImpl default constructor.
	 */
	public JoinedObjectDefImpl() {
		super();
	}
	/**
	 * JoinedObjectDefImpl constructor with initial values.
	 * This constructor fills the property set with initial values.
	 * The array of the objects contains the names in the first column and the values in the second.
	 *
	 * @param inInitialValues java.lang.Object[][]
	 */
	public JoinedObjectDefImpl(Object[][] inInitialValues) {
		super(inInitialValues);
	}

	/**
	 * Returns an ObjectDef holding the meta information for a domain object
	 * which is returned by the JOIN.
	 *
	 * @return org.hip.kernel.bom.model.ObjectDef
	 */
	public ObjectDef getDomainObjectDef() {
		try {
			if (objectDef == null) {
				objectDef = createObjectDef();
			}
		}
		catch (BOMException exc) {
			DefaultExceptionHandler.instance().handle(exc);
		}
		return objectDef;
	}
	
	private ObjectDef createObjectDef() throws BOMException {
		ObjectDef outObjectDef = new ObjectDefImpl();
	
		try {
			outObjectDef.set(ObjectDefDef.objectName, this.get(ObjectDefDef.objectName));
			outObjectDef.set(ObjectDefDef.parent, this.get(ObjectDefDef.parent));
			outObjectDef.set(ObjectDefDef.version, this.get(ObjectDefDef.version));
				
			for (PropertyDef lPropertyDef : propertyDefs) {
				outObjectDef.addPropertyDef(lPropertyDef);
			}
		}
		catch (Exception exc) {
			throw new BOMException();
		}	
		
		return outObjectDef;
	}
	
	/**
	 * Returns the key to set and retrieve the columnDefs in the hashtable.
	 * 
	 * @return String
	 */
	protected String getColumnsKey() {
		return JoinedObjectDefDef.columnDefs;
	}

	/**
	 * JoinedObjectDefs are equal if their objectName, parent 
	 * and version attribute are equal.
	 *
	 * @return boolean
	 */
	public boolean equals(Object inObject) {
		if (inObject == null) return false;
		if (!(inObject instanceof JoinedObjectDef)) return false;
	
		JoinedObjectDef lObjectDef = (JoinedObjectDef)inObject;
		try {
			return ((String)get(JoinedObjectDefDef.objectName)).equals(lObjectDef.get(JoinedObjectDefDef.objectName)) &&
				   ((String)get(JoinedObjectDefDef.parent)).equals(lObjectDef.get(JoinedObjectDefDef.parent)) &&
				   ((String)get(JoinedObjectDefDef.version)).equals(lObjectDef.get(JoinedObjectDefDef.version));
		}
		catch (GettingException exc) {
			return false;
		}
	}
	
	/**
	 * Retrieves the column name out of the attributes of the colums definition.
	 * 
	 * @return java.lang.String
	 * @param inColumnDefAttributes org.hip.kernel.util.NameValueList
	 */
	public String getColumnName(NameValueList inColumnDefAttributes) {
		String outColumnName = "";
		try { 
			outColumnName = getQColumnName(inColumnDefAttributes);
		}
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);
		}
		
		return outColumnName;
	}
	
	/**
	 * Returns the JoinDef of this JoinedObjectDef
	 * 
	 * @return org.hip.kernel.bom.model.JoinDef
	 * @throws org.hip.kernel.bom.BOMException
	 */
	public JoinDef getJoinDef() throws BOMException {
		try {
			return (JoinDef)get(JoinedObjectDefDef.joinDef);
		}
		catch (GettingException exc) {
			throw new BOMException(exc.getMessage());
		}
	}
	
	/**
	 * @return org.hip.kernel.bom.model.MetaModelObject
	 */
	public MetaModelObject getMetaModelObject() {
		return MetaModelHome.singleton.getJoinedObjectDefDef();
	}

	private String getQColumnName(NameValueList inColumnDefAttributes) throws VInvalidNameException, VInvalidValueException, GettingException, BOMException {
		String lColumnDefName = (String)inColumnDefAttributes.getValue(ColumnDefDef.columnName);
		String lTableName = "";
		String lColumnName = lColumnDefName;
		if (inColumnDefAttributes.hasValue(ColumnDefDef.nestedObject)) {
			lTableName = (String)inColumnDefAttributes.getValue(ColumnDefDef.nestedObject);
			NestedDef lNestedDef = (NestedDef)getNestedDefs().get(lTableName);
			if (lNestedDef != null) {
				lColumnName = lNestedDef.getSQLColumnName(lColumnDefName);
			}
		}
		if (inColumnDefAttributes.hasValue(ColumnDefDef.domainObject)) {
			String lDomainObjectName = (String)inColumnDefAttributes.getValue(ColumnDefDef.domainObject);
			ObjectDef lObjectDef = getDomainObjectDef(lDomainObjectName);
			lTableName = getTableName(lObjectDef);
			lColumnName = lObjectDef.getPropertyDef(lColumnDefName).getMappingDef().getColumnName();
		}
		return String.format("%s.%s", lTableName, lColumnName);
	}

	/**
	 * Retrieves a table name involved in the join.
	 * 
	 * @return java.lang.String
	 * @param inObjectClassName java.lang.String
	 */
	public String getTableName(String inObjectClassName) {
		String outTableName = "";
		
		try {
			outTableName = getTableName(getDomainObjectDef(inObjectClassName));
		}	
		catch ( Exception exc ) {
			DefaultExceptionHandler.instance().handle(exc);
		}
		
		return outTableName;
	}

	/**
	 * Returns a hash code value for the object def.
	 *
	 * @return int
	 */
	public int hashCode() {
		int outCode = 1;
		try {
			outCode = ((String)get(JoinedObjectDefDef.objectName)).hashCode() ^
						((String)get(JoinedObjectDefDef.parent)).hashCode() ^
						((String)get(JoinedObjectDefDef.version)).hashCode();
		}
		catch (org.hip.kernel.bom.GettingException exc) {
			//left blank intentionally
		}
		return outCode;
	}

	/**
	 *  Sets the JoinDef of this JoinedObjectDef
	 *
	 * 	@param inJoinDef org.hip.kernel.bom.model.JoinDef
	 */
	public void setJoinDef(JoinDef inJoinDef ) {
		try {
			set(JoinedObjectDefDef.joinDef, inJoinDef);
		}
		catch (SettingException exc) {
			DefaultExceptionHandler.instance().handle(exc);
		}
	}
	
	/**
	 * Adds the specified NestedDef to the collection of NestedDefs.
	 * 
	 * @param inNestedDef NestedDef
	 */
	public void addNestedDef(NestedDef inNestedDef) {
		getNestedDefs().put(inNestedDef.getName(), inNestedDef);
	}
	
	private Hashtable<String, NestedDef> getNestedDefs() {
		if (nestedDefs == null) {
			nestedDefs = new Hashtable<String, NestedDef>(5);
		}
		return nestedDefs;
	}

	public String toString() {
		String lMessage = "";
		try {
			lMessage = JoinedObjectDefDef.objectName + "=\"" + (String)get(JoinedObjectDefDef.objectName) +
						"\" " + JoinedObjectDefDef.parent + "=\"" + (String)get(JoinedObjectDefDef.parent) +
						"\" " + JoinedObjectDefDef.version + "=\"" + (String)get(JoinedObjectDefDef.version) + "\"";
		}
		catch (GettingException exc) {}
		return Debug.classMarkupString(this, lMessage);
	}
}
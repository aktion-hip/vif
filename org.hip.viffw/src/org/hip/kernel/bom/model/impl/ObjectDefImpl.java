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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.ObjectDefDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.bom.model.MetaModelObject;
import org.hip.kernel.bom.model.MetaModelHome;
import org.hip.kernel.bom.model.KeyDef;
import org.hip.kernel.bom.model.KeyDefDef;
import org.hip.kernel.bom.model.PropertyDefDef;
import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.PropertySet;
import org.hip.kernel.bom.impl.PropertySetImpl;
import org.hip.kernel.bom.impl.PropertyImpl;
import org.hip.kernel.util.NameValueList;
import org.hip.kernel.util.NameValue;
import org.hip.kernel.util.DefaultNameValue;
import org.hip.kernel.util.DefaultNameValueList;
import org.hip.kernel.util.Debug;
import org.hip.kernel.util.VInvalidNameException;
import org.hip.kernel.exc.DefaultExceptionHandler;

/**
 * Implements the ObjectDef interface
 * 
 * @author		Benno Luthiger
 * @see		org.hip.kernel.bom.model.ObjectDef
 */
public class ObjectDefImpl extends AbstractModelObject implements ObjectDef {

	// Instance variables
	private Hashtable<String, HelperTableDef> helperTableDefs = null;
	
	/**
	 * ObjectDefImpl default constructor.
	 */
	public ObjectDefImpl() {
		super();
	}
	
	/**
	 * ObjectDefImpl constructor with initial values.
	 * The array of the objects contains the names in the first column and the values in the second.
	 * 
	 * @param inInitialValues java.lang.Object[][]
	 */
	public ObjectDefImpl(Object[][] inInitialValues) {
		super(inInitialValues);
	}
	
	/**
	 * Adds a PropertyDef to the list of propertyDefs.
	 * 
	 * @param inPropertyDef org.hip.kernel.bom.model.PropertyDef
	 */
	public void addPropertyDef( PropertyDef inPropertyDef ) {
		try { 
			PropertySet lSet = (PropertySet) this.get(ObjectDefDef.propertyDefs);
			if (lSet == null) {
				 lSet = new PropertySetImpl(this);
				 this.set(ObjectDefDef.propertyDefs, lSet);
			}
			lSet.add(new PropertyImpl(lSet, (String)inPropertyDef.get(PropertyDefDef.propertyName), inPropertyDef));
		}
		catch ( Exception exc ) {
			DefaultExceptionHandler.instance().handle(exc);
		}
	}
	
	/**
	 * ObjectDefs are equal if their objectName, parent and version
	 * attribute are equal.
	 *
	 * @return boolean
	 */
	public boolean equals(Object inObject) {
		if (inObject == null) return false;
		if (!(inObject instanceof ObjectDef)) return false;
	
		ObjectDef lObjectDef = (ObjectDef)inObject;
		try {
			return ((String)get(ObjectDefDef.objectName)).equals(lObjectDef.get(ObjectDefDef.objectName)) &&
				   ((String)get(ObjectDefDef.parent)).equals(lObjectDef.get(ObjectDefDef.parent)) &&
				   ((String)get(ObjectDefDef.version)).equals(lObjectDef.get(ObjectDefDef.version));
		}
		catch (GettingException exc) {
			return false;
		}
	}
	
	/**
	 * 	Returns the MappingDef associated with the specified property.
	 * 
	 * 	@return org.hip.kernel.bom.model.MappingDef
	 * 	@param inPropertyName java.lang.String
	 */
	public MappingDef getMappingDef( String inPropertyName ) {
		return getPropertyDef(inPropertyName).getMappingDef();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.model.ObjectDef#getMappingDefs()
	 */
	public Iterator<MappingDef> getMappingDefs() {
		return getMappingDefs2().iterator();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.model.ObjectDef#getMappingDefs2()
	 */
	public Collection<MappingDef> getMappingDefs2() {
		Collection<MappingDef> outMappingDefs = new Vector<MappingDef>();
		
		for (PropertyDef lPropertyDef : getPropertyDefs2()) {
			outMappingDefs.add((MappingDef)lPropertyDef.getMappingDef());
		}
		return outMappingDefs;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.model.ObjectDef#getMappingDefsForTable(java.lang.String)
	 */
	public Enumeration<MappingDef> getMappingDefsForTable(String inTableName) {
		Vector<MappingDef> outCollection = new Vector<MappingDef>();
		outCollection.addAll(getMappingDefsForTable2(inTableName));
		return outCollection.elements();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.model.ObjectDef#getMappingDefsForTable2(java.lang.String)
	 */
	public Collection<MappingDef> getMappingDefsForTable2(String inTableName) {
		HelperTableDef lHelperTableDef = (HelperTableDef) helperTableDefs().get(inTableName);
		if (lHelperTableDef == null)
		     return new Hashtable<String, MappingDef>().values();
		     
		return lHelperTableDef.getMappingDefs2();		
	}
	
	/**
	 * @return org.hip.kernel.bom.model.MetaModelObject
	 */
	public MetaModelObject getMetaModelObject() {
		return MetaModelHome.singleton.getObjectDefDef() ;
	}
	
	/**
	 * 	Returns the primary key definition. The actual version of
	 *	this domain object framework does no support secondary
	 *	primary keys.
	 * 
	 * 	@return org.hip.kernel.bom.model.KeyDef
	 */
	public KeyDef getPrimaryKeyDef() {
	
		try {
			//pre
			if (this.get(ObjectDefDef.keyDefs) == null) return null;
	
			return (KeyDef)((NameValueList)this.get(ObjectDefDef.keyDefs)).getValue(KeyDefDef.keyType_PrimaryKey);		
		} 
		catch ( Exception exc ) {
			DefaultExceptionHandler.instance().handle(exc);
			return null;
		}	
	}
	
	/**
	 * 	Returns the PropertyDef for the specified Property.
	 * 
	 * 	@return org.hip.kernel.bom.model.PropertyDef
	 * 	@param lName java.lang.String
	 */
	public PropertyDef getPropertyDef(String lName) {
	
		try { 
			return (PropertyDef)((PropertySet)this.get(ObjectDefDef.propertyDefs)).getValue(lName);
		}
		catch (VInvalidNameException exc) {
			return null;
		}
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.model.ObjectDef#getPropertyDefs()
	 */
	public Iterator<PropertyDef> getPropertyDefs() {
		return getPropertyDefs2().iterator();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.model.ObjectDef#getPropertyDefs2()
	 */
	public Vector<PropertyDef> getPropertyDefs2() {
		Vector<PropertyDef> outDefs = getPropertyDefs2(PropertyDefDef.propertyTypeSimple);
		outDefs.addAll(getPropertyDefs2(PropertyDefDef.propertyTypeComposite));
		outDefs.addAll(getPropertyDefs2(PropertyDefDef.propertyTypeObjectRef));
		return outDefs;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.model.ObjectDef#getPropertyDefs(java.lang.String)
	 */
	public Iterator<PropertyDef> getPropertyDefs(String inPropertyType) {
		return getPropertyDefs2(inPropertyType).iterator();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.model.ObjectDef#getPropertyDefs2(java.lang.String)
	 */
	public Vector<PropertyDef> getPropertyDefs2(String inPropertyType) {
		Vector<PropertyDef> outPropertyDefs = new Vector<PropertyDef>(); 
		try {
			PropertySet lPropertyDefs = (PropertySet)get(ObjectDefDef.propertyDefs);
			if (lPropertyDefs != null) {
				for (NameValue lNameValue : lPropertyDefs.getNameValues2()) {
					PropertyDef lPropertyDef = (PropertyDef)lNameValue.getValue();
					if (lPropertyDef.getPropertyType().equals(inPropertyType)) 
				 		 outPropertyDefs.addElement(lPropertyDef);					
				}
			} //for
			return outPropertyDefs;
		} 
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);
			return new Vector<PropertyDef>();
		} //try-catch		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.model.ObjectDef#getTableNames()
	 */
	public Enumeration<String> getTableNames() {
		return helperTableDefs().keys();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.model.ObjectDef#getTableNames2()
	 */
	public Set<String> getTableNames2() {
		return helperTableDefs().keySet();
	}
	
	/**
	 * Returns a hash code value for the object def.
	 *
	 * @return int
	 */
	public int hashCode() {
		int outCode = 1;
		try {
			outCode = ((String)get(ObjectDefDef.objectName)).hashCode() ^
						((String)get(ObjectDefDef.parent)).hashCode() ^
						((String)get(ObjectDefDef.version)).hashCode();
		}
		catch (GettingException exc) {
			//left blank intentionally
		}
		return outCode;
	}
	
	/**
	 * 	@return java.util.Hashtable
	 */
	private synchronized Hashtable<String, HelperTableDef> helperTableDefs() {
		if (helperTableDefs == null)
			helperTableDefs = HelperTableDef.createTableDefs(this);
		return helperTableDefs;
	}
	
	/**
	 * 	Sets the definition of the primary key.
	 * 
	 * 	@param inKeyDef org.hip.kernel.bom.model.KeyDef
	 */
	public void setPrimaryKeyDef(KeyDef inKeyDef) {
	
		try { 
			NameValueList lKeys = (NameValueList)this.get(ObjectDefDef.keyDefs);
			if (lKeys == null) {
				lKeys = new DefaultNameValueList();
				this.set(ObjectDefDef.keyDefs, lKeys);
			}
			lKeys.add(new DefaultNameValue(lKeys, KeyDefDef.keyType_PrimaryKey, inKeyDef));
		} 
		catch ( Exception exc ) {
			DefaultExceptionHandler.instance().handle(exc);
		}	
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.impl.DomainObjectImpl#toString()
	 */
	public String toString() {
		String lMessage = "";
		try {
			lMessage = "objectName=\"" + (String)get(ObjectDefDef.objectName) +
						"\" parent=\"" + (String)get(ObjectDefDef.parent) +
						"\" version=\"" + (String)get(ObjectDefDef.version) + "\"";
		}
		catch (GettingException esc) {}
		return Debug.classMarkupString(this, lMessage);
	}
}

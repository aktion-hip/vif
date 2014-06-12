package org.hip.kernel.bom.impl;

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

import org.hip.kernel.sys.VSys;
import org.hip.kernel.bom.ObjectRefProperty;
import org.hip.kernel.bom.PropertySet;
import org.hip.kernel.bom.DomainObjectCollection;
import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.model.RelationshipDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.bom.model.PropertyDefDef;
import org.hip.kernel.exc.DefaultExceptionHandler;

/**
 * 	The ObjectRef property is a special kind of property to hold
 *	aggregated objects.
 *
 *	@author		Benno Luthiger
 *  @see		org.hip.kernel.bom.ObjectRefProperty
 */
public class ObjectRefPropertyImpl extends PropertyImpl implements ObjectRefProperty {
	/**
	 * ObjectRefProperty constructor without value.
	 *
	 * @param inSet org.hip.kernel.bom.PropertySet
	 * @param inName java.lang.String
	 */
	public ObjectRefPropertyImpl(PropertySet inSet, String inName) {
		super(inSet, inName);
	}
	
	/**
	 * ObjectRefProperty constructor which initializes with name and value.
	 *
	 * @param inSet org.hip.kernel.bom.PropertySet
	 * @param inName java.lang.String
	 * @param inValue java.lang.Object
	 */
	public ObjectRefPropertyImpl(PropertySet inSet, String inName, Object inValue) {
		super(inSet, inName, inValue);
	}
	
	/**
	 * Returns a DomainObjectCollection as an enumeration.
	 *
	 * @return java.lang.Object
	 */
	public Object getValue() {
		if (value() == null) {
			loadObjects();
		}
		return ((DomainObjectCollection)value()).elements() ;
	}
	
	/**
	 * @return boolean
	 */
	public boolean isSimple() {
		return false;
	}
	
	/**
	 * Sets the value of this instance.
	 * The DomainObjectCollectionImpl is either empty or filled 
	 * with the data from the QueryResult of related entries.
	 */
	private synchronized void loadObjects() {
		GeneralDomainObjectHome lHome = ((DomainObject)this.getOwingObject()).getHome();
		PropertyDef	lPropertyDef = lHome.getPropertyDef(this.getName());
		
		if (lPropertyDef == null) {
			 this.value( new DomainObjectCollectionImpl(null) );
			 return ;
	 	}
		if (PropertyDefDef.propertyTypeObjectRef != lPropertyDef.getPropertyType()) {
			 this.value( new DomainObjectCollectionImpl(null) );
			 return;
		}
		
		RelationshipDef lRelationshipDef = lPropertyDef.getRelationshipDef() ;
		if ( lRelationshipDef == null ) {
			 this.value( new DomainObjectCollectionImpl(null) );
			 return;
		}
		
		GeneralDomainObjectHome lChildHome = (GeneralDomainObjectHome)VSys.homeManager.getHome(lRelationshipDef.getHomeName());
		try {
			QueryResult lResult = lChildHome.select( ((DomainObject)this.getOwingObject()).getKey() );
			DomainObjectCollection lCollection = new DomainObjectCollectionImpl(lResult);
			this.value(lCollection);
	
		} 
		catch ( Exception exc ) {
			DefaultExceptionHandler.instance().handle(exc);
		}
	}
}

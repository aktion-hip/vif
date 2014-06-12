package org.hip.vif.core.search;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.lucene.document.Document;
import org.hip.kernel.bom.DomainObjectVisitor;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.Property;
import org.hip.kernel.bom.PropertySet;
import org.hip.kernel.bom.SettingException;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.PropertyImpl;
import org.hip.kernel.bom.impl.PropertySetImpl;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.exc.VException;

/*
 This package is part of the application VIF.
 Copyright (C) 2005, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/**
 * A domain object backed by a lucene Document object.
 * 
 * @author Benno Luthiger
 * Created on 27.09.2005
 * @see org.apache.lucene.document.Document
 */
public abstract class AbstractHitsDomainObject implements GeneralDomainObject {
	protected Document document;
		
	/**
	 * HitsDomainObject constructor.
	 * 
	 * @param inDocument Document
	 */
	public AbstractHitsDomainObject(Document inDocument) {
		super();
		document = inDocument;
	}

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObject#accept(org.hip.kernel.bom.DomainObjectVisitor)
	 */
	public void accept(DomainObjectVisitor inVisitor) {
		inVisitor.visitDomainObject(this);
	}

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObject#getHome()
	 */
	public GeneralDomainObjectHome getHome() {
		return null;
	}

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObject#getHomeClassName()
	 */
	public String getHomeClassName() {
		return "";
	}

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObject#getKey()
	 */
	public KeyObject getKey() {
		return getKey(true);
	}

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObject#getKey(boolean)
	 */
	public KeyObject getKey(boolean inInitial) {
		KeyObject outKey = new KeyObjectImpl();
		try {
			outKey.setValue(getIDFieldName(), document.get(getIDFieldName()));
		} 
		catch (VException exc) {
			//Left empty intentionally.
		}
		return outKey;
	}

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObject#getObjectDef()
	 */
	public ObjectDef getObjectDef() {
		return null;
	}

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObject#getObjectName()
	 */
	public String getObjectName() {
		String lName = getClass().getName();
		return lName.substring(lName.lastIndexOf(".")+1);
	}

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObject#isChanged()
	 */
	public boolean isChanged() {
		return false;
	}

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObject#release()
	 */
	public void release() {
		// Intentionally left empty.
	}

	/**
	 * @see org.hip.kernel.bom.SemanticObject#set(java.lang.String, java.lang.Object)
	 */
	public void set(String inName, Object inValue) throws SettingException {
		throw new SettingException("Not applicable with this object");
	}

	/**
	 * @see org.hip.kernel.bom.SemanticObject#setVirgin()
	 */
	public void setVirgin() {
		// Intentionally left empty.
	}

	/**
	 * Returns the ID of the wrapped document.
	 * 
	 * @return String ID
	 */
	public String getID() {
		return document.get(getIDFieldName());
	}
	
	/**
	 * Subclasses have to provide the name of the ID field.
	 * 
	 * @return String
	 */
	abstract String getIDFieldName();

	/**
	 * @see org.hip.kernel.bom.SemanticObject#get(java.lang.String)
	 */
	public Object get(String inName) throws GettingException {
		if (getFields().contains(inName)) {
			return document.get(inName);
		}
		throw new GettingException("No field named " + inName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.SemanticObject#getPropertyNames()
	 */
	public Iterator<String> getPropertyNames() {
		return getPropertyNames2().iterator();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.SemanticObject#getPropertyNames2()
	 */
	public Collection<String> getPropertyNames2() {
		return new Vector<String>(getFields());
	}	

	/**
	 * @see org.hip.kernel.bom.SemanticObject#propertySet()
	 */
	public PropertySet propertySet() {
		PropertySet outProperties = new PropertySetImpl(null);
		for (String lName : getFields()) {
			Property lProperty = new PropertyImpl(outProperties, lName, document.get(lName));
			outProperties.add(lProperty);
		}
		return outProperties;
	}
	
	/**
	 * Hook for subclasses to return the appropriate List of Document fields.
	 * 
	 * @return List<String>
	 */
	abstract List<String> getFields();
	
}

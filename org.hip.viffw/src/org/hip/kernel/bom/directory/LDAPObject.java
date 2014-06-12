/*
	This package is part of the framework used for the application VIF.
	Copyright (C) 2006, Benno Luthiger

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

package org.hip.kernel.bom.directory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.DomainObjectVisitor;
import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.Property;
import org.hip.kernel.bom.PropertySet;
import org.hip.kernel.bom.ReadOnlyDomainObject;
import org.hip.kernel.bom.impl.AbstractSemanticObject;
import org.hip.kernel.bom.impl.HomeManagerImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.model.KeyDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.ObjectDefDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.bom.model.PropertyDefDef;
import org.hip.kernel.exc.DefaultExceptionHandler;
import org.hip.kernel.exc.VException;

/**
 * Generic domain object (aka model) for entries stored on and retrieved from a LDAP server.
 *
 * @author Luthiger
 * Created on 06.07.2007
 */
public abstract class LDAPObject extends AbstractSemanticObject implements DomainObject, ReadOnlyDomainObject {
	private enum ModeType { MODE_NEW, MODE_CHANGE };

	private GeneralDomainObjectHome home = null;

	private KeyObject initialKey = null;
	private boolean isLoaded = false;
	
	private ModeType mode;

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.impl.AbstractSemanticObject#initializePropertySet(org.hip.kernel.bom.PropertySet)
	 */
	@Override
	protected void initializePropertySet(PropertySet inSet) {
		ObjectDef lObjectDef = getHome().getObjectDef();	
		for (PropertyDef lPropertyDef : lObjectDef.getPropertyDefs2()) {
			try { 
				inSet.add(lPropertyDef.create(inSet));
			} 
			catch (Exception exc) {
				DefaultExceptionHandler.instance().handle(exc);
			}	
		}
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.impl.AbstractSemanticObject#isDynamicAddAllowed()
	 */
	@Override
	protected boolean isDynamicAddAllowed() {
		return false;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObject#delete()
	 */
	public void delete() throws SQLException {
		//intentionally left empty
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObject#delete(boolean)
	 */
	public void delete(boolean inCommit) throws SQLException {
		//intentionally left empty
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObject#getChangedProperties()
	 */
	public Iterator<Property> getChangedProperties() {
		//intentionally left empty
		return null;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObject#insert()
	 */
	public Long insert() throws SQLException {
		//intentionally left empty
		return null;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObject#insert(boolean)
	 */
	public Long insert(boolean inCommit) throws SQLException {
		//intentionally left empty
		return null;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObject#update()
	 */
	public void update() throws SQLException {
		//intentionally left empty
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObject#update(boolean)
	 */
	public void update(boolean inCommit) throws SQLException {
		//intentionally left empty
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObject#accept(org.hip.kernel.bom.DomainObjectVisitor)
	 */
	public void accept(DomainObjectVisitor inVisitor) {
		inVisitor.visitDomainObject(this);
	}

	/**
	 * Returns the home of this class.
	 *
	 * @return org.hip.kernel.bom.GeneralDomainObjectHome
	 */
	public final synchronized GeneralDomainObjectHome getHome() {	
		if (home == null) {
			home = (GeneralDomainObjectHome)HomeManagerImpl.getSingleton().getHome(getHomeClassName());
		}
		return home;
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObject#getKey()
	 */
	public KeyObject getKey() {
		KeyObject outKey = null;
		
		KeyDef lKeyDef = getObjectDef().getPrimaryKeyDef();
		if (lKeyDef == null)
			return outKey;
	
		outKey = new KeyObjectImpl();
		for (String lKeyName : lKeyDef.getKeyNames2()) {
			try { 
				outKey.setValue(lKeyName, this.get(lKeyName));
			} 
			catch (Exception exc) {
				DefaultExceptionHandler.instance().handle(exc);
			}
		}
		return outKey;
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObject#getKey(boolean)
	 */
	public KeyObject getKey(boolean inInitial) {
		if (inInitial || ModeType.MODE_CHANGE.equals(mode)) {
			return initialKey;
		}
		else {
			return getKey();
		}
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObject#getObjectDef()
	 */
	public ObjectDef getObjectDef() {
		return getHome().getObjectDef() ;
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObject#getObjectName()
	 */
	public String getObjectName() {
		try { 
			return (String)getObjectDef().get(ObjectDefDef.objectName);
		}
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);
			return "";
		}	
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObject#isChanged()
	 */
	public boolean isChanged() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObject#release()
	 */
	public void release() {
		getHome().release(this);
	}

	void loadFromResultSet(SearchResult inResult) {
		GeneralDomainObjectHome lHome = getHome();
		Attributes lAttributes = inResult.getAttributes();
		NamingEnumeration<String> lIDs = lAttributes.getIDs();
		try {
			while (lIDs.hasMore()) {
				String lID = lIDs.next();
				PropertyDef lProperty = lHome.getPropertyDefFor(lID);
				if (lProperty != null) {
				 	String lName = (String) lProperty.get(PropertyDefDef.propertyName);
				 	if (lProperty.getPropertyType() == PropertyDefDef.propertyTypeComposite) {
				 		this.set(lName, getCollection(lAttributes.get(lID)));				 		
				 	}
				 	else {
				 		this.set(lName, lAttributes.get(lID).get());
				 	}
				}			
			}
			initialKey = getKey();
			
			// Let subclasses do additional things
			isLoaded = true;
			getPropertySet().notifyInit(true);
			afterLoad();
		} catch (VException exc) {
			DefaultExceptionHandler.instance().handle(exc);
		} catch (NamingException exc) {
			DefaultExceptionHandler.instance().handle(exc);
		}
	}
	
	private Collection<Object> getCollection(Attribute inAttribute) throws NamingException {
		Collection<Object> outCollection = new Vector<Object>();
		for (NamingEnumeration<?> lEnum = inAttribute.getAll(); lEnum.hasMore();) {
			outCollection.add(lEnum.next());
		}
		return outCollection;
	}

	/**
	 * @return org.hip.kernel.bom.PropertySet
	 */
	private PropertySet getPropertySet() {
		return this.propertySet();
	}
	
	/**
	 * This method will be called after the loadFromResultSet.
	 * It's a hook for subclasses.
	 */
	protected void afterLoad() {}

	/**
	 * Friendly method called by <code>LDAPObjectHome</code>.
	 *
	 */
	void initializeForNew() {
		mode = ModeType.MODE_NEW;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(initialKey);
		out.writeObject(mode);
		out.writeBoolean(isLoaded);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		initialKey = (KeyObject)in.readObject();
		mode = (ModeType)in.readObject();
		isLoaded = in.readBoolean();
	}
	
}

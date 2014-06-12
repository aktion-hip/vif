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

package org.hip.kernel.bom.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.DomainObjectVisitor;
import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.Property;
import org.hip.kernel.bom.PropertySet;
import org.hip.kernel.bom.ReadOnlyDomainObject;
import org.hip.kernel.bom.model.KeyDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.ObjectDefDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.bom.model.PropertyDefDef;
import org.hip.kernel.bom.model.TypeDef;
import org.hip.kernel.exc.VException;
import org.hip.kernel.util.Debug;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the interface DomainObject and ReadOnlyDomainObject.
 *
 * @author	Benno Luthiger
 * @see org.hip.kernel.bom.DomainObject
 * @see org.hip.kernel.bom.ReadOnlyDomainObject
 */
@SuppressWarnings("serial")
abstract public class DomainObjectImpl extends AbstractSemanticObject implements DomainObject, ReadOnlyDomainObject {
	private static final Logger LOG = LoggerFactory.getLogger(DomainObjectImpl.class);

	// Class variables
	private static	String MODE_NEW		= "new".intern();
	private static	String MODE_CHANGE	= "change".intern();
	
	// Instance variables
	private GeneralDomainObjectHome home = null;

	//
	private boolean 	isLoaded	= false;
	private String		mode		= null;
	private KeyObject	initialKey 	= null;
	
	/**
	 * DomainObjectImpl default constructor.
	 */
	public DomainObjectImpl() {
		super();
	}
	
	/**
	 * 	Sets the specified visitor.
	 *  This method implements the visitor pattern.
	 * 
	 * 	@param inVisitor org.hip.kernel.bom.DomainObjectVisitor
	 */
	public void accept( DomainObjectVisitor inVisitor ) {
		inVisitor.visitDomainObject(this);
	}
	
	/**
	 *  This method will be called after the loadFromResultSet.
	 *	It's a hook for subclasses.
	 */
	protected void afterLoad() {
	}
	
	/**
	 * Returns a vector of SQL delete strings.
	 * Each table to update gets a delete string.
	 *
	 * @return java.lang.Vector<String>
	 * @see org.hip.kernel.bom.DBAdapterSimple#createDeleteString(String, DomainObject)
	 */
	private Vector<String> createDeleteString()  {
		Vector<String> outDeletes = new Vector<String>() ;
		for (String lTableName : getObjectDef().getTableNames2()) {
			outDeletes.addElement(((DomainObjectHome)getHome()).createDeleteString(lTableName, this));
		}
		return outDeletes;
	}

	/**
	 * Returns a vector of SQL insert strings.
	 * 
	 * @return java.util.Vector<String>
	 * @see org.hip.kernel.bom.DBAdapterSimple#createInsertString(String, DomainObject)
	 */
	private Vector<String> createInsertString()  {
		Vector<String> outInserts = new Vector<String>();
		for (String lTableName : getObjectDef().getTableNames2()) {
			outInserts.addElement(((DomainObjectHome)getHome()).createInsertString(lTableName, this));
		}
		return outInserts;
	}

	/**
	 * Returns a vector of SQL strings for prepared updates.
	 * 
	 * @return java.util.Vector<String>
	 * @see org.hip.kernel.bom.DBAdapterSimple#createPreparedUpdateString(String, DomainObject)
	 */
	protected Vector<String> createPreparedUpdateString()  {
		Vector<String> outUpdates = new Vector<String>();
		for (String lTableName : getObjectDef().getTableNames2()) {
			outUpdates.addElement(((DomainObjectHome)getHome()).createPreparedUpdateString(lTableName, this));
		}		
		return outUpdates;
	}

	/**
	 * Returns a vector of SQL update strings.
	 * Each table to update gets an update string.
	 *
	 * @return java.lang.Vector<String>
	 * @see org.hip.kernel.bom.DBAdapterSimple#createUpdateString(String, DomainObject)
	 */
	private Vector<String> createUpdateString()  {
		Vector<String> outUpdates = new Vector<String>();
		for (String lTableName : getObjectDef().getTableNames2()) {
			outUpdates.addElement(((DomainObjectHome)getHome()).createUpdateString(lTableName, this));
		}		
		return outUpdates;
	}
	

	/**
	 * DomainObjects are equal if their class (i.e. classname) is equal
	 * and they have the same key
	 *
	 * @return boolean
	 */
	public boolean equals(Object inObject) {
		if (inObject == null) return false;
		if (getClass() != inObject.getClass()) return false;
	
		if (getKey() == null) {
			return (((DomainObject)inObject).getKey() == null);
		}
		else {
			return getKey().equals(((DomainObject)inObject).getKey());
		}
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

	/**
	 * This method returns an instance of a key object.
	 * Null will be returned if no key def could be found.
	 *
	 * @return org.hip.kernel.bom.KeyObject
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
				LOG.error("Error encountered while setting the key!", exc);
			}
		}
		return outKey;
	}
	
	/**
	 * This method returns an instance of a key object.
	 * Null will be returned if no key def could be found.
	 * The initial key object is returned if demanded or 
	 * if this domain objects mode is changed, else the actual key is returned.
	 *
	 * @param inInitial boolean
	 * @return org.hip.kernel.bom.KeyObject
	 */
	public KeyObject getKey(boolean inInitial) {
		if (inInitial || MODE_CHANGE.equals(mode)) {
			return initialKey;
		}
		else {
			return getKey();
		}
	}
	
	/**
	 * 	Returns the object definition for this object.
	 * 
	 * 	@return org.hip.kernel.bom.model.ObjectDef
	 */
	public synchronized ObjectDef getObjectDef() {
		return getHome().getObjectDef() ;
	}

	/**
	 * 	Returns the name of this object.
	 * 
	 * 	@return java.lang.String
	 */
	public String getObjectName() {
		try { 
			return (String)getObjectDef().get(ObjectDefDef.objectName);
		}
		catch (Exception exc) {
			LOG.error("Error encountered while getting the object name!", exc);
			return "";
		}	
	}

	/**
	 * 	@return org.hip.kernel.bom.PropertySet
	 */
	private PropertySet getPropertySet() {
		return this.propertySet();
	}
	
	/**
	 * Returns the changed properties of this domain object.
	 * 
	 * @return java.util.Iterator<Property>
	 */
	public Iterator<Property> getChangedProperties() {
		return getPropertySet().getChangedProperties2().iterator();
	}

	/**
	 * Returns a hash code value for the domain object.
	 *
	 * @return int
	 */
	public int hashCode() {
		return getKey() == null ? 1 : getKey().hashCode();
	}

	/**
	 * Initialize the DomainObject and mark it as new.
	 */
	protected void initializeForNew()  {
		mode = DomainObjectImpl.MODE_NEW;
	}

	/**
	 * Initializing of the domain object's PropertySet with the help
	 * of the ModelObject (i.e. the domain object's ObjectDef).
	 * It takes all the ObjectDefs PropertyDefs and puts it to
	 * the domain object's PropertySet.
	 *
	 * @param inSet org.hip.kernel.bom.PropertySet
	 */
	public void initializePropertySet(PropertySet inSet) {
		ObjectDef lObjectDef = getHome().getObjectDef();	
		for (PropertyDef lPropertyDef : lObjectDef.getPropertyDefs2()) {
			try { 
				inSet.add(lPropertyDef.create(inSet));
			} 
			catch (Exception exc) {
				LOG.error("Error encountered while i!", exc);
			}	
		}
	}
	
	/**
	 * 	Returns true if some properties have been changed.
	 * 
	 * 	@return boolean
	 */
	public boolean isChanged() {
		// Quite streightforward implementation
		return getPropertySet().getChangedProperties2().size() != 0;
	}
	
	/**
	 * 	DomainObjects do not allow a dynamic add. Only those
	 *	properties are supported which are defined in the definition string.
	 * 
	 * 	@return boolean
	 */
	public boolean isDynamicAddAllowed() {
		return false;
	}
	
	/**
	 * This method fills the DomainObject with data from the ResultSet.
	 *
	 * @param inResult java.sql.ResultSet
	 */
	protected void loadFromResultSet(ResultSet inResult) {
		String lName = "";
		String lType = "";
		
		try { 
			GeneralDomainObjectHome lHome = getHome();
		  	ResultSetMetaData lMetaData = inResult.getMetaData();
			for (int i = 1; i <= lMetaData.getColumnCount(); i++) {
				String lColumnName = lMetaData.getColumnLabel(i);
				PropertyDef lProperty = lHome.getPropertyDefFor(lColumnName);
				if (lProperty != null) {
					try {
						lName = (String) lProperty.get(PropertyDefDef.propertyName);
						lType = ((String) lProperty.get(PropertyDefDef.valueType)).intern();
						
						if (lType == TypeDef.String) { 
							this.set(lName, inResult.getString(i));
						} 
						else if (lType == TypeDef.LongVarchar) { 
							this.set(lName, inResult.getAsciiStream(i));
						} 
						else if (lType == TypeDef.Date) {
							this.set(lName, inResult.getDate(i));
						} 
						else if (lType == TypeDef.Timestamp) {
							Timestamp lValue = null;
							try {
								lValue = inResult.getTimestamp(i);
							}
							catch (SQLException exc) {
								//intentionally left empty (because of MySQL zeroDateTimeBehavior)
							}
							this.set(lName, lValue);
						} 
						else if (lType == TypeDef.Integer) {
							this.set(lName, new Integer(inResult.getInt(i)));
						} 
						else if (lType == TypeDef.Long) {
							this.set(lName, new Long(inResult.getLong(i)));
						} 
						else if (lType == TypeDef.BigInteger) {
							java.math.BigDecimal lValue = inResult.getBigDecimal(i);
							this.set(lName, lValue != null ? lValue.toBigInteger(): null);
						} 
						else if (lType == TypeDef.BigDecimal) {	
							this.set(lName, inResult.getBigDecimal(i));
						}
						else if (lType == TypeDef.Number) {
							this.set(lName, inResult.getBigDecimal(i));
						}
						else if (lType == TypeDef.Binary) {
							this.set(lName, inResult.getBlob(i));
						}
					} 
					catch (Exception exc) {
						LOG.error("Error encountered while loading the model instance!", exc);
					}
				} // if
				else {
					//we assume a calculated/modified column and, therefore, a numerical value.
					try {						
						this.set(lColumnName, inResult.getLong(i));
					}
					catch (Exception exc) {
						LOG.error("Error encountered while loading the model instance!", exc);
					}
				}
			} // for	
			initialKey = getKey();
		
			// Let subclasses do additional things
			this.isLoaded = true;
			getPropertySet().notifyInit(true);
			afterLoad();
		} 
		catch (Exception exc) {
			LOG.error("Error encountered while loading the model instance!", exc);
		}	
	}
	
	/**
	 * Initializes the DomainObject again, i.e. sets its properties to its
	 * initial value.
	 */
	public void reinitialize() {
	
		//set key properties as initial
		initialKey = getKey();
		
		//set properties as initial	
		for (Property lProperty : propertySet().getChangedProperties2()) {
			PropertyDef lPropertyDef = lProperty.getPropertyDef();
			
			try {
				Property lNewProperty = lPropertyDef.create(propertySet());
				lNewProperty.setValue(lProperty.getValue());			
				propertySet().add(lNewProperty);
				
			} 
			catch (Exception exc) {
				LOG.error("Error encountered while initializing the model instance!", exc);
			}		
		}
	}
	
	/**
	 * Use this method to release a this DomainObject.
	 * Released objects can act as cache and, therefore, instead of creating a new
	 * instance of a DomainObject from scratch, can improve performance.
	 */
	public void release() {
		getHome().release(this);
	}
	
	public String toString() {
		if (getKey() == null) {
			return Debug.classBOMMarkupString(getHome().getObjectClassName(), getPropertySet());
		}
		else {
			return Debug.classBOMMarkupString(getHome().getObjectClassName(), getKey());
		}
	}

	/**
	 * Insert the new DomainObject in the table of the DB.
	 * The default implementation is without commit (for safety reasons).
	 *
	 * @return Long The auto-generated value of the new entry or 0, if there's no autoincrement column. 
	 * @exception java.sql.SQLException
	 */
	public Long insert() throws SQLException, VException {
		return insert(false);
	}
	
	/**
	 * Insert the new DomainObject in the table of the DB.
	 *
	 * @param inCommit boolean Trigger a COMMIT after the insert?
	 * @return Long The auto-generated value of the new entry or 0, if there's no auto-increment column. 
	 * @exception java.sql.SQLException
	 */
	public Long insert(boolean inCommit) throws SQLException, VException {
		Collection<Long> lAutoKeys = Collections.emptyList();
		InsertStatement lStatement = null;
		Long outKey = new Long(0L);
		try { 
			lStatement = new InsertStatement(getHome());
			lStatement.setInserts(this.createInsertString());
			lAutoKeys = lStatement.executeInsert();
			if (inCommit) {
				lStatement.commit();
				reinitialize();
			}
		} 
		finally {
			if (lStatement != null) {				
				lStatement.close();
			}
		}
		if (!lAutoKeys.isEmpty()) {
			outKey = (Long)lAutoKeys.iterator().next();
			initKeyValue(outKey);
		}
		return outKey;
	}
	
	private void initKeyValue(Long inValue) throws VException {
		String[] lNumericTypes = new String[] {TypeDef.Number, TypeDef.BigInteger, TypeDef.Integer};		
		Collection<String> lAccepted = Arrays.asList(lNumericTypes);
		
		ObjectDef lObjectDef = getObjectDef();
		KeyDef lKeyDef = lObjectDef.getPrimaryKeyDef();
		for (String lKeyName : lKeyDef.getKeyNames2()) {			
			if (lAccepted.contains(lObjectDef.getPropertyDef(lKeyName).getValueType())) {
				set(lKeyName, inValue);
			}
		}
	}
	
	/**
	 * Updates the DomainObject.
	 * The default implementation is without commit (for safety reasons).
	 *
	 * @exception java.sql.SQLException 
	 */
	public void update() throws SQLException {
		update(false);	
	}
	
	/**
	 * Updates the DomainObject.
	 *
	 * @param inCommit boolean Trigger a COMMIT after the insert?
	 * @exception java.sql.SQLException 
	 */
	public void update(boolean inCommit) throws java.sql.SQLException {
	
		UpdateStatement lStatement = null;
		if (this.isChanged()) {
			try {
				lStatement = new UpdateStatement(getHome());
				lStatement.setUpdates(this.createUpdateString());
				lStatement.executeUpdate();
				if (inCommit) {
					lStatement.commit();
					reinitialize();
				}
			} 
			finally {
				if (lStatement != null)
					lStatement.close();
			}
		}	
	}

	/**
	 * Deletes the DomainObject from the table of the DB.
	 * The default implementation is without commit (for safety reasons).
	 * 
	 * @exception java.sql.SQLException
	 */
	public void delete() throws SQLException {
		delete(false);
	}

	/**
	 * Deletes the DomainObject from the table of the DB.
	 *
	 * @param inCommit boolean
	 * @exception java.sql.SQLException
	 */
	public void delete(boolean inCommit) throws SQLException {
	
		UpdateStatement lStatement = null;
		try {
			lStatement = new UpdateStatement(getHome());
			lStatement.setUpdates(this.createDeleteString());
			lStatement.executeUpdate();
			if (inCommit) {
				lStatement.commit();
			}
		} 
		finally {
			if (lStatement != null)
				lStatement.close();
		}
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(initialKey);
		out.writeObject(mode);
		out.writeBoolean(isLoaded);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		initialKey = (KeyObject)in.readObject();
		mode = (String)in.readObject();
		isLoaded = in.readBoolean();
	}

}
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
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.BOMInvalidKeyException;
import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.DomainObjectCache;
import org.hip.kernel.bom.DomainObjectCollection;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.DomainObjectIterator;
import org.hip.kernel.bom.DomainObjectVisitor;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.GroupByObject;
import org.hip.kernel.bom.HavingObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.LimitObject;
import org.hip.kernel.bom.OrderItem;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.Property;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.QueryStatement;
import org.hip.kernel.bom.SetOperatorHome;
import org.hip.kernel.bom.SettingException;
import org.hip.kernel.bom.impl.DomainObjectCacheImpl;
import org.hip.kernel.bom.impl.ModifierStrategy;
import org.hip.kernel.bom.impl.XMLSerializer;
import org.hip.kernel.bom.model.IMappingDefCreator;
import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.bom.model.MappingDefDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.ObjectDefDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.bom.model.impl.MappingDefImpl;
import org.hip.kernel.bom.model.impl.ObjectDefGenerator;
import org.hip.kernel.exc.DefaultExceptionWriter;
import org.hip.kernel.sys.Assert;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.util.SortableItem;
import org.hip.kernel.util.VInvalidValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract home class for LDAP objects, providing general functionality to retrieve entries from a LDAP server. 
 *
 * @author Luthiger
 * Created on 03.07.2007
 */
@SuppressWarnings("serial")
public abstract class LDAPObjectHome implements DomainObjectHome, Serializable {
	private static final Logger LOG = LoggerFactory.getLogger(LDAPObjectHome.class);
	
	private Vector<Object> testObjects;	
	private Vector<GeneralDomainObject>	releasedObjects	= null;
	private Hashtable<String, XMLSerializer> visitors = null;
	
	private	ObjectDef objectDef	= null;
	
	private LDAPAdapter adapter = null;

	// some optimizations
	private	Hashtable<String, String> tableNames = null;
	
	/** domain objects cache */
	private DomainObjectCache cache;
	private boolean useCache = false;
	
	/**
	 * LDAPObjectHome constructor
	 */
	public LDAPObjectHome() {
		super();
		adapter = new LDAPAdapter();
	}
	
	/**
	 * Returns the base directory where the objects of the LDAP server has to be retrieved.
	 * 
	 * @return String 
	 * @throws GettingException
	 */
	protected String getBaseDir() throws GettingException {
		return getObjectDef().get(ObjectDefDef.baseDir).toString();

	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#createQueryStatement()
	 */
	public QueryStatement createQueryStatement() {
		try {
			LDAPQueryStatement outStatement = new LDAPQueryStatement(this, getBaseDir());
			return outStatement;
		} 
		catch (GettingException exc) {
			DefaultExceptionWriter.printOut(this, exc, true);
			return null;
		}
	}
	
	private QueryStatement createQueryStatement(OrderObject inOrder, LimitObject inLimit) {
		LDAPQueryStatement outStatement = (LDAPQueryStatement)createQueryStatement();
		if (inLimit != null) {
			outStatement.setLimit(inLimit);
		}
		if (inOrder != null) {
			try {
				outStatement.setOrder(renderOrder(inOrder));
			} 
			catch (IOException exc) {
				DefaultExceptionWriter.printOut(this, exc, true);
			}
		}
		return outStatement;
	}
	
	private String[] renderOrder(OrderObject inOrder) {
		Collection<String> outSortBy = new Vector<String>();
		for (SortableItem lItem : inOrder.getItems2()) {
			outSortBy.add(getColumnNameFor(((OrderItem)lItem).getColumnName()));
		}		
		return outSortBy.toArray(new String[] {});
	}

	protected String createSelectAllString() {
		return adapter.createSelectAllString();
	}
	
	protected String createSelectString(KeyObject inKey) throws BOMException {
		return adapter.createSelectString(inKey, this);
	}
	
	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#createSelectString(org.hip.kernel.bom.SetOperatorHome, org.hip.kernel.bom.KeyObject)
	 */
	public void createSelectString(SetOperatorHome inSetHome, KeyObject inKey) throws BOMException {
		//intentionally left empty
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#createSelectString(org.hip.kernel.bom.SetOperatorHome, org.hip.kernel.bom.KeyObject, org.hip.kernel.bom.OrderObject)
	 */
	public void createSelectString(SetOperatorHome inSetHome, KeyObject inKey, OrderObject inOrder) throws BOMException {
		//intentionally left empty
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#createSelectString(org.hip.kernel.bom.SetOperatorHome, org.hip.kernel.bom.OrderObject)
	 */
	public void createSelectString(SetOperatorHome inSetHome, OrderObject inOrder) throws BOMException {
		//intentionally left empty
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getColumnNameFor(java.lang.String)
	 */
	public String getColumnNameFor(String inPropertyName) {
		try {
			PropertyDef lPropertyDef = getPropertyDef(inPropertyName);
			if (lPropertyDef != null) {
				MappingDef lMapping = lPropertyDef.getMappingDef();
				return lMapping.get(MappingDefDef.columnName).toString();
			}
			return getHidden(inPropertyName);
		} 
		catch (Exception exc) {
			DefaultExceptionWriter.printOut(this, exc, true);
			return "";
		}
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getCount()
	 */
	public int getCount() throws SQLException, BOMException {
		LOG.debug("Invoked getCount()");
		return ((LDAPQueryResult)select()).getCount();
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getCount(org.hip.kernel.bom.KeyObject)
	 */
	public int getCount(KeyObject inKeyObject) throws SQLException, BOMException {
		return ((LDAPQueryResult)select(inKeyObject)).getCount();
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getCount(org.hip.kernel.bom.KeyObject, org.hip.kernel.bom.HavingObject, org.hip.kernel.bom.GroupByObject)
	 */
	public int getCount(KeyObject inKeyObject, HavingObject inHaving, GroupByObject inGroupBy) throws SQLException, BOMException {
		return getCount(inKeyObject);
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getHidden(java.lang.String)
	 */
	public String getHidden(String inPropertyName) {
		return "";
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectDef()
	 */
	public ObjectDef getObjectDef() {
		if (objectDef == null) {
			objectDef= createObjectDef();
		}	
		return objectDef;
	}
	
	/**
	 * @return org.hip.kernel.bom.model.ObjectDef
	 */
	private synchronized ObjectDef createObjectDef() {	
		try {
			ObjectDef outObjectDef = ObjectDefGenerator.getSingleton().createObjectDef(
					getObjectDefString(), 
					new IMappingDefCreator() {
						public MappingDef createMappingDef() {
							return new LDAPMappingDef();
						}				
					}
			);
			return outObjectDef;
		} 
		catch (Exception exc) {
			DefaultExceptionWriter.printOut(this, exc, true);
			return null;
		}	
	}
	
	/**
	 * 	Returns the object definition string of the class managed	by this home.
	 * 	Concrete subclasses must implement this method.
	 * 
	 * 	@return java.lang.String
	 */
	protected abstract String getObjectDefString();

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getPropertyDef(java.lang.String)
	 */
	public PropertyDef getPropertyDef(String inPropertyName) {
		// Pre: inPropertyName not null
		if (VSys.assertNotNull( this, "getPropertyDef", inPropertyName) == Assert.FAILURE)
			return null;
			 
		return getObjectDef().getPropertyDef(inPropertyName);
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getPropertyDefFor(java.lang.String)
	 */
	public PropertyDef getPropertyDefFor(String inColumnName) {
		if (VSys.assertNotNull(this, "getPropertyDefFor", inColumnName) == Assert.FAILURE)
			return null;
			 
		try {
			for (PropertyDef lPropertyDef : getObjectDef().getPropertyDefs2()) {
				String lColumnName = (String)lPropertyDef.getMappingDef().get(MappingDefDef.columnName);
				if (inColumnName.equals(lColumnName)) {
					return lPropertyDef;
				}
			}
	
			// not found
			return null;
		} 
		catch (Exception exc) {
			DefaultExceptionWriter.printOut(this, exc, true);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getTestObjects()
	 */
	public Iterator<Object> getTestObjects() {
		return testObjects().iterator();
	}
	
	/**
	 * @return java.util.Vector
	 */
	private synchronized Vector<Object> testObjects() {	
		if (testObjects == null) {
			 testObjects = createTestObjects();
	
			 // Subclass has no implementation, we will not try again
			 if (testObjects == null) {
				 testObjects = new Vector<Object>();
			 }
		}
		return testObjects;
	}	
	
	/**
	 * This method can be implemented by concrete subclasses to
	 * create test objects.
	 *
	 * @return java.util.Vector
	 */
	protected Vector<Object> createTestObjects() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getUseCache()
	 */
	public boolean getUseCache() {
		return useCache;
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getVisitor(java.lang.String)
	 */
	public DomainObjectVisitor getVisitor(String inKey) {
		return visitors().get(inKey) ;
	}

	/**
	 * 	Returns the visitors which can be used by the DomainObjects.
	 * 
	 * 	@return java.util.Hashtable
	 */
	private Hashtable<String, XMLSerializer> visitors() {
		if (visitors == null) {
			 visitors = new Hashtable<String, XMLSerializer>();
	
			 // Register the known vistors
			 visitors.put(GeneralDomainObjectHome.xmlSerializer, new XMLSerializer());
		}
		return visitors;
	}
	
	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#initialize()
	 */
	public void initialize() {
		// intentionally left empty
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#release(org.hip.kernel.bom.GeneralDomainObject)
	 */
	public void release(GeneralDomainObject inObject) {
		inObject.setVirgin();
		releasedObjects().addElement(inObject);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.DomainObjectHome#findByKey(org.hip.kernel.bom.KeyObject)
	 */
	public DomainObject findByKey(KeyObject inKey) throws BOMNotFoundException, BOMInvalidKeyException {
		DomainObject outDomainObject = null;
		if (getUseCache() && inKey.isPrimaryKey()) {
			outDomainObject = (DomainObject)cache().get(inKey);
		}
		if (outDomainObject != null) return outDomainObject;
		
		QueryResult lResult = null;
		try { 	 
			lResult = select(inKey);
			outDomainObject = (DomainObject)lResult.nextAsDomainObject();
			lResult.close();
		}
		catch (SQLException exc){
			throw new BOMInvalidKeyException(exc.toString());
		}
		catch (BOMException exc){
			throw new BOMInvalidKeyException(exc.toString());
		}
		if (outDomainObject == null) {
			throw new BOMNotFoundException();
		}
		if (getUseCache() && inKey.isPrimaryKey()) {
			//cache it
			cache().put(outDomainObject);
		}
		return outDomainObject;
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#select()
	 */
	public QueryResult select() throws SQLException, BOMException {
		QueryStatement lStatement = createQueryStatement();
		lStatement.setSQLString(createSelectAllString());
		return select(lStatement);
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#select(java.lang.String)
	 */
	public QueryResult select(String inFilter) throws SQLException {
		if (VSys.assertNotNull(this, "select(String)", inFilter) == Assert.FAILURE)
			return new LDAPQueryResult(null, null, -1, null);
		
		QueryStatement lStatement = createQueryStatement();
		lStatement.setSQLString(inFilter);
		return select(lStatement);
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#select(org.hip.kernel.bom.KeyObject)
	 */
	public QueryResult select(KeyObject inKey) throws SQLException, BOMException {
		QueryStatement lStatement = createQueryStatement();
		lStatement.setSQLString(createSelectString(inKey));
		return select(lStatement);
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#select(org.hip.kernel.bom.KeyObject, org.hip.kernel.bom.OrderObject)
	 */
	public QueryResult select(KeyObject inKey, OrderObject inOrder) throws SQLException, BOMException {
		QueryStatement lStatement = createQueryStatement(inOrder, null);
		lStatement.setSQLString(createSelectString(inKey));
		return select(lStatement);
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#select(org.hip.kernel.bom.KeyObject, org.hip.kernel.bom.OrderObject, org.hip.kernel.bom.HavingObject)
	 */
	public QueryResult select(KeyObject inKey, OrderObject inOrder, HavingObject inHaving) throws SQLException, BOMException {
		return select(inKey, inOrder);
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#select(org.hip.kernel.bom.KeyObject, org.hip.kernel.bom.OrderObject, org.hip.kernel.bom.HavingObject, org.hip.kernel.bom.GroupByObject)
	 */
	public QueryResult select(KeyObject inKey, OrderObject inOrder, HavingObject inHaving, GroupByObject inGroupBy) throws SQLException, BOMException {
		return select(inKey, inOrder);
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#select(org.hip.kernel.bom.OrderObject)
	 */
	public QueryResult select(OrderObject inOrder) throws SQLException, BOMException {
		QueryStatement lStatement = createQueryStatement(inOrder, null);
		lStatement.setSQLString(createSelectAllString());
		return select(lStatement);
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#select(org.hip.kernel.bom.KeyObject, org.hip.kernel.bom.LimitObject)
	 */
	public QueryResult select(KeyObject inKey, LimitObject inLimit) throws SQLException, BOMException {
		QueryStatement lStatement = createQueryStatement(null, inLimit);
		lStatement.setSQLString(createSelectString(inKey));
		return select(lStatement);
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#select(org.hip.kernel.bom.QueryStatement)
	 */
	public QueryResult select(QueryStatement inStatement) throws SQLException {
		if (VSys.assertNotNull(this, "select(QueryStatement)", inStatement) == Assert.FAILURE)
			return new LDAPQueryResult(null, null, -1, null);
	
		LOG.debug("select {}", inStatement.getSQLString());
		QueryResult outResult = inStatement.executeQuery();
		return outResult;
	}

	public GeneralDomainObject newInstance(SearchResult inResult) throws BOMException {
		LDAPObject outObject = (LDAPObject)this.newInstance();
		outObject.loadFromResultSet(inResult);
		return outObject;
	}
	
	/**
	 * 	Returns an empty DomainObject. An empty object is
	 *	initialized but does not contain any values.
	 *	The object goes back into this state after releasing.
	 * 
	 * 	@return org.hip.kernel.bom.DomainObject
	 */
	public DomainObject newInstance() throws BOMException {	
		try { 
			//straightforward
			Iterator<GeneralDomainObject> lReleased = releasedObjects().iterator();
			if (lReleased.hasNext()) {
				DomainObject outDomainObject = (DomainObject)lReleased.next();
				lReleased.remove();
				return outDomainObject;
			} 
			else {
				Class<?> lClass = Class.forName(getObjectClassName());
				return (DomainObject)lClass.newInstance();
			}	
		}
		catch (ClassNotFoundException exc) {
			throw new BOMException("ClassNotFound " + exc.getMessage());
		}
		catch (Exception exc) {
			throw new BOMException(exc.getMessage());
		}	
	}
	
	private Vector<GeneralDomainObject> releasedObjects() {
		if (releasedObjects == null)
			releasedObjects = new Vector<GeneralDomainObject>();
		return releasedObjects;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.DomainObjectHome#create()
	 */
	public DomainObject create() throws BOMException {
		DomainObject outObject = newInstance() ;
		((LDAPObject)outObject).initializeForNew() ;
		return outObject;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObjectHome#createDeleteString(java.lang.String, org.hip.kernel.bom.DomainObject)
	 */
	public String createDeleteString(String inTableName, DomainObject inDomainObject) {
		return null;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObjectHome#createInsertString(java.lang.String, org.hip.kernel.bom.DomainObject)
	 */
	public String createInsertString(String inTableName, DomainObject inDomainObject) {
		return null;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObjectHome#createPreparedInserts()
	 */
	public Vector<String> createPreparedInserts() {
		return null;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObjectHome#createPreparedSelectString(org.hip.kernel.bom.KeyObject)
	 */
	public String createPreparedSelectString(KeyObject inKey) {
		return null;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObjectHome#createPreparedUpdate(org.hip.kernel.bom.KeyObject, org.hip.kernel.bom.KeyObject)
	 */
	public String createPreparedUpdate(KeyObject inChange, KeyObject inWhere) {
		return null;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObjectHome#createPreparedUpdateString(java.lang.String, org.hip.kernel.bom.DomainObject)
	 */
	public String createPreparedUpdateString(String inTableName, DomainObject inDomainObject) {
		return null;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObjectHome#createPreparedUpdates()
	 */
	public Vector<String> createPreparedUpdates() {
		return null;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObjectHome#createUpdateString(java.lang.String, org.hip.kernel.bom.DomainObject)
	 */
	public String createUpdateString(String inTableName, DomainObject inDomainObject) {
		return null;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObjectHome#createUpdateString(java.lang.String, org.hip.kernel.bom.DomainObject)
	 */	
	public String createUpdateString(KeyObject inChange, KeyObject inWhere) {
		return null;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObjectHome#delete(org.hip.kernel.bom.KeyObject, boolean)
	 */
	public void delete(KeyObject inKey, boolean inCommit) throws SQLException {
		//intentionally left empty		
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObjectHome#getMax(java.lang.String)
	 */
	public BigDecimal getMax(String inColumnName) throws SQLException, BOMException {
		return null;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObjectHome#getMax(java.lang.String, org.hip.kernel.bom.KeyObject)
	 */
	public BigDecimal getMax(String inColumnName, KeyObject inKey) throws SQLException, BOMException {
		return null;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObjectHome#getModified(org.hip.kernel.bom.impl.ModifierStrategy)
	 */
	public Collection<Object> getModified(ModifierStrategy inStrategy) throws SQLException {
		return null;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObjectHome#getModified(org.hip.kernel.bom.impl.ModifierStrategy, org.hip.kernel.bom.KeyObject)
	 */
	public Collection<Object> getModified(ModifierStrategy inStrategy, KeyObject inKey) throws SQLException {
		return null;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.kernel.bom.DomainObjectHome#remove(org.hip.kernel.bom.KeyObject)
	 */
	public void remove(KeyObject inKey) throws BOMNotFoundException, BOMInvalidKeyException {
		//intentionally left empty		
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.DomainObjectHome#tableNames()
	 */
	public Hashtable<String, String> tableNames() {
		if (tableNames == null) {
			tableNames = new Hashtable<String, String>(7);
			try {
				for (PropertyDef lPropertyDef : getObjectDef().getPropertyDefs2()) {
					String lTableName = (String)lPropertyDef.getMappingDef().get("");
					if (!tableNames.containsKey(lTableName)) {
						tableNames.put(lTableName, lTableName);
					}
				}
			} 
			catch (Exception exc) {
				DefaultExceptionWriter.printOut(this, exc, true);
			} // try-catch
		} // if
		return tableNames;	
	}
	
	/**
	 * Not applicable here.
	 * 
	 * @see org.hip.kernel.bom.DomainObjectHome#checkStructure(java.lang.String)
	 */
	public boolean checkStructure(String inSchemaPattern) throws SQLException, NamingException {
		return false;
	}

	/**
	 * Returns the attribute's name mapped to the domain object's key.
	 * 
	 * @return String the name of the key column/attribute, e.g. 'cn'.
	 */
	protected String getKeyColumn() {
		return getColumnNameFor(getObjectDef().getPrimaryKeyDef().getKeyName(0));
	}
	
	/**
	 * Returns the domain object cache.
	 * 
	 * @return org.hip.kernel.bom.DomainObjectCache
	 */
	protected DomainObjectCache cache() {
		if (cache == null){
			cache = new DomainObjectCacheImpl();
		}	
		return cache;
	}
	
	/**
	 * Sets cache to null.
	 */
	public void clearCache() {
		cache = null;	
	}

	/**
	 * Loads cache with domainObject from the collection.
	 * This is a simple way to preload the cache.
	 * 
	 * @param inDomainObjectCollection org.hip.kernel.bom.DomainObjectCollection
	 */
	protected void loadCache(DomainObjectCollection inDomainObjectCollection) {
	
		//pre: parameter not null
		if (VSys.assertNotNull(this,"loadCache", inDomainObjectCollection)) return;
	
		DomainObjectIterator lDomainObjects = inDomainObjectCollection.elements();
		while (lDomainObjects.hasMoreElements()){
			cache().put(lDomainObjects.nextElement());
		}
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#setUseCache(boolean)
	 */
	public void setUseCache(boolean inUseCache) {
		useCache = inUseCache;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(objectDef);
		out.writeObject(tableNames);
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		objectDef = (ObjectDef)in.readObject();
		tableNames = (Hashtable<String, String>)in.readObject();
		adapter = new LDAPAdapter();
	}
	
//	---
	/**
	 * We don't want the 'columns' uppercase, therefore, we create a <code>MappingDef</code> class and override the method MappingDef.set().
	 */
	private class LDAPMappingDef extends MappingDefImpl {		
		@Override
		public void set(String inName, Object inValue) throws SettingException {
			Property lProperty = (Property)propertySet().get(inName);
			try { 
				lProperty.setValue(inValue);
			} 
			catch (VInvalidValueException exc) {
				throw new SettingException(exc.getMessage());
			}
		}
	}
	
}

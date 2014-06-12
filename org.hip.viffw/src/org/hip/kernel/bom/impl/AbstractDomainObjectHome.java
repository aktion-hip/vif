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

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.DomainObjectCache;
import org.hip.kernel.bom.DomainObjectCollection;
import org.hip.kernel.bom.DomainObjectIterator;
import org.hip.kernel.bom.DomainObjectVisitor;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.GroupByObject;
import org.hip.kernel.bom.HavingObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.LimitObject;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.QueryStatement;
import org.hip.kernel.bom.SetOperatorHome;
import org.hip.kernel.bom.SettingException;
import org.hip.kernel.bom.SingleValueQueryStatement;
import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.bom.model.MappingDefDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.bom.model.PropertyDefDef;
import org.hip.kernel.bom.model.TypeDef;
import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.kernel.exc.DefaultExceptionWriter;
import org.hip.kernel.sys.Assert;
import org.hip.kernel.sys.VObject;
import org.hip.kernel.sys.VSys;

/**
 * This class implements the DomainObjectHome interface.
 *
 * @author Benno Luthiger
 * @see org.hip.kernel.bom.DomainObjectHome
 */
abstract public class AbstractDomainObjectHome extends VObject implements GeneralDomainObjectHome, Serializable {
	// Instance variables
	private Vector<Object>						testObjects			= null;
	private Vector<GeneralDomainObject>			releasedObjects		= null;
	private Hashtable<String, XMLSerializer>	visitors			= null;

	/** domain objects cache */
	private DomainObjectCache cache;

	/** flag for using read cached domain object */
	private boolean useCache = false;

	/**
	 * DomainObjectHomeImpl: default constructor
	 */
	protected AbstractDomainObjectHome() {
		super();
		initialize();
	}
	
	/**
	 * Returns the domainobject cache.
	 * 
	 * @return org.hip.kernel.bom.DomainObjectCache
	 */
	protected DomainObjectCache cache() {
		synchronized (cache) {
			if (cache == null){
				cache = new DomainObjectCacheImpl();
			}	
		}
		return cache;
	}
	
	/**
	 * Sets cache to null.
	 */
	public void clearCache() {
		synchronized (cache) {
			cache = null;	
		}
	}
	
	/**
	 * Creates the select string to fetch all domain objects. 
	 *
	 * @return java.lang.String
	 * @throws org.hip.kernel.bom.BOMException
	 */
	protected abstract String createSelectAllString() throws BOMException;
	
	/**
	 * Creates the select string to fetch all domain objects matching
	 * the specified key.
	 *
	 * @return java.lang.String
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @throws org.hip.kernel.bom.BOMException
	 */
	protected abstract String createSelectString(KeyObject inKey) throws BOMException;
	
	/**
	 * Creates the select string to fetch all domain objects matching
	 * the specified key ordered by the specified object.
	 *
	 * @return java.lang.String
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @param inOrder org.hip.kernel.bom.OrderObject
	 * @throws org.hip.kernel.bom.BOMException
	 */
	protected abstract String createSelectString(KeyObject inKey, OrderObject inOrder) throws BOMException;
	protected abstract String createSelectString(KeyObject inKey, OrderObject inOrder, HavingObject inHaving) throws BOMException;
	protected abstract String createSelectString(KeyObject inKey, OrderObject inOrder, HavingObject inHaving, GroupByObject inGroupBy) throws BOMException;
	
	/**
	 * Creates the select string to fetch all domain objects 
	 * ordered by the specified object.
	 *
	 * @return java.lang.String
	 * @param inOrder org.hip.kernel.bom.OrderObject
	 * @throws org.hip.kernel.bom.BOMException
	 */
	protected abstract String createSelectString(OrderObject inOrder) throws BOMException;
	
	/**
	 * Creates the select string to fetch all domain objects matching
	 * the specified key limitied by the specified limit.
	 *  
	 * @param inKey KeyObject
	 * @param inLimit LimitObject
	 * @return String
	 * @throws BOMException
	 */
	protected abstract String createSelectString(KeyObject inKey, LimitObject inLimit) throws BOMException;
	
	/**
	 * Creates a select string for a SQL UNION query.
	 * 
	 * @param inSetHome SetOperatorHome
	 * @param inKey KeyObject
	 * @throws BOMException
	 */
	public void createSelectString(SetOperatorHome inSetHome, KeyObject inKey) throws BOMException {
		inSetHome.setSelectString(createSelectString(inKey));
		inSetHome.setCountString(createCountString(inKey));
	}
	/**
	 * @param inSetHome SetOperatorHome
	 * @param inKey KeyObject
	 * @param inOrder OrderObject
	 * @throws BOMException
	 */
	public void createSelectString(SetOperatorHome inSetHome, KeyObject inKey, OrderObject inOrder) throws BOMException {
		inSetHome.setSelectString(createSelectString(inKey, inOrder));
		inSetHome.setCountString(createCountString(inKey));
	}
	/**
	 * @param inSetHome SetOperatorHome
	 * @param inOrder OrderObject
	 * @throws BOMException
	 */
	public void createSelectString(SetOperatorHome inSetHome, OrderObject inOrder) throws BOMException {
		inSetHome.setSelectString(createSelectString(inOrder));
		inSetHome.setCountString(createCountAllString());
	}
	
	/**
	 * Creates the select sql string counting all table entries 
	 * corresponding to this home.
	 *
	 * @return java.lang.String
	 * @throws org.hip.kernel.bom.BOMException
	 */
	protected abstract String createCountAllString() throws BOMException;
	
	/**
	 * Creates the select sql string counting all table entries 
	 * corresponding to this home and the specified key.
	 *
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @return java.lang.String
	 * @throws org.hip.kernel.bom.BOMException
	 */
	protected abstract String createCountString(KeyObject inKey) throws BOMException;
	protected abstract String createCountString(KeyObject inKey, HavingObject inHaving, GroupByObject inGroupBy) throws BOMException;
	
	/**
	 * This method looks for all key columns of the table mapped to the
	 * DomainObject managed by this home and creates a list COUNT(keyField).
	 * Instead of sending SELECT COUNT(*) FROM ... this SQL-sequence can be
	 * used to count all entries in a table with better performance.
	 *
	 * @return java.lang.String
	 */
	protected abstract String createKeyCountColumnList();
	
	/**
	 * This method creates a QueryStatement as part of the frameworks QueryService.
	 *
	 * @return org.hip.kernel.bom.QueryStatement
	 */
	public QueryStatement createQueryStatement() {
		return new DefaultQueryStatement(this);
	}
	
	/**
	 * This method must be implemented by concrete subclasses to
	 * create test objects.
	 *
	 * @return java.util.Vector
	 */
	protected abstract Vector<Object> createTestObjects();
	
	/**
	 * Returns the name of the column which is mapped to the inputed property
	 *
	 * @return java.lang.String
	 * @param inPropertyName java.lang.String
	 */
	public String getColumnNameFor(String inPropertyName)  {
	
		try {
			PropertyDef lPropertyDef = getPropertyDef(inPropertyName);
			if (lPropertyDef != null) {
				MappingDef lMapping = lPropertyDef.getMappingDef();
				String lTableName = (String)lMapping.get(MappingDefDef.tableName);
				if (lTableName.length() == 0) {
					return (String)lMapping.get(MappingDefDef.columnName);
				}
				return lTableName + "." + (String)lMapping.get(MappingDefDef.columnName);
			}
			return getHidden(inPropertyName);
		} 
		catch (Exception exc) {
			DefaultExceptionWriter.printOut(this, exc, true);
			return "";
		}
	}

	/**
	 * Returns number of entries in database corresponding to this
	 * home.
	 *
	 * @return int numer of database entries.
	 * @throws java.sql.SQLException
	 * @throws org.hip.kernel.bom.BOMException
	 */
	public int getCount() throws SQLException, BOMException {
		SingleValueQueryStatement lStatement = createSingleValueQueryStatement();
		return lStatement.executeQuery(createCountAllString()).intValue();
	}

	/**
	 * Returns number of entries in database corresponding to this
	 * home and the given key.
	 *
	 * @return int numer of database entries.
	 * @param org.hip.kernel.bom.KeyObject the (partial key)
	 * @throws java.sql.SQLException
	 * @throws org.hip.kernel.bom.BOMException
	 */
	public int getCount(KeyObject inKey) throws SQLException, BOMException {
		SingleValueQueryStatement lStatement = createSingleValueQueryStatement();
		return lStatement.executeQuery(createCountString(inKey)).intValue();
	}
	
	/**
	 * Returns number of entries in database corresponding to this
	 * home and the given filter.
	 * <b>Note:</b> You can pass empty filtering objects e.g. if you
	 * only need GROUP BY.
	 * 
	 * @param inKeyObject KeyObject
	 * @param inHaving HavingObject
	 * @param inGroupBy GroupByObject
	 * @return int
	 * @throws SQLException
	 * @throws BOMException
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getCount
	 */
	public int getCount(KeyObject inKeyObject, HavingObject inHaving, GroupByObject inGroupBy) throws SQLException, BOMException {
		return 0;
	}

	/**
	 * Returns the name of the objects which the concrete home can create.
	 * This method is abstract and must be implemented by concrete subclasses.
	 *
	 * @return java.lang.String
	 */
	abstract public String getObjectClassName() ;

	/**
	 * Returns the object definition string of the class managed by this home.
	 * Concrete subclasses must implement this method.
	 * 
	 * @return java.lang.String
	 */
	protected abstract String getObjectDefString();

	/**
	 * This method returns the PropertyDef object which describes
	 * the behaviour of the Property object named inPropertyName.
	 *
	 * @return org.hip.kernel.bom.model.PropertyDef
	 * @param inPropertyName java.lang.String
	 */
	public PropertyDef getPropertyDef( String inPropertyName ) {
	
		// Pre: inPropertyName not null
		if (VSys.assertNotNull( this, "getPropertyDef", inPropertyName) == Assert.FAILURE)
			return null;
			 
		return getObjectDef().getPropertyDef( inPropertyName );
	}

	/**
	 * This method returns the PropertyDef object which describes
	 * the behaviour of the Property object mapped to the table's column
	 * named inColumnName.
	 *
	 * @return	org.hip.kernel.bom.model.PropertyDef 
	 * @param	inColumnName java.lang.String
	 */
	public PropertyDef getPropertyDefFor(String inColumnName)  {
	
		if (VSys.assertNotNull(this, "getPropertyDefFor", inColumnName) == Assert.FAILURE)
			return null;
			 
		try {
			for (PropertyDef lPropertyDef : getObjectDef().getPropertyDefs2()) {
				String lColumnName = (String)lPropertyDef.getMappingDef().get(MappingDefDef.columnName);
				if (inColumnName.equalsIgnoreCase(lColumnName)) {
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

	/**
	 * Returns the testObjects created by the Homes.
	 *
	 * @return java.util.Iterator
	 */
	public Iterator<Object> getTestObjects() {
		return testObjects().iterator();
	}

	/**
	 * Returns true if this domain object home caches the domain objects
	 * fetched from database over findByKey.
	 * 
	 * @return boolean
	 */
	public boolean getUseCache() {
		return useCache;
	}

	/**
	 * 	Returns an instance of the visitor named inKey.
	 *  This visitor can be used to serialize the DomainObject managed by 
	 *  the Home class.
	 * 
	 * 	@return org.hip.kernel.bom.DomainObjectVisitor
	 * 	@param inKey java.lang.String
	 */
	public DomainObjectVisitor getVisitor(String inKey) {
		return visitors().get(inKey) ;
	}

	/**
	 * This method can be used to initialize a new instance of the class.
	 */
	public void initialize() {
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

	/**
	 * 	Returns a GeneralDomainObject filled with the values of the inputed ResultSet
	 * 
	 * 	@return org.hip.kernel.bom.GeneralDomainObject
	 *  @param inResult java.sql.ResultSet
	 */
	protected abstract GeneralDomainObject newInstance(ResultSet inResult) throws BOMException;

	/**
	 * Use this method to release a DomainObject.
	 * Released objects can act as cache and, therefore, instead of creating a new
	 * instance of a DomainObject from scratch, can improve performance.
	 * 
	 * @param inObject org.hip.kernel.bom.GeneralDomainObject
	 */
	public void release(GeneralDomainObject inObject) {
		inObject.setVirgin();
		releasedObjects().addElement(inObject);
	}

	/**
	 * Returns all released DomainObjects managed by this home.
	 * Released objects can act as cache and, therefore, instead of creating a new
	 * instance of a DomainObject from scratch, can improve performance.
	 * 
	 * @return java.util.Vector
	 */
	protected Vector<GeneralDomainObject> releasedObjects() {
		if (releasedObjects == null)
			releasedObjects = new Vector<GeneralDomainObject>();
		return releasedObjects;
	}

	/**
	 * This method selects all domain objects of the corresponding 
	 * table or tables (if join).
	 * The returned domain objects are ordered by the table's natural order.
	 *
	 * @return org.hip.kernel.bom.QueryResult
	 * @throws java.sql.SQLException
	 * @throws org.hip.kernel.bom.BOMException
	 */
	public QueryResult select() throws SQLException, BOMException {
		 QueryStatement lStatement = this.createQueryStatement();
		 lStatement.setSQLString(this.createSelectAllString());
		 return this.select(lStatement);
	}

	/**
	 * This method allows to invoke a query. It's a normal version of a select. 
	 * It takes as argument a SQL-string. 
	 * 
	 * @return org.hip.kernel.bom.QueryResult
	 * @param inSQL java.lang.String
	 * @throws java.sql.SQLException 
	 */
	public QueryResult select(String inSQL) throws SQLException {
		if (VSys.assertNotNull(this, "select(String)", inSQL) == Assert.FAILURE)
			return new DefaultQueryResult(null, null, null);
	
		QueryStatement lStatement = this.createQueryStatement();
		lStatement.setSQLString(inSQL);
		return this.select(lStatement);
	}

	/**
	 * This method selects all domain objects of the corresponding 
	 * table matching the specified key.
	 * The returned domain objects are ordered by the table's natural order.
	 * 
	 * @return org.hip.kernel.bom.QueryResult
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @throws java.sql.SQLException
	 * @throws org.hip.kernel.bom.BOMException
	 */
	public QueryResult select(KeyObject inKey) throws SQLException, BOMException {
		QueryStatement lStatement = this.createQueryStatement();
		lStatement.setSQLString(this.createSelectString(inKey));
		return this.select(lStatement);
	}
	
	/**
	 * This method selects all domain objects of the corresponding 
	 * table matching the specified key.
	 * The returned domain objects are ordered according the specified order object.
	 * 
	 * @return org.hip.kernel.bom.QueryResult
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @param inOrder org.hip.kernel.bom.OrderObject
	 * @throws java.sql.SQLException
	 * @throws org.hip.kernel.bom.BOMException
	 */
	public QueryResult select(KeyObject inKey, OrderObject inOrder) throws SQLException, BOMException {
		QueryStatement lStatement = this.createQueryStatement();
		lStatement.setSQLString(this.createSelectString(inKey, inOrder));
		return this.select(lStatement);
	}
	
	/**
	 * This method selects all domain objects of the corresponding 
	 * table matching the specified key meeting the specified HAVING clause.
	 * The returned domain objects are ordered according the specified order object.
	 * <b>Note:</b> You can provide empty key and order objects. 
	 * 
	 * @return org.hip.kernel.bom.QueryResult
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @param inOrder org.hip.kernel.bom.OrderObject
	 * @param inHaving org.hip.kernel.bom.HavingObject
	 * @throws java.sql.SQLException
	 * @throws org.hip.kernel.bom.BOMException
	 */
	public QueryResult select(KeyObject inKey, OrderObject inOrder, HavingObject inHaving) throws SQLException, BOMException {
		QueryStatement lStatement = this.createQueryStatement();
		lStatement.setSQLString(this.createSelectString(inKey, inOrder, inHaving));
		return this.select(lStatement);
	}
	
	/**
	 * This method selects all domain objects of the corresponding 
	 * table matching the specified key meeting the specified HAVING clause.
	 * The returned domain objects are grouped and ordered according 
	 * the specified group and order objects respectively.
	 * <b>Note:</b> You can provide empty key and order objects etc. 
	 * 
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @param inOrder org.hip.kernel.bom.OrderObject
	 * @param inHaving org.hip.kernel.bom.HavingObject
	 * @param inGroupBy org.hip.kernel.bom.GroupByObject
	 * @return org.hip.kernel.bom.QueryResult
	 * @throws java.sql.SQLException
	 * @throws org.hip.kernel.bom.BOMException
	 */
	public QueryResult select(KeyObject inKey, OrderObject inOrder, HavingObject inHaving, GroupByObject inGroupBy) throws SQLException, BOMException {
		QueryStatement lStatement = this.createQueryStatement();
		lStatement.setSQLString(this.createSelectString(inKey, inOrder, inHaving, inGroupBy));
		return this.select(lStatement);
	}
	
	/**
	 * This method selects all domain objects of the corresponding 
	 * table or tables (if join).
	 * The returned domain objects are ordered according the specified order object.
	 * 
	 * @return org.hip.kernel.bom.QueryResult
	 * @param inOrder org.hip.kernel.bom.OrderObject
	 * @throws java.sql.SQLException
	 * @throws org.hip.kernel.bom.BOMException
	 */
	public QueryResult select(OrderObject inOrder) throws SQLException, BOMException {
		QueryStatement lStatement = this.createQueryStatement();
		lStatement.setSQLString(this.createSelectString(inOrder));
		return this.select(lStatement);
	}
	
	public QueryResult select(KeyObject inKey, LimitObject inLimit) throws SQLException, BOMException {
		QueryStatement lStatement = this.createQueryStatement();
		lStatement.setSQLString(this.createSelectString(inKey, inLimit));
		return this.select(lStatement);		
	}
	
	/**
	 * 	This method allows to invoke a query. It's a normal version of a select. 
	 *  It takes as argument a QueryStatement. 
	 * 
	 * 	@return org.hip.kernel.bom.QueryResult
	 * 	@param inStatement org.hip.kernel.bom.QueryStatement
	 * 	@exception java.sql.SQLException The exception description.
	 */
	public QueryResult select(QueryStatement inStatement) throws SQLException {
		if (VSys.assertNotNull(this, "select(QueryStatement)", inStatement) == Assert.FAILURE)
			return new DefaultQueryResult(null, null, null);
	
		QueryResult outResult = inStatement.executeQuery();
		return outResult;
	}

	/**
	 * This method fills a DomainObject with the data from a ResultSet.
	 * 
	 * @param inObject org.hip.kernel.bom.GeneralDomainObject
	 * @param inResult java.sql.ResultSet
	 */
	protected void setFromResultSet(GeneralDomainObject inObject, ResultSet inResult ) throws SQLException, SettingException {
	
		ResultSetMetaData lMetaData = inResult.getMetaData();
	
		for (int i = 1; i <= lMetaData.getColumnCount(); i++ ) {
			String lColumnName = lMetaData.getColumnName(i);
			PropertyDef lProperty = this.getPropertyDefFor( lColumnName );
			if ( lProperty != null ) {
				try {
				 	String lName = (String) lProperty.get(PropertyDefDef.propertyName);
				 	String lType = ((String) lProperty.get(PropertyDefDef.valueType)).intern();
				 	
				 	if (lType == TypeDef.String) { 
					 	 inObject.set( lName, inResult.getString(i));
				 	} 
				 	else if (lType == TypeDef.LongVarchar) { 
					 	 inObject.set( lName, inResult.getAsciiStream(i));
				 	} 
				 	else if (lType == TypeDef.Date) {
					 	 inObject.set( lName, inResult.getDate(i));
				 	} 
				 	else if (lType == TypeDef.Timestamp) {
					 	 inObject.set( lName, inResult.getTimestamp(i));
				 	} 
				 	else if (lType == TypeDef.Integer) {
					 	 inObject.set(lName, new Integer(inResult.getInt(i)));
				 	} 
				 	else if (lType == TypeDef.BigInteger) {	
					 	 inObject.set(lName, new java.math.BigInteger(inResult.getString(i)));
				 	} 
				 	else if (lType == TypeDef.BigDecimal) {
					 	 inObject.set(lName, new java.math.BigDecimal( inResult.getString(i)));
				 	} 	 					 	 
				 	else if (lType == TypeDef.Number) {
					 	 inObject.set(lName, new java.math.BigDecimal( inResult.getString(i)));
				 	} 	 					 	 
				} 
				catch (GettingException exc) {
					DefaultExceptionWriter.printOut(this, exc, true);
				} // catch
			} // if
		} // for	
	}

	/**
	 * If set true, the domain object home will cache domain objects fetched
	 * from the database with the findeByKey.
	 * 
	 * @param inUseCache boolean
	 */
	public void setUseCache(boolean inUseCache) {
		useCache = inUseCache;
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
	
	/**
	 * Creates a <code>SingleValueQueryStatement</code>.
	 * Subclasses may override, e.g. to provide a modified implementation of <code>SingleValueQueryStatement</code>.
	 * 
	 * @return SingleValueQueryStatement
	 */
	protected SingleValueQueryStatement createSingleValueQueryStatement() {
		return new SingleValueQueryStatementImpl();
	}
	
	/**
	 * Returns the <code>DBAdapterType</code> matching the actual DB access configuration.<br/>
	 * Subclasses may override, e.g. to provide the adapter type for a configuration of an external DB access.
	 * 
	 * @return {@link DBAdapterType}
	 */
	protected DBAdapterType retrieveDBAdapterType() {
		return DataSourceRegistry.INSTANCE.getAdapterType();
	}
	
}
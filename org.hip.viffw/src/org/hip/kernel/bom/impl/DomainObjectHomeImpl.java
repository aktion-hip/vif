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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.BOMInvalidKeyException;
import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DBAdapterSimple;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.GroupByObject;
import org.hip.kernel.bom.HavingObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.LimitObject;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.QueryStatement;
import org.hip.kernel.bom.SingleValueQueryStatement;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.bom.model.impl.ObjectDefGenerator;
import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.kernel.exc.DefaultExceptionWriter;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.Assert;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.util.ListJoiner;

/**
 * 	This class implements the DomainObjectHome interface.
 *
 *	@author Benno Luthiger
 *	@see org.hip.kernel.bom.DomainObjectHome
 */
@SuppressWarnings("serial")
abstract public class DomainObjectHomeImpl extends AbstractDomainObjectHome implements DomainObjectHome {
	
	// Instance variables
	private	ObjectDef		objectDef	= null;
	private	DomainObject	temporary	= null;
	private	DBAdapterSimple dbAdapter	= null;

	// some optimizations
	private	Hashtable<String, String> tableNames	= null;

	/**
	 * DomainObjectHomeImpl: default constructor
	 */
	protected DomainObjectHomeImpl() {
		super();
		dbAdapter = getDBAdapter();
	}
	
	/**
	 * @return org.hip.kernel.bom.model.ObjectDef
	 */
	private synchronized ObjectDef createObjectDef() {
	
		try {
			ObjectDef outObjectDef = ObjectDefGenerator.getSingleton().createObjectDef(getObjectDefString());
			return outObjectDef;
		} 
		catch (Exception exc) {
			DefaultExceptionWriter.printOut(this, exc, true);
			return null;
		}	
	}

	/**
	 * This method creates a new instance of a DomainObject
	 *
	 * @return org.hip.kernel.bom.DomainObject
	 */
	public DomainObject create() throws BOMException {
		DomainObject outObject = newInstance() ;
		((DomainObjectImpl)outObject).initializeForNew() ;
		return outObject;
	}
	
	/**
	 * Creates select sql string counting all table entries 
	 * corresponding to this home.
	 *
	 * @return java.lang.String
	 */
	protected String createCountAllString() {
		return dbAdapter.createCountAllString(this);
	}
	
	/**
	 * Creates select sql string counting all table entries 
	 * corresponding to this home.
	 *
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @return java.lang.String
	 */
	protected String createCountString(KeyObject inKey) {
		return dbAdapter.createCountString(inKey, this);
	}
	protected String createCountString(KeyObject inKey, HavingObject inHaving, GroupByObject inGroupBy) throws BOMException {
		return dbAdapter.createCountString(inKey, inHaving, inGroupBy, this);		
	}
	
	/**
	 * Returns the maximum value of the specified column corresponding to this home.
	 * 
	 * @param inColumnName java.lang.String
	 * @return java.math.BigDecimal
	 * @throws java.sql.SQLException
	 * @throws org.hip.kernel.bom.BOMException
	 */ 
	public BigDecimal getMax(String inColumnName) throws SQLException, BOMException {
		SingleValueQueryStatement lStatement = createSingleValueQueryStatement();
		ModifierStrategy lStrategy = new ModifierStrategy(inColumnName, ModifierStrategy.MAX);
		return lStatement.executeQuery(dbAdapter.createModifiedString(lStrategy, this));
	}
	
	/**
	 * Returns a collection of calculated values according to the modifiers strategy passed.
	 * 
	 * @param inStrategy ModifierStrategy
	 * @return Collection containing <code>BigDecimal</code>s or <code>Timestamp</code>s.
	 * @throws SQLException
	 */
	public Collection<Object> getModified(ModifierStrategy inStrategy) throws SQLException {
		SingleValueQueryStatement lStatement = createSingleValueQueryStatement();
		return lStatement.executeQuery2(dbAdapter.createModifiedString(inStrategy, this));
	}
	
	/**
	 * Returns the maximum value of the specified column 
	 * corresponding to this home and the specified key.
	 * 
	 * @param inColumnName java.lang.String
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @return java.math.BigDecimal
	 * @throws java.sql.SQLException
	 * @throws org.hip.kernel.bom.BOMException
	 */ 
	public BigDecimal getMax(String inColumnName, KeyObject inKey) throws SQLException, BOMException {
		SingleValueQueryStatement lStatement = createSingleValueQueryStatement();
		ModifierStrategy lStrategy = new ModifierStrategy(inColumnName, ModifierStrategy.MAX);
		return lStatement.executeQuery(dbAdapter.createModifiedString(lStrategy, inKey, this));
	}
	
	/**
	 * Returns a collection of calculated values according to the modifiers strategy passed.
	 * 
	 * @param inStrategy ModifierStrategy
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @return Collection containing <code>BigDecimal</code>s or <code>Timestamp</code>s.
	 * @throws SQLException
	 */
	public Collection<Object> getModified(ModifierStrategy inStrategy, KeyObject inKey) throws SQLException {
		SingleValueQueryStatement lStatement = createSingleValueQueryStatement();
		return lStatement.executeQuery2(dbAdapter.createModifiedString(inStrategy, inKey, this));
	}
	
	/**
	 * This method looks for all key columns of the table mapped to the
	 * DomainObject managed by this home and creates a list COUNT(keyField).
	 * Instead of sending SELECT COUNT(*) FROM ... this SQL-sequence can be
	 * used to count all entries in a table with better performance.
	 *
	 * @return java.lang.String
	 */
	protected String createKeyCountColumnList() {
		return dbAdapter.createKeyCountColumnList(this);
	}
	
	/**
	 * Creates the prepared select SQL string to fetch all domain objects with the
	 * specified key managed by this home.
	 *
	 * @return java.lang.String
	 * @param inKey org.hip.kernel.bom.KeyObject
	 */
	public String createPreparedSelectString(KeyObject inKey) {
		return dbAdapter.createPreparedSelectString(inKey, this);
	}
	
	/**
	 * Creates select string to fetch all domain objects. 
	 *
	 * @return java.lang.String
	 * @throws org.hip.kernel.bom.BOMException
	 */
	protected String createSelectAllString() throws BOMException {
		return dbAdapter.createSelectAllString();
	}
	
	/**
	 * Creates select SQL string to fetch all domain objects with the
	 * specified key managed by this home.
	 *
	 * @return java.lang.String
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @throws org.hip.kernel.bom.BOMException
	 */
	protected String createSelectString(KeyObject inKey) throws BOMException {
		return dbAdapter.createSelectString(inKey, this);
	}
	
	/**
	 * Creates the select string to fetch all domain objects matching
	 * the specified key ordered by the specified object.
	 *
	 * @return java.lang.String
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @param inOrder org.hip.kernel.bom.OrderObject
	 * @throws org.hip.kernel.bom.BOMException
	 */
	protected String createSelectString(KeyObject inKey, OrderObject inOrder) throws BOMException {
		return dbAdapter.createSelectString(inKey, inOrder, this);
	}
	
	/**
	 * @return java.lang.String
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @param inOrder org.hip.kernel.bom.OrderObject
	 * @param inHaving org.hip.kernel.bom.HavingObject
	 * @throws org.hip.kernel.bom.BOMException
	 */
	protected String createSelectString(KeyObject inKey, OrderObject inOrder, HavingObject inHaving) throws BOMException {
		return dbAdapter.createSelectString(inKey, inOrder, inHaving, this);
	}
	
	/**
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @param inOrder org.hip.kernel.bom.OrderObject
	 * @param inHaving org.hip.kernel.bom.HavingObject
	 * @param inGroupBy org.hip.kernel.bom.GroupByObject
	 * @return java.lang.String
	 */
	protected String createSelectString(KeyObject inKey, OrderObject inOrder, HavingObject inHaving, GroupByObject inGroupBy) throws BOMException {
		return dbAdapter.createSelectString(inKey, inOrder, inHaving, inGroupBy, this);
	}
	
	/**
	 * Creates the select string to fetch all domain objects 
	 * ordered by the specified object.
	 *
	 * @return java.lang.String
	 * @param inOrder org.hip.kernel.bom.OrderObject
	 * @throws org.hip.kernel.bom.BOMException
	 */
	protected String createSelectString(OrderObject inOrder) throws BOMException {
		return dbAdapter.createSelectString(inOrder, this);
	}
	
	/**
	 * Creates the select string to fetch all domain objects matching
	 * the specified key limitied by the specified limit.
	 *  
	 * @param inKey KeyObject
	 * @param inLimit LimitObject
	 * @return String
	 * @throws BOMException
	 */	
	protected String createSelectString(KeyObject inKey, LimitObject inLimit) throws BOMException {
		return dbAdapter.createSelectString(inKey, inLimit, this);
	}

	/**
	 * Creates select SQL string to delete all domain objects with the
	 * specified key managed by this home.
	 *
	 * @return java.lang.String
	 * @param inKey org.hip.kernel.bom.KeyObject
	 */
	protected String createDeleteString(KeyObject inKey) {
		return dbAdapter.createDeleteString(inKey, this);
	}
	
	/**
	 * Returns the SQL string to delete an entry.
	 * 
	 * @param inTableName java.lang.String
	 * @param inSemanticObject org.hip.kernel.bom.DomainObject
	 */	
	public String createDeleteString(String inTableName, DomainObject inDomainObject) {
		return getDBAdapter().createDeleteString(inTableName, inDomainObject);
	}
	
	/**
	 * Returns the SQL string to insert a new entry.
	 * 
	 * @param inTableName java.lang.String
	 * @param inSemanticObject org.hip.kernel.bom.DomainObject
	 */	
	public String createInsertString(String inTableName, DomainObject inDomainObject) {
		return getDBAdapter().createInsertString(inTableName, inDomainObject);
	}
	
	/**
	 * Returns the SQL string to update an entry.
	 * 
	 * @param inTableName java.lang.String
	 * @param inSemanticObject org.hip.kernel.bom.DomainObject
	 */	
	public String createUpdateString(String inTableName, DomainObject inDomainObject) {
		return getDBAdapter().createUpdateString(inTableName, inDomainObject);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.DomainObjectHome#createUpdateString(org.hip.kernel.bom.KeyObject, org.hip.kernel.bom.KeyObject)
	 */
	public String createUpdateString(KeyObject inChange, KeyObject inWhere) {
		return getDBAdapter().createUpdateString(this, inChange, inWhere);
	}
	
	/**
	 * Returns the SQL string to prepare an update of an entry.
	 * 
	 * @param inTableName java.lang.String
	 * @param inSemanticObject org.hip.kernel.bom.DomainObject
	 */	
	public String createPreparedUpdateString(String inTableName, DomainObject inDomainObject) {
		return getDBAdapter().createPreparedUpdateString(inTableName, inDomainObject);
	}
	
	/**
	 * Returns a Vector of prepared SQL string to insert a new entry.
	 * 
	 * @param java.util.Vector<String>
	 */	
	public Vector<String> createPreparedInserts() {
		return getDBAdapter().createPreparedInserts();
	}
	
	/**
	 * Returns a Vector of prepared SQL strings to update entries.
	 * 
	 * @param java.util.Vector<String>
	 */	
	public Vector<String> createPreparedUpdates() {
		return getDBAdapter().createPreparedUpdates();
	}
	
	/**
	 * Returns a SQL command usable for a prepared update.
	 * 
	 * @param inChange KeyObject
	 * @param inWhere KeyObject
	 * @return String
	 */
	public String createPreparedUpdate(KeyObject inChange, KeyObject inWhere) {
		return getDBAdapter().createPreparedUpdate(this, inChange, inWhere);
	}
	
	/**
	 * Returns the name of the objects which the concrete home can create.
	 * This method is abstract and must be implemented by concrete subclasses.
	 *
	 * @return java.lang.String
	 */
	abstract public String getObjectClassName() ;

	/**
	 * 	Returns the object definition for the class managed
	 *	by this home.
	 * 
	 * 	@return ObjectDef
	 */
	public ObjectDef getObjectDef() {
		if (objectDef == null) {
			objectDef= createObjectDef();
		}	
		return objectDef;
	}
	
	/**
	 * No hidden fields, therefore, an empty String is returned.
	 * 
	 * @param inPropertyName String
	 * @return String
	 */
	public String getHidden(String inPropertyName) {
		return "";
	}
	
	/**
	 * 	Returns the object definition string of the class managed	by this home.
	 * 	Concrete subclasses must implement this method.
	 * 
	 * 	@return java.lang.String
	 */
	protected abstract String getObjectDefString();
	
	/**
	 * 	The temporary can used if the client does not use it for long.
	 * 
	 * 	@return org.hip.kernel.bom.DomainObject
	 */
	protected DomainObject getTemporary() throws BOMException {
		if (temporary == null)
			temporary = newInstance();
		return temporary;
	}
	
	private DBAdapterSimple getDBAdapter() {
		if (dbAdapter == null) {
			dbAdapter = retrieveDBAdapterType().getSimpleDBAdapter(getObjectDef());
		}
		return dbAdapter;
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
	
	/**
	 * 	Returns a DomainObject filled with the values of the inputed ResultSet
	 * 
	 * 	@return org.hip.kernel.bom.GeneralDomainObject
	 *  @param inResult java.sql.ResultSet
	 */
	protected GeneralDomainObject newInstance(ResultSet inResult) throws BOMException {
	
		try {
			DomainObjectImpl outObject = (DomainObjectImpl)this.newInstance();
			outObject.loadFromResultSet(inResult);
			return outObject;
		} 
		catch (Exception exc) {
			throw new BOMException("newInstance: " + exc.getMessage());
		}
	}
	
	/**
	 * @return java.util.Hashtable
	 */
	public final synchronized Hashtable<String, String> tableNames() {
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
	 * Returns the table names in a string which can be used to create a SQL select statement.
	 *
	 * @return java.lang.String
	 */
	protected String tableNameString() {	
		ListJoiner lTableNames = new ListJoiner();
		for (String lTableName : tableNames().values()) {
			lTableNames.addEntry(lTableName);
		}
		return lTableNames.joinSpaced(",");
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
	
	/**
	 * Use this method to find a DomainObject by its key.
	 *
	 * @return org.hip.kernel.bom.DomainObject
	 * @param inKey org.hip.kernel.bom.KeyObject
	 */
	public DomainObject findByKey(KeyObject inKey) throws BOMNotFoundException, BOMInvalidKeyException	{
	
		// Pre: inKey not null
		if (VSys.assertNotNull(this, "findByKey", inKey) == Assert.FAILURE)
			 return null;
	
		DomainObject outDomainObject = null;
		
		if (getUseCache() && inKey.isPrimaryKey()) {
			outDomainObject = (DomainObject)cache().get(inKey);
		}
		if (outDomainObject == null) {	
			//not in the cache, we fetch it from database
			QueryResult lResult = null;
			try { 	 
				lResult = this.select(inKey);
				outDomainObject = (DomainObject)lResult.nextAsDomainObject();
				lResult.close();
			}
			catch (SQLException exc){
				throw new BOMInvalidKeyException(exc.toString());
			}
			catch (BOMException exc){
				throw new BOMInvalidKeyException(exc.toString());
			}
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

	/**
	 * This method is a limited version of a delete. 
	 * It takes as argument a KeyObject. 
	 * 
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @param inCommit boolean true if the statement has to bo commited after execution.
	 * @exception java.sql.SQLException The exception description.
	 */
	public void delete(KeyObject inKey, boolean inCommit) throws SQLException {
	
		if (VSys.assertNotNull(this, "delete(KeyObject)", inKey) == Assert.FAILURE)
			return;
		     
		QueryStatement lStatement = this.createQueryStatement();
		lStatement.setSQLString(this.createDeleteString(inKey));
		lStatement.executeUpdate(inCommit);
		lStatement.close();
	}
	
	/**
	 * Use this method to delete an entry from the table.
	 * (Contrary of insert.)
	 *
	 * @param inKey org.hip.kernel.bom.KeyObject 
	 */
	public void remove(KeyObject inKey) throws BOMNotFoundException, BOMInvalidKeyException {
		// not implemented yet
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(objectDef);
		out.writeObject(temporary);
		out.writeObject(tableNames);
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		objectDef = (ObjectDef)in.readObject();
		temporary = (DomainObject)in.readObject();
		tableNames = (Hashtable<String, String>)in.readObject();
		dbAdapter = getDBAdapter();
	}
	
	/**
	 * Checks whether the database table structure corresponds to the domain object definition.
	 * The test compares the number of columns and the column names.
	 * 
	 * @param inSchemaPattern String table schema (may be <code>null</code>)
	 * @return boolean <code>true</code> if the table structure in the database corresponds to the structur according to the home's object definition.
	 * @throws SQLException
	 * @throws VException
	 */
	public boolean checkStructure(String inSchemaPattern) throws SQLException, VException {
		String lTableName = getTableName();		
		Collection<String> lColumnNamesDef = getColumnNamesDef();
		Collection<String> lColumnNamesDB = getColumnNamesDB(inSchemaPattern, lTableName);
		
		//compare
		if (lColumnNamesDB.size() != lColumnNamesDef.size()) return false;
		
		for (String lColumnName : lColumnNamesDB) {
			if (!lColumnNamesDef.contains(lColumnName)) {
				return false;
			}
		}
		return true;		
	}
	
	private String getTableName() {
		return getObjectDef().getTableNames2().toArray(new String[1])[0].toString();
	}
	
	private Collection<String> getColumnNamesDef() {
		Collection<String> lColumnNamesDef = new Vector<String>();
		for (PropertyDef lPropertyDef : getObjectDef().getPropertyDefs2()) {
			lColumnNamesDef.add(lPropertyDef.getMappingDef().getColumnName().toUpperCase());
		}
		return lColumnNamesDef;
	}
	
	/**
	 * Returns a collection of column names in the specified table.
	 * 
	 * @param inSchemaPattern String, may be null
	 * @param inTableName String, must not be null
	 * @return Collection of column names in the specified table.
	 * @throws SQLException
	 * @throws VException 
	 */
	private Collection<String> getColumnNamesDB(String inSchemaPattern, String inTableName) throws SQLException, VException {
		Collection<String> outColumnNamesDB = new Vector<String>();
		
		Connection lConnection = null;
		try {
			lConnection = DataSourceRegistry.INSTANCE.getConnection();
			ResultSet lData = lConnection.getMetaData().getColumns(null, inSchemaPattern, inTableName.toUpperCase(), null);
			while (lData.next()) {
				outColumnNamesDB.add(lData.getString(4).toUpperCase());
			}
			return outColumnNamesDB;
		}
		finally {
			try {
				if (lConnection != null) lConnection.close();
			} 
			catch (SQLException exc) {}
		}
	}

}
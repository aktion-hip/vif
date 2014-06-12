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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.DBAdapterSimple;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.GroupByObject;
import org.hip.kernel.bom.HavingObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryStatement;
import org.hip.kernel.bom.ReadOnlyDomainObject;
import org.hip.kernel.bom.StatisticsHome;
import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.bom.model.impl.ObjectDefGenerator;
import org.hip.kernel.exc.DefaultExceptionHandler;
import org.hip.kernel.exc.DefaultExceptionWriter;
import org.hip.kernel.sys.Assert;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.util.ListJoiner;

/**
 * 	This class implements the StatisticsHome interface.
 *
 *	@author	B. Luthiger
 *	@version	1.0
 *	@see		org.hip.kernel.bom.StatisticsHome
 */
abstract public class AbstractStatisticsHomeImpl extends AbstractDomainObjectHome implements StatisticsHome {
	
	// Instance variables
	private	ObjectDef		internObjectDef	= null;
	private	ObjectDef		externObjectDef	= null;
	private	DBAdapterSimple dbAdapter		= null;
	

	// some optimizations
	private	String columnListIntern = null;
	private Hashtable<String, String> tableNames = null;

	/**
	 * AbstractStatisticsHomeImpl default constructor
	 */
	protected AbstractStatisticsHomeImpl() {
		super();
		initialize();
	}
	
	/**
	 * This method creates a new instance of a ReadOnlyDomainObject
	 *
	 * @return org.hip.kernel.bom.ReadOnlyDomainObject
	 */
	public ReadOnlyDomainObject create() throws BOMException {
		ReadOnlyDomainObject out = newInstance() ;
		((DomainObjectImpl)out).initializeForNew() ;
		return out;
	}
	
	/**
	 * This method creates a list of all columns of the table mapped to the
	 * DomainObject managed by this home.
	 * This method can be used to create the SQL string.
	 *
	 * @return java.lang.String
	 */
	protected String createColumnListIntern() {
		if ( columnListIntern == null ) {
			columnListIntern = "";
			try {
				ListJoiner lColumns = new ListJoiner();
				for (PropertyDef lPropertyDef : getObjectDefIntern().getPropertyDefs2()) {
					MappingDef lMappingDef = lPropertyDef.getMappingDef();
					lColumns.addEntry((String)lMappingDef.get("lColumnName"), "%2$s.%1$s", (String)lMappingDef.get("lTableName"));
				}
				columnListIntern += lColumns.joinSpaced(",");
			} 
			catch (Exception exc) {
				DefaultExceptionHandler.instance().handle(exc);
			}
		}
		
		return columnListIntern;	
	}
	
	/**
	 * @return org.hip.kernel.bom.model.ObjectDef
	 */
	private synchronized ObjectDef createObjectDefExtern() {
		try {
			ObjectDef outObjectDef = ObjectDefGenerator.getSingleton().createObjectDef(getObjectDefStringExtern());
			return outObjectDef;
		} 
		catch (Exception exc) {
			DefaultExceptionWriter.printOut(this, exc, true);
			return null;
		}	
	}
	
	/**
	 * @return org.hip.kernel.bom.model.ObjectDef
	 */
	private synchronized ObjectDef createObjectDefIntern() {
	
		try {
			ObjectDef outObjectDef = ObjectDefGenerator.getSingleton().createObjectDef(getObjectDefStringIntern());
			return outObjectDef;
		} 
		catch (Exception exc) {
			DefaultExceptionWriter.printOut(this, exc, true);
			return null;
		}	
	}
	
	/**
	 * 	Delegates to the adapter's createPreparedSelectString().
	 *
	 * 	@return java.lang.String
	 * 	@param inKey org.hip.kernel.bom.KeyObject
	 */
	protected String createPreparedSelectString(KeyObject inKey) {
		return dbAdapter.createPreparedSelectString(inKey, this);
	}
	
	/**
	 * This method creates a QueryStatement as part of the frameworks QueryService.
	 *
	 * @return org.hip.kernel.bom.QueryStatement
	 */
	public QueryStatement createQueryStatement() {
		return new DefaultQueryStatement( this ) ;
	}
	
	/**
	 * 	This is the very basic implementation to create the select 
	 *	statement .
	 *
	 * 	@return java.lang.String
	 * 	@param inKey org.hip.kernel.bom.KeyObject
	 */
	protected String createSelectString(KeyObject inKey) {
		return dbAdapter.createSelectString(inKey, this);
	}

	protected String createSelectString(KeyObject inKey, OrderObject inOrder) {
		return dbAdapter.createSelectString(inKey, inOrder, this);
	}

	protected String createSelectString(KeyObject inKey, OrderObject inOrder, HavingObject inHaving) {
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

	protected String createSelectString(OrderObject inOrder) {
		return dbAdapter.createSelectString(inOrder, this);
	}

	
	/**
	 * This method must be implemented by concrete subclasses to
	 * create test objects.
	 *
	 * @return java.util.Vector<Object>
	 */
	protected abstract Vector<Object> createTestObjects();
	
	/**
	 * Returns the name of the column which is mapped to the inputed property
	 *
	 * @return java.lang.String
	 * @param inPropertyName java.lang.String
	 */
	public String getColumnNameInternFor( String inPropertyName )  {
	
		try {
			MappingDef lMapping = getPropertyDefIntern( inPropertyName ).getMappingDef() ;
			return (String)lMapping.get("tableName") + "." + lMapping.get( "columnName" ) ;
		} 
		catch ( Exception exc ) {
			DefaultExceptionWriter.printOut(this, exc, true);
			return "";
		}
	}
	
	/**
	 * Returns the name of the objects which the concrete home can create.
	 * This method is abstract and must be implemented by concrete subclasses.
	 *
	 * @return java.lang.String
	 */
	abstract public String getObjectClassName();
	
	/**
	 * 	Returns the object definition for the class managed
	 *	by this home.
	 * 
	 * 	@return ObjectDef
	 */
	public ObjectDef getObjectDef() {
		if (externObjectDef == null) {
			externObjectDef	= createObjectDefExtern();	
		}	
		return externObjectDef;
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
	 * 	Returns the object definition for the class managed
	 *	by this home.
	 * 
	 * 	@return ObjectDef
	 */
	private ObjectDef getObjectDefIntern() {
		if (internObjectDef == null) {
			internObjectDef	= createObjectDefIntern();	
		}	
		return internObjectDef;
	}
	
	/**
	 * 	Returns the object definition string of the class managed by this home.
	 * 	Concrete subclasses must implement this method.
	 * 
	 * 	@return java.lang.String
	 */
	protected abstract String getObjectDefStringExtern();
	
	/**
	 * 	Returns the object definition string for the internal definitions.
	 * 	Concrete subclasses must implement this method.
	 * 
	 * 	@return java.lang.String
	 */
	protected abstract String getObjectDefStringIntern();
	
	/**
	 * This method returns the PropertyDef object which describes
	 * the behaviour of the Property object mapped to the tablecolumne
	 * named inColumnName.
	 *
	 * @return	org.hip.kernel.bom.model.PropertyDef 
	 * @param	inColumnName java.lang.String
	 */
	public PropertyDef getPropertyDefForIntern( String inColumnName )  {
	
		if (VSys.assertNotNull(this, "getPropertyDefForIntern", inColumnName) == Assert.FAILURE)
			return null;
			 
		try {
			for (PropertyDef outPropertyDef : getObjectDef().getPropertyDefs2()) {
				String lColumnName = (String)outPropertyDef.getMappingDef().get("inColumnName");
				if (inColumnName.equals(lColumnName))
					return outPropertyDef;				
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
	 * This method returns the PropertyDef object which describes
	 * the behaviour of the Property object named propertyName.
	 *
	 * @return org.hip.kernel.bom.model.PropertyDef
	 * @param inPropertyName java.lang.String
	 */
	public PropertyDef getPropertyDefIntern( String inPropertyName ) {
	
		// Pre: propertyName not null
		if (VSys.assertNotNull(this, "getPropertyDef", inPropertyName) == Assert.FAILURE)
			return null;
			 
		return getObjectDefIntern().getPropertyDef(inPropertyName);
	}
	
	/**
	 * This method can be used to initialize a new instance of the class.
	 */
	public void initialize() {
		dbAdapter = getDBAdapter();
	}
	
	private DBAdapterSimple getDBAdapter() {
		if (dbAdapter == null) {
			dbAdapter = retrieveDBAdapterType().getSimpleDBAdapter(getObjectDef());
		}
		return dbAdapter;
	}

	/**
	 * 	Returns a ReadOnlyDomainObject filled with the values of the inputed ResultSet
	 * 
	 * 	@return org.hip.kernel.bom.GeneralDomainObject
	 *  @param inResult java.sql.ResultSet
	 */
	protected GeneralDomainObject newInstance( ResultSet inResult ) throws BOMException {
	
		try {
			DomainObjectImpl retVal = (DomainObjectImpl) this.newInstance();
			retVal.loadFromResultSet( inResult );
			return retVal;
		} 
		catch (Exception exc) {
			throw new BOMException("getEmpty");
		}
		
	}

	/**
	 * @return java.util.Hashtable
	 */
	private final synchronized Hashtable<String, String> tableNames() {
		if (tableNames == null) {
			tableNames = new Hashtable<String, String>();
			try {
				for (PropertyDef lPropertyDef : getObjectDefIntern().getPropertyDefs2()) {
					String lTableName = (String)lPropertyDef.getMappingDef().get("tableName");
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
	 * We have to overwrite super.tableNameString() for that we can get the internal
	 * table names accessed via this.tableNames().
	 *
	 * @return java.lang.String
	 */
	public String tableNameString() {
		String outTabelNames = "";
		boolean lFirst = true;
		
		for (Enumeration<String> lTables = this.tableNames().elements(); lTables.hasMoreElements() ; ) {	
			if ( !lFirst ) outTabelNames += ", ";
			lFirst = false		;
			outTabelNames += lTables.nextElement();
		} // for
		
		return outTabelNames;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(internObjectDef);
		out.writeObject(externObjectDef);
		out.writeObject(columnListIntern);
		out.writeObject(tableNames);
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		internObjectDef = (ObjectDef)in.readObject();
		externObjectDef = (ObjectDef)in.readObject();
		columnListIntern = (String)in.readObject();
		tableNames = (Hashtable<String, String>)in.readObject();
		dbAdapter = getDBAdapter();
	}

}
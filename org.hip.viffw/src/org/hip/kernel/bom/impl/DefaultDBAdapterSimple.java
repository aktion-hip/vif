/*
	This package is part of the framework used for the application VIF.
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

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.DBAdapterSimple;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.GroupByObject;
import org.hip.kernel.bom.HavingObject;
import org.hip.kernel.bom.IGetValueStrategy;
import org.hip.kernel.bom.IValueForSQL;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.LimitObject;
import org.hip.kernel.bom.OrderItem;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.Property;
import org.hip.kernel.bom.StatisticsHome;
import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.exc.DefaultExceptionHandler;
import org.hip.kernel.sys.Assert;
import org.hip.kernel.sys.VObject;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.util.ListJoiner;
import org.hip.kernel.util.SortableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation for simple database adapters.
 * It seems that no subclassing is needed. Else, the factory method 
 * <code>DBAdapterType#getSimpleDBAdapter(ObjectDef)</code> has to be adjusted.
 * 
 * Created on 31.08.2002
 * @author Benno Luthiger
 * @see DBAdapterType#getSimpleDBAdapter(ObjectDef)
 */
public class DefaultDBAdapterSimple extends VObject implements DBAdapterSimple {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultDBAdapterSimple.class);
	
	private final static String SQL_SELECT		= "SELECT ";
	private static final String SQL_DISTINCT    = "DISTINCT ";
	private final static String SQL_FROM 		= " FROM ";
	private final static String SQL_WHERE 		= " WHERE ";
	private final static String SQL_GROUP_BY 	= " GROUP BY ";
	private final static String SQL_HAVING 		= " HAVING ";
	private final static String SQL_ORDER_BY 	= " ORDER BY ";
	private final static String SQL_LIMIT		= " LIMIT {0} OFFSET {1}";
	
	private ObjectDef objectDef = null;
	private String 	columnList = null;
	private String	tableNameString	= null;

	/**
	 * Constructor for DefaultDBAdapterSimple.
	 */
	public DefaultDBAdapterSimple(ObjectDef inObjectDef) {
		super();
		objectDef = inObjectDef;
	}
	
	/**
	 * Returns the SQL string to insert a new entry.
	 * 
	 * @param inTableName java.lang.String
	 * @param inDomainObject org.hip.kernel.bom.DomainObject
	 */	
	public String createInsertString(String inTableName, DomainObject inDomainObject) {
		StringBuffer outSQL = new StringBuffer("INSERT INTO ");
		outSQL.append(inTableName);

		ListJoiner lColumns = new ListJoiner();
		ListJoiner lValues = new ListJoiner();
		for (MappingDef lMapping : objectDef.getMappingDefsForTable2(inTableName)) {
			// First we get the value
			String lPropertyName = lMapping.getPropertyDef().getName();
			Object lValue = null;
			try {
				lValue = inDomainObject.get(lPropertyName);
			} 
			catch (Exception exc) {
				DefaultExceptionHandler.instance().handle(exc);
			}
			
			// No further processing if the value is null or empty
			if (lValue == null || lValue.toString().length() == 0) continue;
			
			lColumns.addEntry(lMapping.getColumnName());
			IValueForSQL lSQLValue = createValueForSQL(lValue);
			lValues.addEntry(lSQLValue.getValueAsString(), "%2$s%1$s%3$s", lSQLValue.getDelimiter(), lSQLValue.getDelimiter());
			
		}
		
		outSQL.append("( ").append(lColumns.joinSpaced(",")).append(" ) ");
		outSQL.append("VALUES (").append(lValues.joinSpaced(",")).append(" )");

		String out = new String(outSQL);
		LOG.debug("createInsertString {}", outSQL);
		return out;
	}

	/**
	 * Returns a Vector of prepared SQL strings to insert a new entry.
	 * 
	 * @return java.util.Vector<String>
	 */	
	public Vector<String> createPreparedInserts() {
		Vector<String> outPreparedInserts = new Vector<String>();
		for (String lTableName : objectDef.getTableNames2()) {
			outPreparedInserts.add(getPreparedInsertString(lTableName));
		}
		return outPreparedInserts;
	}
	
	private String getPreparedInsertString(String inTableName) {
		StringBuffer outSQL = new StringBuffer("INSERT INTO ");
		outSQL.append(inTableName);
		
		ListJoiner lColumns = new ListJoiner();
		ListJoiner lValues = new ListJoiner();
		for (MappingDef lMapping : objectDef.getMappingDefsForTable2(inTableName)) {
			lColumns.addEntry(lMapping.getColumnName());
			lValues.addEntry("?");
		}
		outSQL.append("( ").append(lColumns.joinSpaced(",")).append(" ) ");
		outSQL.append("VALUES (").append(lValues.joinSpaced(",")).append(" )");
		
		String out = new String(outSQL);
		LOG.debug("getPreparedInsertString {}", outSQL);
		return out;
	}

	/**
	 * Returns the SQL string to delete an entry.
	 * 
	 * @param inTableName java.lang.String
	 * @param inSemanticObject org.hip.kernel.bom.DomainObject
	 */	
	public String createDeleteString(String inTableName, DomainObject inDomainObject) {
		String outSQL = "DELETE FROM " + inTableName;
		outSQL += createSQLWhere(SQL_WHERE, inDomainObject.getKey(), (DomainObjectHome)inDomainObject.getHome());
		
		LOG.debug("createDeleteString {}", outSQL);
		return outSQL;
	}
		
	/**
	 * Returns the SQL string to update an entry.
	 * 
	 * @param inTableName java.lang.String
	 * @param inSemanticObject org.hip.kernel.bom.DomainObject
	 */	
	public String createUpdateString(String inTableName, DomainObject inDomainObject) {
		return getUpdateString(false, inTableName, inDomainObject);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.DBAdapterSimple#createUpdateString(org.hip.kernel.bom.DomainObjectHome, org.hip.kernel.bom.KeyObject, org.hip.kernel.bom.KeyObject)
	 */
	public String createUpdateString(DomainObjectHome inHome, KeyObject inChange, KeyObject inWhere) {
		return createUpdate(inHome, inChange, inWhere, new CriteriumValueStrategy());
	}
	
	/**
	 * Returns the SQL string to prepare an update of an entry.
	 * 
	 * @param inTableName java.lang.String
	 * @param inSemanticObject org.hip.kernel.bom.DomainObject
	 */	
	public String createPreparedUpdateString(String inTableName, DomainObject inDomainObject) {
		return getUpdateString(true, inTableName, inDomainObject);
	}
	
	private String getUpdateString(boolean inPrepared, String inTableName, DomainObject inDomainObject) {
		StringBuffer outSQL = new StringBuffer(1024);
		outSQL.append("UPDATE ").append(inTableName).append(" SET ");

		// We want to get all changed
		ListJoiner lColumns = new ListJoiner();
		for (Iterator<Property> lChanged = inDomainObject.getChangedProperties(); lChanged.hasNext();) {
			Property lProperty = lChanged.next();
			MappingDef lMapping = objectDef.getMappingDef(lProperty.getName()) ;
			if (!lMapping.getTableName().equals(inTableName))
				 continue;
			
			String lEqualed = "?";
			if (!inPrepared) {
				IValueForSQL lSQLValue = createValueForSQL(lProperty.getValue());
				lEqualed = lSQLValue.getDelimiter() + lSQLValue.getValueAsString() + lSQLValue.getDelimiter();
			}
			lColumns.addEntry(lMapping.getColumnName(), "%1$s = %2$s", lEqualed);
		}
		outSQL.append(lColumns.joinSpaced(","));
			
		if (inPrepared) {
			outSQL.append(createSQLPreparedWhere(SQL_WHERE, inDomainObject.getKey(false), (DomainObjectHome)inDomainObject.getHome()));
		}
		else {
			outSQL.append(createSQLWhere(SQL_WHERE, inDomainObject.getKey(true), (DomainObjectHome)inDomainObject.getHome()));
		}
		return new String(outSQL);
	}
	
	/**
	 * Returns a Vector of prepared SQL strings to update entries.
	 * 
	 * @return java.util.Vector<String>
	 */	
	public Vector<String> createPreparedUpdates() {
		Vector<String> outPreparedUpdates = new Vector<String>();
		for (String lTableName : objectDef.getTableNames2()) {
			outPreparedUpdates.add(getPreparedUpdateString(lTableName));
		}
		return outPreparedUpdates;
	}
	
	/**
	 * Returns a SQL command usable for a prepared update.
	 * 
	 * @param inHome DomainObjectHome
	 * @param inChange KeyObject
	 * @param inWhere KeyObject
	 * @return String
	 */
	public String createPreparedUpdate(DomainObjectHome inHome, KeyObject inChange, KeyObject inWhere) {
		return createUpdate(inHome, inChange, inWhere, new PreparedValueStrategy());
	}
	
	private String createUpdate(DomainObjectHome inHome, KeyObject inChange, KeyObject inWhere, IGetValueStrategy inGetValueStrategy) {
		String lTableName = objectDef.getTableNames2().toArray(new String[1])[0];
		StringBuffer outSQL = new StringBuffer("UPDATE " + lTableName + " SET ");
		inChange.setGetValueStrategy(inGetValueStrategy);
		inChange.setCriteriaStackFactory(new CriteriaStackFactory(CriteriaStackFactory.StackType.FLAT_JOIN, ", "));
		outSQL.append(inChange.render(inHome));
		outSQL.append(SQL_WHERE);
		inWhere.setGetValueStrategy(inGetValueStrategy);
		outSQL.append(inWhere.render(inHome));
		return new String(outSQL);		
	}
	
	private String getPreparedUpdateString(String inTableName) {
		StringBuffer outSQL = new StringBuffer("UPDATE ");
		outSQL.append(inTableName).append(" SET ");
	
		ListJoiner lColumnNames = new ListJoiner();
		for (MappingDef lMapping : objectDef.getMappingDefsForTable2(inTableName)) {
			String lPropertyName = lMapping.getPropertyDef().getName();
			if (isKeyPropertyName(lPropertyName)) {
				continue;
			}
			lColumnNames.addEntry(lMapping.getColumnName(), "%1$s = ?");
		}
		outSQL.append(lColumnNames.joinSpaced(","));
	
		//now the primary key
		outSQL.append(SQL_WHERE);
		lColumnNames = new ListJoiner();
		for (MappingDef lMapping : objectDef.getMappingDefsForTable2(inTableName)) {
			String lPropertyName = lMapping.getPropertyDef().getName();
			if (isKeyPropertyName(lPropertyName)) {
				lColumnNames.addEntry(lMapping.getColumnName(), "%1$s = ?");
			}			
		}
		outSQL.append(lColumnNames.joinSpaced(" AND"));
		return new String(outSQL);
	}
	
	/**
	 * Returns true if passed columnName is part of the primary key.
	 *
	 * @return boolean
	 * @param inPropertyName java.lang.String the name of the property, i.e. the column.
	 */
	private boolean isKeyPropertyName(String inPropertyName) {
		for (String lKeyName : objectDef.getPrimaryKeyDef().getKeyNames2()) {
			if (inPropertyName.equals(lKeyName)) {
				return true;
			}
		}
		return false;
	}	
	
	/**
	 * Creates the select SQL string counting all table entries corresponding to 
	 * the specified home.
	 *
	 * @param inDomainObjectHome org.hip.kernel.bom.DomainObjectHome;
	 * @return java.lang.String
	 */
	public String createCountAllString(DomainObjectHome inDomainObjectHome) {
		String outSQL = getSelect(false) + createKeyCountColumnList(inDomainObjectHome);
		outSQL += SQL_FROM + getTableNameString();
		
		LOG.debug("createCountAllString {}", outSQL);
		return outSQL;
	}
	
	private String getSelect(boolean inDistinct) {
		StringBuilder out = new StringBuilder(SQL_SELECT);
		out.append(inDistinct ? SQL_DISTINCT : "");
		return new String(out);
	}
	
	/**
	 * Creates the select SQL string counting all table entries with the specified
	 * key corresponding to the specified home.
	 *
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @param inDomainObjectHome org.hip.kernel.bom.DomainObjectHome;
	 * @return java.lang.String
	 */
	public String createCountString(KeyObject inKey, DomainObjectHome inDomainObjectHome) {
		StringBuilder outSQL = new StringBuilder(getSelect(inKey.isDistinct()));
		outSQL.append(createKeyCountColumnList(inDomainObjectHome));
		outSQL.append(SQL_FROM).append(getTableNameString());
		outSQL.append(createSQLWhere(SQL_WHERE, inKey, inDomainObjectHome));
		
		String out = new String(outSQL);		
		LOG.debug("createCountString {}", outSQL);
		return out;
	}
	/**
	 * 
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @param inHaving HavingObject
	 * @param inGroupBy GroupByObject
	 * @param inDomainObjectHome org.hip.kernel.bom.DomainObjectHome;
	 * @return java.lang.String
	 */
	public String createCountString(KeyObject inKey, HavingObject inHaving, GroupByObject inGroupBy, DomainObjectHome inDomainObjectHome) {
		String outSQL = createCountAllString(inDomainObjectHome) +
			createSQLWhere(SQL_WHERE, inKey, inDomainObjectHome) +
			createOrderBy(SQL_GROUP_BY, inGroupBy, inDomainObjectHome) +
			createSQLWhere(SQL_HAVING, inHaving, inDomainObjectHome);
		
		LOG.debug("createCountString {}", outSQL);
		return outSQL;
	}

	/**
	 * Creates the select SQL string returning the calculated value(s) according to the modify strategy.
	 * 
	 * @param inStrategy ModifierStrategy
	 * @param inDomainObjectHome org.hip.kernel.bom.DomainObjectHome
	 * @return String
	 */
	public String createModifiedString(ModifierStrategy inStrategy, DomainObjectHome inDomainObjectHome) {
		StringBuffer outSQL = new StringBuffer("SELECT ").append(inStrategy.createModifiedSQL(inDomainObjectHome));
		outSQL.append(SQL_FROM).append(getTableNameString());

		String out = new String(outSQL);
		LOG.debug("createModifiedString {}", outSQL);
		return out;
	}
	/**
	 * Creates the select SQL string returning the calculated value(s) according to the modify strategy.
	 * 
	 * @param inStrategy ModifierStrategy
	 * @param inKey org.hip.kernel.bom.KeyObject for the sub selection.
	 * @param inDomainObjectHome org.hip.kernel.bom.DomainObjectHome
	 * @return String
	 */
	public String createModifiedString(ModifierStrategy inStrategy, KeyObject inKey, DomainObjectHome inDomainObjectHome) {
		StringBuffer outSQL = new StringBuffer(createModifiedString(inStrategy, inDomainObjectHome));
		outSQL.append(createSQLWhere(SQL_WHERE, inKey, inDomainObjectHome));

		String out = new String(outSQL);
		LOG.debug("createModifiedString {}", outSQL);
		return out;
	}
		
	/**
	 * Creates select SQL string to fetch all domain objects
	 * managed by the specified home.
	 *
	 * @return java.lang.String
	 */
	public String createSelectAllString() {
		StringBuilder outSQL = new StringBuilder(getSelect(false));
		outSQL.append(createColumnList());
		outSQL.append(SQL_FROM).append(getTableNameString());
		return new String(outSQL);
	}
	
	/**
	 * Creates the select SQL string to fetch all domain objects with the
	 * specified key managed by the specified home.
	 *
	 * @return java.lang.String
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @param inDomainObjectHome org.hip.kernel.bom.DomainObjectHome;
	 */
	public String createSelectString(KeyObject inKey, DomainObjectHome inDomainObjectHome) {
		StringBuilder outSQL = new StringBuilder(getSelect(inKey.isDistinct()));
		outSQL.append(createColumnList());
		outSQL.append(SQL_FROM).append(getTableNameString());
		outSQL.append(createSQLWhere(SQL_WHERE, inKey, inDomainObjectHome));
		
		String out = new String(outSQL);
		LOG.debug("createSelectString {}", outSQL);
		return out;
	}
	
	public String createSelectString(KeyObject inKey, StatisticsHome inDomainObjectHome) {
		StringBuilder outSQL = new StringBuilder(getSelect(inKey.isDistinct()));
		outSQL.append(createColumnList());
		outSQL.append(SQL_FROM).append(getTableNameString());
		outSQL.append(createSQLWhere(SQL_WHERE, inKey, (GeneralDomainObjectHome)inDomainObjectHome));
		
		String out = new String(outSQL);
		LOG.debug("createSelectString {}", outSQL);
		return out;
	}
		
	/**
	 * Creates the select SQL string to fetch all domain objects with the
	 * specified key that meet the specified having criterion 
	 * managed by the specified home.
	 *
	 * @return java.lang.String
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @param inHaving org.hip.kernel.bom.HavingObject
	 * @param inDomainObjectHome org.hip.kernel.bom.DomainObjectHome;
	 */
	public String createSelectString(KeyObject inKey, HavingObject inHaving, DomainObjectHome inDomainObjectHome) {
		String outSQL = createSelectAllString() +
			createSQLWhere(SQL_WHERE, inKey, inDomainObjectHome) +
			createSQLWhere(SQL_HAVING, inHaving, inDomainObjectHome);
		
		LOG.debug("createSelectString {}", outSQL);
		return outSQL;
	}
	
	public String createSelectString(KeyObject inKey, HavingObject inHaving, StatisticsHome inDomainObjectHome) {
		String outSQL = createSelectAllString() +
			createSQLWhere(SQL_WHERE, inKey, (GeneralDomainObjectHome)inDomainObjectHome) +
			createSQLWhere(SQL_HAVING, inHaving, (GeneralDomainObjectHome)inDomainObjectHome);

		LOG.debug("createSelectString {}", outSQL);
		return outSQL;
	}
	
	public String createSelectString(KeyObject inKey, OrderObject inOrder, DomainObjectHome inDomainObjectHome) {
		String outSQL = createSelectAllString() +
			createSQLWhere(SQL_WHERE, inKey, inDomainObjectHome) +
			createOrderBy(SQL_ORDER_BY, inOrder, inDomainObjectHome);

		LOG.debug("createSelectString {}", outSQL);
		return outSQL;
	}
	
	public String createSelectString(KeyObject inKey, OrderObject inOrder, StatisticsHome inDomainObjectHome) {
		String outSQL = createSelectAllString() +
			createSQLWhere(SQL_WHERE, inKey, (GeneralDomainObjectHome)inDomainObjectHome) +
			createOrderBy(SQL_ORDER_BY, inOrder, (GeneralDomainObjectHome)inDomainObjectHome);

		LOG.debug("createSelectString {}", outSQL);
		return outSQL;
	}

	public String createSelectString(KeyObject inKey, OrderObject inOrder, HavingObject inHaving, DomainObjectHome inDomainObjectHome) {
		String outSQL = createSelectAllString() +
			createSQLWhere(SQL_WHERE, inKey, inDomainObjectHome) +
			createSQLWhere(SQL_HAVING, inHaving, inDomainObjectHome) +
			createOrderBy(SQL_ORDER_BY, inOrder, inDomainObjectHome);
		
		LOG.debug("createSelectString {}", outSQL);
		return outSQL;
	}
	
	public String createSelectString(KeyObject inKey, OrderObject inOrder, HavingObject inHaving, StatisticsHome inDomainObjectHome) {
		String outSQL = createSelectAllString() +
			createSQLWhere(SQL_WHERE, inKey, (GeneralDomainObjectHome)inDomainObjectHome) +
			createSQLWhere(SQL_HAVING, inHaving, (GeneralDomainObjectHome)inDomainObjectHome) +
			createOrderBy(SQL_ORDER_BY, inOrder, (GeneralDomainObjectHome)inDomainObjectHome);

		LOG.debug("createSelectString {}", outSQL);
		return outSQL;
	}

	public String createSelectString(KeyObject inKey, OrderObject inOrder, HavingObject inHaving, GroupByObject inGroupBy, DomainObjectHome inDomainObjectHome) {
		String outSQL = createSelectAllString() +
			createSQLWhere(SQL_WHERE, inKey, inDomainObjectHome) +
			createOrderBy(SQL_GROUP_BY, inGroupBy, inDomainObjectHome) +
			createSQLWhere(SQL_HAVING, inHaving, inDomainObjectHome) +
			createOrderBy(SQL_ORDER_BY, inOrder, inDomainObjectHome);
		
		LOG.debug("createSelectString {}", outSQL);
		return outSQL;
	}
	
	public String createSelectString(KeyObject inKey, OrderObject inOrder, HavingObject inHaving, GroupByObject inGroupBy, StatisticsHome inDomainObjectHome) {
		String outSQL = createSelectAllString() +
			createSQLWhere(SQL_WHERE, inKey, (GeneralDomainObjectHome)inDomainObjectHome) +
			createOrderBy(SQL_GROUP_BY, inGroupBy, (GeneralDomainObjectHome)inDomainObjectHome) +
			createSQLWhere(SQL_HAVING, inHaving, (GeneralDomainObjectHome)inDomainObjectHome) +
			createOrderBy(SQL_ORDER_BY, inOrder, (GeneralDomainObjectHome)inDomainObjectHome);

		LOG.debug("createSelectString {}", outSQL);
		return outSQL;
	}
	
	public String createSelectString(OrderObject inOrder, DomainObjectHome inDomainObjectHome) {
		String outSQL = createSelectAllString() + 
			createOrderBy(SQL_ORDER_BY, inOrder, inDomainObjectHome);
		
		LOG.debug("createSelectString {}", outSQL);
		return outSQL;
	}
	
	public String createSelectString(OrderObject inOrder, StatisticsHome inDomainObjectHome) {
		String outSQL = createSelectAllString() + 
			createOrderBy(SQL_ORDER_BY, inOrder, (GeneralDomainObjectHome)inDomainObjectHome);
		
		LOG.debug("createSelectString {}", outSQL);
		return outSQL;
	}
	
	public String createSelectString(KeyObject inKey, LimitObject inLimit, DomainObjectHome inDomainObjectHome) {
		String outSQL = createSelectAllString() +
		    createSQLWhere(SQL_WHERE, inKey, (GeneralDomainObjectHome)inDomainObjectHome) +
		    createLimitBy(SQL_LIMIT, inLimit);
		
		LOG.debug("createSelectString {}", outSQL);
		return outSQL;
	}
	
	/**
	 * Creates the prepared select SQL string to fetch all domain objects with the
	 * specified key managed by the specified home.
	 *
	 * @return java.lang.String
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @param inDomainObjectHome org.hip.kernel.bom.DomainObjectHome;
	 */
	public String createPreparedSelectString(KeyObject inKey, DomainObjectHome inDomainObjectHome) {
		String outSQL = createSelectAllString();
		outSQL += createSQLPreparedWhere(SQL_WHERE, inKey, inDomainObjectHome);
		
		LOG.debug("createPreparedSelectString {}", outSQL);
		return outSQL;
	}
	
	public String createPreparedSelectString(KeyObject inKey, StatisticsHome inDomainObjectHome) {
		StringBuilder outSQL = new StringBuilder(getSelect(inKey.isDistinct()));
		outSQL.append(createColumnList());
		outSQL.append(SQL_FROM).append(getTableNameString());
		outSQL.append(createSQLPreparedWhere(SQL_WHERE, inKey, (GeneralDomainObjectHome)inDomainObjectHome));

		String out = new String(outSQL);
		LOG.debug("createPreparedSelectString {}", outSQL);
		return out;
	}

	/**
	 * Creates select SQL string to delete all domain objects with the
	 * specified key managed by the specified home.
	 *
	 * @return java.lang.String
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @param inDomainObjectHome org.hip.kernel.bom.DomainObjectHome;
	 */
	public String createDeleteString(KeyObject inKey, DomainObjectHome inDomainObjectHome) {
		String outSQL = "DELETE" + SQL_FROM + getTableNameString();
		outSQL += createSQLWhere(SQL_WHERE, inKey, inDomainObjectHome);
		
		LOG.debug("createDeleteString {}", outSQL);
		return outSQL;
	}
	
	/**
	 * Creates the WHERE or HAVING part of the SQL select string.
	 *
	 * @param inSqlPart String WHERE or HAVING to be set at the beginning of the clause.
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @param inDomainObjectHome org.hip.kernel.bom.GeneralDomainObjectHome;
	 * @return java.lang.String
	 */
	private String createSQLWhere(String inSqlPart, KeyObject inKey, GeneralDomainObjectHome inDomainObjectHome) {
		// Pre: inKey not null
		if (inKey == null)
			return "";
		if (inKey.getItems2().size() == 0)
			return "";
		
		StringBuffer outSQL = new StringBuffer(inSqlPart);
		outSQL.append(inKey.render(inDomainObjectHome));
		return new String(outSQL);
	}
	
	/**
	 * Creates the WHERE or HAVING part of a prepared SQL select string.
	 *
	 * @param inSqlPart String WHERE or HAVING to be set at the beginning of the clause.
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @param inDomainObjectHome org.hip.kernel.bom.GeneralDomainObjectHome;
	 * @return java.lang.String
	 */
	private String createSQLPreparedWhere(String inSqlPart, KeyObject inKey, GeneralDomainObjectHome inDomainObjectHome) {
		// Pre: inKey not null
		if (inKey == null)
			return "";
		if (inKey.getItems2().size() == 0)
			return "";
		
		StringBuffer outSQL = new StringBuffer(inSqlPart);
		inKey.setGetValueStrategy(new PreparedValueStrategy());
		outSQL.append(inKey.render(inDomainObjectHome));
		return new String(outSQL);
	}
	
	/**
	 * Creates the ORDER BY or GROUP BY part of the SQL select string.
	 *
	 * @param inSqlPart String ORDER BY or GROUP BY to be set at the beginning of the clause.
	 * @param inOrder org.hip.kernel.bom.OrderObject
	 * @param inDomainObjectHome org.hip.kernel.bom.GeneralDomainObjectHome;
	 * @return java.lang.String
	 */
	private String createOrderBy(String inSqlPart, OrderObject inOrder, GeneralDomainObjectHome inDomainObjectHome) {
		// Pre: inOrder not null
		if (VSys.assertNotNull(this, "createOrderBy", inOrder) == Assert.FAILURE)
			return "";
		if (inOrder.getItems2().size() == 0)
			return "";

		ListJoiner lJoiner = new ListJoiner();
		for (SortableItem lItem : inOrder.getItems2()) {
			lJoiner.addEntry(inDomainObjectHome.getColumnNameFor(((OrderItem)lItem).getColumnName()), 
					"%1$s %2$s", 
					((OrderItem)lItem).isDescending() ? "DESC": "");			
		}
		return inSqlPart + lJoiner.joinSpaced(",");
	}
	
	/**
	 * Creates the limitation part of the SQL select.
	 * 
	 * @param inLimitPart String LIMIT # OFFSET #
	 * @param inLimit LimitObject
	 * @return String
	 */
	private String createLimitBy(String inLimitPart, LimitObject inLimit) {
		// Pre: inLimit not null
		if (VSys.assertNotNull(this, "createLimitBy", inLimit) == Assert.FAILURE)
			return "";
		
		return MessageFormat.format(inLimitPart, inLimit.getArguments());
	}

	/**
	 * This method looks for all key columns of the table mapped to the
	 * DomainObject managed by the specified home and creates a list COUNT(keyField).
	 * Instead of sending SELECT COUNT(*) FROM ... this SQL-sequence can be
	 * used to count all entries in a table with better performance.
	 *
	 * @return java.lang.String
	 */
	public String createKeyCountColumnList(DomainObjectHome inDomainObjectHome) {
		try {
			// We iterate over all mapping entries
			ListJoiner lList = new ListJoiner();
			for (String lKeyName : objectDef.getPrimaryKeyDef().getKeyNames2()) {
				lList.addEntry(inDomainObjectHome.getColumnNameFor(lKeyName), "COUNT(%1$s)");				
			}
			return lList.joinSpaced(",");
		}
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);
		}
		return "";
	}
	
	/**
	 * This method creates a list of all columns of the table mapped to the
	 * DomainObject managed by this home.
	 * This method can be used to create the SQL string.
	 *
	 * @return java.lang.String
	 */
	private String createColumnList() {
		if (columnList == null) {
			try {
				ListJoiner lColumns = new ListJoiner();
				for (PropertyDef lProperty : objectDef.getPropertyDefs2()) {
					MappingDef lMapping = lProperty.getMappingDef();
					lColumns.addEntry(lMapping.getColumnName(), "%2$s.%1$s", lMapping.getTableName());
				}
				columnList = lColumns.joinSpaced(",");				
			}
			catch (Exception exc) {
				DefaultExceptionHandler.instance().handle(exc);
			}
		}
		return columnList;
	}
	
	/**
	 * Generates the list of table names used for the SQL FROM clause.
	 * 
	 * @return String
	 */
	private String getTableNameString() {
		if (tableNameString == null) {
			ListJoiner lTableNames = new ListJoiner();

			for (String lTableName : objectDef.getTableNames2()) {
				lTableNames.addEntry(lTableName);
			}
			tableNameString = lTableNames.joinSpaced(",");
		}
		return tableNameString;
	}
	
	/**
	 * Factory method to create instances of <code>IValueForSQL</code>.
	 * The default implementation returns an instance of <code>ValueForSQL</code>.
	 * 
	 * @param inValue Object
	 * @return IValueForSQL
	 */
	protected IValueForSQL createValueForSQL(Object inValue) {
		return new ValueForSQL(inValue);
	}

}

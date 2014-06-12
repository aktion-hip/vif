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

import java.sql.SQLException;
import java.util.Vector;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.KeyCriterion;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.exc.DefaultExceptionWriter;
import org.hip.kernel.util.SortableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 	This is the prepared update statement.
 * 
 * 	@author		Benno Luthiger
 *
 */
public class PreparedUpdateStatement extends SqlPreparedStatement {
	private static final Logger LOG = LoggerFactory.getLogger(PreparedUpdateStatement.class);
	
	/**
	 * PreparedUpdateStatement constructor, initializes the home 
	 * and prepares the update statement.
	 * Use this constructor to update a single entry
	 * (the selection is extracted from the home's key).
	 *
	 * @param inHome org.hip.kernel.bom.DomainObjectHome
	 */
	public PreparedUpdateStatement(DomainObjectHome inHome) {
		home = inHome;
		initConnection();	
		prepareStatement();
	}
	
	/**
	 * PreparedUpdateStatement constructor to update a subset of values
	 * of a range of entries selected by the key <code>inWhere</code>.
	 * 
	 * @param inHome DomainObjectHome
	 * @param inChange KeyObject containing the fields to change and their new values.
	 * @param inWhere KeyObject for the selection.
	 */
	public PreparedUpdateStatement(DomainObjectHome inHome, KeyObject inChange, KeyObject inWhere) {
		home = inHome;
		initConnection();
		prepareStatement(inChange, inWhere);
		int lPosition = 0;
		lPosition = setValues(inChange, lPosition);
		lPosition = setValues(inWhere, lPosition);
	}
	
	private int setValues(KeyObject inKey, int inPosition) {
		for (SortableItem lItem : inKey.getItems2()) {
			if (isKey((KeyCriterion)lItem)) {
				//recursive
				inPosition = setValues((KeyObject)lItem.getValue(), inPosition);
			}
			else {
				setValueToStatement(lItem.getValue(), ++inPosition);
			}
		}
		return inPosition;
	}
		
	private boolean isKey(KeyCriterion inCriterion) {
		return KeyCriterion.NAME_FOR_KEY.equals(inCriterion.getName());
	}
		

	/**
	 * This method executes the update.
	 * NOTE: If auto commit is on, the connection is closed. 
	 * Else the connection has to be committed (or rollbacked) and is
	 * closed then. Be CAREFUL to close the Connection in case of failure.
	 * (By default, new connections are in auto-commit mode.)
	 *
	 * @return int the row count
	 * @exception java.sql.SQLException
	 * @see java.sql.Connection#setAutoCommit(boolean)
	 */
	public int executeUpdate() throws SQLException {
		try {
			int outValue;
			outValue = statement.executeUpdate();
			return outValue;
		}
		catch (SQLException exc) {
			LOG.error("Error encountered while processing '{}'!", sqlString, exc);
			throw exc;
		}
		finally {
			close(statement);
			traceWarnings(connection);
			if (connection.getAutoCommit())
				connection.close();
		}
	}
	
	/**
	 * This method was inspired by the DomainObjectImpl.createUpdateString() method.
	 * Except that every property will be updated. This has the advantage of using the one preparedUpdateStatement
	 * for every update.
	 *
	 */
	public void prepareStatement() {
		Vector<String> lSQLs = home.createPreparedUpdates();
		if (lSQLs.size() == 0) 
			throw new Error("Table not defined for : " + home.toString());
		if (lSQLs.size() > 1) 
			throw new Error("More than one table defined for : " + home.toString());
	
		sqlString = (String)lSQLs.elementAt(0);	
	
		try {
			statement = connection.prepareStatement(sqlString);
		}
		catch (SQLException exc) {
			throw new Error("SQL error while preparing statement : " + exc.toString());
		}
	}
	
	private void prepareStatement(KeyObject inChange, KeyObject inWhere) {
		sqlString = home.createPreparedUpdate(inChange, inWhere);
		try {
			statement = connection.prepareStatement(sqlString);
		}
		catch (SQLException exc) {
			throw new Error("SQL error while preparing statement : " + exc.toString());
		}
	}
	
	/**
	 * This method was inspired by the DomainObjectImpl.createUpdateString() method.
	 * 
	 * @param inObject org.hip.kernel.bom.DomainObject
	 */
	public void setValues(DomainObject inObject) {
		
		if (inObject.getHome() != home) {
			throw new Error("The object " + inObject.toString() + " can not be used with this statement which has been prepared with " + home.toString());
		}
		
		ObjectDef lDef = home.getObjectDef();
		String lTable = getTablename(lDef);
		Object lValue = null;
	
		int i = 0;	
		try {
			for (MappingDef lMappingDef : lDef.getMappingDefsForTable2(lTable)) {
				String lPropertyName = lMappingDef.getPropertyDef().getName();
				if (isKeyPropertyName(lDef, lPropertyName)) {
					continue;
				}	
				i++;
	
				// Now we get the value
				lValue = inObject.get(lPropertyName);
	
				if (lValue == null) {
					String lValueType = lMappingDef.getPropertyDef().getValueType();
					statement.setNull(i, convertToSqlType(lValueType)) ;
				}
				else {
					setValueToStatement(lValue, i);
				}				
			}
	
			//now the primary key
			for (MappingDef lMappingDef : lDef.getMappingDefsForTable2(lTable)) {
				String lPropertyName = lMappingDef.getPropertyDef().getName();
				if ( isKeyPropertyName(lDef, lPropertyName) ){
					i++;
					try { 
						lValue  = inObject.get(lPropertyName) ;
					} 
					catch (Exception exc) {
						DefaultExceptionWriter.printOut(this, exc, true);
				  		lValue = null;
					}
					setValueToStatement(lValue, i);
				}
			}
		} // try
		catch(GettingException exc) {
			throw new Error("PreparedUpdateStatement.setValues: " + exc.toString()); 
		}
		catch(SQLException exc) {
			throw new Error("SQL Error while settings values in a prepared update statement : " + exc.toString());
		}
	}
	
	/**
	 * Returns true if passed columnName is part of the primary key.
	 *
	 * @return boolean
	 * @param inObjectDef org.hip.kernel.bom.model.ObjectDef
	 * @param inPropertyName java.lang.String the name of the property, i.e. the column.
	 */
	private boolean isKeyPropertyName(ObjectDef inObjectDef, String inPropertyName) {
		for (String lKeyName : inObjectDef.getPrimaryKeyDef().getKeyNames2()) {
			if (inPropertyName.equals(lKeyName)){
				return true;
			}
		}
		return false;
	}
	
}
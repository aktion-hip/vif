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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.exc.DefaultExceptionHandler;
import org.hip.kernel.exc.VError;
import org.hip.kernel.sys.VSys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 	This is the prepared insert Statement.
 * 
 * 	@author Benno Luthiger
 */
public class PreparedInsertStatement extends SqlPreparedStatement {
	private static final Logger LOG = LoggerFactory.getLogger(PreparedInsertStatement.class);
	
	private int numberOfAffected = 0;
	
	/**
	 * PreparedInsertStatement constructor, initializes the home 
	 * and prepares the insert statement.
	 *
	 * @param inHome org.hip.kernel.bom.impl.DomainObjectHomeImpl
	 */
	public PreparedInsertStatement(DomainObjectHomeImpl inHome) {
		super();
		home = inHome;
		initConnection();	
		prepareStatement();
	}

	/**
	 * This method executes the insert.<br>
	 * The method is named "executeUpdate" because it calls the method
	 * java.sql.PreparedStatement.executeUpdate(), which updates the table,
	 * i.e. executes a SQL INSERT, UPDATE or DELETE statement.<br>
	 * NOTE: If auto commit is on, the connection is closed. 
	 * Else the connection has to be committed (or rollbacked) and is
	 * closed then. Be CAREFUL to close the Connection in case of failure.
	 * Also be careful to close the statement.
	 *
	 * @return Collection of auto-generated keys (Long) of the new entries created by this InsertStatement.
	 * @exception java.sql.SQLException
	 */
	public Collection<Long> executeUpdate() throws SQLException {
		try {
			Collection<Long> outAutoKeys = new Vector<Long>();
			numberOfAffected = statement.executeUpdate();
			ResultSet lResult = statement.getGeneratedKeys();
			while (lResult.next()) {
				outAutoKeys.add(new Long(lResult.getLong(1)));
			}
			return outAutoKeys;
		}
		catch (SQLException exc) {
			LOG.error("Error encountered with '{}'!", sqlString, exc);
			throw exc;
		}
		finally {
			traceWarnings(connection);
			if (!connection.isClosed())
				if (connection.getAutoCommit())
					connection.close();
		}
	}
	
	/**
	 * Returns the number of rows affected by this insert.
	 * 
	 * @return int
	 */
	public int getNumberOfAffected() {
		return numberOfAffected;
	}

	/**
	 * Prepares the statement by creating the SQL string.
	 */
	public void prepareStatement() {
		Vector<String> lSQLs = home.createPreparedInserts();
		if (lSQLs.size() == 0) 
			throw new Error("Table not defined for : " + home.toString());
		if (lSQLs.size() > 1) 
			throw new Error("More than one table defined for : " + home.toString());
	
		sqlString = (String)lSQLs.elementAt(0);	
	
		try {
			statement = connection.prepareStatement(sqlString, Statement.RETURN_GENERATED_KEYS);
		}
		catch (SQLException exc) {
			throw new Error("SQL error while preparing statement : " + exc.toString());
		}
	}

	/**
	 * This method sets the values of the prepared statement according to the
	 * values of the DomainObject.
	 * This method was inspired by the DomainObjectImpl.createInsertString() method.
	 *
	 * @param inObject org.hip.kernel.bom.DomainObject
	 */
	public void setValues(DomainObject inObject) {
		//check existence of statement
		if (VSys.assertNotNull(this, "setValues", statement)) return;
		
		if (inObject.getHome() != home) {
			throw new VError("The object " + inObject.toString() + " can not be used with this statement which has been prepared with " + home.toString());
		}
		
		ObjectDef lDef = home.getObjectDef();
		String lTable = getTablename(lDef);
	
		int i = 0;
		for (MappingDef lMappingDef : lDef.getMappingDefsForTable2(lTable)) {
			i++;
			// Now we get the value
			String lPropertyName = lMappingDef.getPropertyDef().getName() ;
			Object lValue = null;
			try {
				lValue = inObject.get(lPropertyName);
			} 
			catch (Exception exc) {
				DefaultExceptionHandler.instance().handle(exc);
			}
	
			try {
				if (lValue == null) {
					String lValueType = lMappingDef.getPropertyDef().getValueType();
					statement.setNull(i, convertToSqlType(lValueType));
				}
				else {
					setValueToStatement(lValue, i);
				} // else
			} // try
			catch(SQLException exc) {
				throw new Error("SQL Error while settings values in a prepared insert statement : " + exc.toString());
			} // catch			
		}
	}
	
}
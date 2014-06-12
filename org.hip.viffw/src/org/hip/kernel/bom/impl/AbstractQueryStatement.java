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
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.QueryStatement;
import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VObject;
import org.hip.kernel.util.Debug;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 	This is the abstract base implementation of the QueryStatment
 *	interface.
 * 	The QueryStatement interface is a kind of wrapper
 *	around the JDBC statement. The intention of this
 *	interface is the integration with the domain object
 *	framework.
 *  Use the QueryStatement for SELECTs. Use a CommittableStatement for
 *  INSERTs, UPDATEs and DELETEs instead.
 *  Note: The Connection is closed and released to the pool after execution.
 * 
 * 	@author		Benno Luthiger
 *	@see		org.hip.kernel.bom.QueryStatement
 *  @see		org.hip.kernel.bom.impl.CommittableStatement
 */
@SuppressWarnings("serial")
abstract public class AbstractQueryStatement extends VObject implements QueryStatement, Serializable {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractQueryStatement.class);

	// Instance variables
	private GeneralDomainObjectHome	home 		= null;
	
	private String					sqlString	= null;
	private Connection				connection	= null;
	private Statement				statement	= null;

	/**
	 * AbstractQueryStatement constructor, initializing instance variables.
	 *
	 * @param inHome org.hip.kernel.bom.GeneralDomainObjectHome
	 */
	public AbstractQueryStatement(GeneralDomainObjectHome inHome) {
		super();	
		home = inHome;
	}
	
	/**
	 * Constructor for statements without the need of a home. 
	 */
	public AbstractQueryStatement() {
		super();
	}
	
	/**
	 * @exception java.sql.SQLException
	 */
	public void close() throws SQLException {
		if (statement != null) {			
			statement.close();
		}
		if (connection != null) {			
			if (!connection.isClosed()) {
				for (SQLWarning lWarning = connection.getWarnings(); lWarning != null; lWarning = lWarning.getNextWarning()) {
					LOG.warn("SQL-Warning: {}: {}", lWarning.getMessage(), lWarning.getSQLState());
				}
				connection.close();
			}
		}
	}
	
	/**
	 * QueryStatements are equal if their SQL statements are equal.
	 *
	 * @return boolean
	 */
	public boolean equals(Object inObject) {
		if (inObject == null) return false;
		if (!(inObject instanceof QueryStatement)) return false;
	
		String lSQLofCompared = ((AbstractQueryStatement)inObject).getSQLString();
		if (getSQLString() != null) {
			return getSQLString().equals(lSQLofCompared);
		}
		else {
			return (lSQLofCompared == null);
		}
	}
	
	/**
	 * Retrieves the <code>Connection</code>.
	 * Subclasses may override.
	 * 
	 * @return Connection
	 * @throws SQLException
	 * @throws VException 
	 */
	protected Connection getConnection() throws SQLException, VException {
		return DataSourceRegistry.INSTANCE.getConnection();
	}
	
	/**
	 * Execute the query with this statement.
	 * 
	 * @return org.hip.kernel.bom.QueryResult
	 * @exception java.sql.SQLException
	 */
	public QueryResult executeQuery() throws SQLException {
	
		if (getSQLString() != null) {
			try {
				connection = getConnection();
				statement = connection.createStatement();
				ResultSet lResult = statement.executeQuery(getSQLString());
				return createQueryResult(home, lResult, this);
			}
			catch (VException exc) {
				throw new SQLException(exc.getMessage());
			}
		} 
		else {
			return createQueryResult(home, null, this);
		}
	}
	
	/**
	 * Executes the given SQL statement, which may return multiple results.
	 * 
	 * @param inSQL String any SQL statement
	 * @return boolean <code>true</code> if the first result is a <code>ResultSet</code> object; <code>false</code> if it is an update count or there are no results
	 * @throws SQLException
	 * @see {@link Statement#execute(String)}
	 */
	protected boolean executeSQL(String inSQL) throws SQLException {
		try {
			connection = getConnection();
			statement = connection.createStatement();
			return statement.execute(inSQL);
		} 
		catch (VException exc) {
			throw new SQLException(exc.getMessage());
		}
		finally {
			close();
		}
	}
	
	/**
	 * Friendly method used for deserialization of <code>QueryResult</code>.
	 * 
	 * @return ResultSet
	 * @throws SQLException
	 * @see AbstractQueryResult
	 */
	ResultSet retrieveStatement() throws SQLException {
		if (getSQLString() == null) return null;
		
		try {
			connection = getConnection();
			statement = connection.createStatement();
			return statement.executeQuery(getSQLString());
		} 
		catch (VException exc) {
			throw new SQLException(exc.getMessage());
		}
	}
	
	/**
	 * Creates the appropriate QueryResult. Subclasses may override this method.
	 * 
	 * @param inHome GeneralDomainObjectHome
	 * @param inResult ResultSet
	 * @param inStatement QueryStatement
	 * @return QueryResult
	 */
	protected QueryResult createQueryResult(GeneralDomainObjectHome inHome, ResultSet inResult, QueryStatement inStatement) {
		return new DefaultQueryResult(inHome, inResult, inStatement);
	}
	
	/**
	 * Executes an SQL INSERT, UPDATE or DELETE statement.
	 * 
	 * @param inCommit If true, the statement is commited.
	 * @return int The row count for INSERT, UPDATE or DELETE statements.
	 * @exception java.sql.SQLException
	 */
	public int executeUpdate(boolean inCommit) throws SQLException {
		//pre
		if (getSQLString() == null)
			return 0;
			
		try {
			connection = getConnection();
			statement = connection.createStatement();
			int lExecuted = statement.executeUpdate(getSQLString());
			if (inCommit)
				if (!connection.getAutoCommit())
					connection.commit();
				
			return lExecuted;
		}
		catch (SQLException exc) {
			throw exc;
		}
		catch (VException exc) {
			throw new SQLException(exc.getMessage());
		}
		finally {
			if (connection != null) {
				for (SQLWarning lWarning = connection.getWarnings(); lWarning != null; lWarning = lWarning.getNextWarning()) {
					LOG.warn("SQL-Warning: {}: {}", lWarning.getMessage(), lWarning.getSQLState());

				}
				connection.close();
			}
		}
	}
	
	/**
	 * This form of the query accepts as an input a valid SQL statement string.
	 * This is straightforward.
	 * 
	 * @return org.hip.kernel.bom.QueryResult
	 * @param inSQL java.lang.String 
	 * @exception java.sql.SQLException
	 */
	public QueryResult executeQuery(String inSQL) throws SQLException {
	
		setSQLString(inSQL);
		return executeQuery();
		
	}
	
	/**
	 * @return java.lang.String
	 */
	public String getSQLString() {
		return sqlString ;
	}

	/**
	 * Returns a hash code value for the QueryStatement.
	 *
	 * @return int
	 */
	public int hashCode() {
		if (getSQLString() == null) {
			return 1;
		}
		else {
			return getSQLString().hashCode();
		}
	}

	/**
	 * Moves to a Statement's next result.  It returns true if 
	 * this result is a ResultSet.  This method also implicitly
	 * closes any current ResultSet obtained with getResultSet.
	 *
	 * @return boolean
	 */
	public boolean hasMoreResults() {
		try { 
			return (statement != null) ? statement.getMoreResults() : false ;
		} 
		catch (SQLException exc) {
			return false;
		}
	}
	
	/**
	 * Emits a <code>SHOW TABLES</code> call and returns a collection containing the table names of the database catalog.
	 * 
	 * @return Collection<String> tables of the database catalog.
	 * @throws SQLException
	 */
	public Collection<String> showTables() throws SQLException {
		Collection<String> outTables = new Vector<String>();

		try {
			connection = getConnection();
			statement = connection.createStatement();
			if (statement.execute("SHOW TABLES;")) {
				ResultSet lResult = statement.getResultSet();
				while (lResult.next()) {
					outTables.add(lResult.getString(1));
				}			
			}			
		}
		catch (VException exc) {
			throw new SQLException(exc.getMessage());
		}
		finally {
			close();
		}
		return outTables;
	}

	/**
	 * Set a valid SQL statement string to this statement.
	 *
	 * @param inSQL java.lang.String
	 */
	public void setSQLString(String inSQL) {
		sqlString = inSQL;
	}

	public String toString() {
		String lMessage = "SQL=\"" + (getSQLString() == null ? "null" : getSQLString()) + "\"";
		return Debug.classMarkupString(this, lMessage);
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(home);
		out.writeObject(sqlString);
		try {
			close();
		}
		catch (SQLException exc) {
			throw new IOException(exc.getMessage());
		} 
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		home = (GeneralDomainObjectHome)in.readObject();
		sqlString = (String)in.readObject();
		//we don't initialize connection and statement here because we expect
		//that retrieveStatement() is called in the process of deserialization.
	}
}
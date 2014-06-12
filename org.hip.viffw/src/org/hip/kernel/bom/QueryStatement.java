package org.hip.kernel.bom;

import java.sql.SQLException;
import java.util.Collection;

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

/**
 * 	The QueryStatement interface is a kind of wrapper
 *	around the JDBC statement. The intention of this
 *	interface is the integration with the domain object
 *	framework.
 * 
 * 	@author	Benno Luthiger
 */
public interface QueryStatement {
	
	/**
	 * @exception java.sql.SQLException
	 */
	void close() throws SQLException;
	
	/**
	 * @return org.hip.kernel.bom.QueryResult
	 * @exception java.sql.SQLException 
	 */
	QueryResult executeQuery() throws SQLException;
	
	/**
	 * This form of the query accepts as an input a valid SQL statement string.
	 * This is straightforward.
	 * 
	 * @return org.hip.kernel.bom.QueryResult
	 * @param inSQL java.lang.String 
	 * @exception java.sql.SQLException
	 */
	QueryResult executeQuery(String inSQL) throws SQLException;
	
	/**
	 * Executes an SQL INSERT, UPDATE or DELETE statement.
	 * 
	 * @param inCommit If true, the statement is commited.
	 * @return int The row count for INSERT, UPDATE or DELETE statements.
	 * @exception java.sql.SQLException
	 */
	int executeUpdate(boolean inCommit) throws SQLException;
	
	/**
	 * Moves to a Statement's next result.  It returns true if 
	 * this result is a ResultSet.  This method also implicitly
	 * closes any current ResultSet obtained with getResultSet.
	 *
	 * @return boolean
	 */
	boolean hasMoreResults();
	
	/**
	 * Set a valid SQL statement string to this statement.
	 *
	 * @param inSQL java.lang.String
	 */
	void setSQLString(String inSQL);
	
	/**
	 * @return String this statement's SQL string.
	 */
	String getSQLString();
	
	/**
	 * Emits a <code>SHOW TABLES</code> call and returns a collection containing the table names of the database catalog.
	 * 
	 * @return Collection<String> tables of the database catalog.
	 * @throws SQLException
	 */
	public Collection<String> showTables() throws SQLException;
	
}

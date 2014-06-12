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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VObject;
import org.hip.kernel.sys.VSys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the abstract base implementation of any sql Statement
 * which can be committed (INSERT, UPDATE or DELETE).<br>
 * After execution of one or more of these statements, an explicit commit
 * (or rollback) must be issued, using the methods offered by this class.<br>
 * NOTE: This class still holds an open Connection after execution. The Connection
 * is closed after a successful commit (or rollback). Therefore, be careful to
 * commit this statement (or close the Connection in case of failure) for that
 * the Connection can be released to the pool.
 * 
 * @author Benno Luthiger
 */
public abstract class CommittableStatement extends VObject {
	private static final Logger LOG = LoggerFactory.getLogger(CommittableStatement.class);
	
	//instance variable
	protected Connection connection = null;

	/**
	 * Closes the Connection.
	 * This method is public because this class may fail and, therefore, 
	 * the Connection has to be closed by the client in a finally clause.
	 */
	public void close() {
		try {
			if (connection != null) {				
				if (!connection.isClosed()) {					
					connection.close();
				}
			}
		}
		catch (SQLException exc) {
			throw new Error("SQLException during close: " + exc.toString());
		}
	}
	
	/**
	 * Closes the statement.
	 * This method is protected because the statements are closed finally in this hierarchy.
	 *
	 * @param inStatement java.sql.Statement
	 */
	protected void close(Statement inStatement) {
		try {
			if (inStatement != null) {				
				inStatement.close();
			}
		}
		catch (SQLException exc) {
			throw new Error("SQLException during close: " + exc.toString());
		}
	}
	
	/**
	 * Commits and closes the connection
	 */
	public void commit() {
		try {
			if (connection != null) {				
				if (!connection.isClosed()) {
					if (!connection.getAutoCommit()) {						
						connection.commit();
					}					
				}
			}
		}
		catch (SQLException exc) {
			throw new Error("SQLException during commit: " + exc.toString());
		}
		finally {
			close();
		}
	}
	
	/**
	 * Initializes the connection
	 */
	protected void initConnection() {
		try {
			connection = DataSourceRegistry.INSTANCE.getConnection();
		}
		catch (VException exc) {
			throw new Error("SQL error while getting connection: " + exc.toString());
		}
		catch (SQLException exc) {
			throw new Error("SQL error while getting connection: " + exc.toString());
		}
	}
	
	/**
	 * Rollbacks and closes the connection
	 */
	public void rollback() {
		try {
			if (connection != null) {
				if (!connection.getAutoCommit()) {					
					connection.rollback();
				}
			}					 
		}
		catch (SQLException exc) {
			throw new Error("SQLException during rollback: " + exc.toString());
		}
		finally {
			close();
		}
	}
	
	/**
	 * Trace all warnings
	 *
	 * @param inConnection java.sql.Connection 
	 */
	protected void traceWarnings(Connection inConnection) {
		//pre
		if (!VSys.assertNotNull(this, "traceWarnings", inConnection)) return;
		
		try {
			for (SQLWarning lWarning = inConnection.getWarnings(); lWarning != null; lWarning = lWarning.getNextWarning()) {
				LOG.warn("SQL-Warning: {}, {}", lWarning.getMessage(), lWarning.getSQLState());
			}
		}
		catch (SQLException exc) {
			throw new Error("SQL error while tracing warnings: " + exc.toString());
		}
	}
}
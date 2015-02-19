/**
	This package is part of the servlet framework used for the application VIF.
	Copyright (C) 2001-2014, Benno Luthiger

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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

import org.hip.kernel.bom.SingleValueQueryStatement;
import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VObject;
import org.hip.kernel.sys.VSys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** See the interface for detailed description.
 *
 * @author Benno Luthiger
 * @see org.hip.kernel.bom.SingleValueQueryStatement */
public class SingleValueQueryStatementImpl extends VObject implements SingleValueQueryStatement { // NOPMD by lbenno
    private static final Logger LOG = LoggerFactory.getLogger(SingleValueQueryStatementImpl.class);

    /** Returns the single value queried, e.g. the number of rows or a maximum value.
     *
     * @param inSQL java.lang.String
     * @return java.lang.BigDecimal
     * @throws java.sql.SQLException
     * @see org.hip.kernel.bom.SingleValueQueryStatement */
    @Override
    public BigDecimal executeQuery(final String inSQL) throws SQLException {
        // pre
        if (VSys.assertNotNull(this, "executeQuery", inSQL)) {
            return BigDecimal.valueOf(0);
        }

        try {
            return (BigDecimal) prepareDataRetrieval(inSQL).iterator().next();
        } catch (final ClassCastException exc) { // NOPMD by lbenno
            // left empty intentionally
        }
        return BigDecimal.valueOf(0);
    }

    /** Returns a collection of values of the row queried, e.g. the number of rows or a maximum value. Use this method if
     * the statement returns multiple columns.
     *
     * @param inSQL String the SQL statement
     * @return Collection of BigDecimal or Timestamp
     * @throws SQLException */
    @Override
    public Collection<Object> executeQuery2(final String inSQL) throws SQLException {
        // pre
        if (VSys.assertNotNull(this, "executeQuery", inSQL)) {
            return new ArrayList<Object>();
        }
        return prepareDataRetrieval(inSQL);
    }

    private Collection<Object> prepareDataRetrieval(final String inSQL) throws SQLException {
        Collection<Object> outValues = new ArrayList<Object>();
        outValues.add(BigDecimal.valueOf(0));

        Connection lConnection = null;
        Statement lStatement = null;
        ResultSet lResult = null;
        try {
            lConnection = getConnection();

            lStatement = lConnection.createStatement();
            lResult = lStatement.executeQuery(inSQL);

            if (lResult.next()) {
                outValues = loadFromResultSet(lResult);
            }
        } catch (final VException exc) {
            throw new SQLException(exc.getMessage(), exc);
        } finally {
            if (lResult != null) {
                lResult.close();
            }
            if (lStatement != null) {
                lStatement.close();
            }
            if (lConnection != null) {
                for (SQLWarning lWarning = lConnection.getWarnings(); lWarning != null; lWarning = lWarning
                        .getNextWarning()) {
                    LOG.warn("SQL-Warning: {}, {}", lWarning.getMessage(), lWarning.getSQLState());
                }
                lConnection.close();
            }
        }
        return outValues;
    }

    /** Subclasses may override.
     *
     * @return Connection
     * @throws SQLException
     * @throws VException */
    protected Connection getConnection() throws SQLException, VException {
        return DataSourceRegistry.INSTANCE.getConnection();
    }

    private Collection<Object> loadFromResultSet(final ResultSet inResult) throws SQLException {
        final Collection<Object> outValues = new ArrayList<Object>();
        final ResultSetMetaData lMetaData = inResult.getMetaData();
        for (int i = 1; i <= lMetaData.getColumnCount(); i++) {
            switch (lMetaData.getColumnType(i)) {
            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.TINYINT:
                BigDecimal lNumeric = inResult.getBigDecimal(i);
                if (inResult.wasNull()) {
                    lNumeric = BigDecimal.valueOf(0);
                }
                outValues.add(lNumeric);
                break;
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                Timestamp lTime = inResult.getTimestamp(i);
                if (inResult.wasNull()) {
                    lTime = new Timestamp(0);
                }
                outValues.add(lTime);
                break;
            default:
                outValues.add(inResult.getBigDecimal(i));
                break;
            }
        }
        return outValues;
    }
}
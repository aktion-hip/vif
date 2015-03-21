/**
	This package is part of the application VIF.
	Copyright (C) 2011-2015, Benno Luthiger

	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.hip.vif.dbaccess.mysql;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.osgi.service.jdbc.DataSourceFactory;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

/** The MySQL data source provider service.
 *
 * @author Luthiger Created: 26.01.2012 */
public class MySQLJDBCDataSourceService implements DataSourceFactory { // NOPMD

    /** Starts the service.
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException */
    public void start() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final Class<?> lClass = Class.forName(Constants.DRIVER_NAME);
        lClass.newInstance();
    }

    @Override
    public DataSource createDataSource(final Properties inProps) throws SQLException { // NOPMD
        final MysqlDataSource outSource = new MysqlDataSource();
        setup(outSource, inProps);
        return outSource;
    }

    @Override
    public ConnectionPoolDataSource createConnectionPoolDataSource(final Properties inProps) throws SQLException { // NOPMD
        final MysqlConnectionPoolDataSource outSource = new MysqlConnectionPoolDataSource();
        setup(outSource, inProps);
        return outSource;
    }

    @Override
    public XADataSource createXADataSource(final Properties inProps) throws SQLException { // NOPMD
        final MysqlXADataSource outSource = new MysqlXADataSource();
        setupXSource(outSource, inProps);
        return outSource;
    }

    @Override
    public Driver createDriver(final Properties inProps) throws SQLException { // NOPMD
        return new com.mysql.jdbc.Driver();
    }

    private void setup(final MysqlDataSource inSource, final Properties inProperties) { // NOPMD
        if (inProperties == null) {
            return;
        }
        if (inProperties.containsKey(JDBC_DATABASE_NAME)) {
            inSource.setDatabaseName(inProperties.getProperty(JDBC_DATABASE_NAME));
        }
        if (inProperties.containsKey(JDBC_DATASOURCE_NAME)) {
            // not supported?
        }
        if (inProperties.containsKey(JDBC_DESCRIPTION)) {
            // not supported?
        }
        if (inProperties.containsKey(JDBC_NETWORK_PROTOCOL)) {
            // not supported?
        }
        if (inProperties.containsKey(JDBC_PASSWORD)) {
            inSource.setPassword(inProperties.getProperty(JDBC_PASSWORD));
        }
        if (inProperties.containsKey(JDBC_PORT_NUMBER)) {
            inSource.setPortNumber(Integer.parseInt(inProperties.getProperty(JDBC_PORT_NUMBER)));
        }
        if (inProperties.containsKey(JDBC_ROLE_NAME)) {
            // not supported?
        }
        if (inProperties.containsKey(JDBC_SERVER_NAME)) {
            inSource.setServerName(inProperties.getProperty(JDBC_SERVER_NAME));
        }
        if (inProperties.containsKey(JDBC_URL)) {
            inSource.setURL(inProperties.getProperty(JDBC_URL));
        }
        if (inProperties.containsKey(JDBC_USER)) {
            inSource.setUser(inProperties.getProperty(JDBC_USER));
        }
    }

    /** Setup the basic and extended properties for {@link XADataSource}s and {@link ConnectionPoolDataSource}s */
    private void setupXSource(final MysqlXADataSource source, final Properties props) {
        if (props == null) {
            return;
        }
        setup(source, props);
        if (props.containsKey(JDBC_INITIAL_POOL_SIZE)) {
            // not supported?
        }
        if (props.containsKey(JDBC_MAX_IDLE_TIME)) {
            // not supported?
        }
        if (props.containsKey(JDBC_MAX_STATEMENTS)) {
            // not supported?
        }
        if (props.containsKey(JDBC_MAX_POOL_SIZE)) {
            // not supported?
        }
        if (props.containsKey(JDBC_MIN_POOL_SIZE)) {
            // not supported?
        }
    }

}

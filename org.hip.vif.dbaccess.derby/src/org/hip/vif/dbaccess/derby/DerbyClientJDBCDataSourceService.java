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

package org.hip.vif.dbaccess.derby;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.apache.derby.jdbc.ClientConnectionPoolDataSource;
import org.apache.derby.jdbc.ClientDataSource;
import org.apache.derby.jdbc.ClientDriver;
import org.apache.derby.jdbc.ClientXADataSource;
import org.osgi.service.jdbc.DataSourceFactory;

/** The Derby data source provider service in case of client Derby DB.
 *
 * @author Luthiger Created: 29.01.2012 */
public class DerbyClientJDBCDataSourceService implements DataSourceFactory { // NOPMD

    /** Starts the service.
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException */
    public void start() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final Class<?> lClass = Class.forName(Constants.DRIVER_NAME_CLIENT);
        lClass.newInstance();
    }

    @Override
    public DataSource createDataSource(final Properties inProperties) throws SQLException { // NOPMD
        final ClientDataSource outSource = new ClientDataSource();
        setup(outSource, inProperties);
        return outSource;
    }

    @Override
    public ConnectionPoolDataSource createConnectionPoolDataSource(final Properties inProperties) throws SQLException { // NOPMD
        final ClientConnectionPoolDataSource outSource = new ClientConnectionPoolDataSource();
        setup(outSource, inProperties);
        return outSource;
    }

    @Override
    public XADataSource createXADataSource(final Properties inProperties) throws SQLException { // NOPMD
        final ClientXADataSource outSource = new ClientXADataSource();
        setup(outSource, inProperties);
        return outSource;
    }

    @Override
    public Driver createDriver(final Properties inProps) throws SQLException { // NOPMD
        return new ClientDriver();
    }

    private void setup(final ClientDataSource inSource, final Properties inProperties) { // NOPMD
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
        if (inProperties.containsKey(JDBC_USER)) {
            inSource.setUser(inProperties.getProperty(JDBC_USER));
        }
    }

}

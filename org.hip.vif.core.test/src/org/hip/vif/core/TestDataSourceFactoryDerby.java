/*
	This package is part of the application VIF.
	Copyright (C) 2012, Benno Luthiger

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

package org.hip.vif.core;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.apache.derby.jdbc.EmbeddedXADataSource;
import org.osgi.service.jdbc.DataSourceFactory;

/**
 * @author Luthiger
 * Created: 26.02.2012
 */
public class TestDataSourceFactoryDerby implements DataSourceFactory {

	/* (non-Javadoc)
	 * @see org.osgi.service.jdbc.DataSourceFactory#createDataSource(java.util.Properties)
	 */
	public DataSource createDataSource(Properties inProperties) throws SQLException {
		EmbeddedDataSource outSource = new EmbeddedDataSource();
		setup(outSource, inProperties);
		return outSource;
	}
	
	/* (non-Javadoc)
	 * @see org.osgi.service.jdbc.DataSourceFactory#createConnectionPoolDataSource(java.util.Properties)
	 */
	public ConnectionPoolDataSource createConnectionPoolDataSource(Properties inProperties) throws SQLException {
		EmbeddedConnectionPoolDataSource outSource = new EmbeddedConnectionPoolDataSource();
		setup(outSource, inProperties);
		return outSource;
	}
	
	/* (non-Javadoc)
	 * @see org.osgi.service.jdbc.DataSourceFactory#createXADataSource(java.util.Properties)
	 */
	public XADataSource createXADataSource(Properties inProperties) throws SQLException {
		EmbeddedXADataSource outSource = new EmbeddedXADataSource();
		setup(outSource, inProperties);
		return outSource;
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.jdbc.DataSourceFactory#createDriver(java.util.Properties)
	 */
	public Driver createDriver(Properties inProperties) throws SQLException {
		return new EmbeddedDriver();
	}
	
	protected void setup(EmbeddedDataSource inSource, Properties inProperties) {
		if (inProperties == null) {
			return;
		}
		if (inProperties.containsKey(DataSourceFactory.JDBC_DATABASE_NAME)) {
			inSource.setDatabaseName(inProperties.getProperty(DataSourceFactory.JDBC_DATABASE_NAME));
			inSource.setCreateDatabase("create");
		}
		if (inProperties.containsKey(DataSourceFactory.JDBC_DATASOURCE_NAME)) {
			//not supported?
		}
		if (inProperties.containsKey(DataSourceFactory.JDBC_DESCRIPTION)) {
			//not supported?
		}
		if (inProperties.containsKey(DataSourceFactory.JDBC_NETWORK_PROTOCOL)) {
			//not supported?
		}
		if (inProperties.containsKey(DataSourceFactory.JDBC_PASSWORD)) {
			inSource.setPassword(inProperties.getProperty(DataSourceFactory.JDBC_PASSWORD));
		}
		if (inProperties.containsKey(DataSourceFactory.JDBC_USER)) {
			inSource.setUser(inProperties.getProperty(DataSourceFactory.JDBC_USER));
		}
	}

}

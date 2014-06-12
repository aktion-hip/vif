/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

package org.hip.kernel.dbaccess;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.hip.kernel.bom.impl.DBAdapterType;
import org.hip.kernel.exc.VException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The registry of <code>DataSourceFactory</code> components.
 * 
 * @author Luthiger
 * Created: 26.01.2012
 */
public enum DataSourceRegistry {
	INSTANCE;
	
	private static final Logger LOG = LoggerFactory.getLogger(DataSourceRegistry.class);
	
	private Map<String, FactoryProperties> factories = new HashMap<String, FactoryProperties>();
	private DBAccessConfiguration activeConfiguration;
	private DataSourceFactory activeFactory = new NOOpFactory();

	/**
	 * Registers the specified factory to the registry.
	 * 
	 * @param inFactory {@link DataSourceFactory}
	 */
	public void register(DataSourceFactory inFactory) {
		try {
			FactoryProperties lParameters = new FactoryProperties(inFactory);
			factories.put(lParameters.getFactoryID(), lParameters);
			LOG.debug("Registered DB access \"{}\".", lParameters.getFactoryID());
		}
		catch (SQLException exc) {
			LOG.error("Error encountered while registering DB access!", exc);
		}
	}

	/**
	 * Unregisters the specified factory from the registry.
	 * 
	 * @param inFactory {@link DataSourceFactory}
	 */
	public void unregister(DataSourceFactory inFactory) {
		for (FactoryProperties lProperties : factories.values()) {
			if (lProperties.getFactory().equals(inFactory)) {
				factories.remove(lProperties.getFactoryID());
				return;
			}
		}
	}
	
	/**
	 * Returns the <code>DataSourceFactory</code> suitable to the specified properties.
	 * 
	 * @param inProperties {@link DBAccessConfiguration}
	 * @return {@link DataSourceFactory}
	 * @throws VException
	 */
	public DataSourceFactory getFactory(DBAccessConfiguration inProperties) throws VException {
		FactoryProperties out = factories.get(inProperties.getDBSourceID());
		if (out != null) {
			return out.getFactory();
		}		
		LOG.error("Configuration problem: no data access bundle provided for \"{}\".", inProperties.getDBSourceID());
		throw new VException(String.format("Configuration problem: no data access bundle provided for \"%s\".", inProperties.getDBSourceID()));
	}
	
	/**
	 * Returns the <code>DataSource</code> suitable to the specified properties.
	 * 
	 * @param inProperties {@link DBAccessConfiguration}
	 * @return {@link DataSource}
	 * @throws SQLException
	 * @throws VException
	 */
	public DataSource getDataSource(DBAccessConfiguration inProperties) throws SQLException, VException {
		FactoryProperties lFactory = factories.get(inProperties.getDBSourceID());
		if (lFactory != null) {
			return lFactory.getFactory().createDataSource(inProperties.getProperties());
		}
		LOG.error("Configuration problem: no data access bundle provided for \"{}\".", inProperties.getDBSourceID());
		throw new VException(String.format("Configuration problem: no data access bundle provided for \"%s\".", inProperties.getDBSourceID()));
	}
	
	/**
	 * Returns the <code>ConnectionPoolDataSource</code> suitable to the specified properties.
	 * 
	 * @param inProperties {@link DBAccessConfiguration}
	 * @return {@link ConnectionPoolDataSource}
	 * @throws SQLException
	 * @throws VException
	 */
	public ConnectionPoolDataSource getConnectionPoolDataSource(DBAccessConfiguration inProperties) throws SQLException, VException {
		FactoryProperties lFactory = factories.get(inProperties.getDBSourceID());
		if (lFactory != null) {
			return lFactory.getFactory().createConnectionPoolDataSource(inProperties.getProperties());
		}
		LOG.error("Configuration problem: no data access bundle provided for \"{}\".", inProperties.getDBSourceID());
		throw new VException(String.format("Configuration problem: no data access bundle provided for \"%s\".", inProperties.getDBSourceID()));
	}
	
	/**
	 * Returns the list of <code>DBSourceParameter</code> objects describing the registered <code>DataSourceFactory</code> instances.
	 * 
	 * @return Collection&lt;DBSourceParameter>
	 */
	public Collection<DBSourceParameter> getDBSourceParameters() {
		List<DBSourceParameter> out = new Vector<DataSourceRegistry.DBSourceParameter>();
		for (FactoryProperties lProperties : factories.values()) {
			out.add(lProperties.getDBSourceParameter());
		}
		Collections.sort(out);
		return out;
	}
	
	/**
	 * Setter for the active <code>DBAccessConfiguration</code>.
	 * This setter should be called by the application's preferences handler.
	 * 
	 * @param inActiveConfiguration {@link DBAccessConfiguration}
	 */
	public void setActiveConfiguration(DBAccessConfiguration inActiveConfiguration) {
		activeConfiguration = inActiveConfiguration;
		synchronized (activeFactory) {
			FactoryProperties lFactory = factories.get(activeConfiguration.getDBSourceID());
			if (lFactory != null) {
				activeFactory = lFactory.getFactory();
			}
		}
	}
	
	/**
	 * Shortcut method to set the active <code>DataSourceFactory</code> directly.
	 * 
	 * @param inFactory {@link DataSourceFactory}
	 */
	public void setFactory(DataSourceFactory inFactory) {
		activeFactory = inFactory;
	}
	
	private DataSourceFactory getFactory() throws VException {
		if (activeConfiguration == null) {
			throw new VException(String.format("Configuration problem: no DB access configuration provided"));
		}
		
		if (NOOpFactory.class.equals(activeFactory.getClass())) {
			FactoryProperties lFactory = factories.get(activeConfiguration.getDBSourceID());
			if (lFactory == null) {
				throw new VException(String.format("Configuration problem: no data access bundle provided for \"%s\".", activeConfiguration.getDBSourceID()));
			}
			activeFactory = lFactory.getFactory();
		}
		return activeFactory;
	}
	
	/**
	 * Returns a pooled <code>Connection</code> based on the active <code>DBAccessConfiguration</code>. 
	 * 
	 * @return {@link Connection}
	 * @throws SQLException
	 * @throws VException
	 */
	public Connection getConnection() throws SQLException, VException {
		return getFactory().createConnectionPoolDataSource(activeConfiguration.getProperties()).getPooledConnection().getConnection();
	}
	
	/**
	 * Returns a pooled <code>Connection</code> based on the specified <code>DBAccessConfiguration</code>. 
	 * 
	 * @param inConfiguration {@link DBAccessConfiguration} the DB access configuration
	 * @return {@link Connection}
	 * @throws SQLException
	 * @throws VException
	 */
	public Connection getConnection(DBAccessConfiguration inConfiguration) throws SQLException, VException {
		return getConnectionPoolDataSource(inConfiguration).getPooledConnection().getConnection();
	}
	
	/**
	 * Returns the DB adapter type matching the actual DB access configuration.
	 * 
	 * @return {@link DBAdapterType}
	 */
	public DBAdapterType getAdapterType() {
		if (activeConfiguration == null) return DBAdapterType.DB_TYPE_MYSQL;
		return getAdapterType(activeConfiguration);
	}

	/**
	 * Returns the DB adapter type matching the specified DB access configuration.
	 * 
	 * @param inDBConfiguration {@link DBAccessConfiguration}
	 * @return {@link DBAdapterType}
	 */
	public DBAdapterType getAdapterType(DBAccessConfiguration inDBConfiguration) {
		for (DBAdapterType lType : DBAdapterType.values()) {
			if (lType.isOfType(inDBConfiguration.getDBSourceID())) {
				return lType;
			}
		}
		return DBAdapterType.DB_TYPE_MYSQL;
	}
	
// ---
	
	/**
	 * Parameter object.
	 * 
	 * @author Luthiger
	 * Created: 27.01.2012
	 */
	public static class DBSourceParameter implements Comparable<DBSourceParameter> {
		private String factoryID;
		private String factoryName;

		DBSourceParameter(String inFactoryID, String inFactoryName) {
			factoryID = inFactoryID;
			factoryName = inFactoryName;
		}

		public String getFactoryID() {
			return factoryID;
		}
		public String getFactoryName() {
			return factoryName;
		}

		public int compareTo(DBSourceParameter inOther) {
			return getFactoryName().compareTo(inOther.getFactoryName());
		}
	}
	
	private static class FactoryProperties {
		private DataSourceFactory factory;
		private String driverClass;
		private String driverName;
		private String driverVersion;

		FactoryProperties(DataSourceFactory inFactory) throws SQLException {
			factory = inFactory;
			ServiceReference<DataSourceFactory> lServiceReference = getServiceRef(inFactory);
			driverClass = lServiceReference.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_CLASS).toString();
			driverName = (String)lServiceReference.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_NAME);
			driverVersion = (String)lServiceReference.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_VERSION);
		}
		
		@SuppressWarnings("unchecked")
		private ServiceReference<DataSourceFactory> getServiceRef(DataSourceFactory inFactory) throws SQLException {
			String lDriverName = inFactory.createDriver(null).getClass().getName();
			ServiceReference<?>[] lReferences = FrameworkUtil.getBundle(inFactory.getClass()).getRegisteredServices();
			for (ServiceReference<?> lServiceReference : lReferences) {
				if (lDriverName.equals(lServiceReference.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_CLASS))) {
					return (ServiceReference<DataSourceFactory>) lServiceReference;
				}
			}
			return null;
		}
		
		public DataSourceFactory getFactory() {
			return factory;
		}
		public String getFactoryID() {
			StringBuilder out = new StringBuilder(driverClass);
			if (driverName != null) {
				out.append("/").append(driverName);
			}
			if (driverVersion != null) {
				out.append("/").append(driverVersion);				
			}
			return new String(out);
		}
		public DBSourceParameter getDBSourceParameter() {
			return new DBSourceParameter(getFactoryID(), toString());
		}
		
		@Override
		public String toString() {
			return String.format("%s (%s)", driverName, driverVersion);
		}		
	}
	
	private static class NOOpFactory implements DataSourceFactory {
		public DataSource createDataSource(Properties inProps) throws SQLException {
			return null;
		}
		public ConnectionPoolDataSource createConnectionPoolDataSource(Properties inProps) throws SQLException {
			return null;
		}
		public XADataSource createXADataSource(Properties inProps) throws SQLException {
			return null;
		}
		public Driver createDriver(Properties inProps) throws SQLException {
			return null;
		}
	}

}

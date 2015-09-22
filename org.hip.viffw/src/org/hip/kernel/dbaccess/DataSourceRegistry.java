/**
	This package is part of the application VIF.
	Copyright (C) 2011-2014, Benno Luthiger

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

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

/** The registry of <code>DataSourceFactory</code> components.
 *
 * @author Luthiger Created: 26.01.2012 */
public enum DataSourceRegistry {
    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceRegistry.class);

    private final Map<String, FactoryProperties> factories = new ConcurrentHashMap<String, FactoryProperties>(); // NOPMD
    private DBAccessConfiguration activeConfiguration;
    private DataSourceFactory activeFactory = new NOOpFactory();

    /** Registers the specified factory to the registry.
     *
     * @param inFactory {@link DataSourceFactory} */
    public void register(final DataSourceFactory inFactory) {
        try {
            final FactoryProperties lParameters = new FactoryProperties(inFactory);
            factories.put(lParameters.getFactoryID(), lParameters);
            LOG.debug("Registered DB access \"{}\".", lParameters.getFactoryID());
        } catch (final SQLException exc) {
            LOG.error("Error encountered while registering DB access!", exc);
        }
    }

    /** Unregisters the specified factory from the registry.
     *
     * @param inFactory {@link DataSourceFactory} */
    public void unregister(final DataSourceFactory inFactory) {
        for (final FactoryProperties lProperties : factories.values()) {
            if (lProperties.getFactory().equals(inFactory)) {
                factories.remove(lProperties.getFactoryID());
                return;
            }
        }
    }

    /** Returns the <code>DataSourceFactory</code> suitable to the specified properties.
     *
     * @param inProperties {@link DBAccessConfiguration}
     * @return {@link DataSourceFactory}
     * @throws VException */
    public DataSourceFactory getFactory(final DBAccessConfiguration inProperties) throws VException {
        final FactoryProperties out = factories.get(inProperties.getDBSourceID());
        if (out != null) {
            return out.getFactory();
        }
        LOG.error("Configuration problem: no data access bundle provided for \"{}\".", inProperties.getDBSourceID());
        throw new VException(String.format("Configuration problem: no data access bundle provided for \"%s\".", // NOPMD
                // by
                // lbenno
                inProperties.getDBSourceID()));
    }

    /** Returns the <code>DataSource</code> suitable to the specified properties.
     *
     * @param inProperties {@link DBAccessConfiguration}
     * @return {@link DataSource}
     * @throws SQLException
     * @throws VException */
    public DataSource getDataSource(final DBAccessConfiguration inProperties) throws SQLException, VException {
        final FactoryProperties lFactory = factories.get(inProperties.getDBSourceID());
        if (lFactory != null) {
            return lFactory.getFactory().createDataSource(inProperties.getProperties());
        }
        LOG.error("Configuration problem: no data access bundle provided for \"{}\".", inProperties.getDBSourceID());
        throw new VException(String.format("Configuration problem: no data access bundle provided for \"%s\".",
                inProperties.getDBSourceID()));
    }

    /** Returns the <code>ConnectionPoolDataSource</code> suitable to the specified properties.
     *
     * @param inProperties {@link DBAccessConfiguration}
     * @return {@link ConnectionPoolDataSource}
     * @throws SQLException
     * @throws VException */
    public ConnectionPoolDataSource getConnectionPoolDataSource(final DBAccessConfiguration inProperties)
            throws SQLException, VException {
        final FactoryProperties lFactory = factories.get(inProperties.getDBSourceID());
        if (lFactory != null) {
            return lFactory.getFactory().createConnectionPoolDataSource(inProperties.getProperties());
        }
        LOG.error("Configuration problem: no data access bundle provided for \"{}\".", inProperties.getDBSourceID());
        throw new VException(String.format("Configuration problem: no data access bundle provided for \"%s\".",
                inProperties.getDBSourceID()));
    }

    /** Returns the list of <code>DBSourceParameter</code> objects describing the registered
     * <code>DataSourceFactory</code> instances.
     *
     * @return Collection&lt;DBSourceParameter> */
    public Collection<DBSourceParameter> getDBSourceParameters() {
        final List<DBSourceParameter> out = new ArrayList<DataSourceRegistry.DBSourceParameter>();
        for (final FactoryProperties lProperties : factories.values()) {
            out.add(lProperties.getDBSourceParameter());
        }
        Collections.sort(out);
        return out;
    }

    /** Setter for the active <code>DBAccessConfiguration</code>. This setter should be called by the application's
     * preferences handler.
     *
     * @param inActiveConfiguration {@link DBAccessConfiguration} */
    public void setActiveConfiguration(final DBAccessConfiguration inActiveConfiguration) {
        activeConfiguration = inActiveConfiguration;
        synchronized (this) {
            final FactoryProperties lFactory = factories.get(activeConfiguration.getDBSourceID());
            if (lFactory != null) {
                activeFactory = lFactory.getFactory();
            }
        }
    }

    /** Shortcut method to set the active <code>DataSourceFactory</code> directly.
     *
     * @param inFactory {@link DataSourceFactory} */
    public void setFactory(final DataSourceFactory inFactory) {
        activeFactory = inFactory;
    }

    private DataSourceFactory getFactory() throws VException {
        if (activeConfiguration == null) {
            throw new VException(String.format("Configuration problem: no DB access configuration provided"));
        }

        if (NOOpFactory.class.equals(activeFactory.getClass())) {
            if (activeConfiguration.getDBSourceID() == null) {
                throw new VException(String.format("Configuration problem: no data access bundle provided for \"%s\".",
                        activeConfiguration.getDBSourceID()));
            }
            final FactoryProperties lFactory = factories.get(activeConfiguration.getDBSourceID());
            if (lFactory == null) {
                throw new VException(String.format("Configuration problem: no data access bundle provided for \"%s\".",
                        activeConfiguration.getDBSourceID()));
            }
            activeFactory = lFactory.getFactory();
        }
        return activeFactory;
    }

    /** Returns a pooled <code>Connection</code> based on the active <code>DBAccessConfiguration</code>.
     *
     * @return {@link Connection}
     * @throws SQLException
     * @throws VException */
    public Connection getConnection() throws SQLException, VException {
        return getFactory().createConnectionPoolDataSource(activeConfiguration.getProperties()).getPooledConnection()
                .getConnection();
    }

    /** Returns a pooled <code>Connection</code> based on the specified <code>DBAccessConfiguration</code>.
     *
     * @param inConfiguration {@link DBAccessConfiguration} the DB access configuration
     * @return {@link Connection}
     * @throws SQLException
     * @throws VException */
    public Connection getConnection(final DBAccessConfiguration inConfiguration) throws SQLException, VException {
        return getConnectionPoolDataSource(inConfiguration).getPooledConnection().getConnection();
    }

    /** Returns the DB adapter type matching the actual DB access configuration.
     *
     * @return {@link DBAdapterType} */
    public DBAdapterType getAdapterType() {
        if (activeConfiguration == null) {
            return DBAdapterType.DB_TYPE_MYSQL;
        }
        return getAdapterType(activeConfiguration);
    }

    /** Returns the DB adapter type matching the specified DB access configuration.
     *
     * @param inDBConfiguration {@link DBAccessConfiguration}
     * @return {@link DBAdapterType} */
    public DBAdapterType getAdapterType(final DBAccessConfiguration inDBConfiguration) {
        for (final DBAdapterType lType : DBAdapterType.values()) {
            if (lType.isOfType(inDBConfiguration.getDBSourceID())) {
                return lType;
            }
        }
        return DBAdapterType.DB_TYPE_MYSQL;
    }

    // ---

    /** Parameter object.
     *
     * @author Luthiger Created: 27.01.2012 */
    public static class DBSourceParameter implements Comparable<DBSourceParameter> {
        private final String factoryID;
        private final String factoryName;

        DBSourceParameter(final String inFactoryID, final String inFactoryName) {
            factoryID = inFactoryID;
            factoryName = inFactoryName;
        }

        /** @return String */
        public String getFactoryID() {
            return factoryID;
        }

        /** @return String */
        public String getFactoryName() {
            return factoryName;
        }

        @Override
        public int compareTo(final DBSourceParameter inOther) { // NOPMD by lbenno
            return getFactoryName().compareTo(inOther.getFactoryName());
        }
    }

    /** Class holding the DB facroty properties. */
    private static class FactoryProperties {
        private final DataSourceFactory factory;
        private transient final String driverClass;
        private transient final String driverName;
        private transient final String driverVersion;

        FactoryProperties(final DataSourceFactory inFactory) throws SQLException {
            factory = inFactory;
            final ServiceReference<DataSourceFactory> lServiceReference = getServiceRef(inFactory);
            driverClass = lServiceReference.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_CLASS).toString();
            driverName = (String) lServiceReference.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_NAME);
            driverVersion = (String) lServiceReference.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_VERSION);
        }

        @SuppressWarnings("unchecked")
        private ServiceReference<DataSourceFactory> getServiceRef(final DataSourceFactory inFactory)
                throws SQLException {
            final String lDriverName = inFactory.createDriver(null).getClass().getName();
            final ServiceReference<?>[] lReferences = FrameworkUtil.getBundle(inFactory.getClass())
                    .getRegisteredServices();
            for (final ServiceReference<?> lServiceReference : lReferences) {
                if (lDriverName.equals(lServiceReference.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_CLASS))) {
                    return (ServiceReference<DataSourceFactory>) lServiceReference;
                }
            }
            return null;
        }

        /** @return {@link DataSourceFactory} */
        public DataSourceFactory getFactory() {
            return factory;
        }

        /** @return String the factory id */
        public String getFactoryID() {
            final StringBuilder out = new StringBuilder(driverClass);
            if (driverName != null) {
                out.append('/').append(driverName);
            }
            if (driverVersion != null) {
                out.append('/').append(driverVersion);
            }
            return new String(out);
        }

        /** @return {@link DBSourceParameter} */
        public DBSourceParameter getDBSourceParameter() {
            return new DBSourceParameter(getFactoryID(), toString());
        }

        @Override
        public String toString() { // NOPMD by lbenno
            return String.format("%s (%s)", driverName, driverVersion);
        }
    }

    /** No-operation instance of <code>DataSourceFactory</code> */
    private static class NOOpFactory implements DataSourceFactory {
        @Override
        public DataSource createDataSource(final Properties inProps) throws SQLException { // NOPMD by lbenno
            return null;
        }

        @Override
        public ConnectionPoolDataSource createConnectionPoolDataSource(final Properties inProps) throws SQLException { // NOPMD
            return null;
        }

        @Override
        public XADataSource createXADataSource(final Properties inProps) throws SQLException { // NOPMD by lbenno
            return null;
        }

        @Override
        public Driver createDriver(final Properties inProps) throws SQLException { // NOPMD by lbenno
            return null;
        }
    }

}

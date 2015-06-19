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

import java.util.Properties;

import org.osgi.service.jdbc.DataSourceFactory;

/** Parameter object containing the properties to access a database, i.e. create a <code>DataSource</code> instance.
 *
 * @author Luthiger Created: 26.01.2012 */
public class DBAccessConfiguration {
    private transient String dbSourceID = "";
    private transient String server = "";
    private transient String schema = "";
    private transient String user = "";
    private transient String password = "";
    private transient State state;

    /** Private constructor for empty configurations. */
    private DBAccessConfiguration() {
        state = State.NEW;
    }

    /** Constructor.
     *
     * @param inDBSourceID String <code>org.hip.vif.db.driver</code>, i.e. the ID of the <code>DataSourceFactory</code>
     *            instance
     * @param inServer String <code>org.hip.vif.db.server</code>
     * @param inSchema String <code>org.hip.vif.db.schema</code>
     * @param inUser String <code>org.hip.vif.db.userId</code>
     * @param inPassword String <code>org.hip.vif.db.password</code> */
    public DBAccessConfiguration(final String inDBSourceID, final String inServer, final String inSchema,
            final String inUser, final String inPassword) {
        dbSourceID = inDBSourceID;
        server = inServer;
        schema = inSchema;
        user = inUser;
        password = inPassword;

        state = State.NEW;
        if (dbSourceID != null && !dbSourceID.isEmpty()) {
            state = State.INITIALIZED;
            if (server != null && !server.isEmpty() &&
                    schema != null && !schema.isEmpty()) {
                state = State.CONFIGURED;
            }
        }
    }

    /** Crates an empty, i.e. uninitialized configuration instance.
     *
     * @return {@link DBAccessConfiguration} */
    public static DBAccessConfiguration getEmptyConfiguration() {
        return new DBAccessConfiguration();
    }

    /** @return String the ID of the <code>DataSource</code> to configure with this configuration instance */
    public String getDBSourceID() {
        return dbSourceID;
    }

    /** @return {@link State} */
    public State getState() {
        return state;
    }

    /** Checks whether the actual configuration is in the specified state.
     *
     * @param inState {@link State} the state to check
     * @return boolean <code>true</code> if the configuration instance is in the specified state */
    public boolean checkState(final State inState) {
        return state.equals(inState);
    }

    /** The <code>Properties</code> to retrieve a DB connection instance based on this configuration.
     *
     * @return Properties */
    public Properties getProperties() {
        final Properties out = new Properties();
        out.put(DataSourceFactory.JDBC_SERVER_NAME, server);
        out.put(DataSourceFactory.JDBC_DATABASE_NAME, schema);
        out.put(DataSourceFactory.JDBC_USER, user);
        out.put(DataSourceFactory.JDBC_PASSWORD, password);
        return out;
    }

    /** @return boolean <code>true</code> if the config is accessible */
    public boolean isAccessible(final boolean isEmbedded) {
        if (!checkState(DBAccessConfiguration.State.INITIALIZED)) {
            return false;
        }
        // State.INITIALIZED
        if (dbSourceID == null || dbSourceID.isEmpty()) {
            return false;
        }
        if (!isEmbedded) {
            if (server == null || server.isEmpty()) {
                return false;
            }
        }
        return user == null ? true : user.length() == 0 && password == null ? true : password.length() == 0;
    }

    @Override
    public String toString() { // NOPMD by lbenno
        return String.format("DBAccessConfiguration[server=%s, schema=%s, user=%s]", server, schema, user);
    }

    // ---

    /** The state of the configuration instance:
     * <ul>
     * <li><code>NEW</code> for unconfigured instances</li>
     * <li><code>INITIALIZED</code> for instances with initialized driver</li>
     * <li><code>CONFIGURED</code> for instances with fully configured DB access</li>
     * </ul>
     *
     * @author Luthiger Created: 29.01.2012 */
    public enum State {
        NEW, INITIALIZED, CONFIGURED;
    }

}

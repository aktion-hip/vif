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

package org.hip.vif.core.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.hip.kernel.dbaccess.DBAccessConfiguration;
import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.service.PreferencesHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks the DB access.
 * 
 * @author Luthiger
 * Created: 07.02.2012
 */
public class DBConnectionProber {
	private static final Logger LOG = LoggerFactory.getLogger(DBConnectionProber.class);
	
	private static final String EMBEDDED_SCHEMA = "APP";
	
	public enum ProbingState {
		READY, ACCESS, NO_ACCESS, UNDIFEND_PROBLEM;
	}
	
	private ProbingState state = ProbingState.UNDIFEND_PROBLEM;
	
	/**
	 * Constructor.
	 */
	public DBConnectionProber() {
		Connection lConnection = null;
		try {
			LOG.trace("Starting connection prober ...");
			lConnection = DataSourceRegistry.INSTANCE.getConnection();
			DBAccessConfiguration lConfig = PreferencesHandler.INSTANCE.getDBConfiguration();
			LOG.trace("Probing with configuration {}.", lConfig);
			LOG.trace("DB configuration state is {}.", lConfig.getState());
			state = ProbingState.NO_ACCESS;
			if (lConfig.checkState(DBAccessConfiguration.State.CONFIGURED) ||
					(lConfig.checkState(DBAccessConfiguration.State.INITIALIZED) && PreferencesHandler.INSTANCE.isEmbedded())) {				
				state = ProbingState.ACCESS;
			}
			LOG.trace("Connection prober in state {}.", state);
		}
		catch (SQLException exc) {
			state = ProbingState.NO_ACCESS;
			LOG.trace("Connection prober in state {}.", state);
		}
		catch (VException exc) {
			//intentionally left empty
		}
		
		try {
			if (lConnection != null && hasTables(lConnection)) {			
				state = ProbingState.READY;
			}
		}
		catch (SQLException exc) {
			//intentionally left empty
		}
		catch (IOException exc) {
			//intentionally left empty
		}
		LOG.trace("Connection state after proping is {}.", state);
	}
	
	private boolean hasTables(Connection inConnection) throws SQLException, IOException {
		String lCatalog = inConnection.getCatalog();
		String lSchema = null;
		if (lCatalog == null) {
			lSchema = EMBEDDED_SCHEMA;
		}
		//do we have any tables in the catalog/schema?
		return inConnection.getMetaData().getTables(lCatalog, lSchema, null, null).next();
	}
	
	/**
	 * @return boolean <code>true</code> if the probing indicates that the DB access has an undefined problem
	 */
	public boolean isUndefined() {
		return state == ProbingState.UNDIFEND_PROBLEM;
	}

	/**
	 * @return boolean <code>true</code> if the probing indicates that the application needs DB access configuration
	 */
	public boolean needsDBConfiguration() {
		return state == ProbingState.NO_ACCESS;
	}
	
	/**
	 * @return boolean <code>true</code> if the probing indicates that the application can access the DB but the schema is empty
	 */
	public boolean needsTableCreation() {
		return state == ProbingState.ACCESS;
	}
	
	/**
	 * @return boolean <code>true</code> if the probing indicates that the application's member table is empty
	 * @throws Exception 
	 */
	public boolean needsSUCreation() throws Exception {
		int lCount = BOMHelper.getMemberCacheHome().getCount();
		return lCount == 0;
	}
	
}

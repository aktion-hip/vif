/*
	This package is part of the framework used for the application VIF.
	Copyright (C) 2006, Benno Luthiger

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

import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.dbaccess.DBAccessConfiguration;
import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.kernel.exc.VException;

/**
 * Statement to query an additional database while using <code>DefaultQueryStatement</code> to retrieve the main bulk of data.
 *
 * @author Luthiger
 * Created on 16.04.2007
 */
@SuppressWarnings("serial")
public class ExtDBQueryStatement extends AbstractQueryStatement {
	private DBAccessConfiguration dbConfiguration;

	/**
	 * @param inHome GeneralDomainObjectHome
	 * @param inConfiguration {@link DBAccessConfiguration}
	 */
	public ExtDBQueryStatement(GeneralDomainObjectHome inHome, DBAccessConfiguration inConfiguration) {
		super(inHome);
		dbConfiguration = inConfiguration;
	}
	
	/**
	 * Setter for <code>DBAccessConfiguration</code> used by this statement to retrieve data.
	 * 
	 * @param inConfiguration {@link DBAccessConfiguration}
	 */
	public void setConnectionSetting(DBAccessConfiguration inConfiguration) {
		dbConfiguration = inConfiguration;
	}
	
	protected Connection getConnection() throws SQLException, VException {
		return DataSourceRegistry.INSTANCE.getConnection(dbConfiguration);
	}

}

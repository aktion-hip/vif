/*
	This package is part of the application VIF.
	Copyright (C) 2006, Benno Luthiger

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

package org.hip.vif.core.bom.impl;

import java.io.File;

import org.hip.kernel.bom.QueryStatement;
import org.hip.kernel.bom.SingleValueQueryStatement;
import org.hip.kernel.bom.impl.DBAdapterType;
import org.hip.kernel.bom.impl.ExtDBQueryStatement;
import org.hip.kernel.bom.impl.ExtSingleValueQueryStatement;
import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.core.util.ExternalObjectDefUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Home for <code>Member</code> objects retrieved from an external member database.
 * 
 * @author Luthiger
 */
public class ExtMemberHomeImpl extends MemberHomeImpl {
	private static final Logger LOG = LoggerFactory.getLogger(ExtMemberHomeImpl.class);
	
	private final static String MEMBER_CLASS_NAME = "org.hip.vif.core.bom.impl.ExtMemberImpl";
	final static String OBJECT_DEF_FILE = "MEMBEROBJECTDEF.xml";
	private QueryStatement statement = null;
	
	/**
	 * Returns the Member class name
	 *
	 * @return java.lang.String
	 */
	public String getObjectClassName() {
		return MEMBER_CLASS_NAME;
	}	

	/**
	 * Reads the content of the file <code>$TOMCAT_HOME/webapps/vifapp/WEB-INF/conf/MEMBEROBJECTDEF.xml</code>.
	 */
	protected String getObjectDefString() {
		File lObjectDefFile = ExternalObjectDefUtil.getObjectDefFile(OBJECT_DEF_FILE);
		if (!lObjectDefFile.exists()) {
			return "";
		}
		return ExternalObjectDefUtil.readObjectDef(lObjectDefFile);
	}
	
	/**
	 * Overrides super implementation to return specialised QueryStatement.
	 */
	public QueryStatement createQueryStatement() {
		if (statement == null) {
			statement = createStatement();
		}
		return statement;
	}
	
	private QueryStatement createStatement() {
		return new ExtDBQueryStatement(this, PreferencesHandler.INSTANCE.getExtDBConfiguration());
	}
	
	/**
	 * Overrides super implementation to return specialised QueryStatement.
	 */
	protected SingleValueQueryStatement createSingleValueQueryStatement() {
		return new ExtSingleValueQueryStatement(PreferencesHandler.INSTANCE.getExtDBConfiguration());
	}
	
	@Override
	protected DBAdapterType retrieveDBAdapterType() {
		return DataSourceRegistry.INSTANCE.getAdapterType(PreferencesHandler.INSTANCE.getExtDBConfiguration());
	}
	
}

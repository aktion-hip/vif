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

package org.hip.vif.core.upgrade;

import java.io.IOException;
import java.sql.SQLException;

import org.hip.kernel.bom.impl.DefaultStatement;
import org.hip.vif.core.exc.UpgradeException;
import org.hip.vif.core.interfaces.IVIFUpgrade;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.core.service.UpgradeRegistry.ProgressIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Upgrader to adjust the tables according to the needs of VIF 1.1.
 * 
 * @author Luthiger
 * Created: 15.04.2012
 */
public class UpgradeDB10 implements IVIFUpgrade {
	private static final Logger LOG = LoggerFactory.getLogger(UpgradeDB10.class);
	
	private static final String DESCRIPTION = "Upgrades the tables from 1.0 to 1.1.";
	
	private static final String STATEMENT_MYSQL_1 = "CREATE TABLE tblAppVersion (	VersionID	varchar(15) not null,	PRIMARY KEY (VersionID))";
	private static final String STATEMENT_MYSQL_2 = "ALTER TABLE tblQuestionHierarchy ADD COLUMN GroupID INT UNSIGNED NOT NULL";
	private static final String STATEMENT_MYSQL_3 = "CREATE INDEX idxQuestionHierarchy_03 ON tblQuestionHierarchy (GroupID)";
	private static final String STATEMENT_MYSQL_4 = "ALTER TABLE tblMember ADD COLUMN sSettings TEXT";
	private static final String STATEMENT_MYSQL_5 = "ALTER TABLE tblMemberHistory ADD COLUMN sSettings TEXT";
	private static final String[] STATEMENTS = {STATEMENT_MYSQL_1, 
												STATEMENT_MYSQL_2, 
												STATEMENT_MYSQL_3, 
												STATEMENT_MYSQL_4, 
												STATEMENT_MYSQL_5};
	
	/* (non-Javadoc)
	 * @see org.hip.vif.core.interfaces.IVIFUpgrade#version()
	 */
	public String version() {
		return Upgrade11.VERSION;
	}
	
	/* (non-Javadoc)
	 * @see org.hip.vif.core.interfaces.IVIFUpgrade#getDescription()
	 */
	public String getDescription() {
		return DESCRIPTION;
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.core.interfaces.IVIFUpgrade#execute(org.hip.vif.core.service.UpgradeRegistry.ProgressIndicator)
	 */
	public void execute(ProgressIndicator inIndicator) throws UpgradeException {
		try {
			LOG.trace("We don't upgrade a Derby database!");
			if (PreferencesHandler.INSTANCE.isDerbyDB()) return;
		}
		catch (IOException exc) {
			throw new UpgradeException(exc);
		}
		
		UpgradeException lFailure = null;
		DefaultStatement lSQL = new DefaultStatement();
		for (String lStatement : STATEMENTS) {
			try {
				lSQL.execute(lStatement);
				inIndicator.nextStep();
			}
			catch (SQLException exc) {
				LOG.error("Could not process statment '{}'!", lStatement, exc);
				lFailure = new UpgradeException(exc);
			}
		}
		if (lFailure != null) {
			throw lFailure;
		}
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.core.interfaces.IVIFUpgrade#getNumberOfSteps()
	 */
	public int getNumberOfSteps() {
		return 5;
	}

}

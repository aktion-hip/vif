/*
 This package is part of the application VIF.
 Copyright (C) 2012, Benno Luthiger

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
package org.hip.vif.web.stale;

import java.sql.Timestamp;
import java.util.TimerTask;

import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.web.Activator;
import org.hip.vif.web.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The timer task to remove stale requests.
 * 
 * @author Luthiger
 * Created: 02.03.2012
 */
public class RemoveStaleRequests extends TimerTask {
	private static final Logger LOG = LoggerFactory.getLogger(RemoveStaleRequests.class);
	
	@Override
	public void run() {
		LOG.debug("Started timer thread to remove stale 'waiting for review' requests."); //$NON-NLS-1$
		
		LOG.debug(Activator.getMessages().getMessage("errmsg.pwrd.confirm")); //$NON-NLS-1$
		StaleRequestRemover lProcessor = new StaleRequestRemover(getStaleDate());
		try {
			lProcessor.processStaleRequests();
		} 
		catch (Exception exc) {
			LOG.error("Error encountered while running the task to remove stale requests!", exc); //$NON-NLS-1$
		}
	}

	private Timestamp getStaleDate() {
		int lLatency = ApplicationConstants.DFLT_REQUEST_LATENCY;
		try {
			lLatency = Integer.parseInt(PreferencesHandler.INSTANCE.get(PreferencesHandler.KEY_LATENCY_DAYS));
		} 
		catch (Exception exc) {
			LOG.error("Error encountered while initializing the task to remove stale requests!", exc); //$NON-NLS-1$
		}
		return new Timestamp(System.currentTimeMillis() - (lLatency * Constants.ONCE_PER_DAY));
	}
	
}
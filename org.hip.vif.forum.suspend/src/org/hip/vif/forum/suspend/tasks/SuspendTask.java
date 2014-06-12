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

package org.hip.vif.forum.suspend.tasks;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.ParticipantHome;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.bom.impl.JoinParticipantToGroupHome;
import org.hip.vif.core.bom.impl.NestedGroupHome;
import org.hip.vif.forum.suspend.Constants;
import org.hip.vif.forum.suspend.data.GroupContainer;
import org.hip.vif.forum.suspend.ui.SuspendView;
import org.hip.vif.web.tasks.AbstractVIFTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;

/**
 * Task to suspend the participation in a discussion group for a specific period.
 * 
 * @author Luthiger
 * Created: 02.10.2011
 */
@Partlet
public class SuspendTask extends AbstractVIFTask {
	private static final Logger LOG = LoggerFactory.getLogger(SuspendTask.class);
	
	public enum SuspendState {
		INITIAL, HAS_DATES;
	}
	
	private static final String DFT_SORT = GroupHome.KEY_NAME + ", " + NestedGroupHome.KEY_GROUP_ID; //$NON-NLS-1$
	
	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#needsPermission()
	 */
	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_SUSPEND;
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#runChecked()
	 */
	@Override
	protected Component runChecked() throws VException {
		emptyContextMenu();
		
		Long lActorID = getActor().getActorID();
		JoinParticipantToGroupHome lGroupHome = BOMHelper.getJoinParticipantToGroupHome();

		KeyObject lKeySubscribeable = createKey(VIFGroupWorkflow.ENLISTABLE_STATES);                    
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(ParticipantHome.KEY_MEMBER_ID, lActorID); 
		lKey.setValue(lKeySubscribeable, BinaryBooleanOperator.AND);

		try {
			DatePrepare lPrepare = new DatePrepare(BOMHelper.getParticipantHome(), lActorID);
			return new SuspendView(GroupContainer.createData(lGroupHome.select(lKey, createOrder(DFT_SORT, false))),
					lPrepare, this);
		} 
		catch (SQLException exc) {
			throw createContactAdminException(exc);
		}
	}
	
	/**
	 * Callback method to set the suspend period.
	 * 
	 * @param inFrom Date
	 * @param inTo Date
	 * @return boolean <code>true</code> if saving the dates was successful
	 */
	public boolean saveSuspendDates(Date inFrom, Date inTo) {
		try {
			BOMHelper.getParticipantHome().suspendParticipation(getActor().getActorID(), 
					new Timestamp(inFrom.getTime()), 
					new Timestamp(inTo.getTime()));
			return true;
		} 
		catch (VException exc) {
			LOG.error("Error while saving the suspend dates!", exc); //$NON-NLS-1$
		}
		return false;
	}

	/**
	 * Callback method to clear the suspend period.
	 * 
	 * @return boolean <code>true</code> if clearing the suspend period was successful
	 */
	public boolean clearSuspendDates() {
		Timestamp lZero = new Timestamp(1000);
		try {
			BOMHelper.getParticipantHome().suspendParticipation(getActor().getActorID(), lZero, lZero);
			return true;
		} 
		catch (VException exc) {
			LOG.error("Error while clearing the suspend dates!", exc); //$NON-NLS-1$
		}
		return false;
	}
	
// ---
	
	public static class DatePrepare {
		private Timestamp from;
		private Timestamp to;
		private SuspendState suspendState = SuspendState.INITIAL;
		
		DatePrepare(ParticipantHome inHome, Long inActorID) throws VException, SQLException {
			initialize(inHome.getActorSuspend(inActorID).iterator());
		}
		private void initialize(Iterator<Object> inDates) {
			from = (Timestamp)inDates.next();
			to = (Timestamp)inDates.next();
			Timestamp lNow = new Timestamp(new Date().getTime());
			if (from.getTime() + to.getTime() == 0) {
				suspendState = SuspendState.INITIAL;
				from = lNow;
			}
			else if ((from.before(lNow)) && (to.before(lNow))) {
				suspendState = SuspendState.INITIAL;
				from = lNow;
			}
			else {
				suspendState = SuspendState.HAS_DATES;
			}
		}
		
		public Date getFromDate() {
			return new Date(from.getTime());
		}
		public Date getToDate() {
			if (getSuspendDateState() == SuspendState.INITIAL) {
				return null;
			}
			return new Date(to.getTime());
		}
		public SuspendState getSuspendDateState() {
			return suspendState;
		}
	}

}

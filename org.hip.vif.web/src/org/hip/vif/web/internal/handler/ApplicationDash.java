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

package org.hip.vif.web.internal.handler;

import java.io.Serializable;

import org.hip.vif.web.dash.VIFDashBoard;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;

/**
 * Object that holds the application's <code>VIFDashBoard</code> for one user session.
 * 
 * @author Luthiger
 * Created: 18.11.2011
 */
@SuppressWarnings("serial")
public class ApplicationDash implements TransactionListener, Serializable {
	
	private static ThreadLocal<ApplicationDash> instance = new ThreadLocal<ApplicationDash>();
	private VIFDashBoard dashBoard;
	
	/**
	 * Create the session data holder. This method has to be called in the application's init method.
	 * 
	 * @param inApplication {@link Application}
	 */
	public static void create(Application inApplication) {
		ApplicationDash lSessionData = new ApplicationDash();
		instance.set(lSessionData);
		inApplication.getContext().addTransactionListener(lSessionData);
	}

	/* (non-Javadoc)
	 * @see com.vaadin.service.ApplicationContext.TransactionListener#transactionStart(com.vaadin.Application, java.lang.Object)
	 */
	@Override
	public void transactionStart(Application inApplication, Object inTransactionData) {
		instance.set(this);
	}

	/* (non-Javadoc)
	 * @see com.vaadin.service.ApplicationContext.TransactionListener#transactionEnd(com.vaadin.Application, java.lang.Object)
	 */
	@Override
	public void transactionEnd(Application inApplication, Object inTransactionData) {
		instance.set(null);
	}
	
	public static void setDash(VIFDashBoard inDashBoard) {
		instance.get().dashBoard = inDashBoard;
	}
	
	public static VIFDashBoard getDash() {
		return instance.get().dashBoard;
	}

}

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

package org.hip.vif.forum.usersettings.data;

import java.sql.SQLException;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;

import com.vaadin.data.util.BeanItemContainer;

/**
 * Container for subscription bean instances.
 * 
 * @author Luthiger
 * Created: 20.12.2011
 */
@SuppressWarnings("serial")
public class SubscriptionContainer extends BeanItemContainer<SubscriptionBean> {
	public static final String ITEM_CHK = "chk"; //$NON-NLS-1$
	public static final String ITEM_LOCAL = "local"; //$NON-NLS-1$
	public static final String ITEM_CHECKED = "checked"; //$NON-NLS-1$
	public static final String[] NATURAL_COL_ORDER = new String[] {ITEM_CHK, ITEM_LOCAL, "questionDecimal", "questionText"}; //$NON-NLS-1$ //$NON-NLS-2$
	public static final String[] COL_HEADERS = new String[] {"", "ui.usersettings.data.column.local", "ui.usersettings.data.column.number", "ui.usersettings.data.column.question"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	private SubscriptionContainer() {
		super(SubscriptionBean.class);
	}
	
	/**
	 * Factory method: Creates and returns an instance of <code>SubscriptionContainer</code>.  
	 * 
	 * @param inSubscriptions
	 * @return {@link SubscriptionContainer}
	 * @throws VException
	 * @throws SQLException
	 */
	public static SubscriptionContainer createData(QueryResult inSubscriptions) throws VException, SQLException {
		SubscriptionContainer outData = new SubscriptionContainer();
		while (inSubscriptions.hasMoreElements()) {
			outData.addItem(SubscriptionBean.createItem(inSubscriptions.nextAsDomainObject()));
		}
		return outData;
	}
	
}

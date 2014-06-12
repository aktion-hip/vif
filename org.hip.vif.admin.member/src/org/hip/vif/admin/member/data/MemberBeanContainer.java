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

package org.hip.vif.admin.member.data;

import java.sql.SQLException;
import java.util.Collection;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.member.MemberBean;

import com.vaadin.data.util.BeanItemContainer;

/**
 * Container (i.e. view model) for member bean instances.
 * 
 * @author Luthiger
 * Created: 15.11.2011
 */
@SuppressWarnings("serial")
public class MemberBeanContainer extends BeanItemContainer<MemberBean> {
	
	private MemberBeanContainer() {
		super(MemberBean.class);
	}
	
	/**
	 * Factory method: Creates and returns an instance of <code>MemberBeanContainer</code>.
	 * 
	 * @param inMembers {@link QueryResult}
	 * @return {@link MemberBeanContainer}
	 * @throws VException
	 * @throws SQLException
	 */
	public static MemberBeanContainer createData(QueryResult inMembers) throws VException, SQLException {
		MemberBeanContainer outContainer = new MemberBeanContainer();
		while (inMembers.hasMoreElements()) {
			outContainer.addItem(MemberBean.createItem(inMembers.nextAsDomainObject()));
		}
		return outContainer;
	}

	/**
	 * The <code>Select</code> widget displays already selected member entries only if they are
	 * contained in the item container bound to the widget.
	 * Therefore, we add the selected entries here.
	 * 
	 * @param inSelected {@link Collection} of <code>MemberBean</code>s
	 */
	public void addSelected(Collection<MemberBean> inSelected) {
		for (MemberBean lMember : inSelected) {
			addItem(lMember);
		}
	}

}

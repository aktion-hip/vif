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

package org.hip.vif.forum.search.data;

import java.sql.SQLException;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;

import com.vaadin.data.util.BeanItemContainer;

/**
 * View model to display the results of a content search.
 * 
 * @author Luthiger
 * Created: 04.11.2011
 */
@SuppressWarnings("serial")
public class ContributionContainer extends BeanItemContainer<ContributionWrapper> {
	public static final String[] NATURAL_COL_ORDER = new String[] {"decimalID", "question", "author", "group"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	public static String[] COL_HEADERS = new String[] {"ui.search.view.table.decimal", "ui.search.view.table.question", "ui.search.view.table.author", "ui.search.view.table.group"};  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	private ContributionContainer() {
		super(ContributionWrapper.class);
	}
	
	/**
	 * Static factory method to create an instance of <code>ContributionContainer</code>.
	 * 
	 * @param inSearchResult {@link QueryResult}
	 * @return {@link ContributionContainer}
	 * @throws VException
	 * @throws SQLException
	 */
	public static ContributionContainer createData(QueryResult inSearchResult) throws VException, SQLException {
		ContributionContainer out = new ContributionContainer();
		while (inSearchResult.hasMoreElements()) {
			out.addItem(ContributionWrapper.createItem(inSearchResult.nextAsDomainObject()));
		}
		return out;
	}

}
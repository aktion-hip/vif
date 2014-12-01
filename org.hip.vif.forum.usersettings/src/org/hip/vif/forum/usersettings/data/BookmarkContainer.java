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

/** Container for bookmark bean instances.
 *
 * @author Luthiger Created: 20.12.2011 */
@SuppressWarnings("serial")
public class BookmarkContainer extends BeanItemContainer<BookmarkBean> {
    public static final String ITEM_CHK = "chk"; //$NON-NLS-1$
    public static final String ITEM_LOCAL = "local"; //$NON-NLS-1$
    public static final String ITEM_CHECKED = "checked"; //$NON-NLS-1$
    public static final Object[] NATURAL_COL_ORDER = new Object[] { ITEM_CHK, "text" }; //$NON-NLS-1$
    public static final String[] COL_HEADERS = new String[] { "", "ui.usersettings.data.column.bookmark" }; //$NON-NLS-1$ //$NON-NLS-2$

    private BookmarkContainer() {
        super(BookmarkBean.class);
    }

    /** Factory method: Creates and returns an instance of <code>BookmarkContainer</code>.
     * 
     * @param inData {@link QueryResult}
     * @return {@link BookmarkContainer}
     * @throws VException
     * @throws SQLException */
    public static BookmarkContainer createData(final QueryResult inData) throws VException, SQLException {
        final BookmarkContainer outData = new BookmarkContainer();
        while (inData.hasMoreElements()) {
            outData.addItem(BookmarkBean.createItem(inData.nextAsDomainObject()));
        }
        return outData;
    }

}

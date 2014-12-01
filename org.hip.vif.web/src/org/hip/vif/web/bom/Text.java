/**
    This package is part of the application VIF.
    Copyright (C) 2014, Benno Luthiger

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
package org.hip.vif.web.bom;

import java.sql.SQLException;

import org.hip.kernel.exc.VException;

/** Interface for text instances using message and Vaadin functionality.
 *
 * @author lbenno */
public interface Text extends org.hip.vif.core.bom.Text {

    /** <pre>
     * &lt;option value="Value" selected="selected">Label&lt;/option>
     * ...
     * </pre>
     *
     * @return String the html for the options in an html select of a text type. The appropriate option is displayed
     *         <code>selected</code>.
     * @throws VException */
    public String getOptionsSelected() throws VException;

    /** Renders the text entry for a plain text notification mail.
     *
     * @return String
     * @throws VException
     * @throws SQLException */
    public String getNotification() throws VException, SQLException;

    /** Renders the text entry for a html notification mail.
     *
     * @return String
     * @throws VException
     * @throws SQLException */
    public String getNotificationHtml() throws VException, SQLException;

}

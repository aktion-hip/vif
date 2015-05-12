/**
 This package is part of the application VIF.
 Copyright (C) 2010-2015, Benno Luthiger

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
package org.hip.vif.core.bom;

import java.sql.SQLException;

import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;

/** Interface of home for models to download files.
 *
 * @author Luthiger Created: 19.09.2010 */
public interface DownloadTextHome extends DomainObjectHome {
    String KEY_ID = "ID";
    String KEY_LABEL = "Label";
    String KEY_UUID = "UUID";
    String KEY_MIME = "Mime";
    String KEY_DOCTYPE = "DocType";
    String KEY_TEXTID = "TextID";
    String KEY_MEMBERID = "MemberID";

    /** Returns all download entries belonging to the text entry with the specified key.
     *
     * @param inTextID Long text entry ID
     * @return QueryResult
     * @throws VException
     * @throws SQLException */
    QueryResult getDownloads(Long inTextID) throws VException, SQLException;

    /** Returns the download entry for the specified ID.
     *
     * @param inDownloadID String the entry's ID
     * @return DownloadText
     * @throws VException */
    DownloadText getDownload(String inDownloadID) throws VException;

    /** Deletes the entry with the specified ID.
     *
     * @param inDownloadID String the entry's ID
     * @throws VException
     * @throws SQLException */
    void deleteDownload(String inDownloadID) throws VException, SQLException;

}

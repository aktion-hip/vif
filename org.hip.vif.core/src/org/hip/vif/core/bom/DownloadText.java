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

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.exc.VException;
import org.hip.kernel.servlet.impl.FileItem;
import org.hip.vif.core.exc.ProhibitedFileException;

/** Interface of model to download files.
 *
 * @author Luthiger Created: 19.09.2010 */
public interface DownloadText extends DomainObject {

    /** Create a new download entry.
     *
     * @param inValues {@link IDownloadTextValues}
     * @return Long id of the newly created entry
     * @throws VException
     * @throws SQLException */
    Long ucNew(IDownloadTextValues inValues) throws VException, SQLException;

    /** Sets the specified values to the model.
     *
     * @param inValues {@link IDownloadTextValues}
     * @throws VException */
    void setValuesToModel(IDownloadTextValues inValues) throws VException;

    // ---

    /** Interface for parameter objects.
     *
     * @author Luthiger */
    interface IDownloadTextValues {
        void setTextID(Long inTextID); // NOPMD

        Long getTextID(); // NOPMD

        String getLabel(); // NOPMD

        String getUUID(); // NOPMD

        String getMimetype(); // NOPMD

        String getDoctype(); // NOPMD

        Long getMemberID(); // NOPMD

        FileItem getFile(); // NOPMD

        boolean hasUpload(); // NOPMD

        /** Checks the file type of the uploaded file.
         *
         * @throws ProhibitedFileException */
        void checkType() throws ProhibitedFileException;
    }

}

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
package org.hip.vif.core.bom.impl;

import java.sql.SQLException;

import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.DownloadText;
import org.hip.vif.core.bom.DownloadTextHome;
import org.hip.vif.core.exc.Assert;
import org.hip.vif.core.exc.Assert.AssertLevel;

/** Implementation of model to download files.
 *
 * @author Luthiger Created: 19.09.2010 */
@SuppressWarnings("serial")
public class DownloadTextImpl extends DomainObjectImpl implements DownloadText { // NOPMD by lbenno 
    public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.DownloadTextHomeImpl";

    /** This Method returns the class name of the home.
     *
     * @return java.lang.String */
    @Override
    public String getHomeClassName() {
        return HOME_CLASS_NAME;
    }

    @Override
    public Long ucNew(final IDownloadTextValues inValues) throws VException, SQLException { // NOPMD by lbenno 
        preCheck(inValues.getMimetype());
        setValuesToModel(inValues);
        return insert(true);
    }

    @Override
    public void setValuesToModel(final IDownloadTextValues inValues) throws VException { // NOPMD by lbenno 
        set(DownloadTextHome.KEY_LABEL, inValues.getLabel());
        set(DownloadTextHome.KEY_DOCTYPE, inValues.getDoctype());
        set(DownloadTextHome.KEY_MIME, inValues.getMimetype());
        set(DownloadTextHome.KEY_UUID, inValues.getUUID());
        set(DownloadTextHome.KEY_TEXTID, inValues.getTextID());
        set(DownloadTextHome.KEY_MEMBERID, inValues.getMemberID());
    }

    private void preCheck(final String inMimetype) {
        // name of file must be > 0
        Assert.assertTrue(AssertLevel.ERROR, this, "preCheck", inMimetype.length() > 0);
    }

}

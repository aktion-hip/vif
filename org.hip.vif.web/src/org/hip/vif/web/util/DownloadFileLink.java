/**
	This package is part of the application VIF.
	Copyright (C) 2011-2014, Benno Luthiger

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

package org.hip.vif.web.util;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.web.Activator;
import org.hip.vif.web.util.DownloadFileResouce.DownloadFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Link;

/** Link for a download file.
 *
 * @author Luthiger Created: 19.06.2011 */
@SuppressWarnings("serial")
public class DownloadFileLink extends Link {
    private static final Logger LOG = LoggerFactory.getLogger(DownloadFileLink.class);

    private static final String TARGET_NAME = "_new"; //$NON-NLS-1$
    private final Long downloadID;

    /** Constructor
     *
     * @param inCaption String the link text
     * @param inID Long the ID of the <code>DownloadText</code> business model */
    public DownloadFileLink(final String inCaption, final Long inID) {
        super();
        setWidthUndefined();
        setCaption(inCaption);
        setStyleName("vif-uploaded");
        setDescription(Activator.getMessages().getMessage("download.link.description")); //$NON-NLS-1$
        setTargetName(TARGET_NAME);
        downloadID = inID;
    }

    @Override
    public void attach() { // NOPMD
        super.attach();
        prepareResource();
    }

    /** Update the component with actual values.
     *
     * @param inCaption String the link text
     * @param inID Long the ID of the <code>DownloadText</code> business model */
    public void update(final String inCaption, final Long inID) {
        setCaption(inCaption);
        prepareResource();
    }

    private void prepareResource() {
        DownloadFileResouce lDownload;
        try {
            lDownload = new DownloadFileResouce(getCaption(), new DownloadFile(BOMHelper.getDownloadTextHome()
                    .getDownload(downloadID.toString())));
            setResource(lDownload);
        } catch (final VException exc) {
            LOG.error("Error while preparing the link for the download of file with ID {}!", downloadID.toString(), exc); //$NON-NLS-1$
        }
    }

}

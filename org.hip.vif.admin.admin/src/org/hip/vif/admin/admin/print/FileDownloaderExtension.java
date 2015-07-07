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
package org.hip.vif.admin.admin.print;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.hip.vif.admin.admin.data.GroupWrapper;
import org.hip.vif.admin.admin.tasks.PrintGroupTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.DownloadStream;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.UI;

/** Extension to download files.
 *
 * @author lbenno
 *
 * @see http://bit.ly/1l2kFfG
 * @see https://vaadin.com/forum#!/thread/2864064
 * @see https://vaadin.com/wiki/-/wiki/Main/Letting+the+user+download+a+file */
@SuppressWarnings("serial")
public class FileDownloaderExtension extends FileDownloader {
    private static final Logger LOG = LoggerFactory.getLogger(FileDownloaderExtension.class);

    private final ListSelect groupSelect;
    private final PrintGroupTask printTask;

    /** FileDownloaderExtension constructor.
     *
     * @param inGroups {@link ListSelect} the widget with the groups selected for print out.
     * @param inTask {@link PrintGroupTask} the print group task */
    public FileDownloaderExtension(final ListSelect inGroups, final PrintGroupTask inTask) {
        super(new ResourceProxy());
        groupSelect = inGroups;
        printTask = inTask;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean handleConnectorRequest(final VaadinRequest inRequest, final VaadinResponse inResponse,
            final String inPath) throws IOException {
        if (!inPath.matches("dl(/.*)?")) {
            // Ignore if it isn't for us
            return false;
        }

        final VaadinSession lSession = getSession();
        lSession.lock();

        DownloadStream lDownloadStream = null;
        try {
            final Collection<GroupWrapper> lSelected = (Collection<GroupWrapper>) groupSelect.getValue();
            Collection<GroupExtent> lGroups = new ArrayList<GroupExtent>();
            for (final GroupWrapper lGroup : lSelected) {
                final Long lGroupID = lGroup.getGroupID();
                if (lGroupID.equals(0l)) {
                    lGroups = printTask.getAllGroups();
                    break;
                } else {
                    lGroups.add(new GroupExtent(lGroup.getGroupID()));
                }
            }

            final DownloadFile lDownloadFile = new DownloadFile(lGroups, VaadinSession.getCurrent().getLocale());
            lDownloadStream = lDownloadFile.getStream();
        } catch (final Exception exc) {
            LOG.error("Error encountered while printing the discussion groups!", exc); //$NON-NLS-1$
        } finally {
            lSession.unlock();
        }
        if (lDownloadStream != null) {
            lDownloadStream.writeResponse(inRequest, inResponse);
        }
        groupSelect.clear();
        return true;
    }

    @Override
    protected VaadinSession getSession() {
        final UI lUI = groupSelect.getUI();
        if (lUI == null) {
            return null;
        } else {
            return lUI.getSession();
        }
    }

    // ---

    private static class ResourceProxy extends StreamResource {
        protected ResourceProxy() {
            super(null, DownloadFile.getFileName());
        }
    }
}

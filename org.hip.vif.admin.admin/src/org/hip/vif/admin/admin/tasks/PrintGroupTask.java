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
package org.hip.vif.admin.admin.tasks;

import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.QueryResult;
import org.hip.vif.admin.admin.Constants;
import org.hip.vif.admin.admin.data.GroupContainer;
import org.hip.vif.admin.admin.data.GroupWrapper;
import org.hip.vif.admin.admin.print.DownloadFile;
import org.hip.vif.admin.admin.print.GroupExtent;
import org.hip.vif.admin.admin.ui.PrintGroupView;
import org.hip.vif.web.bom.GroupHome;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.AbstractExtension;
import com.vaadin.server.ConnectorResource;
import com.vaadin.server.DownloadStream;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * Task to display the form where the group admin can start the print out of
 * selected groups.
 * 
 * @author Luthiger Created: 30.12.2011
 */
@UseCaseController
public class PrintGroupTask extends SendMailTask {
	private static final Logger LOG = LoggerFactory
			.getLogger(PrintGroupTask.class);

	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_PRINT_GROUP;
	}

	@Override
	protected Component runChecked() throws RiplaException {
		emptyContextMenu();
		final GroupHome lGroupHome = VifBOMHelper.getGroupHome();
		try {
			final Long lActorID = getActor().getActorID();
			return new PrintGroupView(
					GroupContainer.createData(lGroupHome
							.selectForAdministration(lActorID, createOrder()),
							isAdmin(lActorID)), this);
		}
		catch (final Exception exc) {
			throw createContactAdminException(exc);
		}
	}

	/**
	 * Callback method, print the selected groups into an OpenOffice.org
	 * document.
	 * 
	 * @param inGroups
	 *            Collection<GroupWrapper>
	 * @param inView
	 *            {@link AbstractComponent}
	 * @return boolean <code>true</code> if successful
	 */
	public boolean printGroups(final Collection<GroupWrapper> inGroups,
			final AbstractComponent inView) {
		try {
			Collection<GroupExtent> lGroups = new Vector<GroupExtent>();
			for (final GroupWrapper lGroup : inGroups) {
				final Long lGroupID = lGroup.getGroupID();
				if (lGroupID.equals(0l)) {
					lGroups = getAllGroups();
					break;
				} else {
					lGroups.add(new GroupExtent(lGroup.getGroupID()));
				}
			}

			final DownloaderExtension downloader = new DownloaderExtension();
			downloader.extend(inView);
			downloader.setDownloadResource(new DownloadFile(lGroups,
					getAppLocale()));

			sendEvent(PrintGroupTask.class);
			return true;
		}
		catch (final Exception exc) {
			LOG.error(
					"Error encountered while printing the discussion groups!", exc); //$NON-NLS-1$
		}
		return false;
	}

	private Collection<GroupExtent> getAllGroups() throws Exception {
		final Collection<GroupExtent> outGroups = new Vector<GroupExtent>();
		final QueryResult lGroups = VifBOMHelper
				.getGroupHome()
				.selectForAdministration(getActor().getActorID(), createOrder());
		while (lGroups.hasMoreElements()) {
			outGroups.add(new GroupExtent(BeanWrapperHelper.getLong(
					GroupHome.KEY_ID, lGroups.next())));
		}
		return outGroups;
	}

	// ===

	/**
	 * @see http://bit.ly/1l2kFfG
	 */
	@SuppressWarnings("serial")
	private static class DownloaderExtension extends AbstractExtension {

		public void extend(final AbstractComponent inTarget) {
			super.extend(inTarget);
		}

		public void setDownloadResource(final Resource inResource) {
			// forces re-transfer
			getUI().getConnectorTracker().setDiffState(this, null);
			setResource("dl", inResource);
			markAsDirty();
		}

		@Override
		public boolean handleConnectorRequest(
				final com.vaadin.server.VaadinRequest inRequest,
				final com.vaadin.server.VaadinResponse inResponse,
				final String inPath) throws java.io.IOException {

			if (!inPath.matches("dl(/.*)?")) {
				// Ignore if it isn't for us
				return false;
			}

			final VaadinSession session = getSession();
			session.lock();

			DownloadStream stream;
			try {
				final Resource resource = getDownloadResource();
				if (!(resource instanceof ConnectorResource)) {
					return false;
				}
				stream = ((ConnectorResource) resource).getStream();
			} finally {
				session.unlock();
			}

			stream.writeResponse(inRequest, inResponse);
			return true;
		}

		public Resource getDownloadResource() {
			return getResource("dl");
		}
	}

}

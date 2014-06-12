/**
 This package is part of the administration of the application VIF.
 Copyright (C) 2008-2014, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.admin.admin.tasks;

import java.util.Collection;

import org.hip.kernel.servlet.Task;
import org.hip.vif.admin.admin.Activator;
import org.hip.vif.admin.admin.Constants;
import org.hip.vif.admin.admin.ui.ReindexView;
import org.hip.vif.core.authorization.IAuthorization;
import org.hip.vif.core.bom.search.VIFContentIndexer;
import org.hip.vif.core.bom.search.VIFMemberIndexer;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.ripla.interfaces.IMessages;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;

/**
 * Task to display the page with the administration tasks to reindex the
 * database.
 * 
 * @author Benno Luthiger
 */
@UseCaseController
public class RefreshIndexTask extends AbstractWebController {
	private static final String NL = "<br />"; //$NON-NLS-1$

	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_REFRESH_INDEX;
	}

	/**
	 * @see Task#run()
	 */
	@Override
	public Component runChecked() throws RiplaException {
		emptyContextMenu();
		final IAuthorization lAuthorization = getActor().getAuthorization();
		if (!lAuthorization.hasPermission(Constants.PERMISSION_REFRESH_INDEX)) {
			throw createNoPermissionException();
		}
		return new ReindexView(this);
	}

	/**
	 * Callback method, processing the view input.
	 * 
	 * @param inCheckBoxes
	 *            {@link Collection}
	 * @return String the feedback
	 * @throws Exception
	 */
	public String reindex(final Collection<CheckBox> inCheckBoxes)
			throws Exception {
		final IMessages lMessages = Activator.getMessages();
		final StringBuilder out = new StringBuilder();
		for (final CheckBox lCheckBox : inCheckBoxes) {
			final Boolean value = lCheckBox.getValue();
			if (value != null && value) {
				if (Constants.INDEX_CONTENT.equals(lCheckBox.getData())) {
					final VIFContentIndexer lIndexer = new VIFContentIndexer();
					out.append(lMessages
							.getFormattedMessage(
									"admin.reindex.feedback.content", (Object[]) lIndexer.refreshIndex())); //$NON-NLS-1$
					out.append(NL);
				} else {
					final VIFMemberIndexer lIndexer = new VIFMemberIndexer();
					out.append(lMessages
							.getFormattedMessage(
									"admin.reindex.feedback.person", (Object[]) lIndexer.refreshIndex())); //$NON-NLS-1$
					out.append(NL);
				}
			}
		}
		return new String(out);
	}

}

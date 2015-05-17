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
package org.hip.vif.admin.groupadmin.tasks;

import java.sql.SQLException;

import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.web.bom.GroupHome;
import org.hip.vif.web.bom.VifBOMHelper;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.ripla.util.ParameterObject;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;

/** Create a new bibliographical entry.
 *
 * @author Luthiger Created: 26.09.2011 */
@UseCaseController
public class BibliographyNewTask extends AbstractBibliographyTask { // NOPMD by lbenno 

    @Override
    protected Component runChecked() throws RiplaException { // NOPMD by lbenno 
        try {
            final ParameterObject lParameters = getParameters();
            if (lParameters == null) {
                final Group lGroup = VifBOMHelper.getGroupHome().getGroup(getGroupID());
                final String lMessage = Activator
                        .getMessages()
                        .getFormattedMessage(
                                "ui.contributions.process.no.pending", lGroup.get(GroupHome.KEY_ID), lGroup.get(GroupHome.KEY_NAME)); //$NON-NLS-1$
                return reDisplay(lMessage, Notification.Type.HUMANIZED_MESSAGE);
            }
            else {
                final Text lText = (Text) BOMHelper.getTextHome().create();
                lText.set(TextHome.KEY_AUTHOR,
                        lParameters.get(KEY_PARAMETER_AUTHOR));
                lText.set(TextHome.KEY_TITLE, lParameters.get(KEY_PARAMETER_TITLE));
                return editBibliography(lText, 0l, 0, true); //$NON-NLS-1$
            }
        } catch (final VException | SQLException exc) {
            throw createContactAdminException(exc);
        }
    }

}

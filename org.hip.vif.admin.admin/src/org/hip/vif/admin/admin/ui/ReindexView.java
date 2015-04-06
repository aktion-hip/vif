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

package org.hip.vif.admin.admin.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.hip.vif.admin.admin.Activator;
import org.hip.vif.admin.admin.Constants;
import org.hip.vif.admin.admin.tasks.RefreshIndexTask;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

/** @author Luthiger Created: 30.10.2011 */
@SuppressWarnings("serial")
public class ReindexView extends AbstractAdminView {
    private static final Logger LOG = LoggerFactory.getLogger(ReindexView.class);

    private static final String FORMAT_LABEL = "<div class=\"vif-check-label\">%s</div>"; //$NON-NLS-1$
    private final Collection<CheckBox> checkBoxes = new ArrayList<CheckBox>();
    private final Button reindex;

    /** Constructor
     *
     * @param inTask {@link RefreshIndexTask} */
    public ReindexView(final RefreshIndexTask inTask) {
        super();
        final IMessages lMessages = Activator.getMessages();
        final VerticalLayout lLayout = initLayout(lMessages, "admin.reindex.title.page"); //$NON-NLS-1$

        final CheckBox lContentIndex = createCheckbox(
                lMessages.getMessage("admin.reindex.check.content"), Constants.INDEX_CONTENT); //$NON-NLS-1$
        lContentIndex.focus();
        lLayout.addComponent(lContentIndex);
        lLayout.addComponent(new Label(
                String.format(FORMAT_LABEL,
                        lMessages.getMessage("admin.reindex.label.content")), ContentMode.HTML)); //$NON-NLS-1$
        final CheckBox lPersonIndex = createCheckbox(
                lMessages.getMessage("admin.reindex.check.person"), Constants.INDEX_MEMBER); //$NON-NLS-1$
        lLayout.addComponent(lPersonIndex);
        lLayout.addComponent(new Label(
                String.format(FORMAT_LABEL,
                        lMessages.getMessage("admin.reindex.label.person")), ContentMode.HTML)); //$NON-NLS-1$

        lLayout.addComponent(RiplaViewHelper.createSpacer());
        reindex = new Button(lMessages.getMessage("admin.reindex.button.start")); //$NON-NLS-1$
        reindex.setClickShortcut(KeyCode.ENTER);
        reindex.setEnabled(false);
        reindex.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) { // NOPMD
                try {
                    final String lFeedback = inTask.reindex(checkBoxes);
                    new Notification(lMessages.getMessage("admin.reindex.check.content"), lFeedback,
                            Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
                }
                catch (final Exception exc) { // NOPMD
                    LOG.error("Error encountered during search index refresh!", exc);
                    Notification.show(
                            lMessages.getMessage("errmsg.reindex"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                }
            }
        });

        lLayout.addComponent(reindex);
    }

    private CheckBox createCheckbox(final String inLabel, final String inData) {
        final CheckBox outCheckBox = new CheckBox(inLabel);
        outCheckBox.setStyleName("vif-check"); //$NON-NLS-1$
        outCheckBox.setData(inData);
        outCheckBox.setImmediate(true);
        outCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent inEvent) { // NOPMD
                for (final CheckBox lCheckBox : checkBoxes) {
                    final Boolean lValue = lCheckBox.getValue();
                    if (lValue != null && lValue.booleanValue()) {
                        reindex.setEnabled(true);
                        return;
                    }
                }
                reindex.setEnabled(false);
            }
        });
        checkBoxes.add(outCheckBox);
        return outCheckBox;
    }

}

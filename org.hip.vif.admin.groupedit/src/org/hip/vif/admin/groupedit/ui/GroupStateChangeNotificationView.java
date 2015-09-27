/**
    This package is part of the application VIF.
    Copyright (C) 2011-2015, Benno Luthiger

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

package org.hip.vif.admin.groupedit.ui;

import org.hip.vif.admin.groupedit.Activator;
import org.hip.vif.admin.groupedit.tasks.GroupStateChangeNotificationTask;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/** View to ask the group administrator whether to notify the group's participants about state changes of the discussion
 * group.
 *
 * @author Luthiger Created: 13.11.2011 */
@SuppressWarnings("serial")
public class GroupStateChangeNotificationView extends CustomComponent {
    private static final int DEF_WIDTH = 700;
    private static final int DEF_HEIGHT = 250;

    private static final String FLD_SUBJECT = "subject";
    private static final String FLD_BODY = "body";

    /** View constructor.
     *
     * @param inGroupName String
     * @param inSubject String
     * @param inBody String
     * @param inTask {@link GroupStateChangeNotificationTask} */
    public GroupStateChangeNotificationView(final String inGroupName,
            final String inSubject, final String inBody,
            final GroupStateChangeNotificationTask inTask) {
        final VerticalLayout lLayout = new VerticalLayout();
        setCompositionRoot(lLayout);

        final IMessages lMessages = Activator.getMessages();
        lLayout.setStyleName("vif-view"); //$NON-NLS-1$
        lLayout.addComponent(new Label(
                String.format(
                        VIFViewHelper.TMPL_TITLE,
                        "vif-pagetitle", lMessages.getFormattedMessage("ui.group.notify.view.title.page", inGroupName)), //$NON-NLS-1$ //$NON-NLS-2$
                ContentMode.HTML));

        lLayout.addComponent(new Label(
                String.format(
                        VIFViewHelper.TMPL_TITLE, "vif-remark", lMessages.getMessage("ui.group.notify.label.remark")), //$NON-NLS-1$ //$NON-NLS-2$
                ContentMode.HTML));

        final PropertysetItem lMail = new PropertysetItem();
        lMail.addItemProperty(FLD_SUBJECT, new ObjectProperty<String>(""));
        lMail.addItemProperty(FLD_BODY, new ObjectProperty<String>(""));
        final FieldGroup lForm = new FieldGroup(lMail);
        final VerticalLayout lFormLayout = new VerticalLayout();

        String lLabel = lMessages.getMessage("ui.group.notify.label.subject"); //$NON-NLS-1$
        lFormLayout.addComponent(
                new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-title", lLabel), ContentMode.HTML)); //$NON-NLS-1$
        final TextField lSubject = RiplaViewHelper.createTextField(inSubject,
                DEF_WIDTH, new NotEmptyValidator(lLabel, lMessages));
        addField(lLabel, lSubject, lFormLayout, lMessages);

        lLabel = lMessages.getMessage("ui.group.notify.label.body"); //$NON-NLS-1$
        lFormLayout.addComponent(
                new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-title", lLabel), ContentMode.HTML)); //$NON-NLS-1$
        final RichTextArea lBody = VIFViewHelper
                .createTextArea(inBody, DEF_WIDTH, DEF_HEIGHT,
                        new NotEmptyValidator(lLabel, lMessages));
        addField(lLabel, lBody, lFormLayout, lMessages);
        lForm.bind(lSubject, FLD_SUBJECT);
        lForm.bind(lBody, FLD_BODY);
        lLayout.addComponent(lFormLayout);

        final Button lNotify = new Button(
                lMessages.getMessage("ui.group.notify.button.notify")); //$NON-NLS-1$
        lNotify.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                try {
                    lForm.commit();
                    if (!inTask.doNotification(lMail.getItemProperty(FLD_SUBJECT).getValue().toString(),
                            lMail.getItemProperty(FLD_BODY).getValue().toString())) {
                        Notification.show(lMessages
                                .getMessage("errmsg.notification.send"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                    }
                } catch (InvalidValueException | CommitException exc) {
                    // intentionally left empty
                }
            }
        });
        final Button lContinue = new Button(
                lMessages.getMessage("ui.group.notify.button.continue")); //$NON-NLS-1$
        lContinue.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                inTask.doContinue();
            }
        });
        lLayout.addComponent(RiplaViewHelper.createButtons(lNotify, lContinue));
    }

    private void addField(final String inFieldID, final AbstractField<?> inField, final VerticalLayout inFormLayout,
            final IMessages inMessages) {
        inFormLayout.addComponent(inField);
        inField.setRequiredError(inMessages.getFormattedMessage("errmsg.field.not.empty", inFieldID)); //$NON-NLS-1$
        inField.setRequired(true);
        inField.setImmediate(true);
    }

    // --- private classes ---

    private static class NotEmptyValidator extends StringLengthValidator {
        NotEmptyValidator(final String inFieldName, final IMessages inMessages) {
            super(inMessages.getFormattedMessage("errmsg.field.not.empty", inFieldName), 1, -1, false); //$NON-NLS-1$
        }
    }

}
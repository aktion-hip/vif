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

package org.hip.vif.forum.member.ui;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.util.RatingsHelper;
import org.hip.vif.forum.member.Activator;
import org.hip.vif.forum.member.tasks.PersonalDataEditTask;
import org.hip.vif.web.member.MemberBean;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.MemberViewHelper;
import org.hip.vif.web.util.RatingsTable;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.AbstractFormCreator;
import org.ripla.web.util.LabelValueTable;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/** View to edit the actor's member data and show the actor's ratings.
 *
 * @author Luthiger Created: 06.10.2011 */
@SuppressWarnings("serial")
public class EditPersonalDataView extends CustomComponent {
    private static final int DFT_WIDTH_INPUT = 300;

    /** Constructor
     *
     * @param inMember {@link Member} the member model to display
     * @param inRatings {@link RatingsHelper} the helper object containing the ratings to display
     * @param inTask {@link PersonalDataEditTask} the task controlling this view */
    public EditPersonalDataView(final Member inMember, final RatingsHelper inRatings,
            final PersonalDataEditTask inTask) {
        final VerticalLayout lLayout = new VerticalLayout();
        setCompositionRoot(lLayout);

        final IMessages lMessages = Activator.getMessages();
        lLayout.setStyleName("vif-view"); //$NON-NLS-1$
        lLayout.addComponent(new Label(
                String.format(VIFViewHelper.TMPL_TITLE,
                        "vif-pagetitle", //$NON-NLS-1$
                        lMessages
                                .getFormattedMessage(
                                        "ui.member.view.title.page", //$NON-NLS-1$
                                        BeanWrapperHelper.getString(MemberHome.KEY_USER_ID, inMember))),
                ContentMode.HTML)); //$NON-NLS-2$

        final FormCreator lForm = new FormCreator(inMember);
        lLayout.addComponent(lForm.createForm());
        lLayout.addComponent(RiplaViewHelper.createSpacer());

        final Button lSave = new Button(lMessages.getMessage("ui.member.button.save")); //$NON-NLS-1$
        lLayout.addComponent(lSave);
        lSave.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                try {
                    lForm.commit();
                    actualizeAddress(inMember, lForm.getAddress());
                    if (inTask.saveMember(inMember)) {
                        Notification.show(
                                lMessages.getMessage("msg.task.data.changed"), Type.TRAY_NOTIFICATION); //$NON-NLS-1$
                    } else {
                        Notification.show(lMessages.getMessage("errmsg.save.general"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                    }
                } catch (final CommitException exc) {
                    // intentionally left empty
                }
            }
        });

        lLayout.addComponent(RiplaViewHelper.createSpacer());
        lLayout.addComponent(RiplaViewHelper.createSpacer());
        lLayout.addComponent(new RatingsTable(inRatings));
    }

    private void actualizeAddress(final Member inMember, final ComboBox inAddress) {
        try {
            inMember.set(MemberHome.KEY_SEX, new Long(inAddress.getValue().toString()));
        } catch (final VException exc) {
            // intentionally left empty
        }
    }

    // --- inner classes ---

    private class FormCreator extends AbstractFormCreator {
        private static final int WIDTH_ZIP = 50;
        private static final int WIDTH_CITY = 240;

        private final IMessages messages;
        private final ComboBox address;

        FormCreator(final Member inMember) {
            super(MemberBean.createMemberBean(inMember));
            messages = Activator.getMessages();
            address = MemberViewHelper.getMemberAddress(inMember);
        }

        @Override
        protected Component createTable() {
            final LabelValueTable outTable = new LabelValueTable();

            outTable.addRow(messages.getMessage("ui.member.editor.label.address"), address); //$NON-NLS-1$

            String lFieldLabel = messages.getMessage("ui.member.editor.label.firstname");
            final TextField lFirstField = RiplaViewHelper.createTextField(DFT_WIDTH_INPUT);
            lFirstField.focus();
            outTable.addRowEmphasized(lFieldLabel,
                    VIFViewHelper.addWrapped(addFieldRequired(MemberBean.FN_FIRSTNAME, lFirstField, lFieldLabel)));

            lFieldLabel = messages.getMessage("ui.member.editor.label.name"); //$NON-NLS-1$
            outTable.addRowEmphasized(lFieldLabel,
                    VIFViewHelper.addWrapped(addFieldRequired(MemberBean.FN_NAME,
                            RiplaViewHelper.createTextField(DFT_WIDTH_INPUT), lFieldLabel))); // $NON-NLS-1$
            lFieldLabel = messages.getMessage("ui.member.editor.label.street"); //$NON-NLS-1$
            outTable.addRowEmphasized(
                    lFieldLabel,
                    VIFViewHelper.addWrapped(
                            addFieldRequired(MemberBean.FN_STREET, RiplaViewHelper.createTextField(DFT_WIDTH_INPUT),
                                    lFieldLabel)));
            lFieldLabel = messages.getMessage("ui.member.editor.label.city"); //$NON-NLS-1$
            outTable.addRowEmphasized(lFieldLabel, createZipCityFields(lFieldLabel)); // $NON-NLS-1$
            lFieldLabel = messages.getMessage("ui.member.editor.label.phone"); //$NON-NLS-1$
            outTable.addRow(lFieldLabel,
                    addField(MemberBean.FN_PHONE, RiplaViewHelper.createTextField(DFT_WIDTH_INPUT))); // $NON-NLS-1$
            lFieldLabel = messages.getMessage("ui.member.editor.label.fax"); //$NON-NLS-1$
            outTable.addRow(lFieldLabel, addField(MemberBean.FN_FAX, RiplaViewHelper.createTextField(DFT_WIDTH_INPUT))); // $NON-NLS-1$
            lFieldLabel = messages.getMessage("ui.member.editor.label.mail"); //$NON-NLS-1$
            outTable.addRowEmphasized(lFieldLabel,
                    VIFViewHelper.addWrapped(
                            addFieldRequired(MemberBean.FN_MAIL, RiplaViewHelper.createTextField(DFT_WIDTH_INPUT)))); // $NON-NLS-1$

            return outTable;
        }

        private Component createZipCityFields(final String inFieldLabel) {
            final HorizontalLayout outLayout = new HorizontalLayout();
            final Field<?> lZip = addFieldRequired(MemberBean.FN_ZIP, RiplaViewHelper.createTextField(WIDTH_ZIP),
                    messages.getMessage("ui.member.editor.label.zip")); //$NON-NLS-1$
            lZip.addStyleName("no-indicator");
            outLayout.addComponent(lZip);
            outLayout
                    .addComponent(addFieldRequired(MemberBean.FN_CITY, RiplaViewHelper.createTextField(WIDTH_CITY),
                            inFieldLabel));
            return outLayout;
        }

        ComboBox getAddress() {
            return address;
        }

    }

}

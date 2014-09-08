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

package org.hip.vif.admin.member.ui;

import org.hip.kernel.exc.VException;
import org.hip.vif.admin.member.Activator;
import org.hip.vif.admin.member.data.MemberBean;
import org.hip.vif.admin.member.data.RoleContainer;
import org.hip.vif.admin.member.tasks.AbstractMemberTask;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.hip.vif.core.util.RatingsHelper;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.MemberViewHelper;
import org.hip.vif.web.util.RatingsTable;
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
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/** View to display the member's edit form.
 *
 * @author Luthiger Created: 19.10.2011 */
@SuppressWarnings("serial")
public class MemberEditView extends AbstractMemberView {
    private static final int DFT_WIDTH_INPUT = 300;

    /** Constructor for the view to edit a member's data.
     *
     * @param inMember {@link Member}
     * @param inRoles {@link RoleContainer}
     * @param inRatings {@link RatingsHelper}
     * @param inTask {@link AbstractMemberTask} */
    public MemberEditView(final Member inMember, final RoleContainer inRoles,
            final RatingsHelper inRatings, final AbstractMemberTask inTask) {
        final FormCreator lForm = new FormCreator(inMember, inRoles);
        final VerticalLayout lLayout = createEditLayout(inMember, inRoles, inTask,
                lForm, "ui.member.edit.title.page"); //$NON-NLS-1$

        final Button lSave = createButtonSave(inMember, inRoles, inTask, lForm);
        final Button lResetPW = createButtonResetPW(inTask);
        lLayout.addComponent(RiplaViewHelper.createButtons(lSave, lResetPW));
        lLayout.addComponent(RiplaViewHelper.createSpacer());
        lLayout.addComponent(RiplaViewHelper.createSpacer());
        lLayout.addComponent(new RatingsTable(inRatings));
    }

    /** Constructor for the view to create a new member record.
     *
     * @param inMember {@link Member}
     * @param inRoles {@link RoleContainer}
     * @param inTask {@link AbstractMemberTask} */
    public MemberEditView(final Member inMember, final RoleContainer inRoles,
            final AbstractMemberTask inTask) {
        final FormCreator lForm = new FormNewCreator(inMember, inRoles);
        final VerticalLayout lLayout = createEditLayout(inMember, inRoles, inTask,
                lForm, "ui.member.new.title.page"); //$NON-NLS-1$
        lLayout.addComponent(createButtonSave(inMember, inRoles, inTask, lForm));
    }

    private VerticalLayout createEditLayout(final Member inMember,
            final RoleContainer inRoles, final AbstractMemberTask inTask,
            final FormCreator inForm, final String inTitleKey) {
        final IMessages lMessages = Activator.getMessages();
        final VerticalLayout lLayout = initLayout(lMessages, inTitleKey);
        final Component lForm = inForm.createForm();
        lLayout.addComponent(lForm);
        lLayout.addComponent(RiplaViewHelper.createSpacer());
        return lLayout;
    }

    private Button createButtonSave(final Member inMember,
            final RoleContainer inRoles, final AbstractMemberTask inTask,
            final FormCreator inForm) {
        final IMessages lMessages = Activator.getMessages();
        final Button outSave = createSaveButton(lMessages);
        outSave.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                try {
                    inForm.commit();
                    actualizeAddress(inMember, inForm.getAddress());
                    if (!inTask.saveMember(inMember, inRoles)) {
                        Notification.show(lMessages
                                .getMessage("errmsg.save.general"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                    }
                }
                catch (final CommitException exc) {
                    // intentionally left empty
                }
                catch (final ExternIDNotUniqueException exc) {
                    inForm.focusInit();
                    Notification.show(lMessages
                            .getMessage("errmsg.member.not.unique"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                }
            }
        });
        return outSave;
    }

    private Button createButtonResetPW(final AbstractMemberTask inTask) {
        final IMessages lMessages = Activator.getMessages();
        final Button outResetPW = new Button(
                lMessages.getMessage("ui.member.button.reset.pw")); //$NON-NLS-1$
        outResetPW.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                if (!inTask.resetPW()) {

                }
            }
        });
        return outResetPW;
    }

    private void actualizeAddress(final Member inMember, final ComboBox inAddress) {
        try {
            inMember.set(MemberHome.KEY_SEX, new Long(inAddress.getValue()
                    .toString()));
        } catch (final VException exc) {
            // intentionally left empty
        }
    }

    // --- inner classes ---

    private class FormCreator extends AbstractFormCreator {
        private static final int WIDTH_ZIP = 50;
        private static final int WIDTH_CITY = 240;

        protected Member member;
        private final RoleContainer roles;
        protected IMessages messages;
        protected TextField firstfield;
        protected LabelValueTable table;
        private final ComboBox address;

        FormCreator(final Member inMember, final RoleContainer inRoles) {
            super(MemberBean.createMemberBean(inMember, inRoles));
            member = inMember;
            roles = inRoles;
            messages = Activator.getMessages();
            table = new LabelValueTable();
            address = MemberViewHelper.getMemberAddress(member);
        }

        @Override
        protected Component createTable() {
            final Label lUserID = new Label(BeanWrapperHelper.getString(
                    MemberHome.KEY_USER_ID, member), ContentMode.HTML);
            lUserID.setStyleName("vif-value vif-value-emphasized"); //$NON-NLS-1$
            table.addRow(
                    messages.getMessage("ui.member.editor.label.userid"), lUserID); //$NON-NLS-1$

            firstfield = RiplaViewHelper.createTextField(DFT_WIDTH_INPUT);
            focusInit();

            table.addRow(
                    messages.getMessage("ui.member.editor.label.address"), getAddress()); //$NON-NLS-1$

            final String lFieldLabel = messages
                    .getMessage("ui.member.editor.label.firstname"); //$NON-NLS-1$
            table.addRowEmphasized(lFieldLabel,
                    addFieldRequired(MemberBean.FN_FIRSTNAME, firstfield, lFieldLabel));

            return fillTable(table);
        }

        protected LabelValueTable fillTable(final LabelValueTable inTable) {
            String lFieldLabel = messages
                    .getMessage("ui.member.editor.label.name"); //$NON-NLS-1$
            inTable.addRowEmphasized(
                    lFieldLabel,
                    addFieldRequired(MemberBean.FN_NAME, RiplaViewHelper.createTextField(DFT_WIDTH_INPUT), lFieldLabel)); //$NON-NLS-1$
            lFieldLabel = messages.getMessage("ui.member.editor.label.street"); //$NON-NLS-1$
            inTable.addRowEmphasized(
                    lFieldLabel,
                    addFieldRequired(MemberBean.FN_STREET, RiplaViewHelper.createTextField(DFT_WIDTH_INPUT),
                            lFieldLabel));
            lFieldLabel = messages.getMessage("ui.member.editor.label.city"); //$NON-NLS-1$
            inTable.addRowEmphasized(
                    messages.getMessage("ui.member.editor.label.city"), createZipCityFields(member, lFieldLabel)); //$NON-NLS-1$
            inTable.addRow(
                    messages.getMessage("ui.member.editor.label.phone"), addField(MemberBean.FN_PHONE, RiplaViewHelper.createTextField(DFT_WIDTH_INPUT))); //$NON-NLS-1$
            inTable.addRow(
                    messages.getMessage("ui.member.editor.label.fax"), addField(MemberBean.FN_FAX, RiplaViewHelper.createTextField(DFT_WIDTH_INPUT))); //$NON-NLS-1$
            lFieldLabel = messages.getMessage("ui.member.editor.label.mail"); //$NON-NLS-1$
            inTable.addRowEmphasized(
                    lFieldLabel,
                    addFieldRequired(MemberBean.FN_MAIL, RiplaViewHelper.createTextField(DFT_WIDTH_INPUT), lFieldLabel));
            // option group for the roles
            final OptionGroup lRoles = createRolesOptions(roles);
            lRoles.setRequiredError(messages
                    .getMessage("errmsg.member.role.empty")); //$NON-NLS-1$
            inTable.addRowEmphasized(
                    messages.getMessage("ui.member.editor.label.role"), addFieldRequired(MemberBean.FN_ROLES, lRoles)); //$NON-NLS-1$
            return inTable;
        }

        private Component createZipCityFields(final Member inMember,
                final String inFieldLabel) {
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

        void focusInit() {
            firstfield.focus();
        }
    }

    private class FormNewCreator extends FormCreator {
        FormNewCreator(final Member inMember, final RoleContainer inRoles) {
            super(inMember, inRoles);
        }

        @Override
        protected Component createTable() {
            firstfield = RiplaViewHelper.createTextField(DFT_WIDTH_INPUT);
            focusInit();

            String lFieldLabel = messages
                    .getMessage("ui.member.editor.label.userid"); //$NON-NLS-1$
            table.addRowEmphasized(lFieldLabel,
                    addFieldRequired(MemberBean.FN_USER_ID, firstfield, lFieldLabel));
            table.addRow(
                    messages.getMessage("ui.member.editor.label.address"), getAddress()); //$NON-NLS-1$
            lFieldLabel = messages
                    .getMessage("ui.member.editor.label.firstname"); //$NON-NLS-1$
            table.addRowEmphasized(
                    lFieldLabel,
                    addFieldRequired(MemberBean.FN_FIRSTNAME, RiplaViewHelper.createTextField(DFT_WIDTH_INPUT),
                            lFieldLabel));

            return fillTable(table);
        }

    }

}

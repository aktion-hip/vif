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

package org.hip.vif.web.components;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.web.Activator;
import org.hip.vif.web.member.MemberBean;
import org.hip.vif.web.tasks.DBAccessWorkflowItems.CreateSU;
import org.hip.vif.web.util.MemberViewHelper;
import org.hip.vif.web.util.PasswordInputChecker;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.AbstractFormCreator;
import org.ripla.web.util.LabelValueTable;
import org.ripla.web.util.RiplaViewHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/** Child window displaying the field for that the SU can provide the information to create his SU account.
 *
 * @author Luthiger */
@SuppressWarnings("serial")
public class CreateSUPopup extends AbstractConfigurationPopup {
    private static final Logger LOG = LoggerFactory.getLogger(CreateSUPopup.class);

    private static final int DFT_WIDTH_INPUT = 300;

    /** Constructor.
     *
     * @param inMember {@link Member}
     * @param inCreateSU {@link CreateSU} */
    public CreateSUPopup(final Member inMember, final CreateSU inCreateSU) {
        final IMessages lMessages = Activator.getMessages();

        final Window lPopup = createPopup(lMessages.getMessage("ui.member.editor.title")); //$NON-NLS-1$
        lPopup.setWidth(540, Unit.PIXELS);
        lPopup.setHeight(445, Unit.PIXELS);

        final VerticalLayout lLayout = createLayout((VerticalLayout) lPopup.getContent());
        lLayout.addComponent(getCreateForm(inMember, lMessages, inCreateSU));
        lLayout.addComponent(createCloseButton());
        UI.getCurrent().addWindow(lPopup);
    }

    private Component getCreateForm(final Member inMember, final IMessages inMessages, final CreateSU inController) {
        final VerticalLayout outLayout = new VerticalLayout();

        final FormCreator lForm = new FormCreator(inMember);
        outLayout.addComponent(lForm.createForm());
        outLayout.addComponent(RiplaViewHelper.createSpacer());

        final Button lSave = new Button(inMessages.getMessage("config.button.save")); //$NON-NLS-1$
        lSave.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                try {
                    lForm.commit();
                    inMember.set(MemberHome.KEY_SEX, new Long(lForm.getAddress().getValue().toString()));
                    if (lForm.checkPassword()) {
                        inController.save(inMember);
                    }
                }
                catch (final CommitException exc) {
                    // intentionally left empty
                }
                catch (final VException exc) {
                    LOG.error("Error encountered while storing the SU's address.", exc); //$NON-NLS-1$
                }
            }
        });
        outLayout.addComponent(lSave);

        return outLayout;
    }

    // ---

    private class FormCreator extends AbstractFormCreator {
        private static final int WIDTH_ZIP = 50;
        private static final int WIDTH_CITY = 240;

        // private final Member member;
        private final IMessages messages;
        private final ComboBox address;
        private TextField firstfield;
        private PasswordField pass1;
        private PasswordField pass2;

        FormCreator(final Member inMember) {
            super(MemberBean.createMemberBean(inMember));
            // member = inMember;
            messages = Activator.getMessages();
            address = MemberViewHelper.getMemberAddress(inMember);
        }

        @Override
        protected Component createTable() {
            String lFieldLabel = messages.getMessage("ui.member.editor.label.userid"); //$NON-NLS-1$
            firstfield = RiplaViewHelper.createTextField(DFT_WIDTH_INPUT);
            focusInit();

            final LabelValueTable outTable = new LabelValueTable();
            outTable.addRowEmphasized(lFieldLabel, addFieldRequired(MemberBean.FN_USER_ID, firstfield, lFieldLabel)); //$NON-NLS-1$

            lFieldLabel = messages.getMessage("ui.member.editor.label.pass.set"); //$NON-NLS-1$
            pass1 = createPasswordField(MemberBean.FN_PASSWORD, DFT_WIDTH_INPUT, lFieldLabel);
            outTable.addRowEmphasized(lFieldLabel, pass1); //$NON-NLS-1$
            lFieldLabel = messages.getMessage("ui.member.editor.label.pass.confirm"); //$NON-NLS-1$
            pass2 = createPasswordField(null, DFT_WIDTH_INPUT, lFieldLabel);
            outTable.addRowEmphasized(lFieldLabel, pass2); //$NON-NLS-1$
            outTable.addEmtpyRow();

            outTable.addRow(messages.getMessage("ui.member.editor.label.address"), getAddress()); //$NON-NLS-1$
            lFieldLabel = messages.getMessage("ui.member.editor.label.firstname"); //$NON-NLS-1$
            outTable.addRowEmphasized(lFieldLabel, addFieldRequired(MemberBean.FN_FIRSTNAME,
                    RiplaViewHelper.createTextField(DFT_WIDTH_INPUT),
                    lFieldLabel)); //$NON-NLS-1$
            lFieldLabel = messages.getMessage("ui.member.editor.label.name"); //$NON-NLS-1$
            outTable.addRowEmphasized(lFieldLabel, addFieldRequired(MemberBean.FN_NAME,
                    RiplaViewHelper.createTextField(DFT_WIDTH_INPUT), lFieldLabel)); //$NON-NLS-1$
            lFieldLabel = messages.getMessage("ui.member.editor.label.street"); //$NON-NLS-1$
            outTable.addRowEmphasized(
                    lFieldLabel,
                    addFieldRequired(MemberBean.FN_STREET,
                            RiplaViewHelper.createTextField(DFT_WIDTH_INPUT), lFieldLabel)); //$NON-NLS-1$
            lFieldLabel = messages.getMessage("ui.member.editor.label.city"); //$NON-NLS-1$
            outTable.addRowEmphasized(
                    messages.getMessage("ui.member.editor.label.city"), createZipCityFields(lFieldLabel)); //$NON-NLS-1$
            outTable.addRow(
                    messages.getMessage("ui.member.editor.label.phone"), RiplaViewHelper.createTextField(DFT_WIDTH_INPUT)); //$NON-NLS-1$
            outTable.addRow(
                    messages.getMessage("ui.member.editor.label.fax"), RiplaViewHelper.createTextField(DFT_WIDTH_INPUT)); //$NON-NLS-1$
            lFieldLabel = messages.getMessage("ui.member.editor.label.mail"); //$NON-NLS-1$
            outTable.addRowEmphasized(lFieldLabel, addFieldRequired(MemberBean.FN_MAIL,
                    RiplaViewHelper.createTextField(DFT_WIDTH_INPUT), lFieldLabel)); //$NON-NLS-1$
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

        private PasswordField createPasswordField(final String inFieldId, final int inWidth,
                final String inRequiredFieldLbl) {
            final PasswordField out = new PasswordField();
            if (inFieldId != null) {
                addField(inFieldId, out);
            }
            out.setWidth(inWidth, Unit.PIXELS);
            out.setRequiredError(messages.getFormattedMessage("errmsg.error.not.empty", inRequiredFieldLbl)); //$NON-NLS-1$
            out.setRequired(true);
            out.setImmediate(true);
            out.setStyleName("vif-input"); //$NON-NLS-1$
            return out;
        }

        boolean checkPassword() {
            return new PasswordInputChecker(pass1, pass2).checkInput();
        }

        ComboBox getAddress() {
            return address;
        }

        void focusInit() {
            firstfield.focus();
        }
    }

}

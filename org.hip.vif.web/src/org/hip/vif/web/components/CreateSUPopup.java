/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.web.Activator;
import org.hip.vif.web.tasks.DBAccessWorkflowItems.CreateSU;
import org.hip.vif.web.util.AbstractFormCreator;
import org.hip.vif.web.util.BOProperty;
import org.hip.vif.web.util.MemberViewHelper;
import org.hip.vif.web.util.PasswordInputChecker;
import org.hip.vif.web.util.VIFViewHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Child window displaying the field for that the SU can provide the information
 * to create his SU account.
 * 
 * @author Luthiger
 * Created: 12.02.2012
 */
@SuppressWarnings("serial")
public class CreateSUPopup extends AbstractConfigurationPopup {
	private static final Logger LOG = LoggerFactory.getLogger(CreateSUPopup.class);
	
	private static final int DFT_WIDTH_INPUT = 300;	
	
	/**
	 * Constructor.
	 * 
	 * @param inMain {@link Window}
	 * @param inMember {@link Member}
	 * @param inCreateSU {@link CreateSU}
	 */
	public CreateSUPopup(Window inMain, final Member inMember, CreateSU inCreateSU) {
		IMessages lMessages = Activator.getMessages();
		
		Window lPopup = createPopup(lMessages.getMessage("ui.member.editor.title")); //$NON-NLS-1$
		lPopup.setWidth(540, UNITS_PIXELS);
		lPopup.setHeight(445, UNITS_PIXELS);
		
		VerticalLayout lLayout = createLayout((VerticalLayout) lPopup.getContent());
		lLayout.addComponent(getCreateForm(inMember, lMessages, inCreateSU));
		lLayout.addComponent(createCloseButton());
		inMain.addWindow(lPopup);
	}

	private Component getCreateForm(final Member inMember, IMessages inMessages, final CreateSU inController) {
		final VerticalLayout outLayout = new VerticalLayout();
		
		final FormCreator lForm = new FormCreator(inMember);
		outLayout.addComponent(lForm.createForm());
		outLayout.addComponent(VIFViewHelper.createSpacer());
		
		Button lSave = new Button(inMessages.getMessage("config.button.save")); //$NON-NLS-1$
		lSave.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent inEvent) {
				try {
					lForm.commit();
					inMember.set(MemberHome.KEY_SEX, new Long(lForm.getAddress().getValue().toString()));
					if (lForm.checkPassword(getPopup().getWindow())) {						
						inController.save(inMember);
					}
				}
				catch (InvalidValueException exc) {
					//intentionally left empty
				}
				catch (VException exc) {
					LOG.error("Error encountered while storing the SU's address.", exc); //$NON-NLS-1$
				}
			}
		});
		outLayout.addComponent(lSave);

		return outLayout;
	}
	
//	
	
	private class FormCreator extends AbstractFormCreator {
		private Member member;
		private IMessages messages;
		private Select address;
		private TextField firstfield;
		private PasswordField pass1;
		private PasswordField pass2;

		FormCreator(Member inMember) {
			member = inMember;
			messages = Activator.getMessages();
			address = MemberViewHelper.getMemberAddress(member);
		}

		@Override
		protected Component createTable() {
			String lFieldLabel = messages.getMessage("ui.member.editor.label.userid"); //$NON-NLS-1$
			firstfield = VIFViewHelper.createTextField(member, MemberHome.KEY_USER_ID, DFT_WIDTH_INPUT);
			focusInit();
			
			LabelValueTable outTable = new LabelValueTable();
			outTable.addRowEmphasized(lFieldLabel, addFieldRequired("userid", firstfield, lFieldLabel)); //$NON-NLS-1$

			lFieldLabel = messages.getMessage("ui.member.editor.label.pass.set"); //$NON-NLS-1$
			pass1 = createPasswordField(member, MemberHome.KEY_PASSWORD, DFT_WIDTH_INPUT);
			outTable.addRowEmphasized(lFieldLabel, addFieldRequired("password", pass1, lFieldLabel)); //$NON-NLS-1$
			lFieldLabel = messages.getMessage("ui.member.editor.label.pass.confirm"); //$NON-NLS-1$
			pass2 = createPasswordField(null, null, DFT_WIDTH_INPUT);
			outTable.addRowEmphasized(lFieldLabel, addFieldRequired("passwordConfirm", pass2, lFieldLabel)); //$NON-NLS-1$
			outTable.addEmtpyRow();
			
			outTable.addRow(messages.getMessage("ui.member.editor.label.address"), getAddress()); //$NON-NLS-1$
			lFieldLabel = messages.getMessage("ui.member.editor.label.firstname");  //$NON-NLS-1$
			outTable.addRowEmphasized(lFieldLabel, addFieldRequired("name", VIFViewHelper.createTextField(member, MemberHome.KEY_FIRSTNAME, DFT_WIDTH_INPUT), lFieldLabel)); //$NON-NLS-1$
			lFieldLabel = messages.getMessage("ui.member.editor.label.name"); //$NON-NLS-1$
			outTable.addRowEmphasized(lFieldLabel, addFieldRequired("name", VIFViewHelper.createTextField(member, MemberHome.KEY_NAME, DFT_WIDTH_INPUT), lFieldLabel)); //$NON-NLS-1$
			lFieldLabel = messages.getMessage("ui.member.editor.label.street"); //$NON-NLS-1$
			outTable.addRowEmphasized(lFieldLabel, addFieldRequired("street", VIFViewHelper.createTextField(member, MemberHome.KEY_STREET, DFT_WIDTH_INPUT), lFieldLabel)); //$NON-NLS-1$
			lFieldLabel = messages.getMessage("ui.member.editor.label.city"); //$NON-NLS-1$
			outTable.addRowEmphasized(messages.getMessage("ui.member.editor.label.city"), createZipCityFields(member, lFieldLabel)); //$NON-NLS-1$
			outTable.addRow(messages.getMessage("ui.member.editor.label.phone"), VIFViewHelper.createTextField(member, MemberHome.KEY_PHONE, DFT_WIDTH_INPUT)); //$NON-NLS-1$
			outTable.addRow(messages.getMessage("ui.member.editor.label.fax"), VIFViewHelper.createTextField(member, MemberHome.KEY_FAX, DFT_WIDTH_INPUT)); //$NON-NLS-1$
			lFieldLabel = messages.getMessage("ui.member.editor.label.mail"); //$NON-NLS-1$
			outTable.addRowEmphasized(lFieldLabel, addFieldRequired("mail", VIFViewHelper.createTextField(member, MemberHome.KEY_MAIL, DFT_WIDTH_INPUT), lFieldLabel)); //$NON-NLS-1$
			return outTable;
		}

		private Component createZipCityFields(Member inMember, String inFieldLabel) {
			HorizontalLayout outLayout = new HorizontalLayout();
			outLayout.addComponent(VIFViewHelper.createTextField(inMember, MemberHome.KEY_ZIP, 50));
			outLayout.addComponent(addFieldRequired("city", VIFViewHelper.createTextField(inMember, MemberHome.KEY_CITY, 249), inFieldLabel)); //$NON-NLS-1$
			return outLayout;
		}
		
		private PasswordField createPasswordField(GeneralDomainObject inModel, String inKey, int inWidth) {
			PasswordField out = new PasswordField();
			if (inModel != null && inKey != null) {
				out = new PasswordField(new BOProperty<String>((DomainObject) inModel, inKey, String.class));
			}
			out.setWidth(inWidth, Sizeable.UNITS_PIXELS);
			out.setStyleName("vif-input"); //$NON-NLS-1$
			return out;
		}
		
		boolean checkPassword(Window inWindow) {
			return new PasswordInputChecker(pass1, pass2, inWindow).checkInput();
		}
		
		Select getAddress() {
			return address;
		}
		
		void focusInit() {
			firstfield.focus();
		}
	}

}

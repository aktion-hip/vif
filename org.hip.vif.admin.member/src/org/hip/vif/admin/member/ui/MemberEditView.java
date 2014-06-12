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

package org.hip.vif.admin.member.ui;

import org.hip.kernel.exc.VException;
import org.hip.vif.admin.member.Activator;
import org.hip.vif.admin.member.data.RoleContainer;
import org.hip.vif.admin.member.tasks.AbstractMemberTask;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.core.util.RatingsHelper;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * View to display the member's edit form.
 * 
 * @author Luthiger Created: 19.10.2011
 */
@SuppressWarnings("serial")
public class MemberEditView extends AbstractMemberView {
	private static final int DFT_WIDTH_INPUT = 300;

	/**
	 * Constructor for the view to edit a member's data.
	 * 
	 * @param inMember
	 *            {@link Member}
	 * @param inRoles
	 *            {@link RoleContainer}
	 * @param inRatings
	 *            {@link RatingsHelper}
	 * @param inTask
	 *            {@link AbstractMemberTask}
	 */
	public MemberEditView(final Member inMember, final RoleContainer inRoles,
			RatingsHelper inRatings, final AbstractMemberTask inTask) {
		FormCreator lForm = new FormCreator(inMember, inRoles);
		VerticalLayout lLayout = createEditLayout(inMember, inRoles, inTask,
				lForm, "ui.member.edit.title.page"); //$NON-NLS-1$

		Button lSave = createButtonSave(inMember, inRoles, inTask, lForm);
		Button lResetPW = createButtonResetPW(inTask);
		lLayout.addComponent(VIFViewHelper.createButtons(lSave, lResetPW));
		lLayout.addComponent(VIFViewHelper.createSpacer());
		lLayout.addComponent(VIFViewHelper.createSpacer());
		lLayout.addComponent(new RatingsTable(inRatings));
	}

	/**
	 * Constructor for the view to create a new member record.
	 * 
	 * @param inMember
	 *            {@link Member}
	 * @param inRoles
	 *            {@link RoleContainer}
	 * @param inTask
	 *            {@link AbstractMemberTask}
	 */
	public MemberEditView(final Member inMember, final RoleContainer inRoles,
			final AbstractMemberTask inTask) {
		FormCreator lForm = new FormNewCreator(inMember, inRoles);
		VerticalLayout lLayout = createEditLayout(inMember, inRoles, inTask,
				lForm, "ui.member.new.title.page"); //$NON-NLS-1$
		lLayout.addComponent(createButtonSave(inMember, inRoles, inTask, lForm));
	}

	private VerticalLayout createEditLayout(final Member inMember,
			final RoleContainer inRoles, final AbstractMemberTask inTask,
			FormCreator inForm, String inTitleKey) {
		final IMessages lMessages = Activator.getMessages();
		VerticalLayout lLayout = initLayout(lMessages, inTitleKey);
		Form lForm = inForm.createForm();
		lLayout.addComponent(lForm);
		lLayout.addComponent(VIFViewHelper.createSpacer());
		return lLayout;
	}

	private Button createButtonSave(final Member inMember,
			final RoleContainer inRoles, final AbstractMemberTask inTask,
			final FormCreator inForm) {
		final IMessages lMessages = Activator.getMessages();
		Button outSave = createSaveButton(lMessages);
		outSave.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				try {
					inForm.commit();
					actualizeAddress(inMember, inForm.getAddress());
					if (!inTask.saveMember(inMember, inRoles)) {
						getWindow()
								.showNotification(
										lMessages
												.getMessage("errmsg.save.general"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$	
					}
				}
				catch (InvalidValueException exc) {
					// intentionally left empty
				}
				catch (ExternIDNotUniqueException exc) {
					inForm.focusInit();
					getWindow()
							.showNotification(
									lMessages
											.getMessage("errmsg.member.not.unique"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
				}
			}
		});
		return outSave;
	}

	private Button createButtonResetPW(final AbstractMemberTask inTask) {
		final IMessages lMessages = Activator.getMessages();
		Button outResetPW = new Button(
				lMessages.getMessage("ui.member.button.reset.pw")); //$NON-NLS-1$
		outResetPW.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				if (!inTask.resetPW()) {

				}
			}
		});
		return outResetPW;
	}

	private void actualizeAddress(Member inMember, Select inAddress) {
		try {
			inMember.set(MemberHome.KEY_SEX, new Long(inAddress.getValue()
					.toString()));
		}
		catch (VException exc) {
			// intentionally left empty
		}
	}

	// --- inner classes ---

	private class FormCreator extends AbstractFormCreator {
		protected Member member;
		private RoleContainer roles;
		protected IMessages messages;
		protected TextField firstfield;
		protected LabelValueTable table;
		private Select address;

		FormCreator(Member inMember, RoleContainer inRoles) {
			member = inMember;
			roles = inRoles;
			messages = Activator.getMessages();
			table = new LabelValueTable();
			address = MemberViewHelper.getMemberAddress(member);
		}

		@Override
		protected Component createTable() {
			Label lUserID = new Label(BeanWrapperHelper.getString(
					MemberHome.KEY_USER_ID, member), Label.CONTENT_XHTML);
			lUserID.setStyleName("vif-value vif-value-emphasized"); //$NON-NLS-1$
			table.addRow(
					messages.getMessage("ui.member.editor.label.userid"), lUserID); //$NON-NLS-1$

			firstfield = RiplaViewHelper.createTextField(member,
					MemberHome.KEY_FIRSTNAME, DFT_WIDTH_INPUT);
			focusInit();

			table.addRow(
					messages.getMessage("ui.member.editor.label.address"), getAddress()); //$NON-NLS-1$

			String lFieldLabel = messages
					.getMessage("ui.member.editor.label.firstname"); //$NON-NLS-1$
			table.addRowEmphasized(lFieldLabel,
					addFieldRequired("firstname", firstfield, lFieldLabel)); //$NON-NLS-1$

			return fillTable(table);
		}

		protected LabelValueTable fillTable(LabelValueTable inTable) {
			String lFieldLabel = messages
					.getMessage("ui.member.editor.label.name"); //$NON-NLS-1$
			inTable.addRowEmphasized(
					lFieldLabel,
					addFieldRequired(
							"name", RiplaViewHelper.createTextField(member, MemberHome.KEY_NAME, DFT_WIDTH_INPUT), lFieldLabel)); //$NON-NLS-1$
			lFieldLabel = messages.getMessage("ui.member.editor.label.street"); //$NON-NLS-1$
			inTable.addRowEmphasized(
					lFieldLabel,
					addFieldRequired(
							"street", RiplaViewHelper.createTextField(member, MemberHome.KEY_STREET, DFT_WIDTH_INPUT), lFieldLabel)); //$NON-NLS-1$
			lFieldLabel = messages.getMessage("ui.member.editor.label.city"); //$NON-NLS-1$
			inTable.addRowEmphasized(
					messages.getMessage("ui.member.editor.label.city"), createZipCityFields(member, lFieldLabel)); //$NON-NLS-1$
			inTable.addRow(
					messages.getMessage("ui.member.editor.label.phone"), RiplaViewHelper.createTextField(member, MemberHome.KEY_PHONE, DFT_WIDTH_INPUT)); //$NON-NLS-1$
			inTable.addRow(
					messages.getMessage("ui.member.editor.label.fax"), RiplaViewHelper.createTextField(member, MemberHome.KEY_FAX, DFT_WIDTH_INPUT)); //$NON-NLS-1$
			lFieldLabel = messages.getMessage("ui.member.editor.label.mail"); //$NON-NLS-1$
			inTable.addRowEmphasized(
					lFieldLabel,
					addFieldRequired(
							"mail", RiplaViewHelper.createTextField(member, MemberHome.KEY_MAIL, DFT_WIDTH_INPUT), lFieldLabel)); //$NON-NLS-1$
			OptionGroup lRoles = createRolesOptions(roles);
			lRoles.setRequiredError(messages
					.getMessage("errmsg.member.role.empty")); //$NON-NLS-1$
			inTable.addRowEmphasized(
					messages.getMessage("ui.member.editor.label.role"), addFieldRequired("roles", lRoles)); //$NON-NLS-1$ //$NON-NLS-2$
			return inTable;
		}

		private Component createZipCityFields(Member inMember,
				String inFieldLabel) {
			HorizontalLayout outLayout = new HorizontalLayout();
			outLayout.addComponent(VIFViewHelper.RiplaViewHelper(inMember,
					MemberHome.KEY_ZIP, 50));
			outLayout
					.addComponent(addFieldRequired(
							"city", RiplaViewHelper.createTextField(inMember, MemberHome.KEY_CITY, 239), inFieldLabel)); //$NON-NLS-1$
			return outLayout;
		}

		Select getAddress() {
			return address;
		}

		void focusInit() {
			firstfield.focus();
		}
	}

	private class FormNewCreator extends FormCreator {
		FormNewCreator(Member inMember, RoleContainer inRoles) {
			super(inMember, inRoles);
		}

		@Override
		protected Component createTable() {
			firstfield = RiplaViewHelper.createTextField(member,
					MemberHome.KEY_USER_ID, DFT_WIDTH_INPUT);
			focusInit();

			String lFieldLabel = messages
					.getMessage("ui.member.editor.label.userid"); //$NON-NLS-1$
			table.addRowEmphasized(lFieldLabel,
					addFieldRequired("firstname", firstfield, lFieldLabel)); //$NON-NLS-1$
			table.addRow(
					messages.getMessage("ui.member.editor.label.address"), getAddress()); //$NON-NLS-1$
			lFieldLabel = messages
					.getMessage("ui.member.editor.label.firstname"); //$NON-NLS-1$
			table.addRowEmphasized(
					lFieldLabel,
					addFieldRequired(
							"firstname", RiplaViewHelper.createTextField(member, MemberHome.KEY_FIRSTNAME, DFT_WIDTH_INPUT), lFieldLabel)); //$NON-NLS-1$

			return fillTable(table);
		}

	}

}

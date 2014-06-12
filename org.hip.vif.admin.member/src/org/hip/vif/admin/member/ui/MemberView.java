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

import org.hip.vif.admin.member.Activator;
import org.hip.vif.admin.member.data.RoleContainer;
import org.hip.vif.admin.member.data.RoleWrapper;
import org.hip.vif.admin.member.tasks.MemberShowTask;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.core.util.RatingsHelper;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;

/**
 * View to display the member's data.
 * 
 * @author Luthiger Created: 26.10.2011
 */
@SuppressWarnings("serial")
public class MemberView extends AbstractMemberView {

	/**
	 * Constructor for the view to display the member's data, e.g. in a lookup
	 * window.
	 * 
	 * @param inMember
	 *            {@link Member}
	 * @param inRoles
	 *            {@link RoleContainer}
	 * @param inRatings
	 *            {@link RatingsHelper}
	 */
	public MemberView(final Member inMember, final RoleContainer inRoles,
			RatingsHelper inRatings) {
		final IMessages lMessages = Activator.getMessages();
		VerticalLayout lLayout = initLayout(lMessages);

		LabelValueTable lTable = displayMember(inMember, lMessages);
		lTable.addRow(
				lMessages.getMessage("ui.member.editor.label.role"), displayRoles(inRoles)); //$NON-NLS-1$
		lLayout.addComponent(lTable);

		lLayout.addComponent(VIFViewHelper.createSpacer());
		lLayout.addComponent(VIFViewHelper.createSpacer());
		lLayout.addComponent(new RatingsTable(inRatings));
	}

	private Label displayRoles(RoleContainer inRoles) {
		StringBuilder lRoles = new StringBuilder();
		boolean lFirst = true;
		for (RoleWrapper lRole : inRoles.getSelected()) {
			if (!lFirst) {
				lRoles.append("<br />"); //$NON-NLS-1$
			}
			lFirst = false;
			lRoles.append(lRole.getLabel());
		}
		return new Label(String.format(LabelValueTable.STYLE_PLAIN, new String(
				lRoles)), Label.CONTENT_XHTML);
	}

	/**
	 * Constructor for the view to edit the member's role.
	 * 
	 * @param inMember
	 *            {@link Member}
	 * @param inRoles
	 *            {@link RoleContainer}
	 * @param inRatings
	 *            {@link RatingsHelper}
	 * @param inTask
	 *            {@link MemberShowTask}
	 */
	public MemberView(final Member inMember, final RoleContainer inRoles,
			RatingsHelper inRatings, final MemberShowTask inTask) {
		final IMessages lMessages = Activator.getMessages();
		VerticalLayout lLayout = initLayout(lMessages,
				"ui.member.edit.title.page"); //$NON-NLS-1$

		Button lSave = createSaveButton(lMessages);
		VerticalLayout lMemberLayout = new VerticalLayout();
		lMemberLayout.setStyleName("vif-view-member"); //$NON-NLS-1$
		lLayout.addComponent(lMemberLayout);

		LabelValueTable lTable = displayMember(inMember, lMessages);

		lMemberLayout.addComponent(lTable);
		OptionGroup lRoles = createRolesOptions(inRoles);
		lTable.addRowEmphasized(
				lMessages.getMessage("ui.member.editor.label.role"), lRoles); //$NON-NLS-1$

		lLayout.addComponent(VIFViewHelper.createSpacer());
		lLayout.addComponent(lSave);
		lSave.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				if (!inRoles.hasChecked()) {
					getWindow()
							.showNotification(
									lMessages
											.getMessage("errmsg.member.role.empty"), Notification.TYPE_ERROR_MESSAGE); //$NON-NLS-1$
				} else {
					if (!inTask.saveRoles(inMember, inRoles)) {
						getWindow()
								.showNotification(
										lMessages
												.getMessage("errmsg.save.general"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$						
					}
				}
			}
		});

		lLayout.addComponent(VIFViewHelper.createSpacer());
		lLayout.addComponent(VIFViewHelper.createSpacer());
		lLayout.addComponent(new RatingsTable(inRatings));
	}

	/**
	 * @param inMember
	 * @param inMessages
	 * @return
	 */
	private LabelValueTable displayMember(final Member inMember,
			final IMessages inMessages) {
		LabelValueTable lTable = new LabelValueTable();
		lTable.addRow(
				inMessages.getMessage("ui.member.editor.label.address"), MemberViewHelper.getMemberAddressLabel(inMember)); //$NON-NLS-1$		
		lTable.addRow(
				inMessages.getMessage("ui.member.editor.label.firstname"), BeanWrapperHelper.getString(MemberHome.KEY_FIRSTNAME, inMember)); //$NON-NLS-1$
		lTable.addRow(
				inMessages.getMessage("ui.member.editor.label.name"), BeanWrapperHelper.getString(MemberHome.KEY_NAME, inMember)); //$NON-NLS-1$
		lTable.addRow(
				inMessages.getMessage("ui.member.editor.label.street"), BeanWrapperHelper.getString(MemberHome.KEY_STREET, inMember)); //$NON-NLS-1$
		lTable.addRow(
				inMessages.getMessage("ui.member.editor.label.city"), createZipCityLabels(inMember)); //$NON-NLS-1$
		lTable.addRow(
				inMessages.getMessage("ui.member.editor.label.phone"), BeanWrapperHelper.getString(MemberHome.KEY_PHONE, inMember)); //$NON-NLS-1$
		lTable.addRow(
				inMessages.getMessage("ui.member.editor.label.fax"), BeanWrapperHelper.getString(MemberHome.KEY_FAX, inMember)); //$NON-NLS-1$
		lTable.addRow(
				inMessages.getMessage("ui.member.editor.label.mail"), BeanWrapperHelper.getString(MemberHome.KEY_MAIL, inMember)); //$NON-NLS-1$
		return lTable;
	}

	private Component createZipCityLabels(Member inMember) {
		HorizontalLayout outLayout = new HorizontalLayout();
		outLayout.setStyleName("vif-value"); //$NON-NLS-1$
		outLayout.addComponent(RiplaViewHelper.makeUndefinedWidth(new Label(
				BeanWrapperHelper.getString(MemberHome.KEY_ZIP, inMember))));
		outLayout.addComponent(RiplaViewHelper.makeUndefinedWidth(new Label(
				"&#160;", Label.CONTENT_XHTML))); //$NON-NLS-1$
		outLayout.addComponent(RiplaViewHelper.makeUndefinedWidth(new Label(
				BeanWrapperHelper.getString(MemberHome.KEY_CITY, inMember))));
		return outLayout;
	}

}

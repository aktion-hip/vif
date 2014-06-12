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

package org.hip.vif.forum.member.ui;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.core.util.MandatoryFieldChecker;
import org.hip.vif.core.util.RatingsHelper;
import org.hip.vif.forum.member.Activator;
import org.hip.vif.forum.member.tasks.PersonalDataEditTask;
import org.hip.vif.web.components.LabelValueTable;
import org.hip.vif.web.util.MemberViewHelper;
import org.hip.vif.web.util.RatingsTable;
import org.hip.vif.web.util.VIFViewHelper;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

/**
 * View to edit the actor's member data and show the actor's ratings.
 * 
 * @author Luthiger
 * Created: 06.10.2011
 */
@SuppressWarnings("serial")
public class EditPersonalDataView extends CustomComponent {
	private static final int DFT_WIDTH_INPUT = 300;
	private Button save;

	/**
	 * Constructor
	 * 
	 * @param inMember {@link Member} the member model to display
	 * @param inRatings {@link RatingsHelper} the helper object containing the ratings to display
	 * @param inTask {@link PersonalDataEditTask} the task controlling this view
	 */
	public EditPersonalDataView(final Member inMember, RatingsHelper inRatings, final PersonalDataEditTask inTask) {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		
		final IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-view"); //$NON-NLS-1$
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-pagetitle",  //$NON-NLS-1$
				lMessages.getFormattedMessage("ui.member.view.title.page", BeanWrapperHelper.getString(MemberHome.KEY_USER_ID, inMember))), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$

		save = new Button(lMessages.getMessage("ui.member.button.save")); //$NON-NLS-1$
		LabelValueTable lTable = new LabelValueTable();
		
		final Select lAddress = MemberViewHelper.getMemberAddress(inMember);
		lTable.addRow(lMessages.getMessage("ui.member.editor.label.address"), lAddress); //$NON-NLS-1$
		
		TextField lField = VIFViewHelper.setRequired(createTextField(inMember, MemberHome.KEY_FIRSTNAME), new MemberRepaintRequestListener());
		lField.focus();
		lTable.addRowEmphasized(lMessages.getMessage("ui.member.editor.label.firstname"), lField); //$NON-NLS-1$
		lTable.addRowEmphasized(lMessages.getMessage("ui.member.editor.label.name"), VIFViewHelper.setRequired(createTextField(inMember, MemberHome.KEY_NAME), new MemberRepaintRequestListener())); //$NON-NLS-1$
		lTable.addRowEmphasized(lMessages.getMessage("ui.member.editor.label.street"), VIFViewHelper.setRequired(createTextField(inMember, MemberHome.KEY_STREET), new MemberRepaintRequestListener())); //$NON-NLS-1$
		lTable.addRowEmphasized(lMessages.getMessage("ui.member.editor.label.city"), createZipCityFields(inMember, lMessages)); //$NON-NLS-1$
		lTable.addRow(lMessages.getMessage("ui.member.editor.label.phone"), createTextField(inMember, MemberHome.KEY_PHONE)); //$NON-NLS-1$
		lTable.addRow(lMessages.getMessage("ui.member.editor.label.fax"), createTextField(inMember, MemberHome.KEY_FAX)); //$NON-NLS-1$
		lTable.addRowEmphasized(lMessages.getMessage("ui.member.editor.label.mail"), VIFViewHelper.setRequired(createTextField(inMember, MemberHome.KEY_MAIL), new MemberRepaintRequestListener())); //$NON-NLS-1$
		lLayout.addComponent(lTable);
		
		lLayout.addComponent(VIFViewHelper.createSpacer());
		lLayout.addComponent(save);
		save.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				if (!inMember.isValid()) {
					MemberFieldChecker lChecker = new MemberFieldChecker(inMember, lMessages);
					getWindow().showNotification(lChecker.render(), Notification.TYPE_ERROR_MESSAGE);
				}
				else {
					actualizeAddress(inMember, lAddress);
					if (inTask.saveMember(inMember)) {
						getWindow().showNotification(lMessages.getMessage("msg.task.data.changed"), Notification.TYPE_TRAY_NOTIFICATION); //$NON-NLS-1$
					}
					else {
						getWindow().showNotification(lMessages.getMessage("errmsg.save.general"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$						
					}
				}
			}
		});
		
		lLayout.addComponent(VIFViewHelper.createSpacer());
		lLayout.addComponent(VIFViewHelper.createSpacer());
		lLayout.addComponent(new RatingsTable(inRatings));
	}
	
	private void actualizeAddress(Member inMember, Select inAddress) {
		try {
			inMember.set(MemberHome.KEY_SEX, new Long(inAddress.getValue().toString()));
		} catch (VException exc) {
			//intentionally left empty
		}
	}

	private Component createZipCityFields(Member inMember, IMessages inMessages) {
		HorizontalLayout outLayout = new HorizontalLayout();
		outLayout.addComponent(VIFViewHelper.createTextField(inMember, MemberHome.KEY_ZIP, 50));
		outLayout.addComponent(VIFViewHelper.setRequired(VIFViewHelper.createTextField(inMember, MemberHome.KEY_CITY, 239), new MemberRepaintRequestListener()));
		return outLayout;
	}

	private TextField createTextField(GeneralDomainObject inMember, String inKey) {
		return VIFViewHelper.createTextField(inMember, inKey, DFT_WIDTH_INPUT);
	}
	
	// --- 
	
	private class MemberRepaintRequestListener implements RepaintRequestListener {
		public void repaintRequested(RepaintRequestEvent inEvent) {
			if (getWindow() == null) return;
			
			TextField lField = (TextField) inEvent.getSource();
			if (lField.isValid()) {
				save.setEnabled(true);
			}
			else {
				save.setEnabled(false);
				getWindow().showNotification(Activator.getMessages().getMessage("errmsg.field.not.empty"), Notification.TYPE_ERROR_MESSAGE); //$NON-NLS-1$
				lField.focus();
			}
		}
	}
	
	private static class MemberFieldChecker extends MandatoryFieldChecker {
		public MemberFieldChecker(Member inMember, IMessages inMessages) {
			super(inMessages);
			checkMandatory(inMember, MemberHome.KEY_NAME, inMessages.getMessage("ui.member.editor.label.name")); //$NON-NLS-1$
			checkMandatory(inMember, MemberHome.KEY_FIRSTNAME, inMessages.getMessage("ui.member.editor.label.firstname")); //$NON-NLS-1$
			checkMandatory(inMember, MemberHome.KEY_STREET, inMessages.getMessage("ui.member.editor.label.street")); //$NON-NLS-1$
			checkMandatory(inMember, MemberHome.KEY_ZIP, inMessages.getMessage("ui.member.editor.label.zip")); //$NON-NLS-1$
			checkMandatory(inMember, MemberHome.KEY_CITY, inMessages.getMessage("ui.member.editor.label.city")); //$NON-NLS-1$
			checkMandatory(inMember, MemberHome.KEY_MAIL, inMessages.getMessage("ui.member.editor.label.mail")); //$NON-NLS-1$
		}
	}
		
}

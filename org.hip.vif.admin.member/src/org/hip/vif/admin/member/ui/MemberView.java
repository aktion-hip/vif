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

import org.hip.vif.admin.member.Activator;
import org.hip.vif.admin.member.data.RoleContainer;
import org.hip.vif.admin.member.data.RoleWrapper;
import org.hip.vif.admin.member.tasks.MemberShowTask;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.util.RatingsHelper;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.MemberViewHelper;
import org.hip.vif.web.util.RatingsTable;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.LabelValueTable;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;

/** View to display the member's data.
 *
 * @author Luthiger Created: 26.10.2011 */
@SuppressWarnings("serial")
public class MemberView extends AbstractMemberView {

    /** Constructor for the view to display the member's data, e.g. in a lookup window.
     *
     * @param inMember {@link Member}
     * @param inRoles {@link RoleContainer}
     * @param inRatings {@link RatingsHelper} */
    public MemberView(final Member inMember, final RoleContainer inRoles,
            final RatingsHelper inRatings) {
        final IMessages lMessages = Activator.getMessages();
        final VerticalLayout lLayout = initLayout(lMessages);

        final LabelValueTable lTable = displayMember(inMember, lMessages);
        lTable.addRow(
                lMessages.getMessage("ui.member.editor.label.role"), displayRoles(inRoles)); //$NON-NLS-1$
        lLayout.addComponent(lTable);

        lLayout.addComponent(RiplaViewHelper.createSpacer());
        lLayout.addComponent(RiplaViewHelper.createSpacer());
        lLayout.addComponent(new RatingsTable(inRatings));
    }

    private Label displayRoles(final RoleContainer inRoles) {
        final StringBuilder lRoles = new StringBuilder();
        boolean lFirst = true;
        for (final RoleWrapper lRole : inRoles.getSelected()) {
            if (!lFirst) {
                lRoles.append("<br />"); //$NON-NLS-1$
            }
            lFirst = false;
            lRoles.append(lRole.getLabel());
        }
        return new Label(String.format(LabelValueTable.STYLE_PLAIN, new String(
                lRoles)), ContentMode.HTML);
    }

    /** Constructor for the view to edit the member's role.
     *
     * @param inMember {@link Member}
     * @param inRoles {@link RoleContainer}
     * @param inRatings {@link RatingsHelper}
     * @param inTask {@link MemberShowTask} */
    public MemberView(final Member inMember, final RoleContainer inRoles,
            final RatingsHelper inRatings, final MemberShowTask inTask) {
        final IMessages lMessages = Activator.getMessages();
        final VerticalLayout lLayout = initLayout(lMessages, "ui.member.edit.title.page"); //$NON-NLS-1$

        final Button lSave = createSaveButton(lMessages);
        final VerticalLayout lMemberLayout = new VerticalLayout();
        lMemberLayout.setStyleName("vif-view-member"); //$NON-NLS-1$
        lLayout.addComponent(lMemberLayout);

        final LabelValueTable lTable = displayMember(inMember, lMessages);

        lMemberLayout.addComponent(lTable);
        final OptionGroup lRoles = createRolesOptions(inRoles);
        lTable.addRowEmphasized(
                lMessages.getMessage("ui.member.editor.label.role"), lRoles); //$NON-NLS-1$

        lLayout.addComponent(RiplaViewHelper.createSpacer());
        lLayout.addComponent(lSave);
        lSave.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                if (!inRoles.hasChecked()) {
                    Notification.show(lMessages
                            .getMessage("errmsg.member.role.empty"), Type.ERROR_MESSAGE); //$NON-NLS-1$
                } else {
                    if (!inTask.saveRoles(inMember, inRoles)) {
                        Notification.show(lMessages
                                .getMessage("errmsg.save.general"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                    }
                }
            }
        });

        lLayout.addComponent(RiplaViewHelper.createSpacer());
        lLayout.addComponent(RiplaViewHelper.createSpacer());
        lLayout.addComponent(new RatingsTable(inRatings));
    }

    /** @param inMember
     * @param inMessages
     * @return */
    private LabelValueTable displayMember(final Member inMember,
            final IMessages inMessages) {
        final LabelValueTable lTable = new LabelValueTable();
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

    private Component createZipCityLabels(final Member inMember) {
        final HorizontalLayout outLayout = new HorizontalLayout();
        outLayout.setStyleName("vif-value"); //$NON-NLS-1$
        outLayout.addComponent(RiplaViewHelper.makeUndefinedWidth(new Label(
                BeanWrapperHelper.getString(MemberHome.KEY_ZIP, inMember))));
        outLayout.addComponent(RiplaViewHelper.makeUndefinedWidth(new Label("&#160;", ContentMode.HTML))); //$NON-NLS-1$
        outLayout.addComponent(RiplaViewHelper.makeUndefinedWidth(new Label(
                BeanWrapperHelper.getString(MemberHome.KEY_CITY, inMember))));
        return outLayout;
    }

}

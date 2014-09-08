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

package org.hip.vif.admin.permissions.ui;

import org.hip.kernel.code.CodeList;
import org.hip.vif.admin.permissions.Activator;
import org.hip.vif.admin.permissions.Constants;
import org.hip.vif.admin.permissions.data.LoadedPermissionBean;
import org.hip.vif.admin.permissions.data.LoadedPermissionBean.Role;
import org.hip.vif.admin.permissions.data.LoadedPermissionContainer;
import org.hip.vif.admin.permissions.tasks.PermissionsEditTask;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.hip.vif.core.interfaces.ISelectableBean;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.AbstractFormCreator;
import org.ripla.web.util.LabelValueTable;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/** View to add and delete permissions and to associate them with roles.
 *
 * @author Luthiger Created: 14.12.2011 */
@SuppressWarnings("serial")
public class PermissionEditView extends CustomComponent {
    private static final int TABLE_SIZE = 13;
    private static final String TMPL_COLUMN_HEADER = "<div class=\"vif-colheader\">%s</div>"; //$NON-NLS-1$
    private static final String TMPL_LABEL = "<div title=\"%s\">%s</div>"; //$NON-NLS-1$
    private static final String SUBTITLE_WARNING = "<div class=\"vif-warning\">%s</div>"; //$NON-NLS-1$

    private boolean confirmationMode = false;

    /** Constructor
     *
     * @param inPermissions {@link LoadedPermissionContainer} the permissions
     * @param inRoles {@link CodeList} the roles
     * @param inTask {@link PermissionsEditTask} */
    public PermissionEditView(final LoadedPermissionContainer inPermissions,
            final CodeList inRoles, final PermissionsEditTask inTask) {
        final VerticalLayout lLayout = new VerticalLayout();
        setCompositionRoot(lLayout);

        final IMessages lMessages = Activator.getMessages();
        lLayout.setStyleName("vif-table"); //$NON-NLS-1$
        lLayout.addComponent(new Label(
                String.format(
                        VIFViewHelper.TMPL_TITLE, "vif-pagetitle", lMessages.getMessage("component.menu.title")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$

        final Label lSubtitle = new Label(
                String.format(SUBTITLE_WARNING,
                        lMessages.getMessage("ui.permission.remark.delete")), ContentMode.HTML); //$NON-NLS-1$
        lLayout.addComponent(lSubtitle);
        lSubtitle.setVisible(false);

        final Table lTable = new Table();
        lTable.setWidth("100%"); //$NON-NLS-1$
        lTable.setStyleName("vif-permission-table"); //$NON-NLS-1$
        lTable.setContainerDataSource(inPermissions);
        // generate column checkbox for delete
        lTable.addGeneratedColumn(LoadedPermissionContainer.FIELD_CHECK,
                new Table.ColumnGenerator() {
            @Override
            public Object generateCell(final Table inSource, final Object inItemId,
                    final Object inColumnId) {
                if (Constants.PERMISSION_EDIT
                        .equals(((LoadedPermissionBean) inItemId)
                                .getLabel())) {
                    return new Label();
                }
                return VIFViewHelper.createCheck(
                        (ISelectableBean) inItemId,
                        new VIFViewHelper.IConfirmationModeChecker() {
                            @Override
                            public boolean inConfirmationMode() {
                                return confirmationMode;
                            }
                        });
            }
        });
        // generate column label
        lTable.addGeneratedColumn(LoadedPermissionContainer.FIELD_LABEL,
                new Table.ColumnGenerator() {
            @Override
            public Object generateCell(final Table inSource, final Object inItemId,
                    final Object inColumnId) {
                final LoadedPermissionBean lPermission = (LoadedPermissionBean) inItemId;
                return new Label(String.format(TMPL_LABEL,
                        lPermission.getDescription(),
                        lPermission.getLabel()), ContentMode.HTML);
            }
        });
        // generate column checkbox for roles
        lTable.addGeneratedColumn(LoadedPermissionContainer.FIELD_ROLE_SU,
                new ColumnCheckBoxGenerator(Role.SU));
        lTable.addGeneratedColumn(LoadedPermissionContainer.FIELD_ROLE_ADMIN,
                new ColumnCheckBoxGenerator(Role.ADMIN));
        lTable.addGeneratedColumn(
                LoadedPermissionContainer.FIELD_ROLE_GROUPADMIN,
                new ColumnCheckBoxGenerator(Role.GROUP_ADMIN));
        lTable.addGeneratedColumn(
                LoadedPermissionContainer.FIELD_ROLE_PARTICIPANT,
                new ColumnCheckBoxGenerator(Role.PARTICIPANT));
        lTable.addGeneratedColumn(LoadedPermissionContainer.FIELD_ROLE_MEMBER,
                new ColumnCheckBoxGenerator(Role.MEMBER));
        lTable.addGeneratedColumn(LoadedPermissionContainer.FIELD_ROLE_GUEST,
                new ColumnCheckBoxGenerator(Role.GUEST));
        lTable.addGeneratedColumn(
                LoadedPermissionContainer.FIELD_ROLE_EXCLUDED,
                new ColumnCheckBoxGenerator(Role.EXCLUDED));

        lTable.setPageLength(inPermissions.size() > TABLE_SIZE ? TABLE_SIZE : 0);
        lTable.setColumnCollapsingAllowed(true);
        lTable.setColumnReorderingAllowed(true);
        lTable.setSelectable(true);
        lTable.setImmediate(true);

        lTable.setVisibleColumns(LoadedPermissionContainer.NATURAL_COL_ORDER);
        lTable.setColumnHeaders(getHeaders(inRoles, lMessages));
        lTable.setColumnExpandRatio(LoadedPermissionContainer.FIELD_LABEL, 1);
        lLayout.addComponent(lTable);

        final Button lDelete = new Button(
                lMessages.getMessage("ui.permission.button.delete")); //$NON-NLS-1$
        final Button lSave = new Button(
                lMessages.getMessage("ui.permission.button.save")); //$NON-NLS-1$
        lSave.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                if (!inTask.saveChanges()) {
                    Notification.show(
                            lMessages
                            .getMessage("errmsg.permissions.save"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                }
            }
        });
        lLayout.addComponent(RiplaViewHelper.createButtons(lDelete, lSave));

        lLayout.addComponent(RiplaViewHelper.createSpacer());
        final Component lInput = createInput(inTask, lMessages);
        lLayout.addComponent(lInput);

        // add button click listeners
        lDelete.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                if (confirmationMode) {
                    if (!inTask.deletePermissions()) {
                        Notification.show(
                                lMessages
                                        .getMessage("errmsg.permissions.delete"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                    }
                } else {
                    if (VIFViewHelper.processAction(inPermissions)) {
                        confirmationMode = true;
                        inPermissions.addContainerFilter(new SelectedFilter());
                        lSubtitle.setVisible(true);
                        lTable.setSelectable(false);
                        lTable.setPageLength(0);
                        lInput.setVisible(false);
                        lSave.setVisible(false);
                    }
                }
            }
        });
    }

    private Component createInput(final PermissionsEditTask inTask,
            final IMessages inMessages) {
        final VerticalLayout out = new VerticalLayout();
        out.setStyleName("vif-view"); //$NON-NLS-1$
        out.addComponent(new Label(
                String.format(
                        VIFViewHelper.TMPL_TITLE, "vif-title", inMessages.getMessage("ui.permission.subtitle.create")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$

        final FormCreator lForm = new FormCreator();
        out.addComponent(lForm.createForm());

        final Button lNew = new Button(
                inMessages.getMessage("ui.permission.button.create")); //$NON-NLS-1$
        lNew.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                try {
                    lForm.commit();
                    if (!inTask.createPermission(lForm.getLabel(),
                            lForm.getDescription())) {
                        Notification.show(
                                inMessages
                                        .getMessage("errmsg.permissions.create"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                    }
                }
                catch (final ExternIDNotUniqueException exc) {
                    Notification.show(
                            inMessages
                            .getMessage("errmsg.permissions.not.unique"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                } catch (final CommitException exc) {
                    // intentionally left empty
                }
            }
        });

        out.addComponent(lNew);
        return out;
    }

    private String[] getHeaders(final CodeList inRoles, final IMessages inMessages) {
        final String[] out = new String[9];
        out[0] = ""; //$NON-NLS-1$
        out[1] = String.format(TMPL_COLUMN_HEADER,
                inMessages.getMessage("ui.permission.header.permission")); //$NON-NLS-1$
        for (int i = 2; i < 9; i++) {
            out[i] = String.format(
                    TMPL_COLUMN_HEADER,
                    inRoles.getLabel(String.valueOf(i - 1)).replace(" ", "<br/>")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return out;
    }

    // --- private classes ---

    private static class ColumnCheckBoxGenerator implements
            Table.ColumnGenerator {
        private final Role role;

        ColumnCheckBoxGenerator(final Role inRole) {
            role = inRole;
        }

        @Override
        public Object generateCell(final Table inSource, final Object inItemId,
                final Object inColumnId) {
            final LoadedPermissionBean lPermission = (LoadedPermissionBean) inItemId;
            final CheckBox out = new CheckBox();
            out.setImmediate(true);
            out.setValue(lPermission.getRoleValue(role));
            out.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(final ValueChangeEvent inEvent) {
                    ((LoadedPermissionBean) inItemId).setRoleValue(role,
                            ((CheckBox) inEvent.getProperty())
                            .getValue());
                }
            });
            out.setEnabled(!Constants.PERMISSION_EDIT.equals(lPermission
                    .getLabel()));
            return out;
        }
    }

    private static class SelectedFilter implements Filter {
        @SuppressWarnings("rawtypes")
        @Override
        public boolean passesFilter(final Object inItemId, final Item inItem)
                throws UnsupportedOperationException {
            final Property lCheckBox = inItem
                    .getItemProperty(LoadedPermissionContainer.PERMISSION_CHECKED);
            return (Boolean) lCheckBox.getValue();
        }

        @Override
        public boolean appliesToProperty(final Object inPropertyId) {
            return LoadedPermissionContainer.PERMISSION_CHECKED
                    .equals(inPropertyId);
        }

    }

    private class FormCreator extends AbstractFormCreator {

        public FormCreator() {
            // TODO: Item
            super(null);
        }

        private final LabelValueTable table = new LabelValueTable();
        private final TextField label = RiplaViewHelper
                .createTextField("", 180, null); //$NON-NLS-1$
        private final TextField description = RiplaViewHelper.createTextField("", 500, null); //$NON-NLS-1$

        @Override
        protected Component createTable() {
            final IMessages lMessages = Activator.getMessages();
            table.addRow(
                    lMessages.getMessage("ui.permission.label.label"), addFieldRequired("label", label, lMessages.getMessage("ui.permission.label.label"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            table.addRow(
                    lMessages.getMessage("ui.permission.label.description"), addFieldRequired("description", description, lMessages.getMessage("ui.permission.label.description"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            return table;
        }

        String getLabel() {
            return label.getValue().toString();
        }

        String getDescription() {
            return description.getValue().toString();
        }

    }

}

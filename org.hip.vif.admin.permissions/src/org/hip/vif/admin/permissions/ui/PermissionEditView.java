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

package org.hip.vif.admin.permissions.ui;

import org.hip.kernel.code.CodeList;
import org.hip.vif.admin.permissions.Activator;
import org.hip.vif.admin.permissions.Constants;
import org.hip.vif.admin.permissions.data.LoadedPermissionBean;
import org.hip.vif.admin.permissions.data.LoadedPermissionBean.Role;
import org.hip.vif.admin.permissions.data.LoadedPermissionContainer;
import org.hip.vif.admin.permissions.tasks.PermissionsEditTask;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * View to add and delete permissions and to associate them with roles.
 * 
 * @author Luthiger Created: 14.12.2011
 */
@SuppressWarnings("serial")
public class PermissionEditView extends CustomComponent {
	private static final int TABLE_SIZE = 13;
	private static final String TMPL_COLUMN_HEADER = "<div class=\"vif-colheader\">%s</div>"; //$NON-NLS-1$
	private static final String TMPL_LABEL = "<div title=\"%s\">%s</div>"; //$NON-NLS-1$
	private static final String SUBTITLE_WARNING = "<div class=\"vif-warning\">%s</div>"; //$NON-NLS-1$

	private boolean confirmationMode = false;

	/**
	 * Constructor
	 * 
	 * @param inPermissions
	 *            {@link LoadedPermissionContainer} the permissions
	 * @param inRoles
	 *            {@link CodeList} the roles
	 * @param inTask
	 *            {@link PermissionsEditTask}
	 */
	public PermissionEditView(final LoadedPermissionContainer inPermissions,
			CodeList inRoles, final PermissionsEditTask inTask) {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);

		final IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-table"); //$NON-NLS-1$
		lLayout.addComponent(new Label(
				String.format(
						VIFViewHelper.TMPL_TITLE,
						"vif-pagetitle", lMessages.getMessage("component.menu.title")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$

		final Label lSubtitle = new Label(
				String.format(SUBTITLE_WARNING,
						lMessages.getMessage("ui.permission.remark.delete")), Label.CONTENT_XHTML); //$NON-NLS-1$
		lLayout.addComponent(lSubtitle);
		lSubtitle.setVisible(false);

		final Table lTable = new Table();
		lTable.setWidth("100%"); //$NON-NLS-1$
		lTable.setStyleName("vif-permission-table"); //$NON-NLS-1$
		lTable.setContainerDataSource(inPermissions);
		// generate column checkbox for delete
		lTable.addGeneratedColumn(LoadedPermissionContainer.FIELD_CHECK,
				new Table.ColumnGenerator() {
					public Object generateCell(Table inSource, Object inItemId,
							Object inColumnId) {
						if (Constants.PERMISSION_EDIT
								.equals(((LoadedPermissionBean) inItemId)
										.getLabel())) {
							return new Label();
						}
						return VIFViewHelper.createCheck(
								(ISelectableBean) inItemId,
								new VIFViewHelper.IConfirmationModeChecker() {
									public boolean inConfirmationMode() {
										return confirmationMode;
									}
								});
					}
				});
		// generate column label
		lTable.addGeneratedColumn(LoadedPermissionContainer.FIELD_LABEL,
				new Table.ColumnGenerator() {
					public Object generateCell(Table inSource, Object inItemId,
							Object inColumnId) {
						LoadedPermissionBean lPermission = (LoadedPermissionBean) inItemId;
						return new Label(String.format(TMPL_LABEL,
								lPermission.getDescription(),
								lPermission.getLabel()), Label.CONTENT_XHTML);
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

		Button lDelete = new Button(
				lMessages.getMessage("ui.permission.button.delete")); //$NON-NLS-1$
		final Button lSave = new Button(
				lMessages.getMessage("ui.permission.button.save")); //$NON-NLS-1$
		lSave.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				if (!inTask.saveChanges()) {
					getWindow()
							.showNotification(
									lMessages
											.getMessage("errmsg.permissions.save"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
				}
			}
		});
		lLayout.addComponent(VIFViewHelper.createButtons(lDelete, lSave));

		lLayout.addComponent(VIFViewHelper.createSpacer());
		final Component lInput = createInput(inTask, lMessages);
		lLayout.addComponent(lInput);

		// add button click listeners
		lDelete.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				if (confirmationMode) {
					if (!inTask.deletePermissions()) {
						getWindow()
								.showNotification(
										lMessages
												.getMessage("errmsg.permissions.delete"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
					}
				} else {
					if (VIFViewHelper.processAction(inPermissions, getWindow())) {
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
		VerticalLayout out = new VerticalLayout();
		out.setStyleName("vif-view"); //$NON-NLS-1$
		out.addComponent(new Label(
				String.format(
						VIFViewHelper.TMPL_TITLE,
						"vif-title", inMessages.getMessage("ui.permission.subtitle.create")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$

		final FormCreator lForm = new FormCreator();
		out.addComponent(lForm.createForm());

		Button lNew = new Button(
				inMessages.getMessage("ui.permission.button.create")); //$NON-NLS-1$
		lNew.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				try {
					lForm.commit();
					if (!inTask.createPermission(lForm.getLabel(),
							lForm.getDescription())) {
						getWindow()
								.showNotification(
										inMessages
												.getMessage("errmsg.permissions.create"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
					}
				}
				catch (InvalidValueException exc) {
					// intentionally left empty
				}
				catch (ExternIDNotUniqueException exc) {
					getWindow()
							.showNotification(
									inMessages
											.getMessage("errmsg.permissions.not.unique"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
				}
			}
		});

		out.addComponent(lNew);
		return out;
	}

	private String[] getHeaders(CodeList inRoles, IMessages inMessages) {
		String[] out = new String[9];
		out[0] = ""; //$NON-NLS-1$
		out[1] = String.format(TMPL_COLUMN_HEADER,
				inMessages.getMessage("ui.permission.header.permission")); //$NON-NLS-1$
		for (int i = 2; i < 9; i++) {
			out[i] = String.format(
					TMPL_COLUMN_HEADER,
					inRoles.getLabel(String.valueOf(i - 1)).replace(
							" ", "<br/>")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return out;
	}

	// --- private classes ---

	private static class ColumnCheckBoxGenerator implements
			Table.ColumnGenerator {
		private Role role;

		ColumnCheckBoxGenerator(Role inRole) {
			role = inRole;
		}

		public Object generateCell(Table inSource, final Object inItemId,
				Object inColumnId) {
			LoadedPermissionBean lPermission = (LoadedPermissionBean) inItemId;
			CheckBox out = new CheckBox();
			out.setImmediate(true);
			out.setValue(lPermission.getRoleValue(role));
			out.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent inEvent) {
					((LoadedPermissionBean) inItemId).setRoleValue(role,
							(Boolean) ((CheckBox) inEvent.getProperty())
									.getValue());
				}
			});
			out.setEnabled(!Constants.PERMISSION_EDIT.equals(lPermission
					.getLabel()));
			return out;
		}
	}

	private static class SelectedFilter implements Filter {
		public boolean passesFilter(Object inItemId, Item inItem)
				throws UnsupportedOperationException {
			Property lCheckBox = inItem
					.getItemProperty(LoadedPermissionContainer.PERMISSION_CHECKED);
			return (Boolean) lCheckBox.getValue();
		}

		public boolean appliesToProperty(Object inPropertyId) {
			return LoadedPermissionContainer.PERMISSION_CHECKED
					.equals(inPropertyId);
		}

	}

	private class FormCreator extends AbstractFormCreator {
		private LabelValueTable table = new LabelValueTable();
		private TextField label = RiplaViewHelper
				.createTextField("", 180, null); //$NON-NLS-1$
		private TextField description = RiplaViewHelper.createTextField(
				"", 500, null); //$NON-NLS-1$

		@Override
		protected Component createTable() {
			IMessages lMessages = Activator.getMessages();
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

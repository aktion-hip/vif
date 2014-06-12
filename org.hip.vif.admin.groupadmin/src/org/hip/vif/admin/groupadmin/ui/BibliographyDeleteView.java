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

package org.hip.vif.admin.groupadmin.ui;

import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.data.ContributionContainer;
import org.hip.vif.admin.groupadmin.data.ContributionWrapper;
import org.hip.vif.admin.groupadmin.tasks.BibliographyDeleteTask;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * View to display the different tables that show text entries the administrator
 * wants to delete.
 * 
 * @author Luthiger Created: 09.12.2011
 */
@SuppressWarnings("serial")
public class BibliographyDeleteView extends AbstractContributionView {
	private static final String SUBTITLE_WARNING = "<div class=\"vif-warning\">%s</div>"; //$NON-NLS-1$
	private static final String[] COL_HEADERS = new String[] {
			"", "container.table.headers.id", "container.table.headers.text" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private boolean confirmationMode;

	/**
	 * Constructor.
	 * 
	 * @param inDeletable
	 *            {@link ContributionContainer} the deletable entries
	 * @param inReferenced
	 *            {@link ContributionContainer} the referenced entries with
	 *            removalbe references
	 * @param inUndeletable
	 *            {@link ContributionContainer} the referenced entries with
	 *            unremovable references, thus, not deletable by the
	 *            administrator
	 * @param inTask
	 *            {@link BibliographyDeleteTask}
	 */
	public BibliographyDeleteView(final ContributionContainer inDeletable,
			final ContributionContainer inReferenced,
			final ContributionContainer inUndeletable,
			final BibliographyDeleteTask inTask) {
		confirmationMode = false;
		final VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		lLayout.setSpacing(true);

		final IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-view"); //$NON-NLS-1$
		lLayout.addComponent(new Label(
				String.format(
						VIFViewHelper.TMPL_TITLE,
						"vif-pagetitle", lMessages.getMessage("ui.bibliography.link.button.delete")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$

		if (inDeletable.hasItems()) {
			final Label lRemarkDeletable = new Label(
					lMessages
							.getMessage("ui.bibliography.link.remark.deletable"), ContentMode.HTML); //$NON-NLS-1$
			lLayout.addComponent(lRemarkDeletable);
			final Table lTable = createTable(inDeletable, inTask, true);
			lLayout.addComponent(lTable);
			final Button lDelete = new Button(
					lMessages
							.getMessage("ui.discussion.contribution.button.delete")); //$NON-NLS-1$
			lDelete.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(final ClickEvent inEvent) {
					if (confirmationMode) {
						if (!inTask.deleteTexts(inDeletable)) {
							Notification.show(
									lMessages
											.getMessage("errmsg.biblio.delete"), Type.WARNING_MESSAGE); //$NON-NLS-1$
						}
					} else {
						if (VIFViewHelper.processAction(inDeletable)) {
							confirmationMode = true;
							inDeletable
									.addContainerFilter(new SelectedFilter());
							lRemarkDeletable
									.setPropertyDataSource(new ObjectProperty<String>(
											String.format(
													SUBTITLE_WARNING,
													lMessages
															.getMessage("ui.contributions.process.warning")), String.class)); //$NON-NLS-1$
							lTable.setSelectable(false);
						}
					}
				}
			});
			lLayout.addComponent(lDelete);
			lLayout.addComponent(RiplaViewHelper.createSpacer());
		}

		if (inReferenced.hasItems()) {
			lLayout.addComponent(new Label(lMessages
					.getMessage("ui.bibliography.link.remark.referenced"))); //$NON-NLS-1$
			lLayout.addComponent(createTable(inReferenced, inTask, true));
			final Button lRemove = new Button(
					lMessages.getMessage("ui.bibliography.link.button.removeR")); //$NON-NLS-1$
			lRemove.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(final ClickEvent inEvent) {
					if (VIFViewHelper.processAction(inReferenced)) {
						if (!inTask.removeReferences(inReferenced)) {
							Notification.show(
									lMessages
											.getMessage("errmsg.biblio.remove.ref"), Type.WARNING_MESSAGE); //$NON-NLS-1$
						}
					}
				}
			});
			lLayout.addComponent(lRemove);
			lLayout.addComponent(RiplaViewHelper.createSpacer());
		}

		if (inUndeletable.hasItems()) {
			lLayout.addComponent(new Label(lMessages
					.getMessage("ui.bibliography.link.remark.undeletable"))); //$NON-NLS-1$
			lLayout.addComponent(createTable(inUndeletable, inTask, false));
		}
	}

	private Table createTable(final ContributionContainer inTexts,
			final ValueChangeListener inTask, final boolean inShowCheckbox) {
		final Table outTable = new Table();

		outTable.setStyleName("vif-table"); //$NON-NLS-1$
		outTable.setWidth("100%"); //$NON-NLS-1$

		outTable.setContainerDataSource(inTexts);
		// generate check box
		if (inShowCheckbox) {
			outTable.addGeneratedColumn(
					ContributionContainer.CONTRIBUTION_CHECK,
					new VIFViewHelper.CheckBoxColumnGenerator(
							new VIFViewHelper.IConfirmationModeChecker() {
								@Override
								public boolean inConfirmationMode() {
									return confirmationMode;
								}
							}));
		} else {
			outTable.addGeneratedColumn(
					ContributionContainer.CONTRIBUTION_CHECK,
					new Table.ColumnGenerator() {
						@Override
						public Object generateCell(final Table inSource,
								final Object inItemId, final Object inColumnId) {
							return new Label();
						}
					});
		}

		// generate label component for html text
		outTable.addGeneratedColumn(ContributionContainer.CONTRIBUTION_TEXT,
				new Table.ColumnGenerator() {
					@Override
					public Component generateCell(final Table inSource,
							final Object inItemId, final Object inColumnId) {
						return new Label(((ContributionWrapper) inItemId)
								.getContributionText(), ContentMode.HTML);
					}
				});

		outTable.setColumnCollapsingAllowed(true);
		outTable.setColumnReorderingAllowed(true);
		outTable.setSelectable(true);
		outTable.setImmediate(true);
		outTable.setPageLength(0);
		outTable.setColumnExpandRatio(ContributionContainer.CONTRIBUTION_TEXT,
				1);
		outTable.addValueChangeListener(inTask);

		outTable.setVisibleColumns(ContributionContainer.NATURAL_COL_ORDER_WO_STATE);
		outTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(COL_HEADERS,
				Activator.getMessages()));

		return outTable;
	}

	// ---

	private class SelectedFilter implements Filter {
		@SuppressWarnings("unchecked")
		@Override
		public boolean passesFilter(final Object inItemId, final Item inItem)
				throws UnsupportedOperationException {
			final Property<Boolean> lCheckBox = inItem
					.getItemProperty(ContributionContainer.CONTRIBUTION_CHECKED);
			return lCheckBox.getValue();
		}

		@Override
		public boolean appliesToProperty(final Object inPropertyId) {
			return ContributionContainer.CONTRIBUTION_CHECKED
					.equals(inPropertyId);
		}
	}

}

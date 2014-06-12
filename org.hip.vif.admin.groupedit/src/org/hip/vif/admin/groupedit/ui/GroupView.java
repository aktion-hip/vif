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

package org.hip.vif.admin.groupedit.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.SettingException;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupedit.Activator;
import org.hip.vif.admin.groupedit.data.MemberContainer;
import org.hip.vif.admin.groupedit.tasks.AbstractGroupTask;
import org.hip.vif.admin.groupedit.tasks.GroupEditTask;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * View to display the form to create or edit a discussion group.
 * 
 * @author Luthiger Created: 06.11.2011
 */
@SuppressWarnings("serial")
public class GroupView extends CustomComponent {
	private static final Logger LOG = LoggerFactory.getLogger(GroupView.class);

	private static final Long[] REVIEWER_VALUES = { 0l, 1l };
	private static final int DEF_SIZE_BIG = 700;
	private static final int DEF_SIZE = 30;

	/**
	 * View to display the form to create a new group.
	 * 
	 * @param inGroup
	 *            {@link Group}
	 * @param inTask
	 *            {@link AbstractGroupTask}
	 */
	public GroupView(final Group inGroup, final AbstractGroupTask inTask) {
		final IMessages lMessages = Activator.getMessages();
		VerticalLayout lLayout = initializeLayout(
				"ui.group.new.view.title.page", lMessages); //$NON-NLS-1$

		final FormCreator lForm = new FormCreator(inGroup);
		lLayout.addComponent(lForm.createForm());

		lLayout.addComponent(VIFViewHelper.createSpacer());
		Button lSave = createSaveButton(inGroup, inTask, lMessages, lForm);
		lLayout.addComponent(lSave);
	}

	/**
	 * @param inGroup
	 * @param inTask
	 * @param inMessages
	 * @param inForm
	 * @return Button
	 */
	private Button createSaveButton(final Group inGroup,
			final AbstractGroupTask inTask, final IMessages inMessages,
			final FormCreator inForm) {
		Button outSave = new Button(
				inMessages.getMessage("ui.group.editor.button.save")); //$NON-NLS-1$
		outSave.setClickShortcut(KeyCode.ENTER);
		outSave.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				try {
					inForm.commit();
					if (!inTask.save(inGroup)) {
						getWindow()
								.showNotification(
										inMessages
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
									inMessages
											.getMessage("errmsg.group.not.unique"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
				}
			}
		});
		return outSave;
	}

	/**
	 * View to display the form to edit a group.
	 * 
	 * @param inGroup
	 *            {@link Group} the group to edit
	 * @param inNumberOfParticipants
	 *            int number of participants in this group
	 * @param inState
	 *            String the group's actual state
	 * @param inAdmins
	 *            {@link MemberContainer} the group's administrators
	 * @param inTask
	 *            {@link AbstractGroupTask}
	 */
	public GroupView(Group inGroup, int inNumberOfParticipants, String inState,
			final MemberContainer inAdmins, final GroupEditTask inTask) {
		final IMessages lMessages = Activator.getMessages();
		VerticalLayout lLayout = initializeLayout(
				"ui.group.edit.view.title.page", lMessages); //$NON-NLS-1$
		FormCreator lForm = new FormCreator(inGroup, inNumberOfParticipants);
		lLayout.addComponent(lForm.createForm());
		lForm.addRow(
				lMessages.getMessage("ui.group.editor.label.state"), inState); //$NON-NLS-1$

		Button lSave = createSaveButton(inGroup, inTask, lMessages, lForm);
		lLayout.addComponent(lSave);

		// Change state
		lLayout.addComponent(VIFViewHelper.createSpacer());
		lLayout.addComponent(new Label(
				String.format(
						VIFViewHelper.TMPL_TITLE,
						"vif-title", lMessages.getMessage("ui.group.editor.subtitle.state")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$
		TransitionRenderer lTransitions = new TransitionRenderer(inGroup,
				inTask, lMessages);
		for (TransitionComponent lTransition : lTransitions.getTtransitions()) {
			lLayout.addComponent(lTransition);
		}

		// Manage group administration
		lLayout.addComponent(VIFViewHelper.createSpacer());
		lLayout.addComponent(new Label(
				String.format(
						VIFViewHelper.TMPL_TITLE,
						"vif-title", lMessages.getMessage("ui.group.editor.subtitle.admins")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$

		boolean lHasAdmins = inAdmins.getItemIds().size() != 0;
		if (lHasAdmins) {
			lLayout.addComponent(createTable(inAdmins, lMessages, inTask));
		} else {
			lLayout.addComponent(new Label(lMessages
					.getMessage("ui.group.editor.remark.no.admins"))); //$NON-NLS-1$
		}

		Button lAssignAdmins = new Button(
				lMessages.getMessage("ui.group.editor.button.admins.assign")); //$NON-NLS-1$
		lAssignAdmins.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				if (!inTask.selectAdmin()) {
					getWindow()
							.showNotification(
									lMessages
											.getMessage("ui.group.editor.errmsg.admins.assign"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
				}
			}
		});
		Button lDeleteAdmins = new Button(
				lMessages.getMessage("ui.group.editor.button.admins.delete")); //$NON-NLS-1$
		lDeleteAdmins.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				if (VIFViewHelper.processAction(inAdmins, getWindow())) {
					if (!inTask.deleteAdmin()) {
						getWindow()
								.showNotification(
										lMessages
												.getMessage("ui.group.editor.errmsg.admins.delete"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
					}
				}
			}
		});

		if (isPrivate(inGroup)) {
			Button lAssignParticipants = new Button(
					lMessages.getMessage("ui.group.editor.button.participants")); //$NON-NLS-1$
			lAssignParticipants.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent inEvent) {
					if (!inTask.selectParticipants()) {
						getWindow()
								.showNotification(
										lMessages
												.getMessage("ui.group.editor.errmsg.participants"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
					}
				}
			});
			if (lHasAdmins) {
				lLayout.addComponent(VIFViewHelper.createButtons(lDeleteAdmins,
						lAssignAdmins, lAssignParticipants));
			} else {
				lLayout.addComponent(VIFViewHelper.createButtons(lAssignAdmins,
						lAssignParticipants));
			}
		} else {
			if (lHasAdmins) {
				lLayout.addComponent(VIFViewHelper.createButtons(lDeleteAdmins,
						lAssignAdmins));
			} else {
				lLayout.addComponent(lAssignAdmins);
			}
		}
	}

	private boolean isPrivate(Group inGroup) {
		return BeanWrapperHelper.getInteger(GroupHome.KEY_PRIVATE, inGroup)
				.equals(GroupHome.IS_PRIVATE);
	}

	private Table createTable(MemberContainer inAdmins, IMessages inMessages,
			AbstractGroupTask inTask) {
		Table lTable = new Table();
		lTable.setWidth("100%"); //$NON-NLS-1$
		lTable.setContainerDataSource(inAdmins);
		lTable.addGeneratedColumn(MemberContainer.MEMBER_CHECK,
				new VIFViewHelper.CheckBoxColumnGenerator(
						new VIFViewHelper.IConfirmationModeChecker() {
							public boolean inConfirmationMode() {
								return false;
							}
						}));
		lTable.setPageLength(0);
		lTable.setColumnCollapsingAllowed(true);
		lTable.setColumnReorderingAllowed(true);
		lTable.setSelectable(true);
		lTable.setImmediate(true);
		lTable.addListener((Property.ValueChangeListener) inTask);

		lTable.setVisibleColumns(MemberContainer.NATURAL_COL_ORDER);
		lTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(
				MemberContainer.COL_HEADERS, inMessages));
		return lTable;
	}

	private VerticalLayout initializeLayout(String inTitleKey,
			IMessages inMessages) {
		VerticalLayout outLayout = new VerticalLayout();
		setCompositionRoot(outLayout);

		outLayout.setStyleName("vif-view"); //$NON-NLS-1$
		outLayout
				.addComponent(new Label(
						String.format(
								VIFViewHelper.TMPL_TITLE,
								"vif-pagetitle", inMessages.getMessage(inTitleKey)), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$
		return outLayout;
	}

	// --- private classes ---

	private class FormCreator extends AbstractFormCreator {
		private Group group;
		private IMessages messages;
		private TextField name;
		private LabelValueTable table;
		private String annotation = ""; //$NON-NLS-1$

		FormCreator(Group inGroup) {
			group = inGroup;
			messages = Activator.getMessages();
			table = new LabelValueTable();
		}

		FormCreator(Group inGroup, int inNumberOfParticipants) {
			this(inGroup);
			annotation = String
					.format(messages
							.getFormattedMessage(
									"ui.group.editor.annotation.group.size", inNumberOfParticipants)); //$NON-NLS-1$
		}

		@Override
		protected Component createTable() {
			name = RiplaViewHelper.createTextField(group, GroupHome.KEY_NAME,
					DEF_SIZE_BIG);
			focusInit();

			String lFieldLabel = messages
					.getMessage("ui.group.editor.label.name"); //$NON-NLS-1$
			table.addRowEmphasized(lFieldLabel,
					addFieldRequired("name", name, lFieldLabel)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			lFieldLabel = messages
					.getMessage("ui.group.editor.label.description"); //$NON-NLS-1$
			table.addRowEmphasized(
					lFieldLabel, //$NON-NLS-1$
					addFieldRequired(
							"description", createTextArea(group, GroupHome.KEY_DESCRIPTION), lFieldLabel)); //$NON-NLS-1$ //$NON-NLS-2$
			table.addRow(
					messages.getMessage("ui.group.editor.label.number.reviewers"), addField("numberOfReviewers", createSelect(group, GroupHome.KEY_REVIEWERS))); //$NON-NLS-1$ //$NON-NLS-2$

			CheckBox lPrivateCheck = createCheckBox(group,
					GroupHome.KEY_PRIVATE);
			lPrivateCheck.setEnabled(!isOpened());
			lPrivateCheck.setImmediate(true);
			table.addRow(
					messages.getMessage("ui.group.editor.label.private"), addField("isPrivate", lPrivateCheck)); //$NON-NLS-1$ //$NON-NLS-2$

			final TextField lGuestDepth = RiplaViewHelper.createTextField(
					new BOProperty<Long>(group, GroupHome.KEY_GUEST_DEPTH,
							Long.class), DEF_SIZE);
			lGuestDepth.setEnabled(!isPrivate());
			table.addRow(
					messages.getMessage("ui.group.editor.label.guest.depth"), addField("guestDepth", lGuestDepth)); //$NON-NLS-1$ //$NON-NLS-2$

			final TextField lMinGroupSize = RiplaViewHelper.createTextField(
					new BOProperty<Long>(group, GroupHome.KEY_MIN_GROUP_SIZE,
							Long.class), DEF_SIZE);
			lMinGroupSize.setEnabled(!isPrivate());
			table.addRow(
					messages.getMessage("ui.group.editor.label.group.size"), new AnnotatetField(addField("minGroupSize", lMinGroupSize), annotation)); //$NON-NLS-1$ //$NON-NLS-2$

			// handle changes of value 'isPrivate'
			lPrivateCheck.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent inEvent) {
					boolean lIsPrivate = ((CheckBox) inEvent.getProperty())
							.booleanValue();
					lGuestDepth.setEnabled(!lIsPrivate);
					lMinGroupSize.setEnabled(!lIsPrivate);
				}
			});

			return table;
		}

		public void focusInit() {
			name.focus();
		}

		public void addRow(String inLabel, String inValue) {
			table.addRow(inLabel, inValue);
		}

		private TextArea createTextArea(GeneralDomainObject inModel,
				String inKey) {
			TextArea out = new TextArea(new BOProperty<String>(
					(DomainObject) inModel, inKey, String.class));
			out.setWidth(DEF_SIZE_BIG, UNITS_PIXELS);
			out.setRows(4);
			out.setStyleName("vif-input"); //$NON-NLS-1$
			return out;
		}

		private CheckBox createCheckBox(GeneralDomainObject inModel,
				String inKey) {
			CheckBox out = new CheckBox(null, new BooleanProperty<Boolean>(
					(DomainObject) inModel, inKey));
			out.setStyleName("vif-input"); //$NON-NLS-1$
			return out;
		}

		private Select createSelect(GeneralDomainObject inModel, String inKey) {
			Select out = new Select(null, Arrays.asList(REVIEWER_VALUES));
			out.setNullSelectionAllowed(false);
			out.setWidth(45, UNITS_PIXELS);
			out.setPropertyDataSource(new BOProperty<Long>(
					(DomainObject) inModel, inKey, Long.class));
			out.setStyleName("vif-input"); //$NON-NLS-1$
			return out;
		}

		private boolean isPrivate() {
			return GroupHome.IS_PRIVATE.equals(BeanWrapperHelper.getInteger(
					GroupHome.KEY_PRIVATE, group));
		}

		private boolean isOpened() {
			return BeanWrapperHelper.getInteger(GroupHome.KEY_STATE, group) > 1;
		}
	}

	private static class BooleanProperty<T> extends AbstractProperty {
		private DomainObject boInstance;
		private String key;

		BooleanProperty(DomainObject inInstance, String inKey) {
			boInstance = inInstance;
			key = inKey;
		}

		@SuppressWarnings("unchecked")
		public T getValue() {
			try {
				Object lValue = boInstance.get(key);
				if (lValue == null) {
					return (T) Boolean.FALSE;
				}
				return (T) ("0".equals(lValue.toString()) ? Boolean.FALSE : Boolean.TRUE); //$NON-NLS-1$
			}
			catch (VException exc) {
				LOG.error("Can't get value for \"{}\"!", key, exc); //$NON-NLS-1$
			}
			return (T) Boolean.FALSE;
		}

		public void setValue(Object inNewValue) throws ReadOnlyException,
				ConversionException {
			try {
				Long lInput = ((Boolean) inNewValue).booleanValue() ? 1l : 0l;
				boInstance.set(key, lInput);
			}
			catch (SettingException exc) {
				LOG.error("Can't set value for \"{}\"!", key, exc); //$NON-NLS-1$
			}
		}

		public Class<?> getType() {
			return Boolean.class;
		}
	}

	private static class AnnotatetField extends CustomComponent {
		private static final String STYLE_LABEL = "<div class=\"vif-label\">%s</div>"; //$NON-NLS-1$

		AnnotatetField(Field inField, String inAnnotation) {
			HorizontalLayout lLayout = new HorizontalLayout();
			lLayout.setWidth("100%"); //$NON-NLS-1$
			setCompositionRoot(lLayout);
			lLayout.addComponent(inField);
			lLayout.addComponent(new Label(String.format(STYLE_LABEL,
					inAnnotation), Label.CONTENT_XHTML));
		}
	}

	private static class TransitionRenderer {
		private Collection<TransitionComponent> transitions = new Vector<GroupView.TransitionComponent>();

		TransitionRenderer(Group inGroup, final GroupEditTask inTask,
				IMessages inMessages) {
			try {
				for (String lTransitionType : inGroup.getTransitions()) {
					if (VIFGroupWorkflow.TRANS_OPEN.equals(lTransitionType)) {
						TransitionComponent lTransition = new TransitionComponent(
								inMessages
										.getMessage("ui.group.transition.button.open"), //$NON-NLS-1$
								inMessages
										.getMessage("ui.group.transition.remark.open"), //$NON-NLS-1$
								new IClickCommand() {
									public boolean transitionCommand() {
										return inTask.groupOpen();
									}
								});
						transitions.add(lTransition);
					}
					if (VIFGroupWorkflow.TRANS_CLOSE.equals(lTransitionType)) {
						TransitionComponent lTransition = new TransitionComponent(
								inMessages
										.getMessage("ui.group.transition.button.close"), //$NON-NLS-1$
								inMessages
										.getMessage("ui.group.transition.remark.close"), //$NON-NLS-1$
								new IClickCommand() {
									public boolean transitionCommand() {
										return inTask.groupClose();
									}
								});
						transitions.add(lTransition);
					}
					if (VIFGroupWorkflow.TRANS_SUSPEND.equals(lTransitionType)) {
						TransitionComponent lTransition = new TransitionComponent(
								inMessages
										.getMessage("ui.group.transition.button.suspend"), //$NON-NLS-1$
								inMessages
										.getMessage("ui.group.transition.remark.suspend"), //$NON-NLS-1$
								new IClickCommand() {
									public boolean transitionCommand() {
										return inTask.groupSuspend();
									}
								});
						transitions.add(lTransition);
					}
					if (VIFGroupWorkflow.TRANS_REACTIVATE
							.equals(lTransitionType)) {
						TransitionComponent lTransition = new TransitionComponent(
								inMessages
										.getMessage("ui.group.transition.button.reactivate"), //$NON-NLS-1$
								inMessages
										.getMessage("ui.group.transition.remark.reactivate"), //$NON-NLS-1$
								new IClickCommand() {
									public boolean transitionCommand() {
										return inTask.groupReactivate();
									}
								});
						transitions.add(lTransition);
					}
					if (VIFGroupWorkflow.TRANS_DEACTIVATE
							.equals(lTransitionType)) {
						TransitionComponent lTransition = new TransitionComponent(
								inMessages
										.getMessage("ui.group.transition.button.deactivate"), //$NON-NLS-1$
								inMessages
										.getMessage("ui.group.transition.remark.deactivate"), //$NON-NLS-1$
								new IClickCommand() {
									public boolean transitionCommand() {
										return inTask.groupDeactivate();
									}
								});
						transitions.add(lTransition);
					}
				}
			}
			catch (VException exc) {
				LOG.error("Error while rendering the group transitions.", exc); //$NON-NLS-1$
			}
		}

		Collection<TransitionComponent> getTtransitions() {
			return transitions;
		}
	}

	/**
	 * Class to render the buttons to initiate the group's state changes.
	 */
	private static class TransitionComponent extends CustomComponent {

		TransitionComponent(String inLabel, String inRemark,
				final IClickCommand inCommand) {
			VerticalLayout lLayout = new VerticalLayout();
			setCompositionRoot(lLayout);
			lLayout.setStyleName("vif-remark"); //$NON-NLS-1$

			Button lAction = new Button(inLabel);
			lAction.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent inEvent) {
					if (!inCommand.transitionCommand()) {
						getWindow()
								.showNotification(
										"errmsg.state.change", Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
					}
				}
			});
			lLayout.addComponent(lAction);
			lLayout.addComponent(new Label(inRemark));
		}
	}

	private static interface IClickCommand {
		boolean transitionCommand();
	}

}

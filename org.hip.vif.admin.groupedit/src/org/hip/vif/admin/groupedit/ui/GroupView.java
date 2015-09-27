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

package org.hip.vif.admin.groupedit.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupedit.Activator;
import org.hip.vif.admin.groupedit.data.GroupBean;
import org.hip.vif.admin.groupedit.data.MemberContainer;
import org.hip.vif.admin.groupedit.tasks.AbstractGroupTask;
import org.hip.vif.admin.groupedit.tasks.GroupEditTask;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.AbstractFormCreator;
import org.ripla.web.util.LabelValueTable;
import org.ripla.web.util.RiplaViewHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/** View to display the form to create or edit a discussion group.
 *
 * @author Luthiger Created: 06.11.2011 */
@SuppressWarnings("serial")
public class GroupView extends CustomComponent {
    private static final Logger LOG = LoggerFactory.getLogger(GroupView.class);

    private static final Long[] REVIEWER_VALUES = { 0l, 1l };
    private static final int DEF_SIZE_BIG = 700;
    private static final int DEF_SIZE = 30;

    /** View to display the form to create a new group.
     *
     * @param inGroup {@link Group}
     * @param inTask {@link AbstractGroupTask} */
    public GroupView(final Group inGroup, final AbstractGroupTask inTask) {
        final IMessages lMessages = Activator.getMessages();
        final VerticalLayout lLayout = initializeLayout("ui.group.new.view.title.page", lMessages); //$NON-NLS-1$

        final FormCreator lForm = new FormCreator(inGroup);
        lLayout.addComponent(lForm.createForm());

        lLayout.addComponent(RiplaViewHelper.createSpacer());
        final Button lSave = createSaveButton(inGroup, inTask, lMessages, lForm);
        lLayout.addComponent(lSave);
    }

    /** @param inGroup
     * @param inTask
     * @param inMessages
     * @param inForm
     * @return Button */
    private Button createSaveButton(final Group inGroup,
            final AbstractGroupTask inTask, final IMessages inMessages,
            final FormCreator inForm) {
        final Button outSave = new Button(
                inMessages.getMessage("ui.group.editor.button.save")); //$NON-NLS-1$
        outSave.setClickShortcut(KeyCode.ENTER);
        outSave.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                try {
                    inForm.commit();
                    if (!inTask.save(inGroup)) {
                        Notification.show(inMessages
                                .getMessage("errmsg.save.general"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                    }
                } catch (final CommitException exc) {
                    // intentionally left empty
                } catch (final ExternIDNotUniqueException exc) {
                    inForm.focusInit();
                    Notification.show(inMessages
                            .getMessage("errmsg.group.not.unique"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                }
            }
        });
        return outSave;
    }

    /** View to display the form to edit a group.
     *
     * @param inGroup {@link Group} the group to edit
     * @param inNumberOfParticipants int number of participants in this group
     * @param inState String the group's actual state
     * @param inAdmins {@link MemberContainer} the group's administrators
     * @param inTask {@link AbstractGroupTask} */
    public GroupView(final Group inGroup, final int inNumberOfParticipants, final String inState,
            final MemberContainer inAdmins, final GroupEditTask inTask) {
        final IMessages lMessages = Activator.getMessages();
        final VerticalLayout lLayout = initializeLayout("ui.group.edit.view.title.page", lMessages); //$NON-NLS-1$
        final FormCreator lForm = new FormCreator(inGroup, inNumberOfParticipants);
        lLayout.addComponent(lForm.createForm());
        lForm.addRow(
                lMessages.getMessage("ui.group.editor.label.state"), inState); //$NON-NLS-1$

        final Button lSave = createSaveButton(inGroup, inTask, lMessages, lForm);
        lLayout.addComponent(lSave);

        // Change state
        lLayout.addComponent(RiplaViewHelper.createSpacer());
        lLayout.addComponent(new Label(
                String.format(
                        VIFViewHelper.TMPL_TITLE, "vif-title", lMessages.getMessage("ui.group.editor.subtitle.state")), //$NON-NLS-1$ //$NON-NLS-2$
                ContentMode.HTML));
        final TransitionRenderer lTransitions = new TransitionRenderer(inGroup,
                inTask, lMessages);
        for (final TransitionComponent lTransition : lTransitions.getTtransitions()) {
            lLayout.addComponent(lTransition);
        }

        // Manage group administration
        lLayout.addComponent(RiplaViewHelper.createSpacer());
        lLayout.addComponent(new Label(
                String.format(
                        VIFViewHelper.TMPL_TITLE, "vif-title", lMessages.getMessage("ui.group.editor.subtitle.admins")), //$NON-NLS-1$ //$NON-NLS-2$
                ContentMode.HTML));

        final boolean lHasAdmins = inAdmins.getItemIds().size() != 0;
        if (lHasAdmins) {
            lLayout.addComponent(createTable(inAdmins, lMessages, inTask));
        } else {
            lLayout.addComponent(new Label(lMessages
                    .getMessage("ui.group.editor.remark.no.admins"))); //$NON-NLS-1$
        }

        final Button lAssignAdmins = new Button(
                lMessages.getMessage("ui.group.editor.button.admins.assign")); //$NON-NLS-1$
        lAssignAdmins.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                if (!inTask.selectAdmin()) {
                    Notification.show(lMessages
                            .getMessage("ui.group.editor.errmsg.admins.assign"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                }
            }
        });
        final Button lDeleteAdmins = new Button(
                lMessages.getMessage("ui.group.editor.button.admins.delete")); //$NON-NLS-1$
        lDeleteAdmins.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                if (VIFViewHelper.processAction(inAdmins)) {
                    if (!inTask.deleteAdmin()) {
                        Notification.show(lMessages
                                .getMessage("ui.group.editor.errmsg.admins.delete"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                    }
                }
            }
        });

        if (isPrivate(inGroup)) {
            final Button lAssignParticipants = new Button(
                    lMessages.getMessage("ui.group.editor.button.participants")); //$NON-NLS-1$
            lAssignParticipants.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final ClickEvent inEvent) {
                    if (!inTask.selectParticipants()) {
                        Notification.show(lMessages
                                .getMessage("ui.group.editor.errmsg.participants"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                    }
                }
            });
            if (lHasAdmins) {
                lLayout.addComponent(RiplaViewHelper.createButtons(lDeleteAdmins,
                        lAssignAdmins, lAssignParticipants));
            } else {
                lLayout.addComponent(RiplaViewHelper.createButtons(lAssignAdmins,
                        lAssignParticipants));
            }
        } else {
            if (lHasAdmins) {
                lLayout.addComponent(RiplaViewHelper.createButtons(lDeleteAdmins,
                        lAssignAdmins));
            } else {
                lLayout.addComponent(lAssignAdmins);
            }
        }
    }

    private boolean isPrivate(final Group inGroup) {
        return BeanWrapperHelper.getInteger(GroupHome.KEY_PRIVATE, inGroup)
                .equals(GroupHome.IS_PRIVATE);
    }

    private Table createTable(final MemberContainer inAdmins, final IMessages inMessages,
            final AbstractGroupTask inTask) {
        final Table lTable = new Table();
        lTable.setWidth("100%"); //$NON-NLS-1$
        lTable.setContainerDataSource(inAdmins);
        lTable.addGeneratedColumn(MemberContainer.MEMBER_CHECK,
                new VIFViewHelper.CheckBoxColumnGenerator(
                        new VIFViewHelper.IConfirmationModeChecker() {
                            @Override
                            public boolean inConfirmationMode() {
                                return false;
                            }
                        }));
        lTable.setPageLength(0);
        lTable.setColumnCollapsingAllowed(true);
        lTable.setColumnReorderingAllowed(true);
        lTable.setSelectable(true);
        lTable.setImmediate(true);
        lTable.addValueChangeListener((Property.ValueChangeListener) inTask);

        lTable.setVisibleColumns(MemberContainer.NATURAL_COL_ORDER);
        lTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(
                MemberContainer.COL_HEADERS, inMessages));
        return lTable;
    }

    private VerticalLayout initializeLayout(final String inTitleKey,
            final IMessages inMessages) {
        final VerticalLayout outLayout = new VerticalLayout();
        setCompositionRoot(outLayout);

        outLayout.setStyleName("vif-view"); //$NON-NLS-1$
        outLayout
                .addComponent(new Label(
                        String.format(
                                VIFViewHelper.TMPL_TITLE, "vif-pagetitle", inMessages.getMessage(inTitleKey)), //$NON-NLS-1$
                        ContentMode.HTML)); //$NON-NLS-2$
        return outLayout;
    }

    // --- private classes ---

    private class FormCreator extends AbstractFormCreator {
        private final IMessages messages;
        private TextField name;
        private final LabelValueTable table;
        private String annotation = ""; //$NON-NLS-1$
        private final boolean isPrivate;
        private final boolean isOpened;

        FormCreator(final Group inGroup) {
            super(GroupBean.createGroupBean(inGroup));
            isPrivate = GroupHome.IS_PRIVATE.equals(BeanWrapperHelper.getInteger(GroupHome.KEY_PRIVATE, inGroup));
            isOpened = BeanWrapperHelper.getInteger(GroupHome.KEY_STATE, inGroup) > 1;
            messages = Activator.getMessages();
            table = new LabelValueTable();
        }

        FormCreator(final Group inGroup, final int inNumberOfParticipants) {
            this(inGroup);
            annotation = String
                    .format(messages
                            .getFormattedMessage("ui.group.editor.annotation.group.size", inNumberOfParticipants)); //$NON-NLS-1$
        }

        @Override
        protected Component createTable() {
            name = RiplaViewHelper.createTextField(DEF_SIZE_BIG);
            focusInit();

            String lFieldLabel = messages
                    .getMessage("ui.group.editor.label.name"); //$NON-NLS-1$
            table.addRowEmphasized(lFieldLabel,
                    VIFViewHelper.addWrapped(addFieldRequired(GroupBean.FN_NAME, name, lFieldLabel)));
            lFieldLabel = messages
                    .getMessage("ui.group.editor.label.description"); //$NON-NLS-1$
            table.addRowEmphasized(
                    lFieldLabel, // $NON-NLS-1$
                    VIFViewHelper.addWrapped(
                            addFieldRequired(GroupBean.FN_DESC, createTextArea(GroupBean.FN_DESC), lFieldLabel))); // $NON-NLS-1$
                                                                                                                   // //$NON-NLS-2$
            table.addRow(
                    messages.getMessage("ui.group.editor.label.number.reviewers"), //$NON-NLS-1$
                    createSelect(GroupBean.FN_REVIEWERS)); //$NON-NLS-2$

            final CheckBox lPrivateCheck = createCheckBox(GroupBean.FN_PRIVATE);
            lPrivateCheck.setImmediate(true);
            table.addRow(
                    messages.getMessage("ui.group.editor.label.private"), lPrivateCheck); //$NON-NLS-1$ //$NON-NLS-2$

            final TextField lGuestDepth = RiplaViewHelper.createTextField(DEF_SIZE,
                    RiplaViewHelper.ConversionType.STRING_TO_NUMBER);
            table.addRow(
                    messages.getMessage("ui.group.editor.label.guest.depth"), //$NON-NLS-1$
                    addField(GroupBean.FN_GUEST_DEPTH, lGuestDepth)); //$NON-NLS-2$

            final TextField lMinGroupSize = RiplaViewHelper.createTextField(DEF_SIZE,
                    RiplaViewHelper.ConversionType.STRING_TO_NUMBER);
            table.addRow(
                    messages.getMessage("ui.group.editor.label.group.size"), //$NON-NLS-1$
                    new AnnotatetField(addField(GroupBean.FN_GROUP_SIZE, lMinGroupSize), annotation)); //$NON-NLS-2$

            // handle changes of value 'isPrivate'
            lPrivateCheck.addValueChangeListener(new Property.ValueChangeListener() {
                @SuppressWarnings("unchecked")
                @Override
                public void valueChange(final ValueChangeEvent inEvent) {
                    final Boolean lValue = ((Field<Boolean>) inEvent.getProperty()).getValue();
                    final boolean lIsPrivate = (null == lValue) ? false : lValue.booleanValue();
                    lGuestDepth.setEnabled(!lIsPrivate);
                    lMinGroupSize.setEnabled(!lIsPrivate);
                }
            });

            lPrivateCheck.setEnabled(!isOpened);
            lGuestDepth.setEnabled(!isPrivate);
            lMinGroupSize.setEnabled(!isPrivate);
            return table;
        }

        public void focusInit() {
            name.focus();
        }

        public void addRow(final String inLabel, final String inValue) {
            table.addRow(inLabel, inValue);
        }

        private TextArea createTextArea(final String inKey) {
            final TextArea out = new TextArea();
            addField(inKey, out);
            out.setWidth(DEF_SIZE_BIG, Unit.PIXELS);
            out.setRows(4);
            out.setStyleName("ripla-input"); //$NON-NLS-1$
            return out;
        }

        private CheckBox createCheckBox(final String inKey) {
            final CheckBox out = new CheckBox(null);
            addField(inKey, out);
            out.setStyleName("ripla-input"); //$NON-NLS-1$
            return out;
        }

        private ComboBox createSelect(final String inKey) {
            final ComboBox out = new ComboBox(null, Arrays.asList(REVIEWER_VALUES));
            out.setNullSelectionAllowed(false);
            out.setWidth(45, Unit.PIXELS);
            addField(inKey, out);
            out.setStyleName("ripla-input"); //$NON-NLS-1$
            return out;
        }
    }

    private static class AnnotatetField extends CustomComponent {
        private static final String STYLE_LABEL = "<div class=\"vif-label\">%s</div>"; //$NON-NLS-1$

        AnnotatetField(final Field<?> inField, final String inAnnotation) {
            final HorizontalLayout lLayout = new HorizontalLayout();
            lLayout.setWidth("100%"); //$NON-NLS-1$
            setCompositionRoot(lLayout);
            lLayout.addComponent(inField);
            lLayout.addComponent(new Label(String.format(STYLE_LABEL,
                    inAnnotation), ContentMode.HTML));
        }
    }

    private static class TransitionRenderer {
        private final Collection<TransitionComponent> transitions = new Vector<GroupView.TransitionComponent>();

        TransitionRenderer(final Group inGroup, final GroupEditTask inTask,
                final IMessages inMessages) {
            try {
                for (final String lTransitionType : inGroup.getTransitions()) {
                    if (VIFGroupWorkflow.TRANS_OPEN.equals(lTransitionType)) {
                        final TransitionComponent lTransition = new TransitionComponent(
                                inMessages
                                        .getMessage("ui.group.transition.button.open"), //$NON-NLS-1$
                                inMessages
                                        .getMessage("ui.group.transition.remark.open"), //$NON-NLS-1$
                                new IClickCommand() {
                                    @Override
                                    public boolean transitionCommand() {
                                        return inTask.groupOpen();
                                    }
                                });
                        transitions.add(lTransition);
                    }
                    if (VIFGroupWorkflow.TRANS_CLOSE.equals(lTransitionType)) {
                        final TransitionComponent lTransition = new TransitionComponent(
                                inMessages
                                        .getMessage("ui.group.transition.button.close"), //$NON-NLS-1$
                                inMessages
                                        .getMessage("ui.group.transition.remark.close"), //$NON-NLS-1$
                                new IClickCommand() {
                                    @Override
                                    public boolean transitionCommand() {
                                        return inTask.groupClose();
                                    }
                                });
                        transitions.add(lTransition);
                    }
                    if (VIFGroupWorkflow.TRANS_SUSPEND.equals(lTransitionType)) {
                        final TransitionComponent lTransition = new TransitionComponent(
                                inMessages
                                        .getMessage("ui.group.transition.button.suspend"), //$NON-NLS-1$
                                inMessages
                                        .getMessage("ui.group.transition.remark.suspend"), //$NON-NLS-1$
                                new IClickCommand() {
                                    @Override
                                    public boolean transitionCommand() {
                                        return inTask.groupSuspend();
                                    }
                                });
                        transitions.add(lTransition);
                    }
                    if (VIFGroupWorkflow.TRANS_REACTIVATE
                            .equals(lTransitionType)) {
                        final TransitionComponent lTransition = new TransitionComponent(
                                inMessages
                                        .getMessage("ui.group.transition.button.reactivate"), //$NON-NLS-1$
                                inMessages
                                        .getMessage("ui.group.transition.remark.reactivate"), //$NON-NLS-1$
                                new IClickCommand() {
                                    @Override
                                    public boolean transitionCommand() {
                                        return inTask.groupReactivate();
                                    }
                                });
                        transitions.add(lTransition);
                    }
                    if (VIFGroupWorkflow.TRANS_DEACTIVATE
                            .equals(lTransitionType)) {
                        final TransitionComponent lTransition = new TransitionComponent(
                                inMessages
                                        .getMessage("ui.group.transition.button.deactivate"), //$NON-NLS-1$
                                inMessages
                                        .getMessage("ui.group.transition.remark.deactivate"), //$NON-NLS-1$
                                new IClickCommand() {
                                    @Override
                                    public boolean transitionCommand() {
                                        return inTask.groupDeactivate();
                                    }
                                });
                        transitions.add(lTransition);
                    }
                }
            } catch (final VException exc) {
                LOG.error("Error while rendering the group transitions.", exc); //$NON-NLS-1$
            }
        }

        Collection<TransitionComponent> getTtransitions() {
            return transitions;
        }
    }

    /** Class to render the buttons to initiate the group's state changes. */
    private static class TransitionComponent extends CustomComponent {
        TransitionComponent(final String inLabel, final String inRemark,
                final IClickCommand inCommand) {
            final VerticalLayout lLayout = new VerticalLayout();
            setCompositionRoot(lLayout);
            lLayout.setStyleName("vif-remark"); //$NON-NLS-1$

            final Button lAction = new Button(inLabel);
            lAction.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final ClickEvent inEvent) {
                    if (!inCommand.transitionCommand()) {
                        Notification.show("errmsg.state.change", Type.WARNING_MESSAGE); //$NON-NLS-1$
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

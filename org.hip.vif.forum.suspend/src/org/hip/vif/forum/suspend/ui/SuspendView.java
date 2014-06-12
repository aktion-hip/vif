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

package org.hip.vif.forum.suspend.ui;

import java.util.Calendar;
import java.util.Date;

import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.forum.suspend.Activator;
import org.hip.vif.forum.suspend.data.GroupContainer;
import org.hip.vif.forum.suspend.tasks.SuspendTask;
import org.hip.vif.forum.suspend.tasks.SuspendTask.DatePrepare;
import org.hip.vif.forum.suspend.tasks.SuspendTask.SuspendState;
import org.hip.vif.web.components.LabelValueTable;
import org.hip.vif.web.util.VIFViewHelper;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

/**
 * View to display the form for that the user can enter the period she wants to suspend participation. 
 * 
 * @author Luthiger
 * Created: 02.10.2011
 */
@SuppressWarnings("serial")
public class SuspendView extends CustomComponent {
	
	/**
	 * Constructor
	 * 
	 * @param inGroups {@link GroupContainer} the groups that are affected 
	 * @param inDates {@link DatePrepare} an initial or earlier suspend period
	 * @param inTask {@link SuspendTask}
	 */
	public SuspendView(GroupContainer inGroups, DatePrepare inDates, final SuspendTask inTask) {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		
		final IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-view"); //$NON-NLS-1$
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-pagetitle", lMessages.getMessage("ui.suspend.view.title.page")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$
		
		final Button lSuspend = new Button(lMessages.getMessage("ui.suspend.view.button.suspend")); //$NON-NLS-1$
		final Button lClear = new Button(lMessages.getMessage("ui.suspend.view.button.clear")); //$NON-NLS-1$

		String lDatePattern = PreferencesHandler.INSTANCE.getDatePattern();
		LabelValueTable lTable = new LabelValueTable();
		final PopupDateField lFrom = createDateField(inDates.getFromDate(), lDatePattern);
		lFrom.addValidator(new DateFieldValidator());
		lFrom.focus();
		lTable.addRow(lMessages.getFormattedMessage("ui.suspend.view.label.from", lDatePattern), lFrom); //$NON-NLS-1$
		
		final PopupDateField lTo = createDateField(inDates.getToDate(), lDatePattern);
		lTo.addValidator(new DateFieldValidator(lFrom));
		lTable.addRow(lMessages.getFormattedMessage("ui.suspend.view.label.to", lDatePattern), lTo); //$NON-NLS-1$
		
		lLayout.addComponent(lTable);
		lLayout.addComponent(VIFViewHelper.createSpacer());
		
		lSuspend.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				DateFieldValidator lValidator = new DateFieldValidator(lFrom, lTo);
				try {
					lValidator.validate();
					if (inTask.saveSuspendDates((Date)lFrom.getValue(), (Date)lTo.getValue())) {
						showNotification("msg.data.saved", Notification.TYPE_TRAY_NOTIFICATION); //$NON-NLS-1$
						lClear.setVisible(true);
					}
					else {
						showNotification("errmsg.general", Notification.TYPE_ERROR_MESSAGE); //$NON-NLS-1$
					}
				} 
				catch (InvalidValueException exc) {
					showNotification(exc.getMessage(), Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		lSuspend.setClickShortcut(KeyCode.ENTER);
		
		lClear.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				if (inTask.clearSuspendDates()) {
					showNotification("msg.data.cleared", Notification.TYPE_TRAY_NOTIFICATION); //$NON-NLS-1$
					lFrom.setValue(new Date());
					lFrom.focus();
					lTo.setValue(null);
					lClear.setVisible(false);
				}
				else {
					showNotification("errmsg.general", Notification.TYPE_ERROR_MESSAGE); //$NON-NLS-1$
				}
			}
		});
		
		lLayout.addComponent(VIFViewHelper.createButtons(lSuspend, lClear));
		
		//Clear button is visible only if there's something to clear.
		lClear.setVisible(inDates.getSuspendDateState() == SuspendState.HAS_DATES);
		
		lLayout.addComponent(VIFViewHelper.createSpacer());
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-subtitle", lMessages.getMessage("ui.suspend.view.subtitle")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$
		lLayout.addComponent(createTable(inGroups));
	}
	
	private void showNotification(String inMsgKey, int inNotificationType) {
		getWindow().showNotification(Activator.getMessages().getMessage(inMsgKey), inNotificationType);
	}

	private PopupDateField createDateField(Date inDate, String inDatePattern) {
		PopupDateField out = new PopupDateField();
		out.setValue(inDate);
		out.setResolution(PopupDateField.RESOLUTION_DAY);
		out.setDateFormat(inDatePattern);
		out.setImmediate(true);
		out.setParseErrorMessage(Activator.getMessages().getMessage("warning.input.incorrect")); //$NON-NLS-1$
		return out;
	}
	
	private Table createTable(GroupContainer inGroups) {
		Table outTable = new Table();
		outTable.setWidth("100%"); //$NON-NLS-1$
		outTable.setContainerDataSource(inGroups);
		
		outTable.setColumnCollapsingAllowed(true);
		outTable.setColumnReorderingAllowed(true);
		outTable.setSelectable(false);
		outTable.setImmediate(false);
		outTable.setPageLength(VIFViewHelper.getTablePageLength(inGroups.size()));
		
		outTable.setVisibleColumns(GroupContainer.NATURAL_COL_ORDER);
		outTable.setColumnHeaders(getColumnHeaders(GroupContainer.COL_HEADERS));
		return outTable;
	}

	private String[] getColumnHeaders(String[] inKeys) {
		IMessages lMessages = Activator.getMessages();
		String[] outHeaders = new String[inKeys.length];
		for (int i = 0; i < inKeys.length; i++) {
			String lKey = inKeys[i];
			if (lKey.length() == 0) {
				outHeaders[i] = ""; //$NON-NLS-1$
			}
			else {
				outHeaders[i] = lMessages.getMessage(lKey);				
			}
		}
		return outHeaders;
	}
	
// ---
	
	private static class DateFieldValidator implements Validator {
		private Date now;
		private DateField from;
		private DateField to;

		DateFieldValidator() {
			Calendar lCal = Calendar.getInstance();
			lCal.add(Calendar.DAY_OF_YEAR, -1);
			now = lCal.getTime();
		}
		
		DateFieldValidator(DateField inFrom) {
			this();
			from = inFrom;
		}
		
		DateFieldValidator(DateField inFrom, DateField inTo) {
			this();
			from = inFrom;
			to = inTo;
		}
		
		public boolean isValid(Object inValue) {
			if (noDateType(inValue)) {
				return false;
			}
			if (from == null) {				
				return ((Date)inValue).after(now);
			}
			return ((Date)inValue).after((Date) from.getValue());
		}
		private boolean noDateType(Object inValue) {
			return (inValue == null || !(inValue instanceof Date));
		}
		
		public void validate(Object inValue) throws InvalidValueException {
			if (isValid(inValue)) {
				return;
			}
			doFailed(inValue);
		}
		
		public void validate() throws InvalidValueException {
			Object lFrom = from.getValue();
			Object lTo = to.getValue();
			if (!noDateType(lFrom) && !noDateType(lTo) && now.before((Date) lFrom) && ((Date)lFrom).before((Date) lTo)) {
				return;
			}
			doFailed(lTo);
		}
		
		private void doFailed(Object inValue) throws InvalidValueException {
			if (inValue == null || !(inValue instanceof Date)) {
				throw new InvalidValueException("warning.input.incorrect"); //$NON-NLS-1$
			}
			if (from == null || now.after((Date) from.getValue())) {
				throw new InvalidValueException("warning.input.starting");				 //$NON-NLS-1$
			}
			if (! ((Date)inValue).after((Date) from.getValue())) {
				throw new InvalidValueException("warning.input.ending");				 //$NON-NLS-1$
			}
			throw new InvalidValueException("errmsg.input");							 //$NON-NLS-1$
		}
		
	}
	
}
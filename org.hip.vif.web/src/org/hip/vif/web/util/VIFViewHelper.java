/**
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
package org.hip.vif.web.util;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.vif.core.interfaces.ISelectableBean;
import org.hip.vif.web.Activator;
import org.ripla.interfaces.IMessages;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.server.Sizeable;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

/**
 * Constants and templates useful for views in use case providing bundles.
 * 
 * @author Luthiger Created: 01.07.2011
 */
public class VIFViewHelper {
	/**
	 * xhtml for a title tag with a specified class attribute
	 */
	public static final String TMPL_TITLE = "<div class=\"%s\">%s</div>"; //$NON-NLS-1$
	public static final String TMPL_WARNING = "<div class=\"vif-warning\">%s</div>"; //$NON-NLS-1$

	/**
	 * Convenience function:<br />
	 * Checks whether the data is ready to be processed. Because the data is
	 * selectable, data processing makes sense only if at least one of the items
	 * is selected.<br />
	 * If no item is selected, a warning message is displayed.
	 * 
	 * @param inData
	 *            {@link BeanItemContainer} of <code>ISelectableBean</code>s,
	 *            the data to be tested
	 * @return boolean <code>true</code> if the data is suitable for processing,
	 *         <code>false</code> if no data item is selected and, therefore,
	 *         data processing is nonsensical
	 */
	public static boolean processAction(
			final BeanItemContainer<? extends ISelectableBean> inData) {
		if (!nullSelection(inData)) {
			return true;
		}

		Notification.show(
				Activator.getMessages().getMessage("hint.select.entry"),
				Type.WARNING_MESSAGE);
		return false;
	}

	/**
	 * Convenience function:<br />
	 * Checks the selectable data of null selection.
	 * 
	 * @param inData
	 *            {@link BeanItemContainer} of <code>ISelectableBean</code>s,
	 *            the data to be tested
	 * @return boolean <code>true</code> if none of the selectable data has been
	 *         checked, <code>false</code> if at least one item is selected
	 */
	public static boolean nullSelection(
			final BeanItemContainer<? extends ISelectableBean> inData) {
		for (final ISelectableBean lItem : inData.getItemIds()) {
			if (lItem.isChecked()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the table size. If 0 is returned, paging is disabled and the
	 * table border displayed matches exactly the displayed items in the table.
	 * 
	 * @param inTableSize
	 *            int the amount of items to display in the table
	 * @return int the size to set to the table, e.g. 0
	 */
	public static int getTablePageLength(final int inTableSize) {
		final int lPageSize = 15;
		return inTableSize > lPageSize ? lPageSize : 0;
	}

	/**
	 * Convenience function for table header rows (i.e. to display the column
	 * headers): Transforms an array of message keys to an array containing the
	 * proper messages (in the actual language).
	 * 
	 * @param inKeys
	 *            String[] array containing the message keys
	 * @param inMessages
	 *            {@link IMessages}
	 * @return String[] array containing the i18n messages
	 */
	public static String[] getColumnHeaders(final String[] inKeys,
			final IMessages inMessages) {
		final String[] outHeaders = new String[inKeys.length];
		for (int i = 0; i < inKeys.length; i++) {
			final String lKey = inKeys[i];
			if (lKey.length() == 0) {
				outHeaders[i] = ""; //$NON-NLS-1$
			} else {
				outHeaders[i] = inMessages.getMessage(lKey);
			}
		}
		return outHeaders;
	}

	/**
	 * Helper method to create a text field.
	 * 
	 * @param inModel
	 *            {@link GeneralDomainObject} the model to bind to the input
	 *            field
	 * @param inKey
	 *            String the field's key that has to be bound to the input field
	 * @param inWidth
	 *            int the field width
	 * @return {@link TextField}
	 */
	public static TextField createTextField(final GeneralDomainObject inModel,
			final String inKey, final int inWidth) {
		return createTextField(new BOProperty<String>((DomainObject) inModel,
				inKey, String.class), inWidth);
	}

	/**
	 * Helper method to create a text field.<br/>
	 * Note: this method adds an <code>IntegerValidator</code> by default if the
	 * property passed has a numeric type.
	 * 
	 * @param inProperty
	 *            {@link Property} the Property to be edited with this editor
	 * @param inWidth
	 *            int the field width
	 * @return {@link TextField}
	 */
	public static TextField createTextField(final Property inProperty,
			final int inWidth) {
		final TextField out = new TextField(inProperty);
		// add number validator for number type properties
		if (Number.class.isAssignableFrom(inProperty.getType())) {
			out.addValidator(new IntegerValidator(Activator.getMessages()
					.getMessage("errmsg.error.not.number"))); //$NON-NLS-1$
		}
		out.setWidth(inWidth, Sizeable.Unit.PIXELS);
		out.setStyleName("vif-input"); //$NON-NLS-1$
		return out;
	}

	/**
	 * Helper method to create a text field.
	 * 
	 * @param inContent
	 *            String the field's content
	 * @param inWidth
	 *            int the field width (in pixels)
	 * @param inHeight
	 *            int the field width (in pixels)
	 * @param inValidator
	 *            {@link Validator} the field's validator or <code>null</code>,
	 *            if no validation is applied
	 * @return {@link RichTextArea}
	 */
	public static RichTextArea createTextArea(final String inContent,
			final int inWidth, final int inHeight, final Validator inValidator) {
		final RichTextArea out = new RichTextArea();
		out.setValue(inContent);
		if (inValidator != null) {
			out.addValidator(inValidator);
		}
		out.setWidth(inWidth, Unit.PIXELS);
		out.setHeight(inHeight, Unit.PIXELS);
		out.setStyleName("vif-editor"); //$NON-NLS-1$
		return out;
	}

	/**
	 * Helper method to mark a <code>TextField</code> as required.
	 * 
	 * @param inField
	 *            {@link TextField}
	 * @return {@link TextField} the modified input field
	 */
	@SuppressWarnings("unchecked")
	public static <T extends AbstractTextField> T setRequired(
			final AbstractTextField inField) {
		inField.setRequired(true);
		inField.setImmediate(true);
		inField.setValidationVisible(true);
		return (T) inField;
	}

	/**
	 * Helper method to create check boxes for generated table columns.
	 * 
	 * @param inEntry
	 *            {@link ISelectableBean}
	 * @param inChecker
	 *            {@link IConfirmationModeChecker} an instance checking whether
	 *            the view is in confirmation mode or not, <code>null</code> if
	 *            no such functionality is needed
	 * @return {@link CheckBox}
	 */
	@SuppressWarnings("serial")
	public static CheckBox createCheck(final ISelectableBean inEntry,
			final IConfirmationModeChecker inChecker) {
		final CheckBox out = new CheckBox();
		out.setImmediate(true);
		out.setValue(inEntry.isChecked());
		out.setEnabled(inChecker == null ? true : !inChecker
				.inConfirmationMode());
		out.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent inEvent) {
				inEntry.setChecked(((CheckBox) inEvent.getProperty())
						.getValue());
			}
		});
		return out;
	}

	/**
	 * Adds the specified element in front of the passed array.
	 * 
	 * @param inFirst
	 *            String
	 * @param inSource
	 *            String[]
	 * @return String[]
	 */
	public static String[] getModifiedArray(final String inFirst,
			final String[] inSource) {
		final String[] out = new String[inSource.length + 1];
		out[0] = inFirst;
		System.arraycopy(inSource, 0, out, 1, inSource.length);
		return out;
	}

	// --- inner classes ---

	/**
	 * Helper class to generate a table column containing a
	 * <code>CheckBox</code>. Usage example:
	 * 
	 * <pre>
	 * table.addGeneratedColumn(MemberContainer.MEMBER_CHECK,
	 * 		new VIFViewHelper.CheckBoxColumnGenerator(
	 * 				new VIFViewHelper.IConfirmationModeChecker() {
	 * 					public boolean inConfirmationMode() {
	 * 						return confirmationMode;
	 * 					}
	 * 				}));
	 * </pre>
	 * 
	 * @author Luthiger Created: 16.11.2011
	 */
	@SuppressWarnings("serial")
	public static class CheckBoxColumnGenerator implements
			Table.ColumnGenerator {
		private final IConfirmationModeChecker confirmationModeChecker;

		/**
		 * Usage example:
		 * 
		 * <pre>
		 * table.addGeneratedColumn(MemberContainer.MEMBER_CHECK,
		 * 		new VIFViewHelper.CheckBoxColumnGenerator(
		 * 				new VIFViewHelper.IConfirmationModeChecker() {
		 * 					public boolean inConfirmationMode() {
		 * 						return confirmationMode;
		 * 					}
		 * 				}));
		 * </pre>
		 * 
		 * @param inChecker
		 *            {@link IConfirmationModeChecker}
		 */
		public CheckBoxColumnGenerator(final IConfirmationModeChecker inChecker) {
			confirmationModeChecker = inChecker;
		}

		@Override
		public Object generateCell(final Table inSource, final Object inItemId,
				final Object inColumnId) {
			return createCheck((ISelectableBean) inItemId,
					confirmationModeChecker);
		}
	}

	/**
	 * Interface to indicate whether the view is in confirmation mode or not.<br/>
	 * In confirmation mode, the check box should be displayed disabled.
	 * 
	 * @author Luthiger Created: 16.11.2011
	 */
	public static interface IConfirmationModeChecker {
		/**
		 * @return boolean <code>true</code> if the view is in confirmation mode
		 */
		boolean inConfirmationMode();
	}

}
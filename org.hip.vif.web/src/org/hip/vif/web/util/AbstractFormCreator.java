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

package org.hip.vif.web.util;

import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.web.Activator;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;

/**
 * Base class for form creator classes.
 * 
 * @author Luthiger
 * Created: 10.11.2011
 */
public abstract class AbstractFormCreator {
	private Form form = new Form();
	private IMessages messages = Activator.getMessages();
	
	protected Form getForm() {
		form.setStyleName("vif-form"); //$NON-NLS-1$
		return form;
	}
	
	/**
	 * Delegates the commit to the wrapped form component.
	 */
	public void commit() {
		form.commit();
	}

	/**
	 * Adds a field to the form.
	 * 
	 * @param inFieldID String must be unique on the form
	 * @param inField {@link Field}
	 * @return {@link Field}
	 */
	protected Field addField(String inFieldID, Field inField) {
		form.addField(inFieldID, inField);
		return form.getField(inFieldID);
	}
	
	/**
	 * Adds a required field to the form.
	 * 
	 * @param inFieldID String must be unique on the form
	 * @param inField {@link AbstractField}
	 * @return {@link Field}
	 */
	protected Field addFieldRequired(String inFieldID, AbstractField inField) {
		inField.setRequired(true);
		inField.setImmediate(true);
		return addField(inFieldID, inField);
	}

	/**
	 * Adds a required field to the form.
	 * 
	 * @param inFieldID String must be unique on the form
	 * @param inField {@link AbstractField}
	 * @param inRequiredFieldLbl String the label of the required field, to generate the message <code>The field "FieldName" must not be empty!</code>
	 * @return {@link Field}
	 */
	protected Field addFieldRequired(String inFieldID, AbstractField inField, String inRequiredFieldLbl) {
		inField.setRequiredError(messages.getFormattedMessage("errmsg.error.not.empty", inRequiredFieldLbl)); //$NON-NLS-1$
		return addFieldRequired(inFieldID, inField);
	}
	
	public Form createForm() {
		Form outForm = getForm();
		outForm.getLayout().addComponent(createTable());
		return outForm;
	}
	
	abstract protected Component createTable();

}

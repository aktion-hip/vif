/**
    This package is part of the application VIF.
    Copyright (C) 2014, Benno Luthiger

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

import org.hip.vif.core.util.EmbeddedDBHelper;
import org.hip.vif.web.Activator;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.GenericSelect.IProcessor;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

/** Helper class for views to configure the application.
 *
 * @author lbenno */
public class ConfigViewHelper {
    private static final String KEY_DB_SERVER = "dbServer";
    private static final String KEY_DB_SCHEMA = "dbSchema";
    private static final String KEY_DB_USER = "dbUser";
    private static final String KEY_DB_PASSWD = "dbPasswd";

    private transient final IConfigForm configForm;
    private transient final IMessages messages = Activator.getMessages();

    private transient final String serverField;
    private transient final TextField server;
    private transient final String schemaField;
    private transient final TextField schema;
    private transient final String userField;
    private transient final TextField user;
    private transient final String passwordField;
    private transient final PasswordField password;

    /** ConfigViewHelper constructor.
     *
     * @param inConfigForm {@link IConfigForm} */
    public ConfigViewHelper(final IConfigForm inConfigForm) {
        configForm = inConfigForm;

        serverField = messages.getMessage("config.label.db.server"); //$NON-NLS-1$
        server = createInputField(KEY_DB_SERVER, serverField);
        schemaField = messages.getMessage("config.label.db.schema"); //$NON-NLS-1$
        schema = createInputField(KEY_DB_SCHEMA, schemaField);
        userField = messages.getMessage("config.label.db.user"); //$NON-NLS-1$
        user = createInputField(KEY_DB_USER, userField);
        passwordField = messages.getMessage("config.label.db.password"); //$NON-NLS-1$
        password = createPassword(KEY_DB_PASSWD, passwordField);

    }

    /** Creates an input field on a horizontal layout.
     *
     * @param inField Field&lt;?> the field to place
     * @param inMsgKey String the message key for the field label
     * @param inMessages {@link IMessages}
     * @return {@link HorizontalLayout} */
    public HorizontalLayout createInput(final Field<?> inField, final String inMsgKey,
            final IMessages inMessages) {
        final HorizontalLayout out = new HorizontalLayout();
        out.setSizeFull();
        out.addComponent(inField);

        final Label lDescription = new Label(
                String.format(
                        VIFViewHelper.TMPL_TITLE, "vif-contribution-date", inMessages.getMessage(inMsgKey)), ContentMode.HTML); //$NON-NLS-1$
        out.addComponent(lDescription);
        out.setExpandRatio(lDescription, 1);
        return out;
    }

    /** Creates a text field for the specified bean field.
     *
     * @param inKey String the id of the bean field this text field should be bound to
     * @return {@link TextField} the created text field instance */
    public TextField createInputField(final String inKey) {
        return (TextField) configForm.prepareField(new TextField(), inKey, null);
    }

    /** Creates a text field for the specified bean field.
     *
     * @param inKey String the id of the bean field this text field should be bound to
     * @param inRequiredFieldLbl String the label of the text field, if it's required
     * @return {@link TextField} the created text field instance */
    public TextField createInputField(final String inKey, final String inRequiredFieldLbl) {
        return (TextField) configForm.prepareField(new TextField(), inKey, inRequiredFieldLbl);
    }

    /** Creates a password field for the specified bean field.
     *
     * @param inKey String the id of the bean field this password field should be bound to
     * @param inRequiredFieldLbl String the label of the password field, if it's required
     * @return {@link PasswordField} the created password field instance */
    public PasswordField createPassword(final String inKey, final String inRequiredFieldLbl) {
        return (PasswordField) configForm.prepareField(new PasswordField(), inKey, inRequiredFieldLbl);
    }

    /** Decorates a UI field instance with <code>is required</code> and error indicators.
     *
     * @param inInput AbstractField&lt;?> the field to decorate
     * @param inEnable boolean <code>true</code> to set the field enabled
     * @param inMessages {@link IMessages}
     * @param inFieldName String the field label
     * @return AbstractField&lt;?> */
    private AbstractField<?> decorateField(final AbstractField<?> inInput,
            final boolean inEnable, final IMessages inMessages,
            final String inFieldName) {
        inInput.setRequired(inEnable);
        inInput.setRequiredError(inMessages.getFormattedMessage("errmsg.field.not.empty", inFieldName)); //$NON-NLS-1$
        inInput.setEnabled(inEnable);
        inInput.setImmediate(true);
        return inInput;
    }

    /** Creates a <code>IProcessor</code> for this view.
     *
     * @return IProcessor */
    public IProcessor createProcessor() {
        return new IProcessor() {
            @Override
            public void process(final String inItemID) { // NOPMD
                final boolean lEnabled = !EmbeddedDBHelper.checkEmbedded(inItemID);
                decorateField(server, lEnabled, messages, serverField);
                decorateField(schema, lEnabled, messages, schemaField);
                decorateField(user, lEnabled, messages, userField);
                decorateField(password, lEnabled, messages, passwordField);
            }
        };
    }

    /** @return String the field label */
    public String getServerField() {
        return serverField;
    }

    /** @return {@link TextField} */
    public TextField getServer() {
        return server;
    }

    /** @return String the field label */
    public String getSchemaField() {
        return schemaField;
    }

    /** @return {@link TextField} */
    public TextField getSchema() {
        return schema;
    }

    /** @return String the field label */
    public String getUserField() {
        return userField;
    }

    /** @return {@link TextField} */
    public TextField getUser() {
        return user;
    }

    /** @return String the field label */
    public String getPasswordField() {
        return passwordField;
    }

    /** @return {@link PasswordField} */
    public PasswordField getPassword() {
        return password;
    }

    // ---

    /** Interface for forms on a configuration view. */
    public interface IConfigForm {

        /** Method to prepare the specified field, i.e. to bind it to the form.
         *
         * @param inInput AbstractField&lt;?> the field to prepare
         * @param inKey String the id of the bean field this field should be bound to
         * @param inRequiredFieldLbl String the label of the field, may be empty in case the field is not required
         * @return Field&lt;?> */
        Field<?> prepareField(final AbstractField<?> inInput, final String inKey, final String inRequiredFieldLbl);
    }

}

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
package org.hip.vif.web.components;

import java.io.IOException;

import org.hip.vif.web.Activator;
import org.hip.vif.web.tasks.DBAccessWorkflowItems.ShowConfigPopup;
import org.hip.vif.web.util.ConfigViewHelper;
import org.hip.vif.web.util.ConfigViewHelper.IConfigForm;
import org.hip.vif.web.util.ConfigurationItem;
import org.hip.vif.web.util.DBDriverSelect;
import org.hip.vif.web.util.DBDriverSelect.DBDriverBean;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.AbstractFormCreator;
import org.ripla.web.util.LabelValueTable;
import org.ripla.web.util.Popup;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.VerticalLayout;

/** Child window displaying the field for that the SU can provide the information for initial DB access configuration.
 *
 * @author lbenno */
public class DBConfigurationPopup extends AbstractConfigurationPopup {
    private static final int WIDTH = 300;

    /** DBConfigurationPopup constructor.
     *
     * @param inShowConfigPopup {@link ShowConfigPopup} the workflow item
     * @throws IOException */
    public DBConfigurationPopup(final ShowConfigPopup inShowConfigPopup) throws IOException { // NOPMD
        super();
        final VerticalLayout lLayout = createLayout();
        lLayout.addComponent(getDBAccessConfig(ConfigurationItem.createConfiguration(), inShowConfigPopup));
        Popup.displayPopup(Activator.getMessages().getMessage("config.label.db.title"), lLayout, 670, 270);
    }

    @SuppressWarnings("serial")
    private VerticalLayout getDBAccessConfig(final ConfigurationItem inConfiguration, final ShowConfigPopup inController) {
        final IMessages lMessages = Activator.getMessages();

        final VerticalLayout outLayout = new VerticalLayout();

        final FormCreator lForm = new FormCreator(inConfiguration);
        outLayout.addComponent(lForm.createForm());

        final Button lSave = new Button(lMessages.getMessage("config.button.save")); //$NON-NLS-1$
        lSave.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) { // NOPMD
                try {
                    lForm.commit();
                    inController.save(inConfiguration);
                } catch (final CommitException exc) { // NOPMD
                    // intentionally left empty
                }
            }
        });
        lSave.setClickShortcut(KeyCode.ENTER);
        lSave.setImmediate(true);
        outLayout.addComponent(lSave);

        return outLayout;
    }

    // ---

    private class FormCreator extends AbstractFormCreator implements IConfigForm { // NOPMD

        /** @param inItem */
        public FormCreator(final ConfigurationItem inConfiguration) {
            super(new BeanItem<ConfigurationBean>(new ConfigurationBean(inConfiguration)));
        }

        @Override
        protected Component createTable() { // NOPMD
            final IMessages lMessages = Activator.getMessages();
            final LabelValueTable outTable = new LabelValueTable();

            final ConfigViewHelper lViewHelper = new ConfigViewHelper(this);
            final ComboBox lSelect = DBDriverSelect.getDBDriverSelection(WIDTH, false, lViewHelper.createProcessor());
            addField(ConfigurationBean.KEY_DB_DRIVER, lSelect);
            outTable.addRow(
                    lMessages.getMessage("config.label.db.driver"), lViewHelper.createInput(lSelect, "config.desc.db.driver", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            outTable.addRow(lViewHelper.getServerField(), lViewHelper.getServer());
            outTable.addRow(lViewHelper.getSchemaField(), lViewHelper.getSchema());
            outTable.addRow(lViewHelper.getUserField(), lViewHelper.getUser());
            outTable.addRow(lViewHelper.getPasswordField(), lViewHelper.getPassword());
            outTable.addEmtpyRow();

            return outTable;
        }

        @Override
        public Field<?> prepareField(final AbstractField<?> inInput, final String inKey, // NOPMD
                final String inRequiredFieldLbl) {
            inInput.setWidth(WIDTH, Unit.PIXELS);
            inInput.setStyleName("vif-input-config"); //$NON-NLS-1$
            if (inRequiredFieldLbl == null) {
                addField(inKey, inInput);
            }
            else {
                addFieldRequired(inKey, inInput, inRequiredFieldLbl);
            }
            return inInput;
        }
    }

    /** The bean wrapping the configuration. */
    public static class ConfigurationBean {
        protected static final String KEY_DB_DRIVER = "dbDriver";
        // protected static final String KEY_DB_SERVER = "dbServer";
        // protected static final String KEY_DB_SCHEMA = "dbSchema";
        // protected static final String KEY_DB_USER = "dbUser";
        // protected static final String KEY_DB_PASSWD = "dbPasswd";

        private transient final ConfigurationItem configuration;

        /** ConfigurationBean constructor.
         *
         * @param inConfiguration {@link ConfigurationItem} the wrapped configuration */
        protected ConfigurationBean(final ConfigurationItem inConfiguration) {
            configuration = inConfiguration;
        }

        private String getPropertyValue(final String inId) {
            return configuration.getItemProperty(inId).getValue().toString();
        }

        @SuppressWarnings("unchecked")
        private void setPropertyValue(final String inId, final String inValue) {
            configuration.getItemProperty(inId).setValue(inValue);
        }

        public DBDriverBean getDbDriver() { // NOPMD
            return DBDriverSelect.createDriverBean(getPropertyValue(ConfigurationItem.PropertyDef.DB_DRIVER.getPID()));
        }

        public void setDbDriver(final DBDriverBean inValue) { // NOPMD
            setPropertyValue(ConfigurationItem.PropertyDef.DB_DRIVER.getPID(), inValue == null ? "" : inValue.getID());
        }

        public String getDbServer() { // NOPMD
            return getPropertyValue(ConfigurationItem.PropertyDef.DB_SERVER.getPID());
        }

        public void setDbServer(final String inValue) { // NOPMD
            setPropertyValue(ConfigurationItem.PropertyDef.DB_SERVER.getPID(), inValue);
        }

        public String getDbSchema() { // NOPMD
            return getPropertyValue(ConfigurationItem.PropertyDef.DB_SCHEMA.getPID());
        }

        public void setDbSchema(final String inValue) { // NOPMD
            setPropertyValue(ConfigurationItem.PropertyDef.DB_SCHEMA.getPID(), inValue);
        }

        public String getDbUser() { // NOPMD
            return getPropertyValue(ConfigurationItem.PropertyDef.DB_USER.getPID());
        }

        public void setDbUser(final String inValue) { // NOPMD
            setPropertyValue(ConfigurationItem.PropertyDef.DB_USER.getPID(), inValue);
        }

        public String getDbPasswd() { // NOPMD
            return getPropertyValue(ConfigurationItem.PropertyDef.DB_PASSWD.getPID());
        }
    }

}

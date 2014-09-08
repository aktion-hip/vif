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
package org.hip.vif.admin.admin.ui;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.hip.vif.admin.admin.Activator;
import org.hip.vif.admin.admin.tasks.ConfigTask;
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.service.MemberUtility;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.core.util.EmbeddedDBHelper;
import org.hip.vif.web.tasks.LanguageSelect;
import org.hip.vif.web.util.ConfigurableSelect;
import org.hip.vif.web.util.ConfigurationItem;
import org.hip.vif.web.util.DBDriverSelect;
import org.hip.vif.web.util.VIFViewHelper;
import org.osgi.framework.FrameworkUtil;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.GenericSelect;
import org.ripla.web.util.GenericSelect.IProcessor;
import org.ripla.web.util.LabelValueTable;
import org.ripla.web.util.Popup;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/** Displays the configuration properties in edit mode.
 *
 * @author Luthiger Created: 06.01.2012 */
@SuppressWarnings("serial")
public class ConfigView extends AbstractAdminView {
    private static final int WIDTH = 300;

    private int counter = 0;

    /** Constructor
     *
     * @param inConfiguration {@link ConfigurationItem} the wrapper item for the configuration
     * @param inTask {@link ConfigTask}
     * @throws IOException */
    @SuppressWarnings("unchecked")
    public ConfigView(final ConfigurationItem inConfiguration,
            final ConfigTask inTask) throws IOException {
        counter = 0;
        final IMessages lMessages = Activator.getMessages();
        final VerticalLayout lLayout = initLayout(lMessages, "admin.config.title.page"); //$NON-NLS-1$
        final LabelValueTable lTable = new LabelValueTable();

        final Form lForm = new Form();
        lForm.setValidationVisible(true);

        new FieldGroup();

        // localization
        lTable.addRow(createSubtitle("admin.config.sub.localization", lMessages)); //$NON-NLS-1$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.lang.app"), createInput(LanguageSelect.getLanguageSelection(inConfiguration.getItemProperty(ConfigurationItem.PropertyDef.LANGUAGE_DEFAULT.getPID())), "admin.config.desc.lang.app", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.lang.content"), createInput(LanguageSelect.getLanguageSelection(inConfiguration.getItemProperty(ConfigurationItem.PropertyDef.LANGUAGE_CONTENT.getPID())), "admin.config.desc.lang.content", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        final TextField lDatePattern = createInput(
                ConfigurationItem.PropertyDef.DATE_PATTERN, inConfiguration);
        lDatePattern.addValidator(new DatePatternValidator(lMessages
                .getMessage("errmsg.admin.config.valid.date"))); //$NON-NLS-1$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.date.pattern"), createInput(lDatePattern, "admin.config.desc.date.pattern", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$

        // database connection
        lTable.addRow(createSubtitle("admin.config.sub.database", lMessages)); //$NON-NLS-1$

        final TextField lServer = createInput(
                ConfigurationItem.PropertyDef.DB_SERVER, inConfiguration);
        final String lServerField = lMessages
                .getMessage("admin.config.label.db.server"); //$NON-NLS-1$
        final TextField lSchema = createInput(
                ConfigurationItem.PropertyDef.DB_SCHEMA, inConfiguration);
        final String lSchemaField = lMessages
                .getMessage("admin.config.label.db.schema"); //$NON-NLS-1$
        final TextField lUser = createInput(
                ConfigurationItem.PropertyDef.DB_USER, inConfiguration);
        final String lUserField = lMessages
                .getMessage("admin.config.label.db.user"); //$NON-NLS-1$
        final PasswordField lPassword = createPassword(
                ConfigurationItem.PropertyDef.DB_PASSWD, inConfiguration);
        final String lPasswordField = lMessages
                .getMessage("admin.config.label.db.password"); //$NON-NLS-1$

        final IProcessor lProcessor = new IProcessor() {
            @Override
            public void process(final String inItemID) {
                final boolean lEnabled = !EmbeddedDBHelper
                        .checkEmbedded(inItemID);
                decorateField(lServer, lEnabled, lMessages, lServerField);
                decorateField(lSchema, lEnabled, lMessages, lSchemaField);
                decorateField(lUser, lEnabled, lMessages, lUserField);
                decorateField(lPassword, lEnabled, lMessages, lPasswordField);
            }
        };
        final Property<String> lDriver = inConfiguration
                .getItemProperty(ConfigurationItem.PropertyDef.DB_DRIVER
                        .getPID());
        final ComboBox lSelect = DBDriverSelect.getDBDriverSelection(lDriver,
                WIDTH, false, lProcessor);

        lTable.addRow(
                lMessages.getMessage("admin.config.label.db.driver"), createInput(lSelect, "admin.config.desc.db.driver", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        final boolean lEnabled = !EmbeddedDBHelper.checkEmbedded(lDriver
                .getValue().toString());
        lTable.addRow(
                lServerField,
                addToForm(
                        decorateField(lServer, lEnabled, lMessages,
                                lServerField), lForm));
        lTable.addRow(
                lSchemaField,
                addToForm(
                        decorateField(lSchema, lEnabled, lMessages,
                                lSchemaField), lForm));
        lTable.addRow(
                lUserField,
                addToForm(
                        decorateField(lUser, lEnabled, lMessages, lUserField),
                        lForm));
        lTable.addRow(
                lPasswordField,
                addToForm(
                        decorateField(lPassword, lEnabled, lMessages,
                                lPasswordField), lForm));

        lTable.addRow(createSubSubtitle("admin.config.sub.database.ext", lMessages)); //$NON-NLS-1$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.dbx.driver"), createInput(DBDriverSelect.getDBDriverSelection(inConfiguration.getItemProperty(ConfigurationItem.PropertyDef.DBX_DRIVER.getPID()), WIDTH, true, null), "admin.config.desc.db.driver", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.dbx.server"), createInput(ConfigurationItem.PropertyDef.DBX_SERVER, inConfiguration, "admin.config.desc.db.external", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.db.schema"), createInput(ConfigurationItem.PropertyDef.DBX_SCHEMA, inConfiguration, "admin.config.desc.db.external", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.db.user"), createInput(ConfigurationItem.PropertyDef.DBX_USER, inConfiguration, "admin.config.desc.db.external", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.db.password"), createPassword(ConfigurationItem.PropertyDef.DBX_PASSWD, inConfiguration, "admin.config.desc.db.external", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        lTable.addRow(createSubSubtitle("admin.config.sub.database.ldap", lMessages)); //$NON-NLS-1$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.ldap.url"), createInput(ConfigurationItem.PropertyDef.LDAP_URL, inConfiguration, "admin.config.desc.ldap", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.ldap.dn"), createInput(ConfigurationItem.PropertyDef.LDAP_MANAGER_DN, inConfiguration, "admin.config.desc.ldap", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.ldap.passwrd"), createPassword(ConfigurationItem.PropertyDef.LDAP_MANAGER_PW, inConfiguration, "admin.config.desc.ldap", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$

        // searching and authentication
        lTable.addRow(createSubtitle("admin.config.sub.searchers", lMessages)); //$NON-NLS-1$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.search"), createInput(ConfigurableSelect.getSelect(MemberUtility.INSTANCE.getContributionNames(), //$NON-NLS-1$
                        inConfiguration
                        .getItemProperty(ConfigurationItem.PropertyDef.MEMBER_SEARCHER
                                .getPID()), WIDTH),
                                "admin.config.desc.search", lMessages, lForm)); //$NON-NLS-1$

        // mail and notification
        lTable.addRow(createSubtitle("admin.config.sub.mail", lMessages)); //$NON-NLS-1$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.mail.term"), createInput(ConfigurationItem.PropertyDef.FORUM_NAME, inConfiguration, "admin.config.desc.mail.term", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.mail.host"), createInput(ConfigurationItem.PropertyDef.MAIL_HOST, inConfiguration, "admin.config.desc.mail.host", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.mail.address"), createInput(ConfigurationItem.PropertyDef.MAIL_ADDRESS, inConfiguration, "admin.config.desc.mail.address", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.mail.subj.id"), createInput(ConfigurationItem.PropertyDef.MAIL_SUBJECT_ID, inConfiguration, "admin.config.desc.mail.subj.id", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.mail.subj.txt"), createInput(ConfigurationItem.PropertyDef.MAIL_SUBJECT_TEXT, inConfiguration, "admin.config.desc.mail.subj.txt", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.mail.sender"), createInput(ConfigurationItem.PropertyDef.MAIL_NAMING, inConfiguration, "admin.config.desc.mail.sender", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$

        if (!PreferencesHandler.INSTANCE.isEmbedded()) {
            // logging
            lTable.addRow(createSubtitle("admin.config.sub.logging", lMessages)); //$NON-NLS-1$
            lTable.addRow(
                    lMessages.getMessage("admin.config.label.logging.path"), createInput(ConfigurationItem.PropertyDef.LOG_PATH, inConfiguration, "admin.config.desc.logging.path", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
            lTable.addRow(
                    lMessages.getMessage("admin.config.label.logging.level"), //$NON-NLS-1$
                    createInput(
                            GenericSelect.getSelection(
                                    inConfiguration
                                    .getItemProperty(ConfigurationItem.PropertyDef.LOG_LEVEL
                                            .getPID()),
                                            GenericSelect
                                            .toCollection(ApplicationConstants.LOG_LEVELS),
                                            WIDTH, false, null), "admin.config.desc.logging.level", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
            lTable.addRow(
                    lMessages.getMessage("admin.config.label.logging.config"), createInput(ConfigurationItem.PropertyDef.LOG_CONFIG, inConfiguration, "admin.config.desc.logging.config", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // various
        lTable.addRow(createSubtitle("admin.config.sub.sundry", lMessages)); //$NON-NLS-1$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.url.logout"), createInput(ConfigurationItem.PropertyDef.LOGOUT_URL, inConfiguration, "admin.config.desc.url.logout", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        final TextField lUpload = createInput(
                ConfigurationItem.PropertyDef.UPLOAD_QUOTA, inConfiguration);
        // TODO
        // lUpload.addValidator(new IntegerValidator(lMessages
        //				.getMessage("errmsg.admin.config.valid.int"))); //$NON-NLS-1$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.div.quota"), createInput(lUpload, "admin.config.desc.div.quota", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        final TextField lLatency = createInput(
                ConfigurationItem.PropertyDef.LATENCY_DAYS, inConfiguration);
        // lLatency.addValidator(new IntegerValidator(lMessages
        //				.getMessage("errmsg.admin.config.valid.int"))); //$NON-NLS-1$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.div.latency"), createInput(lLatency, "admin.config.desc.div.latency", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.docs.root"), createInput(ConfigurationItem.PropertyDef.DOCS_ROOT, inConfiguration, "admin.config.desc.docs.root", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.div.guests"), createInput(createCheck(inConfiguration.getItemProperty(ConfigurationItem.PropertyDef.GUEST_ALLOW.getPID())), "admin.config.desc.div.guests", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
        lTable.addRow(
                lMessages.getMessage("admin.config.label.div.password"), createInput(createCheck(inConfiguration.getItemProperty(ConfigurationItem.PropertyDef.PW_DISPLAY.getPID())), "admin.config.desc.div.password", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$

        lForm.getLayout().addComponent(lTable);
        lLayout.addComponent(lForm);
        lLayout.addComponent(RiplaViewHelper.createSpacer());

        final Button lSave = new Button(
                lMessages.getMessage("admin.config.button.save")); //$NON-NLS-1$
        lSave.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                try {
                    lForm.commit();
                    if (inTask.save(inConfiguration)) {
                        Notification.show(
                                lMessages
                                .getMessage("admin.config.feedback.save"), Type.TRAY_NOTIFICATION); //$NON-NLS-1$
                    }
                }
                catch (final InvalidValueException exc) {
                    // intentionally left empty
                }
            }
        });

        final Button lSmoke = new Button(
                lMessages.getMessage("admin.config.button.environment")); //$NON-NLS-1$
        lSmoke.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                Popup.displayPopup(
                        lMessages.getMessage("admin.config.popup.title"), getSmoke(lMessages), 1000, 600); //$NON-NLS-1$
            }
        });

        lLayout.addComponent(RiplaViewHelper.createButtons(lSave, lSmoke));
    }

    protected Layout getSmoke(final IMessages inMessages) {
        final VerticalLayout out = new VerticalLayout();
        final LabelValueTable lValues = new LabelValueTable();
        lValues.addRow(new Label(
                String.format(VIFViewHelper.TMPL_TITLE, "vif-title", "Application VIF"), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$
        lValues.addRow("Release:", getVersion()); //$NON-NLS-1$ //$NON-NLS-2$
        lValues.addRow(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-title", "Java"), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$
        lValues.addRow("Vendor:", System.getProperty("java.vendor")); //$NON-NLS-1$ //$NON-NLS-2$
        lValues.addRow("Version:", System.getProperty("java.version")); //$NON-NLS-1$ //$NON-NLS-2$
        lValues.addRow("VM&nbsp;Version:", System.getProperty("java.vm.version")); //$NON-NLS-1$ //$NON-NLS-2$
        lValues.addRow("Runtime&nbsp;Version:", System.getProperty("java.runtime.version")); //$NON-NLS-1$ //$NON-NLS-2$
        lValues.addRow("Java Home:", System.getProperty("java.home")); //$NON-NLS-1$ //$NON-NLS-2$
        lValues.addRow("Class Path:", System.getProperty("java.class.path")); //$NON-NLS-1$ //$NON-NLS-2$
        lValues.addRow("Library&nbsp;Path:", System.getProperty("java.library.path")); //$NON-NLS-1$ //$NON-NLS-2$
        lValues.addRow("OS:", System.getProperty("os.name")); //$NON-NLS-1$ //$NON-NLS-2$
        lValues.addRow("Working&nbsp;Directory:", System.getProperty("user.dir")); //$NON-NLS-1$ //$NON-NLS-2$
        lValues.addRow("Endorsed&nbsp;Directory:", System.getProperty("java.endorsed.dirs")); //$NON-NLS-1$ //$NON-NLS-2$
        out.addComponent(lValues);
        return out;
    }

    private String getVersion() {
        final String lVersion = FrameworkUtil
                .getBundle(ConfigurationItem.class).getHeaders()
                .get("Bundle-Version"); //$NON-NLS-1$
        return lVersion == null ? "Release ?.?.?" : lVersion.toString(); //$NON-NLS-1$
    }

    private Label createSubtitle(final String inMsgKey,
            final IMessages inMessages) {
        return new Label(
                String.format(VIFViewHelper.TMPL_TITLE, "vif-title", inMessages.getMessage(inMsgKey)), ContentMode.HTML); //$NON-NLS-1$
    }

    private Label createSubSubtitle(final String inMsgKey,
            final IMessages inMessages) {
        return new Label(
                String.format(VIFViewHelper.TMPL_TITLE, "vif-emphasized", inMessages.getMessage(inMsgKey)), ContentMode.HTML); //$NON-NLS-1$
    }

    private TextField createInput(
            final ConfigurationItem.PropertyDef inPropertyDef,
            final ConfigurationItem inConfiguration) {
        final TextField out = new TextField(
                inConfiguration.getItemProperty(inPropertyDef.getPID()));
        out.setWidth(WIDTH, Unit.PIXELS);
        out.setStyleName("vif-input-config"); //$NON-NLS-1$
        return out;
    }

    private HorizontalLayout createInput(
            final ConfigurationItem.PropertyDef inPropertyDef,
            final ConfigurationItem inConfiguration,
            final String inDescription, final IMessages inMessages,
            final Form inForm) {
        return createInput(createInput(inPropertyDef, inConfiguration),
                inDescription, inMessages, inForm);
    }

    private PasswordField createPassword(
            final ConfigurationItem.PropertyDef inPropertyDef,
            final ConfigurationItem inConfiguration) {
        final PasswordField out = new PasswordField(
                inConfiguration.getItemProperty(inPropertyDef.getPID()));
        out.setWidth(WIDTH, Unit.PIXELS);
        out.setStyleName("vif-input-config"); //$NON-NLS-1$
        return out;
    }

    private HorizontalLayout createPassword(
            final ConfigurationItem.PropertyDef inPropertyDef,
            final ConfigurationItem inConfiguration,
            final String inDescription, final IMessages inMessages,
            final Form inForm) {
        return createInput(createPassword(inPropertyDef, inConfiguration),
                inDescription, inMessages, inForm);
    }

    private HorizontalLayout createInput(final AbstractField<? extends Object> inInput,
            final String inMsgKey, final IMessages inMessages, final Form inForm) {
        final HorizontalLayout out = new HorizontalLayout();
        out.setSizeFull();
        inForm.addField(String.valueOf(++counter), inInput);
        out.addComponent(inInput);

        final Label lDescription = new Label(
                String.format(
                        VIFViewHelper.TMPL_TITLE, "vif-contribution-date", inMessages.getMessage(inMsgKey)), ContentMode.HTML); //$NON-NLS-1$
        out.addComponent(lDescription);
        out.setExpandRatio(lDescription, 1);
        return out;
    }

    private AbstractField addToForm(final AbstractField inInput,
            final Form inForm) {
        inForm.addField(String.valueOf(++counter), inInput);
        return inInput;
    }

    private CheckBox createCheck(final Property inProperty) {
        final CheckBox out = new CheckBox();
        out.setValue(Boolean.parseBoolean(inProperty.getValue().toString()));
        out.setImmediate(true);
        out.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent inEvent) {
                inProperty
                .setValue(inEvent.getProperty().getValue().toString());
            }
        });
        return out;
    }

    private AbstractField decorateField(final AbstractField inInput,
            final boolean inEnable, final IMessages inMessages,
            final String inFieldName) {
        inInput.setRequired(inEnable);
        inInput.setRequiredError(inMessages.getFormattedMessage("errmsg.field.not.empty", inFieldName)); //$NON-NLS-1$
        inInput.setEnabled(inEnable);
        inInput.setImmediate(true);
        return inInput;
    }

    // ---

    private static class DatePatternValidator extends AbstractValidator<String> {
        DatePatternValidator(final String inMessage) {
            super(inMessage);
        }

        @Override
        protected boolean isValidValue(final String inValue) {
            try {
                new SimpleDateFormat(inValue);
                return true;
            } catch (final Exception exc) {
                // intentionally left empty
            }
            return false;
        }

        @Override
        public Class<String> getType() {
            return String.class;
        }

    }

}

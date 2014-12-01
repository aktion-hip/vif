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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hip.vif.admin.admin.Activator;
import org.hip.vif.admin.admin.tasks.ConfigTask;
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.service.MemberUtility;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.core.util.EmbeddedDBHelper;
import org.hip.vif.web.util.ConfigurationItem;
import org.hip.vif.web.util.DBDriverSelect;
import org.hip.vif.web.util.DBDriverSelect.DBDriverBean;
import org.hip.vif.web.util.LanguageSelectHelper;
import org.hip.vif.web.util.VIFViewHelper;
import org.osgi.framework.FrameworkUtil;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.AbstractFormCreator;
import org.ripla.web.util.GenericSelect.IProcessor;
import org.ripla.web.util.LabelValueTable;
import org.ripla.web.util.Popup;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
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
 * @author Luthiger */
@SuppressWarnings("serial")
public class ConfigView extends AbstractAdminView {
    private static final int WIDTH = 300;

    /** ConfigView constructor
     *
     * @param inConfiguration {@link ConfigurationItem} the wrapper item for the configuration
     * @param inTask {@link ConfigTask}
     * @throws IOException */
    public ConfigView(final ConfigurationItem inConfiguration, final ConfigTask inTask) throws IOException {
        final IMessages lMessages = Activator.getMessages();
        final VerticalLayout lLayout = initLayout(lMessages, "admin.config.title.page"); //$NON-NLS-1$

        final FormCreator lForm = new FormCreator(inConfiguration);
        lLayout.addComponent(lForm.createForm());

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
                                lMessages.getMessage("admin.config.feedback.save"), Type.TRAY_NOTIFICATION); //$NON-NLS-1$
                    }
                } catch (final CommitException exc) {
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

    private static class FormCreator extends AbstractFormCreator {

        public FormCreator(final ConfigurationItem inConfiguration) {
            super(new BeanItem<InputWrapper>(new InputWrapper(inConfiguration)));
        }

        @Override
        protected Component createTable() {
            final IMessages lMessages = Activator.getMessages();
            final LabelValueTable outTable = new LabelValueTable();

            // localization
            outTable.addRow(createSubtitle("admin.config.sub.localization", lMessages)); //$NON-NLS-1$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.lang.app"),
                    createInput(createLanguageSelect(InputWrapper.KEY_LANG_DFT), "admin.config.desc.lang.app",
                            lMessages));
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.lang.content"),
                    createInput(createLanguageSelect(InputWrapper.KEY_LANG_CONTENT), "admin.config.desc.lang.content",
                            lMessages));
            final TextField lDatePattern = createInputField(InputWrapper.KEY_DATE_PATTERN, null);
            lDatePattern.addValidator(new DatePatternValidator(lMessages
                    .getMessage("errmsg.admin.config.valid.date"))); //$NON-NLS-1$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.date.pattern"), createInput(lDatePattern, "admin.config.desc.date.pattern", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$

            // database connection
            outTable.addRow(createSubtitle("admin.config.sub.database", lMessages)); //$NON-NLS-1$

            final String lServerField = lMessages
                    .getMessage("admin.config.label.db.server"); //$NON-NLS-1$
            final TextField lServer = createInputField(InputWrapper.KEY_DB_SERVER, lServerField);
            final String lSchemaField = lMessages
                    .getMessage("admin.config.label.db.schema"); //$NON-NLS-1$
            final TextField lSchema = createInputField(InputWrapper.KEY_DB_SCHEMA, lSchemaField);
            final String lUserField = lMessages
                    .getMessage("admin.config.label.db.user"); //$NON-NLS-1$
            final TextField lUser = createInputField(InputWrapper.KEY_DB_USER, lUserField);
            final String lPasswordField = lMessages
                    .getMessage("admin.config.label.db.password"); //$NON-NLS-1$
            final PasswordField lPassword = createPassword(InputWrapper.KEY_DB_PASSWD, lPasswordField);

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
            final ComboBox lSelect = DBDriverSelect.getDBDriverSelection(WIDTH, false, lProcessor);
            addField(InputWrapper.KEY_DB_DRIVER, lSelect);
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.db.driver"), createInput(lSelect, "admin.config.desc.db.driver", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            outTable.addRow(lServerField, lServer);
            outTable.addRow(lSchemaField, lSchema);
            outTable.addRow(lUserField, lUser);
            outTable.addRow(lPasswordField, lPassword);

            outTable.addRow(createSubSubtitle("admin.config.sub.database.ext", lMessages)); //$NON-NLS-1$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.dbx.driver"), createInput(addField(InputWrapper.KEY_DBX_DRIVER, DBDriverSelect.getDBDriverSelection(WIDTH, true, null)), "admin.config.desc.db.driver", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.dbx.server"), createInput(createInputField(InputWrapper.KEY_DBX_SERVER), "admin.config.desc.db.external", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.db.schema"), createInput(createInputField(InputWrapper.KEY_DBX_SCHEMA), "admin.config.desc.db.external", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.db.user"), createInput(createInputField(InputWrapper.KEY_DBX_USER), "admin.config.desc.db.external", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.db.password"), createInput(createPassword(InputWrapper.KEY_DBX_PASSWD, null), "admin.config.desc.db.external", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$

            outTable.addRow(createSubSubtitle("admin.config.sub.database.ldap", lMessages)); //$NON-NLS-1$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.ldap.url"), createInput(createInputField(InputWrapper.KEY_LDAP_URL), "admin.config.desc.ldap", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.ldap.dn"), createInput(createInputField(InputWrapper.KEY_LDAP_MANAGER_DN), "admin.config.desc.ldap", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.ldap.passwrd"), createInput(createPassword(InputWrapper.KEY_LDAP_MANAGER_PW, null), "admin.config.desc.ldap", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$

            // searching and authentication
            outTable.addRow(createSubtitle("admin.config.sub.searchers", lMessages)); //$NON-NLS-1$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.search"), createInput(createSelect(InputWrapper.KEY_MEMBER_SEARCHER, MemberUtility.INSTANCE.getContributionNames(), WIDTH, false), "admin.config.desc.search", lMessages)); //$NON-NLS-1$

            // mail and notification
            outTable.addRow(createSubtitle("admin.config.sub.mail", lMessages)); //$NON-NLS-1$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.mail.term"), createInput(createInputField(InputWrapper.KEY_FORUM_NAME), "admin.config.desc.mail.term", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.mail.host"), createInput(createInputField(InputWrapper.KEY_MAIL_HOST), "admin.config.desc.mail.host", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.mail.address"), createInput(createInputField(InputWrapper.KEY_MAIL_ADDRESS), "admin.config.desc.mail.address", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.mail.subj.id"), createInput(createInputField(InputWrapper.KEY_MAIL_SUBJECT_ID), "admin.config.desc.mail.subj.id", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.mail.subj.txt"), createInput(createInputField(InputWrapper.KEY_MAIL_SUBJECT_TEXT), "admin.config.desc.mail.subj.txt", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.mail.sender"), createInput(createInputField(InputWrapper.KEY_MAIL_NAMING), "admin.config.desc.mail.sender", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$

            if (!PreferencesHandler.INSTANCE.isEmbedded()) {
                // logging
                outTable.addRow(createSubtitle("admin.config.sub.logging", lMessages)); //$NON-NLS-1$
                outTable.addRow(
                        lMessages.getMessage("admin.config.label.logging.path"), createInput(createInputField(InputWrapper.KEY_LOG_PATH), "admin.config.desc.logging.path", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
                outTable.addRow(
                        lMessages.getMessage("admin.config.label.logging.level"), //$NON-NLS-1$
                        createInput(
                                createSelect(InputWrapper.KEY_LOG_LEVEL, ApplicationConstants.LOG_LEVELS, WIDTH, false),
                                "admin.config.desc.logging.level", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
                outTable.addRow(
                        lMessages.getMessage("admin.config.label.logging.config"), createInput(createInputField(InputWrapper.KEY_LOG_CONFIG), "admin.config.desc.logging.config", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            }

            // various
            outTable.addRow(createSubtitle("admin.config.sub.sundry", lMessages)); //$NON-NLS-1$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.url.logout"), createInput(createInputField(InputWrapper.KEY_LOGOUT_URL), "admin.config.desc.url.logout", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            final TextField lUpload = createInputField(InputWrapper.KEY_UPLOAD_QUOTA);
            lUpload.setConverter(Integer.class);
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.div.quota"), createInput(lUpload, "admin.config.desc.div.quota", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            final TextField lLatency = createInputField(InputWrapper.KEY_LATENCY_DAYS);
            lLatency.setConverter(Integer.class);
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.div.latency"), createInput(lLatency, "admin.config.desc.div.latency", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.docs.root"), createInput(createInputField(InputWrapper.KEY_DOCS_ROOT), "admin.config.desc.docs.root", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.div.guests"), createInput(createCheck(InputWrapper.KEY_GUEST_ALLOW), "admin.config.desc.div.guests", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$
            outTable.addRow(
                    lMessages.getMessage("admin.config.label.div.password"), createInput(createCheck(InputWrapper.KEY_PW_DISPLAY), "admin.config.desc.div.password", lMessages)); //$NON-NLS-1$ //$NON-NLS-2$

            return outTable;
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

        private CheckBox createCheck(final String inKey) {
            final CheckBox out = new CheckBox();
            out.setImmediate(true);
            addField(inKey, out);
            return out;
        }

        private TextField createInputField(final String inKey) {
            return (TextField) prepareField(new TextField(), inKey, null);
        }

        private TextField createInputField(final String inKey, final String inRequiredFieldLbl) {
            return (TextField) prepareField(new TextField(), inKey, inRequiredFieldLbl);
        }

        private PasswordField createPassword(final String inKey, final String inRequiredFieldLbl) {
            return (PasswordField) prepareField(new PasswordField(), inKey, inRequiredFieldLbl);
        }

        private Field<?> prepareField(final AbstractField<?> inInput, final String inKey,
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

        private AbstractField<?> decorateField(final AbstractField<?> inInput,
                final boolean inEnable, final IMessages inMessages,
                final String inFieldName) {
            inInput.setRequired(inEnable);
            inInput.setRequiredError(inMessages.getFormattedMessage("errmsg.field.not.empty", inFieldName)); //$NON-NLS-1$
            inInput.setEnabled(inEnable);
            inInput.setImmediate(true);
            return inInput;
        }

        private ComboBox createLanguageSelect(final String inKey) {
            return createSelect(inKey, LanguageSelectHelper.getLanguages(), 55, false);
        }

        private ComboBox createSelect(final String inKey, final String[] inItems, final int inWidth,
                final boolean inNullSelectionAllowed) {
            final List<String> lItems = new ArrayList<String>(inItems.length);
            Collections.addAll(lItems, inItems);
            return createSelect(inKey, lItems, inWidth, inNullSelectionAllowed);
        }

        private ComboBox createSelect(final String inKey, final List<String> inItems, final int inWidth,
                final boolean inNullSelectionAllowed) {
            final ComboBox outSelect = new ComboBox(null, inItems);
            outSelect.setStyleName("vif-select"); //$NON-NLS-1$
            outSelect.setWidth(inWidth, Unit.PIXELS);
            outSelect.setNullSelectionAllowed(inNullSelectionAllowed);
            outSelect.setImmediate(true);
            addField(inKey, outSelect);
            return outSelect;
        }

        private HorizontalLayout createInput(final Field<?> inField, final String inMsgKey,
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

    }

    public static class InputWrapper {
        protected static final String KEY_LANG_DFT = "languageDefault";
        protected static final String KEY_LANG_CONTENT = "languageContent";
        protected static final String KEY_DATE_PATTERN = "datePattern";
        protected static final String KEY_DB_DRIVER = "dbDriver";
        protected static final String KEY_DB_SERVER = "dbServer";
        protected static final String KEY_DB_SCHEMA = "dbSchema";
        protected static final String KEY_DB_USER = "dbUser";
        protected static final String KEY_DB_PASSWD = "dbPasswd";
        protected static final String KEY_DBX_DRIVER = "dbxDriver";
        protected static final String KEY_DBX_SERVER = "dbxServer";
        protected static final String KEY_DBX_SCHEMA = "dbxSchema";
        protected static final String KEY_DBX_USER = "dbxUser";
        protected static final String KEY_DBX_PASSWD = "dbxPasswd";
        protected static final String KEY_LDAP_URL = "ldapUrl";
        protected static final String KEY_LDAP_MANAGER_DN = "ldapManagerDn";
        protected static final String KEY_LDAP_MANAGER_PW = "ldapManagerPw";
        protected static final String KEY_MEMBER_SEARCHER = "memberSearcher";
        protected static final String KEY_FORUM_NAME = "forumName";
        protected static final String KEY_MAIL_HOST = "mailHost";
        protected static final String KEY_MAIL_ADDRESS = "mailAddress";
        protected static final String KEY_MAIL_SUBJECT_ID = "mailSubjectId";
        protected static final String KEY_MAIL_SUBJECT_TEXT = "mailSubjectText";
        protected static final String KEY_MAIL_NAMING = "mailNaming";
        protected static final String KEY_LOG_PATH = "logPath";
        protected static final String KEY_LOG_LEVEL = "logLevel";
        protected static final String KEY_LOG_CONFIG = "logConfig";
        protected static final String KEY_LOGOUT_URL = "logoutUrl";
        protected static final String KEY_UPLOAD_QUOTA = "uploadQuota";
        protected static final String KEY_LATENCY_DAYS = "latencyDays";
        protected static final String KEY_DOCS_ROOT = "docsRoot";
        protected static final String KEY_GUEST_ALLOW = "guestAllow";
        protected static final String KEY_PW_DISPLAY = "pwDisplay";

        private final ConfigurationItem configuration;

        protected InputWrapper(final ConfigurationItem inConfiguration) {
            configuration = inConfiguration;
        }

        private String getPropertyValue(final String inId) {
            return configuration.getItemProperty(inId).getValue().toString();
        }

        @SuppressWarnings("unchecked")
        private void setPropertyValue(final String inId, final String inValue) {
            configuration.getItemProperty(inId).setValue(inValue);
        }

        public String getLanguageDefault() {
            return getPropertyValue(ConfigurationItem.PropertyDef.LANGUAGE_DEFAULT.getPID());
        }

        public void setLanguageDefault(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.LANGUAGE_DEFAULT.getPID(), inValue);
        }

        public String getLanguageContent() {
            return getPropertyValue(ConfigurationItem.PropertyDef.LANGUAGE_CONTENT.getPID());
        }

        public void setLanguageContent(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.LANGUAGE_CONTENT.getPID(), inValue);
        }

        public String getDatePattern() {
            return getPropertyValue(ConfigurationItem.PropertyDef.DATE_PATTERN.getPID());
        }

        public void setDatePattern(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.DATE_PATTERN.getPID(), inValue);
        }

        public DBDriverBean getDbDriver() {
            return DBDriverSelect.createDriverBean(getPropertyValue(ConfigurationItem.PropertyDef.DB_DRIVER.getPID()));
        }

        public void setDbDriver(final DBDriverBean inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.DB_DRIVER.getPID(), inValue == null ? "" : inValue.getID());
        }

        public String getDbServer() {
            return getPropertyValue(ConfigurationItem.PropertyDef.DB_SERVER.getPID());
        }

        public void setDbServer(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.DB_SERVER.getPID(), inValue);
        }

        public String getDbSchema() {
            return getPropertyValue(ConfigurationItem.PropertyDef.DB_SCHEMA.getPID());
        }

        public void setDbSchema(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.DB_SCHEMA.getPID(), inValue);
        }

        public String getDbUser() {
            return getPropertyValue(ConfigurationItem.PropertyDef.DB_USER.getPID());
        }

        public void setDbUser(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.DB_USER.getPID(), inValue);
        }

        public String getDbPasswd() {
            return getPropertyValue(ConfigurationItem.PropertyDef.DB_PASSWD.getPID());
        }

        public void setDbPasswd(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.DB_PASSWD.getPID(), inValue);
        }

        public DBDriverBean getDbxDriver() {
            return DBDriverSelect.createDriverBean(getPropertyValue(ConfigurationItem.PropertyDef.DBX_DRIVER.getPID()));
        }

        public void setDbxDriver(final DBDriverBean inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.DBX_DRIVER.getPID(), inValue == null ? "" : inValue.getID());
        }

        public String getDbxServer() {
            return getPropertyValue(ConfigurationItem.PropertyDef.DBX_SERVER.getPID());
        }

        public void setDbxServer(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.DBX_SERVER.getPID(), inValue);
        }

        public String getDbxSchema() {
            return getPropertyValue(ConfigurationItem.PropertyDef.DBX_SCHEMA.getPID());
        }

        public void setDbxSchema(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.DBX_SCHEMA.getPID(), inValue);
        }

        public String getDbxUser() {
            return getPropertyValue(ConfigurationItem.PropertyDef.DBX_USER.getPID());
        }

        public void setDbxUser(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.DBX_USER.getPID(), inValue);
        }

        public String getDbxPasswd() {
            return getPropertyValue(ConfigurationItem.PropertyDef.DBX_PASSWD.getPID());
        }

        public void setDbxPasswd(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.DBX_PASSWD.getPID(), inValue);
        }

        public String getLdapUrl() {
            return getPropertyValue(ConfigurationItem.PropertyDef.LDAP_URL.getPID());
        }

        public void setLdapUrl(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.LDAP_URL.getPID(), inValue);
        }

        public String getLdapManagerDn() {
            return getPropertyValue(ConfigurationItem.PropertyDef.LDAP_MANAGER_DN.getPID());
        }

        public void setLdapManagerDn(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.LDAP_MANAGER_DN.getPID(), inValue);
        }

        public String getLdapManagerPw() {
            return getPropertyValue(ConfigurationItem.PropertyDef.LDAP_MANAGER_PW.getPID());
        }

        public void setLdapManagerPw(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.LDAP_MANAGER_PW.getPID(), inValue);
        }

        public String getMemberSearcher() {
            return getPropertyValue(ConfigurationItem.PropertyDef.MEMBER_SEARCHER.getPID());
        }

        public void setMemberSearcher(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.MEMBER_SEARCHER.getPID(), inValue);
        }

        public String getForumName() {
            return getPropertyValue(ConfigurationItem.PropertyDef.FORUM_NAME.getPID());
        }

        public void setForumName(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.FORUM_NAME.getPID(), inValue);
        }

        public String getMailHost() {
            return getPropertyValue(ConfigurationItem.PropertyDef.MAIL_HOST.getPID());
        }

        public void setMailHost(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.MAIL_HOST.getPID(), inValue);
        }

        public String getMailAddress() {
            return getPropertyValue(ConfigurationItem.PropertyDef.MAIL_ADDRESS.getPID());
        }

        public void setMailAddress(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.MAIL_ADDRESS.getPID(), inValue);
        }

        public String getMailSubjectId() {
            return getPropertyValue(ConfigurationItem.PropertyDef.MAIL_SUBJECT_ID.getPID());
        }

        public void setMailSubjectId(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.MAIL_SUBJECT_ID.getPID(), inValue);
        }

        public String getMailSubjectText() {
            return getPropertyValue(ConfigurationItem.PropertyDef.MAIL_SUBJECT_TEXT.getPID());
        }

        public void setMailSubjectText(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.MAIL_SUBJECT_TEXT.getPID(), inValue);
        }

        public String getMailNaming() {
            return getPropertyValue(ConfigurationItem.PropertyDef.MAIL_NAMING.getPID());
        }

        public void setMailNaming(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.MAIL_NAMING.getPID(), inValue);
        }

        public String getLogPath() {
            return getPropertyValue(ConfigurationItem.PropertyDef.LOG_PATH.getPID());
        }

        public void setLogPath(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.LOG_PATH.getPID(), inValue);
        }

        public String getLogLevel() {
            return getPropertyValue(ConfigurationItem.PropertyDef.LOG_LEVEL.getPID());
        }

        public void setLogLevel(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.LOG_LEVEL.getPID(), inValue);
        }

        public String getLogConfig() {
            return getPropertyValue(ConfigurationItem.PropertyDef.LOG_CONFIG.getPID());
        }

        public void setLogConfig(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.LOG_CONFIG.getPID(), inValue);
        }

        public String getLogoutUrl() {
            return getPropertyValue(ConfigurationItem.PropertyDef.LOGOUT_URL.getPID());
        }

        public void setLogoutUrl(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.LOGOUT_URL.getPID(), inValue);
        }

        public Integer getUploadQuota() {
            return Integer.parseInt(getPropertyValue(ConfigurationItem.PropertyDef.UPLOAD_QUOTA.getPID()));
        }

        public void setUploadQuota(final Integer inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.UPLOAD_QUOTA.getPID(), inValue.toString());
        }

        public Integer getLatencyDays() {
            return Integer.parseInt(getPropertyValue(ConfigurationItem.PropertyDef.LATENCY_DAYS.getPID()));
        }

        public void setLatencyDays(final Integer inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.LATENCY_DAYS.getPID(), inValue.toString());
        }

        public String getDocsRoot() {
            return getPropertyValue(ConfigurationItem.PropertyDef.DOCS_ROOT.getPID());
        }

        public void setDocsRoot(final String inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.DOCS_ROOT.getPID(), inValue);
        }

        public Boolean getGuestAllow() {
            return Boolean.parseBoolean(getPropertyValue(ConfigurationItem.PropertyDef.GUEST_ALLOW.getPID()));
        }

        public void setGuestAllow(final Boolean inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.GUEST_ALLOW.getPID(), inValue.toString());
        }

        public Boolean getPwDisplay() {
            return Boolean.parseBoolean(getPropertyValue(ConfigurationItem.PropertyDef.PW_DISPLAY.getPID()));
        }

        public void setPwDisplay(final Boolean inValue) {
            setPropertyValue(ConfigurationItem.PropertyDef.PW_DISPLAY.getPID(), inValue.toString());
        }
    }

}

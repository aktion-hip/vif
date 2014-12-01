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

package org.hip.vif.web.tasks;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.impl.DefaultStatement;
import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.RoleHome;
import org.hip.vif.core.exc.InvalidAuthenticationException;
import org.hip.vif.core.service.MemberUtility;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.core.util.DBConnectionProber;
import org.hip.vif.core.util.EmbeddedDBHelper;
import org.hip.vif.core.util.StatementsFileParser;
import org.hip.vif.web.Activator;
import org.hip.vif.web.components.CreateSUPopup;
import org.hip.vif.web.components.DBConfigurationPopup;
import org.hip.vif.web.util.ConfigurationItem;
import org.ripla.interfaces.IMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

/** Helper class providing the worklow items (i.e. single workflow steps) to configure the DB access.
 *
 * @author Luthiger Created: 14.02.2012 */
public class DBAccessWorkflowItems {
    private static final Logger LOG = LoggerFactory
            .getLogger(DBAccessWorkflowItems.class);

    /** The interface of a workflow item. */
    protected static interface IWorkflowItem {
        void run(DBAccessWorkflow inController) throws WorkflowException;

        boolean hasNextStep();

        IWorkflowItem getNextStep();

        void registerSuccessItem(IWorkflowItem inItem);

        void registerFailureItem(IWorkflowItem inItem);

        int getReturnCode();

        String getErrorMessage();
    }

    /** Abstract workflow item base class. */
    protected static abstract class AbstractWorkflowItem {
        private IWorkflowItem successItem = null;
        private IWorkflowItem failureItem = null;
        private IWorkflowItem nextStep = null;
        private String errorMsg = ""; //$NON-NLS-1$
        private DBAccessWorkflow controller;
        private final IMessages messages = Activator.getMessages();

        public void registerSuccessItem(final IWorkflowItem inItem) {
            successItem = inItem;
        }

        public void registerFailureItem(final IWorkflowItem inItem) {
            failureItem = inItem;
        }

        protected void setOutcome(final boolean inSuccess) {
            if (hasNextStep()) {
                nextStep = inSuccess ? successItem : failureItem;
            }
        }

        public boolean hasNextStep() {
            return successItem != null || failureItem != null;
        }

        public IWorkflowItem getNextStep() {
            if (nextStep != null)
                return nextStep;
            if (successItem != null)
                return successItem;
            return failureItem;
        }

        public String getErrorMessage() {
            return errorMsg;
        }

        protected void setErrorMessage(final String inMsgKey) {
            errorMsg = messages.getMessage(inMsgKey);
        }

        public int getReturnCode() {
            return DBAccessWorkflow.OK_LOGIN;
        }

        protected void runNext() {
            controller.nextStep();
        }

        public void run(final DBAccessWorkflow inController)
                throws WorkflowException {
            controller = inController;
            runStep();
        }

        protected abstract void runStep() throws WorkflowException;
    }

    /** The workflow step to display the form to configure the DB access. */
    public static class ShowConfigPopup extends AbstractWorkflowItem implements
            IWorkflowItem {
        private DBConfigurationPopup dbConfiguration;

        @Override
        public void runStep() throws WorkflowException {
            try {
                dbConfiguration = new DBConfigurationPopup(this);
            } catch (final IOException exc) {
                setErrorMessage("errmsg.dbaccess.configure"); //$NON-NLS-1$
                throw new WorkflowException(exc);
            }
        }

        /** Callback method, saves the input of the DB access configuration popup.
         *
         * @param inConfiguration {@link ConfigurationItem} */
        public void save(final ConfigurationItem inConfiguration) {
            inConfiguration.saveChanges();
            if (EmbeddedDBHelper.checkEmbedded(inConfiguration
                    .getItemProperty(
                            ConfigurationItem.PropertyDef.DB_DRIVER.getPID())
                    .getValue().toString())) {
                PreferencesHandler.INSTANCE.setEmbeddedDB();
            }
            // set the changed setting to the DB source registry
            DataSourceRegistry.INSTANCE
                    .setActiveConfiguration(PreferencesHandler.INSTANCE
                            .getDBConfiguration());
            dbConfiguration.close();
            runNext();
        }
    }

    /** Check DB connection.
     * <ul>
     * <li>success -> checkNoTables</li>
     * <li>failure -> DBAccessConfiguration</li>
     * </ul> */
    protected static class TryConnect extends AbstractWorkflowItem implements
            IWorkflowItem {
        @Override
        public void runStep() throws WorkflowException {
            final DBConnectionProber lProber = new DBConnectionProber();
            setOutcome(!lProber.needsDBConfiguration());
            runNext();
        }
    }

    /** Check whether the DB contains tables.
     * <ul>
     * <li>success -> checkSUExistance</li>
     * <li>failure -> createTables</li>
     * </ul> */
    protected static class CheckNoTables extends AbstractWorkflowItem implements
            IWorkflowItem {
        @Override
        public void runStep() throws WorkflowException {
            final DBConnectionProber lProber = new DBConnectionProber();
            setOutcome(!lProber.needsTableCreation());
            runNext();
        }
    }

    /** Create the tables if the DB is empty. */
    public static class CreateTables extends AbstractWorkflowItem implements
            IWorkflowItem {

        @Override
        public void runStep() throws WorkflowException {
            try {
                final DefaultStatement lSQL = new DefaultStatement();
                for (final String lStatement : getStatements()) {
                    lSQL.execute(lStatement);
                }
                runNext();
            } catch (final Exception exc) {
                setErrorMessage("errmsg.dbaccess.table.create"); //$NON-NLS-1$
                throw new WorkflowException(exc);
            }
        }

        private static Collection<String> getStatements() throws IOException,
                SAXException, ParserConfigurationException {
            final StatementsFileParser lParser = new StatementsFileParser();
            return lParser.getStatements();
        }
    }

    /** Step to display the form to create the application's SU (the super user). */
    public static class CreateSU extends AbstractWorkflowItem implements
            IWorkflowItem {
        protected int returnCode = DBAccessWorkflow.ERROR;
        private CreateSUPopup createView;

        @SuppressWarnings("serial")
        @Override
        public void runStep() throws WorkflowException {
            try {
                createView = new CreateSUPopup((Member) BOMHelper.getMemberHome().create(), this);
                createView.addCloseListener(new CloseListener() {
                    @Override
                    public void windowClose(final CloseEvent inEvent) {
                        registerSuccessItem(null);
                        registerFailureItem(null);
                        setErrorMessage("errmsg.dbaccess.form.su"); //$NON-NLS-1$
                        runNext();
                    }
                });
            } catch (final Exception exc) {
                setErrorMessage("errmsg.dbaccess.create.su"); //$NON-NLS-1$
                throw new WorkflowException(exc);
            }
        }

        /** Callback method.
         *
         * @param inMember {@link Member} */
        public void save(final Member inMember) {
            try {
                final String lPwrd = createSURecord(inMember);
                loginAsSU(inMember, lPwrd);
                runNext();
            } catch (final Exception exc) {
                LOG.error("Error encountered while creating the SU member record!", exc); //$NON-NLS-1$
                setErrorMessage("errmsg.dbaccess.create.su"); //$NON-NLS-1$
            } finally {
                closeView();
            }
        }

        protected void closeView() {
            createView.close();
        }

        protected String createSURecord(final Member inMember)
                throws VException, Exception {
            final String outPwrd = inMember.get(MemberHome.KEY_PASSWORD)
                    .toString();
            inMember.set(MemberHome.KEY_PASSWORD, MemberUtility.INSTANCE
                    .getActiveAuthenticator().encrypt(outPwrd));
            final Collection<String> lRoles = new ArrayList<String>();
            lRoles.add(String.valueOf(RoleHome.ROLE_SU));
            inMember.ucNew(lRoles);
            return outPwrd;
        }

        private void loginAsSU(final Member inMember, final String inPwrd)
                throws InvalidAuthenticationException, VException,
                SQLException, GettingException {
            MemberUtility.INSTANCE.getActiveAuthenticator()
                    .checkAuthentication(
                            inMember.get(MemberHome.KEY_USER_ID).toString(),
                            inPwrd);
            returnCode = DBAccessWorkflow.OK_SU;
        }

        @Override
        public int getReturnCode() {
            return returnCode;
        }
    }

    /** Step to display the form to create the application's SU (the super user).<br />
     * This step doesn't process the SU login. Instead, a notification for a new login is displayed. */
    public static class CreateSUNoLogin extends CreateSU {

        @Override
        public void save(final Member inMember) {
            try {
                createSURecord(inMember);
                returnCode = DBAccessWorkflow.OK_SU;
                runNext();
            } catch (final Exception exc) {
                LOG.error("Error encountered while creating the SU member record!", exc); //$NON-NLS-1$
                setErrorMessage("errmsg.dbaccess.create.su"); //$NON-NLS-1$
            } finally {
                closeView();
            }
        }
    }

    /** Checks the existence of a SU.
     * <ul>
     * <li>success -> showLogin</li>
     * <li>failure -> createSU</li>
     * </ul> */
    protected static class CheckSUExistance extends AbstractWorkflowItem
            implements IWorkflowItem {
        @Override
        public void runStep() throws WorkflowException {
            try {
                final int lCount = BOMHelper.getMemberCacheHome().getCount();
                setOutcome(lCount > 0);
                runNext();
            } catch (final Exception exc) {
                setErrorMessage("errmsg.dbaccess.read.su"); //$NON-NLS-1$
                throw new WorkflowException(exc);
            }
        }
    }

    /** Final workflow step: show the normal login form. */
    protected static class ShowLogin extends AbstractWorkflowItem implements
            IWorkflowItem {
        @Override
        public void runStep() throws WorkflowException {
            runNext();
        }
    }

    @SuppressWarnings("serial")
    public static class WorkflowException extends Exception {
        public WorkflowException(final Throwable inCause) {
            super(inCause);
        }
    }

}

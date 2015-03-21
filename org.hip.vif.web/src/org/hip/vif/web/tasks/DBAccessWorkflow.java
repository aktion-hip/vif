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

import org.hip.vif.web.tasks.DBAccessWorkflowItems.CheckNoTables;
import org.hip.vif.web.tasks.DBAccessWorkflowItems.CheckSUExistance;
import org.hip.vif.web.tasks.DBAccessWorkflowItems.CreateSU;
import org.hip.vif.web.tasks.DBAccessWorkflowItems.CreateSUNoLogin;
import org.hip.vif.web.tasks.DBAccessWorkflowItems.CreateTables;
import org.hip.vif.web.tasks.DBAccessWorkflowItems.IWorkflowItem;
import org.hip.vif.web.tasks.DBAccessWorkflowItems.ShowConfigPopup;
import org.hip.vif.web.tasks.DBAccessWorkflowItems.ShowLogin;
import org.hip.vif.web.tasks.DBAccessWorkflowItems.TryConnect;
import org.hip.vif.web.tasks.DBAccessWorkflowItems.WorkflowException;
import org.ripla.interfaces.IWorkflowListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The controller class of the workflow to create the DB access.
 *
 * @author Luthiger Created: 11.02.2012 */
public final class DBAccessWorkflow {
    private static final Logger LOG = LoggerFactory
            .getLogger(DBAccessWorkflow.class);

    /** The workflow's return codes */
    public static final int OK_SU = 1;
    public static final int OK_LOGIN = 2;
    public static final int ERROR = 3;

    private transient IWorkflowItem workflowItem;
    private transient final IWorkflowListener workflowListener;

    /** Private constructor for the DB access workflow.
     *
     * @param inWorkflowItem {@link IWorkflowItem} the workflow's starting step
     * @param inWorkflowListener {@link IWorkflowListener} the application instance */
    private DBAccessWorkflow(final IWorkflowItem inWorkflowItem,
            final IWorkflowListener inWorkflowListener) {
        workflowItem = inWorkflowItem;
        workflowListener = inWorkflowListener;
    }

    /** Creates the workflow for the initial DB access.
     *
     * @param inWorkflowListener {@link IWorkflowListener}
     * @return {@link DBAccessWorkflow} */
    public static DBAccessWorkflow getInitialWorkflow(final IWorkflowListener inWorkflowListener) {
        // instantiate the workflow steps we need
        final IWorkflowItem lShowDBConfig = new ShowConfigPopup();
        final IWorkflowItem lTryConnect = new TryConnect();
        final IWorkflowItem lCheckNoTables = new CheckNoTables();
        final IWorkflowItem lCreateTables = new CreateTables();
        final IWorkflowItem lCreateSU = new CreateSU();
        final IWorkflowItem lCheckSUExistance = new CheckSUExistance();
        final IWorkflowItem lShowLogin = new ShowLogin();

        // wire the workflow steps
        lShowDBConfig.registerSuccessItem(lTryConnect);
        //
        lTryConnect.registerSuccessItem(lCheckNoTables);
        lTryConnect.registerFailureItem(lShowDBConfig);
        //
        lCheckNoTables.registerSuccessItem(lCheckSUExistance);
        lCheckNoTables.registerFailureItem(lCreateTables);
        //
        lCreateTables.registerSuccessItem(lCreateSU);
        //
        lCheckSUExistance.registerSuccessItem(lShowLogin);
        lCheckSUExistance.registerFailureItem(lCreateSU);

        final DBAccessWorkflow outWorkflow = new DBAccessWorkflow(
                lShowDBConfig, inWorkflowListener);
        return outWorkflow;
    }

    /** The workflow to be used to guide the sysadmin through the step of SU creation.
     *
     * @param inWorkflowListener {@link IWorkflowListener}
     * @return {@link DBAccessWorkflow} */
    public static DBAccessWorkflow getInitialTblCreation(final IWorkflowListener inWorkflowListener) {
        final IWorkflowItem lCreateTables = new CreateTables();
        final IWorkflowItem lCreateSU = new CreateSU();
        lCreateTables.registerSuccessItem(lCreateSU);

        final DBAccessWorkflow outWorkflow = new DBAccessWorkflow(
                lCreateTables, inWorkflowListener);
        return outWorkflow;
    }

    /** Creates the workflow to create and initialize the tables within a DB schema.
     *
     * @param inWorkflowListener {@link IWorkflowListener}
     * @return {@link DBAccessWorkflow} */
    public static DBAccessWorkflow getCreateTablesWorkflow(final IWorkflowListener inWorkflowListener) {
        final IWorkflowItem lCreateTables = new CreateTables();
        final IWorkflowItem lCreateSU = new CreateSUNoLogin();
        lCreateTables.registerSuccessItem(lCreateSU);

        final DBAccessWorkflow outWorkflow = new DBAccessWorkflow(
                lCreateTables, inWorkflowListener);
        return outWorkflow;
    }

    /** Creates the workflow to create the SU record.
     *
     * @param inWorkflowListener {@link IWorkflowListener}
     * @return {@link DBAccessWorkflow} */
    public static DBAccessWorkflow getCreateSUWorkflow(final IWorkflowListener inWorkflowListener) {
        final IWorkflowItem lCreateSU = new CreateSUNoLogin();

        final DBAccessWorkflow outWorkflow = new DBAccessWorkflow(lCreateSU,
                inWorkflowListener);
        return outWorkflow;
    }

    /** Start the workflow to set the application's initial configuration.
     *
     * @throws WorkflowException */
    public void startWorkflow() throws WorkflowException {
        workflowItem.run(this);
    }

    /** @return {@link ReturnCode} returns the configuration workflow's return code */
    public int getReturnCode() {
        return workflowItem.getReturnCode();
    }

    /** Package protected: run the workflow's next step. */
    protected void nextStep() { // NOPMD
        try {
            if (workflowItem.hasNextStep()) {
                workflowItem = workflowItem.getNextStep();
                workflowItem.run(this);
            } else {
                workflowListener.workflowExit(workflowItem.getReturnCode(), ""); //$NON-NLS-1$
            }
        } catch (final WorkflowException exc) {
            LOG.error("Error encountered during initial configuration workflow!", exc); //$NON-NLS-1$
            workflowListener
                    .workflowExit(ERROR, workflowItem.getErrorMessage());
        }
    }

}

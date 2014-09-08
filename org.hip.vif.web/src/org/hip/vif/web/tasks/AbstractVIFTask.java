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

package org.hip.vif.web.tasks;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.authorization.AbstractVIFAuthorization;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.exc.PermissionsNotSufficientException;
import org.hip.vif.core.exc.VIFExceptionHandler;
import org.hip.vif.core.interfaces.IPluggableTask;
import org.hip.vif.core.member.IActor;
import org.hip.vif.core.service.ApplicationData;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.core.util.HtmlCleaner;
import org.hip.vif.web.Activator;
import org.hip.vif.web.Constants;
import org.hip.vif.web.controller.TaskManager;
import org.hip.vif.web.util.LinkButtonHelper;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.hip.vif.web.util.RichTextSanitizer;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.ripla.util.ParameterObject;
import org.ripla.web.util.UseCaseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.WebBrowser;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

/** Base class for all tasks of the application. A task class contains the controller and business logic of a use case.
 *
 * @deprecated: use <code>AbstractWebController</code> instead
 * @author Luthiger Created: 18.05.2011 */
@Deprecated
public abstract class AbstractVIFTask implements IPluggableTask {
    private static final Logger LOG = LoggerFactory
            .getLogger(AbstractVIFTask.class);
    public static final String DFT_PATTERN = "MM/dd/yyyy"; //$NON-NLS-1$

    private EventAdmin eventAdmin;

    /*
     * (non-Javadoc)
     * 
     * @see org.hip.vif.core.interfaces.IPluggableTask#run()
     */
    @Override
    public Component run() throws VException {
        if (getActor() == null) {
            return new Label(Activator.getMessages().getMessage("task.login.failure")); //$NON-NLS-1$
        }
        checkRoles();
        return runChecked();
    }

    protected abstract Component runChecked() throws VException;

    private void checkRoles() throws PermissionsNotSufficientException {
        final IActor lActor = getActor();
        final AbstractVIFAuthorization lAuthorization = (AbstractVIFAuthorization) lActor
                .getAuthorization();
        if (!lAuthorization.hasPermission(needsPermission())) {
            final StringBuilder lLogMsg = new StringBuilder(
                    "Note: The user has not sufficient permissions for the requested task.\n"); //$NON-NLS-1$
            lLogMsg.append("   User: ").append(lActor.getUserID()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
            lLogMsg.append("   IP number: ").append(((WebBrowser) ApplicationData.getWindow().getTerminal()).getAddress()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
            LOG.warn(new String(lLogMsg));
            throw new PermissionsNotSufficientException();
        }
    }

    /** Each deriving task has to define the permission it needs to be executed. Note: An empty String means that every
     * role can execute the task.
     *
     * @return java.lang.String */
    protected abstract String needsPermission();

    /** Creates a key object that filters all groups in the specified group states. E.g.
     *
     * <pre>
     * createKey(VIFGroupWorkflow.VISIBLE_STATES)
     * </pre>
     *
     * @param inStates Integer[] the group states
     * @return {@link KeyObject}
     * @throws VException */
    protected KeyObject createKey(final Integer[] inStates) throws VException {
        final KeyObject outKey = new KeyObjectImpl();
        for (int i = 0; i < inStates.length; i++) {
            outKey.setValue(GroupHome.KEY_STATE, inStates[i], "=", BinaryBooleanOperator.OR); //$NON-NLS-1$
        }
        return outKey;
    }

    /** Creates an OrderObject from the specified String. The method expects a comma separated list of Property names of
     * a DomainObject. The returned OrderObject can be used for the ORDER BY part of an SQL command.
     *
     * @param inOrder java.lang.String Comma separated list of Property names.
     * @param inDescending boolean
     * @return org.hip.kernel.bom.OrderObject */
    protected OrderObject createOrder(final String inOrder,
            final boolean inDescending) throws VException {
        final OrderObject outOrder = new OrderObjectImpl();
        final StringTokenizer lTokens = new StringTokenizer(inOrder, ","); //$NON-NLS-1$
        int i = 0;
        while (lTokens.hasMoreTokens()) {
            outOrder.setValue(lTokens.nextToken().trim(), inDescending, i++);
        }
        return outOrder;
    }

    /** Creates a view component displaying the message 'Please contact the administrator' after the application
     * encountered a serious problem.
     *
     * @param inExc {@link Exception} the exception causing the problem.
     * @return {@link VException} */
    protected VException createContactAdminException(final Throwable inExc) {
        final String lMessage = inExc.getMessage();
        LOG.error(lMessage == null ? inExc.toString() : lMessage);
        return (VException) VIFExceptionHandler.getInstance()
                .convert(
                        inExc,
                        Activator.getMessages().getMessage("errmsg.error.contactAdmin")); //$NON-NLS-1$
    }

    /** Creates an exception signaling that the user has not sufficient permissions to process the task.
     *
     * @return {@link VException} */
    protected VException createNoPermissionException() {
        return new VException(Activator.getMessages().getMessage("errmsg.error.noPermission")); //$NON-NLS-1$
    }

    /** @return {@link IActor} the actor, i.e. the actual user. */
    protected IActor getActor() {
        return ApplicationData.getActor();
    }

    /** @return {@link Locale} the actual session's locale. */
    protected Locale getAppLocale() {
        return ApplicationData.getLocale();
    }

    /** @param inGroupID sets the actually relevant group's id (because he selected that group) */
    protected void setGroupID(final Long inGroupID) {
        ApplicationData.setGroupID(inGroupID);
    }

    /** @return Long the id of the group actually relevant for the user (because he works on the group's questions) */
    public Long getGroupID() {
        return ApplicationData.getGroupID();
    }

    /** @param inQuestionID Long sets the actually relevant question's id (because he selected that question) */
    protected void setQuestionID(final Long inQuestionID) {
        ApplicationData.setQuestionID(inQuestionID);
    }

    /** @return Long the id of the question actually relevant for the user (because he works on that question) */
    protected Long getQuestionID() {
        return ApplicationData.getQuestionID();
    }

    /** @param inCompletionID Long sets the actually relevant completion's id (because he selected that completion) */
    protected void setCompletionID(final Long inCompletionID) {
        ApplicationData.setCompletionID(inCompletionID);
    }

    /** @return Long the id of the completion actually relevant for the user (because he works on that completion) */
    protected Long getCompletionID() {
        return ApplicationData.getCompletionID();
    }

    /** Sets the text id-version actually relevant for the application.
     *
     * @param inTextID String the text id (id-version) */
    protected void setTextID(final String inTextID) {
        if (inTextID.contains(Text.DELIMITER_ID_VERSION)) {
            final String[] lIDVersion = inTextID
                    .split(Text.DELIMITER_ID_VERSION);
            ApplicationData.setTextID(Long.parseLong(lIDVersion[0]));
            ApplicationData.setTextVersion(lIDVersion[1]);
        } else {
            ApplicationData.setTextID(Long.parseLong(inTextID));
        }
    }

    /** @return Long the id of the bibliography entry actually relevant for the user (e.g. because he clicked the lookup
     *         for a bibliography entry) */
    protected Long getTextID() {
        return ApplicationData.getTextID();
    }

    /** @return Long the version of the bibliography entry actually relevant for the user (e.g. because he clicked the
     *         lookup for a bibliography entry) */
    protected Long getTextVersion() {
        return ApplicationData.getTextVersion();
    }

    /** Set generic parameters.<br/>
     * Use e.g.:
     *
     * <pre>
     * ParameterObject lParameters = new ParameterObject();
     * lParameters.set(Constants.KEY_PARAMETER_MEMBER, lMemberID);
     * setParameters(lParameters);
     * </pre>
     *
     * @param inParameters {@link ParameterObject} the parameters to set or <code>null</code> to clear the parameter
     *            settings */
    protected void setParameters(final ParameterObject inParameters) {
        ApplicationData.setParameters(inParameters);
    }

    /** Returns the generic parameters passed by a task/controller.<br/>
     * <b>Note</b>: the parameter settings a cleared after calling this method!
     *
     * @return {@link ParameterObject} generic parameters */
    protected ParameterObject getParameters() {
        return getParameters(true);
    }

    /** Returns the generic parameters passed by a task/controller.
     *
     * @param inClear boolean if <code>true</code>, the parameter settings are cleared, if <code>false</code>, they are
     *            retained
     * @return {@link ParameterObject} generich parameters */
    protected ParameterObject getParameters(final boolean inClear) {
        final ParameterObject out = ApplicationData.getParameters();
        if (inClear) {
            ApplicationData.setParameters(null);
        }
        return out;
    }

    /** Create a fully qualified task name with the specified task.
     *
     * @param inTask {@link IPluggableTask}
     * @return String the fully qualified name of the task */
    protected String createFullyQualifiedTaskName(
            final Class<? extends IPluggableTask> inTask) {
        return UseCaseHelper.createFullyQualifiedControllerName(inTask);
    }

    /** Use OSGi event service to display the next content view.
     *
     * @param inClass Class the next task */
    protected void sendEvent(final Class<? extends IPluggableTask> inTask) {
        sendEvent(createFullyQualifiedTaskName(inTask));
    }

    /** Use OSGi event service to display the next content view.
     *
     * @param inTaskName String the fully qualified name of the next task */
    protected void sendEvent(final String inTaskName) {
        final Map<String, Object> lProperties = new HashMap<String, Object>();
        lProperties.put(Constants.EVENT_PROPERTY_NEXT_TASK, inTaskName);

        final Event lEvent = new Event(Constants.EVENT_TOPIC_TASKS, lProperties);
        eventAdmin.sendEvent(lEvent);
    }

    /** Runs the specified tasks and returns it's view.
     *
     * @param inTask Class the task
     * @param inIsForum boolean <code>true</code> if the task to forward to is a forum task, else <code>false</code>
     * @return {@link Component}
     * @throws VException */
    protected Component forwardToTask(
            final Class<? extends IPluggableTask> inTask,
            final boolean inIsForum) throws VException {
        if (inIsForum) {
            return TaskManager.INSTANCE.getForumContent(UseCaseHelper
                    .createFullyQualifiedControllerName(inTask));
        }
        return TaskManager.INSTANCE.getAdminContent(UseCaseHelper
                .createFullyQualifiedControllerName(inTask));
    }

    /** Use OSGi event service to display a notification message.
     *
     * @param inMessage String
     * @param inNotificationType int the message type (e.g. <code>Notification.TYPE_HUMANIZED_MESSAGE</code>)
     * @see com.vaadin.ui.Window.Notification */
    protected void showNotification(final String inMessage,
            final int inNotificationType) {
        final Map<String, Object> lProperties = new HashMap<String, Object>();
        lProperties.put(Constants.EVENT_PROPERTY_NOTIFICATION_MSG, inMessage);
        lProperties.put(Constants.EVENT_PROPERTY_NOTIFICATION_TYPE,
                inNotificationType);

        final Event lEvent = new Event(Constants.EVENT_TOPIC_NOTIFICATION,
                lProperties);
        eventAdmin.sendEvent(lEvent);
    }

    /** Use OSGi event service to display a notification message with type
     * <code>Notification.TYPE_TRAY_NOTIFICATION</code>.
     *
     * @param inMessage String */
    protected void showNotification(final String inMessage) {
        showNotification(inMessage, Type.TRAY_NOTIFICATION);
    }

    /** Use OSGi event service to trigger a refresh of the dash board. */
    protected void refreshDash() {
        final Map<String, Object> lProperties = new HashMap<String, Object>();
        lProperties.put(Constants.EVENT_PROPERTY_REFRESH, "refresh");

        final Event lEvent = new Event(Constants.EVENT_TOPIC_REFRESH,
                lProperties);
        eventAdmin.sendEvent(lEvent);
    }

    /** Use OSGi event service to display the context menu.
     *
     * @param inSetName String the ID of the context menu to display */
    protected void loadContextMenu(final String inSetName) {
        final Map<String, Object> lProperties = new HashMap<String, Object>();
        lProperties.put(Constants.EVENT_PROPERTY_CONTEXT_MENU_ID,
                UseCaseHelper.createFullyQualifiedID(inSetName, getClass()));

        final Event lEvent = new Event(Constants.EVENT_TOPIC_CONTEXT_MENU,
                lProperties);
        eventAdmin.sendEvent(lEvent);
    }

    /** Clear the context menu panel. */
    protected void emptyContextMenu() {
        loadContextMenu(Constants.MENU_SET_ID_EMPTY);
    }

    /** Use OSGi event service.
     *
     * @param inType {@link LookupType} the type of lookup
     * @param inID Long the ID of the item to display in the lookup */
    @Override
    public void requestLookup(final LinkButtonHelper.LookupType inType,
            final Long inID) {
        requestLookup(inType, inID.toString());
    }

    @Override
    public void requestLookup(final LinkButtonHelper.LookupType inType,
            final String inTextID) {
        final Map<String, Object> lProperties = new HashMap<String, Object>();
        lProperties.put(Constants.EVENT_PROPERTY_LOOKUP_TYPE, inType);
        lProperties.put(Constants.EVENT_PROPERTY_LOOKUP_ID, inTextID);

        final Event lEvent = new Event(Constants.EVENT_TOPIC_LOOKUP,
                lProperties);
        eventAdmin.sendEvent(lEvent);
    }

    /** Sets the <code>EventAdmin</code> to the task for that the task can send events.
     *
     * @param inEventAdmin {@link EventAdmin} */
    @Override
    public void setEventAdmin(final EventAdmin inEventAdmin) {
        eventAdmin = inEventAdmin;
    }

    protected EventAdmin getEventAdmin() {
        return eventAdmin;
    }

    /** Reruns the last task and displays the specified message.
     *
     * @param inMessage String
     * @param inNotificationType int The message type (e.g. Notification.TYPE_HUMANIZED_MESSAGE)
     * @return {@link Component} the rendered component
     * @throws VException */
    protected Component reDisplay(final String inMessage,
            final int inNotificationType) throws VException {
        ApplicationData.popLastTask();
        showNotification(inMessage, inNotificationType);
        return ApplicationData.getLastTask().run();
    }

    /** Converts rich text field input to proper XHTML body, all surrounding white space removed.
     *
     * @param inHTML String
     * @return String */
    protected String cleanUp(final String inHTML) {
        return HtmlCleaner.cleanUp(RichTextSanitizer.sanitize(inHTML));
    }

    /** Returns the forum's date format, defined in vif.properties
     *
     * @return DateFormat
     * @see <code>vif.properties</code>: org.hip.vif.datePattern */
    protected static DateFormat getFormat() {
        String lPattern = DFT_PATTERN;
        try {
            lPattern = PreferencesHandler.INSTANCE
                    .get(PreferencesHandler.KEY_DATE_PATTERN);
        } catch (final IOException exc) {
            // intentionally left empty
        }
        return new SimpleDateFormat(lPattern);
    }
}

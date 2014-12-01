/**
	This package is part of the application VIF.
	Copyright (C) 2012-2014, Benno Luthiger

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
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.member.IActor;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.core.util.HtmlCleaner;
import org.hip.vif.web.Activator;
import org.hip.vif.web.Constants;
import org.hip.vif.web.exc.VIFExceptionHandler;
import org.hip.vif.web.exc.VIFWebException;
import org.hip.vif.web.interfaces.IPluggableWithLookup;
import org.hip.vif.web.interfaces.IVIFEventDispatcher;
import org.hip.vif.web.interfaces.IVIFEventDispatcher.Event;
import org.hip.vif.web.tasks.ForwardControllerRegistry.Alias;
import org.hip.vif.web.util.LinkButtonHelper;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.hip.vif.web.util.RichTextSanitizer;
import org.hip.vif.web.util.VIFAppHelper;
import org.ripla.exceptions.NoControllerFoundException;
import org.ripla.exceptions.RiplaException;
import org.ripla.interfaces.IRiplaEventDispatcher;
import org.ripla.util.ParameterObject;
import org.ripla.web.controllers.AbstractController;
//import org.ripla.web.interfaces.IForwardingController;
import org.ripla.web.interfaces.IPluggable;
import org.ripla.web.util.ControllerStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;

/** Base class for controllers of the VIF application.
 *
 * @author lbenno */
public abstract class AbstractWebController extends AbstractController implements IPluggableWithLookup {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractWebController.class);

    public static final String EVENT_TOPIC_LOOKUP = "org/hip/vif/web/LookupEvent/LOOKUP"; //$NON-NLS-1$
    public static final String EVENT_PROPERTY_LOOKUP_TYPE = "lookup.type"; //$NON-NLS-1$
    public static final String EVENT_PROPERTY_LOOKUP_ID = "lookup.id"; //$NON-NLS-1$
    public static final String EVENT_PROPERTY_LOOKUP_CONTROLLER = "lookup.controller"; //$NON-NLS-1$
    public static final String DFT_PATTERN = "MM/dd/yyyy"; //$NON-NLS-1$

    /** Creates a view component displaying the message 'Please contact the administrator' after the application
     * encountered a serious problem.
     *
     * @param inExc {@link Throwable} the exception causing the problem.
     * @return {@link VIFWebException} */
    protected VIFWebException createContactAdminException(final Throwable inExc) {
        final String lMessage = inExc.getMessage();
        LOG.error(lMessage == null ? inExc.toString() : lMessage);
        return (VIFWebException) VIFExceptionHandler.INSTANCE
                .convert(
                        inExc,
                        Activator.getMessages().getMessage(
                                "errmsg.error.contactAdmin"));
    }

    /** Creates an exception signaling that the user has not sufficient permissions to process the task.
     *
     * @return {@link VIFWebException} */
    protected VIFWebException createNoPermissionException() {
        return new VIFWebException(Activator.getMessages().getMessage("errmsg.error.noPermission")); //$NON-NLS-1$
    }

    /** @return {@link IActor} the actor, i.e. the actual user. */
    protected IActor getActor() {
        try {
            VaadinSession.getCurrent().getLockInstance().lock();
            return VaadinSession.getCurrent().getAttribute(IActor.class);
        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }
    }

    /** @param inGroupID sets the actually relevant group's id (because he selected that group) */
    protected void setGroupID(final Long inGroupID) {
        VIFAppHelper.setValueToSession(Constants.GROUP_ID_KEY, inGroupID);
    }

    /** @return Long the id of the group actually relevant for the user (because he works on the group's questions) */
    public Long getGroupID() {
        return VIFAppHelper.getValueFromSession(Constants.GROUP_ID_KEY);
    }

    /** @param inQuestionID Long sets the actually relevant question's id (because he selected that question) */
    protected void setQuestionID(final Long inQuestionID) {
        VIFAppHelper.setValueToSession(Constants.QUESTION_ID_KEY, inQuestionID);
    }

    /** @return Long the id of the question actually relevant for the user (because he works on that question) */
    protected Long getQuestionID() {
        return VIFAppHelper.getValueFromSession(Constants.QUESTION_ID_KEY);
    }

    /** @param inCompletionID Long sets the actually relevant completion's id (because he selected that completion) */
    protected void setCompletionID(final Long inCompletionID) {
        VIFAppHelper.setValueToSession(Constants.COMPLETION_ID_KEY, inCompletionID);
    }

    /** @return Long the id of the completion actually relevant for the user (because he works on that completion) */
    protected Long getCompletionID() {
        return VIFAppHelper.getValueFromSession(Constants.COMPLETION_ID_KEY);
    }

    /** Sets the text id-version actually relevant for the application.
     *
     * @param inTextID String the text id (id-version) */
    protected void setTextID(final String inTextID) {
        if (inTextID.contains(Text.DELIMITER_ID_VERSION)) {
            final String[] lIDVersion = inTextID
                    .split(Text.DELIMITER_ID_VERSION);
            VIFAppHelper.setValueToSession(Constants.TEXT_ID_KEY,
                    Long.parseLong(lIDVersion[0]));
            VIFAppHelper.setValueToSession(Constants.TEXT_VERSION_ID_KEY,
                    Long.parseLong(lIDVersion[1]));
        } else {
            VIFAppHelper.setValueToSession(Constants.TEXT_ID_KEY, Long.parseLong(inTextID));
        }
    }

    /** @return Long the id of the bibliography entry actually relevant for the user (e.g. because he clicked the lookup
     *         for a bibliography entry) */
    protected Long getTextID() {
        return VIFAppHelper.getValueFromSession(Constants.TEXT_ID_KEY);
    }

    /** @return Long the version of the bibliography entry actually relevant for the user (e.g. because he clicked the
     *         lookup for a bibliography entry) */
    protected Long getTextVersion() {
        return VIFAppHelper.getValueFromSession(Constants.TEXT_VERSION_ID_KEY);
    }

    /** Use Vaadin event service to display the next content view.
     *
     * @param inClass Class the next task */
    protected void sendEvent(final Class<? extends IPluggable> inTask) {
        sendEvent(createFullyQualifiedControllerName(inTask));
    }

    /** Use Vaadin event service to display the next content view identified by the specified alias.
     *
     * @param inAlias {@link Alias} */
    protected void sendAliasEvent(final Alias inAlias) {
        sendEvent(ForwardControllerRegistry.INSTANCE.getTargetOf(inAlias));
    }

    /** Use Vaadin event service to display the next content view.
     *
     * @param inControllerName String the fully qualified name of the next task/controller */
    protected void sendEvent(final String inControllerName) {
        final Map<String, Object> lProperties = new HashMap<String, Object>();
        lProperties.put(Constants.EVENT_PROPERTY_NEXT_TASK, inControllerName);
        getRiplaDispatcher().dispatch(IRiplaEventDispatcher.Event.LOAD_CONTROLLER, lProperties);
    }

    private IRiplaEventDispatcher getRiplaDispatcher() {
        try {
            VaadinSession.getCurrent().getLockInstance().lock();
            return VaadinSession.getCurrent().getAttribute(
                    IRiplaEventDispatcher.class);
        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }
    }

    private IVIFEventDispatcher getDispatcher() {
        try {
            VaadinSession.getCurrent().getLockInstance().lock();
            return VaadinSession.getCurrent().getAttribute(
                    IVIFEventDispatcher.class);
        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }
    }

    /** Use Vaadin event service to trigger a refresh of the dash board. */
    protected void refreshDash() {
        final Map<String, Object> lProperties = new HashMap<String, Object>();
        lProperties.put(Constants.EVENT_PROPERTY_REFRESH, "refresh");
        getDispatcher().dispatch(Event.SEND, lProperties);
    }

    /** Use Vaadin event service to call a lookup window.
     *
     * @param inType {@link LookupType} the type of lookup
     * @param inID Long the ID of the item to display in the lookup */
    @Override
    public void requestLookup(final LinkButtonHelper.LookupType inType,
            final Long inID) {
        requestLookup(inType, inID.toString());
    }

    /** Use Vaadin event service to call a lookup window.
     *
     * @param inType {@link LookupType} the type of lookup
     * @param inTextID String the ID of the item to display in the lookup */
    @Override
    public void requestLookup(final LinkButtonHelper.LookupType inType,
            final String inTextID) {
        final Map<String, Object> lProperties = new HashMap<String, Object>();
        lProperties.put(EVENT_PROPERTY_LOOKUP_TYPE, inType);
        lProperties.put(EVENT_PROPERTY_LOOKUP_ID, inTextID);
        lProperties.put(EVENT_PROPERTY_LOOKUP_CONTROLLER, this);
        getDispatcher().dispatch(Event.LOOKUP, lProperties);
    }

    /** Reruns the last task and displays the specified message.
     *
     * @param inMessage String
     * @param inNotificationType {@link Notification.Type} The message type (e.g. Notification.TYPE_HUMANIZED_MESSAGE)
     * @return {@link Component} the rendered component
     * @throws RiplaException */
    protected Component reDisplay(final String inMessage,
            final Notification.Type inNotificationType) throws RiplaException {
        final ControllerStack controllers = ControllerStack
                .getControllerStack();
        controllers.pop();
        showNotification(inMessage, inNotificationType);
        return controllers.peek().run();
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

    // protected Component forwardTo(final IForwardingController inAlias)
    // throws NoControllerFoundException {
    // return forwardTo(ForwardControllerRegistry.INSTANCE
    // .getTargetOf(inAlias));
    // }

    protected Locale getLocaleChecked() {
        try {
            VaadinSession.getCurrent().getLockInstance().lock();
            return VaadinSession.getCurrent().getLocale();
        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }
    }

    /** Forwards the (lookup) control to the specified controller.
     *
     * @param inControllerName String the controller name
     * @return {@link Component} the component the controller returns
     * @throws NoControllerFoundException */
    public Component sendLookupTo(final String inControllerName) throws NoControllerFoundException {
        return super.forwardTo(inControllerName);
    }

    /** Set generic parameters.
     *
     * @param inParameters {@link ParameterObject} */
    public void setUseCaseParameter(final ParameterObject inParameters) {
        setParameters(inParameters);
    }

    /** Returns the id of the model that should be processed (e.g. displayed). The model could be a member or a question
     * object for example. The parameter object evaluated has two entries. The first entry (with ID
     * <code>generic_key</code>) has the value of the key of the second entry. The second's entry's value is the ID to
     * look up.
     *
     * @return Long the model's id */
    protected Long getModelIdFromParameter() {
        final ParameterObject lParameters = getParameters();
        return new Long(lParameters.get(
                lParameters.get(ApplicationConstants.PARAMETER_KEY_GENERIC).toString()).toString());
    }

}

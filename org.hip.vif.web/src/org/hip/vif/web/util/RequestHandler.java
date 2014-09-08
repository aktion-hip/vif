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

import java.util.HashMap;
import java.util.Map;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.exc.NoTaskFoundException;
import org.hip.vif.core.service.ApplicationData;
import org.hip.vif.web.Constants;
import org.hip.vif.web.controller.TaskManager;
import org.hip.vif.web.interfaces.IDashBoard;
import org.ripla.util.ParameterObject;
import org.ripla.web.interfaces.IPluggable;
import org.ripla.web.util.UseCaseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Parameter handler to handle request parameters:
 *
 * <pre>
 * http://my.host.org/forum?request=parameter
 * </pre>
 *
 * @author Luthiger Created: 27.09.2011 */
@SuppressWarnings("serial")
public class RequestHandler implements ParameterHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    private static final String TMPL_REQUEST_URL = "%s?%s=%s&%s=%s"; //$NON-NLS-1$

    private RequestParameter requestParameter;

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.terminal.ParameterHandler#handleParameters(java.util.Map)
     */
    @Override
    public void handleParameters(final Map<String, String[]> inParameters) {
        if (inParameters.containsKey(ApplicationConstants.KEY_REQUEST_PARAMETER)) {
            requestParameter = new RequestParameter(inParameters.get(ApplicationConstants.KEY_REQUEST_PARAMETER)[0]);
            requestParameter.handleParameters(inParameters);
        }
    }

    /** Displays the requested view on the dash board.
     *
     * @param inDashBoard {@link IDashBoard}
     * @return boolean <code>true</code> if the parameter request has been processed successfully, <code>false</code> if
     *         no parameter request provided. */
    public boolean process(final IDashBoard inDashBoard) {
        if (requestParameter == null)
            return false;

        try {
            final String lTaskName = requestParameter.prepare();
            requestParameter = null;
            inDashBoard.setContentView(TaskManager.INSTANCE.getForumContent(lTaskName));
            return true;
        } catch (final NoTaskFoundException exc) {
            // intentionally left empty
        } catch (final NumberFormatException exc) {
            LOG.error("Error encountered while processing the request parameters!", exc); //$NON-NLS-1$
        } catch (final VException exc) {
            LOG.error("Error encountered while processing the request parameters!", exc); //$NON-NLS-1$
        }
        return false;
    }

    /** Creates the URL to the view of the specified task, e.g.
     * <code>http://localhost:8084/forum?request=org.hip.vif.forum.groups/org.hip.vif.groups.tasks.RequestsListTask&groupID=21</code>
     * .
     *
     * @param inTask {@link IPluggable}
     * @param inIsForum boolean <code>true</code> if the requested url should call the forum application,
     *            <code>false</code> for the admin application
     * @return String the bookmarkable URL to the view of the specified task */
    public static String createRequestedURL(final Class<? extends IPluggable> inTask, final boolean inIsForum) {
        return createRequestedURL(inTask, inIsForum, ApplicationConstants.KEY_GROUP_ID, ApplicationData.getGroupID());
    }

    /** Creates the URL to the view of the specified task, e.g.
     * <code>http://localhost:8084/forum?request=org.hip.vif.forum.groups/org.hip.vif.groups.tasks.RequestsListTask&key=value</code>
     * .
     *
     * @param inTask {@link IPluggable} the task that is called in the request
     * @param inIsForum boolean <code>true</code> if the requested url should call the forum application,
     *            <code>false</code> for the admin application
     * @param inKey String the additional parameter's key
     * @param inValue Long the additional parameter's value
     * @return the bookmarkable URL to the view of the specified task */
    public static String createRequestedURL(final Class<? extends IPluggable> inTask, final boolean inIsForum,
            final String inKey, final Long inValue) {
        return String.format(TMPL_REQUEST_URL, inIsForum ? getMainForumURL() : getMainAdminURL(),
                ApplicationConstants.KEY_REQUEST_PARAMETER,
                UseCaseHelper.createFullyQualifiedControllerName(inTask),
                inKey, inValue.toString());
    }

    /** @return String e.g. <code>http://localhost:8084/forum</code> */
    public static String getMainForumURL() {
        return ApplicationData.getRequestURL().replaceAll(Constants.CONTEXT_ADMIN, Constants.CONTEXT_FORUM);
    }

    /** @return String e.g. <code>http://localhost:8084/admin</code> */
    public static String getMainAdminURL() {
        return ApplicationData.getRequestURL().replaceAll(Constants.CONTEXT_FORUM, Constants.CONTEXT_ADMIN);
    }

    // ---

    private static class RequestParameter {
        private final String taskName;
        private final Map<String, String> parameters = new HashMap<String, String>();

        RequestParameter(final String inTaskName) {
            taskName = inTaskName;
        }

        void handleParameters(final Map<String, String[]> inParameters) {
            check(ApplicationConstants.KEY_GROUP_ID, inParameters);
            check(ApplicationConstants.KEY_RATING_ID, inParameters);
        }

        private void check(final String inKey, final Map<String, String[]> inParameters) {
            if (inParameters.containsKey(inKey)) {
                parameters.put(inKey, inParameters.get(inKey)[0]);
            }
        }

        String prepare() throws VException, NumberFormatException {
            final String lGroupID = parameters.get(ApplicationConstants.KEY_GROUP_ID);
            if (lGroupID != null) {
                ApplicationData.setGroupID(Long.parseLong(lGroupID));
            }
            final String lRatingID = parameters.get(ApplicationConstants.KEY_RATING_ID);
            if (lRatingID != null) {
                final ParameterObject lRating = new ParameterObject();
                lRating.set(ApplicationConstants.KEY_RATING_ID, Long.parseLong(lRatingID));
                ApplicationData.setParameters(lRating);
            }
            return taskName;
        }
    }

}

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

package org.hip.vif.web.util;

import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.web.Constants;
import org.ripla.util.ParameterObject;
import org.ripla.web.interfaces.IBodyComponent;
import org.ripla.web.interfaces.IPluggable;
import org.ripla.web.util.RiplaRequestHandler;
import org.ripla.web.util.UseCaseHelper;

import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;

/** OSGi DS serivce provider for the <code>com.vaadin.server.RequestHandler</code> service.<br />
 * Instances of this class are registered at servlet initialization and the can handle the request parameters:
 *
 * <pre>
 * http://my.host.org/forum?request=parameter
 * </pre>
 *
 * @author Luthiger */
@SuppressWarnings("serial")
public class VIFRequestHandler extends RiplaRequestHandler implements RequestHandler {
    private static final String TMPL_REQUEST_URL = "%s?%s=%s&%s=%s"; //$NON-NLS-1$

    @Override
    protected IRequestParameter createRequestParameter(final String inControllerName) {
        return new RequestParameter(inControllerName);
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
        return createRequestedURL(inTask, inIsForum, ApplicationConstants.KEY_GROUP_ID,
                VIFAppHelper.getValueFromSession(Constants.GROUP_ID_KEY));
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
        return String.format(TMPL_REQUEST_URL,
                inIsForum ? VIFAppHelper.getMainForumURL() : VIFAppHelper.getMainAdminURL(),
                org.ripla.web.Constants.KEY_REQUEST_PARAMETER,
                UseCaseHelper.createFullyQualifiedControllerName(inTask),
                inKey, inValue.toString());
    }

    // ---

    private static class RequestParameter extends DftRequestParameter {

        public RequestParameter(final String inControllerName) {
            super(inControllerName);
        }

        @Override
        public void handleParameters(final VaadinSession inSession, final VaadinRequest inRequest,
                final VaadinResponse inResponse) {
            final String lGroupID = inRequest.getParameter(ApplicationConstants.KEY_GROUP_ID);
            if (lGroupID != null) {
                inSession.setAttribute(Constants.GROUP_ID_KEY, Long.parseLong(lGroupID));
            }
            final String lRatingID = inRequest.getParameter(ApplicationConstants.KEY_RATING_ID);
            if (lRatingID != null) {
                final ParameterObject lRating = new ParameterObject();
                lRating.set(ApplicationConstants.KEY_RATING_ID, Long.parseLong(lRatingID));
                inSession.setAttribute(ParameterObject.class, lRating);
            }

            setParameterToSession(inSession);
        }

        @Override
        public boolean process(final IBodyComponent inBody) {
            final Component component = getComponent(getControllerName());
            if (component != null) {
                inBody.setContentView(component);
                return true;
            }
            return false;
        }

    }

}

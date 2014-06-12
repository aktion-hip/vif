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
import org.hip.vif.core.interfaces.IPluggableTask;
import org.hip.vif.core.service.ApplicationData;
import org.hip.vif.core.util.ParameterObject;
import org.hip.vif.web.Constants;
import org.hip.vif.web.controller.TaskManager;
import org.hip.vif.web.interfaces.IDashBoard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.terminal.ParameterHandler;

/**
 * Parameter handler to handle request parameters:<pre>
 * http://my.host.org/forum?request=parameter</pre>
 * 
 * @author Luthiger
 * Created: 27.09.2011
 */
@SuppressWarnings("serial")
public class RequestHandler implements ParameterHandler {
	private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);
	
	private static final String TMPL_REQUEST_URL = "%s?%s=%s&%s=%s"; //$NON-NLS-1$
	
	private RequestParameter requestParameter;

	/* (non-Javadoc)
	 * @see com.vaadin.terminal.ParameterHandler#handleParameters(java.util.Map)
	 */
	@Override
	public void handleParameters(Map<String, String[]> inParameters) {
		if (inParameters.containsKey(ApplicationConstants.KEY_REQUEST_PARAMETER)) {
			requestParameter = new RequestParameter(inParameters.get(ApplicationConstants.KEY_REQUEST_PARAMETER)[0]);
			requestParameter.handleParameters(inParameters);
		}
	}

	/**
	 * Displays the requested view on the dash board.
	 * 
	 * @param inDashBoard {@link IDashBoard}
	 * @return boolean <code>true</code> if the parameter request has been processed successfully, 
	 * <code>false</code> if no parameter request provided.
	 */
	public boolean process(IDashBoard inDashBoard) {
		if (requestParameter == null) return false;
		
		try {
			String lTaskName = requestParameter.prepare();
			requestParameter = null;
			inDashBoard.setContentView(TaskManager.INSTANCE.getForumContent(lTaskName));
			return true;
		} 
		catch (NoTaskFoundException exc) {
			// intentionally left empty
		}
		catch (NumberFormatException exc) {
			LOG.error("Error encountered while processing the request parameters!", exc); //$NON-NLS-1$
		}
		catch (VException exc) {
			LOG.error("Error encountered while processing the request parameters!", exc); //$NON-NLS-1$
		}
		return false;
	}

	/**
	 * Creates the URL to the view of the specified task, e.g.
	 * <code>http://localhost:8084/forum?request=org.hip.vif.forum.groups/org.hip.vif.groups.tasks.RequestsListTask&groupID=21</code>. 
	 * 
	 * @param inTask {@link IPluggableTask}
	 * @param inIsForum boolean <code>true</code> if the requested url should call the forum application, <code>false</code> for the admin application
	 * @return String the bookmarkable URL to the view of the specified task 
	 */
	public static String createRequestedURL(Class<? extends IPluggableTask> inTask, boolean inIsForum) {
		return createRequestedURL(inTask, inIsForum, ApplicationConstants.KEY_GROUP_ID, ApplicationData.getGroupID());
	}
	
	/**
	 * Creates the URL to the view of the specified task, e.g.
	 * <code>http://localhost:8084/forum?request=org.hip.vif.forum.groups/org.hip.vif.groups.tasks.RequestsListTask&key=value</code>. 
	 * 
	 * @param inTask {@link IPluggableTask} the task that is called in the request
	 * @param inIsForum boolean <code>true</code> if the requested url should call the forum application, <code>false</code> for the admin application
	 * @param inKey String the additional parameter's key
	 * @param inValue Long the additional parameter's value
	 * @return the bookmarkable URL to the view of the specified task 
	 */
	public static String createRequestedURL(Class<? extends IPluggableTask> inTask, boolean inIsForum, String inKey, Long inValue) {		
		return String.format(TMPL_REQUEST_URL, inIsForum ? getMainForumURL() : getMainAdminURL(), 
				ApplicationConstants.KEY_REQUEST_PARAMETER,
				UseCaseHelper.createFullyQualifiedControllerName(inTask),
				inKey, inValue.toString());
	}
	
	/**
	 * @return String e.g. <code>http://localhost:8084/forum</code>
	 */
	public static String getMainForumURL() {
		return ApplicationData.getRequestURL().replaceAll(Constants.CONTEXT_ADMIN, Constants.CONTEXT_FORUM);
	}
	
	/**
	 * @return String e.g. <code>http://localhost:8084/admin</code>
	 */
	public static String getMainAdminURL() {
		return ApplicationData.getRequestURL().replaceAll(Constants.CONTEXT_FORUM, Constants.CONTEXT_ADMIN);
	}
	
// ---
	
	private static class RequestParameter {
		private String taskName;
		private Map<String, String> parameters = new HashMap<String, String>();

		RequestParameter(String inTaskName) {
			taskName = inTaskName;
		}
		
		void handleParameters(Map<String, String[]> inParameters) {
			check(ApplicationConstants.KEY_GROUP_ID, inParameters);
			check(ApplicationConstants.KEY_RATING_ID, inParameters);
		}

		private void check(String inKey, Map<String, String[]> inParameters) {
			if (inParameters.containsKey(inKey)) {
				parameters.put(inKey, inParameters.get(inKey)[0]);
			}
		}
		
		String prepare() throws VException, NumberFormatException {
			String lGroupID = parameters.get(ApplicationConstants.KEY_GROUP_ID);
			if (lGroupID != null) {
				ApplicationData.setGroupID(Long.parseLong(lGroupID));				
			}
			String lRatingID = parameters.get(ApplicationConstants.KEY_RATING_ID);
			if (lRatingID != null) {
				ParameterObject lRating = new ParameterObject();
				lRating.set(ApplicationConstants.KEY_RATING_ID, Long.parseLong(lRatingID));
				ApplicationData.setParameters(lRating);
			}
			return taskName;
		}
	}

}

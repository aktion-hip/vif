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

import java.util.HashMap;
import java.util.Map;

import org.hip.vif.web.interfaces.ITaskConfiguration;
import org.hip.vif.web.interfaces.ITaskSet;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.ripla.exceptions.RiplaException;
import org.ripla.web.controllers.AbstractController;
import org.ripla.web.controllers.AbstractForwardingController;
import org.ripla.web.interfaces.IForwardingController;
import org.ripla.web.interfaces.IPluggable;
import org.ripla.web.util.UseCaseHelper;

import com.vaadin.ui.Component;

/**
 * Registry for all forwarding controllers that provides the mapping between the
 * forward alias and the implementing controller.<br />
 * An application has to provide the possibility to forward the control across
 * bundles. To achieve this, a use case controller implementation has to be
 * registered both at the Ripla controller manager and this forward controller
 * registry. Then, this registry can look up the controller by it's forward
 * alias and thus forward the control to the proper controller implementation.
 * 
 * Controllers in bundles can forward to registered controllers as follows:
 * 
 * <pre>
 * sendEvent(ForwardControllerRegistry.ForwardQuestionShow.class);
 * </pre>
 * 
 * @author Luthiger
 */
public enum ForwardControllerRegistry {
	INSTANCE;

	public static final String FORWARD_REQUEST_LIST = "showRequestList"; //$NON-NLS-1$
	public static final String FORWARD_QUESTION_SHOW = "showQuestion"; //$NON-NLS-1$
	public static final String FORWARD_GROUP_ADMIN_PENDING = "showGroupAdminsPending"; //$NON-NLS-1$
	public static final String FORWARD_RATING_FORM = "showRatingForm"; //$NON-NLS-1$
	public static final String FORWARD_PWCHNGE_FORM = "changePWForm"; //$NON-NLS-1$

	private final Map<String, String> targetRegistry = new HashMap<String, String>();
	private final Map<String, IForwardingController> controllerRegistry = new HashMap<String, IForwardingController>();

	// private final Map<String, Class<? extends IForwardingController>>
	// controllerRegistry = new HashMap<String, Class<? extends
	// IForwardingController>>();

	/**
	 * Singleton constructor, fills the task manager with all forwarding tasks.
	 */
	private ForwardControllerRegistry() {
		// register final forwards to this registry
		controllerRegistry.put(FORWARD_REQUEST_LIST, new ForwardRequestsList());
		controllerRegistry
				.put(FORWARD_QUESTION_SHOW, new ForwardQuestionShow());
		controllerRegistry.put(FORWARD_GROUP_ADMIN_PENDING,
				new ForwardGroupAdminPending());
		controllerRegistry.put(FORWARD_RATING_FORM, new ForwardRatingForm());
		controllerRegistry.put(FORWARD_PWCHNGE_FORM, new ForwardPWChangeForm());
	}

	/**
	 * Returns the name of the controller class that is mapped to the specified
	 * alias.
	 * 
	 * @param inAlias
	 *            String the forward alias
	 * @return String the fully qualified controller name of the specified
	 *         forward
	 */
	public String getTargetOf(final IForwardingController inAlias) {
		final IForwardingController mapped = controllerRegistry.get(inAlias
				.getAlias());
		// TODO: handle mapped = null, e.g. by returning the
		// no-controller-found-Controller
		if (mapped == null) {
			return UseCaseHelper
					.createFullyQualifiedControllerName(NoControllerFound.class);
		}
		return UseCaseHelper.createFullyQualifiedControllerName(mapped
				.getTarget());
	}

	// /**
	// * Returns the task class that is registered with the specified alias. <br
	// />
	// * This method is public, as clients have to call it.
	// *
	// * @param inAlias
	// * String
	// * @return IPluggableTask class
	// */
	// public Class<? extends IForwardingController> getTask(final String
	// inAlias) {
	// return controllerRegistry.get(inAlias);
	// }

	/**
	 * Returns the target class that is registered with the specified alias. <br />
	 * This method is package friendly, as only the <code>ForwardTask</code> has
	 * to call it.
	 * 
	 * @param inAlias
	 *            String
	 * @return String the target's fully qualified class name
	 */
	String getTarget(final String inAlias) {
		return targetRegistry.get(inAlias);
	}

	/**
	 * Registeres the specified target class with the specified alias.
	 * 
	 * @param inAlias
	 *            String
	 * @param inTarget
	 *            IPluggableTask class
	 */
	public void registerTarget(final String inAlias,
			final Class<? extends IPluggable> inTarget) {
		final IForwardingController mapping = controllerRegistry.get(inAlias);
		if (mapping != null) {
			mapping.setTarget(inTarget);
		}
	}

	/**
	 * Unregisteres the target task with the specified alias.
	 * 
	 * @param inAlias
	 *            String
	 */
	public void unregisterTarget(final String inAlias) {
		targetRegistry.remove(inAlias);
	}

	// --- inner classes ---

	/**
	 * The task set of all forwarding tasks is configured here.
	 */
	private static class ForwardTaskSet implements ITaskSet {
		final Bundle bundle = FrameworkUtil.getBundle(getClass());

		@Override
		public ITaskConfiguration[] getTaskConfigurations() {
			return new ITaskConfiguration[] { new ITaskConfiguration() {
				@Override
				public String getTaskName() {
					return ForwardRequestsList.class.getName();
				}

				@Override
				public Bundle getBundle() {
					return bundle;
				}
			}, new ITaskConfiguration() {
				@Override
				public String getTaskName() {
					return ForwardQuestionShow.class.getName();
				}

				@Override
				public Bundle getBundle() {
					return bundle;
				}
			}, new ITaskConfiguration() {
				@Override
				public String getTaskName() {
					return ForwardGroupAdminPending.class.getName();
				}

				@Override
				public Bundle getBundle() {
					return bundle;
				}
			}, new ITaskConfiguration() {
				@Override
				public String getTaskName() {
					return ForwardRatingForm.class.getName();
				}

				@Override
				public Bundle getBundle() {
					return bundle;
				}
			}, new ITaskConfiguration() {
				@Override
				public String getTaskName() {
					return ForwardPWChangeForm.class.getName();
				}

				@Override
				public Bundle getBundle() {
					return bundle;
				}
			} };
		}

	}

	// --- implementation of forwarding tasks ---

	public static class ForwardRequestsList extends
			AbstractForwardingController {
		@Override
		public String getAlias() {
			return FORWARD_REQUEST_LIST;
		}
	}

	public static class ForwardQuestionShow extends
			AbstractForwardingController {
		@Override
		public String getAlias() {
			return FORWARD_QUESTION_SHOW;
		}
	}

	public static class ForwardGroupAdminPending extends
			AbstractForwardingController {
		@Override
		public String getAlias() {
			return FORWARD_GROUP_ADMIN_PENDING;
		}
	}

	public static class ForwardRatingForm extends AbstractForwardingController {
		@Override
		public String getAlias() {
			return FORWARD_RATING_FORM;
		}
	}

	public static class ForwardPWChangeForm extends
			AbstractForwardingController {
		@Override
		public String getAlias() {
			return FORWARD_PWCHNGE_FORM;
		}
	}

	public static class NoControllerFound extends AbstractController implements
			IPluggable {

		@Override
		protected String needsPermission() {
			return "";
		}

		@Override
		protected Component runChecked() throws RiplaException {
			// TODO Auto-generated method stub
			return null;
		}
	}

}

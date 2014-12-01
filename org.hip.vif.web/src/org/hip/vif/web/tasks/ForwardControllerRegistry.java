/**
    This package is part of the application VIF.
    Copyright (C) 2014, Benno Luthiger

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

import org.hip.vif.web.Activator;
import org.ripla.exceptions.RiplaException;
import org.ripla.web.controllers.AbstractController;
import org.ripla.web.controllers.AbstractForwardingMapper;
import org.ripla.web.interfaces.IForwardingMapper;
import org.ripla.web.interfaces.IPluggable;
import org.ripla.web.util.ForwardingUtil;
import org.ripla.web.util.UseCaseHelper;

import com.vaadin.ui.Component;

/** Registry for forwarding tasks/controllers.
 * <p>
 * The aim of defining forwarding tasks is that the application needs tasks that are not bundle (i.e. use case)
 * specific. Put in other words, the application needs to provide controller functionality in various use cases that is
 * implemented by some special bundles. The application does this by defining aliases (i.e. forwarding tasks) for tasks
 * that are used in various bundles. The bundles using this functionality call the aliases defined centrally. The alias
 * then forwards the tasks to the implementation provided by the bundle that knows how to handle the task.
 * </p>
 *
 * @see org.ripla.web.interfaces.IForwarding
 * @author lbenno */
public enum ForwardControllerRegistry {
    INSTANCE;

    public enum Alias {
        FORWARD_REQUEST_LIST("showRequestList"), FORWARD_QUESTION_SHOW("showQuestion"), FORWARD_GROUP_ADMIN_PENDING(
                "showGroupAdminsPending"), FORWARD_RATING_FORM("showRatingForm"), FORWARD_PWCHNGE_FORM("changePWForm");

        private String name;

        Alias(final String inName) {
            name = inName;
        }

        public String getName() {
            return name;
        }
    }

    private final ForwardingUtil registry = new ForwardingUtil(5);

    private ForwardControllerRegistry() {
        registry.put(Alias.FORWARD_REQUEST_LIST.getName(), new ForwardRequestsList());
        registry.put(Alias.FORWARD_QUESTION_SHOW.getName(), new ForwardQuestionShow());
        registry.put(Alias.FORWARD_GROUP_ADMIN_PENDING.getName(), new ForwardGroupAdminPending());
        registry.put(Alias.FORWARD_RATING_FORM.getName(), new ForwardRatingForm());
        registry.put(Alias.FORWARD_PWCHNGE_FORM.getName(), new ForwardPWChangeForm());
    }

    /** Registeres the specified target class with the specified alias.<br />
     * The forwarding controller evaluates the <code>IForwarding</code> instances and registeres the forward task
     * implementation here.
     *
     * @param inAlias String
     * @param inTarget {@link IPluggable} */
    public void registerTarget(final String inAlias, final Class<? extends IPluggable> inTarget) {
        final IForwardingMapper mapping = registry.get(inAlias);
        if (mapping != null) {
            mapping.setTarget(inTarget);
        }
    }

    /** Unregisteres the target task with the specified alias.<br />
     * Called when a <code>IForwarding</code> instance is removed.
     *
     * @param inAlias String */
    public void unregisterTarget(final String inAlias) {
        registry.unregisterTarget(inAlias);
    }

    /** Returns the name of the controller class that is mapped to the specified alias.<br />
     * This method translates (i.e. forwards) the alias to a call for the proper task implementation.
     *
     * @param inAlias {@link Alias}
     * @return String the fully qualified controller name of the specified forward */
    public String getTargetOf(final Alias inAlias) {
        final IForwardingMapper mapped = registry.get(inAlias.getName());
        if (mapped == null) {
            return UseCaseHelper.createFullyQualifiedControllerName(NoControllerFound.class);
        }
        return UseCaseHelper.createFullyQualifiedControllerName(mapped
                .getTarget());
    }

    /** Returns the target class that is registered with the specified alias.
     *
     * @param inAlias {@link Alias}
     * @return {@link IPluggable} the forward's implementation */
    public Class<? extends IPluggable> getController(final Alias inAlias) {
        final IForwardingMapper mapped = registry.get(inAlias.getName());
        if (mapped == null) {
            return NoControllerFound.class;
        }
        return mapped.getTarget();
    }

    // --- implementation of forwarding tasks ---

    public static class ForwardRequestsList extends
    AbstractForwardingMapper {
        @Override
        public String getAlias() {
            return Alias.FORWARD_REQUEST_LIST.getName();
        }
    }

    public static class ForwardQuestionShow extends
    AbstractForwardingMapper {
        @Override
        public String getAlias() {
            return Alias.FORWARD_QUESTION_SHOW.getName();
        }
    }

    public static class ForwardGroupAdminPending extends
    AbstractForwardingMapper {
        @Override
        public String getAlias() {
            return Alias.FORWARD_GROUP_ADMIN_PENDING.getName();
        }
    }

    public static class ForwardRatingForm extends AbstractForwardingMapper {
        @Override
        public String getAlias() {
            return Alias.FORWARD_RATING_FORM.getName();
        }
    }

    public static class ForwardPWChangeForm extends
    AbstractForwardingMapper {
        @Override
        public String getAlias() {
            return Alias.FORWARD_PWCHNGE_FORM.getName();
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
            return new DefaultVIFView(Activator.getMessages().getMessage("errmsg.error.contactAdmin"));
        }
    }

}

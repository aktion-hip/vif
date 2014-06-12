package org.hip.vif.app.admin;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ripla.interfaces.IMessages;
import org.ripla.web.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The bundle's activator.
 * 
 * @author Luthiger
 */
public class Activator implements BundleActivator {
	private final static Logger LOG = LoggerFactory.getLogger(Activator.class);
	volatile private static IMessages cMessages; // NOPMD by Luthiger

	@Override
	public void start(final BundleContext inContext) throws Exception { // NOPMD
		cMessages = new Messages();
		LOG.debug("{} started.", inContext.getBundle().getSymbolicName()); //$NON-NLS-1$
	}

	@Override
	public void stop(final BundleContext inContext) throws Exception { // NOPMD
		cMessages = null; // NOPMD by Luthiger
		LOG.debug("{} stopped.", inContext.getBundle().getSymbolicName()); //$NON-NLS-1$
	}

	public static IMessages getMessages() {
		return cMessages == null ? new Messages() : cMessages;
	}

}

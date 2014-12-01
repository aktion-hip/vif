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
package org.hip.vif.web;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Timer;

import org.hip.vif.web.stale.RemoveStaleRequests;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ripla.interfaces.IMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The bundle's activator.
 *
 * @author Luthiger Created: 12.05.2011 */
public class Activator implements BundleActivator {
    private final static Logger LOG = LoggerFactory.getLogger(Activator.class);
    volatile private static IMessages cMessages;
    private Timer timer;

    @Override
    public void start(final BundleContext inContext) throws Exception {
        cMessages = new Messages();
        LOG.debug("{} started.", inContext.getBundle().getSymbolicName()); //$NON-NLS-1$

        // start the thread to remove stale tasks
        timer = new Timer();
        timer.scheduleAtFixedRate(new RemoveStaleRequests(),
                getTomorrowMorning4am(), Constants.ONCE_PER_DAY);
    }

    @Override
    public void stop(final BundleContext inContext) throws Exception {
        cMessages = null;
        LOG.debug("{} stopped.", inContext.getBundle().getSymbolicName()); //$NON-NLS-1$
    }

    public static IMessages getMessages() {
        return cMessages != null ? cMessages : new TestMessages(Locale.ENGLISH);
    }

    private static Date getTomorrowMorning4am() {
        final Calendar lTomorrow = new GregorianCalendar();
        lTomorrow.add(Calendar.DATE, 1);
        final Calendar out = new GregorianCalendar(
                lTomorrow.get(Calendar.YEAR), lTomorrow.get(Calendar.MONTH),
                lTomorrow.get(Calendar.DATE), 4, 0);
        return out.getTime();
    }

}

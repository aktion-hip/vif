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

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.core.util.HtmlCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.VaadinSession;

/** Helper class for bean wrappers.
 *
 * @author Luthiger Created: 21.05.2011 */
public class BeanWrapperHelper {
    private static final Logger LOG = LoggerFactory
            .getLogger(BeanWrapperHelper.class);

    /** Returns a <code>Long</code> value from the domain object.
     *
     * @param inKey String the value's key in the domain object
     * @param inDomainObject {@link GeneralDomainObject} the object
     * @return Long the value extracted from the domain object */
    public static Long getLong(final String inKey,
            final GeneralDomainObject inDomainObject) {
        try {
            final Object lValue = get(inKey, inDomainObject);
            return lValue == null ? new Long(0) : new Long(lValue.toString());
        } catch (final Exception exc) {
            LOG.error("Error getting Long value from domain object.", exc);
        }
        return new Long(0);
    }

    /** Returns a <code>Integer</code> value from the domain object.
     *
     * @param inKey String the value's key in the domain object
     * @param inDomainObject {@link GeneralDomainObject} the object
     * @return Integer the value extracted from the domain object */
    public static Integer getInteger(final String inKey,
            final GeneralDomainObject inDomainObject) {
        try {
            final Object lValue = get(inKey, inDomainObject);
            return lValue == null ? new Integer(0) : new Integer(
                    lValue.toString());
        } catch (final Exception exc) {
            LOG.error("Error getting Integer value from domain object.", exc);
        }
        return new Integer(0);
    }

    /** Returns a <code>String</code> value from the domain object.
     *
     * @param inKey String the value's key in the domain object
     * @param inDomainObject {@link GeneralDomainObject} the object
     * @return String the value extracted from the domain object */
    public static String getString(final String inKey,
            final GeneralDomainObject inDomainObject) {
        try {
            final Object lValue = get(inKey, inDomainObject);
            return lValue == null ? "" : lValue.toString();
        } catch (final Exception exc) {
            LOG.error("Error getting String value from domain object.", exc);
        }
        return "";
    }

    private static Object get(final String inKey,
            final GeneralDomainObject inDomainObject) throws Exception {
        return inDomainObject.get(inKey);
    }

    /** Returns a plain <code>String</code> value from the domain object. Possible <code>Textile</code> format markers
     * are removed.
     *
     * @param inKey String the value's key in the domain object
     * @param inDomainObject {@link GeneralDomainObject} the object
     * @return String the value extracted from the domain object with any style removed */
    public static String getPlain(final String inKey,
            final GeneralDomainObject inDomainObject) {
        try {
            final Object lValue = get(inKey, inDomainObject);
            return lValue == null ? "" : HtmlCleaner.toPlain(lValue.toString());
        } catch (final Exception exc) {
            LOG.error("Error getting html String value from domain object.",
                    exc);
        }
        return "";
    }

    /** Returns a Date value formatted as String.
     *
     * @param inKey String the value's key in the domain object
     * @param inDomainObject {@link GeneralDomainObject} the object
     * @return String the date value formatted as <code>String</code> */
    public static String getFormattedDate(final String inKey,
            final GeneralDomainObject inDomainObject) {
        try {
            final Object lValue = inDomainObject.get(inKey);
            if (lValue == null)
                return "";

            final DateFormat lDateFormat = new SimpleDateFormat(
                    PreferencesHandler.INSTANCE.getDatePattern(),
                    getAppLocale());
            final Calendar lCalendar = lDateFormat.getCalendar();

            if (lValue instanceof Timestamp) {
                lCalendar.setTime((Timestamp) lValue);
            }
            if (lValue instanceof Date) {
                lCalendar.setTime((Date) lValue);
            }
            if (lValue instanceof Time) {
                lCalendar.setTime((Time) lValue);
            }
            return lDateFormat.format(lCalendar.getTime());
        } catch (final Exception exc) {
            LOG.error("Error getting Date value from domain object.", exc);
        }
        return "";
    }

    private static Locale getAppLocale() {
        if (VaadinSession.getCurrent() == null) {
            return Locale.ENGLISH;
        }
        try {
            VaadinSession.getCurrent().getLockInstance().lock();
            return VaadinSession.getCurrent().getLocale();
        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }
    }

}

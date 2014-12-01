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
package org.hip.vif.web;

import java.util.Locale;

/** Messages class for testing purposes.
 *
 * @author lbenno */
public class TestMessages extends Messages {
    public static final String PROP_LOCALE = "vif.testing.locale";

    private Locale locale;

    public TestMessages(final Locale inLocale) {
        final String language = System.getProperty(PROP_LOCALE);
        if (language == null) {
            locale = inLocale;
        }
        else {
            locale = new Locale(language);
        }
    }

    @Override
    protected Locale getLocaleChecked() {
        return locale;
    };

    @Override
    protected String getBaseName() {
        // a special properties file in the test fragment
        return "testingMsg";
    }

}

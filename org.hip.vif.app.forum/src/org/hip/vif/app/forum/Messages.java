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
package org.hip.vif.app.forum;

import org.ripla.web.util.AbstractWebMessages;

/** Bundle specific messages.
 *
 * @author Luthiger */
public class Messages extends AbstractWebMessages {
    private static final String BASE_NAME = "messages"; //$NON-NLS-1$

    @Override
    protected ClassLoader getLoader() {
        return getClass().getClassLoader();
    }

    @Override
    protected String getBaseName() {
        return BASE_NAME;
    }

}
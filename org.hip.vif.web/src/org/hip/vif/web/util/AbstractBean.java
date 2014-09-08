/**
    This package is part of the persistency layer of the application VIF.
    Copyright (C) 2003-2014, Benno Luthiger

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

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.exc.VException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Abstract class for POJO bean classes wrapping a <code>DomainObject</code> model.
 *
 * @author lbenno */
public abstract class AbstractBean {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractBean.class);

    private final DomainObject model;

    protected AbstractBean(final DomainObject inModel) {
        model = inModel;
    }

    protected String getString(final String inKey) {
        return BeanWrapperHelper.getString(inKey, model);
    }

    protected Long getLong(final String inKey) {
        return BeanWrapperHelper.getLong(inKey, model);
    }

    protected Integer getInteger(final String inKey) {
        return BeanWrapperHelper.getInteger(inKey, model);
    }

    protected void setValue(final String inKey, final Object inValue) throws VException {
        try {
            model.set(inKey, inValue);
        } catch (final VException exc) {
            LOG.error("Error encoutered while writing input value from form to model!", exc);
            throw exc;
        }
    }

}

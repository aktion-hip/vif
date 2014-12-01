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

import java.util.ArrayList;
import java.util.List;

import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.kernel.dbaccess.DataSourceRegistry.DBSourceParameter;
import org.ripla.web.util.GenericSelect.IProcessor;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.ComboBox;

/** Helper class to create a select of registered DB access factories/drivers.
 *
 * @author Luthiger Created: 27.01.2012 */
public class DBDriverSelect {

    /** Create the <code>Select</code> widget containing the registered DB drivers.
     *
     * @param inProperty {@link Property}
     * @param inWidth int
     * @param inAllowNull boolean
     * @param inProcessor {@link IProcessor} may be <code>null</code>
     * @return {@link ComboBox} */
    @SuppressWarnings("serial")
    public static ComboBox getDBDriverSelection(
            final Property<String> inProperty, final int inWidth,
            final boolean inAllowNull, final IProcessor inProcessor) {
        final DBDriverContainer lContainer = DBDriverContainer
                .getDBDriverSelection(inProperty.getValue().toString());
        final ComboBox outSelect = new ComboBox(null, lContainer);
        outSelect.select(lContainer.getActiveDriver());
        outSelect.setStyleName("vif-select"); //$NON-NLS-1$
        outSelect.setWidth(inWidth, Unit.PIXELS);
        outSelect.setNullSelectionAllowed(inAllowNull);
        outSelect.setImmediate(true);
        outSelect.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent inEvent) {
                final String lItemID = ((DBDriverBean) inEvent.getProperty()
                        .getValue()).getID();
                inProperty.setValue(lItemID);
                if (inProcessor != null) {
                    inProcessor.process(lItemID);
                }
            }
        });
        return outSelect;
    }

    @SuppressWarnings("serial")
    public static ComboBox getDBDriverSelection(final int inWidth,
            final boolean inAllowNull, final IProcessor inProcessor) {
        final ComboBox outSelect = new ComboBox(null, getDbDrivers());
        outSelect.setStyleName("vif-select"); //$NON-NLS-1$
        outSelect.setWidth(inWidth, Unit.PIXELS);
        outSelect.setNullSelectionAllowed(inAllowNull);
        outSelect.setImmediate(true);
        outSelect.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent inEvent) {
                final String lItemID = ((DBDriverBean) inEvent.getProperty()
                        .getValue()).getID();
                if (inProcessor != null) {
                    inProcessor.process(lItemID);
                }
            }
        });
        return outSelect;
    }

    private static List<DBDriverBean> getDbDrivers() {
        final List<DBDriverBean> out = new ArrayList<DBDriverBean>();
        for (final DBSourceParameter lDriverParameter : DataSourceRegistry.INSTANCE
                .getDBSourceParameters()) {
            out.add(new DBDriverBean(lDriverParameter));
        }
        return out;
    }

    // ---

    @SuppressWarnings("serial")
    private static class DBDriverContainer extends
    BeanItemContainer<DBDriverBean> {
        private DBDriverBean activeDriver;

        private DBDriverContainer() {
            super(DBDriverBean.class);
        }

        static DBDriverContainer getDBDriverSelection(
                final String inActiveDriver) {
            final DBDriverContainer out = new DBDriverContainer();
            for (final DBSourceParameter lDriverParameter : DataSourceRegistry.INSTANCE
                    .getDBSourceParameters()) {
                final DBDriverBean lBean = new DBDriverBean(lDriverParameter);
                out.addItem(lBean);
                if (inActiveDriver.equals(lDriverParameter.getFactoryID())) {
                    out.setActiveDriver(lBean);
                }
            }
            return out;
        }

        private void setActiveDriver(final DBDriverBean inDriver) {
            activeDriver = inDriver;
        }

        DBDriverBean getActiveDriver() {
            return activeDriver;
        }

    }

    public static class DBDriverBean {
        private final DBSourceParameter driverParameter;

        DBDriverBean(final DBSourceParameter inDriverParameter) {
            driverParameter = inDriverParameter;
        }

        public String getID() {
            return driverParameter.getFactoryID();
        }

        @Override
        public String toString() {
            return driverParameter.getFactoryName();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((driverParameter.getFactoryID() == null) ? 0 : driverParameter.getFactoryID().hashCode());
            result = prime * result
                    + ((driverParameter.getFactoryName() == null) ? 0 : driverParameter.getFactoryName().hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final DBDriverBean other = (DBDriverBean) obj;
            if (driverParameter.getFactoryID() == null) {
                if (other.driverParameter.getFactoryID() != null)
                    return false;
            } else if (!driverParameter.getFactoryID().equals(other.driverParameter.getFactoryID()))
                return false;
            if (driverParameter.getFactoryName() == null) {
                if (other.driverParameter.getFactoryName() != null)
                    return false;
            } else if (!driverParameter.getFactoryName().equals(other.driverParameter.getFactoryName()))
                return false;
            return true;
        }

    }

    public static DBDriverBean createDriverBean(final String inDriverId) {
        for (final DBSourceParameter lDriverParameter : DataSourceRegistry.INSTANCE
                .getDBSourceParameters()) {
            if (inDriverId.equals(lDriverParameter.getFactoryID())) {
                return new DBDriverBean(lDriverParameter);
            }
        }
        return null;
    }

    // /**
    // * Process the selection.
    // *
    // * @author Luthiger
    // * Created: 09.02.2012
    // */
    // public static interface IProcessor {
    // /**
    // * Do something depending on the selected item.
    // *
    // * @param inItemID String the selected item's id
    // */
    // void process(String inItemID);
    // }

}

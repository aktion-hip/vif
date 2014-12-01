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
package org.hip.vif.forum.usersettings.data;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.impl.JoinRatingsToRater;
import org.hip.vif.core.bom.impl.RatingsHome;
import org.hip.vif.forum.usersettings.data.RatingsContainer.RatingItem;
import org.hip.vif.web.util.AbstractBean;

import com.vaadin.data.util.BeanItem;

/** This is a POJO bean for the <code>JoinRatingsToRater</code> model.
 *
 * @author lbenno */
public class RatingsBean extends AbstractBean {
    public static final String FN_CORRECTNESS = "correctness";
    public static final String FN_EFFICIENCY = "efficiency";
    public static final String FN_ETIQUETTE = "etiquette";
    public static final String FN_REMARK = "remark";

    private RatingsBean(final JoinRatingsToRater inRating) {
        super(inRating);
    }

    public RatingItem getCorrectness() {
        return RatingsContainer.getRatingItem(getInteger(RatingsHome.KEY_CORRECTNESS));
    }

    public void setCorrectness(final RatingItem inValue) throws VException {
        setValue(RatingsHome.KEY_CORRECTNESS, inValue.getId());
    }

    public RatingItem getEfficiency() {
        return RatingsContainer.getRatingItem(getInteger(RatingsHome.KEY_EFFICIENCY));
    }

    public void setEfficiency(final RatingItem inValue) throws VException {
        setValue(RatingsHome.KEY_EFFICIENCY, inValue.getId());
    }

    public RatingItem getEtiquette() {
        return RatingsContainer.getRatingItem(getInteger(RatingsHome.KEY_ETIQUETTE));
    }

    public void setEtiquette(final RatingItem inValue) throws VException {
        setValue(RatingsHome.KEY_ETIQUETTE, inValue.getId());
    }

    public String getRemark() {
        return getString(RatingsHome.KEY_REMARK);
    }

    public void setRemark(final String inValue) throws VException {
        setValue(RatingsHome.KEY_REMARK, inValue);
    }

    // ---

    /** Factory method to create Vaadin bean items of rating model instances.
     *
     * @param inRatings {@link JoinRatingsToRater} the model the bean instance should be bound to
     * @return BeanItem&lt;RatingsBean> the bean instance */
    public static BeanItem<RatingsBean> createRatingsBean(final JoinRatingsToRater inRatings) {
        return new BeanItem<RatingsBean>(new RatingsBean(inRatings));
    }

}

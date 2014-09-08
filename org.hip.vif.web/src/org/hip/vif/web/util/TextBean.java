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

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.TextHome;

import com.vaadin.data.util.BeanItem;

/** This is a POJO bean for the <code>Text</code> model.
 *
 * @author lbenno */
public class TextBean extends AbstractBean {

    public static final String FN_AUTHOR = "author";
    public static final String FN_TITLE = "title";
    public static final String FN_SUBTITLE = "subtitle";
    public static final String FN_YEAR = "year";
    public static final String FN_PUBLICATION = "publication";
    public static final String FN_PUBLISHER = "publisher";
    public static final String FN_PLACE = "place";
    public static final String FN_PAGES = "pages";
    public static final String FN_VOLUME = "volume";
    public static final String FN_NUMBER = "number";
    public static final String FN_REMARKS = "remarks";

    /** Private constructor.
     *
     * @param inText {@link Text} */
    private TextBean(final Text inText) {
        super(inText);
    }

    public String getAuthor() {
        return getString(TextHome.KEY_AUTHOR);
    }

    public void setAuthor(final String inValue) throws VException {
        setValue(TextHome.KEY_AUTHOR, inValue);
    }

    public String getTitle() {
        return getString(TextHome.KEY_TITLE);
    }

    public void setTitle(final String inValue) throws VException {
        setValue(TextHome.KEY_TITLE, inValue);
    }

    public String getSubtitle() {
        return getString(TextHome.KEY_SUBTITLE);
    }

    public void setSubtitle(final String inValue) throws VException {
        setValue(TextHome.KEY_SUBTITLE, inValue);
    }

    public String getYear() {
        return getString(TextHome.KEY_YEAR);
    }

    public void setYear(final String inValue) throws VException {
        setValue(TextHome.KEY_YEAR, inValue);
    }

    public String getPublication() {
        return getString(TextHome.KEY_PUBLICATION);
    }

    public void setPublication(final String inValue) throws VException {
        setValue(TextHome.KEY_PUBLICATION, inValue);
    }

    public String getPublisher() {
        return getString(TextHome.KEY_PUBLISHER);
    }

    public void setPublisher(final String inValue) throws VException {
        setValue(TextHome.KEY_PUBLISHER, inValue);
    }

    public String getPlace() {
        return getString(TextHome.KEY_PLACE);
    }

    public void setPlace(final String inValue) throws VException {
        setValue(TextHome.KEY_PLACE, inValue);
    }

    public String getPages() {
        return getString(TextHome.KEY_PAGES);
    }

    public void setPages(final String inValue) throws VException {
        setValue(TextHome.KEY_PAGES, inValue);
    }

    public String getVolume() {
        return getString(TextHome.KEY_VOLUME);
    }

    public void setVolume(final String inValue) throws VException {
        setValue(TextHome.KEY_VOLUME, inValue);
    }

    public String getNumber() {
        return getString(TextHome.KEY_NUMBER);
    }

    public void setNumber(final String inValue) throws VException {
        setValue(TextHome.KEY_NUMBER, inValue);
    }

    public String getRemarks() {
        return getString(TextHome.KEY_REMARK);
    }

    public void setRemarks(final String inValue) throws VException {
        setValue(TextHome.KEY_REMARK, inValue);
    }

    // ---
    public static BeanItem<TextBean> createTextBean(final Text inText) {
        return new BeanItem<TextBean>(new TextBean(inText));
    }

}

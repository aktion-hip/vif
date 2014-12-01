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
package org.hip.vif.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hip.vif.web.Constants;
import org.ripla.web.util.LanguageSelect.LanguagesContainer;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.ComboBox;

/** Helper class to create a combo to select the language.
 *
 * @author lbenno */
public final class LanguageSelectHelper {

    private LanguageSelectHelper() {
        // prevent instantiation
    }

    /** Convenience method, create a language select containing the available languages.
     *
     * @param inLanguageProperty {@link Property} the property containing the language definition actually configured
     * @return {@link ComboBox} */
    @SuppressWarnings({ "serial" })
    public static ComboBox getLanguageSelection(final Property<String> inLanguageProperty) {
        final ComboBox outSelect = createSelect(inLanguageProperty.getValue().toString());
        outSelect.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent inEvent) {
                inLanguageProperty.setValue(inEvent.getProperty().getValue().toString());
            }
        });
        return outSelect;
    }

    /** Convenience method, create a language select containing the available languages.
     *
     * @param inActiveLanguage String
     * @return {@link ComboBox} */
    private static ComboBox createSelect(final String inActiveLanguage) {
        final LanguagesContainer lLanguages = LanguagesContainer.getLanguages(Constants.LANGUAGES, inActiveLanguage);
        final ComboBox outSelect = new ComboBox(null, lLanguages);
        outSelect.select(lLanguages.getActiveLanguage());
        outSelect.setStyleName("vif-select"); //$NON-NLS-1$
        outSelect.setWidth(55, Unit.PIXELS);
        outSelect.setNullSelectionAllowed(false);
        outSelect.setImmediate(true);
        return outSelect;
    }

    /** Convenience method: returns the application's languages as list.
     *
     * @return List&lt;String> */
    public static List<String> getLanguages() {
        final List<String> out = new ArrayList<String>(Constants.LANGUAGES.length);
        for (final Locale language : Constants.LANGUAGES) {
            out.add(language.getLanguage());
        }
        return out;
    }

}

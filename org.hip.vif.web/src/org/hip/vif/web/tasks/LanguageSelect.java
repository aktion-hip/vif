/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

package org.hip.vif.web.tasks;

import java.util.Locale;

import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.service.ApplicationData;
import org.hip.vif.web.Activator;
import org.hip.vif.web.Constants;
import org.hip.vif.web.interfaces.IDisposable;
import org.hip.vif.web.layout.VIFApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;

/**
 * Displays language selector and handles change of language.
 * 
 * @author Luthiger
 * Created: 04.06.2011
 */
@SuppressWarnings("serial")
public class LanguageSelect extends CustomComponent implements IDisposable {
	private static final Logger LOG = LoggerFactory.getLogger(LanguageSelect.class);
	
	private VIFApplication app;
	
	/**
	 * Constructor
	 * 
	 * @param inApplication {@link VIFApplication}
	 */
	private LanguageSelect(VIFApplication inApplication) {
		setStyleName("vif-language-select"); //$NON-NLS-1$
		app = inApplication;

		HorizontalLayout lLayout = new HorizontalLayout();
		setCompositionRoot(lLayout);
		lLayout.setWidth("100%"); //$NON-NLS-1$
		lLayout.setHeight(22, UNITS_PIXELS);
		
		Label lLabel = new Label(Activator.getMessages().getMessage("service.bar.label.language"), Label.CONTENT_XHTML); //$NON-NLS-1$
		lLabel.setStyleName("vif-servicebar-label"); //$NON-NLS-1$
		lLayout.addComponent(lLabel);
		lLayout.setComponentAlignment(lLabel, Alignment.MIDDLE_RIGHT);
		lLayout.setExpandRatio(lLabel, 1);
		
		final Select lSelect = createSelect(inApplication.getActorLanguage().getLanguage());
		lSelect.addListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent inEvent) {
				Locale lNew = ((LocaleWrapper)lSelect.getValue()).getLocale();
				Locale lOld = ApplicationData.getLocale();
				if (!lOld.equals(lNew)) {
					setActorLanguage(lNew.getLanguage());
					app.refreshDash();
					LOG.debug("Changed locale to '{}'.", lNew.getLanguage()); //$NON-NLS-1$
				}
			}
		});
		lLayout.addComponent(lSelect);	
	}
	
	private void setActorLanguage(String inLanguage) {
		try {
			Member lMember = BOMHelper.getMemberCacheHome().getMember(ApplicationData.getActor().getActorID());
			lMember.setUserSettings(ApplicationConstants.USER_SETTINGS_LANGUAGE, inLanguage);
		}
		catch (Exception exc) {
			LOG.error("Error encountered while setting the actor's language user setting!", exc); //$NON-NLS-1$
		}		
	}
	
	@Override
	public void dispose() {
		app = null;
	}
	
	private static Select createSelect(String inActiveLanguage) {
		LanguagesContainer lLanguages = LanguagesContainer.getLanguages(inActiveLanguage);
		final Select outSelect = new Select(null, lLanguages);
		outSelect.select(lLanguages.getActiveLanguage());
		outSelect.setStyleName("vif-select"); //$NON-NLS-1$
		outSelect.setWidth(55, UNITS_PIXELS);
		outSelect.setNullSelectionAllowed(false);
		outSelect.setImmediate(true);
		return outSelect;
	}
	
	/**
	 * Factory method, creates an instance of the <code>LanguageSelect</code>.
	 * 
	 * @param inApplication {@link VIFApplication}
	 * @return {@link LanguageSelect}
	 */
	public static LanguageSelect getLanguageSelection(VIFApplication inApplication) {
		return new LanguageSelect(inApplication);
	}

	/**
	 * Convenience method, create a language select containing the available languages. 
	 * 
	 * @param inLanguageProperty {@link Property} the property containing the language definition actually configured
	 * @return {@link Select}
	 */
	public static Select getLanguageSelection(final Property inLanguageProperty) {
		Select outSelect = createSelect(inLanguageProperty.getValue().toString());
		outSelect.addListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent inEvent) {
				inLanguageProperty.setValue(inEvent.getProperty().getValue().toString());
			}
		});
		return outSelect;
	}
	
// ---
	
	private static class LanguagesContainer extends BeanItemContainer<LocaleWrapper> {
		private LocaleWrapper activeLanguage;
		
		private LanguagesContainer() {
			super(LocaleWrapper.class);
		}
		
		static LanguagesContainer getLanguages(String inActiveLanguage) {
			LanguagesContainer out = new LanguagesContainer();
			for (Locale lLocale : Constants.LANGUAGES) {
				LocaleWrapper lWrapped = new LocaleWrapper(lLocale);
				out.addItem(lWrapped);
				if (inActiveLanguage.equals(lLocale.getLanguage())) {
					out.setActiveLanguage(lWrapped);
				}
			}
			return out;
		}
		private void setActiveLanguage(LocaleWrapper inActiveLanguage) {
			activeLanguage = inActiveLanguage;
		}
		LocaleWrapper getActiveLanguage() {
			return activeLanguage;
		}
	}
	
	private static class LocaleWrapper {
		private Locale locale;

		LocaleWrapper(Locale inLocale) {
			locale = inLocale;
		}
		Locale getLocale() {
			return locale;
		}
		@Override
		public String toString() {
			return locale.getLanguage();
		}
	}

}

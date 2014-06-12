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

package org.hip.vif.web.layout;

import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.exc.InvalidAuthenticationException;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.service.ApplicationData;
import org.hip.vif.core.service.MemberUtility;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.web.Activator;
import org.hip.vif.web.interfaces.ISkin;
import org.hip.vif.web.util.SkinRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

/**
 * The application's login view, displayed when the application is called.
 * 
 * @author Luthiger
 * Created: 12.05.2011
 */
@SuppressWarnings("serial")
public class VIFLogin extends CustomComponent implements Button.ClickListener {
	private static final Logger LOG = LoggerFactory.getLogger(VIFLogin.class);
	
	private Form form;
	private Button loginButton;
	private boolean isAdmin = false;

	/**
	 * Constructor
	 * 
	 * @param inApplication {@link VIFApplication}
	 */
	public VIFLogin(VIFApplication inApplication) {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		
		//add space at top
		isAdmin = inApplication.getIsAdmin();
		createForm(lLayout);
		loginButton.addListener(inApplication);
	}
	
	private void createForm(VerticalLayout inLayout) {
		IMessages lMessages = Activator.getMessages();
		form = new Form();
		form.setDescription(getWelcome(lMessages.getMessage("login.welcome"))); //$NON-NLS-1$
		form.setWidth(400, UNITS_PIXELS);
		form.setStyleName("vif-login-form"); //$NON-NLS-1$
		
		loginButton = new Button(lMessages.getMessage("login.button")); //$NON-NLS-1$
		loginButton.addListener(this);
		loginButton.setClickShortcut(KeyCode.ENTER);
		form.getFooter().addComponent(loginButton);
		
		Collection<String> props = new Vector<String>();
		props.add(LoginData.NAME_USERID);
		props.add(LoginData.NAME_PASSWORD);		
		form.setItemDataSource(new BeanItem<LoginData>(new LoginData(), props));
		form.setFormFieldFactory(new LoginFieldFacory());
		form.setItemDataSource(new BeanItem<LoginData>(new LoginData()));
		form.setVisibleItemProperties(props);
		form.setImmediate(false);
		form.focus();

		inLayout.addComponent(form);
		inLayout.setComponentAlignment(form, Alignment.TOP_CENTER);
	}
	
	private String getWelcome(String inDfltWelcome) {
		try {
			ISkin lSkin = SkinRegistry.INSTANCE.getActiveSkin();
			return isAdmin ? lSkin.getWelcomeAdmin() : lSkin.getWelcomeForum();
		}
		catch (VException exc) {
			//intentionally left empty
		}
		return inDfltWelcome;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
	 */
	public void buttonClick(ClickEvent inEvent) {
		if (inEvent.getButton() != loginButton) return;
		
		IMessages lMessages = Activator.getMessages();
		Item lData = form.getItemDataSource();
		String lUserId = lData.getItemProperty(LoginData.NAME_USERID).getValue().toString();
		String lPassword = lData.getItemProperty(LoginData.NAME_PASSWORD).getValue().toString();
	
		//check login as guest
		if (lUserId.length() == 0 && lPassword.length() == 0) {
			if (!isAllowedForGuests()) {
				loginFailedFeedback(lMessages.getMessage("login.guest.not.allowed")); //$NON-NLS-1$
				return;
			}
		}
		
		String lFeedback = ""; //$NON-NLS-1$
		try {
			MemberUtility.INSTANCE.getActiveAuthenticator().checkAuthentication(lUserId, lPassword);
			return;
		}
		catch (InvalidAuthenticationException exc) {
			lFeedback = lMessages.getMessage("login.failed"); //$NON-NLS-1$
		} 
		catch (Exception exc) {
			lFeedback = lMessages.getMessage("login.error"); //$NON-NLS-1$
			LOG.error("Login error!", exc); //$NON-NLS-1$
		}
		loginFailedFeedback(lFeedback);
	}

	/**
	 * @param inFeedback
	 */
	private void loginFailedFeedback(String inFeedback) {
		getApplication().setUser(null);
		ApplicationData.setActor(null);
		getWindow().showNotification(inFeedback, Notification.TYPE_WARNING_MESSAGE);
		form.focus();
	}
	
	private boolean isAllowedForGuests() {
		try {
			return Boolean.parseBoolean(PreferencesHandler.INSTANCE.get(PreferencesHandler.KEY_GUEST_ALLOW));
		}
		catch (IOException exc) {
			LOG.error("Failed to retrieve the guest login setting!", exc); //$NON-NLS-1$
		}
		return false;
	}
	
	public boolean checkSource(Component inComponent) {
		return loginButton.equals(inComponent);
	}
	
// ---
	
	public static class LoginData {
		static final String NAME_USERID = "userid"; //$NON-NLS-1$
		static final String NAME_PASSWORD = "password"; //$NON-NLS-1$
		
		private String userid=""; //$NON-NLS-1$
		private String password=""; //$NON-NLS-1$
		
		public void setUserid(String userid) {
			this.userid = userid;
		}
		public String getUserid() {
			return userid;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getPassword() {
			return password;
		}
	}
	
	private class LoginFieldFacory implements FormFieldFactory {

		public Field createField(Item item, Object propertyId, Component uiContext) {
			IMessages lMessages = Activator.getMessages();
			Field lField = null;
			if ("userid".equals(propertyId)) { //$NON-NLS-1$
				lField = new TextField(String.format("%s:", lMessages.getMessage("login.field.user"))); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else if ("password".equals(propertyId)) { //$NON-NLS-1$
				lField = new PasswordField(String.format("%s:", lMessages.getMessage("login.field.pass"))); //$NON-NLS-1$ //$NON-NLS-2$
			}
			lField.setRequired(false);
			return lField;
		}
	}

}

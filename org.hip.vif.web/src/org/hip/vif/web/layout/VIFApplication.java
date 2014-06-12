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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.hip.vif.core.Activator;
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.service.ApplicationData;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.core.util.DBConnectionProber;
import org.hip.vif.web.controller.PermissionRegistry;
import org.hip.vif.web.dash.VIFDashBoard;
import org.hip.vif.web.interfaces.IDashBoard;
import org.hip.vif.web.interfaces.IDisposable;
import org.hip.vif.web.interfaces.IWorkflowListener;
import org.hip.vif.web.internal.handler.ApplicationDash;
import org.hip.vif.web.tasks.DBAccessWorkflow;
import org.hip.vif.web.tasks.DBAccessWorkflow.ReturnCode;
import org.hip.vif.web.tasks.DBAccessWorkflowItems.WorkflowException;
import org.hip.vif.web.util.RequestHandler;
import org.hip.vif.web.util.SkinRegistry;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

/**
 * Base class for applications developed with VIF.
 * 
 * @author Luthiger
 * Created: 11.05.2011
 * @see Application
 */
@SuppressWarnings("serial")
public abstract class VIFApplication extends Application implements Component.Listener, HttpServletRequestListener, IWorkflowListener {
	private static final Logger LOG = LoggerFactory.getLogger(VIFApplication.class);
	
	private VerticalLayout contentPane;
	private VIFLogin loginView;
	private Component footer;
	private RequestHandler requestHandler;
	private String requestURL;

	/**
	 * Subclasses must call this method in their <code>init()</code> method.
	 * 
	 * @param inMain {@link Window}
	 */
	protected void afterInit(Window inMain) {
		initializeContext();
		initializeLayout(inMain);
		
		ApplicationData.create(this);
		ApplicationData.initLocale(PreferencesHandler.INSTANCE.getLocale(true, getLocale()));
		ApplicationData.setRequestURL(requestURL);
		ApplicationData.setWindow(inMain);
		
		ApplicationDash.create(this);

		//initial execution: DB access configuration
		DBConnectionProber lProber = new DBConnectionProber();
		if (lProber.needsDBConfiguration()) {
			try {
				DBAccessWorkflow.getInitialWorkflow(inMain, this).startWorkflow();
				return;
			}
			catch (WorkflowException exc) {
				LOG.error("Error encountered during initial DB access configuration!", exc); //$NON-NLS-1$
				inMain.showNotification(Activator.getMessages().getMessage("errmsg.init.configuration"), Notification.TYPE_ERROR_MESSAGE); //$NON-NLS-1$
				return;
			}
		}
		else  if (lProber.isUndefined()) {
			LOG.error("Undefined problem encountered while trying to access the DB connection!"); //$NON-NLS-1$
			inMain.showNotification(Activator.getMessages().getMessage("errmsg.init.dbaccess"), Notification.TYPE_ERROR_MESSAGE); //$NON-NLS-1$
			return;
		}

		//normal execution
		PermissionRegistry.INSTANCE.createPermissions();
		setMainComponent(getLoginView());
		requestHandler = setRequestHandler(inMain);
		setLogoutURL(getVIFLogoutURL());
		
		LOG.trace("Initialized VIF application.");		 //$NON-NLS-1$
	}
	
	private void initializeContext() {
		String lContextDir = getContext().getBaseDirectory().getPath();
		if (lContextDir.length() <= 1) {
			//embedded app, i.e. Jetty
			VSys.useConfPath(false);
			String lConfigPath = new File("").getAbsolutePath(); //$NON-NLS-1$
			VSys.setContextPath(lConfigPath);
		}
		else {
			//OSGi in servlet container, e.g. Tomcat
			VSys.useConfPath(true);
			VSys.setContextPath(lContextDir);
		}
		DataSourceRegistry.INSTANCE.setActiveConfiguration(PreferencesHandler.INSTANCE.getDBConfiguration());
	}
	
	private String getVIFLogoutURL() {
		try {
			String outURL = PreferencesHandler.INSTANCE.get(PreferencesHandler.KEY_LOGOUT_URL);
			return outURL == null || outURL.length() == 0 ? null : outURL;
		}
		catch (IOException exc) {
			return null;
		}
	}

	/**
	 * Callback method for the initial configuration workflow (i.e. <code>InitialAccessController</code>).
	 * 
	 * @param inReturnCode {@link ReturnCode}
	 * @param inMessage String the message to display in the error case
	 */
	public void workflowExit(ReturnCode inReturnCode, String inMessage) {
		switch (inReturnCode) {
		case OK_LOGIN:			
			PermissionRegistry.INSTANCE.createPermissions();
			setMainComponent(getLoginView());
			requestHandler = setRequestHandler(getMainWindow());
			break;
		case OK_SU:			
			PermissionRegistry.INSTANCE.createPermissions();
			requestHandler = setRequestHandler(getMainWindow());
			try {
				ApplicationData.getActor().refreshAuthorization();
			}
			catch (BOMChangeValueException exc) {
				LOG.error("Could not refresh the SU's authorization!", exc); //$NON-NLS-1$
			}
			showAfterLogin();
			break;
		case ERROR:
			getMainWindow().showNotification(inMessage, Notification.TYPE_ERROR_MESSAGE);
			break;
		default:
			getMainWindow().showNotification(Activator.getMessages().getMessage("errmsg.init.generic"), Notification.TYPE_WARNING_MESSAGE);			 //$NON-NLS-1$
		}
	}
	
	/**
	 * @return String the actor's language according to his language settings
	 */
	public Locale getActorLanguage() {
		try {
			Member lMember = BOMHelper.getMemberCacheHome().getMember(ApplicationData.getActor().getActorID());
			String outLanguage = lMember.getUserSettings(ApplicationConstants.USER_SETTINGS_LANGUAGE);
			return outLanguage == null ? ApplicationData.getLocale() : new Locale(outLanguage);
		}
		catch (Exception exc) {
			LOG.error("Error encountered while retrieving the actor's language user setting!", exc); //$NON-NLS-1$
		}
		return ApplicationData.getLocale();
	}


	/**
	 * @param inMain
	 */
	private void initializeLayout(Window inMain) {
		inMain.setStyleName("vif-window"); //$NON-NLS-1$
		inMain.addListener(new Window.CloseListener() {
			public void windowClose(CloseEvent inEvent) {
				close();
			}
		});

		VerticalLayout lLayout = new VerticalLayout();
		lLayout.setSizeFull();
		
		addHeader(lLayout);
		createContentPane(lLayout);
		addFooter(lLayout);
		
		inMain.getContent().setSizeFull();
		getMainWindow().setContent(lLayout);
		
		try {
			setTheme(SkinRegistry.INSTANCE.getActiveSkin().getSkinID());
		}
		catch (VException exc) {
			LOG.error("Run application with default skin.", exc); //$NON-NLS-1$
			setTheme(ApplicationConstants.DFLT_SKIN);
		}
	}
	
	private RequestHandler setRequestHandler(Window inMain) {
		RequestHandler out = new RequestHandler();
		inMain.addParameterHandler(out);
		return out;
	}

	private void addHeader(VerticalLayout inLayout) {
		Layout lHeader = new HorizontalLayout();
		lHeader.setWidth("100%"); //$NON-NLS-1$
		lHeader.setHeight(80, Sizeable.UNITS_PIXELS);
		try {
			lHeader.addComponent(SkinRegistry.INSTANCE.getActiveSkin().getHeader());
		}
		catch (VException exc) {
			lHeader.addComponent(new Label("VIF Forum")); //$NON-NLS-1$
			LOG.error("Could not display the skin's header!", exc); //$NON-NLS-1$
		}

		inLayout.addComponent(lHeader);
		inLayout.setExpandRatio(lHeader, 0);
	}
	
	private void addFooter(VerticalLayout inLayout) {
		try {
			footer = SkinRegistry.INSTANCE.getActiveSkin().getFooter();
		}
		catch (VException exc) {
			footer = VIFFooter.createFooter(VIFFooter.DFT_FOOTER_TEXT);
			LOG.error("Could not display the skin's footer!", exc); //$NON-NLS-1$
		}
		inLayout.addComponent(footer);
		inLayout.setExpandRatio(footer, 0);
	}
	
	private void createContentPane(VerticalLayout inLayout) {
		contentPane = new VerticalLayout();
		contentPane.setStyleName("vif-content"); //$NON-NLS-1$
		contentPane.setSizeFull();
		inLayout.addComponent(contentPane);
		inLayout.setExpandRatio(contentPane, 1);		
	}

	private void setMainComponent(Component inComponent) {
		contentPane.removeAllComponents();
		contentPane.addComponent(inComponent);
	}
	
	private Component getLoginView() {
		if (loginView == null) {
			loginView = new VIFLogin(this);			
		}
		return loginView;
	}
	
	protected Component getContentPane() {
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.Component.Listener#componentEvent(com.vaadin.ui.Component.Event)
	 */
	public void componentEvent(Event inEvent) {
		if (loginView.checkSource(inEvent.getComponent())) {
			if (!isLoggedIn()) return;
			if (footer instanceof VIFFooter) {
				((VIFFooter)footer).setLoggedIn();
			}
			showAfterLogin();
		}
	}
	
	/**
	 * @return boolean <code>true</code> if the user logged in
	 */
	public boolean isLoggedIn() {
		return ApplicationData.getActor() != null;
	}

	private void showAfterLogin() {
		ApplicationData.initLocale(getActorLanguage());
		
		Component lDashboard = getAfterLoginView();
		ApplicationDash.setDash((VIFDashBoard) lDashboard);
		
		//we set the dashboard as main component
		setMainComponent(lDashboard);
		
		//then we process the request parameters
		if (lDashboard instanceof IDashBoard) {
			if (!requestHandler.process((IDashBoard)lDashboard)) {
				((IDashBoard)lDashboard).showDefault();
			}
		}
	}

	abstract protected Component getAfterLoginView();

	/**
	 * Refresh the application's view e.g. after the user changed the language.
	 */
	public void refreshDash() {
		showAfterLogin();
	}

	private void disposeComponents() {
		Iterator<Component> lComponents = contentPane.getComponentIterator();
		while (lComponents.hasNext()) {
			Component lComponent = lComponents.next();
			if (lComponent instanceof IDisposable) {
				((IDisposable) lComponent).dispose();				
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.terminal.gwt.server.HttpServletRequestListener#onRequestStart(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void onRequestStart(HttpServletRequest inRequest, HttpServletResponse inResponse) {
		requestURL = new String(inRequest.getRequestURL());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.vaadin.terminal.gwt.server.HttpServletRequestListener#onRequestEnd(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void onRequestEnd(HttpServletRequest inRequest, HttpServletResponse inResponse) {
		//intentionally left empty		
	}
	
	@Override
	public String getVersion() {
		String lVersion = FrameworkUtil.getBundle(VIFApplication.class).getHeaders().get("Bundle-Version"); //$NON-NLS-1$
		return lVersion == null ? super.getVersion() : lVersion.toString(); //$NON-NLS-1$
	}

	@Override
	public void close() {
		disposeComponents();
		super.close();
	}

	public abstract boolean getIsAdmin();
	
}

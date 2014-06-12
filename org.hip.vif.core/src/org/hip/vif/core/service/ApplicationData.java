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

package org.hip.vif.core.service;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import org.hip.vif.core.interfaces.IPluggableTask;
import org.hip.vif.core.member.IActor;
import org.hip.vif.core.util.ParameterObject;
import org.hip.vif.core.util.TaskStack;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Window;

/**
 * Object that holds data for one user session.
 * 
 * @author Luthiger
 * Created: 12.05.2011
 */
@SuppressWarnings("serial")
public class ApplicationData implements TransactionListener, Serializable {
	private static final Long DFT_TEXT_VERSION = -1l;
	
	private Locale locale; // current locale
	
	private IActor actor;
	private Long groupID;
	private Long questionID;
	private Long completionID;
	private Long textID;
	private Long textVersion;
	private ParameterObject parameters;
	private String requestURL;
	private TaskStack taskStack;
	private Window window;
	private MenuItem activeMenuItem;
	private Map<String, MenuItem> menuMap;
	
	private static ThreadLocal<ApplicationData> instance = new ThreadLocal<ApplicationData>();
	
	/**
	 * Create the session data holder. This method has to be called in the application's init method.
	 * 
	 * @param inApplication {@link Application} the application
	 */
	public static void create(Application inApplication) {
		ApplicationData lSessionData = new ApplicationData();
		instance.set(lSessionData);
		inApplication.getContext().addTransactionListener(lSessionData);
	}
	
	/**
	 * Initializes the <code>ResourceBundle</code> for the i18n messages.
	 * This method has to be called in the application's init method and whenever the user changes the locale.
	 * 
	 * @param inLocale {@link Locale}
	 */
	public static void initLocale(Locale inLocale) {
		instance.get().locale = inLocale;
		instance.get().taskStack = new TaskStack();
		
		instance.get().groupID = 0l;
		instance.get().questionID = 0l;
		instance.get().completionID = 0l;
		instance.get().textID = 0l;
		instance.get().textVersion = DFT_TEXT_VERSION;
	}

	public void transactionStart(Application inApplication, Object inTransactionData) {
		instance.set(this);
	}

	public void transactionEnd(Application inApplication, Object inTransactionData) {
		instance.set(null);
	}
	
	public static Locale getLocale() {
		return instance.get().locale;
	}

	public static void setActor(IActor inActor) {
		instance.get().actor = inActor;
	}
	
	/**
	 * @return {@link IActor} the authenticated user
	 */
	public static IActor getActor() {
		return instance.get().actor;
	}

	public static void setGroupID(Long inGroupID) {
		instance.get().groupID = inGroupID;
	}

	/**
	 * @return Long the ID of the group actually selected
	 */
	public static Long getGroupID() {
		return instance.get().groupID;
	}
	
	public static void setQuestionID(Long inQuestionID) {
		instance.get().questionID = inQuestionID;
	}
	
	/**
	 * @return Long the ID of the question actually selected
	 */
	public static Long getQuestionID() {
		return instance.get().questionID;
	}

	public static void setCompletionID(Long inCompletionID) {
		instance.get().completionID = inCompletionID;
	}

	/**
	 * @return Long the ID of the completion actually selected
	 */
	public static Long getCompletionID() {
		return instance.get().completionID;
	}
	
	public static void setTextID(Long inTextID) {
		instance.get().textID = inTextID;
	}
	
	/**
	 * @return Long the ID of the text actually selected
	 */
	public static Long getTextID() {
		return instance.get().textID;
	}
	
	public static void setTextVersion(String inTextVersion) {
		try {
			instance.get().textVersion = Long.parseLong(inTextVersion);
		}
		catch (Exception exc) {
			instance.get().textVersion = DFT_TEXT_VERSION;			
		}
	}
	
	/**
	 * @return Long the version of the text actually selected
	 */
	public static Long getTextVersion() {
		return instance.get().textVersion;
	}
	
	/**
	 * Set's a generic <code>ParameterObject</code> to the application context. <br/>Use e.g.:<pre>
	 * ParameterObject lParameters = new ParameterObject();
	 * lParameters.set(Constants.KEY_PARAMETER_MEMBER, lMemberID);
	 * setParameters(lParameters);
	 * </pre>
	 * 
	 * @param inParameters ParameterObject
	 */
	public static void setParameters(ParameterObject inParameters) {
		instance.get().parameters = inParameters;
	}
	
	/**
	 * Returns the generic <code>ParameterObject</code> previously set.
	 * 
	 * @return {@link ParameterObject}
	 */
	public static ParameterObject getParameters() {
		return instance.get().parameters;
	}

	/**
	 * Sets the application's request url, i.e. <code>http://localhost:8084/admin</code>.
	 * 
	 * @param inRequestURL String
	 */
	public static void setRequestURL(String inRequestURL) {
		instance.get().requestURL = inRequestURL;
	}
	
	/**
	 * @return String the application's request url, e.g. <code>http://localhost:8084/admin</code>
	 */
	public static String getRequestURL() {
		return instance.get().requestURL;
	}
	
	/**
	 * Sets the application's main window
	 * 
	 * @param inWindow {@link Window}
	 */
	public static void setWindow(Window inWindow) {
		instance.get().window = inWindow;
	}
	
	/**
	 * @return {@link Window} the application's main window
	 */
	public static Window getWindow() {
		return instance.get().window;
	}
	
	/**
	 * Pushes a task to the task history.
	 * 
	 * @param inTask {@link IPluggableTask}
	 * @return {@link IPluggableTask} the pushed task
	 */
	public static IPluggableTask pushToHistory(IPluggableTask inTask) {
		return instance.get().taskStack.push(inTask);
	}
	
	/**
	 * Pops the last task from the task stack.
	 * 
	 * @return {@link IPluggableTask} the last task
	 */
	public static IPluggableTask popLastTask() {
		return instance.get().taskStack.pop();
	}
	
	/**
	 * Returns the last task without removing it.
	 * 
	 * @return {@link IPluggableTask} the last task
	 */
	public static IPluggableTask getLastTask() {
		return instance.get().taskStack.peek();
	}
	
	/**
	 * @param inMenuMap Map&lt;String, MenuItem> sets the map of top level menu items to the bundle symbolic names
	 */
	public static void setMenuMap(Map<String, MenuItem> inMenuMap) {
		instance.get().menuMap = inMenuMap;
	}
	
	/**
	 * Sets the bundle's menu item active.<br />
	 * Each bundle is providing its menu entries. The menu can be styled to indicate the active menu 
	 * (style <code>vif-menu-active</code>), i.e. the menu from the bundle of the task actually processed. 
	 * 
	 * @param inBundleName String the bundle's symbolic name
	 */
	public static void setActiveMenuItem(String inBundleName) {
		MenuItem lOldItem = instance.get().activeMenuItem;
		if (lOldItem != null) {
			lOldItem.setStyleName("");
		}
		MenuItem lNewItem = instance.get().menuMap.get(inBundleName);
		if (lNewItem != null) {
			lNewItem.setStyleName("vif-menu-active");
		}
		instance.get().activeMenuItem = lNewItem;
	}
	
}

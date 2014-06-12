/*
	This package is part of the servlet framework used for the application VIF.
	Copyright (C) 2001, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.kernel.servlet.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import org.hip.kernel.servlet.Context;
import org.hip.kernel.servlet.HtmlView;
import org.hip.kernel.servlet.MIMEFile;
import org.hip.kernel.sys.Assert;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.util.NameValue;
import org.hip.kernel.util.NameValueList;

/**
 * 	Baseclass of all contexts.
 *
 *	@author	Benno Luthiger
 *  @see org.hip.kernel.servlet.Context
 */
public abstract class AbstractContext implements Context, Serializable {
	//constants
	/**Keyname for the session*/
	public static final String SESSION_NAME			= "Session";
	/**Keyname for the response*/
	public static final String RESPONSE_KEY			= "Response";
	/**Keyname of the request type */
	public static final String REQUEST_TYPE 		= "requestType";
	/**Keyname of the context, i.e. key for context object in session */
	public static final String CONTEXT		 		= "context";
	/**Keyname of the userId */
	public static final String USER_ID_KEY 			= "userId";
	/**Keyname of the password*/
	public static final String USER_PASSWORD_KEY 	= "pwd";
	/**Keyname of a view/page shown in the browser*/
	public static final String VIEW					= "view";
	/**Keyname of a MIME file provided through the browser*/
	public static final String MIME_FILE			= "mimeFile";
	/**Keyname of query result view*/
	public static final String QUERY_RESULT_VIEW_KEY = "queryResultView";
	/**Keyname of the language*/
	public static final String LANGUAGE_KEY			= "language";
	/**Keyname of newSession-property */
	public static final String NEW_SESSION_KEY 		= "newSession";
	/**Keyname of userAuthorized-property */
	public static final String USER_AUTHORIZED_KEY	= "userAuthorized";	
	/**Keyname of error message */
	public static final String ERROR_MESSAGE_KEY	= "errorMessage";
	/**Keyname of sub application path */
	private static final String SERVLET_PATH		= "servletPath";
	/**Keyname of remote address field */
	private static final String REMOTE_ADDR 		= "remoteAddr";
	/**Keyname of remote host field */
	private static final String REMOTE_HOST 		= "remoteHost";
	/**Keyname of request URL */
	private static final String REQUEST_URL			= "requestURL";
	
	//instance attributes
	private Hashtable<String, Object> properties = null;
	private Hashtable<String, Object> parameters = null;
	private Hashtable<String, FileItem> fileItems = null;
	
	public AbstractContext() {
		super();
	}
	
	/**
	 * Returns the value with key <code>inName</code> if set in the context.
	 * Returns null if not found.
	 *
	 * @param String inName - Key of a value 
	 * @return value as Object
	 */
	public Object get(String inName) {
		return properties().get(inName);
	}
	
	/**
	 * @return java.lang.String
	 */	 
	public String getLanguage() {
		String outLanguage = (get(LANGUAGE_KEY) != null)? get(LANGUAGE_KEY).toString() : VSys.dftLanguage;
		
		return outLanguage;
	}
	
	/**
	 * Returns an enumeration of the paramternames set in the context. (Parameternames from the request to the servlet) 
	 *
	 * @return java.util.Enumeration<String> Enumeration of parameters
	 */
	public Enumeration<String> getParameterNames() {
		return this.parameters().keys();
	}
	
	/**
	 * Returns a Collection view of the parameter names contained in this context.
	 * 
	 * @return Set<String> parameter names
	 */
	public Set<String> getParameterNames2() {
		return this.parameters().keySet();
	}
	
	/**
	 * Returns the parameter with key <code>inName</code> as string-value. (Parameter from the request to the servlet) 
	 *
	 * @param inName java.lang.String Name of parameter
	 * @return java.lang.String  Stringvalue of a requestparameter.
	 */
	public String getParameterValue(String inName) {
		if(this.parameters().containsKey(inName)) {
			return this.parameters().get(inName).toString();
		} 
		else {
			return "";
		}
	}

	/**
	 * Returns the file item with the specified name or <code>null</code>.
	 * 
	 * @param inName String Name of parameter.
	 * @return FileItem
	 */
	public FileItem getFileItem(String inName) {
		return this.fileItems().get(inName);
	}
	
	/**
	 * Returns an array of parameter values if found in context. Returns an empty
	 * String array (length: 0) if no parameters of the specified name are found in the context.
	 *
	 * @param inName java.lang.String Name of parameter
	 * @return java.lang.String[] Array of Parameters
	 */
	public String[] getParameterValueArray(String inName) {
		String[] outValues = null;
		
		if (this.parameters().containsKey(inName)) {
			if (this.parameters().get(inName) instanceof String[]) {
				outValues = (String[]) parameters().get(inName);
			} 
			else {
				outValues = new String[1];
				outValues[0] = (String) parameters().get(inName);
			}
		} 
		else {
			outValues = new String[0];
		}
		return outValues;
	}
	
	/**
	 * @return java.lang.String
	 */
	public String getUserID() {
		String outUserID = (get(USER_ID_KEY) != null)? get(USER_ID_KEY).toString() : "";
		
		return outUserID;
	}
	
	/**
	 * @return org.hip.kernel.servlet.HtmlView
	 */
	public HtmlView getView() {
		Object lView = get(VIEW);
		return lView != null ? (HtmlView)lView : null;
	}
	
	/**
	 * @return {@link MIMEFile}
	 */
	public MIMEFile getMIMEFile() {
		Object lFile = get(MIME_FILE);
		return  lFile != null ? (MIMEFile)lFile : null;
	}
	
	/**
	 * Returns true if context contains parameter with name: <code>inParamaterName</code>
	 *
	 * @return boolean -true if parameter set.
	 * @param inParameterName java.lang.String Name of parameter
	 */
	public boolean hasParameter(String inParameterName) {
		return this.parameters().containsKey(inParameterName);
	}
	
	/**
	 * Returns parameters.
	 *
	 * @return java.util.Hashtable Table of parameters
	 */
	private Hashtable<String, Object> parameters() {
		if (parameters == null) {
			parameters = new Hashtable<String, Object>();
		}
		return parameters;
	}
	
	/**
	 * @return Hashtable<String, FileItem>
	 */
	private Hashtable<String, FileItem> fileItems() {
		if (fileItems == null) {
			fileItems = new Hashtable<String, FileItem>();
		}
		return fileItems;
	}
	
	/**
	 * Returns properties.
	 *
	 * @return java.util.Hashtable Table of properties
	 */
	private Hashtable<String, Object> properties() {
		if (properties == null) {
			properties = new Hashtable<String, Object>();
		}
		return properties;
	}
	
	/**
	 * Sets property <code>inValue</code> in context with key or name <code>inName</code>.
	 *
	 * @param inName java.lang.String key or name of property
	 * @param inValue java.lang.Object value of property
	 */
	public void set(String inName, Object inValue) {
		if (inValue == null) {
			properties().remove(inName);
		}
		else {			
			properties().put(inName, inValue);
		}
	}
	
	/**
	 * @param inLanguage java.lang.String
	 */
	public void setLanguage(String inLanguage) {
		set(LANGUAGE_KEY, inLanguage);
	}
	
	/**
	 * Sets a Name-Value-List as parameters of this context.
	 *
	 * @param inList org.hip.kernel.util.NameValueList
	 */
	public void setParameters(NameValueList inList) {
		this.parameters().clear();
		for (NameValue lNameValue : inList.getNameValues2()) {
			parameters().put(lNameValue.getName(), lNameValue.getValue());
		}
	}
	
	/**
	 * Sets a Name-Value-List as file items of this context.
	 * 
	 * @param inList {@link NameValueList}
	 */
	public void setFileItems(NameValueList inList) {
		this.fileItems().clear();
		for (NameValue lNameValue : inList.getNameValues2()) {
			fileItems().put(lNameValue.getName(), (FileItem)lNameValue.getValue());
		}
	}
	
	/**
	 * Sets the specified parameter with the specified value to the context.
	 * 
	 * @param inName java.lang.String
	 * @param inValue java.lang.String
	 */
	public void setParameter(String inName, String inValue) {
		VSys.assertNotNull(Assert.ERROR, this, "setParameter", inName);
		VSys.assertNotNull(Assert.ERROR, this, "setParameter", inValue);
		
		parameters().put(inName, inValue);
	}
	
	/**
	 * @param inUserID java.lang.String
	 */
	public void setUserID(String inUserID) {
		set(USER_ID_KEY, inUserID);
	}
	
	/**
	 * @param inView org.hip.kernel.servlet.HtmlView
	 */
	public void setView(HtmlView inView) {
		set(VIEW, inView);
	}
	
	/**
	 * @param inFile {@link MIMEFile}
	 */
	public void setMIMEFile(MIMEFile inFile) {
		set(MIME_FILE, inFile);
	}
	
	/**
	 * Returns string with names and values of all parameters in the context.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		StringBuffer lBuffer = new StringBuffer();
		Enumeration<String> lNames = this.properties().keys();
		Enumeration<Object> lValues = properties().elements();
		
		while (lNames.hasMoreElements()) {
			String lName 	= lNames.nextElement().toString();
			String lValue 	= lValues.nextElement().toString();
			lBuffer.append(lName + "=" + lValue +  ", ");
		}
	
		return lBuffer.toString();
	}
	
	/**
	 * The path to the sub application in the same webapp of the servlet engine.
	 * 
	 * @return String
	 */
	public String getServletPath() {
		Object lPath = get(SERVLET_PATH);
		return (lPath != null ? (String)lPath : "");
	}
	
	/**
	 * Sets the path of the sub application to the context.
	 * 
	 * @param inPath String
	 */
	public void setServletPath(String inPath) {
		set(SERVLET_PATH, inPath);
	}
	
	/**
	 * Sets the Internet Protocol (IP) address of the client
	 * that sent the request.
	 * 
	 * @param inRemoteAddr String containing the IP address
	 */
	public void setRemoteAddr(String inRemoteAddr) {
		set(REMOTE_ADDR, inRemoteAddr);
	}

	/**
	 * Sets the fully qualified name of the client that sent the request.
	 * 
	 * @param inRemoteHost String containing the fully qualified name
	 */
	public void setRemoteHost(String inRemoteHost) {
		set(REMOTE_HOST, inRemoteHost);
	}
	
	/**
	 * @return String containing the IP address of the client that sent the request.
	 */
	public String getRemoteAddr() {
		return (String)get(REMOTE_ADDR);
	}
	
	/**
	 * @return String containing the fully qualified name of the client.
	 */
	public String getRemoteHost() {
		return (String)get(REMOTE_HOST);
	}

	/**
	 * Sets the URL the client used to make the request, 
	 * i.e. <code>http://localhost:8080/vifapp/forum</code>.
	 * 
	 * @param StringBuffer inRequestURL
	 */
	public void setRequestURL(StringBuffer inRequestURL) {
		set(REQUEST_URL, inRequestURL);
	}
	
	/**
	 * @return StringBuffer Reconstructs the URL the client used to make the request, 
	 * i.e. <code>http://localhost:8080/vifapp/forum</code>.
	 */
	public StringBuffer getRequestURL() {
		return (StringBuffer)get(REQUEST_URL);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		properties.remove(SESSION_NAME);
		properties.remove(RESPONSE_KEY);
		properties.remove(VIEW);
		out.writeObject(properties);
		out.writeObject(parameters);
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		properties = (Hashtable<String, Object>)in.readObject();
		parameters = (Hashtable<String, Object>)in.readObject();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.servlet.Context#dispose()
	 */
	public void dispose() {
		properties.clear();
		parameters.clear();
		properties = null;
		parameters = null;
	}
}

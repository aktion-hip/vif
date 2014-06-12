package org.hip.kernel.exc;

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

import java.util.Date;
import	java.util.Locale;
import	java.util.Hashtable;
import	java.util.ResourceBundle;
import	java.util.PropertyResourceBundle;

import	org.hip.kernel.sys.VSys;

/**
 * Container for exception data. Handles multilingual error messages.
 * 
 * @author Benno Luthiger
 */ 
public class ExceptionData {
	//constants
	public  static final String DEFAULT_MESSAGE					= "no message text provided";
	private static final String DEFAULT_ERR_MSG_BUNDLE_BASENAME	= "ErrorMessages";
	private static final String ERROR_MSG_BASENAME				= "org.hip.vif.error.msg.basename";
	
	//class attributes
	static public	String		DATA_DATETIME		= "dateTime"		;
	static public	String		DATA_EXCEPTION		= "exception"		;
	static public	String		DATA_MSG_SOURCE		= "msg_source"		;
	static public	String		DATA_MSG_PROVIDER	= "msg_provider"	;
	static public	String		DATA_MSG_KEY		= "msg_key"			;
	static public	String		DATA_MSG_PARMS		= "msg_parms"		;
	
	private static 	String		cMsgBundleBasename	= DEFAULT_ERR_MSG_BUNDLE_BASENAME;
	private static 	Hashtable<Locale, ResourceBundle>	cMessagesHashtable	= null;
		
	//instance attributes
	private String      	msgCache           	= null;
	private String     	 	msgKey				= null;
	private Locale			locale				= VSys.dftLocale;

	private Hashtable<String, Object>		data				= null;

	//static initializer
	static {
		String lPropertyValue = null;
	
		//get trace switch
		try {	
			lPropertyValue = VSys.getVSysProperties().getProperty(ERROR_MSG_BASENAME);		
		} 
		catch (Exception exc) {
			//intentionally left empty
		}
	
		if(lPropertyValue != null) {
			cMsgBundleBasename = lPropertyValue;
		}
	}
	
	/**
	 * Constructor for ExceptionData with the specified Throwable
	 * 
	 * @param inThrowable java.lang.Throwable
	 */
	public ExceptionData(Throwable inThrowable) {
		this(inThrowable, DEFAULT_MESSAGE);
	}

	/**
	 * Constructor for ExceptionData with the Throwable and simple message
	 * 
	 * @param inThrowable 		java.lang.Throwable
	 * @param inSimpleMessage 	java.lang.String
	 */
	public ExceptionData(Throwable inThrowable, String inSimpleMessage) {
		super();
		msgCache = inSimpleMessage;
	}

	/**
	 * Constructor for ExceptionData with exception source and message key.
	 * 
	 * @param inThrowable java.lang.Throwable
	 * @param inMsgSource java.lang.String Source of exception.
	 * @param inMsgKey java.lang.String The key of the error message in the ressource bundle.
	 */
	public ExceptionData(Throwable inThrowable, String inMsgSource, String inMsgKey) {
		this(inThrowable, inMsgSource, inMsgKey, null);
	}
	
	/**
	 * Constructor for ExceptionData with exception source, message key and parameters.
	 * 
	 * @param inThrowable 	java.lang.Throwable
	 * @param inMsgSource 	java.lang.String Source of exception.
	 * @param inMsgKey 	java.lang.String The key of the error message in the ressource bundle.
	 * @param inMsgParms 	java.lang.Object[]
	 */
	public ExceptionData(Throwable inThrowable, String inMsgSource, String inMsgKey, Object[] inMsgParms) {
		super();
		msgKey = inMsgKey;
		data().put(ExceptionData.DATA_DATETIME, new Date());
		
		if (inThrowable != null)
			data().put(ExceptionData.DATA_EXCEPTION, inThrowable);
	
		if (inMsgSource != null)
			data().put(ExceptionData.DATA_MSG_SOURCE, inMsgSource);
	
		if (inMsgKey != null)
			data().put(ExceptionData.DATA_MSG_KEY, inMsgKey);
	
		if (inMsgParms != null)
			data().put(ExceptionData.DATA_MSG_PARMS, inMsgParms);
	}
	
	private synchronized Hashtable<String, Object> data() {
		
		if (data == null) {
			data = new Hashtable<String, Object>(11);
		}
		return data;
	}
	
	/**
	 * 	Returns object with the specified name.
	 * 
	 * 	@return java.lang.Object
	 * 	@param inName java.lang.String
	 */
	public Object get(String inName) {
		return data().get(inName);
	}
	
	/**
	 * 	Returns the localized Message based on the default locale.
	 * 
	 * 	@return java.lang.String
	 */
	public String getLocalizedMessage( ) {
		return this.getLocalizedMessage(locale);
	}

	/**
	 * Returns the localized message based on the specified locale.
	 * 
	 * @param inLocale java.util.Locale
	 * @return java.lang.String
	 */
	public String getLocalizedMessage(Locale inLocale) {
		
		if (msgKey == null) {
			if (msgCache == null) {
				return DEFAULT_MESSAGE;
			} 
			else {
				return msgCache;
			}
		}
		return messages(inLocale).getString(msgKey);
	}
	
	/**
	 * Returns the message based on the default locale.
	 * 
	 * @return java.lang.String
	 */
	public String getMessage() {
		return this.getMessage(locale);
	}

	/**
	 * Returns the message with the specified id based on the default locale
	 * 
	 * @param inId java.lang.String
	 * @return java.lang.String
	 */
	public static String getMessage(String inId) {
		return getMessage(VSys.dftLocale, inId);
	}
	
	/**
	 * Returns the message based on the specified locale
	 * 
	 * @param inLocale java.util.Locale
	 * @return java.lang.String
	 */
	public String getMessage(Locale inLocale) {
		return getLocalizedMessage(inLocale);
	}
	
	/**
	 * Returns the message with the specified id based on the specified locale
	 * 
	 * @param inLocale java.util.Locale
	 * @param inId java.lang.String
	 * @return java.lang.String
	 */
	public static String getMessage(Locale inLocale, String inId) {
		return messages(inLocale).getString(inId);
	}
	
	/**
	 * Returns the message parameter at the specified position
	 * 
	 * @param inIndex int
	 * @return java.lang.Object
	 */
	public Object getMessageParameter(int inIndex) {
		try { 
			Object[] lParameters = (Object[])data().get(ExceptionData.DATA_MSG_PARMS);
			return lParameters[inIndex];
		} 
		catch (ArrayIndexOutOfBoundsException exc) {
			ExceptionHandler.cDefaultHandler.handle(exc);
			return null;
		}
		catch (NullPointerException exc) {
			ExceptionHandler.cDefaultHandler.handle(exc);
			return null;
		}
	}
	
	/**
	 * Dynamically loads the Bundle for the requested Language (ONLY LANGUAGE!!)
	 *
	 * @return java.util.ResourceBundle
	 */
	private static synchronized ResourceBundle messages(Locale inLocale) {
		ResourceBundle outBundle = null;
	
		//create table for bundles	
		if(cMessagesHashtable == null) {
			cMessagesHashtable = new Hashtable<Locale, ResourceBundle>(5);
		}
	
		//load bundle if not already loaded
		if((outBundle = cMessagesHashtable.get(inLocale)) == null) {
			outBundle = PropertyResourceBundle.getBundle(cMsgBundleBasename, new Locale(inLocale.getLanguage(), ""));
			cMessagesHashtable.put(inLocale, outBundle);
		}
		
		return outBundle;
	}
	
	/**
	 * Puts a name-value pair to this object.
	 * 
	 * @param inName java.lang.String
	 * @param inValue java.lang.Object
	 */
	public void put( String inName, Object inValue ) {
		// Pre: name_not_null
		if (VSys.assertNotNull(this, "put", inName)) return;
	
		data().put(inName, inValue);
	}
	
	/**
	 * Sets this exception data's locale for that language sensitive messages can be returned.
	 * 
	 * @param inLocale Locale
	 */
	public void setLocale(Locale inLocale) {
		locale = inLocale;
	}
	
	/**
	 * Returns a string representation of the object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return getMessage();
	}
}

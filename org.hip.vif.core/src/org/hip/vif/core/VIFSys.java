/*
	This package is part of the application VIF.
	Copyright (C) 2001, Benno Luthiger

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
package org.hip.vif.core;

import java.io.File;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.hip.kernel.sys.VSys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a wrapper around java.lang.System. 
 *
 * @author Benno Luthiger
 */
 
public class VIFSys extends VSys {
	private static final Logger LOG = LoggerFactory.getLogger(VIFSys.class);
	
	//constants
	private static final String SYS_NAME = "vif"; //$NON-NLS-1$

	private static final String ERR_MSG_BUNDLE_BASENAME = "VIFErrMessages";
	private static final String MSG_BUNDLE_BASENAME 	= "VIFMessages";
	
	/** a default Locale */
	private static final Locale cDefaultLocale = new Locale(dftLanguage, "");
	
	/**
	 * Sets the application configuration.
	 */
	public static void configureVSys() {
		VSys.setSysName(SYS_NAME);
		VSys.useConfPath(false);
		String lConfigPath = new File("").getAbsolutePath();
		VSys.setContextPath(lConfigPath);
		LOG.debug("Set the application's config path to {}.", lConfigPath);
	}
	
	/**
	 * Returns the default ErrorMessage bundle.
	 * 
	 * @return java.util.ResourceBundle
	 */
	public static ResourceBundle getErrMessageBundle() {
		return getErrMessageBundle(cDefaultLocale);
	}
	
	/**
	 * Returns an ErrorMessage bundle for a specific language.
	 * 
	 * @return java.util.ResourceBundle
	 * @param inLanguage java.lang.String the desired language
	 */
	public static ResourceBundle getErrMessageBundle(String inLanguage) {
		return getErrMessageBundle(new Locale(inLanguage, ""));
	}

	/**
	 * Returns an ErrorMessage bundle for a specific locale.
	 * 
	 * @return java.util.ResourceBundle
	 * @param inLocale java.util.Locale the desired locale
	 */
	public static ResourceBundle getErrMessageBundle(Locale inLocale) {
		return PropertyResourceBundle.getBundle(ERR_MSG_BUNDLE_BASENAME, inLocale);
	}

	/**
	 * Returns a default ErrorMessage.
	 * 
	 * @return java.lang.String
	 * @param inId java.lang.String
	 */
	public static String getErrorMessage(String inId) {
		return getErrorMessage(cDefaultLocale, inId);
	}

	/**
	 * Returns an ErrorMessage in a specific language.
	 *
	 * @return java.lang.String
	 * @param inLanguage java.lang.String the desired language
	 * @param inId java.lang.String
	 */ 
	public static String getErrorMessage(String inLanguage, String inId) {
		return getErrMessageBundle(new Locale(inLanguage, "")).getString(inId);
	}

	/**
	 * Returns an ErrorMessage for a specific locale.
	 * 
	 * @return java.lang.String
	 * @param inId java.lang.String
	 * @param inLocale java.util.Locale the desired locale
	 */
	public static String getErrorMessage(Locale inLocale, String inId) {
		return getErrMessageBundle(inLocale).getString(inId);
	}

	/**
	 * Gets an error string from the resource bundle and formats it with the argument
	 * 
	 * @param inKey	String the key used to get the bundle value, must not be null
	 * @param inArgs Object[]
	 * @return String
	 */
	public static String getFormattedErrorMessage(String inId, Object... inArgs) {
		return String.format(getErrorMessage(inId), inArgs);
	}

	/**
	 * Gets a localized error string from the resource bundle and formats it with the argument
	 * 
	 * @param inLanguage String the desired language
	 * @param inKey	String the key used to get the bundle value, must not be null
	 * @param inArgs Object[]
	 * @return String
	 */
	public static String getFormattedErrorMessage(String inLanguage, String inId, Object... inArgs) {
		return String.format(getErrorMessage(inLanguage, inId), inArgs);
	}

	/**
	 * Returns a default Message.
	 * 
	 * @return java.lang.String
	 * @param inId java.lang.String
	 */
	public static String getMessage(String inId) {
		return "";
//		return MessageHandler.getInstance().getMessage(inId, cDefaultLocale);
	}

	/**
	 * Returns a Message in a specific language.
	 * 
	 * @return java.lang.String
	 * @param inLanguage java.lang.String The desired language
	 * @param inId java.lang.String
	 */
	public static String getMessage(String inLanguage, String inId) {
		return "";
//		return MessageHandler.getInstance().getMessage(inId, new Locale(inLanguage, ""));
	}

	/**
	 * Returns a Message for a specific locale.
	 * 
	 * @return java.lang.String
	 * @param inId java.lang.String
	 * @param inLocale java.util.Locale the desired locale
	 */
	public static String getMessage(Locale inLocale, String inId) {
		return "";
//		return MessageHandler.getInstance().getMessage(inId, inLocale);
	}

	/**
	 * Gets a string from the resource bundle and formats it with the argument
	 * 
	 * @param inKey	String the key used to get the bundle value, must not be null
	 * @param inArgs Object[]
	 * @return String
	 */
	public static String getFormattedMessage(String inId, Object... inArgs) {
		return String.format(getMessage(inId), inArgs);
	}

	/**
	 * Gets a localized string from the resource bundle and formats it with the argument
	 *
	 * @param inLanguage String the desired language
	 * @param inKey	String the key used to get the bundle value, must not be null
	 * @param inArgs Object[]
	 * @return String
	 */	
	public static String getFormattedMessage(String inLanguage, String inId, Object... inArgs) {
		return String.format(getMessage(inLanguage, inId), inArgs);
	}
	
	/**
	 * Returns the default Message bundle.
	 * 
	 * @return java.util.ResourceBundle
	 */
	public static ResourceBundle getMessageBundle() {
		return getMessageBundle(cDefaultLocale);
	}
	
	/**
	 * Returns a Message bundle for a specific language.
	 * 
	 * @return java.util.ResourceBundle
	 * @param inLanguage java.lang.String the desired language
	 */
	public static ResourceBundle getMessageBundle(String inLanguage) {
		return getMessageBundle(new Locale(inLanguage, ""));
	}
	
	/**
	 * Returns a Message bundle for a specific locale.
	 * 
	 * @return java.util.ResourceBundle
	 * @param inLocale java.util.Locale the desired locale
	 */
	public static ResourceBundle getMessageBundle(Locale inLocale) {
		return PropertyResourceBundle.getBundle(MSG_BUNDLE_BASENAME, inLocale);
	}
	
}

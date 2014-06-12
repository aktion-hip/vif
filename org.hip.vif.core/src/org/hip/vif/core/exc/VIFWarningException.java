/*
	This package concernes exception handling of the application VIF.
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
package org.hip.vif.core.exc;

import java.util.Locale;

/**
 * Warning VIF application exception
 * 
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class VIFWarningException extends VIFException {
	/**
	 * VIFWarningException default constructor.
	 */
	public VIFWarningException() {
		super("no message text provided");
	}

	/**
	 * VIFWarningException with message
	 * 
	 * @param inSimpleMessage
	 *            java.lang.String
	 */
	public VIFWarningException(final String inSimpleMessage) {
		super(inSimpleMessage);
	}

	/**
	 * @param inMsgKey
	 *            String
	 * @param inLocale
	 *            Locale
	 */
	public VIFWarningException(final String inMsgKey, final Locale inLocale) {
		super(inMsgKey, inLocale);
	}

	/**
	 * @param inMsgKey
	 *            java.lang.String
	 * @param inMsgParameters
	 *            java.lang.Object[]
	 */
	public VIFWarningException(final String inMsgKey,
			final Object[] inMsgParameters) {
		super(inMsgKey, inMsgParameters);
	}

	/**
	 * 
	 * @param inMsgKey
	 *            java.lang.String
	 * @param inMsgParameters
	 *            java.lang.Object[]
	 * @param inLocale
	 *            Locale
	 */
	public VIFWarningException(final String inMsgKey,
			final Object[] inMsgParameters, final Locale inLocale) {
		super(inMsgKey, inMsgParameters, inLocale);
	}

	/**
	 * @param inMsgSource
	 *            java.lang.String
	 * @param inMsgKey
	 *            java.lang.String
	 * @param inMsgParameters
	 *            java.lang.Object[]
	 */
	public VIFWarningException(final String inMsgSource, final String inMsgKey,
			final java.lang.Object[] inMsgParameters) {
		super(inMsgSource, inMsgKey, inMsgParameters);
	}
}

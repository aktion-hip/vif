/**
	This package is part of the application VIF.
	Copyright (C) 2012-2014, Benno Luthiger

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
package org.hip.vif.web.exc;

import java.util.Locale;

import org.hip.kernel.exc.ExceptionData;
import org.hip.kernel.exc.VException;
import org.ripla.exceptions.RiplaException;

/** Base exception class for the VIF application.
 *
 * @author lbenno */
@SuppressWarnings("serial")
public class VIFWebException extends RiplaException {

    private Throwable rootCause;
    private ExceptionData exceptionData = null;

    /** @param inSimpleMessage */
    public VIFWebException() {
        super("");
    }

    /** @param inSimpleMessage */
    public VIFWebException(final String inSimpleMessage) {
        super("");
        exceptionData = new ExceptionData(this, inSimpleMessage);
    }

    /** Converts the specified exception to a <code>RiplaException</code>.
     * 
     * @param inExc {@link VException} */
    public VIFWebException(final VException inExc) {
        super(inExc.getMessage());
    }

    /** @param inMsgKey
     * @param inLocale */
    public VIFWebException(final String inMsgKey, final Locale inLocale) {
        this(inMsgKey, null, inLocale);
    }

    /** VException constructor with message key and parameters
     * 
     * @param inMsgKey java.lang.String The key of the error message in the ressource bundle.
     * @param inMsgParameters java.lang.Object[] */
    public VIFWebException(final String inMsgKey, final Object[] inMsgParameters) {
        this((String) null, inMsgKey, inMsgParameters);
    }

    /** VException constructor with message key, parameters and application context
     * 
     * @param inMsgKey String The key of the error message in the resource bundle.
     * @param inMsgParameters java.lang.Object[]
     * @param inLocale Locale */
    public VIFWebException(final String inMsgKey,
            final Object[] inMsgParameters, final Locale inLocale) {
        this((String) null, inMsgKey, inMsgParameters);
        exceptionData.setLocale(inLocale);
    }

    /** VException constructor with message source, key and parameters
     * 
     * @param inMsgSource java.lang.String The source the error happened.
     * @param inMsgKey java.lang.String The key of the error message in the ressource bundle.
     * @param inMsgParameters java.lang.Object[] */
    public VIFWebException(final String inMsgSource, final String inMsgKey,
            final Object[] inMsgParameters) {
        super("");
        exceptionData = new ExceptionData(this, inMsgSource, inMsgKey,
                inMsgParameters);
    }

    /** Returns the original exception if any.
     * 
     * @return java.lang.Throwable */
    public synchronized final Throwable getRootCause() {
        return rootCause;
    }

    /** Returns true if this exception contains a root cause exception.
     * 
     * @return boolean */
    public boolean hasRootCause() {
        return rootCause != null;
    }

    /** Setter for the root cause exception.
     * 
     * @param inRootCause java.lang.Throwable */
    public synchronized void setRootCause(final Throwable inRootCause) {
        rootCause = inRootCause;
    }

    /** A hook to allow subclasses to define their own ExceptionData classes.
     * 
     * @return org.hip.kernel.exc.ExceptionData */
    protected ExceptionData createExceptionData() {
        return new ExceptionData(this);
    }

    /** Creates the ExceptionData
     * 
     * @return org.hip.kernel.exc.ExceptionData */
    private ExceptionData excData() {
        if (exceptionData == null) {
            exceptionData = createExceptionData();
        }
        return exceptionData;
    }

    /** Returns the message localized according to the actual language setting
     * 
     * @return java.lang.String */
    @Override
    public String getLocalizedMessage() {
        return excData().getLocalizedMessage();
    }

    /** Returns the message localized according to the specified language
     * 
     * @return java.lang.String
     * @param inLanguage java.util.Locale */
    public String getLocalizedMessage(final Locale inLanguage) {
        return excData().getLocalizedMessage(inLanguage);
    }

    /** Returns the exception's message
     * 
     * @return java.lang.String */
    @Override
    public String getMessage() {
        return excData().getMessage();
    }

    /** We override this method to invoke the getMessage.
     * 
     * @return java.lang.String */
    @Override
    public String toString() {
        final String lMessage = (excData().get(ExceptionData.DATA_MSG_KEY) == null) ? excData()
                .getMessage() : excData().get(ExceptionData.DATA_MSG_KEY)
                        .toString();
        return String.format("%s: %s", this.getClass().getName(), lMessage);
    }

}

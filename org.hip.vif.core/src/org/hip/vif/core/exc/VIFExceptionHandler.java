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
package org.hip.vif.core.exc;

import org.hip.kernel.exc.ExceptionHandler;

/**
 * Handler for VIF exceptions.
 * 
 * @author lbenno
 */
public enum VIFExceptionHandler implements ExceptionHandler {
	INSTANCE;

	@Override
	public Throwable convert(final Throwable inThrowable) {
		return postProcess(inThrowable, new VIFWarningException());
	}

	@Override
	public Throwable convert(final Throwable inThrowable, final String inMessage) {
		return postProcess(inThrowable, new VIFFatalException(inMessage));
	}

	@Override
	public void handle(final Object inCatchingObject,
			final Throwable inThrowable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(final Object inCatchingObject,
			final Throwable inThrowable, final boolean inPrintStackTrace) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(final Throwable inThrowable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(final Throwable inThrowable,
			final boolean inPrintStackTrace) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rethrow(final Throwable inThrowable) throws Throwable {
		// TODO Auto-generated method stub

	}

	/**
	 * @param inThrowableToBeConverted
	 *            java.lang.Throwable
	 * @param inException
	 *            org.hip.kernel.exc.VException
	 * @return java.lang.Throwable
	 */
	private Throwable postProcess(final Throwable inThrowableToBeConverted,
			final VIFException inException) {
		inException.setRootCause(inThrowableToBeConverted);
		inException.fillInStackTrace();
		return inException;
	}

}

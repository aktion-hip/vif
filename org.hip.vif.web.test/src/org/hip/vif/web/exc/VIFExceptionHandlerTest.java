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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author lbenno
 */
public class VIFExceptionHandlerTest {
	private static final String NL = System.getProperty("line.separator");

	private ByteArrayOutputStream out;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testHandle1() {
		final VIFWebException lExc = new VIFWebException();
		VIFExceptionHandler.INSTANCE.handle(lExc);

		final String[] lOutput = out.toString().split(NL);
		assertEquals(
				"[main] ERROR o.h.vif.web.exc.VIFExceptionHandler - VIF application: Error handled:",
				lOutput[0].substring(13));
		assertEquals(
				"org.hip.vif.web.exc.VIFWebException: no message text provided",
				lOutput[1]);
	}

	@Test
	public final void testHandle2() {
		final VIFWebException lExc = new VIFWebException();
		VIFExceptionHandler.INSTANCE.handle(this, lExc);

		final String[] lOutput = out.toString().split(NL);
		assertEquals(
				"[main] ERROR o.h.vif.web.exc.VIFExceptionHandler - VIF application: Error catched in org.hip.vif.web.exc.VIFExceptionHandlerTest.",
				lOutput[0].substring(13));
		assertEquals(
				"org.hip.vif.web.exc.VIFWebException: no message text provided",
				lOutput[1]);
	}

}

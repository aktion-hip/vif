package org.hip.kernel.sys.test;

import static org.junit.Assert.*;
import org.hip.kernel.sys.Assert;
import org.hip.kernel.sys.AssertionFailedError;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class AssertTest {

	@Test
	public void testAssert() {
		assertTrue("Test assertTrue condition true", Assert.assertTrue(Assert.ERROR_OUT, this, "testAssert", 2 > 0) == Assert.NO_FAILURE);
		assertTrue("Test assertTrue condition false", Assert.assertTrue(Assert.ERROR_OUT,this, "testAssert", 0 > 2) == Assert.FAILURE);
		assertTrue("Test assertTrue not null", Assert.assertNotNull(Assert.ERROR_OUT,this, "testAssert", this) == Assert.NO_FAILURE);
		assertTrue("Test assertTrue null", Assert.assertNotNull(Assert.ERROR_OUT,this, "testAssert", null) == Assert.FAILURE);
	
		AssertionFailedError lError = null;
		try {
			assertTrue("Test assertTrue null", Assert.assertNotNull(Assert.ERROR, this, "testAssert", null) == Assert.FAILURE);
		}
		catch (AssertionFailedError err) {
			lError = err;
		}
		assertNotNull("Test AssertionFailedError", lError);
	}
}

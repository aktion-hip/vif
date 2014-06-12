package org.hip.kernel.exc.test;

import static org.junit.Assert.*;
import org.hip.kernel.exc.ExceptionHandler;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class ExceptionHandlerTest {

	@Test
	public void testConvert() {
		String lMessage = "Test";
		ExceptionHandler lExceptionHandler = TestExceptionHandler.instance();
		Throwable lNewException = lExceptionHandler.convert(new ArrayIndexOutOfBoundsException(), lMessage);
		assertEquals("Test of convert ", lMessage, lNewException.getMessage());
	}
	
	@Test
	public void testHandle() {
		ExceptionHandler lExceptionHandler = TestExceptionHandler.instance();
	
		int lExpected = ((TestExceptionHandler)lExceptionHandler).getNumberOfHandles();
		lExceptionHandler.handle(this, new ArrayIndexOutOfBoundsException());
		assertEquals("test handle 1", lExpected+1, ((TestExceptionHandler)lExceptionHandler).getNumberOfHandles());
		
		lExceptionHandler.handle(new ArrayIndexOutOfBoundsException());
		assertEquals("test handle 2", lExpected+2, ((TestExceptionHandler)lExceptionHandler).getNumberOfHandles());
		
		lExceptionHandler.handle(new ArrayIndexOutOfBoundsException(), true);
		assertEquals("test handle 3", lExpected+3, ((TestExceptionHandler)lExceptionHandler).getNumberOfHandles());
		
		lExceptionHandler.handle(this, new ArrayIndexOutOfBoundsException(), true);
		assertEquals("test handle 4", lExpected+4, ((TestExceptionHandler)lExceptionHandler).getNumberOfHandles());
	}
}

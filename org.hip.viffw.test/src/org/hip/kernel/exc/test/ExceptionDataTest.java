package org.hip.kernel.exc.test;

import static org.junit.Assert.*;
import java.util.Locale;

import org.hip.kernel.exc.ExceptionData;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class ExceptionDataTest {

	@Test
	public void testGetMessage() {
		ExceptionData lExceptionData = new ExceptionData(new ArrayIndexOutOfBoundsException());
		assertEquals("Test getMessage 1", ExceptionData.DEFAULT_MESSAGE, lExceptionData.getMessage());
	
		String lMessage = "Test ExceptionData";
		lExceptionData = new ExceptionData(new ArrayIndexOutOfBoundsException(), lMessage);
		assertEquals("Test getMessage 2", lMessage, lExceptionData.getMessage());
		assertEquals("Test getMessage 3", lMessage, lExceptionData.getMessage(Locale.GERMAN));
	
		/* 
		   For the following tests we need a the ResourceFiles named VIFErrMessages_de.properties and VIFErrMessages_en.properties
		   which contains a key org.hip.vif.errmsg.error.contactAdmin.
		   This properties file has to reside in the project_resources\VIF Framework Test subdirectory
		   (in the case of Eclipse IDE).
		*/
		lMessage = "In der Anwendung ist ein Fehler aufgetreten. Kontaktieren Sie bitte den Administrator.";
		lExceptionData = new ExceptionData(new ArrayIndexOutOfBoundsException(), "testGetMessage", "org.hip.vif.errmsg.error.contactAdmin");
		assertEquals("Test getMessage localized", lMessage, lExceptionData.getMessage(Locale.GERMAN));
		lMessage = "An error occurred in the application. Please contact the administrator.";
		assertEquals("Test getMessage localized", lMessage, lExceptionData.getMessage(Locale.ENGLISH));
		assertNull(lExceptionData.getMessageParameter(2));
	
		String[] lObjects = {"Test1", "Test2"};
		lExceptionData = new ExceptionData(new ArrayIndexOutOfBoundsException(), "testGetMessage", "org.hip.vif.errmsg.error.contactAdmin", lObjects);
		assertEquals("getMessageParameter", "Test1", lExceptionData.getMessageParameter(0));
	}
}

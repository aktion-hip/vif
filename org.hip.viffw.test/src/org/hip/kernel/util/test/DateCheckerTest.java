package org.hip.kernel.util.test;

import static org.junit.Assert.*;
import java.util.GregorianCalendar;

import org.hip.kernel.servlet.Context;
import org.hip.kernel.servlet.test.TestContext;
import org.hip.kernel.util.DateChecker;
import org.hip.kernel.util.DateCheckerImpl;
import org.hip.kernel.util.DefaultNameValueList;
import org.hip.kernel.util.NameValueList;
import org.junit.Test;

/**
 * Test case for testing the DateChecker implementation
 * 
 * @author: Benno Luthiger
 */
public class DateCheckerTest {
	private NameValueList getDateValues(String inDay, String inMonth, String inYear) {
		DefaultNameValueList lNameValueList = null;
		try {
			lNameValueList = new DefaultNameValueList();
			lNameValueList.setValue(DateChecker.FROM_DAY_KEY, inDay);
			lNameValueList.setValue(DateChecker.FROM_MONTH_KEY, inMonth);
			lNameValueList.setValue(DateChecker.FROM_YEAR_KEY, inYear);
		}
		catch (org.hip.kernel.util.VInvalidNameException exc) {}
		catch (org.hip.kernel.util.VInvalidValueException exc) {}
		
		return lNameValueList;
	}
	
	@Test
	public void testReadFormDateFields() {
		Context lContext = new TestContext();
		lContext.setParameters(getDateValues("24", "12", "1961"));
	
		DateChecker lDateChecker = new DateCheckerImpl(true, false);
		lDateChecker.readFormDateFields(lContext);
		assertEquals("mode 1", DateChecker.DATE_OK, lDateChecker.getMode());
		assertEquals("start date 1", new GregorianCalendar(1961, 11, 24), lDateChecker.getStartDate());
	
		lContext.setParameters(getDateValues("32", "12", "1961"));	
		lDateChecker = new DateCheckerImpl(true, false);
		lDateChecker.readFormDateFields(lContext);
		assertEquals("mode 2", DateChecker.FAILURE_INVALID_DATE, lDateChecker.getMode());
		assertEquals("start date 2", new GregorianCalendar().get(GregorianCalendar.DATE), lDateChecker.getStartDate().get(GregorianCalendar.DATE));
	
		lContext.setParameters(getDateValues("2a", "12", "1961"));	
		lDateChecker = new DateCheckerImpl(true, false);
		lDateChecker.readFormDateFields(lContext);
		assertEquals("mode 3", DateChecker.FAILURE_NO_NUMERIC_VALUES, lDateChecker.getMode());
		assertEquals("start date 3", new GregorianCalendar().get(GregorianCalendar.DATE), lDateChecker.getStartDate().get(GregorianCalendar.DATE));
	
		lContext.setParameters(getDateValues("", "12", "1961"));	
		lDateChecker = new DateCheckerImpl(true, false);
		lDateChecker.readFormDateFields(lContext);
		assertEquals("mode 4", DateChecker.FAILURE_EMPTY_FIELDS_FOUND, lDateChecker.getMode());
	
		lContext.setParameters(getDateValues("", "", ""));	
		lDateChecker = new DateCheckerImpl(true, true);
		lDateChecker.readFormDateFields(lContext);
		assertEquals("mode 5", DateChecker.DATE_OK, lDateChecker.getMode());
	
		lDateChecker = new DateCheckerImpl(true, false);
		lDateChecker.readFormDateFields(new TestContext());
		assertEquals("mode 6", DateChecker.FAILURE_NO_PARAMETERS_FOUND, lDateChecker.getMode());
	}
}

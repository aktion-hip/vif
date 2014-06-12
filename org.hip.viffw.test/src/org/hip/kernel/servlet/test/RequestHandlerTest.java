package org.hip.kernel.servlet.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import junit.framework.AssertionFailedError;

import org.hip.kernel.servlet.Context;
import org.hip.kernel.servlet.impl.AbstractContext;
import org.hip.kernel.servlet.test.TestServletRequest.Builder;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class RequestHandlerTest {
	private static final String NL = System.getProperty("line.separator");
	private final static String EXPECTED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" + 
		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<head>\n" +  
		"<meta http-equiv=\"content-type\" content=\"text/html;charset=utf8\" />\n" + 
		"<title>VIF</title>\n</head>\n<body bgcolor='#FFFFFF' text='#505050' onLoad=\"\">" + NL +
		"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><transformed><Root>" + NL +
		"<firstLine>This is the first line.</firstLine>" + NL +
		"<secondLine>This line follows the first.</secondLine>" + NL +
		"</Root></transformed></body></html>" + NL;
//	private final static String EXPECTED_ERR = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" + 
//		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<head>\n" +  
//		"<meta http-equiv=\"content-type\" content=\"text/html;charset=utf8\" />\n" + 
//		"<title>VIF</title>\n</head>\n<body bgcolor='#FFFFFF' text='#505050' onLoad=\"\">" + NL +
//		"This is an error message in an error view!</br>\n" +
//		"No request type defined!</br>\n" +
//		"</body></html>" + NL;
		
	@Test
	public void testDoGet1() throws Exception {
		TestRequestHandler lRequestHandler = new TestRequestHandler();
		
		TestServletResponse lResponse = new TestServletResponse();
		TestServletRequest.Builder lBuilder = new TestServletRequest.Builder();
		Hashtable<String, String[]> lParameters = new Hashtable<String, String[]>(4);
		lParameters.put(AbstractContext.REQUEST_TYPE, new String[] {"testtask"});
		lParameters.put("name",new String[] { "VIF"});
		lParameters.put("version", new String[] {"1.0"});
		lParameters.put("state", new String[] {"beta"});
		HttpServletRequest lRequest1 = lBuilder.setParameters(lParameters).build();
		lRequestHandler.doGet(lRequest1, lResponse);
		
		TestPrintWriter lWriter = (TestPrintWriter)lResponse.getWriter();
		assertEquals("rendered 1", EXPECTED, lWriter.getStreamedText());
		lWriter.close();
		lResponse = new TestServletResponse();
		
		//request without request type specified
		lBuilder = new TestServletRequest.Builder();
		lParameters = new Hashtable<String, String[]>(3);
		lParameters.put("name", new String[] {"VIF"});
		lParameters.put("version", new String[] {"1.0"});
		lParameters.put("state", new String[] {"beta"});
		HttpServletRequest lRequest2 = lBuilder.setParameters(lParameters).build();
		lRequestHandler.doGet(lRequest2, lResponse);
		
		 lWriter = (TestPrintWriter)lResponse.getWriter();
		assertEquals("rendered 2", EXPECTED, lWriter.getStreamedText());
		lWriter.close();		
	}
	
	@Test
	public void testDoGet2() throws Exception {
		TestRequestHandler lRequestHandler = new TestRequestHandler();
		TestServletResponse lResponse = new TestServletResponse();
	
		try {
			lRequestHandler.doGet(null, lResponse);
			fail("shouldn't get here");
		}
		catch (ServletException exc) {
			fail(exc.getMessage());
		}
		catch (IOException exc) {
			fail(exc.getMessage());
		}
		catch (Throwable t) {
			//left blank intentionally
		}
	}
		
	@Test
	public void testDoGet3() throws Exception {
		TestRequestHandler lRequestHandler = new TestRequestHandler();
		TestServletResponse lResponse = new TestServletResponse();
		try {
			//request which doesn't exist
			//we handled it by throwing an AssertionFailedError
			Builder lBuilder = new TestServletRequest.Builder();
			Hashtable<String, String[]> lParameters = new Hashtable<String, String[]>(4);
			lParameters.put(AbstractContext.REQUEST_TYPE, new String[] {"test!!"});
			lParameters.put("name", new String[] {"VIF"});
			lParameters.put("version", new String[] {"1.0"});
			lParameters.put("state", new String[] {"beta"});
			HttpServletRequest lRequest3 = lBuilder.setParameters(lParameters).build();
			lRequestHandler.doGet(lRequest3, lResponse);
			fail("shouldn't get here");
		}
		catch (ServletException exc) {
			fail(exc.getMessage());
		}
		catch (IOException exc) {
			fail(exc.getMessage());
		}
		catch (AssertionFailedError exc) {
			//left blank intentionally
		}
	}
	
	@Test
	public void testGetContext() {
		TestRequestHandler lRequestHandler = new TestRequestHandler();
		assertEquals("Name of Context class", TestContext.class.getName(), lRequestHandler.getContextClassName());
	
		Builder lBuilder = new TestServletRequest.Builder();
		Hashtable<String, String[]> lParameters = new Hashtable<String, String[]>(3);
		lParameters.put("name", new String[] {"VIF"});
		lParameters.put("version", new String[] {"1.0"});
		lParameters.put("state", new String[] {"beta"});
		HttpServletRequest lRequest = lBuilder.setParameters(lParameters).build();
		Context lContext = lRequestHandler.getTestContext(lRequest);
		assertNotNull("context is not null", lContext);
		assertEquals("empty context returns default language", "en", lContext.getLanguage());
		assertTrue("no parameters", lContext.getParameterNames2().isEmpty());	
	}
	
}

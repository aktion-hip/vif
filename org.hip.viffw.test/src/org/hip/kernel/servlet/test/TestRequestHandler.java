package org.hip.kernel.servlet.test;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
 
import org.hip.kernel.servlet.impl.AbstractRequestHandler;
import org.hip.kernel.servlet.RequestException;
import org.hip.kernel.servlet.TaskManager;
import org.hip.kernel.servlet.Context;

import junit.framework.AssertionFailedError;

/**
 * Concrete implementation of an AbstractRequestHandler for testing purpose.
 * 
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class TestRequestHandler extends AbstractRequestHandler {
	private static final String SYS_NAME = "vif";
	private static final String CONTEXT_CLASSNAME = "org.hip.kernel.servlet.test.TestContext";
	
	/**
	 * Handles GET-Requests.
	 *
	 * @param inRequest javax.servlet.http.HttpServletRequest
	 * @param inResponse javax.servlet.http.HttpServletResponse
	 * @exception javax.servlet.ServletException
	 * @exception java.io.IOException
	 */ 
	public void doGet(HttpServletRequest inRequest, HttpServletResponse inResponse) throws ServletException, IOException {
		try {
			super.doGet(inRequest, inResponse);
		} 
		catch (Throwable ex) {
			this.handle(getContext(inRequest), ex);
		}
	}
	
	/**
	 * Subclasses, which implement this method, return the name of the specialiced application-context.
	 *
	 * @return java.lang.String
	 */
	protected String getContextClassName() {
		return CONTEXT_CLASSNAME;
	}
	
	/**
	 * Subclasses which implement this methode, have to return an instance 
	 * of the applications specific TaskManager.
	 *
	 * @return an instance of the taskmanager
	 * @see org.hip.kernel.servlet.TaskManager
	 */
	protected TaskManager getTaskManager() {
		return TestTaskManager.getInstance();
	}
	
	/**
	 * Opens the method getContext() for testing purpose
	 */
	public Context getTestContext(HttpServletRequest inRequest) {
		return getContext(inRequest);
	}
	
	/**
	 * @param inContext 	org.hip.kernel.servlet.Context
	 * @param inThrowable 	java.lang.Throwable
	 */
	protected void handle(Context inContext, Throwable inThrowable) {
		
		//handling of errors
		throw new AssertionFailedError(inThrowable.getMessage());
	}
	
	/**
	 * service method comment.
	 */
	public void service(javax.servlet.ServletRequest arg1, javax.servlet.ServletResponse arg2) throws javax.servlet.ServletException, java.io.IOException {}
		
	/**
	 * Returns the name of the application's properties file.
	 * 
	 * @return java.lang.String
	 */
	protected String getSysName() {
		return SYS_NAME;
	}
	
	@Override
	protected String getDefaultRequestType() throws RequestException {
		return "testtask";
	}
}

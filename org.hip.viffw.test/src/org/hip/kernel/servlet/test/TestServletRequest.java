package org.hip.kernel.servlet.test;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Implementation of the HttpServletRequest for testing purpose.
 * 
 * @author: Benno Luthiger
 */
public class TestServletRequest implements HttpServletRequest {
	private TestSession session = null;
	private Hashtable<String, String[]> parameters;
	private String method;
	private String contentType;
	
	/**
	 * TestServletRequest constructor comment.
	 */
	private TestServletRequest(Builder inBuilder) {
		method = inBuilder.method;
		contentType = inBuilder.contentType;
		parameters = inBuilder.parameters;
	}

	/**
	 * getAttribute method comment.
	 */
	public Object getAttribute(String arg1) {
		return null;
	}
	/**
	 * getAttributeNames method comment.
	 */
	@SuppressWarnings("rawtypes")
	public java.util.Enumeration getAttributeNames() {
		return null;
	}
	/**
	 * getAuthType method comment.
	 */
	public String getAuthType() {
		return null;
	}
	/**
	 * getCharacterEncoding method comment.
	 */
	public String getCharacterEncoding() {
		return null;
	}
	/**
	 * getContentLength method comment.
	 */
	public int getContentLength() {
		return 0;
	}
	/**
	 * getContentType method comment.
	 */
	public String getContentType() {
		return contentType;
	}
	/**
	 * getContextPath method comment.
	 */
	public String getContextPath() {
		return null;
	}
	/**
	 * getCookies method comment.
	 */
	public javax.servlet.http.Cookie[] getCookies() {
		return null;
	}
	/**
	 * getDateHeader method comment.
	 */
	public long getDateHeader(String arg1) {
		return 0;
	}
	/**
	 * getHeader method comment.
	 */
	public String getHeader(String arg1) {
		return null;
	}
	/**
	 * getHeaderNames method comment.
	 */
	@SuppressWarnings("rawtypes")
	public java.util.Enumeration getHeaderNames() {
		return null;
	}
	/**
	 * getHeaders method comment.
	 */
	@SuppressWarnings("rawtypes")
	public java.util.Enumeration getHeaders(String arg1) {
		return null;
	}
	/**
	 * getInputStream method comment.
	 */
	public javax.servlet.ServletInputStream getInputStream() throws java.io.IOException {
		return null;
	}
	/**
	 * getIntHeader method comment.
	 */
	public int getIntHeader(String arg1) {
		return 0;
	}
	/**
	 * getLocale method comment.
	 */
	public java.util.Locale getLocale() {
		return null;
	}
	/**
	 * getLocales method comment.
	 */
	@SuppressWarnings("rawtypes")
	public java.util.Enumeration getLocales() {
		return null;
	}
	/**
	 * getMethod method comment.
	 */
	public String getMethod() {
		return method;
	}
	public String getParameter(String inKey) {
		String[] out = parameters.get(inKey);
		return out == null ? "" : (out.length == 0 ? "" : out[0]);
	}
	@SuppressWarnings("rawtypes")
	public Enumeration getParameterNames() {
		return parameters.keys();
	}
	/*
	 * Returns an array of String objects containing all of the values the given 
	 * request parameter has, or null if the parameter does not exist.<br/>
	 *
	 * If the parameter has a single value, the array has a length of 1.
	 *
	 * @param inKey java.lang.String a String containing the name of the parameter whose value is requested
	 * @return java.lang.String[] an array of String objects containing the parameter's values
	 */
	public String[] getParameterValues(String inKey) {
		Object lValue = parameters.get(inKey);
		if (lValue instanceof String[]) {
			return (String[])lValue;
		}
		else {
			return new String[] {(String)lValue};
		}
	}
	/**
	 * getPathInfo method comment.
	 */
	public String getPathInfo() {
		return null;
	}
	/**
	 * getPathTranslated method comment.
	 */
	public String getPathTranslated() {
		return null;
	}
	/**
	 * getProtocol method comment.
	 */
	public String getProtocol() {
		return null;
	}
	/**
	 * getQueryString method comment.
	 */
	public String getQueryString() {
		return null;
	}
	/**
	 * getReader method comment.
	 */
	public java.io.BufferedReader getReader() throws java.io.IOException {
		return null;
	}
	/**
	 * getRemoteAddr method comment.
	 */
	public String getRemoteAddr() {
		return "null";
	}
	/**
	 * getRemoteHost method comment.
	 */
	public String getRemoteHost() {
		return "null";
	}
	/**
	 * getRemoteUser method comment.
	 */
	public String getRemoteUser() {
		return null;
	}
	/**
	 * getRequestDispatcher method comment.
	 */
	public javax.servlet.RequestDispatcher getRequestDispatcher(String arg1) {
		return null;
	}
	/**
	 * getRequestedSessionId method comment.
	 */
	public String getRequestedSessionId() {
		return null;
	}
	/**
	 * getRequestURI method comment.
	 */
	public String getRequestURI() {
		return null;
	}
	/**
	 * getScheme method comment.
	 */
	public String getScheme() {
		return null;
	}
	/**
	 * getServerName method comment.
	 */
	public String getServerName() {
		return null;
	}
	/**
	 * getServerPort method comment.
	 */
	public int getServerPort() {
		return 0;
	}
	/**
	 * getServletPath method comment.
	 */
	public String getServletPath() {
		return "";
	}
	/**
	 * Returns the current session associated with this request, or if the 
	 * request does not have a session, creates one.
	 *
	 * @return javax.servlet.http.HttpSession
	 */
	public HttpSession getSession() {
		return getSession(true);
	}
	/**
	 * Returns the current HttpSession associated with this request or, 
	 * if if there is no current session and create is true, returns a new session.<br/> 
	 * If create is <code>false</code> and the request has no valid HttpSession, 
	 * this method returns <code>null</code>. 
	 *
	 * To make sure the session is properly maintained, you must call this method before 
	 * the response is committed.
	 *
	 * @param inNew boolean to create a new session for this request if necessary; <code>false</code> to return <code>null</code> if there's no current session
	 * @return javax.servlet.http.HttpSession
	 */
	public HttpSession getSession(boolean inNew) {
		if (inNew)
			if (session == null)
				session = new TestSession();
			
		return session;
	}
	/**
	 * getUserPrincipal method comment.
	 */
	public java.security.Principal getUserPrincipal() {
		return null;
	}
	/**
	 * isRequestedSessionIdFromCookie method comment.
	 */
	public boolean isRequestedSessionIdFromCookie() {
		return true;
	}
	/**
	 * isRequestedSessionIdFromUrl method comment.
	 */
	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}
	/**
	 * isRequestedSessionIdFromURL method comment.
	 */
	public boolean isRequestedSessionIdFromURL() {
		return false;
	}
	/**
	 * isRequestedSessionIdValid method comment.
	 */
	public boolean isRequestedSessionIdValid() {
		return false;
	}
	/**
	 * isSecure method comment.
	 */
	public boolean isSecure() {
		return false;
	}
	/**
	 * isUserInRole method comment.
	 */
	public boolean isUserInRole(String arg1) {
		return false;
	}
	/**
	 * removeAttribute method comment.
	 */
	public void removeAttribute(String arg1) {}
	/**
	 * setAttribute method comment.
	 */
	public void setAttribute(String arg1, Object arg2) {}
	/**
	 * getRealPath method comment.
	 */
	public String getRealPath(String arg1) {
		return null;
	}
	@SuppressWarnings("rawtypes")
	public Map getParameterMap() {
		return parameters;
	}
	public void setCharacterEncoding(String inEnv) throws UnsupportedEncodingException {
	}
	public StringBuffer getRequestURL() {
		return new StringBuffer();
	}
	public int getRemotePort() {
		// TODO Auto-generated method stub
		return 0;
	}
	public String getLocalName() {
		// TODO Auto-generated method stub
		return "";
	}
	public String getLocalAddr() {
		// TODO Auto-generated method stub
		return "";
	}
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 0;
	}
	
// --- 
	
	public static class Builder {
		private String method = "GET";
		private String contentType = "";
		private Hashtable<String, String[]> parameters = new Hashtable<String, String[]>();

		public Builder setMethod(String inMethod) {
			method = inMethod;
			return this;
		}
		public Builder setContentType(String inContentType) {
			contentType = inContentType;
			return this;
		}
		public Builder setParameters(Hashtable<String, String[]> inParameters) {
			parameters = inParameters;
			return this;
		}
		
		public HttpServletRequest build() {
			return new TestServletRequest(this);
		}
		
	}
}

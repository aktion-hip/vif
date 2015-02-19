package org.hip.kernel.servlet.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/** Implementation of the HttpServletRequest for testing purpose.
 *
 * @author: Benno Luthiger */
public class TestServletRequest implements HttpServletRequest {
    private TestSession session = null;
    private final Hashtable<String, String[]> parameters;
    private final String method;
    private final String contentType;

    /** TestServletRequest constructor comment. */
    private TestServletRequest(final Builder inBuilder) {
        method = inBuilder.method;
        contentType = inBuilder.contentType;
        parameters = inBuilder.parameters;
    }

    /** getAttribute method comment. */
    @Override
    public Object getAttribute(final String arg1) {
        return null;
    }

    /** getAttributeNames method comment. */
    @Override
    @SuppressWarnings("rawtypes")
    public java.util.Enumeration getAttributeNames() {
        return null;
    }

    /** getAuthType method comment. */
    @Override
    public String getAuthType() {
        return null;
    }

    /** getCharacterEncoding method comment. */
    @Override
    public String getCharacterEncoding() {
        return null;
    }

    /** getContentLength method comment. */
    @Override
    public int getContentLength() {
        return 0;
    }

    /** getContentType method comment. */
    @Override
    public String getContentType() {
        return contentType;
    }

    /** getContextPath method comment. */
    @Override
    public String getContextPath() {
        return null;
    }

    /** getCookies method comment. */
    @Override
    public javax.servlet.http.Cookie[] getCookies() {
        return null;
    }

    /** getDateHeader method comment. */
    @Override
    public long getDateHeader(final String arg1) {
        return 0;
    }

    /** getHeader method comment. */
    @Override
    public String getHeader(final String arg1) {
        return null;
    }

    /** getHeaderNames method comment. */
    @Override
    @SuppressWarnings("rawtypes")
    public java.util.Enumeration getHeaderNames() {
        return null;
    }

    /** getHeaders method comment. */
    @Override
    @SuppressWarnings("rawtypes")
    public java.util.Enumeration getHeaders(final String arg1) {
        return null;
    }

    /** getInputStream method comment. */
    @Override
    public javax.servlet.ServletInputStream getInputStream() throws java.io.IOException {
        return null;
    }

    /** getIntHeader method comment. */
    @Override
    public int getIntHeader(final String arg1) {
        return 0;
    }

    /** getLocale method comment. */
    @Override
    public java.util.Locale getLocale() {
        return null;
    }

    /** getLocales method comment. */
    @Override
    @SuppressWarnings("rawtypes")
    public java.util.Enumeration getLocales() {
        return null;
    }

    /** getMethod method comment. */
    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getParameter(final String inKey) {
        final String[] out = parameters.get(inKey);
        return out == null ? "" : (out.length == 0 ? "" : out[0]);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Enumeration getParameterNames() {
        return parameters.keys();
    }

    /*
     * Returns an array of String objects containing all of the values the given request parameter has, or null if the
     * parameter does not exist.<br/>
     * 
     * If the parameter has a single value, the array has a length of 1.
     * 
     * @param inKey java.lang.String a String containing the name of the parameter whose value is requested
     * 
     * @return java.lang.String[] an array of String objects containing the parameter's values
     */
    @Override
    public String[] getParameterValues(final String inKey) {
        final Object lValue = parameters.get(inKey);
        if (lValue instanceof String[]) {
            return (String[]) lValue;
        }
        else {
            return new String[] { (String) lValue };
        }
    }

    /** getPathInfo method comment. */
    @Override
    public String getPathInfo() {
        return null;
    }

    /** getPathTranslated method comment. */
    @Override
    public String getPathTranslated() {
        return null;
    }

    /** getProtocol method comment. */
    @Override
    public String getProtocol() {
        return null;
    }

    /** getQueryString method comment. */
    @Override
    public String getQueryString() {
        return null;
    }

    /** getReader method comment. */
    @Override
    public java.io.BufferedReader getReader() throws java.io.IOException {
        return null;
    }

    /** getRemoteAddr method comment. */
    @Override
    public String getRemoteAddr() {
        return "null";
    }

    /** getRemoteHost method comment. */
    @Override
    public String getRemoteHost() {
        return "null";
    }

    /** getRemoteUser method comment. */
    @Override
    public String getRemoteUser() {
        return null;
    }

    /** getRequestDispatcher method comment. */
    @Override
    public javax.servlet.RequestDispatcher getRequestDispatcher(final String arg1) {
        return null;
    }

    /** getRequestedSessionId method comment. */
    @Override
    public String getRequestedSessionId() {
        return null;
    }

    /** getRequestURI method comment. */
    @Override
    public String getRequestURI() {
        return null;
    }

    /** getScheme method comment. */
    @Override
    public String getScheme() {
        return null;
    }

    /** getServerName method comment. */
    @Override
    public String getServerName() {
        return null;
    }

    /** getServerPort method comment. */
    @Override
    public int getServerPort() {
        return 0;
    }

    /** getServletPath method comment. */
    @Override
    public String getServletPath() {
        return "";
    }

    /** Returns the current session associated with this request, or if the request does not have a session, creates one.
     *
     * @return javax.servlet.http.HttpSession */
    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    /** Returns the current HttpSession associated with this request or, if if there is no current session and create is
     * true, returns a new session.<br/>
     * If create is <code>false</code> and the request has no valid HttpSession, this method returns <code>null</code>.
     *
     * To make sure the session is properly maintained, you must call this method before the response is committed.
     *
     * @param inNew boolean to create a new session for this request if necessary; <code>false</code> to return
     *            <code>null</code> if there's no current session
     * @return javax.servlet.http.HttpSession */
    @Override
    public HttpSession getSession(final boolean inNew) {
        if (inNew)
            if (session == null)
                session = new TestSession();

        return session;
    }

    /** getUserPrincipal method comment. */
    @Override
    public java.security.Principal getUserPrincipal() {
        return null;
    }

    /** isRequestedSessionIdFromCookie method comment. */
    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return true;
    }

    /** isRequestedSessionIdFromUrl method comment. */
    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    /** isRequestedSessionIdFromURL method comment. */
    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    /** isRequestedSessionIdValid method comment. */
    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    /** isSecure method comment. */
    @Override
    public boolean isSecure() {
        return false;
    }

    /** isUserInRole method comment. */
    @Override
    public boolean isUserInRole(final String arg1) {
        return false;
    }

    /** removeAttribute method comment. */
    @Override
    public void removeAttribute(final String arg1) {
    }

    /** setAttribute method comment. */
    @Override
    public void setAttribute(final String arg1, final Object arg2) {
    }

    /** getRealPath method comment. */
    @Override
    public String getRealPath(final String arg1) {
        return null;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Map getParameterMap() {
        return parameters;
    }

    @Override
    public void setCharacterEncoding(final String inEnv) throws UnsupportedEncodingException {
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer();
    }

    @Override
    public int getRemotePort() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getLocalName() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String getLocalAddr() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public int getLocalPort() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.ServletRequest#getServletContext()
     */
    @Override
    public ServletContext getServletContext() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.ServletRequest#startAsync()
     */
    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.ServletRequest#startAsync(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    @Override
    public AsyncContext startAsync(final ServletRequest inServletRequest, final ServletResponse inServletResponse)
            throws IllegalStateException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.ServletRequest#isAsyncStarted()
     */
    @Override
    public boolean isAsyncStarted() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.ServletRequest#isAsyncSupported()
     */
    @Override
    public boolean isAsyncSupported() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.ServletRequest#getAsyncContext()
     */
    @Override
    public AsyncContext getAsyncContext() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.ServletRequest#getDispatcherType()
     */
    @Override
    public DispatcherType getDispatcherType() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServletRequest#authenticate(javax.servlet.http.HttpServletResponse)
     */
    @Override
    public boolean authenticate(final HttpServletResponse inResponse) throws IOException, ServletException {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServletRequest#login(java.lang.String, java.lang.String)
     */
    @Override
    public void login(final String inUsername, final String inPassword) throws ServletException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServletRequest#logout()
     */
    @Override
    public void logout() throws ServletException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServletRequest#getParts()
     */
    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServletRequest#getPart(java.lang.String)
     */
    @Override
    public Part getPart(final String inName) throws IOException, ServletException {
        // TODO Auto-generated method stub
        return null;
    }

    // ---

    public static class Builder {
        private String method = "GET";
        private String contentType = "";
        private Hashtable<String, String[]> parameters = new Hashtable<String, String[]>();

        public Builder setMethod(final String inMethod) {
            method = inMethod;
            return this;
        }

        public Builder setContentType(final String inContentType) {
            contentType = inContentType;
            return this;
        }

        public Builder setParameters(final Hashtable<String, String[]> inParameters) {
            parameters = inParameters;
            return this;
        }

        public HttpServletRequest build() {
            return new TestServletRequest(this);
        }

    }

}

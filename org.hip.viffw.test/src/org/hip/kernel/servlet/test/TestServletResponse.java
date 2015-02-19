package org.hip.kernel.servlet.test;

import java.util.Collection;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/** Implementation of the HttpServletResponse for testing purpose.
 *
 * @author: Benno Luthiger */
public class TestServletResponse implements HttpServletResponse {
    private TestPrintWriter outWriter = null;

    /** TestServletResponse constructor comment. */
    public TestServletResponse() {
        super();
    }

    /** addCookie method comment. */
    @Override
    public void addCookie(final javax.servlet.http.Cookie arg1) {
    }

    /** addDateHeader method comment. */
    @Override
    public void addDateHeader(final String arg1, final long arg2) {
    }

    /** addHeader method comment. */
    @Override
    public void addHeader(final String arg1, final String arg2) {
    }

    /** addIntHeader method comment. */
    @Override
    public void addIntHeader(final String arg1, final int arg2) {
    }

    /** containsHeader method comment. */
    @Override
    public boolean containsHeader(final String arg1) {
        return false;
    }

    /** encodeRedirectUrl method comment. */
    @Override
    public String encodeRedirectUrl(final String arg1) {
        return null;
    }

    /** encodeRedirectURL method comment. */
    @Override
    public String encodeRedirectURL(final String arg1) {
        return null;
    }

    /** encodeUrl method comment. */
    @Override
    public String encodeUrl(final String arg1) {
        return null;
    }

    /** encodeURL method comment. */
    @Override
    public String encodeURL(final String arg1) {
        return null;
    }

    /** flushBuffer method comment. */
    @Override
    public void flushBuffer() throws java.io.IOException {
    }

    /** getBufferSize method comment. */
    @Override
    public int getBufferSize() {
        return 0;
    }

    /** getCharacterEncoding method comment. */
    @Override
    public String getCharacterEncoding() {
        return null;
    }

    /** getLocale method comment. */
    @Override
    public java.util.Locale getLocale() {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws java.io.IOException {
        return null;
    }

    /** getWriter method comment. */
    @Override
    public java.io.PrintWriter getWriter() throws java.io.IOException {
        if (outWriter == null) {
            outWriter = new TestPrintWriter();
        }
        return outWriter;
    }

    /** isCommitted method comment. */
    @Override
    public boolean isCommitted() {
        return false;
    }

    /** reset method comment. */
    @Override
    public void reset() {
    }

    /** sendError method comment. */
    @Override
    public void sendError(final int arg1) throws java.io.IOException {
    }

    /** sendError method comment. */
    @Override
    public void sendError(final int arg1, final String arg2) throws java.io.IOException {
    }

    /** sendRedirect method comment. */
    @Override
    public void sendRedirect(final String arg1) throws java.io.IOException {
    }

    /** setBufferSize method comment. */
    @Override
    public void setBufferSize(final int arg1) {
    }

    /** setContentLength method comment. */
    @Override
    public void setContentLength(final int arg1) {
    }

    /** setContentType method comment. */
    @Override
    public void setContentType(final String arg1) {
    }

    /** setDateHeader method comment. */
    @Override
    public void setDateHeader(final String arg1, final long arg2) {
    }

    /** setHeader method comment. */
    @Override
    public void setHeader(final String arg1, final String arg2) {
    }

    /** setIntHeader method comment. */
    @Override
    public void setIntHeader(final String arg1, final int arg2) {
    }

    /** setLocale method comment. */
    @Override
    public void setLocale(final java.util.Locale arg1) {
    }

    /** setStatus method comment. */
    @Override
    public void setStatus(final int arg1) {
    }

    /** setStatus method comment. */
    @Override
    public void setStatus(final int arg1, final String arg2) {
    }

    @Override
    public void resetBuffer() {
    }

    @Override
    public String getContentType() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public void setCharacterEncoding(final String inArg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServletResponse#getStatus()
     */
    @Override
    public int getStatus() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServletResponse#getHeader(java.lang.String)
     */
    @Override
    public String getHeader(final String inName) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServletResponse#getHeaders(java.lang.String)
     */
    @Override
    public Collection<String> getHeaders(final String inName) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServletResponse#getHeaderNames()
     */
    @Override
    public Collection<String> getHeaderNames() {
        // TODO Auto-generated method stub
        return null;
    }
}

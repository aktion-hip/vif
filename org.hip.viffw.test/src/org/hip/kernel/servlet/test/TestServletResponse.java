package org.hip.kernel.servlet.test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * Implementation of the HttpServletResponse for testing purpose.
 * 
 * @author: Benno Luthiger
 */
public class TestServletResponse implements HttpServletResponse {
	private TestPrintWriter outWriter = null;
	
	/**
	 * TestServletResponse constructor comment.
	 */
	public TestServletResponse() {
		super();
	}
	/**
	 * addCookie method comment.
	 */
	public void addCookie(javax.servlet.http.Cookie arg1) {}
	/**
	 * addDateHeader method comment.
	 */
	public void addDateHeader(String arg1, long arg2) {}
	/**
	 * addHeader method comment.
	 */
	public void addHeader(String arg1, String arg2) {}
	/**
	 * addIntHeader method comment.
	 */
	public void addIntHeader(String arg1, int arg2) {}
	/**
	 * containsHeader method comment.
	 */
	public boolean containsHeader(String arg1) {
		return false;
	}
	/**
	 * encodeRedirectUrl method comment.
	 */
	public String encodeRedirectUrl(String arg1) {
		return null;
	}
	/**
	 * encodeRedirectURL method comment.
	 */
	public String encodeRedirectURL(String arg1) {
		return null;
	}
	/**
	 * encodeUrl method comment.
	 */
	public String encodeUrl(String arg1) {
		return null;
	}
	/**
	 * encodeURL method comment.
	 */
	public String encodeURL(String arg1) {
		return null;
	}
	/**
	 * flushBuffer method comment.
	 */
	public void flushBuffer() throws java.io.IOException {}
	/**
	 * getBufferSize method comment.
	 */
	public int getBufferSize() {
		return 0;
	}
	/**
	 * getCharacterEncoding method comment.
	 */
	public String getCharacterEncoding() {
		return null;
	}
	/**
	 * getLocale method comment.
	 */
	public java.util.Locale getLocale() {
		return null;
	}
	public ServletOutputStream getOutputStream() throws java.io.IOException {
		return null;
	}
	/**
	 * getWriter method comment.
	 */
	public java.io.PrintWriter getWriter() throws java.io.IOException {
		if (outWriter == null) {
			outWriter = new TestPrintWriter();
		}
		return outWriter;
	}
	/**
	 * isCommitted method comment.
	 */
	public boolean isCommitted() {
		return false;
	}
	/**
	 * reset method comment.
	 */
	public void reset() {}
	/**
	 * sendError method comment.
	 */
	public void sendError(int arg1) throws java.io.IOException {}
	/**
	 * sendError method comment.
	 */
	public void sendError(int arg1, String arg2) throws java.io.IOException {}
	/**
	 * sendRedirect method comment.
	 */
	public void sendRedirect(String arg1) throws java.io.IOException {}
	/**
	 * setBufferSize method comment.
	 */
	public void setBufferSize(int arg1) {}
	/**
	 * setContentLength method comment.
	 */
	public void setContentLength(int arg1) {}
	/**
	 * setContentType method comment.
	 */
	public void setContentType(String arg1) {}
	/**
	 * setDateHeader method comment.
	 */
	public void setDateHeader(String arg1, long arg2) {}
	/**
	 * setHeader method comment.
	 */
	public void setHeader(String arg1, String arg2) {}
	/**
	 * setIntHeader method comment.
	 */
	public void setIntHeader(String arg1, int arg2) {}
	/**
	 * setLocale method comment.
	 */
	public void setLocale(java.util.Locale arg1) {}
	/**
	 * setStatus method comment.
	 */
	public void setStatus(int arg1) {}
	/**
	 * setStatus method comment.
	 */
	public void setStatus(int arg1, String arg2) {}
	public void resetBuffer() {}
	public String getContentType() {
		// TODO Auto-generated method stub
		return "";
	}
	public void setCharacterEncoding(String inArg0) {
		// TODO Auto-generated method stub
		
	}
}

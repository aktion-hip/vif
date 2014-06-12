package org.hip.kernel.servlet.test;

import java.util.Hashtable;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

/**
 * Implementation of the HttpSession for testing purpose.
 * 
 * @author: Benno Luthiger
 */
public class TestSession implements HttpSession {
	@SuppressWarnings("rawtypes")
	private Hashtable lValues = null;
	@SuppressWarnings("rawtypes")
	private Hashtable lAttributes = null;
	
	private boolean isNewSession;
	
	/**
	 * TestSession default constructor.
	 */
	@SuppressWarnings("rawtypes")
	public TestSession() {
		super();
		lValues = new Hashtable(7);
		lAttributes = new Hashtable(7);
		isNewSession = true;
	}
	
	public Object getAttribute(String inKey) {
		return lAttributes.get(inKey);
	}
	
	@SuppressWarnings("rawtypes")
	public java.util.Enumeration getAttributeNames() {
		return lAttributes.keys();
	}
	
	/**
	 * getCreationTime method comment.
	 */
	public long getCreationTime() {
		return 0;
	}
	
	/**
	 * getId method comment.
	 */
	public String getId() {
		return null;
	}
	
	/**
	 * getLastAccessedTime method comment.
	 */
	public long getLastAccessedTime() {
		return 0;
	}
	
	/**
	 * getMaxInactiveInterval method comment.
	 */
	public int getMaxInactiveInterval() {
		return 0;
	}
	/**
	 * getSessionContext method comment.
	 */
	@SuppressWarnings("deprecation")
	public javax.servlet.http.HttpSessionContext getSessionContext() {
		return null;
	}
	public Object getValue(String inKey) {
		return lValues.get(inKey);
	}
	/**
	 * getValueNames method comment.
	 */
	public java.lang.String[] getValueNames() {
		return null;
	}
	/**
	 * invalidate method comment.
	 */
	public void invalidate() {}
	public boolean isNew() {
		return isNewSession;
	}
	@SuppressWarnings("unchecked")
	public void putValue(String inKey, Object inValue) {
		lValues.put(inKey, inValue);
	}
	/**
	 * removeAttribute method comment.
	 */
	public void removeAttribute(String arg1) {}
	/**
	 * removeValue method comment.
	 */
	public void removeValue(String arg1) {}
	@SuppressWarnings("unchecked")
	public void setAttribute(String inKey, Object inValue) {
		lAttributes.put(inKey, inValue);
	}
	/**
	 * setMaxInactiveInterval method comment.
	 */
	public void setMaxInactiveInterval(int arg1) {}
	public ServletContext getServletContext() {
		return null;
	}
}

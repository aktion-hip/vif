package org.hip.kernel.bom.directory.test;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;

import org.hip.kernel.bom.directory.DirContextWrapper;

public class TestDirContext extends DirContextWrapper {

	private int count;
	private Control[] requestControls = null;

	public TestDirContext() {
		super(new TestLdapContext());
	}

	/**
	 * @param inRequestControls Control[], may be <code>null</code>;
	 */
	public TestDirContext(Control[] inRequestControls) {
		super(new TestLdapContext());
		requestControls  = inRequestControls;
	}

	@Override
	public Iterator<SearchResult> search(String inFilter, SearchControls inControls) throws NamingException {
		LdapContext lContext = new TestLdapContext();
		lContext.setRequestControls(requestControls);
		count = -1;
		NamingEnumeration<SearchResult> lResult = lContext.search("", inFilter, inControls);
		if (lResult == null) {
			return null;
		}
		
		//retrieve the number of entries returned
		count = 0;
		Collection<SearchResult> outResult = new Vector<SearchResult>();
		while (lResult.hasMore()) {
			outResult.add(lResult.next());
			count++;
		}
		return outResult.iterator();
	}

	@Override
	public void close() throws NamingException {
		// intentionally left empty
	}
	
	public int getCount() {
		return count;
	}
	
}

package org.hip.kernel.bom.directory.test;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;

public class TestNamingEnumeration<S> implements NamingEnumeration<SearchResult> {
	private Collection<SearchResult> result = null;
	private Enumeration<SearchResult> resultEnum;
	
//	
	private final static String[] SEARCH = new String[] {"dn=first", "dn=second", "dn=third", "dn=forth", "dn=fith", "dn=sixth", "dn=seventh", "dn=eighth", "dn=ninth", "dn=tenth"};
	private final static String[] ATTR_CN = new String[] {"first", "second", "third", "forth", "fith", "sixth", "seventh", "eighth", "ninth", "tenth"};
	private final static String[] ATTR_UID = new String[] {"111", "2222", "333333", "4", "5", "6", "7", "8", "9", "1010"};
	private final static String[] ATTR_NAME = new String[] {"First", "Second", "Third", "Forth", "Fith", "Sixth", "Seventh", "Eighth", "Ninth", "Tenth"};
	private final static String[] ATTR_SN = new String[] {"First's sn", "Second's sn", "Third's sn", "Forth's sn", "Fith's sn", "Sixth's sn", "Seventh's sn", "Eighth's sn", "Ninth's sn", "Tenth's sn"};
	private final static String[] ATTR_MAIL = new String[] {"first@my.org", "second@my.org", "third@my.org", "forth@my.org", "fith@my.org", "sixth@my.org", "seventh@my.org", "eighth@my.org", "ninth@my.org", "tenth@my.org"};

	private long limit = SEARCH.length;
	private Control[] requestControls;

	public TestNamingEnumeration() {
		resultEnum = createCollection().elements();
	}
	
	public TestNamingEnumeration(long inLimit, Control[] inRequestControls) {
		if (inLimit > 0) {
			limit = Math.min(limit, inLimit);
		}
		resultEnum = createCollection().elements();
		requestControls = inRequestControls;
	}

	public void close() throws NamingException {
		//nothing to do
	}

	public boolean hasMore() throws NamingException {
		return resultEnum.hasMoreElements();
	}

	public SearchResult next() throws NamingException {
		return resultEnum.nextElement();
	}

	public boolean hasMoreElements() {
		return resultEnum.hasMoreElements();
	}

	public SearchResult nextElement() {
		return resultEnum.nextElement();
	}
	
	private Vector<SearchResult> createCollection() {
		if (result == null) {
			Vector<SearchResult> lCollection = new Vector<SearchResult>();
			for (int i = 0; i < limit; i++) {
				lCollection.add(createSearchResult(SEARCH[i], ATTR_CN[i], ATTR_UID[i], ATTR_NAME[i], ATTR_SN[i], ATTR_MAIL[i]));				
			}
			result = lCollection;
		}
		return (Vector<SearchResult>) result;
	}

	/**
	 * Each <code>SearchResult</code> is an entry.
	 */
	private SearchResult createSearchResult(String inSearchItemName, String inCN, String inUID, String inName, String inSN, String inMail) {
		return new SearchResult(inSearchItemName, null, createAttributes(inCN, inUID, inName, inSN, inMail));
	}

	private Attributes createAttributes(String inCN, String inUID, String inName, String inSN, String inMail) {
		Attributes outAttributes = new BasicAttributes();
		outAttributes.put("cn", inCN);
		outAttributes.put("uid", inUID);
		outAttributes.put("name", inName);
		outAttributes.put("sn", inSN);
		outAttributes.put("mail", inMail);
		return outAttributes;
	}
	
	public int getCount() {
		return createCollection().size();
	}
	
	public Control[] getControls() {
		return requestControls;
	}

}

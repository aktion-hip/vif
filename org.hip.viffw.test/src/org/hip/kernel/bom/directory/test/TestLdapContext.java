package org.hip.kernel.bom.directory.test;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.ExtendedResponse;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsResponseControl;

/**
 * <code>LdapContext</code> implementation for testing purposes (offline).
 *
 * @author Luthiger
 * Created: 20.07.2007
 */
public class TestLdapContext implements LdapContext {
    protected Hashtable<Object,Object> props = null;
    private Control[] connectControls = null;
    private Control[] requestControls;
	private int count;

	public ExtendedResponse extendedOperation(ExtendedRequest request)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Control[] getConnectControls() throws NamingException {
		return connectControls;
	}

	public Control[] getRequestControls() throws NamingException {
		return requestControls;
	}

	public Control[] getResponseControls() throws NamingException {
		Control[] outResponse = new Control[1];
		try {
			byte lCount = Byte.parseByte(String.valueOf(count));
//			byte[] lValue = new byte[] {Ber.ASN_INTEGER,2,2,1,lCount,4,1,2,3,4};
			byte[] lValue = new byte[] {0,2,2,1,lCount,4,1,2,3,4};
			outResponse[0] = new PagedResultsResponseControl("ResponseControl", Control.CRITICAL, lValue);
		} catch (IOException exc) {
			outResponse[0] = null;
		}
		return outResponse;
	}

	public LdapContext newInstance(Control[] requestControls)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public void reconnect(Control[] connCtls) throws NamingException {
		// TODO Auto-generated method stub

	}

	public void setRequestControls(Control[] requestControls) throws NamingException {
		this.requestControls = requestControls;
	}

	public void bind(Name name, Object obj, Attributes attrs)
			throws NamingException {
		// TODO Auto-generated method stub

	}

	public void bind(String name, Object obj, Attributes attrs)
			throws NamingException {
		// TODO Auto-generated method stub

	}

	public DirContext createSubcontext(Name name, Attributes attrs)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public DirContext createSubcontext(String name, Attributes attrs)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Attributes getAttributes(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Attributes getAttributes(String name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Attributes getAttributes(Name name, String[] attrIds)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Attributes getAttributes(String name, String[] attrIds)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public DirContext getSchema(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public DirContext getSchema(String name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public DirContext getSchemaClassDefinition(Name name)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public DirContext getSchemaClassDefinition(String name)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public void modifyAttributes(Name name, ModificationItem[] mods)
			throws NamingException {
		// TODO Auto-generated method stub

	}

	public void modifyAttributes(String name, ModificationItem[] mods)
			throws NamingException {
		// TODO Auto-generated method stub

	}

	public void modifyAttributes(Name name, int mod_op, Attributes attrs)
			throws NamingException {
		// TODO Auto-generated method stub

	}

	public void modifyAttributes(String name, int mod_op, Attributes attrs)
			throws NamingException {
		// TODO Auto-generated method stub

	}

	public void rebind(Name name, Object obj, Attributes attrs)
			throws NamingException {
		// TODO Auto-generated method stub

	}

	public void rebind(String name, Object obj, Attributes attrs)
			throws NamingException {
		// TODO Auto-generated method stub

	}

	public NamingEnumeration<SearchResult> search(Name name,
			Attributes matchingAttributes) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public NamingEnumeration<SearchResult> search(String name,
			Attributes matchingAttributes) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public NamingEnumeration<SearchResult> search(Name name,
			Attributes matchingAttributes, String[] attributesToReturn)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public NamingEnumeration<SearchResult> search(String name,
			Attributes matchingAttributes, String[] attributesToReturn)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public NamingEnumeration<SearchResult> search(Name name, String filter, SearchControls inControls) throws NamingException {
		return search("", filter, inControls);
	}

	public NamingEnumeration<SearchResult> search(String name, String filter, SearchControls inControls) throws NamingException {
		count = 0;
		NamingEnumeration<SearchResult> outEnumeration = new TestNamingEnumeration<SearchResult>(getCountLimit(inControls), getRequestControls());
		count = ((TestNamingEnumeration<SearchResult>)outEnumeration).getCount();
		return outEnumeration;
	}
	
	private long getCountLimit(SearchControls inControls) {
		if (inControls == null) return 0;
		return inControls.getCountLimit();
	}

	public NamingEnumeration<SearchResult> search(Name name, String filterExpr,
			Object[] filterArgs, SearchControls cons) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public NamingEnumeration<SearchResult> search(String name,
			String filterExpr, Object[] filterArgs, SearchControls cons)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object addToEnvironment(String propName, Object propVal)
			throws NamingException {
		props.put(propName, propVal);
		return null;
	}

	public void bind(Name name, Object obj) throws NamingException {
		// TODO Auto-generated method stub

	}

	public void bind(String name, Object obj) throws NamingException {
		// TODO Auto-generated method stub

	}

	public void close() throws NamingException {
		props = null;
	}

	public Name composeName(Name name, Name prefix) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public String composeName(String name, String prefix)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Context createSubcontext(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Context createSubcontext(String name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public void destroySubcontext(Name name) throws NamingException {
		// TODO Auto-generated method stub

	}

	public void destroySubcontext(String name) throws NamingException {
		// TODO Auto-generated method stub

	}

	public Hashtable<?, ?> getEnvironment() throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNameInNamespace() throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public NameParser getNameParser(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public NameParser getNameParser(String name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public NamingEnumeration<NameClassPair> list(Name name)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public NamingEnumeration<NameClassPair> list(String name)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public NamingEnumeration<Binding> listBindings(Name name)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public NamingEnumeration<Binding> listBindings(String name)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object lookup(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object lookup(String name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object lookupLink(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object lookupLink(String name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public void rebind(Name name, Object obj) throws NamingException {
		// TODO Auto-generated method stub

	}

	public void rebind(String name, Object obj) throws NamingException {
		// TODO Auto-generated method stub

	}

	public Object removeFromEnvironment(String propName) throws NamingException {
		return props.remove(propName);
	}

	public void rename(Name oldName, Name newName) throws NamingException {
		// TODO Auto-generated method stub

	}

	public void rename(String oldName, String newName) throws NamingException {
		// TODO Auto-generated method stub

	}

	public void unbind(Name name) throws NamingException {
		// TODO Auto-generated method stub

	}

	public void unbind(String name) throws NamingException {
		// TODO Auto-generated method stub
	}

}

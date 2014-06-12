/*
	This package is part of the framework used for the application VIF.
	Copyright (C) 2006, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.hip.kernel.bom.directory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.SortControl;

import org.hip.kernel.bom.LimitObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.QueryStatement;
import org.hip.kernel.bom.impl.AbstractQueryResult;
import org.hip.kernel.exc.VException;

/**
 * Querying the LDAP means searching the directory.
 *
 * @author Luthiger
 * Created on 03.07.2007
 */
public class LDAPQueryStatement implements QueryStatement, Serializable {
	private LDAPObjectHome home = null;
	private String filter = null;
	private String baseDir = "";
	private SearchControls controls;
	private SortControl sort = null;

	/**
	 * LDAPQueryStatement constructor with home an base directory of LDAP context.
	 * 
	 * @param inHome LDAPObjectHome
	 * @param inBaseDir String
	 */
	public LDAPQueryStatement(LDAPObjectHome inHome, String inBaseDir) {
		home = inHome;
		baseDir = inBaseDir;
		controls = new SearchControls();
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.QueryStatement#close()
	 */
	public void close() throws SQLException {
		//intentionally left empty
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.QueryStatement#executeQuery()
	 */
	public QueryResult executeQuery() throws SQLException {
		DirContextWrapper lContext = null;
		try {
			if (getSQLString() != null) {
				lContext = createContext();
				lContext.setRequestControls(sort);
				Iterator<SearchResult> lResult = lContext.search(getSQLString(), controls);
				return createSearchResult(home, lResult, lContext.getCount(), this);
			} 
		}
		catch (NamingException exc) {
			throw new SQLException(exc.getMessage());
		} 
		catch (VException exc) {
			throw new SQLException(exc.getMessage());
		}
		finally {
			if (lContext != null) {
				try {
					lContext.close();
				} catch (NamingException exc) {
					// intentionally left empty
				}
			}
		}
		return createSearchResult(home, null, -1, this);
	}
	
	/**
	 * Limits the number of results
	 * 
	 * @param inLimit LimitObject
	 */
	public void setLimit(LimitObject inLimit) {
		int lLimit = (Integer)inLimit.getArguments()[0];
		controls.setCountLimit(lLimit);
	}
	
	/**
	 * Makes the results returned sorted
	 * 
	 * @param inSortBy String[] list of attributes in ascending order.
	 * @throws IOException 
	 */
	public void setOrder(String[] inSortBy) throws IOException {
		sort = new SortControl(inSortBy, Control.NONCRITICAL);
	}

	private QueryResult createSearchResult(LDAPObjectHome inHome, Iterator<SearchResult> inResult, int inCount, LDAPQueryStatement inStatement) {
		return new LDAPQueryResult(inHome, inResult, inCount, inStatement);
	}

	protected DirContextWrapper createContext() throws NamingException, VException {
		return LDAPContextManager.getInstance().getContext(baseDir, createControls());
	}
	
	protected Control[] createControls() {
		if (sort == null) {
			return null;
		}
		return new Control[] {sort};
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.QueryStatement#executeQuery(java.lang.String)
	 */
	public QueryResult executeQuery(String inFilter) throws SQLException {
		setSQLString(inFilter);
		return executeQuery();
	}

	/**
	 * Not used.
	 * 
	 * @see org.hip.kernel.bom.QueryStatement#executeUpdate(boolean)
	 */
	public int executeUpdate(boolean inCommit) throws SQLException {
		return 0;
	}

	/**
	 * In the LDAP case, this method returns the search filter.
	 * 
	 * @see org.hip.kernel.bom.QueryStatement#getSQLString()
	 */
	public String getSQLString() {
		return filter;
	}

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.QueryStatement#hasMoreResults()
	 */
	public boolean hasMoreResults() {
		return false;
	}

	/**
	 * In the LDAP case, this method is used to set the search filter.
	 * 
	 * @see org.hip.kernel.bom.QueryStatement#setSQLString(java.lang.String)
	 */
	public void setSQLString(String inFilter) {
		filter = inFilter;
	}
	
	/**
	 * Friendly method used for deserialization of <code>QueryResult</code>.
	 * 
	 * @return Iterator<SearchResult>
	 * @throws SQLException
	 * @see AbstractQueryResult
	 */
	Iterator<SearchResult> retrieveStatement() throws SQLException {
		if (getSQLString() == null) return null;
		
		DirContextWrapper lContext = null;
		try {
			lContext = createContext();
			return lContext.search(getSQLString(), null);
		} 
		catch (NamingException exc) {
			throw new SQLException(exc.getExplanation());
		}
		catch (VException exc) {
			throw new SQLException(exc.getMessage());
		}
		finally {
			if (lContext != null) {
				try {
					lContext.close();
				} catch (NamingException exc) {
					// intentionally left empty
				}
			}			
		}
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(home);
		out.writeObject(filter);
		try {
			close();
		}
		catch (SQLException exc) {
			throw new IOException(exc.getMessage());
		} 
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		home = (LDAPObjectHome)in.readObject();
		filter = (String)in.readObject();
	}

	public Collection<String> showTables() throws SQLException {
		return new Vector<String>();
	}
	
}

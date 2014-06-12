package org.hip.kernel.bom.impl;

/*
	This package is part of the servlet framework used for the application VIF.
	Copyright (C) 2001, Benno Luthiger

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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Locale;
import java.util.Vector;

import org.hip.kernel.bom.AlternativeModel;
import org.hip.kernel.bom.AlternativeModelFactory;
import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObjectCollection;
import org.hip.kernel.bom.DomainObjectIterator;
import org.hip.kernel.bom.DomainObjectVisitor;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.Page;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.QueryStatement;
import org.hip.kernel.exc.DefaultExceptionHandler;
import org.hip.kernel.sys.VObject;
import org.hip.kernel.util.Debug;

/**
 * 	This is the abstract implementation of the
 *	QueryResult interface.
 * 
 * 	@author		Benno Luthiger
 *	@see		org.hip.kernel.bom.QueryResult
 */
abstract public class AbstractQueryResult extends VObject implements QueryResult, Serializable {

	// Instance variables
	private ResultSet				result			= null;
	private GeneralDomainObjectHome	home			= null;

	private GeneralDomainObject		current			= null;
	private GeneralDomainObject		next			= null;
	
	private QueryStatement			statement		= null;
	private Page                	currentPage		= null;

	//class attributes
	private static XMLCharacterFilter cCharacterFilter = null;
	
	/**
	 * AbstractQueryResult constructor. Initializes the instance variables and
	 * intializes the first DomainObject with data from the ResultSet.
	 *
	 * @param inHome org.hip.kernel.bom.GeneralDomainObjectHome
	 * @param inResult java.sql.ResultSet
	 * @param inStatement org.hip.kernel.bom.QueryStatement
	 */
	public AbstractQueryResult(GeneralDomainObjectHome inHome, ResultSet inResult, QueryStatement inStatement) {
		super();
	
		home 	= inHome;
		result 	= inResult;
		statement = inStatement;
		
		if (!isCollectionLoading()) firstRead();
	}

	/**
	 * AbstractQueryResult default constructor.
	 * 
	 * @param inHome org.hip.kernel.bom.GeneralDomainObjectHome
	 */
	public AbstractQueryResult(GeneralDomainObjectHome inHome) {
		super();
		home = inHome;
	}
	
	/**
	 * @exception java.sql.SQLException
	 */
	public void close() throws SQLException {
		if (result != null) 
			 result.close();
		if (statement != null)
		     statement.close();
	}
	
	/**
	 * 	Initializes the first DomainObject with data from the ResultSet.
	 */
	private void firstRead() {
		try {
			if (result.next())
				next = ((AbstractDomainObjectHome)home).newInstance(result);
		} 
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);
		}
	}
	
	/**
	 * @return org.hip.kernel.bom.GeneralDomainObject
	 */
	public GeneralDomainObject getCurrent() {
		return current;
	}
	
	/**
	 * @return org.hip.kernel.bom.Page
	 */
	public Page getCurrentPage() {
		return currentPage;
	}
	
	/**
	 * @return org.hip.kernel.bom.impl.XMLCharacterFilter
	 */
	private synchronized XMLCharacterFilter getFilter() {		
		if(cCharacterFilter == null) {
			cCharacterFilter = XMLCharacterFilter.DEFAULT_FILTER;
		}		
		return cCharacterFilter;
	}
	
	/**
	 * Returns the key of the current domain object.
	 * 
	 * @return org.hip.kernel.bom.KeyObject
	 * @exception org.hip.kernel.bom.BOMNotFoundException
	 */
	public KeyObject getKey() throws BOMNotFoundException {
		if (current != null) {
			 return current.getKey();
		} 
		else {
			throw new BOMNotFoundException();
		}	
	}
	
	/**
	 * 	Returns the ResultSet MetaData.
	 * 
	 * 	@return java.sql.ResultSetMetaData
	 *  @exception java.sql.SQLException
	 */
	protected ResultSetMetaData getMetaData() throws SQLException {
		return (result != null) ? result.getMetaData() : null ;
	}
	
	/**
	 * Returns the internally hold ResultSet.
	 * 
	 * @return java.sql.ResultSet
	 */
	protected ResultSet getResultSet() {
		return result;
	}
	
	/**
	 * @return boolean <code>true</code> if the result set still has more elements, <code>false</code> if the cursor is at the end.
	 */
	public boolean hasMoreElements() {
		return (next != null);
	}
	
	/**
	 * Returns a new Instance of the DomainObject initialized
	 * with data from the QueryResult.
	 *
	 * @exception <code>SQLException </code>
	 * @exception org.hip.kernel.bom.BOMException
	 * @return instance of next domain object. Returns null if query result has no more elements.
	 */
	public GeneralDomainObject next() throws SQLException, BOMException {
		GeneralDomainObject outModel = null;
		if (result != null) {		
			current = next;
			outModel = current;
			if (result.next()) {
				next = ((AbstractDomainObjectHome)home).newInstance(result);
			} 
			else {
				next = null;
				this.close();
				result = null;
			}
		}
		return outModel;
	}
	
	/**
	 * @return org.hip.kernel.bom.GeneralDomainObject
	 * @exception java.sql.SQLException
	 * @exception org.hip.kernel.bom.BOMException
	 */
	public GeneralDomainObject nextAsDomainObject() throws SQLException, BOMException {
		return next();
	}
	
	/**
	 * 	Returns the next entry in form of an XML string. No
	 *	Domain object will be instatiated. This method is useful
	 *	if the data is used in browsers.
	 *	Application then will instantiate a DomainObject through
	 *	the findByPrimaryKey method.
	 * 
	 * 	@return java.lang.String
	 *  @exception java.sql.SQLException
	 *  @exception org.hip.kernel.bom.BOMException
	 */ 
	public String nextAsXMLString() throws SQLException, BOMException {
		return nextAsXMLString(new XMLSerializer(getFilter()));
	}
	/**
	 * 	Returns the next entry in form of an XML string using the specified serializer. 
	 *  No Domain object will be instatiated. This method is useful
	 *	if the data is used in browsers.
	 *	Application then will instantiate a DomainObject through
	 *	the findByPrimaryKey method.
	 * 
	 *  @param inSerializerName java.lang.String The name of the serializer class.
	 * 	@return java.lang.String
	 *  @exception java.sql.SQLException
	 *  @exception org.hip.kernel.bom.BOMException
	 */
	public String nextAsXMLString(String inSerializerName) throws SQLException, BOMException {
		return nextAsXMLString(inSerializerName, true);
	}
	
	/**
	 * 	Returns the next entry in form of an XML string using the specified serializer. 
	 *  No Domain object will be instatiated. This method is useful
	 *	if the data is used in browsers.
	 *	Application then will instantiate a DomainObject through
	 *	the findByPrimaryKey method.
	 * 
	 *  @param inSerializerName java.lang.String The name of the serializer class.
	 *  @param inUseFilter boolean True, if the serializer should be constructed with filter.
	 * 	@return java.lang.String
	 *  @exception java.sql.SQLException
	 *  @exception org.hip.kernel.bom.BOMException
	 */
	public String nextAsXMLString(String inSerializerName, boolean inUseFilter) throws SQLException, BOMException {
		return nextAsXMLString(inSerializerName, inUseFilter, null);
	}
	
	/**
	 * @see QueryResult#nextAsXMLString(String inSerializerName, boolean inUseFilter)
	 * 
	 * @param inSerializerName java.lang.String The name of the serializer class.
	 * @param inUseFilter boolean True, if the serializer should be constructed with filter.
	 * @param inLocale The locale to format date values.
	 * @return java.lang.String
	 * @throws SQLException
	 * @throws BOMException
	 */
	public String nextAsXMLString(String inSerializerName, boolean inUseFilter, Locale inLocale) throws SQLException, BOMException {
		try {
			Class<?> lClass = Class.forName(inSerializerName);
			XMLSerializer lSerializer = (XMLSerializer)lClass.newInstance();
			if (inUseFilter) {
				lSerializer.setFilter(getFilter());
			}
			lSerializer.setLocale(inLocale);
			return nextAsXMLString(lSerializer);
		}
		catch (ClassNotFoundException exc) {
			throw new BOMException("ClassNotFound " + exc.getMessage());
		}
		catch (Exception exc) {
			throw new BOMException(exc.getMessage());
		}	
	}
	
	private String nextAsXMLString(DomainObjectVisitor inSerializer) {
		String outXML = null;
		try { 
			GeneralDomainObject lObject = nextAsDomainObject();
			if (lObject == null)
			 	return null;
			lObject.accept(inSerializer);
			outXML = inSerializer.toString();
			
			lObject.release(); 	
			return outXML;
		} 
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);
			return null;
		}	
	}
	
	/**
	 * 	Returns a specified amount of DomainObjects
	 * 
	 * 	@return org.hip.kernel.bom.DomainObjectCollection
	 * 	@param inHowMany int
	 *  @exception java.sql.SQLException
	 *  @exception org.hip.kernel.bom.BOMException
	 */
	public DomainObjectCollection nextn(int inHowMany) throws SQLException, BOMException {
	
		DomainObjectCollection outObject = new DomainObjectCollectionImpl();
		try { 
			for (int i = 0; this.hasMoreElements() && i < inHowMany; i++ ) {
				GeneralDomainObject lObject = this.next();
				if (lObject == null)
			     	continue;
				outObject.add(lObject);
			}
		} 
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);
		}
		return outObject;
	}
	
	/**
	 * Returns the next n entries in form of an XML string. No
	 * Domain object will be instatiated. This method is useful
	 * if the data is used in browsers.
	 * Application then will instantiate a DomainObject through
	 * the findByPrimaryKey method.
	 * 
	 * @return java.lang.String
	 * @param inHowMany int number of serialized DomainObjects to return
	 * @exception java.sql.SQLException
	 * @exception org.hip.kernel.bom.BOMException
	 */
	public String nextnAsXMLString(int inHowMany) throws SQLException, BOMException {
	
		DomainObjectIterator lIterator = nextn(inHowMany).elements();
		XMLSerializer lSerializer = new XMLSerializer(getFilter());
		lIterator.accept(lSerializer);
		
		return lSerializer.toString();
	}

	/**
	 * Returns the next n entries in form of an XML string using the specified 
	 * serializer. No Domain object will be instatiated. This method is useful
	 * if the data is used in browsers.
	 * Application then will instantiate a DomainObject through
	 * the findByPrimaryKey method.
	 * 
	 * @return java.lang.String
	 * @param inHowMany int number of serialized DomainObjects to return.
	 * @param inSerializerName java.lang.String The name of the serializer to be used.
	 * @exception java.sql.SQLException
	 * @exception org.hip.kernel.bom.BOMException
	 */
	public String nextnAsXMLString(int inHowMany, String inSerializerName) throws SQLException, BOMException {
		return nextnAsXMLString(inHowMany, inSerializerName, true);
	}
	
	/**
	 * Returns the next n entries in form of an XML string. No
	 * Domain object will be instatiated. This method is useful
	 * if the data is used in browsers.
	 * Application then will instantiate a DomainObject through
	 * the findByPrimaryKey method.
	 * 
	 * @return java.lang.String
	 * @param inHowMany int number of serialized DomainObjects to return.
	 * @param inSerializerName java.lang.String The name of the serializer to be used.
	 * @param inUseFilter boolean True, if the serializer should be constructed with filter.
	 * @exception java.sql.SQLException
	 * @exception org.hip.kernel.bom.BOMException
	 */
	public String nextnAsXMLString(int inHowMany, String inSerializerName, boolean inUseFilter) throws SQLException, BOMException {
		return nextnAsXMLString(inHowMany, inSerializerName, inUseFilter, null);
	}

	/**
	 * @see QueryResult#nextnAsXMLString(int inHowMany, String inSerializerName, boolean inUseFilter)
	 * 
	 * @param inHowMany int number of serialized DomainObjects to return.
	 * @param inSerializerName java.lang.String The name of the serializer to be used.
	 * @param inUseFilter boolean True, if the serializer should be constructed with filter.
	 * @param inLocale The locale to format date values.
	 * @return java.lang.String
	 * @throws SQLException
	 * @throws BOMException
	 */
	public String nextnAsXMLString(int inHowMany, String inSerializerName, boolean inUseFilter, Locale inLocale) throws SQLException, BOMException {
		try {
			Class<?> lClass = Class.forName(inSerializerName);
			Class<?>[] lParameters = {XMLCharacterFilter.class};
			Object[] lInitArgs = {null};
			if (inUseFilter) {
				lInitArgs[0] = getFilter();
			}
			XMLSerializer lSerializer = (XMLSerializer)lClass.getConstructor(lParameters).newInstance(lInitArgs);
			lSerializer.setLocale(inLocale);

			DomainObjectIterator lIterator = nextn(inHowMany).elements();
			lIterator.accept(lSerializer);
			return lSerializer.toString();
		}
		catch (ClassNotFoundException exc) {
			throw new BOMException("ClassNotFound " + exc.getMessage());
		}
		catch (Exception exc) {
			throw new BOMException(exc.getMessage());
		}	
	}
	
	/**
	 * @param inPage org.hip.kernel.bom.Page
	 */
	public void setCurrentPage(Page inPage) {
		currentPage = inPage; 
	}
	
	public String toString() {
		String lMessage = "ResultSet=\"" + (getResultSet() == null ? "null" : getResultSet().toString()) + "\"";
		return Debug.classMarkupString(this, lMessage);
	}

	/**
	 * Signals whether this query result loads into a Collection or not.
	 * Subclasses may override.
	 *  
	 * @return boolean
	 */
	protected boolean isCollectionLoading() {
		return false;
	}
	
	/**
	 * Loads the query result into a Collection, using the specified model factory.
	 * Returns an empty collection by default.
	 * Subclasses may override this method.
	 * 
	 * @param inModelFactory AlternativeModelFactory
	 * @return Collection<AlternativeModel>
	 * @throws SQLException
	 */
	public Collection<AlternativeModel> load(AlternativeModelFactory inModelFactory) throws SQLException {
		Collection<AlternativeModel> outSet = new Vector<AlternativeModel>();
		if (!isCollectionLoading()) return outSet;
		
		while (result.next()) {
			outSet.add(inModelFactory.createModel(result));
		}
		this.close();
		return outSet;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.QueryResult#load(org.hip.kernel.bom.AlternativeModelFactory, int)
	 */
	public Collection<AlternativeModel> load(AlternativeModelFactory inModelFactory, int inMaxEntries) throws SQLException {
		Collection<AlternativeModel> outSet = new Vector<AlternativeModel>();
		if (!isCollectionLoading()) return outSet;
		
		int i = 0;
		while (result.next() && i++ < inMaxEntries) {
			outSet.add(inModelFactory.createModel(result));
		}
		this.close();
		return outSet;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(home);
		out.writeObject(current);
		out.writeObject(next);
		out.writeObject(statement);
		out.writeObject(currentPage);
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		home = (GeneralDomainObjectHome)in.readObject();
		GeneralDomainObject lCurrent = (GeneralDomainObject)in.readObject();
		next = (GeneralDomainObject)in.readObject();
		statement = (QueryStatement)in.readObject();
		currentPage = (Page)in.readObject();
		
		if (statement == null) return;
		
		try {
			//we retrieve the ResultSet backing this object using the statement
			result = ((AbstractQueryStatement)statement).retrieveStatement();
			
			//now we position the cursor on the correct place
			if (lCurrent == null) {
				firstRead();
			}
			else {
				while (!lCurrent.equals(current)) {
					next();
					if (!hasMoreElements()) return;
				}
			}
		}
		catch (SQLException exc) {
			throw new IOException(exc.getMessage());
		} 
		catch (BOMException exc) {
			throw new IOException(exc.getMessage());
		}
	}
	
}
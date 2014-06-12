/*
	This package is part of the application VIF.
	Copyright (C) 2005, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	General Public License for more details.

	You should have received a copy of the GNU General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.hip.vif.core.search;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Locale;
import java.util.Vector;

import org.apache.lucene.document.Document;
import org.hip.kernel.bom.AlternativeModel;
import org.hip.kernel.bom.AlternativeModelFactory;
import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObjectCollection;
import org.hip.kernel.bom.DomainObjectIterator;
import org.hip.kernel.bom.DomainObjectVisitor;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.Page;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.DomainObjectCollectionImpl;
import org.hip.kernel.bom.impl.XMLCharacterFilter;
import org.hip.kernel.bom.impl.XMLSerializer;
import org.hip.kernel.exc.DefaultExceptionHandler;

/**
 * A query result containing HitsDomainObjects, i.e. the result
 * of a lucene search query.
 * 
 * @author Benno Luthiger
 * Created on 27.09.2005
 */
public abstract class AbstractHitsQueryResult implements QueryResult {
	private GeneralDomainObject	current			= null;
	private GeneralDomainObject	next			= null;

	private Page currentPage = null;
	private Document[] docs;
	private int currentIndex = 0;
	
	/**
	 * HitsQueryResult constructor.
	 * 
	 * @param inHits Document[]
	 */
	public AbstractHitsQueryResult(Document[] inHits) { //throws IOException, NoHitsException {
		super();
		
		docs = inHits;
		next = getHitsDomainObject(docs[currentIndex++]);
	}
	
	abstract AbstractHitsDomainObject getHitsDomainObject(Document inDocument);

	/**
	 * @see org.hip.kernel.bom.QueryResult#close()
	 */
	public void close() throws SQLException {
		//Intentionally left empty.
	}

	/**
	 * @see org.hip.kernel.bom.QueryResult#getCurrent()
	 */
	public GeneralDomainObject getCurrent() {
		return current;
	}

	/**
	 * @see org.hip.kernel.bom.QueryResult#getCurrentPage()
	 */
	public Page getCurrentPage() {
		return currentPage;
	}

	/**
	 * @see org.hip.kernel.bom.QueryResult#getKey()
	 */
	public KeyObject getKey() throws BOMNotFoundException {
		//Intentionally left empty.
		return null;
	}

	/**
	 * @see org.hip.kernel.bom.QueryResult#hasMoreElements()
	 */
	public boolean hasMoreElements() {
		return (next != null);
	}

	/**
	 * Returns a new instance of the HitsDomainObject
	 * initialized with the content of the next Hits Document found.
	 *  
	 * @throws SQLException
	 * @throws BOMException
	 */
	public GeneralDomainObject next() throws SQLException, BOMException {
		GeneralDomainObject outModel = null;
		if (docs != null) {		
			current = next;
			outModel = current;
			if (currentIndex < docs.length) {
				next = getHitsDomainObject(docs[currentIndex++]);
			} 
			else {
				next = null;
				docs = null;
			}
		}
		return outModel;
	}

	/**
	 * @see org.hip.kernel.bom.QueryResult#nextAsDomainObject()
	 */
	public GeneralDomainObject nextAsDomainObject() throws SQLException, BOMException {
		return next();
	}

	/**
	 * @see org.hip.kernel.bom.QueryResult#nextAsXMLString()
	 */
	public String nextAsXMLString() throws SQLException, BOMException {
		return nextAsXMLString(new XMLSerializer());
	}

	/**
	 * @see org.hip.kernel.bom.QueryResult#nextAsXMLString(java.lang.String)
	 */
	public String nextAsXMLString(String inSerializerName) throws SQLException, BOMException {
		return nextAsXMLString(inSerializerName, true);
	}

	/**
	 * @see org.hip.kernel.bom.QueryResult#nextAsXMLString(java.lang.String, boolean)
	 */
	public String nextAsXMLString(String inSerializerName, boolean inUseFilter) throws SQLException, BOMException {
		return nextAsXMLString(inSerializerName, inUseFilter, null);
	}

	/**
	 * @see org.hip.kernel.bom.QueryResult#nextAsXMLString(java.lang.String, boolean, java.util.Locale)
	 */
	public String nextAsXMLString(String inSerializerName, boolean inUseFilter, Locale inLocale) throws SQLException, BOMException {
		try {
			Class<?> lClass = Class.forName(inSerializerName);
			Class<?>[] lParameters = {XMLCharacterFilter.class};
			Object[] lInitArgs = {null};
			if (inUseFilter) {
				lInitArgs[0] = null; //Not used: getFilter();
			}
			XMLSerializer lSerializer = (XMLSerializer)lClass.getConstructor(lParameters).newInstance(lInitArgs);
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

	/**
	 * @see org.hip.kernel.bom.QueryResult#nextn(int)
	 */
	public DomainObjectCollection nextn(int inHowMany) throws SQLException, BOMException {
		DomainObjectCollection outObject = new DomainObjectCollectionImpl();
		Vector<String> lCheck = new Vector<String>(inHowMany);
		try { 
			for (int i = 0; this.hasMoreElements() && i < inHowMany; i++ ) {
				AbstractHitsDomainObject lObject = (AbstractHitsDomainObject)this.next();
				if (lObject == null)
			     	continue;
				String lNextID = lObject.getID();
				if (lCheck.contains(lNextID)) 
					continue;
				lCheck.add(lNextID);
				outObject.add(lObject);
			}
		} 
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);
		}
		return outObject;
	}

	/**
	 * @see org.hip.kernel.bom.QueryResult#nextnAsXMLString(int)
	 */
	public String nextnAsXMLString(int inHowMany) throws SQLException, BOMException {
		DomainObjectIterator lIterator = nextn(inHowMany).elements();
		XMLSerializer lSerializer = new XMLSerializer();
		lIterator.accept(lSerializer);
		return lSerializer.toString();
	}

	/**
	 * @see org.hip.kernel.bom.QueryResult#nextnAsXMLString(int, java.lang.String)
	 */
	public String nextnAsXMLString(int inHowMany, String inSerializerName) throws SQLException, BOMException {
		return nextnAsXMLString(inHowMany, inSerializerName, false);
	}

	/**
	 * @see org.hip.kernel.bom.QueryResult#nextnAsXMLString(int, java.lang.String, boolean)
	 */
	public String nextnAsXMLString(int inHowMany, String inSerializerName, boolean inUseFilter) throws SQLException, BOMException {
		return nextnAsXMLString(inHowMany, inSerializerName, inUseFilter, null);
	}

	/**
	 * @see org.hip.kernel.bom.QueryResult#nextnAsXMLString(int, java.lang.String, boolean, java.util.Locale)
	 */
	public String nextnAsXMLString(int inHowMany, String inSerializerName, boolean inUseFilter, Locale inLocale) throws SQLException, BOMException {
		try {
			Class<?> lClass = Class.forName(inSerializerName);
			Class<?>[] lParameters = {XMLCharacterFilter.class};
			Object[] lInitArgs = {null};
			if (inUseFilter) {
				lInitArgs[0] = null;
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
	 * @see org.hip.kernel.bom.QueryResult#setCurrentPage(org.hip.kernel.bom.Page)
	 */
	public void setCurrentPage(Page inPage) {
		currentPage = inPage;
	}

	/**
	 * @see org.hip.kernel.bom.QueryResult#load(org.hip.kernel.bom.AlternativeModelFactory)
	 */
	public Collection<AlternativeModel> load(AlternativeModelFactory inModelFactory) throws SQLException {
		//Intentionally left empty.
		return new Vector<AlternativeModel>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.QueryResult#load(org.hip.kernel.bom.AlternativeModelFactory, int)
	 */
	public Collection<AlternativeModel> load(AlternativeModelFactory inModelFactory, int inMaxEntries) throws SQLException {
		return load(inModelFactory);
	}
	
}

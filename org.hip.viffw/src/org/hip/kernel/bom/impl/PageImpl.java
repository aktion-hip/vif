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

import java.io.Serializable;

import org.hip.kernel.sys.VObject;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.Page;
import org.hip.kernel.bom.DomainObjectCollection;
import org.hip.kernel.bom.DomainObjectIterator;
import org.hip.kernel.exc.DefaultExceptionHandler;
import org.hip.kernel.util.Debug;

/**
 * 	This class implements the Page interface.
 *
 *  <PRE>
 *		QueryResult lResult = home.query("SELECT * FROM XYZ");
 *		Page lPage = new PageImpl(lResult, null, 15);
 *
 *		String lXML = "";
 *      Serializer lSerializer = new XMLSerializer();
 *
 *		while (lPage.hasMoreElements()) {
 *			DomainObject lBOM = lPage.nextElement();
 *          lBOM.accept(lSerializer);
 *			lXML += lSerializer.toString();
 *           		
 *		}
 *    	lPage.setSerialized(lXML);
 *		lPage.release();
 *
 *  </PRE>
 *
 * 	@author	Benno Luthiger
 *  @see	org.hip.kernel.bom.Page
 */
public class PageImpl extends VObject implements Page, Serializable {
		// Instance variables
		private	QueryResult				result 		= null	;
		private Page					previous	= null	;
		private Page					next		= null	;
		private DomainObjectCollection	collection	= null	;
		private DomainObjectIterator    iterator	= null	;
		private boolean					nextLoaded 	= false ;
		private String                  xmlCache   	= null	;
		private int						pageSize	= Page.DEFAULT_PAGE_SIZE ;
		private int						pageNumber 	= 1 ;

	/**
	 * PageImpl constructor for default page size.
	 *
	 * @param inResult org.hip.kernel.bom.QueryResult
	 * @param inPrevious org.hip.kernel.bom.Page
	 */
	public PageImpl(QueryResult inResult, Page inPrevious) {
		this(inResult, inPrevious, Page.DEFAULT_PAGE_SIZE);
	}
	/**
	 * PageImpl constructor, initializes the a collection of DomainObjects.
	 *
	 * @param inResult org.hip.kernel.bom.QueryResult
	 * @param inPrevious org.hip.kernel.bom.Page
	 * @param inPageSize int
	 */
	public PageImpl(QueryResult inResult, Page inPrevious, int inPageSize) {
		super();
		result		= inResult;
		previous	= inPrevious;
		pageSize	= inPageSize;
		this.initCollection();
		if (inPrevious != null) 
			pageNumber = inPrevious.getPageNumber() + 1;
		     
	}
	/**
	 * @return org.hip.kernel.bom.Page
	 */
	public Page getNextPage() {
	
		// Lazy load
		if (nextLoaded == false)
			loadNextPage();
		
		return (next != null) ? next : this;
	}
	/**
	 * Returns the collection with all DomainObjects.
	 *
	 * @return org.hip.kernel.bom.DomainObjectCollection
	 */
	public DomainObjectCollection getObjects() {
		return (collection != null) ? collection : new DomainObjectCollectionImpl();
	}
	/**
	 * @return int
	 */
	public int getPageNumber() {
		return pageNumber;
	}
	/**
	 * @return org.hip.kernel.bom.Page
	 */
	public Page getPreviousPage() {
		return (previous != null) ? previous : this;
	}
	/**
	 * @return org.hip.kernel.bom.QueryResult
	 */
	public QueryResult getQueryResult() {
		return result;
	}
	/**
	 * 	@return java.lang.String
	 */
	public String getSerialized() {
		return (xmlCache != null) ? xmlCache : "";
	}
	/**
	 * Returns true if the page has more elements, that is
	 * more DomainObjects.
	 *
	 * @return boolean
	 */
	public boolean hasMoreElements() {
		return iterator().hasMoreElements();
	}
	/**
	 * 
	 * @param coll org.hip.kernel.bom.DomainObjectCollection
	 */
	private synchronized void initCollection() {
		try { 
			collection = result.nextn(pageSize);
		} 
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);
		}   
	}
	/**
	 * @return boolean
	 */
	public boolean isFirstPage() {
		return getPreviousPage() == this;
	}
	/**
	 * @return boolean
	 */
	public boolean isLastPage() {
		
		if (nextLoaded == true) {
			// If loaded and null, the this is the last page
			return (next == null); 
		} 
		else {  
			return (!result.hasMoreElements());
		}	
	}
	/**
	 * 	Returns an iterator over the included domain objects.
	 * 	@return org.hip.kernel.bom.DomainObjectIterator
	 */
	private synchronized DomainObjectIterator iterator() {
		if (iterator == null)
			iterator =  getObjects().elements();
		return iterator;
	}
	private synchronized void loadNextPage() {
		// Already tryed to load
		if (nextLoaded)
			return;
			
		// Test if there are more elements in the cursor     
		if (!result.hasMoreElements())
			return;
	
		// Create new page
		next = new PageImpl(result, this, this.pageSize);
		nextLoaded = true;
	}
	/**
	 * Returns the next element.
	 *
	 * @return org.hip.kernel.bom.GeneralDomainObject
	 */
	public GeneralDomainObject nextElement() {
		return iterator().nextElement();
	}
	/**
	 * Returns the page as XML string. The output is the same as
	 * in nextnAsXML in the QueryResult.
	 *
	 * @return java.lang.String
	 */
	public String pageAsXML() {
		if (xmlCache == null) {
			XMLSerializer lSerializer = new XMLSerializer();
		    DomainObjectIterator lIterator = collection.elements();
		    lIterator.accept(lSerializer);
		    xmlCache = lSerializer.toString();
		}
		 
		return xmlCache;
	}
	/**
	 * 	This method release every contained DomainObject.
	 *  Use this with caution. The intetion of this method
	 *  is to minimize object instantiation.
	 */
	public void release() {
	}
	/**
	 * @param inXML java.lang.String
	 */
	public void setSerialized( String inXML ) {
		xmlCache = inXML;
	}
	public String toString() {
		return Debug.classMarkupString(this, "PageNumber=" + pageNumber + " PageSize=" + pageSize);
	}
}
package org.hip.kernel.servlet.impl;

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

import java.sql.SQLException;
import java.text.MessageFormat;

import org.hip.kernel.util.XMLRepresentation;
import org.hip.kernel.exc.VException;
import org.hip.kernel.exc.DefaultExceptionHandler;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.Page;
import org.hip.kernel.bom.impl.PageImpl;

import org.hip.kernel.servlet.Pageable;
import org.hip.kernel.servlet.Context;

/**
 * 	Baseclass of all views that shows data of a QueryResult object
 *
 *	@author		Benno Luthiger
 */
abstract public class AbstractXSLQueryResultView extends AbstractXSLView implements Pageable {
	//constant
	private final static String SORTED_INFO_TEMPLATE = "<sortedColumn>{0}</sortedColumn>\n<sortDir>{1}</sortDir>\n";
	
	//instance attributes
	private QueryResult result = null;
	private Page	page = null;
	private int 	numberOfRows = 0;
	private int 	lastPageNr = 0;
	private int 	pageSize = 10;
	private String sortedColumn = "";
	private String sortDirection = "";
		
	/**
	 * Class constructor with a QueryResult only
	 *
	 * @param inResult org.hip.kernel.bom.QueryResult
	 */
	public AbstractXSLQueryResultView(QueryResult inResult) {
		this(null, inResult);
	}
	
	/**
	 * Class constructor with a specified Context and QueryResult
	 *
	 * @param inContext org.hip.kernel.servlet.Context
	 * @param inResult  org.hip.kernel.bom.QueryResult
	 */
	public AbstractXSLQueryResultView(Context inContext, QueryResult inResult) {
		super(inContext);
		result = inResult;
	}
	
	/**
	 * Destructor which closes the QueryResult.
	 */
	public void finalize() throws VException {
		try{
			this.getQueryResult().close();
			super.finalize();
		}
		catch(SQLException ex){
			throw (VException) DefaultExceptionHandler.instance().convert(ex);
		}
		catch(Throwable err){
			throw (VException) DefaultExceptionHandler.instance().convert(err);
		}
	}
	
	/**
	 * Returns the number of the last page according to the current page size
	 *
	 * @return int
	 */
	protected int getLastPageNumber() {
		
		if (lastPageNr > 0)
			return lastPageNr;
	
		if (pageSize == 0)
			return 0;
		
		lastPageNr = Math.abs(numberOfRows / pageSize);
		
		if ( !(numberOfRows % pageSize == 0) ) {
			lastPageNr++;
		}
		
		return lastPageNr;
	}
	
	/**
	 * Returns the number of rows (of the QueryResult)
	 *
	 * @return int
	 * @see setNumberOfRows()
	 */
	public int getNumberOfRows() {
		return numberOfRows;
	}
	
	/**
	 * Returns the page
	 *
	 * @param org.hip.kernel.bom.Page
	 */
	protected Page getPage() {
		return page;
	}
	
	/**
	 * Returns the key for to read a page from the context or to write a
	 * page to the context.
	 *
	 * @param java.lang.String
	 */
	abstract protected String getPageKey();
	
	/**
	 * Returns the page size
	 * @return int
	 */
	public int getPageSize() {
		return pageSize;
	}
	
	/**
	 * Returns the query-result-object.
	 *
	 * @return org.hip.kernel.bom.QueryResult
	 * @see org.hip.kernel.bom.QueryResult
	 */
	protected QueryResult getQueryResult() {
		return result;
	}
	
	/**
	 * Switches to the next page of entries
	 */
	public void nextPage() {
		nextPage(1);
	}
	
	/**
	 * Skips the next inJump-1 pages and jumps to page actual+inJump
	 *
	 * @param inJump int 
	 */
	public void nextPage(int inJump) {
		for (int i = 0; i < inJump; i++) {
			page = page.getNextPage();
		}
		
		String lXmlString = "";
		if (page.getSerialized() == null || page.getSerialized().equals("")) {
			setSerialized(page, getLastPageNumber());
		}
		lXmlString = page.getSerialized();
		prepareTransformation(new XMLRepresentation(lXmlString));
	
		setPageToContext(page);
	}
	
	/**
	 * Switches to the previous page of entries
	 */
	public void previousPage() {
		previousPage(1);
	}
	
	/**
	 * Skips the previous inJump-1 pages and jumps to page actual-inJump
	 *
	 * @param inJump int 
	 */
	public void previousPage(int inJump) {
		for (int i = 0; i < inJump; i++) {
			page = page.getPreviousPage();
		}
		
		String lXmlString = "";
		if (page.getSerialized() == null || page.getSerialized().equals("")) {
			setSerialized(page, getLastPageNumber());
		}
		lXmlString = page.getSerialized();
		prepareTransformation(new XMLRepresentation(lXmlString));
	
		setPageToContext(page);
	}
	
	/**
	 * Lets the subclasses create a new page instance
	 * for paging the query result.
	 */
	protected void setNewPage() {
		page = new PageImpl(result, null, pageSize);	
	}
	
	/**
	 * Sets the number of rows the QueryResult has.
	 *
	 * @param inNumberOfRows int
	 */
	public void setNumberOfRows(int inNumberOfRows) {
		numberOfRows = inNumberOfRows;
	}
	
	/**
	 * Sets the page size
	 * @param inPageSize int
	 */
	public void setPageSize(int inPageSize) {
		pageSize = inPageSize;
	}
	
	/**
	 * Sets the page to the context.
	 */
	private void setPageToContext(Page inPage) {
		getContext().set(AbstractContext.QUERY_RESULT_VIEW_KEY, inPage);
	}
	
	/**
	 * Set the serialized string
	 *
	 * @param inPage org.hip.kernel.bom.Page
	 * @param inLastPageNumber int 
	 */
	abstract protected void setSerialized(Page inPage, int inLastPageNumber);
	
	/**
	 * Sets the information about the sorted columns in the query.
	 * 
	 * @param inSortedColumn String Number of the column that is sorted.
	 * @param inSortDir boolean true if the column is sorted DESC, false if ASC.
	 */
	protected void setSortedInfo(String inSortedColumn, boolean inSortDir) {
		sortedColumn = inSortedColumn;
		sortDirection = inSortDir ? "1" : "0";
	}
	
	/**
	 * Returns the information about the sorted columns in the query as XML string.
	 * 
	 * @return String
	 */
	protected String getSortedInfoXML() {
		return MessageFormat.format(SORTED_INFO_TEMPLATE, new Object[] {sortedColumn, sortDirection});
	}
	
	/**
	 * Set the new page content with the given QueryResult and the modified
	 * information about the sorted column.
	 *
	 * @param inNewQueryResult org.hip.kernel.bom.QueryResult
	 * @param inSortedColumn String Number of the column that is sorted.
	 * @param inSortDir boolean true if the column is sorted DESC, false if ASC.
	 */
	public void setToCurrentPage(QueryResult inNewQueryResult, String inSortedColumn, boolean inSortDir) {
		setSortedInfo(inSortedColumn, inSortDir);
		setToCurrentPage(inNewQueryResult);
	}
	
	/**
	 * Set the new page content with the given QueryResult
	 *
	 * @param inNewQueryResult org.hip.kernel.bom.QueryResult
	 */
	public void setToCurrentPage(QueryResult inNewQueryResult) {
	
		int lCurrentPageNr = page.getPageNumber();
		lastPageNr = 0;
	
		// create new Page for paging the query result
		page = new PageImpl(inNewQueryResult, null, pageSize);
		setSerialized(page, getLastPageNumber());
	 	setPageToContext(page);
	 	prepareTransformation(new XMLRepresentation(page.getSerialized()));
	
		while (lCurrentPageNr > 1 && !(page.isLastPage())){
			nextPage();
			lCurrentPageNr--;
		}
	}
	
	/**
	 * Loads the first data-page.
	 */
	public void setToFirstPage() {
		int lCurrent = page.getPageNumber();
		if (lCurrent > 1)
			previousPage(lCurrent-1);
	}
	
	/**
	 * Loads the last data-page.
	 */
	public void setToLastPage() {
		int lJump = getLastPageNumber() - page.getPageNumber();
		if (lJump > 0)
			nextPage(lJump);
	}
	
	/**
	 * Returns the current Directory view as a HTML string.
	 *
	 * @return java.lang.String
	 */ 
	public String toString() {
		return this.getHTMLString();
	}
}

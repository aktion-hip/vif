/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.hip.vif.core.util;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.TextHome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.filter.UnsupportedFilterException;

/**
 * Helper class for auto complete fields to look up bibliography items.
 * This class provides either in-memory or lazy loading containers.
 * <br/>
 * If the number of bibliography items exceeds the <code>THRESHOLD</code>,
 * the helper class returns <code>AutoCompleteHelper.LazyLoadingBibliographyContainer</code>.
 * 
 * @author Luthiger
 * Created: 04.09.2011
 */
public class AutoCompleteHelper {
	private static final int THRESHOLD = 50;
	
	private IndexedContainer titlesContainer;
	private IndexedContainer authorsContainer;
	
	/**
	 * Constructor
	 * 
	 * @param inHome TextHome the home to retrieve the text entries
	 * @param inKey KeyObject
	 * @throws VException
	 * @throws SQLException
	 */
	public AutoCompleteHelper(TextHome inHome, KeyObject inKey) throws VException, SQLException {
		if (inHome.getCount(inKey) <= THRESHOLD) {
			initInMemory(inHome, inKey);
		}
		else {
			initLazyLoading(inHome, inKey);
		}
	}
	
	/**
	 * @return IndexedContainer for the titles auto-complete field
	 */
	public IndexedContainer getTitlesContainer() {
		return titlesContainer;
	}

	/**
	 * @return IndexedContainer for the authors auto-complete field
	 */
	public IndexedContainer getAuthorsContainer() {
		return authorsContainer;
	}	

	private void initInMemory(TextHome inHome, KeyObject inKey) throws VException, SQLException {
		Set<String> lTitles = new HashSet<String>();
		Set<String> lAuthors = new HashSet<String>();
		QueryResult lTexts = inHome.select(inKey);
		while (lTexts.hasMoreElements()) {
			GeneralDomainObject lText = lTexts.next();
			lTitles.add(lText.get(TextHome.KEY_TITLE).toString());
			lAuthors.add(lText.get(TextHome.KEY_AUTHOR).toString());
		}
		
		titlesContainer = new InMemoryBibliographyContainer(new Vector<String>(lTitles));
		authorsContainer = new InMemoryBibliographyContainer(new Vector<String>(lAuthors));
	}
	
	private void initLazyLoading(TextHome inHome, KeyObject inKey) {
		titlesContainer = new LazyLoadingBibliographyContainer(inHome, TextHome.KEY_TITLE);
		authorsContainer = new LazyLoadingBibliographyContainer(inHome, TextHome.KEY_AUTHOR);
	}
	
// --- private classes ---
	
	/**
	 * The container to hold the data in memory.
	 * 
	 * @author Luthiger
	 * Created: 04.09.2011
	 */
	@SuppressWarnings("serial")
	public static class InMemoryBibliographyContainer extends IndexedContainer {
		InMemoryBibliographyContainer(List<String> inCaptions) {
			Collections.sort(inCaptions);
			for (String lCaption : inCaptions) {
				addItem(lCaption);
			}
		}
	}
	
	/**
	 * The container for big data sets loading the data lazy, i.e. after the user
	 * entered some characters in the field.
	 * 
	 * @author Luthiger
	 * Created: 04.09.2011
	 */
	@SuppressWarnings("serial")
	public static class LazyLoadingBibliographyContainer extends IndexedContainer {
		private static final Logger LOG = LoggerFactory.getLogger(LazyLoadingBibliographyContainer.class);
		
		private static final int MIN_FILTER = 2;
		
		private TextHome textHome;
		private String keyField;
		
		private List<String> filteredItemIds;

		LazyLoadingBibliographyContainer(TextHome inHome, String inKeyField) {
			textHome = inHome;
			keyField = inKeyField;
		}
		
		private void addItems(List<String> inItems) {
			for (String lCaption : inItems) {
				addItem(lCaption);
			}
		}
		
		@Override
		public int size() {
			return filteredItemIds == null ? 0 : filteredItemIds.size();
		}
		
		@Override
		public Object getIdByIndex(int inIndex) {
			return filteredItemIds == null ? null : filteredItemIds.get(inIndex);
		}
		
		@Override
		protected void addFilter(Filter inFilter) throws UnsupportedFilterException {
			String lFilterString = getFilterString(inFilter);
			if (lFilterString.length() <= MIN_FILTER) {
				filteredItemIds = null;
				return;
			}

	        // Filter
	        try {
				filteredItemIds = textHome.getAutoCompleteSelection(keyField, getFilterString(inFilter));
				addItems(filteredItemIds);
			} catch (VException exc) {
				LOG.error("Error while lazy loading text items.", exc);
				filteredItemIds = Collections.emptyList();
			} catch (SQLException exc) {
				LOG.error("Error while lazy loading text items.", exc);
				filteredItemIds = Collections.emptyList();
			}
		}
		
		private String getFilterString(Filter inFilter) {
			if (inFilter instanceof SimpleStringFilter) {
				return ((SimpleStringFilter) inFilter).getFilterString();
			}
			return "";
		}
		
	}

}

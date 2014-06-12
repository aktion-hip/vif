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

package org.hip.vif.forum.search.tasks;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import org.apache.lucene.queryParser.ParseException;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.search.VIFContentSearcher;
import org.hip.vif.forum.search.Constants;
import org.hip.vif.forum.search.data.ContributionContainer;
import org.hip.vif.forum.search.data.ContributionWrapper;
import org.hip.vif.forum.search.ui.SearchContentView;
import org.hip.vif.web.tasks.AbstractVIFTask;
import org.hip.vif.web.tasks.ForwardTaskRegistry;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Component;

/**
 * Task to search questions.
 * 
 * @author Luthiger
 * Created: 29.09.2011
 */
@SuppressWarnings("serial")
@Partlet
public class SearchContentTask extends AbstractVIFTask implements Property.ValueChangeListener {

	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#needsPermission()
	 */
	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_SEARCH;
	}

	@Override
	protected Component runChecked() throws VException {
		emptyContextMenu();
		return new SearchContentView(getHelpContent(), this);
	}
	
	private URL getHelpContent() {
		String lHelpContentFile = String.format("searchHelpContent_%s.html", getAppLocale().getLanguage()); //$NON-NLS-1$
		return this.getClass().getClassLoader().getResource(lHelpContentFile);
	}

	/**
	 * Callback method.
	 * 
	 * @param inQuery String the search query
	 * @return {@link ContributionContainer} the search result
	 * @throws VException 
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws {@link NoHitsException}
	 */
	public ContributionContainer search(String inQuery) throws VException, SQLException, IOException, ParseException {
		VIFContentSearcher lSearcher = new VIFContentSearcher();
		return ContributionContainer.createData(lSearcher.search(inQuery));
	}

	public void valueChange(ValueChangeEvent inEvent) {
		Property lProperty = inEvent.getProperty();
		if (lProperty.getValue() instanceof ContributionWrapper) {
			ContributionWrapper lContribution = (ContributionWrapper) lProperty.getValue();
			setQuestionID(lContribution.getQuestionID());
			setGroupID(lContribution.getGroupID());
			sendEvent(ForwardTaskRegistry.ForwardQuestionShow.class);
		}
	}	

}

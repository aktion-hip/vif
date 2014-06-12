/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2010, Benno Luthiger

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
package org.hip.vif.core.bom;

import java.sql.SQLException;
import java.util.List;

import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Text.ITextValues;

/**
 * The interface for the home of <code>Text</code> models.
 *
 * @author Luthiger
 * Created: 14.06.2010
 */
public interface TextHome extends DomainObjectHome {

	public final static String KEY_ID = "ID";
	public final static String KEY_VERSION = "Version";
	public final static String KEY_TITLE = "Title";
	public final static String KEY_AUTHOR = "Author";
	public final static String KEY_COAUTHORS = "CoAuthors";
	public final static String KEY_SUBTITLE = "Subtitle";
	public final static String KEY_YEAR = "Year";
	public final static String KEY_PUBLICATION = "Publication";
	public final static String KEY_PAGES = "Pages";
	public final static String KEY_VOLUME = "Volume";
	public final static String KEY_NUMBER = "Number";
	public final static String KEY_PUBLISHER = "Publisher";
	public final static String KEY_PLACE = "Place";
	public final static String KEY_REMARK = "Remark";
	public final static String KEY_REFERENCE = "Reference";
	public final static String KEY_TYPE = "Type";
	public final static String KEY_STATE = "State";
	public final static String KEY_FROM = "From";
	public final static String KEY_TO = "To";
	public static final String KEY_BIBLIO_TYPE = "biblioType"; //to make field in 'author/reviewer to text' unambiguous.
	public static final String KEY_BIBLIOGRAPHY = "bibliography";
	
	/**
	 * Returns the published version of the text entry identified by the specified id.
	 * 
	 * @param inTextID String
	 * @return {@link Text}
	 * @throws VException
	 * @throws SQLException
	 */
	public Text getTextPublished(Long inTextID) throws VException, SQLException;
	
	/**
	 * Tests for the existence of a published version of a bibliography entry with a specified ID.
	 * 
	 * @param inTextID Long
	 * @return boolean <code>true</code> if there is a published version of the entry with the specified ID 
	 * @throws VException
	 * @throws SQLException
	 */
	public boolean hasPublishedVersion(Long inTextID) throws VException, SQLException;
	
	/**
	 * Returns the text entry identified by the specified id.
	 * 
	 * @param inTextID Long
	 * @param inVersion int
	 * @return {@link Text}
	 * @throws VException
	 * @throws SQLException
	 */
	public Text getText(Long inTextID, int inVersion) throws VException, SQLException;
	
	/**
	 * Returns the text entry identified by the specified id-version string.
	 * 
	 * @param inIDVersion String the entry's id and version in a <code>String</code> of form '<code>ID-Version</code>'
	 * @return {@link Text}
	 * @throws VException
	 * @throws SQLException
	 */
	public Text getText(String inIDVersion) throws VException, SQLException;
	
	/**
	 * Returns the selection of published text entries that matches the string entered in the auto complete field.
	 * 
	 * @param inField String the name of the selection field
	 * @param inLookup String the search string entered in the auto complete field
	 * @return List<String>
	 * @throws VException
	 * @throws SQLException 
	 */
	public List<String> getAutoCompleteSelection(String inField, String inLookup) throws VException, SQLException;

	/**
	 * Returns a list of published entries
	 * 
	 * @param inTitle String
	 * @param inAuthor String
	 * @return {@link QueryResult}
	 * @throws VException 
	 * @throws SQLException 
	 */
	public QueryResult selectTitleOrAuthor(String inTitle, String inAuthor) throws VException, SQLException;

	/**
	 * Creates a new version of an existing entry using the specified values. 
	 * 
	 * @param inText {@link Text} the existing entry 
	 * @param inValues {@link ITextValues} new values
	 * @param inActorID Long
	 * @throws SQLException 
	 * @throws VException 
	 */
	public void createNewVersion(Text inText, ITextValues inValues, Long inActorID) throws VException, SQLException;

	/**
	 * Checks whether the specified reference is unique and, if not, create one being unique.
	 * 
	 * @param inReference String the reference to check
	 * @return String the unique reference
	 * @throws VException
	 * @throws SQLException
	 */
	public String checkReference(String inReference) throws VException, SQLException;
	
}

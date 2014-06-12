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

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.SettingException;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.exc.BOMChangeValueException;

/**
 * The interface of the <code>Text</code> model.
 * 
 * @author Luthiger Created: 14.06.2010
 */
public interface Text extends DomainObject {
	public static String FORMAT_ID_VERSION = "%s-%s";
	public static String DELIMITER_ID_VERSION = "-";

	/**
	 * Use case: save the values of a new text entry.
	 * 
	 * @param inValues
	 *            {@link ITextValues}
	 * @param inActorID
	 *            Long
	 * @return Long the id of the newly created <code>Text</code> entry.
	 * @throws BOMChangeValueException
	 */
	public Long ucNew(ITextValues inValues, Long inActorID)
			throws BOMChangeValueException;

	/**
	 * Use case: create a new entry in the table with the instance's values.
	 * 
	 * @param inActorID
	 *            Long
	 * @return Long the id of the newly created <code>Text</code> entry.
	 * @throws BOMChangeValueException
	 */
	public Long ucNew(Long inActorID) throws BOMChangeValueException;

	/**
	 * Use case: save the values of a new text entry with the specified version.
	 * 
	 * @param inTextID
	 *            Long the entries ID
	 * @param inVersion
	 *            int the version of the new text entry
	 * @param inReference
	 *            String the existing version's reference
	 * @param inValues
	 *            {@link ITextValues}
	 * @param inActorID
	 *            Long
	 * @return Long the id of the newly created <code>Text</code> entry.
	 * @throws BOMChangeValueException
	 */
	public Long ucNew(Long inTextID, int inVersion, String inReference,
			ITextValues inValues, Long inActorID)
			throws BOMChangeValueException;

	/**
	 * Use case: save changed values to <code>Text</code> model.
	 * 
	 * @param inValues
	 *            {@link ITextValues} parameter object containing the new
	 *            values.
	 * @param inActorID
	 *            Long the author's id
	 * @throws BOMChangeValueException
	 */
	public void ucSave(ITextValues inValues, Long inActorID)
			throws BOMChangeValueException;

	/**
	 * Use case: save changed values to <code>Text</code> model.
	 * 
	 * @param inValues
	 *            {@link ITextValues} parameter object containing the new
	 *            values.
	 * @param inState
	 *            String the model's workflow state
	 * @param inActorID
	 *            Long the author's id
	 * @throws BOMChangeValueException
	 */
	public void ucSave(ITextValues inValues, String inState, Long inActorID)
			throws BOMChangeValueException;

	/**
	 * Use case: the business object's values have changed, therefore, persist
	 * these changes.
	 * 
	 * @param inActorID
	 *            Long the author's id
	 * @throws BOMChangeValueException
	 */
	public void ucSave(Long inActorID) throws BOMChangeValueException;

	/**
	 * Creates a new version of this text entry.
	 * 
	 * @param inActorID
	 *            Long the author's id
	 * @return Long the id of the newly created <code>Text</code> entry.
	 * @throws BOMChangeValueException
	 */
	public Long createNewVersion(Long inActorID) throws BOMChangeValueException;

	// /**
	// * <pre>&lt;option value="Value" selected="selected">Label&lt;/option>
	// * ...
	// * </pre>
	// *
	// * @return String the html for the options in an html select of a text
	// type. The appropriate option is displayed <code>selected</code>.
	// * @throws VException
	// */
	// public String getOptionsSelected() throws VException;

	/**
	 * Sets the values of this model to the passed values.
	 * 
	 * @param inValues
	 *            {@link ITextValues}
	 * @throws SettingException
	 */
	public void setValuesToModel(ITextValues inValues) throws SettingException;

	// /**
	// * Renders the text entry for a plain text notification mail.
	// *
	// * @return String
	// * @throws VException
	// * @throws SQLException
	// */
	// public String getNotification() throws VException, SQLException;
	//
	// /**
	// * Renders the text entry for a html notification mail.
	// *
	// * @return String
	// * @throws VException
	// * @throws SQLException
	// */
	// public String getNotificationHtml() throws VException, SQLException;

	/**
	 * Returns the entry's version ID.
	 * 
	 * @return String <code>ID-Version</code>
	 * @throws VException
	 */
	public String getIDVersion() throws VException;

	/**
	 * Returns the entry's version.
	 * 
	 * @return int
	 * @throws VException
	 */
	public int getVersion() throws VException;

	/**
	 * Validates the instance, i.e. checks whether the instance's fields
	 * <code>title</code>, <code>author</code> and <code>year</code> are filled.
	 * 
	 * @return boolean <code>true</code> if the instance is ready to be saved.
	 */
	public boolean isValid();

	// --- inner classes ---

	/**
	 * Interface for parameter objects.
	 * 
	 * @author Luthiger Created: 08.07.2010
	 */
	public static interface ITextValues {
		public Long getBiblioType();

		public String getBiblioTitle();

		public String getBiblioAuthor();

		public String getBiblioCoAuthor();

		public String getBiblioSubtitle();

		public String getBiblioYear();

		public String getBiblioPublication();

		public String getBiblioPages();

		public String getBiblioVolume();

		public String getBiblioNumber();

		public String getBiblioPublisher();

		public String getBiblioPlace();

		public String getBiblioText();
	}

}

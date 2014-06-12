/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2001, Benno Luthiger

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
import java.util.Collection;
import java.util.Locale;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.exc.VException;
import org.hip.kernel.util.XMLRepresentation;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.exc.InvalidAuthenticationException;

/**
 * This interface defines the behaviour of the Member domain object. This domain
 * object holds all the information to identify a person to make it capable to
 * contribute to the discussion forum.
 * 
 * @author: Benno Luthiger
 */
public interface Member extends DomainObject {
	/**
	 * Authenticate this member wiht the specified password.
	 * 
	 * @param inPassword
	 *            java.lang.String
	 * @param inLocale
	 *            Locale
	 * @exception org.hip.vif.core.exc.bom.impl.InvalidAuthenticationException
	 */
	void checkAuthentication(String inPassword, Locale inLocale)
			throws VException, InvalidAuthenticationException;

	/**
	 * Returns the content of this object as XML string. The data is secure
	 * because information about ID and password is filtered.
	 * 
	 * @return org.hip.xml.utilities.XMLRepresentation
	 */
	XMLRepresentation getSecureData();

	/**
	 * Save the new password.
	 * 
	 * @param inPassword
	 *            java.lang.String
	 * @exception org.hip.vif.core.exc.bom.impl.BOMChangeValueException
	 */
	void savePwrd(String inPassword) throws BOMChangeValueException;

	/**
	 * Insert a new entry and save the data.
	 * 
	 * @param inUserID
	 *            java.lang.String
	 * @param inName
	 *            java.lang.String
	 * @param inFirstName
	 *            java.lang.String
	 * @param inStreet
	 *            java.lang.String
	 * @param inZIP
	 *            java.lang.String
	 * @param inCity
	 *            java.lang.String
	 * @param inTel
	 *            java.lang.String
	 * @param inFax
	 *            java.lang.String
	 * @param inMail
	 *            java.lang.String
	 * @param inSex
	 *            java.lang.String
	 * @param inLanguage
	 *            java.lang.String
	 * @param inPassword
	 *            java.lang.String
	 * @param inRoles
	 *            java.lang.String[]
	 * @return Long the new entry's MemberID.
	 * @throws Exception
	 */
	Long ucNew(String inUserID, String inName, String inFirstName,
			String inStreet, String inZIP, String inCity, String inTel,
			String inFax, String inMail, String inSex, String inLanguage,
			String inPassword, String[] inRoles) throws Exception;

	/**
	 * Persist the new member entry.
	 * 
	 * @param inRoles
	 *            inRoles Collection of ids of roles assigned to the new member
	 * @return Long the new entry's MemberID
	 * @throws Exception
	 */
	Long ucNew(Collection<String> inRoles) throws Exception;

	/**
	 * Save the data of an edited entry.
	 * 
	 * @param inName
	 *            java.lang.String
	 * @param inFirstName
	 *            java.lang.String
	 * @param inStreet
	 *            java.lang.String
	 * @param inZIP
	 *            java.lang.String
	 * @param inCity
	 *            java.lang.String
	 * @param inTel
	 *            java.lang.String
	 * @param inFax
	 *            java.lang.String
	 * @param inMail
	 *            java.lang.String
	 * @param inSex
	 *            java.lang.String
	 * @param inLanguage
	 *            java.lang.String
	 * @param inRoles
	 *            java.lang.String[]
	 * @param inActorID
	 *            java.lang.Long
	 * @return boolean <code>true</code> if the member's new roles are different
	 *         from the old ones.
	 * @exception org.hip.vif.core.exc.bom.impl.BOMChangeValueException
	 */
	boolean ucSave(String inName, String inFirstName, String inStreet,
			String inZIP, String inCity, String inTel, String inFax,
			String inMail, String inSex, String inLanguage, String[] inRoles,
			Long inActorID) throws BOMChangeValueException;

	/**
	 * Save the data of an edited entry. No roles data is added, therefore, the
	 * associations to roles are left unchanged.
	 * 
	 * @param inName
	 *            java.lang.String
	 * @param inFirstName
	 *            java.lang.String
	 * @param inStreet
	 *            java.lang.String
	 * @param inZIP
	 *            java.lang.String
	 * @param inCity
	 *            java.lang.String
	 * @param inTel
	 *            java.lang.String
	 * @param inFax
	 *            java.lang.String
	 * @param inMail
	 *            java.lang.String
	 * @param inSex
	 *            java.lang.String
	 * @param inLanguage
	 *            java.lang.String
	 * @param inActorID
	 *            java.lang.Long
	 * @exception org.hip.vif.core.exc.bom.impl.BOMChangeValueException
	 */
	public void ucSave(String inName, String inFirstName, String inStreet,
			String inZIP, String inCity, String inTel, String inFax,
			String inMail, String inSex, String inLanguage, Long inActorID)
			throws BOMChangeValueException;

	/**
	 * Use case: the business object's values have changed, therefore, persist
	 * these changes.
	 * 
	 * @param inActorID
	 *            Long
	 * @exception org.hip.vif.core.exc.bom.impl.BOMChangeValueException
	 */
	void ucSave(Long inActorID) throws BOMChangeValueException;

	/**
	 * Use case: the business object's values have changed, therefore, persist
	 * these changes.
	 * 
	 * @param inRoles
	 *            {@link Collection} the IDs of the member's roles
	 * @param inActorID
	 *            Long
	 * @return boolean <code>true</code> if the member's new roles are different
	 *         from the old ones.
	 * @throws BOMChangeValueException
	 */
	boolean ucSave(Collection<String> inRoles, Long inActorID)
			throws BOMChangeValueException;

	/**
	 * This method inserts an new su for initials administration purpose
	 * 
	 * @param inUserID
	 *            java.lang.String
	 * @param inPassword
	 *            java.lang.String
	 * @param inLanguage
	 *            java.lang.String
	 * @exception org.hip.vif.core.exc.bom.impl.BOMChangeValueException
	 */
	void ucCreateSU(String inUserID, String inPassword, String inLanguage)
			throws BOMChangeValueException;

	/**
	 * This method deletes the member entry and reports the deletion to the
	 * member history table. Overwrites method of superclass.
	 * 
	 * @param inActorID
	 *            java.lang.Long
	 * @exception java.sql.SQLException
	 * @throws VException
	 */
	void delete(Long inActorID) throws SQLException, VException;

	/**
	 * Returns the ID of the best role the member has.
	 * 
	 * @return int the ID of the best role the member has
	 * @throws VException
	 * @throws SQLException
	 */
	int getBestRole() throws VException, SQLException;

	/**
	 * Validates the instance, i.e. checks whether the mandatory fields are
	 * filled.
	 * 
	 * @return boolean <code>true</code> if the instance is ready to be saved.
	 */
	boolean isValid();

	/**
	 * The member object's text field <code>UserSettings</code> can contain any
	 * xml formatted settings. The settings are organized in key/value pairs.
	 * 
	 * @param inKey
	 *            String the setting's key
	 * @return String the value, may be <code>null</code> if there's no setting
	 *         with the specified key
	 * @throws VException
	 */
	String getUserSettings(String inKey) throws VException;

	/**
	 * Persists the specified value under the specified key in the usersettings.
	 * 
	 * @param inKey
	 *            String
	 * @param inValue
	 *            String
	 * @throws VException
	 * @throws SQLException
	 */
	void setUserSettings(String inKey, String inValue) throws VException,
			SQLException;
}

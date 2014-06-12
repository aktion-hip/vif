/*
	This package is part of the application VIF.
	Copyright (C) 2007, Benno Luthiger

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
package org.hip.vif.member.ldap;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Locale;

import org.hip.kernel.bom.directory.LDAPObject;
import org.hip.kernel.bom.impl.XMLCharacterFilter;
import org.hip.kernel.bom.impl.XMLSerializer;
import org.hip.kernel.exc.VException;
import org.hip.kernel.util.XMLRepresentation;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.exc.ExternIDNotUniqueException;

/**
 * Member object retrieved from a LDAP server.
 *
 * @author Luthiger
 * 06.07.2007
 */
@SuppressWarnings("serial")
public class LDAPMemberObject extends LDAPObject implements Member {
	public final static String HOME_CLASS_NAME = "org.hip.vif.member.ldap.LDAPMemberHome";

	/* (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObject#getHomeClassName()
	 */
	public String getHomeClassName() {
		return HOME_CLASS_NAME;
	}

	public void checkAuthentication(String inPassword, Locale inLocale) throws VException {		
		// intentionally left empty		
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.vif.bom.Member#delete(java.lang.Long)
	 */
	public void delete(Long inActorID) throws SQLException, VException {
		// intentionally left empty		
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.vif.bom.Member#getBestRole()
	 */
	public int getBestRole() throws VException, SQLException {
		// intentionally left empty		
		return 0;
	}

	/**
	 * @see org.hip.vif.bom.Member#getSecureData()
	 */
	public XMLRepresentation getSecureData() {
		XMLSerializer lSerializer = new XMLSerializer(XMLCharacterFilter.DEFAULT_FILTER);
		accept(lSerializer);
		return new XMLRepresentation(lSerializer.toString());
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.vif.bom.Member#savePwrd(java.lang.String)
	 */
	public void savePwrd(String inPassword) throws BOMChangeValueException {
		// intentionally left empty		
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.vif.bom.Member#ucCreateSU(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void ucCreateSU(String inUserID, String inPassword, String inLanguage) throws BOMChangeValueException {
		// intentionally left empty		
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.vif.bom.Member#ucNew(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	public Long ucNew(String inUserID, String inName, String inFirstName,
			String inStreet, String inZIP, String inCity, String inTel,
			String inFax, String inMail, String inSex, String inLanguage,
			String inPassword, String[] inRoles)
			throws BOMChangeValueException, ExternIDNotUniqueException {
		// intentionally left empty		
		return null;
	}
	public Long ucNew(Collection<String> inRoles) throws Exception {
		// intentionally left empty		
		return null;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.vif.bom.Member#ucSave(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String[], java.lang.Long)
	 */
	public boolean ucSave(String inName, String inFirstName, String inStreet,
			String inZIP, String inCity, String inTel, String inFax,
			String inMail, String inSex, String inLanguage, String[] inRoles,
			Long inActorID) throws BOMChangeValueException {
		// intentionally left empty		
		return false;
	}

	/**
	 * No implementation provided.
	 * 
	 * @see org.hip.vif.bom.Member#ucSave(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Long)
	 */
	public void ucSave(String inName, String inFirstName, String inStreet,
			String inZIP, String inCity, String inTel, String inFax,
			String inMail, String inSex, String inLanguage, Long inActorID)
			throws BOMChangeValueException {
		// intentionally left empty		
	}
	public void ucSave(Long inActorID) throws BOMChangeValueException {
		// intentionally left empty		
	}
	public boolean ucSave(Collection<String> inRoles, Long inActorID) throws BOMChangeValueException {
		// intentionally left empty		
		return false;
	}

	public boolean isValid() {
		return true;
	}

	public String getUserSettings(String inKey) throws VException {
		return null;
	}

	public void setUserSettings(String inKey, String inValue) throws VException, SQLException {
		// intentionally left empty
	}

}

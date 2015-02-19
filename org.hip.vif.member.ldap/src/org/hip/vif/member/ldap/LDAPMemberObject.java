/**
	This package is part of the application VIF.
	Copyright (C) 2007-2015, Benno Luthiger

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

/** Member object retrieved from a LDAP server.
 *
 * @author Luthiger 06.07.2007 */
@SuppressWarnings("serial")
public class LDAPMemberObject extends LDAPObject implements Member { // NOPMD by lbenno
    public final static String HOME_CLASS_NAME = "org.hip.vif.member.ldap.LDAPMemberHome";

    @Override
    public String getHomeClassName() { // NOPMD by lbenno
        return HOME_CLASS_NAME;
    }

    @Override
    public void checkAuthentication(final String inPassword, final Locale inLocale) throws VException { // NOPMD by
        // lbenno
        // intentionally left empty
    }

    /** No implementation provided.
     *
     * @see org.hip.vif.bom.Member#delete(java.lang.Long) */
    @Override
    public void delete(final Long inActorID) throws SQLException, VException {
        // intentionally left empty
    }

    /** No implementation provided.
     *
     * @see org.hip.vif.bom.Member#getBestRole() */
    @Override
    public int getBestRole() throws VException, SQLException {
        // intentionally left empty
        return 0;
    }

    /** @see org.hip.vif.bom.Member#getSecureData() */
    @Override
    public XMLRepresentation getSecureData() {
        final XMLSerializer lSerializer = new XMLSerializer(XMLCharacterFilter.DEFAULT_FILTER);
        accept(lSerializer);
        return new XMLRepresentation(lSerializer.toString());
    }

    /** No implementation provided.
     *
     * @see org.hip.vif.bom.Member#savePwrd(java.lang.String) */
    @Override
    public void savePwrd(final String inPassword) throws BOMChangeValueException {
        // intentionally left empty
    }

    /** No implementation provided.
     *
     * @see org.hip.vif.bom.Member#ucCreateSU(java.lang.String, java.lang.String, java.lang.String) */
    @Override
    public void ucCreateSU(final String inUserID, final String inPassword, final String inLanguage)
            throws BOMChangeValueException {
        // intentionally left empty
    }

    /** No implementation provided.
     *
     * @see org.hip.vif.bom.Member#ucNew(java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String[]) */
    @Override
    public Long ucNew(final String inUserID, final String inName, final String inFirstName, // NOPMD by lbenno 
            final String inStreet, final String inZIP, final String inCity, final String inTel,
            final String inFax, final String inMail, final String inSex, final String inLanguage,
            final String inPassword, final String[] inRoles) // NOPMD by lbenno 
                    throws BOMChangeValueException, ExternIDNotUniqueException {
        // intentionally left empty
        return null;
    }

    @Override
    public Long ucNew(final Collection<String> inRoles) throws Exception { // NOPMD by lbenno
        // intentionally left empty
        return null;
    }

    /** No implementation provided.
     *
     * @see org.hip.vif.bom.Member#ucSave(java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String[], java.lang.Long) */
    @Override
    public boolean ucSave(final String inName, final String inFirstName, final String inStreet, // NOPMD by lbenno 
            final String inZIP, final String inCity, final String inTel, final String inFax,
            final String inMail, final String inSex, final String inLanguage, final String[] inRoles,
            final Long inActorID) throws BOMChangeValueException {
        // intentionally left empty
        return false;
    }

    /** No implementation provided.
     *
     * @see org.hip.vif.bom.Member#ucSave(java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.Long) */
    @Override
    public void ucSave(final String inName, final String inFirstName, final String inStreet, // NOPMD by lbenno 
            final String inZIP, final String inCity, final String inTel, final String inFax,
            final String inMail, final String inSex, final String inLanguage, final Long inActorID)
                    throws BOMChangeValueException {
        // intentionally left empty
    }

    @Override
    public void ucSave(final Long inActorID) throws BOMChangeValueException { // NOPMD by lbenno
        // intentionally left empty
    }

    @Override
    public boolean ucSave(final Collection<String> inRoles, final Long inActorID) throws BOMChangeValueException { // NOPMD
        // by
        // lbenno
        // intentionally left empty
        return false;
    }

    @Override
    public boolean isValid() { // NOPMD by lbenno
        return true;
    }

    @Override
    public String getUserSettings(final String inKey) throws VException { // NOPMD by lbenno
        return null;
    }

    @Override
    public void setUserSettings(final String inKey, final String inValue) throws VException, SQLException { // NOPMD by
        // lbenno
        // intentionally left empty
    }

}

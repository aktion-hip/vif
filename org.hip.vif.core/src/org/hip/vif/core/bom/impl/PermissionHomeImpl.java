/**
    This package is part of the persistency layer of the application VIF.
	Copyright (C) 2001-2014, Benno Luthiger

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
package org.hip.vif.core.bom.impl;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.BOMInvalidKeyException;
import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.AbstractDomainObjectHome;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Permission;
import org.hip.vif.core.bom.PermissionHome;
import org.hip.vif.core.exc.ExternIDNotUniqueException;

/** This domain object implements the PermissionHome interface.
 *
 * @author Benno Luthiger
 * @see org.hip.vif.core.bom.PermissionHome */
@SuppressWarnings("serial")
public class PermissionHomeImpl extends DomainObjectHomeImpl implements PermissionHome {

    /*
     * Every home has to know the class it handles. They provide access to this name through the method
     * <I>getObjectClassName</I>;
     */
    private final static String PERMISSION_CLASS_NAME = "org.hip.vif.core.bom.impl.PermissionImpl";

    /*
     * The current version of the domain object framework provides no support for externelized metadata. We build them
     * up with hard coded definition strings.
     */
    // CAUTION: The current version of the lightweight DomainObject
    // framework makes only a limited check of the correctness
    // of the definition string. Make extensive basic test to
    // ensure that the definition works correct.
    private final static String XML_OBJECT_DEF =
            "<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
                    "<objectDef objectName='Permission' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	\n" +
                    "	<keyDefs>	\n" +
                    "		<keyDef>	\n" +
                    "			<keyItemDef seq='0' keyPropertyName='" + KEY_ID + "'/>	\n" +
                    "		</keyDef>	\n" +
                    "	</keyDefs>	\n" +
                    "	<propertyDefs>	\n" +
                    "		<propertyDef propertyName='" + KEY_ID + "' valueType='Number' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblPermission' columnName='PERMISSIONID'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + KEY_LABEL + "' valueType='String' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblPermission' columnName='SPERMISSIONLABEL'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + KEY_DESCRIPTION
                    + "' valueType='String' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblPermission' columnName='SPERMISSIONDESCRIPTION'/>	\n" +
                    "		</propertyDef>	\n" +
                    "	</propertyDefs>	\n" +
                    "</objectDef>";

    /** Constructor for PermissionHomeImpl. */
    public PermissionHomeImpl() {
        super();
    }

    /** @see GeneralDomainObjectHome#getObjectClassName() */
    @Override
    public String getObjectClassName() {
        return PERMISSION_CLASS_NAME;
    }

    /** @see AbstractDomainObjectHome#getObjectDefString() */
    @Override
    protected String getObjectDefString() {
        return XML_OBJECT_DEF;
    }

    /** Returns the Permission identified by the specified permission ID.
     *
     * @param inPermissionID java.lang.String
     * @throws org.hip.kernel.bom.BOMInvalidKeyException */
    @Override
    public Permission getPermission(final Long inPermissionID) throws BOMInvalidKeyException {
        final KeyObject lKey = new KeyObjectImpl();
        try {
            lKey.setValue(KEY_ID, inPermissionID);
            return (Permission) findByKey(lKey);
        } catch (final VException exc) {
            throw new BOMInvalidKeyException(exc.getMessage());
        }
    }

    @Override
    public Permission getPermission(final String inPermissionID) throws BOMInvalidKeyException {
        return getPermission(new Long(inPermissionID));
    }

    /** Creates a new permission entry.
     *
     * @param inLabel String
     * @param inDescription String
     * @return Long The auto-generated value of the new entry.
     * @throws BOMException
     * @throws ExternIDNotUniqueException */
    @Override
    public Long createPermission(final String inLabel, final String inDescription) throws BOMException,
            ExternIDNotUniqueException {
        final Permission lPermission = (Permission) create();
        return lPermission.ucNew(inLabel, inDescription);
    }

}

/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2003, Benno Luthiger

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

import java.sql.SQLException;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.Responsible;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.bom.TextAuthorReviewerHome;

/** This domain object home implements the TextAuthorReviewerHome interface.
 *
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.TextAuthorReviewerHome */
@SuppressWarnings("serial")
public class TextAuthorReviewerHomeImpl extends AbstractResponsibleHome implements TextAuthorReviewerHome {
    /*
     * Every home has to know the class it handles. They provide access to this name through the method
     * <I>getObjectClassName</I>;
     */
    private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.TextAuthorReviewerImpl";

    private final static String XML_OBJECT_DEF =
            "<?xml version='1.0' encoding='ISO-8859-1'?>	\n"
                    +
                    "<objectDef objectName='TextAuthorReviewer' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	\n"
                    +
                    "	<keyDefs>	\n" +
                    "		<keyDef>	\n" +
                    "			<keyItemDef seq='0' keyPropertyName='"
                    + KEY_TEXT_ID
                    + "'/>	\n"
                    +
                    "			<keyItemDef seq='1' keyPropertyName='"
                    + KEY_VERSION
                    + "'/>	\n"
                    +
                    "			<keyItemDef seq='2' keyPropertyName='"
                    + ResponsibleHome.KEY_MEMBER_ID
                    + "'/>	\n"
                    +
                    "		</keyDef>	\n"
                    +
                    "	</keyDefs>	\n"
                    +
                    "	<propertyDefs>	\n"
                    +
                    "		<propertyDef propertyName='"
                    + KEY_TEXT_ID
                    + "' valueType='Long' propertyType='simple'>	\n"
                    +
                    "			<mappingDef tableName='tblTextAuthorReviewer' columnName='TextID'/>	\n"
                    +
                    "		</propertyDef>	\n"
                    +
                    "		<propertyDef propertyName='"
                    + KEY_VERSION
                    + "' valueType='Long' propertyType='simple'>	\n"
                    +
                    "			<mappingDef tableName='tblTextAuthorReviewer' columnName='nVersion'/>	\n"
                    +
                    "		</propertyDef>	\n"
                    +
                    "		<propertyDef propertyName='"
                    + ResponsibleHome.KEY_MEMBER_ID
                    + "' valueType='Long' propertyType='simple'>	\n"
                    +
                    "			<mappingDef tableName='tblTextAuthorReviewer' columnName='MemberID'/>	\n"
                    +
                    "		</propertyDef>	\n"
                    +
                    "		<propertyDef propertyName='"
                    + ResponsibleHome.KEY_TYPE
                    + "' valueType='Number' propertyType='simple'>	\n"
                    +
                    "			<mappingDef tableName='tblTextAuthorReviewer' columnName='nType'/>	\n"
                    +
                    "		</propertyDef>	\n"
                    +
                    "		<propertyDef propertyName='"
                    + ResponsibleHome.KEY_CREATED
                    + "' valueType='Timestamp' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblTextAuthorReviewer' columnName='dtCreation'/>	\n" +
                    "		</propertyDef>	\n" +
                    "	</propertyDefs>	\n" +
                    "</objectDef>";

    /** @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectClassName() */
    @Override
    public String getObjectClassName() {
        return OBJECT_CLASS_NAME;
    }

    /** @see org.hip.kernel.bom.impl.AbstractDomainObjectHome#getObjectDefString() */
    @Override
    protected String getObjectDefString() {
        return XML_OBJECT_DEF;
    }

    @Override
    public void setAuthor(final Long inMemberID, final Long inTextID, final int inVersion) throws VException,
            SQLException {
        final DomainObject lAuthor = create();
        lAuthor.set(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        lAuthor.set(TextAuthorReviewerHome.KEY_VERSION, new Long(inVersion));
        setAuthorReviewer(lAuthor, inMemberID, inTextID);
    }

    @Override
    public void setReviewer(final Long inMemberID, final Long inTextID, final int inVersion) throws VException,
            SQLException {
        final DomainObject lReviwer = create();
        lReviwer.set(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue());
        lReviwer.set(TextAuthorReviewerHome.KEY_VERSION, new Long(inVersion));
        setAuthorReviewer(lReviwer, inMemberID, inTextID);
    }

    private void setAuthorReviewer(final DomainObject inDomainObject, final Long inMemberID, final Long inTextID)
            throws VException, SQLException {
        inDomainObject.set(ResponsibleHome.KEY_MEMBER_ID, inMemberID);
        inDomainObject.set(TextAuthorReviewerHome.KEY_TEXT_ID, inTextID);
        inDomainObject.insert(true);
    }

    @Override
    protected KeyObject getContributionKey(final Integer inContributionID) throws VException {
        final KeyObject outKey = new KeyObjectImpl();
        outKey.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, inContributionID);
        return outKey;
    }

    /** We have to override because the text contribution's id is something like <code>id-version</code> */
    @Override
    public Responsible getAuthor(final String inContributionID) throws VException, SQLException {
        final String[] lTextID = inContributionID.split("-");
        final KeyObject lKey = getContributionKey(new Integer(lTextID[0]));
        lKey.setValue(TextAuthorReviewerHome.KEY_VERSION, new Long(lTextID[1]));
        lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        return (Responsible) findByKey(lKey);
    }

    @Override
    public void removeReviewer(final Long inReviewerID, final Long inTextID, final int inVersion) throws VException,
            SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, inReviewerID);
        lKey.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, inTextID);
        lKey.setValue(TextAuthorReviewerHome.KEY_VERSION, new Long(inVersion));
        lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue());
        final DomainObject lEntry = findByKey(lKey);
        lEntry.set(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER_REFUSED.getValue());
        lEntry.update(true);
    }

    @Override
    public Member getAuthor(final Long inTextID, final int inVersion) throws Exception {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, inTextID);
        lKey.setValue(TextAuthorReviewerHome.KEY_VERSION, new Long(inVersion));
        lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        final DomainObject lEntry = findByKey(lKey);
        return BOMHelper.getMemberCacheHome().getMember(lEntry.get(ResponsibleHome.KEY_MEMBER_ID).toString());
    }

    @Override
    public boolean checkRefused(final Long inReviewerID, final Long inTextID, final int inVersion) throws VException,
            SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, inTextID);
        lKey.setValue(TextAuthorReviewerHome.KEY_VERSION, new Long(inVersion));
        lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, inReviewerID);
        lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER_REFUSED.getValue());
        return getCount(lKey) != 0;
    }

}

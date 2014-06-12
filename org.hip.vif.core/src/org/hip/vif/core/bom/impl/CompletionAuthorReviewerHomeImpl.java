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
import org.hip.vif.core.bom.CompletionAuthorReviewerHome;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.ResponsibleHome;

/**
 * This domain object home implements the CompletionAuthorReviewerHome interface.
 * 
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.CompletionAuthorReviewerHome
 */
public class CompletionAuthorReviewerHomeImpl extends AbstractResponsibleHome implements CompletionAuthorReviewerHome {
	/* Every home has to know the class it handles. They provide access to
		this name through the method <I>getObjectClassName</I>;
	*/
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl";

	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
		"<objectDef objectName='CompletionAuthorReviewer' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	\n" +
		"	<keyDefs>	\n" +
		"		<keyDef>	\n" +
		"			<keyItemDef seq='0' keyPropertyName='" + KEY_COMPLETION_ID + "'/>	\n" +
		"			<keyItemDef seq='1' keyPropertyName='" + ResponsibleHome.KEY_MEMBER_ID + "'/>	\n" +
		"		</keyDef>	\n" +
		"	</keyDefs>	\n" +
		"	<propertyDefs>	\n" +
		"		<propertyDef propertyName='" + KEY_COMPLETION_ID + "' valueType='Number' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblCompletionAuthorReviewer' columnName='CompletionID'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + ResponsibleHome.KEY_MEMBER_ID + "' valueType='Number' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblCompletionAuthorReviewer' columnName='MemberID'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + ResponsibleHome.KEY_TYPE + "' valueType='Number' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblCompletionAuthorReviewer' columnName='nType'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + ResponsibleHome.KEY_CREATED + "' valueType='Timestamp' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblCompletionAuthorReviewer' columnName='dtCreation'/>	\n" +
		"		</propertyDef>	\n" +
		"	</propertyDefs>	\n" +
		"</objectDef>";	

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectClassName()
	 */
	public String getObjectClassName() {
		return OBJECT_CLASS_NAME;
	}

	/**
	 * @see org.hip.kernel.bom.impl.AbstractDomainObjectHome#getObjectDefString()
	 */
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}

	/**
	 * Sets the specified member as author of the specified completion.
	 * 
	 * @param inMemberID java.lang.Long
	 * @param inCompletionID java.lang.Long
	 * @throws org.hip.kernel.exc.VException
	 * @throws java.sql.SQLException
	 */
	public void setAuthor(Long inMemberID, Long inCompletionID) throws VException, SQLException {
		DomainObject lAuthor = create();
		lAuthor.set(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		setAuthorReviewer(lAuthor, inMemberID, inCompletionID);
	}

	/**
	 * Sets the specified member as reviewer of the specified completion.
	 * 
	 * @param inMemberID java.lang.Long
	 * @param inCompletionID java.lang.Long
	 * @throws org.hip.kernel.exc.VException
	 * @throws java.sql.SQLException
	 */
	public void setReviewer(Long inMemberID, Long inCompletionID) throws VException, SQLException {
		DomainObject lReviewer = create();
		lReviewer.set(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue());
		setAuthorReviewer(lReviewer, inMemberID, inCompletionID);
	}
	
	private void setAuthorReviewer(DomainObject inDomainObject, Long inMemberID, Long inCompletionID) throws VException, SQLException {
		inDomainObject.set(ResponsibleHome.KEY_MEMBER_ID, inMemberID);
		inDomainObject.set(KEY_COMPLETION_ID, inCompletionID);
		inDomainObject.insert(true);
	}

	protected KeyObject getContributionKey(Integer inContributionID) throws VException {
		KeyObject outKey = new KeyObjectImpl();
		outKey.setValue(KEY_COMPLETION_ID, inContributionID);
		return outKey;
	}

	public void removeReviewer(Long inReviewerID, Long inCompletionID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, inReviewerID);
		lKey.setValue(CompletionAuthorReviewerHome.KEY_COMPLETION_ID, inCompletionID);
		lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue());
		DomainObject lEntry = findByKey(lKey);
		lEntry.set(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER_REFUSED.getValue());
		lEntry.update(true);
	}

	public Member getAuthor(Long inCompletionID) throws VException, Exception {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(CompletionAuthorReviewerHome.KEY_COMPLETION_ID, inCompletionID);
		lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		DomainObject lEntry = findByKey(lKey);
		return BOMHelper.getMemberCacheHome().getMember(lEntry.get(ResponsibleHome.KEY_MEMBER_ID).toString());
	}

	public boolean checkRefused(Long inReviewerID, Long inCompletionID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(CompletionAuthorReviewerHome.KEY_COMPLETION_ID, inCompletionID);
		lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, inReviewerID);
		lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER_REFUSED.getValue());		
		return getCount(lKey) != 0;
	}

}

package org.hip.vif.core.bom.impl;

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

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.QuestionAuthorReviewerHome;
import org.hip.vif.core.bom.ResponsibleHome;

/**
 * Home of join from the question-author/reviewer BOM to the member BOM.
 * This home can be used to retrieve the member data of all authors or reviewers
 * responsible for a specified question.
 * 
 * @author: Benno Luthiger
 */
public class JoinQuestionToAuthorReviewerHome extends JoinedDomainObjectHomeImpl {
	//	Every home has to know the class it handles. They provide access to
	//	this name through the method <I>getObjectClassName</I>;
	private final static String JOIN_OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinQuestionToAuthorReviewer";

	/* 	The current version of the domain object framework provides
		no support for externelized metadata. We build them up with
		hard coded definition strings.
	*/
	//	CAUTION:	The current version of the lightweight DomainObject
	//				framework makes only a limited check of the correctness
	//				of the definition string. Make extensive basic test to
	//				ensure that the definition works correct.

	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
		"<joinedObjectDef objectName='JoinQuestionToAuthorReviewer' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n" +
		"	<columnDefs>	\n" +
		"		<columnDef columnName='" + QuestionAuthorReviewerHome.KEY_QUESTION_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionAuthorReviewerImpl'/>	\n" +
		"		<columnDef columnName='" + ResponsibleHome.KEY_TYPE + "' domainObject='org.hip.vif.core.bom.impl.QuestionAuthorReviewerImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_USER_ID + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_NAME + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_FIRSTNAME + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_STREET + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_ZIP + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_CITY + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_SEX + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_MAIL + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"	</columnDefs>	\n" +
		"	<joinDef joinType='EQUI_JOIN'>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.QuestionAuthorReviewerImpl'/>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<joinCondition>	\n" +
		"			<columnDef columnName='" + ResponsibleHome.KEY_MEMBER_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionAuthorReviewerImpl'/>	\n" +
		"			<columnDef columnName='" + MemberHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		</joinCondition>	\n" +
		"	</joinDef>	\n" +
		"</joinedObjectDef>";
		
	/**
	 * Constructor for JoinQuestionToAuthorReviewer.
	 */
	public JoinQuestionToAuthorReviewerHome() {
		super();
	}

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectClassName()
	 */
	public String getObjectClassName() {
		return JOIN_OBJECT_CLASS_NAME;
	}

	/**
	 * @see org.hip.kernel.bom.impl.AbstractDomainObjectHome#getObjectDefString()
	 */
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}

	/**
	 * Returns the entry of the author responsible for the specified question.
	 * 
	 * @param inQuestionID String
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getAuthors(String inQuestionID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionAuthorReviewerHome.KEY_QUESTION_ID, new Long(inQuestionID));
		lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		return select(lKey);
	}
	
	/**
	 * Returns the entry of the reviewer responsible for the specified question.
	 * 
	 * @param inQuestionID String
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getReviewers(String inQuestionID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionAuthorReviewerHome.KEY_QUESTION_ID, new Long(inQuestionID));
		lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue());
		return select(lKey);
	}
}

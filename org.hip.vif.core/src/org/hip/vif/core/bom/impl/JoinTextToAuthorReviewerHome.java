package org.hip.vif.core.bom.impl;

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.bom.TextAuthorReviewerHome;

/**
 * Home of join from the text-author/reviewer BOM to the member BOM.
 * This home can be used to retrieve the member data of all authors or reviewers
 * responsible for a specified text entry.
 *
 * @author Luthiger
 * Created: 01.07.2010
 */
@SuppressWarnings("serial")
public class JoinTextToAuthorReviewerHome extends JoinedDomainObjectHomeImpl {
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinTextToAuthorReviewer";
	
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<joinedObjectDef objectName='JoinTextToAuthorReviewer' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	" +
		"	<columnDefs>	\n" +
		"		<columnDef columnName='" + TextAuthorReviewerHome.KEY_TEXT_ID + "' domainObject='org.hip.vif.core.bom.impl.TextAuthorReviewerImpl'/>	\n" +
		"		<columnDef columnName='" + TextAuthorReviewerHome.KEY_VERSION + "' domainObject='org.hip.vif.core.bom.impl.TextAuthorReviewerImpl'/>	\n" +
		"		<columnDef columnName='" + ResponsibleHome.KEY_TYPE + "' domainObject='org.hip.vif.core.bom.impl.TextAuthorReviewerImpl'/>	\n" +
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
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.TextAuthorReviewerImpl'/>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<joinCondition>	\n" +
		"			<columnDef columnName='" + ResponsibleHome.KEY_MEMBER_ID + "' domainObject='org.hip.vif.core.bom.impl.TextAuthorReviewerImpl'/>	\n" +
		"			<columnDef columnName='" + MemberHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		</joinCondition>	\n" +
		"	</joinDef>	\n" +
		"</joinedObjectDef>";

	/**
	 * Returns the name of the objects which this home can create.
	 *
	 * @return java.lang.String
	 */
	public String getObjectClassName() {
		return OBJECT_CLASS_NAME;
	}

	/**
	 * Returns the object definition string of the class managed by this home.
	 *
	 * @return java.lang.String
	 */
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}

	/**
	 * Returns the entry of the author responsible for the specified text version. 
	 * 
	 * @param inTextID Long
	 * @param inVersion int
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getAuthors(Long inTextID, int inVersion) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, inTextID);
		lKey.setValue(TextAuthorReviewerHome.KEY_VERSION, new Integer(inVersion));
		lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		return select(lKey);
	}
	
	/**
	 * Returns the entry of the reviewer responsible for the specified text version.
	 * 
	 * @param inTextID Long
	 * @param inVersion int
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getReviewers(Long inTextID, int inVersion) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, inTextID);
		lKey.setValue(TextAuthorReviewerHome.KEY_VERSION, new Integer(inVersion));
		lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue());
		return select(lKey);
	}

}

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

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.naming.NamingException;

import org.hip.kernel.bom.BOMInvalidKeyException;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.directory.LDAPContextManager;
import org.hip.kernel.bom.directory.LDAPObjectHome;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.exc.InvalidAuthenticationException;
import org.hip.vif.core.member.IMemberInformation;
import org.hip.vif.core.util.ExternalObjectDefUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Home class for member objects retrieved from a LDAP server.
 *
 * @author Luthiger
 * 03.07.2007
 */
@SuppressWarnings("serial")
public class LDAPMemberHome extends LDAPObjectHome implements MemberHome {
	private static final Logger LOG = LoggerFactory.getLogger(LDAPMemberHome.class);
	
	private final static String OBJECT_DEF_FILE = "LDAPOBJECTDEF.xml";
	public final static String OBJECT_CLASS_NAME = "org.hip.vif.member.ldap.LDAPMemberObject";

	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectClassName()
	 */
	public String getObjectClassName() {
		return OBJECT_CLASS_NAME;
	}

	/**
	 * Reads the content of the file <code>$TOMCAT_HOME/webapps/vifapp/WEB-INF/conf/LDAPOBJECTDEF.xml</code>.
	 */
	protected String getObjectDefString() {
		File lObjectDefFile = ExternalObjectDefUtil.getObjectDefFile(OBJECT_DEF_FILE);
		if (!lObjectDefFile.exists()) {
			return "";
		}
		
		return ExternalObjectDefUtil.readObjectDef(lObjectDefFile);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.bom.MemberHome#checkAuthentication(java.lang.String, java.lang.String, org.hip.vif.servlets.VIFContext)
	 */
	public Member checkAuthentication(String inUserID, String inPassword) throws InvalidAuthenticationException, BOMChangeValueException {
		String lMessage = getClass().getName() + ": Couldn't validate";
		try {
			if (LDAPContextManager.getInstance().checkAuthentication(getKeyColumn(), getBaseDir(), inUserID, inPassword)) {
				return null;				
			}
		} 
		catch (VException exc) {
			lMessage = exc.getMessage();
		}
		throw new InvalidAuthenticationException(lMessage);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.bom.MemberHome#getMemberByUserID(java.lang.String)
	 */
	public Member getMemberByUserID(String inUserID) throws BOMInvalidKeyException {
		KeyObject lKey = new KeyObjectImpl();
		try {
			lKey.setValue(MemberHome.KEY_USER_ID, inUserID);
			return (Member)findByKey(lKey);
		}
		catch (VException exc) {
			throw new BOMInvalidKeyException(exc.getMessage());
		}
	}

	/**
	 * Not applicable in this case, therefore, no implementation provided.
	 * 
	 * @see org.hip.vif.bom.MemberHome#getActor(org.hip.vif.servlets.VIFContext)
	 */
	public Member getActor() throws BOMInvalidKeyException {
		// intentionally left empty
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.bom.MemberHome#getMember(java.lang.String)
	 */
	public Member getMember(String inMemberID) throws BOMInvalidKeyException {
		//create a key for the MemberID
		KeyObject lKeyUserID = new KeyObjectImpl();
		try {
			LOG.debug("LDAPMemberHome.getMember({})", inMemberID);
			lKeyUserID.setValue(KEY_ID, inMemberID);
			return (Member)findByKey(lKeyUserID);
		}
		catch (VException exc) {
			throw new BOMInvalidKeyException(exc.getMessage());
		}
	}

	/**
	 * Not applicable in this case, therefore, no implementation provided.
	 * 
	 * @see org.hip.vif.bom.MemberHome#getMember(java.lang.Long)
	 */
	public Member getMember(Long inMemberID) throws BOMInvalidKeyException {
		// intentionally left empty
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.core.bom.MemberHome#getMembers(java.util.Collection)
	 */
	public Collection<Member> getMembers(Collection<Long> inMemberIDs) {
		Collection<Member> outMembers = new ArrayList<Member>();
		for (Long lMemberID : inMemberIDs) {			
			try {
				outMembers.add(getMember(lMemberID));
			}
			catch (BOMInvalidKeyException exc) {
				//left blank intentionally
			}
		}
		return outMembers;
	}

	/**
	 * Not applicable in this case, therefore, no implementation provided.
	 * 
	 * @see org.hip.vif.bom.MemberHome#updateMemberCache(org.hip.vif.member.IMemberInformation)
	 */
	public Member updateMemberCache(IMemberInformation inInformation) throws SQLException, VException {
		// intentionally left empty
		return null;
	}

	/**
	 * Not applicable in this case, therefore, no implementation provided.
	 * 
	 * @see org.hip.kernel.bom.directory.LDAPObjectHome#checkStructure(java.lang.String)
	 */
	public boolean checkStructure(String inSchemaPattern) throws SQLException, NamingException {
		// intentionally left empty
		return false;
	}

	@Override
	protected Vector<Object> createTestObjects() {
		return null;
	}
	
}

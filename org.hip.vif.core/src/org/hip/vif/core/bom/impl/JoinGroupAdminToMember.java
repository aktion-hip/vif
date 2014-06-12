package org.hip.vif.core.bom.impl;

/**
 * This class implements the join between the domain objects GroupAdmin and Member.
 * 
 * Created on 24.07.2002
 * @author Benno Luthiger
 */
public class JoinGroupAdminToMember extends AbstractMember {
	public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinGroupAdminToMemberHome";

	/**
	 * Constructor for JoinGroupAdminToMember.
	 */
	public JoinGroupAdminToMember() {
		super();
	}

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObject#getHomeClassName()
	 */
	public String getHomeClassName() {
		return HOME_CLASS_NAME;
	}

}

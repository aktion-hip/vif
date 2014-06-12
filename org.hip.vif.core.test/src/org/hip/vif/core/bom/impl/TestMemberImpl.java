package org.hip.vif.core.bom.impl;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.impl.MemberImpl;

/**
 * Special member object for testing purpose, overriding the method <code>isSaveableMember()</code> and returning <code>true</code> in any case.
 *
 * @author Luthiger
 * Created: 08.05.2008
 */
@SuppressWarnings("serial")
public class TestMemberImpl extends MemberImpl {
	public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.MemberImplTest$MemberHomeImplSub";
	@Override
	protected boolean isSaveableMember() throws VException {
		return true;
	}
}

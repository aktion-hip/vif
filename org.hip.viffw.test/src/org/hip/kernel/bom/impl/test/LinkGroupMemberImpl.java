package org.hip.kernel.bom.impl.test;

import org.hip.kernel.bom.impl.DomainObjectImpl;

/**
 * TestObject used to test the joining of domain objects.
 * 
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class LinkGroupMemberImpl extends DomainObjectImpl {
	private final static String TESTOBJECT_HOME_CLASS_NAME = "org.hip.kernel.bom.impl.test.LinkGroupMemberHomeImpl";

	public String getHomeClassName() {
		return TESTOBJECT_HOME_CLASS_NAME;
	}
}

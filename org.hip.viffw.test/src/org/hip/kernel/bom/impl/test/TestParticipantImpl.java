package org.hip.kernel.bom.impl.test;

import org.hip.kernel.bom.impl.DomainObjectImpl;

/**
 * TestObject used for the unit test JoinedObjectDefGeneratorTest (and others)
 * 
 * @author Benno Luthiger
 * Created on Dec 21, 2003
 */
@SuppressWarnings("serial")
public class TestParticipantImpl extends DomainObjectImpl {
	private final static String HOME_CLASS_NAME = "org.hip.kernel.bom.impl.test.TestParticipantHomeImpl";

	/**
	 * This Method returns the class name of the home.
	 *
	 * @return java.lang.String
	 */
	public String getHomeClassName() {
		return HOME_CLASS_NAME;
	}
}

package org.hip.kernel.bom.impl.test;

import org.hip.kernel.bom.impl.DomainObjectImpl;

/**
 * TestObject used for the unit test DomainObjectImplTest
 * 
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class QuestionImpl extends DomainObjectImpl {
	private final static String TESTOBJECT_HOME_CLASS_NAME = "org.hip.kernel.bom.impl.test.QuestionHomeImpl";

	public String getHomeClassName() {
		return TESTOBJECT_HOME_CLASS_NAME;
	}
}

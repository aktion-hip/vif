package org.hip.kernel.bom.impl.test;

import org.hip.kernel.bom.impl.DomainObjectImpl;

/**
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class Test2DomainObjectImpl extends DomainObjectImpl {
	private final static String TESTOBJECT_HOME_CLASS_NAME = "org.hip.kernel.bom.impl.test.Test2DomainObjectHomeImpl";

	public String getHomeClassName() {
		return TESTOBJECT_HOME_CLASS_NAME;
	}
}

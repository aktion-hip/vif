package org.hip.vif.web.bom.test;

import org.hip.kernel.bom.impl.DomainObjectImpl;

/**
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class Test2DomainObjectImpl extends DomainObjectImpl {
	public final static String TESTOBJECT_HOME_CLASS_NAME = "org.hip.vif.bom.impl.Test2DomainObjectHomeImpl";

	public String getHomeClassName() {
		return TESTOBJECT_HOME_CLASS_NAME;
	}
}

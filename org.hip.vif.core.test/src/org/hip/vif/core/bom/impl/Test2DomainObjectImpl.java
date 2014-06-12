package org.hip.vif.core.bom.impl;

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

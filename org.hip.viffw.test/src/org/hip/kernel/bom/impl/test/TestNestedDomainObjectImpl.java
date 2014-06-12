package org.hip.kernel.bom.impl.test;

import org.hip.kernel.bom.impl.DomainObjectImpl;

/**
 * 
 * 
 * @author Benno Luthiger
 * Created on Dec 24, 2003
 */
@SuppressWarnings("serial")
public class TestNestedDomainObjectImpl extends DomainObjectImpl {

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObject#getHomeClassName()
	 */
	public String getHomeClassName() {
		return "org.hip.kernel.bom.impl.test.TestNestedDomainObjectHomeImpl";
	}
}

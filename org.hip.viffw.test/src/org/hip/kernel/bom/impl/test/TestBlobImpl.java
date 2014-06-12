package org.hip.kernel.bom.impl.test;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.impl.DomainObjectImpl;

/**
 * @author Benno Luthiger
 * Created on 23.02.2006
 */
@SuppressWarnings("serial")
public class TestBlobImpl extends DomainObjectImpl implements DomainObject {
	public final static String HOME_CLASS_NAME = "org.hip.kernel.bom.impl.test.TestBlobHomeImpl";

	/**
	 * TestBlobImpl constructor.
	 */
	public TestBlobImpl() {
		super();
	}

	/**
	 * This Method returns the class name of the home.
	 *
	 * @return java.lang.String
	 */
	public String getHomeClassName() {
		return HOME_CLASS_NAME;
	}

}

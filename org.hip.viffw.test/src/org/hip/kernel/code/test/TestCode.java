package org.hip.kernel.code.test;

import org.hip.kernel.code.AbstractCode;

/**
 * Concrete Code for testing purpose.
 * @author: Benno Luthiger
 */
public class TestCode extends AbstractCode {
	public static final String CODEID = "CodeTest";
	/**
	 * TestCode default constructor
	 */
	public TestCode() {
		super(CODEID);
	}
	/**
	 * TestCode constructor with specified elementID.
	 *
	 * @param inElementID java.lang.String
	 */
	public TestCode(String inElementID) {
		super(CODEID, inElementID);
	}
}

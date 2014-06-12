package org.hip.kernel.util.test;

import org.hip.kernel.util.VectorAdapter;

/**
 * Collection for testing purpose.
 * Only String objects can be added to the collection.
 * 
 * @author Benno Luthiger
 */
public class TestVector extends VectorAdapter {

	/**
	 * Constructor for TestVector.
	 */
	protected TestVector() {
		super();
	}

	public boolean add(String inString) {
		return addElement(inString);
	}
}

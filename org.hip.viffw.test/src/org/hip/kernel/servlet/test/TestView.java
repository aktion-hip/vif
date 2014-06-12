package org.hip.kernel.servlet.test;

import org.hip.kernel.servlet.Context;
import org.hip.kernel.servlet.impl.AbstractView;

/**
 * Concrete implementation of a View for testing purpose.
 * 
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class TestView extends AbstractView {
	
	public TestView() {
	}

	/**
	 * @param inContext org.hip.kernel.servlet.Context
	 */
	public TestView(Context inContext) {
		super(inContext);
	}
}

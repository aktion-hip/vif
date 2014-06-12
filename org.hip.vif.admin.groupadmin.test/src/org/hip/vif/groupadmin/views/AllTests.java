package org.hip.vif.groupadmin.views;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Luthiger
 * Created: 03.09.2010
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.hip.vif.groupadmin.views");
		//$JUnit-BEGIN$
		suite.addTestSuite(BibliographyViewTest.class);
		suite.addTestSuite(QuestionViewTest.class);
		suite.addTestSuite(CompletionViewTest.class);
		//$JUnit-END$
		return suite;
	}

}

package org.hip.kernel.util.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CheckValueForUmlautsTest.class, DateCheckerTest.class,
		DefaultNameValueListTest.class, DefaultNameValueTest.class,
		EscapedStringTokenizerTest.class, FindFirstUmlautTest.class,
		ListJoinerTest.class, NameValueListVisitorTest.class,
		SortableItemTest.class, SortedListTest.class,
		TransformerProxyTest.class, VectorTest.class,
		XMLRepresentationTest.class })
public class AllTests {

}

package org.hip.vif.core.search;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ContentHitsObjectTest.class, HitsDomainObjectTest.class,
		HitsQueryResultTest.class, LuceneTest.class })
public class AllTests {

}

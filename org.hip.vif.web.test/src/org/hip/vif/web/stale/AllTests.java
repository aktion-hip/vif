package org.hip.vif.web.stale;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ RequestExpirationMailTest.class, StaleRequestHelperTest.class,
		StaleRequestRemoverTest.class, StaleTextCollectorTest.class,
		TextAuthorNotificationTest.class })
public class AllTests {

}

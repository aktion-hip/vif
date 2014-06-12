package org.hip.vif.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	org.hip.vif.core.adapters.AllTests.class,
	org.hip.vif.core.authorization.AllTests.class,
	org.hip.vif.core.bom.impl.AllTests.class,
	org.hip.vif.core.bom.search.AllTests.class,
	org.hip.vif.core.code.AllTests.class,
	org.hip.vif.core.mail.AllTests.class,
	org.hip.vif.core.member.AllTests.class,
	org.hip.vif.core.search.AllTests.class,
	org.hip.vif.core.service.AllTests.class,
	org.hip.vif.core.upgrade.AllTests.class,
	org.hip.vif.core.util.AllTests.class
})
public class AllTests {

}

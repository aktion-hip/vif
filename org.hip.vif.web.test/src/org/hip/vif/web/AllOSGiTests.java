package org.hip.vif.web;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ org.hip.vif.web.controller.AllOSGiTests.class,
	org.hip.vif.web.internal.menu.AllOSGiTests.class,
	org.hip.vif.web.tasks.AllOSGiTests.class,
	org.hip.vif.web.util.AllOSGiTests.class })
public class AllOSGiTests {

}

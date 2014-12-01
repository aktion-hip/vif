package org.hip.vif.web;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ org.hip.vif.web.components.AllTests.class,
        org.hip.vif.web.internal.menu.AllTests.class,
        org.hip.vif.web.stale.AllTests.class,
        org.hip.vif.web.tasks.AllTests.class,
        org.hip.vif.web.util.AllTests.class })
public class AllTests {

}

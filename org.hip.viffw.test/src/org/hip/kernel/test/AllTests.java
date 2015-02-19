package org.hip.kernel.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/** TestSuite that runs all the tests of the framework.<br />
 * Note: to run the tests, the properties file <code>bin/vif.properties.sample</code> has to be renamed to
 * <code>bin/vif.properties</code>.
 *
 * @author: Benno Luthiger */
@RunWith(Suite.class)
@SuiteClasses({
    org.hip.kernel.bitmap.test.AllTests.class,
    org.hip.kernel.bom.directory.test.AllTests.class,
    org.hip.kernel.bom.impl.test.AllTests.class,
    org.hip.kernel.bom.model.impl.test.AllTests.class,
    org.hip.kernel.code.test.AllTests.class,
    org.hip.kernel.exc.test.AllTests.class,
    org.hip.kernel.servlet.test.AllTests.class,
    org.hip.kernel.stext.test.AllTests.class,
    org.hip.kernel.sys.test.AllTests.class,
    org.hip.kernel.util.test.AllTests.class,
    org.hip.kernel.workflow.test.AllTests.class })
public class AllTests {

    @BeforeClass
    public static void renameToRunning() {
        VSysHelper.INSTANCE.copyToRunning();
    }

    @AfterClass
    public static void renameToWaiting() {
        VSysHelper.INSTANCE.deleteRunning();
    }
}

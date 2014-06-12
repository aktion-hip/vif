package org.hip.kernel.test;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * TestSuite that runs all the tests of the framework.<br />
 * Note: to run the tests, the properties file <code>bin/vif.properties.sample</code>
 * has to be renamed to <code>bin/vif.properties</code>.
 *
 * @author: Benno Luthiger
 */
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
	org.hip.kernel.workflow.test.AllTests.class})
public class AllTests {
	private static final String NAME_WAITING = "vif.properties.sample";
	private static final String NAME_RUNNING = "vif.properties";
	
	
	@BeforeClass
	public static void renameToRunning() {
		File lDir = new File("./bin");
		File lWaiting = new File(lDir, NAME_WAITING);
		File lRunning = new File(lDir, NAME_RUNNING);
		if (lWaiting.exists()) {
			if (lWaiting.renameTo(lRunning)) {
				System.out.println("Successfully renamed properties to " + lRunning.getAbsolutePath());
			}
		}
	}
	
	@AfterClass
	public static void renameToWaiting() {
		File lDir = new File("./bin");
		File lRunning = new File(lDir, NAME_RUNNING);
		File lWaiting = new File(lDir, NAME_WAITING);
		if (lRunning.exists()) {
			if (lRunning.renameTo(lWaiting)) {
				System.out.println("Successfully renamed properties back to " + lWaiting.getAbsolutePath());
			}
		}
	}
}

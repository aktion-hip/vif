package org.hip.vif.core.service;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Version;

/**
 * @author Luthiger
 * Created: 16.02.2012
 */
public class UpgradeRegistryTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testVersion() {
		String lVersionStr1 = "0.1";
		String lVersionStr2 = "1.1";
		String lVersionStr3 = "1.1.1";
		
		Version lVersion1 = Version.parseVersion(lVersionStr1);
		Version lVersion2 = Version.parseVersion(lVersionStr2);
		Version lVersion3 = Version.parseVersion(lVersionStr3);
		
		assertTrue(lVersion1.compareTo(lVersion1) == 0);
		assertTrue(lVersion1.compareTo(lVersion2) < 0);
		assertTrue(lVersion1.compareTo(lVersion3) < 0);
		assertTrue(lVersion2.compareTo(lVersion1) > 0);
		assertTrue(lVersion2.compareTo(lVersion2) == 0);
		assertTrue(lVersion2.compareTo(lVersion3) < 0);
		assertTrue(lVersion3.compareTo(lVersion1) > 0);
		assertTrue(lVersion3.compareTo(lVersion2) > 0);
		assertTrue(lVersion3.compareTo(lVersion3) == 0);
	}

}

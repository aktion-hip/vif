package org.hip.vif.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.hip.kernel.sys.VSys;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Luthiger
 */
public class ExternalObjectDefUtilTest {
	private final static String FILE_NAME = "EXTERNAL_TEST.xml";
	private final static String EXPECTED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>	<content>This is a test file</content></root>";
	
	private String oldContextPath;

	@Before
	public void setUp() throws Exception {
		oldContextPath = VSys.getContextPath();
	}

	@After
	public void tearDown() throws Exception {
		VSys.setContextPath(oldContextPath);
	}

	@Test
	public void testDo() throws Exception {
		File lLocation = new File("");
		VSys.setContextPath(lLocation.getCanonicalPath());
		File lTestFile = ExternalObjectDefUtil.getObjectDefFile(FILE_NAME);
		assertTrue("file exists", lTestFile.exists());
		assertEquals("name of file", FILE_NAME, lTestFile.getName());
		
		String lContent = ExternalObjectDefUtil.readObjectDef(lTestFile);
		assertEquals("content", EXPECTED, lContent);
	}

}

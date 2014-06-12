package org.hip.vif.core.util;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.hip.kernel.sys.VSys;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 23.09.2010
 */
public class WorkspaceHelperTest {

	@Test
	public void testPath() throws Exception {
		String lHere = new File("").getAbsolutePath();
		VSys.setContextPath(lHere);

		assertEquals("path", new File(lHere).getParentFile().getAbsolutePath(), WorkspaceHelper.getRootDir());
	}

}

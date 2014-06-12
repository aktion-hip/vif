package org.hip.vif.core.code;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.hip.kernel.sys.VSys;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 20.02.2012
 */
public class RoleTest {

	@BeforeClass
	public static void setUp() {
		VSys.setContextPath(new File("").getAbsolutePath());
	}

	@Test
	public final void testGetLabels() throws Exception {		
		Collection<String> lExpected = new ArrayList<String>();
		Collections.addAll(lExpected, "Administrator", "Group-Administrator", "Guest", "Member", "excluded Participant", "Participant", "SU");
		Role lState = new Role("1");
		for (String lLabel : lState.getLabels(Locale.ENGLISH.getLanguage())) {
			assertTrue(lExpected.contains(lLabel));
		}
		assertEquals(7, lState.getLabels(Locale.ENGLISH.getLanguage()).length);
	}

}

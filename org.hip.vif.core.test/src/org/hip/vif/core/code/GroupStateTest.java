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

public class GroupStateTest {
	
	@BeforeClass
	public static void setUp() {
		VSys.setContextPath(new File("").getAbsolutePath());
	}

	@Test
	public final void testGetCodeID() {
		GroupState lState = new GroupState("1");
		assertEquals("GROUPSTATES", lState.getCodeID());
	}

	@Test
	public final void testGetLabel() throws Exception {
		GroupState lState = new GroupState("1");
		assertEquals("created", lState.getLabel(Locale.ENGLISH.getLanguage()));
		assertEquals("eröffnet", lState.getLabel(Locale.GERMAN.getLanguage()));
	}

	@Test
	public final void testGetLabels() throws Exception {
		Collection<String> lExpected = new ArrayList<String>();
		Collections.addAll(lExpected, "active", "closed", "created", "open", "settled", "suspended");
		GroupState lState = new GroupState("1");
		for (String lLabel : lState.getLabels(Locale.ENGLISH.getLanguage())) {
			assertTrue(lExpected.contains(lLabel));
		}
		assertEquals(6, lState.getLabels(Locale.ENGLISH.getLanguage()).length);
	}

}

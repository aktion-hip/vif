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
 * Created: 19.02.2012
 */
public class QuestionStateTest {
	private static final String NL = "\n"; //System.getProperty("line.separator");
	
	@BeforeClass
	public static void setUp() {
		VSys.setContextPath(new File("").getAbsolutePath());
	}

	@Test
	public final void testCreateQuestionStates() throws Exception {
		String lExpected = "<codeList> " + NL + 
			"<codeListItem id=\"1\" >private</codeListItem> " + NL + 
			"<codeListItem id=\"2\" >waiting for review</codeListItem> " + NL + 
			"<codeListItem id=\"3\" >under revision</codeListItem> " + NL + 
			"<codeListItem id=\"4\" >open</codeListItem> " + NL + 
			"<codeListItem id=\"5\" >state change requested</codeListItem> " + NL + 
			"<codeListItem id=\"6\" selected=\"true\">answered</codeListItem> " + NL + 
			"<codeListItem id=\"7\" >reopen requested</codeListItem> " + NL + 
			"<codeListItem id=\"8\" >deleted</codeListItem> " + NL + 
			"</codeList> " + NL;

		assertEquals(lExpected, QuestionState.createQuestionStates("6", Locale.ENGLISH.getLanguage()));
	}
	
	@Test
	public final void testGetLabels() throws Exception {
		Collection<String> lExpected = new ArrayList<String>();
		Collections.addAll(lExpected, "private", "waiting for review", "under revision", "open", "state change requested", "answered", "reopen requested", "deleted");
		QuestionState lState = new QuestionState("1");
		for (String lLabel : lState.getLabels(Locale.ENGLISH.getLanguage())) {
			assertTrue(lExpected.contains(lLabel));
		}
		assertEquals(8, lState.getLabels(Locale.ENGLISH.getLanguage()).length);
	}

}

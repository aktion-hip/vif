package org.hip.vif.core.util;


import static org.junit.Assert.assertEquals;

import org.hip.vif.core.util.HtmlCleaner;
import org.junit.Test;

public class HtmlCleanerTest {
	private static final String NL = System.getProperty("line.separator");

	@Test
	public final void testCleanUp() throws Exception {
		String lInput = "Ohne Frage, keine <ins>Antwort</ins> !<br><strong>Neuer</strong> Abschnitt, relativ <span style=\"font-style: italic;\">flüssig</span>, da viel H<sub>2</sub>O.<br><br><br><br>";
		String lExpected = "Ohne Frage, keine " + NL + 
		"<ins>Antwort</ins>!" + NL + 
		"<br />" + NL + "<strong>Neuer</strong> Abschnitt, relativ " + NL + 
		"<span style=\"font-style: italic;\">fl&#252;ssig</span>, da viel H" + NL + 
		"<sub>2</sub>O." + NL ;
		assertEquals(lExpected, HtmlCleaner.cleanUp(lInput));
	}

	@Test
	public void testPlain() throws Exception {
		String lInput = "Ohne Frage, keine " + NL + 
		"<ins>Antwort</ins>!" + NL + 
		"<br />" + NL + "<strong>Neuer</strong> Abschnitt, relativ " + NL + 
		"<span style=\"font-style: italic;\">fl&#252;ssig</span>, da viel H" + NL + 
		"<sub>2</sub>O." + NL ;
		String lExpected = "Ohne Frage, keine Antwort! " + NL + "Neuer Abschnitt, relativ flüssig, da viel H 2O.";
		assertEquals(lExpected, HtmlCleaner.toPlain(lInput));
	}

}

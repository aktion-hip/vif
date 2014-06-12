package org.hip.kernel.util.test;

import static org.junit.Assert.*;
import org.hip.kernel.util.UmlautChange;

/**
 * @author: Benno Luthiger
 */
public class UmlautChangeTest {
	public void testConvert() {
		assertEquals("convert 1", "Jaemmerlich", UmlautChange.convert("Jämmerlich"));
		assertEquals("convert 2", "Aebuemaekkoele", UmlautChange.convert("Äbümäkköle")) ;
		assertEquals("convert 3", "Ueberzaehlig", UmlautChange.convert("Überzählig")) ;
	}
}

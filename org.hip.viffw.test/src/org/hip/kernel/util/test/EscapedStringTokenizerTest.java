package org.hip.kernel.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.hip.kernel.util.EscapedStringTokenizer;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class EscapedStringTokenizerTest {

	@Test
	public void testDo() {
		String lTest = "This is a test!";
		String[] lTestArr = {"This", "is", "a", "test!"};
		EscapedStringTokenizer lTokenizer = new EscapedStringTokenizer(lTest, " ", false);
	
		int i = 0;
		while (lTokenizer.hasMoreElements()) {
			assertEquals("token 1." + i, lTestArr[i], lTokenizer.nextElement());
			i++;
		}
	
		lTokenizer = new EscapedStringTokenizer(lTest, " ", true);
		i = 0;
		boolean lRegularToken = true;
		while (lTokenizer.hasMoreElements()) {
			if (lRegularToken) {
				assertEquals("token 2." + i, lTestArr[i], lTokenizer.nextElement());
				i++;
			}
			else {
				assertEquals("delimiter token 2." + i, " ", lTokenizer.nextElement());
			}
			lRegularToken = !lRegularToken;
		}
		
		lTest = "This is? a test!";
		String[] lTestArr2 = {"This", "is a", "test!"};
		lTokenizer = new EscapedStringTokenizer(lTest, " ", false, '?');
		i = 0;
		while (lTokenizer.hasMoreElements()) {
			assertEquals("token 3." + i, lTestArr2[i], lTokenizer.nextElement());
			i++;
		}
		
		lTokenizer = new EscapedStringTokenizer(lTest, " ", true, '?');
		i = 0;
		lRegularToken = true;
		while (lTokenizer.hasMoreElements()) {
			if (lRegularToken) {
				assertEquals("token 4." + i, lTestArr2[i], lTokenizer.nextElement());
				i++;
			}
			else {
				assertEquals("delimiter token 4." + i, " ", lTokenizer.nextElement());
			}
			lRegularToken = !lRegularToken;
		}
	
		String[] lTestArr3 = {"This", "is? a", "test!"};
		lTokenizer = new EscapedStringTokenizer(lTest, " ", false, '?', true);
		i = 0;
		while (lTokenizer.hasMoreElements()) {
			assertEquals("token 5." + i, lTestArr3[i], lTokenizer.nextElement());
			i++;
		}
	
		try {
			lTokenizer.nextElement();
			fail("Shouldn't get here.");
		}
		catch (java.util.NoSuchElementException exc) {
		}		
	}
}

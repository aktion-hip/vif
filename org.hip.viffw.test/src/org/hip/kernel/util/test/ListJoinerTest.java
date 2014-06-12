package org.hip.kernel.util.test;

import static org.junit.Assert.*;
import org.hip.kernel.util.ListJoiner;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 29.05.2007
 */
public class ListJoinerTest {
	
	@Test
	public void testJoin() {
		String lExpected = "name, firstname, zip, street";
		
		ListJoiner lJoiner = new ListJoiner();
		lJoiner.addEntry("name");
		lJoiner.addEntry("firstname");
		lJoiner.addEntry("zip");
		lJoiner.addEntry("street");		
		assertEquals("all entries exist", lExpected, lJoiner.joinSpaced(","));
		
		lExpected = "name, firstname, zip DESC, street";
		lJoiner = new ListJoiner();
		lJoiner.addEntry("name");
		lJoiner.addEntry("firstname");
		lJoiner.addEntry("zip", "%1$s %2$s", "DESC");
		lJoiner.addEntry("street");				
		assertEquals("all entries exist with modifications", lExpected, lJoiner.joinSpaced(","));
		
		lExpected = "name, firstname";
		lJoiner = new ListJoiner();
		lJoiner.addEntry("name");
		lJoiner.addEntry("firstname");
		lJoiner.addEntry("", "%1$s %2$s", "DESC");
		lJoiner.addEntry("");				
		assertEquals("last two don't exist", lExpected, lJoiner.joinSpaced(","));
		
		lExpected = "name, firstname, street";
		lJoiner = new ListJoiner();
		lJoiner.addEntry("name");
		lJoiner.addEntry("firstname");
		lJoiner.addEntry("", "%1$s %2$s", "DESC");
		lJoiner.addEntry("street");				
		assertEquals("third doesn't exist", lExpected, lJoiner.joinSpaced(","));
	}

}

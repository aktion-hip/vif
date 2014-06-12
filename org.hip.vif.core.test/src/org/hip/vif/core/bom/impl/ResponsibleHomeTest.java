package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;
import java.math.BigDecimal;

import org.hip.vif.core.bom.ResponsibleHome;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 14.08.2010
 */
public class ResponsibleHomeTest {
	
	@Test
	public void testType() throws Exception {
		assertEquals("author", new Integer(0), ResponsibleHome.Type.AUTHOR.getValue());
		assertEquals("reviewer", new Integer(1), ResponsibleHome.Type.REVIEWER.getValue());
		assertEquals("reviewer refused", new Integer(2), ResponsibleHome.Type.REVIEWER_REFUSED.getValue());
		
		assertTrue("check author int", ResponsibleHome.Type.AUTHOR.check(0));
		assertTrue("check author Integer", ResponsibleHome.Type.AUTHOR.check(new Integer(0)));
		assertTrue("check author BigDecimal", ResponsibleHome.Type.AUTHOR.check(new BigDecimal(0)));
		assertTrue("check author Long", ResponsibleHome.Type.AUTHOR.check(new Long(0)));
		assertTrue("check author String", ResponsibleHome.Type.AUTHOR.check("0"));
		
		assertFalse("not author 1", ResponsibleHome.Type.AUTHOR.check(1));
		assertFalse("not author 2", ResponsibleHome.Type.AUTHOR.check("1"));
		assertFalse("not author 3", ResponsibleHome.Type.AUTHOR.check(null));
		assertFalse("not author 4", ResponsibleHome.Type.AUTHOR.check(this));
	}

}

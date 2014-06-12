package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.*;
import org.hip.kernel.bom.SettingException;
import org.hip.kernel.bom.model.JoinDef;
import org.hip.kernel.bom.model.JoinDefDef;
import org.hip.kernel.bom.model.impl.JoinDefImpl;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class JoinDefImplTest {

	@Test
	public void testEquals() throws Exception {
	
		JoinDef lDef1 = new JoinDefImpl();
		JoinDef lDef2 = new JoinDefImpl();
		JoinDef lDef3 = new JoinDefImpl();
	
		assertNotNull("testEquals 1", lDef1);
		assertNotNull("testEquals 2", lDef2);
		assertNotNull("testEquals 3", lDef3);

		lDef1.set(JoinDefDef.joinType, "EQUI_JOIN");
		lDef1.setTableName("tblLeft");
		lDef1.setTableName("tblRight");
		lDef1.addJoinCondition(null);
		lDef1.addColumnDef("column1");
		lDef1.addColumnDef("column2");

		lDef2.set(JoinDefDef.joinType, "LEFT_JOIN");
		lDef2.setTableName("tblLeft");
		lDef2.setTableName("tblRight");
		lDef2.addJoinCondition(null);
		lDef2.addColumnDef("column1");
		lDef2.addColumnDef("column2");

		lDef3.set(JoinDefDef.joinType, "EQUI_JOIN");
		lDef3.setTableName("tblLeft");
		lDef3.setTableName("tblRight");
		lDef3.addJoinCondition(null);
		lDef3.addColumnDef("column1");
		lDef3.addColumnDef("column2");

		assertTrue("equals", lDef1.equals(lDef3));
		assertEquals("equal hash code", lDef1.hashCode(), lDef3.hashCode());

		assertTrue("not equals", !lDef1.equals(lDef2));
		assertTrue("not equal hash code", lDef1.hashCode() != lDef2.hashCode());
	}

	@Test
	public void testSet() throws Exception {
	
		JoinDef lDef = new JoinDefImpl() ;
		assertNotNull("testSet 1", lDef ) ;
	
		lDef.set(JoinDefDef.joinType, "EQUI_JOIN");
		lDef.setTableName("tblLeft");
		lDef.setTableName("tblRight");
		lDef.addJoinCondition(null);
		lDef.addColumnDef("column1");
		lDef.addColumnDef("column2");
	
		//incorrect type
		try {
			lDef.set(JoinDefDef.joinType, new Integer(0));
			fail("incorrect type");
		}
		catch (SettingException exc) {
		}
	}
	
	@Test
	public void testToString() throws Exception {
	
	    JoinDef lDef = new JoinDefImpl();
	    assertNotNull("testToString 1", lDef);
	
	    JoinDef lOuterDef = new JoinDefImpl();
	    assertNotNull("testToString 2", lOuterDef);
	
        lDef.set(JoinDefDef.joinType, "EQUI_JOIN");
        lDef.setTableName("tblLeft");
        lDef.setTableName("tblRight");
        lDef.addJoinCondition(null);
        lDef.addColumnDef("column1");
        lDef.addColumnDef("column2");
        String lExp1 = "< org.hip.kernel.bom.model.impl.JoinDefImpl joinType=\"EQUI_JOIN\" />";

        assertEquals("toString 1", lExp1, lDef.toString());

        lOuterDef.set(JoinDefDef.joinType, "LEFT_JOIN");
        lOuterDef.setTableName("tblLeftOuter");
        lOuterDef.setChildJoinDef(lDef);
        lOuterDef.addJoinCondition(null);
        lOuterDef.addColumnDef("outerColumn1");
        lOuterDef.addColumnDef("outerColumn2");
        String lExp2 = "< org.hip.kernel.bom.model.impl.JoinDefImpl joinType=\"LEFT_JOIN\" />";

        assertEquals("toString 2", lExp2, lOuterDef.toString());
	}
}

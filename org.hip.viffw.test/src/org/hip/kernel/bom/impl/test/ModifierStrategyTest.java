package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.*;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.impl.ModifierStrategy;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on 25.05.2006
 */
public class ModifierStrategyTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllFromSimple();
	}

	/*
	 * Test method for 'org.hip.kernel.bom.impl.ModifierStrategy.createModifiedSQL(DomainObjectHome)'
	 */
	@Test
	public void testCreateModifiedSQL() {
		DomainObjectHome lHome = data.getSimpleHome();
		
		ModifierStrategy lStrategy = new ModifierStrategy("Double", ModifierStrategy.AVERAGE);
		assertEquals("average", "AVG(tblTest.FDOUBLE)", new String(lStrategy.createModifiedSQL(lHome)));
		
		lStrategy = new ModifierStrategy("Double", ModifierStrategy.STDDEV);
		assertEquals("average", "STDDEV(tblTest.FDOUBLE)", new String(lStrategy.createModifiedSQL(lHome)));

		lStrategy = new ModifierStrategy(new String[] {"Double", "Amount"}, ModifierStrategy.MIN);
		assertEquals("two columns min", "MIN(tblTest.FDOUBLE), MIN(tblTest.FAMOUNT)", new String(lStrategy.createModifiedSQL(lHome)));
		
		int[] lModifiers = {ModifierStrategy.VARIANCE, ModifierStrategy.SUM};
		lStrategy = new ModifierStrategy(new String[] {"Double", "Amount"}, lModifiers);		
		assertEquals("two columns var/sum", "VARIANCE(tblTest.FDOUBLE), SUM(tblTest.FAMOUNT)", new String(lStrategy.createModifiedSQL(lHome)));
	}

}

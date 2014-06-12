package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.*;
import java.sql.Date;
import java.sql.Timestamp;

import org.hip.kernel.bom.impl.BetweenObjectImpl;
import org.junit.Test;

public class BetweenObjectImplTest {

	@Test
	public void testToString() {
		Date lDate1 = Date.valueOf("1989-08-20");
		Date lDate2 = Date.valueOf("1999-04-24");
		
		BetweenObjectImpl lBetween = new BetweenObjectImpl(lDate1, lDate2);
		assertEquals("between dates", lBetween.toString(), "BETWEEN DATE('1989-08-20') AND DATE('1999-04-24')");
		
		lBetween = new BetweenObjectImpl(lDate2, lDate1);
		assertEquals("between dates", lBetween.toString(), "BETWEEN DATE('1989-08-20') AND DATE('1999-04-24')");
		
		lBetween = new BetweenObjectImpl(new Timestamp(lDate1.getTime()), new Timestamp(lDate2.getTime()));
		assertEquals("between timestamps", lBetween.toString(), "BETWEEN TIMESTAMP('1989-08-20 00:00:00') AND TIMESTAMP('1999-04-24 00:00:00')");
	}

	@Test
	public void testToPrepared() {
		Date lDate1 = Date.valueOf("1989-08-20");
		Date lDate2 = Date.valueOf("1999-04-24");
		
		BetweenObjectImpl lBetween = new BetweenObjectImpl(lDate1, lDate2);
		assertEquals("between dates", lBetween.toPrepared(), "BETWEEN ? AND ?");
		
		lBetween = new BetweenObjectImpl(new Timestamp(lDate1.getTime()), new Timestamp(lDate2.getTime()));
		assertEquals("between timestamps", lBetween.toPrepared(), "BETWEEN ? AND ?");
	}

}

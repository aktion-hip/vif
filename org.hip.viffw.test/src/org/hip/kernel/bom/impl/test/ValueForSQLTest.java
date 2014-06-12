package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import org.hip.kernel.bom.impl.BetweenObjectImpl;
import org.hip.kernel.bom.impl.InObjectImpl;
import org.hip.kernel.bom.impl.ValueForSQL;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 15.10.2006
 */
public class ValueForSQLTest {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testGetValueAsString() {
		ValueForSQL lValue = new ValueForSQL("What's the purpose of the decimal ID?");
		assertEquals("string 1", "What\\'s the purpose of the decimal ID?", lValue.getValueAsString());
		
		lValue = new ValueForSQL("This is a quote: \"Hallo World\"");
		assertEquals("string 2", "This is a quote: \"Hallo World\"", lValue.getValueAsString());
		
		lValue = new ValueForSQL(new BigDecimal("9876.5432"));
		assertEquals("number", "9876.5432", lValue.getValueAsString());
		
		Date lDate = Date.valueOf("1989-01-20");
		lValue = new ValueForSQL(lDate);
		assertEquals("date", "DATE('1989-01-20')", lValue.getValueAsString());
		
		lValue = new ValueForSQL(new Timestamp(lDate.getTime()));
		assertEquals("timestamp", "TIMESTAMP('1989-01-20 00:00:00')", lValue.getValueAsString());
		
		Date lTo = Date.valueOf("1996-02-21");
		BetweenObjectImpl lBetween = new BetweenObjectImpl(lDate, lTo);
		lValue = new ValueForSQL(lBetween);
		assertEquals("between", "BETWEEN DATE('1989-01-20') AND DATE('1996-02-21')", lValue.getValueAsString());
		
		InObjectImpl lInObject = new InObjectImpl(new Object[] {new Integer(55), new Integer(57), new Integer(78)});
		lValue = new ValueForSQL(lInObject);
		assertEquals("in", "IN (55, 57, 78)", lValue.getValueAsString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testGetValueForPrepared() {
		ValueForSQL lValue = new ValueForSQL("What's the purpose of the decimal ID?");
		assertEquals("string 1", "?", lValue.getValueForPrepared());
		
		lValue = new ValueForSQL("This is a quote: \"Hallo World\"");
		assertEquals("string 2", "?", lValue.getValueForPrepared());
		
		lValue = new ValueForSQL(new BigDecimal("9876.5432"));
		assertEquals("number", "?", lValue.getValueForPrepared());
		
		Date lDate = Date.valueOf("1989-01-20");
		lValue = new ValueForSQL(lDate);
		assertEquals("date", "?", lValue.getValueForPrepared());
		
		lValue = new ValueForSQL(new Timestamp(lDate.getTime()));
		assertEquals("timestamp", "?", lValue.getValueForPrepared());
		
		Date lTo = Date.valueOf("1996-02-21");
		BetweenObjectImpl lBetween = new BetweenObjectImpl(lDate, lTo);
		lValue = new ValueForSQL(lBetween);
		assertEquals("between", "BETWEEN ? AND ?", lValue.getValueForPrepared());
		
		InObjectImpl lInObject = new InObjectImpl(new Object[] {new Integer(55), new Integer(57), new Integer(78)});
		lValue = new ValueForSQL(lInObject);
		assertEquals("in", "IN (?, ?, ?)", lValue.getValueForPrepared());
	}

}

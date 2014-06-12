package org.hip.kernel.bitmap.test;

import static org.junit.Assert.*;

import org.hip.kernel.bitmap.BitField;
import org.hip.kernel.bitmap.BitFieldImpl;
import org.hip.kernel.bitmap.BitRow;
import org.hip.kernel.bitmap.BitRowImpl;
import org.hip.kernel.bitmap.MatrixPosition;
import org.hip.kernel.bitmap.MatrixPositions;
import org.hip.kernel.bitmap.Tuple;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Benno Luthiger
 */
public class BitFieldTest {
	private BitField field;
	private static int columnSize = 8;

	@Before
	public void setUp() throws Exception {
		int columnSize = 8;
		field = new BitFieldImpl();

		BitRow lRow = new BitRowImpl(columnSize);
		lRow.setBitValue(1);
		field.addRow("row 1", lRow);
		lRow = new BitRowImpl(columnSize);
		lRow.setBitValue(2);
		field.addRow("row 2", lRow);
		lRow = new BitRowImpl(columnSize);
		lRow.setBitValue(4);
		field.addRow("row 3", lRow);
		lRow = new BitRowImpl(columnSize);
		lRow.setBitValue(8);
		field.addRow("row 4", lRow);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testDo() {
		assertTrue("position 1", field.getBit(2, 2));
		assertTrue("position 2", !field.getBit(1, 2));
		assertTrue("position 3", field.getBit(3, 3));
		assertTrue("position 4", !field.getBit(3, 4));
		
		BitField lField2 = field.invert();
		assertTrue("position 5", !lField2.getBit(2, 2));
		assertTrue("position 6", lField2.getBit(1, 2));
		assertTrue("position 7", !lField2.getBit(3, 3));
		assertTrue("position 8", lField2.getBit(3, 4));
		
		BitField lField3 = field.and(lField2);
		int lSum = 0;
		for (int i = 0; i < lField3.getRowSize(); i++)
			lSum += lField3.getBitRow(i).getBitValue();
		assertEquals("and of inverted", 0, lSum);
		
		assertTrue("not equal 1", !field.equals(lField2));
		assertTrue("not equal 2", !field.equals(lField3));
		assertTrue("not equal 3", !field.equals("test"));

		BitField lField4 = new BitFieldImpl();

		BitRow lRow = new BitRowImpl(columnSize);
		lRow.setBitValue(1);
		lField4.addRow("row 1", lRow);
		lRow = new BitRowImpl(columnSize);
		lRow.setBitValue(2);
		lField4.addRow("row 2", lRow);
		lRow = new BitRowImpl(columnSize);
		lRow.setBitValue(4);
		lField4.addRow("row 3", lRow);
		lRow = new BitRowImpl(columnSize);
		lRow.setBitValue(8);
		lField4.addRow("row 4", lRow);
		
		assertTrue("equal 1", field.equals(lField4));
		assertEquals("equal hash code", field.hashCode(), lField4.hashCode());
		
		MatrixPosition lPosition = new MatrixPosition(2, 2);
		assertTrue("position 9", field.getBit(lPosition));

		field.setBit(lPosition, false);
		assertTrue("not equal 4", !field.equals(lField4));
		field.setBit(lPosition, true);
		assertTrue("equal 2", field.equals(lField4));
		
		field.setBit(lPosition, false);
		MatrixPositions lDiffs = field.getDifferences(lField4);
		assertEquals("number of diffs", 1, lDiffs.size());
		assertEquals("position of diff", lPosition, (MatrixPosition)lDiffs.get(0));
		
		Tuple lTuple = field.removeRow(1);
		assertEquals("value of removed", 2, lTuple.getTupleBitRow().getBitValue());
		assertEquals("number of rows after remove", 3, field.getRowSize());
		lTuple = field.getTuple(2);
		assertEquals("value of last", 8, lTuple.getTupleBitRow().getBitValue());		
	}
	
	@Test
	public void testClone() {
		BitField lField2 = (BitField)field.clone();
		assertTrue("not equal objects 1", !field.equals(lField2));
		
		assertTrue("position 1", !lField2.getBit(2, 2));
		assertTrue("position 2", !lField2.getBit(1, 2));
		assertTrue("position 3", !lField2.getBit(3, 3));
		assertTrue("position 4", !lField2.getBit(3, 4));
		
		BitField lField3 = (BitField)field.clone();
		assertTrue("not equal objects 2", !field.equals(lField3));
		assertEquals("equal number of rows", field.getRowSize(), lField3.getRowSize());

		BitRow lRow = new BitRowImpl(columnSize);
		lRow.setBitValue(1);
		lField3.addRow("123", lRow);
		assertTrue("unequal number of rows", field.getRowSize() != lField3.getRowSize());			
	}
}

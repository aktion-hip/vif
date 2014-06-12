package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.*;
import org.hip.kernel.bom.ICriteriaStack;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.impl.SQLCriteriaStack;
import org.junit.Test;

public class SQLCriteriaStackTest {

	@Test
	public void testRender() {
		ICriteriaStack lStack = new SQLCriteriaStack();
		assertEquals("empty stack 0", "", lStack.render());
		
		lStack.addCriterium(new StringBuffer("tbl.A"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		assertEquals("stack 0", "tbl.A", lStack.render());
		
		lStack = new SQLCriteriaStack();
		lStack.addCriterium(new StringBuffer("tbl.A"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("tbl.B"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		assertEquals("stack 1", "tbl.A AND tbl.B", lStack.render());
		
		lStack = new SQLCriteriaStack();
		lStack.addCriterium(new StringBuffer("tbl.A"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("tbl.B"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("tbl.C"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		assertEquals("stack 2", "tbl.A AND tbl.B AND tbl.C", lStack.render());
		
		lStack = new SQLCriteriaStack();
		lStack.addCriterium(new StringBuffer("tbl.A"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("tbl.B"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("tbl.C"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("tbl.D"));
		lStack.addOperator(BinaryBooleanOperator.OR);
		assertEquals("stack 3", "(tbl.A AND tbl.B AND tbl.C) OR tbl.D", lStack.render());
				
		lStack = new SQLCriteriaStack();
		lStack.addCriterium(new StringBuffer("tbl.A"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("tbl.B"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("tbl.C"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("tbl.D"));
		lStack.addOperator(BinaryBooleanOperator.OR);
		lStack.addCriterium(new StringBuffer("tbl.E"));
		lStack.addOperator(BinaryBooleanOperator.OR);		
		assertEquals("stack 4", "((tbl.A AND tbl.B AND tbl.C) OR tbl.D) OR tbl.E", lStack.render());
		
		lStack = new SQLCriteriaStack();
		lStack.addCriterium(new StringBuffer("tbl.A"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("tbl.B"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("tbl.C"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("tbl.D"));
		lStack.addOperator(BinaryBooleanOperator.OR);
		lStack.addCriterium(new StringBuffer("tbl.E"));
		lStack.addOperator(BinaryBooleanOperator.OR);		
		lStack.addCriterium(new StringBuffer("tbl.F"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		assertEquals("stack 5", "(((tbl.A AND tbl.B AND tbl.C) OR tbl.D) OR tbl.E) AND tbl.F", lStack.render());

		lStack = new SQLCriteriaStack();
		lStack.addCriterium(new StringBuffer("tbl.A"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("tbl.B"));
		lStack.addOperator(BinaryBooleanOperator.OR);
		lStack.addCriterium(new StringBuffer("tbl.C"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("tbl.D"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		assertEquals("stack 6", "((tbl.A OR tbl.B) AND tbl.C) AND tbl.D", lStack.render());
		
		lStack = new SQLCriteriaStack();
		lStack.addCriterium(new StringBuffer("tbl.A"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("tbl.B"));
		lStack.addOperator(BinaryBooleanOperator.XOR);
		assertEquals("stack 7", "tbl.A XOR tbl.B", lStack.render());
	}

	@Test
	public void testRender2() {
		ICriteriaStack lStack = new SQLCriteriaStack(", ");
		System.out.println(lStack.render());
		
		lStack.addCriterium(new StringBuffer("tbl.A"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		assertEquals("stack 1", "tbl.A", lStack.render());

		lStack = new SQLCriteriaStack(", ");
		lStack.addCriterium(new StringBuffer("tbl.A"));
		lStack.addCriterium(new StringBuffer("tbl.B"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		assertEquals("stack 1", "tbl.A, tbl.B", lStack.render());

		lStack = new SQLCriteriaStack(", ");
		lStack.addCriterium(new StringBuffer("tbl.A"));
		lStack.addCriterium(new StringBuffer("tbl.B"));
		lStack.addCriterium(new StringBuffer("tbl.C"));
		lStack.addOperator(BinaryBooleanOperator.OR);
		assertEquals("stack 1", "tbl.A, tbl.B, tbl.C", lStack.render());
	}

}

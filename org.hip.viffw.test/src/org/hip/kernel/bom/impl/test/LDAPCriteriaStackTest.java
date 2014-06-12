package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;

import org.hip.kernel.bom.ICriteriaStack;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.impl.LDAPCriteriaStack;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 10.07.2007
 */
public class LDAPCriteriaStackTest {

	@Test
	public void testRender() {
		ICriteriaStack lStack = new LDAPCriteriaStack();
		lStack.addCriterium(new StringBuffer("cn=A"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		assertEquals("stack 0", "cn=A", lStack.render());
		
		lStack = new LDAPCriteriaStack();
		lStack.addCriterium(new StringBuffer("cn=A"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("cn=B"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		assertEquals("stack 1", "&(cn=A)(cn=B)", lStack.render());
		
		lStack = new LDAPCriteriaStack();
		lStack.addCriterium(new StringBuffer("cn=A"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("cn=B"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("cn=C"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		assertEquals("stack 2", "&(cn=A)(cn=B)(cn=C)", lStack.render());
		
		lStack = new LDAPCriteriaStack();
		lStack.addCriterium(new StringBuffer("cn=A"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("cn=B"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("cn=C"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("cn=D"));
		lStack.addOperator(BinaryBooleanOperator.OR);
		assertEquals("stack 3", "|(&(cn=A)(cn=B)(cn=C))(cn=D)", lStack.render());
				
		lStack = new LDAPCriteriaStack();
		lStack.addCriterium(new StringBuffer("cn=A"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("cn=B"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("cn=C"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("cn=D"));
		lStack.addOperator(BinaryBooleanOperator.OR);
		lStack.addCriterium(new StringBuffer("cn=E"));
		lStack.addOperator(BinaryBooleanOperator.OR);		
		assertEquals("stack 4", "|(|(&(cn=A)(cn=B)(cn=C))(cn=D))(cn=E)", lStack.render());
		
		lStack = new LDAPCriteriaStack();
		lStack.addCriterium(new StringBuffer("cn=A"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("cn=B"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("cn=C"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("cn=D"));
		lStack.addOperator(BinaryBooleanOperator.OR);
		lStack.addCriterium(new StringBuffer("cn=E"));
		lStack.addOperator(BinaryBooleanOperator.OR);		
		lStack.addCriterium(new StringBuffer("cn=F"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		assertEquals("stack 5", "&(|(|(&(cn=A)(cn=B)(cn=C))(cn=D))(cn=E))(cn=F)", lStack.render());

		lStack = new LDAPCriteriaStack();
		lStack.addCriterium(new StringBuffer("cn=A"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("cn=B"));
		lStack.addOperator(BinaryBooleanOperator.OR);
		lStack.addCriterium(new StringBuffer("cn=C"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		lStack.addCriterium(new StringBuffer("cn=D"));
		lStack.addOperator(BinaryBooleanOperator.AND);
		assertEquals("stack 6", "&(&(|(cn=A)(cn=B))(cn=C))(cn=D)", lStack.render());
	}	

}

package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hip.kernel.bom.model.JoinOperation;
import org.hip.kernel.bom.model.PlaceholderDef;
import org.hip.kernel.bom.model.impl.JoinOperationImpl;
import org.hip.kernel.bom.model.impl.PlaceholderDefImpl;
import org.junit.Test;

/**
 * JoinOperationImplTest.java
 * 
 * Created on 08.09.2002
 * @author Benno Luthiger
 */
public class JoinOperationImplTest {
	
	@Test
	public void testEquals() {
		JoinOperation lJoinOperation1 = new JoinOperationImpl();
		lJoinOperation1.addJoinCondition(null);
		lJoinOperation1.setTableName("tblTestMember");
		lJoinOperation1.setTableName("tblLinkMember");
		lJoinOperation1.setJoinOperand("tblTestMember.TESTMEMBERID");
		lJoinOperation1.setJoinOperand("tblLinkMember.MEMBERID");
		lJoinOperation1.setJoinType("EQUI_JOIN");
		
		JoinOperation lJoinOperation2 = new JoinOperationImpl();
		lJoinOperation2.addJoinCondition(null);
		lJoinOperation2.setTableName("tblTestMember");
		lJoinOperation2.setTableName("tblLinkMember");
		lJoinOperation2.setJoinOperand("tblTestMember.TESTMEMBERID");
		lJoinOperation2.setJoinOperand("tblLinkMember.MEMBERID");
		lJoinOperation2.setJoinType("EQUI_JOIN");
	
		assertTrue("equals", lJoinOperation1.equals(lJoinOperation2));

		lJoinOperation2.setJoinType("LEFT_JOIN_OUTER");	
		assertTrue("not equals 1", !lJoinOperation1.equals(lJoinOperation2));
		
		JoinOperation lJoinOperation3 = new JoinOperationImpl();
		lJoinOperation3.addJoinCondition(null);
		lJoinOperation3.setTableName("tblTestMember");
		lJoinOperation3.setJoinOperand("tblTestMember.TESTMEMBERID");
		lJoinOperation3.setJoinOperand("tblLinkMember.MEMBERID");
		lJoinOperation3.setJoinType("EQUI_JOIN");
		assertTrue("not equals 2", !lJoinOperation1.equals(lJoinOperation3));
	}
	
	@Test
	public void testToString() {
		String lExpected = "< org.hip.kernel.bom.model.impl.JoinOperationImpl left_tablename=\"tblTestMember\", right_tablename=\"tblLinkMember\", join_type=\"EQUI_JOIN\", SQL [tblTestMember.TESTMEMBERID = tblLinkMember.MEMBERID] />";
		JoinOperation lJoinOperation1 = new JoinOperationImpl();
		lJoinOperation1.addJoinCondition(null);
		lJoinOperation1.setTableName("tblTestMember");
		lJoinOperation1.setTableName("tblLinkMember");
		lJoinOperation1.setJoinOperand("tblTestMember.TESTMEMBERID");
		lJoinOperation1.setJoinOperand("tblLinkMember.MEMBERID");
		lJoinOperation1.setJoinType("EQUI_JOIN");
		
		assertEquals("toString", lExpected, lJoinOperation1.toString());
	}
	
	@Test
	public void testAddPlaceholderDef() {
		String lExpected = "Test-Placeholder";
		PlaceholderDef lPlaceholder = new PlaceholderDefImpl(lExpected);

		JoinOperation lJoinOperation = new JoinOperationImpl();
		lJoinOperation.setTableName("Left");
		
		assertFalse("no placeholder", lJoinOperation.hasPlaceholder(lExpected));
		
		lJoinOperation.addPlaceholderDef(lPlaceholder);
		assertTrue("with placeholder", lJoinOperation.hasPlaceholder(lExpected));
	}
	
	@Test
	public void testRenderSQL() throws Exception {
		JoinOperation lJoinOperation = new JoinOperationImpl();
		lJoinOperation.setTableName("tblTestMember");
		lJoinOperation.setTableName("tblLinkMember");
		lJoinOperation.setJoinType("EQUI_JOIN");

		lJoinOperation.addJoinCondition(null);
		lJoinOperation.setJoinOperand("tblTestMember.TESTMEMBERID");
		lJoinOperation.setJoinOperand("tblLinkMember.MEMBERID");

		lJoinOperation.addJoinCondition("AND");
		lJoinOperation.setJoinOperand("tblTestMember.VERSION");
		lJoinOperation.setJoinOperand("tblLinkMember.VERSION");
		
		String lExpected = "tblTestMember.TESTMEMBERID = tblLinkMember.MEMBERID AND tblTestMember.VERSION = tblLinkMember.VERSION";
		assertEquals("sql", lExpected, lJoinOperation.renderSQL());
	}
}

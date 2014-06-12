package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.*;
import org.hip.kernel.bom.model.impl.JoinDefOperand;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 04.08.2010
 */
public class JoinDefOperandTest {

	@Test
	public void testRender() throws Exception {
		JoinDefOperand lOperand = new JoinDefOperand();
		lOperand.setJoinOperand("TableLeft.Field1");
		lOperand.setJoinOperand("TableRight.Field1");		
		assertEquals("simple", "TableLeft.Field1 = TableRight.Field1", lOperand.renderSQL());
				
		lOperand = new JoinDefOperand("AND");
		lOperand.setJoinOperand("TableLeft.Field1");
		lOperand.setJoinOperand("TableRight.Field1");
		assertEquals("additional", " AND TableLeft.Field1 = TableRight.Field1", lOperand.renderSQL());
	}

}

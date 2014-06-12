package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.*;
import org.hip.kernel.bom.model.GroupingDef;
import org.hip.kernel.bom.model.NestedDef;
import org.hip.kernel.bom.model.impl.GroupingDefImpl;
import org.hip.kernel.bom.model.impl.NestedDefImpl;
import org.hip.kernel.util.DefaultNameValueList;
import org.hip.kernel.util.NameValueList;
import org.hip.kernel.util.VNameValueException;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on Dec 23, 2003
 */
public class NestedDefImplTest {
	NestedDef nestedDef = null;
	
	@Before
	public void setUp() throws Exception {
		String[][] lAttr1 = {{"columnName", "GroupID"}, {"domainObject", "org.hip.kernel.bom.impl.test.TestParticipantImpl"}};
		String[][] lAttr2 = {{"columnName", "MemberID"}, {"as", "Registered"}, {"modifier", "COUNT"}, {"domainObject", "org.hip.kernel.bom.impl.test.TestParticipantImpl"}};

		nestedDef = new NestedDefImpl("test");
		nestedDef.addColumnDef(createColumns(lAttr1));
		nestedDef.addColumnDef(createColumns(lAttr2));
	}
	
	private NameValueList createColumns(String[][] inAttributes) throws VNameValueException {
		NameValueList outList = new DefaultNameValueList();
		for (int i = 0; i < inAttributes.length; i++) {
			outList.setValue(inAttributes[i][0], inAttributes[i][1]);
		}
		return outList;
	}

	@Test
	public void testGetNestedQuery() {
		String lExpected = "(SELECT tblParticipant.GROUPID, COUNT(tblParticipant.MEMBERID) AS Registered FROM tblParticipant GROUP BY tblParticipant.GROUPID) test";
		try {
			GroupingDef lGroupingDef = new GroupingDefImpl("GROUP");
			String[][] lAttr = {{"columnName", "GroupID"}, {"domainObject", "org.hip.kernel.bom.impl.test.TestParticipantImpl"}};
			lGroupingDef.addColumnDef(createColumns(lAttr));
			nestedDef.addGroupingDef(lGroupingDef);
			
			assertEquals("nested", lExpected, nestedDef.getNestedQuery());
		}
		catch (Exception exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testGetSQLColumnName() {
		String lExpected = "GROUPID";
		try {
			String[][] lAttr = {{"columnName", "GroupID"}, {"alias", "Test"}, {"domainObject", "org.hip.kernel.bom.impl.test.TestParticipantImpl"}};
			nestedDef.addColumnDef(createColumns(lAttr));
			assertEquals("SQL column name 1", lExpected, nestedDef.getSQLColumnName("Test"));
			assertEquals("SQL column name 2", lExpected, nestedDef.getSQLColumnName("GroupID"));
		}
		catch (Exception exc) {
			fail(exc.getMessage());
		}
	}
}

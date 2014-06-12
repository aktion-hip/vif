package org.hip.kernel.bom.directory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.KeyCriterion;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.directory.LDAPObject;
import org.hip.kernel.bom.directory.LDAPObjectHome;
import org.hip.kernel.bom.impl.XMLSerializer;
import org.hip.kernel.util.SortableItem;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 21.07.2007
 */
public class LDAPObjectTest {
	private final static String NL = System.getProperty("line.separator");
	private final static String SERIALIZED = NL +
"<Member>" + NL +
"    <propertySet>" + NL +
"        <ID>first</ID>" + NL +
"        <Name>First</Name>" + NL +
"        <UserID>111</UserID>" + NL +
"        <Firstname>First's sn</Firstname>" + NL +
"        <Mail>first@my.org</Mail>" + NL +
"    </propertySet>" + NL +
"</Member>";

	private GeneralDomainObject model;
	
	@Before
	public void setUp() throws Exception {
		LDAPObjectHome lHome = new TestLDAPObjectHome();
		QueryResult lResult = lHome.select();
		model = lResult.next();
	}

	@Test
	public void testDo() {
		assertTrue("instanceof LDAPObject", model instanceof LDAPObject);
		assertEquals("object name", "Member", model.getObjectName());
	}
	
	@Test
	public void testAccecpt() throws BOMException, SQLException {
		XMLSerializer lSerializer = new XMLSerializer();
		model.accept(lSerializer);
		assertEquals("", SERIALIZED, lSerializer.toString());
	}
	
	@Test
	public void testGetKey() {
		KeyObject lKey = model.getKey();
		lKey.getItems2();
		for (SortableItem lItem : lKey.getItems2()) {
			KeyCriterion lCriterion = (KeyCriterion)lItem;
			assertEquals("Name", "ID", lCriterion.getName());
			assertEquals("Value", "first", lCriterion.getValue());
		}
	}

}

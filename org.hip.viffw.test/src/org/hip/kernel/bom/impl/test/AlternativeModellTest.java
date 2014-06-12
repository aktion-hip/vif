package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.hip.kernel.bom.AlternativeModel;
import org.hip.kernel.bom.AlternativeModelFactory;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.sys.VObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on Sep 22, 2004
 */
public class AlternativeModellTest {
	private static DataHouseKeeper data;
	private String[] expected = {"AAAAAAAAAA", "BBBBBBBB", "CCCCC"};
	
	private class TestAlternativeModel extends VObject implements AlternativeModel {
		public String name = "";
		public String firstName = "";
		public String toString() {
			return name + ":" + firstName;
		}
	}

	private class TestAlternativeFactory extends VObject implements AlternativeModelFactory {
		public TestAlternativeFactory() {
			super();
		}
		public AlternativeModel createModel(ResultSet inResultSet) throws SQLException {
			TestAlternativeModel outModel = new TestAlternativeModel();
			outModel.name = inResultSet.getString("sName");
			outModel.firstName = inResultSet.getString("sFirstname");
			return outModel;
		}
	}
	
	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		populate();
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromSimple();
	}
	
	@Test
	public void testDo() throws Exception {
		TestAlternativeHomeImpl lHome = data.getAlternativeHome();
		QueryResult lResult = lHome.select();
		Collection<AlternativeModel> lSet = lResult.load(new TestAlternativeFactory());
		
		assertEquals("size", expected.length, lSet.size());
		
		String lCheck = "";
		for (AlternativeModel lModel : lSet) {
			lCheck += lModel + ";";
		}
		for (int i = 0; i < expected.length; i++) {
			assertTrue("contains " + i, lCheck.indexOf(expected[i] + ":" + expected[i].toLowerCase()) >= 0);				
		}
	}

	private void populate() throws Exception {
		DomainObject lDomainObject;
		for (int i = 0; i < expected.length; i++) {
			lDomainObject = data.getSimpleHome().create();
			lDomainObject.set("Name", expected[i]);
			lDomainObject.set("Firstname", expected[i].toLowerCase());
			lDomainObject.set("Sex", new Integer(1));
			lDomainObject.insert(true);
		}
	}
}

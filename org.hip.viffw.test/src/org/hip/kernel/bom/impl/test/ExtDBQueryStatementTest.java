package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.QueryStatement;
import org.hip.kernel.bom.impl.ExtDBQueryStatement;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 18.04.2007
 */
public class ExtDBQueryStatementTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromSimple();
	}

	/**
	 * Note: needs OSGi running, because we need to register a DataSourceFactory to the DataSourceRegistery.
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testDo() throws Exception {
		String[] lNames = {"1 eins", "2 zwei", "3 drei"};
		data.createTestEntry(lNames[2]);		
		data.createTestEntry(lNames[1]);
		data.createTestEntry(lNames[0]);
		
//		DataSourceRegistry.INSTANCE.register(null);

		QueryStatement lStatement = new ExtDBQueryStatement(data.getSimpleHome(), data.getDBAccessConfiguration());
		QueryResult lResult = lStatement.executeQuery("SELECT TESTID, SNAME, SFIRSTNAME FROM tblTest ORDER BY sName");
		int i = 0;
		while (lResult.hasMoreElements()) {
			GeneralDomainObject lModel = lResult.next();
			assertEquals("name " + i, lNames[i++], lModel.get("Name"));
		}
	}

}

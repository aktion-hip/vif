package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.*;
import java.sql.SQLException;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Testing security issues (code injection etc.).
 * 
 * @author Luthiger
 * Created: 22.07.2007
 * @see http://www.owasp.org/index.php/OWASP_Top_Ten_Project
 */
public class SecurityTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromSimple();
	}
	
	@Test
	public void testCodeInjectionInsert() throws VException, SQLException {
		DomainObjectHome lHome = data.getSimpleHome();
		assertEquals("count 0", 0, lHome.getCount());
		
		//create new domain objects and insert them
		//normal
		DomainObject lDomainObject = lHome.create();
		lDomainObject.set("Name", "Doe");
		lDomainObject.set("Firstname", "Jane 1");
		lDomainObject.set("Sex", new Integer(1));
		lDomainObject.insert(true);
		assertEquals("count 1", 1, lHome.getCount());
		
		//having apostrophe included
		lDomainObject = lHome.create();
		lDomainObject.set("Name", "Doe 'test");
		lDomainObject.set("Firstname", "Jane 2");
		lDomainObject.set("Sex", new Integer(1));
		lDomainObject.insert(true);		
		assertEquals("count 2", 2, lHome.getCount());
		
		//having quotation mark included
		lDomainObject = lHome.create();
		lDomainObject.set("Name", "Doe \"test");
		lDomainObject.set("Firstname", "Jane 3");
		lDomainObject.set("Sex", new Integer(1));
		lDomainObject.insert(true);		
		assertEquals("count 3", 3, lHome.getCount());
		
		//various selects
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue("Firstname", "Jane 3");
		assertEquals("select 1", 1, process(lHome.select(lKey)));
		
		lKey = new KeyObjectImpl();
		lKey.setValue("Firstname", "Jane 3'-");
		assertEquals("select 2", 0, process(lHome.select(lKey)));
		
		lKey = new KeyObjectImpl();
		lKey.setValue("Firstname", "'DELETE tblTest -");
		assertEquals("select 3", 0, process(lHome.select(lKey)));
		
		assertEquals("select 4", 3, process(lHome.select()));
		assertEquals("count 4", 3, lHome.getCount());
	}

	private int process(QueryResult inResult) throws VException, SQLException {
		int outCount = 0;
		while (inResult.hasMoreElements()) {
			outCount++;
			inResult.next();
		}
		return outCount;
	}

}

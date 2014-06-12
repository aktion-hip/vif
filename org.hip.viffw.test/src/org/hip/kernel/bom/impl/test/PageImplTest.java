package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.Page;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.bom.impl.PageImpl;
import org.hip.kernel.exc.VException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class PageImplTest {	
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromSimple();
		System.out.println("Deleted all entries in tblTest.");
	}

	@Test
	public void testDo() throws SQLException, NamingException, VException {
		String[] lNames = {"1 eins", "2 zwei", "3 drei", "4 vier"};
	
		data.createTestEntry(lNames[3]);		
		data.createTestEntry(lNames[2]);		
		data.createTestEntry(lNames[1]);
		data.createTestEntry(lNames[0]);

		OrderObject lOrder = new OrderObjectImpl();
		lOrder.setValue(Test2DomainObjectHomeImpl.KEY_NAME, 1);
		Page lPage = new PageImpl(data.getSimpleHome().select(lOrder), null, 3);

		Page lFirst = lPage;
		Page lSecond = lPage.getNextPage();
		assertTrue("Identity 1", lPage.getPreviousPage() == lPage);
		assertEquals("page number of first page", 1, lPage.getPageNumber());
		assertEquals("page number of second page", 2, lPage.getNextPage().getPageNumber());
		assertEquals("page number of page following second", 2, lSecond.getNextPage().getPageNumber());
		assertTrue("Identity 1", lSecond.getPreviousPage() == lFirst);
		
		String lExpected1 = "< org.hip.kernel.bom.impl.PageImpl PageNumber=1 PageSize=3 />";
		String lExpected2 = "< org.hip.kernel.bom.impl.PageImpl PageNumber=2 PageSize=3 />";
		assertEquals("toString 1", lExpected1, lFirst.toString());
		assertEquals("toString 2", lExpected2, lSecond.toString());

		int i = 0;
		//proceed through first page
		while (lFirst.hasMoreElements()) {
			lPage.nextElement();
			i++;
		}
		assertEquals("number of elements of first", 3, i);
		i = 0;
		//proceed through second page
		while (lSecond.hasMoreElements()) {
			lSecond.nextElement();
			i++;
		}
		assertEquals("number of elements of second", 1, i);

		//print out all pages
		while (!lPage.isLastPage()) {
			System.out.println(">>>>> Page " + lPage.getPageNumber() + ":");
			System.out.println(lPage.pageAsXML());
			lPage = lPage.getNextPage();
		}
		System.out.println(">>>>> last Page " + lPage.getPageNumber() + ":");
		System.out.println(lPage.pageAsXML());
	}
	
	@Test
	public void testSerialization() throws SQLException, NamingException, VException, IOException, ClassNotFoundException {
		String[] lNames = {"1 eins", "2 zwei", "3 drei", "4 vier"};
		
		data.createTestEntry(lNames[3]);		
		data.createTestEntry(lNames[2]);		
		data.createTestEntry(lNames[1]);
		data.createTestEntry(lNames[0]);

		OrderObject lOrder = new OrderObjectImpl();
		lOrder.setValue(Test2DomainObjectHomeImpl.KEY_NAME, 1);
		Page lPage = new PageImpl(data.getSimpleHome().select(lOrder), null, 3);
		
		
		ByteArrayOutputStream lBytesOut = new ByteArrayOutputStream();
		ObjectOutputStream lObjectOut = new ObjectOutputStream(lBytesOut);
		lObjectOut.writeObject(lPage);
		byte[] lSerialized = lBytesOut.toByteArray();
		lObjectOut.close();
		lBytesOut.close();
		lPage = null;
		
		ByteArrayInputStream lBytesIn = new ByteArrayInputStream(lSerialized);
		ObjectInputStream lObjectIn = new ObjectInputStream(lBytesIn);
		Page lRetrieved = (Page)lObjectIn.readObject();
		lObjectIn.close();
		lBytesIn.close();
		
		Page lSecond = lRetrieved.getNextPage();
		String lExpected1 = "< org.hip.kernel.bom.impl.PageImpl PageNumber=1 PageSize=3 />";
		String lExpected2 = "< org.hip.kernel.bom.impl.PageImpl PageNumber=2 PageSize=3 />";
		assertEquals("toString 1", lExpected1, lRetrieved.toString());
		assertEquals("toString 2", lExpected2, lSecond.toString());
	}
	
}

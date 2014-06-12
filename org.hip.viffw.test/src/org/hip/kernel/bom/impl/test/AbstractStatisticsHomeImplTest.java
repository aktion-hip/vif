package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import org.hip.kernel.sys.VSys;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class AbstractStatisticsHomeImplTest {
	private static String expected =
		"SELECT S \"SORTORDER\", P \"PERIOD\", FROM ( \n" +
		"SELECT TO_CHAR(tblQustion.MUTDATUM, 'yyyyMM') \"S\", TO_CHAR(tblQustion.MUTDATUM, 'MM yyyy') \"P\", tblQustion.SERIALNR, tblQustion.DOKTYPE,  FROM tblQustion, tblGroup WHERE tblQustion.QUALIFIER = tblGroup.QUALIFIER AND tblGroup.GROUPID = 111\n" +
		"UNION ALL \n" +
		"SELECT TO_CHAR(tblQustion.MUTDATUM, 'yyyyMM') \"S\", TO_CHAR(tblQustion.MUTDATUM, 'MM yyyy') \"P\", tblQustion.SERIALNR, tblQustion.DOKTYPE,  FROM tblQustion, tblGroup WHERE tblQustion.QUALIFIER = tblGroup.QUALIFIER AND tblGroup.GROUPID = 222\n" +
		"UNION ALL \n" +
		"SELECT TO_CHAR(tblQustion.MUTDATUM, 'yyyyMM') \"S\", TO_CHAR(tblQustion.MUTDATUM, 'MM yyyy') \"P\", tblQustion.SERIALNR, tblQustion.DOKTYPE,  FROM tblQustion, tblGroup WHERE tblQustion.QUALIFIER = tblGroup.QUALIFIER AND tblGroup.GROUPID = 123\n" +
		") GROUP BY S, P";
		
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testObjects() {
		TestStatisticsHome lHome = (TestStatisticsHome)VSys.homeManager.getHome("org.hip.kernel.bom.impl.test.TestStatisticsHome");
	
		int i = 0;
		for (Iterator lTestObjects = lHome.getTestObjects(); lTestObjects.hasNext(); ) {
			assertEquals("testObjects " + i, expected, (String)lTestObjects.next());
			i++;
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		TestStatisticsHome lHome = (TestStatisticsHome)VSys.homeManager.getHome("org.hip.kernel.bom.impl.test.TestStatisticsHome");
		
		ByteArrayOutputStream lBytesOut = new ByteArrayOutputStream();
		ObjectOutputStream lObjectOut = new ObjectOutputStream(lBytesOut);
		lObjectOut.writeObject(lHome);
		byte[] lSerialized = lBytesOut.toByteArray();
		lObjectOut.close();
		lBytesOut.close();
		lHome = null;
		
		ByteArrayInputStream lBytesIn = new ByteArrayInputStream(lSerialized);
		ObjectInputStream lObjectIn = new ObjectInputStream(lBytesIn);
		TestStatisticsHome lRetrieved = (TestStatisticsHome)lObjectIn.readObject();
		lObjectIn.close();
		lBytesIn.close();
		
		int i = 0;
		for (Iterator lTestObjects = lRetrieved.getTestObjects(); lTestObjects.hasNext(); ) {
			assertEquals("testObjects " + i, expected, (String)lTestObjects.next());
			i++;
		}
	}
	
}

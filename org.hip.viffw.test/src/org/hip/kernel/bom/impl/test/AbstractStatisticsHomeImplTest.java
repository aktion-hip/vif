package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import org.hip.kernel.sys.VSys;
import org.junit.Test;

/** @author: Benno Luthiger */
public class AbstractStatisticsHomeImplTest {
    private static String expected =
            "SELECT S \"SORTORDER\", P \"PERIOD\", FROM ( \n"
                    +
                    "SELECT TO_CHAR(tblQustion.MUTDATUM, 'yyyyMM') \"S\", TO_CHAR(tblQustion.MUTDATUM, 'MM yyyy') \"P\", tblQustion.SERIALNR, tblQustion.DOKTYPE,  FROM tblGroup, tblQustion WHERE tblQustion.QUALIFIER = tblGroup.QUALIFIER AND tblGroup.GROUPID = 111\n"
                    +
                    "UNION ALL \n"
                    +
                    "SELECT TO_CHAR(tblQustion.MUTDATUM, 'yyyyMM') \"S\", TO_CHAR(tblQustion.MUTDATUM, 'MM yyyy') \"P\", tblQustion.SERIALNR, tblQustion.DOKTYPE,  FROM tblGroup, tblQustion WHERE tblQustion.QUALIFIER = tblGroup.QUALIFIER AND tblGroup.GROUPID = 222\n"
                    +
                    "UNION ALL \n"
                    +
                    "SELECT TO_CHAR(tblQustion.MUTDATUM, 'yyyyMM') \"S\", TO_CHAR(tblQustion.MUTDATUM, 'MM yyyy') \"P\", tblQustion.SERIALNR, tblQustion.DOKTYPE,  FROM tblGroup, tblQustion WHERE tblQustion.QUALIFIER = tblGroup.QUALIFIER AND tblGroup.GROUPID = 123\n"
                    +
                    ") GROUP BY S, P";

    @SuppressWarnings("rawtypes")
    @Test
    public void testObjects() {
        final TestStatisticsHome lHome = (TestStatisticsHome) VSys.homeManager
                .getHome("org.hip.kernel.bom.impl.test.TestStatisticsHome");

        int i = 0;
        for (final Iterator lTestObjects = lHome.getTestObjects(); lTestObjects.hasNext();) {
            assertEquals("testObjects " + i, expected, lTestObjects.next());
            i++;
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        TestStatisticsHome lHome = (TestStatisticsHome) VSys.homeManager
                .getHome("org.hip.kernel.bom.impl.test.TestStatisticsHome");

        final ByteArrayOutputStream lBytesOut = new ByteArrayOutputStream();
        final ObjectOutputStream lObjectOut = new ObjectOutputStream(lBytesOut);
        lObjectOut.writeObject(lHome);
        final byte[] lSerialized = lBytesOut.toByteArray();
        lObjectOut.close();
        lBytesOut.close();
        lHome = null;

        final ByteArrayInputStream lBytesIn = new ByteArrayInputStream(lSerialized);
        final ObjectInputStream lObjectIn = new ObjectInputStream(lBytesIn);
        final TestStatisticsHome lRetrieved = (TestStatisticsHome) lObjectIn.readObject();
        lObjectIn.close();
        lBytesIn.close();

        int i = 0;
        for (final Iterator lTestObjects = lRetrieved.getTestObjects(); lTestObjects.hasNext();) {
            assertEquals("testObjects " + i, expected, lTestObjects.next());
            i++;
        }
    }

}

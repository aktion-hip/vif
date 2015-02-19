package org.hip.kernel.bom.directory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import javax.naming.NamingException;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.directory.DirContextWrapper;
import org.hip.kernel.bom.directory.LDAPObjectHome;
import org.hip.kernel.bom.directory.LDAPQueryResult;
import org.hip.kernel.bom.directory.LDAPQueryStatement;
import org.hip.kernel.exc.VException;
import org.junit.Test;

/** @author Luthiger Created: 06.07.2007 */
public class LDAPQueryResultTest {
    private final static String NL = System.getProperty("line.separator");
    private final static String[] EXPECTED = new String[] {
            "Output: cn=first, uid=111, name=First, sn=First's sn, mail=first@my.org",
            "Output: cn=second, uid=2222, name=Second, sn=Second's sn, mail=second@my.org",
            "Output: cn=third, uid=333333, name=Third, sn=Third's sn, mail=third@my.org",
            "Output: cn=forth, uid=4, name=Forth, sn=Forth's sn, mail=forth@my.org",
            "Output: cn=fith, uid=5, name=Fith, sn=Fith's sn, mail=fith@my.org",
            "Output: cn=sixth, uid=6, name=Sixth, sn=Sixth's sn, mail=sixth@my.org",
            "Output: cn=seventh, uid=7, name=Seventh, sn=Seventh's sn, mail=seventh@my.org",
            "Output: cn=eighth, uid=8, name=Eighth, sn=Eighth's sn, mail=eighth@my.org",
            "Output: cn=ninth, uid=9, name=Ninth, sn=Ninth's sn, mail=ninth@my.org",
            "Output: cn=tenth, uid=1010, name=Tenth, sn=Tenth's sn, mail=tenth@my.org" };

    @Test
    public void testDo() throws NamingException, VException, SQLException {
        final DirContextWrapper lContext = new TestDirContext();
        final LDAPQueryResult lResult = new LDAPQueryResult(new TestLDAPObjectHome(),
                lContext.search("(cn=test)", null), EXPECTED.length, null);

        final Collection<String> lRetrieved = new Vector<String>();
        while (lResult.hasMoreElements()) {
            final GeneralDomainObject lModel = lResult.next();
            lRetrieved.add(getContent(lModel));
        }

        assertEquals("Number of retrieved", EXPECTED.length, lRetrieved.size());
        for (int i = 0; i < EXPECTED.length; i++) {
            assertTrue("Element retrieved: " + i, lRetrieved.contains(EXPECTED[i]));
        }
    }

    private String getContent(final GeneralDomainObject inModel) throws VException {
        return String.format("Output: cn=%s, uid=%s, name=%s, sn=%s, mail=%s",
                inModel.get(TestLDAPObjectHome.KEY_ID),
                inModel.get(TestLDAPObjectHome.KEY_USER_ID),
                inModel.get(TestLDAPObjectHome.KEY_NAME),
                inModel.get(TestLDAPObjectHome.KEY_FIRST_NAME),
                inModel.get(TestLDAPObjectHome.KEY_MAIL));
    }

    @Test
    public void testDoAsXML() throws NamingException, VException, SQLException {
        final String lExpected =
                "<Member>" + NL +
                        "    <propertySet>" + NL +
                        "        <Name>First</Name>" + NL +
                        "        <ID>first</ID>" + NL +
                        "        <UserID>111</UserID>" + NL +
                        "        <Firstname>First's sn</Firstname>" + NL +
                        "        <Mail>first@my.org</Mail>" + NL +
                        "    </propertySet>" + NL +
                        "</Member>";

        final DirContextWrapper lContext = new TestDirContext();
        final LDAPQueryResult lResult = new LDAPQueryResult(new TestLDAPObjectHome(),
                lContext.search("(cn=test)", null), EXPECTED.length, null);

        final StringBuffer lTest = new StringBuffer();
        while (lResult.hasMoreElements()) {
            lTest.append(lResult.nextAsXMLString());
        }

        assertTrue("Expected String is included", lTest.indexOf(lExpected) >= 0);
    }

    @Test
    public void testSerialization() throws NamingException, SQLException, IOException, ClassNotFoundException,
            VException {
        final DirContextWrapper lContext = new TestDirContext();

        final String lFilter = "(cn=test)";
        final LDAPObjectHome lHome = new TestLDAPObjectHome();
        final LDAPQueryStatement lStatement = new TestLDAPQueryStatement(lHome);
        lStatement.setSQLString(lFilter);

        // 1. test: setting cursor two steps ahead
        LDAPQueryResult lResult = new LDAPQueryResult(lHome, lContext.search(lFilter, null), EXPECTED.length,
                lStatement);
        lResult.next();
        lResult.next();

        LDAPQueryResult lRetrieved = serDes(lResult);
        lResult = null;

        Collection<String> lContents = new Vector<String>();
        while (lRetrieved.hasMoreElements()) {
            lContents.add(getContent(lRetrieved.next()));
        }

        assertEquals("size 1", 8, lContents.size());
        for (int i = 2; i < EXPECTED.length; i++) {
            assertTrue("contains " + i, lContents.contains(EXPECTED[i]));
        }
        assertFalse("not contained: 0", lContents.contains(EXPECTED[0]));
        assertFalse("not contained: 1", lContents.contains(EXPECTED[1]));

        // 2. test: setting cursor one steps ahead
        lResult = new LDAPQueryResult(lHome, lContext.search(lFilter, null), EXPECTED.length, lStatement);
        lResult.next();

        lRetrieved = serDes(lResult);
        lResult = null;

        lContents = new Vector<String>();
        while (lRetrieved.hasMoreElements()) {
            lContents.add(getContent(lRetrieved.next()));
        }

        assertEquals("size 1", 9, lContents.size());
    }

    private LDAPQueryResult serDes(LDAPQueryResult inResult) throws IOException, ClassNotFoundException {
        final ByteArrayOutputStream lBytesOut = new ByteArrayOutputStream();
        final ObjectOutputStream lObjectOut = new ObjectOutputStream(lBytesOut);
        lObjectOut.writeObject(inResult);
        final byte[] lSerialized = lBytesOut.toByteArray();
        lObjectOut.close();
        lBytesOut.close();
        inResult = null;

        final ByteArrayInputStream lBytesIn = new ByteArrayInputStream(lSerialized);
        final ObjectInputStream lObjectIn = new ObjectInputStream(lBytesIn);
        final LDAPQueryResult outRetrieved = (LDAPQueryResult) lObjectIn.readObject();
        lObjectIn.close();
        lBytesIn.close();

        return outRetrieved;
    }

}

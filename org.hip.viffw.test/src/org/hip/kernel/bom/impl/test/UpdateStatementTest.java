package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Vector;

import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.UpdateStatement;
import org.hip.kernel.exc.VException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author: Benno Luthiger */
public class UpdateStatementTest {
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
    public void testDo() throws SQLException, VException {
        final String lName = "Dummy";
        final String lFirstName = "Fi";
        final String lFirstName2 = "Adam";
        assertEquals("number before insert", 0, data.getSimpleHome().getCount());
        data.createTestEntry(lName, lFirstName);
        data.createTestEntry("Adam", "Pfiff");
        assertEquals("number after insert", 2, data.getSimpleHome().getCount());

        KeyObject lKey = new KeyObjectImpl();
        lKey.setValue("Name", lName);
        lKey.setValue("Firstname", lFirstName);
        DomainObject lFound = data.getSimpleHome().findByKey(lKey);
        final int lKeyValue = ((Number) lFound.get("TestID")).intValue();

        final String lSQL = "UPDATE TBLTEST SET SFIRSTNAME = '" + lFirstName2 + "' WHERE TESTID=" + lKeyValue;
        final Vector<String> lUpdates = new Vector<String>();
        lUpdates.addElement(lSQL);

        final UpdateStatement lStatement = new UpdateStatement();
        lStatement.setUpdates(lUpdates);
        lStatement.executeUpdate();
        lStatement.close();

        // test find updated
        try {
            lFound = data.getSimpleHome().findByKey(lKey);
            fail("Should not find object with Firstname=" + lFirstName);
        } catch (final BOMNotFoundException exc) {
        }

        lKey = new KeyObjectImpl();
        lKey.setValue("Name", lName);
        lKey.setValue("Firstname", lFirstName2);
        lFound = data.getSimpleHome().findByKey(lKey);
        assertNotNull("found updated", lFound);
    }
}

package org.hip.vif.web.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.impl.TextQuestionHome;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.web.bom.JoinQuestionToTextHome;
import org.hip.vif.web.bom.VifBOMHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Luthiger Created: 29.07.2010 */
public class JoinQuestionToTextHomeTest {
    private static DataHouseKeeper data;

    @BeforeClass
    public static void init() {
        data = DataHouseKeeper.getInstance();
    }

    @Before
    public void setUp() throws Exception {
        IndexHouseKeeper.redirectDocRoot(true);
    }

    @After
    public void tearDown() throws Exception {
        data.deleteAllInAll();
        IndexHouseKeeper.deleteTestIndexDir();
    }

    @Test
    public void testObjects() throws Exception {
        final JoinQuestionToTextHome lHome = new JoinQuestionToTextHomeSub();
        final Iterator<Object> lTest = lHome.getTestObjects();
        final String lExpected = "SELECT tblTextQuestion.QUESTIONID, tblTextVersion.TEXTID, tblTextVersion.NVERSION, tblTextVersion.STITLE, tblTextVersion.SAUTHOR, tblTextVersion.SCOAUTHORS, tblTextVersion.SSUBTITLE, tblTextVersion.SYEAR, tblTextVersion.SPUBLICATION, tblTextVersion.SPAGES, tblTextVersion.NVOLUME, tblTextVersion.NNUMBER, tblTextVersion.SPUBLISHER, tblTextVersion.SPLACE, tblTextVersion.NTYPE, tblTextVersion.NSTATE, tblTextVersion.SREFERENCE, tblTextVersion.NTYPE AS biblioType FROM tblTextQuestion INNER JOIN tblTextVersion ON tblTextQuestion.TEXTID = tblTextVersion.TEXTID WHERE tblTextQuestion.QUESTIONID = 8";
        assertEquals("test sql", lExpected, lTest.next());
    }

    @Test
    public void testSelect() throws Exception {
        // create 3 text entries
        final Long lTextID1 = data.createText("The first Book", "Riese, Adam");
        final Long lTextID2 = data.createText("Another Book", "Doe, Jane");
        final Long lTextID3 = data.createText("Book three", "Ford, Henry");
        // create link entries
        final Long lQuestionID1 = new Long(55);
        final Long lQuestionID2 = new Long(66);
        createTextQuestion(lTextID1, lQuestionID1);
        createTextQuestion(lTextID2, lQuestionID1);
        createTextQuestion(lTextID3, lQuestionID1);
        createTextQuestion(lTextID3, lQuestionID2);

        final JoinQuestionToTextHome lHome = VifBOMHelper.getJoinQuestionToTextHome();
        assertQueryResult(lHome.select(lQuestionID1, new Integer[] { WorkflowAwareContribution.S_PRIVATE }),
                new Long[] { lTextID1, lTextID2, lTextID3 }, "select unpublished");
        assertQueryResult(lHome.selectPublished(lQuestionID1), new Long[] {}, "select published None");

        // publish text entries
        final TextHome lTextHome = data.getTextHome();
        publish(lTextID1, lTextHome);
        publish(lTextID2, lTextHome);
        publish(lTextID3, lTextHome);

        assertQueryResult(lHome.selectPublished(lQuestionID1), new Long[] { lTextID1, lTextID2, lTextID3 },
                "select published A");
        assertQueryResult(lHome.selectPublished(lQuestionID2), new Long[] { lTextID3 }, "select published B");
    }

    private void assertQueryResult(final QueryResult inResult, final Long[] inExpectedIDs, final String inMessage)
            throws VException, SQLException {
        final List<Long> lExpected = Arrays.asList(inExpectedIDs);
        int i = 0;
        while (inResult.hasMoreElements()) {
            i++;
            final GeneralDomainObject lResult = inResult.next();
            assertTrue(inMessage + ": " + i, lExpected.contains(lResult.get(TextHome.KEY_ID)));
        }
        assertEquals(inMessage + ": size", inExpectedIDs.length, i);
    }

    private void publish(final Long inTextID, final TextHome inTextHome) throws VException, SQLException {
        final Text lText = inTextHome.getText(inTextID, 0);
        lText.set(TextHome.KEY_STATE, new Long(WorkflowAwareContribution.S_OPEN));
        lText.update(true);
    }

    private void createTextQuestion(final Long inTextID, final Long inQuestionID) throws VException, SQLException {
        final DomainObject lTextQuestion = BOMHelper.getTextQuestionHome().create();
        lTextQuestion.set(TextQuestionHome.KEY_TEXTID, inTextID);
        lTextQuestion.set(TextQuestionHome.KEY_QUESTIONID, inQuestionID);
        lTextQuestion.insert(true);
    }

    // ---

    @SuppressWarnings("serial")
    private class JoinQuestionToTextHomeSub extends JoinQuestionToTextHome {
        @Override
        protected Vector<Object> createTestObjects() {
            final Vector<Object> out = new Vector<Object>();
            final KeyObject lKey = new KeyObjectImpl();
            try {
                lKey.setValue(TextQuestionHome.KEY_QUESTIONID, new Long(8));
                out.add(createSelectString(lKey));
            } catch (final VException exc) {
                fail(exc.getMessage());
            }
            return out;
        }
    }

}

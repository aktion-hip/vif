package org.hip.vif.core.bom.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.search.AbstractSearching;
import org.hip.vif.core.search.NoHitsException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Benno Luthiger Created on 07.10.2005 */
public class VIFMemberIndexerTest {
    private IndexReader reader;
    private static DataHouseKeeper data;

    @BeforeClass
    public static void init() {
        data = DataHouseKeeper.getInstance();
    }

    @Before
    public void setUp() throws Exception {
        // create test index with two indexed member entries
        IndexHouseKeeper.redirectDocRoot(true);
        data.create2Members();
    }

    @After
    public void tearDown() throws Exception {
        data.deleteAllInAll();
        reader.close();
        IndexHouseKeeper.deleteTestIndexDir();
    }

    /*
     * Test method for 'org.hip.vif.search.VIFMemberIndexer.addMemberToIndex(KeyObject)'
     */
    @Test
    public void testAddMemberToIndex() throws Exception {
        final String lMail = "test@my.site.org";
        assertEquals("number of indexed 1", 2, IndexHouseKeeper.countIndexedMembers());

        final String lID = data.createMember("91", lMail);

        assertEquals("number of indexed 2", 3, IndexHouseKeeper.countIndexedMembers());

        reader = openReader(reader);
        Document[] lHits = searchMail(lMail);
        assertEquals("number of found 1", 1, lHits.length);
        assertEquals("id of found", lID, lHits[0].get(AbstractSearching.IndexField.MEMBER_ID.fieldName));

        final VIFMemberIndexer lIndexer = new VIFMemberIndexer();
        lIndexer.deleteMemberInIndex(lID);
        assertEquals("number of indexed 3", 2, IndexHouseKeeper.countIndexedMembers());

        reader = openReader(reader);
        try {
            lHits = searchMail(lMail);
            fail("shouldn't get here, because we don't expect a hit");
        } catch (final NoHitsException exc) {
            // intentionally left empty
        }
    }

    /*
     * Test method for 'org.hip.vif.search.VIFMemberIndexer.refreshMemberInIndex(String)'
     */
    @Test
    public void testRefreshMemberInIndex() throws Exception {
        final String lMail1 = "test@my.site.org";
        final String lMail2 = "changed@my.site.org";

        assertEquals("number of indexed 1", 2, IndexHouseKeeper.countIndexedMembers());

        final String lID = data.createMember("91", lMail1);
        assertEquals("number of indexed 2", 3, IndexHouseKeeper.countIndexedMembers());

        reader = openReader(reader);
        Document[] lHits = searchMail(lMail1);
        assertEquals("number of found 1", 1, lHits.length);
        assertEquals("id of found 1", lID, lHits[0].get(AbstractSearching.IndexField.MEMBER_ID.fieldName));

        final Member lMember = data.getMemberHome().getMember(lID);
        lMember.ucSave("Name", "FirstName", "Street", "Postal", "City", "Phone", "Fax", lMail2, "1", "en", new Long(15));

        assertEquals("number of indexed 3", 3, IndexHouseKeeper.countIndexedMembers());

        reader = openReader(reader);
        try {
            lHits = searchMail(lMail1);
            fail("shouldn't get here, because we don't expect a hit");
        } catch (final NoHitsException exc) {
            // intentionally left empty
        }

        reader = openReader(reader);
        lHits = searchMail(lMail2);
        assertEquals("number of found 3", 1, lHits.length);
        assertEquals("id of found 2", lID, lHits[0].get(AbstractSearching.IndexField.MEMBER_ID.fieldName));
    }

    private Document[] searchMail(final String inMail) throws CorruptIndexException, NoHitsException, IOException {
        return IndexHouseKeeper.search(new TermQuery(new Term(AbstractSearching.IndexField.MEMBER_MAIL.fieldName,
                inMail)), reader);
    }

    private IndexReader openReader(final IndexReader inReader) throws Exception {
        if (inReader != null) {
            inReader.close();
        }
        return DirectoryReader.open(IndexHouseKeeper.getMembersIndexDir());
    }

}

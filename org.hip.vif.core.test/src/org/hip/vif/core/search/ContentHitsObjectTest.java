package org.hip.vif.core.search;

import static org.junit.Assert.assertTrue;

import org.apache.lucene.document.Document;
import org.hip.kernel.bom.impl.XMLSerializer;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Luthiger Created: 22.02.2012 */
public class ContentHitsObjectTest {
    private Document document;

    @BeforeClass
    public static void init() {
        DataHouseKeeper.getInstance();
    }

    @Before
    public void setUp() throws Exception {
        IndexHouseKeeper.redirectDocRoot(true);

        document = new Document();
        document.add(AbstractSearching.IndexField.AUTHOR_NAME.createField("Jane Doe"));
        document.add(AbstractSearching.IndexField.DECIMAL_ID.createField("77:1.2.3"));
        document.add(AbstractSearching.IndexField.CONTRIBUTION_ID.createField("68"));
        document.add(AbstractSearching.IndexField.QUESTION_TEXT.createField("Hallo World"));
        document.add(AbstractSearching.IndexField.GROUP_ID.createField("8"));
        document.add(AbstractSearching.IndexField.GROUP_NAME.createField("Test of indexing"));
    }

    @After
    public void tearDown() throws Exception {
        IndexHouseKeeper.deleteTestIndexDir();
    }

    @Test
    public void testAccept() {
        final AbstractHitsDomainObject lDocObject = new ContentHitsObject(document);
        final XMLSerializer lSerializer = new XMLSerializer();
        lDocObject.accept(lSerializer);

        final String lActual = lSerializer.toString();
        assertTrue(lActual.contains("<ContentHitsObject>"));
        assertTrue(lActual.contains("<propertySet>"));
        assertTrue(lActual.contains("<contributionId>68</contributionId>"));
        assertTrue(lActual.contains("<groupId>8</groupId>"));
        assertTrue(lActual.contains("<author>Jane Doe</author>"));
        assertTrue(lActual.contains("<questionText>Hallo World</questionText>"));
        assertTrue(lActual.contains("<groupName>Test of indexing</groupName>"));
        assertTrue(lActual.contains("<groupName>Test of indexing</groupName>"));
        assertTrue(lActual.contains("<id>77:1.2.3</id>"));
        assertTrue(lActual.contains("</propertySet>"));
        assertTrue(lActual.contains("</ContentHitsObject>"));
    }

}

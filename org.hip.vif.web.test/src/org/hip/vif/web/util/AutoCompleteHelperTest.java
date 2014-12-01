package org.hip.vif.web.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vaadin.data.util.IndexedContainer;

/** @author Luthiger Created: 11.09.2011 */
public class AutoCompleteHelperTest {
    private static DataHouseKeeper data;

    @BeforeClass
    public static void init() {
        data = DataHouseKeeper.getInstance();
    }

    @Before
    public void setUp() throws Exception {
        IndexHouseKeeper.redirectDocRoot(true);

        data.createText("Title 1", "Author 1");
        data.createText("Title 2", "Author 2");
        data.createText("Title 3", "Author 1");
        data.createText("Title 4", "Author 1");
    }

    @After
    public void tearDown() throws Exception {
        data.deleteAllInAll();
        IndexHouseKeeper.deleteTestIndexDir();
    }

    @Test
    public final void testTitlesContainer() throws Exception {
        final AutoCompleteHelper lHelper = new AutoCompleteHelper(data.getTextHome(), new KeyObjectImpl());

        final IndexedContainer lTitlesContainer = lHelper.getTitlesContainer();
        final Collection<?> lTitles = lTitlesContainer.getItemIds();
        assertEquals(4, lTitles.size());
        assertTrue(lTitles.contains("Title 1"));
        assertTrue(lTitles.contains("Title 2"));
        assertTrue(lTitles.contains("Title 3"));
        assertTrue(lTitles.contains("Title 4"));

        final IndexedContainer lAuthorsContainer = lHelper.getAuthorsContainer();
        final Collection<?> lAuthors = lAuthorsContainer.getItemIds();
        assertEquals(2, lAuthors.size());
        assertTrue(lAuthors.contains("Author 1"));
        assertTrue(lAuthors.contains("Author 2"));
    }

}

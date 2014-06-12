package org.hip.vif.core.search;

import static org.junit.Assert.assertEquals;

import org.apache.lucene.document.Document;
import org.hip.kernel.bom.impl.XMLSerializer;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 22.02.2012
 */
public class ContentHitsObjectTest {
	private final static String NL = System.getProperty("line.separator");
	
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
		String lExpected = "" + NL +
			"<ContentHitsObject>" + NL +
			"    <propertySet>" + NL +
			"        <contributionId>68</contributionId>" + NL +
			"        <groupId>8</groupId>" + NL +
			"        <author>Jane Doe</author>" + NL +
			"        <questionText>Hallo World</questionText>" + NL +
			"        <groupName>Test of indexing</groupName>" + NL +
			"        <id>77:1.2.3</id>" + NL +
			"    </propertySet>" + NL +
			"</ContentHitsObject>";

		AbstractHitsDomainObject lDocObject = new ContentHitsObject(document);
		XMLSerializer lSerializer = new XMLSerializer();
		lDocObject.accept(lSerializer);
		assertEquals("serialization", lExpected, lSerializer.toString());
	}

}

package org.hip.vif.core.search;

import org.apache.lucene.document.Document;
import org.hip.kernel.bom.impl.XMLSerializer;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on 28.09.2005
 */
public class HitsDomainObjectTest {
	private final static String nl = System.getProperty("line.separator");
	
	private Document document;

	@Before
	public void setUp() throws Exception {
		DataHouseKeeper.getInstance();
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
		String lExpected = "" + nl +
			"<ContentHitsObject>" + nl +
			"    <propertySet>" + nl +
			"        <contributionId>68</contributionId>" + nl +
			"        <groupId>8</groupId>" + nl +
			"        <author>Jane Doe</author>" + nl +
			"        <questionText>Hallo World</questionText>" + nl +
			"        <groupName>Test of indexing</groupName>" + nl +
			"        <id>77:1.2.3</id>" + nl +
			"    </propertySet>" + nl +
			"</ContentHitsObject>";

		AbstractHitsDomainObject lDocObject = new ContentHitsObject(document);
		XMLSerializer lSerializer = new XMLSerializer();
		lDocObject.accept(lSerializer);
		Assert.assertEquals("serialization", lExpected, lSerializer.toString());
	}

}

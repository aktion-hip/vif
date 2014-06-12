package org.hip.vif.web.stale;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.web.stale.StaleRequestHelper.TextCollector;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 03.03.2012
 */
public class TextAuthorNotificationTest {
	private static final String NL = System.getProperty("line.separator");
	private static DataHouseKeeper data;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		data = DataHouseKeeper.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		IndexHouseKeeper.redirectDocRoot(true);
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromMember();
		data.deleteAllFromText();
		data.deleteAllFromLinkMemberRole();
		data.deleteAllFromTextAuthorReviewer();
		IndexHouseKeeper.deleteTestIndexDir();
	}

	@Test
	public final void testMailBody() throws Exception {
		String lMemberID = data.createMember();
		Member lMember = data.getMemberHome().getMember(lMemberID);
		
		Long lTextID = data.createText("The Title", "Doe, Jane");
		data.createTextProducer(lTextID, 0, new Long(lMemberID), true);
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(TextHome.KEY_ID, lTextID);
		QueryResult lText = BOMHelper.getJoinTextToMemberHome().select(lKey);
		
		StaleTextCollector lNotificator = new StaleTextCollector();
		TextCollector lCollector = new StaleRequestHelper.TextCollector(lText.next());
		lCollector.accept(lNotificator);
		
		TextAuthorNotificationSub lMail = new TextAuthorNotificationSub((VIFMember) lMember, lNotificator);
		
		String lExpected = "For your information: Your request for review of the following text contribution has been ignored:\n\n" +
			"Type: Article" + NL +
			"Title: The Title" + NL +
			"Subtitle: About the subtitle" + NL +
			"Author: Doe, Jane" + NL +
			"Year: 2010" + NL +
			"Pages: 44-55" + NL +
			"Volume: 12" + NL +
			"Number: 8" + NL +
			"\nPlease submit another request for review (forum application: discussion group (of the contributions) -> Pending contributions).";
		assertEquals(lExpected, lMail.getBody().toString());
		
		lExpected = "<p>For your information: Your request for review of the following text contribution has been ignored:</p><hr noshade width=\"200\" size=\"1\" align=\"left\" /><p><b>:</b></p><table border=\"0\" style=\"font-size:11pt;\"><tr><td><i>Type</i>:</td><td>Article</td></tr><tr><td><i>Title</i>:</td><td>The Title</td></tr><tr><td><i>Subtitle</i>:</td><td>About the subtitle</td></tr><tr><td><i>Author</i>:</td><td>Doe, Jane</td></tr><tr><td><i>Year</i>:</td><td>2010</td></tr><tr><td><i>Pages</i>:</td><td>44-55</td></tr><tr><td><i>Volume</i>:</td><td>12</td></tr><tr><td><i>Number</i>:</td><td>8</td></tr></table><hr noshade width=\"200\" size=\"1\" align=\"left\" /><p>Please submit another request for review (forum application: discussion group (of the contributions) -> Pending contributions).</p>";
		assertEquals(lExpected, lMail.getBodyHtml().toString());
	}

// ---
	
	private static class TextAuthorNotificationSub extends TextAuthorNotification {

		public TextAuthorNotificationSub(VIFMember inReceiver, StaleTextCollector inNotificator) throws VException, IOException {
			super(inReceiver, inNotificator);
		}
		
		@Override
		protected StringBuilder getBody() {
			return super.getBody();
		}
		@Override
		protected StringBuilder getBodyHtml() {
			return super.getBodyHtml();
		}
	}

}

package org.hip.vif.web.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Locale;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.TestAppContext;
import org.hip.vif.core.TestApplication;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.core.service.ApplicationData;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 22.02.2012
 */
public class RequestForReviewMailTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
		TestApplication lApp = new TestApplication();
		lApp.start(null, null, new TestAppContext());
		ApplicationData.create(lApp);
		ApplicationData.initLocale(Locale.ENGLISH);
	}
	
	@Before
	public void setUp() throws Exception {
		IndexHouseKeeper.redirectDocRoot(false);
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromMember();
		IndexHouseKeeper.deleteTestIndexDir();
	}

	@Test
	public void testBody() throws Exception {
		String[] lMemberIDs = data.create2Members();
		
		String lBodyPlain = "List of contributions";
		String lBodyHtml = "<p>List of contributions</p>";
		
		//first, test without request URL
		RequestForReviewMailSub lMail = new RequestForReviewMailSub(getMember(lMemberIDs[0]), getMember(lMemberIDs[1]), new StringBuilder(lBodyPlain), new StringBuilder(lBodyHtml));
		String lExpected = "You are kindly requested to review the following contributions:\n\nList of contributions\n\n" +
				"You can accept (or refuse) the review task in the forum application: discussion group (of the contributions) -> Process requests\n" +
				"For that the discussion can go on, it is important to inform about your decision as fast as possible.";
		assertEquals(lExpected, lMail.getBody().toString());
		lExpected = "<p>You are kindly requested to review the following contributions:</p><hr noshade width=\"200\" size=\"1\" align=\"left\" /><p>List of contributions</p><hr noshade width=\"200\" size=\"1\" align=\"left\" /><p>You can accept (or refuse) the review task in the forum application: discussion group (of the contributions) -> Process requests<br/>For that the discussion can go on, it is important to inform about your decision as fast as possible.</p>";
		assertEquals(lExpected, lMail.getBodyHtml().toString());	
		
		//after, set request URL
//		ApplicationData.setRequestURL("http://my.site.org/vifapp/forum");
//		lMail = new RequestForReviewMailSub(getMember(lMemberIDs[0]), getMember(lMemberIDs[1]), new StringBuilder(lBodyPlain), new StringBuilder(lBodyHtml));
//		lExpected = "You are kindly requested to review the following contributions:\n\nList of contributions\n\n" +
//				"You can accept (or refuse) the review task in the forum application: discussion group (of the contributions) -> Process requests\n" +
//				"For that the discussion can go on, it is important to inform about your decision as fast as possible.";
//		assertEquals(lExpected, lMail.getBody().toString());
//		lExpected = "<p>You are kindly requested to review the following contributions:</p><hr noshade width=\"200\" size=\"1\" align=\"left\" /><p>List of contributions</p><hr noshade width=\"200\" size=\"1\" align=\"left\" /><p>You can accept (or refuse) the review task in the forum application: discussion group (of the contributions) -> Process requests<br/>For that the discussion can go on, it is important to inform about your decision as fast as possible.</p>";
//		assertEquals(lExpected, lMail.getBodyHtml().toString());
	}
	
	@Test
	public void testBodyEN() throws Exception {
		String[] lMemberIDs = data.create2Members();
		
		String lBodyPlain = "List of contributions";
		String lBodyHtml = "<p>List of contributions</p>";
		
		//first, test without request URL
		RequestForReviewMailSub lMail = new RequestForReviewMailSub(getMember(lMemberIDs[0]), getMember(lMemberIDs[1]), new StringBuilder(lBodyPlain), new StringBuilder(lBodyHtml));
		String lExpected = "You are kindly requested to review the following contributions:\n\nList of contributions\n\n" +
			"You can accept (or refuse) the review task in the forum application: discussion group (of the contributions) -> Process requests\n" +
			"For that the discussion can go on, it is important to inform about your decision as fast as possible.";
		assertEquals(lExpected, lMail.getBody().toString());
		lExpected = "<p>You are kindly requested to review the following contributions:</p><hr noshade width=\"200\" size=\"1\" align=\"left\" /><p>List of contributions</p><hr noshade width=\"200\" size=\"1\" align=\"left\" /><p>You can accept (or refuse) the review task in the forum application: discussion group (of the contributions) -> Process requests<br/>For that the discussion can go on, it is important to inform about your decision as fast as possible.</p>";
		assertEquals(lExpected, lMail.getBodyHtml().toString());	
		
		//after, set request URL
//		ApplicationData.setRequestURL("http://my.site.org/vifapp/forum");
//		lMail = new RequestForReviewMailSub(getMember(lMemberIDs[0]), getMember(lMemberIDs[1]), new StringBuilder(lBodyPlain), new StringBuilder(lBodyHtml));
//		lExpected = "You are kindly requested to review the following contributions:\n\nList of contributions\n\n" +
//		"You can accept (or refuse) the review task in the forum application: discussion group (of the contributions) -> Process requests\n" +
//		"For that the discussion can go on, it is important to inform about your decision as fast as possible.";
//		assertEquals(lExpected, lMail.getBody().toString());
//		lExpected = "<p>You are kindly requested to review the following contributions:</p><hr noshade width=\"200\" size=\"1\" align=\"left\" /><p>List of contributions</p><hr noshade width=\"200\" size=\"1\" align=\"left\" /><p>You can accept (or refuse) the review task in the forum application: discussion group (of the contributions) -> Process requests<br/>For that the discussion can go on, it is important to inform about your decision as fast as possible.</p>";
//		assertEquals(lExpected, lMail.getBodyHtml().toString());
	}
	
	//if a local mail smpt server is operating, the following test method can be used to test the fully formatted mail
	@Test
	@Ignore
	public void testSend() throws Exception {
		String[] lMemberIDs = data.create2Members();		
		
		String lBodyPlain = "List of contributions";
		String lBodyHtml = "<p>List of contributions</p>";
		RequestForReviewMail lMail = new RequestForReviewMail(getMember(lMemberIDs[0]), getMember(lMemberIDs[1]), new StringBuilder(lBodyPlain), new StringBuilder(lBodyHtml));
		lMail.send();
	}
	
	private VIFMember getMember(String inMemberID) throws Exception {
		VIFMember out = (VIFMember) data.getMemberHome().getMember(inMemberID);
		out.set(MemberHome.KEY_MAIL, "test1@localhost");
		return out;
	}

// ---
	
	private class RequestForReviewMailSub extends RequestForReviewMail {
		public RequestForReviewMailSub(VIFMember inReviewer, VIFMember inAuthor, StringBuilder inNotificationText,
				StringBuilder inNotificationTextHtml) throws VException, IOException {
			super(inReviewer, inAuthor, inNotificationText, inNotificationTextHtml);
		}
		@Override
		public StringBuilder getBody() {
			return super.getBody();
		}
		@Override
		public StringBuilder getBodyHtml() {
			return super.getBodyHtml();
		}
	}


}

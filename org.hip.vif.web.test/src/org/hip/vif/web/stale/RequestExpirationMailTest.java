package org.hip.vif.web.stale;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.web.stale.StaleRequestHelper.AuthorGroup;
import org.hip.vif.web.stale.StaleRequestHelper.Collector;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 22.02.2012
 */
public class RequestExpirationMailTest {
	
	@BeforeClass
	public static void init() {
		DataHouseKeeper.getInstance();
	}

	@Test
	public void testGetBody() throws Exception {		
		RequestExpirationMailSub lMail = new RequestExpirationMailSub(createMember(), createContributionCollection(true));
		String lExpected = "For your information: The request to review the following contributions has expired:\n\nQuestion\nQuestion to test the mail.\n\n" +
		"Completion\nCompletion to test the mail.\n\nText\nFoo 2010, Howto operate VIF.\n\n";
		assertEquals(lExpected, lMail.getBody().toString());
		
		lMail = new RequestExpirationMailSub(createMember(), createContributionCollection(false));
		lExpected = "For your information: The request to review the following contribution has expired:\n\nQuestion\nQuestion to test the mail.\n\n";
		assertEquals(lExpected, lMail.getBody().toString());
	}
	
	@Test
	public void testGetBodyHtml() throws Exception {		
		RequestExpirationMailSub lMail = new RequestExpirationMailSub(createMember(), createContributionCollection(true));
		String lExpected = "<p>For your information: The request to review the following contributions has expired:</p><p><b>Question</b></p><p>Question to test the mail.</p><p><b>Completion</b></p><p>Completion to test the mail.</p><p><b>Text</b></p><p>Foo 2010, Howto operate VIF.</p>";
		assertEquals(lExpected, lMail.getBodyHtml().toString());
		
		lMail = new RequestExpirationMailSub(createMember(), createContributionCollection(false));
		lExpected = "<p>For your information: The request to review the following contribution has expired:</p><p><b>Question</b></p><p>Question to test the mail.</p>";
		assertEquals(lExpected, lMail.getBodyHtml().toString());
	}
	
	private VIFMember createMember() throws Exception {
		VIFMember out = (VIFMember) BOMHelper.getMemberHome().create();
		out.set(MemberHome.KEY_MAIL, "test1@localhost");
		out.set(MemberHome.KEY_SEX, new Integer(0));
		out.set(MemberHome.KEY_FIRSTNAME, "Jane");
		out.set(MemberHome.KEY_NAME, "Doe");
		return out;
	}

	private Collection<Collector> createContributionCollection(boolean inMultiple) throws VException {
		Collection<Collector> out = new Vector<Collector>();
		out.add(new TestCollector("Question", "Question to test the mail."));
		if (inMultiple) {
			out.add(new TestCollector("Completion", "Completion to test the mail."));
			out.add(new TestCollector("Text", "Foo 2010, Howto operate VIF."));
		}
		return out;
	}
	
	//if a local mail smpt server is operating, the following test method can be used to test the fully formatted mail
	@Test
	@Ignore
	public void testSend() throws Exception {
		RequestExpirationMailSub lMail = new RequestExpirationMailSub(createMember(), createContributionCollection(true));
		lMail.send();
	}

	// ---
		
		private class TestCollector implements Collector {
			private String type;
			private String title;

			TestCollector(String inType, String inTitle) {
				type = inType;
				title = inTitle;
			}
			public Long getReviewerID() {
				return null;
			}
			public String getReviewerFirstname() {
				return null;
			}
			public String getReviewerName() {
				return null;
			}	
			public String getReviewerMail() {
				return null;
			}
			public void removeReviewer() throws VException, SQLException {}
			public String getContributionType() {
				return type;
			}
			public String getContributionTitle() {
				return title;
			}
			public AuthorGroup getAuthorGroup() throws Exception {
				return null;
			}
			public void setReviewer(Long inReviewerID) throws VException, SQLException {}
			public boolean checkRefused(Long inReviewerID) throws VException, SQLException {
				return false;
			}
			@Override
			public void accept(StaleTextCollector inNotificator) {}
		}
		
		private class RequestExpirationMailSub extends RequestExpirationMail {

			public RequestExpirationMailSub(VIFMember inReceiver, Collection<Collector> inEntries) throws VException, IOException {
				super(inReceiver, inEntries);
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

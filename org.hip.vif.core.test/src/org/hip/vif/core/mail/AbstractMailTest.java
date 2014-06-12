package org.hip.vif.core.mail;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Locale;

import org.hip.kernel.exc.VException;
import org.hip.kernel.mail.MailGenerationException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.TestAppContext;
import org.hip.vif.core.TestApplication;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.core.service.ApplicationData;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 18.05.2008
 */
public class AbstractMailTest {
	private static DataHouseKeeper data;
	private MailSub mail;

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
		String inID = data.createMember();
		mail = new MailSub((VIFMember) data.getMemberHome().getMember(inID));
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromMember();
		data.deleteAllFromLinkMemberRole();
		IndexHouseKeeper.deleteTestIndexDir();
	}

	@Test
	public void testGetSubjectText() {
		String lExpected = "Message from the forum administration";
		assertEquals("mail subject", lExpected, mail.getSubjectText());
	}
	
	@Test
	public void testCreateMailAddress() throws Exception {
		assertEquals("Dear VornameT1 NameT1", mail.createMailAddress().toString());
	}

	
//	--- private classes ---
	private class MailSub extends AbstractMail {

		public MailSub(VIFMember inReceiver) throws VException, IOException {
				super(inReceiver);
			}
	
		@Override
		public StringBuilder getBody() {
			return null;
		}
		public StringBuilder getBodyHtml() {
			return null;
		}
		public String getSubjectText() {
			return super.getSubjectText();
		}
		@Override
		protected StringBuilder createMailAddress() throws MailGenerationException {
			return super.createMailAddress();
		}
	}

}

package org.hip.vif.web.mail;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.hip.kernel.exc.VException;
import org.hip.kernel.mail.MailGenerationException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.VIFMember;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Luthiger Created: 18.05.2008 */
public class AbstractMailTest {
    private static DataHouseKeeper data;
    private MailSub mail;

    @BeforeClass
    public static void init() {
        data = DataHouseKeeper.getInstance();
    }

    @Before
    public void setUp() throws Exception {
        IndexHouseKeeper.redirectDocRoot(false);
        final String inID = data.createMember();
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
        final String lExpected = "Message from the forum administration";
        assertEquals("mail subject", lExpected, mail.getSubjectText());
    }

    @Test
    public void testCreateMailAddress() throws Exception {
        assertEquals("Dear VornameT1 NameT1", mail.createMailAddress().toString());
    }

    // --- private classes ---
    private class MailSub extends AbstractMail {

        public MailSub(final VIFMember inReceiver) throws VException, IOException {
            super(inReceiver);
        }

        @Override
        public StringBuilder getBody() {
            return null;
        }

        @Override
        public StringBuilder getBodyHtml() {
            return null;
        }

        @Override
        public String getSubjectText() {
            return super.getSubjectText();
        }

        @Override
        protected StringBuilder createMailAddress() throws MailGenerationException {
            return super.createMailAddress();
        }
    }

}

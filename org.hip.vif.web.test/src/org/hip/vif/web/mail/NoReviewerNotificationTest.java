package org.hip.vif.web.mail;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Locale;

import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.adapters.AddressAdapter;
import org.hip.vif.web.TestMessages;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/** @author Luthiger Created: 20.10.2010 */
public class NoReviewerNotificationTest {
    private static final String GROUP_NAME = "Test Group";
    private static final String MAILS = "test1@localhost, test2@localhost";

    @BeforeClass
    public static void init() {
        DataHouseKeeper.getInstance();
    }

    @Before
    public void setUp() throws Exception {
        IndexHouseKeeper.redirectDocRoot(false);
    }

    @After
    public void tearDown() throws Exception {
        IndexHouseKeeper.deleteTestIndexDir();
    }

    @Test
    public void testGetBody() throws Exception {
        System.setProperty(TestMessages.PROP_LOCALE, Locale.GERMAN.getLanguage());
        NoReviewerNotificationSub lNotification = new NoReviewerNotificationSub(AddressAdapter.parse(MAILS), GROUP_NAME);
        String lExpected = "An die Administration der Gruppe 'Test Group'\n\nMomentan sind keine Teilnehmer als Reviewer verfügbar!\nBitte stellen Sie sicher, dass neu Beiträge geprüft werden können.\n\nMit freundlichen Grüssen\n\nThe VIF Administration";
        assertEquals(lExpected, lNotification.getBody().toString());

        System.setProperty(TestMessages.PROP_LOCALE, Locale.ENGLISH.getLanguage());
        lNotification = new NoReviewerNotificationSub(AddressAdapter.parse(MAILS), GROUP_NAME);
        lExpected = "To the administrators of group 'Test Group'\n\nAt the moment, there are not participants available as reviewers?\nPlease ensure that new contributions can be reviewed.\n\nRegards,\n\nThe VIF Administration";
        assertEquals(lExpected, lNotification.getBody().toString());
    }

    // if a local mail smpt server is operating, the following test method can be used to test the fully formatted mail
    @Test
    @Ignore
    public void testSend() throws Exception {
        final NoReviewerNotification lNotification = new NoReviewerNotification(AddressAdapter.parse(MAILS), GROUP_NAME);
        lNotification.send();
    }

    // ---

    private class NoReviewerNotificationSub extends NoReviewerNotification {
        public NoReviewerNotificationSub(final Collection<AddressAdapter> inReceiverMails, final String inGroupName) {
            super(inReceiverMails, inGroupName);
        }

        @Override
        public StringBuilder getBody() {
            return super.getBody();
        }
    }

}

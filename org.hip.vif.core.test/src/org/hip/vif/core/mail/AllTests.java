package org.hip.vif.core.mail;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AbstractMailTest.class, GroupStateChangeNotificationTest.class,
		MailUtilsTest.class, NoReviewerNotificationTest.class })
public class AllTests {

}

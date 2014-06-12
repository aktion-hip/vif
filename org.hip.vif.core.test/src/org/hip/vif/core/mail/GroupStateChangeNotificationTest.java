package org.hip.vif.core.mail;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.TestAppContext;
import org.hip.vif.core.TestApplication;
import org.hip.vif.core.service.ApplicationData;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 22.02.2012
 */
public class GroupStateChangeNotificationTest {
	
	@BeforeClass 
	public static void init() {
		DataHouseKeeper.getInstance();
		TestApplication lApp = new TestApplication();
		lApp.start(null, null, new TestAppContext());
		ApplicationData.create(lApp);
		ApplicationData.initLocale(Locale.ENGLISH);
	}

	@Test
	public void testGetSubject() throws Exception {
		String lExpected = "[VIF] State change of discussion group \"The Test Group\"";
		assertEquals("subject", lExpected, GroupStateChangeNotification.getSubject("The Test Group"));
	}

	@Test
	public void testGetBody() throws Exception {
		String lExpected = "To the participants of the discussion group \"The Test Group\"<p>The state of the group \"The Test Group\" has changed.<br/>Tell this!!!</p><p>Regards,<br/>The VIF Administration</p>";
		assertEquals("body", lExpected, GroupStateChangeNotification.getBody("The Test Group", "Tell this!!!"));
	}

}

package org.hip.vif.web.mail;

import static org.junit.Assert.assertEquals;

import org.hip.vif.core.DataHouseKeeper;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Luthiger Created: 22.02.2012 */
public class GroupStateChangeNotificationTest {

    @BeforeClass
    public static void init() {
        DataHouseKeeper.getInstance();
    }

    @Test
    public void testGetSubject() throws Exception {
        final String lExpected = "[VIF] State change of discussion group \"The Test Group\"";
        assertEquals("subject", lExpected, GroupStateChangeNotification.getSubject("The Test Group"));
    }

    @Test
    public void testGetBody() throws Exception {
        final String lExpected = "To the participants of the discussion group \"The Test Group\"<p>The state of the group \"The Test Group\" has changed.<br/>Tell this!!!</p><p>Regards,<br/>The VIF Administration</p>";
        assertEquals("body", lExpected, GroupStateChangeNotification.getBody("The Test Group", "Tell this!!!"));
    }

}

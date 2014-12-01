package org.hip.vif.web.util;

import static org.junit.Assert.assertEquals;

import org.hip.vif.web.tasks.BackTask;
import org.junit.Test;

/** Note: needs special Target Platform for testing in place.
 *
 * @author Luthiger Created: 26.12.2011 */
public class RequestHandlerTest {

    @Test
    public void testCreateRequestedURL() throws Exception {
        assertEquals("http://my.test.app/forum?request=org.hip.vif.web/org.hip.vif.web.tasks.BackTask&groupID=68",
                VIFRequestHandler.createRequestedURL(BackTask.class, true));
        assertEquals("http://my.test.app/forum?request=org.hip.vif.web/org.hip.vif.web.tasks.BackTask&myParameter=31",
                VIFRequestHandler.createRequestedURL(BackTask.class, true, "myParameter", 31l));
    }

}

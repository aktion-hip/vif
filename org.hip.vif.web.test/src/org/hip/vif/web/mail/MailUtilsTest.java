package org.hip.vif.web.mail;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.hip.kernel.sys.VSys;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Luthiger Created: 26.12.2011 */
public class MailUtilsTest {
    private static final String NL = System.getProperty("line.separator");

    @BeforeClass
    public static void init() {
        VSys.setContextPath(new File("").getAbsolutePath());
    }

    @Test
    public final void test() {
        final String lExpected = NL + NL + "Regards," + NL + NL + "The VIF Administration";
        assertEquals(lExpected, MailUtils.getMailGreetings().toString());
        assertEquals("<p>Regards,<br/><i>The VIF Administration</i></p>", MailUtils.getMailGreetingsHtml().toString());
    }

}

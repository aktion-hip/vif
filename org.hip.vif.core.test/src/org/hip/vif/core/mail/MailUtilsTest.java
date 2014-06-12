package org.hip.vif.core.mail;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Locale;

import org.hip.kernel.sys.VSys;
import org.hip.vif.core.TestAppContext;
import org.hip.vif.core.TestApplication;
import org.hip.vif.core.service.ApplicationData;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 26.12.2011
 */
public class MailUtilsTest {
	private static final String NL = System.getProperty("line.separator");
	
	@BeforeClass
	public static void init() {
		VSys.setContextPath(new File("").getAbsolutePath());
		
		TestApplication lApp = new TestApplication();
		lApp.start(null, null, new TestAppContext());
		ApplicationData.create(lApp);
		ApplicationData.initLocale(Locale.ENGLISH);
	}

	@Test
	public final void test() {
		String lExpected = NL + NL + "Regards," + NL + NL + "The VIF Administration";
		assertEquals(lExpected, MailUtils.getMailGreetings().toString());
		assertEquals("<p>Regards,<br/><i>The VIF Administration</i></p>", MailUtils.getMailGreetingsHtml().toString());
	}

}

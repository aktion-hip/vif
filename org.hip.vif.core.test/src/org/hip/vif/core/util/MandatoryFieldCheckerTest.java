package org.hip.vif.core.util;

import static junit.framework.Assert.assertEquals;

import java.util.Locale;

import org.hip.kernel.bom.DomainObject;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.TestAppContext;
import org.hip.vif.core.TestApplication;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.service.ApplicationData;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 23.02.2012
 */
public class MandatoryFieldCheckerTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() throws Exception {
		data = DataHouseKeeper.getInstance();
		
		TestApplication lApp = new TestApplication();
		lApp.start(null, null, new TestAppContext());
		ApplicationData.create(lApp);
		ApplicationData.initLocale(Locale.ENGLISH);
	}

	@Test
	public final void test() throws Exception {
		DomainObject lMember = data.getMemberHome().create();
		IMessages lMessages = new Messages();
		MandatoryFieldChecker lChecker = new MandatoryFieldChecker(lMessages);
		lChecker.checkMandatory(lMember, MemberHome.KEY_NAME, "member.editor.label.name");
		assertEquals("The field 'Name' is mandatory!", lChecker.render());
		
		lChecker = new MandatoryFieldChecker(lMessages);
		lChecker.checkMandatory(lMember, MemberHome.KEY_NAME, "member.editor.label.name");
		lChecker.checkMandatory(lMember, MemberHome.KEY_STREET, "member.editor.label.street");
		assertEquals("The fields 'Name', 'Street' are mandatory!", lChecker.render());
		
		lMember.set(MemberHome.KEY_NAME, "Foo");
		lChecker = new MandatoryFieldChecker(lMessages);
		lChecker.checkMandatory(lMember, MemberHome.KEY_NAME, "member.editor.label.name");
		lChecker.checkMandatory(lMember, MemberHome.KEY_STREET, "member.editor.label.street");
		assertEquals("The field 'Street' is mandatory!", lChecker.render());
	}
	
// ---
	
	private class Messages extends AbstractMessages {
		@Override
		protected ClassLoader getLoader() {
			return getClass().getClassLoader();
		}
		@Override
		protected String getBaseName() {
			return "testMessages";
		}
	}
	

}

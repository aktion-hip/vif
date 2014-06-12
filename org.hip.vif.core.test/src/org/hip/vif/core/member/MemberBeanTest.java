package org.hip.vif.core.member;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.TestAppContext;
import org.hip.vif.core.TestApplication;
import org.hip.vif.core.service.ApplicationData;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 22.02.2012
 */
public class MemberBeanTest {
	private static DataHouseKeeper data;

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
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromMember();
		data.deleteAllFromLinkMemberRole();
		IndexHouseKeeper.deleteTestIndexDir();
	}

	@Test
	public final void testCreateItem() throws Exception {
		String inID = data.createMember();
		MemberBean lBean = MemberBean.createItem(data.getMemberHome().getMember(inID));
		assertEquals("VornameT1 NameT1", lBean.getName());
		assertEquals("TestUsr-DHK1", lBean.getUserID());
		assertEquals("1.mail@test", lBean.getMail());
		assertEquals("NameT1, VornameT1 (TestUsr-DHK1)", lBean.toString());
	}

}

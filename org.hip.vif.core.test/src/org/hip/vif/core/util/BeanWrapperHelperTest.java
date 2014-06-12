package org.hip.vif.core.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.sql.Timestamp;
import java.util.Locale;

import org.hip.kernel.sys.VSys;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.TestAppContext;
import org.hip.vif.core.TestApplication;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.service.ApplicationData;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 23.02.2012
 */
public class BeanWrapperHelperTest {
	private static DataHouseKeeper data;
	private static final Long TIME = 1070000000000l;
	
	private String memberID;

	@BeforeClass
	public static void init() throws Exception {
		data = DataHouseKeeper.getInstance();
		VSys.setContextPath(new File("").getAbsolutePath());

		TestApplication lApp = new TestApplication();
		lApp.start(null, null, new TestAppContext());
		ApplicationData.create(lApp);
		ApplicationData.initLocale(Locale.ENGLISH);
	}


	@Before
	public void setUp() throws Exception {
		IndexHouseKeeper.redirectDocRoot(false);
		
		memberID = data.createMember("testUser", "test@vif.org");
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromMember();
		data.deleteAllFromLinkMemberRole();
		IndexHouseKeeper.deleteTestIndexDir();
	}

	@Test
	public final void testGetValues() throws Exception {
		Member lMember = data.getMemberHome().getMember(memberID);
		assertEquals("test@vif.org", BeanWrapperHelper.getString(MemberHome.KEY_MAIL, lMember));
		assertEquals("NameTtestUser", BeanWrapperHelper.getString(MemberHome.KEY_NAME, lMember));
		assertEquals(new Long(memberID), BeanWrapperHelper.getLong(MemberHome.KEY_ID, lMember));
		assertEquals(new Integer(1), BeanWrapperHelper.getInteger(MemberHome.KEY_SEX, lMember));
		
		//retrieving values with false key
		assertEquals("", BeanWrapperHelper.getString("no_key", lMember));
		assertEquals(new Long(0), BeanWrapperHelper.getLong("no_key", lMember));
		assertEquals(new Integer(0), BeanWrapperHelper.getInteger("no_key", lMember));
		
		//formatted date
		Timestamp lTime = new Timestamp(TIME);
		lMember.set(MemberHome.KEY_MUTATION, lTime);
		assertEquals("28.11.2003", BeanWrapperHelper.getFormattedDate(MemberHome.KEY_MUTATION, lMember));
	}

}

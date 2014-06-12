package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 */
public class MemberHomeImplTest {
//	private final static String[] MEMBER_PROPERTIES = {MemberHome.KEY_USER_ID, 
//		   MemberHome.KEY_NAME,
//		   MemberHome.KEY_FIRSTNAME,
//		   MemberHome.KEY_MAIL};
	
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		IndexHouseKeeper.redirectDocRoot(true);
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllInAll();
		IndexHouseKeeper.deleteTestIndexDir();
	}
	
	@Test
	public void testGetMember() throws Exception {
		String lNameExpected1 = "ThisName";
		String lNameExpected2 = "AnotherName";
		String lUserID = "testMe";

		//pre
		data.checkForEmptyTable();
		
		Long lMemberID1 = data.createMember2Roles(lNameExpected1);
		data.createMember1Role(lUserID, lNameExpected2);			
		MemberHome lMemberHome = data.getMemberHome();
		
		assertEquals("Number of created", 2, lMemberHome.getCount());
		
		Member lMember1 = lMemberHome.getMember(String.valueOf(lMemberID1));
		assertEquals("Name of retrieved 1", lNameExpected1, lMember1.get(MemberHome.KEY_NAME));
		
		Member lMember2 = lMemberHome.getMemberByUserID(lUserID);
		assertEquals("Name of retrieved 2", lNameExpected2, lMember2.get(MemberHome.KEY_NAME));
	}
	
//	public void testUpdateMemberCacheInsert() throws VException, SQLException {
//		String[] lExpected = new String[] {"expected_UserID", "expected_Name", "expected_FirstName", "expected_Mail"};
//		MemberHome lHome = data.getMemberHome();
//		
//		IMemberInformation lInformation = new ExtDBMemberInformation(getSource(lHome, lExpected));
//		assertEquals("count 0", 0, lHome.getCount());
//		
//		lHome.updateMemberCache(lInformation);
//		assertEquals("count 1", 1, lHome.getCount());
//		Member lRetrieved = lHome.getMemberByUserID(lExpected[0]);
//		
//		for (int i = 0; i < lExpected.length; i++) {
//			assertEquals("value " + i, lExpected[i], lRetrieved.get(MEMBER_PROPERTIES[i]));
//		}
//	}
//	
//	public void testUpdateMemberCacheUpdate() throws VException, SQLException {
//		String[] lExpected = new String[] {"", "expected_Name", "expected_FirstName", "expected_Mail"};
//		
//		MemberHome lHome = data.getMemberHome();
//		
//		assertEquals("count 0", 0, lHome.getCount());
//		
//		String lMemberID = data.createMember("ID_before", "Mail_before");
//		assertEquals("count 1", 1, lHome.getCount());
//		
//		Member lToUpdate = lHome.getMember(lMemberID);
//		String lUserID = (String) lToUpdate.get(MemberHome.KEY_USER_ID);
//		lExpected[0] = lUserID;
//		String lOldName = (String) lToUpdate.get(MemberHome.KEY_NAME);
//		
//		IMemberInformation lInformation = new ExtDBMemberInformation(getSource(lHome, lExpected));
//		assertEquals("count 2", 1, lHome.getCount());
//		lHome.updateMemberCache(lInformation);
//		assertEquals("count 3", 1, lHome.getCount());
//		
//		Member lRetrieved = lHome.getMember(lMemberID);
//		for (int i = 0; i < lExpected.length; i++) {
//			assertEquals("value " + i, lExpected[i], lRetrieved.get(MEMBER_PROPERTIES[i]));			
//		}
//		assertFalse("compare name", lOldName.equals(lRetrieved.get(MemberHome.KEY_NAME)));
//	}
	
//	private Member getSource(MemberHome inHome, String[] inValues) throws VException {
//		Member lSource = (Member)inHome.create();
//		setValues(lSource, inValues);
//		return lSource;
//	}
	
//	private void setValues(Member inMember, String[] inValues) throws VException {
//		for (int i = 0; i < MEMBER_PROPERTIES.length; i++) {
//			inMember.set(MEMBER_PROPERTIES[i], inValues[i]);
//		}
//	}

//	public void testForDocumentation() throws VException, SQLException {
//		DomainObjectHome lHome = data.getMemberHome();
//		KeyObject lKey = new KeyObjectImpl();
//		SQLRange lIn = new InObjectImpl(new Object[] {new Integer(18), new Integer(45), new Integer(65)});
//		lKey.setValue("Sex", lIn);
//		QueryResult lEntries = lHome.select(lKey);
		
//		KeyObject lKey = new KeyObjectImpl();
//		Date lDate1 = Date.valueOf("1989-08-20");
//		Date lDate2 = Date.valueOf("1999-04-24");		
//		SQLRange lBetween = new BetweenObjectImpl(lDate1, lDate2);
//		lKey.setValue("Mutation", lBetween);
//		QueryResult lEntries = lHome.select(lKey);
//	}

}

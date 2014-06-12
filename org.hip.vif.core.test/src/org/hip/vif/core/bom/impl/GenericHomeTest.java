package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.CompletionAuthorReviewerHome;
import org.hip.vif.core.bom.CompletionHistoryHome;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.MemberHistoryHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.PermissionHome;
import org.hip.vif.core.bom.QuestionAuthorReviewerHome;
import org.hip.vif.core.bom.QuestionHierarchyHome;
import org.hip.vif.core.bom.QuestionHistoryHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.bom.RoleHome;
import org.hip.vif.core.bom.SubscriptionHome;
import org.hip.vif.core.bom.TextAuthorReviewerHome;
import org.hip.vif.core.bom.TextHome;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Generic test for simple homes and domain objects.
 * Only test if the specified domain object can be created, inserted and deleted.
 * 
 * Created on 23.08.2002
 * @author Benno Luthiger
 */
public class GenericHomeTest {
	private static DataHouseKeeper data;

	/**
	 * Inner class for a single test.
	 * Holds information to create a home and 
	 * to initialize a domain object with test values.
	 */
	private static class SingleTest extends Object {
		String homeName = "";
		Map<String, Object> values = null;
		Collection<String> ignoreKey = new ArrayList<String>();

		private SingleTest(String inHomeName) {
			homeName = inHomeName;
			values = new HashMap<String, Object>();
		}
		
		public void setValue(String inKey, Object inValue) {
			values.put(inKey, inValue);
		}
		public void setValueNoKey(String inKey, Object inValue) {
			ignoreKey.add(inKey);
			setValue(inKey, inValue);
		}		
		
		public String getHome() {
			return homeName;
		}
		
		public Iterator<String> getKeys() {
			return values.keySet().iterator();
		}
		
		public Object getValue(String inKey) {
			return values.get(inKey);
		}

		public boolean isFitAsKey(String inName) {
			return !ignoreKey.contains(inName);
		}
	}

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllInAll();
	}
	
	@Test
	public void testDo() throws VException, SQLException {
		//Collection containing the information for the homes to test.
		Collection<SingleTest> lSimpleTests = new Vector<SingleTest>();
		SingleTest lSingleTest;
		
		//load test for org.hip.vif.bom.impl.MemberHistoryHomeImpl
		lSingleTest = new SingleTest(MemberHistoryImpl.HOME_CLASS_NAME);
		lSingleTest.setValue(MemberHome.KEY_ID, new BigDecimal(22));
		lSingleTest.setValue(MemberHome.KEY_MUTATION, new Timestamp(System.currentTimeMillis()));
		lSingleTest.setValue(MemberHistoryHome.KEY_VALID_TO, new Timestamp(System.currentTimeMillis()));
		lSingleTest.setValue(MemberHome.KEY_USER_ID, "TestHistory");
		lSingleTest.setValue(MemberHome.KEY_SEX, new Long(1));
		lSingleTest.setValue(MemberHome.KEY_NAME, "TestHistory");
		lSingleTest.setValueNoKey(MemberHome.KEY_SETTINGS, "TestSettings");
		lSingleTest.setValue(MemberHistoryHome.KEY_EDITOR_ID, new Long(21));
		lSimpleTests.add(lSingleTest);
		
		//load test for org.hip.vif.bom.impl.PermissionHomeImpl
		lSingleTest = new SingleTest(PermissionImpl.HOME_CLASS_NAME);
		//lSingleTest.setValue("ID", new BigDecimal(21));
		lSingleTest.setValue(PermissionHome.KEY_LABEL, "PermissionTest");
		lSingleTest.setValue(PermissionHome.KEY_DESCRIPTION, "Only for testing purpose");
		lSimpleTests.add(lSingleTest);
		
		//load test for org.hip.vif.bom.impl.RoleHomeImpl
		lSingleTest = new SingleTest(RoleImpl.HOME_CLASS_NAME);
		lSingleTest.setValue(RoleHome.KEY_CODE_ID, "RoleTest");
		lSingleTest.setValue(RoleHome.KEY_DESCRIPTION, "Only for testing purpose");
		lSingleTest.setValue(RoleHome.KEY_GROUP_SPECIFIC, new Long(0));
		lSimpleTests.add(lSingleTest);
		
		//load test for org.hip.vif.bom.impl.CompletionHomeImpl
		lSingleTest = new SingleTest(CompletionImpl.HOME_CLASS_NAME);
		lSingleTest.setValueNoKey(CompletionHome.KEY_COMPLETION, "This is a completely new completion.");
		lSingleTest.setValue(CompletionHome.KEY_STATE, new Long(1));
		lSingleTest.setValue(CompletionHome.KEY_QUESTION_ID, new Long(34));
		lSingleTest.setValue(CompletionHome.KEY_MUTATION, new Timestamp(System.currentTimeMillis()));
		lSimpleTests.add(lSingleTest);
		
		//load test for org.hip.vif.bom.impl.CompletionHistoryHomeImpl
		lSingleTest = new SingleTest(CompletionHistoryImpl.HOME_CLASS_NAME);
		lSingleTest.setValue(CompletionHome.KEY_ID, new BigDecimal(33));
		lSingleTest.setValueNoKey(CompletionHome.KEY_COMPLETION, "This completion is for archivation.");
		lSingleTest.setValue(CompletionHome.KEY_MUTATION, new Timestamp(System.currentTimeMillis()));
		lSingleTest.setValue(CompletionHistoryHome.KEY_VALID_TO, new Timestamp(System.currentTimeMillis()));
		lSingleTest.setValue(CompletionHome.KEY_STATE,new Long(2));
		lSingleTest.setValue(CompletionHome.KEY_QUESTION_ID, new Long(3));
		lSingleTest.setValue(CompletionHistoryHome.KEY_MEMBER_ID, new Long(4));
		lSimpleTests.add(lSingleTest);
		
		//load test for org.hip.vif.bom.impl.QuestionHomeImpl
		lSingleTest = new SingleTest(QuestionImpl.HOME_CLASS_NAME);
		lSingleTest.setValue(QuestionHome.KEY_QUESTION_DECIMAL, "2.13.5");
		lSingleTest.setValueNoKey(QuestionHome.KEY_QUESTION, "What's the purpose of the decimal ID?");
		lSingleTest.setValueNoKey(QuestionHome.KEY_REMARK, "This is a quote: \"Hallo World\"");
		lSingleTest.setValue(QuestionHome.KEY_STATE, new Long(1));
		lSingleTest.setValue(QuestionHome.KEY_MUTATION, new Timestamp(System.currentTimeMillis()));
		lSingleTest.setValue(QuestionHome.KEY_GROUP_ID, new Long(22));
		lSingleTest.setValue(QuestionHome.KEY_ROOT_QUESTION, new Long(0));
		lSimpleTests.add(lSingleTest);
		
		//load test for org.hip.vif.bom.impl.QuestionHistoryHomeImpl
		lSingleTest = new SingleTest(QuestionHistoryImpl.HOME_CLASS_NAME);
		lSingleTest.setValue(QuestionHome.KEY_ID, new BigDecimal(34));
		lSingleTest.setValue(QuestionHome.KEY_QUESTION_DECIMAL, "2.13.5");
		lSingleTest.setValueNoKey(QuestionHome.KEY_QUESTION, "Why do we have to archive?");
		lSingleTest.setValueNoKey(QuestionHome.KEY_REMARK, "To answer this question is of insignificant value.");
		lSingleTest.setValue(QuestionHome.KEY_STATE, new Long(1));
		lSingleTest.setValue(QuestionHome.KEY_GROUP_ID, new Long(23));
		lSingleTest.setValue(QuestionHome.KEY_ROOT_QUESTION, new Long(0));
		lSingleTest.setValue(QuestionHome.KEY_MUTATION, new Timestamp(System.currentTimeMillis()));
		lSingleTest.setValue(QuestionHistoryHome.KEY_VALID_TO, new Timestamp(System.currentTimeMillis()));
		lSingleTest.setValue(QuestionHistoryHome.KEY_MEMBER_ID, new Long(4));
		lSimpleTests.add(lSingleTest);
		
		//load test for org.hip.vif.bom.impl.QuestionAuthorReviewerHomeImpl
		lSingleTest = new SingleTest(QuestionAuthorReviewerImpl.HOME_CLASS_NAME);
		lSingleTest.setValue(ResponsibleHome.KEY_MEMBER_ID, new Long(1));
		lSingleTest.setValue(QuestionAuthorReviewerHome.KEY_QUESTION_ID, new Long(1));
		lSingleTest.setValue(ResponsibleHome.KEY_TYPE, new Long(1));
		lSimpleTests.add(lSingleTest);
		
		//load test for org.hip.vif.bom.impl.CompletionAuthorReviewerHomeImpl
		lSingleTest = new SingleTest(CompletionAuthorReviewerImpl.HOME_CLASS_NAME);
		lSingleTest.setValue(ResponsibleHome.KEY_MEMBER_ID, new Long(1));
		lSingleTest.setValue(CompletionAuthorReviewerHome.KEY_COMPLETION_ID, new Long(1));
		lSingleTest.setValue(ResponsibleHome.KEY_TYPE, new Long(1));
		lSimpleTests.add(lSingleTest);
		
		//load test for org.hip.vif.bom.impl.TextAuthorReviewerHomeImpl
		lSingleTest = new SingleTest(TextAuthorReviewerImpl.HOME_CLASS_NAME);
		lSingleTest.setValue(ResponsibleHome.KEY_MEMBER_ID, new Long(1));
		lSingleTest.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, new Long(1));
		lSingleTest.setValue(ResponsibleHome.KEY_TYPE, new Long(1));
		lSingleTest.setValue(TextAuthorReviewerHome.KEY_VERSION, new Long(0));
		lSimpleTests.add(lSingleTest);
		
		//load test for org.hip.vif.bom.impl.TextHomeImpl
		lSingleTest = new SingleTest(TextImpl.HOME_CLASS_NAME);
		lSingleTest.setValue(TextHome.KEY_TITLE, "The Title");
		lSingleTest.setValue(TextHome.KEY_AUTHOR, "Doe, Jane");
		lSingleTest.setValue(TextHome.KEY_COAUTHORS, "Foo, John");
		lSingleTest.setValue(TextHome.KEY_SUBTITLE, "The Sub");
		lSingleTest.setValue(TextHome.KEY_YEAR, "2010");
		lSingleTest.setValue(TextHome.KEY_PUBLICATION, "The Publication");
		lSingleTest.setValue(TextHome.KEY_PAGES, "22-33");
		lSingleTest.setValue(TextHome.KEY_VOLUME, "5");
		lSingleTest.setValue(TextHome.KEY_NUMBER, "99");
		lSingleTest.setValue(TextHome.KEY_PUBLISHER, "The Press");
		lSingleTest.setValueNoKey(TextHome.KEY_REMARK, "Remarks");
		lSimpleTests.add(lSingleTest);
		
		//load test for org.hip.vif.bom.impl.QuestionHierarchyHomeImpl
		lSingleTest = new SingleTest(QuestionHierarchyImpl.HOME_CLASS_NAME);
		lSingleTest.setValue(QuestionHierarchyHome.KEY_PARENT_ID, new Long(1));
		lSingleTest.setValue(QuestionHierarchyHome.KEY_CHILD_ID, new Long(1));
		lSingleTest.setValue(QuestionHierarchyHome.KEY_GROUP_ID, new Long(2));
		lSimpleTests.add(lSingleTest);

		//load test for org.hip.vif.bom.impl.SubscriptionHomeImpl
		lSingleTest = new SingleTest(SubscriptionImpl.HOME_CLASS_NAME);
		lSingleTest.setValue(SubscriptionHome.KEY_MEMBERID, new Long(56));
		lSingleTest.setValue(SubscriptionHome.KEY_QUESTIONID, new Long(78));
		lSingleTest.setValue(SubscriptionHome.KEY_LOCAL, new Long(1));
		lSimpleTests.add(lSingleTest);
		
		doSimpleTests(lSimpleTests);
	}
	
	private void doSimpleTests(Collection<SingleTest> inSimpleTests) throws VException, SQLException {
		int i = 0;
		for (SingleTest lSingleTest : inSimpleTests) {
			//create home
			DomainObjectHome lHome = (DomainObjectHome)VSys.homeManager.getHome(lSingleTest.getHome());
			//get count
			int lCount = lHome.getCount();

			//create key object
			KeyObject lKey = new KeyObjectImpl();
			
			//create object
			DomainObject lEntry = lHome.create();
			for (Iterator<String> lKeys = lSingleTest.getKeys(); lKeys.hasNext();) {
				String lName = (String)lKeys.next();
				lEntry.set(lName, lSingleTest.getValue(lName));
				if (lSingleTest.isFitAsKey(lName)) {					
					lKey.setValue(lName, lSingleTest.getValue(lName));
				}
			}

			//insert
			lEntry.insert(true);
			//assert count + 1
			assertEquals(lHome.getObjectClassName() + ": entry added", lCount + 1, lHome.getCount());
			//find
			lEntry = lHome.findByKey(lKey);
			//delete
			lEntry.delete(true);
			//assert count
			assertEquals(lHome.getObjectClassName() + ": entry deleted", lCount, lHome.getCount());
			
			System.out.println(String.valueOf(i) + ": Test of " + lHome.getObjectClassName() + " sucessful.");
			i++;
		}
		System.out.println("Ended " + i + " sucessful simple tests with domain objects.");
	}
}

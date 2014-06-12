package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Vector;

import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.QuestionHierarchyHome;
import org.hip.vif.core.bom.impl.QuestionHierarchyHomeImpl.ChildrenChecker;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 24.05.2011
 */
public class QuestionHierarchyHomeImplTest {
	private static final Long GROUP_ID = 8l;
	private static DataHouseKeeper data;
	
	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		createHierachy();
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromQuestionHierarchy();
		data.deleteAllFromQuestion();
	}

	@Test
	public final void testGetParent() throws Exception {
		QuestionHierarchyHome lHome = data.getQuestionHierarchyHome();
		assertEquals("1", lHome.getParent(3l).get(QuestionHierarchyHome.KEY_PARENT_ID).toString());
		assertEquals("2", lHome.getParent(5l).get(QuestionHierarchyHome.KEY_PARENT_ID).toString());
		assertEquals("2", lHome.getParent(6l).get(QuestionHierarchyHome.KEY_PARENT_ID).toString());
		assertEquals("4", lHome.getParent(11l).get(QuestionHierarchyHome.KEY_PARENT_ID).toString());
		
		try {
			lHome.getParent(1l);
			fail("shouldn't get here");
		}
		catch (BOMNotFoundException exc) {
			//left blank intentionally
		}
	}

	@Test
	public final void testGetChilds() throws Exception {
		QuestionHierarchyHome lHome = data.getQuestionHierarchyHome();
		Vector<String> lChildIDs = getChildIDs(lHome.getChilds(1l));
		assertTrue(lChildIDs.contains("2"));
		assertTrue(lChildIDs.contains("3"));
		assertTrue(lChildIDs.contains("4"));
		assertTrue(!lChildIDs.contains("5"));
		assertEquals("number of childs 1", 3, lChildIDs.size());

		lChildIDs = getChildIDs(lHome.getChilds(4l));
		assertTrue(lChildIDs.contains("9"));
		assertTrue(lChildIDs.contains("10"));
		assertTrue(lChildIDs.contains("11"));
		assertEquals("number of childs 2", 3, lChildIDs.size());
	}
	
	@Test
	public final void testGetSiblings() throws Exception {
		QuestionHierarchyHome lHome = data.getQuestionHierarchyHome();
		Vector<String> lSiblingIDs = getChildIDs(lHome.getSiblings(2l));
		assertTrue(lSiblingIDs.contains("2"));
		assertTrue(lSiblingIDs.contains("3"));
		assertTrue(lSiblingIDs.contains("4"));
		assertTrue(!lSiblingIDs.contains("5"));
		assertEquals("number of siblings 1", 3, lSiblingIDs.size());
		
		lSiblingIDs = getChildIDs(lHome.getSiblings(5l));
		assertTrue(lSiblingIDs.contains("5"));
		assertTrue(lSiblingIDs.contains("6"));
		assertEquals("number of siblings 2", 2, lSiblingIDs.size());

		try {
			lHome.getSiblings(1l);
			fail("shouldn't get here");
		}
		catch (BOMNotFoundException exc) {
			//left blank intentionally
		}
	}
	
	@Test
	public final void testCountChilds() throws Exception {
		QuestionHierarchyHome lHome = data.getQuestionHierarchyHome();
		assertEquals(3, lHome.countChilds(1l));
		assertEquals(2, lHome.countChilds(2l));
		assertEquals(2, lHome.countChilds(3l));
		assertEquals(3, lHome.countChilds(4l));
		assertEquals(0, lHome.countChilds(10l));
		assertEquals(0, lHome.countChilds(11l));

		assertEquals(0, lHome.countChilds(50l));
	}
	
	@Test
	public final void testHasParent() throws Exception {
		QuestionHierarchyHome lHome = data.getQuestionHierarchyHome();
		assertTrue(lHome.hasParent(9l));
		assertTrue(lHome.hasParent(7l));
		assertTrue(lHome.hasParent(4l));
		assertTrue(lHome.hasParent(3l));
		assertTrue(lHome.hasParent(2l));
		assertFalse(lHome.hasParent(1l));
	}
	
	@Test
	public final void testIsVisibleForGuestDepth() throws Exception {
		Long lQRootID = data.createQuestion("Root", "1:55", new Long(1), true);
		Long lQElseID = data.createQuestion("Not a root", "1:55.4", new Long(1), false);
		
		QuestionHierarchyHome lHome = data.getQuestionHierarchyHome();
		assertFalse("depth 0", lHome.isVisibleForGuestDepth(lQRootID, 0l));
		assertTrue("depth 1: root", lHome.isVisibleForGuestDepth(lQRootID, 1l));
		assertFalse("depth 1: not root", lHome.isVisibleForGuestDepth(lQElseID, 1l));
		
		assertTrue("depth 2: visible", lHome.isVisibleForGuestDepth(2l, 2l));
		assertFalse("depth 2: not visible", lHome.isVisibleForGuestDepth(5l, 2l));
		
		assertFalse("depth 2: not visible", lHome.isVisibleForGuestDepth(11l, 2l));
		assertTrue("depth 3: visible", lHome.isVisibleForGuestDepth(11l, 3l));

		assertTrue("checkin non existent QuestionID", lHome.isVisibleForGuestDepth(1000l, 3l));
	}
	
	@Test
	public final void testChildrenChecker() throws Exception {
		data.createQuestionHierachy(20l, 21l, 9l);
		data.createQuestionHierachy(20l, 22l, 9l);
		data.createQuestionHierachy(22l, 23l, 9l);
		
		QuestionHierarchyHome lHome = data.getQuestionHierarchyHome();
		ChildrenChecker lChecker = lHome.getChildrenChecker(GROUP_ID);
		assertTrue(lChecker.hasChildren(new Long("1")));
		assertTrue(lChecker.hasChildren(new Long("2")));
		assertTrue(lChecker.hasChildren(new Long("3")));
		assertTrue(lChecker.hasChildren(new Long("4")));
		
		assertFalse(lChecker.hasChildren(new Long("5")));
		assertFalse(lChecker.hasChildren(new Long("6")));
		assertFalse(lChecker.hasChildren(new Long("7")));
		assertFalse(lChecker.hasChildren(new Long("8")));
		assertFalse(lChecker.hasChildren(new Long("9")));
		assertFalse(lChecker.hasChildren(new Long("10")));
		assertFalse(lChecker.hasChildren(new Long("11")));
		
		assertEquals(new Long(1), lChecker.getParentID((long) 2));
		assertEquals(new Long(1), lChecker.getParentID((long) 3));
		assertEquals(new Long(1), lChecker.getParentID((long) 4));

		assertEquals(new Long(2), lChecker.getParentID((long) 5));
		assertEquals(new Long(2), lChecker.getParentID((long) 6));

		assertEquals(new Long(3), lChecker.getParentID((long) 7));
		assertEquals(new Long(3), lChecker.getParentID((long) 8));
		
		assertEquals(new Long(4), lChecker.getParentID((long) 9));
		assertEquals(new Long(4), lChecker.getParentID((long) 10));
		assertEquals(new Long(4), lChecker.getParentID((long) 11));
	}
	
	private void createHierachy() throws Exception {
		data.createQuestionHierachy(1l, 2l, GROUP_ID);
		data.createQuestionHierachy(1l, 3l, GROUP_ID);
		data.createQuestionHierachy(1l, 4l, GROUP_ID);

		data.createQuestionHierachy(2l, 5l, GROUP_ID);
		data.createQuestionHierachy(2l, 6l, GROUP_ID);

		data.createQuestionHierachy(3l, 7l, GROUP_ID);
		data.createQuestionHierachy(3l, 8l, GROUP_ID);

		data.createQuestionHierachy(4l, 9l, GROUP_ID);
		data.createQuestionHierachy(4l, 10l, GROUP_ID);
		data.createQuestionHierachy(4l, 11l, GROUP_ID);
	}
	
	private Vector<String> getChildIDs(QueryResult inChilds) throws SQLException, VException {
		Vector<String> outIDs = new Vector<String>();
		while (inChilds.hasMoreElements()) {
			outIDs.add(inChilds.next().get(QuestionHierarchyHome.KEY_CHILD_ID).toString());
		}
		return outIDs;
	}

}

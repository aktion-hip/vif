package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class DomainObjectImplTest {
	private static DataHouseKeeper data;
	private PrivateDomainObjectHome lPrivatHome = new PrivateDomainObjectHome();

	@SuppressWarnings("serial")
	public class PrivateDomainObjectHome extends Test2DomainObjectHomeImpl {
		public PrivateDomainObjectHome() {
			super();
		}
		public int getNumberOfReleased() {
			return releasedObjects().size();
		}
		public String getObjectClassName() {
			return "org.hip.kernel.bom.impl.test.DomainObjectImplTest$PrivateDomainObject";
		}
		public DomainObject create() throws BOMException {
			PrivateDomainObject outBOM = (PrivateDomainObject)newInstance();
			outBOM.initForNew();
			return outBOM;
		}
		public DomainObject newInstance() throws BOMException {
			PrivateDomainObject outBOM;
			if ( releasedObjects().elements().hasMoreElements() ) {
				 outBOM = (PrivateDomainObject)releasedObjects().elements().nextElement() ;
				 releasedObjects().removeElement(outBOM);
			} 
			else {
				outBOM = new PrivateDomainObject();
			}
			return outBOM;
		}		
	}
	
	@SuppressWarnings("serial")
	private class PrivateDomainObject extends Test2DomainObjectImpl {
		private final static String lSuperHome = "org.hip.kernel.bom.impl.test.Test2DomainObjectHomeImpl";
		public String getHomeClassName() {
			return lSuperHome;
		}
		protected void initForNew()  {
			initializeForNew();
		}
		public void release() {
			lPrivatHome.release(this);
		}
	}
	

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllFromSimple();
		System.out.println("Deleted all entries in tblTest.");
	}
	
	@Test
	public void testDelete() throws VException, SQLException {
		assertEquals("number before insert", 0, data.getSimpleHome().getCount());
		String lName = "Dummy";
		String lFirstname = "Fi";
		
		//create a new domain objects and insert it
		DomainObject lDomainObject = data.getSimpleHome().create();
		lDomainObject.set("Name", lName);
		lDomainObject.set("Firstname", lFirstname);
		lDomainObject.set("Sex", new Integer(1));
		lDomainObject.insert(true);
		assertEquals("number after insert", 1, data.getSimpleHome().getCount());

		//test find
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue("Name", lName);
		lKey.setValue("Firstname", lFirstname);

		//update
		DomainObject lFound = data.getSimpleHome().findByKey(lKey);
		assertNotNull(lFound);
		lFound.delete(true);

		//test find deleted
		try {
			lFound = data.getSimpleHome().findByKey(lKey);
			fail("Should not find deleted");
		}
		catch(org.hip.kernel.bom.BOMNotFoundException exc) {
			//intentionally left empty
		}
		assertEquals("number after delete", 0, data.getSimpleHome().getCount());
	}
	
	@Test
	public void testInsert() throws VException, SQLException {
		int lCount = data.getSimpleHome().getCount();
		assertEquals("number before insert", 0, lCount);

		String lName = "Dummy";
		String lFirstname = "Fi";
		
		//create two new domain objects and insert them
		DomainObject lDomainObject = data.getSimpleHome().create();
		lDomainObject.set("Name", lName);
		lDomainObject.set("Firstname", lFirstname);
		lDomainObject.set("Sex", new Integer(1));
		Long lObjectID = lDomainObject.insert(true);
		assertEquals("ID 1", lObjectID.toString(), lDomainObject.get(Test2DomainObjectHomeImpl.KEY_ID).toString());
		lDomainObject.release();

		lDomainObject = data.getSimpleHome().create();
		lDomainObject.set("Name", lName);
		lDomainObject.set("Firstname", lFirstname + lFirstname);
		lDomainObject.set("Sex", new Integer(1));
		lObjectID = lDomainObject.insert(true);
		assertEquals("ID 2", lObjectID.toString(), lDomainObject.get(Test2DomainObjectHomeImpl.KEY_ID).toString());
		lDomainObject.release();
		
		assertEquals("number after insert", 2, data.getSimpleHome().getCount());
		
		//test find
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue("Name", lName);
		lKey.setValue("Firstname", lFirstname);
		
		DomainObject lFound1 = data.getSimpleHome().findByKey(lKey);
		DomainObject lFound2 = data.getSimpleHome().findByKey(lKey);
		assertTrue("equal 1", lFound1.equals(lFound2));
		assertEquals("equal hash code 1", lFound1.hashCode(), lFound2.hashCode());
		
		//domain object should be equal even if their content differs
		//as long as their keys are equal
		lFound1.set("Firstname", lFirstname + lFirstname);
		assertTrue("equal 2", lFound1.equals(lFound2));
		assertEquals("equal hash code 2", lFound1.hashCode(), lFound2.hashCode());

		lDomainObject = data.getSimpleHome().create();
		assertTrue("not equal", !lFound1.equals(lDomainObject));
		assertTrue("not equal hash code", lFound1.hashCode() != lDomainObject.hashCode());
		
		lDomainObject.release();
		lFound1.release();
		lFound2.release();
		
		//test storing of primitive data types
		lDomainObject = data.getSimpleHome().create();
		lDomainObject.set(Test2DomainObjectHomeImpl.KEY_NAME, lName);
		lDomainObject.set(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, lFirstname);
		lDomainObject.set(Test2DomainObjectHomeImpl.KEY_SEX, 1);  //primitive data type
		lObjectID = lDomainObject.insert(true);
		
		lKey = new KeyObjectImpl();
		lKey.setValue(Test2DomainObjectHomeImpl.KEY_ID, lObjectID);
		lFound1 = data.getSimpleHome().findByKey(lKey);
		Object lValue = lFound1.get(Test2DomainObjectHomeImpl.KEY_SEX);
		assertEquals("primitives are retrieved as BigDecimal objects", new BigDecimal(1), lValue);
	}
	
	@Test
	public void testRelease() throws VException, SQLException {
		assertEquals("number before insert", 0, lPrivatHome.getCount());
		int lNumberOfReleased = lPrivatHome.getNumberOfReleased();

		String lName = "Dummy";
		String lFirstname = "Fi";
		
		//create two new domain objects and insert them
		DomainObject lDomainObject = lPrivatHome.create();
		lDomainObject.set("Name", lName);
		lDomainObject.set("Firstname", lFirstname);
		lDomainObject.set("Sex", new Integer(1));
		lDomainObject.insert(true);
		assertEquals("number after first insert", 1, lPrivatHome.getCount());

		//release first
		lDomainObject.release();
		assertEquals("number of realesed 1", lNumberOfReleased+1, lPrivatHome.getNumberOfReleased());

		//create second with released instance
		lDomainObject = lPrivatHome.create();
		lDomainObject.set("Name", lName);
		lDomainObject.set("Firstname", lFirstname + lFirstname);
		lDomainObject.set("Sex", new Integer(1));
		lDomainObject.insert(true);
		assertEquals("number after second insert", 2, lPrivatHome.getCount());
		
		//release first instance again
		lDomainObject.release();
		assertEquals("number of realesed 2", lNumberOfReleased+1, lPrivatHome.getNumberOfReleased());
		
		//test find
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue("Name", lName);
		lKey.setValue("Firstname", lFirstname);

		//get 3 instances	
		DomainObject lFound1 = lPrivatHome.findByKey(lKey);
		DomainObject lFound2 = lPrivatHome.findByKey(lKey);
		lDomainObject = lPrivatHome.create();

		//release all instances
		lDomainObject.release();
		lFound1.release();
		lFound2.release();
		assertEquals("number of realesed 3", lNumberOfReleased+3, lPrivatHome.getNumberOfReleased());
	}
	
	@Test
	public void testUpdate() throws VException, SQLException {
		String lName = "Dummy";
		String lFirstname = "Fi";
		
		//create a new domain objects and insert it
		DomainObject lDomainObject = data.getSimpleHome().create();
		lDomainObject.set("Name", lName);
		lDomainObject.set("Firstname", lFirstname);
		lDomainObject.set("Sex", new Integer(1));
		lDomainObject.insert(true);

		//test find
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue("Name", lName);
		lKey.setValue("Firstname", lFirstname);

		//update
		DomainObject lFound = data.getSimpleHome().findByKey(lKey);
		lFound.set("Firstname", lFirstname + lFirstname);
		lFound.update(true);

		//test find updated
		try {
			lFound = data.getSimpleHome().findByKey(lKey);
			fail("Should not find object with Firstname=" + lFirstname);
		}
		catch(BOMNotFoundException exc) {
			assertNotNull("findByKey", exc);
		}
		
		lKey = new KeyObjectImpl();
		lKey.setValue("Name", lName);
		lKey.setValue("Firstname", lFirstname+lFirstname);
		lFound = data.getSimpleHome().findByKey(lKey);
		assertNotNull("found updated", lFound);
	}

}

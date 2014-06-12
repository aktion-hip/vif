package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.PreparedInsertStatement;
import org.hip.kernel.bom.impl.PreparedUpdateStatement;
import org.hip.kernel.exc.VException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class PreparedStatementTest {
	private static DataHouseKeeper data;

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
	public void testPreparedInsert() throws VException, SQLException {
		String lName1 = "Dummy";
		String lName2 = "Pfiff";
		Timestamp lTime1 = new Timestamp(Date.valueOf("1989-10-21").getTime());
		String lFirstName1 = "Fi";
		String lFirstName2 = "Adam";
		Timestamp lTime2 = new Timestamp(Date.valueOf("2000-06-17").getTime());
		assertEquals("number before insert", 0, data.getSimpleHome().getCount());

		DomainObject lBOM1 = data.getSimpleHome().create();
		lBOM1.set("Name", lName1);
		lBOM1.set("Firstname", lFirstName1);
		lBOM1.set("Sex", new Integer(1));
		lBOM1.set("Mutation", lTime1);
		DomainObject lBOM2 = data.getSimpleHome().create();
		lBOM2.set("Name", lName2);
		lBOM2.set("Firstname", lFirstName2);
		lBOM2.set("Sex", new Integer(1));
		lBOM2.set("Amount", new BigDecimal(17.556));
		lBOM2.set("Mutation", lTime2);

		//insert	
		PreparedInsertStatement lStatement = new PreparedInsertStatement(data.getSimpleHome());
		lStatement.setValues(lBOM1);
		Collection<Long> lAutoKeys = lStatement.executeUpdate();
		lStatement.commit();
		assertEquals("number after first insert", 1, data.getSimpleHome().getCount());
		assertEquals("number of auto generated keys 1", 1, countAutoKeys(lAutoKeys));
		lStatement.setValues(lBOM2);
		lAutoKeys = lStatement.executeUpdate();
		lStatement.commit();
		assertEquals("number after second insert", 2, data.getSimpleHome().getCount());
		assertEquals("number of auto generated keys 2", 1, countAutoKeys(lAutoKeys));
		lStatement.close();

		//retrieve inserted	
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue("Name", lName1);
		lKey.setValue("Firstname", lFirstName1);
		DomainObject lFound = data.getSimpleHome().findByKey(lKey);
		assertNotNull(lFound);
		Timestamp lTimeRetrieved = (Timestamp)lFound.get("Mutation");
		assertEquals("time 1", lTime1.getTime(), lTimeRetrieved.getTime());
		
		lKey = new KeyObjectImpl();
		lKey.setValue("Name", lName2);
		lKey.setValue("Firstname", lFirstName2);
		lFound = data.getSimpleHome().findByKey(lKey);
		assertNotNull(lFound);
		lTimeRetrieved = (Timestamp)lFound.get("Mutation");
		assertEquals("time 2", lTime2.getTime(), lTimeRetrieved.getTime());
	}
	
	@Test
	public void testPreparedUpdateSingle() throws VException, SQLException {
		DomainObjectHome lHome = data.getSimpleHome();
		assertEquals("count 0", 0, lHome.getCount());
		
		String lWhereStreet = "Street-One";
		String lWhereCity = "This";
		
		//first create three entries
		PreparedInsertStatement lStatement = new PreparedInsertStatement(data.getSimpleHome());
		DomainObject lModel = lHome.create();
		insertEntry(lStatement, "1", "Main Street", lWhereCity, "8090", lModel);
		insertEntry(lStatement, "2", lWhereStreet, lWhereCity, "8090", lModel);
		insertEntry(lStatement, "3", "Dorfstrasse", lWhereCity, "8090", lModel);
		assertEquals("count 1", 3, lHome.getCount());
		
		KeyObject lFind = new KeyObjectImpl();
		lFind.setValue("Street", lWhereStreet);
		lFind.setValue("City", lWhereCity);
		
		lModel = lHome.findByKey(lFind);
		assertNotNull("Retrieve entry", lModel);
		
		String lNewName = "";
		String lNewStreet = "";
		String lNewCity = "";
		String lNewPLZ = "";
		
		KeyObject lTest = new KeyObjectImpl();
		lTest.setValue("Name", lNewName);
		lTest.setValue("Street", lNewStreet);
		lTest.setValue("City", lNewCity);
		lTest.setValue("PLZ", lNewPLZ);
		
		assertEquals("test before", 0, lHome.getCount(lTest));
		
		PreparedUpdateStatement lUpdate = new PreparedUpdateStatement(lHome);
		lModel.set("Name", lNewName);
		lModel.set("Street", lNewStreet);
		lModel.set("City", lNewCity);
		lModel.set("PLZ", lNewPLZ);
		
		lUpdate.setValues(lModel);
		int lChanged = lUpdate.executeUpdate();
		lUpdate.commit();
		
		assertEquals("number of updated", 1, lChanged);
		assertEquals("test after", 1, lHome.getCount(lTest));
	}
	
	@Test
	public void testPreparedUpdate() throws VException, SQLException {
		DomainObjectHome lHome = data.getSimpleHome();
		assertEquals("count 0", 0, lHome.getCount());
		
		String lWhereStreet = "Street-One";
		String lWhereCity = "This";
		
		//first create three entries
		PreparedInsertStatement lStatement = new PreparedInsertStatement(data.getSimpleHome());
		DomainObject lModel = lHome.create();
		insertEntry(lStatement, "1", lWhereStreet, lWhereCity, "8090", lModel);
		insertEntry(lStatement, "2", "Main-Street", lWhereCity, "8090", lModel);
		insertEntry(lStatement, "3", lWhereStreet, lWhereCity, "8090", lModel);
		assertEquals("count 1", 3, lHome.getCount());
		
		String lNewPlz = "9000";
		String lNewName = "Doe";
		KeyObject lChange = new KeyObjectImpl();
		lChange.setValue("PLZ", lNewPlz);
		lChange.setValue("Name", lNewName);
		
		KeyObject lWhere = new KeyObjectImpl();
		lWhere.setValue("Street", lWhereStreet);
		lWhere.setValue("City", lWhereCity);

		assertEquals("test before", 0, lHome.getCount(lChange));
		
		PreparedUpdateStatement lUpdate = new PreparedUpdateStatement(lHome, lChange, lWhere);
		int lChanged = lUpdate.executeUpdate();
		lUpdate.commit();
		
		assertEquals("number of updated", 2, lChanged);
		lChange = new KeyObjectImpl();
		lChange.setValue("PLZ", lNewPlz);
		lChange.setValue("Name", lNewName);		
		assertEquals("test after", 2, lHome.getCount(lChange));
	}
	
	private void insertEntry(PreparedInsertStatement inStatement, String inName, String inStreet, String inCity, String inPLZ, DomainObject inModel) throws VException, SQLException {
		inModel.setVirgin();
		inModel.set("Name", inName);
		inModel.set("Street", inStreet);
		inModel.set("City", inCity);
		inModel.set("PLZ", inPLZ);
		inModel.set("Sex", new Integer(1));
		inStatement.setValues(inModel);
		inStatement.executeUpdate();
		inStatement.commit();
	}
	
	private int countAutoKeys(Collection<Long> inKeys) {
		int outNumber = 0;
		for (Long lKey : inKeys) {
			System.out.println(lKey.toString());
			outNumber++;
		}
		return outNumber;
	}
}

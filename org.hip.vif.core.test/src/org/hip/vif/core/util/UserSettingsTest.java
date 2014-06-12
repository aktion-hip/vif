package org.hip.vif.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 12.01.2012
 */
public class UserSettingsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testGetKeys() throws Exception {
		XMLBuilder lXML = new XMLBuilder();
		lXML.setRootNodeName("UserSettings");
		lXML.inject("<language value=\"de\"/>");
		lXML.inject("<mySetting value=\"myValue\"/>");
		
		UserSettings lSettings = new UserSettings(lXML.toString());
		Collection<String> lKeys = lSettings.getKeys();
		
		List<String> lCompare = Arrays.asList("language", "mySetting");
		assertEquals(lCompare.size(), lKeys.size());
		for (String lKey : lKeys) {			
			assertTrue(lCompare.contains(lKey));
		}
		
		lSettings = new UserSettings(null);
		assertEquals(0, lSettings.getKeys().size());
		
		lSettings = new UserSettings("");
		assertEquals(0, lSettings.getKeys().size());
	}
	
	@Test
	public void testGetValue() throws Exception {
		XMLBuilder lXML = new XMLBuilder();
		lXML.setRootNodeName("UserSettings");
		lXML.inject("<language value=\"de\"/>");
		lXML.inject("<mySetting value=\"myValue\"/>");
		
		UserSettings lSettings = new UserSettings(lXML.toString());
		assertEquals("de", lSettings.getValue("language"));
		assertEquals("myValue", lSettings.getValue("mySetting"));
		
		lSettings = new UserSettings(null);
		assertNull(lSettings.getValue("language"));
	}
	
	@Test
	public void testSetValue() throws Exception {
		XMLBuilder lXML = new XMLBuilder();
		lXML.setRootNodeName("UserSettings");
		lXML.inject("<mySetting value=\"myValue\"/>");
		
		UserSettings lSettings = new UserSettings(lXML.toString());
		assertNull(lSettings.getValue("language"));
		assertEquals(1, lSettings.getKeys().size());
		
		lSettings.setValue("language", "de");
		assertEquals(2, lSettings.getKeys().size());
		assertEquals("de", lSettings.getValue("language"));
		
		lSettings.setValue("language", "en");
		assertEquals(2, lSettings.getKeys().size());
		assertEquals("en", lSettings.getValue("language"));
		
		lSettings = new UserSettings(null);
		assertNull(lSettings.getValue("language"));
		
		lSettings.setValue("language", "de");
		assertEquals(1, lSettings.getKeys().size());
		assertEquals("de", lSettings.getValue("language"));
		
		lSettings.setValue("language", "en");
		assertEquals(1, lSettings.getKeys().size());
		assertEquals("en", lSettings.getValue("language"));
	}

	@Test
	public void testSerialize() throws Exception {
		UserSettings lSettings = new UserSettings(null);
		lSettings.setValue("language", "en");
		lSettings.setValue("mySetting", "myValue");
		
		String lExpected = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n<UserSettings><language value=\"en\"/><mySetting value=\"myValue\"/></UserSettings>";
		assertEquals(lExpected, lSettings.toXML());
		
		UserSettings lSettings2 = new UserSettings(lExpected);
		assertEquals(2, lSettings2.getKeys().size());
		assertEquals("en", lSettings2.getValue("language"));
		assertEquals("myValue", lSettings2.getValue("mySetting"));		
	}
}

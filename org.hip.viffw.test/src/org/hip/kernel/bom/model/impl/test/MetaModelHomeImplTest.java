package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.hip.kernel.bom.Home;
import org.hip.kernel.bom.model.MetaModelHome;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class MetaModelHomeImplTest {
	public static MetaModelHome	home = null ;

	@BeforeClass
	public static void init() {
		home = (MetaModelHome) Home.manager.getHome("org.hip.kernel.bom.model.impl.MetaModelHomeImpl") ; 
	}
	
	@Test
	public void testGetDefDefs() {
		// Test if all DefDefs will be instantiated
		assertNotNull("testGetDefDefs 1", home.getObjectDefDef() ) ; 
		assertNotNull("testGetDefDefs 2", home.getPropertyDefDef() ) ; 
		assertNotNull("testGetDefDefs 3", home.getMappingDefDef() ) ; 
		assertNotNull("testGetDefDefs 4", home.getJoinDefDef() ) ; 
		assertNotNull("testGetDefDefs 5", home.getJoinedObjectDefDef() ) ; 
		assertNotNull("testGetDefDefs 6", home.getKeyDefDef() ) ; 
		assertNotNull("testGetDefDefs 7", home.getRelationshipDefDef() ) ;
	
		assertTrue("instanceOf 1", home.getObjectDefDef() instanceof org.hip.kernel.bom.model.ObjectDefDef);
		assertTrue("instanceOf 2", home.getPropertyDefDef() instanceof org.hip.kernel.bom.model.PropertyDefDef);
		assertTrue("instanceOf 3", home.getMappingDefDef() instanceof org.hip.kernel.bom.model.MappingDefDef);
		assertTrue("instanceOf 4", home.getJoinDefDef() instanceof org.hip.kernel.bom.model.JoinDefDef);
		assertTrue("instanceOf 5", home.getJoinedObjectDefDef() instanceof org.hip.kernel.bom.model.JoinedObjectDefDef);
		assertTrue("instanceOf 6", home.getKeyDefDef() instanceof org.hip.kernel.bom.model.KeyDefDef);
		assertTrue("instanceOf 7", home.getRelationshipDefDef() instanceof org.hip.kernel.bom.model.RelationshipDefDef);
	}

	@Test
	public void testInstantiation() {
		assertNotNull("testInstantiation", home ) ; 
	}
}

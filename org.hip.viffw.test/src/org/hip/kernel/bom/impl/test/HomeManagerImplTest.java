package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.*;
import org.hip.kernel.bom.HomeManager;
import org.hip.kernel.bom.impl.HomeManagerImpl;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class HomeManagerImplTest {
	
	@Test
	public void testGetHome() {
		HomeManager lManager = HomeManagerImpl.getSingleton();
		assertNotNull("not null Manager", lManager);
	
		String lHomeClassName = "org.hip.kernel.bom.impl.test.Test2DomainObjectHomeImpl";
		assertNotNull("not null home", lManager.getHome(lHomeClassName));
		assertNull("not null home", lManager.getHome(lHomeClassName + "!!"));
	}
}

package org.hip.vif.core.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.hip.vif.core.code.Role;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 23.02.2012
 */
public class RolesCheckTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void test() {
		Role lRole1 = new Role("1");
		Role lRole2 = new Role("2");
		Role lRole3 = new Role("3");
		Role lRole4 = new Role("4");
		
		Collection<Role> lRoles1 = new ArrayList<Role>();
		Collections.addAll(lRoles1, lRole1, lRole2);
		Collection<Role> lRoles2 = new ArrayList<Role>();
		Collections.addAll(lRoles2, lRole1, lRole2, lRole3);
		
		RolesCheck lChecker = new RolesCheck(lRoles1, new String[] {});
		assertTrue(lChecker.hasChanged());
		
		lChecker = new RolesCheck(lRoles1, new String[] {"2"});
		assertTrue(lChecker.hasChanged());
		
		lChecker = new RolesCheck(lRoles1, new String[] {"2", "1"});
		assertFalse(lChecker.hasChanged());
		
		//role 3 is ignored
		lChecker = new RolesCheck(lRoles2, new String[] {});
		assertTrue(lChecker.hasChanged());
		
		lChecker = new RolesCheck(lRoles2, new String[] {"2"});
		assertTrue(lChecker.hasChanged());
		
		lChecker = new RolesCheck(lRoles2, new String[] {"2", "1"});
		assertFalse(lChecker.hasChanged());
		
		//role 4 is ignored
		lRoles2.add(lRole4);
		
		lChecker = new RolesCheck(lRoles2, new String[] {"2", "1"});
		assertFalse(lChecker.hasChanged());
		
		lChecker = new RolesCheck(lRoles2, new String[] {"1"});
		assertTrue(lChecker.hasChanged());
		
		assertTrue(lChecker.hasRole(1));
		assertTrue(lChecker.hasRole(2));
		assertTrue(lChecker.hasRole(3));
		assertTrue(lChecker.hasRole(4));
		assertFalse(lChecker.hasRole(5));
		assertFalse(lChecker.hasRole(6));
		assertFalse(lChecker.hasRole(7));
		assertFalse(lChecker.hasRole(8));
	}

}

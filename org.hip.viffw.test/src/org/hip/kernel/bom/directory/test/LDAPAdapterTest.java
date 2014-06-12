package org.hip.kernel.bom.directory.test;

import static org.junit.Assert.*;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.directory.LDAPAdapter;
import org.hip.kernel.bom.directory.LDAPObjectHome;
import org.hip.kernel.bom.impl.CriteriaStackFactory;
import org.hip.kernel.bom.impl.KeyCriterionImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 14.07.2007
 */
public class LDAPAdapterTest {

	@Test
	public void testCreateSelectString() throws VException {
		LDAPAdapter lAdapter = new LDAPAdapter();
		LDAPObjectHome lHome = new TestLDAPObjectHome();
		String lFilter = lAdapter.createSelectString(createKey(), lHome);
		assertEquals("filter 1", "(|(name=query*)(sn=query*))", lFilter);
	}

	private KeyObject createKey() throws VException {
		String lQueryTerm = "query";
		KeyObject outKey = new KeyObjectImpl();
		outKey.setValue(TestLDAPObjectHome.KEY_NAME, lQueryTerm + "*");
		outKey.setValue(TestLDAPObjectHome.KEY_FIRST_NAME, lQueryTerm + "*", "=", BinaryBooleanOperator.OR);
		outKey.setCriteriaStackFactory(new CriteriaStackFactory(CriteriaStackFactory.StackType.LDAP));
		outKey.setLevelReturnFormatter(KeyCriterionImpl.LEVEL_STRAIGHT);
		return outKey;
	}

}

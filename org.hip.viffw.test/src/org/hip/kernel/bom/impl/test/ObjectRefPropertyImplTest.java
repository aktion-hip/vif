package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.*;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.ObjectRefProperty;
import org.hip.kernel.bom.PropertySet;
import org.hip.kernel.bom.impl.ObjectRefPropertyImpl;
import org.hip.kernel.bom.impl.PropertySetImpl;
import org.hip.kernel.sys.VSys;

/**
 * @author: Benno Luthiger
 */
public class ObjectRefPropertyImplTest {

	public void testGetValue() {
		try {
			DomainObject lDomainObject = ((TestRelDefDomainObjectHomeImpl)VSys.homeManager.getHome("org.hip.kernel.bom.impl.test.TestRelDefDomainObjectHomeImpl")).create();
			lDomainObject.set("TestID", new Integer(20));
			PropertySet lPropertySet = new PropertySetImpl(lDomainObject);
			assertNotNull(lPropertySet);
			
			ObjectRefProperty lObjectRef = new ObjectRefPropertyImpl(lPropertySet, "FirstName");
			Object lRet = lObjectRef.getValue();
			assertNotNull(lRet);
		}
		catch (org.hip.kernel.bom.SettingException exc) {
			fail(exc.getMessage());
		}
		catch (org.hip.kernel.bom.BOMException exc) {
			fail(exc.getMessage());
		}
	}
}

package org.hip.kernel.bom.directory.test;

import org.hip.kernel.bom.directory.DirContextWrapper;
import org.hip.kernel.bom.directory.LDAPObjectHome;
import org.hip.kernel.bom.directory.LDAPQueryStatement;

/**
 * Mock object
 *
 * @author Luthiger
 * Created: 06.07.2007
 */
@SuppressWarnings("serial")
public class TestLDAPQueryStatement extends LDAPQueryStatement {

	public TestLDAPQueryStatement(LDAPObjectHome inHome) {
		super(inHome, "");
	}

	protected DirContextWrapper createContext() {
		return new TestDirContext(createControls());
	}		

}

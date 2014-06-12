package org.hip.kernel.bom.impl.test;

import java.io.File;
import java.sql.SQLException;

import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.exc.VException;

/**
 * @author Benno Luthiger
 * Created on 23.02.2006
 */
public interface TestBlobHome extends DomainObjectHome {
	public final static String KEY_ID = "ID";
	public final static String KEY_NAME = "Name";
	public final static String KEY_XVALUE = "Value";
	

	TestBlobImpl ucNew(String inContent, File inFile) throws VException, SQLException;
}

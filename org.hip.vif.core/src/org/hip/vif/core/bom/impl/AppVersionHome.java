package org.hip.vif.core.bom.impl;

import java.sql.SQLException;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Home of the AppVersion model.
 * 
 * @author Luthiger
 * Created: 16.02.2012
 */
@SuppressWarnings("serial")
public class AppVersionHome extends DomainObjectHomeImpl {
	private static final Logger LOG = LoggerFactory.getLogger(AppVersionHome.class);
	
	private static final String DFT_VERSION = "0.1";

	public final static String KEY_ID = "ID";
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.AppVersion";
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<objectDef objectName='AppVersion' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	" +
		"	<keyDefs>	" +
		"		<keyDef>	" +
		"			<keyItemDef seq='0' keyPropertyName='" + KEY_ID + "'/>	" +
		"		</keyDef>	" +
		"	</keyDefs>	" +
		"	<propertyDefs>	" +
		"		<propertyDef propertyName='" + KEY_ID + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblAppVersion' columnName='VersionID'/>	" +
		"		</propertyDef>	" +
		"	</propertyDefs>	" +
		"</objectDef>";

	/**
	 * Returns the name of the objects which this home can create.
	 *
	 * @return java.lang.String
	 */
	public String getObjectClassName() {
		return OBJECT_CLASS_NAME;
	}

	/**
	 * Returns the object definition string of the class managed by this home.
	 *
	 * @return java.lang.String
	 */
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}

	/**
	 * Stores the specified version.
	 * 
	 * @param inVersion String
	 */
	public void setVersion(String inVersion) {
		try {
			if (getCount() > 0) {
				KeyObject lKey = new KeyObjectImpl();
				delete(lKey, true);
			}
			DomainObject lVersion = create();
			lVersion.set(KEY_ID, inVersion);
			lVersion.insert(true);
		}
		catch (SQLException exc) {
			LOG.error("Error encountered while saving the version information!", exc);
		}
		catch (VException exc) {
			LOG.error("Error encountered while saving the version information!", exc);
		}
	}

	/**
	 * Retrieves the stored version information.
	 * 
	 * @return String the application's actual version
	 */
	public String getVersion() {
		try {
			QueryResult lVersions = select();
			while (lVersions.hasMoreElements()) {
				return lVersions.nextAsDomainObject().get(KEY_ID).toString();
			}
		}
		catch (VException exc) {
			LOG.error("Error encountered while retrieving the version information!", exc);
		}
		catch (SQLException exc) {
			LOG.error("Error encountered while retrieving the version information!", exc);
		}
		
		return DFT_VERSION;
	}

}

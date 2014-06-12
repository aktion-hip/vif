package org.hip.kernel.bom.impl.test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import junit.framework.TestCase;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.impl.DBAdapterType;
import org.hip.kernel.dbaccess.DBAccessConfiguration;
import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Utility class for testing purpose.
 * Creating and deleting of entries in viftest.tblTest.</p>
 * <p><b>Note:</b> the MySQL pool size has to be set to 200 (or greater)</p>
 * 
 * @author: Benno Luthiger
 */
public class DataHouseKeeper {
	private static final Logger LOG = LoggerFactory.getLogger(DataHouseKeeper.class);
	private static final int SLEEP_PERIOD = 100; //milliseconds
	
	private static DataHouseKeeper singleton = new DataHouseKeeper();
	
	//constants
	private static final String PROPERTIES_FILE = "vif_db.properties";

	private final static String SIMPLE_HOME_NAME 	= "org.hip.kernel.bom.impl.test.Test2DomainObjectHomeImpl";
	private final static String ALTERNATIVE_HOME 	= "org.hip.kernel.bom.impl.test.TestAlternativeHomeImpl";
	private final static String LINK_HOME_NAME 		= "org.hip.kernel.bom.impl.test.LinkGroupMemberHomeImpl";
	private final static String JOIN_HOME_NAME 		= "org.hip.kernel.bom.impl.test.Test2JoinedDomainObjectHomeImpl";
	private final static String GROUP_HOME_NAME 	= "org.hip.kernel.bom.impl.test.TestGroupHomeImpl";
	private final static String PARTICIPANT_HOME_NAME 	= "org.hip.kernel.bom.impl.test.TestParticipantHomeImpl";	
	private final static String NESTED_HOME_NAME 	= "org.hip.kernel.bom.impl.test.TestNestedDomainObjectHomeImpl";	
	
	//instance variables
	private Test2DomainObjectHomeImpl 	simpleHome = null;
	private LinkGroupMemberHomeImpl 	linkHome = null;
	private Test2JoinedDomainObjectHomeImpl joinHome = null;
	private DBAccessConfiguration dbConfiguration = null;
	
	/**
	 * DataHouseKeeper default constructor.
	 */
	private DataHouseKeeper() {
		try {
			DataSourceRegistry.INSTANCE.setFactory(new TestDataSourceFactory());
			DataSourceRegistry.INSTANCE.setActiveConfiguration(getDBAccessConfiguration());
		}
		catch (IOException exc) {
			LOG.error("Could not initialize the DataHouseKeeper!", exc);
		}
	}
	
	public static DataHouseKeeper getInstance() {
		return singleton;
	}

	public Long createTestEntry(String inName) throws SQLException {
		return createTestEntry(inName, "Adam");
	}
	
	public Long createTestEntry(String inName, String inFirstName) throws SQLException {
		Long outID = null;
		try {
			DomainObject lNew = getSimpleHome().create();
			lNew.set("Name", inName);
			lNew.set("Firstname", inFirstName);
			lNew.set("Mail", "dummy1@aktion-hip.ch");
			lNew.set("Sex", new Long(1));
			lNew.set("Amount", new Float(12.45));
			lNew.set("Double", new Float(13.11)); 
			outID = lNew.insert(true);
			lNew.release();
		}
		catch (VException exc) {
			TestCase.fail(exc.getMessage());
		}
		return outID;
	}
	
	public void createTestLinkEntry(int inMebmerID, int inGroupID) throws SQLException, VException {
		DomainObject lNew = getLinkHome().create();
		lNew.set("MemberID", new Long(inMebmerID));
		lNew.set("GroupID", new Long(inGroupID));
		lNew.insert();
		lNew.release();
	}

	public void deleteAllFromLink() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblLinkGroupMember");
	}

	public void deleteAllFromSimple() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblTest");
	}

	public void deleteAllFromGroup() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblGroup");
	}

	public void deleteAllFromParticipant() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblParticipant");
	}

	@SuppressWarnings("static-access")
	void deleteAllFrom(String inTableName) throws SQLException, VException, InterruptedException {
		Connection lConnection = DataSourceRegistry.INSTANCE.getConnection();
		Statement lStatement = lConnection.createStatement();
		lStatement.execute("DELETE FROM " + inTableName);
//		lConnection.commit();
		lStatement.close();
		lConnection.close();
		Thread.currentThread().sleep(SLEEP_PERIOD);
	}
	
	public ResultSet executeQuery(String inSQL) throws SQLException, VException {
		Connection lConnection = DataSourceRegistry.INSTANCE.getConnection();
		Statement lStatement = lConnection.createStatement();
		ResultSet lResultSet = lStatement.executeQuery(inSQL);
		//lStatement.close();
		lConnection.close();
		return lResultSet;
	}
	public LinkGroupMemberHomeImpl getLinkHome() {
		if (linkHome == null)
			linkHome = (LinkGroupMemberHomeImpl)VSys.homeManager.getHome(LINK_HOME_NAME);
		
		return linkHome;
	}

//	public int getNumberOfConnections() throws SQLException {
//		return PersistencyManager.singleton.getDefaultConnectionDriver().getNumberOfConnections();
//	}
	
	public Test2DomainObjectHomeImpl getSimpleHome() {
		if (simpleHome == null)
			simpleHome = (Test2DomainObjectHomeImpl)VSys.homeManager.getHome(SIMPLE_HOME_NAME);
	
		return simpleHome;
	}
	
	public TestAlternativeHomeImpl getAlternativeHome() {
		return (TestAlternativeHomeImpl)VSys.homeManager.getHome(ALTERNATIVE_HOME);
	}
	
	public Test2JoinedDomainObjectHomeImpl getJoinHome() {
		if (joinHome == null) {
			joinHome = (Test2JoinedDomainObjectHomeImpl)VSys.homeManager.getHome(JOIN_HOME_NAME);
		}
		return joinHome;
	}
	
	public TestGroupHomeImpl getGroupHome() {
		return (TestGroupHomeImpl)VSys.homeManager.getHome(GROUP_HOME_NAME);
	}
	
	public TestParticipantHomeImpl getParticipantHome() {
		return (TestParticipantHomeImpl)VSys.homeManager.getHome(PARTICIPANT_HOME_NAME);
	}
	
	public TestNestedDomainObjectHomeImpl getNestedDomainObjectHomeImpl() {
		return (TestNestedDomainObjectHomeImpl)VSys.homeManager.getHome(NESTED_HOME_NAME);
	}
	
	public Statement getStatement() throws SQLException, VException {
		Connection lConnection = DataSourceRegistry.INSTANCE.getConnection();
		return lConnection.createStatement();
	}

	public void insertTestEntry(DomainObject inDomainObject) throws SQLException, VException {
		inDomainObject.insert(true);
	}
	
	public boolean isDBMySQL() {
		return DataSourceRegistry.INSTANCE.getAdapterType().isOfType(DBAdapterType.DB_TYPE_MYSQL.getType());
	}
	
	public boolean isDBOracle() {
		return DataSourceRegistry.INSTANCE.getAdapterType().isOfType(DBAdapterType.DB_TYPE_ORACLE.getType());
	}
	
	public boolean isDBPostgreSQL() {
		return DataSourceRegistry.INSTANCE.getAdapterType().isOfType(DBAdapterType.DB_TYPE_POSTGRESQL.getType());
	}
	
	private DBAccessConfiguration createDBAccessConfiguration() throws IOException {
		InputStream lStream = null;
		try {
			lStream = DataHouseKeeper.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
			Properties lProperties = new Properties();
			lProperties.load(lStream);
			lStream.close();
			return new DBAccessConfiguration(lProperties.getProperty("org.hip.vif.db.driver"), 
					lProperties.getProperty("org.hip.vif.db.server"), 
					lProperties.getProperty("org.hip.vif.db.schema"), 
					lProperties.getProperty("org.hip.vif.db.userId"), 
					lProperties.getProperty("org.hip.vif.db.password"));
		}
		finally {
			if (lStream != null) {
				lStream.close();
			}
		}
	}
	
	/**
	 * @return {@link DBAccessConfiguration} the db access configuration for testing purposes
	 * @throws IOException
	 */
	public DBAccessConfiguration getDBAccessConfiguration() throws IOException {
		if (dbConfiguration == null) {
			dbConfiguration = createDBAccessConfiguration();
		}
		return dbConfiguration;
	}
}

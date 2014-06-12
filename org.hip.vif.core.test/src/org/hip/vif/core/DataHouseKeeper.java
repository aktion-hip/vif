package org.hip.vif.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.hip.kernel.bitmap.IDPosition;
import org.hip.kernel.bitmap.IDPositions;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.DefaultStatement;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.dbaccess.DBAccessConfiguration;
import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.BookmarkHome;
import org.hip.vif.core.bom.CompletionAuthorReviewerHome;
import org.hip.vif.core.bom.CompletionHistoryHome;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.DownloadTextHome;
import org.hip.vif.core.bom.GroupAdminHome;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.JoinParticipantToMemberHome;
import org.hip.vif.core.bom.LinkMemberRoleHome;
import org.hip.vif.core.bom.LinkPermissionRole;
import org.hip.vif.core.bom.LinkPermissionRoleHome;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHistoryHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.ParticipantHome;
import org.hip.vif.core.bom.Permission;
import org.hip.vif.core.bom.PermissionHome;
import org.hip.vif.core.bom.QuestionAuthorReviewerHome;
import org.hip.vif.core.bom.QuestionHierarchyHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.SubscriptionHome;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.TextAuthorReviewerHome;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.impl.JoinAuthorReviewerToCompletionHome;
import org.hip.vif.core.bom.impl.JoinAuthorReviewerToQuestionHome;
import org.hip.vif.core.bom.impl.JoinAuthorReviewerToTextHome;
import org.hip.vif.core.bom.impl.JoinCompletionToMemberHome;
import org.hip.vif.core.bom.impl.JoinCompletionToQuestionHome;
import org.hip.vif.core.bom.impl.JoinQuestionToAuthorReviewerHome;
import org.hip.vif.core.bom.impl.JoinQuestionToChildAndAuthorHome;
import org.hip.vif.core.bom.impl.JoinQuestionToChildHome;
import org.hip.vif.core.bom.impl.JoinQuestionToContributorsHome;
import org.hip.vif.core.bom.impl.JoinSubscriptionToMemberHome;
import org.hip.vif.core.bom.impl.JoinTextToMemberHome;
import org.hip.vif.core.bom.impl.MemberImpl;
import org.hip.vif.core.bom.impl.Test2DomainObjectHomeImpl;
import org.hip.vif.core.bom.impl.TextQuestionHome;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.util.StatementsFileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Utility class for testing purpose.
 * Creating and deleting of entries in viftest-DB.<br />
 * Note: had to adjust the MySQL configuration my.ini: max_connections=1024 
 * 
 * @author: Benno Luthiger
 */
public class DataHouseKeeper {
	private static final Logger LOG = LoggerFactory.getLogger(DataHouseKeeper.class);
	private static final int SLEEP_PERIOD = 50; //milliseconds 50 200
	private static final String EMBEDDED_DERBY = "org.apache.derby.jdbc.EmbeddedDriver/Derby (embedded)/10.5.1.1";
	
	private static DataHouseKeeper singleton = new DataHouseKeeper();
	
	//constants
	private static final String PROPERTIES_FILE = "vif_db.properties";
	
	public final static String KEY_PERMISSION_ID = "ID";
	public final static String KEY_PERMISSION_LABEL = "Label";
	public final static String KEY_PERMISSION_DESCR = "Description";
	
	public final static String KEY_LINK_PERMISSION_ID = "PermissionID";
	public final static String KEY_LINK_ROLE_ID = "RoleID";
	
	public final static String PERMISSION_LABEL_1 = "testPermission1";
	public final static String PERMISSION_LABEL_2 = "testPermission2";

	private final static String MEMBER_USER_ID = "TestUsr-DHK";
	private final static String DFT_NAME = "NameT";
	private final static String GROUP_ID = "TestGroup";
	public static final Long DFT_GROUP_ID = new Long(23);
	
	private final static String SIMPLE_HOME_NAME = "org.hip.vif.bom.impl.test.Test2DomainObjectHomeImpl";
	
	private final static String KEY_PROPERTY_URL = "org.hip.vif.db.url";
	

	//instance variables
	private Test2DomainObjectHomeImpl simpleHome = null;
	private String urlDB = null;
	private boolean isEmbeddedDerby = false;
	
	/**
	 * Constructor for DataHouseKeeper.
	 * @throws IOException 
	 */
	private DataHouseKeeper() {
		initMySQL();
//		initDerbyEmbedded();
	}
	
	public void reInitialize() {
		if (isEmbeddedDerby) {
			initDerbyEmbedded();
		}
		else {
			initMySQL();
		}
	}
	
	/**
	 * @return boolean <code>true</code> if the configured connection setting is for an embedded database
	 */
	public boolean isEmbedded() {
		return isEmbeddedDerby;
	}
	
	private void initMySQL() {		
		try {
			DataSourceRegistry.INSTANCE.setFactory(new TestDataSourceFactoryMySQL());
			DataSourceRegistry.INSTANCE.setActiveConfiguration(createDBAccessConfiguration());
			
			VSys.setContextPath(new File("").getAbsolutePath());
		}
		catch (IOException exc) {
			LOG.error("Could not initialize the DataHouseKeeper for MySQL!", exc);
		}
	}
	
	private void initDerbyEmbedded() {		
		try {
			isEmbeddedDerby = true;
			DataSourceRegistry.INSTANCE.setFactory(new TestDataSourceFactoryDerby());
			DataSourceRegistry.INSTANCE.setActiveConfiguration(createDBAccessConfigurationEmbedded());
			
			VSys.setContextPath(new File("").getAbsolutePath());
			createEmbeddedTables();
		}
		catch (Exception exc) {
			LOG.error("Could not initialize the DataHouseKeeper for Derby!", exc);
		}
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
	
	private DBAccessConfiguration createDBAccessConfigurationEmbedded() {
		File lSchema = new File(".");
		return new DBAccessConfiguration(EMBEDDED_DERBY, "", new File(lSchema, "data/vif_data").getAbsolutePath(), "", "");
	}
	
	public static DataHouseKeeper getInstance() {
		return singleton;
	}
	
	public MemberHome getMemberHome() throws Exception {
		return (MemberHome)VSys.homeManager.getHome(MemberImpl.HOME_CLASS_NAME);
	}

	public LinkMemberRoleHome getLinkMemberRoleHome() {
		return (LinkMemberRoleHome)BOMHelper.getLinkMemberRoleHome();
	}

	public MemberHistoryHome getMemberHistoryHome() {
		return (MemberHistoryHome)BOMHelper.getMemberHistoryHome();
	}
	
	public LinkPermissionRoleHome getLinkPermissionRoleHome() {
		return (LinkPermissionRoleHome)BOMHelper.getLinkPermissionRoleHome();
	}
	
	public PermissionHome getPermissionHome() {
		return (PermissionHome)BOMHelper.getPermissionHome();
	}

	public GroupHome getGroupHome() {
		return (GroupHome)BOMHelper.getGroupHome();
	}

	public GroupAdminHome getGroupAdminHome() {
		return (GroupAdminHome)BOMHelper.getGroupAdminHome();
	}

	public ParticipantHome getParticipantHome() {
		return (ParticipantHome)BOMHelper.getParticipantHome();
	}
	
	public QuestionHome getQuestionHome() {
		return (QuestionHome)BOMHelper.getQuestionHome();
	}
	
	public CompletionHome getCompletionHome() {
		return (CompletionHome)BOMHelper.getCompletionHome(); 
	}
	
	public CompletionHistoryHome getCompletionHistoryHome() {
		return (CompletionHistoryHome)BOMHelper.getCompletionHistoryHome(); 
	}
	
	public CompletionAuthorReviewerHome getCompletionAuthorReviewerHome() {
		return (CompletionAuthorReviewerHome)BOMHelper.getCompletionAuthorReviewerHome();
	}
	
	public QuestionAuthorReviewerHome getQuestionAuthorReviewerHome() {
		return (QuestionAuthorReviewerHome)BOMHelper.getQuestionAuthorReviewerHome();
	}
	
	public QuestionHierarchyHome getQuestionHierarchyHome() {
		return (QuestionHierarchyHome)BOMHelper.getQuestionHierarchyHome();
	}
	
	public SubscriptionHome getSubscriptionHome() {
		return (SubscriptionHome)BOMHelper.getSubscriptionHome(); 
	}
	
	public BookmarkHome getBookmarkHome() {
		return (BookmarkHome)BOMHelper.getBookmarkHome();
	}
	
	public JoinQuestionToAuthorReviewerHome getJoinQuestionToAuthorReviewerHome() {
		return (JoinQuestionToAuthorReviewerHome)BOMHelper.getJoinQuestionToAuthorReviewerHome();
	}
	
	public JoinQuestionToContributorsHome getJoinQuestionToContributorsHome() {
		return BOMHelper.getJoinQuestionToContributorsHome();
	}
	
	public JoinQuestionToChildHome getJoinQuestionToChildHome() {
		return (JoinQuestionToChildHome)BOMHelper.getJoinQuestionToChildHome();
	}
	
	public JoinAuthorReviewerToQuestionHome getJoinAuthorReviewerToQuestionHome() {
		return (JoinAuthorReviewerToQuestionHome)BOMHelper.getJoinAuthorReviewerToQuestionHome();
	}
	
	public JoinAuthorReviewerToCompletionHome getJoinAuthorReviewerToCompletionHome() {
		return (JoinAuthorReviewerToCompletionHome)BOMHelper.getJoinAuthorReviewerToCompletionHome();
	}
	
	public JoinAuthorReviewerToTextHome getJoinAuthorReviewerToTextHome() {
		return (JoinAuthorReviewerToTextHome)BOMHelper.getJoinAuthorReviewerToTextHome();
	}
	
	public TextAuthorReviewerHome getTextAuthorReviewerHome() {
		return (TextAuthorReviewerHome) BOMHelper.getTextAuthorReviewerHome();
	}
	
	public TextQuestionHome getTextQuestionHome() {
		return BOMHelper.getTextQuestionHome();
	}
	
	public TextHome getTextHome() {
		return BOMHelper.getTextHome();
	}
	
	public JoinCompletionToQuestionHome getJoinCompletionToQuestionHome() {
		return (JoinCompletionToQuestionHome)BOMHelper.getJoinCompletionToQuestionHome();
	}
	
	public JoinCompletionToMemberHome getJoinCompletionToMemberHome() {
		return (JoinCompletionToMemberHome)BOMHelper.getJoinCompletionToMemberHome();
	}
	
	public JoinQuestionToChildAndAuthorHome getJoinQuestionToChildAndAuthorHome() {
		return (JoinQuestionToChildAndAuthorHome)BOMHelper.getJoinQuestionToChildAndAuthorHome();
	}
	
	public JoinParticipantToMemberHome getJoinParticipantToMemberHome() {
		return (JoinParticipantToMemberHome)BOMHelper.getJoinParticipantToMemberHome();
	}
	
	public JoinSubscriptionToMemberHome getJoinSubscriptionToMemberHome() {
		return (JoinSubscriptionToMemberHome)BOMHelper.getJoinSubscriptionToMemberHome();
	}

	public DownloadTextHome getDownloadTextHome() {
		return BOMHelper.getDownloadTextHome();
	}	
	
	public JoinTextToMemberHome getJoinTextToMemberHome() {
		return BOMHelper.getJoinTextToMemberHome();
	}

// --- delete methods
	
	public void deleteAllFromAppVersion() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblAppVersion");
	}
	
	public void deleteAllFromMember() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblMember");
	}
	
	public void deleteAllFromMemberHistory() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblMemberHistory");
	}
	
	public void deleteAllFromLinkMemberRole() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblLinkMemberRole");
	}
		
	public void deleteAllFromLinkPermissionRole() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblLinkPermissionRole");
	}
	
	public void deleteAllFromPermission() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblPermission");
	}
	
	public void deleteAllFromGroup() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblGroup");
	}
	
	public void deleteAllFromGroupAdmin() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblGroupAdmin");
	}
	
	public void deleteAllFromParticipant() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblParticipant");
	}
	
	public void deleteAllFromQuestion() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblQuestion");
	}
	
	public void deleteAllFromQuestionHistory() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblQuestionHistory");
	}

	public void deleteAllFromQuestionHierarchy() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblQuestionHierarchy");
	}
	
	public void deleteAllFromCompletion() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblCompletion");
	}
	
	public void deleteAllFromCompletionHistory() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblCompletionHistory");
	}
	
	public void deleteAllFromQuestionAuthorReviewer() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblQuestionAuthorReviewer");
	}
	
	public void deleteAllFromCompletionAuthorReviewer() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblCompletionAuthorReviewer");
	}
	
	public void deleteAllFromTextAuthorReviewer() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblTextAuthorReviewer");
	}
	
	private void deleteAllFromTextQuestion() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblTextQuestion");
	}
	
	public void deleteAllFromSubscription() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblSubscription");
	}
	
	public void deleteAllFromBookmark() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblBookmark");
	}
	
	private void deleteAllFromRatings() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblRatings");		
	}
	
	public void deleteAllFromRatingEvents() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblRatingEvents");		
	}

	public void deleteAllFromText() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblTextVersion");
	}
	
	public void deleteAllFromDownload() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblDownloadText");
	}
	
	private void deleteAllFromTextHistory() throws SQLException, VException, InterruptedException {
		deleteAllFrom("tblTextVersionHistory");
	}
	
	public void deleteAllInAll() throws SQLException, VException, InterruptedException {
		deleteAllFromMember();
		deleteAllFromMemberHistory();
		deleteAllFromLinkMemberRole();
		deleteAllFromPermission();
		deleteAllFromLinkPermissionRole();
		deleteAllFromGroup();
		deleteAllFromGroupAdmin();
		deleteAllFromParticipant();
		deleteAllFromQuestion();
		deleteAllFromQuestionHistory();
		deleteAllFromQuestionHierarchy();
		deleteAllFromCompletion();
		deleteAllFromCompletionHistory();
		deleteAllFromQuestionAuthorReviewer();
		deleteAllFromCompletionAuthorReviewer();
		deleteAllFromSubscription();
		deleteAllFromBookmark();
		deleteAllFromRatings();
		deleteAllFromRatingEvents();
		deleteAllFromText();
		deleteAllFromTextHistory();
		deleteAllFromTextAuthorReviewer();
		deleteAllFromTextQuestion();
	}

	@SuppressWarnings("static-access")
	void deleteAllFrom(String inTableName) throws SQLException, VException, InterruptedException {
		Connection lConnection = DataSourceRegistry.INSTANCE.getConnection();
		Statement lStatement = lConnection.createStatement();
		lStatement.execute("DELETE FROM " + inTableName);
//		lConnection.commit();
		lStatement.close();
		lConnection.close();
		if (!isEmbeddedDerby) {
			Thread.currentThread().sleep(SLEEP_PERIOD);
		}		
	}
	
	/**
	 * Creates a member entry
	 * 
	 * @param inNr String
	 * @return String member ID
	 * @throws BOMChangeValueException
	 */
	public String createMember(String inNr) throws Exception {
		return createMember(inNr, inNr + ".mail@test");
	}
	
	/**
	 * Creates a member entry with the specified user id and mail address
	 * 
	 * @param inNr User id
	 * @param inMail Mail address
	 * @return String The ID of the new member
	 * @throws BOMChangeValueException
	 */
	public String createMember(String inNr, String inMail) throws Exception {
		try {
			Member lMember = (Member)getMemberHome().create();
			Long outMemberID = lMember.ucNew(MEMBER_USER_ID + inNr, "NameT" + inNr, "VornameT" + inNr, "StrasseT", "PLZ-T", "StadtT", "", "", inMail, "1", "de", "123", new String[] {ApplicationConstants.ROLE_ID_SU, ApplicationConstants.ROLE_ID_GROUP_ADMIN});
			return outMemberID.toString();
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}
	
	
	/**
	 * Creates a member and returns his id
	 * 
	 * @return java.lang.String
	 * @throws BOMChangeValueException
	 */
	public String createMember() throws Exception {
		return createMember("1");
	}
	
	/**
	 * Creates two members and returns their ids
	 * 
	 * @return java.lang.String[]
	 * @throws BOMChangeValueException
	 */
	public String[] create2Members() throws Exception {
		String[] outIDs = new String[2];
		outIDs[0] = createMember("1");
		outIDs[1] = createMember("2");
		return outIDs;
	}
	
	/**
	 * Creates three members and returns their ids
	 * 
	 * @return java.lang.String[]
	 * @throws BOMChangeValueException
	 */
	public String[] create3Members() throws Exception {
		String[] outIDs = new String[3];
		outIDs[0] = createMember("1");
		outIDs[1] = createMember("2");
		outIDs[2] = createMember("3");
		return outIDs;
	}
		
	/**
	 * Creates a member entry and two associated role entries.
	 * 
	 * @return java.math.Long ID of created member entry
	 */
	public Long createMember2Roles() throws Exception {
		return createMember2Roles(DFT_NAME);
	}
	
	/**
	 * Creates a member entry with the specified member name and 
	 * two associated role entries.
	 * 
	 * @return Long ID of created member entry
	 */
	public Long createMember2Roles(String inName) throws Exception {
		return createMemberRoles(inName, new String[] {ApplicationConstants.ROLE_ID_SU, ApplicationConstants.ROLE_ID_GROUP_ADMIN});
	}
	
	/**
	 * Creates a member entry with the specified member name and 
	 * the specified role entries.
	 * 
	 * @param inName String
	 * @param inRoles String[]
	 * @return Long ID of created member entry
	 * @throws BOMChangeValueException
	 */
	public Long createMemberRoles(String inName, String[] inRoles) throws Exception {
		return createMemberRoles(MEMBER_USER_ID, inName, inRoles);
	}

	/**
	 * Creates a member entry with the specified user id and member name and 
	 * the specified role entries.
	 * 
	 * @param inUserID String
	 * @param inName String
	 * @param inRoles String[]
	 * @return Long ID of created member entry
	 * @throws BOMChangeValueException
	 */
	public Long createMemberRoles(String inUserID, String inName, String[] inRoles) throws Exception {
		try {
			Member lMember = (Member)getMemberHome().create();
			return lMember.ucNew(inUserID, inName, "VornameT", "StrasseT", "PLZ-T", "StadtT", "", "", "mail@test", "1", "de", "123", inRoles);
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}
	
	/**
	 * Creates a member entry with the specified member name and 
	 * one associated role.
	 * 
	 * @return Long ID of created member entry
	 */
	public Long createMember1Role(String inUserID, String inName) throws Exception {
		try {
			Member lMember = (Member)getMemberHome().create();
			lMember.ucNew(inUserID, inName, "VornameT2", "StrasseT2", "PLZ-T2", "StadtT2", "", "", "mail.2@test", "0", "de", "321", new String[] {ApplicationConstants.ROLE_ID_PARTICIPANT});
			
			KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(MemberHome.KEY_USER_ID, MEMBER_USER_ID);
			lMember = (Member)getMemberHome().findByKey(lKey);
			return new Long(lMember.get(MemberHome.KEY_ID).toString());
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}
	
	public String getExpectedName() {
		return DFT_NAME;
	}
	
	public String getExpectedUserID() {
		return MEMBER_USER_ID;
	}

	public void checkForEmptyTable() {
		try {
			if (getMemberHome().getCount() > 0)
				fail("Don't test with non empty member table!");
			if (getLinkMemberRoleHome().getCount() > 0)
				fail("Don't test with non empty link table!");
		}
		catch (Exception exc) {
			fail(exc.getMessage());
		}
	}
	
	/**
	 * Creates two permissions (P1, P2).
	 * Associates both permissions to SU, Group-Admin only has P1 associated.
	 */
	public IDPositions create2Permissions3Links() {
		IDPositions outPositions = new IDPositions();
		try {
			Permission lPermission = (Permission)getPermissionHome().create();
			lPermission.set(KEY_PERMISSION_LABEL, PERMISSION_LABEL_1);
			lPermission.set(KEY_PERMISSION_DESCR, "Test permission 1");
			lPermission.insert(true);
			KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(KEY_PERMISSION_LABEL, PERMISSION_LABEL_1);
			Long lPermissionID1 = new Long(getPermissionHome().findByKey(lKey).get(KEY_PERMISSION_ID).toString());
			
			lPermission = (Permission)getPermissionHome().create();
			lPermission.set(KEY_PERMISSION_LABEL, PERMISSION_LABEL_2);
			lPermission.set(KEY_PERMISSION_DESCR, "Test permission 2");
			lPermission.insert(true);
			lKey = new KeyObjectImpl();
			lKey.setValue(KEY_PERMISSION_LABEL, PERMISSION_LABEL_2);
			Long lPermissionID2 = new Long(getPermissionHome().findByKey(lKey).get(KEY_PERMISSION_ID).toString());
			
			LinkPermissionRole lLink = (LinkPermissionRole)getLinkPermissionRoleHome().create();
			lLink.set(KEY_LINK_PERMISSION_ID, lPermissionID1);
			lLink.set(KEY_LINK_ROLE_ID, new Long(ApplicationConstants.ROLE_ID_SU));
			lLink.insert(true);
			outPositions.add(new IDPosition(String.valueOf(lPermissionID1), ApplicationConstants.ROLE_ID_SU));
			
			lLink.setVirgin();
			lLink.set(KEY_LINK_PERMISSION_ID, lPermissionID1);
			lLink.set(KEY_LINK_ROLE_ID, new Long(ApplicationConstants.ROLE_ID_GROUP_ADMIN));
			lLink.insert(true);
			outPositions.add(new IDPosition(String.valueOf(lPermissionID1), ApplicationConstants.ROLE_ID_GROUP_ADMIN));
			
			lLink.setVirgin();
			lLink.set(KEY_LINK_PERMISSION_ID, lPermissionID2);
			lLink.set(KEY_LINK_ROLE_ID, new Long(ApplicationConstants.ROLE_ID_SU));
			lLink.insert(true);
			outPositions.add(new IDPosition(String.valueOf(lPermissionID2), ApplicationConstants.ROLE_ID_SU));
			
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}		
		catch (SQLException exc) {
			fail(exc.getMessage());
		}
		return outPositions;
	}
	
	/**
	 * Creates two members (M1, M2) and two permissions (P1, P2).
	 * M1 has SU-Role, M2 has Group-Admin-Role
	 * SU has both permissions associated, Group-Admin only P1
	 * 
	 * @return Long[] IDs of created members
	 */
	public Long[] create2MembersAndRoleAndPermissions() throws Exception {
		String lUserID1 = "authorization1";
		String lUserID2 = "authorization2";
		Long[] outMemberIDs = new Long[2];
		try {
			Member lMember = (Member)getMemberHome().create();
			lMember.ucNew(lUserID1, "Author 1", "VornameT2", "StrasseT2", "PLZ-T2", "StadtT2", "", "", "mail.2@test", "0", "de", "321", new String[] {ApplicationConstants.ROLE_ID_SU});
			
			KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(MemberHome.KEY_USER_ID, lUserID1);
			lMember = (Member)getMemberHome().findByKey(lKey);
			outMemberIDs[0] = new Long(lMember.get(MemberHome.KEY_ID).toString());
			
			lMember = (Member)getMemberHome().create();
			lMember.ucNew(lUserID2, "Author 2", "VornameT2", "StrasseT2", "PLZ-T2", "StadtT2", "", "", "mail.2@test", "0", "de", "321", new String[] {ApplicationConstants.ROLE_ID_GROUP_ADMIN});
			
			lKey = new KeyObjectImpl();
			lKey.setValue(MemberHome.KEY_USER_ID, lUserID2);
			lMember = (Member)getMemberHome().findByKey(lKey);
			outMemberIDs[1] = new Long(lMember.get(MemberHome.KEY_ID).toString());
			
			create2Permissions3Links();
			return outMemberIDs;
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	private Long createGroup(String inNr) throws BOMChangeValueException {
		try {
			return getGroupHome().createNew(GROUP_ID + inNr, "Group Nr. " + inNr, "1", "3", "10", false);
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}
	
	/**
	 * Creates one group and returns its id
	 * 
	 * @return java.lang.String
	 * @throws BOMChangeValueException
	 */
	public Long createGroup() throws BOMChangeValueException {
		return createGroup("1");
	}
	
	/**
	 * Creates two groups and returns their ids
	 * 
	 * @return java.lang.String[]
	 * @throws BOMChangeValueException
	 */
	public Long[] create2Groups() throws BOMChangeValueException {
		Long[] outIDs = new Long[2];
		outIDs[0] = createGroup("1");
		outIDs[1] = createGroup("2");
		return outIDs;
	}
	
	/**
	 * Creates five groups and returns their ids
	 * 
	 * @return java.lang.String[]
	 * @throws BOMChangeValueException
	 */
	public Long[] create5Groups() throws BOMChangeValueException {
		Long[] outIDs = new Long[5];
		outIDs[0] = createGroup("1");
		outIDs[1] = createGroup("2");
		outIDs[2] = createGroup("3");
		outIDs[3] = createGroup("4");
		outIDs[4] = createGroup("5");
		return outIDs;
	}
	
	/**
	 * Create a new entry to design a group admin for the specified group.
	 * 
	 * @param inGroupID Long
	 * @param inMemberID Long
	 */
	public void createGroupAdmin(Long inGroupID, Long inMemberID) {
		try {
			DomainObject lObject = getGroupAdminHome().create();
			lObject.set(GroupAdminHome.KEY_GROUP_ID, inGroupID);
			lObject.set(GroupAdminHome.KEY_MEMBER_ID, inMemberID);
			lObject.insert(true);
		}
		catch (Exception exc) {
			fail(exc.getMessage());
		}
	}
	
	/**
	 * Creates a Question and returns its ID.
	 * 
	 * @param inQuestion
	 * @param inDecimal
	 * @return Long the Question's ID
	 * @throws BOMChangeValueException
	 */
	public Long createQuestion(String inQuestion, String inDecimal) throws BOMChangeValueException {
		return createQuestion(inQuestion, inDecimal, DFT_GROUP_ID, true);
	}
	
	/**
	 * Creates a Question with specified group and returns its ID.
	 * 
	 * @param inQuestion
	 * @param inDecimal
	 * @param inGroupID
	 * @param isRoot
	 * @return Long the Question's ID
	 * @throws BOMChangeValueException
	 */
	public Long createQuestion(String inQuestion, String inDecimal, Long inGroupID, boolean isRoot) throws BOMChangeValueException {
		return createQuestion(inQuestion, inDecimal, inGroupID, WorkflowAwareContribution.S_PRIVATE, isRoot);
	}

	/**
	 * Creates a Question with specified state and returns its ID.
	 * 
	 * @param inQuestion
	 * @param inDecimal
	 * @param inGroupID
	 * @param inState
	 * @param isRoot
	 * @return Long the Question's ID
	 * @throws BOMChangeValueException
	 */
	public Long createQuestion(String inQuestion, String inDecimal, Long inGroupID, int inState, boolean isRoot) throws BOMChangeValueException {
		Long lRoot = isRoot ? QuestionHome.IS_ROOT : QuestionHome.NOT_ROOT;
		try {
			DomainObject lQuestion = getQuestionHome().create();
			lQuestion.set(QuestionHome.KEY_QUESTION, inQuestion);
			lQuestion.set(QuestionHome.KEY_REMARK, "Remark");
			lQuestion.set(QuestionHome.KEY_QUESTION_DECIMAL, inDecimal);
			lQuestion.set(QuestionHome.KEY_GROUP_ID, inGroupID);
			lQuestion.set(QuestionHome.KEY_ROOT_QUESTION, new Long(lRoot));
			lQuestion.set(QuestionHome.KEY_STATE, new Long(inState));
			return lQuestion.insert(true);
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	/**
	 * Creates a text entry.
	 * 
	 * @param inTitle
	 * @param inAuthor
	 * @return String the created entry's ID
	 * @throws BOMChangeValueException 
	 */
	public Long createText(String inTitle, String inAuthor) throws BOMChangeValueException {
		try {
			DomainObject lText = BOMHelper.getTextHome().create();
			lText.set(TextHome.KEY_VERSION, new Long(0));
			lText.set(TextHome.KEY_TYPE, new Long(1));
			lText.set(TextHome.KEY_TITLE, inTitle);
			lText.set(TextHome.KEY_AUTHOR, inAuthor);
			lText.set(TextHome.KEY_COAUTHORS, "");
			lText.set(TextHome.KEY_SUBTITLE, "About the subtitle");
			lText.set(TextHome.KEY_YEAR, "2010");
			lText.set(TextHome.KEY_PUBLICATION, "");
			lText.set(TextHome.KEY_PAGES, "44-55");
			lText.set(TextHome.KEY_VOLUME, "12");
			lText.set(TextHome.KEY_NUMBER, "8");
			lText.set(TextHome.KEY_PUBLISHER, "");
			lText.set(TextHome.KEY_PLACE, "");
			lText.set(TextHome.KEY_REMARK, "");
			lText.set(TextHome.KEY_REFERENCE, "-");
			lText.set(TextHome.KEY_STATE, new Long(WorkflowAwareContribution.S_PRIVATE));
			return lText.insert(true);
		} catch (Exception exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}
	
	/**
	 * Creates a subscription.
	 * 
	 * @param inQuestionID
	 * @param inMemberID
	 * @param inLocal
	 * @throws VException
	 * @throws SQLException
	 */
	public void createSubscription(Long inQuestionID, Long inMemberID, boolean inLocal) throws VException, SQLException {
		DomainObject lObject = getSubscriptionHome().create();
		lObject.set(SubscriptionHome.KEY_QUESTIONID, inQuestionID);
		lObject.set(SubscriptionHome.KEY_MEMBERID, inMemberID);
		lObject.set(SubscriptionHome.KEY_LOCAL, new Integer(inLocal ? 1 : 0));
		lObject.insert(true);
	}
	
	/**
	 * Creates a Completion and returns its ID.
	 * 
	 * @param inCompletion
	 * @param inQuestionID
	 * @return Long ID
	 * @throws VException
	 * @throws SQLException
	 */
	public Long createCompletion(String inCompletion, Long inQuestionID) throws VException, SQLException {
		return createCompletion(inCompletion, inQuestionID, WorkflowAwareContribution.S_OPEN);
	}
	
	/**
	 * Creates a Completion with specified state and returns its ID.
	 * 
	 * @param inCompletion
	 * @param inQuestionID
	 * @param inState int
	 * @return Long ID
	 * @throws VException
	 * @throws SQLException
	 */
	public Long createCompletion(String inCompletion, Long inQuestionID, int inState) throws VException, SQLException {
		DomainObject lCompletion = getCompletionHome().create();
		lCompletion.set(CompletionHome.KEY_COMPLETION, inCompletion);
		lCompletion.set(CompletionHome.KEY_QUESTION_ID, inQuestionID);
		lCompletion.set(CompletionHome.KEY_STATE, new Long(inState));
		return lCompletion.insert(true);
	}
	
	/**
	 * Creates an author or reviewer entry in the QuestionAuthorReviewer table.
	 * 
	 * @param inQuestionID Long
	 * @param inMemberID Integer
	 * @param isAuthor boolean
	 */
	public void createQuestionProducer(Long inQuestionID, Long inMemberID, boolean isAuthor) throws BOMChangeValueException {
		try {
			if (isAuthor) {
				((QuestionAuthorReviewerHome)getQuestionAuthorReviewerHome()).setAuthor(inMemberID, inQuestionID);
			}
			else {
				((QuestionAuthorReviewerHome)getQuestionAuthorReviewerHome()).setReviewer(inMemberID, inQuestionID);
			}
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}
	
	/**
	 * Creates an author or reviewer entry in the CompletionAuthorReviewer table.
	 * 
	 * @param inCompletionID
	 * @param inMemberID
	 * @param isAuthor
	 * @throws BOMChangeValueException
	 */
	public void createCompletionProducer(Long inCompletionID, Long inMemberID, boolean isAuthor) throws BOMChangeValueException {
		try {
			if (isAuthor) {
				((CompletionAuthorReviewerHome)getCompletionAuthorReviewerHome()).setAuthor(inMemberID, inCompletionID);
			}
			else {
				((CompletionAuthorReviewerHome)getCompletionAuthorReviewerHome()).setReviewer(inMemberID, inCompletionID);
			}
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}
	
	/**
	 * Creates an author or reviewer entry in the TextAuthorReviewer table.
	 * 
	 * @param inTextID
	 * @param inVersion
	 * @param inMemberID
	 * @param isAuthor
	 * @throws BOMChangeValueException
	 */
	public void createTextProducer(Long inTextID, int inVersion, Long inMemberID, boolean isAuthor) throws BOMChangeValueException {
		try {
			if (isAuthor) {
				((TextAuthorReviewerHome)getTextAuthorReviewerHome()).setAuthor(inMemberID, inTextID, inVersion);
			}
			else {
				((TextAuthorReviewerHome)getTextAuthorReviewerHome()).setReviewer(inMemberID, inTextID, inVersion);
			}
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}
	
	public void createQuestionHierachy(Long inParentID, Long inChildID, Long inGroupID) throws VException, SQLException {
		getQuestionHierarchyHome().ucNew(inParentID, inChildID, inGroupID);
	}
	
	/**
	 * Returns the home for the test table.
	 * 
	 * @return org.hip.vif.bom.impl.test.Test2DomainObjectHomeImpl
	 */
	public Test2DomainObjectHomeImpl getSimpleHome() {
		if (simpleHome == null)
			simpleHome = (Test2DomainObjectHomeImpl)VSys.homeManager.getHome(SIMPLE_HOME_NAME);
	
		return simpleHome;
	}
	
	/**
	 * Creates a new entry with the specified name in the test table.
	 * 
	 * @param inName java.lang.String
	 */
	public void createTestEntry(String inName) throws SQLException {
		try {
			DomainObject lNew = getSimpleHome().create();
			lNew.set("Name", inName);
			lNew.set("Firstname", "Evan");
			lNew.set("Mail", "dummy1@aktion-hip.ch");
			lNew.set("Sex", new Integer(1));
			lNew.set("Amount", new Float(12.45));
			lNew.set("Double", new Float(13.11)); 
			lNew.insert(true);
			lNew.release();
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
	}

	/**
	 * Deletes all entries from the test table.
	 */
	public void deleteAllFromTest() {
		Connection lConnection = null;
		Statement lStatement = null;
		try {
			lConnection = DataSourceRegistry.INSTANCE.getConnection();
			lStatement = lConnection.createStatement();
			lStatement.execute("DELETE FROM tblTest");
//			lConnection.commit();
		}
		catch (SQLException exc) {
			fail(exc.getMessage());
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
		finally {
			try {
				if (lStatement != null) lStatement.close();
				if (lConnection != null) lConnection.close();
			}
			catch (SQLException exc) {
				fail(exc.getMessage());
			}
		}
	}	

	/**
	 * Returns the result of a query with the specified SQL statement.
	 * 
	 * @param inSQL java.lang.String
	 * @return java.sql.ResultSet
	 */
	public ResultSet executeQuery(String inSQL) {
		Connection lConnection = null;
		Statement lStatement = null;
		ResultSet lResultSet = null;
		try {
			lConnection = DataSourceRegistry.INSTANCE.getConnection();
			lStatement = lConnection.createStatement();
			lResultSet = lStatement.executeQuery(inSQL);
		}
		catch (SQLException exc) {
			fail(exc.getMessage());
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
		finally {
			try {
				//if (lStatement != null) lStatement.close();
				if (lConnection != null) lConnection.close();
			}
			catch (SQLException exc) {
				fail(exc.getMessage());
			}
		}
		return lResultSet;
	}
	
	public boolean isDBMySQL() {
		return isDBType("mysql");
	}
	
	public boolean isDBOracle() {
		return isDBType("oracle");
	}
	
	public boolean isDBPostgreSQL() {
		return isDBType("psql");
	}

	private boolean isDBType(String inDBTypeName) {
		return getDBUrl().indexOf(inDBTypeName) >= 0;
	}
	
	private String getDBUrl() {
		if (urlDB == null) {
			try {
				urlDB = VSys.getVSysProperties().getProperty(KEY_PROPERTY_URL);
			}
			catch (IOException exc) {
				fail(exc.getMessage());
			}
		}
		return urlDB;		 
	}

	/**
	 * Checks the <code>QueryResult</code> against the specified array.
	 * Both the elements and the lenght of the result set are evaluated.
	 * 
	 * @param inExpected Long[] containing the expected values
	 * @param inResult QueryResult the result set to check
	 * @param inColumn String the column to evaluate within the result set
	 * @param inAssert String the assert string.
	 */
	public void checkQueryResult(Long[] inExpected, QueryResult inResult, String inColumn, String inAssert) {
		try {
			Collection<Long> lExpected = new Vector<Long>(Arrays.asList(inExpected));
			int i = 0;
			while (inResult.hasMoreElements()) {
				GeneralDomainObject lObject = inResult.nextAsDomainObject();				
				assertTrue(inAssert, lExpected.contains((Long)lObject.get(inColumn)));
				i++;
			}
			assertEquals(inAssert + " (count)", inExpected.length, i);
		}
		catch (Exception exc) {
			fail(exc.getMessage());
		}
	}
	public void checkQueryResult(String[] inExpected, QueryResult inResult, String inColumn, String inAssert) {		
		try {
			Collection<String> lExpected = new Vector<String>(Arrays.asList(inExpected));
			int i = 0;
			while (inResult.hasMoreElements()) {
				GeneralDomainObject lObject = inResult.nextAsDomainObject();				
				assertTrue(inAssert, lExpected.contains(lObject.get(inColumn).toString()));
				i++;
			}
			assertEquals(inAssert + " (count)", inExpected.length, i);
		}
		catch (Exception exc) {
			fail(exc.getMessage());
		}
	}

// --- inner classes ---
	
	public static class TextValues implements Text.ITextValues {
		private int biblioType;
		private String biblioTitle;
		private String biblioAuthor;
		private String biblioCoAuthor;
		private String biblioSubtitle;
		private String biblioYear;
		private String biblioPublication;
		private String biblioPages;
		private String biblioVolume;
		private String biblioNumber;
		private String biblioPublisher;
		private String biblioPlace;
		private String biblioText;

		public TextValues(String inTitle, String inAuthor, String inCoAuthor, String inSubtitle, String inYear, String inPublication, String inPages, 
				String inVolume, String inNumber, String inPublisher, String inPlace, String inText, int inType) {
			biblioTitle = inTitle;
			biblioAuthor = inAuthor;
			biblioCoAuthor = inCoAuthor;
			biblioSubtitle = inSubtitle;
			biblioYear = inYear;
			biblioPublication = inPublication;
			biblioPages = inPages;
			biblioVolume = inVolume;
			biblioNumber = inNumber;
			biblioPublisher = inPublisher;
			biblioPlace = inPlace;
			biblioText = inText;
			biblioType = inType;
		}

		public String getBiblioAuthor() {
			return biblioAuthor;
		}
		public String getBiblioCoAuthor() {
			return biblioCoAuthor;
		}
		public String getBiblioNumber() {
			return biblioNumber;
		}
		public String getBiblioPages() {
			return biblioPages;
		}
		public String getBiblioPlace() {
			return biblioPlace;
		}
		public String getBiblioPublication() {
			return biblioPublication;
		}
		public String getBiblioPublisher() {
			return biblioPublisher;
		}
		public String getBiblioSubtitle() {
			return biblioSubtitle;
		}
		public String getBiblioText() {
			return biblioText;
		}
		public String getBiblioTitle() {
			return biblioTitle;
		}
		public Long getBiblioType() {
			return new Long(biblioType);
		}
		public String getBiblioVolume() {
			return biblioVolume;
		}
		public String getBiblioYear() {
			return biblioYear;
		}
	}
	
// ---
	
	private void createEmbeddedTables() throws IOException, SAXException, ParserConfigurationException, SQLException {
		DefaultStatement lSQL = new DefaultStatement();
		for (String lStatement : getStatements()) {
			lSQL.execute(lStatement);
		}
	}
	
	private Collection<String> getStatements() throws IOException, SAXException, ParserConfigurationException {
		StatementsFileParser lParser = new TestStatementsFileParser();
		return lParser.getStatements();
	}		

}

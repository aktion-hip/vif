package org.hip.vif.web.bom;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.hip.kernel.bom.impl.DefaultStatement;
import org.hip.kernel.dbaccess.DBAccessConfiguration;
import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.hip.vif.core.TestDataSourceFactoryDerby;
import org.hip.vif.core.TestDataSourceFactoryMySQL;
import org.hip.vif.core.TestStatementsFileParser;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.exc.VIFException;
import org.hip.vif.core.util.StatementsFileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/** Utility class for testing purpose. Creating and deleting of entries in viftest-DB.<br />
 * Note: had to adjust the MySQL configuration my.ini: max_connections=1024
 *
 * @author: Benno Luthiger */
public class DataHouseKeeper {
    private static final Logger LOG = LoggerFactory.getLogger(DataHouseKeeper.class);
    private static final int SLEEP_PERIOD = 50; // milliseconds 50 200
    private static final String EMBEDDED_DERBY = "org.apache.derby.jdbc.EmbeddedDriver/Derby (embedded)/10.5.1.1";

    private static DataHouseKeeper singleton = new DataHouseKeeper();

    // constants
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

    private boolean isEmbeddedDerby = false;

    /** Constructor for DataHouseKeeper.
     *
     * @throws IOException */
    private DataHouseKeeper() {
        initMySQL();
        // initDerbyEmbedded();
    }

    public void reInitialize() {
        if (isEmbeddedDerby) {
            initDerbyEmbedded();
        }
        else {
            initMySQL();
        }
    }

    /** @return boolean <code>true</code> if the configured connection setting is for an embedded database */
    public boolean isEmbedded() {
        return isEmbeddedDerby;
    }

    private void initMySQL() {
        try {
            DataSourceRegistry.INSTANCE.setFactory(new TestDataSourceFactoryMySQL());
            DataSourceRegistry.INSTANCE.setActiveConfiguration(createDBAccessConfiguration());

            VSys.setContextPath(new File("").getAbsolutePath());
        } catch (final IOException exc) {
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
        } catch (final Exception exc) {
            LOG.error("Could not initialize the DataHouseKeeper for Derby!", exc);
        }
    }

    private DBAccessConfiguration createDBAccessConfiguration() throws IOException {
        InputStream lStream = null;
        try {
            lStream = DataHouseKeeper.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
            final Properties lProperties = new Properties();
            lProperties.load(lStream);
            lStream.close();
            return new DBAccessConfiguration(lProperties.getProperty("org.hip.vif.db.driver"),
                    lProperties.getProperty("org.hip.vif.db.server"),
                    lProperties.getProperty("org.hip.vif.db.schema"),
                    lProperties.getProperty("org.hip.vif.db.userId"),
                    lProperties.getProperty("org.hip.vif.db.password"));
        } finally {
            if (lStream != null) {
                lStream.close();
            }
        }
    }

    private DBAccessConfiguration createDBAccessConfigurationEmbedded() {
        final File lSchema = new File(".");
        return new DBAccessConfiguration(EMBEDDED_DERBY, "", new File(lSchema, "data/vif_data").getAbsolutePath(), "",
                "");
    }

    public static DataHouseKeeper getInstance() {
        return singleton;
    }

    public GroupHome getGroupHome() {
        return VifBOMHelper.getGroupHome();
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
    void deleteAllFrom(final String inTableName) throws SQLException, VException, InterruptedException {
        final Connection lConnection = DataSourceRegistry.INSTANCE.getConnection();
        final Statement lStatement = lConnection.createStatement();
        lStatement.execute("DELETE FROM " + inTableName);
        // lConnection.commit();
        lStatement.close();
        lConnection.close();
        if (!isEmbeddedDerby) {
            Thread.currentThread().sleep(SLEEP_PERIOD);
        }
    }

    public String getExpectedName() {
        return DFT_NAME;
    }

    public String getExpectedUserID() {
        return MEMBER_USER_ID;
    }

    private Long createGroup(final String inNr) throws BOMChangeValueException, VIFException {
        try {
            return getGroupHome().createNew(GROUP_ID + inNr, "Group Nr. " + inNr, "1", "3", "10", false);
        } catch (final VException exc) {
            throw new BOMChangeValueException(exc.getMessage());
        } catch (final SQLException exc) {
            throw new BOMChangeValueException(exc.getMessage());
        }
    }

    /** Creates one group and returns its id
     *
     * @return java.lang.String
     * @throws BOMChangeValueException
     * @throws VIFException */
    public Long createGroup() throws BOMChangeValueException, VIFException {
        return createGroup("1");
    }

    /** Creates two groups and returns their ids
     *
     * @return java.lang.String[]
     * @throws BOMChangeValueException
     * @throws VIFException */
    public Long[] create2Groups() throws BOMChangeValueException, VIFException {
        final Long[] outIDs = new Long[2];
        outIDs[0] = createGroup("1");
        outIDs[1] = createGroup("2");
        return outIDs;
    }

    /** Creates five groups and returns their ids
     *
     * @return java.lang.String[]
     * @throws BOMChangeValueException
     * @throws VIFException */
    public Long[] create5Groups() throws BOMChangeValueException, VIFException {
        final Long[] outIDs = new Long[5];
        outIDs[0] = createGroup("1");
        outIDs[1] = createGroup("2");
        outIDs[2] = createGroup("3");
        outIDs[3] = createGroup("4");
        outIDs[4] = createGroup("5");
        return outIDs;
    }

    // ---

    private void createEmbeddedTables() throws IOException, SAXException, ParserConfigurationException, SQLException {
        final DefaultStatement lSQL = new DefaultStatement();
        for (final String lStatement : getStatements()) {
            lSQL.execute(lStatement);
        }
    }

    private Collection<String> getStatements() throws IOException, SAXException, ParserConfigurationException {
        final StatementsFileParser lParser = new TestStatementsFileParser();
        return lParser.getStatements();
    }
}

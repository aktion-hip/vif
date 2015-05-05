package org.hip.vif.web.bom;

import java.sql.SQLException;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.bom.TextAuthorReviewerHome;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;

/** Home of join from the text-author/reviewer BOM to the text BOM.<br/>
 * This home can be used to retrieve all text entries authored/reviewed by a given person.<br/>
 * Note: Text entries are versioned and authors/reviewers are linked to a specific version of a text entry.
 *
 * @author Luthiger */
@SuppressWarnings("serial")
public class JoinAuthorReviewerToTextHome extends JoinedDomainObjectHomeImpl { // NOPMD
    private final static String OBJECT_CLASS_NAME = "org.hip.vif.web.bom.JoinAuthorReviewerToText";

    private final static String XML_OBJECT_DEF = "<?xml version='1.0' encoding='ISO-8859-1'?>	"
            + "<joinedObjectDef objectName='JoinAuthorReviewerToText' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	"
            + "	<columnDefs>	" + "		<columnDef columnName='" // NOPMD
            + ResponsibleHome.KEY_MEMBER_ID
            + "' domainObject='org.hip.vif.core.bom.impl.TextAuthorReviewerImpl'/>	\n"
            + "		<columnDef columnName='"
            + ResponsibleHome.KEY_TYPE
            + "' domainObject='org.hip.vif.core.bom.impl.TextAuthorReviewerImpl'/>	\n"
            + "		<columnDef columnName='"
            + TextHome.KEY_ID
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	\n" // NOPMD
            + "		<columnDef columnName='"
            + TextHome.KEY_VERSION
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	\n"
            + "		<columnDef columnName='"
            + TextHome.KEY_TITLE
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	\n"
            + "		<columnDef columnName='"
            + TextHome.KEY_AUTHOR
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	\n"
            + "		<columnDef columnName='"
            + TextHome.KEY_COAUTHORS
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	\n"
            + "		<columnDef columnName='"
            + TextHome.KEY_SUBTITLE
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	\n"
            + "		<columnDef columnName='"
            + TextHome.KEY_PUBLICATION
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	\n"
            + "		<columnDef columnName='"
            + TextHome.KEY_YEAR
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	\n"
            + "		<columnDef columnName='"
            + TextHome.KEY_PAGES
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	\n"
            + "		<columnDef columnName='"
            + TextHome.KEY_VOLUME
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	\n"
            + "		<columnDef columnName='"
            + TextHome.KEY_NUMBER
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	\n"
            + "		<columnDef columnName='"
            + TextHome.KEY_PUBLISHER
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	\n"
            + "		<columnDef columnName='"
            + TextHome.KEY_PLACE
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	\n"
            + "		<columnDef columnName='"
            + TextHome.KEY_STATE
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	\n"
            + "		<columnDef columnName='"
            + TextHome.KEY_REFERENCE
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	\n"
            + "		<columnDef columnName='"
            + TextHome.KEY_REMARK
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	\n"
            + "		<columnDef columnName='"
            + TextHome.KEY_FROM
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	\n"
            + "		<columnDef columnName='"
            + TextHome.KEY_TYPE
            + "' as='"
            + TextHome.KEY_BIBLIO_TYPE
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	\n"
            + "	</columnDefs>	"
            + "	<joinDef joinType='EQUI_JOIN'>	"
            + "		<objectDesc objectClassName='org.hip.vif.web.bom.impl.TextImpl'/>	"
            + "		<objectDesc objectClassName='org.hip.vif.core.bom.impl.TextAuthorReviewerImpl'/>	"
            + "		<joinCondition>	"
            + "			<columnDef columnName='" // NOPMD
            + TextHome.KEY_ID
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	"
            + "			<columnDef columnName='"
            + TextAuthorReviewerHome.KEY_TEXT_ID
            + "' domainObject='org.hip.vif.core.bom.impl.TextAuthorReviewerImpl'/>	"
            + "		</joinCondition>	"
            + "		<joinCondition operatorType='AND'>	"
            + "			<columnDef columnName='"
            + TextHome.KEY_VERSION
            + "' domainObject='org.hip.vif.web.bom.impl.TextImpl'/>	"
            + "			<columnDef columnName='"
            + TextAuthorReviewerHome.KEY_VERSION
            + "' domainObject='org.hip.vif.core.bom.impl.TextAuthorReviewerImpl'/>	"
            + "		</joinCondition>	" + "	</joinDef>	" + "</joinedObjectDef>";

    /** Returns the name of the objects which this home can create.
     *
     * @return java.lang.String */
    @Override
    public String getObjectClassName() {
        return OBJECT_CLASS_NAME;
    }

    /** Returns the object definition string of the class managed by this home.
     *
     * @return java.lang.String */
    @Override
    protected String getObjectDefString() {
        return XML_OBJECT_DEF;
    }

    /** Returns the author's unpublished text entries.
     *
     * @param inMemberID Long
     * @return {@link QueryResult}
     * @throws VException
     * @throws SQLException */
    public QueryResult getAuthorsUnpublishedTexts(final Long inMemberID)
            throws VException, SQLException {
        return getAuthorsTexts(inMemberID, WorkflowAwareContribution.STATES_UNPUBLISHED);
    }

    /** Returns the reviewer's unpublished text entries.
     *
     * @param inMemberID Long
     * @return {@link QueryResult}
     * @throws VException
     * @throws SQLException */
    public QueryResult getReviewersUnpublishedTexts(final Long inMemberID)
            throws VException, SQLException {
        return getReviewersTexts(inMemberID,
                WorkflowAwareContribution.STATES_UNPUBLISHED);
    }

    private QueryResult getAuthorsTexts(final Long inMemberID,
            final Integer... inState) throws VException, SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(ResponsibleHome.KEY_TYPE,
                ResponsibleHome.Type.AUTHOR.getValue());
        return getTextsResponsibleFor(lKey, inMemberID, inState);
    }

    private QueryResult getReviewersTexts(final Long inMemberID,
            final Integer... inState) throws VException, SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(ResponsibleHome.KEY_TYPE,
                ResponsibleHome.Type.REVIEWER.getValue());
        return getTextsResponsibleFor(lKey, inMemberID, inState);
    }

    private QueryResult getTextsResponsibleFor(final KeyObject inKey,
            final Long inMemberID, final Integer... inState) throws VException,
            SQLException {
        inKey.setValue(ResponsibleHome.KEY_MEMBER_ID, inMemberID);
        inKey.setValue(BOMHelper.getKeyStates(TextHome.KEY_STATE, inState));
        return select(inKey);
    }

    /** Returns the published version of the text entry identified by the specified id.
     *
     * @param inTextID Long
     * @return {@link PublishedText}
     * @throws VException
     * @throws SQLException */
    public PublishedText getTextPublished(final Long inTextID)
            throws VException, SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(TextHome.KEY_ID, inTextID);
        lKey.setValue(BOMHelper.getKeyPublished(TextHome.KEY_STATE),
                BinaryBooleanOperator.AND);
        return new PublishedText(select(lKey));
    }

    // ---

    /** Parameter object containing the bibliography entry and the author/reviewer of this entry. */
    public static class PublishedText {
        private GeneralDomainObject text;
        private Long authorID;
        private Long reviewerID;

        PublishedText(final QueryResult inResult) throws VException,
                SQLException {
            while (inResult.hasMoreElements()) {
                text = inResult.nextAsDomainObject();
                if (ResponsibleHome.Type.REVIEWER.check(text
                        .get(ResponsibleHome.KEY_TYPE))) {
                    reviewerID = Long.parseLong(text.get(
                            ResponsibleHome.KEY_MEMBER_ID).toString());
                } else {
                    authorID = Long.parseLong(text.get(
                            ResponsibleHome.KEY_MEMBER_ID).toString());
                }
            }
        }

        /** @return {@link GeneralDomainObject} the text entry */
        public GeneralDomainObject getText() {
            return text;
        }

        public Long getAuthorID() { // NOPMD
            return authorID;
        }

        public Long getReviewerID() { // NOPMD
            return reviewerID;
        }
    }

}

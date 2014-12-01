/**
 This package is part of the application VIF.
 Copyright (C) 2010-2014, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.web.bom;

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.impl.TextQuestionHome;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;

/** Home to retrieve all text entries linked to a given question.
 *
 * @author Luthiger Created: 28.07.2010 */
@SuppressWarnings("serial")
public class JoinQuestionToTextHome extends JoinedDomainObjectHomeImpl {
    private final static String OBJECT_CLASS_NAME = "org.hip.vif.web.bom.JoinQuestionToText";

    private final static String XML_OBJECT_DEF =
            "<?xml version='1.0' encoding='ISO-8859-1'?>	"
                    +
                    "<joinedObjectDef objectName='JoinQuestionToText' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	"
                    +
                    "	<columnDefs>	" +
                    "		<columnDef columnName='"
                    + TextQuestionHome.KEY_QUESTIONID
                    + "' domainObject='org.hip.vif.core.bom.impl.TextQuestion'/>	"
                    +
                    "		<columnDef columnName='"
                    + TextHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + TextHome.KEY_VERSION
                    + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + TextHome.KEY_TITLE
                    + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + TextHome.KEY_AUTHOR
                    + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + TextHome.KEY_COAUTHORS
                    + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + TextHome.KEY_SUBTITLE
                    + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + TextHome.KEY_YEAR
                    + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + TextHome.KEY_PUBLICATION
                    + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + TextHome.KEY_PAGES
                    + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + TextHome.KEY_VOLUME
                    + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + TextHome.KEY_NUMBER
                    + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + TextHome.KEY_PUBLISHER
                    + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + TextHome.KEY_PLACE
                    + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + TextHome.KEY_TYPE
                    + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + TextHome.KEY_STATE
                    + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + TextHome.KEY_REFERENCE
                    + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + TextHome.KEY_TYPE
                    + "' as='"
                    + TextHome.KEY_BIBLIO_TYPE
                    + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	\n"
                    +
                    "	</columnDefs>	"
                    +
                    "	<joinDef joinType='EQUI_JOIN'>	"
                    +
                    "		<objectDesc objectClassName='org.hip.vif.core.bom.impl.TextQuestion'/>	"
                    +
                    "		<objectDesc objectClassName='org.hip.vif.core.bom.impl.TextImpl'/>	"
                    +
                    "		<joinCondition>	"
                    +
                    "			<columnDef columnName='"
                    + TextQuestionHome.KEY_TEXTID
                    + "' domainObject='org.hip.vif.core.bom.impl.TextQuestion'/>	"
                    +
                    "			<columnDef columnName='"
                    + TextHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	" +
                    "		</joinCondition>	" +
                    "	</joinDef>	" +
                    "</joinedObjectDef>";

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

    /** Returns the published bibliography entries linked to the specified question.
     *
     * @param inQuestionID Long
     * @return QueryResult */
    public QueryResult selectPublished(final Long inQuestionID) throws VException, SQLException {
        return select(inQuestionID, WorkflowAwareContribution.STATES_PUBLISHED);
    }

    /** Returns the bibliography entries being in the specified states belonging to the specified question.
     *
     * @param inQuestionID Long
     * @param inState Integer[]
     * @return QueryResult
     * @throws VException
     * @throws SQLException */
    public QueryResult select(final Long inQuestionID, final Integer[] inState) throws VException, SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(TextQuestionHome.KEY_QUESTIONID, inQuestionID);
        lKey.setValue(BOMHelper.getKeyStates(TextHome.KEY_STATE, inState));
        final OrderObject lOrder = new OrderObjectImpl();
        lOrder.setValue(TextHome.KEY_REFERENCE, 0);
        return select(lKey, lOrder);
    }

}

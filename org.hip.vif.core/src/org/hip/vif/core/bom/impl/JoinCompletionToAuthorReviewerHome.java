/**
    This package is part of the application VIF.
    Copyright (C) 2014, Benno Luthiger

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.core.bom.impl;

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.CompletionAuthorReviewerHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.ResponsibleHome;

/** Home of join from the completion-author/reviewer BOM to the member BOM. This home can be used to retrieve the member
 * data of all authors or reviewers responsible for a specified completion.
 *
 * @author lbenno */
@SuppressWarnings("serial")
public class JoinCompletionToAuthorReviewerHome extends JoinedDomainObjectHomeImpl { // NOPMD
    private final static String JOIN_OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinCompletionToAuthorReviewer";

    private final static String XML_OBJECT_DEF =
            "<?xml version='1.0' encoding='ISO-8859-1'?>    \n"
                    +
                    "<joinedObjectDef objectName='JoinCompletionToAuthorReviewer' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'> \n"
                    +
                    "   <columnDefs>    \n" +
                    "       <columnDef columnName='" // NOPMD
                    + CompletionAuthorReviewerHome.KEY_COMPLETION_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>  \n"
                    +
                    "       <columnDef columnName='"
                    + ResponsibleHome.KEY_TYPE
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>  \n"
                    +
                    "       <columnDef columnName='"
                    + MemberHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>  \n" // NOPMD
                    +
                    "       <columnDef columnName='"
                    + MemberHome.KEY_USER_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>  \n"
                    +
                    "       <columnDef columnName='"
                    + MemberHome.KEY_NAME
                    + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>  \n"
                    +
                    "       <columnDef columnName='"
                    + MemberHome.KEY_FIRSTNAME
                    + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>  \n"
                    +
                    "       <columnDef columnName='"
                    + MemberHome.KEY_STREET
                    + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>  \n"
                    +
                    "       <columnDef columnName='"
                    + MemberHome.KEY_ZIP
                    + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>  \n"
                    +
                    "       <columnDef columnName='"
                    + MemberHome.KEY_CITY
                    + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>  \n"
                    +
                    "       <columnDef columnName='"
                    + MemberHome.KEY_SEX
                    + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>  \n"
                    +
                    "       <columnDef columnName='"
                    + MemberHome.KEY_MAIL
                    + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>  \n"
                    +
                    "   </columnDefs>   \n"
                    +
                    "   <joinDef joinType='EQUI_JOIN'>  \n"
                    +
                    "       <objectDesc objectClassName='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>    \n"
                    +
                    "       <objectDesc objectClassName='org.hip.vif.core.bom.impl.MemberImpl'/>    \n"
                    +
                    "       <joinCondition> \n"
                    +
                    "           <columnDef columnName='"
                    + ResponsibleHome.KEY_MEMBER_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>  \n"
                    +
                    "           <columnDef columnName='"
                    + MemberHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>  \n" +
                    "       </joinCondition>    \n" +
                    "   </joinDef>  \n" +
                    "</joinedObjectDef>";

    @Override
    public String getObjectClassName() { // NOPMD
        return JOIN_OBJECT_CLASS_NAME;
    }

    @Override
    protected String getObjectDefString() { // NOPMD
        return XML_OBJECT_DEF;
    }

    /** @param inCompletionID String
     * @return {@link QueryResult} the specified completion' authors
     * @throws VException
     * @throws NumberFormatException
     * @throws SQLException */
    public QueryResult getAuthors(final String inCompletionID) throws VException, NumberFormatException, SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(CompletionAuthorReviewerHome.KEY_COMPLETION_ID, Long.parseLong(inCompletionID));
        lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        return select(lKey);
    }

    /** @param inCompletionID String
     * @return {@link QueryResult} the specified completion' reviewers
     * @throws VException
     * @throws NumberFormatException
     * @throws SQLException */
    public QueryResult getReviewers(final String inCompletionID) throws VException, NumberFormatException, SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(CompletionAuthorReviewerHome.KEY_COMPLETION_ID, Long.parseLong(inCompletionID));
        lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue());
        return select(lKey);
    }

}

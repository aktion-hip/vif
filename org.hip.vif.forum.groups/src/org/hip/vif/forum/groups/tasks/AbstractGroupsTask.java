/**
	This package is part of the application VIF.
	Copyright (C) 2011-2014, Benno Luthiger

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

package org.hip.vif.forum.groups.tasks;

import java.sql.SQLException;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.tasks.AbstractWebController;

/** Abstract base class for tasks in the groups bundle.
 *
 * @author Luthiger Created: 27.07.2011 */
public abstract class AbstractGroupsTask extends AbstractWebController {

    protected QueryResult getAuthors(final String inQuestionID) throws VException, SQLException {
        return BOMHelper.getJoinQuestionToAuthorReviewerHome().getAuthors(inQuestionID);
    }

    protected QueryResult getReviewers(final String inQuestionID) throws VException, SQLException {
        return BOMHelper.getJoinQuestionToAuthorReviewerHome().getReviewers(inQuestionID);
    }

    protected QueryResult getPublishedCompletions(final Long inQuestionID) throws NumberFormatException, VException,
            SQLException {
        return BOMHelper.getJoinCompletionToMemberHome().selectPublishedWithResponsibles(inQuestionID);
    }

    protected QueryResult getPublishedBibliography(final Long inQuestionID) throws VException, SQLException {
        return VifBOMHelper.getJoinQuestionToTextHome().selectPublished(inQuestionID);
    }

    /** Returns the collection of download files belonging to the specified text entry.
     *
     * @param inTextID Long the text entry's idea (ignoring the version part)
     * @return {@link QueryResult}
     * @throws VException
     * @throws SQLException */
    protected QueryResult getDownloads(final Long inTextID) throws VException, SQLException {
        return BOMHelper.getDownloadTextHome().getDownloads(inTextID);
    }

}

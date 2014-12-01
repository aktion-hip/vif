/**
This package is part of the administration of the application VIF.
Copyright (C) 2005-2014, Benno Luthiger

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.core.bom.search;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.impl.JoinCompletionForIndexHome;
import org.hip.vif.core.bom.impl.JoinQuestionForIndexHome;
import org.hip.vif.core.search.AbstractVIFIndexer;
import org.hip.vif.core.search.VIFIndexing;

/** Creates an index of the content for full text search using lucene.
 *
 * @author Benno Luthiger Created on 24.09.2005 */
public class VIFContentIndexer extends AbstractVIFIndexer {
    static final int limit = 10;

    @Override
    protected Integer[] doIndex(final IndexWriter inWriter) throws VException, SQLException, IOException {
        final Integer[] outIndexed = { null, null, null };

        outIndexed[0] = new Integer(indexQuestions(inWriter));
        outIndexed[1] = new Integer(indexCompletions(inWriter));
        outIndexed[2] = new Integer(indexTexts(inWriter));

        return outIndexed;
    }

    @Override
    protected int getLimit() {
        return limit;
    }

    private int indexQuestions(final IndexWriter inWriter) throws VException, SQLException, IOException {
        final JoinQuestionForIndexHome lHome = BOMHelper.getJoinQuestionForIndexHome();
        final KeyObject lKey = JoinQuestionForIndexHome.getKeyPublicAndAuthor(QuestionHome.KEY_STATE);

        return processSelection(inWriter, lHome, lKey);
    }

    private int indexCompletions(final IndexWriter inWriter) throws VException, SQLException, IOException {
        final JoinCompletionForIndexHome lHome = BOMHelper.getJoinCompletionForIndexHome();
        final KeyObject lKey = JoinCompletionForIndexHome.getKeyPublicAndAuthor(CompletionHome.KEY_STATE);

        return processSelection(inWriter, lHome, lKey);
    }

    private int indexTexts(final IndexWriter inWriter) throws VException, SQLException, IOException {
        return processSelection(inWriter, BOMHelper.getTextHome(), BOMHelper.getKeyPublished(TextHome.KEY_STATE));
    }

    /** Adds the question with the specified key to the full text search index. This method has to be called when a
     * question is published (see WorkflowAwareContribution).
     *
     * @param inQuestionID KeyObject with QuestionID as value.
     * @throws VException
     * @throws SQLException
     * @throws IOException
     * @see org.hip.vif.core.bom.impl.WorkflowAwareContribution#onTransition_Publish(Long) */
    public void addQuestionToIndex(final KeyObject inQuestionID) throws VException, SQLException, IOException {
        final JoinQuestionForIndexHome lHome = BOMHelper.getJoinQuestionForIndexHome();
        final KeyObject lKey = JoinQuestionForIndexHome.getKeyPublicAndAuthor(QuestionHome.KEY_STATE);
        lKey.setValue(inQuestionID, BinaryBooleanOperator.AND);
        addEntryToIndex(lHome, lKey);
    }

    /** Adds the completion with the specified key to the full text search index. This method has to be called when a
     * completion is published (see WorkflowAwareContribution).
     *
     * @param inCompletionID KeyObject with CompletionID as value.
     * @throws VException
     * @throws SQLException
     * @throws IOException
     * @see org.hip.vif.core.bom.impl.WorkflowAwareContribution#onTransition_Publish(Long) */
    public void addCompletionToIndex(final KeyObject inCompletionID) throws VException, SQLException, IOException {
        final JoinCompletionForIndexHome lHome = BOMHelper.getJoinCompletionForIndexHome();
        final KeyObject lKey = JoinCompletionForIndexHome.getKeyPublicAndAuthor(CompletionHome.KEY_STATE);
        lKey.setValue(inCompletionID);
        addEntryToIndex(lHome, lKey);
    }

    /** Adds the bibliography entry with the specified key to the full text search index. This method has to be called
     * when a bibliography entry is published (see WorkflowAwareContribution).
     *
     * @param inTextID {@link KeyObject} with TextID as value.
     * @throws SQLException
     * @throws VException
     * @throws IOException */
    public void addTextToIndex(final KeyObject inTextID) throws IOException, VException, SQLException {
        final KeyObject lKey = BOMHelper.getKeyPublished(TextHome.KEY_STATE);
        lKey.setValue(inTextID, BinaryBooleanOperator.AND);
        addEntryToIndex(BOMHelper.getTextHome(), lKey);
    }

    /** Adds the specified group's content to the search index.
     *
     * @param inGroupID String
     * @throws VException
     * @throws NumberFormatException
     * @throws IOException
     * @throws SQLException */
    public void addGroupContentToIndex(final String inGroupID) throws VException, NumberFormatException, IOException,
    SQLException {
        final KeyObject lGroupID = new KeyObjectImpl();
        lGroupID.setValue(QuestionHome.KEY_GROUP_ID, new Long(inGroupID));

        // add questions
        final JoinQuestionForIndexHome lQuestionHome = BOMHelper.getJoinQuestionForIndexHome();
        KeyObject lKey = JoinQuestionForIndexHome.getKeyPublicAndAuthor(QuestionHome.KEY_STATE);
        lKey.setValue(lGroupID, BinaryBooleanOperator.AND);
        addEntryToIndex(lQuestionHome, lKey);

        // add completions
        final JoinCompletionForIndexHome lCompletionHome = BOMHelper.getJoinCompletionForIndexHome();
        lKey = JoinCompletionForIndexHome.getKeyPublicAndAuthor(CompletionHome.KEY_STATE);
        lKey.setValue(lGroupID);
        addEntryToIndex(lCompletionHome, lKey);
    }

    /** Removed the content of the specified group from the search index.
     *
     * @param inGroupID String
     * @throws VException
     * @throws SQLException
     * @throws IOException */
    public void removeGroupContent(final String inGroupID) throws VException, SQLException, IOException {
        final OrderObject lOrder = new OrderObjectImpl();
        lOrder.setValue(GroupHome.KEY_ID, 0);
        final QuestionHome lHome = BOMHelper.getQuestionHome();
        final QueryResult lQuestions = lHome.selectOfGroup(inGroupID, lOrder);
        while (lQuestions.hasMoreElements()) {
            deleteQuestionInIndex(lQuestions.next().get(QuestionHome.KEY_ID).toString());
        }
    }

    /** Deletes the document with the specified question id from the full text search index.
     *
     * @param inQuestionID String
     * @throws IOException */
    public void deleteQuestionInIndex(final String inQuestionID) throws IOException {
        deleteEntryInIndex(new Term(IndexField.CONTRIBUTION_ID.fieldName, inQuestionID));
    }

    /** Deletes the document with the specified text (bibliography) id from the full text search index.
     *
     * @param inTextID String
     * @throws IOException */
    public void deleteTextInIndex(final String inTextID) throws IOException {
        deleteEntryInIndex(new Term(IndexField.BIBLIO_ID.fieldName, inTextID));
    }

    @Override
    protected IndexWriter getIndexWriter(final boolean inCreate) throws CorruptIndexException, IOException {
        return VIFIndexing.INSTANCE.getContentIndexWriter(inCreate);
    }

    @Override
    protected void afterChange() throws IOException {
        VIFIndexing.INSTANCE.refreshContentIndexReader();
    }

}

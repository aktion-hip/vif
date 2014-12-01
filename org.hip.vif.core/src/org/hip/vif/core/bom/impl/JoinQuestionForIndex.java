/*
 This package is part of the persistency layer of the application VIF.
 Copyright (C) 2005, Benno Luthiger

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
package org.hip.vif.core.bom.impl;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.search.AbstractSearching;
import org.hip.vif.core.search.FullTextHelper;
import org.hip.vif.core.search.Indexable;

/** Joined domain object containing the information relevant for indexation.
 *
 * @author Benno Luthiger Created on 26.09.2005 */
@SuppressWarnings("serial")
public class JoinQuestionForIndex extends DomainObjectImpl implements Indexable {

    public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinQuestionForIndexHome";

    /** JoinQuestionForIndex constructor. */
    public JoinQuestionForIndex() {
        super();
    }

    /** This Method returns the class name of the home.
     *
     * @return java.lang.String */
    @Override
    public String getHomeClassName() {
        return HOME_CLASS_NAME;
    }

    /** @throws IOException
     * @throws VException
     * @see Indexable#indexContent(IndexWriter) */
    @Override
    public void indexContent(final IndexWriter inWriter) throws IOException, VException {
        final FullTextHelper lFullText = new FullTextHelper();
        final Document lDocument = new Document();
        lDocument.add(AbstractSearching.IndexField.CONTRIBUTION_ID.createField(get(QuestionHome.KEY_ID).toString()));
        lDocument.add(AbstractSearching.IndexField.AUTHOR_NAME.createField(lFullText.add(get(MemberHome.KEY_FIRSTNAME)
                .toString() + " " + get(MemberHome.KEY_NAME).toString())));
        lDocument.add(AbstractSearching.IndexField.DECIMAL_ID.createField(get(QuestionHome.KEY_QUESTION_DECIMAL)
                .toString()));
        lDocument.add(AbstractSearching.IndexField.QUESTION_TEXT.createField(lFullText.add(get(
                QuestionHome.KEY_QUESTION).toString())));
        lFullText.add(get(QuestionHome.KEY_REMARK).toString());
        lDocument.add(AbstractSearching.IndexField.GROUP_ID.createField(get(QuestionHome.KEY_GROUP_ID).toString()));
        lDocument.add(AbstractSearching.IndexField.GROUP_NAME.createField(get(JoinQuestionForIndexHome.JOIN_GROUP_NAME)
                .toString()));
        lDocument.add(AbstractSearching.IndexField.CONTENT_FULL.createField(lFullText.getFullText()));
        synchronized (this) {
            inWriter.addDocument(lDocument);
        }
    }
}

/**
	This package is part of the application VIF.
	Copyright (C) 2004-2014, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public
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

package org.hip.vif.core.util;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHierarchyHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.impl.QuestionHierarchyVisitor;

/** Iterator climbing the Questions hierarchy up to the root.
 *
 * @author Benno Luthiger Created on Feb 15, 2004 */
public class QuestionTreeIterator {
    private Long startID = null;
    private final Collection<Long> memory = new Vector<Long>();

    /** QuestionTreeIterator constructor */
    public QuestionTreeIterator() {
        super();
    }

    /** QuestionTreeIterator constructor with specified ID of the node the iterator has to start.
     *
     * @param inStartID Long */
    public QuestionTreeIterator(final Long inStartID) {
        super();
        startID = inStartID;
    }

    /** Starts the iteration at the specified node id.
     *
     * @param inStartID Long ID of the node the iteration starts.
     * @param inStartHere boolean true if the iterator has to process the start node too, false if processing begins at
     *            parent node.
     * @param inVisitor DomainObjectVisitor used for processing the iterated nodes.
     * @throws VException
     * @throws SQLException */
    public void start(final Long inStartID, final boolean inStartHere, final QuestionHierarchyVisitor inVisitor)
            throws VException, SQLException {
        startID = inStartID;
        start(inStartHere, inVisitor);
    }

    /** Starts the iteration.
     *
     * @param inStartHere boolean true if the iterator has to process the start node too, false if processing begins at
     *            parent node.
     * @param inVisitor DomainObjectVisitor used for processing the iterated nodes.
     * @throws VException
     * @throws SQLException */
    public void start(final boolean inStartHere, final QuestionHierarchyVisitor inVisitor) throws VException,
            SQLException {
        // iterate only with defined start id
        if (startID == null) {
            return;
        }

        Long lCurrent = startID;
        if (inStartHere) {
            if (iteratedNodeBefore(lCurrent)) {
                return;
            }

            final QuestionHierarchyEntry lQuestion = (QuestionHierarchyEntry) BOMHelper.getQuestionHome().getQuestion(
                    lCurrent.toString());
            lQuestion.accept(inVisitor);
            memory.add(lCurrent);
        }
        final QuestionHierarchyHome lHierarchyHome = BOMHelper.getQuestionHierarchyHome();
        while (lHierarchyHome.hasParent(lCurrent)) {
            final Question lQuestion = lHierarchyHome.getParentQuestion(lCurrent);
            lCurrent = new Long(lQuestion.get(QuestionHome.KEY_ID).toString());
            if (iteratedNodeBefore(lCurrent)) {
                return;
            }

            ((QuestionHierarchyEntry) lQuestion).accept(inVisitor);
            memory.add(lCurrent);
        }
    }

    /** Starts the iteration using INodeCheckedProcessor. This visitor doesn't visit the questions found, instead, it
     * processes them.
     *
     * @param inStartHere boolean true if the iterator has to process the start node too, false if processing begins at
     *            parent node.
     * @param inVisitor IQuestionLevelVisitor
     * @return Long ID of the last node processed, <code>null</code> if no node processed.
     * @throws WorkflowException
     * @throws VException
     * @throws SQLException */
    public Long start(final boolean inStartHere, final INodeCheckedProcessor inVisitor) throws WorkflowException,
            VException, SQLException {
        Long lPreceding = null;

        // iterate only with defined start id
        if (startID == null) {
            return lPreceding;
        }

        Long lCurrent = startID;
        if (inStartHere) {
            if (!inVisitor.checkPreCondition(lCurrent)) {
                return lPreceding;
            }
            lPreceding = lCurrent;
            inVisitor.doAction(lCurrent);
        }

        final QuestionHierarchyHome lHierarchyHome = BOMHelper.getQuestionHierarchyHome();
        while (lHierarchyHome.hasParent(lCurrent)) {
            lCurrent = new Long(lHierarchyHome.getParent(lCurrent).get(QuestionHierarchyHome.KEY_PARENT_ID).toString());
            if (!inVisitor.checkPreCondition(lCurrent)) {
                return lPreceding;
            }
            lPreceding = lCurrent;
            inVisitor.doAction(lCurrent);
        }
        return lPreceding;
    }

    private boolean iteratedNodeBefore(final Long inIDtoTest) {
        return memory.contains(inIDtoTest);
    }
}

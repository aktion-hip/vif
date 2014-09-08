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
package org.hip.vif.core.adapters;

import java.sql.SQLException;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.impl.QuestionHierarchyVisitor;
import org.hip.vif.core.util.QuestionHierarchyEntry;

/** Adapts a <code>VIFWorkflowAware</code> entry to a <code>QuestionHierarchyEntry</code>.
 *
 * @author Luthiger Created: 19.10.2010 */
public class QuestionHierachyAdapter implements QuestionHierarchyEntry {
    private QuestionHierarchyEntry entry;

    public QuestionHierachyAdapter(final Object inContribution) {
        if (inContribution instanceof QuestionHierarchyEntry) {
            entry = (QuestionHierarchyEntry) inContribution;
        }
    }

    @Override
    public void accept(final QuestionHierarchyVisitor inVisitor) throws VException, SQLException {
        if (entry != null) {
            entry.accept(inVisitor);
        }
    }

}

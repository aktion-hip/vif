/**
	This package is part of the application VIF
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

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.impl.QuestionHierarchyVisitor;

/** Interface for an entry in the QuestionHierarchy.
 *
 * @author Benno Luthiger Created on Mar 12, 2004 */
public interface QuestionHierarchyEntry {

    /** Sets the specified visitor (see visitor pattern).
     * 
     * @param inVisitor QuestionHierarchyVisitor
     * @throws VException
     * @throws SQLException */
    public void accept(QuestionHierarchyVisitor inVisitor) throws VException, SQLException;
}

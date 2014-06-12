/*
 This package is part of the application VIF.
 Copyright (C) 2008, Benno Luthiger

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
package org.hip.vif.core.bom.impl;

import java.sql.SQLException;

import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.EmptyQueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;

/**
 * Special home class for use with Derby database, overrides method <code>selectOfGroup()</code> returning an empty <code>QueryResult</code>.
 *
 * @author Luthiger
 * Created: 22.10.2009
 */
@SuppressWarnings("serial")
public class QuestionForGuestsDerbyHome extends QuestionForGuestsHome {

	@Override
	public QueryResult selectOfGroup(Long inGroupID, Long inGuestDepth, OrderObject inOrder) throws VException, SQLException {
		return new EmptyQueryResult(BOMHelper.getQuestionHome());
	}
}

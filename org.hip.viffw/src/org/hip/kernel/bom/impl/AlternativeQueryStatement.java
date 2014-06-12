package org.hip.kernel.bom.impl;

/*
	This package is part of the servlet framework used for the application VIF.
	Copyright (C) 2004, Benno Luthiger
	
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

import java.sql.ResultSet;

import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.QueryStatement;

/**
 * An alternative QueryStatement.
 * The only purpose of this QueryStatement is to create and return an 
 * AlternativeQueryResult in the method createQueryResult().
 * 
 * @see AlternativeQueryResult
 * @author Benno Luthiger
 * Created on Sep 23, 2004
 */
public class AlternativeQueryStatement extends AbstractQueryStatement {

	/**
	 * @param inHome GeneralDomainObjectHome
	 */
	public AlternativeQueryStatement(GeneralDomainObjectHome inHome) {
		super(inHome);
	}

	/**
	 * Creates an AlternativeQueryResult.
	 * 
	 * @param inHome GeneralDomainObjectHome
	 * @param inResult ResultSet
	 * @param inStatement QueryStatement
	 * @return QueryResult
	 */
	protected QueryResult createQueryResult(GeneralDomainObjectHome inHome, ResultSet inResult, QueryStatement inStatement) {
		return new AlternativeQueryResult(inHome, inResult, inStatement);
	}
}

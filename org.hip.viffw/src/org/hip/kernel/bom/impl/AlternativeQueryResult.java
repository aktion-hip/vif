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
import org.hip.kernel.bom.QueryStatement;

/**
 * An alternative QueryResult.
 * The only purpose of this QueryResult is to signal the super class
 * that the result of the query shall be loaded into a Collection.
 * This is done by letting the method isCollectionLoading() return 
 * the value <code>true</code>.   
 * 
 * @author Benno Luthiger
 * Created on Sep 23, 2004
 */
public class AlternativeQueryResult extends AbstractQueryResult {

	/**
	 * @param inHome GeneralDomainObjectHome
	 * @param inResult ResultSet
	 * @param inStatement QueryStatement
	 */
	public AlternativeQueryResult(GeneralDomainObjectHome inHome, ResultSet inResult, QueryStatement inStatement) {
		super(inHome, inResult, inStatement);
	}

	/**
	 * @param inHome GeneralDomainObjectHome
	 */
	public AlternativeQueryResult(GeneralDomainObjectHome inHome) {
		super(inHome);
	}
	
	/**
	 * @return boolean true, to signal the superclass that this QueryResult loads into a Collection.
	 */
	protected boolean isCollectionLoading() {
		return true;
	}
}

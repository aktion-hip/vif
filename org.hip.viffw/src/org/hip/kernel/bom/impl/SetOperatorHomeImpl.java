package org.hip.kernel.bom.impl;

/*
 This package is part of persistency framework used for the application VIF.
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

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.OrderItem;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.QueryStatement;
import org.hip.kernel.bom.SetOperatorHome;
import org.hip.kernel.bom.SingleValueQueryStatement;
import org.hip.kernel.sys.Assert;
import org.hip.kernel.sys.VObject;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.util.ListJoiner;
import org.hip.kernel.util.SortableItem;

/**
 * Implementation of the SetOperatorHome interface.
 * 
 * @author Benno Luthiger
 * @see org.hip.kernel.bom.SetOperatorHome
 * Created on Oct 31, 2004
 */
public class SetOperatorHomeImpl extends VObject implements SetOperatorHome {
	protected Collection<String> partsSelect = new Vector<String>();
	protected Collection<String> partsCount = new Vector<String>();
	private GeneralDomainObjectHome home;
	private int operatorType = 0;
	private final static String[] operators = {"UNION", "UNION ALL", "INTERSECT", "MINUS"};
	
	/**
	 * SetOperatorHomeImpl constructor.
	 */
	public SetOperatorHomeImpl(int inOperatorType) {
		super();
		operatorType = inOperatorType;
	}

	/**
	 * Add the set for the operation.
	 * 
	 * @param inHome GeneralDomainObjectHome
	 * @param inKey KeyObject
	 * @throws BOMException
	 */
	public void addSet(GeneralDomainObjectHome inHome, KeyObject inKey) throws BOMException {
		home = inHome;
		inHome.createSelectString(this, inKey);
	}
	/**
	 * @param inHome GeneralDomainObjectHome
	 * @param inKey KeyObject
	 * @param inOrder OrderObject
	 * @throws BOMException
	 */
	public void addSet(GeneralDomainObjectHome inHome, KeyObject inKey, OrderObject inOrder) throws BOMException {
		home = inHome;
		inHome.createSelectString(this, inKey, inOrder);
	}
	/**
	 * @param inHome GeneralDomainObjectHome
	 * @param inOrder OrderObject
	 * @throws BOMException
	 */
	public void addSet(GeneralDomainObjectHome inHome, OrderObject inOrder) throws BOMException {
		home = inHome;
		inHome.createSelectString(this, inOrder);
	}
	
	/**
	 * Sets the set's SQL select string.
	 * 
	 * @param inSQL String
	 */
	public void setSelectString(String inSQL) {
		partsSelect.add(inSQL);
	}
	
	/**
	 * Sets the set's SQL select count string.
	 * 
	 * @param inSQL String
	 */
	public void setCountString(String inSQL) {
		partsCount.add(inSQL);
	}
	
	/**
	 * Executes the set operation and returns the QueryResult.
	 * 
	 * @return org.hip.kernel.bom.QueryResult
	 * @param inOrder org.hip.kernel.bom.OrderObject
	 * @throws java.sql.SQLException
	 * @throws org.hip.kernel.bom.BOMException
	 */
	public QueryResult select(OrderObject inOrder) throws SQLException, BOMException {
		QueryStatement lStatement = home.createQueryStatement();
		lStatement.setSQLString(createSelect() + createOrderBy(inOrder));
		return home.select(lStatement);
	}
	
	/**
	 * Executes the set operation and returns the QueryResult.
	 * 
	 * @return org.hip.kernel.bom.QueryResult
	 * @throws java.sql.SQLException
	 * @throws org.hip.kernel.bom.BOMException
	 */
	public QueryResult select() throws SQLException, BOMException {
		QueryStatement lStatement = home.createQueryStatement();
		lStatement.setSQLString(createSelect());
		return home.select(lStatement);		
	}
	
	/**
	 * Returns the number of rows in the resulting set.
	 * 
	 * @return int number of rows.
	 * @throws SQLException
	 */
	public int getCount() throws SQLException {
		int outCount = 0;
		SingleValueQueryStatement lStatement = createSingleValueQueryStatement();
		for (String lPart : partsCount) {
			outCount += lStatement.executeQuery(lPart).intValue();
		}
		return outCount;
	}
	
	protected String createSelect() {
		ListJoiner lSelect = new ListJoiner();
		for (String lPart : partsSelect) {
			lSelect.addEntry(lPart, "(%1$s)", "");
		}
		return lSelect.joinSpaced("\n " + operators[operatorType]);
	}
	
	protected String createOrderBy(OrderObject inOrder) {
		// Pre: inOrder not null
		if (VSys.assertNotNull(this, "createOrderBy", inOrder) == Assert.FAILURE)
			 return "";
			 
		ListJoiner outSQL = new ListJoiner();
		for (SortableItem lItem : inOrder.getItems2()) {
			String lColumnName = home.getColumnNameFor(((OrderItem)lItem).getColumnName());
			outSQL.addEntry(lColumnName.substring(lColumnName.indexOf(".")+1),
					"%1$s%2$s",
					((OrderItem)lItem).isDescending() ? " DESC" : "");
		}
		return " ORDER BY " + outSQL.joinSpaced(",");
	}
	
	/**
	 * Creates a <code>SingleValueQueryStatement</code>.
	 * Subclasses may override, e.g. to provide a modified implementation of <code>SingleValueQueryStatement</code>.
	 * 
	 * @return SingleValueQueryStatement
	 */
	protected SingleValueQueryStatement createSingleValueQueryStatement() {
		return new SingleValueQueryStatementImpl();
	}	
	
}

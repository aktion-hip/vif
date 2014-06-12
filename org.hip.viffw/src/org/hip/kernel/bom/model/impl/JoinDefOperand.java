/*
	This package is part of the application VIF.
	Copyright (C) 2010, Benno Luthiger

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

package org.hip.kernel.bom.model.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * Helper class to render <code>leftOperand = rightOperand</code> in SQL JOIN statements.
 * 
 * @author Luthiger
 * Created: 04.08.2010
 */
public class JoinDefOperand implements Serializable {
	private static final String TMPL_TO_PREVIOUS = " %s ";
	private static final String TMPL_RENDER = "%s%s = %s";
	
	private String operatorType = ""; //AND | OR,  links this operand to previous operand, empty string if first operand
	private List<String> operands = new Vector<String>(2);
	
	/**
	 * Construction for first (and probably only) operand in join.
	 */
	public JoinDefOperand() {
		this("");
	}
	
	/**
	 * Constructor for subsequent operand that adds to previous operand with the specified operator type.
	 * 
	 * @param inOperatorType String <code>AND | OR</code>
	 */
	public JoinDefOperand(String inOperatorType) {
		operatorType = inOperatorType;
	}
	
	/**
	 * @param inJoinOperand String the table field the join should operate on. 
	 */
	public void setJoinOperand(String inJoinOperand) {
		operands.add(inJoinOperand);
	}
	
	/**
	 * Renders the SQL.
	 * 
	 * @return String the rendered SQL
	 */
	public String renderSQL() {
		String lOperateToPrevious = operatorType.length() == 0 ? "" : String.format(TMPL_TO_PREVIOUS, operatorType);
		return String.format(TMPL_RENDER, lOperateToPrevious, operands.get(0), operands.get(1));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((operands == null) ? 0 : operands.hashCode());
		result = prime * result + ((operatorType == null) ? 0 : operatorType.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JoinDefOperand other = (JoinDefOperand) obj;
		if (operands == null) {
			if (other.operands != null)
				return false;
		} else if (!operands.equals(other.operands))
			return false;
		if (operatorType == null) {
			if (other.operatorType != null)
				return false;
		} else if (!operatorType.equals(other.operatorType))
			return false;
		return true;
	}

}

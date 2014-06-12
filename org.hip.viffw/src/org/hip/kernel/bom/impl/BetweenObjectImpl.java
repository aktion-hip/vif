/*
	This package is part of the framework used for the application VIF.
	Copyright (C) 2006, Benno Luthiger

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

package org.hip.kernel.bom.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Key object to specify date ranges, e.g.
 * <code>BETWEEN DATE('1989-08-20') AND DATE('1999-04-24')</code>
 *
 * @author Luthiger
 * Created: 14.10.2006
 */
public class BetweenObjectImpl implements SQLRange {
	private String dateFunction = "DATE";
	private String from;
	private String to;

	/**
	 * BetweenObjectImpl constructor with dates.
	 * 
	 * @param inFrom Date
	 * @param inTo Date
	 */
	public BetweenObjectImpl(Date inFrom, Date inTo) {
		initialize(inFrom, inTo, new SimpleDateFormat("yyyy-MM-dd"));
	}
	
	/**
	 * BetweenObjectImpl constructor with timestamps.
	 * 
	 * @param inFrom Timestamp
	 * @param inTo Timestamp
	 */
	public BetweenObjectImpl(Timestamp inFrom, Timestamp inTo) {
		dateFunction = "TIMESTAMP";
		initialize(inFrom, inTo, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}
	
	private void initialize(java.util.Date inFrom, java.util.Date inTo, DateFormat inFormat) {
		if (inTo.after(inFrom)) {
			from = inFormat.format(inFrom);
			to = inFormat.format(inTo);			
		}
		else {
			from = inFormat.format(inTo);			
			to = inFormat.format(inFrom);
		}
	}
	
	/**
	 * @return String the representation in a prepared SQL command.
	 */
	public String toPrepared() {
		return "BETWEEN ? AND ?";
	}

	public String toString() {
		StringBuffer out = new StringBuffer("BETWEEN ");
		out.append(dateFunction).append("(\'");
		out.append(from);
		out.append("\') AND ");
		out.append(dateFunction).append("(\'");
		out.append(to);
		out.append("\')");
		return new String(out);
	}
}

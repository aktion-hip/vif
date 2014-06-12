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

import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

/**
 * Key object to specify value ranges, e.g.
 * <code>value IN (val1, val2, val3)</code>
 * 
 * @author Luthiger
 * Created: 14.10.2006
 */
public class InObjectImpl<T> implements SQLRange {
	private Collection<T> values;
	private ValueConverter converter;

	/**
	 * InObjectImpl constructor
	 * 
	 * @param inValues T[] the values that make up the range.
	 */
	public InObjectImpl(T[] inValues) {
		values = new Vector<T>();
		Collections.addAll(values, inValues);
	}
	/**
	 * InObjectImpl constructor
	 * 
	 * @param inValues {@link Collection} of the values that make up the range.
	 */
	public InObjectImpl(Collection<T> inValues) {
		values = inValues;
	}
	
	public String toString() {
		StringBuilder out = new StringBuilder("IN (");
		if (values.isEmpty()) {			
			return new String(out.append("0)"));
		}
		
		boolean lFirst = true;
		for (T lValue : values) {
			if (!lFirst) {
				out.append(", ");
			}
			lFirst = false;
			out.append(getConverter(lValue).convert(lValue));
		}
		out.append(")");
		return new String(out);
	}
	
	/**
	 * @return String the representation in a prepared SQL command.
	 */
	@SuppressWarnings("unused")
	public String toPrepared() {
		StringBuilder out = new StringBuilder("IN (");
		
		boolean lFirst = true;
		for (T lValue : values) {
			if (!lFirst) {
				out.append(", ");
			}
			lFirst = false;
			out.append("?");
		}
		out.append(")");
		return new String(out);
	}
	
	private ValueConverter getConverter(T inValue) {
		if (converter != null) return converter;

		if (inValue instanceof String) {
			return new StringValueConverter();
		}
		else if (inValue instanceof Number) {
			return new NumberValueConverter();
		}
		return new StringValueConverter();
	}
	
// ---
	
	private static interface ValueConverter {
		String convert(Object inValue);
	}
	
	private static class StringValueConverter implements ValueConverter {
		public String convert(Object inValue) {
			return String.format("\'%s\'", inValue.toString());
		}
	}
	private static class NumberValueConverter implements ValueConverter {
		public String convert(Object inValue) {
			return inValue.toString();
		}
	}

}

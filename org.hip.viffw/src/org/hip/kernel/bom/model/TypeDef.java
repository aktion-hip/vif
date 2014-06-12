/*
	This package is part of the servlet framework used for the application VIF.
	Copyright (C) 2001, Benno Luthiger

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

package org.hip.kernel.bom.model;

/**
 * 	This class provides access to type informations.
 * 
 * 	@author	Benno Luthiger
 */
public interface TypeDef extends ModelObject {

	// class variables
	public final static String String		= "String".intern();
	public final static String LongVarchar	= "LongVarchar".intern();
	public final static String Integer		= "Integer".intern();
	public final static String Long			= "Long".intern();
	public final static String Double		= "Double".intern();
	public final static String Boolean		= "Boolean".intern();
	public final static String Date			= "Date".intern();	
	public final static String Timestamp	= "Timestamp".intern();
	public final static String BigInteger	= "BigInteger".intern();
	public final static String BigDecimal	= "BigDecimal".intern();
	public final static String Number		= "Number".intern();
	public final static String Binary		= "Binary".intern();

	public final static String[][] valueTypes = {
		{Number, 	"java.lang.Number"},
		{Long, 		"java.lang.Long"},
		{String, 	"java.lang.String"},
		{Timestamp, "java.sql.Timestamp"},
		{Date, 		"java.sql.Date"},
		{Binary,	Binary}
	};
}
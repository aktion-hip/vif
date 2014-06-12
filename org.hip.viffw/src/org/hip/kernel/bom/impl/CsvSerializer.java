package org.hip.kernel.bom.impl;

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

import org.hip.kernel.bom.AbstractSerializer;
import org.hip.kernel.bom.DomainObjectIterator;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.Property;
import org.hip.kernel.bom.PropertySet;
import org.hip.kernel.bom.SortedArray;
import org.hip.kernel.util.ListJoiner;

/**
 * 	This class produces CSV-Files.
 *
 *	@author		Benno Luthiger
 */
public class CsvSerializer extends AbstractSerializer {

	private StringBuffer header	= null;
	
	/**
	 * CsvSerializer default constructor.
	 */
	public CsvSerializer() {
		super();
	}
	/**
	 * CsvSerializer for inputed level.
	 *
	 * @param inLevel int
	 */
	public CsvSerializer(int inLevel) {
		super(inLevel);
	}
	/**
	 * Sequence ending the visit of a DomainObject
	 *
	 * @param inObject org.hip.kernel.bom.GeneralDomainObject
	 */
	protected void endDomainObject(GeneralDomainObject inObject) {
	//	emit_nl();
	}
	/**
	 * Sequence ending the visit of a DomainObjectIterator
	 *
	 * @param inIterator org.hip.kernel.bom.DomainObjectIterator
	 */
	protected void endIterator(DomainObjectIterator inIterator) {
	}
	/**
	 * Sequence ending the visit of a Property
	 *
	 * @param inProperty org.hip.kernel.bom.Property
	 */
	protected void endProperty(Property inProperty) {
		emitText( "," );
	}
	/**
	 * Sequence ending the visit of a PropertySet
	 *
	 * @param inSet org.hip.kernel.bom.PropertySet
	 */
	protected void endPropertySet(PropertySet inSet) {
	}
	/**
	 * Sequence ending the visit of a SortedArray
	 *
	 * @param inSortedArray org.hip.kernel.bom.SortedArray
	 */
	protected void endSortedArray(SortedArray inSortedArray) {
	}
	/**
	 * 
	 * @return java.lang.String
	 */
	public String getHeader() {
		return header.toString() ;
	}
	
	/**
	 * Sequence starting the visit of a DomainObject
	 *
	 * @param inObject org.hip.kernel.bom.GeneralDomainObject
	 */
	protected void startDomainObject(GeneralDomainObject inObject) {
		if (header == null) {
			
		   	header =  new StringBuffer( 1024 ) ;
		   	ListJoiner lHeader = new ListJoiner();
		   	for (String lName : inObject.getPropertyNames2()) {
		   		lHeader.addEntry(lName);
		   	}
		   	header.append(lHeader.joinSpaced(","));
		}
	}

	/**
	 * Sequence starting the visit of a DomainObjectIterator
	 *
	 * @param inIterator org.hip.kernel.bom.DomainObjectIterator
	 */
	protected void startIterator(DomainObjectIterator inIterator) {
	}
	
	/**
	 * Sequence starting the visit of a Property
	 *
	 * @param inProperty org.hip.kernel.bom.Property
	 */
	protected void startProperty(Property inProperty) {
		Object lObject = inProperty.getValue()	;
		String lText = "" ;
		if( lObject != null )
			lText = lObject.toString();
	
		lText = lText.trim() 		;
		lText = lText.replace(',', ' ')	;
		emitText( lText ) 		;
	}
	/**
	 * Sequence starting the visit of a PropertySet
	 *
	 * @param inSet org.hip.kernel.bom.PropertySet
	 */
	protected void startPropertySet(PropertySet inSet) {
	}
	/**
	 * Sequence starting the visit of a SortedArray
	 *
	 * @param inSortedArray org.hip.kernel.bom.SortedArray
	 */
	protected void startSortedArray(SortedArray inSortedArray) {
	}
}

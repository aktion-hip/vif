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

/**
 * 	Default Serializer, a dummy implementation.
 * 
 * 	@author		Benno Luthiger
 * 	@see		java.io.Serializable
 */
public class DftSerializer extends AbstractSerializer implements java.io.Serializable {
/**
 * XMLSerializer default constructor.
 */
public DftSerializer() {
	super();
}
/**
 * DftSerializer constructor of inputed level.
 *
 * @param inLevel int
 */
public DftSerializer( int inLevel ) {
	super( inLevel );
}
/**
 * Emits a comment line.
 * 
 * @param inComment java.lang.String
 */
protected synchronized void emitComment( String inComment ) {
	emit_nl() ;
	emit( "<!--" + inComment + "-->" ) ;
}
/**
 * @param inContent java.lang.String
 */
protected synchronized void emitEndTag( String inContent ) {
}
/**
 * Emits a start tag.
 * 
 * @param inContent java.lang.String
 */
protected synchronized void emitStartTag( String inContent ) {
	emit( "<" + inContent + ">" ) ;
}
/**
 * Sequence ending the visit of a DomainObject
 *
 * @param inObject org.hip.kernel.bom.GeneralDomainObject
 */
protected void endDomainObject(GeneralDomainObject inObject) {
	emit_nl();
	emit_indent();
	emitEndTag(inObject.getObjectName());
}
/**
 * Emits the sequence ending the visit of a DomainObjectIterator
 *
 * @param inIterator org.hip.kernel.bom.DomainObjectIterator
 */
protected void endIterator(DomainObjectIterator inIterator) {
	emit_nl() ;
	emit_indent() ;
	emitEndTag( "ObjectList" ) ;
}
/**
 * Sequence ending the visit of a Property
 *
 * @param inProperty org.hip.kernel.bom.Property
 */
protected void endProperty(Property inProperty) {
	emitEndTag( inProperty.getName())		;
}
/**
 * Sequence ending the visit of a PropertySet
 *
 * @param inSet org.hip.kernel.bom.PropertySet
 */
protected void endPropertySet(PropertySet inSet) {
	emit_nl() 		;
	emit_indent() 	;
	emitEndTag( "propertySet" ) ;
}
/**
 * Emits the sequence ending the visit of a SortedArray
 *
 * @param inSortedArray org.hip.kernel.bom.SortedArray
 */
protected void endSortedArray(SortedArray inSortedArray) {
	emit_nl() ;
	emit_indent() ;
	emitEndTag( "SortedArray" ) ;
}
public void start() {
}
/**
 * Sequence starting the visit of a DomainObject
 *
 * @param inObject org.hip.kernel.bom.GeneralDomainObject
 */
protected void startDomainObject(GeneralDomainObject inObject) {
	emit_nl();
	emitStartTag(inObject.getObjectName());
}
/**
 * Sequence starting the visit of a DomainObjectIterator
 *
 * @param inIterator org.hip.kernel.bom.DomainObjectIterator
 */
protected void startIterator(DomainObjectIterator inIterator) {
	emit_nl() ;
	emitStartTag( "ObjectList" ) ;
}
/**
 * Sequence starting the visit of a Property
 *
 * @param inProperty org.hip.kernel.bom.Property
 */
protected void startProperty(Property inProperty) {
	emit_nl();
	emitStartTag( inProperty.getName() ) 	;
	emitText( inProperty.getValue() ) 	;
}
/**
 * Sequence starting the visit of a PropertySet
 *
 * @param inSet org.hip.kernel.bom.PropertySet
 */
protected void startPropertySet(PropertySet inSet) {
	emit_nl() ;
	emitStartTag( "propertySet" ) ;
}
/**
 * Sequence starting the visit of a SortedArray
 *
 * @param inSortedArray org.hip.kernel.bom.SortedArray
 */
protected void startSortedArray(SortedArray inSortedArray) {
	emit_nl() ;
	emitStartTag( "SortedArray" ) ;
}
}
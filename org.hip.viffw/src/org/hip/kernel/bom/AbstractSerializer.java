package org.hip.kernel.bom;

/*
	This package is part of the framework used for the application VIF.
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

import java.io.Serializable;


/**
 * 	An abstract serializer offering basic functionality for concrete serializer classes.
 * 
 * 	@author	Benno Luthiger
 *	@see	java.io.Serializable
 */
abstract public class AbstractSerializer extends AbstractDomainObjectVisitor implements Serializable {

	// Instance variables
	private StringBuffer	buffer	= new StringBuffer(1024);
	private String			nl		= null;
	private int				tab		= 4;
	private String    		indent 	= "                                                ";
	private String			nullString = "";

	/**
	 * AbstractSerializer default constructor.
	 */
	public AbstractSerializer() {
		super();
	}
	
	/**
	 * AbstractSerializer initializes inputed level.
	 *
	 * @param inLevel int
	 */
	public AbstractSerializer(int inLevel) {
		super(inLevel);
	}
	
	public void clear() {
		buffer = new StringBuffer(1024);
	}
	
	/**
	 * Emits a string.
	 * 
	 * @param inWhat java.lang.String
	 */
	protected final synchronized void emit( String inWhat ) {
		buffer.append( indent() + inWhat ) ;
	}
	
	/**
	 * Emits line indentation.
	 * 
	 */
	protected final synchronized void emit_indent( ) {
		buffer.append( indent() ) ;
	}
	
	/**
	 * Emits a new line.
	 * 
	 * @param java.lang.String
	 */
	protected final synchronized void emit_nl( ) {
		buffer.append( nl() ) ;
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
	 * Emits an end tag.
	 * 
	 * @param inContent java.lang.String
	 */
	protected synchronized void emitEndTag( String inContent ) {
		buffer.append( "</" + inContent + ">" ) ;
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
	 * Emits an object.
	 * 
	 * @param inObject java.lang.Object
	 */
	protected synchronized void emitText( Object inObject ) {
		buffer.append( (inObject != null)? inObject : this.getNullString() ) ;
	}
	
	/**
	 * Sequence ending the visit of a DomainObject
	 *
	 * @param inObject org.hip.kernel.bom.GeneralDomainObject
	 */
	abstract protected void endDomainObject(GeneralDomainObject inObject);

	/**
	 * Sequence ending the visit of a DomainObjectIterator
	 *
	 * @param inIterator org.hip.kernel.bom.DomainObjectIterator
	 */
	abstract protected void endIterator(org.hip.kernel.bom.DomainObjectIterator inIterator) ;

	/**
	 * Sequence ending the visit of a Property
	 *
	 * @param inProperty org.hip.kernel.bom.Property
	 */
	abstract protected void endProperty(org.hip.kernel.bom.Property inProperty)  ;

	/**
	 * Sequence ending the visit of a PropertySet
	 *
	 * @param inSet org.hip.kernel.bom.PropertySet
	 */
	abstract protected void endPropertySet(org.hip.kernel.bom.PropertySet inSet) ;

	/**
	 * 	Returns the buffer.
	 * 
	 * 	@return java.lang.StringBuffer
	 */
	protected synchronized StringBuffer getBuffer() {
		if ( buffer == null )
		     buffer =  new StringBuffer( 1024 ) ;
		return buffer ;
	}
	
	/**
	 * @return java.lang.String
	 */
	public final String getNullString() {
		return nullString ;
	}
	
	/**
	 * Returns line indentation.
	 * 
	 * @return java.lang.String
	 */
	private String indent() {
		int indentLen = getLevel() * tab 	;
		if ( indent.length() <= indentLen )
			 indent += indent ;
			 
		return indent.substring(0, indentLen ) ;
	}
	
	/**
	 * Returns a new line.
	 * 
	 * @return java.lang.String
	 */
	protected String nl() {
		if ( nl == null )
		     nl = System.getProperty( "line.separator" ) ;
		return nl ;
	}
	
	/**
	 * @param inNullString java.lang.String
	 */
	public final void setNullString( String inNullString ) {
		nullString = (inNullString != null)? inNullString : nullString  ;
	}
	
	public void start() {
	}
	
	/**
	 * Sequence starting the visit of a DomainObject
	 *
	 * @param inObject org.hip.kernel.bom.GeneralDomainObject
	 */
	abstract protected void startDomainObject(GeneralDomainObject inObject);

	/**
	 * Sequence starting the visit of a DomainObjectIterator
	 *
	 * @param inIterator org.hip.kernel.bom.DomainObjectIterator
	 */

	abstract protected void startIterator(org.hip.kernel.bom.DomainObjectIterator inIterator) ;
	/**
	 * Sequence starting the visit of a Property
	 *
	 * @param inProperty org.hip.kernel.bom.Property
	 */

	abstract protected void startProperty(org.hip.kernel.bom.Property inProperty) ;
	/**
	 * Sequence starting the visit of a PropertySet
	 *
	 * @param inSet org.hip.kernel.bom.PropertySet
	 */
	abstract protected void startPropertySet(org.hip.kernel.bom.PropertySet inSet) ;

	/**
	 * @return java.lang.String
	 */
	public String toString() {
		return new String(buffer);
	}
}

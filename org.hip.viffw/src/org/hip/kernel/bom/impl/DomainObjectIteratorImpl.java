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

import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.DomainObjectVisitor;
import org.hip.kernel.bom.GeneralDomainObject;

/**
 * 	This is the default implementation of all
 *	domain object iterator classes.
 *	
 *	@author		Benno Luthiger
 *  @see		org.hip.kernel.bom.DomainObjectIterator
 */
public class DomainObjectIteratorImpl extends AbstractObjectIteratorImpl {
	// Instance variables
	private	Vector<?>		objects	= null;
	private Iterator<?>	iterator = null;
	
	/**
	 * DomainObjectIteratorImpl constructor.
	 *
	 * @param inObjects java.util.Vector Objects to iterate through.
	 */
	public DomainObjectIteratorImpl(Vector<?> inObjects) {
		super();
		objects = inObjects;
	}
	
	/**
	 * 	Sets the specified inVisitor.
	 *  This method implements the inVisitor pattern.
	 * 
	 * 	@param inVisitor org.hip.kernel.bom.DomainObjectVisitor
	 */
	public void accept( DomainObjectVisitor inVisitor ) {
		inVisitor.visitDomainObjectIterator(this);
	}
	
	/**
	 * 	Returns an enumeration of DomainObjects
	 *
	 * 	@return java.util.Iterator
	 */
	private final synchronized Iterator<?> enumeration() {
		if (iterator == null) {
			iterator = objects().iterator();
		}
		return iterator;
	}
	
	/**
	 * @return boolean
	 */
	public boolean hasMoreElements() { 
		return enumeration().hasNext();
	}
	
	/**
	 * @return org.hip.kernel.bom.DomainObject
	 */
	public GeneralDomainObject nextElement() { 
		return (GeneralDomainObject)enumeration().next();
	}
	
	/**
	 * 	@return java.util.Vector
	 */
	private final synchronized Vector<?> objects() {
		if ( objects == null )
			 objects = new Vector<Object>() ;
		return objects;
	}
}
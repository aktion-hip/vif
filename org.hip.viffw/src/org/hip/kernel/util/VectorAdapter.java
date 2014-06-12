package org.hip.kernel.util;

/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2001, Benno Luthiger

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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

/**
 * An abstract adapter super class for classes that adapt to a Vector.
 * 
 * @author Benno Luthiger
 * @see java.util.List
 * @see java.util.Vector
 */
public abstract class VectorAdapter implements List<Object> {
	private Vector<Object> collection;

	/**
	 * Constructor for VectorAdapter.
	 */
	protected VectorAdapter() {
		super();
		collection = new Vector<Object>();
	}
	
	/**
	 * Add method with reduced visibility. Only subclasses can access this
	 * method and, therefore, add elements to the collection.
	 * 
	 * @param inObject java.lang.Object The element to add.
	 */
	protected boolean addElement(Object inObject) {
		return collection.add(inObject);
	}
	
	/**
	 * @see Collection#add(Object)
	 */
	public boolean add(Object inObject) {
		return false;
	}

	/**
	 * Returns the number of elements in this collection.
	 * 
	 * @return int
	 */
	public int size() {
		return collection.size();
	}
	
	/**
	 * @see Collection#isEmpty()
	 */
	public boolean isEmpty() {
		return collection.isEmpty();
	}

	/**
	 * @see Collection#contains(Object)
	 */
	public boolean contains(Object arg0) {
		return collection.contains(arg0);
	}

	/**
	 * @see Collection#iterator()
	 */
	public Iterator<Object> iterator() {
		return collection.iterator();
	}

	/**
	 * @see Collection#toArray()
	 */
	public Object[] toArray() {
		return collection.toArray();
	}

	/**
	 * @see Collection#toArray(Object[])
	 */
	@SuppressWarnings("hiding")
	public <Object> Object[] toArray(Object[] arg0) {
		return collection.toArray(arg0);
	}

	/**
	 * @see Collection#remove(Object)
	 */
	public boolean remove(Object arg0) {
		return collection.remove(arg0);
	}

	/**
	 * @see Collection#containsAll(Collection)
	 */
	public boolean containsAll(Collection<?> arg0) {
		return collection.containsAll(arg0);
	}

	/**
	 * @see Collection#addAll(Collection)
	 */
	public boolean addAll(Collection<?> arg0) {
		return false;
	}

	/**
	 * @see List#addAll(int, Collection)
	 */
	public boolean addAll(int arg0, Collection<?> arg1) {
		return false;
	}

	/**
	 * @see Collection#removeAll(Collection)
	 */
	public boolean removeAll(Collection<?> arg0) {
		return collection.removeAll(arg0);
	}

	/**
	 * @see Collection#retainAll(Collection)
	 */
	public boolean retainAll(Collection<?> arg0) {
		return collection.retainAll(arg0);
	}

	/**
	 * @see Collection#clear()
	 */
	public void clear() {
		collection.clear();
	}

	/**
	 * @see List#get(int)
	 */
	public Object get(int arg0) {
		return collection.get(arg0);
	}

	/**
	 * @see List#set(int, Object)
	 */
	public Object set(int arg0, Object arg1) {
		return null;
	}

	/**
	 * @see List#add(int, Object)
	 */
	public void add(int arg0, Object arg1) {
	}

	/**
	 * @see List#remove(int)
	 */
	public Object remove(int arg0) {
		return collection.remove(arg0);
	}

	/**
	 * @see List#indexOf(Object)
	 */
	public int indexOf(Object arg0) {
		return collection.indexOf(arg0);
	}

	/**
	 * @see List#lastIndexOf(Object)
	 */
	public int lastIndexOf(Object arg0) {
		return collection.lastIndexOf(arg0);
	}

	/**
	 * @see List#listIterator()
	 */
	public ListIterator<Object> listIterator() {
		return collection.listIterator();
	}

	/**
	 * @see List#listIterator(int)
	 */
	public ListIterator<Object> listIterator(int arg0) {
		return collection.listIterator(arg0);
	}

	/**
	 * @see List#subList(int, int)
	 */
	public List<Object> subList(int arg0, int arg1) {
		return collection.subList(arg0, arg1);
	}}

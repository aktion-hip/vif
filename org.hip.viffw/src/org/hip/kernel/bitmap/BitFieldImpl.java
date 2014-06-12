package org.hip.kernel.bitmap;

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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.hip.kernel.sys.VObject;

/**
 * Implemantation of a bit field.
 * 
 * @author Benno Luthiger
 */
public class BitFieldImpl extends VObject implements BitField, Cloneable {
	private List<Tuple> fieldTuples;
	
//	private class Tuples extends VectorAdapter {
//		public Tuples() {
//			super();
//		}
//		
//		public boolean add(Tuple inTuple) {
//			return addElement(inTuple);
//		}
//	}
	
	/**
	 * Constructor for BitFieldImpl.
	 */
	public BitFieldImpl() {
		super();
		fieldTuples = new Vector<Tuple>();
//		fieldTuples = new Tuples();
	}

	/**
	 * Adds the specified tuple at the end of the rows.
	 * 
	 * @param inTuple org.hip.kernel.bitmap.Tuple
	 */
	public void addRow(Tuple inTuple) {
		if (!equalRowSize(inTuple.getTupleBitRow())) {
			fieldTuples.add(new Tuple(inTuple.getTupleObject(), resizeRow(inTuple.getTupleBitRow())));
		}
		else {
			fieldTuples.add(inTuple);
		}
	}

	/**
	 * Adds the specified object and bit row at the end of the rows.
	 * 
	 * @param inObject java.lang.Object
	 * @param inRow org.hip.kernel.bitmap.BitRow
	 */
	public void addRow(Object inObject, BitRow inRow) {
		if (!equalRowSize(inRow)) {
			fieldTuples.add(new Tuple(inObject, resizeRow(inRow)));
		}
		else {
			fieldTuples.add(new Tuple(inObject, inRow));
		}
	}
	
	private boolean equalRowSize(BitRow inRow) {
		if (fieldTuples.size() == 0) return true;
		return ((Tuple)fieldTuples.get(0)).getTupleBitRow().getSize() == inRow.getSize();
	}
	
	private BitRow resizeRow(BitRow inRow) {
		BitRow outRow = new BitRowImpl(getColumnSize());
		outRow.setBitValue(inRow.getBitValue());
		return outRow;
	}

	/**
	 * @see BitField#removeRow(int)
	 */
	public Tuple removeRow(int inRowPosition) {
		return (Tuple)fieldTuples.remove(inRowPosition);
	}
	
	/**
	 * Returns the tuple at the specified position.
	 * 
	 * @param inRowPosition int
	 * @return org.hip.kernel.bitmap.Tuple
	 */	
	public Tuple getTuple(int inRowPosition) {
		return (Tuple)fieldTuples.get(inRowPosition);
	}
	
	/**
	 * Returns the object at the specified row position.
	 * 
	 * @param inRowPosition int
	 * @return java.lang.Object
	 */	
	public Object getObject(int inRowPosition) {
		return ((Tuple)fieldTuples.get(inRowPosition)).getTupleObject();
	}
	
	/**
	 * Returns the bit pattern at the specified row position.
	 * 
	 * @param inRowPosition int
	 * @return org.hip.kernel.bitmap.BitRow
	 */	
	public BitRow getBitRow(int inRowPosition) {
		return ((Tuple)fieldTuples.get(inRowPosition)).getTupleBitRow();
	}
	
	/**
	 * @see BitField#getRowSize()
	 */
	public int getRowSize() {
		return fieldTuples.size();
	}

	/**
	 * @see BitField#getColumnSize()
	 */
	public int getColumnSize() {
		if (fieldTuples == null) return 0;
		if (fieldTuples.size() == 0) return 0;
		return ((Tuple)fieldTuples.get(0)).getTupleBitRow().getSize();
	}

	/**
	 * @see BitField#getBit(int, int)
	 */
	public boolean getBit(int inRowPosition, int inColumnPosition) {
		return ((Tuple)fieldTuples.get(inRowPosition)).getTupleBitRow().getBit(inColumnPosition);
	}

	/**
	 * @see BitField#getBit(MatrixPosition)
	 */
	public boolean getBit(MatrixPosition inPosition) {
		return ((Tuple)fieldTuples.get(inPosition.getRowPosition())).getTupleBitRow().getBit(inPosition.getColumnPosition());
	}

	/**
	 * @see BitField#setBit(int, int, boolean)
	 */
	public void setBit(int inRowPosition, int inColumnPosition, boolean inValue) {
		((Tuple)fieldTuples.get(inRowPosition)).getTupleBitRow().setBit(inColumnPosition, inValue);
	}

	/**
	 * @see BitField#setBit(MatrixPosition, boolean)
	 */
	public void setBit(MatrixPosition inPosition, boolean inValue) {
		((Tuple)fieldTuples.get(inPosition.getRowPosition())).getTupleBitRow().setBit(inPosition.getColumnPosition(), inValue);
	}

	/**
	 * @see BitField#invert()
	 */
	public BitField invert() {
		BitField outField = new BitFieldImpl();
		for (Iterator<?> lTuples = fieldTuples.iterator(); lTuples.hasNext();)
			outField.addRow(((Tuple)lTuples.next()).invert());
			
		return outField;
	}

	/**
	 * @see BitField#and(BitField)
	 */
	public BitField and(BitField inAnd) {
		int lSize = Math.min(getRowSize(), inAnd.getRowSize());
		BitField outField = new BitFieldImpl();
		for (int i = 0; i < lSize; i++)
			outField.addRow(getTuple(i).and(inAnd.getTuple(i)));
			
		return outField;
	}

	/**
	 * @see BitField#or(BitField)
	 */
	public BitField or(BitField inOr) {
		int lSize = Math.min(getRowSize(), inOr.getRowSize());
		BitField outField = new BitFieldImpl();
		for (int i = 0; i < lSize; i++)
			outField.addRow(getTuple(i).or(inOr.getTuple(i)));
			
		return outField;
	}

	/**
	 * @see BitField#xor(BitField)
	 */
	public BitField xor(BitField inXOr) {
		int lSize = Math.min(getRowSize(), inXOr.getRowSize());
		BitField outField = new BitFieldImpl();
		for (int i = 0; i < lSize; i++)
			outField.addRow(getTuple(i).xor(inXOr.getTuple(i)));
			
		return outField;
	}

	/**
	 * Compares this bit field with the specified bit field and
	 * returns a collection of positions the two bit fields differ.
	 * 
	 * @param inCompare org.hip.kernel.bitmap.BitField
	 * @return org.hip.kernel.bitmap.MatrixPositions
	 * @deprecated Use <code>{@link #getDifferences2(BitField)}</code> instead.
	 */
	public MatrixPositions getDifferences(BitField inCompare) {
		MatrixPositions outPositions = new MatrixPositions();
		
		int lColumnSize = getColumnSize();
		int lRowSize = Math.min(getRowSize(), inCompare.getRowSize());
		
		for (int i = 0; i < lRowSize; i++) {
			for (int j = 0; j < lColumnSize; j++) {
				if (getBit(i, j) ^ inCompare.getBit(i, j))
					outPositions.add(i, j);
			} //columns
		} //rows
		
		return outPositions;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bitmap.BitField#getDifferences2(org.hip.kernel.bitmap.BitField)
	 */
	public Collection<MatrixPosition> getDifferences2(BitField inCompare) {
		Collection<MatrixPosition> outPositions = new Vector<MatrixPosition>();
		
		int lColumnSize = getColumnSize();
		int lRowSize = Math.min(getRowSize(), inCompare.getRowSize());
		
		for (int i = 0; i < lRowSize; i++) {
			for (int j = 0; j < lColumnSize; j++) {
				if (getBit(i, j) ^ inCompare.getBit(i, j)) {
					outPositions.add(new MatrixPosition(i, j));
				}
			} //columns
		} //rows
		
		return outPositions;	
	}
	
	/**
	 * Returns a hash code value for this bit pattern.
	 * 
	 * @return int
	 */
	public int hashCode() {
		int outCode = 0;
		for (Iterator<?> lTuples = fieldTuples.iterator(); lTuples.hasNext();) 
			outCode ^= ((Tuple)lTuples.next()).hashCode();
			
		return outCode;
	}
	
	/**
	 * Compares this object against the specified object.
	 * 
	 * @param inObject java.lang.Object
	 * @return boolean
	 */
	public boolean equals(Object inObject) {
		if (inObject == null) return false;
		if (getClass() != inObject.getClass()) return false;
		
		BitField lToCompare = (BitField)inObject;
		if (getRowSize() != lToCompare.getRowSize()) return false;
		if (getColumnSize() != lToCompare.getColumnSize()) return false;
		
		for (int i = 0; i < getRowSize(); i++) {
			if (!getTuple(i).equals(lToCompare.getTuple(i)))
				return false;
		}
		return true;
	}
	
	/**
	 * Returns a string representation of this bit pattern.
	 * 
	 * @param java.lang.String
	 */
	public String toString() {
		return "";
	}
	
	/**
	 * Returns a collection of checked positions of the specified row.
	 * Note: The positions contain indexes in the matrix.
	 * 
	 * @param inRowPosition int
	 * @return org.hip.kernel.bitmap.MatrixPositions
	 * @deprecated Use <code>BitFieldImpl#getCheckedOfRow2(int inRowPosition)</code> instead.
	 */
	public MatrixPositions getCheckedOfRow(int inRowPosition) {
		BitRow lBitRow = getBitRow(inRowPosition);
		MatrixPositions outPositions = new MatrixPositions();
		for (int i = 0; i < lBitRow.getSize(); i++) {
			if (lBitRow.getBit(i))
				outPositions.add(inRowPosition, i);
		}
		return outPositions;
	}
	
	/**
	 * Returns a collection of checked positions of the specified row.
	 * Note: The positions contain indexes in the matrix.
 
	 * @param inRowPosition int The element's position in the row.
	 * @return Collection<MatrixPosition>
	 */
	public Collection<MatrixPosition> getCheckedOfRow2(int inRowPosition) {
		BitRow lBitRow = getBitRow(inRowPosition);
		Collection<MatrixPosition> outPositions = new Vector<MatrixPosition>();
		for (int i = 0; i < lBitRow.getSize(); i++) {
			if (lBitRow.getBit(i)) {
				outPositions.add(new MatrixPosition(inRowPosition, i));
			}
		}		
		return outPositions;
	}
	
	/**
	 * Creates and returns a copy of this object.
	 * Note: The bit pattern of the copy is empty.
	 * 
	 * @return java.lang.Object
	 */
	public Object clone() {
		BitField outField = new BitFieldImpl();
		for (int i = 0; i < getRowSize(); i++) {
			Tuple lTuple = (Tuple)getTuple(i).clone();
			lTuple.setTupleBitRowEmpty();
			outField.addRow(lTuple);
		}		
		return outField;
	}

}

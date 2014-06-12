package org.hip.kernel.util.test;

import org.hip.kernel.util.AbstractSortableItem;
import org.hip.kernel.util.Debug;
import org.hip.kernel.util.SortableItem;
import org.hip.kernel.util.VInvalidValueException;

/**
 * TestSortableItem.java
 * 
 * Created on 13.09.2002
 * @author Benno Luthiger
 */
@SuppressWarnings("serial")
public class TestSortableItem extends AbstractSortableItem {
	private int position;

	/**
	 * Constructor for TestSortableItem.
	 */
	public TestSortableItem(Object inValue, int inPosition) throws VInvalidValueException {
		super();
		
		//protected setter of value
		checkValue(inValue);
		value(inValue);
		position = inPosition;
	}

	/**
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(SortableItem inSortableItem) {
		return position - ((TestSortableItem)inSortableItem).getPosition();
	}

	public int getPosition() {
		return position;
	}
	
	/**
	 * @param value java.lang.Object
	 * @exception org.hip.kernel.util.VInvalidValueException
	 */
	protected void checkValue(Object inValue) throws VInvalidValueException {
		if (inValue == null) {
			throw new VInvalidValueException("No value set.");
		}
	}
	
	/**
	 * Returns true if inObject is a <code>SortableItem</code> and its
	 * value is equal to this SortableItem's value.
	 * 
	 * @return boolean
	 * @param inObject java.lang.Object
	 */
	public boolean equals(Object inObject) {
	
		if (inObject == null) return false;
		if (!(inObject instanceof TestSortableItem)) return false;
		
		TestSortableItem lSortableItem = (TestSortableItem)inObject;
		return getValue().equals(lSortableItem.getValue()) && getPosition() == lSortableItem.getPosition();
	}	
	/**
	 * @return java.lang.String
	 */
	public String toString() {
		String lMessage = "value=\"" + ((getValue() != null) ? getValue().toString() : "null" ) + "\"" +
		                  " position=\"" + getPosition() + "\"";
		return Debug.classMarkupString(this, lMessage);
	}
	
	/**
	 * Returns the HashCode calculated from the value.
	 * 
	 * @return int
	 */
	public int hashCode() {
		return super.hashCode() ^ getPosition();
	}
}


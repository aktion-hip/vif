package org.hip.kernel.util.test;

import org.hip.kernel.util.AbstractSortedList;
import org.hip.kernel.util.SortableItem;
import org.hip.kernel.util.VInvalidSortCriteriaException;
import org.hip.kernel.util.VInvalidValueException;

/**
 * TestSortedList.java
 * 
 * Created on 13.09.2002
 * @author Benno Luthiger
 */
@SuppressWarnings("serial")
public class TestSortedList extends AbstractSortedList {

	/**
	 * Creation of TestSortableItem
	 *
	 * @return org.hip.kernel.util.SortableItem
	 * @param inValue java.lang.Object
	 * @param inPosition int
	 * @throws org.hip.kernel.util.VInvalidSortCriteriaException
	 * @throws org.hip.kernel.util.VInvalidValueException
	 */
	protected SortableItem create(Object inValue, int inPosition) throws VInvalidSortCriteriaException, VInvalidValueException {
		return new TestSortableItem(inValue, inPosition);
	}

	/**
	 * No TestSortableItem created with sort criteria.
	 *
	 * @return org.hip.kernel.util.SortableItem
	 * @param inValue java.lang.Object
	 * @param inSortCriteria java.lang.Object
	 * @throws org.hip.kernel.util.VInvalidSortCriteriaException
	 * @throws org.hip.kernel.util.VInvalidValueException
	 */
	protected SortableItem create(Object inValue, Object inSortCriteria) throws VInvalidSortCriteriaException, VInvalidValueException {
		throw new VInvalidSortCriteriaException("SortCriteria not supported for TestSortableItem");
	}
}

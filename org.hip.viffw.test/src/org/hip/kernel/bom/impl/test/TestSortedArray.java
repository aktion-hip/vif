package org.hip.kernel.bom.impl.test;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.impl.AbstractSortedArray;

/**
 * Concrete implementation of a sorted array for testing purpose.
 *
 * @author: Benno Luthiger
 */
public class TestSortedArray extends AbstractSortedArray {

	/**
	 * Adds a new element to the sortet array, i.e. inserts the element
	 * at the correct place.
	 * 
	 * @param inDomainObject org.hip.kernel.bom.DomainObject
	 */
	protected void add(DomainObject inDomainObject) {
		try {
			addSorted(inDomainObject, (String)inDomainObject.get("Name"));
		}
		catch (GettingException exc) {
			exc.printStackTrace();
		}
	}
	/**
	 * Adds a new element to the sortet array, i.e. inserts the element
	 * at the correct place.
	 * 
	 * @param inDomainObject org.hip.kernel.bom.DomainObject
	 */
	protected void addUnique(DomainObject inDomainObject) {
		try {
			addSortedUnique(inDomainObject, (String)inDomainObject.get("Name"));
		}
		catch (GettingException exc) {
			exc.printStackTrace();
		}
	}
}

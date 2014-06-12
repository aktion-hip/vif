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

package org.hip.kernel.bom.impl;

import java.util.Collection;
import java.util.Iterator;

import org.hip.kernel.bom.ColumnModifier;
import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.ICriteriaStack;
import org.hip.kernel.bom.ICriteriumRenderStrategy;
import org.hip.kernel.bom.IGetValueStrategy;
import org.hip.kernel.bom.KeyCriterion;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyCriterionImpl.LevelReturnFormatter;
import org.hip.kernel.bom.model.KeyDef;
import org.hip.kernel.sys.Assert;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.util.AbstractSortedList;
import org.hip.kernel.util.SortableItem;
import org.hip.kernel.util.VInvalidNameException;
import org.hip.kernel.util.VInvalidSortCriteriaException;
import org.hip.kernel.util.VInvalidValueException;

/**
 * This is the default implementation of the KeyObject interface.
 * The KeyObject holds KeyCriterion items in the order the have been added
 * to the KeyObject.
 *
 * @author Benno Luthiger
 * @see    org.hip.kernel.bom.KeyObject
 */
public class KeyObjectImpl extends AbstractSortedList implements KeyObject {
 	// Instance variables
 	private String	schemaName = KeyDef.SCHEMA_PRIMARY_KEY;
 	private int position;
 	private ICriteriaStack criteriaStack;
	private boolean distinct = false;

	/**
	 * Constructor for KeyObjectImpl.
	 */
	public KeyObjectImpl() {
		super();
		position = 0;
		criteriaStack = new SQLCriteriaStack();
	}

	/**
	 * @see org.hip.kernel.util.AbstractSortedList#create(Object, int)
	 */
	protected SortableItem create(Object inValue, int inPosition) throws VInvalidSortCriteriaException, VInvalidValueException {
		try {
			KeyCriterion outCriterion = (KeyCriterion)inValue;
			outCriterion.setPosition(inPosition);
			outCriterion.setOwingList(this);
			return outCriterion;
		}
		catch (ClassCastException exc) {
			throw new VInvalidValueException();
		}
	}

	/**
	 * @see org.hip.kernel.util.AbstractSortedList#create(Object, Object)
	 */
	protected SortableItem create(Object inValue, Object inSortCriteria) throws VInvalidSortCriteriaException, VInvalidValueException {
		throw new VInvalidSortCriteriaException("SortCriteria not supported for OrderObject.");
	}

	/**
	 * @see org.hip.kernel.bom.KeyObject#getSchemaName()
	 */
	public String getSchemaName() {
		return schemaName;
	}

	/**
	 * @see org.hip.kernel.bom.KeyObject#isPrimaryKey()
	 */
	public boolean isPrimaryKey() {
		return schemaName == KeyDef.SCHEMA_PRIMARY_KEY;
	}

	/**
	 * 	Sets the schema name. By default the schema name is 
	 *	"primaryKey".
	 * 
	 * 	@param inSchemaName java.lang.String
	 */
	public void setSchemaName(String inSchemaName) {
		// Pre: inSchemaName not null
		if (VSys.assertNotNull(this, "setSchemaName", inSchemaName) == Assert.FAILURE)
			return;
	
		// Post: set the schema name
		schemaName = inSchemaName.intern();
	}
	
	private String createComparison(Object inValue) {
		if (inValue instanceof SQLRange || inValue instanceof Collection<?>) {
			return "";
		}
		else {
			return KeyCriterionImpl.COMPARISON_OPERATOR_DFT;
		}
	}
	

	/**
	 * @see org.hip.kernel.util.NameValueList#setValue(String, Object)
	 */
	public void setValue(Object inName, Object inValue) throws VInvalidSortCriteriaException, VInvalidValueException {
		try {
			String lName = (String)inName;
			setValue(lName, inValue, createComparison(inValue), KeyCriterionImpl.LINK_OPERATOR_DFT, KeyCriterionImpl.COLUMN_MODIFIER_DFT);
		}
		catch (ClassCastException exc) {
			throw new VInvalidValueException();
		}
		catch (VInvalidNameException exc) {
			throw new VInvalidValueException();
		}
	}

	/**
	 * @see org.hip.kernel.util.NameValueList#setValue(String, Object)
	 */
	public void setValue(String inName, Object inValue) throws VInvalidNameException, VInvalidValueException {
		setValue(inName, inValue, createComparison(inValue), KeyCriterionImpl.LINK_OPERATOR_DFT, KeyCriterionImpl.COLUMN_MODIFIER_DFT);
	}
	
	/**
	 * @see org.hip.kernel.bom.KeyObject#setValue(String, Object, String)
	 */
	public void setValue(String inName, Object inValue, String inComparison) throws VInvalidNameException, VInvalidValueException {
		setValue(inName, inValue, inComparison, KeyCriterionImpl.LINK_OPERATOR_DFT, KeyCriterionImpl.COLUMN_MODIFIER_DFT);
	}

	public void setValue(String inName, Object inValue, String inComparison, boolean inLinkType) throws VInvalidNameException, VInvalidValueException {
		setValue(inName, inValue, inComparison, inLinkType, KeyCriterionImpl.COLUMN_MODIFIER_DFT);
	}
	public void setValue(String inName, Object inValue, String inComparison, BinaryBooleanOperator inBinaryOperator) throws VInvalidNameException, VInvalidValueException {
		setValue(inName, inValue, inComparison, inBinaryOperator, KeyCriterionImpl.COLUMN_MODIFIER_DFT);
	}

	public void setValue(String inName, Object inValue, String inComparison, boolean inLinkType, ColumnModifier inColumnModifier) throws VInvalidNameException, VInvalidValueException {
		this.setValue(inName, inValue, inComparison, inLinkType ? BinaryBooleanOperator.AND : BinaryBooleanOperator.OR, inColumnModifier);
	}
	public void setValue(String inName, Object inValue, String inComparison, BinaryBooleanOperator inBinaryOperator, ColumnModifier inColumnModifier) throws VInvalidNameException, VInvalidValueException {
		try {
			add(create(new KeyCriterionImpl(inName, inValue, inComparison, inBinaryOperator, inColumnModifier), position++));
		}
		catch (VInvalidSortCriteriaException exc) {
			throw new VInvalidValueException();
		}
	}
	
	/**
	 * Sets a KeyObject as group in an enclosing KeyObject.
	 * This method can be used to create SQL expressions like<br/>
	 * KEY1 AND (KEY2)
	 * e.g. date >= 20030801 AND (name LIKE 'VI%' OR name LIKE 'vi%')
	 * 
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @throws org.hip.kernel.util.VInvalidNameException
	 * @throws org.hip.kernel.util.VInvalidValueException
	 */
	public void setValue(KeyObject inKey) throws VInvalidNameException, VInvalidValueException {
		setValue(inKey, KeyCriterionImpl.LINK_OPERATOR_DFT);
	}
	
	public void setValue(KeyObject inKey, boolean inLinkType) throws VInvalidNameException, VInvalidValueException {
		this.setValue(inKey, inLinkType ? BinaryBooleanOperator.AND : BinaryBooleanOperator.OR);
	}
	public void setValue(KeyObject inKey, BinaryBooleanOperator inBinaryOperator) throws VInvalidNameException, VInvalidValueException {
		try {
			add(create(new KeyCriterionImpl(inKey, inBinaryOperator), position++));
		}
		catch (VInvalidSortCriteriaException exc) {
			throw new VInvalidValueException();
		}
	}

	/**
	 * Compares all items of KeyObjects.
	 * 
	 * @return boolean
	 * @param inObject java.lang.Object
	 */
	public boolean equals(Object inObject) {
		//pre
		if (inObject == null) return false;
		if (!(inObject instanceof KeyObject)) return false;
		
		KeyObject lKeyObject = ((KeyObject)inObject);
		if (!lKeyObject.getSchemaName().equals(getSchemaName())) return false;
		if (lKeyObject.size() != size()) return false;
		
		Iterator<SortableItem> lThis = getItems();
		Iterator<SortableItem> lItems = lKeyObject.getItems2().iterator();
		while (lItems.hasNext()) {
			if (!((KeyCriterion)lItems.next()).equals(lThis.next()))
				return false;
		}
		
		return true;
	}

	/**
	 * Returns a hash code value for the order object.
	 *
	 * @return int
	 */
	public int hashCode() {
		int outHashCode = getSchemaName().hashCode();
	
		for (Iterator<SortableItem> lItems = getItems(); lItems.hasNext();) {
			outHashCode ^= ((KeyCriterion)lItems.next()).hashCode();
		}
		return outHashCode;
	}

	/**
	 * Renders the key to a valid part in an SQL statement.
	 * 
	 * @param inDomainObjectHome GeneralDomainObjectHome
	 * @return StringBuffer the rendered SQL part.
	 */	
	public StringBuffer render(final GeneralDomainObjectHome inDomainObjectHome) {
		for (SortableItem lKeyCriterion : getItems2()) {
			criteriaStack.addOperator(((KeyCriterion)lKeyCriterion).getBinaryBooleanOperator());
			criteriaStack.addCriterium(((KeyCriterion)lKeyCriterion).render(inDomainObjectHome));
		}
		return new StringBuffer(criteriaStack.render());
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.KeyObject#setCriteriumRenderer(org.hip.kernel.bom.ICriteriumRenderStrategy)
	 */
	public void setCriteriumRenderer(ICriteriumRenderStrategy inStrategy) {
		for (SortableItem lKeyCriterion : getItems2()) {
			((KeyCriterion) lKeyCriterion).setCriteriumRenderer(inStrategy);			
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.KeyObject#setCriteriaStackFactory(org.hip.kernel.bom.impl.CriteriaStackFactory)
	 */
	public void setCriteriaStackFactory(CriteriaStackFactory inFactory) {
		criteriaStack = inFactory.getCriteriaStack();
		for (SortableItem lKeyCriterion : getItems2()) {
			((KeyCriterion) lKeyCriterion).setCriteriaStackFactory(inFactory);			
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.KeyObject#setGetValueStrategy(org.hip.kernel.bom.IGetValueStrategy)
	 */
	public void setGetValueStrategy(IGetValueStrategy inGetValueStrategy) {
		for (SortableItem lKeyCriterion : getItems2()) {
			((KeyCriterion) lKeyCriterion).setGetValueStrategy(inGetValueStrategy);			
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.KeyObject#setLevelReturnFormatter(org.hip.kernel.bom.impl.KeyCriterionImpl.LevelReturnFormatter)
	 */
	public void setLevelReturnFormatter(LevelReturnFormatter inFormatter) {
		for (SortableItem lKeyCriterion : getItems2()) {
			((KeyCriterion) lKeyCriterion).setLevelReturnFormatter(inFormatter);			
		}		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.KeyObject#setDistinct(boolean)
	 */
	public void setDistinct(boolean inDistinct) {
		distinct = inDistinct;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.KeyObject#isDistinct()
	 */
	public boolean isDistinct() {
		return distinct;
	}
	
}
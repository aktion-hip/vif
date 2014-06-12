package org.hip.kernel.bom.impl;

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

import org.hip.kernel.bom.ColumnModifier;
import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.ICriteriumRenderStrategy;
import org.hip.kernel.bom.IGetValueStrategy;
import org.hip.kernel.bom.KeyCriterion;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.util.AbstractNameValueList;
import org.hip.kernel.util.NameValue;
import org.hip.kernel.util.SortedList;
import org.hip.kernel.util.VInvalidNameException;
import org.hip.kernel.util.VInvalidValueException;

/**
 * Parameter object to create the criteria in SQL WHERE statements.
 * With this object it is possible to create statements as
 * <pre>
 * ... WHERE MODIFIER(field_name) COMPARISON VALUE LINK MODIFIER(field_name) COMPARISON VALUE
 * </pre>
 * e.g.
 * <pre>
 * ... WHERE UCASE(name) LIKE 'VI%' AND YEAR(mutation_date) >= 2001
 * </pre>
 * 
 * Created on 10.09.2002
 * @author Benno Luthiger
 */
public class KeyCriterionImpl extends AbstractNameValueList implements KeyCriterion, Comparable<KeyCriterion> {
	public final static boolean LINK_OPERATOR_DFT = true;
	public final static String COMPARISON_OPERATOR_DFT = "=";
	public final static ColumnModifier COLUMN_MODIFIER_DFT = new DefaultColumnModifier();
	public static LevelReturnFormatter LEVEL_STRAIGHT = null;
	
	private final static String NAME_KEY = "keyName";
	private final static String VALUE_KEY = "keyValue";
	private final static String BINARY_BOOLEAN_OPERATOR_KEY = "binaryBooleanOperator";
	private final static String COMPARISON_OPERATOR_KEY = "comparisonOperator";
	private final static String COLUMN_MODIFIER_KEY = "columnModifier";
	
	//instance variables
	private SortedList	owingList = null;
	private int position;
	private KeyObjectTraversal traversal;
	private KeyObjectRecursion recursion;
	private ICriteriumRenderStrategy renderStrategy;
	private IGetValueStrategy getValueStrategy = new CriteriumValueStrategy();
	private LevelReturnFormatter levelFormatter = new LevelReturnEnclosed();
	
	static {
		LEVEL_STRAIGHT = new LevelReturnFormatter() {
			public StringBuffer format(StringBuffer inRenderedKey) {
				return inRenderedKey;
			}			
		};
	}

	/**
	 * Constructor for KeyCriterionImpl.
	 * 
	 * @param inName java.lang.String Name of the field used for the criterion.
	 * @param inValue java.lang.Object Value to match in the criterion.
	 * @param inComparison java.lang.String Comparison operator for the criterion.
	 * @param inLinkType boolean Type of link if concatenation multiple criteria. True (default) links with AND, false with OR.
	 * @param inColumnModifier org.hip.kernel.bom.ColumnModifier Function to modify field value in the criterion.
	 * @throws VInvalidValueException
	 * @deprecated use constructor with BinaryBooleanOperator instead of boolean inLinkType
	 */
	public KeyCriterionImpl(String inName, Object inValue, String inComparison, boolean inLinkType, ColumnModifier inColumnModifier) throws VInvalidValueException {
		this(inName, inValue, inComparison, inLinkType ? BinaryBooleanOperator.AND : BinaryBooleanOperator.OR, inColumnModifier);
	}
	/**
	 * Constructor for KeyCriterionImpl.
	 * 
	 * @param inName String Name of the field used for the criterion.
	 * @param inValue Object Value to match in the criterion.
	 * @param inComparison String Comparison operator for the criterion.
	 * @param inBinaryOperator BinaryBooleanOperator the binary operator used for the criterion operands.
	 * @param inColumnModifier ColumnModifier Function to modify field value in the criterion.
	 * @throws VInvalidValueException
	 */
	public KeyCriterionImpl(String inName, Object inValue, String inComparison, BinaryBooleanOperator inBinaryOperator, ColumnModifier inColumnModifier) throws VInvalidValueException {
		traversal = new KeyObjectTraversal() {
			public void setCriteriumRenderer(ICriteriumRenderStrategy inStrategy) {
				//nothing to do				
			}
			public void setCriteriaStackFactory(CriteriaStackFactory inFactory) {
				//nothing to do				
			}
			public void setGetValueStrategy(IGetValueStrategy inStrategy) {
				//nothing to do				
			}
			public void setLevelReturnFormatter(LevelReturnFormatter inFormatter) {
				//nothing to do				
			}
		};
		recursion = new NoRecursion();
		renderStrategy = new SQLCriteriumRenderer();
		
		try {
			add(create(NAME_KEY, inName));
			add(create(VALUE_KEY, inValue));
			add(create(COMPARISON_OPERATOR_KEY, inComparison));
			add(create(BINARY_BOOLEAN_OPERATOR_KEY, inBinaryOperator));
			add(create(COLUMN_MODIFIER_KEY, inColumnModifier));
		}
		catch (VInvalidNameException exc) {
			throw new VInvalidValueException();
		}
	}
	
	/**
	 * KeyCriterionImpl constructor to create Keys embedded in other Keys.
	 * 
	 * @param inKey KeyObject the Key to embed
	 * @param inLinkType boolean Type of link if concatenation multiple criteria. True (default) links with AND, false with OR.
	 * @throws VInvalidValueException
	 * @deprecated use constructor with BinaryBooleanOperator instead of boolean inLinkType
	 */
	public KeyCriterionImpl(KeyObject inKey, boolean inLinkType) throws VInvalidValueException {
		this(inKey, inLinkType ? BinaryBooleanOperator.AND : BinaryBooleanOperator.OR);
	}
	/**
	 * KeyCriterionImpl constructor to create Keys embedded in other Keys.
	 * 
	 * @param inKey KeyObject the Key to embed
	 * @param inBinaryOperator BinaryBooleanOperator the binary operator used for the criterion operands.
	 * @throws VInvalidValueException
	 */
	public KeyCriterionImpl(KeyObject inKey, BinaryBooleanOperator inBinaryOperator) throws VInvalidValueException {
		traversal = new KeyObjectTraversal() {
			public void setCriteriumRenderer(ICriteriumRenderStrategy inStrategy) {
				((KeyObject)getValue()).setCriteriumRenderer(inStrategy);
			}
			public void setCriteriaStackFactory(CriteriaStackFactory inFactory) {
				((KeyObject)getValue()).setCriteriaStackFactory(inFactory);
			}
			public void setGetValueStrategy(IGetValueStrategy inStrategy) {
				((KeyObject)getValue()).setGetValueStrategy(inStrategy);				
			}
			public void setLevelReturnFormatter(LevelReturnFormatter inFormatter) {
				((KeyObject)getValue()).setLevelReturnFormatter(inFormatter);				
			}
		};
		recursion = new NormalRecursion();
		renderStrategy = new SQLCriteriumRenderer();
		
		try {
			add(create(NAME_KEY, NAME_FOR_KEY));
			add(create(VALUE_KEY, inKey));
			add(create(BINARY_BOOLEAN_OPERATOR_KEY, inBinaryOperator));
		}
		catch (VInvalidNameException exc) {
			throw new VInvalidValueException();
		}
	}

	public String getName() {
		return (String)get(NAME_KEY).getValue();
	}
	
	public Object getValue() {
		return get(VALUE_KEY).getValue();
	}
	
	public BinaryBooleanOperator getBinaryBooleanOperator() {
		return (BinaryBooleanOperator)get(BINARY_BOOLEAN_OPERATOR_KEY).getValue();
	}
	
	public String getComparison() {
		return (String)get(COMPARISON_OPERATOR_KEY).getValue();
	}
	
	public ColumnModifier getColumnModifier() {
		return (ColumnModifier)get(COLUMN_MODIFIER_KEY).getValue();
	}
	
	/**
	 * This method creates a KeyItemImpl (NameValue) 
	 * and initializes it with the specified value.
	 *
	 * @return org.hip.kernel.util.NameValue
	 * @param inName java.lang.String
	 * @param inValue java.lang.Object
	 * @exception org.hip.kernel.util.VInvalidNameException
	 * @exception org.hip.kernel.util.VInvalidValueException
	 */
	protected NameValue create(String inName, Object inValue) throws VInvalidValueException, VInvalidNameException {
		NameValue outKeyItem = new KeyItemImpl(this, inName);
		outKeyItem.setValue(inValue);
		return outKeyItem;		  
	}

	/**
	 * @return boolean
	 */
	protected boolean dynamicAddAllowed() {
		return false;
	}
	
	/**
	 * Position of item.
	 * 
	 * @return int
	 */
	public int getPosition() {
		return position;
	}
	
	/**
	 * Sets the position of this KeyCriterion in the KeyObject.
	 * 
	 * @param inPosition int
	 */
	public void setPosition(int inPosition) {
		position = inPosition;
	}

	/**
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(KeyCriterion inKeyCriterion) {
		return position - inKeyCriterion.getPosition();
	}
	
	/**
	 * Returns the list the instance is member from.
	 *
	 * @return org.hip.kernel.util.SortedList
	 */
	public SortedList getOwingList() {
		return owingList;
	}
	
	/**
	 * @param inOwingList org.hip.kernel.util.SortedList
	 */
	public void setOwingList(SortedList inOwingList) {
		owingList = inOwingList;
	}

	/**
	 * Renders the criterion to a valid part in an SQL statement.
	 * 
	 * @param inDomainObjectHome GeneralDomainObjectHome
	 * @return StringBuffer the rendered SQL part.
	 */
	public StringBuffer render(GeneralDomainObjectHome inDomainObjectHome) {
		return recursion.run(inDomainObjectHome);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.KeyCriterion#setCriteriumRenderer(org.hip.kernel.bom.ICriteriumRenderStrategy)
	 */
	public void setCriteriumRenderer(ICriteriumRenderStrategy inStrategy) {
		renderStrategy = inStrategy;
		traversal.setCriteriumRenderer(inStrategy);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.KeyCriterion#setOperandStack(org.hip.kernel.bom.IOperandStack)
	 */
	public void setCriteriaStackFactory(CriteriaStackFactory inFactory) {
		traversal.setCriteriaStackFactory(inFactory);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.KeyCriterion#setGetValueStrategy(org.hip.kernel.bom.IGetValueStrategy)
	 */
	public void setGetValueStrategy(IGetValueStrategy inGetValueStrategy) {
		getValueStrategy = inGetValueStrategy;
		traversal.setGetValueStrategy(inGetValueStrategy);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.KeyCriterion#setLevelReturnFormatter(org.hip.kernel.bom.impl.KeyCriterionImpl.LevelReturnFormatter)
	 */
	public void setLevelReturnFormatter(LevelReturnFormatter inFormatter) {
		levelFormatter = inFormatter;
		traversal.setLevelReturnFormatter(inFormatter);
	}
	
//	 --- inner classes ---
	
	private class SQLCriteriumRenderer extends AbstractCriteriumRenderer implements Serializable {
		public StringBuffer render() {
			StringBuffer outSQL = new StringBuffer(operand1);
			outSQL.append(" ");
			if (comparison.length() > 0) {
				outSQL.append(comparison).append(" ");				
			}
			outSQL.append(operand2);
			return outSQL;
		}
	}
	
	private interface KeyObjectTraversal extends Serializable {
		void setCriteriumRenderer(ICriteriumRenderStrategy inStrategy);
		void setCriteriaStackFactory(CriteriaStackFactory inFactory);
		void setGetValueStrategy(IGetValueStrategy inStrategy);
		void setLevelReturnFormatter(LevelReturnFormatter inFormatter);
	}
	
	private interface KeyObjectRecursion extends Serializable {
		StringBuffer run(GeneralDomainObjectHome inDomainObjectHome);
	}
	
	private class NoRecursion implements KeyObjectRecursion {
		public StringBuffer run(GeneralDomainObjectHome inDomainObjectHome) {
			renderStrategy.setOperand1(getColumnModifier().modifyColumn(inDomainObjectHome.getColumnNameFor(getName())));
			renderStrategy.setOperand2(getValueStrategy.getValue(KeyCriterionImpl.this));
			renderStrategy.setComparison(getComparison());
			return renderStrategy.render();
		}
	}
	
	private class NormalRecursion implements KeyObjectRecursion {
		public StringBuffer run(GeneralDomainObjectHome inDomainObjectHome) {
			return levelFormatter.format(((KeyObject)getValue()).render(inDomainObjectHome));
		}		
	}
	
	/**
	 * Interface to define how interwoven levels of keys are formatted.
	 *
	 * @author Luthiger
	 * Created on 25.08.2007
	 */
	public interface LevelReturnFormatter extends Serializable {
		StringBuffer format(StringBuffer inRenderedKey);
	}
	
	/**
	 * Implementation of <code>LevelReturnFormatter</code> to enclose interwoven levels in parenthesis.
	 *
	 * @see LevelReturnFormatter
	 * @author Luthiger
	 * Created on 25.08.2007
	 */
	public class LevelReturnEnclosed implements LevelReturnFormatter {
		public StringBuffer format(StringBuffer inRenderedKey) {
			StringBuffer out = new StringBuffer("(");
			out.append(inRenderedKey).append(")");
			return out;
		}		
	}
	
}

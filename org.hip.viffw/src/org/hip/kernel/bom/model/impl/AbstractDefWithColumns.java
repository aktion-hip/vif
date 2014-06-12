package org.hip.kernel.bom.model.impl;

/*
	This package is part of the domain object framework used for the application VIF.
	Copyright (C) 2003, Benno Luthiger

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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.SettingException;
import org.hip.kernel.bom.model.ColumnDefDef;
import org.hip.kernel.bom.model.HiddenDef;
import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.bom.model.MappingDefDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.bom.model.PropertyDefDef;
import org.hip.kernel.exc.DefaultExceptionHandler;
import org.hip.kernel.exc.DefaultExceptionWriter;
import org.hip.kernel.util.ListJoiner;
import org.hip.kernel.util.NameValueList;
import org.hip.kernel.util.VNameValueException;

/**
 * Abstract class providing functionality for joined domain objects.
 * 
 * @author Benno Luthiger
 * Created on Dec 20, 2003
 */
public abstract class AbstractDefWithColumns extends AbstractModelObject {
	protected Collection<PropertyDef> propertyDefs = new Vector<PropertyDef>();
	private Hashtable<String, ObjectDef> objectDefs = new Hashtable<String, ObjectDef>(5);
	private Hashtable<String, String> hidden = new Hashtable<String, String>(3);

	/**
	 * AbstractDefWithColumns default constructor.
	 */
	public AbstractDefWithColumns() {
		super();
	}
	/**
	 * AbstractDefWithColumns constructor with initial values.
	 * @param inInitialValues
	 */
	public AbstractDefWithColumns(Object[][] inInitialValues) {
		super(inInitialValues);
	}

	/**
	 * Adds a column def to the object definition.
	 * The purpose of this method is to retrieve an existing PropertyDef 
	 * (with its table mapping) to reuse it for the JoinedDomainObject, i.e.
	 * the SQL commands to retrieve the persistent data.
	 * The result of this method (i.e. the SQL command that will be created) is 
	 * contingent on the existence of the different attributes: 
	 * columnName, alias, domainObject, nestedObject, valueType, modifier.
	 * 'columnName' is required.
	 * if 'domainObject' exists => SQL: mappedTable.mappedColumnName
	 * if 'nestedObject' existes => SQL: nestedObject.columnName
	 * if 'domainObject' and 'nestedObject' => SQL: nestedObject.mappedColumnName
	 * if 'alias' => sets the propertyDef's tableName attribute to the specified alias
	 * if 'valueType' => sets the propertyDef's valueType to the specified value
	 * if 'as' => SQL: (tab.col) AS alias
	 * if 'modifier' => SQL modifier(tab.col)
	 * if 'template' => SQL format(template, new Object[] {tab.col})
	 * 
	 * @param inColumnDefAttributes org.hip.kernel.util.NameValueList
	 * @throws BOMException
	 */
	public void addColumnDef(NameValueList inColumnDefAttributes) throws BOMException {
		try {
			String lVisibleColumnName = (String)inColumnDefAttributes.getValue(ColumnDefDef.columnName);
			String lMappedColumnName = lVisibleColumnName;
			String lTableName = "";
			PropertyDef lPropertyDef = null;
	
			boolean lHasValueAttr = inColumnDefAttributes.hasValue(ColumnDefDef.valueType);
			boolean lHasDomainObjDef = false;
			if (lHasDomainObjDef = inColumnDefAttributes.hasValue(ColumnDefDef.domainObject)) {
				String lDomainObjectName = (String)inColumnDefAttributes.getValue(ColumnDefDef.domainObject);
				ObjectDef lObjectDef = getDomainObjectDef(lDomainObjectName);
				lMappedColumnName = lObjectDef.getPropertyDef(lVisibleColumnName).getMappingDef().getColumnName();
				lTableName = getTableName(lObjectDef);
				
				//We need a clone of the original PropertyDef because we want to change some attributes.
				lPropertyDef = (PropertyDef)((PropertyDefImpl)lObjectDef.getPropertyDef(lVisibleColumnName)).clone();
				if (lHasValueAttr) {
					lPropertyDef.set(PropertyDefDef.valueType, inColumnDefAttributes.getValue(ColumnDefDef.valueType));
				}
			}
			
			if (inColumnDefAttributes.hasValue(ColumnDefDef.nestedObject)) {
				lTableName = (String)inColumnDefAttributes.getValue(ColumnDefDef.nestedObject);
				
				//We need either a valueType or domainObject attribute here.
				if (!(lHasValueAttr || lHasDomainObjDef)) {
					throw new BOMException("Either attribute valueType or domainObject must be defined for a nested object.");
				}
				if (lHasValueAttr) {
					String lValueType = (String)inColumnDefAttributes.getValue(ColumnDefDef.valueType);
					lPropertyDef = createPropertyDef(lTableName, lVisibleColumnName, lValueType);
				}
			}
	
			StringBuffer lSQLCommand = new StringBuffer(lTableName);
			lSQLCommand.append(".").append(lMappedColumnName);
			if (inColumnDefAttributes.hasValue(ColumnDefDef.modifier)) {
				StringBuffer lModified = new StringBuffer((String)inColumnDefAttributes.getValue(ColumnDefDef.modifier));
				lModified.append("(").append(lSQLCommand).append(")");
				lSQLCommand = lModified;
			}
			if (inColumnDefAttributes.hasValue(ColumnDefDef.template)) {
				StringBuffer lModified = new StringBuffer("(");
				lModified.append(MessageFormat.format((String)inColumnDefAttributes.getValue(ColumnDefDef.template), new Object[] {lSQLCommand})).append(")");
				lSQLCommand = lModified;				
			}
			if (inColumnDefAttributes.hasValue(ColumnDefDef.alias)) {
				String lAlias = (String)inColumnDefAttributes.getValue(ColumnDefDef.alias);
				lPropertyDef.set(PropertyDefDef.propertyName, lAlias);
			}
			if (inColumnDefAttributes.hasValue(ColumnDefDef.as)) {
				String lAlias = (String)inColumnDefAttributes.getValue(ColumnDefDef.as);
				lPropertyDef.set(PropertyDefDef.propertyName, lAlias);
				MappingDef lMappingDef = lPropertyDef.getMappingDef();
				lMappingDef.set(MappingDefDef.columnName, lAlias);
				lMappingDef.set(MappingDefDef.tableName, "");
				lSQLCommand.append(" AS ").append(lAlias);
			}
	
			propertyDefs.add(lPropertyDef);
			getColumns().addElement(new String(lSQLCommand));
		}
		catch (VNameValueException exc) {
			throw new BOMException(exc.getMessage());
		}
	}
	
	/**
	 * Adds a hidden column def to the object definition.
	 * 
	 * @param inHiddenAttributes NameValueList
	 */
	public void addHidden(NameValueList inHiddenAttributes) {
		try {
			String lPropertyName = (String)inHiddenAttributes.getValue(HiddenDef.columnName);
			String lDomainObjectName = (String)inHiddenAttributes.getValue(HiddenDef.domainObject);
			
			ObjectDef lObjectDef = getDomainObjectDef(lDomainObjectName);
			String lColumnName = lObjectDef.getPropertyDef(lPropertyName).getMappingDef().getColumnName();
			String lExpression = getTableName(lObjectDef) + "." + lColumnName;
			hidden.put(lPropertyName, lExpression);
		}
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);
		}
	}
	
	/**
	 * Returns the mapping of the specified hidden field.
	 * 
	 * @param inPropertyName String
	 * @return String
	 */
	public String getHidden(String inPropertyName) {
		return (String)hidden.get(inPropertyName);
	}
	
	private PropertyDef createPropertyDef(String inTableName, String inPropertyName, String inValueType) throws SettingException {
		PropertyDef outDef = new PropertyDefImpl();
		outDef.set(PropertyDefDef.propertyName, inPropertyName);
		outDef.set(PropertyDefDef.valueType, inValueType);
		outDef.set(PropertyDefDef.propertyType, PropertyDefDef.propertyTypeSimple);
		MappingDef lMappingDef = new MappingDefImpl();
		lMappingDef.set(MappingDefDef.tableName, inTableName);
		lMappingDef.set(MappingDefDef.columnName, inPropertyName);
		outDef.setMappingDef(lMappingDef);
		return outDef;
	}
	
	/**
	 * Returns the key to set and retrieve the columnDefs in the hashtable.
	 * 
	 * @return String
	 */
	abstract String getColumnsKey();
	
	/**
	 * 	@return java.util.Vector
	 */
	@SuppressWarnings("unchecked")
	private Vector<String> getColumns() {
		try {
			Vector<String> outColumns = (Vector<String>)get(getColumnsKey());
			if (outColumns == null) {
				outColumns = new Vector<String>();
				set(getColumnsKey(), outColumns);
			}
			return outColumns;
		}
		catch (VNameValueException exc) {
			DefaultExceptionWriter.printOut(this, exc, true);
			return new Vector<String>();
		}
	}

	protected ObjectDef getDomainObjectDef(String inObjectName) throws BOMException {
		ObjectDef outObjectDef = (ObjectDef)objectDefs.get(inObjectName);
		if (outObjectDef == null) {
			try {
				Class<?> lClass = Class.forName(inObjectName);
				DomainObject lDomainObject = (DomainObject)lClass.newInstance();
				outObjectDef = lDomainObject.getHome().getObjectDef();
				objectDefs.put(inObjectName, outObjectDef);
			}
			catch (Exception exc) {
				throw new BOMException("getDomainObjectDef");
			}
		}
		return outObjectDef;
	}
	
	/**
	 * Returns the SQL name of the table where this column set resides.
	 * 
	 * @return String SQL name of table
	 */
	protected String getTableName() {
		return getTableName(objectDefs.values().toArray(new ObjectDef[1])[0]);
	}

	/**
	 * Returns the SQL name of the table from the specified <code>ObjectDef</code>.
	 * 
	 * @param inObjectDef ObjectDef
	 * @return String table name
	 */
	protected String getTableName(ObjectDef inObjectDef) {
		return inObjectDef.getTableNames2().toArray(new String[1])[0];
	}
	
	/** 
	 * @return String Comma separated list of field names.
	 */
	protected String getFields() {
		ListJoiner lColumns = new ListJoiner();
		for (String lColumn : getColumns()) {
			lColumns.addEntry(lColumn);
		}
		return lColumns.joinSpaced(",");
	}
}
package org.hip.kernel.bom.model.impl;

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

import java.io.StringReader;
import java.util.Stack;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import org.hip.kernel.sys.VSys;
import org.hip.kernel.sys.Assert;
import org.hip.kernel.bom.SettingException;
import org.hip.kernel.bom.model.ColumnDefDef;
import org.hip.kernel.bom.model.GroupingDef;
import org.hip.kernel.bom.model.GroupingDefDef;
import org.hip.kernel.bom.model.JoinedObjectDef;
import org.hip.kernel.bom.model.ModelObject;
import org.hip.kernel.bom.model.JoinDef;
import org.hip.kernel.bom.model.JoinDefDef;
import org.hip.kernel.bom.model.NestedDef;
import org.hip.kernel.bom.model.NestedDefDef;
import org.hip.kernel.bom.model.ObjectDescDef;
import org.hip.kernel.bom.model.PlaceholderDefDef;
import org.hip.kernel.exc.DefaultExceptionHandler;
import org.hip.kernel.exc.DefaultExceptionWriter;
import org.hip.kernel.util.DefaultNameValueList;
import org.hip.kernel.util.NameValueList;
import org.hip.kernel.util.VInvalidNameException;

/**
 * SAX-Parser for JoinedObjectDef
 * 
 * @author Benno Luthiger
 */
public class JoinedObjectDefGenerator extends DefaultHandler {
	
	// Class variables 
	private static JoinedObjectDefGenerator singleton = null;
		
	// Instance variables
	private	XMLReader		parser				= null;
	private	JoinedObjectDef	objectDef			= null;
	private	JoinDef			currentJoinDef		= null;
	private	ColumnDefState	columnDefState		= null;
	private NestedDef		nestedDef			= null;
	private GroupingDef		groupingDef			= null;
	
	/**
	 * Inner class to track the states of columnDef.
	 */
	private class ColumnDefState extends Object {
		public final static int COLUMN_DEF 		= 1;
		public final static int JOIN_CONDITION 	= 2;
		public final static int OBJECT_NESTED 	= 3;
		public final static int RESULT_GROUPING	= 4;
		
		private Stack<Integer> states = null;
		
		public ColumnDefState() {
			super();
			states = new Stack<Integer>();
		}
		public void setToColumnDefs() {
			states.push(new Integer(COLUMN_DEF));
		}
		public void setToJoinCondition() {
			states.push(new Integer(JOIN_CONDITION));
		}
		public void setToObjectNested() {
			states.push(new Integer(OBJECT_NESTED));
		}
		public void setToResultGrouping() {
			states.push(new Integer(RESULT_GROUPING));
		}
		public void pop() {
			states.pop();
		}
		public int getState() {
			if (states.empty()) return 0;
			return states.peek().intValue();
		}
	}
	
	/**
	 * JoinedObjectDefGenerator default constructor.
	 */
	private JoinedObjectDefGenerator() {
		super();
	}
	
	/**
	 * 	Creates a JoinedObjectDef from a definition string.
	 * 
	 *  @return org.hip.kernel.bom.model.JoinedObjectDef
	 * 	@param inDefinitionString java.lang.String
	 */
	public JoinedObjectDef createJoinedObjectDef(String inDefinitionString) throws SAXException {
	
		try {
			StringReader lReader = new StringReader(inDefinitionString);
			InputSource lInputSource = new InputSource(lReader);
	  		parser().parse(lInputSource);
	
		} 
		catch (Throwable t) {
			DefaultExceptionWriter.printOut(this, t, true);
		}
		
		return objectDef;
	}
	
	/**
	 * @return org.hip.kernel.bom.model.impl.JoinedObjectDefGenerator
	 */
	public static JoinedObjectDefGenerator getSingleton() {
	
		if (singleton == null)
			singleton = new JoinedObjectDefGenerator();
		
		return singleton;
	}
	
	/**
	 * @return org.xml.sax.XMLReader
	 */
	private XMLReader parser() {
	
		if (parser == null) {
			try { 
				parser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
				parser.setContentHandler(this);
				parser.setErrorHandler(this);
			} 
			catch (Exception exc) {
				DefaultExceptionWriter.printOut(this, exc, true);
			}
		}
		return parser;
	}
		
	/**
	 * Handles the end of the <columnDef> tag.
	 * 
	 * @param inName java.lang.String
	 */
	private void end_columnDef(String inName) {
	}
	
	/**
	 * Handles the end of the <hidden> tag.
	 * 
	 * @param inName java.lang.String
	 */
	private void end_hidden(String inName) {
	}
	
	/**
	 * Handles the end of the <joinCondition> tag.
	 * 
	 * @param inName java.lang.String
	 */
	private void end_joinCondition(String inName) {
		columnDefState.pop();
	}
	
	/**
	 * Handles the end of the <objectNested> tag.
	 * 
	 * @param inName java.lang.String
	 */
	private void end_objectNested(String inName) {
		// There must be something wrong in the definition
		if (VSys.assertNotNull(this, "end_objectNested", currentJoinDef) == Assert.FAILURE) 
			return;
		if (VSys.assertNotNull(this, "end_objectNested", nestedDef) == Assert.FAILURE) 
			return;

		currentJoinDef.setNestedQuery(nestedDef.getNestedQuery());
		columnDefState.pop();
	}
	
	/**
	 * Handles the end of the <resultGrouping> tag.
	 * 
	 * @param inName java.lang.String
	 */
	private void end_resultGrouping(String inName) {
		columnDefState.pop();
	}
	
	/**
	 * Handles the end of the <objectPlaceholder> tag.
	 * 
	 * @param inName java.lang.String
	 */
	private void end_objectPlaceholder(String inName) {
	}
	
	/**
	 * Handles the end of the <joinDef> tag.
	 * 
	 * @param inName java.lang.String
	 */
	private void end_joinDef(String inName) {
		//Pre
		if (VSys.assertNotNull(this, "end_joinDef", currentJoinDef) == Assert.FAILURE)
			return;
	
		JoinDef lJoinDef = currentJoinDef.getParentJoinDef();
		if (lJoinDef != null) {
			lJoinDef.setChildJoinDef(currentJoinDef); 	// set current as child in parent
			currentJoinDef.setParentJoinDef(null);		// set parent of current to null
		}
		currentJoinDef = lJoinDef;
	}
	
	/**
	 * Handles the end of the <joinedObjectDef> tag.
	 * 
	 * @param inName java.lang.String
	 */
	private void end_objectDef( String inName ) {
	}
	
	/**
	 * Handles the end of the <objectDesc> tag.
	 * 
	 * @param inName java.lang.String
	 */
	private void end_objectDesc( String inName ) {
	}
	
	/**
	 * @exception org.xml.sax.SAXException
	 */
	public void endDocument() throws SAXException {
	}
	
	/**
	 * Receive notification of the end of an element.
	 *
	 * @param inUri java.lang.String The Namespace URI.
	 * @param inLocalName java.lang.String The local name.
	 * @param inRawName java.lang.String The qualified (prefixed) name.
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly wrapping another exception.
	 * @see org.xml.sax.ContentHandler#endElement
	 */
	public void endElement(String inUri, String inLocalName, String inRawName) throws SAXException {
			
		if (inRawName != null)
			// make sure the string is internalized to compare references
			 inRawName = inRawName.intern();
	
		// tag <joinedObjectDef>	 
		if (inRawName == ModelObject.joinedObjectDef) {
			 end_objectDef(inRawName);
			 return;
		}
		// tag <columnDef>	 
		if (inRawName == ModelObject.columnDef) {
			 end_columnDef(inRawName);
			 return;
		}
		// tag <hidden>
		if (inRawName == ModelObject.hidden) {
			end_hidden(inRawName);
			return;
		}
		// tag <joinDef>	 
		if (inRawName == ModelObject.joinDef) {
			 end_joinDef(inRawName);
			 return;
		}
		// tag <objectDesc>	 
		if (inRawName == ModelObject.objectDesc) {
			 end_objectDesc(inRawName);
			 return;
		}
		// tag <joinCondition>	 
		if (inRawName == ModelObject.joinCondition) {
			 end_joinCondition(inRawName);
			 return;
		}
		// tag <objectNested>
		if (inRawName == ModelObject.objectNested) {
			end_objectNested(inRawName);
			return;
		}
		// tag <resultGrouping >
		if (inRawName == ModelObject.resultGrouping) {
			end_resultGrouping(inRawName);
			return;
		}
		// tag <objectPlaceholder>
		if (inRawName == ModelObject.objectPlaceholder) {
			end_objectPlaceholder(inRawName);
			return;
		}
	}
	
	/**
	 * Handles the start of the <columnDef> tag.
	 * 
	 * @param inName java.lang.String
	 * @param inAttributes org.xml.sax.Attributes
	 */
	private void start_columnDef(String inName, Attributes inAttributes) {
		DefaultNameValueList lList = createAttributesList(inAttributes);
		try {
			switch (columnDefState.getState()) {
				case ColumnDefState.COLUMN_DEF:
					objectDef.addColumnDef(lList);
					break;
				case ColumnDefState.JOIN_CONDITION:
					currentJoinDef.addColumnDef(retrieveColumnName(lList));
					break;
				case ColumnDefState.OBJECT_NESTED:
					nestedDef.addColumnDef(lList);
					break;
				case ColumnDefState.RESULT_GROUPING:
					groupingDef.addColumnDef(lList);
					break;
				default :
					objectDef.addColumnDef(lList);
					break;
			}
		}
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);
		}
	}
	
	private String retrieveColumnName(NameValueList inList) throws VInvalidNameException {
		String outColumnName = objectDef.getColumnName(inList);
		if (inList.hasValue(ColumnDefDef.nestedObject)) {
			String lTableName = (String)inList.getValue(ColumnDefDef.nestedObject);
			outColumnName = lTableName + outColumnName.substring(outColumnName.indexOf("."));
		}
		return outColumnName;
	}
	
	/**
	 * Handles the start of the <hidden> tag.
	 * 
	 * @param inName java.lang.String
	 * @param inAttributes org.xml.sax.Attributes
	 */
	private void start_hidden(String inName, Attributes inAttributes) {
		DefaultNameValueList lList = createAttributesList(inAttributes);		
		objectDef.addHidden(lList);
	}
	
	private DefaultNameValueList createAttributesList(Attributes inAttributes) {
		DefaultNameValueList outList = new DefaultNameValueList();
		for (int i = 0; i < inAttributes.getLength(); i++) {
			try {
				outList.setValue(inAttributes.getQName(i), inAttributes.getValue(i));
			} 
			catch (Exception exc) {
				DefaultExceptionWriter.printOut(this, exc, true);
			}	
		}
		return outList;
	}
	
	/**
	 * Handles the start of the <joinCondition> tag.
	 * 
	 * @param inName java.lang.String
	 * @param inAttributes org.xml.sax.Attributes
	 */
	private void start_joinCondition(String inName, Attributes inAttributes) {
	
		// There must be something wrong in the definition
		if (VSys.assertNotNull(this, "start_joinCondition", currentJoinDef) == Assert.FAILURE)
			return;
	
		columnDefState.setToJoinCondition();
		currentJoinDef.addJoinCondition(inAttributes.getValue(0));
	}
	
	/**
	 * Handles the start of the <joinDef> tag.
	 * 
	 * @param inName java.lang.String
	 * @param inAttributes org.xml.sax.Attributes
	 */
	private void start_joinDef( String inName, Attributes inAttributes ) {
	
		// There must be something wrong in the definition
		if (VSys.assertNotNull(this, "start_joinDef", objectDef) == Assert.FAILURE) 
			 return;
	
		String lJoinType = (inAttributes != null) ? inAttributes.getValue(JoinDefDef.joinType) : null;
	
		JoinDef lJoinDef = new JoinDefImpl();
		try {
			lJoinDef.set(JoinDefDef.joinType, lJoinType);
		}
		catch (SettingException exc) {
			DefaultExceptionWriter.printOut(this, exc, true);
		}
		
		if (currentJoinDef == null) {
			objectDef.setJoinDef(lJoinDef);
		}
		else {
			lJoinDef.setParentJoinDef(currentJoinDef);
		}
		currentJoinDef = lJoinDef;
	}
	
	/**
	 * Handles the start of the <joinedObjectDef> tag.
	 * 
	 * @param inName java.lang.String
	 * @param inAttributes org.xml.sax.Attributes
	 */
	synchronized private void start_objectDef( String inName, Attributes inAttributes ) {
	
		this.objectDef = new JoinedObjectDefImpl();
		for (int i = 0; i < inAttributes.getLength(); i++) {
			try {
				objectDef.set(inAttributes.getQName(i), inAttributes.getValue(i).intern());
			} 
			catch (Exception exc) {
				DefaultExceptionWriter.printOut(this, exc, true);
			}	
		}
	}
	
	/**
	 * Handles the start of the <objectDesc> tag.
	 * 
	 * @param inName java.lang.String
	 * @param inAttributes org.xml.sax.Attributes
	 */
	private void start_objectDesc(String inName, Attributes inAttributes) {
	
		// There must be something wrong in the definition
		if (VSys.assertNotNull(this, "start_objectDesc", currentJoinDef) == Assert.FAILURE)
			return;
	
		String lObjectClassName = (inAttributes != null) ? inAttributes.getValue(ObjectDescDef.objectClassName) : null ;
		currentJoinDef.setTableName(objectDef.getTableName(lObjectClassName));
	}
	
	/**
	 * Handles the start of the <objectNested> tag.
	 * 
	 * @param inName java.lang.String
	 * @param inAttributes org.xml.sax.Attributes
	 */
	private void start_objectNested(String inName, Attributes inAttributes) {
		// There must be something wrong in the definition
		if (VSys.assertNotNull(this, "start_objectNested", currentJoinDef) == Assert.FAILURE) 
			return;
		
		String lNestingName = (inAttributes != null) ? inAttributes.getValue(NestedDefDef.name) : null;
		nestedDef = new NestedDefImpl(lNestingName);
		objectDef.addNestedDef(nestedDef);
		columnDefState.setToObjectNested();
	}
	
	/**
	 * Handles the start of the <resultGrouping> tag.
	 * 
	 * @param inName java.lang.String
	 * @param inAttributes org.xml.sax.Attributes
	 */
	private void start_resultGrouping(String inName, Attributes inAttributes) {
		// There must be something wrong in the definition
		if (VSys.assertNotNull(this, "start_resultGrouping", nestedDef) == Assert.FAILURE) 
			return;

		String lModifierType = (inAttributes != null) ? inAttributes.getValue(GroupingDefDef.modifier) : null;
		groupingDef = new GroupingDefImpl(lModifierType);
		nestedDef.addGroupingDef(groupingDef);
		columnDefState.setToResultGrouping();
	}
	
	/**
	 * Handles the start of the <objectPlaceholder> tag.
	 * 
	 * @param inName java.lang.String
	 * @param inAttributes org.xml.sax.Attributes
	 */
	private void start_objectPlaceholder(String inName, Attributes inAttributes) {
		// There must be something wrong in the definition
		if (VSys.assertNotNull(this, "start_objectPlaceholder", currentJoinDef) == Assert.FAILURE) 
			return;
		
		String lPlaceholderName = (inAttributes != null) ? inAttributes.getValue(PlaceholderDefDef.name) : null;
		currentJoinDef.addPlaceholderDef(new PlaceholderDefImpl(lPlaceholderName));
	}
	
	/**
	 * 	@exception org.xml.sax.SAXException
	 */
	public void startDocument() throws SAXException {
		columnDefState = new ColumnDefState();
		columnDefState.setToColumnDefs();
		currentJoinDef = null;
	}
	
	/**
	 * Receive notification of the start of an element.
	 *
	 * @param inUri java.lang.String The Namespace URI.
	 * @param inLocalName java.lang.String The local name.
	 * @param inRawName java.lang.String The qualified (prefixed) name.
	 * @param inAttributes org.xml.sax.Attributes The specified or defaulted attributes.
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly wrapping another exception.
	 * @see org.xml.sax.ContentHandler#startElement
	 */
	public void startElement(String inUri, String inLocalName, String inRawName, Attributes inAttributes) throws SAXException {
	
		// make sure the string is internalized to compare references
		if (inRawName != null )
			inRawName = inRawName.intern(); 
	
		// tag <joinedObjectDef>	 
		if (inRawName == ModelObject.joinedObjectDef) {
			 start_objectDef(inRawName, inAttributes);
			 return ; 
		}
		// tag <columnDef>	 
		if (inRawName == ModelObject.columnDef) {
			 start_columnDef(inRawName, inAttributes);
			 return ; 
		}
		// tag <hidden>
		if (inRawName == ModelObject.hidden) {
			 start_hidden(inRawName, inAttributes);
			 return; 			
		}
		// tag <joinDef>	 
		if (inRawName == ModelObject.joinDef) {
			 start_joinDef(inRawName, inAttributes);
			 return ; 
		}
		// tag <objectDesc>	 
		if (inRawName == ModelObject.objectDesc) {
			 start_objectDesc(inRawName, inAttributes);
			 return ; 
		}
		// tag <joinCondition>	 
		if (inRawName == ModelObject.joinCondition) {
			 start_joinCondition(inRawName, inAttributes);
			 return ; 
		}
		// tag <objectNested>
		if (inRawName == ModelObject.objectNested) {
			start_objectNested(inRawName, inAttributes);
			return;
		}
		// tag <resultGrouping >
		if (inRawName == ModelObject.resultGrouping) {
			start_resultGrouping(inRawName, inAttributes);
			return;
		}
		// tag <objectPlaceholder>
		if (inRawName == ModelObject.objectPlaceholder) {
			start_objectPlaceholder(inRawName, inAttributes);
			return;
		}
	}
}
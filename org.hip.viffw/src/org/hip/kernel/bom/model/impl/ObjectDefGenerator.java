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
package org.hip.kernel.bom.model.impl;

import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import org.hip.kernel.sys.VSys;
import org.hip.kernel.sys.Assert;
import org.hip.kernel.bom.model.IMappingDefCreator;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.KeyDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.bom.model.RelationshipDef;
import org.hip.kernel.bom.model.ModelObject;
import org.hip.kernel.bom.model.KeyDefDef;
import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.exc.DefaultExceptionWriter;

/**
 * This class is used to generate ObjectDefs
 *
 * @author Benno Luthiger
 */
public class ObjectDefGenerator extends DefaultHandler {
	
	// Class variables 
	private static ObjectDefGenerator singleton = null;
		
	// Instance variables
	private		ObjectDef		objectDef				= null;
	private		KeyDef			currentKeyDef			= null;
	private		PropertyDef		currentPropertyDef		= null;
	private		XMLReader		parser					= null;
	private		int				keyItemIndex			= 0;
	
	private IMappingDefCreator mappingDefCreator = new DefaultMappingDefCreator();
	
	/**
	 * ObjectDefGenerator default constructor.
	 */
	private ObjectDefGenerator() {
		super();
	}
	
	/**
	 * Creates a ObjectDef from a definition string.
	 * 
	 * @param inDefinitionString java.lang.String
	 * @return org.hip.kernel.bom.model.ObjectDef
	 * @throws SAXException
	 */
	public ObjectDef createObjectDef(String inDefinitionString) throws SAXException {	
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
	 * Creates a ObjectDef from a definition string using the specified <code>IMappingDefCreator</code>.
	 * 
	 * @param inDefinitionString String
	 * @param inMappingDefCreator IMappingDefCreator
	 * @return ObjectDef 
	 * @throws SAXException
	 */
	public ObjectDef createObjectDef(String inDefinitionString, IMappingDefCreator inMappingDefCreator) throws SAXException {
		IMappingDefCreator lOldCreator = mappingDefCreator;
		if (inMappingDefCreator != null) {
			mappingDefCreator = inMappingDefCreator;
		}
		try {
			createObjectDef(inDefinitionString);
		}
		finally {
			mappingDefCreator = lOldCreator;
		}
		return objectDef;
	}
	
//	---
	
	/**
	 * 	Handles the end of the <keyDef> tag.
	 * 
	 * 	@param inName java.lang.String
	 */
	private void end_keyDef( String inName ) {
	}
	/**
	 * 	Handles the end of the </keyItemDef> tag.
	 * 
	 * 	@param name java.lang.String
	 */
	private void end_keyItemDef( String inName ) {
		this.keyItemIndex = 0 ;
	}
	
	/**
	 * 	Handles the end of the <mappingDef> tag.
	 * 
	 * 	@param inName java.lang.String
	 */
	private void end_mappingDef( String inName ) {
	}
	
	/**
	 * Handles the end of the <objectDef> tag.
	 * 
	 * @param inName java.lang.String
	 */
	private void end_objectDef( String inName ) {
	}
	
	/**
	 * Handles the end of the <propertyDef> tag.
	 * 
	 * @param inName java.lang.String
	 */
	private void end_propertyDef( String inName ) {
		currentPropertyDef = null ;
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
			
		if ( inRawName != null )
			// make sure the string is internalized to compare references
			 inRawName = inRawName.intern()	; 
	
		// tag <objectDef>	 
		if ( inRawName == ModelObject.objectDef ){
			 end_objectDef( inRawName ) ;
			 return ; 
		}
		// tag <propertyDef>	 
		if ( inRawName == ModelObject.propertyDef ) { 
			 end_propertyDef( inRawName ) ;
			 return ;
		}
		// tag <mappingDef>	 
		if ( inRawName == ModelObject.mappingDef ) { 
			 end_mappingDef( inRawName ) ;
			 return ;
		}	
		// tag <keyDef>	 
		if ( inRawName == ModelObject.keyDef ) { 
			 end_keyDef( inRawName ) ;
			 return ;
		}	
		// tag <keyItemDef>	 
		if ( inRawName == ModelObject.keyItemDef ) { 
			 end_keyItemDef( inRawName ) ;
			 return ;
		}	
	}
	
	/**
	 * @return org.hip.kernel.bom.model.impl.ObjectDefGenerator
	 */
	public static ObjectDefGenerator getSingleton() {
	
		if (singleton == null)
			 singleton = new ObjectDefGenerator();
		
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
	 * 	This method handles the keyDef tag.
	 * 
	 * 	@param inName java.lang.String
	 * 	@param inAttributes org.xml.sax.Attributes
	 */
	private void start_keyDef( String inName, Attributes inAttributes ) {
	
		// There must be something wrong in the definition
		if (VSys.assertNotNull(this, "start_keyDef", objectDef) == Assert.FAILURE)
			return;
	
		String lType = (inAttributes != null) ? inAttributes.getValue(KeyDefDef.keyType) : null ;
		
		if ( (lType == KeyDef.TYPE_PRIMARY_KEY) || (lType == null) )
		     currentKeyDef = new PrimaryKeyDefImpl();
	
		((ObjectDefImpl) objectDef).setPrimaryKeyDef(currentKeyDef);
	}
	
	/**
	 * 	This method handles the keyItemDef tag.
	 * 
	 * 	@param inName java.lang.String
	 * 	@param inAttributes org.xml.sax.Attributes
	 */
	private void start_keyItemDef( String inName, Attributes inAttributes ) {
	
		// There must be something wrong in the definition
		if (VSys.assertNotNull(this, "start_keyItemDef", currentKeyDef) == Assert.FAILURE)
			return;
	
		this.currentKeyDef.addKeyNameAt(inAttributes.getValue(KeyDef.keyPropertyName).intern(), this.keyItemIndex++);
	}
	
	/**
	 * 	This method handles the mappingDef tag.
	 * 
	 * 	@param inName java.lang.String
	 * 	@param inAttributes org.xml.sax.Attributes
	 */
	private void start_mappingDef( String inName, Attributes inAttributes ) {
	
		// There must be something wrong in the definition
		if (VSys.assertNotNull(this, "start_mappingDef", currentPropertyDef) == Assert.FAILURE)
			return;
	
		// Create new Mapping
		MappingDef lMappingDef = mappingDefCreator.createMappingDef(); 
		for ( int i = 0; i < inAttributes.getLength(); i++ ) {
			try {
				lMappingDef.set(inAttributes.getQName(i), inAttributes.getValue(i));
			} 
			catch ( Exception exc ) {
				DefaultExceptionWriter.printOut(this, exc, true);
			}
		} // for
		currentPropertyDef.setMappingDef(lMappingDef);
	}
	
	/**
	 * Handles the start of the <objectDef> tag.
	 *
	 * @param inName java.lang.String
	 * @param inAttributes org.xml.sax.Attributes
	 */
	synchronized private void start_objectDef( String inName, Attributes inAttributes ) {
	
		this.objectDef = new ObjectDefImpl();
		for ( int i = 0; i < inAttributes.getLength(); i++ ) {
			try {
				objectDef.set(inAttributes.getQName(i), inAttributes.getValue(i).intern());
			} 
			catch ( Exception exc ) {
				DefaultExceptionWriter.printOut(this, exc, true);
			}	
		}
	}
	
	/**
	 * Handles the start of the <propertyDef> tag.
	 * 
	 * @param inName java.lang.String
	 * @param inAttributes org.xml.sax.Attributes
	 */
	private void start_propertyDef( String inName, Attributes inAttributes ) {
	
		currentPropertyDef = new PropertyDefImpl();
		for ( int i = 0; i < inAttributes.getLength(); i++ ) {
			try {
				currentPropertyDef.set(inAttributes.getQName(i), inAttributes.getValue(i).intern());
			} 
			catch ( Exception exc ) {
				DefaultExceptionWriter.printOut(this, exc, true);
			}	
		}
		objectDef.addPropertyDef(currentPropertyDef);	
	}
	
	/**
	 * 	This method handles the <relationshipDef> tag.
	 * 
	 * 	@param inName java.lang.String
	 * 	@param inAttributes org.xml.sax.Attributes
	 */
	private void start_relationshipDef( String inName, Attributes inAttributes ) {
	
		// There must be something wrong in the definition
		if (VSys.assertNotNull(this, "start_relationshipDef", currentPropertyDef) == Assert.FAILURE)
			return;
	
		// Create new Mapping
		RelationshipDef lRelationshipDef = new RelationshipDefImpl();
		for ( int i = 0; i < inAttributes.getLength(); i++ ) {
			try {
				lRelationshipDef.set(inAttributes.getQName(i), inAttributes.getValue(i).intern());
			} 
			catch ( Exception exc ) {
				DefaultExceptionWriter.printOut(this, exc, true);
			}
			
		} // for
		currentPropertyDef.setRelationshipDef(lRelationshipDef);
	}
	
	/**
	 * 	@exception org.xml.sax.SAXException
	 */
	public void startDocument() throws SAXException {
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
	public void startElement (String inUri, String inLocalName, String inRawName, Attributes inAttributes) throws SAXException {
	
		// make sure the string is internalized to compare references
		if ( inRawName != null )
			inRawName = inRawName.intern()	; 
	
		// tag <objectDef>	 
		if ( inRawName == ModelObject.objectDef ) {
			 start_objectDef( inRawName, inAttributes ) ;
			 return ; 
		}
		// tag <propertyDef>	 
		if ( inRawName == ModelObject.propertyDef ) { 
			 start_propertyDef( inRawName, inAttributes ) ;
			 return ;
		}
		// tag <mappingDef>	 
		if ( inRawName == ModelObject.mappingDef ) { 
			 start_mappingDef( inRawName, inAttributes ) ;
			 return ;
		}	
		// tag <keyDef>	 
		if ( inRawName == ModelObject.keyDef ) { 
			 start_keyDef( inRawName, inAttributes ) ;
			 return ;
		}	
		// tag <keyItemDef>	 
		if ( inRawName == ModelObject.keyItemDef ) { 
			 start_keyItemDef( inRawName, inAttributes ) ;
			 return ;
		}
		// tag <relationshipDef>
		if ( inRawName == ModelObject.relationshipDef ) { 
			 start_relationshipDef( inRawName, inAttributes ) ;
			 return ;
		}
	}
	
//	---
	
	private class DefaultMappingDefCreator implements IMappingDefCreator {
		public MappingDef createMappingDef() {
			return new MappingDefImpl();
		}		
	}
	
}

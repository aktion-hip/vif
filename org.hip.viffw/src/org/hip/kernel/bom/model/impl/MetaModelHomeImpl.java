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

import org.hip.kernel.bom.model.GroupingDefDef;
import org.hip.kernel.bom.model.MetaModelHome;
import org.hip.kernel.bom.model.NestedDefDef;
import org.hip.kernel.bom.model.ObjectDefDef;
import org.hip.kernel.bom.model.KeyDefDef;
import org.hip.kernel.bom.model.PlaceholderDefDef;
import org.hip.kernel.bom.model.PropertyDefDef;
import org.hip.kernel.bom.model.RelationshipDefDef;
import org.hip.kernel.bom.model.MappingDefDef;
import org.hip.kernel.bom.model.MetaModelObject;
import org.hip.kernel.bom.model.JoinDefDef;
import org.hip.kernel.bom.model.JoinedObjectDefDef;

/**
 * 	Creates MetaModelObjects. As we are on a meta-meta-level,
 *	much is hard-coded. There is no further abstraction level.
 *	But, for the moment, this is enough.
 * 
 * 	@author		Benno Luthiger
 * 	@see		org.hip.kernel.bom.model.MetaModelHome
 */
public class MetaModelHomeImpl implements MetaModelHome {

	// Class Variables
	static private MetaModelHome singleton = null ;

	// Instance variables
	private	ObjectDefDef		objectDefDef		= null;
	private KeyDefDef			keyDefDef			= null;
	private	PropertyDefDef		propertyDefDef		= null;
	private	RelationshipDefDef	relationshipDefDef	= null;
	private MappingDefDef		mappingDefDef		= null;
	private JoinDefDef			joinDefDef			= null;
	private JoinedObjectDefDef	joinedObjectDefDef	= null;
	private GroupingDefDef		groupingDefDef		= null;
	private NestedDefDef		nestedDefDef		= null;
	private PlaceholderDefDef   placeholderDefDef	= null;

	/**
	 * MetaModelHomeImpl default constructor.
	 */
	public MetaModelHomeImpl() {
		super();
	}
	/**
	 * 	Returns the definition of the JoinDef.
	 * 
	 * 	@return org.hip.kernel.bom.model.JoinDefDef
	 */
	public JoinDefDef getJoinDefDef() {
	
		if ( joinDefDef == null )
			 joinDefDef =  new JoinDefDefImpl() ;
			 
		return joinDefDef ;
	}
	/**
	 * 	Returns the definition of the JoinedObjectDef.
	 * 
	 * 	@return org.hip.kernel.bom.model.JoinedObjectDefDef
	 */
	public JoinedObjectDefDef getJoinedObjectDefDef() {
	
		if ( joinedObjectDefDef == null )
			 joinedObjectDefDef =  new JoinedObjectDefDefImpl();
			 
		return joinedObjectDefDef ;
	}
	/**
	 * Returns the KeyDefDef.
	 * 
	 * @return org.hip.kernel.bom.model.MappingDefDef
	 */
	public KeyDefDef getKeyDefDef() {
	
		if ( keyDefDef == null )
			 keyDefDef =  new KeyDefDefImpl() ;
			 
		return keyDefDef ;
	}
	/**
	 * Returns the MappingDefDef.
	 * 
	 * @return org.hip.kernel.bom.model.MappingDefDef
	 */
	public MappingDefDef getMappingDefDef() {
	
		if ( mappingDefDef == null )
			 mappingDefDef =  new MappingDefDefImpl() ;
			 
		return mappingDefDef ;
	}
	/**
	 * 	Returns null.
	 */
	public MetaModelObject getMetaModelObject(String modelObjectType) {
		return null ;
	}
	/**
	 * 	Returns a ObjectDefDef.
	 * 
	 * 	@return org.hip.kernel.bom.model.ObjectDefDef
	 */
	public ObjectDefDef getObjectDefDef() {
	
		if ( objectDefDef == null )
		     objectDefDef =  new ObjectDefDefImpl()	;
		     
		return objectDefDef ;
	}
	/**
	 * Returns a PropertyDefDef.
	 * 
	 * @return org.hip.kernel.bom.model.PropertyDefDef
	 */
	public PropertyDefDef getPropertyDefDef() {
	
		if ( propertyDefDef == null )
		     propertyDefDef =  new PropertyDefDefImpl() ;
		     
		return propertyDefDef ;
	}
	/**
	 * Returns the RelationshipDefDef.
	 * 
	 * @return org.hip.kernel.bom.model.MappingDefDef
	 */
	public RelationshipDefDef getRelationshipDefDef() {
		if ( relationshipDefDef == null )
			 relationshipDefDef =  new RelationshipDefDefImpl() ;
			 
		return relationshipDefDef;
	}
	
	/**
	 * Returns the GroupingDefDef.
	 * 
	 * @return GroupingDefDef
	 */
	public GroupingDefDef getGroupingDefDef() {
		if (groupingDefDef == null) 
			groupingDefDef = new GroupingDefDefImpl();
		
		return groupingDefDef;
	}

	/**
	 * Returns the NestedDefDef.
	 * 
	 * @return NestedDefDef
	 */
	public NestedDefDef getNestedDefDef() {
		if (nestedDefDef == null) 
			nestedDefDef = new NestedDefDefImpl();
		
		return nestedDefDef;
	}
	
	/**
	 * Returns the definition of the PlaceholderDefDef.
	 * 
	 * @return PlaceholderDefDef
	 */
	public PlaceholderDefDef getPlaceholderDefDef() {
		if (placeholderDefDef == null)
			placeholderDefDef = new PlaceholderDefDefImpl();
		return placeholderDefDef;
	}

	/**
	 * 	@return org.hip.kernel.bom.model.MetaModelHome
	 */
	public static synchronized MetaModelHome getSingleton() {
	
		if (singleton == null)
			singleton =  new MetaModelHomeImpl();
			 
		return singleton;
	}
}

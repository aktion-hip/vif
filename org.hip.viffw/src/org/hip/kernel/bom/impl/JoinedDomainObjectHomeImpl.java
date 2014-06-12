package org.hip.kernel.bom.impl;

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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.BOMInvalidKeyException;
import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DBAdapterJoin;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.GroupByObject;
import org.hip.kernel.bom.HavingObject;
import org.hip.kernel.bom.JoinedDomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.LimitObject;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.ReadOnlyDomainObject;
import org.hip.kernel.bom.impl.PlacefillerCollection.Placefiller;
import org.hip.kernel.bom.model.JoinDef;
import org.hip.kernel.bom.model.JoinedObjectDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.impl.JoinedObjectDefGenerator;
import org.hip.kernel.exc.DefaultExceptionWriter;
import org.hip.kernel.sys.Assert;
import org.hip.kernel.sys.VSys;

/**
 * This class implements the responsibilities of "Homes" for joined
 * domain objects.
 * 
 * @author: Benno Luthiger
 */
abstract public class JoinedDomainObjectHomeImpl extends AbstractDomainObjectHome implements JoinedDomainObjectHome {
	private ObjectDef 				objectDef 		= null;
	private JoinedObjectDef 		joinedObjectDef = null;
	private ReadOnlyDomainObject	temporary		= null;
	private DBAdapterJoin 			dbAdapter		= null;
	
	/**
	 * JoinedDomainObjectHomeImpl default constructor.
	 */
	protected JoinedDomainObjectHomeImpl() {
		super();
		dbAdapter = getDBAdapter();
	}

	/**
	 * This method can be implemented by concrete subclasses to
	 * create test objects.
	 *
	 * @return java.util.Vector
	 */
	protected Vector<Object> createTestObjects() {
		return null;
	}
	
	/**
	 * This method creates a new instance of a DomainObject
	 *
	 * @return org.hip.kernel.bom.ReadOnlyDomainObject
	 */
	public ReadOnlyDomainObject create() throws BOMException {
		ReadOnlyDomainObject outDomainObject = (ReadOnlyDomainObject)newInstance() ;
		((DomainObjectImpl)outDomainObject).initializeForNew();
		return outDomainObject;
	}
	
	/**
	 * Creates the select string to fetch all domain objects. 
	 *
	 * @return java.lang.String
	 * @throws org.hip.kernel.bom.BOMException
	 */
	protected String createSelectAllString() throws BOMException {
		return dbAdapter.createSelectAllSQL();
	}
	
	/**
	 * Creates the select string to fetch all domain objects matching
	 * the specified key.
	 *
	 * @return java.lang.String
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @throws org.hip.kernel.bom.BOMException
	 */
	protected String createSelectString(KeyObject inKey) throws BOMException {
		return dbAdapter.createSelectSQL(inKey, this);
	}
	
	/**
	 * Creates the select string to fetch all domain objects matching
	 * the specified key ordered by the specified object.
	 *
	 * @return java.lang.String
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @param inOrder org.hip.kernel.bom.OrderObject
	 * @throws org.hip.kernel.bom.BOMException
	 */
	protected String createSelectString(KeyObject inKey, OrderObject inOrder) throws BOMException {
		return dbAdapter.createSelectSQL(inKey, inOrder, this);
	}
	
	/**
	 * Creates the select string to fetch all domain objects matching
	 * the specified key and meeting the specified having criterion
	 * sorted by the specified order.
	 * Note: To specify a clause only using e.g. the HAVING part, you can
	 * provide empty objects for key and order.
	 * 
	 * @param inKey KeyObject
	 * @param inOrder OrderObject
	 * @param inHaving HavingObject
	 * @return String
	 * @throws BOMException
	 */
	protected String createSelectString(KeyObject inKey, OrderObject inOrder, HavingObject inHaving) throws BOMException {
		return dbAdapter.createSelectSQL(inKey, inOrder, inHaving, this);
	}	
	
	/**
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @param inOrder org.hip.kernel.bom.OrderObject
	 * @param inHaving org.hip.kernel.bom.HavingObject
	 * @param inGroupBy org.hip.kernel.bom.GroupByObject
	 * @return java.lang.String
	 */
	protected String createSelectString(KeyObject inKey, OrderObject inOrder, HavingObject inHaving, GroupByObject inGroupBy) throws BOMException {
		return dbAdapter.createSelectSQL(inKey, inOrder, inHaving, inGroupBy, this);
	}
	
	/**
	 * Creates the select string to fetch all domain objects 
	 * ordered by the specified object.
	 *
	 * @return java.lang.String
	 * @param inOrder org.hip.kernel.bom.OrderObject
	 * @throws org.hip.kernel.bom.BOMException
	 */
	protected String createSelectString(OrderObject inOrder) throws BOMException {
		return dbAdapter.createSelectSQL(inOrder, this);
	}

	/**
	 * Creates the select string to fetch all domain objects matching
	 * the specified key limitied by the specified limit.
	 *  
	 * @param inKey KeyObject
	 * @param inLimit LimitObject
	 * @return String
	 * @throws BOMException
	 */	
	protected String createSelectString(KeyObject inKey, LimitObject inLimit) throws BOMException {
		return dbAdapter.createSelectSQL(inKey, inLimit, this);
	}
	
	/**
	 * Creates the select sql string counting all table entries 
	 * corresponding to this home.
	 *
	 * @return java.lang.String
	 * @throws org.hip.kernel.bom.BOMException
	 */
	protected String createCountAllString() throws BOMException {
		return dbAdapter.createCountAllSQL();
	}
	
	/**
	 * Creates the select sql string counting all table entries 
	 * corresponding to this home and the specified key.
	 *
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @return java.lang.String
	 * @throws org.hip.kernel.bom.BOMException
	 */
	protected String createCountString(KeyObject inKey) throws BOMException {
		return dbAdapter.createCountSQL(inKey, this);
	}
	protected String createCountString(KeyObject inKey, HavingObject inHaving, GroupByObject inGroupBy) throws BOMException {
		return dbAdapter.createCountSQL(inKey, inHaving, inGroupBy, this);
	}
	
	/**
	 * This method looks for all key columns of the table mapped to the
	 * DomainObject managed by this home and creates a list COUNT(keyField).
	 * Instead of sending SELECT COUNT(*) FROM ... this SQL-sequence can be
	 * used to count all entries in a table with better performance.
	 *
	 * @return java.lang.String
	 */
	protected String createKeyCountColumnList() {
		String lKeyCountColumnList = "";
		return lKeyCountColumnList;
	}
	
	/**
	 * @return org.hip.kernel.bom.model.JoinedObjectDef
	 */
	private synchronized JoinedObjectDef createObjectDef() {
		try {
			JoinedObjectDef outVal = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef( getObjectDefString() );
			return outVal;
		} 
		catch (Exception exc) {
			DefaultExceptionWriter.printOut(this, exc, true);
			return null ;		
		}	
	}
	
	/**
	 * Use this method to find a DomainObject by its key.
	 *
	 * @return org.hip.kernel.bom.ReadOnlyDomainObject
	 * @param inKey org.hip.kernel.bom.KeyObject
	 * @throws org.hip.kernel.bom.BOMNotFoundException
	 * @throws org.hip.kernel.bom.BOMInvalidKeyException
	 */
	public ReadOnlyDomainObject findByKey(KeyObject inKey) throws BOMNotFoundException, BOMInvalidKeyException {
	
		// Pre: inKey not null
		if (VSys.assertNotNull(this, "findByKey", inKey) == Assert.FAILURE)
			return null;
	
		ReadOnlyDomainObject outDomainObject = null;
		
		if (getUseCache() && inKey.isPrimaryKey()){
			outDomainObject = (ReadOnlyDomainObject)cache().get(inKey);
		}
		if (outDomainObject == null) {	
			//not in the cache, we fetch it from database
			QueryResult lResult = null;
			try { 	 
				lResult = this.select(inKey);
				outDomainObject = (ReadOnlyDomainObject)lResult.nextAsDomainObject() 	;
				lResult.close();
				if ( outDomainObject == null ) {
					throw new BOMNotFoundException();
				}
				if (getUseCache() && inKey.isPrimaryKey()) { //cache it
					cache().put(outDomainObject);
				}
			}
			catch (SQLException exc){
				throw new BOMInvalidKeyException(exc.toString());
			}
			catch (BOMException exc){
				throw new BOMInvalidKeyException(exc.toString());
			}
		}
	
		return outDomainObject;
	}
	
	/**
	 * 	Returns the joined object definition.
	 * 
	 * 	@return org.hip.kernel.bom.model.JoinedObjectDef
	 */
	private JoinedObjectDef getJoinedObjectDef() {
	
		if (joinedObjectDef == null) {
			 joinedObjectDef = createObjectDef();
		}	
		return joinedObjectDef;
	}
	
	/**
	 * 	Returns the object definition for the class managed
	 *	by this home.
	 * 
	 * 	@return ObjectDef
	 */
	public ObjectDef getObjectDef() {
	
		if (objectDef == null) {
			 objectDef = getJoinedObjectDef().getDomainObjectDef();	
		}	
		return objectDef;
	}
	
	/**
	 * Returns the mappings of hidden fields or an empty String.
	 * 
	 * @param inPropertyName String
	 * @return String
	 */
	public String getHidden(String inPropertyName) {
		return getJoinedObjectDef().getHidden(inPropertyName);
	}
	
	/**
	 * 	The temporary can used if the client does not use it for long.
	 * 
	 * 	@return org.hip.kernel.bom.ReadOnlyDomainObject
	 */
	protected ReadOnlyDomainObject getTemporary() throws BOMException {
		if (temporary == null)
			 temporary =  newInstance();
		return temporary;
	}
	
	private DBAdapterJoin getDBAdapter() {
		if (dbAdapter == null) {
			dbAdapter = retrieveDBAdapterType().getJoinDBAdapter(getJoinedObjectDef());
		}
		return dbAdapter;
	}
	
	/**
	 * 	Returns an empty DomainObject. An empty object is
	 *	initialized but does not contain any values.
	 *	The object goes back into this state after releasing.
	 * 
	 * 	@return org.hip.kernel.bom.ReadOnlyDomainObject
	 */
	public ReadOnlyDomainObject newInstance() throws BOMException {
		try { 
			//straightforward
			Iterator<GeneralDomainObject> lReleased = releasedObjects().iterator();
			if (lReleased.hasNext()) {
				ReadOnlyDomainObject outDomainObject = (ReadOnlyDomainObject)lReleased.next();
				lReleased.remove();
				return outDomainObject;
			} 
			else {
				Class<?> lClass = Class.forName(getObjectClassName());
				return (ReadOnlyDomainObject)lClass.newInstance();
			}	
		} 
		catch (Exception exc) {
			throw new BOMException(exc.getMessage());
		}	
	}
	
	/**
	 * 	Returns a GeneralDomainObject filled with the values of the inputed ResultSet
	 * 
	 * 	@return org.hip.kernel.bom.GeneralDomainObject
	 *  @param inResult java.sql.ResultSet
	 */
	protected GeneralDomainObject newInstance(ResultSet inResult) throws BOMException {
	
		try {
			DomainObjectImpl retVal = (DomainObjectImpl)this.newInstance();
			retVal.loadFromResultSet(inResult);
			return retVal;		
		} 
		catch (Exception exc) {
			throw new BOMException("getEmpty");
		}
	}
	
	public QueryResult select(PlacefillerCollection inPlacefillers) throws SQLException, BOMException {
		processPlacefillers(inPlacefillers);
		return select();
	}
	
	public QueryResult select(KeyObject inKey, PlacefillerCollection inPlacefillers) throws SQLException, BOMException {
		processPlacefillers(inPlacefillers);
		return select(inKey);
	}
	
	public QueryResult select(KeyObject inKey, OrderObject inOrder, PlacefillerCollection inPlacefillers) throws SQLException, BOMException {
		processPlacefillers(inPlacefillers);
		return select(inKey, inOrder);
	}
	
	private void processPlacefillers(PlacefillerCollection inPlacefillers) throws BOMException {
		for (Iterator<Placefiller> lPlacefillers = inPlacefillers.iterator(); lPlacefillers.hasNext();) {
			PlacefillerCollection.Placefiller lPlacefiller = (PlacefillerCollection.Placefiller)lPlacefillers.next();
			String lAlias = lPlacefiller.alias;
			StringBuffer lSQL = new StringBuffer("(");
			lSQL.append(((AbstractDomainObjectHome)lPlacefiller.home).createSelectString(lPlacefiller.key)).append(")");
			lSQL.append(" AS ").append(lAlias);
			JoinDef lPlaceholdersDef = traverseJoinDefs(joinedObjectDef.getJoinDef(), lAlias);
			if (lPlaceholdersDef != null) {
				lPlaceholdersDef.fillPlaceholder(lAlias, new String(lSQL));
			}
		}
		if (inPlacefillers.size() > 0) {
			dbAdapter.reset();
		}
	}
	
	private JoinDef traverseJoinDefs(JoinDef inJoinDef, String inAlias) throws BOMException {
		if (inJoinDef.hasPlaceholder(inAlias)) {
			return inJoinDef;
		}
		else {
			JoinDef lJoinDef = inJoinDef.getChildJoinDef();
			if (lJoinDef == null) return null;
			return traverseJoinDefs(lJoinDef, inAlias);
		}
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(objectDef);
		out.writeObject(joinedObjectDef);
		out.writeObject(temporary);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		objectDef = (ObjectDef)in.readObject();
		joinedObjectDef = (JoinedObjectDef)in.readObject();
		temporary = (ReadOnlyDomainObject)in.readObject();
		dbAdapter = getDBAdapter();
	}	
	
}
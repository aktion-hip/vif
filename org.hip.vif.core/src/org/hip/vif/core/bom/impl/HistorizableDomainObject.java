/*
	This package is part of the application VIF.
	Copyright (C) 2003, Benno Luthiger

	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package org.hip.vif.core.bom.impl;

import java.sql.Timestamp;
import java.util.Iterator;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.vif.core.exc.BOMChangeValueException;

/**
 * This abstract class provides generic functionality for historizing data.
 * 
 * Created on 31.05.2003
 * @author Luthiger
 */
@SuppressWarnings("serial")
public abstract class HistorizableDomainObject extends DomainObjectImpl {

	/**
	 * Fills the specified history with the values of the actual domain object. 
	 * 
	 * @param inHistory DomainObject
	 * @throws BOMChangeValueException
	 */
	protected void fillHistory(DomainObject inHistory) throws BOMChangeValueException {
		try {
			for (Iterator<String> lNames = getPropertyNames(); lNames.hasNext();) {
				String lPropertyName = (String)lNames.next();
				inHistory.set(lPropertyName, this.get(lPropertyName));
			}
		}
		catch (Exception exc){
			throw new BOMChangeValueException("HistorizableDomainObject.fillHistory:  " + exc.getMessage());
		}
	}
	
	/**
	 * Creates a new history entry with the values of the actual domain object.
	 * 
	 * @param inHistory DomainObject the history object to fill
	 * @param inKeyTimeStamp java.lang.String
	 * @param inKeyAuthor java.lang.String
	 * @param inAuthorID java.lang.Long
	 * @throws BOMChangeValueException
	 */
	protected void insertHistoryEntry(DomainObject inHistory, String inKeyTimeStamp, String inKeyAuthor, Long inAuthorID) throws BOMChangeValueException {
		try {
			fillHistory(inHistory);
			Timestamp lMutationDate = new Timestamp(System.currentTimeMillis());
			inHistory.set(inKeyTimeStamp, lMutationDate);
			inHistory.set(inKeyAuthor, inAuthorID);
			inHistory.insert(true);
		}
		catch (Exception exc){
			throw new BOMChangeValueException("HistorizableDomainObject.insertHistoryEntry:  " + exc.getMessage());
		}
	}
	
	/**
	 * Creates a new history entry with the values of the actual domain object.
	 * 
	 * @return {@link DomainObject} the business object's history
	 * @throws BOMException
	 */
	public DomainObject createHistory() throws BOMException {
		DomainObject outHistory = getHistoryObject();
		fillHistory(outHistory);
		return outHistory;
	}

	/**
	 * Creates an empty history object instance.
	 * 
	 * @return DomainObject
	 * @throws BOMException
	 */
	protected abstract DomainObject getHistoryObject() throws BOMException;
	
}

/*
 This package is part of the application VIF.
 Copyright (C) 2008, Benno Luthiger

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
package org.hip.vif.core.util;

import org.hip.kernel.bom.impl.PropertySetImpl;
import org.hip.kernel.exc.VException;

/**
 * Helper object containing a contributors <code>Firstname Familyname</code>.
 * Instances of this class can be serialized using a <code>XMLSerializer</code>.
 *
 * @author Luthiger
 * Created: 20.09.2009
 */
@SuppressWarnings("serial")
public class Contributor extends PropertySetImpl {
	private final static String KEY_NAME = "FamilyName";
	private final static String KEY_FIRSTNAME = "FirstName";
	private final static String KEY_FULL_NAME = "FullName";
	private final static String KEY_IS_AUTHOR = "IsAuthor";

	/**
	 * Contributor constructor.
	 * 
	 * @param inName String
	 * @param inFirstname String
	 * @param inIsAuthor boolean
	 * @throws VException 
	 * @throws  
	 */
	public Contributor(String inName, String inFirstname, boolean inIsAuthor) throws VException {
		super(null);
		setValue(KEY_NAME, inName);
		setValue(KEY_FIRSTNAME, inFirstname);
		setValue(KEY_FULL_NAME, inFirstname + " " + inName);
		setValue(KEY_IS_AUTHOR, inIsAuthor ? "1" : "0");
	}

	/**
	 * @return String The contributor's full name, i.e. <code>Firstname Familyname</code>.
	 * @throws VException
	 */
	public String getFullname() throws VException {
		return getFirstName() + " " + getName();
	}

	/**
	 * @return String The contributor's family name.
	 * @throws VException
	 */
	protected String getName() throws VException {
		return getValue(KEY_NAME).toString();
	}

	/**
	 * @return String The contributor's firstname.
	 * @throws VException
	 */
	protected String getFirstName() throws VException {
		return getValue(KEY_FIRSTNAME).toString();
	}
	
}

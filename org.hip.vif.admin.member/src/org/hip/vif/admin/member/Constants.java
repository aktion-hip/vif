/*
	This package is part of the member administration of the application VIF.
	Copyright (C) 2008, Benno Luthiger

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
package org.hip.vif.admin.member;

/**
 * Constants used in this bundle.
 *
 * @author Luthiger
 * Created: 30.11.2007
 */
public class Constants {
	public enum SearchType {
		QUICK, DETAILED;
	}
	
	public final static String MENU_SET_ID_DEFAULT = "contextDefault"; //$NON-NLS-1$
	public final static String MENU_SET_ID_LIST = "contextMemberList"; //$NON-NLS-1$
	
	public static final String KEY_PARAMETER_MEMBER = "member_id"; //$NON-NLS-1$
	public static final String KEY_PARAMETER_SEARCH_TPYE = "search_type"; //$NON-NLS-1$
	public static final String KEY_PARAMETER_SEARCH_QUERY = "search_query"; //$NON-NLS-1$
	public static final String KEY_PARAMETER_SEARCH_NAME = "search_name"; //$NON-NLS-1$
	public static final String KEY_PARAMETER_SEARCH_FIRSTNAME = "search_firstname"; //$NON-NLS-1$
	public static final String KEY_PARAMETER_SEARCH_STREET = "search_street"; //$NON-NLS-1$
	public static final String KEY_PARAMETER_SEARCH_ZIP = "search_zip"; //$NON-NLS-1$
	public static final String KEY_PARAMETER_SEARCH_CITY = "search_city"; //$NON-NLS-1$
	public static final String KEY_PARAMETER_SEARCH_MAIL = "search_mail"; //$NON-NLS-1$
	
	public static final String PERMISSION_SEARCH = "searchMembers"; //$NON-NLS-1$
	
	public static final int FULL_LIST_THRESHOLD = 30;
}

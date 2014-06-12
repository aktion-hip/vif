/**
 This package is part of the administration of the application VIF.
 Copyright (C) 2008-2014, Benno Luthiger

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
package org.hip.vif.admin.admin;

/**
 * Constants used in this bundle.
 * 
 * @author Luthiger Created: 25.12.2007
 */
public class Constants {
	// TODO: uncomment permissions
	public final static String PERMISSION_REFRESH_INDEX = ""; // "refreshIndex"; //$NON-NLS-1$
	public final static String PERMISSION_SELECT_SKIN = ""; // "selectSkin"; //$NON-NLS-1$
	public final static String PERMISSION_SEND_MAIL = ""; // "sendMail"; //$NON-NLS-1$
	public static final String PERMISSION_PRINT_GROUP = ""; // "printGroup"; //$NON-NLS-1$
	public static final String PERMISSION_CONFIGURATION = ""; // "applicationConfiguration"; //$NON-NLS-1$
	public static final String PERMISSION_UPGRADE = ""; // "applicationUpgrade"; //$NON-NLS-1$

	public static final String INDEX_CONTENT = "content"; //$NON-NLS-1$
	public static final String INDEX_MEMBER = "member"; //$NON-NLS-1$

	public final static String EXTENDIBLE_MENU_ID = "adminMenu"; //$NON-NLS-1$
	public final static String EXTENDIBLE_MENU_POSITION_START = "menuStart"; //$NON-NLS-1$
	public final static String EXTENDIBLE_MENU_POSITION_ADDITIONS = "additions"; //$NON-NLS-1$
	public final static String EXTENDIBLE_MENU_POSITION_END = "menuEnd"; //$NON-NLS-1$

	public static final String CONTENT_START = "<text:h text:style-name=\"Heading_20_1\" text:outline-level=\"1\">%s</text:h>\n            <text:p text:style-name=\"P1\">%s</text:p>\n"; //$NON-NLS-1$
	public static final String CONTENT_END = "        </office:text>\n    </office:body>\n</office:document-content>"; //$NON-NLS-1$

	public static final String LOCAL_RESOURCES_DIR = "/resources"; //$NON-NLS-1$

	// scr:component/@name and metatype:MetaData/Designate/@pid
	public static final String COMPONENT_NAME = "org.hip.vif.admin.admin.configuration";

	public static final String KEY_CONFIG_SKIN = "org.hip.vif.admin.admin.skin";
}

/*
	This package is part of the application VIF.
	Copyright (C) 2004, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public
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

/**
 * Parameter object used for handling group state changes.
 * 
 * @author Benno Luthiger
 * Created on Jan 17, 2004
 */
public class GroupStateChangeParameters {
	private boolean notification;
	private String mailBody = "";
	private String mailSubject = "";
	private String groupName = "";
	private String groupID = "";
	
	/**
	 * GroupStateChangeParameters constructor
	 */
	public GroupStateChangeParameters() {
		notification = false;
	}
	
	/**
	 * Getter of notification status.
	 * 
	 * @return boolean
	 */
	public boolean doNotification() {
		return notification;
	}
	
	/**
	 * Setter of notification status.
	 * 
	 * @param inNotification boolean
	 */
	public void setNotification(boolean inNotification) {
		notification = inNotification;
	}
	
	/**
	 * Getter of mail body.
	 * 
	 * @return String
	 */
	public String getMailBody() {
		return mailBody;
	}
	
	/**
	 * Setter of mail body.
	 * 
	 * @param inMailBody String
	 */
	public void setMailBody(String inMailBody) {
		mailBody = inMailBody;
	}

	/**
	 * Setter of mail subject.
	 * 
	 * @param inMailSubject String
	 * Setter of mail subject.
	 */
	public void setMailSubject(String inMailSubject) {
		mailSubject = inMailSubject;
	}

	/**
	 * Getter of mail subject.
	 * 
	 * @return String
	 */
	public String getMailSubject() {
		return mailSubject;
	}

	/**
	 * Setter of group name.
	 * 
	 * @param inGroupName String
	 */
	public void setGroupName(String inGroupName) {
		groupName = inGroupName;
	}

	/**
	 * Getter of group name.
	 * 
	 * @return String
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Setter of group id.
	 * 
	 * @param inGroupID String
	 */
	public void setGroupID(String inGroupID) {
		groupID = inGroupID;
	}

	/**
	 * Getter of group id.
	 * 
	 * @return String
	 */
	public String getGroupID() {
		return groupID;
	}
}

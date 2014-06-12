/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

package org.hip.vif.admin.member.tasks;

import java.net.URL;
import java.sql.SQLException;

import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.member.Activator;
import org.hip.vif.admin.member.Constants;
import org.hip.vif.admin.member.data.MemberContainer;
import org.hip.vif.admin.member.data.MemberWrapper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.LinkMemberRoleHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.interfaces.IMemberQueryStrategy;
import org.hip.vif.core.service.MemberUtility;
import org.hip.vif.core.util.ParameterObject;
import org.hip.vif.web.tasks.AbstractVIFTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * Base class for searching members and displaying the list of members.
 * 
 * @author Luthiger
 * Created: 04.11.2011
 */
@SuppressWarnings("serial")
public abstract class AbstractMemberSearchTask extends AbstractVIFTask implements ValueChangeListener {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractMemberSearchTask.class);
	
	private static final String SORT_ORDER = MemberHome.KEY_USER_ID;

	private MemberContainer members;
	
	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#needsPermission()
	 */
	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_SEARCH;
	}
	
	protected boolean isExternal() throws VException {
		return MemberUtility.INSTANCE.getActiveAuthenticator().isExternal();
	}

	/**
	 * Processing the quick search for members.
	 * 
	 * @param inQuery String the quick search query
	 * @param inWindow Window the lookup window to close
	 * @return boolean
	 */
	public boolean search(String inQuery, Window inWindow) {
		try {
			ParameterObject lParameters = new ParameterObject();
			lParameters.set(Constants.KEY_PARAMETER_SEARCH_TPYE, Constants.SearchType.QUICK);
			lParameters.set(Constants.KEY_PARAMETER_SEARCH_QUERY, inQuery);
			setParameters(lParameters);
			callShowListTask(inWindow);
			return true;
		} 
		catch (VException exc) {
			LOG.error("Error encountered while preparing member search!", exc); //$NON-NLS-1$
		}
		return false;
	}
	
	/**
	 * This method is called when the user clicks the form's <code>search</code> button.<br />
	 * The intended action is to display the list of search results.<br /> 
	 * Subclasses may override.
	 * 
	 * @param inWindow {@link Window}
	 */
	protected void callShowListTask(Window inWindow) {
		sendEvent(MemberShowListTask.class);
	}
	
	/**
	 * Processing the detailed search for members.
	 * 
	 * @param inName String
	 * @param inFirstname String
	 * @param inStreet String
	 * @param inZIP String
	 * @param inCity String
	 * @param inMail String
	 * @return
	 */
	public boolean search(String inName, String inFirstname, String inStreet, String inZIP, String inCity, String inMail, Window inWindow) {
		try {
			ParameterObject lParameters = new ParameterObject();
			lParameters.set(Constants.KEY_PARAMETER_SEARCH_TPYE, Constants.SearchType.DETAILED);
			lParameters.set(Constants.KEY_PARAMETER_SEARCH_NAME, inName);
			lParameters.set(Constants.KEY_PARAMETER_SEARCH_FIRSTNAME, inFirstname);
			lParameters.set(Constants.KEY_PARAMETER_SEARCH_STREET, inStreet);
			lParameters.set(Constants.KEY_PARAMETER_SEARCH_ZIP, inZIP);
			lParameters.set(Constants.KEY_PARAMETER_SEARCH_CITY, inCity);
			lParameters.set(Constants.KEY_PARAMETER_SEARCH_MAIL, inMail);
			setParameters(lParameters);
			callShowListTask(inWindow);
			return true;
		} 
		catch (VException exc) {
			LOG.error("Error encountered while preparing member search!", exc); //$NON-NLS-1$
		}
		return false;
	}

	protected URL getHelpContent() {
		String lHelpContentFile = String.format("searchHelpContent_%s.html", getAppLocale().getLanguage()); //$NON-NLS-1$
		return this.getClass().getClassLoader().getResource(lHelpContentFile);
	}
	
	protected MemberContainer createMemberContainer(IMemberQueryStrategy inStrategy) throws VException, SQLException {
		return createMemberContainer(inStrategy, createOrder(SORT_ORDER, false));
	}
	
	protected MemberContainer createMemberContainer(IMemberQueryStrategy inStrategy, OrderObject inOrder) throws VException, SQLException {
		members = MemberContainer.createData(inStrategy.getQueryResult(inOrder)); 
		return members;
	}
	
	/**
	 * Deletes the selected members.
	 * 
	 * @return boolean
	 */
	public boolean deleteMember() {
		try {
			if (isExternal()) return false;
			
			int lCount = 0;
			for (MemberWrapper lMember : members.getItemIds()) {
				if (lMember.isChecked()) {
					deleteMemberAndRelated(lMember.getMemberID());
					lCount++;
				}
			}
			
			String lMessage = Activator.getMessages().getMessage(lCount == 1 ? "msg.member.delete.ok1" : "msg.member.delete.okP"); //$NON-NLS-1$ //$NON-NLS-2$
			showNotification(lMessage, Notification.TYPE_TRAY_NOTIFICATION);
			sendEvent(MemberSearchTask.class);
			return true;
		} 
		catch (Exception exc) {
			LOG.error("Error while deleting a member entry.", exc); //$NON-NLS-1$
		}
		return false;
	}
	
	private void deleteMemberAndRelated(Long inMemberID) throws SQLException, Exception {
		LinkMemberRoleHome lLinkHome = BOMHelper.getLinkMemberRoleHome();
		lLinkHome.deleteRolesOf(inMemberID);
		BOMHelper.getMemberHome().getMember(inMemberID).delete(getActor().getActorID());
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
	 */
	public void valueChange(ValueChangeEvent inEvent) {
		try {
			Object lMember = inEvent.getProperty().getValue();
			if (lMember instanceof MemberWrapper) {
				Long lMemberID = ((MemberWrapper)lMember).getMemberID();
				ParameterObject lParameters = new ParameterObject();
				lParameters.set(Constants.KEY_PARAMETER_MEMBER, lMemberID);
				setParameters(lParameters);
				sendEvent(MemberShowTask.class);
			}
		} 
		catch (VException exc) {
			LOG.error("Error while displaying the view to edit a member entry.", exc); //$NON-NLS-1$
		}
	}

	protected String getString(String inDefault, ParameterObject inParameters, String inParameterKey) {
		String outTitle = inDefault;
		if (inParameters == null) return outTitle;
	
		try {
			Object lParameter = inParameters.get(inParameterKey);
			if (lParameter == null) return outTitle;
			return lParameter.toString();
		}
		catch (VException exc) {
			return outTitle;
		}
	}

}

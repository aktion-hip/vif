/**
    This package is part of the application VIF.
    Copyright (C) 2011-2014, Benno Luthiger

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
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.util.ParameterObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Window;

/** Base class for searching members and displaying the list of members.
 *
 * @author Luthiger Created: 04.11.2011 */
@SuppressWarnings("serial")
public abstract class AbstractMemberSearchTask extends AbstractWebController implements ValueChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractMemberSearchTask.class);

    private static final String SORT_ORDER = MemberHome.KEY_USER_ID;

    private MemberContainer members;

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_SEARCH;
    }

    protected boolean isExternal() throws VException {
        return MemberUtility.INSTANCE.getActiveAuthenticator().isExternal();
    }

    /** Processing the quick search for members.
     *
     * @param inQuery String the quick search query
     * @return boolean */
    public boolean search(final String inQuery) {
        final ParameterObject lParameters = new ParameterObject();
        lParameters.set(Constants.KEY_PARAMETER_SEARCH_TPYE, Constants.SearchType.QUICK);
        lParameters.set(Constants.KEY_PARAMETER_SEARCH_QUERY, inQuery);
        setParameters(lParameters);
        callShowListTask();
        return true;
    }

    /** This method is called when the user clicks the form's <code>search</code> button.<br />
     * The intended action is to display the list of search results.<br />
     * Subclasses may override.
     *
     * @param inWindow {@link Window} */
    protected void callShowListTask() {
        sendEvent(MemberShowListTask.class);
    }

    /** Processing the detailed search for members.
     *
     * @param inName String
     * @param inFirstname String
     * @param inStreet String
     * @param inZIP String
     * @param inCity String
     * @param inMail String
     * @return */
    public boolean search(final String inName, final String inFirstname, final String inStreet, final String inZIP,
            final String inCity, final String inMail) {
        final ParameterObject lParameters = new ParameterObject();
        lParameters.set(Constants.KEY_PARAMETER_SEARCH_TPYE, Constants.SearchType.DETAILED);
        lParameters.set(Constants.KEY_PARAMETER_SEARCH_NAME, inName);
        lParameters.set(Constants.KEY_PARAMETER_SEARCH_FIRSTNAME, inFirstname);
        lParameters.set(Constants.KEY_PARAMETER_SEARCH_STREET, inStreet);
        lParameters.set(Constants.KEY_PARAMETER_SEARCH_ZIP, inZIP);
        lParameters.set(Constants.KEY_PARAMETER_SEARCH_CITY, inCity);
        lParameters.set(Constants.KEY_PARAMETER_SEARCH_MAIL, inMail);
        setParameters(lParameters);
        callShowListTask();
        return true;
    }

    protected URL getHelpContent() {
        final String lHelpContentFile = String.format("searchHelpContent_%s.html", getAppLocale().getLanguage()); //$NON-NLS-1$
        return this.getClass().getClassLoader().getResource(lHelpContentFile);
    }

    protected MemberContainer createMemberContainer(final IMemberQueryStrategy inStrategy) throws VException,
    SQLException {
        return createMemberContainer(inStrategy, createOrder(SORT_ORDER, false));
    }

    protected MemberContainer createMemberContainer(final IMemberQueryStrategy inStrategy, final OrderObject inOrder)
            throws VException, SQLException {
        members = MemberContainer.createData(inStrategy.getQueryResult(inOrder));
        return members;
    }

    /** Deletes the selected members.
     *
     * @return boolean */
    public boolean deleteMember() {
        try {
            if (isExternal())
                return false;

            int lCount = 0;
            for (final MemberWrapper lMember : members.getItemIds()) {
                if (lMember.isChecked()) {
                    deleteMemberAndRelated(lMember.getMemberID());
                    lCount++;
                }
            }

            final String lMessage = Activator.getMessages().getMessage(
                    lCount == 1 ? "msg.member.delete.ok1" : "msg.member.delete.okP"); //$NON-NLS-1$ //$NON-NLS-2$
            showNotification(lMessage);
            sendEvent(MemberSearchTask.class);
            return true;
        } catch (final Exception exc) {
            LOG.error("Error while deleting a member entry.", exc); //$NON-NLS-1$
        }
        return false;
    }

    private void deleteMemberAndRelated(final Long inMemberID) throws SQLException, Exception {
        final LinkMemberRoleHome lLinkHome = BOMHelper.getLinkMemberRoleHome();
        lLinkHome.deleteRolesOf(inMemberID);
        BOMHelper.getMemberHome().getMember(inMemberID).delete(getActor().getActorID());
    }

    @Override
    public void valueChange(final ValueChangeEvent inEvent) {
        final Object lMember = inEvent.getProperty().getValue();
        if (lMember instanceof MemberWrapper) {
            final Long lMemberID = ((MemberWrapper) lMember).getMemberID();
            final ParameterObject lParameters = new ParameterObject();
            lParameters.set(Constants.KEY_PARAMETER_MEMBER, lMemberID);
            setParameters(lParameters);
            sendEvent(MemberShowTask.class);
        }
    }

    protected String getString(final String inDefault, final ParameterObject inParameters, final String inParameterKey) {
        final String outTitle = inDefault;
        if (inParameters == null) {
            return outTitle;
        }

        final Object lParameter = inParameters.get(inParameterKey);
        if (lParameter == null)
            return outTitle;
        return lParameter.toString();
    }

}

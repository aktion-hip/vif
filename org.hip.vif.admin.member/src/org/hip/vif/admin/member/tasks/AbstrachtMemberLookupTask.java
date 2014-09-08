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

import java.util.Collection;
import java.util.Vector;

import org.hip.vif.admin.member.Activator;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.hip.vif.web.util.MemberBean;
import org.ripla.interfaces.IMessages;
import org.ripla.util.ParameterObject;
import org.ripla.web.util.Popup;

/** Base class for the controller of member lookup views.
 *
 * @author Luthiger Created: 17.11.2011 */
@SuppressWarnings("serial")
public abstract class AbstrachtMemberLookupTask extends AbstractMemberSearchTask {

    private static final String PARAMETER_KEY_PROCESSING = "lookupWindowProcessing"; //$NON-NLS-1$
    private static final String PARAMETER_KEY_SUB_TITLE = "lookupWindowSubtitleSelect"; //$NON-NLS-1$
    private static final String PARAMETER_KEY_TITLE = "lookupWindowTitleSearch"; //$NON-NLS-1$
    private static final String PARAMETER_KEY_COLUMN_TITLE = "lookupWindowColumnSelected"; //$NON-NLS-1$
    private static final String PARAMETER_KEY_SELECTED = "lookupWindowSelection"; //$NON-NLS-1$
    private static final String PARAMETER_KEY_RESULT = "lookupWindowResult"; //$NON-NLS-1$

    private String selectionProcessing;
    private String lookupTitle;
    private String lookupSubtitle;
    private String lookupRightColumnTitle;
    private Collection<MemberBean> assignedMembers;

    /** Evaluates the <code>ParameterObject</code>. Must be called in the controller's <code>runChecked()</code> method. */
    protected void prepareParameters() {
        final IMessages lMessages = Activator.getMessages();
        final ParameterObject lParameters = getParameters();
        selectionProcessing = getString(null, lParameters, PARAMETER_KEY_PROCESSING); //$NON-NLS-1$
        assignedMembers = getSelection(lParameters);
        lookupSubtitle = getString("", lParameters, PARAMETER_KEY_SUB_TITLE); //$NON-NLS-1$ //$NON-NLS-2$
        lookupTitle = getString(lMessages.getMessage("ui.member.search.title.page"), lParameters, PARAMETER_KEY_TITLE); //$NON-NLS-1$ //$NON-NLS-2$
        lookupRightColumnTitle = getString(
                lMessages.getMessage("ui.member.lookup.assigned"), lParameters, PARAMETER_KEY_COLUMN_TITLE); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** @return <code>lookupWindowSelection</code> */
    protected Collection<MemberBean> getAssignedMembers() {
        return assignedMembers;
    }

    /** @return <code>lookupWindowSubtitleSelect</code> */
    protected String getLookupSubtitle() {
        return lookupSubtitle;
    }

    /** @return <code>lookupWindowTitleSearch</code> */
    protected String getLookupTitle() {
        return lookupTitle;
    }

    /** @return <code>lookupWindowColumnSelected</code> */
    protected String getLookupRightColumnTitle() {
        return lookupRightColumnTitle;
    }

    /** @return String the fully qualified name of the task processing the selection. */
    protected String getTaskNameSelectionProcessor() {
        return selectionProcessing;
    }

    /** Callback method, processes the selected members.<br />
     * This method is called if the task displays the full list of members in the lookup window.
     *
     * @param inMembers {@link Collection} of <code>MemberBean</code>s, the selected members
     * @return boolean <code>true</code> if successful */
    public boolean selectMembers(final Collection<MemberBean> inMembers) {
        if (getTaskNameSelectionProcessor() == null) {
            return false;
        }

        final ParameterObject lParameters = new ParameterObject();
        lParameters.set(PARAMETER_KEY_RESULT, inMembers); //$NON-NLS-1$
        setParameters(lParameters);
        Popup.removePopups();
        sendEvent(getTaskNameSelectionProcessor());
        return true;
    }

    @Override
    protected void callShowListTask() {
        // The parameter object is initialized in the super class, before this method is called.
        // Therefore, we only add some parameters.
        final ParameterObject lParameters = getParameters(false);
        lParameters.set(PARAMETER_KEY_SUB_TITLE, lookupSubtitle); //$NON-NLS-1$
        lParameters.set(PARAMETER_KEY_COLUMN_TITLE, lookupRightColumnTitle); //$NON-NLS-1$
        lParameters.set(PARAMETER_KEY_SELECTED, assignedMembers); //$NON-NLS-1$
        lParameters.set(PARAMETER_KEY_PROCESSING, getTaskNameSelectionProcessor()); //$NON-NLS-1$
        Popup.removePopups();
        requestLookup(LookupType.MEMBER_SELECT, 0l);
    }

    @SuppressWarnings("unchecked")
    private Collection<MemberBean> getSelection(final ParameterObject inParameters) {
        final Collection<MemberBean> out = new Vector<MemberBean>();
        if (inParameters == null)
            return out;

        setParameters(null);
        final Object lParameter = inParameters.get(PARAMETER_KEY_SELECTED); //$NON-NLS-1$
        if (lParameter == null)
            return out;
        return (Collection<MemberBean>) lParameter;
    }

}

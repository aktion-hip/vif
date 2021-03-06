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

package org.hip.vif.web.util;

import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GettingException;
import org.hip.vif.web.Activator;
import org.ripla.interfaces.IMessages;

/** Helper class to check mandatory fields of model objects.<br/>
 * Usage: This class is designed to be subclassed. Configure the field checker by calling <code>checkMandatory()</code>
 * in the subclass' constructor.<br/>
 * Call the subclassed instance in the button's listener, e.g.:
 *
 * <pre>
 * public void buttonClick(ClickEvent inEvent) {
 *     if (!inMember.isValid()) {
 *         MemberFieldChecker lChecker = new MemberFieldChecker(inMember, lMessages);
 *         Notification.show(lChecker.render(), Notification.Type.ERROR_MESSAGE);
 *     }
 * }
 * </pre>
 *
 * @author Luthiger */
public class MandatoryFieldChecker {
    private final Collection<String> fields;
    private final IMessages messages;

    /** @param inMessages {@link IMessages} the bundles messages */
    public MandatoryFieldChecker(final IMessages inMessages) {
        messages = inMessages;
        fields = new Vector<String>();
    }

    /** Add the mandatory fields to check.
     *
     * @param inModel {@link DomainObject} the model instance
     * @param inName String the mandatory field's key
     * @param inMsgKey String the message key to display if the check failed */
    public void checkMandatory(final DomainObject inModel, final String inName, final String inMsgKey) {
        try {
            final Object lValue = inModel.get(inName);
            if (lValue == null) {
                addMessage(inMsgKey);
            }
            else if (lValue.toString().length() == 0) {
                addMessage(inMsgKey);
            }
        } catch (final GettingException exc) {
            addMessage(inMsgKey);
        }
    }

    private void addMessage(final String inMsgKey) {
        fields.add(messages.getMessage(inMsgKey));
    }

    /** @return String renders the feedback message */
    public String render() {
        final StringBuilder out = new StringBuilder();
        int lCount = 0;
        for (final String lMessage : fields) {
            if (lCount > 0) {
                out.append(", "); //$NON-NLS-1$
            }
            out.append("'").append(lMessage).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
            lCount++;
        }
        return Activator.getMessages().getFormattedMessage(
                lCount == 1 ? "errmsg.field.madatoryS" : "errmsg.field.madatoryM", out); //$NON-NLS-1$ //$NON-NLS-2$
    }

}

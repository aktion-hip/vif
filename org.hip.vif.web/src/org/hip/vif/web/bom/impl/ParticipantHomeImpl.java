/**
    This package is part of the application VIF.
    Copyright (C) 2014, Benno Luthiger

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
package org.hip.vif.web.bom.impl;

import org.hip.kernel.bom.BOMInvalidKeyException;
import org.hip.kernel.bom.GettingException;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.web.bom.ParticipantHome;
import org.hip.vif.web.bom.VifBOMHelper;

/** Implementation of the ParticipantHome.
 *
 * @author lbenno */
@SuppressWarnings("serial")
public class ParticipantHomeImpl extends org.hip.vif.core.bom.impl.ParticipantHomeImpl implements ParticipantHome {

    private final static String PARTICIPANT_CLASS_NAME = "org.hip.vif.web.bom.impl.ParticipantImpl";

    /** @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectClassName() */
    @Override
    public String getObjectClassName() {
        return PARTICIPANT_CLASS_NAME;
    }

    @Override
    protected void checkActivation(final Long inGroupID) throws GettingException, WorkflowException,
            BOMInvalidKeyException, BOMChangeValueException {
        VifBOMHelper.getGroupHome().getGroup(inGroupID).checkActivationState(
                getParticipantsOfGroup(inGroupID));
    }

}

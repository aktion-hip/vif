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
package org.hip.vif.core.bom.impl;

import org.hip.kernel.workflow.WorkflowException;

/** Model object for Question entries for guest views.
 *
 * @author Luthiger Created: 15.10.2009 */
@SuppressWarnings("serial")
public class QuestionForGuests extends QuestionImpl {
    public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.QuestionForGuestsHome";

    public QuestionForGuests() throws WorkflowException {
        super();
    }

    @Override
    public String getHomeClassName() {
        return HOME_CLASS_NAME;
    }

}

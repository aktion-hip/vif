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

package org.hip.vif.admin.member.internal;

import org.hip.vif.admin.member.tasks.MemberViewTask;
import org.hip.vif.web.interfaces.ILookupWindow;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.ripla.web.util.UseCaseHelper;

/** Service provider component for the <code>IContentLookup</code> interface.<br />
 * This component provides the view for the member lookup.
 *
 * @author Luthiger Created: 26.10.2011 */
public class LookupMemberComponent implements ILookupWindow {

    @Override
    public int getWidth() {
        return 500;
    }

    @Override
    public int getHeight() {
        return 600;
    }

    @Override
    public String getControllerName() {
        return UseCaseHelper.createFullyQualifiedControllerName(MemberViewTask.class);
    }

    @Override
    public boolean isForum() {
        return false;
    }

    @Override
    public LookupType getType() {
        return LookupType.MEMBER;
    }

}

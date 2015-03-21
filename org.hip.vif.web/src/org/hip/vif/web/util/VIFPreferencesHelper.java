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
package org.hip.vif.web.util;

import java.sql.SQLException;
import java.util.Locale;

import org.hip.kernel.bom.BOMInvalidKeyException;
import org.hip.kernel.bom.SettingException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.osgi.service.useradmin.User;
import org.ripla.util.PreferencesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** PreferencesHelper for the VIF application.
 *
 * @author lbenno */
public class VIFPreferencesHelper extends PreferencesHelper { // NOPMD
    private static final Logger LOG = LoggerFactory.getLogger(VIFPreferencesHelper.class);

    /** We persist the changed language preference to the member table. */
    @Override
    public void setLocale(final Locale inLocale, final User inUser) {
        super.setLocale(inLocale, inUser);
        try {
            final Member lMember = BOMHelper.getMemberHome().getMemberByUserID(inUser.getName());
            lMember.set(MemberHome.KEY_LANGUAGE, inLocale.getLanguage());
            lMember.update(true);
        } catch (BOMInvalidKeyException | SettingException | SQLException exc) {
            LOG.error("Unable to store the changed language setting!", exc);
        }
    }

}

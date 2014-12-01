/*
 This package is part of the administration of the application VIF.
 Copyright (C) 2009, Benno Luthiger

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

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.QuestionHome;

/** Model for a question entry that contains the names of the question's contributors.
 *
 * @author Luthiger Created: 20.09.2009 */
@SuppressWarnings("serial")
public class JoinQuestionToContributors extends JoinQuestionToCompletionAndContributors {
    public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinQuestionToContributorsHome";

    private final static String FILL = "00000";
    private final static int WIDTH = FILL.length();
    private final static String DELIMITER = ".";

    /** This Method returns the class name of the home.
     *
     * @return java.lang.String */
    @Override
    public String getHomeClassName() {
        return HOME_CLASS_NAME;
    }

    /** @return Long The question's ID.
     * @throws VException */
    @Override
    public Long getID() throws VException {
        return (Long) get(QuestionHome.KEY_ID);
    }

    /** The question's decimal ID made sortable, i.e. each part padding left with <code>0</code>.
     * 
     * @return String, e.g. <code>"00004.00010.00002"</code>
     * @throws VException */
    public String getSortString() throws VException {
        final String lDecimal = get(QuestionHome.KEY_QUESTION_DECIMAL).toString();
        final String[] lParts = lDecimal.split(":")[1].split("\\" + DELIMITER);
        final StringBuilder out = new StringBuilder();
        for (final String lPart : lParts) {
            final String lFilled = FILL + lPart;
            out.append(lFilled.substring(lFilled.length() - WIDTH)).append(DELIMITER);
        }
        return new String(out.substring(0, out.length() - 1));
    }

}

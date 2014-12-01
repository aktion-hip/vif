/**
 This package is part of the application VIF.
 Copyright (C) 2010-2014, Benno Luthiger

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
package org.hip.vif.web.bom;

import org.hip.kernel.bom.DomainObjectVisitor;
import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.kernel.exc.DefaultExceptionWriter;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.web.biblio.BibliographyAdapter;
import org.hip.vif.web.util.BibliographyFormatter;

/** Business object to retrieve all text entries linked to a given question.
 *
 * @author Luthiger Created: 28.07.2010 */
@SuppressWarnings("serial")
public class JoinQuestionToText extends DomainObjectImpl {
    public final static String HOME_CLASS_NAME = "org.hip.vif.web.bom.JoinQuestionToTextHome";

    /** This Method returns the class name of the home.
     *
     * @return java.lang.String */
    @Override
    public String getHomeClassName() {
        return HOME_CLASS_NAME;
    }

    @Override
    public void accept(final DomainObjectVisitor inVisitor) {
        try {
            final BibliographyFormatter lFormatter = new BibliographyFormatter(new BibliographyAdapter(this,
                    TextHome.KEY_BIBLIO_TYPE));
            propertySet().setValue(TextHome.KEY_BIBLIOGRAPHY, lFormatter.renderHtml());
        } catch (final VException exc) {
            DefaultExceptionWriter.printOut(this, exc, true);
        }
        super.accept(inVisitor);
    }

}

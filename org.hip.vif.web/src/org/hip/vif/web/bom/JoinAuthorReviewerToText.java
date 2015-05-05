package org.hip.vif.web.bom;

import org.hip.kernel.bom.DomainObjectVisitor;
import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.kernel.exc.DefaultExceptionWriter;
import org.hip.kernel.exc.VException;
import org.hip.vif.web.biblio.BibliographyAdapter;
import org.hip.vif.web.util.BibliographyFormatter;

/** This class implements the join between text-author/reviewer and text entries.
 *
 * @author Luthiger */
@SuppressWarnings("serial")
public class JoinAuthorReviewerToText extends DomainObjectImpl { // NOPMD
    public final static String HOME_CLASS_NAME = "org.hip.vif.web.bom.JoinAuthorReviewerToTextHome";

    /** This Method returns the class name of the home.
     *
     * @return java.lang.String */
    @Override
    public String getHomeClassName() {
        return HOME_CLASS_NAME;
    }

    @Override
    public void accept(final DomainObjectVisitor inVisitor) { // NOPMD
        try {
            final BibliographyFormatter lFormatter = new BibliographyFormatter(new
                    BibliographyAdapter(this, TextHome.KEY_BIBLIO_TYPE));
            propertySet().setValue(TextHome.KEY_BIBLIOGRAPHY,
                    lFormatter.renderHtml());
        } catch (final VException exc) {
            DefaultExceptionWriter.printOut(this, exc, true);
        }
        super.accept(inVisitor);
    }

}

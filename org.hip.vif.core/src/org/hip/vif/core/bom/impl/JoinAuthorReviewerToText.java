package org.hip.vif.core.bom.impl;

import org.hip.kernel.bom.impl.DomainObjectImpl;

/**
 * This class implements the join between text-author/reviewer and text entries.
 * 
 * @author Luthiger Created: 22.06.2010
 */
@SuppressWarnings("serial")
public class JoinAuthorReviewerToText extends DomainObjectImpl {
	public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinAuthorReviewerToTextHome";

	/**
	 * This Method returns the class name of the home.
	 * 
	 * @return java.lang.String
	 */
	@Override
	public String getHomeClassName() {
		return HOME_CLASS_NAME;
	}

	// @Override
	// public void accept(DomainObjectVisitor inVisitor) {
	// try {
	// BibliographyFormatter lFormatter = new BibliographyFormatter(new
	// BibliographyAdapter(this, TextHome.KEY_BIBLIO_TYPE));
	// propertySet().setValue(TextHome.KEY_BIBLIOGRAPHY,
	// lFormatter.renderHtml());
	// } catch (VException exc) {
	// DefaultExceptionWriter.printOut(this, exc, true);
	// }
	// super.accept(inVisitor);
	// }

}

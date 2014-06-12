/**
	This package is part of the application VIF.
	Copyright (C) 2012-2014, Benno Luthiger

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
package org.hip.vif.web.biblio;

import org.hip.kernel.bom.DomainObjectVisitor;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.impl.JoinAuthorReviewerToText;
import org.hip.vif.web.util.BibliographyFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter for model instances of type <code>JoinAuthorReviewerToText</code>.<br />
 * This adapter provides functionality to render bibliographical information of
 * the adapted model.
 * 
 * @author lbenno
 */
public class JoinAuthorReviewerToTextAdapter {
	private static final Logger LOG = LoggerFactory
			.getLogger(JoinAuthorReviewerToTextAdapter.class);

	private final JoinAuthorReviewerToText text;

	/**
	 * JoinAuthorReviewerToTextAdapter constructor.
	 * 
	 * @param inText
	 *            {@link JoinAuthorReviewerToText} the join to adapt
	 */
	public JoinAuthorReviewerToTextAdapter(final JoinAuthorReviewerToText inText) {
		text = inText;
	}

	/**
	 * Used to accept a DomainObjectVisitor.
	 * 
	 * @param inVisitor
	 *            {@link DomainObjectVisitor}
	 */
	public void accept(final DomainObjectVisitor inVisitor) {
		try {
			final BibliographyFormatter lFormatter = new BibliographyFormatter(
					new BibliographyAdapter(text, TextHome.KEY_BIBLIO_TYPE));
			text.propertySet().setValue(TextHome.KEY_BIBLIOGRAPHY,
					lFormatter.renderHtml());
		}
		catch (final VException exc) {
			LOG.error(
					"An error encountered while rendering bibliographical information!",
					exc);
		}
		text.accept(inVisitor);
	}

}

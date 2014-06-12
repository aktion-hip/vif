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
package org.hip.vif.web.biblio;

import java.util.regex.Pattern;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.TextHome;

/**
 * Adapts a <code>DomainObject</code> that provides bibliographical information.
 * 
 * @author Luthiger Created: 31.07.2010
 */
public class BibliographyAdapter implements IBibliography {
	private static final Pattern PATTERN_URL = Pattern
			.compile("(((http|https|ftp)://)|www.)");

	private final GeneralDomainObject bibliography;
	private final String typeField;

	/**
	 * Adapter constructor
	 * 
	 * @param inBibliography
	 *            {@link GeneralDomainObject} the adapted domain object.
	 * @param inTypeField
	 *            String the field name containing the information about the
	 *            type of the bibliography (see <code>TextType</code>).
	 */
	public BibliographyAdapter(final GeneralDomainObject inBibliography,
			final String inTypeField) {
		bibliography = inBibliography;
		typeField = inTypeField;
	}

	@Override
	public TextType getType() throws VException {
		final Object lType = bibliography.get(typeField);
		for (final TextType lTextType : TextType.values()) {
			if (lTextType.checkType(lType))
				return lTextType;
		}
		// default
		return TextType.BOOK;
	}

	private String getChecked(final String inKey) throws VException {
		final Object out = bibliography.get(inKey);
		return out == null ? "" : out.toString();
	}

	@Override
	public String getTitle() throws VException {
		return getChecked(TextHome.KEY_TITLE);
	}

	@Override
	public String getAuthor() throws VException {
		return getChecked(TextHome.KEY_AUTHOR);
	}

	@Override
	public String getCoAuthor() throws VException {
		return getChecked(TextHome.KEY_COAUTHORS);
	}

	@Override
	public String getPlace() throws VException {
		return getChecked(TextHome.KEY_PLACE);
	}

	@Override
	public String getPublisher() throws VException {
		return getChecked(TextHome.KEY_PUBLISHER);
	}

	@Override
	public String getSubtitle() throws VException {
		return getChecked(TextHome.KEY_SUBTITLE);
	}

	@Override
	public String getYear() throws VException {
		return getChecked(TextHome.KEY_YEAR);
	}

	@Override
	public String getPages() throws VException {
		return getChecked(TextHome.KEY_PAGES);
	}

	@Override
	public String getVolume() throws VException {
		return getChecked(TextHome.KEY_VOLUME);
	}

	@Override
	public String getNumber() throws VException {
		return getChecked(TextHome.KEY_NUMBER);
	}

	@Override
	public String getPublication() throws VException {
		return getChecked(TextHome.KEY_PUBLICATION);
	}

	/**
	 * Convenience method.
	 * 
	 * @return boolean <code>true</code> if the text entry has a correct URL in
	 *         the WebPage/Publication field.
	 * @throws VException
	 */
	public boolean hasWebPageUrl() throws VException {
		if (!TextType.WEBPAGE.equals(getType()))
			return false;
		return PATTERN_URL.matcher(getPublication()).find();
	}

}

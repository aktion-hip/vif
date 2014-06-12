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
package org.hip.vif.web.util;

import org.hip.kernel.exc.VException;
import org.hip.vif.web.Activator;
import org.hip.vif.web.Messages;
import org.hip.vif.web.biblio.IBibliography;
import org.ripla.interfaces.IMessages;

/**
 * The standard scheme is as follows:
 * <dl>
 * <dt>Book:</dt>
 * <dd>[auth] and [coauth]\n[year]. [tit]. [subtit]. [place]: [publisher].</dd>
 * <dt>Article:</dt>
 * <dd>[auth] and [coauth]\n[year]. "[tit]". [publication] [vol]:[nr], [page].</dd>
 * <dt>Contribution:</dt>
 * <dd>[auth]\n[year]. "[tit]", in [publication]. Eds. [coauth], pp. [page].
 * [place]: [publisher].</dd>
 * <dt>Webpage:</dt>
 * <dd>[auth] and [coauth]\n[year]. "[tit]. [subtit]", [publication]. (accessed
 * [place])</dd>
 * </dl>
 * 
 * @author Luthiger Created: 24.06.2010
 */
public class BibliographyFormatter {
	// keys for messages
	private static final String KEY_AND = "org.hip.vif.bibliography.and";
	private static final String KEY_EDITORS = "org.hip.vif.bibliography.editors";
	private static final String KEY_ACCESSED = "org.hip.vif.bibliography.accessed";

	private final IBibliography bibliography;
	private final IMessages messages = Activator.getMessages();

	/**
	 * BibliographyFormatter constructor
	 * 
	 * @param inBibliography
	 *            {@link IBibliography}
	 */
	public BibliographyFormatter(final IBibliography inBibliography) {
		bibliography = inBibliography;
	}

	/**
	 * Renders the bibliography format according the adapted object's type.
	 * 
	 * @return String
	 * @throws VException
	 */
	public String renderPlain() throws VException {
		final PartFormatter outFormatter = new PartFormatter();

		switch (bibliography.getType()) {
		case ARTICLE:
			outFormatter.append(getAuthorCoAuthor(bibliography.getAuthor(),
					bibliography.getCoAuthor(), getAnd()));
			outFormatter.append(getCheckedPrePost(bibliography.getYear(), " (",
					")"));
			outFormatter.close();
			outFormatter.append("\"").append(bibliography.getTitle())
					.append("\"");
			outFormatter.close();
			outFormatter.append(bibliography.getPublication()).append(
					getCheckedPre(
							getFirstOrSecondOrBoth(
									getFirstOrSecondOrBoth(
											bibliography.getVolume(),
											bibliography.getNumber(), ":"),
									bibliography.getPages(), ", "), " "));
			outFormatter.close();
			break;
		case CONTRIBUTION:
			outFormatter.append(bibliography.getAuthor()).append(
					getCheckedPrePost(bibliography.getYear(), " (", ")"));
			outFormatter.close();
			outFormatter.append("\"").append(bibliography.getTitle())
					.append("\"");
			outFormatter.conditional(", ");
			outFormatter.append(getCheckedPre(bibliography.getPublication(),
					"in "));
			outFormatter.close();
			outFormatter.append(getCheckedPre(bibliography.getCoAuthor(),
					getEds()));
			outFormatter.conditional(", ");
			outFormatter.append(getCheckedPre(bibliography.getPages(), "pp. "));
			outFormatter.close();
			outFormatter.append(getFirstOrSecondOrBoth(bibliography.getPlace(),
					bibliography.getPublisher(), ": "));
			outFormatter.close();
			break;
		case WEBPAGE:
			outFormatter.append(getAuthorCoAuthor(bibliography.getAuthor(),
					bibliography.getCoAuthor(), getAnd()));
			outFormatter.append(getCheckedPrePost(bibliography.getYear(), " (",
					")"));
			outFormatter.close();
			outFormatter.append("\"").append(bibliography.getTitle());
			outFormatter
					.append(getCheckedPre(bibliography.getSubtitle(), ". "))
					.append("\"");
			outFormatter.conditional(", ");
			outFormatter.append(bibliography.getPublication());
			outFormatter.close();
			final String lAccessed = getCheckedPre(bibliography.getPlace(),
					getAccessed());
			if (lAccessed.length() != 0) {
				outFormatter.append("(").append(lAccessed).append(")");
				outFormatter.close(true);
			}
			break;
		default: // BOOK is default
			outFormatter.append(getAuthorCoAuthor(bibliography.getAuthor(),
					bibliography.getCoAuthor(), getAnd()));
			outFormatter.append(getCheckedPrePost(bibliography.getYear(), " (",
					")"));
			outFormatter.close();
			outFormatter.addPart(bibliography.getTitle());
			outFormatter.addPart(bibliography.getSubtitle());
			outFormatter
					.addPart(getFirstOrSecondOrBoth(bibliography.getPlace(),
							bibliography.getPublisher(), ": "));
			break;
		}

		return outFormatter.getJoined();
	}

	/**
	 * Delegates to renderPlain()
	 * 
	 * @return String
	 * @throws VException
	 */
	public String renderHtml() throws VException {
		return renderPlain();
	}

	private String getEds() {
		return messages().getMessage(KEY_EDITORS) + " ";
	}

	private String getAccessed() {
		return messages().getMessage(KEY_ACCESSED) + " ";
	}

	private String getAnd() {
		return messages().getMessage(KEY_AND);
	}

	private IMessages messages() {
		if (messages != null)
			return messages;
		return new Messages();
	}

	private String getAuthorCoAuthor(final String inAuthor,
			final String inCoAuthor, final String inLink) {
		if (inCoAuthor.length() == 0) {
			return inAuthor;
		}
		return String.format("%s %s %s", inAuthor, inLink, inCoAuthor);
	}

	/**
	 * Returns the first item or the second or both, linked if needed.
	 * 
	 * @param inFirst
	 *            String, may be empty
	 * @param inSecond
	 *            String, may be empty
	 * @param inLink
	 *            String
	 * @return String
	 */
	private String getFirstOrSecondOrBoth(final String inFirst,
			final String inSecond, final String inLink) {
		final boolean hasFirst = inFirst.length() != 0;
		final boolean hasSecond = inSecond.length() != 0;

		if (hasFirst && hasSecond) {
			return inFirst + inLink + inSecond;
		}
		if (hasFirst) {
			return inFirst;
		}
		return inSecond;
	}

	/**
	 * Puts the prefix in front of the item if the item's not empty.
	 * 
	 * @param inToCheck
	 * @param inPrefix
	 * @return String
	 */
	private String getCheckedPre(final String inToCheck, final String inPrefix) {
		if (inToCheck.length() == 0)
			return ""; //$NON-NLS-1$
		return String.format("%s%s", inPrefix, inToCheck);
	}

	/**
	 * Puts prefix and ending if the item's not empty.
	 * 
	 * @param inToCheck
	 *            String
	 * @param inPrefix
	 *            String
	 * @param inPostfix
	 *            String
	 * @return String
	 */
	private String getCheckedPrePost(final String inToCheck,
			final String inPrefix, final String inPostfix) {
		if (inToCheck.length() == 0)
			return "";
		return String.format("%s%s%s", inPrefix, inToCheck, inPostfix);
	}

	// --- inner classes ---

	private class PartFormatter {
		private final static String PERIOD = ". ";

		private final StringBuilder parts = new StringBuilder();
		private StringBuilder sub = new StringBuilder();

		private boolean conditional = false;
		private StringBuilder conditionalPart;
		private String conditionalSeparator;

		PartFormatter addPart(final String inPart) {
			if (inPart.trim().length() == 0)
				return this;
			parts.append(inPart).append(PERIOD);
			return this;
		}

		PartFormatter append(final String inPart) {
			if (inPart.trim().length() == 0)
				return this;
			if (conditional) {
				conditionalPart.append(inPart);
			} else {
				sub.append(inPart);
			}
			return this;
		}

		PartFormatter conditional(final String inSep) {
			conditional = true;
			conditionalSeparator = inSep;
			conditionalPart = new StringBuilder();
			return this;
		}

		void close() {
			close(false);
		}

		void close(final boolean inBare) {
			if (sub.length() == 0) {
				conditional = false;
				return;
			}
			parts.append(sub);
			if (conditional) {
				if (conditionalPart.length() != 0) {
					parts.append(conditionalSeparator).append(conditionalPart);
				}
				conditional = false;
			}
			parts.append(inBare ? "" : PERIOD);
			sub = new StringBuilder();
		}

		String getJoined() {
			return new String(parts).trim();
		}

		@Override
		public String toString() {
			return getJoined();
		}
	}

}

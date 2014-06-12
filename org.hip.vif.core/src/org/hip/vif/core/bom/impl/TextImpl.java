/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2010, Benno Luthiger

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
package org.hip.vif.core.bom.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.SettingException;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.DefaultExceptionWriter;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.AssertionFailedError;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.DownloadText;
import org.hip.vif.core.bom.DownloadTextHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.TextAuthorReviewerHome;
import org.hip.vif.core.bom.TextHistory;
import org.hip.vif.core.bom.TextHistoryHome;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.search.VIFContentIndexer;
import org.hip.vif.core.exc.Assert;
import org.hip.vif.core.exc.Assert.AssertLevel;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.search.AbstractSearching;
import org.hip.vif.core.search.FullTextHelper;
import org.hip.vif.core.search.Indexable;
import org.hip.vif.core.util.BibliographyHelper;
import org.hip.vif.core.util.QuestionHierarchyEntry;

/**
 * The implementation of the <code>Text</code> model.
 * 
 * @author Luthiger Created: 14.06.2010
 */
@SuppressWarnings("serial")
public class TextImpl extends WorkflowAwareContribution implements Text,
		Indexable, QuestionHierarchyEntry {
	public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.TextHomeImpl";

	// private static final String TMPL_OPTION =
	// "<option value=\"%s\"%s>%s</option>";
	// private static final String NL = System.getProperty("line.separator");
	// private static final String TMPL_MAIL_PLAIN = "%s: %s";
	// private static final String TMPL_MAIL_HTML =
	// "<tr><td><i>%s</i>:</td><td>%s</td></tr>";

	private static final int LEN_REFERENCE = 20;

	/**
	 * Constructor
	 * 
	 * @throws WorkflowException
	 */
	public TextImpl() throws WorkflowException {
		super();
	}

	/**
	 * This Method returns the class name of the home.
	 * 
	 * @return java.lang.String
	 */
	@Override
	public String getHomeClassName() {
		return HOME_CLASS_NAME;
	}

	/**
	 * Use case: save the values of a new text entry.
	 * 
	 * @param inValues
	 *            {@link ITextValues}
	 * @param inActorID
	 *            Long
	 * @return Long the id of the newly created <code>Text</code> entry.
	 * @throws BOMChangeValueException
	 */
	@Override
	public Long ucNew(final ITextValues inValues, final Long inActorID)
			throws BOMChangeValueException {
		try {
			set(TextHome.KEY_REFERENCE,
					createReference(inValues.getBiblioAuthor(),
							inValues.getBiblioYear()));
		}
		catch (final VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (final SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		return ucNew(0, inValues, inActorID);
	}

	@Override
	public Long ucNew(final Long inActorID) throws BOMChangeValueException {
		try {
			preCheck(get(TextHome.KEY_TITLE).toString(),
					get(TextHome.KEY_AUTHOR).toString(), get(TextHome.KEY_YEAR)
							.toString());

			set(TextHome.KEY_REFERENCE,
					createReference(get(TextHome.KEY_AUTHOR).toString(),
							get(TextHome.KEY_YEAR).toString()));
			set(TextHome.KEY_VERSION, new Long(0));
			set(TextHome.KEY_STATE, new Long(
					WorkflowAwareContribution.S_PRIVATE));
			final Long outTextID = insert(true);

			// create entry in authors table
			BOMHelper.getTextAuthorReviewerHome().setAuthor(inActorID,
					outTextID, 0);

			return outTextID;
		}
		catch (final VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (final SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hip.vif.bom.Text#ucNew(java.lang.Long, int,
	 * org.hip.vif.bom.Text.ITextValues, java.lang.Long)
	 */
	@Override
	public Long ucNew(final Long inTextID, final int inVersion,
			final String inReference, final ITextValues inValues,
			final Long inActorID) throws BOMChangeValueException {
		try {
			set(TextHome.KEY_ID, inTextID);
			set(TextHome.KEY_REFERENCE, inReference);
			return ucNew(inVersion, inValues, inActorID);
		}
		catch (final SettingException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	private Long ucNew(final int inVersion, final ITextValues inValues,
			final Long inActorID) throws BOMChangeValueException {
		preCheck(inValues.getBiblioTitle(), inValues.getBiblioAuthor(),
				inValues.getBiblioYear());

		try {
			setValuesToModel(inValues);
			set(TextHome.KEY_VERSION, new Long(inVersion));
			set(TextHome.KEY_STATE, new Long(
					WorkflowAwareContribution.S_PRIVATE));
			final Long outTextID = insert(true);

			// create entry in authors table
			BOMHelper.getTextAuthorReviewerHome().setAuthor(inActorID,
					outTextID, inVersion);

			return outTextID;
		}
		catch (final SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (final VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	/**
	 * Use case: save changed values to <code>Text</code> model.
	 * 
	 * @param inValues
	 *            {@link ITextValues} parameter object containing the new
	 *            values.
	 * @param inActorID
	 *            Long the author's id
	 * @throws BOMChangeValueException
	 */
	@Override
	public void ucSave(final ITextValues inValues, final Long inActorID)
			throws BOMChangeValueException {
		ucSave(inValues, null, inActorID);
	}

	/**
	 * Use case: save changed values to <code>Text</code> model.
	 * 
	 * @param inValues
	 *            {@link ITextValues} parameter object containing the new
	 *            values.
	 * @param inState
	 *            String the model's workflow state
	 * @param inActorID
	 *            Long the author's id
	 * @throws BOMChangeValueException
	 */
	@Override
	public void ucSave(final ITextValues inValues, final String inState,
			final Long inActorID) throws BOMChangeValueException {
		preCheck(inValues.getBiblioTitle(), inValues.getBiblioAuthor(),
				inValues.getBiblioYear());
		try {
			// historize changes
			final TextHistory lHistory = (TextHistory) BOMHelper
					.getTextHistoryHome().create();
			fillHistory(lHistory);

			// update text model
			setValuesToModel(inValues);
			if (inState != null) {
				set(TextHome.KEY_STATE, new Long(inState));
			}

			// save changes only if new values differ from old ones
			if (!isChanged())
				return;

			final Timestamp lMutationDate = new Timestamp(
					System.currentTimeMillis());
			lHistory.set(TextHome.KEY_TO, lMutationDate);
			lHistory.set(TextHistoryHome.KEY_MEMBER_ID, inActorID);
			lHistory.insert(true);

			final Object lTo = get(TextHome.KEY_TO);
			if (lTo != null) {
				set(TextHome.KEY_FROM, lTo);
			}
			set(TextHome.KEY_TO, lMutationDate);
			update(true);

			updateAuthor(inActorID);
		}
		catch (final VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (final SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	private void updateAuthor(final Long inActorID) throws VException,
			SQLException {
		final Long lTextID = new Long(get(TextHome.KEY_ID).toString());
		final Long lTextVersion = new Long(get(TextHome.KEY_VERSION).toString());

		final KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, lTextID);
		lKey.setValue(TextAuthorReviewerHome.KEY_VERSION, lTextVersion);
		lKey.setValue(ResponsibleHome.KEY_TYPE,
				ResponsibleHome.Type.AUTHOR.getValue());
		final DomainObject lAuthor = BOMHelper.getTextAuthorReviewerHome()
				.findByKey(lKey);
		if (!(lAuthor.get(ResponsibleHome.KEY_MEMBER_ID)).equals(new Long(
				inActorID.toString()))) {
			lAuthor.set(ResponsibleHome.KEY_MEMBER_ID, inActorID);
			lAuthor.update(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hip.vif.bom.Text#ucSave(java.lang.Long)
	 */
	@Override
	public void ucSave(final Long inActorID) throws BOMChangeValueException {
		if (!isChanged())
			return;
		try {
			preCheck(get(TextHome.KEY_TITLE).toString(),
					get(TextHome.KEY_AUTHOR).toString(), get(TextHome.KEY_YEAR)
							.toString());

			// historize changes
			final Text lOriginal = BOMHelper.getTextHome().getText(
					getIDVersion());
			final DomainObject lHistory = ((TextImpl) lOriginal)
					.createHistory();

			final Timestamp lMutationDate = new Timestamp(
					System.currentTimeMillis());
			lHistory.set(TextHome.KEY_TO, lMutationDate);
			lHistory.set(TextHistoryHome.KEY_MEMBER_ID, inActorID);
			lHistory.insert(true);

			final Object lTo = get(TextHome.KEY_TO);
			if (lTo != null) {
				set(TextHome.KEY_FROM, lTo);
			}
			set(TextHome.KEY_TO, lMutationDate);
			update(true);

			updateAuthor(inActorID);
		}
		catch (final BOMException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (final VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (final SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	@Override
	public Long createNewVersion(final Long inActorID)
			throws BOMChangeValueException {
		try {
			preCheck(get(TextHome.KEY_TITLE).toString(),
					get(TextHome.KEY_AUTHOR).toString(), get(TextHome.KEY_YEAR)
							.toString());

			int lVersion = 0;
			final Object lID = get(TextHome.KEY_ID);
			if (lID != null) {
				lVersion = BOMHelper.getTextMaxHome().getMaxVersion((Long) lID) + 1;
			}
			set(TextHome.KEY_VERSION, new Long(lVersion));
			set(TextHome.KEY_STATE, new Long(
					WorkflowAwareContribution.S_PRIVATE));
			set(TextHome.KEY_FROM, new Timestamp(System.currentTimeMillis()));
			set(TextHome.KEY_TO, null);

			final Long outTextID = insert(true);
			// create entry in authors table
			BOMHelper.getTextAuthorReviewerHome().setAuthor(inActorID,
					outTextID, lVersion);

			return outTextID;
		}
		catch (final VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (final SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hip.vif.bom.Text#setValuesToModel(org.hip.vif.bom.Text.ITextValues)
	 */
	@Override
	public void setValuesToModel(final ITextValues inValues)
			throws SettingException {
		set(TextHome.KEY_TYPE, inValues.getBiblioType());
		set(TextHome.KEY_TITLE, inValues.getBiblioTitle());
		set(TextHome.KEY_AUTHOR, inValues.getBiblioAuthor());
		set(TextHome.KEY_COAUTHORS, inValues.getBiblioCoAuthor());
		set(TextHome.KEY_SUBTITLE, inValues.getBiblioSubtitle());
		set(TextHome.KEY_YEAR, inValues.getBiblioYear());
		set(TextHome.KEY_PUBLICATION, inValues.getBiblioPublication());
		set(TextHome.KEY_PAGES, inValues.getBiblioPages());
		set(TextHome.KEY_VOLUME, inValues.getBiblioVolume());
		set(TextHome.KEY_NUMBER, inValues.getBiblioNumber());
		set(TextHome.KEY_PUBLISHER, inValues.getBiblioPublisher());
		set(TextHome.KEY_PLACE, inValues.getBiblioPlace());
		set(TextHome.KEY_REMARK, inValues.getBiblioText());
	}

	private String createReference(final String inAuthor, final String inYear)
			throws VException, SQLException {
		final String[] lAuthorParts = inAuthor.split(",|\\s");
		final String lAuthor = inAuthor.indexOf(",") == -1 ? lAuthorParts[lAuthorParts.length - 1]
				: lAuthorParts[0];
		final String lYear = inYear.trim();
		final String outReference = String.format(
				"%s %s",
				lAuthor.substring(0,
						Math.min(LEN_REFERENCE - 5, lAuthor.length())),
				lYear.substring(0, Math.min(4, lYear.length())));
		final TextHome lHome = BOMHelper.getTextHome();
		return lHome.checkReference(outReference);
	}

	private void preCheck(final String inTitle, final String inAuthor,
			final String inYear) {
		// pre: Title must be set
		Assert.assertTrue(AssertLevel.ERROR, this, "preCheck",
				!"".equals(inTitle));

		// pre: Author must be set
		Assert.assertTrue(AssertLevel.ERROR, this, "preCheck",
				!"".equals(inAuthor));

		// pre: Year must be set
		Assert.assertTrue(AssertLevel.ERROR, this, "preCheck",
				!"".equals(inYear));
	}

	@Override
	void addToIndex() throws WorkflowException {
		try {
			final KeyObject lKey = createTextVersionKey();
			lKey.setValue(BOMHelper.getKeyPublished(TextHome.KEY_STATE));
			final VIFContentIndexer lIndexer = new VIFContentIndexer();
			lIndexer.addTextToIndex(lKey);
		}
		catch (final VException exc) {
			throw new WorkflowException(exc.getMessage());
		}
		catch (final SQLException exc) {
			throw new WorkflowException(exc.getMessage());
		}
		catch (final IOException exc) {
			throw new WorkflowException(exc.getMessage());
		}
	}

	@Override
	void removeFromIndex() throws WorkflowException {
		try {
			final VIFContentIndexer lIndexer = new VIFContentIndexer();
			lIndexer.deleteTextInIndex(get(TextHome.KEY_ID).toString());
		}
		catch (final VException exc) {
			throw new WorkflowException(exc.getMessage());
		}
		catch (final IOException exc) {
			throw new WorkflowException(exc.getMessage());
		}
	}

	private KeyObject createTextVersionKey() throws VException {
		final KeyObject outKey = new KeyObjectImpl();
		outKey.setValue(TextHome.KEY_ID, get(TextHome.KEY_ID));
		outKey.setValue(TextHome.KEY_VERSION, get(TextHome.KEY_VERSION));
		return outKey;
	}

	@Override
	String getActualStateValue() throws GettingException {
		return get(TextHome.KEY_STATE).toString();
	}

	@Override
	void setState(final int inNewState, final Long inAuthorID)
			throws BOMChangeValueException {
		try {
			insertHistoryEntry(BOMHelper.getTextHistoryHome().create(),
					TextHome.KEY_TO, TextHistoryHome.KEY_MEMBER_ID, inAuthorID);
			set(TextHome.KEY_STATE, new Long(inNewState));
		}
		catch (final Exception exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	@Override
	public Long getNodeID() throws VException {
		return new Long(get(TextHome.KEY_ID).toString());
	}

	@Override
	public boolean isNode() {
		return true;
	}

	/**
	 * @throws IOException
	 * @throws VException
	 * @see Indexable#indexContent(IndexWriter)
	 */
	@Override
	public void indexContent(final IndexWriter inWriter) throws IOException,
			VException {
		final FullTextHelper lFullText = new FullTextHelper();
		final Document lDocument = new Document();
		lDocument.add(AbstractSearching.IndexField.BIBLIO_ID.createField(get(
				TextHome.KEY_ID).toString()));
		lDocument.add(AbstractSearching.IndexField.BIBLIO_TITLE
				.createField(lFullText.add(get(TextHome.KEY_TITLE))));
		lDocument.add(AbstractSearching.IndexField.BIBLIO_AUTHOR
				.createField(lFullText.add(get(TextHome.KEY_AUTHOR))));
		lDocument.add(AbstractSearching.IndexField.BIBLIO_COAUTHOR
				.createField(lFullText.add(get(TextHome.KEY_COAUTHORS))));
		lDocument.add(AbstractSearching.IndexField.BIBLIO_SUBTITLE
				.createField(lFullText.add(get(TextHome.KEY_SUBTITLE))));
		lDocument.add(AbstractSearching.IndexField.BIBLIO_YEAR
				.createField(getChecked(TextHome.KEY_YEAR)));
		lDocument.add(AbstractSearching.IndexField.BIBLIO_PUBLICATION
				.createField(lFullText.add(get(TextHome.KEY_PUBLICATION))));
		lDocument.add(AbstractSearching.IndexField.BIBLIO_PAGES
				.createField(getChecked(TextHome.KEY_PAGES)));
		lDocument.add(AbstractSearching.IndexField.BIBLIO_VOLUME
				.createField(getChecked(TextHome.KEY_VOLUME)));
		lDocument.add(AbstractSearching.IndexField.BIBLIO_NUMBER
				.createField(getChecked(TextHome.KEY_NUMBER)));
		lDocument.add(AbstractSearching.IndexField.BIBLIO_PUBLISHER
				.createField(lFullText.add(get(TextHome.KEY_PUBLISHER))));
		lDocument.add(AbstractSearching.IndexField.BIBLIO_PLACE
				.createField(lFullText.add(get(TextHome.KEY_PLACE))));
		lDocument.add(AbstractSearching.IndexField.BIBLIO_REMARK
				.createField(lFullText.add(get(TextHome.KEY_REMARK))));
		synchronized (this) {
			inWriter.addDocument(lDocument);
		}
	}

	private String getChecked(final String inKey) throws VException {
		final Object out = get(inKey);
		return out == null ? "" : out.toString();
	}

	/**
	 * <code>&lt;option value="Value">Label&lt;/option></code>
	 * 
	 * @param inLocale
	 *            {@link Locale}
	 * @return String the html for the options in an html select of a text type.
	 */
	// public static String getOptions(Locale inLocale) {
	// StringBuilder out = new StringBuilder();
	// for (TextType lTextType : TextType.values()) {
	// out.append(lTextType.renderHtml(false)).append(NL);
	// }
	// return new String(out);
	// }

	/**
	 * <code>&lt;option value="Value" selected="selected">Label&lt;/option></code>
	 * 
	 * @return String the html for the options in an html select of a text type.
	 *         The appropriate option is displayed <code>selected</code>.
	 * @throws VException
	 */
	// public String getOptionsSelected() throws VException {
	// Object lType = get(TextHome.KEY_TYPE);
	// StringBuilder out = new StringBuilder();
	// for (TextType lTextType : TextType.values()) {
	// out.append(lTextType.renderHtml(lTextType.checkType(lType))).append(NL);
	// }
	// return new String(out);
	// }

	@Override
	public void setReviewer(final Long inReviewerID) throws VException,
			SQLException {
		BOMHelper.getTextAuthorReviewerHome().setReviewer(inReviewerID,
				new Long(get(TextHome.KEY_ID).toString()),
				Integer.parseInt(get(TextHome.KEY_VERSION).toString()));
	}

	@Override
	public void accept(final QuestionHierarchyVisitor inVisitor)
			throws VException, SQLException {
		inVisitor.visitText(this);
	}

	// @Override
	// public String getNotification() throws VException, SQLException {
	// final IMessages lMessages = Activator.getMessages();
	// StringBuilder out = createNotification(TMPL_MAIL_PLAIN, lMessages, NL);
	//
	// // special treatment for remark
	// final Object lValue = get(TextHome.KEY_REMARK);
	// if (lValue != null && lValue.toString().length() > 0) {
	// out.append(
	// String.format(TMPL_MAIL_PLAIN, lMessages
	// .getMessage("org.hip.vif.bibliography.remark"),
	// HtmlCleaner.toPlain(lValue.toString()))).append(NL);
	// }
	// out = appendFileChecked(out,
	// lMessages.getMessage("org.hip.vif.bibliography.file"),
	// TMPL_MAIL_PLAIN, NL);
	// return new String(out);
	// }
	//
	// @Override
	// public String getNotificationHtml() throws VException, SQLException {
	// final IMessages lMessages = Activator.getMessages();
	// StringBuilder out = createNotification(TMPL_MAIL_HTML, lMessages, "");
	//
	// // special treatment for remark, because we want wiki text properly
	// // formatted
	// final Object lValue = get(TextHome.KEY_REMARK);
	// if (lValue != null && lValue.toString().length() > 0) {
	// out.append(String.format(TMPL_MAIL_HTML,
	// lMessages.getMessage("org.hip.vif.bibliography.remark"),
	// lValue.toString()));
	// }
	// out = appendFileChecked(out,
	// lMessages.getMessage("org.hip.vif.bibliography.file"),
	// TMPL_MAIL_HTML, "");
	// return String
	// .format("<table border=\"0\" style=\"font-size:11pt;\">%s</table>",
	// out);
	// }
	//
	// private StringBuilder createNotification(final String inTemplate,
	// final IMessages inMessages, final String inNL) throws VException {
	// StringBuilder out = new StringBuilder();
	// out.append(
	// String.format(inTemplate,
	// inMessages.getMessage("org.hip.vif.bibliography.type"),
	// getType().getLabel())).append(inNL);
	// out.append(
	// String.format(
	// inTemplate,
	// inMessages.getMessage("org.hip.vif.bibliography.title"),
	// get(TextHome.KEY_TITLE).toString())).append(inNL);
	// out = appendPlainChecked(out, TextHome.KEY_SUBTITLE,
	// inMessages.getMessage("org.hip.vif.bibliography.subtitle"),
	// inTemplate, inNL);
	// out.append(
	// String.format(inTemplate, inMessages
	// .getMessage("org.hip.vif.bibliography.author"),
	// get(TextHome.KEY_AUTHOR).toString())).append(inNL);
	// out = appendPlainChecked(out, TextHome.KEY_COAUTHORS,
	// inMessages.getMessage("org.hip.vif.bibliography.coauthors"),
	// inTemplate, inNL);
	// out = appendPlainChecked(out, TextHome.KEY_YEAR,
	// inMessages.getMessage("org.hip.vif.bibliography.year"),
	// inTemplate, inNL);
	// out = appendPlainChecked(out, TextHome.KEY_PUBLICATION,
	// inMessages.getMessage("org.hip.vif.bibliography.publication"),
	// inTemplate, inNL);
	// out = appendPlainChecked(out, TextHome.KEY_PUBLISHER,
	// inMessages.getMessage("org.hip.vif.bibliography.publisher"),
	// inTemplate, inNL);
	// out = appendPlainChecked(out, TextHome.KEY_PLACE,
	// inMessages.getMessage("org.hip.vif.bibliography.place"),
	// inTemplate, inNL);
	// out = appendPlainChecked(out, TextHome.KEY_PAGES,
	// inMessages.getMessage("org.hip.vif.bibliography.pages"),
	// inTemplate, inNL);
	// out = appendPlainChecked(out, TextHome.KEY_VOLUME,
	// inMessages.getMessage("org.hip.vif.bibliography.volume"),
	// inTemplate, inNL);
	// out = appendPlainChecked(out, TextHome.KEY_NUMBER,
	// inMessages.getMessage("org.hip.vif.bibliography.number"),
	// inTemplate, inNL);
	// return out;
	// }
	//
	// private StringBuilder appendPlainChecked(final StringBuilder inText,
	// final String inKey, final String inMsg, final String inTemplate,
	// final String inNL) throws VException {
	// final Object lValue = get(inKey);
	// if (lValue == null)
	// return inText;
	// if (lValue.toString().length() == 0)
	// return inText;
	//
	// inText.append(String.format(inTemplate, inMsg, lValue.toString()))
	// .append(inNL);
	// return inText;
	// }
	//
	// private StringBuilder appendFileChecked(final StringBuilder inText,
	// final String inMessage, final String inTemplate, final String inNL)
	// throws VException, SQLException {
	// final QueryResult lDownloads = BOMHelper.getDownloadTextHome()
	// .getDownloads((Long) get(TextHome.KEY_ID));
	// while (lDownloads.hasMoreElements()) {
	// inText.append(
	// String.format(inTemplate, inMessage,
	// lDownloads.next().get(DownloadTextHome.KEY_LABEL)))
	// .append(inNL);
	// }
	// return inText;
	// }
	//
	// private TextType getType() throws GettingException {
	// final Object lTypeValue = get(TextHome.KEY_TYPE);
	// for (final TextType lType : TextType.values()) {
	// if (lType.checkType(lTypeValue))
	// return lType;
	// }
	// return TextType.BOOK;
	// }

	// @Override
	// public void accept(final DomainObjectVisitor inVisitor) {
	// try {
	// final BibliographyFormatter lFormatter = new BibliographyFormatter(
	// new BibliographyAdapter(this, TextHome.KEY_TYPE));
	// propertySet().setValue(TextHome.KEY_BIBLIOGRAPHY,
	// lFormatter.renderHtml());
	// }
	// catch (final VException exc) {
	// DefaultExceptionWriter.printOut(this, exc, true);
	// }
	// super.accept(inVisitor);
	// }

	@Override
	public String getIDVersion() throws VException {
		return String.format(Text.FORMAT_ID_VERSION, get(TextHome.KEY_ID),
				get(TextHome.KEY_VERSION));
	}

	@Override
	public int getVersion() throws VException {
		final Object out = get(TextHome.KEY_VERSION);
		return out == null ? 0 : Integer.parseInt(out.toString());
	}

	@Override
	public void onTransition_Delete(final Long inAuthorID) {
		super.onTransition_Delete(inAuthorID);

		try {
			final Long lTextID = (Long) get(TextHome.KEY_ID);

			// delete download files
			final DownloadTextHome lDownloadHome = BOMHelper
					.getDownloadTextHome();
			final QueryResult lDownloads = lDownloadHome.getDownloads(lTextID);
			while (lDownloads.hasMoreElements()) {
				final DownloadText lDownload = (DownloadText) lDownloads.next();
				BibliographyHelper.deleteUpload(lDownload);
				lDownload.delete(true);
			}

			// delete link text to question
			BOMHelper.getTextQuestionHome().deleteByText(lTextID);
		}
		catch (final VException exc) {
			DefaultExceptionWriter.printOut(this, exc, true);
		}
		catch (final SQLException exc) {
			DefaultExceptionWriter.printOut(this, exc, true);
		}
	}

	@Override
	public void onTransition_Publish(final Long inReviewerID)
			throws WorkflowException {
		processPublishedVersion(inReviewerID);
		super.onTransition_Publish(inReviewerID);
	}

	@Override
	public void onTransition_AdminPublish(final Long inAuthorID)
			throws WorkflowException {
		processPublishedVersion(inAuthorID);
		super.onTransition_AdminPublish(inAuthorID);
	}

	/**
	 * We only accept one published text entry per time. Therefore, we have to
	 * check for a published version and, if existing, set it deleted
	 * 
	 * @param inReviewerID
	 *            Long
	 * @throws WorkflowException
	 */
	private void processPublishedVersion(final Long inReviewerID)
			throws WorkflowException {
		try {
			final Long lTextID = (Long) get(TextHome.KEY_ID);
			final int lVersion = Integer.parseInt(get(TextHome.KEY_VERSION)
					.toString());

			// check if this entry has a version > 0
			if (lVersion > 0) {
				// if yes, look for a published version
				try {
					// if found, set it deleted
					final Text lPublished = BOMHelper.getTextHome()
							.getTextPublished(lTextID);
					((WorkflowAwareContribution) lPublished).doTransition(
							TRANS_ADMIN_DELETE2, new Object[] { inReviewerID });
				}
				catch (final BOMNotFoundException exc1) {
					// intentionally left empty
				}
			}
		}
		catch (final VException exc) {
			throw new WorkflowException(exc.getMessage());
		}
		catch (final SQLException exc) {
			throw new WorkflowException(exc.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hip.vif.bom.VIFWorkflowAware#checkRefused(java.lang.Long)
	 */
	@Override
	public boolean checkRefused(final Long inReviewerID) throws VException,
			SQLException {
		return BOMHelper.getTextAuthorReviewerHome().checkRefused(inReviewerID,
				new Long(get(TextHome.KEY_ID).toString()), getVersion());
	}

	@Override
	protected DomainObject getHistoryObject() throws BOMException {
		return BOMHelper.getTextHistoryHome().create();
	}

	@Override
	public boolean isValid() {
		try {
			preCheck(get(TextHome.KEY_TITLE).toString(),
					get(TextHome.KEY_AUTHOR).toString(), get(TextHome.KEY_YEAR)
							.toString());
			return true;
		}
		catch (final Exception exc) {
			return false;
		}
		catch (final AssertionFailedError exc) {
			return false;
		}
	}

	/**
	 * Splits the text version's unique ID <code>ID-version</code> and returns
	 * the ID part (<code>Long</code> value).
	 * 
	 * @param inTextID
	 *            String
	 * @return Long the ID part that can be converted to a <code>Long</code>
	 */
	public static Long splitTextID(final String inTextID) {
		return Long.parseLong(inTextID.split(Text.DELIMITER_ID_VERSION)[0]); //$NON-NLS-1$
	}

}

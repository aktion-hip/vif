/**
 This package is part of the administration of the application VIF.
 Copyright (C) 2008-2014, Benno Luthiger

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
package org.hip.vif.admin.admin.print;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarOutputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.XMLSerializer;
import org.hip.kernel.exc.VException;
import org.hip.kernel.servlet.ISourceCreatorStrategy;
import org.hip.kernel.util.TransformerProxy;
import org.hip.kernel.util.XMLRepresentation;
import org.hip.kernel.util.XSLProcessingException;
import org.hip.vif.admin.admin.Constants;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.impl.JoinQuestionToCompletionAndContributors;
import org.hip.vif.core.bom.impl.JoinQuestionToContributors;
import org.hip.vif.core.util.CleanupSerializer;
import org.hip.vif.core.util.Contributor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.ripla.interfaces.IMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class, renders the content of a discussion group to an output stream.<br/>
 * This class contains a structure of the group content, i.e. the questions
 * including their completions.
 * 
 * @author Luthiger Created: 20.09.2009
 */
public class GroupContentRenderer {
	private static final Logger LOG = LoggerFactory
			.getLogger(GroupContentRenderer.class);

	private static final String XML_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>%s"; //$NON-NLS-1$

	private static final String XSL_GROUP = "print_odtGroup.xsl"; //$NON-NLS-1$
	private static final String XSL_QUESTION = "print_odtQuestion.xsl"; //$NON-NLS-1$
	private static final String XSL_COMPLETION = "print_odtCompletion.xsl"; //$NON-NLS-1$
	private static final String XSL_BIBLIOGRAPHY = "print_odtBibliography.xsl"; //$NON-NLS-1$

	private final XMLSerializer serializer = new CleanupSerializer();

	private final Map<Long, CompletionRenderer> completions = new Hashtable<Long, CompletionRenderer>();
	private final Map<Long, QuestionRenderer> questions = new Hashtable<Long, QuestionRenderer>();

	private final String content;
	private final Locale locale;
	private final IMessages messages;

	/**
	 * GroupContentRenderer constructor.
	 * 
	 * @param inGroup
	 *            GroupExtent
	 * @param inLocale
	 *            Locale
	 * @throws Exception
	 */
	public GroupContentRenderer(final GroupExtent inGroup,
			final Locale inLocale, final IMessages inMessages) throws Exception {
		locale = inLocale;
		messages = inMessages;
		serializer.setLocale(locale);

		evaluateCompletions(inGroup.getCompletions());
		evaluateQuestions(inGroup.getQuestions());
		consolidateQuestions();

		serializer.clear();
		inGroup.getGroup().accept(serializer);
		content = serializer.toString();
	}

	private void consolidateQuestions() {
		for (final CompletionRenderer lCompletion : completions.values()) {
			final QuestionRenderer lQuestion = questions.get(lCompletion
					.getQuestionID());
			lQuestion.addCompletion(lCompletion);
		}
	}

	private void evaluateQuestions(final QueryResult inQuestions)
			throws VException, SQLException {
		while (inQuestions.hasMoreElements()) {
			final JoinQuestionToContributors lQuestion = (JoinQuestionToContributors) inQuestions
					.next();
			final Long lQuestionID = lQuestion.getID();
			QuestionRenderer lQuestionRenderer = questions.get(lQuestionID);
			if (lQuestionRenderer == null) {
				lQuestionRenderer = new QuestionRenderer(lQuestion);
				questions.put(lQuestionID, lQuestionRenderer);
			} else {
				lQuestionRenderer.addContributor(lQuestion.getContributor());
			}
		}
	}

	private void evaluateCompletions(final QueryResult inCompletions)
			throws VException, SQLException {
		while (inCompletions.hasMoreElements()) {
			final JoinQuestionToCompletionAndContributors lCompletion = (JoinQuestionToCompletionAndContributors) inCompletions
					.next();
			final Long lCompletionID = lCompletion.getID();
			CompletionRenderer lCompletionRenderer = completions
					.get(lCompletionID);
			if (lCompletionRenderer == null) {
				lCompletionRenderer = new CompletionRenderer(lCompletion);
				completions.put(lCompletionID, lCompletionRenderer);
			} else {
				lCompletionRenderer
						.addContributor(lCompletion.getContributor());
			}
		}
	}

	/**
	 * Renders the content of the group formated as OOorg document to the output
	 * stream.
	 * 
	 * @param inJar
	 *            JarOutputStream
	 * @throws XSLProcessingException
	 */
	public void renderAsOO(final JarOutputStream inJar)
			throws XSLProcessingException {
		// first render the group information
		final TransformerProxy lTransformer = new TransformerProxy(new XSL(
				XSL_GROUP), new XMLRepresentation(String.format(XML_TEMPLATE,
				content)), createStyleSheetParamter(
				"GroupLbl", getMessage("admin.print.lbl.group"))); //$NON-NLS-1$ //$NON-NLS-2$
		lTransformer.renderToStream(inJar, ""); //$NON-NLS-1$
		LOG.debug(String.format(XML_TEMPLATE, content));

		// then render each question
		final List<QuestionRenderer> lQuestions = new Vector<QuestionRenderer>(
				questions.values());
		Collections.sort(lQuestions);
		for (final QuestionRenderer lQuestion : lQuestions) {
			lQuestion.renderAsOO(inJar);
		}
	}

	private HashMap<String, Object> createStyleSheetParamter(
			final String inName, final String inValue) {
		final HashMap<String, Object> outParameters = new HashMap<String, Object>();
		outParameters.put(inName, inValue);
		return outParameters;
	}

	private String serializeContributors(
			final Collection<Contributor> inContributors) {
		serializer.clear();
		for (final Contributor lContributor : inContributors) {
			lContributor.accept(serializer);
		}
		return serializer.toString();
	}

	private String getMessage(final String inMessageKey) {
		return messages.getMessage(inMessageKey);
	}

	// --- private classes ---

	private class CompletionRenderer {
		private static final String XML_COMPLETION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><completion>%s<contributors>%s</contributors></completion>"; //$NON-NLS-1$

		private String content = ""; //$NON-NLS-1$
		private final Long questionID;
		private final Collection<Contributor> contributors = new Vector<Contributor>();

		CompletionRenderer(
				final JoinQuestionToCompletionAndContributors inCompletion)
				throws VException {
			questionID = inCompletion.getQuestionID();
			addContributor(inCompletion.getContributor());

			serializer.clear();
			inCompletion.accept(serializer);
			content = serializer.toString();
		}

		public void addContributor(final Contributor inContributor) {
			contributors.add(inContributor);
		}

		public Long getQuestionID() {
			return questionID;
		}

		public void renderAsOO(final JarOutputStream inJar)
				throws XSLProcessingException {
			final HashMap<String, Object> lParameters = createStyleSheetParamter(
					"CompletionLbl", getMessage("admin.print.lbl.completion")); //$NON-NLS-1$ //$NON-NLS-2$
			lParameters.put("AuthorLbl", getMessage("admin.print.lbl.author")); //$NON-NLS-1$ //$NON-NLS-2$
			lParameters.put(
					"ReviewerLbl", getMessage("admin.print.lbl.reviewer")); //$NON-NLS-1$ //$NON-NLS-2$
			final TransformerProxy lTransformer = new TransformerProxy(new XSL(
					XSL_COMPLETION), new XMLRepresentation(String.format(
					XML_COMPLETION, content,
					serializeContributors(contributors))), lParameters);
			lTransformer.renderToStream(inJar, ""); //$NON-NLS-1$
			LOG.debug(String.format(XML_COMPLETION, content,
					serializeContributors(contributors)));
		}
	}

	private class QuestionRenderer implements Comparable<QuestionRenderer> {
		private static final String XML_QUESTION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><question>%s<contributors>%s</contributors></question>"; //$NON-NLS-1$

		private String content = ""; //$NON-NLS-1$
		private final String sortString;
		private final TextRenderer texts;

		private final Collection<Contributor> contributors = new Vector<Contributor>();
		private final Collection<CompletionRenderer> completions = new Vector<CompletionRenderer>();

		public QuestionRenderer(final JoinQuestionToContributors inQuestion)
				throws VException, SQLException {
			sortString = inQuestion.getSortString();
			addContributor(inQuestion.getContributor());

			serializer.clear();
			inQuestion.accept(serializer);
			content = serializer.toString();

			texts = new TextRenderer(inQuestion.getID());
		}

		public void addCompletion(final CompletionRenderer inCompletion) {
			completions.add(inCompletion);
		}

		public void addContributor(final Contributor inContributor) {
			contributors.add(inContributor);
		}

		public String getSortString() {
			return sortString;
		}

		@Override
		public int compareTo(final QuestionRenderer inOuestion) {
			return sortString.compareTo(inOuestion.getSortString());
		}

		public void renderAsOO(final JarOutputStream inJar)
				throws XSLProcessingException {
			final HashMap<String, Object> lParameters = createStyleSheetParamter(
					"QuestionLbl", getMessage("admin.print.lbl.question")); //$NON-NLS-1$ //$NON-NLS-2$
			lParameters.put("RemarkLbl", getMessage("admin.print.lbl.remark")); //$NON-NLS-1$ //$NON-NLS-2$
			lParameters.put("AuthorLbl", getMessage("admin.print.lbl.author")); //$NON-NLS-1$ //$NON-NLS-2$
			lParameters.put(
					"ReviewerLbl", getMessage("admin.print.lbl.reviewer")); //$NON-NLS-1$ //$NON-NLS-2$
			lParameters.put("StateLbl", getMessage("admin.print.lbl.state")); //$NON-NLS-1$ //$NON-NLS-2$
			lParameters.put(
					"StateOpenLbl", getMessage("admin.print.lbl.state.open")); //$NON-NLS-1$ //$NON-NLS-2$
			lParameters
					.put("StateAnsweredRequestedLbl", getMessage("admin.print.lbl.state.requested")); //$NON-NLS-1$ //$NON-NLS-2$
			lParameters
					.put("StateAnsweredLbl", getMessage("admin.print.lbl.state.answered")); //$NON-NLS-1$ //$NON-NLS-2$
			final TransformerProxy lTransformer = new TransformerProxy(new XSL(
					XSL_QUESTION),
					new XMLRepresentation(String.format(XML_QUESTION, content,
							serializeContributors(contributors))), lParameters);
			lTransformer.renderToStream(inJar, ""); //$NON-NLS-1$
			LOG.debug(String.format(XML_QUESTION, content,
					serializeContributors(contributors)));

			// then render all completions
			for (final CompletionRenderer lCompletion : completions) {
				lCompletion.renderAsOO(inJar);
			}

			// then render the question entry's bibliography
			texts.renderAsOO(inJar);
		}
	}

	private class TextRenderer {
		private static final String XML_BIBLIOGRAPHY = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><texts>%s</texts>"; //$NON-NLS-1$

		private String texts = ""; //$NON-NLS-1$

		TextRenderer(final Long inQuestionID) throws VException, SQLException {
			final QueryResult lTexts = BOMHelper.getJoinQuestionToTextHome()
					.selectPublished(inQuestionID);
			serializer.clear();
			while (lTexts.hasMoreElements()) {
				lTexts.next().accept(serializer);
			}
			texts = serializer.toString();
		}

		public void renderAsOO(final JarOutputStream inJar)
				throws XSLProcessingException {
			if (texts.length() == 0)
				return;

			final TransformerProxy lTransformer = new TransformerProxy(
					new XSL(XSL_BIBLIOGRAPHY),
					new XMLRepresentation(String
							.format(XML_BIBLIOGRAPHY, texts)),
					createStyleSheetParamter(
							"BibliographyLbl", getMessage("admin.print.lbl.bibiograpy"))); //$NON-NLS-1$ //$NON-NLS-2$
			lTransformer.renderToStream(inJar, ""); //$NON-NLS-1$
			LOG.debug(String.format(XML_BIBLIOGRAPHY, texts));
		}

	}

	private static class XSL implements ISourceCreatorStrategy {
		private final String xslName;

		XSL(final String inFileName) {
			xslName = inFileName;
		}

		@Override
		public Source createSource() throws IOException {
			final Bundle lBundle = FrameworkUtil.getBundle(this.getClass());
			return new StreamSource(
					lBundle.getEntry(
							String.format(
									"%s/%s", Constants.LOCAL_RESOURCES_DIR, xslName)).openStream(), //$NON-NLS-1$
					lBundle.getEntry(Constants.LOCAL_RESOURCES_DIR)
							.toExternalForm());
		}

		@Override
		public String getResourceId() {
			return xslName;
		}
	}

}

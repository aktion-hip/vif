/**
	This package is part of the application VIF.
	Copyright (C) 2011-2014, Benno Luthiger

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
package org.hip.vif.admin.groupadmin.tasks;

import java.sql.SQLException;
import java.util.Collection;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowAware;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.Constants;
import org.hip.vif.admin.groupadmin.data.ContributionContainer;
import org.hip.vif.admin.groupadmin.data.ContributionWrapper;
import org.hip.vif.admin.groupadmin.ui.BibliographyDeleteView;
import org.hip.vif.admin.groupadmin.ui.ReferencingQuestionLookup;
import org.hip.vif.admin.groupadmin.util.BiblioDeleteHelper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.impl.TextImpl;
import org.hip.vif.core.bom.impl.TextQuestionHome;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.web.exc.VIFWebException;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.ripla.interfaces.IMessages;
import org.ripla.util.ParameterObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Component;

/**
 * Task to delete bibliography entries by displaying the deltable, referenced
 * and undeletable entries.
 * 
 * @author Luthiger Created: 08.12.2011
 */
@SuppressWarnings("serial")
@UseCaseController
public class BibliographyDeleteTask extends AbstractBibliographyTask implements
		ValueChangeListener {
	private static final Logger LOG = LoggerFactory
			.getLogger(BibliographyDeleteTask.class);

	private ContributionContainer deletable;
	private ContributionContainer referenced;
	private ContributionContainer undeletable;
	private Collection<Long> allEntries;

	@Override
	protected Component runChecked() throws RiplaException {
		try {
			final CodeList lCodeList = CodeListHome.instance().getCodeList(
					QuestionState.class, getAppLocale().getLanguage());
			final BiblioDeleteHelper lHelper = (BiblioDeleteHelper) getParameters()
					.get(Constants.KEY_PARAMETER_DELETE_TEXT);
			allEntries = lHelper.getAllTexts();
			deletable = retrieveTexts(lHelper.getDeletableTexts(), lCodeList);
			referenced = retrieveTexts(lHelper.getReferencedTexts(), lCodeList);
			undeletable = retrieveTexts(lHelper.getUndeletableTexts(),
					lCodeList);
			return new BibliographyDeleteView(deletable, referenced,
					undeletable, this);
		}
		catch (final SQLException exc) {
			throw createContactAdminException(exc);
		}
		catch (final VException exc) {
			throw new VIFWebException(exc);
		}
	}

	private ContributionContainer retrieveTexts(final Collection<Long> inTexts,
			final CodeList inCodeList) throws VException, SQLException {
		if (inTexts.isEmpty()) {
			return ContributionContainer.createEmpty();
		}

		final KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(TextHome.KEY_ID, inTexts);
		lKey.setValue(TextHome.KEY_STATE, WorkflowAwareContribution.S_OPEN,
				"=", BinaryBooleanOperator.AND); //$NON-NLS-1$
		final ContributionContainer out = ContributionContainer.createTexts(
				BOMHelper.getTextHome().select(lKey), inCodeList);
		// we preselect all entries
		for (final ContributionWrapper lEntry : out.getItemIds()) {
			lEntry.setChecked(true);
		}
		return out;
	}

	@Override
	public void valueChange(final ValueChangeEvent inEvent) {
		try {
			final Object lEntry = inEvent.getProperty().getValue();
			if (lEntry instanceof ContributionWrapper) {
				final ContributionWrapper lText = (ContributionWrapper) lEntry;
				final Long lTextID = Long.valueOf(TextImpl.splitTextID(lText
						.getID()));
				if (referenced.getItemIds().contains(lText)) {
					// show the list of questions referencing the specified text
					// entry
					final CodeList lCodeList = CodeListHome.instance()
							.getCodeList(QuestionState.class,
									getAppLocale().getLanguage());
					final ContributionContainer lReferencing = ContributionContainer
							.createQuestions(BOMHelper
									.getJoinTextToQuestionHome()
									.selectPublished(lTextID), lCodeList);
					final ReferencingQuestionLookup lLookup = new ReferencingQuestionLookup(
							lReferencing, 700, 300);
					if (lLookup.getParent() == null) {
						// TODO
						// ApplicationData.getWindow().addWindow(
						// lLookup.getLookupWindow());
					}
				} else {
					requestLookup(LookupType.BIBLIOGRAPHY, lTextID);
				}
			}
		}
		catch (final NumberFormatException exc) {
			LOG.error("Error encountered while looking up the entry!", exc); //$NON-NLS-1$
		}
		catch (final VException exc) {
			LOG.error("Error encountered while looking up the entry!", exc); //$NON-NLS-1$
		}
		catch (final SQLException exc) {
			LOG.error("Error encountered while looking up the entry!", exc); //$NON-NLS-1$
		}
	}

	/**
	 * Callback function, removes the references to the selected entries.
	 * 
	 * @param inReferenced
	 *            {@link ContributionContainer}
	 * @return boolean <code>True</code> if successful
	 */
	public boolean removeReferences(final ContributionContainer inReferenced) {
		try {
			for (final ContributionWrapper lEntry : inReferenced.getItemIds()) {
				if (lEntry.isChecked()) {
					unlinkBibliography(TextImpl.splitTextID(lEntry.getID()));
				}
			}
			final ParameterObject lParameter = new ParameterObject();
			lParameter
					.set(Constants.KEY_PARAMETER_DELETE_TEXT,
							new BiblioDeleteHelper(allEntries, getActor()
									.getActorID()));
			setParameters(lParameter);
			showNotification(Activator.getMessages().getMessage(
					"msg.bibliography.link.removedP")); //$NON-NLS-1$
			sendEvent(BibliographyDeleteTask.class);
			return true;
		}
		catch (final SQLException exc) {
			LOG.error("Error encountered while removing the references!", exc); //$NON-NLS-1$
		}
		catch (final VException exc) {
			LOG.error("Error encountered while removing the references!", exc); //$NON-NLS-1$
		}
		return false;
	}

	private void unlinkBibliography(final Long inTextID) throws SQLException,
			VException {
		final TextQuestionHome lHome = BOMHelper.getTextQuestionHome();
		final KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(TextQuestionHome.KEY_TEXTID, inTextID);
		lHome.delete(lKey, true);
	}

	/**
	 * Callback function, deletes the selected texts entries.
	 * 
	 * @param inDeletable
	 *            {@link ContributionContainer}
	 * @return boolean <code>True</code> if successful
	 */
	public boolean deleteTexts(final ContributionContainer inDeletable) {
		try {
			final TextHome lTextHome = BOMHelper.getTextHome();
			final Object[] lArguments = new Object[] { getActor().getActorID() };
			int lCount = 0;
			for (final ContributionWrapper lEntry : inDeletable.getItemIds()) {
				if (lEntry.isChecked()) {
					final WorkflowAware lBibliography = (WorkflowAware) lTextHome
							.getText(lEntry.getID());
					lBibliography.doTransition(
							WorkflowAwareContribution.TRANS_ADMIN_DELETE2,
							lArguments);
					lCount++;
				}
			}
			final IMessages lMessages = Activator.getMessages();
			showNotification(lCount == 1 ? lMessages
					.getMessage("admin.msg.contributions.delete.ok1") : lMessages.getMessage("admin.msg.contributions.delete.okP")); //$NON-NLS-1$ //$NON-NLS-2$
			sendEvent(BibliographyHandleTask.class);
			return true;
		}
		catch (final VException exc) {
			LOG.error("Error encountered while deleting the text entry!", exc); //$NON-NLS-1$
		}
		catch (final SQLException exc) {
			LOG.error("Error encountered while deleting the text entry!", exc); //$NON-NLS-1$
		}
		catch (final WorkflowException exc) {
			LOG.error("Error encountered while deleting the text entry!", exc); //$NON-NLS-1$
		}
		return false;
	}

}

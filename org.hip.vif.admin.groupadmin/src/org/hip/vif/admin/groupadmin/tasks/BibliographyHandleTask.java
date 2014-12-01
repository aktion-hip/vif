/**
 This package is part of the application VIF.
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
package org.hip.vif.admin.groupadmin.tasks;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.Constants;
import org.hip.vif.admin.groupadmin.data.ContributionContainer;
import org.hip.vif.admin.groupadmin.data.ContributionWrapper;
import org.hip.vif.admin.groupadmin.ui.BibliographySearchView;
import org.hip.vif.admin.groupadmin.util.BiblioDeleteHelper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.impl.TextImpl;
import org.hip.vif.core.bom.impl.TextQuestionHome;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.util.AutoCompleteHelper;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.ripla.interfaces.IMessages;
import org.ripla.util.ParameterObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;

/** Task to search for a Bibliography entry for that it can be linked to a question.
 *
 * @author Luthiger Created: 14.06.2010 */
@SuppressWarnings("serial")
@UseCaseController
public class BibliographyHandleTask extends AbstractBibliographyTask implements
ValueChangeListener {
    private static final Logger LOG = LoggerFactory
            .getLogger(BibliographyHandleTask.class);

    private ContributionContainer texts;

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_EDIT_BIBLIOGRAPHY;
    }

    @Override
    protected Component runChecked() throws RiplaException {
        final IMessages lMessages = Activator.getMessages();

        try {
            final Group lGroup = VifBOMHelper.getGroupHome().getGroup(
                    getGroupID());
            if (!lGroup.isParticipant(getActor().getActorID())) {
                return reDisplay(
                        lMessages.getMessage("errmsg.not.participant"), Notification.Type.WARNING_MESSAGE); //$NON-NLS-1$
            }

            loadContextMenu(Constants.MENU_SET_ID_GROUP_CONTENT);

            final String lQuestionID = getQuestionID().toString();
            final Question lQuestion = BOMHelper.getQuestionHome().getQuestion(
                    lQuestionID);

            final KeyObject lKey = BOMHelper.getKeyStates(TextHome.KEY_STATE,
                    WorkflowAwareContribution.STATES_PUBLISHED);
            final AutoCompleteHelper lHelper = new AutoCompleteHelper(
                    BOMHelper.getTextHome(), lKey);
            return new BibliographySearchView(lQuestion, lGroup,
                    lHelper.getTitlesContainer(),
                    lHelper.getAuthorsContainer(), this);
        } catch (final Exception exc) {
            throw createContactAdminException(exc);
        }
    }

    /** Do search for titles or authors.
     *
     * @param inTitle Object, input, may be <code>null</code>
     * @param inAuthor Object, input, may be <code>null</code>
     * @return boolean <code>true</code> if the input has been processed successfully, <code>false</code> else. */
    public boolean searchFor(final Object inTitle, final Object inAuthor) {
        try {
            final QueryResult lSearchResult = BOMHelper.getTextHome()
                    .selectTitleOrAuthor(
                            inTitle == null ? "" : inTitle.toString(), //$NON-NLS-1$
                                    inAuthor == null ? "" : inAuthor.toString()); //$NON-NLS-1$
            if (lSearchResult.hasMoreElements()) {
                // display list of search results
                texts = ContributionContainer.createTexts(
                        lSearchResult,
                        CodeListHome.instance().getCodeList(
                                QuestionState.class,
                                getAppLocale().getLanguage()));
                return true;
            } else {
                return createNew(inTitle, inAuthor);
            }
        } catch (final VException exc) {
            LOG.error("Error while searching for bibliography items.", exc); //$NON-NLS-1$
        } catch (final SQLException exc) {
            LOG.error("Error while searching for bibliography items.", exc); //$NON-NLS-1$
        }
        return false;
    }

    /** Display form to create bibliographical entry.
     *
     * @param inTitle
     * @param inAuthor
     * @return boolean <code>true</code> if the input has been processed successfully, <code>false</code> else. */
    public boolean createNew(final Object inTitle, final Object inAuthor) {
        final ParameterObject lParameters = new ParameterObject();
        lParameters.set(KEY_PARAMETER_TITLE,
                inTitle == null ? "" : inTitle.toString()); //$NON-NLS-1$
        lParameters.set(KEY_PARAMETER_AUTHOR,
                inAuthor == null ? "" : inAuthor.toString()); //$NON-NLS-1$
        setParameters(lParameters);
        sendEvent(BibliographyNewTask.class);
        return true;
    }

    /** Returns the data container containing the text entries according to the input in the combo boxes.
     *
     * @return {@link ContributionContainer} */
    public ContributionContainer getTexts() {
        return texts;
    }

    /** Adds the selected bibliography items. */
    public void addBibliography() {
        final TextQuestionHome lHome = BOMHelper.getTextQuestionHome();
        int lCount = 0;
        try {
            for (final ContributionWrapper lText : texts.getItemIds()) {
                if (!lText.isChecked()) {
                    continue;
                }
                lCount++;
                lHome.createEntry(TextImpl.splitTextID(lText.getID()),
                        getQuestionID().toString());
            }
        } catch (final VException exc) {
            LOG.error("Error while linking the bibliography item to the question.", exc); //$NON-NLS-1$
        } catch (final SQLException exc) {
            LOG.error("Error while linking the bibliography item to the question.", exc); //$NON-NLS-1$
        }

        final IMessages lMessages = Activator.getMessages();
        if (lCount == 0) {
            showNotification(
                    lMessages.getMessage("errmsg.save.general"), Notification.Type.ERROR_MESSAGE); //$NON-NLS-1$
            return;
        }
        showNotification(lMessages
                .getMessage(lCount == 1 ? "msg.bibliography.addedS" : "msg.bibliography.addedM")); //$NON-NLS-1$ //$NON-NLS-2$
        sendEvent(AdminQuestionShowTask.class);
    }

    /** Method called when user clicks the table of the search results => show bibliography entry in popup window.
     *
     * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent) */
    @Override
    @SuppressWarnings("rawtypes")
    public void valueChange(final ValueChangeEvent inEvent) {
        final Property lProperty = inEvent.getProperty();
        if (lProperty instanceof Table) {
            final Object lValue = ((Table) lProperty).getValue();
            if (lValue instanceof ContributionWrapper) {
                final Long lID = TextImpl
                        .splitTextID(((ContributionWrapper) lValue).getID());
                requestLookup(LookupType.BIBLIOGRAPHY, lID);
            }
        }
    }

    /** Displays the selected bibliography item in edit view.<br/>
     * If more the one entries are selected, the first of the is displayed in edit view. */
    public void editBibliography() {
        for (final ContributionWrapper lText : texts.getItemIds()) {
            if (!lText.isChecked()) {
                continue;
            }
            setTextID(lText.getID());
            sendEvent(BibliographyEditPublishedTask.class);
            return;
        }
    }

    /** Tries to delete the selected bibliography entries.<br />
     * The application only deletes entries if they're not references. Therefore, the actor might have to remove
     * references first for that entry deletion can be processed.
     *
     * @return boolean <code>true</code> if successful */
    public boolean deleteBibliography() {
        try {
            final Collection<Long> lIDs = new Vector<Long>();
            for (final ContributionWrapper lText : texts.getItemIds()) {
                if (lText.isChecked()) {
                    lIDs.add(BiblioDeleteHelper.getTextID(lText.getID()));
                }
            }
            final ParameterObject lParameter = new ParameterObject();
            lParameter.set(Constants.KEY_PARAMETER_DELETE_TEXT,
                    new BiblioDeleteHelper(lIDs, getActor().getActorID()));
            setParameters(lParameter);
            sendEvent(BibliographyDeleteTask.class);
            return true;
        } catch (final VException exc) {
            LOG.error("Error encountered while deleting the text entry!", exc); //$NON-NLS-1$
        } catch (final SQLException exc) {
            LOG.error("Error encountered while deleting the text entry!", exc); //$NON-NLS-1$
        }
        return false;
    }

}

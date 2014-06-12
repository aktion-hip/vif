/*
 This package is part of the application VIF.
 Copyright (C) 2008, Benno Luthiger

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
package org.hip.vif.forum.groups.tasks;

import java.io.File;
import java.sql.SQLException;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.DownloadText;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.DownloadText.IDownloadTextValues;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.core.exc.ProhibitedFileException;
import org.hip.vif.core.util.BibliographyHelper;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.Constants;
import org.hip.vif.forum.groups.ui.BibliographyView;
import org.hip.vif.web.interfaces.IBibliographyTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window.Notification;

/**
 * Functionality for bibliography tasks.
 *
 * @author Luthiger
 * Created: 05.07.2010
 */
public abstract class AbstractBibliographyTask extends AbstractGroupsTask implements IBibliographyTask {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractBibliographyTask.class);
	
	protected static final String KEY_PARAMETER_TITLE = "biblio_title"; //$NON-NLS-1$
	protected static final String KEY_PARAMETER_AUTHOR = "biblio_author"; //$NON-NLS-1$
	
	protected static final String[] NODE_IDS = {"biblioTitle", "biblioAuthor"}; //$NON-NLS-1$ //$NON-NLS-2$
	
	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_EDIT_BIBLIOGRAPHY;
	}
	
	/**
	 * Creates view to display the bibliography's edit form.
	 * 
	 * @param inText Text
	 * @param inTextID String 
	 * @param inTextVersion int
	 * @param inCreateVersion boolean <code>true</code> if the form should create a new (private) version of the published entry, <code>false</code> in case of editing an unpublished version.
	 * @throws Exception
	 */
	protected Component editBibliography(Text inText, Long inTextID, int inTextVersion, boolean inCreateVersion) throws Exception {
		CodeList lCodeList = CodeListHome.instance().getCodeList(QuestionState.class, getAppLocale().getLanguage());
		return new BibliographyView(inText, getDownloads(inTextID),
				getAuthors(inTextID, inTextVersion),
				getReviewers(inTextID, inTextVersion),
				lCodeList, inCreateVersion, this);
	}
	
	protected QueryResult getAuthors(final Long inTextID, final int inTextVersion) throws VException, SQLException {
		return BOMHelper.getJoinTextToAuthorReviewerHome().getAuthors(inTextID, inTextVersion);
	}
	
	protected QueryResult getReviewers(final Long inTextID, final int inTextVersion) throws VException, SQLException {
		return BOMHelper.getJoinTextToAuthorReviewerHome().getReviewers(inTextID, inTextVersion);
	}

	/**
	 * Saves the uploaded file to the system.
	 * 
	 * @param inTempUpload File the temporary upload
	 * @param inFileName String
	 * @param inMimeType String
	 * @param inDeleteDownloads boolean <code>true</code> if the existing download files should be deleted first
	 * @return Long the download entry's ID or 0, if an error happened
	 * @throws ProhibitedFileException
	 */
	public Long saveFileUpload(File inTempUpload, String inFileName, String inMimeType, boolean inDeleteDownloads) throws ProhibitedFileException {
		try {
			IDownloadTextValues lDownloadValues = new BibliographyHelper.DownloadTextValues(inTempUpload, inFileName, inMimeType, getTextID(), getActor().getActorID());
			lDownloadValues.checkType();
			if (inDeleteDownloads && !deleteDownloads()) {
				return 0l;
			}
			DownloadText lUpload = (DownloadText) BOMHelper.getDownloadTextHome().create();
			Long outID = lUpload.ucNew(lDownloadValues);
			inTempUpload.renameTo(BibliographyHelper.createUploadFile(lDownloadValues.getUUID(), lDownloadValues.getDoctype()));
			return outID;
		} catch (BOMException exc) {
			LOG.error("Error while uploading the file.", exc); //$NON-NLS-1$
		} catch (VException exc) {
			LOG.error("Error while uploading the file.", exc); //$NON-NLS-1$
		} catch (SQLException exc) {
			LOG.error("Error while uploading the file.", exc); //$NON-NLS-1$
		}
		return 0l;
	}
	
	/**
	 * Deletes the existing download file(s). 
	 * 
	 * @return boolean <code>true</code> if the files could be deleted successfully
	 * @throws VException
	 * @throws SQLException
	 */
	public boolean deleteDownloads() {
		try {
			QueryResult lDownloads = getDownloads(getTextID());
			while (lDownloads.hasMoreElements()) {
				DownloadText lDownload = (DownloadText) lDownloads.next();
				BibliographyHelper.deleteUpload(lDownload);
				lDownload.delete(true);
			}
			return true;
		} catch (BOMException exc) {
			LOG.error("Error while deleting the download file.", exc); //$NON-NLS-1$
		} catch (VException exc) {
			LOG.error("Error while deleting the download file.", exc); //$NON-NLS-1$
		} catch (SQLException exc) {
			LOG.error("Error while deleting the download file.", exc); //$NON-NLS-1$
		}
		return false;
	}

	/**
	 * Persist the changes made to the text.
	 * 
	 * @param inText {@link Text} the actual text model containing the changes
	 * @param inCreateVersion boolean <code>true</code> if the changes should be saved to a new version, <code>false</code> if the actual version should be updated
	 * @return boolean <code>true</code> if the changes could be successfully persisted
	 */
	public boolean saveText(Text inText, boolean inCreateVersion) {
		try {
			//clean up rich text area input
			inText.set(TextHome.KEY_REMARK, cleanUp(inText.get(TextHome.KEY_REMARK).toString()));
			if (inCreateVersion) {
				if (inText.get(TextHome.KEY_ID) == null) {
					//create bibliography entry
					Long lTextID = inText.ucNew(getActor().getActorID());
					//link bibliography entry
					BOMHelper.getTextQuestionHome().createEntry(lTextID, getQuestionID().toString());
				}
				else {
					inText.createNewVersion(getActor().getActorID());
				}
			}
			else {
				inText.ucSave(getActor().getActorID());
			}
			showNotification(Activator.getMessages().getMessage("msg.task.data.changed"), Notification.TYPE_TRAY_NOTIFICATION); //$NON-NLS-1$
			sendEvent(ContributionsListTask.class);
			return true;
		} catch (VException exc) {
			LOG.error("Error while saving the bibliography.", exc); //$NON-NLS-1$
		} catch (SQLException exc) {
			LOG.error("Error while saving the bibliography.", exc); //$NON-NLS-1$
		}
		return false;
	}
	
}
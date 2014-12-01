/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

package org.hip.vif.web.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.bom.DownloadTextHome;
import org.hip.vif.core.exc.ProhibitedFileException;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.web.Activator;
import org.hip.vif.web.Constants;
import org.hip.vif.web.interfaces.IBibliographyTask;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.Dialog;
import org.ripla.web.util.Dialog.AbstractDialogWindow;
import org.ripla.web.util.Dialog.DialogWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamVariable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;

/** Displays file upload button and handles file upload. When the user selects a file to upload and a download file
 * exists already, a popup window is displayed to ask whether the old file has to be replaced by the new one. If no, the
 * newly upload file is discarded.
 *
 * @author Luthiger Created: 17.08.2011 */
@SuppressWarnings("serial")
public class UploadComponent extends CustomComponent {
    private static final Logger LOG = LoggerFactory
            .getLogger(UploadComponent.class);

    private static final int MEGA = 1048576;

    private final HorizontalLayout layout;
    private final Component dropBox;
    private final Upload upload;
    private final Button downloadDelete;

    private final AbstractDialogWindow dialog;
    private File tempUpload;

    private boolean hasDownloads;
    private boolean uploadFinished;
    private FileInfo fileInfo;

    /** Constructor
     *
     * @param inDownloads {@link QueryResult} the existing download files to display
     * @param inTask {@link IBibliographyTask}
     * @throws VException
     * @throws SQLException */
    public UploadComponent(final QueryResult inDownloads,
            final IBibliographyTask inTask) throws VException, SQLException {
        hasDownloads = false;
        dialog = createDialog(inTask);

        layout = createLayout();
        setCompositionRoot(layout);
        layout.setStyleName("vif-upload"); //$NON-NLS-1$

        dropBox = createDropBox(inTask);
        layout.addComponent(dropBox);
        upload = createUpload(inTask);
        layout.addComponent(upload);
        Component lLast = upload;

        downloadDelete = createDeleteFileButton(inTask);
        if (inDownloads.hasMoreElements()) {
            // add button to delete file and download link to download layout
            hasDownloads = true;
            final GeneralDomainObject lDownload = inDownloads.next();
            final DownloadFileLink lDownloadLink = new DownloadFileLink(
                    BeanWrapperHelper.getString(DownloadTextHome.KEY_LABEL,
                            lDownload), BeanWrapperHelper.getLong(
                                    DownloadTextHome.KEY_ID, lDownload));
            layout.addComponent(downloadDelete);
            layout.addComponent(lDownloadLink);
            lLast = lDownloadLink;
        }
        lLast.setWidth("100%"); //$NON-NLS-1$
        layout.setExpandRatio(lLast, 1);
        layout.setComponentAlignment(lLast, Alignment.MIDDLE_LEFT);
    }

    private Button createDeleteFileButton(final IBibliographyTask inTask) {
        final IMessages lMessages = Activator.getMessages();
        final Button outDelete = new Button(
                lMessages.getMessage("ui.button.delete.file")); //$NON-NLS-1$
        outDelete.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                if (inTask.deleteDownloads()) {
                    // update view component
                    layout.removeAllComponents();
                    addComponents(dropBox, upload);
                    hasDownloads = false;
                } else {
                    Notification.show(
                            lMessages.getMessage("errmsg.save.general"), Notification.Type.WARNING_MESSAGE);//$NON-NLS-1$
                }
            }
        });
        return outDelete;
    }

    private HorizontalLayout createLayout() {
        final HorizontalLayout outLayout = new HorizontalLayout();
        outLayout.setSpacing(true);
        outLayout.setWidth("100%"); //$NON-NLS-1$
        return outLayout;
    }

    private void addComponents(final Component... inComponents) {
        for (final Component lComponent : inComponents) {
            layout.addComponent(lComponent);
        }
        final Component lLast = inComponents[inComponents.length - 1];
        lLast.setWidth("100%"); //$NON-NLS-1$
        layout.setExpandRatio(lLast, 1);
        layout.setComponentAlignment(lLast, Alignment.MIDDLE_LEFT);
    }

    private AbstractDialogWindow createDialog(final IBibliographyTask inTask) {
        final IMessages lMessages = Activator.getMessages();

        final Dialog.DialogWindow outDialog = (DialogWindow) Dialog
                .openQuestion(
                        lMessages.getMessage("ui.upload.dialog.title"), lMessages.getMessage("ui.upload.dialog.question")); //$NON-NLS-1$ //$NON-NLS-2$
        outDialog.addYesListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                dialog.setVisible(false);
                checkUploadState();
                if (tempUpload != null) {
                    handleUpload(inTask, true);
                }
            }
        });
        outDialog.addNoListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                dialog.setVisible(false);
                checkUploadState();
                handleDeleteTemp();
            }
        });
        return outDialog;
    }

    private OutputStream createStream(final String inFilename) {
        if (inFilename.length() != 0) {
            try {
                tempUpload = File.createTempFile(Constants.TMP_UPLOAD_PREFIX,
                        Constants.TMP_UPLOAD_SUFFIX);
                return new FileOutputStream(tempUpload);
            } catch (final IOException exc) {
                LOG.error("Could not upload file '{}'.", inFilename, exc); //$NON-NLS-1$
            }
        }
        return new NoOpStream();
    }

    private Component createDropBox(final IBibliographyTask inTask) {
        final CssLayout lDropPane = new CssLayout();
        lDropPane.setWidth("150px"); //$NON-NLS-1$
        lDropPane.setHeight("30px"); //$NON-NLS-1$
        lDropPane.addStyleName("v-textfield"); //$NON-NLS-1$
        final Label lHint = new Label(Activator.getMessages().getMessage("ui.upload.drop.box")); //$NON-NLS-1$
        lHint.setStyleName("vif-drop-hint"); //$NON-NLS-1$
        lDropPane.addComponent(lHint);

        final DragAndDropWrapper outDrop = new DragAndDropWrapper(lDropPane);
        outDrop.setDropHandler(new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(final DragAndDropEvent inEvent) {
                final Transferable lTransferable = inEvent.getTransferable();
                if (lTransferable instanceof WrapperTransferable) {
                    final Html5File[] lFiles = ((WrapperTransferable) lTransferable)
                            .getFiles();
                    for (final Html5File lFile : lFiles) {
                        lFile.setStreamVariable(createStreamVariable(
                                lFile.getFileName(), inTask));
                    }
                }
            }
        });

        outDrop.setSizeUndefined();
        outDrop.setImmediate(true);
        return outDrop;
    }

    private Upload createUpload(final IBibliographyTask inTask) {
        final Upload outUpload = new Upload();
        outUpload.setWidth(SIZE_UNDEFINED, Sizeable.Unit.POINTS);
        outUpload.setReceiver(new Upload.Receiver() {
            @Override
            public OutputStream receiveUpload(final String inFilename,
                    final String inMimeType) {
                return createStream(inFilename);
            }
        });

        final String lCaption = Activator.getMessages().getMessage("ui.upload.button.lbl"); //$NON-NLS-1$
        outUpload.setButtonCaption(lCaption);
        outUpload.setImmediate(true);
        outUpload.setStyleName("vif-upload"); //$NON-NLS-1$

        outUpload.addStartedListener(new Upload.StartedListener() {
            @Override
            public void uploadStarted(final StartedEvent inEvent) {
                fileInfo = new FileInfo(inEvent.getFilename(), inEvent
                        .getMIMEType());
                tempUpload = null;
                uploadFinished = false;
                outUpload.setVisible(false);
                if (hasDownloads) {
                    dialog.setVisible(true); // FF
                }
            }
        });
        outUpload.addFinishedListener(new Upload.FinishedListener() {
            @Override
            public void uploadFinished(final FinishedEvent inEvent) {
                uploadFinished = true;
                outUpload.setVisible(true);
            }
        });
        outUpload.addSucceededListener(new Upload.SucceededListener() {
            @Override
            public void uploadSucceeded(final SucceededEvent inEvent) {
                if (!hasDownloads) {
                    handleUpload(inTask, false);
                }
            }
        });
        outUpload.addFailedListener(new Upload.FailedListener() {
            @Override
            public void uploadFailed(final FailedEvent inEvent) {
                handleDeleteTemp();
            }
        });

        return outUpload;
    }

    private StreamVariable createStreamVariable(final String inFileName,
            final IBibliographyTask inTask) {
        return new StreamVariable() {
            @Override
            public OutputStream getOutputStream() {
                return createStream(inFileName);
            }

            @Override
            public void streamingStarted(final StreamingStartEvent inEvent) {
                fileInfo = new FileInfo(inEvent.getFileName(),
                        inEvent.getMimeType());
                tempUpload = null;
                uploadFinished = false;
                upload.setVisible(false);
                if (hasDownloads) {
                    dialog.setVisible(true); // Google
                }
            }

            @Override
            public void streamingFinished(final StreamingEndEvent inEvent) {
                uploadFinished = true;
                upload.setVisible(true);
                if (!hasDownloads) {
                    handleUpload(inTask, false);
                }
            }

            @Override
            public void streamingFailed(final StreamingErrorEvent inEvent) {
                handleDeleteTemp();
            }

            @Override
            public void onProgress(final StreamingProgressEvent inEvent) {
            }

            @Override
            public boolean listenProgress() {
                return false;
            }

            @Override
            public boolean isInterrupted() {
                return false;
            }
        };
    }

    private void handleDeleteTemp() {
        if (tempUpload != null) {
            if (!tempUpload.delete()) {
                tempUpload.deleteOnExit();
            }
        }
    }

    private void handleUpload(final IBibliographyTask inTask,
            final boolean inDeleteDownloads) {
        final IMessages lMessages = Activator.getMessages();
        // check upload file size
        if (tempUpload.length() > getMaxSize() * MEGA) {
            handleDeleteTemp();
            Notification
            .show(lMessages.getFormattedMessage("errmsg.upload.exceeded", getMaxSize()), Notification.Type.WARNING_MESSAGE);//$NON-NLS-1$
            return;
        }
        try {
            final Long lDownloadID = inTask.saveFileUpload(tempUpload,
                    fileInfo.fileName, fileInfo.mimeType, inDeleteDownloads);
            if (lDownloadID == 0) {
                // show error notification
                Notification
                .show(lMessages.getMessage("errmsg.save.general"), Notification.Type.ERROR_MESSAGE);//$NON-NLS-1$
            } else {
                // update download layout
                hasDownloads = true;
                layout.removeAllComponents();
                addComponents(dropBox, upload, downloadDelete,
                        new DownloadFileLink(fileInfo.fileName, lDownloadID));
            }
        } catch (final ProhibitedFileException exc) {
            Notification
            .show(lMessages.getMessage("errmsg.upload.prohibited"), Notification.Type.ERROR_MESSAGE);//$NON-NLS-1$
        }
    }

    private void checkUploadState() {
        while (!uploadFinished) {
            try {
                Thread.sleep(100);
            } catch (final InterruptedException exc) {
                // intentionally left empty
            }
        }
    }

    private long getMaxSize() {
        try {
            return Long.parseLong(PreferencesHandler.INSTANCE
                    .get(PreferencesHandler.KEY_UPLOAD_QUOTA));
        } catch (final NumberFormatException exc) {
            LOG.error("Error encountered while retrieving the upload quoata preference!", exc); //$NON-NLS-1$
        } catch (final IOException exc) {
            LOG.error("Error encountered while retrieving the upload quoata preference!", exc); //$NON-NLS-1$
        }
        return ApplicationConstants.DFLT_UPLOADE_QUOTA;
    }

    @Override
    public void attach() {
        super.attach();
        if (hasDownloads) {
            dialog.setVisible(false);
            // if (dialog.isDisplayable()) {
            // upload.getWindow().addWindow(dialog.getWindow());
            // }
            dialog.center();
        }
    }

    @Override
    public void detach() {
        super.detach();
        dialog.close();
    }

    // ---

    private static class NoOpStream extends OutputStream {
        @Override
        public void write(final int inByte) throws IOException {
            // No operation
        }
    }

    /** Parameter object, e.g. "2011-06-26_11-04-43_20.jpg" :: "image/jpeg"
     *
     * @author Luthiger Created: 22.08.2011 */
    private static class FileInfo {
        String fileName;
        String mimeType;

        FileInfo(final String inFileName, final String inMimeType) {
            fileName = inFileName;
            mimeType = inMimeType;
        }
    }

}

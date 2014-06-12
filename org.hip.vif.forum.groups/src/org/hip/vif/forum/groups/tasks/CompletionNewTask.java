package org.hip.vif.forum.groups.tasks;

import java.sql.SQLException;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Window.Notification;

/**
 * Task to create a new completion entry.
 * 
 * Created on 11.08.2003
 * @author Luthiger
 */
@Partlet
public class CompletionNewTask extends AbstractCompletionTask {
	private static final Logger LOG = LoggerFactory.getLogger(CompletionNewTask.class);

	/**
	 * @see org.hip.vif.servlets.AbstractVIFTask#needsPermission()
	 */
	protected String needsPermission() {
		return Constants.PERMISSION_NEW_COMPLETION;
	}
	
	@Override
	protected Long getCompletionsQuestionID() throws VException, SQLException {
		return getQuestionID();
	}
	
	@Override
	protected String getCompletionText() throws VException, SQLException {
		return ""; //$NON-NLS-1$
	}
	
	@Override
	protected Long getActCompletionID() throws VException, SQLException {
		return 0l;
	}
	
	/**
	 * Callback function for view.
	 * 
	 * @param inCompletion String the inputed completion text.
	 * @return boolean <code>true</code> if the completion has been saved successfully
	 */
	public boolean saveCompletion(String inCompletion) {
		try {
			Completion lCompletionBOM = (Completion) BOMHelper.getCompletionHome().create();
			lCompletionBOM.ucNew(cleanUp(inCompletion), getQuestionID().toString(), getActor().getActorID());
			showNotification(Activator.getMessages().getMessage("msg.completion.create"), Notification.TYPE_TRAY_NOTIFICATION); //$NON-NLS-1$
			sendEvent(ContributionsListTask.class);
			return true;
		} 
		catch (BOMException exc) {
			LOG.error("Error while saving the completion.", exc); //$NON-NLS-1$
		}
		return false;
	}
 	
}

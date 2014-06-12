package org.hip.vif.forum.groups.tasks;

import java.sql.SQLException;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.QuestionHierarchyHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.forum.groups.Constants;
import org.hip.vif.forum.groups.data.GroupContentContainer;
import org.hip.vif.forum.groups.data.GroupContentWrapper;
import org.hip.vif.forum.groups.ui.GroupContentView;
import org.hip.vif.web.tasks.AbstractVIFTask;

import com.vaadin.ui.Component;

/**
 * Task to display the contribution threads of a discussion group.
 * 
 * Created on 10.08.2002
 * @author Benno Luthiger
 */
@Partlet
public class GroupShowTask extends AbstractVIFTask {
	private final static String SORT_ORDER = QuestionHome.KEY_QUESTION_DECIMAL;

	/**
	 * @see org.hip.vif.servlets.AbstractVIFTask#needsPermission()
	 */
	protected String needsPermission() {
		return ""; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#runChecked()
	 */
	public Component runChecked() throws VException {
		try {
			loadContextMenu(Constants.MENU_SET_ID_GROUP_REVIEW);
			
			Long lGroupID = getGroupID();
			QuestionHome lQuestionHome = BOMHelper.getQuestionHome();
			QuestionHierarchyHome lHierarchyHome = BOMHelper.getQuestionHierarchyHome();

			Group lGroup = null;
			QueryResult lResult = null;
			if (getActor().isGuest()) {
				lGroup = BOMHelper.getGroupHome().getGroup(lGroupID);
				lResult = BOMHelper.getQuestionForGuestsHome().selectOfGroup(lGroupID, lGroup.getGuestDepth(), createOrder(SORT_ORDER, false));
			}
			else {
				lGroup = BOMHelper.getGroupHome().getGroup(lGroupID);
				lResult = lQuestionHome.selectOfGroupPublished(lGroupID, createOrder(SORT_ORDER, false));
			}
			CodeList lCodeList = CodeListHome.instance().getCodeList(QuestionState.class, getAppLocale().getLanguage());
			return new GroupContentView(lGroup, GroupContentContainer.createData(lResult, lHierarchyHome.getChildrenChecker(lGroupID), lCodeList, 2), this);
		}
		catch (SQLException exc) {
			throw createContactAdminException(exc);
		}
	}
	
	/**
	 * Callback method for components managed by this task instance.
	 * 
	 * @param inSelection {@link GroupContentWrapper} the selected item of the group's content
	 */
	public void processSelection(GroupContentWrapper inSelection) {
		setQuestionID(inSelection.getQuestionID());
		sendEvent(QuestionShowTask.class);
	}
	
}

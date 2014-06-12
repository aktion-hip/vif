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

package org.hip.vif.forum.groups.ui;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.interfaces.IPluggableTask;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.data.CompletionsHelper.Completion;
import org.hip.vif.forum.groups.data.QuestionContainer;
import org.hip.vif.forum.groups.data.QuestionWrapper;
import org.hip.vif.forum.groups.tasks.QuestionShowTask;
import org.hip.vif.forum.groups.util.SwitchHelper;
import org.hip.vif.web.util.VIFViewHelper;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * The view to display a question.
 * 
 * @author Luthiger
 * Created: 01.06.2011
 */
@SuppressWarnings("serial")
public class QuestionView extends AbstractQuestionView {

	private Collection<Table> tables = new Vector<Table>();
	private Button subscribe;
	private Button bookmark;
	
	/**
	 * Constructor for normal view.
	 * 
	 * @param inGroup {@link Group} the discussion group the question belongs to
	 * @param inQuestion {@link Question} the question to display its content
	 * @param inCodeList {@link CodeList} the list of codes for the question state
	 * @param inParent {@link QuestionContainer} the question's parent
	 * @param inChildren {@link QuestionContainer} the question's follow up questions
	 * @param inAuthors {@link QueryResult}
	 * @param inReviewers {@link QueryResult}
	 * @param inCompletions List<Completion>
	 * @param inBibliography {@link QueryResult}
	 * @param inSwitchHelper {@link SwitchHelper}
	 * @param isGuest boolean if the user is guest, we provide less functionality (i.e. less information about authors/reviewers)
	 * @param inTask {@link QuestionShowTask} this view's controller
	 * @throws VException
	 * @throws SQLException 
	 */
	public QuestionView(Group inGroup, Question inQuestion, CodeList inCodeList, QuestionContainer inParent, QuestionContainer inChildren, QueryResult inAuthors, QueryResult inReviewers, 
			List<Completion> inCompletions, QueryResult inBibliography, SwitchHelper inSwitchHelper, boolean isGuest, QuestionShowTask inTask) throws VException, SQLException {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		
		IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-view"); //$NON-NLS-1$
		String lTitle = String.format(lMessages.getMessage("ui.question.view.title.page"), inGroup.get(GroupHome.KEY_NAME).toString()); //$NON-NLS-1$
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-pagetitle", lTitle), Label.CONTENT_XHTML)); //$NON-NLS-1$

		if (!isGuest) {			
			createSubscribeBookmarkButtons(inQuestion, inSwitchHelper, lMessages, lLayout, inTask);
		}
		
		//question
		String lLabel = String.format(lMessages.getMessage("ui.question.view.title.question"), BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION_DECIMAL, inQuestion)); //$NON-NLS-1$
		lLayout.addComponent(createQuestion(inQuestion, lLabel, inCodeList, inAuthors, inReviewers, lMessages, isGuest, inTask));
		
		//completions
		for (Completion lCompletion : inCompletions) {			
			lLayout.addComponent(createCompletion(lCompletion, inCodeList, lMessages, isGuest, inTask));
		}
		
		//texts
		createBibliography(lLayout, inBibliography, lMessages, inTask);
		
		lLayout.addComponent(VIFViewHelper.createSpacer());
		
		//parent question
		if (!inQuestion.isRoot()) {
			lLayout.addComponent(createParent(inParent, inTask, lMessages));
		}
		//follow up questions
		if (!inChildren.isEmpty()) {			
			lLayout.addComponent(createChildren(inChildren, inTask, lMessages));
		}
	}
	
	/**
	 * Constructor for view of question in lookup window.
	 *  
	 * @param inQuestion {@link Question} the question to display its content
	 * @param inCodeList {@link CodeList} the list of codes for the question state
	 * @param inAuthors
	 * @param inReviewers
	 * @param inCompletions
	 * @param inBibliography
	 * @throws VException
	 * @throws SQLException
	 */
	public QuestionView(Question inQuestion, CodeList inCodeList, QueryResult inAuthors, QueryResult inReviewers, List<Completion> inCompletions, QueryResult inBibliography) throws VException, SQLException {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		
		IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-view"); //$NON-NLS-1$

		//question
		String lLabel = String.format(lMessages.getMessage("ui.question.view.title.question"), BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION_DECIMAL, inQuestion)); //$NON-NLS-1$
		lLayout.addComponent(createQuestion(inQuestion, lLabel, inCodeList, inAuthors, inReviewers, lMessages));
		
		//completions
		for (Completion lCompletion : inCompletions) {			
			lLayout.addComponent(createCompletionPlain(lCompletion, inCodeList, lMessages));
		}
		
		//texts
		createBibliography(lLayout, inBibliography, lMessages);
	}

	private Component createParent(QuestionContainer inParent, IPluggableTask inTask, IMessages inMessages) {
		return createTable(inMessages.getMessage("ui.question.view.question.parent"), inParent, inTask); //$NON-NLS-1$
	}
	
	private Component createChildren(QuestionContainer inChildren, IPluggableTask inTask, IMessages inMessages) throws VException, SQLException {
		return createTable(inMessages.getMessage("ui.question.view.question.follow.up"), inChildren, inTask); //$NON-NLS-1$
	}
	
	private VerticalLayout createTable(String inCaption, Container inDataSource, IPluggableTask inTask) {
		VerticalLayout out = new VerticalLayout();
		out.setStyleName("vif-question-table"); //$NON-NLS-1$
		
		out.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-caption", inCaption), Label.CONTENT_XHTML)); //$NON-NLS-1$
		
		Table lTable = new Table();
		lTable.setStyleName("vif-table"); //$NON-NLS-1$
		lTable.setWidth("100%"); //$NON-NLS-1$
		lTable.setColumnCollapsingAllowed(true);
		lTable.setColumnReorderingAllowed(true);
		lTable.setSelectable(true);
		lTable.setImmediate(true);
		lTable.setPageLength(0);
		
		lTable.setContainerDataSource(inDataSource);
		lTable.setVisibleColumns(QuestionContainer.NATURAL_COL_ORDER);
		lTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(QuestionContainer.COL_HEADERS, Activator.getMessages()));
		lTable.addGeneratedColumn(QuestionContainer.QUESTION_ID, new Table.ColumnGenerator() {
			public Component generateCell(Table inSource, Object inItemId, Object inColumnId) {
				return new Label(((QuestionWrapper)inItemId).getQuestion(), Label.CONTENT_XHTML);
			}
		});
		lTable.setColumnExpandRatio(QuestionContainer.QUESTION_ID, 1);
		
		lTable.addListener((ValueChangeListener) inTask);
		tables.add(lTable);

		out.addComponent(lTable);
		return out;
	}

	/**
	 * Buttons to subscribe and bookmark.
	 * 
	 * @param inQuestion 
	 * @param inSwitchHelper 
	 * @param inMessages
	 * @param inLayout
	 * @param inTask
	 */
	private void createSubscribeBookmarkButtons(Question inQuestion, final SwitchHelper inSwitchHelper, final IMessages inMessages, VerticalLayout inLayout, final QuestionShowTask inTask) {
		HorizontalLayout lButtons = new HorizontalLayout();
		lButtons.setSpacing(true);
		lButtons.setWidth("100%"); //$NON-NLS-1$
	
		subscribe = new Button(inSwitchHelper.getSubscriptionCaption());
		final SubscribeDialog lSubscribeDialog = new SubscribeDialog(inMessages);
		lSubscribeDialog.setStoreClickListener(new ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				handleSwitchAction(inSwitchHelper.createSubscription(lSubscribeDialog.getCheckBoxValue()), subscribe, 
						inSwitchHelper.getSubscriptionCaption(), 
						inMessages.getMessage("ui.question.view.subscription.msg1"),  //$NON-NLS-1$
						inMessages.getMessage("ui.question.view.subscription.msg2")); //$NON-NLS-1$
				lSubscribeDialog.close();
			}
		});
		subscribe.addListener(new ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				if (inSwitchHelper.hasSubscription()) {
					handleSwitchAction(inSwitchHelper.deleteSubscription(), subscribe, 
							inSwitchHelper.getSubscriptionCaption(), 
							inMessages.getMessage("ui.question.view.subscription.msg3"),  //$NON-NLS-1$
							inMessages.getMessage("ui.question.view.subscription.msg4")); //$NON-NLS-1$
				}
				else {					
					showDialog(lSubscribeDialog);
				}
			}
		});
		lButtons.addComponent(subscribe);
		lButtons.setExpandRatio(subscribe, 1);
		lButtons.setComponentAlignment(subscribe, Alignment.MIDDLE_RIGHT);

		bookmark = new Button(inSwitchHelper.getBookmarkCaption());
		final BookmarkDialog lBookmarkDialog = new BookmarkDialog(inMessages,
				String.format("%s - %s",  //$NON-NLS-1$
				BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION_DECIMAL, inQuestion),
				BeanWrapperHelper.getPlain(QuestionHome.KEY_QUESTION, inQuestion)));
		lBookmarkDialog.setStoreClickListener(new ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				handleSwitchAction(inSwitchHelper.createBookmark(lBookmarkDialog.getFieldValue()), bookmark, 
						inSwitchHelper.getBookmarkCaption(), 
						inMessages.getMessage("ui.question.view.bookmark.msg1"),  //$NON-NLS-1$
						inMessages.getMessage("ui.question.view.bookmark.msg2")); //$NON-NLS-1$
				lBookmarkDialog.close();
			}
		});
		bookmark.addListener(new ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				if (inSwitchHelper.hasBookmark()) {					
					handleSwitchAction(inSwitchHelper.deleteBookmark(), bookmark, 
							inSwitchHelper.getBookmarkCaption(), 
							inMessages.getMessage("ui.question.view.bookmark.msg3"),  //$NON-NLS-1$
							inMessages.getMessage("ui.question.view.bookmark.msg4")); //$NON-NLS-1$
				}
				else {
					showDialog(lBookmarkDialog);
				}
			}
		});
		
		lButtons.addComponent(bookmark);
		inLayout.addComponent(lButtons);
	}
	
	private void handleSwitchAction(boolean inOutcome, Button inButton, String inCaption, String inMsgSuccess, String inMsgFailure) {
		if (inOutcome) {
			inButton.setCaption(inCaption);
			getWindow().showNotification(inMsgSuccess, Notification.TYPE_TRAY_NOTIFICATION);
		}
		else {
			getWindow().showNotification(inMsgFailure, Notification.TYPE_WARNING_MESSAGE);
		}
	}

	protected void showDialog(Dialog inDialog) {
		if (inDialog.isDisplayable()) {
			getWindow().addWindow(inDialog.getWindow());
		}
	}

	/**
	 * Checks whether this component is the origin of a value change event.
	 * 
	 * @param inProperty {@link Property}
	 * @return boolean <code>true</code> if the value change event originated on this component
	 */
	public boolean checkSelectionSource(Property inProperty) {
		return tables.contains(inProperty);
	}
	
// --- private classes ---
	
	private static abstract class Dialog {
		private Window dialog;
		protected Button save;
		protected Button cancel;

		Dialog(IMessages inMessages, String inMsgKey) {
			dialog = createDialog(inMessages.getMessage(inMsgKey));
			save = new Button(inMessages.getMessage("ui.button.save")); //$NON-NLS-1$
			save.setClickShortcut(KeyCode.ENTER);
			
			cancel = new Button(inMessages.getMessage("ui.button.cancel")); //$NON-NLS-1$
			cancel.setClickShortcut(KeyCode.ESCAPE);
			cancel.addListener(new ClickListener() {
				public void buttonClick(ClickEvent inEvent) {
					close();
				}
			});
		}
		
		private Window createDialog(String inTitle) {
			Window outDialog = new Window(inTitle);
			VerticalLayout lLayout = (VerticalLayout) outDialog.getContent();
			lLayout.setMargin(true);
			lLayout.setSpacing(true);
			lLayout.setSizeUndefined();
			
			outDialog.setModal(true);
			outDialog.center();
			return outDialog;
		}
		
		void close() {
			dialog.getParent().removeWindow(dialog);
		}
		
		void setStoreClickListener(ClickListener inListener) {
			save.addListener(inListener);
		}
		
		boolean isDisplayable() {
			return dialog.getParent() == null;
		}
		
		Window getWindow() {
			return dialog;
		}
	}
	
	private static class BookmarkDialog extends Dialog {
		private TextField bookmarkText;

		BookmarkDialog(IMessages inMessages, String inInput) {
			super(inMessages, "ui.question.view.bookmark.title"); //$NON-NLS-1$
			VerticalLayout lLayout = (VerticalLayout) getWindow().getContent();
			
			lLayout.addComponent(new Label(inMessages.getMessage("ui.question.view.bookmark.msg"), Label.CONTENT_XHTML)); //$NON-NLS-1$
			bookmarkText = new TextField();
			bookmarkText.setValue(inInput);
			bookmarkText.setColumns(50);
			bookmarkText.setWidth("100%"); //$NON-NLS-1$
			lLayout.addComponent(bookmarkText);
			
			lLayout.addComponent(VIFViewHelper.createButtons(save, cancel));
		}
		String getFieldValue() {
			return bookmarkText.getValue().toString();
		}
	}
	
	private static class SubscribeDialog extends Dialog {
		private CheckBox checkbox;

		SubscribeDialog(IMessages inMessages) {
			super(inMessages, "ui.question.view.subscription.title"); //$NON-NLS-1$
			VerticalLayout lLayout = (VerticalLayout) getWindow().getContent();

			lLayout.addComponent(new Label(inMessages.getMessage("ui.question.view.subscription.statement"), Label.CONTENT_XHTML)); //$NON-NLS-1$
			checkbox = new CheckBox(inMessages.getMessage("ui.question.view.subscription.check")); //$NON-NLS-1$
			checkbox.setWidth("100%"); //$NON-NLS-1$
			lLayout.addComponent(checkbox);
			
			lLayout.addComponent(VIFViewHelper.createButtons(save, cancel));
		}
		boolean getCheckBoxValue() {
			return ((Boolean)checkbox.getValue()).booleanValue();
		}
	}
		
}

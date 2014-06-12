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

package org.hip.vif.admin.member.ui;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.hip.vif.admin.member.Activator;
import org.hip.vif.admin.member.tasks.AbstractMemberSearchTask;
import org.hip.vif.admin.member.tasks.MemberSearchTask;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * View to display the form to search a member entry.
 * 
 * @author Luthiger Created: 16.10.2011
 */
@SuppressWarnings("serial")
public class MemberSearchView extends CustomComponent {
	private static final int DFT_WIDTH_INPUT = 300;
	private static final int MIN_INPUT_SIZE = 2;
	private LayoutHandler layoutHandler;

	/**
	 * Constructor of search form in normal view.
	 * 
	 * @param inHelpContent
	 *            URL the url to read the content of the help window
	 * @param inTask
	 *            {@link MemberSearchTask}
	 */
	public MemberSearchView(URL inHelpContent,
			final AbstractMemberSearchTask inTask) {
		VerticalLayout lLayout = createLayout(Activator.getMessages()
				.getMessage("ui.member.search.title.page")); //$NON-NLS-1$
		layoutHandler = new LayoutHandlerWithHelp(lLayout, inTask,
				inHelpContent);
		layoutHandler.createLayout();
	}

	/**
	 * Constructor of search form in lookup window.
	 * 
	 * @param inTitle
	 *            String
	 * @param inTask
	 *            {@link AbstractMemberSearchTask}
	 */
	public MemberSearchView(String inTitle,
			final AbstractMemberSearchTask inTask) {
		VerticalLayout lLayout = createLayout(inTitle);
		layoutHandler = new LayoutHandler(lLayout, inTask);
		layoutHandler.createLayout();
	}

	private VerticalLayout createLayout(String inTitle) {
		VerticalLayout outLayout = new VerticalLayout();
		setCompositionRoot(outLayout);

		outLayout.setStyleName("vif-search"); //$NON-NLS-1$
		outLayout.addComponent(new Label(
				String.format(VIFViewHelper.TMPL_TITLE,
						"vif-pagetitle", inTitle), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$

		return outLayout;
	}

	@Override
	public void attach() {
		layoutHandler.setWindow(getWindow());
		super.attach();
	}

	// ---

	private static class FieldHandler {
		private Map<String, TextField> fields = new HashMap<String, TextField>();

		TextField createTextField(String inFieldName, int inWidth) {
			TextField out = new TextField();
			out.setWidth(inWidth, Sizeable.UNITS_PIXELS);
			out.setStyleName("vif-input"); //$NON-NLS-1$
			fields.put(inFieldName, out);
			return out;
		}

		String get(String inKey) {
			TextField lField = fields.get(inKey);
			return lField == null ? "" : lField.getValue().toString().trim(); //$NON-NLS-1$
		}

		boolean validate() {
			for (TextField lField : fields.values()) {
				if (lField.getValue().toString().trim().length() > MIN_INPUT_SIZE) {
					return true;
				}
			}
			return false;
		}
	}

	private static class LayoutHandler {
		protected IMessages messages = Activator.getMessages();
		private VerticalLayout layout;
		private Window window;
		private AbstractMemberSearchTask task;

		LayoutHandler(VerticalLayout inLayout, AbstractMemberSearchTask inTask) {
			layout = inLayout;
			task = inTask;
		}

		public void setWindow(Window inWindow) {
			window = inWindow;
		}

		void createLayout() {
			HorizontalLayout lInput = new HorizontalLayout();
			lInput.setWidth(SIZE_UNDEFINED, 0);
			lInput.setMargin(true, true, true, false);
			Label lLabel = new Label(
					String.format(
							"%s:&#160;", messages.getMessage("ui.member.search.label.quick")), Label.CONTENT_XHTML); //$NON-NLS-1$ //$NON-NLS-2$
			lInput.addComponent(RiplaViewHelper.makeUndefinedWidth(lLabel));
			lInput.setComponentAlignment(lLabel, Alignment.MIDDLE_LEFT);
			final TextField lSearch = new TextField();
			lSearch.setColumns(45);
			lSearch.focus();
			lInput.addComponent(lSearch);
			handleHelpButton(lInput);
			layout.addComponent(lInput);

			Button lQuickSearch = new Button(
					messages.getMessage("ui.member.search.button.search")); //$NON-NLS-1$
			lQuickSearch.setClickShortcut(KeyCode.ENTER);
			lQuickSearch.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent inEvent) {
					String lQuery = lSearch.getValue().toString().trim();
					if (lQuery.length() <= MIN_INPUT_SIZE) {
						window.showNotification(
								messages.getMessage("errmsg.search.no.input"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
					} else {
						if (!task.search(lQuery, window)) {
							window.showNotification(
									messages.getMessage("errmsg.search.process"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
						}
					}
				}
			});
			layout.addComponent(lQuickSearch);

			layout.addComponent(VIFViewHelper.createSpacer());
			layout.addComponent(VIFViewHelper.createSpacer());
			layout.addComponent(new Label(
					String.format(
							VIFViewHelper.TMPL_TITLE,
							"vif-title", messages.getMessage("ui.member.search.label.detailed")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$

			final FieldHandler lFields = new FieldHandler();
			LabelValueTable lTable = new LabelValueTable();
			lTable.addRow(
					messages.getMessage("ui.member.label.name"), lFields.createTextField("name", DFT_WIDTH_INPUT)); //$NON-NLS-1$ //$NON-NLS-2$
			lTable.addRow(
					messages.getMessage("ui.member.label.firstname"), lFields.createTextField("firstname", DFT_WIDTH_INPUT)); //$NON-NLS-1$ //$NON-NLS-2$
			lTable.addRow(
					messages.getMessage("ui.member.label.street"), lFields.createTextField("street", DFT_WIDTH_INPUT)); //$NON-NLS-1$ //$NON-NLS-2$
			lTable.addRow(
					messages.getMessage("ui.member.label.zip"), lFields.createTextField("zip", 40)); //$NON-NLS-1$ //$NON-NLS-2$
			lTable.addRow(
					messages.getMessage("ui.member.label.city"), lFields.createTextField("city", DFT_WIDTH_INPUT)); //$NON-NLS-1$ //$NON-NLS-2$
			lTable.addRow(
					messages.getMessage("ui.member.label.mail"), lFields.createTextField("mail", DFT_WIDTH_INPUT)); //$NON-NLS-1$ //$NON-NLS-2$
			lTable.setHeight(Sizeable.SIZE_UNDEFINED, 0);
			layout.addComponent(lTable);

			layout.addComponent(VIFViewHelper.createSpacer());
			Button lDetailSearch = new Button(
					messages.getMessage("ui.member.search.button.search")); //$NON-NLS-1$
			lDetailSearch.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent inEvent) {
					if (!lFields.validate()) {
						window.showNotification(
								messages.getMessage("errmsg.search.no.input"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
					} else {
						if (!task.search(
								lFields.get("name"), lFields.get("firstname"), lFields.get("street"), lFields.get("zip"), lFields.get("city"), lFields.get("mail"), window)) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
							window.showNotification(
									messages.getMessage("errmsg.search.process"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
						}
					}
				}
			});
			layout.addComponent(lDetailSearch);
		}

		protected void handleHelpButton(HorizontalLayout inLayout) {
			// intentionally left empty
		}
	}

	private static class LayoutHandlerWithHelp extends LayoutHandler {
		private URL helpContent;

		LayoutHandlerWithHelp(VerticalLayout inLayout,
				AbstractMemberSearchTask inTask, URL inHelpContent) {
			super(inLayout, inTask);
			helpContent = inHelpContent;
		}

		@Override
		protected void handleHelpButton(HorizontalLayout inLayout) {
			HelpButton lHelp = new HelpButton(
					messages.getMessage("ui.member.search.button.help"), helpContent, 600, 380); //$NON-NLS-1$
			inLayout.addComponent(lHelp);
			inLayout.setExpandRatio(lHelp, 1);
		}
	}

}

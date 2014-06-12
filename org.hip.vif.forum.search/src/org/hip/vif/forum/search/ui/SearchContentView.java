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

package org.hip.vif.forum.search.ui;

import java.net.URL;

import org.hip.vif.forum.search.Activator;
import org.hip.vif.forum.search.data.ContributionContainer;
import org.hip.vif.forum.search.tasks.SearchContentTask;

import com.vaadin.data.Property;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * View to display the input field to search for contributions.
 * 
 * @author Luthiger Created: 30.09.2011
 */
@SuppressWarnings("serial")
public class SearchContentView extends CustomComponent {
	private static final int PAGE_LENGTH = 15;
	private static final int MIN_INPUT = 2;

	/**
	 * View constructor.
	 * 
	 * @param inHelpContent
	 *            URL
	 * @param inTask
	 *            {@link SearchContentTask}
	 */
	public SearchContentView(URL inHelpContent, final SearchContentTask inTask) {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);

		final IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-view"); //$NON-NLS-1$
		lLayout.addComponent(new Label(
				String.format(
						VIFViewHelper.TMPL_TITLE,
						"vif-pagetitle", lMessages.getMessage("ui.search.view.title.page")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$

		HorizontalLayout lInput = new HorizontalLayout();
		lInput.setMargin(true, true, true, false);
		Label lLabel = new Label(
				String.format(
						"%s:&#160;", lMessages.getMessage("ui.search.view.label.input")), Label.CONTENT_XHTML); //$NON-NLS-1$ //$NON-NLS-2$
		lInput.addComponent(RiplaViewHelper.makeUndefinedWidth(lLabel));
		lInput.setComponentAlignment(lLabel, Alignment.MIDDLE_LEFT);

		final TextField lSearch = new TextField();
		lSearch.setColumns(50);
		lSearch.focus();
		lInput.addComponent(lSearch);
		lInput.addComponent(new HelpButton(
				lMessages.getMessage("ui.search.view.button.help"), inHelpContent, 700, 620)); //$NON-NLS-1$
		lLayout.addComponent(lInput);

		final Table lSearchResult = new Table();

		Button lButton = new Button(
				lMessages.getMessage("ui.search.view.button.search")); //$NON-NLS-1$
		lButton.setClickShortcut(KeyCode.ENTER);
		lButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				String lQuery = lSearch.getValue().toString().trim();
				if (lQuery.length() <= MIN_INPUT) {
					getWindow()
							.showNotification(
									lMessages
											.getMessage("errmsg.search.noInput"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
				} else {
					try {
						initTable(inTask.search(lQuery), lSearchResult);
						lSearchResult.setVisible(true);
					}
					catch (NoHitsException exc) {
						lSearchResult.setVisible(false);
						getWindow()
								.showNotification(
										lMessages
												.getFormattedMessage(
														"errmsg.search.noHits", exc.getQueryString()), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
					}
					catch (Exception exc) {
						lSearchResult.setVisible(false);
						getWindow()
								.showNotification(
										lMessages
												.getMessage("errmsg.search.wrongInput"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
					}
				}
			}
		});
		lLayout.addComponent(lButton);

		lLayout.addComponent(VIFViewHelper.createSpacer());

		lSearchResult.setVisible(false);
		lSearchResult.setWidth("100%"); //$NON-NLS-1$
		lSearchResult.setColumnCollapsingAllowed(true);
		lSearchResult.setColumnReorderingAllowed(true);
		lSearchResult.setSelectable(true);
		lSearchResult.setImmediate(true);
		lSearchResult.addListener((Property.ValueChangeListener) inTask);
		lLayout.addComponent(lSearchResult);
	}

	protected void initTable(ContributionContainer inSearchResult, Table inTable) {
		inTable.setContainerDataSource(inSearchResult);
		inTable.setPageLength(inSearchResult.size() > PAGE_LENGTH ? PAGE_LENGTH
				: 0);
		inTable.setVisibleColumns(ContributionContainer.NATURAL_COL_ORDER);
		inTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(
				ContributionContainer.COL_HEADERS, Activator.getMessages()));
	}

}

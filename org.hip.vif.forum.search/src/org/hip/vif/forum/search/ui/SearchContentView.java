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

package org.hip.vif.forum.search.ui;

import java.net.URL;

import org.hip.vif.core.search.NoHitsException;
import org.hip.vif.forum.search.Activator;
import org.hip.vif.forum.search.data.ContributionContainer;
import org.hip.vif.forum.search.tasks.SearchContentTask;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.HelpButton;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/** View to display the input field to search for contributions.
 *
 * @author Luthiger Created: 30.09.2011 */
@SuppressWarnings("serial")
public class SearchContentView extends CustomComponent {
    private static final int PAGE_LENGTH = 15;
    private static final int MIN_INPUT = 2;

    /** View constructor.
     *
     * @param inHelpContent URL
     * @param inTask {@link SearchContentTask} */
    public SearchContentView(final URL inHelpContent, final SearchContentTask inTask) {
        final VerticalLayout lLayout = new VerticalLayout();
        setCompositionRoot(lLayout);

        final IMessages lMessages = Activator.getMessages();
        lLayout.setStyleName("vif-view"); //$NON-NLS-1$
        lLayout.addComponent(new Label(
                String.format(
                        VIFViewHelper.TMPL_TITLE, "vif-pagetitle", lMessages.getMessage("ui.search.view.title.page")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$

        final HorizontalLayout lInput = new HorizontalLayout();
        lInput.setMargin(new MarginInfo(true, true, true, false));
        final Label lLabel = new Label(
                String.format("%s:&#160;", lMessages.getMessage("ui.search.view.label.input")), ContentMode.HTML); //$NON-NLS-1$ //$NON-NLS-2$
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

        final Button lButton = new Button(
                lMessages.getMessage("ui.search.view.button.search")); //$NON-NLS-1$
        lButton.setClickShortcut(KeyCode.ENTER);
        lButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                final String lQuery = lSearch.getValue().toString().trim();
                if (lQuery.length() <= MIN_INPUT) {
                    Notification.show(lMessages
                            .getMessage("errmsg.search.noInput"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                } else {
                    try {
                        initTable(inTask.search(lQuery), lSearchResult);
                        lSearchResult.setVisible(true);
                    }
                    catch (final NoHitsException exc) {
                        lSearchResult.setVisible(false);
                        Notification.show(
                                lMessages
                                .getFormattedMessage("errmsg.search.noHits", exc.getQueryString()), Type.WARNING_MESSAGE); //$NON-NLS-1$
                    }
                    catch (final Exception exc) {
                        lSearchResult.setVisible(false);
                        Notification.show(lMessages
                                .getMessage("errmsg.search.wrongInput"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                    }
                }
            }
        });
        lLayout.addComponent(lButton);

        lLayout.addComponent(RiplaViewHelper.createSpacer());

        lSearchResult.setVisible(false);
        lSearchResult.setWidth("100%"); //$NON-NLS-1$
        lSearchResult.setColumnCollapsingAllowed(true);
        lSearchResult.setColumnReorderingAllowed(true);
        lSearchResult.setSelectable(true);
        lSearchResult.setImmediate(true);
        lSearchResult.addValueChangeListener(inTask);
        lLayout.addComponent(lSearchResult);
    }

    protected void initTable(final ContributionContainer inSearchResult, final Table inTable) {
        inTable.setContainerDataSource(inSearchResult);
        inTable.setPageLength(inSearchResult.size() > PAGE_LENGTH ? PAGE_LENGTH
                : 0);
        inTable.setVisibleColumns(ContributionContainer.NATURAL_COL_ORDER);
        inTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(
                ContributionContainer.COL_HEADERS, Activator.getMessages()));
    }

}

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

package org.hip.vif.forum.groups.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.data.ContributionContainer;
import org.hip.vif.forum.groups.data.ContributionWrapper;
import org.hip.vif.forum.groups.tasks.BibliographyHandleTask;
import org.hip.vif.web.util.AutoCompleteHelper;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.LabelValueTable;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/** The form to search for Bibliography entries to be linked to a question.
 *
 * @author Luthiger Created: 01.09.2011 */
@SuppressWarnings("serial")
public class BibliographySearchView extends AbstractContributionView {

    private final ComboBox title;
    private final ComboBox author;

    /** View constructor.
     *
     * @param inQuestion {@link Question} the actual question, possibly the question the bibliography will be linked
     *            with
     * @param inGroup {@link Group} the actual group
     * @param inTitles {@link IndexedContainer} the titles of existing bibliography entries, to be filled in the combo
     *            box
     * @param inAuthors {@link IndexedContainer} the authors of existing bibliography entries, to be filled in the combo
     *            box
     * @param inTask {@link BibliographyHandleTask} the task (controller) of this view */
    public BibliographySearchView(final Question inQuestion, final Group inGroup, final IndexedContainer inTitles,
            final IndexedContainer inAuthors, final BibliographyHandleTask inTask) {
        final VerticalLayout lLayout = new VerticalLayout();
        setCompositionRoot(lLayout);
        lLayout.setSpacing(true);

        final IMessages lMessages = Activator.getMessages();
        lLayout.setStyleName("vif-view"); //$NON-NLS-1$
        final String lTitle = lMessages.getFormattedMessage("ui.bibliography.link.title.page", //$NON-NLS-1$
                BeanWrapperHelper.getPlain(QuestionHome.KEY_QUESTION, inQuestion),
                BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION_DECIMAL, inQuestion));
        lLayout.addComponent(new Label(
                String.format(VIFViewHelper.TMPL_TITLE, "vif-pagetitle", lTitle), ContentMode.HTML)); //$NON-NLS-1$

        lLayout.addComponent(new Label(lMessages.getMessage("ui.bibliography.link.remark"), ContentMode.HTML)); //$NON-NLS-1$

        final LabelValueTable lTable = new LabelValueTable();
        title = createComboBox(inTitles);
        title.focus();
        lTable.addRow(lMessages.getMessage("ui.bibliography.label.title"), title); //$NON-NLS-1$

        author = createComboBox(inAuthors);
        lTable.addRow(lMessages.getMessage("ui.bibliography.label.author"), author); //$NON-NLS-1$
        lLayout.addComponent(lTable);

        final Button lSearch = new Button(lMessages.getMessage("ui.bibliography.link.button.search")); //$NON-NLS-1$
        final Button lCreate = new Button(lMessages.getMessage("ui.bibliography.link.button.create")); //$NON-NLS-1$
        lCreate.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                if (!inTask.createNew(title.getValue(), author.getValue())) {
                    Notification.show(Activator.getMessages().getMessage("errmsg.general"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                }
            }
        });
        lCreate.setVisible(false);

        final ResultView lResultView = new ResultView(inTask);
        lResultView.setVisible(false);

        lSearch.setClickShortcut(KeyCode.ENTER);
        lSearch.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                if (inTask.searchFor(title.getValue(), author.getValue())) {
                    lCreate.setVisible(true);
                    lResultView.fillTable(inTask.getTexts());
                    lResultView.setVisible(true);
                }
            }
        });

        lLayout.addComponent(RiplaViewHelper.createButtons(lSearch, lCreate));
        lLayout.addComponent(RiplaViewHelper.createSpacer());
        lLayout.addComponent(lResultView);
    }

    private ComboBox createComboBox(final IndexedContainer inEntries) {
        final ComboBox out = new AutoCompleteSelection();
        out.setContainerDataSource(inEntries);
        out.setItemCaptionMode(ItemCaptionMode.ID);
        out.setWidth(400, Unit.PIXELS);
        out.setFilteringMode(FilteringMode.STARTSWITH);
        out.setImmediate(true);
        out.setStyleName("vif-input"); //$NON-NLS-1$
        return out;
    }

    // ---

    /** Extended <code>ComboBox</code> to attach lazy loading behavior.
     *
     * @author Luthiger Created: 11.09.2011 */
    private static class AutoCompleteSelection extends ComboBox {
        private String filterString;
        private boolean filteringContainer;

        @Override
        public void containerItemSetChange(final Container.ItemSetChangeEvent inEvent) {
            if (!filteringContainer) {
                super.containerItemSetChange(inEvent);
            }
        }

        @Override
        public void changeVariables(final Object inSource, final Map<String, Object> inVariables) {
            filterString = (String) inVariables.get("filter"); //$NON-NLS-1$
            if (filterString != null) {
                filterString = filterString.toLowerCase();
            }
            super.changeVariables(inSource, inVariables);
        }

        /** Lazy loading happens here: We call the container's lazy loading methods. */
        @Override
        protected List<?> getFilteredOptions() {
            final Container lContainer = getContainerDataSource();
            if (!(lContainer instanceof AutoCompleteHelper.LazyLoadingBibliographyContainer)) {
                return super.getFilteredOptions();
            }

            final Filterable lFilterable = (Filterable) lContainer;
            final Filter lFilter = buildFilter(filterString, getFilteringMode());
            if (lFilter == null) {
                return Collections.EMPTY_LIST;
            }

            filteringContainer = true;
            lFilterable.addContainerFilter(lFilter);
            final Indexed lIndexed = (Indexed) lContainer;
            final int lFilteredSize = lContainer.size();
            final List<Object> lOptions = new ArrayList<Object>();
            for (int i = 0; i < lFilteredSize; ++i) {
                lOptions.add(lIndexed.getIdByIndex(i));
            }

            // to the outside, filtering should not be visible
            if (lFilter != null) {
                lFilterable.removeContainerFilter(lFilter);
            }
            filteringContainer = false;

            return lOptions;
        }
    }

    /** View component to display the table of search results.
     *
     * @author Luthiger Created: 25.09.2011 */
    private static class ResultView extends CustomComponent {
        private static final String[] COL_HEADERS = new String[] {
            "", "container.table.headers.id", "container.table.headers.text" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        private Component table;
        private final VerticalLayout layout;
        private ContributionContainer texts;
        private final BibliographyHandleTask bibliographyTask;

        /** Constructor
         *
         * @param inTask BibliographyHandleTask */
        ResultView(final BibliographyHandleTask inTask) {
            bibliographyTask = inTask;
            layout = new VerticalLayout();
            setCompositionRoot(layout);
            layout.setSpacing(true);

            final IMessages lMessages = Activator.getMessages();
            layout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE,
                    "vif-subtitle", lMessages.getMessage("ui.bibliography.link.subtitle")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$

            table = new Label("Dummy"); //$NON-NLS-1$
            layout.addComponent(table);

            final Button lAdd = new Button(lMessages.getMessage("ui.bibliography.link.button.link")); //$NON-NLS-1$
            final Button lEdit = new Button(lMessages.getMessage("ui.bibliography.link.button.edit")); //$NON-NLS-1$
            layout.addComponent(RiplaViewHelper.createButtons(lAdd, lEdit));

            lAdd.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final ClickEvent inEvent) {
                    if (VIFViewHelper.processAction(texts)) {
                        bibliographyTask.addBibliography();
                    }
                }
            });

            lEdit.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final ClickEvent inEvent) {
                    if (VIFViewHelper.processAction(texts)) {
                        inTask.editBibliography();
                    }
                }
            });
        }

        /** Fill the table according to the information entered into the combo boxes.
         *
         * @param inTexts ContributionContainer */
        void fillTable(final ContributionContainer inTexts) {
            if (inTexts == null)
                return;
            if (!inTexts.hasItems())
                return;

            texts = inTexts;
            final Component lOld = table;
            table = createTable(inTexts);
            layout.replaceComponent(lOld, table);
        }

        Component createTable(final ContributionContainer inTexts) {
            final Table outTable = new Table();

            outTable.setStyleName("vif-table"); //$NON-NLS-1$
            outTable.setWidth("100%"); //$NON-NLS-1$

            outTable.setContainerDataSource(inTexts);
            // generate check box
            outTable.addGeneratedColumn(ContributionContainer.CONTRIBUTION_CHECK,
                    new VIFViewHelper.CheckBoxColumnGenerator(new VIFViewHelper.IConfirmationModeChecker() {
                        @Override
                        public boolean inConfirmationMode() {
                            return false;
                        }
                    }));
            // generate label component for html text
            outTable.addGeneratedColumn(ContributionContainer.CONTRIBUTION_TEXT, new Table.ColumnGenerator() {
                @Override
                public Component generateCell(final Table inSource, final Object inItemId, final Object inColumnId) {
                    return new Label(((ContributionWrapper) inItemId).getContributionText(), ContentMode.HTML);
                }
            });

            outTable.setColumnCollapsingAllowed(true);
            outTable.setColumnReorderingAllowed(true);
            outTable.setSelectable(true);
            outTable.setImmediate(true);
            outTable.setPageLength(0);
            outTable.setColumnExpandRatio(ContributionContainer.CONTRIBUTION_TEXT, 1);
            outTable.addValueChangeListener(bibliographyTask);

            outTable.setVisibleColumns(ContributionContainer.NATURAL_COL_ORDER_WO_STATE);
            outTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(COL_HEADERS, Activator.getMessages()));

            return outTable;
        }
    }

}

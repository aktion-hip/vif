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

package org.hip.vif.web.util;

import java.sql.SQLException;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.DownloadTextHome;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.web.Activator;
import org.hip.vif.web.biblio.BibliographyAdapter;
import org.hip.vif.web.biblio.TextType;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.AbstractFormCreator;
import org.ripla.web.util.LabelValueTable;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;

/** Helper class providing functionality for bibliography views.
 *
 * @author Luthiger Created: 03.12.2011 */
public class BibliographyViewHelper {

    /** Returns a factory class to create the form to edit bibliography entries.
     *
     * @param inText {@link Text}
     * @param inUpload {@link UploadComponent}
     * @param inState String
     * @return {@link FormCreator} */
    public static FormCreator createBiblioForm(final Text inText,
            final UploadComponent inUpload, final String inState) {
        return new FormCreator(inText, inUpload, inState);
    }

    /** Returns the view to display the data of a bibliography entry.
     *
     * @param inText {@link Text}
     * @param inDownloads {@link QueryResult}
     * @param inKeyType String the object's key for the text type
     * @return {@link LabelValueTable}
     * @throws VException
     * @throws SQLException */
    public static LabelValueTable createBiblioView(
            final GeneralDomainObject inText, final QueryResult inDownloads,
            final String inKeyType) throws VException, SQLException {
        final IMessages lMessages = Activator.getMessages();
        final int lType = BeanWrapperHelper.getInteger(inKeyType, inText);
        final DynamicLabels lLabels = new DynamicLabels(lType, lMessages);

        final LabelValueTable lTable = new LabelValueTable();
        lTable.addRow(
                lMessages.getMessage("ui.bibliography.label.typ"), TextType.getOption(lType)); //$NON-NLS-1$
        lTable.addRowEmphasized(
                lMessages.getMessage("ui.bibliography.label.title"), BeanWrapperHelper.getString(TextHome.KEY_TITLE, inText)); //$NON-NLS-1$
        lTable.addRow(
                lMessages.getMessage("ui.bibliography.label.subtitle"), BeanWrapperHelper.getString(TextHome.KEY_SUBTITLE, inText)); //$NON-NLS-1$
        lTable.addRowEmphasized(
                lMessages.getMessage("ui.bibliography.label.author"), BeanWrapperHelper.getString(TextHome.KEY_AUTHOR, inText)); //$NON-NLS-1$
        lTable.addRow(lLabels.label1,
                BeanWrapperHelper.getString(TextHome.KEY_COAUTHORS, inText));
        lTable.addRow(
                lMessages.getMessage("ui.bibliography.label.year"), BeanWrapperHelper.getString(TextHome.KEY_YEAR, inText)); //$NON-NLS-1$
        String lPublicationValue = BeanWrapperHelper.getString(
                TextHome.KEY_PUBLICATION, inText);
        if (new BibliographyAdapter(inText, inKeyType).hasWebPageUrl()) {
            lPublicationValue = createLink(lPublicationValue);
        }
        lTable.addRow(lLabels.label2, lPublicationValue);
        lTable.addRow(
                lMessages.getMessage("ui.bibliography.label.publisher"), BeanWrapperHelper.getString(TextHome.KEY_PUBLISHER, inText)); //$NON-NLS-1$
        lTable.addRow(lLabels.label3,
                BeanWrapperHelper.getString(TextHome.KEY_PLACE, inText));
        lTable.addRow(
                lMessages.getMessage("ui.bibliography.label.pages"), BeanWrapperHelper.getString(TextHome.KEY_PAGES, inText)); //$NON-NLS-1$
        lTable.addRow(
                lMessages.getMessage("ui.bibliography.label.volume"), BeanWrapperHelper.getString(TextHome.KEY_VOLUME, inText)); //$NON-NLS-1$
        lTable.addRow(
                lMessages.getMessage("ui.bibliography.label.number"), BeanWrapperHelper.getString(TextHome.KEY_NUMBER, inText)); //$NON-NLS-1$
        lTable.addRow(
                lMessages.getMessage("ui.bibliography.label.remarks"), BeanWrapperHelper.getString(TextHome.KEY_REMARK, inText)); //$NON-NLS-1$

        final String lLabel = lMessages
                .getMessage("ui.bibliography.label.upload"); //$NON-NLS-1$
        createDownloadLinks(inDownloads, lTable, lLabel);
        return lTable;
    }

    private static void createDownloadLinks(final QueryResult inDownloads,
            final LabelValueTable inTable, final String inLabel)
            throws SQLException, BOMException {
        while (inDownloads.hasMoreElements()) {
            final GeneralDomainObject lDownload = inDownloads.next();
            final DownloadFileLink lDownloadButton = new DownloadFileLink(
                    BeanWrapperHelper.getString(DownloadTextHome.KEY_LABEL,
                            lDownload), BeanWrapperHelper.getLong(
                            DownloadTextHome.KEY_ID, lDownload));
            inTable.addRow(inLabel, lDownloadButton);
        }
    }

    private static String createLink(final String inPublicationValue) {
        final String lHref = String
                .format(" href=\"%s\"", inPublicationValue.startsWith("www.") ? "http://" + inPublicationValue : inPublicationValue); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return String.format("<a target=\"_blank\"%s>%s</a>", lHref, inPublicationValue); //$NON-NLS-1$
    }

    // --- inner classes ---

    public static class FormCreator extends AbstractFormCreator {
        private static final int DFT_WIDTH_INPUT = 300;

        private final IMessages messages;
        // private final Text text;
        private final UploadComponent upload;
        private final String state;

        private final LabelValueTable table;
        private TextField author;
        private Label label1;
        private Label label2;
        private Label label3;
        private ComboBox types;
        private final Integer type;

        FormCreator(final Text inText, final UploadComponent inUpload,
                final String inState) {
            super(TextBean.createTextBean(inText));
            type = BeanWrapperHelper.getInteger(TextHome.KEY_TYPE, inText);
            upload = inUpload;
            state = inState;
            messages = Activator.getMessages();
            table = new LabelValueTable();
        }

        @Override
        protected Component createTable() {
            final DynamicLabels lLabels = new DynamicLabels(type, messages);
            // types
            String lFieldLabel = messages
                    .getMessage("ui.bibliography.label.typ"); //$NON-NLS-1$
            types = getBiblioTypes(type, messages, this);
            table.addRow(lFieldLabel, types);
            // author
            lFieldLabel = messages.getMessage("ui.bibliography.label.author"); //$NON-NLS-1$
            author = RiplaViewHelper.createTextField(DFT_WIDTH_INPUT);
            focusInit();
            table.addRowEmphasized(lFieldLabel,
                    addFieldRequired(TextBean.FN_AUTHOR, author, lFieldLabel)); //$NON-NLS-1$
            // dynamic label handling
            label1 = table.addRow(lLabels.label1, RiplaViewHelper.createTextField(DFT_WIDTH_INPUT));
            // title
            lFieldLabel = messages.getMessage("ui.bibliography.label.title"); //$NON-NLS-1$
            table.addRowEmphasized(
                    lFieldLabel,
                    addFieldRequired(
                            TextBean.FN_TITLE, RiplaViewHelper.createTextField(DFT_WIDTH_INPUT), lFieldLabel)); //$NON-NLS-1$
            table.addRow(
                    messages.getMessage("ui.bibliography.label.subtitle"),
                    addField(TextBean.FN_SUBTITLE, RiplaViewHelper.createTextField(DFT_WIDTH_INPUT)));
            lFieldLabel = messages.getMessage("ui.bibliography.label.year"); //$NON-NLS-1$
            table.addRowEmphasized(lFieldLabel, addFieldRequired(
                    TextBean.FN_YEAR, RiplaViewHelper.createTextField(DFT_WIDTH_INPUT), lFieldLabel)); //$NON-NLS-1$
            // dynamic label handling
            label2 = table.addRow(lLabels.label2,
                    addField(TextBean.FN_PUBLICATION, RiplaViewHelper.createTextField(DFT_WIDTH_INPUT)));
            table.addRow(
                    messages.getMessage("ui.bibliography.label.pages"), createPageVolNoFields(messages)); //$NON-NLS-1$
            table.addRow(
                    messages.getMessage("ui.bibliography.label.publisher"), addField(TextBean.FN_PUBLISHER, RiplaViewHelper.createTextField(DFT_WIDTH_INPUT))); //$NON-NLS-1$
            // dynamic label handling
            label3 = table
                    .addRow(lLabels.label3,
                            addField(TextBean.FN_PLACE, RiplaViewHelper.createTextField(DFT_WIDTH_INPUT))); //$NON-NLS-1$
            table.addRow(addField(TextBean.FN_REMARKS, createEditField(180)));

            table.addRow(
                    messages.getMessage("ui.bibliography.label.upload"), upload); //$NON-NLS-1$
            table.addRow(state);

            return table;
        }

        private Component createPageVolNoFields(final IMessages inMessages) {
            final HorizontalLayout outLayout = new HorizontalLayout();

            outLayout.addComponent(addField(TextBean.FN_PAGES, RiplaViewHelper.createTextField(80)));
            outLayout.addComponent(LabelValueTable.createPlainLabel(inMessages
                    .getMessage("ui.bibliography.label.volume"))); //$NON-NLS-1$
            outLayout.addComponent(addField(TextBean.FN_VOLUME, RiplaViewHelper.createTextField(80)));
            outLayout.addComponent(LabelValueTable.createPlainLabel(inMessages
                    .getMessage("ui.bibliography.label.number"))); //$NON-NLS-1$
            outLayout.addComponent(addField(TextBean.FN_NUMBER, RiplaViewHelper.createTextField(80)));
            return outLayout;
        }

        private RichTextArea createEditField(final int inHeight) {
            final RichTextArea outEditor = new RichTextArea();
            outEditor.setWidth("70%"); //$NON-NLS-1$
            outEditor.setHeight(inHeight, Unit.PIXELS);
            outEditor.setStyleName("vif-editor"); //$NON-NLS-1$
            return outEditor;
        }

        void focusInit() {
            author.focus();
        }

        void updateLabels(final int inType) {
            final DynamicLabels lLabels = new DynamicLabels(inType, messages);
            label1.setPropertyDataSource(new ObjectProperty<String>(String
                    .format(LabelValueTable.STYLE_LABEL, lLabels.label1),
                    String.class));
            label2.setPropertyDataSource(new ObjectProperty<String>(String
                    .format(LabelValueTable.STYLE_LABEL, lLabels.label2),
                    String.class));
            label3.setPropertyDataSource(new ObjectProperty<String>(String
                    .format(LabelValueTable.STYLE_LABEL, lLabels.label3),
                    String.class));
        }

        public ComboBox getTypes() {
            return types;
        }

        @SuppressWarnings("serial")
        private ComboBox getBiblioTypes(final int inType,
                final IMessages inMessages, final FormCreator inForm) {
            final ComboBox outTypes = new ComboBox();
            for (final TextType lType : TextType.values()) {
                final int lID = lType.getTypeValue();
                outTypes.addItem(lID);
                outTypes.setItemCaption(lID, lType.getLabel());
            }
            outTypes.select(inType);
            outTypes.setStyleName("vif-input"); //$NON-NLS-1$
            outTypes.setWidth(110, Unit.PIXELS);
            outTypes.setNullSelectionAllowed(false);
            outTypes.addValueChangeListener(new ValueChangeListener() {
                @Override
                public void valueChange(final ValueChangeEvent inEvent) {
                    inForm.updateLabels(Integer.parseInt(((ComboBox) inEvent
                            .getProperty()).getValue().toString()));
                }
            });
            outTypes.setImmediate(true);
            return outTypes;
        }
    }

    private static class DynamicLabels {
        String label1;
        String label2;
        String label3;

        DynamicLabels(final int inType, final IMessages inMessages) {
            label1 = inType == 2 ? inMessages
                    .getMessage("ui.bibliography.label.editor") : inMessages.getMessage("ui.bibliography.label.coauthor"); //$NON-NLS-1$ //$NON-NLS-2$

            label2 = inMessages.getMessage("ui.bibliography.label.publication"); //$NON-NLS-1$
            if (inType == 2) {
                label2 = inMessages
                        .getMessage("ui.bibliography.label.booktitle"); //$NON-NLS-1$
            } else if (inType == 3) {
                label2 = inMessages.getMessage("ui.bibliography.label.web"); //$NON-NLS-1$
            }

            label3 = inType == 3 ? inMessages
                    .getMessage("ui.bibliography.label.access") : inMessages.getMessage("ui.bibliography.label.place"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

}

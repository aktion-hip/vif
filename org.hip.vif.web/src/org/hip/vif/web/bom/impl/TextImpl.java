/**
    This package is part of the application VIF.
    Copyright (C) 2014, Benno Luthiger

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
package org.hip.vif.web.bom.impl;

import java.sql.SQLException;
import java.util.Locale;

import org.hip.kernel.bom.DomainObjectVisitor;
import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.DefaultExceptionWriter;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.DownloadTextHome;
import org.hip.vif.core.util.HtmlCleaner;
import org.hip.vif.web.Activator;
import org.hip.vif.web.biblio.BibliographyAdapter;
import org.hip.vif.web.biblio.TextType;
import org.hip.vif.web.bom.Text;
import org.hip.vif.web.util.BibliographyFormatter;
import org.ripla.interfaces.IMessages;

/** This domain object implements the Text interface.
 *
 * @author lbenno
 * @see org.hip.vif.core.bom.Text */
@SuppressWarnings("serial")
public class TextImpl extends org.hip.vif.core.bom.impl.TextImpl implements Text {
    public final static String HOME_CLASS_NAME = "org.hip.vif.web.bom.impl.TextHomeImpl";

    private static final String NL = System.getProperty("line.separator");

    private static final String TMPL_MAIL_PLAIN = "%s: %s";
    private static final String TMPL_MAIL_HTML = "<tr><td><i>%s</i>:</td><td>%s</td></tr>";

    /** @throws WorkflowException */
    public TextImpl() throws WorkflowException {
        super();
    }

    /** <code>&lt;option value="Value">Label&lt;/option></code>
     *
     * @param inLocale {@link Locale}
     * @return String the html for the options in an html select of a text type. */
    public static String getOptions(final Locale inLocale) {
        final StringBuilder out = new StringBuilder();
        for (final TextType lTextType : TextType.values()) {
            out.append(lTextType.renderHtml(false)).append(NL);
        }
        return new String(out);
    }

    /** <code>&lt;option value="Value" selected="selected">Label&lt;/option></code>
     *
     * @return String the html for the options in an html select of a text type. The appropriate option is displayed
     *         <code>selected</code>.
     * @throws VException */
    @Override
    public String getOptionsSelected() throws VException {
        final Object lType = get(org.hip.vif.core.bom.TextHome.KEY_TYPE);
        final StringBuilder out = new StringBuilder();
        for (final TextType lTextType : TextType.values()) {
            out.append(lTextType.renderHtml(lTextType.checkType(lType))).append(NL);
        }
        return new String(out);
    }

    @Override
    public String getNotification() throws VException, SQLException {
        final IMessages lMessages = Activator.getMessages();
        StringBuilder out = createNotification(TMPL_MAIL_PLAIN, lMessages, NL);

        // special treatment for remark
        final Object lValue = get(org.hip.vif.core.bom.TextHome.KEY_REMARK);
        if (lValue != null && lValue.toString().length() > 0) {
            out.append(
                    String.format(TMPL_MAIL_PLAIN, lMessages
                            .getMessage("org.hip.vif.bibliography.remark"),
                            HtmlCleaner.toPlain(lValue.toString()))).append(NL);
        }
        out = appendFileChecked(out,
                lMessages.getMessage("org.hip.vif.bibliography.file"),
                TMPL_MAIL_PLAIN, NL);
        return new String(out);
    }

    @Override
    public String getNotificationHtml() throws VException, SQLException {
        final IMessages lMessages = Activator.getMessages();
        StringBuilder out = createNotification(TMPL_MAIL_HTML, lMessages, "");

        // special treatment for remark, because we want wiki text properly
        // formatted
        final Object lValue = get(org.hip.vif.core.bom.TextHome.KEY_REMARK);
        if (lValue != null && lValue.toString().length() > 0) {
            out.append(String.format(TMPL_MAIL_HTML,
                    lMessages.getMessage("org.hip.vif.bibliography.remark"),
                    lValue.toString()));
        }
        out = appendFileChecked(out,
                lMessages.getMessage("org.hip.vif.bibliography.file"),
                TMPL_MAIL_HTML, "");
        return String
                .format("<table border=\"0\" style=\"font-size:11pt;\">%s</table>",
                        out);
    }

    private StringBuilder createNotification(final String inTemplate,
            final IMessages inMessages, final String inNL) throws VException {
        StringBuilder out = new StringBuilder();
        out.append(
                String.format(inTemplate,
                        inMessages.getMessage("org.hip.vif.bibliography.type"),
                        getType().getLabel())).append(inNL);
        out.append(
                String.format(
                        inTemplate,
                        inMessages.getMessage("org.hip.vif.bibliography.title"),
                        get(org.hip.vif.core.bom.TextHome.KEY_TITLE).toString())).append(inNL);
        out = appendPlainChecked(out, org.hip.vif.core.bom.TextHome.KEY_SUBTITLE,
                inMessages.getMessage("org.hip.vif.bibliography.subtitle"),
                inTemplate, inNL);
        out.append(
                String.format(inTemplate, inMessages
                        .getMessage("org.hip.vif.bibliography.author"),
                        get(org.hip.vif.core.bom.TextHome.KEY_AUTHOR).toString())).append(inNL);
        out = appendPlainChecked(out, org.hip.vif.core.bom.TextHome.KEY_COAUTHORS,
                inMessages.getMessage("org.hip.vif.bibliography.coauthors"),
                inTemplate, inNL);
        out = appendPlainChecked(out, org.hip.vif.core.bom.TextHome.KEY_YEAR,
                inMessages.getMessage("org.hip.vif.bibliography.year"),
                inTemplate, inNL);
        out = appendPlainChecked(out, org.hip.vif.core.bom.TextHome.KEY_PUBLICATION,
                inMessages.getMessage("org.hip.vif.bibliography.publication"),
                inTemplate, inNL);
        out = appendPlainChecked(out, org.hip.vif.core.bom.TextHome.KEY_PUBLISHER,
                inMessages.getMessage("org.hip.vif.bibliography.publisher"),
                inTemplate, inNL);
        out = appendPlainChecked(out, org.hip.vif.core.bom.TextHome.KEY_PLACE,
                inMessages.getMessage("org.hip.vif.bibliography.place"),
                inTemplate, inNL);
        out = appendPlainChecked(out, org.hip.vif.core.bom.TextHome.KEY_PAGES,
                inMessages.getMessage("org.hip.vif.bibliography.pages"),
                inTemplate, inNL);
        out = appendPlainChecked(out, org.hip.vif.core.bom.TextHome.KEY_VOLUME,
                inMessages.getMessage("org.hip.vif.bibliography.volume"),
                inTemplate, inNL);
        out = appendPlainChecked(out, org.hip.vif.core.bom.TextHome.KEY_NUMBER,
                inMessages.getMessage("org.hip.vif.bibliography.number"),
                inTemplate, inNL);
        return out;
    }

    private StringBuilder appendPlainChecked(final StringBuilder inText, final String inKey, final String inMsg,
            final String inTemplate, final String inNL) throws VException {
        final Object lValue = get(inKey);
        if (lValue == null) {
            return inText;
        }
        if (lValue.toString().length() == 0) {
            return inText;
        }
        inText.append(String.format(inTemplate, inMsg, lValue.toString())).append(inNL);
        return inText;
    }

    private StringBuilder appendFileChecked(final StringBuilder inText,
            final String inMessage, final String inTemplate, final String inNL)
                    throws VException, SQLException {
        final QueryResult lDownloads = BOMHelper.getDownloadTextHome()
                .getDownloads((Long) get(org.hip.vif.core.bom.TextHome.KEY_ID));
        while (lDownloads.hasMoreElements()) {
            inText.append(
                    String.format(inTemplate, inMessage,
                            lDownloads.next().get(DownloadTextHome.KEY_LABEL)))
                            .append(inNL);
        }
        return inText;
    }

    private TextType getType() throws GettingException {
        final Object lTypeValue = get(org.hip.vif.core.bom.TextHome.KEY_TYPE);
        for (final TextType lType : TextType.values()) {
            if (lType.checkType(lTypeValue))
                return lType;
        }
        return TextType.BOOK;
    }

    @Override
    public void accept(final DomainObjectVisitor inVisitor) {
        try {
            final BibliographyFormatter lFormatter = new BibliographyFormatter(new BibliographyAdapter(this,
                    org.hip.vif.core.bom.TextHome.KEY_TYPE));
            propertySet().setValue(org.hip.vif.core.bom.TextHome.KEY_BIBLIOGRAPHY, lFormatter.renderHtml());
        } catch (final VException exc) {
            DefaultExceptionWriter.printOut(this, exc, true);
        }
        super.accept(inVisitor);
    }

}

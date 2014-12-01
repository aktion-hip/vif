/**
	This package is part of the application VIF.
	Copyright (C) 2010-2014, Benno Luthiger

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

package org.hip.vif.markup.serializer;

import java.io.Writer;
import java.util.Collection;
import java.util.Vector;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.core.parser.builder.AbstractXmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.util.XmlStreamWriter;

/** A <code>DocumentBuilder</code> that creates plain text from text containing markup.
 *
 * @author Luthiger Created: 23.05.2010 */
public class PlainTextDocumentBuilder extends AbstractXmlDocumentBuilder {
    private static final String NL = System.getProperty("line.separator");
    private static final String LIST_ITEM = "%s%s ";
    private static final String BULLET = "*";
    private static final String URL_FORMAT = " [%s]";

    private enum ListType {
        NONE, BULLET_LIST, NUMERIC_LIST, LIST_ITEM;

        private ListType parent;

        ListType getParent() {
            return parent == null ? ListType.NONE : parent;
        }

        void setParent(final ListType inParent) {
            parent = inParent;
        }
    }

    private final String htmlNsUri = "";
    private ListType listType = ListType.NONE;
    private int counter = 0;
    private String href = null;
    private final Collection<String> entities = new Vector<String>();

    /** @param inWriter {@link Writer} */
    public PlainTextDocumentBuilder(final Writer inWriter) {
        super(inWriter);
    }

    /** @param inWriter {@link XmlStreamWriter} */
    public PlainTextDocumentBuilder(final XmlStreamWriter inWriter) {
        super(inWriter);
    }

    @Override
    public void acronym(final String inText, final String inDefinition) {
        writer.writeStartElement(htmlNsUri, "acronym"); //$NON-NLS-1$
        writer.writeAttribute("title", inDefinition); //$NON-NLS-1$
        writer.writeCharacters(inText);
        writer.writeEndElement();
    }

    @Override
    public void charactersUnescaped(final String inLiteral) {
        writer.writeLiteral(inLiteral);
    }

    @Override
    public void beginBlock(final BlockType inType, final Attributes inAttributes) {
        // intentionally left empty
        // <ul id="id" class="cssClass" style="cssStyle" lang="language"
        switch (inType) {
        case BULLETED_LIST:
            listType = ListType.BULLET_LIST;
            break;
        case NUMERIC_LIST:
            listType = ListType.NUMERIC_LIST;
            break;
        case LIST_ITEM:
            final ListType lItem = ListType.LIST_ITEM;
            lItem.setParent(listType);

            switch (listType) {
            case NUMERIC_LIST:
                writer.writeLiteral(String.format(LIST_ITEM, NL, ++counter));
                listType = lItem;
                break;
            default:
                writer.writeLiteral(String.format(LIST_ITEM, NL, BULLET));
                listType = lItem;
                break;
            }
            break;
        default:
            break;
        }
    }

    @Override
    public void endBlock() {
        // ></div>
        listType = listType.getParent();
        if (listType == ListType.NONE) {
            counter = 0;
        }
    }

    @Override
    public void beginDocument() {
        // intentionally left empty
        // <?xml version='1.0' encoding='utf-8' ?><html xmlns="http://www.w3.org/1999/xhtml"><head><meta
        // http-equiv="Content-Type" content="text/html; charset=utf-8"/></head><body
    }

    @Override
    public void endDocument() {
        // ></body></html>
    }

    @Override
    public void beginHeading(final int inLevel, final Attributes inAttributes) {
        // <h1 id="id" class="cssClass" style="cssStyle" lang="language"
    }

    @Override
    public void endHeading() {
        // ></h1>
    }

    @Override
    public void beginSpan(final SpanType inType, final Attributes inAttributes) {
        // <cite id="id" class="cssClass" style="cssStyle" lang="language"
        if (inType == SpanType.LINK) {
            href = ((LinkAttributes) inAttributes).getHref();
        }
    }

    @Override
    public void endSpan() {
        // ></cite>
        if (href != null) {
            writer.writeLiteral(String.format(URL_FORMAT, href));
            href = null;
        }
    }

    @Override
    public void entityReference(final String inName) {
        final String lEntity = String.format("'%s'", inName);
        entities.add(lEntity);
        writer.writeLiteral(lEntity);
    }

    @Override
    public void image(final Attributes inAttributes, final String inUrl) {
        // <img id="id" class="cssClass" style="cssStyle" lang="language" border="0" src="url"
    }

    @Override
    public void imageLink(final Attributes inLinkAttributes, final Attributes inImageAttributes, final String inHref,
            final String inImageUrl) {
        // <a href="href" id="id" class="cssClass" style="cssStyle" lang="language"><img id="id" class="cssClass"
        // style="cssStyle" lang="language" border="0" src="imageUrl"/></a>
    }

    @Override
    public void lineBreak() {
        // <br
        writer.writeLiteral(NL);
    }

    @Override
    public void link(final Attributes inAttributes, final String inHrefOrHashName, final String inText) {
        // <a href="hrefOrHashName" id="id" class="cssClass" style="cssStyle" lang="language">text</a>
        writer.writeLiteral("link:");
        writer.writeLiteral(inText);
    }

    public Collection<String> getEntities() {
        return entities;
    }

}

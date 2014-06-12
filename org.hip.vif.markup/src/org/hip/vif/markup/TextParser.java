/*
	This package is part of the application VIF.
	Copyright (C) 2010, Benno Luthiger

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
/*
	This package is part of the application VIF.
	Copyright (C) 2010, Benno Luthiger

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
package org.hip.vif.markup;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import org.hip.vif.markup.serializer.PlainTextDocumentBuilder;

/**
 * Parser class that converts markup text (using the textile format) to html. 
 *
 * @author Luthiger
 */
public class TextParser {
	private MarkupParser markupParser;
	
	private static final HashMap<String, String> ENTITY_REPLACE;
	
	static {
		ENTITY_REPLACE = new HashMap<String, String>();
		ENTITY_REPLACE.put("'#8221'", "\"");
		ENTITY_REPLACE.put("'#8222'", "\"");
		ENTITY_REPLACE.put("'#8212'", "-");
		ENTITY_REPLACE.put("'#8211'", "-");
		ENTITY_REPLACE.put("'#8482'", "(tm)");
		ENTITY_REPLACE.put("'#169'", "(c)");
		ENTITY_REPLACE.put("'#174'", "(r)");
		ENTITY_REPLACE.put("'#215'", "x");
		ENTITY_REPLACE.put("'#8218'", "'");
		ENTITY_REPLACE.put("'#8217'", "'");
	}

	/**
	 * Constructor
	 */
	public TextParser() {
		markupParser = new MarkupParser();
		markupParser.setMarkupLanguage(new TextileLanguage());
	}
	
	/**
	 * Parses textile markup to html.
	 * 
	 * @param inWikiText the text containing textile markup.
	 * @return String the converted html.
	 */
	public String parseToHtml(String inWikiText) {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder lBuilder = new HtmlDocumentBuilder(out);
		lBuilder.setEmitAsDocument(false);
		markupParser.setBuilder(lBuilder);
		markupParser.parse(inWikiText);
		markupParser.setBuilder(null);
		return out.toString();
	}
	
	/**
	 * Parses textile markup to plain text.
	 * 
	 * @param inWikiText the text containing textile markup.
	 * @return String the text with all markup removed.
	 * @throws IOException 
	 */
	public String parseToPlain(String inWikiText) throws IOException {
		StringWriter lWriter = new StringWriter();
		PlainTextDocumentBuilder lBuilder = new PlainTextDocumentBuilder(lWriter);
		markupParser.setBuilder(lBuilder);
		markupParser.parse(inWikiText);
		markupParser.setBuilder(null);
		String out = replaceSpecialChars(lWriter.toString(), lBuilder.getEntities());
		lWriter.close();
		return out;
	}

	private String replaceSpecialChars(String inText, Collection<String> inEntities) {
		String out = inText;
		for (String lEntity : inEntities) {
			String lReplace = ENTITY_REPLACE.get(lEntity);
			out = out.replace(lEntity, lReplace == null ? "" : lReplace);
		}
		return out;
	}
	
}

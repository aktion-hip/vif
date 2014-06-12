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

package org.hip.vif.markup.serializer;

import org.hip.kernel.bom.Property;
import org.hip.kernel.bom.impl.XMLSerializer;
import org.hip.vif.markup.TextParser;

/**
 * Serializer to convert text with textile markup to html.
 * 
 * @author Luthiger
 * Created: 21.05.2010
 */
@SuppressWarnings("serial")
public class MarkupToHtmlSerializer extends XMLSerializer {
	private TextParser parser = new TextParser();

	@Override
	protected Object emitPropertyValue(Property inProperty) {
		return parser.parseToHtml((String) inProperty.getValue());
	}

}

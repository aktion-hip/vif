/*
	This package is part of the servlet framework used for the application VIF.
	Copyright (C) 2004, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package org.hip.kernel.servlet.impl;

import java.io.Serializable;

import org.hip.kernel.servlet.Context;

/**
 * 	Implements a html page for a frameset.
 *
 *	@author	Benno Luthiger
 */
public class FrameSetHtmlPage extends AbstractHtmlPage implements Serializable {
	private final static String FRAMESET_BEGIN = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\"\n     \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n";
	
	/**
	 * FrameSetHtmlPage constructor comment.
	 */
	public FrameSetHtmlPage() {
		super();
	}

	/**
	 * 
	 * @param inContext org.hip.kernel.servlet.Context
	 */
	public FrameSetHtmlPage(Context inContext) {
		super(inContext);
	}

	protected String getXMLName() {
		return null;
	}
	
	/**
	 * Returns begin of this page as html-string. Contains stylesheet-include and onLoad-script.
	 *
	 * @return java.lang.String
	 */ 
	protected String createBegin() {
		StringBuffer outBegin = new StringBuffer(FRAMESET_BEGIN);
		outBegin.append(HEAD_BEGIN);
		outBegin.append(getTitle()).append(getHtmlHead()).append(cssLinks()).append(scriptLinks());
		outBegin.append(HEAD_END);
		return new String(outBegin);
	}

	/**
	 * Returns end of this page as html-string.
	 * 
	 * @return java.lang.String
	 * @param inString java.lang.String
	 */
	protected String createEnd() {
		return HTML_END;
	}
}

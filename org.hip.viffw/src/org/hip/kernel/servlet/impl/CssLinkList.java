package org.hip.kernel.servlet.impl;

/*
	This package is part of the servlet framework used for the application VIF.
	Copyright (C) 2001, Benno Luthiger

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

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.sys.VObject;

/**
 * List of links to CSS-Stylesheet defined in a file.
 * 
 * @author: Benno Luthiger
 */
public class CssLinkList extends VObject implements Serializable {
	private Vector<CssLink> cssLinks = null;
	
	/**
	 * CssLinkList default constructor.
	 */
	public CssLinkList() {
		super();
	}
	
	/**
	 * Adds the CssLink to the list if not added before.
	 * 
	 * @param inLink org.hip.kernel.servlet.impl.CssLink
	 */
	public void addCssLink(CssLink inLink) {
	
		//pre: parameter not null and css-link not added before
		if (inLink == null)
			return;
		if (cssLinks().contains(inLink))
			return;
		
		cssLinks().addElement(inLink);	
	}
	
	/**
	 * Returns Vector of CssLinks added to this list.
	 * 
	 * @return java.util.Vector
	 */
	protected Vector<CssLink> cssLinks() {
		if (cssLinks == null) 
			cssLinks = new Vector<CssLink>(5);
	
		return cssLinks;
	}
	
	/**
	 * Returns an Iterator of the links this list contains.
	 *
	 * @return java.util.Iterator
	 */
	public Iterator<CssLink> getCssLinks() {
		return cssLinks().iterator();	
	}
	
	/**
	 * Returns HTML-String-Representation of all css-links in this list.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
	
		StringBuffer outLinks = new StringBuffer();
		Iterator<CssLink> lLinks = getCssLinks();
		while (lLinks.hasNext())
			outLinks.append(lLinks.next()+"\n");
		
		return new String(outLinks);
	}
}

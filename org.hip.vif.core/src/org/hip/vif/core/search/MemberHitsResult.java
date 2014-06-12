/*
 This package is part of the application VIF.
 Copyright (C) 2005, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.hip.vif.core.search;

import java.io.IOException;

import org.apache.lucene.document.Document;

/**
 * Query result containing the hits of a search query of a member search.
 * 
 * @author Benno Luthiger
 * Created on 02.10.2005
 */
public class MemberHitsResult extends AbstractHitsQueryResult {

	/**
	 * @param inHits Document[]
	 * @throws IOException
	 * @throws NoHitsException
	 */
	public MemberHitsResult(Document[] inHits) {
		super(inHits);
	}

	/**
	 * @see org.hip.vif.core.search.AbstractHitsQueryResult#getHitsDomainObject(org.apache.lucene.document.Document)
	 */
	protected AbstractHitsDomainObject getHitsDomainObject(Document inDocument) {
		return new MemberHitsObject(inDocument);
	}
}

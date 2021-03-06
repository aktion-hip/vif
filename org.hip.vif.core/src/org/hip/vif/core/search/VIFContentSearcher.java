/**
	This package is part of the application VIF.
	Copyright (C) 2005-2014, Benno Luthiger

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
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.hip.kernel.bom.QueryResult;

/** Implements full text searching using lucene.
 *
 * @author Benno Luthiger Created on 27.09.2005 */
public class VIFContentSearcher extends AbstractVIFSearcher {

    @Override
    protected QueryResult getHitsQueryResult(final Document[] inHits) {
        return new ContentHitsResult(inHits);
    }

    @Override
    protected Query parseQuery(final String inQueryTerm) throws ParseException {
        new QueryParser("", getAnalyzer());
        final QueryParser lParser = new QueryParser(IndexField.CONTENT_FULL.fieldName, getAnalyzer());
        return lParser.parse(inQueryTerm);
    }

    @Override
    protected IndexReader getIndexReader() throws CorruptIndexException, IOException {
        return VIFIndexing.INSTANCE.createContentIndexReader();
    }

}

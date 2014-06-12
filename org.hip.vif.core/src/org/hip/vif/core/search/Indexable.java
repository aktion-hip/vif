package org.hip.vif.core.search;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.hip.kernel.exc.VException;

/*
This package is part of the administration of the application VIF.
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

/**
 * Interface for all domain objects (i.e. contributions) that are indexable,
 * i.e. that have content that should be included into full text search.
 * 
 * @author Benno Luthiger
 * Created on 25.09.2005 
 */
public interface Indexable {
	/**
	 * Add the content of this domain object to the full text search index.
	 * @param inWriter
	 */
	void indexContent(IndexWriter inWriter) throws IOException, VException;
}

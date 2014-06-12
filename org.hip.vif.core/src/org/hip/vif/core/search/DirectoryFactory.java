/*
	This package is part of the application VIF.
	Copyright (C) 2007, Benno Luthiger

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
package org.hip.vif.core.search;

import java.io.IOException;

import org.apache.lucene.store.Directory;

/**
 * Interface for Directory factories.
 *
 * @author Luthiger
 * Created: 25.11.2007
 */
public interface DirectoryFactory {
	/**
	 * Returns the specified index directory.
	 * 
	 * @param inIndexName String the name of the index
	 * @return org.apache.lucene.store.Directory
	 * @throws IOException
	 */
	public Directory getDirectory(String inIndexName) throws IOException;
	
	/**
	 * Returns the specified index directory that is aimed for an index refresh.
	 * 
	 * @param inIndexName String the name of the index
	 * @return org.apache.lucene.store.Directory
	 * @throws IOException
	 */
	public Directory getDirectoryForRefresh(String inIndexName) throws IOException;
	
	/**
	 * Hook that can be used for actions before a change of the specified index is made.
	 * 
	 * @param inIndexName String the name of the index
	 * @throws IOException
	 */
	public void beforeChangeAction(String inIndexName) throws IOException;
	
	/**
	 * Hook that can be used for actions after a change of the specified index is made.
	 * 
	 * @param inIndexName String the name of the index
	 * @throws IOException
	 */
	public void afterChangeAction(String inIndexName) throws IOException;

}

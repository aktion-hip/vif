/*
 This package is part of application VIF.
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
package org.hip.vif.core.bom.search;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.search.AbstractVIFIndexer;
import org.hip.vif.core.search.VIFIndexing;

/**
 * Creates an index of the members for member searching using lucene.
 * 
 * @author Benno Luthiger
 * Created on 01.10.2005
 */
public class VIFMemberIndexer extends AbstractVIFIndexer {
	private static final int limit = 20;

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.search.AbstractVIFIndexer#doIndex(org.apache.lucene.index.IndexWriter)
	 */
	protected Integer[] doIndex(IndexWriter inWriter) throws Exception {
		Integer[] outIndexed = {null};
		
		MemberHome lHome = (MemberHome)BOMHelper.getMemberHome();
		outIndexed[0] = new Integer(processSelection(inWriter, lHome, new KeyObjectImpl()));
		return outIndexed;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.search.AbstractVIFIndexer#getLimit()
	 */
	protected int getLimit() {
		return limit;
	}
	
	/**
	 * Adds the member with the specified key to the member search index.
	 * This method has to be called when a member is added.
	 * 
	 * @param inMemberID KeyObject with MemberID as value
	 * @throws VException
	 * @throws SQLException
	 * @throws IOException
	 */
	public void addMemberToIndex(KeyObject inMemberID) throws Exception {
		addEntryToIndex(BOMHelper.getMemberHome(), inMemberID);
	}
	
	/**
	 * Deletes the document with the specified member id from the member search index.
	 * 
	 * @param inMemberID String
	 * @throws IOException
	 */
	public void deleteMemberInIndex(String inMemberID) throws IOException {
		deleteEntryInIndex(new Term(IndexField.MEMBER_ID.fieldName, inMemberID));		
	}
	
	/**
	 * Refreshes the content of the document with the specified id in the
	 * member search index.
	 * 
	 * @param inMemberID String
	 * @throws VException
	 * @throws SQLException
	 * @throws IOException
	 */
	public void refreshMemberInIndex(String inMemberID) throws Exception {
		deleteMemberInIndex(inMemberID);
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(MemberHome.KEY_ID, new Long(inMemberID));
		addMemberToIndex(lKey);
	}

	@Override
	protected IndexWriter getIndexWriter(boolean inCreate) throws CorruptIndexException, IOException {
		return VIFIndexing.INSTANCE.getMemberIndexWriter(inCreate);
	}
	
	@Override
	protected void afterChange() throws IOException {
		VIFIndexing.INSTANCE.refreshMemberIndexReader();
	}

}

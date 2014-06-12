/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

package org.hip.vif.admin.groupadmin.util;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.impl.JoinTextToQuestionHome;
import org.hip.vif.core.bom.impl.TextImpl;
import org.hip.vif.core.util.BeanWrapperHelper;

/**
 * <p>Class to help to delete bibliography entries.</p>
 * <p>We can delete bibliography entries only if they're not referenced.
 * Therefore, we have to divide the set of entries selected for deletion into unreferenced entries (which are unproblematic)
 * and referenced entries.</p>
 * <p>For referenced entries we have to find the referencing questions. If these questions belong to groups
 * the actors is administering, he can dereference the entry and, thus, make entry deletion possible.</p>
 * <p>For entries referenced by questions belonging to groups not administered by the actor, entry deletion
 * is impossible.</p>
 * 
 * @author Luthiger
 * Created: 07.12.2011
 */
public class BiblioDeleteHelper {
	private Collection<Long> deletableTexts = new Vector<Long>();
	private Collection<Long> referencedTexts = new Vector<Long>();
	private Collection<Long> undeletableTexts = new Vector<Long>();
	private Collection<Long> allTexts;
	private Collection<Long> actorsGroups;
	
	/**
	 * Constructor
	 * 
	 * @param inTextIDs {@link Collection} of Text ids
	 * @param inActorID Long
	 * @throws VException
	 * @throws SQLException
	 */
	public BiblioDeleteHelper(Collection<Long> inTextIDs, Long inActorID) throws VException, SQLException {
		allTexts = inTextIDs;
		JoinTextToQuestionHome lQuestionHome = BOMHelper.getJoinTextToQuestionHome();
		for (Long lTextID : inTextIDs) {
			QueryResult lReferencing = lQuestionHome.selectPublished(lTextID);
			if (lReferencing.hasMoreElements()) {
				if (checkRemovableReferences(lReferencing, inActorID)) {
					referencedTexts.add(lTextID);
				}
				else {
					undeletableTexts.add(lTextID);
				}
			}
			else {
				deletableTexts.add(lTextID);
			}
		}
	}
	
	/**
	 * Returns the collection of deletable text entries.
	 * 
	 * @return {@link Collection} of <code>Long</code> text entry id
	 */
	public Collection<Long> getDeletableTexts() {
		return deletableTexts;
	}
	
	/**
	 * Returns the collection of referenced text entries.
	 * These entries are not yet deletable, but all references can be removed by the actor thus making
	 * the entries deletable.
	 * 
	 * @return {@link Collection} of <code>Long</code> text entry id
	 */
	public Collection<Long> getReferencedTexts() {
		return referencedTexts;
	}

	/**
	 * Returns the collection of undeletable text entries.
	 * Theses entries are referenced with references that can't be removed by the actor.
	 * 
	 * @return {@link Collection} of <code>Long</code> text entry id
	 */
	public Collection<Long> getUndeletableTexts() {
		return undeletableTexts;
	}
	
	/**
	 * Returns the collection of IDs of all text entries.
	 * 
	 * @return {@link Collection} of <code>Long</code>
	 */
	public Collection<Long> getAllTexts() {
		return allTexts;
	}
	
	/**
	 * Checks whether the passed result set of questions referencing the text entry can be removed.
	 * 
	 * @param inReferencing {@link QueryResult}
	 * @param inActorID Long
	 * @return boolean <code>true</code> if the references passed can be removed and, thus, the entry is deletable
	 * @throws VException
	 * @throws SQLException
	 */
	private boolean checkRemovableReferences(QueryResult inReferencing, Long inActorID) throws VException, SQLException {
		Set<Long> outGroupIDs = new HashSet<Long>();
		while (inReferencing.hasMoreElements()) {
			outGroupIDs.add(BeanWrapperHelper.getLong(QuestionHome.KEY_GROUP_ID, inReferencing.nextAsDomainObject()));
		}
		Collection<Long> lActorsGroups = getActorsGroups(inActorID);
		for (Long lGroupID : outGroupIDs) {
			if (!lActorsGroups.contains(lGroupID)) {
				return false;
			}
		}
		return true;
	}
	
	private Collection<Long> getActorsGroups(Long inActorID) throws VException, SQLException {
		if (actorsGroups == null) {
			actorsGroups = new Vector<Long>();
			QueryResult lActorsGroups = BOMHelper.getJoinGroupAdminToGroupHome().select(inActorID, new OrderObjectImpl());
			while (lActorsGroups.hasMoreElements()) {
				actorsGroups.add(BeanWrapperHelper.getLong(GroupHome.KEY_ID, lActorsGroups.nextAsDomainObject()));
			}
		}
		return actorsGroups;
	}

	/**
	 * Extracts the id part of the 'id-version' string.
	 * 
	 * @param inTextID String 'id-version'
	 * @return Long the text entry's version
	 */
	public static Long getTextID(String inTextID) {
		return new Long(TextImpl.splitTextID(inTextID));
	}

}

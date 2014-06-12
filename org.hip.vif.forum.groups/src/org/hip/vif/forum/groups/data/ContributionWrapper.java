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

package org.hip.vif.forum.groups.data;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.code.CodeList;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.interfaces.ISelectableBean;
import org.hip.vif.core.util.BeanWrapperHelper;

/**
 * Bean wrapping a contribution model instance.
 * A contribution is either a <code>Question</code>, <code>Completion</code> or <code>Text</code> (aka. bibliography).
 * 
 * @author Luthiger
 * Created: 03.07.2011
 */
public class ContributionWrapper implements ISelectableBean {
	public enum EntryType {
		QUESTION(QuestionHome.KEY_STATE), 
		COMPLETION(CompletionHome.KEY_STATE), 
		TEXT(TextHome.KEY_STATE);
		
		private String keyState;

		EntryType(String inStateKey) {
			keyState = inStateKey;
		}
	}
	
	private String id;
	private String sortValue;
	private String publicID;
	private String text;
	private Long stateValue;
	private String state;
	private EntryType entryType;
	private boolean checked = false;

	private ContributionWrapper(GeneralDomainObject inDomainObject, EntryType inEntryType, String inSortValue, String inID, String inPublicID, String inText, CodeList inCodeList) {
		id = inID;
		entryType = inEntryType;
		sortValue = inSortValue;
		publicID = inPublicID;
		text = inText;
		stateValue = BeanWrapperHelper.getLong(inEntryType.keyState, inDomainObject);
		state = inCodeList.getLabel(stateValue.toString());
	}

	/**
	 * Factory method, creation the instance.
	 * 
	 * @param inDomainObject {@link GeneralDomainObject}
	 * @param inEntryType {@link EntryType}
	 * @param inSortValue String the value to sort the items
	 * @param inID String the entry's unique ID 
	 * @param inPublicID String the value displayed as the item's id
	 * @param inText String the data displayed as the item's content
	 * @param inCodeList {@link CodeList}
	 * @return {@link ContributionWrapper}
	 */
	public static ContributionWrapper createItem(GeneralDomainObject inDomainObject, EntryType inEntryType, String inSortValue, String inID, String inPublicID, String inText, CodeList inCodeList) {
		return new ContributionWrapper(inDomainObject, inEntryType, inSortValue, inID, inPublicID, inText, inCodeList);
	}
	
	public String getPublicID() {
		return publicID;
	}
	
	public String getContributionText() {
		return text;
	}
	
	public String getContributionState() {
		return state;
	}
	
	public String getSortValue() {
		return sortValue;
	}
	
	public void setChecked(boolean inChecked) {
		checked = inChecked;
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	/**
	 * @return int the wrapped contribution's state
	 */
	public int getState() {
		return stateValue.intValue();
	}
	
	/**
	 * @return {@link EntryType} the type of the contribution
	 */
	public EntryType getEntryType() {
		return entryType;
	}
	
	/**
	 * @return String the contribution's unique ID
	 */
	public String getID() {
		return id;
	}

}

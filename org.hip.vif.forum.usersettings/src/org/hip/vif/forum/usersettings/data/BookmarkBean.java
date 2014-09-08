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

package org.hip.vif.forum.usersettings.data;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.vif.core.bom.BookmarkHome;
import org.hip.vif.core.interfaces.ISelectableBean;
import org.hip.vif.web.util.BeanWrapperHelper;

/**
 * Bean class (adapter) for bookmark models.
 * 
 * @author Luthiger
 * Created: 20.12.2011
 */
public class BookmarkBean implements ISelectableBean {
	private Long questionID;
	private String text;
	private boolean checked;

	private BookmarkBean(GeneralDomainObject inDomainObject) {
		questionID = BeanWrapperHelper.getLong(BookmarkHome.KEY_QUESTIONID, inDomainObject);
		text = BeanWrapperHelper.getPlain(BookmarkHome.KEY_BOOKMARKTEXT, inDomainObject);
	}
	
	/**
	 * Factory method, creation the instance.
	 * 
	 * @param inDomainObject {@link GeneralDomainObject}
	 * @return {@link BookmarkBean}
	 */
	public static BookmarkBean createItem(GeneralDomainObject inDomainObject) {
		return new BookmarkBean(inDomainObject);
	}

	public Long getQuestionID() {
		return questionID;
	}

	public String getText() {
		return text;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean inChecked) {
		checked = inChecked;
	}

}

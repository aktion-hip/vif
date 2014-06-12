/*
 This package is part of the administration of the application VIF.
 Copyright (C) 2009, Benno Luthiger

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
package org.hip.vif.core.bom.impl;

import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.util.Contributor;

/**
 * Model for completion entries and its authors/reviewers.
 *
 * @author Luthiger
 * Created: 20.09.2009
 */
public class JoinQuestionToCompletionAndContributors extends DomainObjectImpl {
	public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinQuestionToCompletionAndContributorsHome";

	/**
	 * This Method returns the class name of the home.
	 *
	 * @return java.lang.String
	 */
	public String getHomeClassName() {
		return HOME_CLASS_NAME;
	}

	/**
	 * @return Long The completion's ID.
	 * @throws VException
	 */
	public Long getID() throws VException {
		return (Long) get(CompletionHome.KEY_ID);
	}

	/**
	 * @return Long The question's ID this completion belongs to.
	 * @throws VException
	 */
	public Long getQuestionID() throws VException {
		return (Long) get(CompletionHome.KEY_QUESTION_ID);
	}

	/**
	 * @return Contributor This completion's contributor (author or reviewer).
	 * @throws VException
	 */
	public Contributor getContributor() throws VException {
		return new Contributor(get(MemberHome.KEY_NAME).toString(),	get(MemberHome.KEY_FIRSTNAME).toString(), isAuthor());
	}
	
	private boolean isAuthor() throws VException {
		return ResponsibleHome.Type.AUTHOR.check(get(ResponsibleHome.KEY_TYPE));
	}
	
}

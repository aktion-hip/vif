package org.hip.vif.core.bom.impl;

import org.hip.kernel.bom.impl.DomainObjectImpl;

/*
	This package is part of ...
	Copyright (C) 2004, Benno Luthiger

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
/**
 * 
 * 
 * @author Benno Luthiger
 * Created on Feb 29, 2004
 */
public class JoinSubscriptionToQuestion extends DomainObjectImpl {
	public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinSubscriptionToQuestionHome";
	
	/**
	 * JoinSubscriptionToQuestion constructor.
	 */
	public JoinSubscriptionToQuestion() {
		super();
	}
	/**
	 * This Method returns the class name of the home.
	 *
	 * @return java.lang.String
	 */
	public String getHomeClassName() {
		return HOME_CLASS_NAME;
	}
}

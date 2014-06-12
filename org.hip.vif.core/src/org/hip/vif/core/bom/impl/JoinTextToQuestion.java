package org.hip.vif.core.bom.impl;

import org.hip.kernel.bom.impl.DomainObjectImpl;

/**
 * Model to link a question to a given text entry.
 *
 * @author Luthiger
 * Created: 07.09.2010
 */
public class JoinTextToQuestion extends DomainObjectImpl {
	public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinTextToQuestionHome";

	/**
	 * This Method returns the class name of the home.
	 *
	 * @return java.lang.String
	 */
	public String getHomeClassName() {
		return HOME_CLASS_NAME;
	}

}

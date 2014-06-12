package org.hip.vif.web.util;

import org.hip.kernel.bom.impl.DomainObjectImpl;

@SuppressWarnings("serial")
public class TestModel extends DomainObjectImpl {
	public static final String HOME_CLASS_NAME = "org.hip.vif.web.util.TestModelHome";

	@Override
	public String getHomeClassName() {
		return HOME_CLASS_NAME;
	}
}

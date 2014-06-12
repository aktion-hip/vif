package org.hip.vif.core;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;
import com.vaadin.terminal.ApplicationResource;

@SuppressWarnings("serial")
public class TestAppContext implements ApplicationContext {

	@Override
	public File getBaseDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Application> getApplications() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addTransactionListener(TransactionListener inListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTransactionListener(TransactionListener inListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public String generateApplicationResourceURL(ApplicationResource inResource, String inUrlKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isApplicationResourceURL(URL inContext, String inRelativeUri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getURLKey(URL inContext, String inRelativeUri) {
		// TODO Auto-generated method stub
		return null;
	}

}

package org.hip.kernel.servlet.impl;

import java.net.MalformedURLException;
import java.net.URL;

/*
 This package is part servlet framework used for the application VIF.
 Copyright (C) 2006, Benno Luthiger

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
 * Singleton that acts as generic wrapper of 
 * methods and fields special to the servlet container installed. 
 * 
 * For the time being (2008/04/14), the servlet container wrapper is implemented
 * only for Tomcat and Jetty.
 * 
 * To wrap an additional servlet container, you have to create an additional
 * wrapper class (inner class) implementing the <code>ServletContainerWrapper</code>
 * interface and adjust the <code>init()</code> method for that the 
 * new wrapper can be detected.
 * 
 * @author Benno Luthiger
 * Created on 14.02.2006
 */
public class ServletContainer {
	private static ServletContainer singleton = new ServletContainer();
	private ServletContainerWrapper servletWrapper;
	private String serverInfo = null;
	
	private interface ServletContainerWrapper {
		String getBasePath();
		String getHomePath();
	}
	
	private class TomcatWrapper implements ServletContainerWrapper {
		public String getBasePath() {
			return System.getProperty("catalina.base");
		}

		public String getHomePath() {
			return System.getProperty("catalina.home");
		}
	}
	
	private class JettyWrapper implements ServletContainerWrapper {
		public String getBasePath() {
			return getHomePath();
		}
		public String getHomePath() {
			try {
				URL lUrl = new URL(System.getProperty("osgi.configuration.area"));
				return lUrl.getFile();
			} catch (MalformedURLException exc) {
				// intentionally left empty
			}
			return "";
		}		
	}

	/**
	 * Singleton constructor.
	 */
	private ServletContainer() {
		super();
		//Fallback: take tomcat
		servletWrapper = new TomcatWrapper();
	}
	
	public static ServletContainer getInstance() {
		return singleton;
	}
	
	/**
	 * The selection of the actual system's servlet container is done here.
	 * Add you selection condition after having created a new 
	 * ServletContainerWrapper.
	 */
	private void init() {
		if (serverInfo.toLowerCase().startsWith("apache tomcat")) {
			servletWrapper = new TomcatWrapper();
		}
		else if (serverInfo.toLowerCase().startsWith("jetty")) {
			servletWrapper = new JettyWrapper();
		}
//		else if () {}
	}
	
	/**
	 * Sets the server info.
	 * You have to provide this information in the application's init() method.
	 * 
	 * @param inServerInfo String
	 * @see AbstractRequestHandler#init()
	 */
	public void setServerInfo(String inServerInfo) {
		serverInfo = inServerInfo;
		init();
	}
	
	/**
	 * Returns the server info.
	 * 
	 * @return String
	 */
	public String getServerInfo() {
		return serverInfo;
	}
	
	/**
	 * Returns the path to the servlet container's instance the 
	 * application is running in. 
	 * 
	 * @return String
	 */
	public String getBasePath() {
		return servletWrapper.getBasePath();
	}
	
	/**
	 * Returns the path to the servlet container's home,
	 * i.e. where the servlet conainer is installed.
	 * 
	 * @return String
	 */
	public String getHomePath() {
		return servletWrapper.getHomePath();
	}

}

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

package org.hip.vif.quickstart;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.hip.vif.quickstart.SplashWindow.ProgressBar;
import org.hip.vif.quickstart.SplashWindow.SplashFrame;

/**
 * The VIF quickstart bundle's main class.
 * 
 * @author Luthiger
 * Created: 14.01.2012
 */
public class VIFQuickstart {
	private static final Logger LOG = Logger.getLogger(VIFQuickstart.class.getName());
	private static final int SLEEP_TIME = 500;
	private static final String LOG_SYSTEM_PROPERTY = "logback.configurationFile";
	private static final String LOG_CONFIG_XML = "logback.xml";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		VIFQuickstart lVIFStarter = new VIFQuickstart();
		lVIFStarter.start();
	}
	
	private VIFQuickstart() {}
	
	void start() {
		SplashScreen lSplash = showSpash();
		
		//create environment and start OSGi runtime
		String lPort = getPort();
		try {
			ProgressBar lProgress = lSplash == null ? new NOOpProgressMonitor() : new ProgressBar(lSplash);
			EnvironmentHelper.createHelper().createEnvironment(lPort, lProgress);

			prepareClassPath();
			OSGiStarter lStarter = OSGiStarter.getOSGi(lPort, new File("."));

			SplashFrame lWindow = SplashWindow.createSplashWindow(lSplash, lStarter);
			lWindow.showSplash();
			lStarter.run();
			lStarter.installBundles();
			
			startBrowser(lPort, lWindow);
		}
		catch (Exception exc) {
			LOG.log(Level.SEVERE, "Error encountered while quick starting the application!", exc);
		}
	}
	
	private void startBrowser(String inPort, SplashFrame inSplash) {
		String lURL = String.format(Constants.URI_STRING, inPort);
		String lErrorMsg = "Please open the browser on " + lURL;
		SuccessIndicator lSuccess = new SuccessIndicator();
		if (!Desktop.isDesktopSupported()) {
			inSplash.setErrorMsg(lErrorMsg);
		}
		
		if (lSuccess.canBrowse) {
			Desktop lDesktop = Desktop.getDesktop();
			if (lDesktop.isSupported(Desktop.Action.BROWSE)) {
				lSuccess.setDesktop(lDesktop);
			}
			else {
				inSplash.setErrorMsg(lErrorMsg);
			}
		}
		
		//start browser and show application
		if (lSuccess.canBrowse) {
			try {
				URI lURI = new URI(lURL);
				lSuccess.browse(lURI);
			}
			catch (URISyntaxException exc) {
				LOG.log(Level.SEVERE, "Error encountered while starting the browser!", exc);
				inSplash.setErrorMsg(lErrorMsg);
			}
			catch (IOException exc) {
				LOG.log(Level.SEVERE, "Error encountered while starting the browser!", exc);
				inSplash.setErrorMsg(lErrorMsg);
			}
		}
	}
	
	private void prepareClassPath() throws InterruptedException, SecurityException, NoSuchMethodException, IllegalArgumentException, MalformedURLException, IllegalAccessException, InvocationTargetException {
		File lFile = new File(".");
		File[] lFrameworkJars = lFile.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File inDir, String inName) {
				return Constants.OSGI_FRAMEWORK.matcher(inName).matches();
			}
		});
		while (lFrameworkJars.length == 0) {
			Thread.sleep(SLEEP_TIME);
		}
		File lFrameworkJar = lFrameworkJars[0];
		while (!lFrameworkJars[0].exists()) {
			Thread.sleep(SLEEP_TIME);
		}
		
		//add jar to class loader
		final ClassLoader lClassLoader = VIFQuickstart.class.getClassLoader();
		if (!(lClassLoader instanceof URLClassLoader)) {
			LOG.log(Level.SEVERE, "The starter's class loader is not an URL classloader!");
			return;
		}
		final Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] {URL.class});
		addURL.setAccessible(true);
		addURL.invoke(lClassLoader, new Object[] {lFrameworkJar.toURI().toURL()});
		
		//logback configuration
		System.setProperty(LOG_SYSTEM_PROPERTY, new File(new File("."), LOG_CONFIG_XML).getAbsolutePath());
	}
	
	private String getPort() {
		File lJar = new File(VIFQuickstart.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));
		if (!lJar.exists()) return Constants.DFT_PORT;
		Matcher lMatcher = Constants.STARTER_JAR.matcher(lJar.getName());
		if (lMatcher.find()) {
			return lMatcher.group(1);
		}
		return Constants.DFT_PORT;
	}
	
	private static class SuccessIndicator {
		private Desktop desktop;
		boolean canBrowse = true;

		void setDesktop(Desktop inDesktop) {
			desktop = inDesktop;
		}
		void browse(URI inURI) throws IOException {
			desktop.browse(inURI);
		}
	}
	
	private SplashScreen showSpash() {
		final SplashScreen outSplash = SplashScreen.getSplashScreen();
		if (outSplash == null) {
			return null;
		}
		Graphics2D lGraphics = outSplash.createGraphics();
		if (lGraphics == null) {
			return null;
		}
		lGraphics.setColor(Color.WHITE);
		SpashUtil.writeUpper("Extracting the package:", lGraphics);
		return outSplash;
	}
	
// ---	
	private static class NOOpProgressMonitor extends ProgressBar {
		NOOpProgressMonitor() {
			super(null);
		}
		@Override
		public void progress() {
			// do nothing
		}
		@Override
		public void echoExtracted(String inEntryName) {
			// do nothing
		}
	}

}

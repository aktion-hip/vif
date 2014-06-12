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

package org.hip.vif.www;

import javax.servlet.ServletException;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

/**
 * @author Luthiger
 * Created: 22.01.2012
 */
public class Client {
	private static final String WEBAPP_NAME = "/viftest";
	
	public void registerWithHttp(HttpService inService) {
		try {
			inService.registerServlet(WEBAPP_NAME, new TestServlet(), null, null);
		}
		catch (ServletException exc) {
			exc.printStackTrace();
		}
		catch (NamespaceException exc) {
			exc.printStackTrace();
		}
	}

	public void unregisterWithHttp(HttpService inService) {
		inService.unregister(WEBAPP_NAME);
	}
	
}

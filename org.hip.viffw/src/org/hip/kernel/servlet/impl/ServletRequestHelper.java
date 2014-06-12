/*
	This package is part of the application VIF.
	Copyright (C) 2010, Benno Luthiger

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

package org.hip.kernel.servlet.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.hip.kernel.servlet.Context;
import org.hip.kernel.servlet.RequestException;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.util.DefaultNameValue;
import org.hip.kernel.util.DefaultNameValueList;
import org.hip.kernel.util.NameValueList;

/**
 * Helper class for inspecting the servlet request.
 * This class knows how to parse mulitpart requests.
 * 
 * @author Luthiger
 * Created: 21.09.2010
 */
public class ServletRequestHelper {
	private static final String KEY_MAX_SIZE = "org.hip.vif.upload.size";
	private static final int DFLT_SIZE = 50;
	private static final int MEGA = 1048576;
	
	private IInspector inspector;

	public ServletRequestHelper(HttpServletRequest inRequest) throws RequestException {
		inspector = getInspector(inRequest);
		inspector.parse();
	}
	
	private IInspector getInspector(HttpServletRequest inRequest) {
		if (ServletFileUpload.isMultipartContent(inRequest)) {
			return new MultiPartInspector(inRequest);
		}
		return new ClassicInspector(inRequest);
	}

	/**
	 * Checks whether the request to inspect contains the specified parameter.
	 * 
	 * @param inName String name of the parameter to look up
	 * @return boolean <code>true</code> if the wrapped request contains the specified parameter
	 */
	public boolean containsParameter(String inName) {
		return inspector.containsParameter(inName);
	}

	/**
	 * Returns the specified parameter's value from the wrapped request.
	 * 
	 * @param inName String name of the parameter to return the value
	 * @return String the parameter's value or <code>null</code>, if the parameter doesn't exist in the request
	 */
	public String getParameterValue(String inName) {
		return inspector.getParameterValue(inName);
	}

	/**
	 * Extracts all parameters except the specified out of the servlet request
	 * and adds them to the context.
	 *
	 * @param inContext	org.hip.kernel.servlet.Context The context set for the clients session
	 * @param inExcept String the name of the parameter to ignore
	 */
	public void addParametersToContext(Context inContext, String inExcept) {
		inspector.addParametersToContext(inContext, inExcept);
	}

	/**
	 * Convenience method: checks the passed request for a parameter with the specified name. 
	 * 
	 * @param inRequest HttpServletRequest
	 * @param inName String parameter name.
	 * @return boolean
	 */
	public static boolean containsParameter(HttpServletRequest inRequest, String inName) {
		for (Enumeration<?> lParameters = inRequest.getParameterNames(); lParameters.hasMoreElements();) {
			if (lParameters.nextElement().equals(inName)) {
				return true;
			}
		}
		return false;
	}
	
// --- inner classes ---
	
	private static interface IInspector {
		/**
		 * Parse the wrapped request.
		 * @throws RequestException 
		 */
		public void parse() throws RequestException;
		
		/**
		 * Checks whether the request to inspect contains the specified parameter.
		 * 
		 * @param inName String name of the parameter to look up
		 * @return boolean <code>true</code> if the wrapped request contains the specified parameter
		 */
		public boolean containsParameter(String inName);

		/**
		 * Returns the specified parameter's value from the wrapped request.
		 * 
		 * @param inName String
		 * @return String the parameter's value or <code>null</code>, if the parameter doesn't exist in the request
		 */
		public String getParameterValue(String inName);
		
		public void addParametersToContext(Context inContext, String inExcept);
	}
	
	private static class ClassicInspector implements IInspector {
		private HttpServletRequest request;
		private Map<String, String[]> parameters = new HashMap<String, String[]>();
		
		ClassicInspector(HttpServletRequest inRequest) {
			request = inRequest;
		}
		
		@SuppressWarnings("unchecked")
		public void parse() {
			parameters = request.getParameterMap();
		}

		public boolean containsParameter(String inName) {
			return parameters.containsKey(inName);
		}

		public String getParameterValue(String inName) {
			String[] outValue = parameters.get(inName);
			return outValue == null ? null : outValue[0];
		}

		public void addParametersToContext(Context inContext, String inExcept) {
			NameValueList lList = new DefaultNameValueList();
			for (String lKey : parameters.keySet()) {
				if (lKey.equals(inExcept)) continue;
				
				String[] lValues = parameters.get(lKey);
				lList.add(new DefaultNameValue(lList, lKey, lValues.length == 1 ? lValues[0] : lValues));
			}
			inContext.setParameters(lList);
		}
	}
	
	private static class MultiPartInspector implements IInspector {
		private HttpServletRequest request;
		private Map<String, FileItem> uploadParameters = new HashMap<String, FileItem>();
		private Map<String, Collection<String>> formParameters = new HashMap<String, Collection<String>>();
		
		MultiPartInspector(HttpServletRequest inRequest) {
			request = inRequest;
		}
		
		@SuppressWarnings("unchecked")
		public void parse() throws RequestException {
			FileItemFactory lFactory = new DiskFileItemFactory();
			ServletFileUpload lUpload = new ServletFileUpload(lFactory);
			long lMegas = DFLT_SIZE;
			try {
				lMegas = Long.parseLong(VSys.getVSysProperties().getProperty(KEY_MAX_SIZE));
			} 
			catch (IOException exc) {
				//intentionally left empty
			}
			lUpload.setFileSizeMax(lMegas * MEGA);
			try {
				List<FileItem> lItems = lUpload.parseRequest(request);
				for (FileItem lItem : lItems) {
					String lFieldName = lItem.getFieldName();
					if (lItem.isFormField()) {
						Collection<String> lValues = formParameters.get(lFieldName);
						if (lValues == null) {
							lValues = new Vector<String>();
						}
						lValues.add(lItem.getString(AbstractRequestHandler.ENCODING));						
						formParameters.put(lFieldName, lValues);
					}
					else {
						uploadParameters.put(lFieldName, lItem);
					}
				}
			}
			catch (FileUploadException exc) {
				throw new RequestException(exc.getMessage());
			} 
			catch (UnsupportedEncodingException exc) {
				throw new RequestException(exc.getMessage());
			}
		}
		
		public boolean containsParameter(String inName) {
			return formParameters.containsKey(inName);
		}
		
		public String getParameterValue(String inName) {
			Collection<String> outValue = formParameters.get(inName);
			if (outValue == null) return null;
			return outValue.iterator().next();
		}

		public void addParametersToContext(Context inContext, String inExcept) {
			NameValueList lParameters = new DefaultNameValueList();
			for (String lKey : formParameters.keySet()) {
				if (lKey.equals(inExcept)) continue;
				
				Collection<String> lValues = formParameters.get(lKey);
				lParameters.add(new DefaultNameValue(lParameters, lKey, lValues.size() == 1 ? lValues.iterator().next() : lValues.toArray(new String[0])));
			}
			inContext.setParameters(lParameters);
			
			NameValueList lFileItems = new DefaultNameValueList();
			for (String lKey : uploadParameters.keySet()) {
				lFileItems.add(new DefaultNameValue(lParameters, lKey, new org.hip.kernel.servlet.impl.FileItem(uploadParameters.get(lKey))));				
			}
			inContext.setFileItems(lFileItems);
		}
	}

}

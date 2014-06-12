/*
	This package is part of the servlet framework used for the application VIF.
	Copyright (C) 2001, Benno Luthiger

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
package org.hip.kernel.servlet.impl;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import javax.servlet.ServletOutputStream;

import org.hip.kernel.servlet.Context;
import org.hip.kernel.servlet.HtmlView;
import org.hip.kernel.servlet.IPage;
import org.hip.kernel.servlet.RequestException;

/**
 * 	<p>Baseclass of all html pages. A html page is a container of html views. 
 *  You can add different html-views or pages (-> composite-pattern) to this container.</p>
 *
 *  <p>The HTML representation of this page can be showed in a browser.
 *  It builds a html-body arround the HTML-representations of the views added to this page.</p>
 *
 *  <p>You can set links to Stylesheets (CSS) used in this page.</p>
 *
 *	@author	Benno Luthiger
 */
abstract public class AbstractHtmlPage extends AbstractHtmlView implements IPage {
	//class attributes
	protected static String HTML_BEGIN	= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n";
	protected static String HTML_END	= "</html>";

	protected static String HEAD_BEGIN	= "<head>\n<meta http-equiv=\"content-type\" content=\"text/html;charset=utf8\" />\n";
	protected static String HEAD_TITLE	= "<title>VIF</title>\n";
	protected static String HEAD_END	= "</head>\n";

	protected static String BODY_BEGIN_1	= "<body bgcolor='#FFFFFF' text='#505050' onLoad=";
	protected static String BODY_BEGIN_2	= ">";
	protected static String BODY_END		= "</body>";		
	
	//instance attributes
	private Vector<HtmlView> views = null;
	private String errorMessage = "";
	private String statusMessage = "";
	private String onLoad = "";
	private String htmlHead = "";
	private CssLinkList cssLinks = null;
	private ScriptLinkList scriptLinks = null;
	
	/**
	 * AbstractHtmlPage default constructor.
	 *
	 */
	public AbstractHtmlPage() {
		this(null);
	}
	
	/**
	 * AbstractHtmlPage constructor with specified context.
	 *
	 * @param inContext org.hip.kernel.servlet.Context
	 */
	public AbstractHtmlPage(Context inContext) {
		super(inContext);
	}
	
	/**
	 * Adds a new html-view to this page.
	 *
	 * @param inView org.hip.kernel.servlet.HtmlView
	 */ 
	public void add(HtmlView inView) {
		this.getViews().addElement(inView);
	}
	
	/**
	 * Returns begin of this page as html-string. Contains stylesheet-include and onLoad-script.
	 *
	 * @return java.lang.String
	 */ 
	protected String createBegin() {
		StringBuffer outBegin = new StringBuffer(HTML_BEGIN);
		outBegin.append(HEAD_BEGIN);
		outBegin.append(getTitle()).append(getHtmlHead()).append(cssLinks()).append(scriptLinks());
		outBegin.append(HEAD_END).append(BODY_BEGIN_1).append('"').append(getOnLoad()).append('"').append(BODY_BEGIN_2);
		return new String(outBegin);
	}

	/**
	 * Returns end of this page as html-string.
	 * 
	 * @return java.lang.String
	 * @param inString java.lang.String
	 */
	protected String createEnd() {
		return BODY_END + HTML_END;
	}
	
	/**
	 * Returns a list of CssLink.
	 * 
	 * @return CssLinkList
	 */
	protected CssLinkList cssLinks() {
		if (cssLinks == null) {			
			cssLinks = new CssLinkList();
		}
		return cssLinks;
	}
	
	/**
	 * Returns a list of ScriptLink.
	 * 
	 * @return ScriptLinkList
	 */
	protected ScriptLinkList scriptLinks() {
		if (scriptLinks == null) {
			scriptLinks = new ScriptLinkList();
		}
		return scriptLinks;
	}
	
	/**
	 * Returns additional html code for this page's html head.
	 * 
	 * @return java.lang.String
	 */
	protected String getHtmlHead() {
		return htmlHead;
	}
	
	/**
	 * Sets the style definitions for the page.
	 * 
	 * @param inCssStyle java.lang.String
	 * @deprecated Use method setHeadHtml(String) instead.
	 */
	public void setCssStyle(String inCssStyle) {
		htmlHead = inCssStyle;
	}
	
	/**
	 * The html code passed will be added to the <head/> part of the page's html.
	 * 
	 * @param inHtml java.lang.String The html code to be written in the page's html.
	 */
	public void setHeadHtml(String inHtml) {
		htmlHead = inHtml;
	}
	
	/**
	 * Returns the title of the page.
	 * <pre><title>getTitle()</title></pre>
	 * 
	 * @return java.lang.String
	 */
	protected String getTitle() {
		return HEAD_TITLE;
	}
	
	/**
	 * Returns vector of views (lazy initializing).
	 *  
	 * @return java.util.Vector
	 */
	protected Vector<HtmlView> getViews() {
		if (views == null) {	
			views = new Vector<HtmlView>(3);
		}
		return views;
	}
	
	/**
	 * Returns a list of CssLink.
	 * 
	 * @return CssLinkList
	 */
	public CssLinkList getCssLinks() {
		return cssLinks();
	}
	
	/**
	 * Returns a list of ScriptLink.
	 * 
	 * @return ScriptLinkList
	 */
	public ScriptLinkList getScriptLinks() {
		return scriptLinks();
	}
	
	/**
	 * Returns the error message set to this page.
	 * Returns an empty string if not set.
	 *
	 * @return java.lang.String
	 */
	public String getErrorMessage() {
		return errorMessage.trim();
	}
	
	/**
	 * Returns script set for the onLoad-call in the body of this pages html representation.
	 *
	 * @return java.lang.String
	 */
	public String getOnLoad() {
		return onLoad;
	}
	
	/**
	 * Returns the status message set to this page.
	 * Returns an empty string if not set.
	 *
	 * @return java.lang.String
	 */
	public String getStatusMessage() {
		return statusMessage.trim();
	}
	
	private boolean hasMessages() {
		return  (!"".equals(getErrorMessage())) ||
				(!"".equals(getStatusMessage()));
	}
	
	/**
	 * Add the message information to all views.
	 * It's up the the views how to handle this information.
	 *
	 * @return boolean true, if the messages have been included
	 * @param inStatus java.lang.String
	 * @param inError java.lang.String
	 */
	private boolean includeMessagesToElements(String inStatus, String inError) {
		//pre: inStatus and inError not empty
		if ("".equals(inStatus + inError))
			return true;
			
		boolean outCouldInclude = false;
		for (HtmlView lView : getViews()) {
			if (lView.getTransformer() != null) {
				outCouldInclude = true;
				if (!"".equals(inStatus)) {
					lView.getTransformer().includeMessage(inStatus);
				}
				if (!"".equals(inError)) {
					lView.getTransformer().includeErrorMessage(inError);
				}
			}
		}
		return outCouldInclude;
	}
	
	/**
	 * Writes html-representation of all views in this page to the servlet's response writer.
	 * 
	 * @param inWriter PrintWriter
	 * @param inSessionID String
	 * @throws RequestException
	 */
	private void renderElementsToWriter(PrintWriter inWriter, String inSessionID) throws RequestException {
		for (HtmlView lView : getViews()) {
			lView.renderToWriter(inWriter, inSessionID);
		}
	}
	
	/**
	 * Writes the view as html-String to the passed servlet output stream.<br/>
	 * <b>Note:</b> Use this method to output bit streams.<br/> 
	 * To render the view with the correct encodings set, use <code>renderToWriter(PrintWriter, String)</code> instead.
	 *
	 * @param inStream javax.servlet.ServletOutputStream
	 * @param inSessionID java.lang.String
	 * @throws org.hip.kernel.servlet.RequestException
	 */
	public void renderToStream(ServletOutputStream inStream, String inSessionID) throws RequestException {
		try {
			renderElementsToWriter(new PrintWriter(new OutputStreamWriter(inStream, "")), inSessionID);
		} 
		catch (UnsupportedEncodingException exc) {
			new Error("Something went wrong in AbstractHtmlPage:renderToStream(), while writting the html-page to the servlet-outputstream" + exc);
		}
	}
	
	/**
	 * Writes the view as html-String to the passed servlet's response writer.<br />
	 * The error-message will be added to the XML before rendering and then cleared.<br />
	 * Using the servlet's writer to stream the view, you can use <code>ServletResponse.setCharacterEncoding(ENCODING)</code> to set the view's encoding correctly.
	 * 
	 * @param inWriter PrintWriter
	 * @param inSessionID String
	 * @throws RequestException
	 */	
	public void renderToWriter(PrintWriter inWriter, String inSessionID) throws RequestException {
		inWriter.println(createBegin());
		boolean lHandledMessages = true;
		if (hasMessages()) {
			lHandledMessages = includeMessagesToElements(getStatusMessage(), getErrorMessage());
		}
		
		this.renderElementsToWriter(inWriter, inSessionID);
		
		if (!lHandledMessages) {
			//either there are no messages or they havn't written to the output stream yet
			//(because there was no XSL to handle them) so add them to the HTML view now
			if (!"".equals(getStatusMessage()))
				inWriter.println("<b><font face='Arial' color='#0000cc' size='3'>"+getStatusMessage()+"</font></b>");
			if (!"".equals(getErrorMessage()))
				inWriter.println("<b><font face='Arial' color='#cc0000' size='3'>"+getErrorMessage()+"</font></b>");				
		}
		//clear messages
		errorMessage = "";
		statusMessage = "";		
		
		inWriter.println(createEnd());
	}
	
	/**
	 * Sets a CssLink for this html page. 
	 * Previous set links will be overwritten.
	 * 
	 * @param inCssLink CssLink
	 */
	public void setCssLink(CssLink inCssLink) {
	
		//pre: parameter not null 
		if (inCssLink == null){
			return;
		}
		cssLinks().addCssLink(inCssLink);
	}
	
	/**
	 * Sets a list of CssLink for this html page. 
	 * Previous set links will be overwritten.
	 * 
	 * @param inCssLinks CssLinkList
	 */
	public void setCssLinks(CssLinkList inCssLinks) {
	
		//pre: parameter not null 
		if (inCssLinks == null)
			return;
		cssLinks = inCssLinks;
	}
	
	/**
	 * Sets a javascript link to this html page.
	 * 
	 * @param inScriptLink ScriptLink
	 */
	public void setScriptLink(ScriptLink inScriptLink) {
	
		//pre: parameter not null 
		if (inScriptLink == null){
			return;
		}
		scriptLinks().addScriptLink(inScriptLink);
	}
	
	/**
	 * Sets a list of CssLink for this html page. 
	 * Previous set links will be overwritten.
	 * 
	 * @param inScriptLinks ScriptLinkList
	 */
	public void setScriptLinks(ScriptLinkList inScriptLinks) {
	
		//pre: parameter not null 
		if (inScriptLinks == null)
			return;
		scriptLinks = inScriptLinks;
	}
	
	/**
	 * Sets ErrorMessage which will be added to the content
	 * of this page. After sending the html-representation of this page to the client, including the
	 * error-message, the error-message will be cleared and has to be set again if needed.
	 *
	 * @param inErrorMessage java.lang.String
	 */
	public void setErrorMessage(String inErrorMessage) {
	
		//pre: parameter not null
		if (inErrorMessage == null)
			return;
	
		errorMessage = inErrorMessage;	
	}
	
	/**
	 * Sets a command in the onLoad-tag of this pages body.
	 *
	 * @param inOnLoadCmd java.lang.String
	 */
	public void setOnLoad(String inOnLoadCmd) {
		
		//pre: parameter not null
		if (inOnLoadCmd == null)
			return;
	
		onLoad=inOnLoadCmd;
	}
	
	/**
	 * Sets status-message which will be added to the content
	 * of this page. After sending the html-representation of this page to the client, including the
	 * status-message, the status-message will be cleared and has to be set again if needed.
	 *
	 * @param inMessage java.lang.String
	 */
	public void setStatusMessage(String inMessage) {
		
		//pre: parameter not null
		if (inMessage == null)
			return;
	
		statusMessage = inMessage;		
	}
	
	/**
	 * Clears all status messages from the views.
	 */
	public void clearStatusMessage() {
		for (HtmlView lView : getViews()) {
			if (lView.getTransformer() != null) {
				lView.getTransformer().clearMessages();
			}
		}
	}
	
	/**
	 * Clears all error messages from the views.
	 */
	public void clearErrorMessage() {
		for (HtmlView lView : getViews()) {
			if (lView.getTransformer() != null) {
				lView.getTransformer().clearErrorMessages();
			}
		}
	}
	

	/**
	 * Two pages are equal all views they contain are equal.
	 */
	public boolean equals(Object inObject) {
		if (this == inObject)
			return true;
		if (inObject == null)
			return false;
		if (getClass() != inObject.getClass())
			return false;
		final AbstractHtmlPage lOther = (AbstractHtmlPage)inObject;
		if (views == null) {
			if (lOther.views != null)
				return false;
		} 
		else {
			Vector<HtmlView> lOtherViews = lOther.views;
			if (lOtherViews == null)
				return false;
			
			if (views.size() != lOtherViews.size()) 
				return false;
			
			int i = 0;
			for (HtmlView lView : views) {
				if (!lView.equals(lOtherViews.get(i++)))
					return false;
			}
		}
		return true;
	}	
}

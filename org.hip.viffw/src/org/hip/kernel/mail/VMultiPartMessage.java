package org.hip.kernel.mail;

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

//imports
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.MessagingException;

import org.hip.kernel.servlet.HtmlView;
import org.hip.kernel.servlet.impl.AbstractHtmlView;
import org.hip.kernel.util.Debug;

/**
 * This mail message can be used to send mime messages with 
 * multipart content.
 *
 * @author: Benno Luthiger
 */
public class VMultiPartMessage {

	/** content of this message */
	private MimeMultipart content;

	public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain; charset=UTF-8";
	public static final String CONTENT_TYPE_TEXT_HTML = "text/html; charset=UTF-8";
	
	private static final String SUBTYPE_MIXED = "mixed";
	private static final String SUBTYPE_ALTERNATIVE = "alternative";
	
	private InternetAddress[] toAddresses;
	private InternetAddress[] ccAddresses;
	private InternetAddress[] bccAddresses;
	private InternetAddress fromAddress;
	private String subject = "";
	
	/**
	 * VMultiPartMessage default constructor.
	 */
	public VMultiPartMessage() {
		super();
	}
	
	/**
	 * Adds the specified html String to the content of the message
	 * 
	 * @param inHtmlString java.lang.String
	 */
	public void addHtmlPart(String inHtmlString)  throws MessagingException{
	   MimeBodyPart lBodyPart = new MimeBodyPart();
	   lBodyPart.setContent(inHtmlString, CONTENT_TYPE_TEXT_HTML);
	   getContent(SUBTYPE_ALTERNATIVE).addBodyPart(lBodyPart);
	   
	}
	
	/**
	 * Adds the specified plain text String to the content of the message
	 * 
	 * @param inPlainText java.lang.String
	 */
	public void addPart(String inPlainText) throws MessagingException {	
	   MimeBodyPart lBodyPart = new MimeBodyPart();
	   lBodyPart.setContent(inPlainText, CONTENT_TYPE_TEXT_PLAIN);
	   getContent().addBodyPart(lBodyPart);
	}
	
	/**
	 * Adds the specified HtmlView to the content of the message
	 * 
	 * @param inHtmlView org.hip.kernel.servlet.HtmlView
	 */
	public void addPart(HtmlView inHtmlView) throws MessagingException {
	   MimeBodyPart lBodyPart = new MimeBodyPart();
	   if (inHtmlView instanceof AbstractHtmlView){
		    lBodyPart.setContent(((AbstractHtmlView)inHtmlView).getHTMLString(),CONTENT_TYPE_TEXT_HTML)	;
	   }
	   else {
		   lBodyPart.setContent(inHtmlView.toString(),CONTENT_TYPE_TEXT_HTML);
	   }
	   getContent().addBodyPart(lBodyPart);
	}
	
	/**
	 * Returns array with addresses of the blind carbon copy recipiants 
	 * 
	 * @return javax.mail.internet.InternetAddress[]
	 */
	public InternetAddress[] getBccAddresses() {
		return bccAddresses;
	}
	
	/**
	 * Returns array with addresses of the carbon copy recipiants 
	 * 
	 * @return javax.mail.internet.InternetAddress[]
	 */
	public InternetAddress[] getCcAddresses() {
		return ccAddresses;
	}
	
	/**
	 * Gets the content of the prepared message.
	 * 
	 * @return javax.mail.Multipart
	 */
	public Multipart getContent() {
		return getContent(SUBTYPE_MIXED);
	}
	
	private Multipart getContent(String inSubtype) {
		if (content == null) {
			content = new MimeMultipart(inSubtype);
		}
		else {
			if (SUBTYPE_ALTERNATIVE.equals(inSubtype)) {
				//we only switch to "alternative", but not back to "mixed"
				try {
					content.setSubType(inSubtype);
				} catch (MessagingException exc) {
					//intentionally left empty
				}
			}
		}
		return content;
	}
	
	/**
	 * Returns address of the sender. 
	 * 
	 * @return javax.mail.internet.InternetAddress
	 */
	public InternetAddress getFromAddress() {
		return fromAddress;
	}
	
	/**
	 * Returns subject string set for this mail message.
	 * 
	 * @return java.lang.String
	 */
	public String getSubject() {
		return subject;
	}
	
	/**
	 * Returns array with addresses of the primary recipiants 
	 * 
	 * @return javax.mail.internet.InternetAddress[]
	 */
	public InternetAddress[] getToAddresses() {
		return toAddresses;
	}
	
	/**
	 * Sets array with addresses of blind carbon copy recipients.
	 * 
	 * @param inAddresses javax.mail.internet.InternetAddress[]
	 */
	public void setBccAddresses(InternetAddress[] inAddresses) {
		bccAddresses = inAddresses;
	}
	/**
	 * Sets array with addresses of carbon copy recipients.
	 * 
	 * @param inAddresses javax.mail.internet.InternetAddress[]
	 */
	public void setCcAddresses(InternetAddress[] inAddresses) {
		ccAddresses = inAddresses;
	}
	
	/**
	 * Sets address of the sender.
	 * 
	 * @param inAddresses javax.mail.internet.InternetAddress[]
	 */
	public void setFromAddress(InternetAddress inAddress) {
		fromAddress = inAddress;
	}
	
	/**
	 * Sets the subject for this mail message.
	 * 
	 * @param inSubject java.lang.String
	 */
	public void setSubject(String inSubject) {
		subject = inSubject;	
	}
	
	/**
	 * Sets array with addresses of primary recipients.
	 * 
	 * @param inAddresses javax.mail.internet.InternetAddress[]
	 */
	public void setToAddresses(InternetAddress[] inAddresses) {
		toAddresses = inAddresses;
	}
	
	/**
	 * @param java.lang.String
	 */
	public String toString() {
		return Debug.classMarkupString(this, "");
	}
}

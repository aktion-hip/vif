package org.hip.kernel.mail.test;

import javax.mail.internet.InternetAddress;

import org.hip.kernel.mail.MailClient;
import org.hip.kernel.mail.VMultiPartMessage;
import org.junit.Test;

/**
 * 
 * @author: Benno Luthiger
 */
public class MailClientTest {

	@Test
	public void testSendMail() throws Exception {
		String lTo = "lbenno@openlu.ethz.ch";
		String lFrom = "benno.luthiger@id.ethz.ch";
//		String lBody = "Testmail von James";
		String lBodyHtml = "Testmail f�r James</br>Bitte <a href=\"http://www.aktion-hip.ch/\">antworten</a>";
		String lSubject = "[VIF] Mail Test 4";
//			InternetAddress lAddress = new InternetAddress();
		InternetAddress lAddressFrom = new InternetAddress(lFrom);
		VMultiPartMessage lMessage = new VMultiPartMessage();
		lMessage.setSubject(lSubject);
		lMessage.setToAddresses(InternetAddress.parse(lTo));
//			lMessage.addPart(lBody);
		lMessage.addHtmlPart(lBodyHtml);
		lMessage.setFromAddress(lAddressFrom);
		MailClient lMailer = new MailClient();
		lMailer.sendMail(lMessage);
		System.out.println("Testmail sent.");
	}
}

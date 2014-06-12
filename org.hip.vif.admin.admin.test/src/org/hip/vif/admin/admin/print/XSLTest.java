package org.hip.vif.admin.admin.print;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import junit.framework.Assert;

import org.hip.kernel.servlet.ISourceCreatorStrategy;
import org.hip.kernel.util.TransformerProxy;
import org.hip.kernel.util.XMLRepresentation;
import org.hip.vif.admin.admin.Messages;
import org.hip.vif.core.TestApplication;
import org.hip.vif.core.interfaces.IMessages;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * @author Luthiger
 * Created: 01.01.2012
 */
public class XSLTest {
	private static final String NL = System.getProperty("line.separator");
	private static final String INDENT = "                ";
	
	private static final String FOLDER_XSL = "xsl";
	private static final String XSL_GROUP = "print_odtGroup.xsl";
	private static final String XSL_QUESTION = "print_odtQuestion.xsl";
	private static final String XSL_COMPLETION = "print_odtCompletion.xsl";
	private static final String XSL_BIBLIOGRAPHY = "print_odtBibliography.xsl";
	
	private static final String FOLDER_XML = "xml";
	private static final String XML_GROUP = "group.xml";
	private static final String XML_QUESTION1 = "question1.xml";
	private static final String XML_QUESTION2 = "question2.xml";
	private static final String XML_COMPLETION = "completion.xml";
	private static final String XML_BIBLIOGRAPHY = "biblio.xml";
		
	private static IMessages messages;
	
	@BeforeClass
	public static void init() {
		TestApplication.initialize(Locale.GERMAN);
		messages = new Messages();
	}

	@Test
	public final void testGroup() throws Exception {
		ByteArrayOutputStream lResult = new ByteArrayOutputStream();
		
		TransformerProxy lTransformer = new TransformerProxy(new XSL(XSL_GROUP), new XMLRepresentation(getXML(XML_GROUP)), 
				createStyleSheetParamter("GroupLbl", getMessage("org.hip.vif.msg.print.lbl.group")));
		lTransformer.renderToStream(lResult, "");
		
		Document lODF = TestViewHelper.getXML(lResult.toString(), true);
		XPath lXPath = TestViewHelper.getXPath();
		lXPath.setNamespaceContext(new ODFNameSpace());
		Assert.assertEquals("!org.hip.vif.msg.print.lbl.group! \"Test Vaadin\"", lXPath.evaluate("//text:h", lODF, XPathConstants.STRING));
		Assert.assertEquals("Neue Gruppe zu Testzwecken.", lXPath.evaluate("//text:p", lODF, XPathConstants.STRING));
	}
	
	@Test
	public void testQuestion() throws Exception {
		ByteArrayOutputStream lResult = new ByteArrayOutputStream();
		
		HashMap<String, Object> lParameters = createStyleSheetParamter("QuestionLbl", getMessage("org.hip.vif.msg.print.lbl.question"));
		lParameters.put("RemarkLbl", getMessage("org.hip.vif.msg.print.lbl.remark"));
		lParameters.put("AuthorLbl", getMessage("org.hip.vif.msg.print.lbl.author"));
		lParameters.put("ReviewerLbl", getMessage("org.hip.vif.msg.print.lbl.reviewer"));
		lParameters.put("StateLbl", getMessage("org.hip.vif.msg.print.lbl.state"));
		lParameters.put("StateOpenLbl", getMessage("org.hip.vif.msg.print.lbl.state.open"));
		lParameters.put("StateAnsweredRequestedLbl", getMessage("org.hip.vif.msg.print.lbl.state.requested"));
		lParameters.put("StateAnsweredLbl", getMessage("org.hip.vif.msg.print.lbl.state.answered"));
		TransformerProxy lTransformer = new TransformerProxy(new XSL(XSL_QUESTION), 
				new XMLRepresentation(getXML(XML_QUESTION1)), 
				lParameters);
		lTransformer.renderToStream(lResult, "");

		Document lODF = TestViewHelper.getXML(lResult.toString(), true);
		XPath lXPath = TestViewHelper.getXPath();
		lXPath.setNamespaceContext(new ODFNameSpace());
		
		Assert.assertEquals("!org.hip.vif.msg.print.lbl.question! 45:1", lXPath.evaluate("//text:h", lODF, XPathConstants.STRING));
		Assert.assertEquals("Wofür ein neues VIF?", lXPath.evaluate("//text:p[1]", lODF, XPathConstants.STRING));
		Assert.assertEquals("!org.hip.vif.msg.print.lbl.remark!:", lXPath.evaluate("//text:p[2]/text:span", lODF, XPathConstants.STRING));
		Assert.assertEquals("Wegen Vaadin? Reicht das als Grund oder ist das nur ein Vorwand zum\n" + INDENT + "Testen?", lXPath.evaluate("//text:p[3]", lODF, XPathConstants.STRING));
		Assert.assertEquals("!org.hip.vif.msg.print.lbl.state!:", lXPath.evaluate("//text:p[4]/text:span[1]", lODF, XPathConstants.STRING));
		Assert.assertEquals("!org.hip.vif.msg.print.lbl.state.open!", lXPath.evaluate("//text:p[4]/text:span[2]", lODF, XPathConstants.STRING));
		Assert.assertEquals("!org.hip.vif.msg.print.lbl.author!:", lXPath.evaluate("//text:p[5]/text:span[1]", lODF, XPathConstants.STRING));
		Assert.assertEquals("Benno Luthiger (30.12.2011)", lXPath.evaluate("//text:p[5]/text:span[2]", lODF, XPathConstants.STRING));
	}
	
	@Test
	public void testQuestion2() throws Exception {
		ByteArrayOutputStream lResult = new ByteArrayOutputStream();
		
		HashMap<String, Object> lParameters = createStyleSheetParamter("QuestionLbl", getMessage("org.hip.vif.msg.print.lbl.question"));
		lParameters.put("RemarkLbl", getMessage("org.hip.vif.msg.print.lbl.remark"));
		lParameters.put("AuthorLbl", getMessage("org.hip.vif.msg.print.lbl.author"));
		lParameters.put("ReviewerLbl", getMessage("org.hip.vif.msg.print.lbl.reviewer"));
		lParameters.put("StateLbl", getMessage("org.hip.vif.msg.print.lbl.state"));
		lParameters.put("StateOpenLbl", getMessage("org.hip.vif.msg.print.lbl.state.open"));
		lParameters.put("StateAnsweredRequestedLbl", getMessage("org.hip.vif.msg.print.lbl.state.requested"));
		lParameters.put("StateAnsweredLbl", getMessage("org.hip.vif.msg.print.lbl.state.answered"));
		TransformerProxy lTransformer = new TransformerProxy(new XSL(XSL_QUESTION), 
				new XMLRepresentation(getXML(XML_QUESTION2)), 
				lParameters);
		lTransformer.renderToStream(lResult, "");
		
		Document lODF = TestViewHelper.getXML(lResult.toString(), true);
		XPath lXPath = TestViewHelper.getXPath();
		lXPath.setNamespaceContext(new ODFNameSpace());
		
		System.out.println(lResult.toString());
	}
	
	@Test
	public void testCompletion() throws Exception {
		ByteArrayOutputStream lResult = new ByteArrayOutputStream();
		
		HashMap<String, Object> lParameters = createStyleSheetParamter("CompletionLbl", getMessage("org.hip.vif.msg.print.lbl.completion"));
		lParameters.put("AuthorLbl", getMessage("org.hip.vif.msg.print.lbl.author"));
		lParameters.put("ReviewerLbl", getMessage("org.hip.vif.msg.print.lbl.reviewer"));
		TransformerProxy lTransformer = new TransformerProxy(new XSL(XSL_COMPLETION), 
				new XMLRepresentation(getXML(XML_COMPLETION)), 
				lParameters);
		lTransformer.renderToStream(lResult, "");
		
		Document lODF = TestViewHelper.getXML(lResult.toString(), true);
		XPath lXPath = TestViewHelper.getXPath();
		lXPath.setNamespaceContext(new ODFNameSpace());

		Assert.assertEquals("!org.hip.vif.msg.print.lbl.completion!:", lXPath.evaluate("//text:p[1]/text:span", lODF, XPathConstants.STRING));
		Assert.assertEquals("Vaadin ist ein modernes Framerwork und kann eine Anwendung\n" + INDENT + "modernisieren.", lXPath.evaluate("//text:p[2]", lODF, XPathConstants.STRING));
		Assert.assertEquals("!org.hip.vif.msg.print.lbl.author!:", lXPath.evaluate("//text:p[3]/text:span[1]", lODF, XPathConstants.STRING));
		Assert.assertEquals("Benno Luthiger, ", lXPath.evaluate("//text:p[3]/text:span[2]", lODF, XPathConstants.STRING));
		Assert.assertEquals("!org.hip.vif.msg.print.lbl.reviewer!:", lXPath.evaluate("//text:p[3]/text:span[3]", lODF, XPathConstants.STRING));
		Assert.assertEquals("Group Admin4 (30.12.2011)", lXPath.evaluate("//text:p[3]/text:span[4]", lODF, XPathConstants.STRING));
	}
	
	@Test
	public void testBiblio() throws Exception {
		ByteArrayOutputStream lResult = new ByteArrayOutputStream();

		TransformerProxy lTransformer = new TransformerProxy(new XSL(XSL_BIBLIOGRAPHY), 
				new XMLRepresentation(getXML(XML_BIBLIOGRAPHY)), 
				createStyleSheetParamter("BibliographyLbl", getMessage("org.hip.vif.msg.print.lbl.bibiograpy")));
		lTransformer.renderToStream(lResult, "");

		Document lODF = TestViewHelper.getXML(lResult.toString(), true);
		XPath lXPath = TestViewHelper.getXPath();
		lXPath.setNamespaceContext(new ODFNameSpace());
		
		Assert.assertEquals("!org.hip.vif.msg.print.lbl.bibiograpy!:", lXPath.evaluate("//text:p[1]/text:span", lODF, XPathConstants.STRING));
		Assert.assertEquals("Baumert 2011", lXPath.evaluate("//text:p[2]/text:span", lODF, XPathConstants.STRING));
		Assert.assertEquals("Ueda 2005", lXPath.evaluate("//text:p[3]/text:span", lODF, XPathConstants.STRING));
//		System.out.println(lResult.toString());
//		System.out.println(lXPath.evaluate("//text:p[2]", lODF, XPathConstants.STRING));
	}
	
	private String getXML(String inFileName) throws IOException {
		byte[] lBuffer = new byte[1024];
		StringBuilder out = new StringBuilder();
		
		BufferedInputStream lBuffered = null;
		try {
			lBuffered = new BufferedInputStream(new FileInputStream(String.format("%s/%s", FOLDER_XML, inFileName)));
			while (lBuffered.read(lBuffer) >= 0) {
				out.append(new String(lBuffer).trim());
				lBuffer = new byte[1024];
			}
		}
		finally {
			if (lBuffered != null) lBuffered.close();
		}
		return new String(out).trim();
	}
	
	private HashMap<String, Object> createStyleSheetParamter(String inName, String inValue) {
		HashMap<String, Object> outParameters = new HashMap<String, Object>();
		outParameters.put(inName, inValue);
		return outParameters;
	}
	
	private String getMessage(String inMessageKey) {
		return messages.getMessage(inMessageKey);
	}	
	
// ---
	
	private static class XSL implements ISourceCreatorStrategy {		
		private String xslName;

		XSL(String inFileName) {
			xslName = inFileName;
		}

		public Source createSource() throws IOException {
			return new StreamSource(new FileInputStream(String.format("%s/%s", FOLDER_XSL, xslName)), 
					new File("xsl").getAbsolutePath() + "/");
		}
		
		public String getResourceId() { 
			return xslName;
		}		
	}
	
	private static class ODFNameSpace implements NamespaceContext {
		private static String PREFIX = "text"; //xmlns:text
		private static String URL = "urn:oasis:names:tc:opendocument:xmlns:text:1.0";

		public String getNamespaceURI(String inPrefix) {
			if (PREFIX.equals(inPrefix)) {
				return URL;
			}
			return XMLConstants.NULL_NS_URI;
		}

		public String getPrefix(String inNamespaceURI) {
			if (URL.equals(inNamespaceURI)) {
				return PREFIX;
			}
			return null;
		}

		public Iterator getPrefixes(String inNamespaceURI) {
			return null;
		}
	}

}

package org.hip.vif.groupadmin.views;

import java.util.Locale;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import junit.framework.TestCase;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.servlet.Context;
import org.hip.kernel.servlet.ISourceCreatorStrategy;
import org.hip.kernel.servlet.impl.FullyQualifiedNameStrategy;
import org.hip.kernel.util.XMLRepresentation;
import org.hip.vif.bom.Group;
import org.hip.vif.bom.GroupHome;
import org.hip.vif.bom.Member;
import org.hip.vif.bom.MemberHome;
import org.hip.vif.bom.Question;
import org.hip.vif.bom.Text;
import org.hip.vif.bom.TextHome;
import org.hip.vif.bom.impl.BOMHelper;
import org.hip.vif.bom.impl.JoinAuthorReviewerToTextHome.PublishedText;
import org.hip.vif.bom.impl.TextImpl;
import org.hip.vif.bom.impl.WorkflowAwareContribution;
import org.hip.vif.bom.impl.test.DataHouseKeeper;
import org.hip.vif.search.test.IndexHouseKeeper;
import org.hip.vif.servlets.VIFContext;
import org.hip.vif.test.resources.TestPrintWriter;
import org.hip.vif.test.resources.TestViewHelper;
import org.hip.vif.util.IQueryStrategy;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Luthiger
 * Created: 02.09.2010
 */
public class BibliographyViewTest extends TestCase {
	private static final String SCRIPT_NODE = "<script type=\"text/javascript\">Setup of the JavaScript</script>";
	private static final String NBSP = new String(new byte[] {-62, -96});
	
	DataHouseKeeper data;

	public BibliographyViewTest(String inName) {
		super(inName);
		data = DataHouseKeeper.getInstance();
	}
	
	protected void setUp() throws Exception {
		IndexHouseKeeper.redirectDocRoot(true);
	}

	protected void tearDown() throws Exception {
		data.deleteAllInAll();
		IndexHouseKeeper.deleteTestIndexDir();
	}
	
	public void testViewNew() throws Exception {
		Question lQuestion = (Question) BOMHelper.getQuestionHome().create();
		Group lGroup = (Group) BOMHelper.getGroupHome().create();
		lGroup.set(GroupHome.KEY_ID, new Long(8));
		
		String lBiblioTitle = "This Title";
		String lBiblioAuthor = "This Author";
		BibliographyViewSub2 lView = new BibliographyViewSub2(TestViewHelper.getTestContext(), lBiblioTitle, lBiblioAuthor, lQuestion, lGroup, 
				SCRIPT_NODE, "Editor-Id");
		
		Document lXML = lView.getXML().reveal();
		XPath lXPath = TestViewHelper.getXPath();
		assertEquals("Biblio Title", lBiblioTitle, lXPath.evaluate("/Root/BiblioTitle", lXML, XPathConstants.STRING));
		assertEquals("Biblio Author", lBiblioAuthor, lXPath.evaluate("/Root/BiblioAuthor", lXML, XPathConstants.STRING));
		assertNotNull("Question", lXPath.evaluate("/Root/Question/propertySet", lXML, XPathConstants.NODE));
		assertEquals("setup", "Setup of the JavaScript", lXPath.evaluate("/Root/setup", lXML, XPathConstants.STRING));
		assertEquals("options (number of nodes * 2", 8, ((Node)lXPath.evaluate("/Root/options" ,lXML, XPathConstants.NODE)).getChildNodes().getLength());
	}
	
	/**
	 * Test the new entry form.
	 */
	public void testRenderNew() throws Exception {
		Question lQuestion = (Question) BOMHelper.getQuestionHome().create();
		Group lGroup = (Group) BOMHelper.getGroupHome().create();
		lGroup.set(GroupHome.KEY_ID, new Long(8));
		
		String lBiblioTitle = "This Title";
		String lBiblioAuthor = "This Author";
		TestPrintWriter lWriter = new TestPrintWriter();
		BibliographyViewSub lView = new BibliographyViewSub(TestViewHelper.getTestContext(), lBiblioTitle, lBiblioAuthor, lQuestion, lGroup, "Setup of the JavaScript", "Editor-Id");
		
		lView.renderToWriter(lWriter, "sessionId");
		Document lHTML = lWriter.getXML();
		XPath lXPath = TestViewHelper.getXPath();
		assertEquals("Biblio Title", lBiblioTitle, lXPath.evaluate("//form/table/tr/td/input[@name=\"fldBiblioTitle\"]/@value", lHTML, XPathConstants.STRING));
		assertEquals("Biblio Author", lBiblioAuthor, lXPath.evaluate("//form/table/tr/td/input[@name=\"fldBiblioAuthor\"]/@value", lHTML, XPathConstants.STRING));
		assertEquals("Biblio Year empty", "", lXPath.evaluate("//form/table/tr/td/input[@name=\"fldBiblioYear\"]/@value", lHTML, XPathConstants.STRING));
		assertEquals("Button", "Literatureintrag erzeugen", lXPath.evaluate("//form/table/tr/td/input[@class=\"button\"]/@value", lHTML, XPathConstants.STRING));
		assertEquals("Label Author de", "Autor", lXPath.evaluate("//form/table/tr[2]/td[1]", lHTML, XPathConstants.STRING));

		lWriter = new TestPrintWriter();
		lView = new BibliographyViewSub(TestViewHelper.getTestContext(Locale.ENGLISH.getLanguage()), lBiblioTitle, lBiblioAuthor, lQuestion, lGroup, "Setup of the JavaScript", "Editor-Id");
		lView.renderToWriter(lWriter, "sessionId");
		lHTML = lWriter.getXML();
		lXPath = TestViewHelper.getXPath();
		assertEquals("Button en", "Create bibliography", lXPath.evaluate("//form/table/tr/td/input[@class=\"button\"]/@value", lHTML, XPathConstants.STRING));
		assertEquals("Label Author en", "Author", lXPath.evaluate("//form/table/tr[2]/td[1]", lHTML, XPathConstants.STRING));
		//test readonly attribute of author and year field
		assertEquals("Biblio Author input", "", lXPath.evaluate("//form/table/tr/td/input[@name=\"fldBiblioAuthor\"]/@readonly", lHTML, XPathConstants.STRING));
		assertEquals("Biblio Year input", "", lXPath.evaluate("//form/table/tr/td/input[@name=\"fldBiblioYear\"]/@readonly", lHTML, XPathConstants.STRING));
	}
	
	/**
	 * Test the edit entry form.
	 */
	public void testRenderEdit() throws Exception {
		Locale lLocale = Locale.GERMAN;
		Long lTextID = new Long(5);
		int lVersionID = 0;
		String[] lIds = data.create2Members();
		data.createTextProducer(lTextID, lVersionID, new Long(lIds[0]), true);
		data.createTextProducer(lTextID, lVersionID, new Long(lIds[1]), false);
		
		String lBiblioTitle = "This Title";
		String lBiblioAuthor = "This Author";
		String lBiblioRemarks = "Some remarks";
		String lBiblioYear = "1984";
		Text lText = (Text)BOMHelper.getTextHome().create();
		lText.set(TextHome.KEY_TYPE, new Long(2));
		lText.set(TextHome.KEY_TITLE, lBiblioTitle);
		lText.set(TextHome.KEY_AUTHOR, lBiblioAuthor);
		lText.set(TextHome.KEY_REMARK, lBiblioRemarks);
		lText.set(TextHome.KEY_YEAR, lBiblioYear);
		lText.set(TextHome.KEY_STATE, new Long(WorkflowAwareContribution.S_PRIVATE));
		
		TestPrintWriter lWriter = new TestPrintWriter();
		
		//test view
		BibliographyViewSub lView = new BibliographyViewSub(TestViewHelper.getTestContext(lLocale.getLanguage()), lText, getDownloads(lTextID), 
				getAuthor(lTextID, lVersionID), getReviewer(lTextID, lVersionID), SCRIPT_NODE, "Editor-Id", lText.getOptionsSelected(lLocale));
		
		lView.renderToWriter(lWriter, "sessionId");
		Document lHTML = lWriter.getXML();
		XPath lXPath = TestViewHelper.getXPath();
		assertEquals("Biblio Title", lBiblioTitle, lXPath.evaluate("//form/table/tr/td/input[@name=\"fldBiblioTitle\"]/@value", lHTML, XPathConstants.STRING));
		assertEquals("Biblio Author", lBiblioAuthor, lXPath.evaluate("//form/table/tr/td/input[@name=\"fldBiblioAuthor\"]/@value", lHTML, XPathConstants.STRING));
		assertEquals("Remarks", lBiblioRemarks, lXPath.evaluate("//form/table/tr/td/textarea[@id=\"Editor-Id\"]", lHTML, XPathConstants.STRING));
		assertEquals("Button de", "Speichern", lXPath.evaluate("//form/table/tr/td/input[@class=\"button\"]/@value", lHTML, XPathConstants.STRING));
		
		assertEquals("privat", lXPath.evaluate("//form/table/tr[13]/td[2]", lHTML, XPathConstants.STRING).toString());
		assertEquals("VornameT1"+NBSP+"NameT1", lXPath.evaluate("//form/table/tr[14]/td[2]", lHTML, XPathConstants.STRING).toString());
		assertEquals("VornameT2"+NBSP+"NameT2", lXPath.evaluate("//form/table/tr[15]/td[2]", lHTML, XPathConstants.STRING).toString());
		assertEquals("fldUpload", lXPath.evaluate("//form/table/tr[12]/td[2]/input/@id", lHTML, XPathConstants.STRING).toString());
		assertEquals("file", lXPath.evaluate("//form/table/tr[12]/td[2]/input/@type", lHTML, XPathConstants.STRING).toString());

		lWriter = new TestPrintWriter();
		lLocale = Locale.ENGLISH;
		lView = new BibliographyViewSub(TestViewHelper.getTestContext(lLocale.getLanguage()), lText, getDownloads(lTextID), 
				getAuthor(lTextID, lVersionID), getReviewer(lTextID, lVersionID), SCRIPT_NODE, "Editor-Id", lText.getOptionsSelected(lLocale));
		lView.renderToWriter(lWriter, "sessionId");
		lHTML = lWriter.getXML();
		assertEquals("Button en", "Save", lXPath.evaluate("//form/table/tr/td/input[@class=\"button\"]/@value", lHTML, XPathConstants.STRING));
		assertEquals("entry state en", "private", lXPath.evaluate("//form/table/tr[13]/td[2]", lHTML, XPathConstants.STRING).toString());
		//editor view renders author and year readonly 
		assertEquals("Biblio Author is readonly", "readonly", lXPath.evaluate("//form/table/tr/td/input[@name=\"fldBiblioAuthor\"]/@readonly", lHTML, XPathConstants.STRING));
		assertEquals("Biblio Year is readonly", "readonly", lXPath.evaluate("//form/table/tr/td/input[@name=\"fldBiblioYear\"]/@readonly", lHTML, XPathConstants.STRING));
	}
	
	/**
	 * Test the text form's behavior.
	 */
	public void testForm() throws Exception {
		Locale lLocale = Locale.ENGLISH;
		Long lTextID = new Long(5);
		int lVersionID = 0;
		String[] lIds = data.create2Members();
		data.createTextProducer(lTextID, lVersionID, new Long(lIds[0]), true);
		data.createTextProducer(lTextID, lVersionID, new Long(lIds[1]), false);
		
		String lBiblioTitle = "This Title";
		String lBiblioAuthor = "This Author";
		String lBiblioRemarks = "Some remarks";
		Text lText = (Text)BOMHelper.getTextHome().create();
		lText.set(TextHome.KEY_TYPE, new Long(0));
		lText.set(TextHome.KEY_TITLE, lBiblioTitle);
		lText.set(TextHome.KEY_AUTHOR, lBiblioAuthor);
		lText.set(TextHome.KEY_REMARK, lBiblioRemarks);
		lText.set(TextHome.KEY_STATE, new Long(WorkflowAwareContribution.S_PRIVATE));
		
		TestPrintWriter lWriter = new TestPrintWriter();
		
		//test view
		BibliographyViewSub lView = new BibliographyViewSub(TestViewHelper.getTestContext(lLocale.getLanguage()), lText, getDownloads(lTextID), 
				getAuthor(lTextID, lVersionID), getReviewer(lTextID, lVersionID), SCRIPT_NODE, "Editor-Id", lText.getOptionsSelected(lLocale));
		lView.renderToWriter(lWriter, "sessionId");
		Document lHTML = lWriter.getXML();
		XPath lXPath = TestViewHelper.getXPath();
		assertEquals("label co-authors (type 0)", "Co-Authors", lXPath.evaluate("//form/table/tr/td[@id='lblBiblioChng1']", lHTML, XPathConstants.STRING));
		assertEquals("label publication (type 0)", "Publication", lXPath.evaluate("//form/table/tr/td[@id='lblBiblioChng2']", lHTML, XPathConstants.STRING));
		assertEquals("label place (type 0)", "Place", lXPath.evaluate("//form/table/tr/td[@id='lblBiblioChng3']", lHTML, XPathConstants.STRING));
		assertEquals("selected option 0", "selected", lXPath.evaluate("//form/table/tr/td/select/option[@value=\"0\"]/@selected", lHTML, XPathConstants.STRING));
		assertEquals("selected option 0 value", "0", lXPath.evaluate("//form/table/tr/td/select/option[@selected=\"selected\"]/@value", lHTML, XPathConstants.STRING));

		lWriter = new TestPrintWriter();
		lText.set(TextHome.KEY_TYPE, new Long(1));
		lView = new BibliographyViewSub(TestViewHelper.getTestContext(lLocale.getLanguage()), lText, getDownloads(lTextID), 
				getAuthor(lTextID, lVersionID), getReviewer(lTextID, lVersionID), SCRIPT_NODE, "Editor-Id", lText.getOptionsSelected(lLocale));
		lView.renderToWriter(lWriter, "sessionId");
		lHTML = lWriter.getXML();
		assertEquals("label co-authors (type 1)", "Co-Authors", lXPath.evaluate("//form/table/tr/td[@id='lblBiblioChng1']", lHTML, XPathConstants.STRING));
		assertEquals("label publication (type 1)", "Publication", lXPath.evaluate("//form/table/tr/td[@id='lblBiblioChng2']", lHTML, XPathConstants.STRING));
		assertEquals("label place (type 1)", "Place", lXPath.evaluate("//form/table/tr/td[@id='lblBiblioChng3']", lHTML, XPathConstants.STRING));
		assertEquals("selected option 1", "selected", lXPath.evaluate("//form/table/tr/td/select/option[@value=\"1\"]/@selected", lHTML, XPathConstants.STRING));
		assertEquals("selected option 1 value", "1", lXPath.evaluate("//form/table/tr/td/select/option[@selected=\"selected\"]/@value", lHTML, XPathConstants.STRING));
		
		lWriter = new TestPrintWriter();
		lText.set(TextHome.KEY_TYPE, new Long(2));
		lView = new BibliographyViewSub(TestViewHelper.getTestContext(lLocale.getLanguage()), lText, getDownloads(lTextID), 
				getAuthor(lTextID, lVersionID), getReviewer(lTextID, lVersionID), SCRIPT_NODE, "Editor-Id", lText.getOptionsSelected(lLocale));
		lView.renderToWriter(lWriter, "sessionId");
		lHTML = lWriter.getXML();
		assertEquals("label co-authors (type 2)", "Editors", lXPath.evaluate("//form/table/tr/td[@id='lblBiblioChng1']", lHTML, XPathConstants.STRING));
		assertEquals("label publication (type 2)", "Booktitle", lXPath.evaluate("//form/table/tr/td[@id='lblBiblioChng2']", lHTML, XPathConstants.STRING));
		assertEquals("label place (type 2)", "Place", lXPath.evaluate("//form/table/tr/td[@id='lblBiblioChng3']", lHTML, XPathConstants.STRING));
		assertEquals("selected option 2", "selected", lXPath.evaluate("//form/table/tr/td/select/option[@value=\"2\"]/@selected", lHTML, XPathConstants.STRING));
		assertEquals("selected option 2 value", "2", lXPath.evaluate("//form/table/tr/td/select/option[@selected=\"selected\"]/@value", lHTML, XPathConstants.STRING));
		
		lWriter = new TestPrintWriter();
		lText.set(TextHome.KEY_TYPE, new Long(3));
		lView = new BibliographyViewSub(TestViewHelper.getTestContext(lLocale.getLanguage()), lText, getDownloads(lTextID), 
				getAuthor(lTextID, lVersionID), getReviewer(lTextID, lVersionID), SCRIPT_NODE, "Editor-Id", lText.getOptionsSelected(lLocale));
		lView.renderToWriter(lWriter, "sessionId");
		lHTML = lWriter.getXML();
		assertEquals("label co-authors (type 3)", "Co-Authors", lXPath.evaluate("//form/table/tr/td[@id='lblBiblioChng1']", lHTML, XPathConstants.STRING));
		assertEquals("label publication (type 3)", "Webpage", lXPath.evaluate("//form/table/tr/td[@id='lblBiblioChng2']", lHTML, XPathConstants.STRING));
		assertEquals("label place (type 3)", "Access", lXPath.evaluate("//form/table/tr/td[@id='lblBiblioChng3']", lHTML, XPathConstants.STRING));
		assertEquals("selected option 3", "selected", lXPath.evaluate("//form/table/tr/td/select/option[@value=\"3\"]/@selected", lHTML, XPathConstants.STRING));
		assertEquals("selected option 3 value", "3", lXPath.evaluate("//form/table/tr/td/select/option[@selected=\"selected\"]/@value", lHTML, XPathConstants.STRING));
	}
	
	public void testRenderShowPublished() throws Exception {
		Locale lLocale = Locale.ENGLISH;
		String lOptions = TextImpl.getOptions(lLocale);
		
		String lBiblioTitle = "This Title";
		String lBiblioAuthor = "This Author";
		String lBiblioRemarks = "Some remarks";

		TextHome lTextHome = data.getTextHome();
		String lTextID = data.createText(lBiblioTitle, lBiblioAuthor);
		int lVersionID = 0;
		String[] lIds = data.create2Members();
		data.createTextProducer(new Long(lTextID), lVersionID, new Long(lIds[0]), true);
		data.createTextProducer(new Long(lTextID), lVersionID, new Long(lIds[1]), false);
		
		MemberHome lMemberHome = data.getMemberHome();
		Member lAuthor = lMemberHome.getMember(lIds[0]);
		Member lReviewer = lMemberHome.getMember(lIds[1]);
		
		Text lText = lTextHome.getText(lTextID, lVersionID);
		((WorkflowAwareContribution)lText).doTransition(WorkflowAwareContribution.TRANS_ADMIN_PUBLISH, new Object[] {new Long(lIds[1])});
		PublishedText lTextPublished = BOMHelper.getJoinAuthorReviewerToTextHome().getTextPublished(lTextID);
		
		GeneralDomainObject lTextBO = lTextPublished.getText();
		lTextBO.set(TextHome.KEY_BIBLIO_TYPE, new Long(TextImpl.TextType.BOOK.getTypeValue()));
		lTextBO.set(TextHome.KEY_REMARK, lBiblioRemarks);
		lTextBO.set(TextHome.KEY_PUBLICATION, "www.vif.org");
		
		TestPrintWriter lWriter = new TestPrintWriter();
		
		//test view
		BibliographyViewSub lView = new BibliographyViewSub(TestViewHelper.getTestContext(lLocale.getLanguage()), lTextBO, getDownloads(0l), lAuthor, lReviewer, lOptions);
		lView.renderToWriter(lWriter, "sessionId");
		Document lHTML = lWriter.getXML();
		XPath lXPath = TestViewHelper.getXPath();
		assertEquals("label title", "Title", lXPath.evaluate("//table/tbody/tr[2]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("value title", lBiblioTitle, lXPath.evaluate("//table/tbody/tr[2]/td[2]/b", lHTML, XPathConstants.STRING));
		assertEquals("label author", "Author", lXPath.evaluate("//table/tbody/tr[4]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("value author", lBiblioAuthor, lXPath.evaluate("//table/tbody/tr[4]/td[2]/b", lHTML, XPathConstants.STRING));
		assertEquals("label remarks", "Remarks", lXPath.evaluate("//table/tbody/tr[13]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("value remarks", lBiblioRemarks, lXPath.evaluate("//table/tbody/tr[13]/td[2]", lHTML, XPathConstants.STRING));
		assertEquals("value publication", "www.vif.org", lXPath.evaluate("//table/tbody/tr[7]/td[2]", lHTML, XPathConstants.STRING));
		assertEquals("no url", "", lXPath.evaluate("//table/tbody/tr[7]/td[2]/a/@href", lHTML, XPathConstants.STRING));
		assertEquals("type 0", "Book", lXPath.evaluate("//table/tbody/tr[1]/td[2]", lHTML, XPathConstants.STRING));
	
		lWriter = new TestPrintWriter();
		lTextBO.set(TextHome.KEY_BIBLIO_TYPE, new Long(TextImpl.TextType.ARTICLE.getTypeValue()));
		lView = new BibliographyViewSub(TestViewHelper.getTestContext(lLocale.getLanguage()), lTextBO, getDownloads(0l), lAuthor, lReviewer, lOptions);
		lView.renderToWriter(lWriter, "sessionId");
		lHTML = lWriter.getXML();
		assertEquals("type 1", "Article", lXPath.evaluate("//table/tbody/tr[1]/td[2]", lHTML, XPathConstants.STRING));
		
		lWriter = new TestPrintWriter();
		lTextBO.set(TextHome.KEY_BIBLIO_TYPE, new Long(TextImpl.TextType.CONTRIBUTION.getTypeValue()));
		lView = new BibliographyViewSub(TestViewHelper.getTestContext(lLocale.getLanguage()), lTextBO, getDownloads(0l), lAuthor, lReviewer, lOptions);
		lView.renderToWriter(lWriter, "sessionId");
		lHTML = lWriter.getXML();
		assertEquals("type 2", "Contribution", lXPath.evaluate("//table/tbody/tr[1]/td[2]", lHTML, XPathConstants.STRING));
		
		lWriter = new TestPrintWriter();
		lTextBO.set(TextHome.KEY_BIBLIO_TYPE, new Long(TextImpl.TextType.WEBPAGE.getTypeValue()));
		lView = new BibliographyViewSub(TestViewHelper.getTestContext(lLocale.getLanguage()), lTextBO, getDownloads(0l), lAuthor, lReviewer, lOptions);
		lView.renderToWriter(lWriter, "sessionId");
		lHTML = lWriter.getXML();
		assertEquals("type 3", "Web-Page", lXPath.evaluate("//table/tbody/tr[1]/td[2]", lHTML, XPathConstants.STRING));
		assertEquals("entry's author name", "VornameT1"+NBSP+"NameT1", lXPath.evaluate("//table/tbody/tr[15]/td[1]/a[1]", lHTML, XPathConstants.STRING).toString());
		assertEquals("entry's reviewer name", "VornameT2"+NBSP+"NameT2", lXPath.evaluate("//table/tbody/tr[15]/td[1]/a[2]", lHTML, XPathConstants.STRING).toString());

		//test whether urls are made clickable
		assertEquals("url value www", "www.vif.org", lXPath.evaluate("//table/tbody/tr[7]/td[2]", lHTML, XPathConstants.STRING));
		assertEquals("url http", "http://www.vif.org", lXPath.evaluate("//table/tbody/tr[7]/td[2]/a/@href", lHTML, XPathConstants.STRING));	
		
		lWriter = new TestPrintWriter();
		lTextBO.set(TextHome.KEY_PUBLICATION, "something");
		lView = new BibliographyViewSub(TestViewHelper.getTestContext(lLocale.getLanguage()), lTextBO, getDownloads(0l), lAuthor, lReviewer, lOptions);
		lView.renderToWriter(lWriter, "sessionId");
		lHTML = lWriter.getXML();
		assertEquals("publication value", "something", lXPath.evaluate("//table/tbody/tr[7]/td[2]", lHTML, XPathConstants.STRING));
		assertEquals("no url", "", lXPath.evaluate("//table/tbody/tr[7]/td[2]/a/@href", lHTML, XPathConstants.STRING));	
		
		
		lWriter = new TestPrintWriter();
		lTextBO.set(TextHome.KEY_PUBLICATION, "https://sf.vif.net/");
		lView = new BibliographyViewSub(TestViewHelper.getTestContext(lLocale.getLanguage()), lTextBO, getDownloads(0l), lAuthor, lReviewer, lOptions);
		lView.renderToWriter(lWriter, "sessionId");
		lHTML = lWriter.getXML();
		assertEquals("url text https", "https://sf.vif.net/", lXPath.evaluate("//table/tbody/tr[7]/td[2]", lHTML, XPathConstants.STRING));
		assertEquals("url https", "https://sf.vif.net/", lXPath.evaluate("//table/tbody/tr[7]/td[2]/a/@href", lHTML, XPathConstants.STRING));	
	}
	
	public void testRenderShowVersion() throws Exception {
		Locale lLocale = Locale.ENGLISH;
		String lOptions = TextImpl.getOptions(lLocale);
		
		TestPrintWriter lWriter = new TestPrintWriter();
		
		String lBiblioTitle = "This Title";
		String lBiblioAuthor = "This Author";
		String lBiblioRemarks = "Some remarks";
		Text lText = (Text) data.getTextHome().create();
		lText.set(TextHome.KEY_TITLE, lBiblioTitle);
		lText.set(TextHome.KEY_AUTHOR, lBiblioAuthor);
		lText.set(TextHome.KEY_REMARK, lBiblioRemarks);
		lText.set(TextHome.KEY_STATE, new Long(WorkflowAwareContribution.S_OPEN));
		lText.set(TextHome.KEY_TYPE, new Long(TextImpl.TextType.WEBPAGE.getTypeValue()));
		
		//test view
		BibliographyViewSub lView = new BibliographyViewSub(TestViewHelper.getTestContext(lLocale.getLanguage()), lText, getDownloads(0l), lOptions);
		lView.renderToWriter(lWriter, "sessionId");
		Document lHTML = lWriter.getXML();
		XPath lXPath = TestViewHelper.getXPath();
		assertEquals("label title", "Title", lXPath.evaluate("//table/tbody/tr[2]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("value title", lBiblioTitle, lXPath.evaluate("//table/tbody/tr[2]/td[2]/b", lHTML, XPathConstants.STRING));
		assertEquals("label author", "Author", lXPath.evaluate("//table/tbody/tr[4]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("value author", lBiblioAuthor, lXPath.evaluate("//table/tbody/tr[4]/td[2]/b", lHTML, XPathConstants.STRING));
		assertEquals("label remarks", "Remarks", lXPath.evaluate("//table/tbody/tr[13]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("value remarks", lBiblioRemarks, lXPath.evaluate("//table/tbody/tr[13]/td[2]", lHTML, XPathConstants.STRING));
		assertEquals("type", "Web-Page", lXPath.evaluate("//table/tbody/tr[1]/td[2]", lHTML, XPathConstants.STRING));
	}
	
	private IQueryStrategy getDownloads(final Long inTextID) {
		return new IQueryStrategy() {
			public QueryResult getQueryResult() throws Exception {
				return BOMHelper.getDownloadTextHome().getDownloads(inTextID.toString());
			}
		};
	}

	private IQueryStrategy getAuthor(final Long inTextID, final int inVersion) {
		return new IQueryStrategy() {
			public QueryResult getQueryResult() throws Exception {
				return BOMHelper.getJoinTextToAuthorReviewerHome().getAuthors(inTextID.toString(), inVersion);
			}
		};
	}
	
	private IQueryStrategy getReviewer(final Long inTextID, final int inVersion) {
		return new IQueryStrategy() {
			public QueryResult getQueryResult() throws Exception {
				return BOMHelper.getJoinTextToAuthorReviewerHome().getReviewers(inTextID.toString(), inVersion);
			}
		};
	}
	
// ---
	
	private class BibliographyViewSub extends BibliographyView {
		//new
		public BibliographyViewSub(Context inContext, String inTitle, String inAuthor, Question inQuestion, Group inGroup, 
				String inScriptSetup, String inEditorId) throws GettingException {
			super(inContext, inTitle, inAuthor, inQuestion, inGroup, inScriptSetup, inEditorId);
		}
		//edit
		public BibliographyViewSub(Context inContext, Text inText, IQueryStrategy inDownloads,
				IQueryStrategy inAuthors, IQueryStrategy inReviewers, 
				String inScriptSetup, String inEditorId, String inOptions) throws Exception {
			super(inContext, inText, inDownloads, inAuthors, inReviewers, inScriptSetup, inEditorId, inOptions, new Long(787), true);
		}
		//show published
		public BibliographyViewSub(VIFContext inContext, GeneralDomainObject inText, IQueryStrategy inDownloads, Member inAuthor, Member inReviewer, String inOptions) throws Exception {
			super(inContext, inText, inDownloads, inAuthor, inReviewer, inOptions, false); 
		}
		//show version
		public BibliographyViewSub(VIFContext inContext, Text inText, IQueryStrategy inDownloads, String inOptions) throws Exception {
			super(inContext, inText, inDownloads, inOptions); 
		}
		
		@Override
		protected ISourceCreatorStrategy getSourceStrategy() {
			return new FullyQualifiedNameStrategy(getRelativeXSLName());
		}
		
		private String getRelativeXSLName() {
			return DOCS_ROOT + getSubAppPath() + this.getLanguage() + "/" + getXMLName();
		}
		
		protected String getSubAppPath() {
			return "..\\org.hip.vif.admin.groupadmin\\resources\\";
		}
		
	}
	
	private class BibliographyViewSub2 extends BibliographyViewSub {
		private XMLRepresentation xml;

		public BibliographyViewSub2(Context inContext, String inTitle,
				String inAuthor, Question inQuestion, Group inGroup, String inScriptSetup, String inEditorId) throws GettingException {
			super(inContext, inTitle, inAuthor, inQuestion, inGroup, inScriptSetup, inEditorId);
		}
		
		@Override
		protected void prepareTransformation(XMLRepresentation inXML) {
			super.prepareTransformation(inXML);
			xml = inXML;
		}
		
		public XMLRepresentation getXML() {
			return xml;
		}
		
		@Override
		protected ISourceCreatorStrategy getSourceStrategy() {
			return TestViewHelper.getSourceStrategy();
		}		
	}
}

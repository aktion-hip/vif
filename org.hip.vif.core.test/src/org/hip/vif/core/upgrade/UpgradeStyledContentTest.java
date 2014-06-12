package org.hip.vif.core.upgrade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.hip.kernel.bom.QueryResult;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.interfaces.IVIFUpgrade;
import org.hip.vif.core.service.UpgradeRegistry;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 17.02.2012
 */
public class UpgradeStyledContentTest {
	private static final String NL = System.getProperty("line.separator");
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromQuestion();
	}

	@Test
	public final void testExecute() throws Exception {
		String lTextile1 = "This is an example of a text formatted according to structured text rules." + NL +
			"This is the next paragraph." + NL +
			"Here comes a bulleted list:" + NL +
			"* List element 1" + NL +
			"* List element 2 " + NL +
			"* p. Following paragraph in the same list element." + NL +
			"* Just another paragraph." + NL +
			"* This is the last list element" + NL +
			"*Heres a new paragraph.*" + NL +
			"Let's now test a numbered list:" + NL +
			"# List element 1" + NL +
			"# List element 2 " + NL +
			"# p.  Following paragraph in the same list element." + NL +
			"# This is the +last+ list element" + NL +
			"" + NL +
			"This is the end (of the first example). Was geschieht *damit*?";
		String lTextile2 = "Die _Perspektiven_ von VIF sollen gewisse Entscheidungen in der aktuellen Version vorspuren.";
		
		String lHtml1 = "<p>This is an example of a text formatted according to structured text rules.<br/>This is the next paragraph.<br/>Here comes a bulleted list:</p><ul><li>List element 1</li><li>List element 2 </li><li>p. Following paragraph in the same list element.</li><li>Just another paragraph.</li><li>This is the last list element</li></ul><p><strong>Heres a new paragraph.</strong><br/>Let&#8217;s now test a numbered list:</p><ol><li>List element 1</li><li>List element 2 </li><li>p.  Following paragraph in the same list element.</li><li>This is the <ins>last</ins> list element</li></ol><p>This is the end (of the first example). Was geschieht <strong>damit</strong>?</p>";
		String lHtml2 = "<p>Die <em>Perspektiven</em> von VIF sollen gewisse Entscheidungen in der aktuellen Version vorspuren.</p>";
		
		QuestionHome lHome = data.getQuestionHome();
		data.createQuestion(lTextile1, "1.1");
		data.createQuestion(lTextile2, "1.1.1");
		assertEquals(2, lHome.getCount());
		
		IVIFUpgrade lUpgrader = new UpgradeStyledContent();
		lUpgrader.execute(UpgradeRegistry.getNOOpProgressIndicator());
		Collection<String> lTexts = new ArrayList<String>();
		QueryResult lQuestions = lHome.select();
		while (lQuestions.hasMoreElements()) {
			lTexts.add(lQuestions.nextAsDomainObject().get(QuestionHome.KEY_QUESTION).toString());
		}
		
		assertTrue(lTexts.contains(lHtml1));
		assertTrue(lTexts.contains(lHtml2));
	}
	
	@Test
	public void testExecuteHtml() throws Exception {
		String lNoChange1 = "A text <strong>already</strong> in html should not change <em>at all</em>!"; 
		String lNoChange2 = "<p>A text <strong>already</strong> in html should not change <em>at all</em>!" + NL +
				"A text <strong>already</strong> in html should not change <em>at all</em>!</p>"; 
		
		QuestionHome lHome = data.getQuestionHome();
		data.createQuestion(lNoChange1, "1.1");
		data.createQuestion(lNoChange2, "1.2");
		assertEquals(2, lHome.getCount());
		
		IVIFUpgrade lUpgrader = new UpgradeStyledContent();
		lUpgrader.execute(UpgradeRegistry.getNOOpProgressIndicator());
		Collection<String> lTexts = new ArrayList<String>();
		QueryResult lQuestions = lHome.select();
		while (lQuestions.hasMoreElements()) {
			lTexts.add(lQuestions.nextAsDomainObject().get(QuestionHome.KEY_QUESTION).toString());
		}
		assertTrue(lTexts.contains("<p>" + lNoChange1 + "</p>"));
		assertTrue(lTexts.contains(lNoChange2));
	}

}

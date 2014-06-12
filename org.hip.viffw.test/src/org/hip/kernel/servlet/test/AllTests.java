package org.hip.kernel.servlet.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CssLinkTest.class, HtmlPageTest.class, HtmlViewTest.class,
		PageableHtmlPageTest.class, RequestHandlerTest.class,
		ServletContainerTest.class, ServletRequestHelperTest.class,
		ViewTest.class, XSLQueryResultViewTest.class, XSLViewTest.class })
public class AllTests {

}

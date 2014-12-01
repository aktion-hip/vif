package org.hip.vif.web.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AutoCompleteHelperTest.class, BeanWrapperHelperTest.class, BibliographyFormatterTest.class,
    BOPropertyTest.class, ConfigurationItemTest.class, MemberBeanTest.class, RequestForReviewMailTest.class,
    RequestHandlerTest.class, RichTextSanitizerTest.class })
public class AllTests {

}

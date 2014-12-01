package org.hip.vif.core.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BibliographyHelperTest.class, ContributorTest.class, DBConnectionProberTest.class,
        ExternalObjectDefUtilTest.class, HtmlCleanerTest.class, QuestionStateCheckerTest.class,
        QuestionTreeIteratorTest.class, RatingsHelperTest.class, RolesCheckTest.class, UserSettingsTest.class,
        WorkspaceHelperTest.class, XMLBuilderTest.class })
public class AllTests {

}

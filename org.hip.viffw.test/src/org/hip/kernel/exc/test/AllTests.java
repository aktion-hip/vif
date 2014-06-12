package org.hip.kernel.exc.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DefaultExceptionWriterTest.class, ExceptionDataTest.class,
		ExceptionHandlerTest.class })
public class AllTests {

}

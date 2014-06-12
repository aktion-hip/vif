package org.hip.kernel.servlet.test;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TestPrintWriter extends PrintWriter {

	public TestPrintWriter() {
		super(new StringWriter(), true);
	}
	
	public String getStreamedText() {
		return out.toString();
	}

}

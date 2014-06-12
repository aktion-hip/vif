package org.hip.kernel.exc.test;

import java.io.IOException;

public class TestObject {
	public TestObject() throws IOException {
		throw new IOException("Another test");
	}
}

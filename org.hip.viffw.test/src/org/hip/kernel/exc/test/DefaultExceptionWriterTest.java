package org.hip.kernel.exc.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.hip.kernel.exc.DefaultExceptionWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 01.09.2007
 */
public class DefaultExceptionWriterTest {
	private final static String FILE_NAME = "TestWriteError.log";
	private File errorLog = null;

	@Before
	public void setUp() throws Exception {
		errorLog = createErrorLog();
	}

	@After
	public void tearDown() throws Exception {
		deleteErrorLog(errorLog);
	}

	@Test
	public void testPrintOut() {
		try {
			new TestObject();
		} catch (IOException exc) {
			DefaultExceptionWriter.printOut(this, exc, false);
		}
	}
	
	@Test
	public void testPrintOut2() throws IOException {
		PrintWriter lWriter = new PrintWriter(errorLog);
		try {
			new TestObject();
		}
		catch (IOException exc) {
			DefaultExceptionWriter.printOut(this, exc, true, lWriter);
			lWriter.close();
			String lContent = getFileContent(errorLog);
			assertEquals("exc msg", "java.io.IOException: Another test", lContent.substring(0, 33));
			assertEquals("exc root", "at org.hip.kernel.exc.test.TestObject.<init>(TestObject.java:7)", lContent.substring(34, 97));
		}
		finally {
			lWriter.close();
		}
	}
	
	private String getFileContent(File inFile) throws IOException {
		StringWriter outContent = new StringWriter();
		
		BufferedWriter lWriter = new BufferedWriter(outContent);
		BufferedReader lBuffer = new BufferedReader(new FileReader(inFile));
		String lLine;
		while ((lLine = lBuffer.readLine()) != null) {
			lWriter.write(lLine, 0, lLine.length());
		}
		lBuffer.close();
		lWriter.close();
		return outContent.toString();
	}

	private File createErrorLog() throws IOException {
		File out = new File(FILE_NAME);
		if (!out.exists()) {
			out.createNewFile();
		}
		return out;
	}
	
	private void deleteErrorLog(File inFile) {
		if (inFile == null || !inFile.exists()) return;
		
		if (!inFile.delete()) {
			inFile.deleteOnExit();
		}
	}

}

package org.hip.kernel.servlet.test;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;

/**
 * Utility class to read and write files
 * 
 * @author: Benno Luthiger
 */
public class FileReadWriteUtility {

	/**
	 * Deletes the specified File or, of not possible, empties it.
	 *
	 * @param inFile java.io.File
	 */
	public static void deleteFile(File inFile) {
		//pre
		if (!inFile.exists()) return;
	
		if (!inFile.delete())
			emptyFile(inFile);
	}
	
	/**
	 * Deletes the specified File or, of not possible, empties it.
	 *
	 * @param inFileName java.lang.String
	 */
	public static void deleteFile(String inFileName) {
		File lFileToDelete = new File(inFileName);
		deleteFile(lFileToDelete);
	}
	
	/**
	 * Empties the specified File.
	 *
	 * @param inFile java.io.File
	 */
	public static void emptyFile(File inFile) {
		try {
			FileWriter lWriter = new FileWriter(inFile);
			lWriter.write("");
			lWriter.close();
		}
		catch (Exception exc) {
			System.out.println("FileReadWriteUtility.emptyFile: " + exc.getMessage());
		}
	}
	
	/**
	 * Reads the content of the specified File
	 *
	 * @param inFile java.io.File
	 * @return java.lang.String
	 */
	public static String readFile(File inFile) {
		StringBuffer outContent = new StringBuffer("");
		String lNL = System.getProperty("line.separator");
		FileReader lReader = null;
		BufferedReader lBuffer = null;
		
		if (inFile.exists()) {
			try {
				lReader = new FileReader(inFile);
				lBuffer = new BufferedReader(lReader);
				String lLine;
				while ((lLine = lBuffer.readLine()) != null)
					outContent.append(lLine + lNL);
			}
			catch (Exception exc) {
				System.out.println(exc.getMessage());
			}
			finally {
				try {
					if (lBuffer != null)
						lBuffer.close();
					if (lReader != null)
						lReader.close();
				}
				catch (java.io.IOException exc) {
					System.out.println(exc.getMessage());
				}
			}
		}
			
		return new String(outContent);
	}
	
	/**
	 * Reads the content of the specified File
	 *
	 * @param inFileName java.lang.String
	 * @return java.lang.String
	 */
	public static String readFile(String inFileName) {
		return readFile(new File(inFileName));
	}
	
	/**
	 * Writes the specified text to the File
	 * 
	 * @param inText java.lang.String
	 * @param inFile java.io.File
	 */
	public static void writeFile(String inText, File inFile) {
		try {
			FileWriter lWriter = new FileWriter(inFile);
			lWriter.write(inText);
			lWriter.close();
		}
		catch (Exception exc) {
			System.out.println("FileReadWriteUtility.writeFile: " + exc.getMessage());
		}	
	}
	
	public static void writeFile(String inText, String inFileName) {
		writeFile(inText, new File(inFileName));
	}
}
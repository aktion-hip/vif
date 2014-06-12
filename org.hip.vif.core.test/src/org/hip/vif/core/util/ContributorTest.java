package org.hip.vif.core.util;

import static org.junit.Assert.assertEquals;

import org.hip.kernel.bom.impl.XMLSerializer;
import org.hip.kernel.exc.VException;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Luthiger
 * Created 21.09.2009
 */
public class ContributorTest {
	private final static String NL = System.getProperty("line.separator");
	private final static String EXPECTED1 = NL + 
		"<propertySet>" + NL + 
		"    <IsAuthor>1</IsAuthor>" + NL + 
		"    <FirstName>Adam</FirstName>" + NL + 
		"    <FamilyName>Miller</FamilyName>" + NL + 
		"    <FullName>Adam Miller</FullName>" + NL + 
		"</propertySet>";	
	private final static String EXPECTED2 = NL + 
		"<propertySet>" + NL + 
		"    <IsAuthor>0</IsAuthor>" + NL + 
		"    <FirstName>Jane</FirstName>" + NL + 
		"    <FamilyName>Doe</FamilyName>" + NL + 
		"    <FullName>Jane Doe</FullName>" + NL + 
		"</propertySet>";	
	
	private Contributor author;
	private Contributor reviewer;

	@Before
	public void setUp() throws Exception {
		author = new Contributor("Miller", "Adam", true);
		reviewer = new Contributor("Doe", "Jane", false);
	}

	@Test
	public void testGetFullname() throws VException {
		assertEquals("author", "Adam Miller", author.getFullname());
		assertEquals("reviewer", "Jane Doe", reviewer.getFullname());
	}
	
	@Test
	public void testVisit() throws Exception {
		XMLSerializer lVisitor = new XMLSerializer();
		author.accept(lVisitor);
		assertEquals("author serialized", EXPECTED1, lVisitor.toString());
		
		lVisitor.clear();
		reviewer.accept(lVisitor);
		assertEquals("reviewer serialized", EXPECTED2, lVisitor.toString());
	}

}

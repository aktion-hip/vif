package org.hip.vif.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.hip.kernel.bom.impl.XMLSerializer;
import org.hip.kernel.exc.VException;
import org.junit.Before;
import org.junit.Test;

/** @author Luthiger Created 21.09.2009 */
public class ContributorTest {
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
        final XMLSerializer lVisitor = new XMLSerializer();
        author.accept(lVisitor);
        String lActual = lVisitor.toString();
        assertTrue(lActual.contains("<propertySet>"));
        assertTrue(lActual.contains("<IsAuthor>1</IsAuthor>"));
        assertTrue(lActual.contains("<FirstName>Adam</FirstName>"));
        assertTrue(lActual.contains("<FamilyName>Miller</FamilyName>"));
        assertTrue(lActual.contains("<FullName>Adam Miller</FullName>"));
        assertTrue(lActual.contains("</propertySet>"));

        lVisitor.clear();
        reviewer.accept(lVisitor);
        lActual = lVisitor.toString();
        assertTrue(lActual.contains("<propertySet>"));
        assertTrue(lActual.contains("<IsAuthor>0</IsAuthor>"));
        assertTrue(lActual.contains("<FirstName>Jane</FirstName>"));
        assertTrue(lActual.contains("<FamilyName>Doe</FamilyName>"));
        assertTrue(lActual.contains("<FullName>Jane Doe</FullName>"));
        assertTrue(lActual.contains("</propertySet>"));
    }

}

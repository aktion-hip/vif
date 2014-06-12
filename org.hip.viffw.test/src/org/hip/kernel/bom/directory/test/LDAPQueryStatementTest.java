package org.hip.kernel.bom.directory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.LimitObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.directory.LDAPQueryResult;
import org.hip.kernel.bom.directory.LDAPQueryStatement;
import org.hip.kernel.bom.impl.LimitObjectImpl;
import org.hip.kernel.exc.VException;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 06.07.2007
 */
public class LDAPQueryStatementTest {
	private final static String[] EXPECTED = new String[] {
		"Output: cn=first, uid=111, name=First, sn=First's sn, mail=first@my.org",
		"Output: cn=second, uid=2222, name=Second, sn=Second's sn, mail=second@my.org",
		"Output: cn=third, uid=333333, name=Third, sn=Third's sn, mail=third@my.org",
		"Output: cn=forth, uid=4, name=Forth, sn=Forth's sn, mail=forth@my.org",
		"Output: cn=fith, uid=5, name=Fith, sn=Fith's sn, mail=fith@my.org",
		"Output: cn=sixth, uid=6, name=Sixth, sn=Sixth's sn, mail=sixth@my.org",
		"Output: cn=seventh, uid=7, name=Seventh, sn=Seventh's sn, mail=seventh@my.org",
		"Output: cn=eighth, uid=8, name=Eighth, sn=Eighth's sn, mail=eighth@my.org",
		"Output: cn=ninth, uid=9, name=Ninth, sn=Ninth's sn, mail=ninth@my.org",
		"Output: cn=tenth, uid=1010, name=Tenth, sn=Tenth's sn, mail=tenth@my.org"};

	@Test
	public void testExecuteQuery() throws SQLException, VException, IOException {
		LDAPQueryStatement lStatement = new TestLDAPQueryStatement(new TestLDAPObjectHome());
		QueryResult lResult = lStatement.executeQuery("(mail=test@my.site.org)");

		Collection<String> lRetrieved = new Vector<String>(); 
		while (lResult.hasMoreElements()) {
			GeneralDomainObject lModel = lResult.next();
			lRetrieved.add(getContent(lModel));
		}
		
		assertEquals("Number of retrieved 1.1", EXPECTED.length, lRetrieved.size());
		assertEquals("Number of retrieved 1.2", EXPECTED.length, ((LDAPQueryResult)lResult).getCount());
		for (int i = 0; i < EXPECTED.length; i++) {
			assertTrue("Element retrieved: " + i, lRetrieved.contains(EXPECTED[i]));
		}
		
//		set limit
		int lExpectedLimit = 7;
		LimitObject lLimit = new LimitObjectImpl(lExpectedLimit);
		lStatement = new TestLDAPQueryStatement(new TestLDAPObjectHome());
		lStatement.setLimit(lLimit);
		lResult = lStatement.executeQuery("(mail=test@my.site.org)");
		int i = 0;
		while (lResult.hasMoreElements()) {
			lResult.next();
			i++;
		}
		assertEquals("Number of retrieved 2.1", lExpectedLimit, i);
		assertEquals("Number of retrieved 2.2", lExpectedLimit, ((LDAPQueryResult)lResult).getCount());
		
//		set sort order: we only test for no exception
		String[] lSort = new String[] {"name", "mail"};
		lStatement = new TestLDAPQueryStatement(new TestLDAPObjectHome());
		lStatement.setOrder(lSort);
		lResult = lStatement.executeQuery("(mail=test@my.site.org)");
	}

	private String getContent(GeneralDomainObject inModel) throws VException {
		return String.format("Output: cn=%s, uid=%s, name=%s, sn=%s, mail=%s", 
				inModel.get(TestLDAPObjectHome.KEY_ID), 
				inModel.get(TestLDAPObjectHome.KEY_USER_ID), 
				inModel.get(TestLDAPObjectHome.KEY_NAME), 
				inModel.get(TestLDAPObjectHome.KEY_FIRST_NAME),
				inModel.get(TestLDAPObjectHome.KEY_MAIL));
	}	

}

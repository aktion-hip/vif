package org.hip.vif.core.adapters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Vector;

import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.MemberHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 20.10.2010
 */
public class AddressAdapterTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		IndexHouseKeeper.redirectDocRoot(true);		
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromMember();
		IndexHouseKeeper.deleteTestIndexDir();
	}
	
	@Test
	public void testParse() throws Exception {
		String lMails = "my@site.org, test@localhost,jane.foo@gov.us,   adam.riese@math.org";
		Collection<AddressAdapter> lAddressList = AddressAdapter.parse(lMails);
		assertEquals(4, lAddressList.size());
		
		Collection<String> lAddresses = createStringList(lAddressList);
		assertTrue(lAddresses.contains("my@site.org"));
		assertTrue(lAddresses.contains("test@localhost"));
		assertTrue(lAddresses.contains("jane.foo@gov.us"));
		assertTrue(lAddresses.contains("adam.riese@math.org"));
	}

	private Collection<String> createStringList(Collection<AddressAdapter> inAddresses) {
		Collection<String> outAddresses = new Vector<String>();
		for (AddressAdapter lAddressAdapter : inAddresses) {
			outAddresses.add(lAddressAdapter.getInternetAddress().getAddress());
		}
		return outAddresses;
	}
	
	@Test
	public void testFill() throws Exception {
		data.create2Members();
		Collection<AddressAdapter> lAddressList = AddressAdapter.fill(data.getMemberHome().select(), MemberHome.KEY_MAIL);
		assertEquals(2, lAddressList.size());
		
		Collection<String> lAddresses = createStringList(lAddressList);
		assertTrue(lAddresses.contains("1.mail@test"));
		assertTrue(lAddresses.contains("2.mail@test"));
	}

}

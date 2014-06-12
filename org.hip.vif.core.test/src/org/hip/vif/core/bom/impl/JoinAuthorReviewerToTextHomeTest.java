package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.ResponsibleHome;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 03.08.2010
 */
public class JoinAuthorReviewerToTextHomeTest {

	@BeforeClass
	public static void init() {
		DataHouseKeeper.getInstance();
	}
	
	@Test
	public void testObjects() throws Exception {
		JoinAuthorReviewerToTextHome lHome = new JoinAuthorReviewerToTextHomeSub();
		Iterator<Object> lTest = lHome.getTestObjects();
		String lExpected = "SELECT tblTextAuthorReviewer.MEMBERID, tblTextAuthorReviewer.NTYPE, tblTextVersion.TEXTID, tblTextVersion.NVERSION, tblTextVersion.STITLE, tblTextVersion.SAUTHOR, tblTextVersion.SCOAUTHORS, tblTextVersion.SSUBTITLE, tblTextVersion.SPUBLICATION, tblTextVersion.SYEAR, tblTextVersion.SPAGES, tblTextVersion.NVOLUME, tblTextVersion.NNUMBER, tblTextVersion.SPUBLISHER, tblTextVersion.SPLACE, tblTextVersion.NSTATE, tblTextVersion.SREFERENCE, tblTextVersion.SREMARK, tblTextVersion.DTFROM, tblTextVersion.NTYPE AS biblioType FROM tblTextVersion INNER JOIN tblTextAuthorReviewer ON tblTextVersion.TEXTID = tblTextAuthorReviewer.TEXTID AND tblTextVersion.NVERSION = tblTextAuthorReviewer.NVERSION WHERE tblTextAuthorReviewer.MEMBERID = 8";
		assertEquals("test sql", lExpected, (String)lTest.next());
	}

// --- inner class ---
	
	@SuppressWarnings("serial")
	private class JoinAuthorReviewerToTextHomeSub extends JoinAuthorReviewerToTextHome {
		@Override
		protected Vector<Object> createTestObjects() {
			Vector<Object> out = new Vector<Object>();
			KeyObject lKey = new KeyObjectImpl();
			try {
				lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, new Long(8));
				out.add(createSelectString(lKey));
			} 
			catch (VException exc) {
				fail(exc.getMessage());
			}
			return out;
		}
	}
}

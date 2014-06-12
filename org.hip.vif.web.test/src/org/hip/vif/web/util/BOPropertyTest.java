package org.hip.vif.web.util;


import java.io.File;

import junit.framework.Assert;

import org.hip.kernel.sys.VSys;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 31.07.2011
 */
public class BOPropertyTest {
	private static final String KEY1 = "Firstname";
	private static final String KEY2 = "Amount";
	private static final String KEY3 = "Double";

	@BeforeClass
	public static void init() throws Exception {
		File lLocation = new File("");
		VSys.setContextPath(lLocation.getAbsolutePath());
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testDo() throws Exception {
		TestModelHome lHome = (TestModelHome) VSys.homeManager.getHome(TestModel.HOME_CLASS_NAME);
		TestModel lBO = (TestModel) lHome.create();
		lBO.set(KEY1, "Hallo");
		lBO.set(KEY2, new Integer(42));
		
		BOProperty<String> lProperty = new BOProperty<String>(lBO, KEY1, String.class);
		Assert.assertEquals("Hallo", lProperty.getValue());
		
		lProperty.setValue("Velo");
		Assert.assertEquals("Velo", lProperty.getValue());
		
		lProperty.setValue("");
		Assert.assertEquals("", lProperty.getValue());
		
		lProperty.setValue(null);
		Assert.assertEquals("", lProperty.getValue());
		
		BOProperty<Integer> lPropertyNumeric = new BOProperty<Integer>(lBO, KEY2, Integer.class);
		Assert.assertEquals(42, (int)lPropertyNumeric.getValue());
		
		lPropertyNumeric.setValue(55);
		Assert.assertEquals(55, (int)lPropertyNumeric.getValue());
		
		lPropertyNumeric.setValue(null);
		Assert.assertNull(lPropertyNumeric.getValue());

		BOProperty<Double> lPropertyNumeric2 = new BOProperty<Double>(lBO, KEY3, Double.class);
		Assert.assertNull(lPropertyNumeric2.getValue());
	}
	
}

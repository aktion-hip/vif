package org.hip.vif.web.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.hip.kernel.sys.VSys;
import org.hip.vif.web.util.ConfigurationItem.PropertyDef;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConfigurationItemTest {

	@BeforeClass
	public static void init() {
		final File lLocation = new File("");
		VSys.setContextPath(lLocation.getAbsolutePath());
	}

	@SuppressWarnings("unchecked")
	@Test
	public final void testIsDirty() throws IOException {
		final ConfigurationItem config = ConfigurationItem
				.createConfiguration();
		assertNotNull(config);

		assertFalse(config.isDirty(PropertyDef.COUNTRY_DFT));
		assertFalse(config.isDirty(PropertyDef.LATENCY_DAYS));

		final Object oldCountry = config.getItemProperty(
				PropertyDef.COUNTRY_DFT.getPID()).getValue();
		assertEquals("US", oldCountry);

		config.getItemProperty(PropertyDef.COUNTRY_DFT.getPID()).setValue("US");
		config.getItemProperty(PropertyDef.LATENCY_DAYS.getPID()).setValue("");

		assertFalse(config.isDirty(PropertyDef.COUNTRY_DFT));
		assertFalse(config.isDirty(PropertyDef.LATENCY_DAYS));

		config.getItemProperty(PropertyDef.COUNTRY_DFT.getPID()).setValue("DE");
		config.getItemProperty(PropertyDef.LATENCY_DAYS.getPID()).setValue("3");

		assertTrue(config.isDirty(PropertyDef.COUNTRY_DFT));
		assertTrue(config.isDirty(PropertyDef.LATENCY_DAYS));
	}

}

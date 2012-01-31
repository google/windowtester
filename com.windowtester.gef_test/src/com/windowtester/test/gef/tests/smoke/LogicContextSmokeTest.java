package com.windowtester.test.gef.tests.smoke;

import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.test.gef.tests.common.BaseLogicDrivingTest;

/**
 * Test to verify basic actions in the context of a logic diagram.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Jaime Wren
 *
 */
public class LogicContextSmokeTest extends BaseLogicDrivingTest {
	
	public void testClickItemsAndDrawers() throws Exception {
		clickItems();
		for (int i=0; i < 5; ++i)
			clickDrawers(); //NOTE: will conceal TODO: NOT CLICKING?!??
		pause(1000);
		clickItems();
		
		assertNoLoggedExceptions();
	}

	private void clickDrawers() throws WidgetSearchException {
		for (Drawer d : Drawer.values()) {
			clickPalette(d.getPath());
		}
	}

	private void clickItems() throws WidgetSearchException {
		for (int i = 0; i < ALL_PALETTE_LOCATIONS.length; i++) {
			clickPalette(ALL_PALETTE_LOCATIONS[i]);
		}
	}

}

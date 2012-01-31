package com.windowtester.test.gef.tests.smoke;

import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.test.gef.tests.common.BaseShapeDrivingTest;

/**
 * Test to verify basic actions in the context of a shape diagram.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Jaime Wren
 * @author Phil Quitslund
 *
 */
public class ShapeContextSmokeTest extends BaseShapeDrivingTest {
	
	
	private static boolean setupComplete; //one-time setup flag
	
	@Override
	protected void setUp() throws Exception {
		if (setupComplete) 
			return;
		doSetup();
	}


	private void doSetup() throws Exception {
		super.setUp();
		setupComplete = true;
	}
	
	
	public void testClickItemsAndDrawers() throws Exception {
		clickItems();
		clickDrawers();
		clickItems();
		
		assertNoLoggedExceptions();
	}
	
	public void testPaletteFindFailure() throws Exception {
		try {
			getUI().click(new PaletteItemLocator("bogus"));
			fail();
		} catch(WidgetSearchException e) {
			//pass
		}
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

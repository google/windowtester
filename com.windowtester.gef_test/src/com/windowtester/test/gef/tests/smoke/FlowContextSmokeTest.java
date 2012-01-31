package com.windowtester.test.gef.tests.smoke;

import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.internal.locator.provisional.api.IndexedFigureLocator;
import com.windowtester.runtime.gef.locator.FigureLocator;
import com.windowtester.test.gef.factories.FigureMatcherFactory;
import com.windowtester.test.gef.tests.common.BaseFlowDrivingTest;

import static com.windowtester.test.gef.factories.FigureMatcherFactory.*;

/**
 * Test to verify basic actions in the context of a flow diagram.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Jaime Wren
 * @author Phil Quitslund
 *
 */
public class FlowContextSmokeTest extends BaseFlowDrivingTest {
	
	public void testClickItemsAndDrawers() throws Exception {
		clickItems();
		clickDrawers();
		clickItems();
		
		
		// so that the clicking below doesn't cause a change in the flow diagram
		clickPalette("Select");
		
		// click through all the simple activity labels in the editor
		for (int i = 0; i < 17; i++) {
			getUI().click(new IndexedFigureLocator(i,simpleActivityLabelMatcher, yxComparator()));
			pauseForModel();
		}
		
		// make some clicks using the names of the simple activity labels
		
		getUI().click(new FigureLocator(FigureMatcherFactory.and(simpleActivityLabelMatcher, new LabelNameMatcher("Turn off alarm"))));
		pauseForModel();
		
		getUI().click(new FigureLocator(FigureMatcherFactory.and(simpleActivityLabelMatcher, new LabelNameMatcher("Comb hair"))));
		pauseForModel();
		
		getUI().click(new FigureLocator(FigureMatcherFactory.and(simpleActivityLabelMatcher, new LabelNameMatcher("Hit snooze button"))));
		pauseForModel();
		
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

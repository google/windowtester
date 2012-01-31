package com.windowtester.test.gef.tests.smoke.locators;

import static com.windowtester.test.gef.helpers.GEFExampleHelper.andFigure;
import static com.windowtester.test.gef.helpers.GEFExampleHelper.orFigure;
import static com.windowtester.test.gef.helpers.GEFAssert.assertConnected;
import static com.windowtester.test.gef.helpers.GEFAssert.assertNotConnected;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.Position;
import com.windowtester.runtime.gef.internal.locator.provisional.api.AnchorLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.test.eclipse.BaseTest;
import com.windowtester.test.gef.helpers.LogicExampleManager;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class AnchorLocatorSmokeTest extends BaseTest {
	

	private final LogicExampleManager exampleManager = new LogicExampleManager(this);
		
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		exampleManager.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		exampleManager.tearDown();
		super.tearDown();
	}

	public void testAnchorConnectionSmoke() throws WidgetSearchException {
		
		IUIContext ui = getUI();
		
		//create
		ui.click(new PaletteItemLocator("Components/And Gate"));
		ui.click(new FigureCanvasXYLocator(100, 100));	
		ui.click(new PaletteItemLocator("Components/Or Gate"));
		ui.click(new FigureCanvasXYLocator(150, 150));	
		
		assertNotConnected(ui, andFigure(), orFigure());
		
		//connect
		ui.click(new PaletteItemLocator("Connection"));
		ui.click(new AnchorLocator(Position.BOTTOM, andFigure()));
		ui.pause(1000);
		ui.click(new AnchorLocator(Position.TOP_LEFT, orFigure()));
		ui.pause(1000);
		
		//TODO: convert to a condition!
		assertConnected(ui, andFigure(), orFigure());	
		//ui.assertThat(new FiguresConnectedCondition(andFigure(), orFigure()));
			
	}




	
	
	
	
}

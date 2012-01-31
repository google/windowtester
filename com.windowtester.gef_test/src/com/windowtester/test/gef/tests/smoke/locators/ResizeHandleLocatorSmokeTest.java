package com.windowtester.test.gef.tests.smoke.locators;

import static com.windowtester.test.gef.helpers.GEFExampleHelper.getBounds;

import org.eclipse.draw2d.geometry.Rectangle;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.Position;
import com.windowtester.runtime.gef.internal.locator.provisional.api.ResizeHandleLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.FigureClassLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.test.eclipse.BaseTest;
import com.windowtester.test.gef.helpers.ShapeExampleManager;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ResizeHandleLocatorSmokeTest extends BaseTest {

	
	private final ShapeExampleManager exampleManager = new ShapeExampleManager(this);
	
	
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
	

	public void testResizeHandleSmoke() throws WidgetSearchException {
		IUIContext ui = getUI();
		
		//create
		ui.click(new PaletteItemLocator("Shapes/Ellipse"));
		ui.click(new FigureCanvasXYLocator(100, 100));	
		
		//select
		IWidgetLocator ellipse = ui.click(new FigureClassLocator("org.eclipse.draw2d.Ellipse"));		
		
		//cache bounds
		Rectangle originalBounds = getBounds(ellipse).getCopy();
		
		//resize
		ui.click(new ResizeHandleLocator(Position.EAST, new FigureClassLocator("org.eclipse.draw2d.Ellipse")));
		ui.dragTo(new FigureCanvasXYLocator(200, 200));
		
		//assert
		ui.pause(2000);
		Rectangle newBounds = getBounds(ellipse);
		assertWider(newBounds, originalBounds);
		
	}


	private void assertWider(Rectangle newBounds, Rectangle originalBounds) {
		assertTrue(newBounds.width > originalBounds.width);
	}



	
	
}

package com.windowtester.test.gef.tests.smoke.scenarios;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.test.gef.tests.common.BaseShapeDrivingTest;

/**
 * Revised GEF example to test new API
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Jaime Wren
 * @author Dan Rubel
 */
public class ShapeDrivingSmokeTest1a extends BaseShapeDrivingTest {
	
	// uses only XYLocator to identify the widgets on the screen
	public void test1aShapeDrive() throws Exception {
		IUIContext ui = getUI();
		
		// select and drop ellipse
		ui.click(new PaletteItemLocator("Shapes/Ellipse"));
		IWidgetLocator ellipse = ui.click(new FigureCanvasXYLocator(100, 100));
		
		// create surrounding rectangles
		ui.click(new PaletteItemLocator("Shapes/Rectangle"));
		IWidgetLocator rectangle1 = ui.click(new FigureCanvasXYLocator(0, 0));
		ui.click(new PaletteItemLocator("Shapes/Rectangle"));
		IWidgetLocator rectangle2 = ui.click(new FigureCanvasXYLocator(0, 200));
		ui.click(new PaletteItemLocator("Shapes/Rectangle"));
		IWidgetLocator rectangle3 = ui.click(new FigureCanvasXYLocator(200, 0));
		ui.click(new PaletteItemLocator("Shapes/Rectangle"));
		IWidgetLocator rectangle4 = ui.click(new FigureCanvasXYLocator(200, 200));
		
		// connect the ellipse and the 4 rectangles
		
		ui.click(new PaletteItemLocator("Solid connection"));
		ui.click(ellipse);
		ui.click(rectangle2);
		ui.click(new PaletteItemLocator("Dashed connection"));
		ui.click(ellipse);
		ui.click(rectangle3);
		
		// stretch the rectangle

		ui.click(new PaletteItemLocator("Select"));
		ui.click(rectangle1);
//		ui.click(new ResizeHandleLocator(WT.RIGHT | WT.BOTTOM));
//		ui.dragBy(10, 20);
		
		// stretch 2 rectangles at the same time
		
		ui.click(rectangle2);
		ui.click(1, rectangle3, WT.SHIFT);
//		ui.click(new ResizeHandleLocator(rectangle2, WT.RIGHT | WT.BOTTOM));
//		ui.dragBy(10, 20);
		
		// pause to look at editor:
		pause(3000);
		
		save();
		
		assertNoLoggedExceptions();
	}
}

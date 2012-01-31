package com.windowtester.test.gef.tests.smoke.scenarios;

import com.windowtester.runtime.util.ScreenCapture;
import com.windowtester.test.gef.tests.common.BaseShapeDrivingTest;

/**
 * 
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Jaime Wren
 *
 */
public class ShapeDrivingSmokeTest1 extends BaseShapeDrivingTest {
	
	// uses only XYLocator to identify the widgets on the screen
	public void test1ShapeDrive() throws Exception {
		ScreenCapture.createScreenCapture(getClass().getName() + "_test1ShapeDrive-Start");
		
		// select and drop ellipse
		createEllipseAt(100, 100);
		
		// create surrounding rectangles
		createRectangleAt(0,   0);
		createRectangleAt(0,   200);
		createRectangleAt(200, 0);
		createRectangleAt(200, 200);
		
		// connect the ellipse and the 4 rectangles
		
		// strange drawing behavior from Shapes, could send request to GEF Team
		//makeSolidConnection(115, 115, 10, 10);
		
		makeSolidConnection(115, 115, 10, 210);
		makeDashedConnection(115, 115, 210, 10);
		
		// strange drawing behavior from Shapes, could send request to GEF Team
		//makeSolidConnection(115, 115, 210, 210);
		
		// pause to look at editor:
		ScreenCapture.createScreenCapture(getClass().getName() + "_test1ShapeDrive-End");
		pause(3000);
		
		save();
		
		assertNoLoggedExceptions();
	}

}

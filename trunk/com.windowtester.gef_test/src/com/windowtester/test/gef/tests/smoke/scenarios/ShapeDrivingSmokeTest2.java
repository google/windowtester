package com.windowtester.test.gef.tests.smoke.scenarios;

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
public class ShapeDrivingSmokeTest2 extends BaseShapeDrivingTest {
	
	// same actions and output as test1ShapeDrive, except this identifies the
	// shapes via indexes when making the connections
	public void test2ShapeDrive() throws Exception {
		
		// select and drop ellipse
		createEllipseAt(100, 100);
		
		// create surrounding rectangles
		createRectangleAt(0,   0);
		createRectangleAt(0,   200);
		createRectangleAt(200, 0);
		createRectangleAt(200, 200);
		
		clickPalette("Solid connection");
		clickEllipseIndexedAt(0);
		clickRectangleIndexedAt(1);
		pauseForModel();
		
		clickPalette("Dashed connection");
		clickEllipseIndexedAt(0);
		clickRectangleIndexedAt(2);
		pauseForModel();
		
		// pause to look at editor:
		pause(3000);
		
		save();
		
		assertNoLoggedExceptions();
	}

}

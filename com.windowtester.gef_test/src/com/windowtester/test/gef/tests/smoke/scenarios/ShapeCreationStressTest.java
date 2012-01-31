package com.windowtester.test.gef.tests.smoke.scenarios;

import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.locator.FigureCanvasLocator;
import com.windowtester.runtime.gef.locator.FigureLocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.test.gef.tests.common.BaseShapeDrivingTest;

public class ShapeCreationStressTest extends BaseShapeDrivingTest {

	
	//conjecture: issue has to do with interactions with palette to make visible...
	
	
	public void testStressShapeCreation() throws Exception {

		
		//getUI().click(new PaletteButtonLocator());
		
		int numRows = 10;
		int numColumns = 20;

		int rowSpace = 30;
		int columnSpace = 30;

		
		
		//int currentShapeCount = 0;
		
		for (int i= 0; i < numRows; ++i) {
			for (int j= 0; j < numColumns; ++j) {
				createEllipseAt(j*columnSpace, i*rowSpace);
				/*
				 * Notice we don't want to put the assertion
				 * here since this will just give the operation
				 * time to complete during the find retry
				 */
				
				//faster fail for debugging:
				//assertTrue(getUI().findAll(new FigureLocator(ellipseMatcher)).length == ++currentShapeCount);
			}
		}

		int numShapes = numColumns*numRows;		
		assertCanFindShapes(numShapes);
		
//		for (int i= 0; i < numShapes; ++i) {			
//			getUI().contextClick(new LRLocator(i, ellipseLocator()), "&Undo shape creation");
//		}
		
	}

	//overriding to ensure no model pauses
	protected void createEllipseAt(int x, int y) throws WidgetSearchException {
		clickPalette("Shapes/Ellipse");
		getUI().click(new XYLocator(new FigureCanvasLocator(), x, y));
	}
	
	
	private void assertCanFindShapes(int numShapes) {
		int numFound = getUI().findAll(ellipseLocator()).length;
		assertEquals("expected " + numShapes + " but got: " + numFound, numShapes, numFound);		
	}

	private FigureLocator ellipseLocator() {
		return new FigureLocator(ellipseMatcher);
	}

	
	
}

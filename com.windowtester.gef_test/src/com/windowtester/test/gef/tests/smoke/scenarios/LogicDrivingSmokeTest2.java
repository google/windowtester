package com.windowtester.test.gef.tests.smoke.scenarios;

import static com.windowtester.test.gef.factories.FigureMatcherFactory.xyComparator;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.gef.internal.locator.provisional.api.IndexedFigureLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasLocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.locator.eclipse.ContributedToolItemLocator;
import com.windowtester.test.gef.tests.common.BaseLogicDrivingTest;

/**
 * 
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Jaime Wren
 *
 */
public class LogicDrivingSmokeTest2 extends BaseLogicDrivingTest {
	
	public void test2LogicDrive() throws Exception {
		
		final IUIContext ui = getUI();
		
		clickPalette("Components/Circuit");
		ui.mouseMove(new XYLocator(new FigureCanvasLocator(), 100, 100));
		ui.dragTo(new XYLocator(new FigureCanvasLocator(), 300, 250));
		pauseForModel();
		
		clickPalette("Components/And Gate");
		ui.click(new XYLocator(new FigureCanvasLocator(), 150, 150));
		pauseForModel();
		
		clickPalette("Components/Or Gate");
		ui.click(new XYLocator(new FigureCanvasLocator(), 200, 150));
		pauseForModel();
		
		clickPalette("Components/XOR Gate");
		ui.click(new XYLocator(new FigureCanvasLocator(), 250, 150));
		pauseForModel();
		
		clickPalette("Components/LED");
		ui.click(new XYLocator(new FigureCanvasLocator(), 125, 20));
		pauseForModel();
		
		clickPalette("Components/LED");
		ui.click(new XYLocator(new FigureCanvasLocator(), 225, 20));
		pauseForModel();
		
		clickPalette("Components/LED");
		ui.click(new XYLocator(new FigureCanvasLocator(), 100, 300));
		pauseForModel();
		
		clickPalette("Components/LED");
		ui.click(new XYLocator(new FigureCanvasLocator(), 175, 300));
		pauseForModel();
		
		clickPalette("Components/LED");
		ui.click(new XYLocator(new FigureCanvasLocator(), 250, 300));
		pauseForModel();
		
		
		// make some connections:
		
		// bottom far left pin of the top left LED to the first pin on the circuit
		clickPalette("Connection");
		ui.click(new XYLocator(new IndexedFigureLocator(1,ledMatcher, xyComparator()),20,20));
		ui.click(new XYLocator(new IndexedFigureLocator(0,circuitMatcher, xyComparator()),-68,-75));
		pauseForModel();
		
		// bottom far left pin of the top right LED to the last pin on the circuit
		clickPalette("Connection");
		ui.click(new XYLocator(new IndexedFigureLocator(3,ledMatcher, xyComparator()),20,20));
		ui.click(new XYLocator(new IndexedFigureLocator(0,circuitMatcher, xyComparator()),68,-75));
		pauseForModel();
		
		// first circuit pin to each of the gates
		clickPalette("Connection");
		ui.click(new XYLocator(new IndexedFigureLocator(0,circuitMatcher, xyComparator()),-68,-75));
		ui.click(new XYLocator(new IndexedFigureLocator(0,andGateMatcher, xyComparator()),100,100));
		pauseForModel();
		
		clickPalette("Connection");
		ui.click(new XYLocator(new IndexedFigureLocator(0,circuitMatcher, xyComparator()),-68,-75));
		ui.click(new XYLocator(new IndexedFigureLocator(0,orGateMatcher, xyComparator()),100,100));
		pauseForModel();
		
		clickPalette("Connection");
		ui.click(new XYLocator(new IndexedFigureLocator(0,circuitMatcher, xyComparator()),-68,-75));
		ui.click(new XYLocator(new IndexedFigureLocator(0,xOrGateMatcher, xyComparator()),100,100));
		pauseForModel();
		
		// last top circuit pin to each of the gates
		clickPalette("Connection");
		ui.click(new XYLocator(new IndexedFigureLocator(0,circuitMatcher, xyComparator()),71,-75));
		ui.click(new XYLocator(new IndexedFigureLocator(0,andGateMatcher, xyComparator()),100+10,100));
		pauseForModel();
		
		clickPalette("Connection");
		ui.click(new XYLocator(new IndexedFigureLocator(0,circuitMatcher, xyComparator()),71,-75));
		ui.click(new XYLocator(new IndexedFigureLocator(0,orGateMatcher, xyComparator()),100+10,100));
		pauseForModel();
		
		clickPalette("Connection");
		ui.click(new XYLocator(new IndexedFigureLocator(0,circuitMatcher, xyComparator()),71,-75));
		ui.click(new XYLocator(new IndexedFigureLocator(0,xOrGateMatcher, xyComparator()),100+10,100));
		pauseForModel();
		
		// connect the 
		clickPalette("Connection");
		ui.click(new XYLocator(new IndexedFigureLocator(0,andGateMatcher, xyComparator()),100,100));
		ui.click(new XYLocator(new IndexedFigureLocator(0,circuitMatcher, xyComparator()),-68,75));
		pauseForModel();
		
		clickPalette("Connection");
		ui.click(new XYLocator(new IndexedFigureLocator(0,orGateMatcher, xyComparator()),100,100));
		ui.click(new XYLocator(new IndexedFigureLocator(0,circuitMatcher, xyComparator()),-68+50,75));
		pauseForModel();
		
		clickPalette("Connection");
		ui.click(new XYLocator(new IndexedFigureLocator(0,xOrGateMatcher, xyComparator()),100,100));
		ui.click(new XYLocator(new IndexedFigureLocator(0,circuitMatcher, xyComparator()),-68+100,75));
		pauseForModel();
		
		// connect the bottom of the circuit to output LEDs
		clickPalette("Connection");
		ui.click(new XYLocator(new IndexedFigureLocator(0,circuitMatcher, xyComparator()),-68,75));
		ui.click(new XYLocator(new IndexedFigureLocator(0,ledMatcher, xyComparator()),20,-20));
		pauseForModel();
		
		clickPalette("Connection");
		ui.click(new XYLocator(new IndexedFigureLocator(0,circuitMatcher, xyComparator()),-68+50,75));
		ui.click(new XYLocator(new IndexedFigureLocator(2,ledMatcher, xyComparator()),20,-20));
		pauseForModel();
		
		clickPalette("Connection");
		ui.click(new XYLocator(new IndexedFigureLocator(0,circuitMatcher, xyComparator()),-68+100,75));
		ui.click(new XYLocator(new IndexedFigureLocator(4,ledMatcher, xyComparator()),20,-20));
		pauseForModel();
		
		
		clickPalette("Select");
		
		ui.click(new IndexedFigureLocator(1,ledMatcher, xyComparator()));
		ui.click(new ContributedToolItemLocator("Increment"));
		pauseForModel();
		
		ui.click(new IndexedFigureLocator(3,ledMatcher, xyComparator()));
		ui.click(new ContributedToolItemLocator("Increment"));
		pauseForModel();
		
		ui.click(new IndexedFigureLocator(1,ledMatcher, xyComparator()));
		ui.click(new ContributedToolItemLocator("Decrement"));
		pauseForModel();
		
		
		clickPalette("Marquee");
		ui.mouseMove(new XYLocator(new FigureCanvasLocator(), 5, 5));
		ui.dragTo(new XYLocator(new FigureCanvasLocator(), 500, 500));
		pauseForModel();
		
		clickPalette("Select");
		ui.mouseMove(new XYLocator(new FigureCanvasLocator(), 120, 120));
		ui.dragTo(new XYLocator(new FigureCanvasLocator(), 200, 200));
		pauseForModel();
		
		// pause to look at editor:
		//pause(3000);
		
		save();
		
		assertNoLoggedExceptions();
	}
	
	
	@Override
	//TODO: why is this needed?  
	protected void pauseForModel() {
		getUI().pause(750);
	}
	
}

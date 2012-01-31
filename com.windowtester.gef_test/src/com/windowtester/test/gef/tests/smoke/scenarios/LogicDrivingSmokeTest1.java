package com.windowtester.test.gef.tests.smoke.scenarios;

import org.eclipse.gef.examples.logicdesigner.figures.LEDFigure;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.locator.provisional.api.IndexedFigureLocator;
import com.windowtester.runtime.gef.internal.util.TextHelper;
import com.windowtester.runtime.swt.locator.eclipse.ContributedToolItemLocator;
import com.windowtester.test.gef.tests.common.BaseLogicDrivingTest;

import static com.windowtester.test.gef.factories.FigureMatcherFactory.*;

/**
 * 
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Jaime Wren
 *
 */
public class LogicDrivingSmokeTest1 extends BaseLogicDrivingTest {
	
	public LogicDrivingSmokeTest1() {
		super(true);
	}
	
	public void test1LogicDrive() throws Exception {
		
		final IUIContext ui = getUI();
		
		ui.click(new IndexedFigureLocator(0,ledMatcher, xyComparator()));
		pauseForModel();
		
		assertAdditionWorks();
		ui.click(new ContributedToolItemLocator("Increment")); assertAdditionWorks();
		ui.click(new ContributedToolItemLocator("Increment")); assertAdditionWorks();
		ui.click(new ContributedToolItemLocator("Increment")); assertAdditionWorks();
		pause(500);
		
		ui.click(new ContributedToolItemLocator("Decrement")); assertAdditionWorks();
		ui.click(new ContributedToolItemLocator("Decrement")); assertAdditionWorks();
		pause(500);
		
		ui.click(new IndexedFigureLocator(2,ledMatcher, xyComparator()));
		pauseForModel();
		
		ui.click(new ContributedToolItemLocator("Increment")); assertAdditionWorks();
		ui.click(new ContributedToolItemLocator("Increment")); assertAdditionWorks();
		
		// pause to look at editor:
		pause(3000);
		
		save();
		
		assertNoLoggedExceptions();
	}
	
	private void assertAdditionWorks() throws WidgetSearchException {
		// get the input LED figures:
		IFigureReference refInput1 = (IFigureReference)getUI().find(new IndexedFigureLocator(0,ledMatcher,xyComparator()));
		LEDFigure ledFigureInput1 = (LEDFigure)refInput1.getFigure();
		String strValueInput1 = TextHelper.getStringFieldValue(ledFigureInput1, "value");
		int valueInput1 = Integer.parseInt(strValueInput1);
		
		IFigureReference refInput2 = (IFigureReference)getUI().find(new IndexedFigureLocator(2,ledMatcher,xyComparator()));
		LEDFigure ledFigureInput2 = (LEDFigure)refInput2.getFigure();
		String strValueInput2 = TextHelper.getStringFieldValue(ledFigureInput2, "value");
		int valueInput2 = Integer.parseInt(strValueInput2);
		
		// get the output LED
		IFigureReference refOutput = (IFigureReference)getUI().find(new IndexedFigureLocator(1,ledMatcher,xyComparator()));
		LEDFigure ledFigureOutput = (LEDFigure)refOutput.getFigure();
		String strValueOutput = TextHelper.getStringFieldValue(ledFigureOutput, "value");
		int valueOutput = Integer.parseInt(strValueOutput);
		
		// prints the assertion
		//System.out.println(valueInput1 + " + " + valueInput2 + " = " + valueOutput);
		
		// the assertion:
		assertTrue(valueInput1+valueInput2 == valueOutput);
	}
	
	
}

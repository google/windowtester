package com.windowtester.test.gef.tests.smoke.scenarios;

import static com.windowtester.test.gef.factories.FigureMatcherFactory.yxComparator;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.gef.internal.locator.provisional.api.IndexedFigureLocator;
import com.windowtester.runtime.gef.locator.FigureLocator;
import com.windowtester.test.gef.factories.FigureMatcherFactory;
import com.windowtester.test.gef.tests.common.BaseFlowDrivingTest;

/**
 * Simple flow driving test.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Jaime Wren
 * @author Phil Quitslund
 *
 */
public class FlowDrivingSmokeTest1 extends BaseFlowDrivingTest {
	
	
	private class FlowLabelLocator extends FigureLocator {
		private static final long serialVersionUID = 4764124989295101680L;

		public FlowLabelLocator(String labelText) {
			super(FigureMatcherFactory.and(simpleActivityLabelMatcher, new LabelNameMatcher(labelText)));
		}
	}
	

	public void test1FlowDrive() throws Exception {
		
		final IUIContext ui = getUI();
		
		// create a new activity under Sleep.....
		clickPalette("Components/Activity");
		ui.click(new FlowLabelLocator("Sleep....."));
		pauseForModel();
		
		// change name of new simple activity label
		ui.click(new IndexedFigureLocator(2,simpleActivityLabelMatcher, yxComparator()));
		pause(1000);
		ui.enterText("Alarm Failure!!!");
		pauseForModel();
		
		// create a new activity under Alarm Failure!!!
		clickPalette("Components/Activity");
		ui.click(new FlowLabelLocator("Alarm Failure!!!"));
		pauseForModel();
		
		// change name of new simple activity label
		ui.click(new IndexedFigureLocator(3,simpleActivityLabelMatcher,yxComparator()));
		pause(1000);
		ui.enterText("How much time?");
		pauseForModel();
		
		clickPalette("Connection Creation");
		ui.click(new FlowLabelLocator("How much time?"));
		ui.click(new FigureLocator(FigureMatcherFactory.and(startTagMatcher, new LabelNameMatcher("Bathroom activities"))));
		pauseForModel();
		
		clickPalette("Connection Creation");
		ui.click(new FlowLabelLocator("How much time?"));
		ui.click(new FlowLabelLocator("Put on clothes"));
		pauseForModel();
		
		clickPalette("Connection Creation");
		ui.click(new FlowLabelLocator("How much time?"));
		ui.click(new FlowLabelLocator("Drive to work"));
		pauseForModel();
		
		// pause to look at editor:
		pause(2000);
		
		save();
		
		assertNoLoggedExceptions();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.test.gef.tests.common.AbstractGEFDrivingTest#pauseForModel()
	 */
	@Override
	protected void pauseForModel() {
		pause(750);
	}
	
}

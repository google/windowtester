package com.windowtester.test.gef.tests.smoke.scenarios;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.gef.locator.FigureClassLocator;
import com.windowtester.runtime.gef.locator.LRLocator;
import com.windowtester.test.gef.tests.common.BaseShapeDrivingTest;

/**
 * https://fogbugz.instantiations.com/fogbugz/default.asp?45764
 * <p/>
 * Copyright (c) 2010, Instantiations, Inc.<br/>
 * All Rights Reserved
 * 
 * @author Phil Quitslund
 *
 */
public class FigureContextClickTest extends BaseShapeDrivingTest {

	private class EllipseCountCondition implements ICondition {
		private final int count;

		public EllipseCountCondition(int count) {
			this.count = count;
		}
		
		public boolean test() {			
			return getUI().findAll(new FigureClassLocator("org.eclipse.draw2d.Ellipse")).length == count;
		}
	}
	
	
	
	public void testShapeContextClick() throws Exception {
		
		// select and drop ellipse
		createEllipseAt(100, 100);
		
		IUIContext ui = getUI();

		ui.assertThat(new EllipseCountCondition(1));	
		
		ui.contextClick(new LRLocator(0, new FigureClassLocator("org.eclipse.draw2d.Ellipse")), "Delete");
		
		ui.assertThat(new EllipseCountCondition(0));	
		
		assertNoLoggedExceptions();
	}
	
	
}

package com.windowtester.test.gef.tests.smoke.scenarios;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.swt.locator.eclipse.ActiveEditorLocator;
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
public class CanvasContextClickTest extends BaseShapeDrivingTest {

	
	public void testShapeContextClick() throws Exception {
		
		// select and drop ellipse
		createEllipseAt(100, 100);

//		getUI().contextClick(new LRLocator(0, new FigureClassLocator("org.eclipse.draw2d.Ellipse")), "Undo shape creation");
		
		IUIContext ui = getUI();
		
		ui.contextClick(new FigureCanvasXYLocator(200, 200), "Undo shape creation");
		
		ui.assertThat(new ActiveEditorLocator().isDirty(false));	
		
		assertNoLoggedExceptions();
	}
	
	
}

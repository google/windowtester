package com.windowtester.runtime.internal.scope;


import static com.windowtester.test.gef.factories.LocatorFactory.button;
import static com.windowtester.test.gef.factories.LocatorFactory.menuItem;
import static com.windowtester.test.gef.factories.LocatorFactory.shellDisposed;
import static com.windowtester.test.gef.factories.LocatorFactory.shellShowing;
import static com.windowtester.test.gef.factories.LocatorFactory.treeItem;

import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.locator.FigureLocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.gef.tests.common.AbstractGEFDrivingTest;


/**
 * A smoke test to drive view scoped figure location.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ViewScopeSmokeTest extends AbstractGEFDrivingTest {

	
	public void testViewScopeSmoke() throws WidgetSearchException {
		
		//setup
		click(menuItem("Window/Show View/Other..."));
		wait(shellShowing("Show View"));
		click(treeItem("Other/Draw 2D DND View"));
		click(button("OK"));
		wait(shellDisposed("Show View"));
		
		
		IFigureMatcher matcher = new IFigureMatcher() {
			public boolean matches(IFigureReference figure) {	
				if (figure == null)
					return false;
				return figure.getFigure().getClass().getName().equals("org.eclipse.draw2d.RectangleFigure");
			}
			
		};
		ViewLocator view = new ViewLocator("com.windowtester.gef.test.views.DNDDraw2DView");
		FigureLocator locator = new FigureLocator(matcher, view);
		
		IWidgetLocator[] all = getUI().findAll(locator);
		assertEquals(1, all.length);
		
		
	}

	
}

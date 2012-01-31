package com.windowtester.test.gef.helpers;

import static junit.framework.Assert.fail;

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.finder.ConnectionFinder;
import com.windowtester.runtime.gef.locator.FigureLocator;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class GEFAssert {
	
	public static void assertNotConnected(IUIContext ui, FigureLocator fl1, FigureLocator fl2) throws WidgetSearchException {
		
		IFigure figure1 = findFigure(ui, fl1);
		IFigure figure2 = findFigure(ui, fl2);
		
		IFigure[] connectedFigures = ConnectionFinder.getDefault().findAllConnectedFigures(figure1);
		for (IFigure figure : connectedFigures) {
			if (figure2 == figure)
				fail();
		}
		return;
	}

	public static void assertConnected(IUIContext ui, FigureLocator fl1, FigureLocator fl2) throws WidgetSearchException {
		
		IFigure figure1 = findFigure(ui, fl1);
		IFigure figure2 = findFigure(ui, fl2);
		
		IFigure[] connectedFigures = ConnectionFinder.getDefault().findAllConnectedFigures(figure1);
		for (IFigure figure : connectedFigures) {
			if (figure2 == figure)
				return;
		}
		fail();
	}

	public static IFigure findFigure(IUIContext ui, FigureLocator fl1) throws WidgetSearchException {
		return ((IFigureReference)ui.find(fl1)).getFigure();
	}
}

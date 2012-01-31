package com.windowtester.test.gef.tests.runtime;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.locator.FigureLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.test.gef.tests.common.BaseShapeDrivingTest;

/**
 * <p>
 * Copyright (c) 2008, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class FigureXYSelectionTest extends BaseShapeDrivingTest {

	
	private static final int CLOSE_ENOUGH_THRESHHOLD = 2;
	
	private Point clicked = new Point();
	
	public void testClickShapeAtXY() throws Exception {
		
		createEllipseAt(50, 50);
		
		IFigure figure = find(ellipse()).getFigure();
		
		figure.addMouseListener(new MouseListener.Stub() {
			public void mousePressed(MouseEvent me) {
				clicked.x = me.x;
				clicked.y = me.y;
				//System.out.println("("+ me.x +", " + me.y + ")");
			}
		});

		
		Rectangle bounds = figure.getBounds();

		click(ellipse());		
		Point center = bounds.getCenter();		
		assertCloseTo(center.x, clicked.x);
		assertCloseTo(center.y, clicked.y);
		
		click(xy(ellipse(), 10, 10));
		assertCloseTo(bounds.x + 10, clicked.x);
		assertCloseTo(bounds.y + 10, clicked.y);
		
		click(xy(ellipse(), 20, 30));
		assertCloseTo(bounds.x + 20, clicked.x);
		assertCloseTo(bounds.y + 30, clicked.y);
		
		click(xy(ellipse(), 30, 10));
		assertCloseTo(bounds.x + 30, clicked.x);
		assertCloseTo(bounds.y + 10, clicked.y);
		
		click(xy(ellipse(), 33, 45));
		assertCloseTo(bounds.x + 33, clicked.x);
		assertCloseTo(bounds.y + 45, clicked.y);
		
	}

	
	private void assertCloseTo(final int x1, final int x2) {
		getUI().assertThat(new ICondition() {
			public boolean test() {
				return isCloseTo(x1, x2);
			}
			/* (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				return "for " +  x2 + " to be closer to " + x1 + " ( not within threshold of " + CLOSE_ENOUGH_THRESHHOLD + ")";
			}
			
		});
	}


	private boolean isCloseTo(int x1, int x2) {
		if (x1 == x2)
			return true;
		return Math.abs(x1-x2) < CLOSE_ENOUGH_THRESHHOLD;
	}


	private IFigureReference find(FigureLocator locator) throws WidgetSearchException {
		return (IFigureReference) getUI().find(locator);
	}


	private XYLocator xy(ILocator loc, int x, int y) {
		return new XYLocator(loc, x, y);
	}
	
	private FigureLocator ellipse() {
		return new FigureLocator(ellipseMatcher);
	}
	
	
}

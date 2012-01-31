package com.windowtester.test.gef.tests.runtime.finder;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.windowtester.runtime.gef.internal.FigureReference;

import junit.framework.TestCase;

import static com.windowtester.runtime.gef.internal.locator.ByOrientationLocator.getPoint;
import static com.windowtester.runtime.gef.internal.locator.ByOrientationLocator.PositionHelper.getNearestOrientationRelativeTo;
import static com.windowtester.runtime.gef.internal.locator.ByOrientationLocator.PositionHelper.getNearestPointToOrientation;


/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ResizeHandleMatcherTest extends TestCase implements PositionConstants {

	public void testNE() {
		assertEquals(NORTH_EAST, getNearestOrientationRelativeTo(point(50,0), rectangle(0, 0, 50, 50)));
	}
	public void testSE() {
		assertEquals(SOUTH_EAST, getNearestOrientationRelativeTo(point(50,50), rectangle(0, 0, 50, 50)));
	}
	public void testNW() {
		assertEquals(NORTH_WEST, getNearestOrientationRelativeTo(point(0,0), rectangle(0, 0, 50, 50)));
	}
	public void testSW() {
		assertEquals(SOUTH_WEST, getNearestOrientationRelativeTo(point(0,50), rectangle(0, 0, 50, 50)));
	}
	public void testN() {
		assertEquals(NORTH, getNearestOrientationRelativeTo(point(25,0), rectangle(0, 0, 50, 50)));
	}
	public void testS() {
		assertEquals(SOUTH, getNearestOrientationRelativeTo(point(25,50), rectangle(0, 0, 50, 50)));
	}
	public void testE() {
		assertEquals(EAST, getNearestOrientationRelativeTo(point(50,25), rectangle(0, 0, 50, 50)));
	}
	public void testW() {
		assertEquals(WEST, getNearestOrientationRelativeTo(point(0,25), rectangle(0, 0, 50, 50)));
	}
	
	public void testProposalSE() {
		Point[] points = new Point[]{point(0,0), point(25, 25), point(50,50)};
		Rectangle rect = rectangle(0, 0, 50, 50);
		assertEquals(point(50,50), getNearestPointToOrientation(points, rect, SOUTH_EAST));
	}
	
	public void testProposalNW() {
		Point[] points = new Point[]{point(0,0), point(25, 25), point(50,50)};
		Rectangle rect = rectangle(0, 0, 50, 50);
		assertEquals(point(0,0), getNearestPointToOrientation(points, rect, NORTH_WEST));
	}
	
	public void testProposalSW() {
		Point[] points = new Point[]{point(0,40), point(25, 25), point(50,50)};
		Rectangle rect = rectangle(0, 0, 50, 50);
		assertEquals(point(0,40), getNearestPointToOrientation(points, rect, SOUTH_WEST));
	}
	
	
	public void testGetPoint() {
		Figure f = new Figure();
		f.setBounds(rectangle(0, 0, 100, 100));
		FigureReference ref = FigureReference.create(f);
		assertEquals(point(50,50), getPoint(ref));
	}
	
	
	
	
	
	private Rectangle rectangle(int x, int y, int w, int h) {
		return new Rectangle(x, y, w, h);
	}

	private Point point(int x, int y) {
		return new Point(x, y);
	}
	
	
}

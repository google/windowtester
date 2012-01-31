package com.windowtester.test.gef.tests.runtime.finder;

import junit.framework.TestCase;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.windowtester.runtime.gef.internal.finder.position.Points;
import com.windowtester.runtime.gef.internal.finder.position.PositionHelper;
import com.windowtester.runtime.gef.internal.finder.position.PositionFinder;
import com.windowtester.runtime.gef.internal.finder.position.PositionSpec;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class PositionSpecTest extends TestCase {

	
	public void testCenterDiameter() {
		assertEquals(15.0, PositionSpec.CenterRegion.deriveRadiusForCenter(rectangle(50, 50)));
		assertEquals(15.0, PositionSpec.CenterRegion.deriveRadiusForCenter(rectangle(50, 100)));
		assertEquals(24.0, PositionSpec.CenterRegion.deriveRadiusForCenter(rectangle(80, 100)));
	}
	
	public void testPointDirections() {
		assertTrue(PositionHelper.WEST.describesPointRelativeTo(point(0,0), point(5,0)));
		assertTrue(PositionHelper.EAST.describesPointRelativeTo(point(5,0), point(0,0)));
		assertTrue(PositionHelper.NORTH.describesPointRelativeTo(point(0,0), point(0,5)));
		assertTrue(PositionHelper.SOUTH.describesPointRelativeTo(point(5,10), point(5,5)));
		assertTrue(PositionHelper.NORTH_WEST.describesPointRelativeTo(point(0,0), point(5,5)));
		assertTrue(PositionHelper.NORTH_EAST.describesPointRelativeTo(point(5,0), point(0,5)));
		assertTrue(PositionHelper.SOUTH_WEST.describesPointRelativeTo(point(0,5), point(5,0)));
		assertTrue(PositionHelper.SOUTH_EAST.describesPointRelativeTo(point(5,5), point(0,0)));
		assertTrue(PositionHelper.CENTER.describesPointRelativeTo(point(0,0), point(0,0)));
		
		assertTrue(PositionHelper.NORTH_WEST.describesPointRelativeTo(point(0,0), point(25,25)));
		
		
	}
	
	public void testSpecs() {
		assertEquals(PositionSpec.CENTER, PositionSpec.forPointRelativeTo(point(25,25), rectangle(50,50)));
		assertEquals(PositionSpec.LEFT,   PositionSpec.forPointRelativeTo(point(0,25),  rectangle(50,50)));
		assertEquals(PositionSpec.RIGHT,  PositionSpec.forPointRelativeTo(point(50,25), rectangle(50,50)));
		assertEquals(PositionSpec.TOP,    PositionSpec.forPointRelativeTo(point(25,0),  rectangle(50,50)));
		assertEquals(PositionSpec.BOTTOM, PositionSpec.forPointRelativeTo(point(25,50), rectangle(50,50)));
		
		assertEquals(PositionSpec.TOP_LEFT, PositionSpec.forPointRelativeTo(point(0,0), rectangle(50,50)));
		
		//...
	}
	
	public void testFinder() {
		assertEquals(PositionHelper.NONE, PositionFinder.findIdentifyingPosition(point(0,0), rectangle(50,50), points(point(0,2))));
		assertEquals(PositionHelper.TOP, PositionFinder.findIdentifyingPosition(point(25,0), rectangle(50,50), points(point(0,0), point(50,50))));
		
		String toMatch = "&Also delete contents under 'C:\\Eclipse-3.2.2\\workspace\\Plugins\\cate\\Test'";
		String regex = "&Also delete contents .*";
		System.out.println(toMatch.matches(regex));
		
	}
	
	
	
	
	private Points points(Point ... points) {
		return Points.forArray(points);
	}

	private Point point(int x, int y) {
		return new Point(x,y);
	}


	private static Rectangle rectangle(int w, int h) {
		return rectangle(0, 0, w, h);
	}
	
	
	private static Rectangle rectangle(int x,int y, int w, int h) {
		return new Rectangle(x, y, w, h);
	}
	
	
}

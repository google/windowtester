package com.windowtester.test.gef.tests.runtime;

import static com.windowtester.runtime.draw2d.internal.selectors.ClickTranslator.makeRelativeToCenter;
import static com.windowtester.runtime.swt.locator.SWTLocators.button;
import junit.framework.TestCase;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Point;

import com.windowtester.runtime.ClickDescription;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.gef.locator.NamedFigureLocator;
import com.windowtester.runtime.locator.XYLocator;

/**
 * <p>
 * Copyright (c) 2008, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ClickTranslationTest extends TestCase {

	
	public void testCenter() throws Exception {
		Point pt = makeRelativeToCenter(ClickDescription.create(1, button("OK"), WT.BUTTON1), null);
		assertEquals(0, pt.x);
		assertEquals(0, pt.y);
	}


	public void testOffCenterNW() throws Exception {
		Point pt = relativize(5, 5);
		assertEquals(-20, pt.x);
		assertEquals(-20, pt.y);
	}

	public void testOffCenterSE() throws Exception {
		Point pt = relativize(40, 40);
		assertEquals(15, pt.x);
		assertEquals(15, pt.y);
	}

	public void testOffCenterNE() throws Exception {
		Point pt = relativize(40, 5);
		assertEquals(15, pt.x);
		assertEquals(-20, pt.y);
	}
	
	public void testOffCenterSW() throws Exception {
		Point pt = relativize(5, 40);
		assertEquals(-20, pt.x);
		assertEquals(15, pt.y);
	}
	
	private Point relativize(int x, int y) {
		Figure f = new Figure();
		f.setBounds(new Rectangle(25, 25, 50, 50));
		return makeRelativeToCenter(ClickDescription.create(1, new XYLocator(new NamedFigureLocator("foo"), x, y), WT.BUTTON1), f);
	}

	
	
	
	
	
}

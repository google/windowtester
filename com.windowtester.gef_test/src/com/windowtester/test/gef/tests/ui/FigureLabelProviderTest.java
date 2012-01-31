package com.windowtester.test.gef.tests.ui;

import junit.framework.TestCase;

import com.windowtester.runtime.gef.internal.locator.FigureLabelProvider;
import com.windowtester.runtime.gef.locator.FigureCanvasLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.FigureClassLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.locator.ILocator;

/**
 * Basic label provider tests.
 * <p>
 * Copyright (c) 2008, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class FigureLabelProviderTest extends TestCase {

	
	private static final FigureLabelProvider provider = new FigureLabelProvider();
	
	public void testPaletteItemText() {
		assertEquals("Palette Item: \"foo/bar\"", getText(new PaletteItemLocator("foo/bar")));
	}
	
	public void testFigureClassText() {
		assertEquals("Figure (MyFigure.class)", getText(new FigureClassLocator("MyFigure.class")));
	}

	public void testFigureCanvasText() {
		assertEquals("Figure Canvas", getText(new FigureCanvasLocator()));
	}
	
	public void testFigureCanvasXYText() {
		assertEquals("Figure Canvas", getText(new FigureCanvasXYLocator(3,3)));
	}
	
	private String getText(ILocator locator) {
		return provider.getText(locator);
	}
	

	
}

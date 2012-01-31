package com.windowtester.test.gef.tests.recorder;

import static com.windowtester.test.util.Serializer.serializeOut;

import java.io.IOException;

import junit.framework.TestCase;

import com.windowtester.runtime.gef.Position;
import com.windowtester.runtime.gef.internal.locator.provisional.api.AnchorLocator;
import com.windowtester.runtime.gef.internal.locator.provisional.api.ResizeHandleLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.FigureClassLocator;
import com.windowtester.runtime.gef.locator.LRLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

/**
 * 
 * Tests that GEF bits serialize out safely.  NOTE: this does not
 * ensure that they are rightly read-in!
 * <p>
 * Copyright (c) 2008, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class GEFLocatorSerializationSmokeTest extends TestCase {
	
	
	public void testPaletteItemLocator() throws IOException {
		serializeOut(new PaletteItemLocator("Foo"));
		serializeOut(new PaletteItemLocator("Foo", new EditorLocator("Bar")));
	}

	public void testFigureLocator() throws IOException {
		//serializeOut(new FigureLocator());
		//?: is this sent over the wire?
	}
	
	public void testAnchorLocator() throws IOException {
		serializeOut(new AnchorLocator(Position.BOTTOM, new FigureClassLocator("clsName")));
	}
	
	public void testLRLocator() throws IOException {
		serializeOut(new LRLocator(2, new FigureClassLocator("clsName")));
	}
	
	public void testResizeHandleLocator() throws IOException {
		serializeOut(new ResizeHandleLocator(Position.TOP, new FigureClassLocator("clsName")));
	}
	
	
	public void testFigureCanvasLocator() throws IOException {
		serializeOut(new FigureCanvasLocator("foo"));
		serializeOut(new FigureCanvasLocator(new EditorLocator("Bar")));
		serializeOut(new FigureCanvasLocator(new ViewLocator("Blah")));
		serializeOut(new FigureCanvasXYLocator(5, 10));
		serializeOut(new FigureCanvasXYLocator("foo", 5, 10));
	}
	
	
}

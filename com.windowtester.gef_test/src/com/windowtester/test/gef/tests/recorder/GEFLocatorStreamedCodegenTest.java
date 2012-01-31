package com.windowtester.test.gef.tests.recorder;

import java.io.IOException;

import com.windowtester.codegen.generator.LocatorJavaStringFactory;
import com.windowtester.runtime.gef.Position;
import com.windowtester.runtime.gef.internal.locator.provisional.api.AnchorLocator;
import com.windowtester.runtime.gef.internal.locator.provisional.api.ResizeHandleLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.FigureClassLocator;
import com.windowtester.runtime.gef.locator.LRLocator;
import com.windowtester.runtime.gef.locator.NamedEditPartFigureLocator;
import com.windowtester.runtime.gef.locator.NamedFigureLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.util.Serializer;


/**
 * Sanity checks to ensure that streamed in locators are getting properly codegened.
 * 
 * <p>
 * Copyright (c) 2008, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class GEFLocatorStreamedCodegenTest extends BaseGEFCodegenTest {

	
	private final GEFTestCodeGenerator cg = new GEFTestCodeGenerator();
	
	@Override
	protected void setUp() throws Exception {
		//this is a little uncomfortable but we need to fake a contribution
		//via the plugin registry (but would like to do it w/o the 
		//Platform running
		LocatorJavaStringFactory.TestOverride.setToStringDelegate(cg);
	}
	
	
	public void testPaletteItem() throws Exception {
		assertGenerates("new PaletteItemLocator(\"foo\")", (new PaletteItemLocator("foo")));
	}

	public void testPaletteItemInEditor() throws Exception {
		assertGenerates("new PaletteItemLocator(\"foo\", new EditorLocator(\"bar\"))", new PaletteItemLocator("foo", new EditorLocator("bar")));
	}

	public void testFigureXYCanvas() throws Exception {
		assertGenerates("new FigureCanvasXYLocator(15, 15)", new FigureCanvasXYLocator(15,15));
	}

	public void testFigureCanvasInView() throws Exception {
		assertGenerates("new FigureCanvasLocator(new ViewLocator(\"my.view\"))", new FigureCanvasLocator(new ViewLocator("my.view")));
	}

	public void testFigureCanvasInEditor() throws Exception {
		assertGenerates("new FigureCanvasLocator(new EditorLocator(\"my.editor\"))", new FigureCanvasLocator(new EditorLocator("my.editor")));
	}
	
	public void testFigureClass() throws Exception {
		assertGenerates("new MyClassLocator()", new FigureClassLocator("MyClass"));
	}
	
	public void testAnchor() throws Exception {
		assertGenerates("new AnchorLocator(Position.BOTTOM, new MyClassLocator())", new AnchorLocator(Position.BOTTOM, new FigureClassLocator("MyClass")));
	}
	
	public void testHandleLocator() throws Exception {
		assertGenerates("new ResizeHandleLocator(Position.BOTTOM_LEFT, new MyClassLocator())", new ResizeHandleLocator(Position.BOTTOM_LEFT, new FigureClassLocator("MyClass")));
	}
	
	public void testLRLocator() throws Exception {
		assertGenerates("new LRLocator(2, new MyClassLocator())", new LRLocator(2, new FigureClassLocator("MyClass")));
	}
	
	public void testNamedEditPartLocator() throws Exception {
		assertGenerates("new NamedEditPartFigureLocator(\"my.name\")", new NamedEditPartFigureLocator("my.name"));		
	}
	
	public void testNamedFigureLocator() throws Exception {
		assertGenerates("new NamedFigureLocator(\"my.name\")", new NamedFigureLocator("my.name"));		
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	//
	// Helpers
	//
	/////////////////////////////////////////////////////////////////////////////////
	
	protected void assertGenerates(String expected, ILocator locatorToStream) throws IOException, ClassNotFoundException {
		assertEquals(expected, toJava(streamed(locatorToStream)));
	}
	
	private <T> T streamed(T locator) throws IOException, ClassNotFoundException {
		return Serializer.serializeOutAndIn(locator);
	}
	
}

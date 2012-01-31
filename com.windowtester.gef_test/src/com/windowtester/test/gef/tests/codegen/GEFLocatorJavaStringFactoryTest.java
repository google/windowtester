package com.windowtester.test.gef.tests.codegen;


import com.windowtester.runtime.gef.Position;
import com.windowtester.runtime.gef.internal.locator.provisional.api.AnchorLocator;
import com.windowtester.runtime.gef.locator.FigureClassLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;
import com.windowtester.test.codegen.SWTAPICodeBlockBuilderTest;
import com.windowtester.test.gef.tests.recorder.BaseGEFCodegenTest;

/**
 * Test to verify that locators are being properly "stringified".
 * 
 * <p>
 * @see SWTAPICodeBlockBuilderTest
 * 
 * Copyright (c) 2008, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class GEFLocatorJavaStringFactoryTest extends BaseGEFCodegenTest {

	public void testFigurePalette() {
		assertEquals("new PaletteItemLocator(\"Shapes/Ellipse\")", toJava(new PaletteItemLocator("Shapes/Ellipse")));
	}
	
	public void testFigurePaletteEditorScoped() {
		assertEquals("new PaletteItemLocator(\"Shapes/Ellipse\", new EditorLocator(\"Foo.shape\"))", toJava(new PaletteItemLocator("Shapes/Ellipse", new EditorLocator("Foo.shape"))));
	}
		
	public void testAnchor1() {
		assertEquals("new AnchorLocator(Position.RIGHT, new FooLocator())", toJava(new AnchorLocator(Position.RIGHT, new FigureClassLocator("Foo"))));
	}
	
//	public void testTextFlow() {
//		assertEquals("new TextFlowLocator(\"some text\")", toJava(new TextFlowLocator("some text")));
//	}
	
	
	
}

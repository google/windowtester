package com.windowtester.test.gef.tests.codegen;

import static com.windowtester.test.codegen.CodeGenFixture.mockEvent;

import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.recorder.event.user.SemanticMenuSelectionEvent;
import com.windowtester.recorder.event.user.UISemanticEvent.EventInfo;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.FigureClassLocator;
import com.windowtester.runtime.swt.internal.preferences.ICodeGenConstants;
import com.windowtester.test.codegen.SWTAPICodeBlockBuilderTest;
import com.windowtester.test.gef.tests.recorder.BaseGEFCodegenTest;

/**
 * Test to verify that GEF actions are being properly "blocked".
 * 
 * <p>
 * @see SWTAPICodeBlockBuilderTest
 * <p>
 * Copyright (c) 2008, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class GEFCodeBlockBuilderTest extends BaseGEFCodegenTest {

	public static final String NEW_LINE = ICodeGenConstants.NEW_LINE;
	
	public void testFigureXYContextClick() throws Exception {
		
		EventInfo info = mockEvent(new FigureCanvasXYLocator(3,3));
		info.button = 3; //CClick
		SemanticMenuSelectionEvent select = new SemanticMenuSelectionEvent(info);
		
		select.setPath("foo/bar");
		CodeBlock block = blockBuilder().buildMenuSelect(select);
		assertEquals("ui.contextClick(new FigureCanvasXYLocator(3, 3), \"foo/bar\");", block);
	}	
	
	public void testFigureClassContextClick() throws Exception {
		
		EventInfo info = mockEvent(new FigureClassLocator("org.acme.MyFigure"));
		info.button = 3; //CClick
		SemanticMenuSelectionEvent select = new SemanticMenuSelectionEvent(info);
		
		select.setPath("foo/bar");
		CodeBlock block = blockBuilder().buildMenuSelect(select);
		assertEquals("ui.contextClick(new MyFigureLocator(), \"foo/bar\");", block);
	}	
	

	
}

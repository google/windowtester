package com.windowtester.runtime.gef.internal;

import static com.windowtester.runtime.gef.test.builder.FigureBuilder.addChild;
import static com.windowtester.runtime.gef.test.builder.FigureBuilder.figure;
import junit.framework.TestCase;

import org.eclipse.draw2d.IFigure;


/**
 * The class <code>FigureInfoBuilderTest</code> contains tests for the class
 * {@link <code>FigureInfoBuilder</code>}
 *
 * @pattern JUnit Test Case
 *
 * @generatedBy CodePro at 9/21/07 1:07 PM
 *
 * @author Phil Quitslund
 *
 * @version $Revision: 1.3 $
 */
public class FigureInfoBuilderTest extends TestCase {


	public void testGetChildrenEmpty() {
		FigureInfoBuilder fixture = new FigureInfoBuilder();
		IFigureList result = fixture.getChildren(figure());
		assertNotNull(result);
		assertEquals(0,result.toArray().length);
	}

	public void testGetChildrenNotEmpty() {
		FigureInfoBuilder fixture = new FigureInfoBuilder();
		IFigure f1 = figure();
		IFigure f2 = figure();
		addChild(f1, f2);
		IFigureList result = fixture.getChildren(f1);
		assertNotNull(result);
		assertEquals(1, result.toArray().length);
	}
	
	public void testGetConnections() {
		fail("Newly generated method - fix or disable");
	}

	public void testGetPartNone() {
		// add test code here
		FigureInfoBuilder fixture = new FigureInfoBuilder();
		IEditPartReference result = fixture.getPart(figure());
		assertNotNull(result);
	}
	
}

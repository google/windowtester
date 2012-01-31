package com.windowtester.samples.gef.tests;


import static com.windowtester.samples.eclipse.common.WorkBenchHelper.createSimpleProject;
import static com.windowtester.samples.eclipse.common.WorkBenchHelper.openView;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.draw2d.matchers.ByClassNameFigureMatcher;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.FigureLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.samples.eclipse.common.WorkBenchHelper.View;
import com.windowtester.samples.gef.common.AbstractSampleGEFTest;
import com.windowtester.samples.gef.common.provisional.LRLocator;

/**
 * 
 * Sample shape driving test.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 * 
 */
public class SampleGEFShapeTest extends AbstractSampleGEFTest {


	private static final String NEW_SHAPE_EXAMPLE_PATH = GEF_EXAMPLES_ROOT + "Shapes Diagram";
	
	private final String PROJECT_NAME = getClass().getName() + "Project";

	
	private static final class RectangleLocator extends FigureLocator {
		public RectangleLocator() {
			super(new ByClassNameFigureMatcher("org.eclipse.draw2d.RectangleFigure"));
		}
	}
	
	private static final class EllipseLocator extends FigureLocator {
		public EllipseLocator() {
			super(new ByClassNameFigureMatcher("org.eclipse.draw2d.Ellipse"));
		}
	}
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createShapeDiagramExample();
	}
	

	
	protected void createShapeDiagramExample() throws WidgetSearchException {
		createSimpleProject(getUI(), PROJECT_NAME);
		openView(getUI(), View.JAVA_PACKAGEEXPLORER);
		createShapeDiagramExample(PROJECT_NAME);
	}
	
	protected void createShapeDiagramExample(String projectName) throws WidgetSearchException {
		IUIContext ui = getUI();
		
		openNewWizard(projectName);

		ui.click(new TreeItemLocator(NEW_SHAPE_EXAMPLE_PATH));
		ui.click(new ButtonLocator("Next >"));
		ui.click(new ButtonLocator("Finish"));
		ui.wait(new ShellDisposedCondition("New"));
	}
	
	
	public void testShape() throws Exception {

		createEllipseAt(100, 100);

		createRectangleAt(0, 0);
		createRectangleAt(0, 200);
		createRectangleAt(200, 0);
		createRectangleAt(200, 200);

		
		clickPalette("Solid connection");
		clickEllipseIndexedAt(0);
		clickRectangleIndexedAt(1);
		pauseForModel();

		clickPalette("Dashed connection");
		clickEllipseIndexedAt(0);
		clickRectangleIndexedAt(2);

		//demonstrate a drag and drop
		clickPalette("Select");
		clickRectangleIndexedAt(0);
		getUI().dragTo(new FigureCanvasXYLocator(150, 60));

		
		// pause to look at editor:
		getUI().pause(3000);

		save();

	}

	
	protected void createRectangleAt(int x, int y) throws WidgetSearchException {
		clickPalette("Shapes/Rectangle");
		getUI().click(new FigureCanvasXYLocator(x, y));
		pauseForModel();
	}
	
	protected void createEllipseAt(int x, int y) throws WidgetSearchException {
		clickPalette("Shapes/Ellipse");
		getUI().click(new FigureCanvasXYLocator(x, y));
		pauseForModel();
	}
	
	protected void clickPalette(String palettePath) throws WidgetSearchException {
		getUI().click(new PaletteItemLocator(palettePath));
	}
	
	
	protected void clickRectangleIndexedAt(int indexLeftToRight) throws WidgetSearchException {
		getUI().click(new LRLocator(new RectangleLocator(), indexLeftToRight));
	}
	
	protected void clickEllipseIndexedAt(int indexLeftToRight) throws WidgetSearchException {
		getUI().click(new LRLocator(new EllipseLocator(), indexLeftToRight));	}
	
}

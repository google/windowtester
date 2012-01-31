package com.windowtester.test.gef.tests.common;


import static com.windowtester.test.gef.factories.FigureMatcherFactory.xyComparator;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.draw2d.matchers.ByClassNameFigureMatcher;
import com.windowtester.runtime.gef.internal.locator.provisional.api.IndexedFigureLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasLocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.internal.condition.ModalDialogShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.test.gef.helpers.WorkBenchHelper.View;

/**
 * Base class for shape driving tests.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class BaseShapeDrivingTest extends AbstractGEFDrivingTest  {

	public static final String[] ALL_PALETTE_LOCATIONS = {
		"Select",
		"Marquee",
		"Solid connection",
		"Dashed connection",
		"Shapes/Ellipse",
		"Shapes/Rectangle"
	};
	
	public static enum Drawer {

		COMPONENTS("Shapes");
		
		private final String _path;

		private Drawer(String path) {
			_path = path;
		}

		public String getPath() {
			return _path;
		}
	}
	
	//"Examples/Shapes Diagram"
	public static final String NEW_SHAPE_EXAMPLE_PATH = "Examples/GEF Team Examples/Shapes Diagram";
	
	private final String shapeExamplePath;
	
	final String PROJECT_NAME = getClass().getName() + "Project";
	
	protected ByClassNameFigureMatcher rectangleMatcher = new ByClassNameFigureMatcher("org.eclipse.draw2d.RectangleFigure");
	
	protected ByClassNameFigureMatcher ellipseMatcher = new ByClassNameFigureMatcher("org.eclipse.draw2d.Ellipse");
	
	
	
	public BaseShapeDrivingTest() {
		super();
		this.shapeExamplePath = NEW_SHAPE_EXAMPLE_PATH;
	}
	
	public BaseShapeDrivingTest(String shapeExamplePath) {
		super();
		this.shapeExamplePath = shapeExamplePath;
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createShapeDiagramExample();
	}
	
	protected void createShapeDiagramExample(String projectName) throws WaitTimedOutException, Exception {
		IUIContext ui = getUI();
		
		openNewWizard(projectName, ui);

		ui.click(new TreeItemLocator(shapeExamplePath));
		ui.click(new ButtonLocator("&Next >"));
		ui.click(new ButtonLocator("&Finish"));
		//ui.wait(new ShellDisposedCondition("New"));
		ui.wait(new ModalDialogShowingCondition().not());
	}

	protected void createShapeDiagramExample() throws Exception {
		createSimpleProject(PROJECT_NAME);
		createShapeDiagramExample(PROJECT_NAME);
	}
	
	protected void createRectangleAt(int x, int y) throws WidgetSearchException {
		clickPalette("Shapes/Rectangle");
		getUI().click(new XYLocator(new FigureCanvasLocator(), x, y));
		pauseForModel();
	}
	
	protected void createEllipseAt(int x, int y) throws WidgetSearchException {
		clickPalette("Shapes/Ellipse");
		getUI().click(new XYLocator(new FigureCanvasLocator(), x, y));
		pauseForModel();
	}
	
	protected void makeSolidConnection(int x1, int y1, int x2, int y2) throws WidgetSearchException {
		makeConnection(true, x1, y1, x2, y2);
	}
	
	protected void makeDashedConnection(int x1, int y1, int x2, int y2) throws WidgetSearchException {
		makeConnection(false, x1, y1, x2, y2);
	}
	
	private void makeConnection(boolean isSolidConnection, int x1, int y1, int x2, int y2) throws WidgetSearchException {
		if(isSolidConnection) {
			clickPalette("Solid connection");
		} else {
			clickPalette("Dashed connection");
		}
		getUI().click(new XYLocator(new FigureCanvasLocator(), x1, y1));
		getUI().click(new XYLocator(new FigureCanvasLocator(), x2, y2));
		pauseForModel();
	}
	
	protected void clickRectangleIndexedAt(int i) throws WidgetSearchException {
		getUI().click(new IndexedFigureLocator(i, rectangleMatcher, xyComparator()));
	}
	
	protected void clickEllipseIndexedAt(int i) throws WidgetSearchException {
		getUI().click(new IndexedFigureLocator(i, ellipseMatcher, xyComparator()));
	}
	
}

package com.windowtester.test.gef.tests.common;


import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.draw2d.matchers.ByClassNameFigureMatcher;
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
 * @author Jaime Wren
 *
 */
public class BaseLogicDrivingTest extends AbstractGEFDrivingTest  {

	public static final String[] ALL_PALETTE_LOCATIONS = {
			"Select",
			"Marquee",
			"Connection",
			"Components/Flow Container",
			"Components/Circuit",
			"Components/Label",
			"Components/LED",
			"Components/Or Gate",
			"Components/XOR Gate",
			"Components/And Gate",
			"Components/V+",
			"Canned Parts/HalfAdder",
			"Canned Parts/FullAdder"
			};
	
	
	public static enum Drawer {

		COMPONENTS("Components"), CANNED_PARTS("Canned Parts");
		
		private final String _path;

		private Drawer(String path) {
			_path = path;
		}

		public String getPath() {
			return _path;
		}
	}
	
	protected ByClassNameFigureMatcher ledMatcher = new ByClassNameFigureMatcher("org.eclipse.gef.examples.logicdesigner.figures.LEDFigure");
	
	protected ByClassNameFigureMatcher andGateMatcher = new ByClassNameFigureMatcher("org.eclipse.gef.examples.logicdesigner.figures.AndGateFigure");
	
	protected ByClassNameFigureMatcher xOrGateMatcher = new ByClassNameFigureMatcher("org.eclipse.gef.examples.logicdesigner.figures.XOrGateFigure");
	
	protected ByClassNameFigureMatcher orGateMatcher =  new ByClassNameFigureMatcher("org.eclipse.gef.examples.logicdesigner.figures.OrGateFigure");
	
	protected ByClassNameFigureMatcher circuitMatcher =  new ByClassNameFigureMatcher("org.eclipse.gef.examples.logicdesigner.figures.CircuitFigure");
	
	public static final String NEW_LOGIC_EXAMPLE_PATH = "Examples/GEF Team Examples/Logic Diagram";
	
	private final String logicExamplePath;
	
	final String PROJECT_NAME = getClass().getName() + "Project";
	
	private final boolean fourBitAdder;
	
	public BaseLogicDrivingTest() {
		super();
		this.logicExamplePath = NEW_LOGIC_EXAMPLE_PATH;
		this.fourBitAdder = false;
	}
	
	public BaseLogicDrivingTest(String logicExamplePath) {
		super();
		this.logicExamplePath = logicExamplePath;
		this.fourBitAdder = false;
	}
	
	public BaseLogicDrivingTest(boolean fourBitAdder) {
		super();
		this.logicExamplePath = NEW_LOGIC_EXAMPLE_PATH;
		this.fourBitAdder = fourBitAdder;
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createLogicDiagramExample();
	}
	
	protected void createLogicDiagramExample(String projectName) throws Exception {
		IUIContext ui = getUI();
		
		openNewWizard(projectName, ui);

		ui.click(new TreeItemLocator(logicExamplePath));
		ui.click(new ButtonLocator("&Next >"));
		if(fourBitAdder) {
			ui.click(new ButtonLocator("F&our-bit Adder Model"));
		}
		ui.click(new ButtonLocator("&Finish"));
//		ui.wait(new ShellDisposedCondition("New"));
		ui.wait(new ModalDialogShowingCondition().not());
	}

	protected void createLogicDiagramExample() throws Exception {
		createSimpleProject(PROJECT_NAME);
		createLogicDiagramExample(PROJECT_NAME);
	}
}
